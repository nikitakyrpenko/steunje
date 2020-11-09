package com.negeso.module.statistics;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.negeso.framework.Env;

public class Month {
	private static Logger logger = Logger.getLogger(Month.class);
	//1-12
	private int month;
	//eg. 2005
	private int year;
	
	private String name;
	
	private String [] monthNames = 
		{"January", "February", "March", "April",
		 "May", "June", "July", "August",
		 "September", "October", "November", "December"};
	
	public Month(int month, int year){
		this.month = month;
		this.year = year;
	}
	
	public int getMonth() {
		return month;
	}
	
	public void setMonth(int month) {
		this.month = month;
	}
	
	public int getYear() {
		return year;
	}
	
	public void setYear(int year) {
		this.year = year;
	}
	
	public String getMonthId(){
		return ""+month+"_"+year;
	}
	
	public Month getNextMonth(){
		int newMonth;
		int newYear;
		if (this.month==12){
			newMonth= 1;
			newYear = this.year+1;
		}
		else{
			newMonth=this.month+1;
			newYear=this.year;
		}
		return new Month(newMonth, newYear);
	}
	
	public boolean equals(Month month){
		if (month == null){
			return false;
		}
		if (this.month==month.month && this.year == month.year){
			return true;
		}
		else{
			return false;
		}
	}
	
	public static Month getFirstMonth(){
			String date = Env.getProperty("statistics.firstDate");
			SimpleDateFormat SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Integer year = 2008;
			Integer month = 5;
			try {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(SimpleDateFormat.parse(date));
				year = calendar.get(Calendar.YEAR);
				month = calendar.get(Calendar.MONTH)+1;
			} catch (Exception e) {
				// TODO: handle exception
			}
			return new Month(month, year);
	}
	
    public static ArrayList<Month> getMonths(Integer year){
        ArrayList<Month> result = new ArrayList<Month>();
        int first = Month.getFirstMonth().getYear();
        int last = Month.getLastMonth().getYear();
        Month firstM = Month.getFirstMonth();
        Month lastM = Month.getLastMonth();
        
        if( year<first || year>last ){
            return new ArrayList<Month>();
        }
        else if( year>first && year<last ){
            for( int i=1; i<=12; i++ ){
                result.add( new Month(i,year) );
            }
        }
        else if( year==first ) {
            int tmpL = 12;
            if( lastM.getYear()==firstM.getYear() ){
                tmpL = lastM.getMonth();
            }
            for( int i=firstM.getMonth(); i<=tmpL; i++ ){
                result.add( new Month(i,year) );
            }
            
        }
        else if( year==last ){
            int tmpF = 1;
            if( lastM.getYear()==firstM.getYear() ){
                tmpF = firstM.getYear();
            }
            for( int i=tmpF; i<=lastM.getMonth(); i++ ){
                result.add( new Month(i,year) );
            }
        }
            
        if( result==null ){
            return new ArrayList<Month>();
        }
        else{
            return result;
        }
   }
	
		
	public static Month getLastMonth(){
		Date date = Calendar.getInstance().getTime();
		return new Month(date.getMonth()+1, date.getYear()+1900);
	}
	
	public String getShowName(){
		return monthNames[this.month-1]+" "+this.year;	
	}
	
	public static Month getMonthById(String id){
		if (id.length()<6 || id.length()>7)
		{
			logger.error("length="+id.length());
			return null;
		}
		Long year = new Long(id.substring(id.length()-4,id.length()));
		if (year<2000 || year>2100){
			logger.error("Year="+year);
			return null;
		}
		int index = id.indexOf("_");
		Long mth = new Long(id.substring(0, index));
		if(mth>12 || mth<1)
		{
			return null;
		}
		return new Month(mth.intValue(), year.intValue());
	}

	public String getName() {
		return getShowName();
	}

	
}
