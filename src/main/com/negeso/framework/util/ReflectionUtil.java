package com.negeso.framework.util;

import com.negeso.framework.exception.ParseFileException;
import com.negeso.framework.exception.ValidationException;
import com.negeso.framework.io.xls.XlsColumnNumber;
import com.negeso.framework.io.xls.XlsObject;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.sql.Timestamp;


public class ReflectionUtil {

	public static <T>T toObject(Row row, Class<T> clazz) throws ParseFileException {
		T instance;
		try {
			instance = clazz.newInstance();
		} catch (InstantiationException e) {
			throw new ParseFileException("Unable to parse line " + row.getRowNum());
		} catch (IllegalAccessException e) {
			throw new ParseFileException("Unable to parse line " + row.getRowNum());
		}
		Field[] declaredFields = clazz.getDeclaredFields();

		for (Field field : declaredFields) {
			if (field.isAnnotationPresent(XlsColumnNumber.class)){
				int columnNumber = field.getAnnotation(XlsColumnNumber.class).value();
				boolean required = field.getAnnotation(XlsColumnNumber.class).required();
				try {
					if (String.class.isAssignableFrom(field.getType())){
						Cell cell = row.getCell(columnNumber, Row.RETURN_BLANK_AS_NULL);
						if (cell == null){
							if (required)
								throw new ParseFileException(String.format("Unable to parse line %d and column %c. ", row.getRowNum(), columnNumber+65));
						}else{
							String stringCellValue = cell.getStringCellValue();
							if (field.getAnnotation(XlsColumnNumber.class).encription() == Encryption.MD5){
								stringCellValue = MD5Encryption.md5(stringCellValue);
							}

							newValue(instance, field, stringCellValue);
						}
					}else if (Integer.class.isAssignableFrom(field.getType())){
						Integer value;
						Cell cell = row.getCell(columnNumber, Row.RETURN_BLANK_AS_NULL);
						if (cell != null){
							try {
								value = new Double(cell.getNumericCellValue()).intValue();
							} catch (IllegalStateException e) {
								value = new Integer(cell.getStringCellValue());
							}

							newValue(instance, field, value);
						}
					}else if (BigDecimal.class.isAssignableFrom(field.getType())){
						Cell cell = row.getCell(columnNumber, Row.RETURN_BLANK_AS_NULL);
						if (cell != null){
							double numericCellValue = cell.getNumericCellValue();
							BigDecimal value = BigDecimal.valueOf(numericCellValue);
							newValue(instance, field, value);
						}
					}else if (Boolean.class.isAssignableFrom(field.getType())){
						String val = row.getCell(columnNumber, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
						Boolean value = !(val.equals("N") || val.equalsIgnoreCase("false"));
						newValue(instance, field, value);
					}else if (Timestamp.class.isAssignableFrom(field.getType())){
						Date val = row.getCell(columnNumber).getDateCellValue();
						newValue(instance, field, new Timestamp(val.getTime()));
					}else if (field.getAnnotation(XlsObject.class) != null){
						Object object;
						int[] columnNumbers = field.getAnnotation(XlsObject.class).values();
						int valuesLength = columnNumbers.length;
						if (valuesLength == 0){
							object = toObject(row, field.getType());
						}else {
							Map<Integer, String> columnValues = new LinkedHashMap<Integer, String>(valuesLength);
							for(int i = 0; i < valuesLength; i++){
								columnNumber = columnNumbers[i];
								Cell cell = row.getCell(columnNumber, Row.RETURN_BLANK_AS_NULL);
								if (cell != null){
									String stringCellValue = cell.getStringCellValue();
									columnValues.put(valuesLength - i, stringCellValue);
								}
							}
							Assert.notEmpty(columnValues);
							Class<?> type = field.getType();
							object = type.getConstructor(Map.class).newInstance(columnValues);
						}

						newValue(instance, field, object);
					}else {
						String value;
						Cell cell = row.getCell(columnNumber, Row.RETURN_BLANK_AS_NULL);
						if (cell == null) {
//							throw new NullPointerException("No identifier found");
							newValue(instance, field, null);
						}else {
							try {
								value = cell.getStringCellValue();
							} catch (Exception e) {
								NumberFormat nf = new DecimalFormat("#.####");
								value = nf.format(cell.getNumericCellValue());
							}
							Class<?> type = field.getType();
							Object newInstance = type.getConstructor(String.class).newInstance(value);
							newValue(instance, field, newInstance);
						}

					}
				}catch (Exception ex){
					throw new ParseFileException(String.format("Unable to parse line %d and column %c. ", row.getRowNum()+1, columnNumber+65), ex);
				}
			}
		}

		if (instance instanceof Validator){
			try {
				((Validator) instance).validate();
			} catch (ValidationException e) {
				throw new ParseFileException(String.format("Row: %d. Message: %s", row.getRowNum()+1, e.getMessage()), e);
			}
		}

		return instance;
	}

	private static <T> void newValue(T instance, Field field, Object val) throws IllegalAccessException {
		boolean accessible = field.isAccessible();
		field.setAccessible(true);
		field.set(instance, val);
		field.setAccessible(accessible);
	}
}
