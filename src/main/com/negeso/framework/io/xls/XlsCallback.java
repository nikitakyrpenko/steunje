package com.negeso.framework.io.xls;

import org.apache.poi.ss.usermodel.Workbook;

public interface XlsCallback<T> {
	T process(Workbook book);
}
