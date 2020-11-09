package com.negeso.module.statistics;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.negeso.framework.Env;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.util.Timer;
import com.negeso.module.statistics.domain.Statistics;

public class StatisticsCron extends QuartzJobBean {
												   
	private static String FOLDER_FOR_STATISTICS = "/WEB-INF/generated/statistics_archive";
	private static String DATE_DELIMITER = ".";
	private static String STATISTICS_FILE_EXTENSION = ".zip";
	
	private static Logger logger = Logger.getLogger(StatisticsCron.class);

	private boolean repeat = false;
	
	private Timer timer;
	/**
	 * Calls automatically by spring at 00:01 of the first day every month
	 */   
	protected void executeInternal(JobExecutionContext ctx)	throws JobExecutionException {
		logger.debug("+ start statistics cron, repeated=" + repeat);
		if (!repeat){
			timer = new Timer();
			timer.start();
		}
		repeat = false;
		File statFolder = new File(Env.getRealPath(FOLDER_FOR_STATISTICS));
		if (!statFolder.exists()){
			if (!statFolder.mkdirs()){
				logger.error("- statistics folder cannot be created");
				return;
			}
		}
		//get statistics for each month.year(MM.yyyy)
		Map<String, ArrayList<Statistics>> statisticsMap = getStatisticsByPeriod();
		
		if (statisticsMap == null){
			logger.debug("- error during site statistics processing");
			return;
		}
		if (statisticsMap.size() == 0){
			logger.debug("- site statistics is empty");
			return;
		}
		//remove statistics for current new month
		String currentDate = new String(new SimpleDateFormat("M" + DATE_DELIMITER + "yyyy").format(new Date()));
		if (statisticsMap.containsKey(currentDate)){
			statisticsMap.remove(currentDate);
		}
		
		archiveStatisticsToDB(statisticsMap);
		
		//create statistics file archive
		Set<String> keys = statisticsMap.keySet();
		Iterator<String> i = keys.iterator();
		
		while(i.hasNext()){
			String key = i.next();
			File fileStat = getStatisticsFile(key.replace(".", "_"));
			try{
				writeStatisticsToFile(fileStat, key, statisticsMap.get(key));
			}catch(IOException e){
				logger.error("error during writing statistics to file");
			}
			try {
				fileStat.createNewFile();
			} catch (IOException e) {
				logger.error("- error during creating statistics file");
				return;
			}
		}
		
		dropOldStatistics(statisticsMap);
		
		if (repeat){
			statisticsMap.clear();
			statisticsMap = null;
			System.gc();
			execute(ctx);
		}else{
			logger.debug("- statistics cron finished. time=" + timer.stop());
		}
	}
	
	private void archiveStatisticsToDB(Map<String, ArrayList<Statistics>> map){
		logger.debug("+ start archive old statistics");
		if (map == null || map.isEmpty()){
			logger.debug("- map is empty,return");
			return;
		}
		try{
			Connection conn = DBHelper.getConnection();
			Iterator<String> i = map.keySet().iterator();
			while(i.hasNext()){
				String key = i.next();
				ArrayList<Statistics> arrStat = map.get(key);
				StatisticsPeriod.insertByMonth(conn, analyzePeriod(arrStat, key));
				StatisticsPeriod.deletePeriod(conn, arrStat);
			}
		}catch(Exception e){
			logger.error("error during archiving statistics to file:" + e);
		}
		logger.debug("- end archive old statistics");
	}
	
	private ArrayList<StatisticsPeriod> analyzePeriod(ArrayList<Statistics> period, String stringDate){
		logger.debug("+ start analyzing old statistics period");
		if (period == null || period.isEmpty()){
			logger.debug("- period is empty, return");
			return new ArrayList<StatisticsPeriod>();
		}
		ArrayList<StatisticsPeriod> stPeriod = new ArrayList<StatisticsPeriod>();
		
		Iterator<Statistics> i = period.iterator();
		while(i.hasNext()){
			Statistics s = i.next();
			int p = isStatisticsPresent(stPeriod, s.getUserId(), s.getPageName().toLowerCase());
			if (p < 0){
				StatisticsPeriod sP = new StatisticsPeriod();
				sP.setPageName(s.getPageName());		
				sP.setUserId(s.getUserId());
				sP.addHit();
				sP.setSiteId(s.getSiteId());
				sP.setMonth(Long.parseLong(stringDate.substring(0, stringDate.indexOf(DATE_DELIMITER))));
				sP.setYear(Long.parseLong(stringDate.substring(stringDate.indexOf(DATE_DELIMITER) + 1, stringDate.length())));
				stPeriod.add(sP);
			}else{
				stPeriod.get(p).addHit();
			}
		}
		return stPeriod;
	}
	
	private int isStatisticsPresent(ArrayList<StatisticsPeriod> st_period, Long userId, String pageName){
		if (pageName != null){
			for (int i = 0 ; i < st_period.size(); i++){
				Long userId2 = st_period.get(i).getUserId();
				String pageName2 = st_period.get(i).getPageName().trim().toLowerCase();
				if ((userId2 == null && userId == null) && pageName.trim().toLowerCase().equals(pageName2)){
					return i;
				}else
				if ((userId2 != null && userId != null) && pageName.trim().toLowerCase().equals(pageName2) && userId.equals(userId2)){
					return i;
				}
			}
		}
		return -1;
	}
	
	private void dropOldStatistics(Map<String, ArrayList<Statistics>> stat_map){
		Connection conn = null;
		try{
			conn = DBHelper.getConnection();
			Iterator<String> a = stat_map.keySet().iterator();
			while(a.hasNext()){
				String key = a.next();
				ArrayList<Statistics> arrStat = stat_map.get(key);
				Iterator<Statistics> i = arrStat.iterator();
				while(i.hasNext()){
					Statistics old_statistics = i.next();
					old_statistics.delete(conn);
				}
			}
		}catch(Exception e){
			logger.error("- error during DB cleaning");
			return;
		}finally{
			DBHelper.close(conn);
		}
	}
	
	private File getStatisticsFile(String string){
		logger.debug("+");
		String fileName = Env.getRealPath(FOLDER_FOR_STATISTICS + "/" +getFileForStatistics(string) + STATISTICS_FILE_EXTENSION);
		File file;
		try{
			file = new File(fileName);
		}catch(Exception e){
			logger.error("- error during creating statistics file");
			return null;
		}
		logger.debug("-");
		return file;
	}
	
	private void writeStatisticsToFile(File file, String key, ArrayList<Statistics> stats) throws IOException{
		logger.debug("+");
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(file));
		try{
			out.putNextEntry(new ZipEntry(file.getName()));
			StringBuffer sb = new StringBuffer(1000);
			sb.append("Negeso Site Statistics\n");
			sb.append("Generated date: " + new SimpleDateFormat("HH:mm:ss,dd.MM.yyyy").format(new Date()) + "\n");
			sb.append("Statistics period: " + key + "\n");
			sb.append("User login,User ip,Hit date,Hit time,Page name,Referer,Event,Site id\n");
			out.write(sb.toString().getBytes(), 0, sb.toString().getBytes().length);
			Iterator<Statistics> iStat = stats.iterator();
			while(iStat.hasNext()){
				sb = new StringBuffer(50000);
				Statistics stat = iStat.next();
				sb.append(String.format(
					"%1$s,%2$s,%3$s,%4$s,%5$s,%6$s,%7$s,%8$s\n", 
					stat.getUserLogin(),
					stat.getUserIp(),
					new SimpleDateFormat("dd.MM.yyyy").format(stat.getHitDate()).toString(),
					new SimpleDateFormat("HH:mm:ss").format(stat.getHitTime()).toString(),
					stat.getPageName(),
					stat.getReferer(),
					stat.getEvent(),
					stat.getSiteId()));
				out.write(sb.toString().getBytes(), 0, sb.toString().getBytes().length);
			}
		}catch(Exception e){
			logger.error("- error while wring statistics to file:" + e);
		}finally{
			out.flush();
			out.close();
		}
	}
	
	private String getFileForStatistics(String stat_name){
		String newFileName = stat_name;
		File file = new File(Env.getRealPath(FOLDER_FOR_STATISTICS + "/" + newFileName + STATISTICS_FILE_EXTENSION));
		int i = 0;
		while(file.exists()){
			newFileName = stat_name + "#" + ++i;
			file = new File(Env.getRealPath(FOLDER_FOR_STATISTICS + "/" + newFileName + STATISTICS_FILE_EXTENSION));
		}
		file = null;
		return newFileName;
	}
	
	private Map<String, ArrayList<Statistics>> getStatisticsByPeriod(){
		Map<String, ArrayList<Statistics>> map = new LinkedHashMap<String, ArrayList<Statistics>>();
		Connection conn = null;
		try{
			conn = DBHelper.getConnection();
			List<String> years = StatisticsPeriod.findYears(conn);
			Iterator<String> iYear = years.iterator();
			while(iYear.hasNext()){
				String currYear = (String) iYear.next();
				List<String> monthes = StatisticsPeriod.findMonthesByYear(conn, currYear);
				Iterator<String> iMonth = monthes.iterator();
				while(iMonth.hasNext()){
					String currMonth = (String) iMonth.next();
					ArrayList<Statistics> statistics = new ArrayList<Statistics>();
					statistics = StatisticsPeriod.findByYearAndMonth(conn, currYear, currMonth);
					if (statistics.size() >= StatisticsPeriod.LIMIT_STATISCTICS_NUMBER){
						repeat = true;
					}
					map.put(currMonth + DATE_DELIMITER + currYear, statistics);
				}
			}
		}catch(CriticalException e){
			logger.error("- error while getting statistics");
		}catch(SQLException e){
			logger.error("- error connecting to database");
		}finally{
			DBHelper.close(conn);
		}
		return map;
	}
}