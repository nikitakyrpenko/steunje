/*
 * @(#)$Id: Currency.java,v 1.12, 2006-04-19 16:25:23Z, Dmitry Dzifuta$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.domain;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;



/**
 *
 * Currency domain
 * 
 * @author        Olexiy Strashko
 * @version       $Revision: 13$
 */
public class Currency {
	private static Logger logger = Logger.getLogger( Currency.class );
	
	
	public static final String CURRENCY_PATTERN = "\u00A4 ###,###,###.##";
	public static final String NOSYMBOL_CURRENCY_PATTERN = "###,###,###.##";
	
	private static final String getCurrenciesSql = 
	    " SELECT id, title, code, sign " +
	    " FROM pm_currency"
	;

	private static Currency EURO = null;
	private static Currency USD = null;
	private static Currency POUND = null;
	private static Currency ZLOTY = null;
	
	private DecimalFormat formatter = null;
	private DecimalFormat noSymbolFformatter = null;
	private Long id = null;
	private String code = null;
	private String symbol = null;
	private String title = null;
	
	
	public static Currency getEuro(){
		if ( Currency.EURO == null ){
			Currency.EURO = new Currency();
			Currency.EURO.setCode("EUR");
			Currency.EURO.setSymbol("\u20AC");
			Currency.EURO.setTitle("Euro");
			Currency.EURO.setId(1L);
		}
		return Currency.EURO;
	}
		
	public static Currency getDollar() {
		if ( Currency.USD == null ){
			Currency.USD = new Currency();
			Currency.USD.setCode("USD");
			Currency.USD.setSymbol("$");
			Currency.USD.setTitle("Dollar");
			Currency.USD.setId(4L);

		}
		return Currency.USD;
	}

	public static Currency getPound() {
		if ( Currency.POUND == null ){
			Currency.POUND = new Currency();
			Currency.POUND.setCode("GBP");
			Currency.POUND.setSymbol("£");
			Currency.POUND.setTitle("Pound");
			Currency.POUND.setId(2L);
		}
		return Currency.POUND;
	}

	public static Currency getZloty() {
		if ( Currency.ZLOTY == null ){
			Currency.ZLOTY = new Currency();
			Currency.ZLOTY.setCode("PLZ");
			Currency.ZLOTY.setSymbol("zł");
			Currency.ZLOTY.setTitle("Zloty");
			Currency.ZLOTY.setId(3L);
		}
		return Currency.ZLOTY;
	}
	
	/**
	 * Get all the taxes in Map (id: Tax)
	 * 
	 * @return
	 * @throws CriticalException
	 */
	public static Map getCurrencies() throws CriticalException {
		Map<Long, Currency> currencyMap = new HashMap<Long, Currency>();
		Connection con = null;
		try{
			con = DBHelper.getConnection();
			PreparedStatement stmt = con.prepareStatement(getCurrenciesSql);
			ResultSet rs = stmt.executeQuery();
			Currency currency = null;
			while( rs.next() ){
				currency = new Currency();
				currency.setId( DBHelper.makeLong(rs.getLong("id")) );
				currency.setTitle( rs.getString("title") );
				currency.setCode( rs.getString("code") );
				currency.setSymbol( rs.getString("sign") );
				currencyMap.put(currency.getId(), currency);
			}
			rs.close();
			stmt.close();
		}
		catch(SQLException e){
			logger.error("-error", e);
			throw new CriticalException(e);
		}
		finally{
			DBHelper.close(con);
		}
		return currencyMap;
	}

	
	/**
	 * 
	 * @return Returns the code.
	 */
	public String getCode() {
		return this.code;
	}

	/**
	 * 
	 * @param code
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return Returns the symbol.
	 */
	public String getSymbol() {
		return this.symbol;
	}
	
	/**
	 * @return Returns the title.
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * @param title The title to set.
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * @param symbol The symbol to set.
	 */
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	
	public String format( BigDecimal value ) {
		return this.getFormatter().format( value );
	}

	public String formatNoSymbol( BigDecimal value ) {
		return this.getNoSymbolFormatter().format( value );
	}

	private DecimalFormat getFormatter() {
		if ( this.formatter == null ){
			DecimalFormatSymbols symbols = new DecimalFormatSymbols();
			symbols.setGroupingSeparator(' ');
			symbols.setCurrencySymbol( this.getSymbol() );
			this.formatter = new DecimalFormat( 
				Currency.CURRENCY_PATTERN, 
				symbols
			);
			this.formatter.setGroupingUsed( true );
			this.formatter.setMaximumFractionDigits(2);
			this.formatter.setMinimumFractionDigits(2);
		}
		return this.formatter;
	}
	
	private DecimalFormat getNoSymbolFormatter() {
		if ( this.noSymbolFformatter == null ){
			DecimalFormatSymbols symbols = new DecimalFormatSymbols();
			symbols.setGroupingSeparator(' ');
			//symbols.setCurrencySymbol( this.getSymbol() );
			this.noSymbolFformatter = new DecimalFormat( 
				Currency.NOSYMBOL_CURRENCY_PATTERN, 
				symbols
			);
			this.noSymbolFformatter.setGroupingUsed( true );
			this.noSymbolFformatter.setMaximumFractionDigits(2);
			this.noSymbolFformatter.setMinimumFractionDigits(2);
		}
		return this.noSymbolFformatter;
	}
	
	/**
	 * @return Returns the id.
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @param id The id to set.
	 */
	public void setId(Long id) {
		this.id = id;
	}
	
	public static Currency get(long id) {
		if (id == 1) return getEuro();
		return getDollar();
	}
}
