package com.negeso.module.statistics;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.negeso.framework.view.AbstractHttpView;




public class CSVBuilder {
	private static Logger logger = Logger.getLogger(CSVBuilder.class);	
	public static final String CSV_ATTRIBUTE = "statistics_csv";
	public static final String CSV_LIST_ATTRIBUTE = "statistics_csv_list";
    private static final String CSV_MIME_TYPE = "text/csv";
	
	public static StringBuffer writeTitle(StringBuffer buf, String title){
		if( buf==null ){
			buf = new StringBuffer();
		}
		
		if( title!=null ){
			buf.append(title+";"+"\n\n\n");
		}
		return buf;
	}
	
	/**
	 * Writes head for data to the buf
	 * Example
	 * HEAD1; HEAD2
	 * data1; data2
	 * data3; data4
	 *  
	 * @param buf
	 * @param data
	 * 
	 */
	public static StringBuffer writeHead(StringBuffer buf, String head){
		if( buf==null ){
			buf = new StringBuffer();
		}		
		if( head!=null ){
			buf.append(head+"\n");
		}
		return buf;		
	}
	
	/**
	 * Writes data to the buf
	 * data may be multi-line 
	 * Multi-line example:
	 * note1; note2
	 * note3; note4
	 *  
	 * @param buf
	 * @param data
	 * 
	 */
	
	public static StringBuffer writeData(StringBuffer buf, StringBuffer data){
		if( buf==null ){
			buf = new StringBuffer();
		}		
		if( data!=null ){
			buf.append(data);
		}
		return buf;
	}

	public static void buildFile(HttpServletResponse httpResponse, StringBuffer buf) {
		logger.debug("+");		
		httpResponse.setDateHeader(AbstractHttpView.HEADER_EXPIRES, 0);
        httpResponse.setContentType(CSV_MIME_TYPE);

        OutputStream out;
		try {
			out = httpResponse.getOutputStream();
			String outString = buf.substring(0,buf.length());			
			out.write(outString.getBytes());
			
			out.flush();
			out.close();
		} catch (IOException e) {
			logger.error("-" + e.getMessage());
		}
		logger.debug("-");		
	}


}
