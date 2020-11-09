package com.negeso.module.webshop.service;

import com.google.gson.JsonObject;
import com.negeso.framework.friendly_url.UrlEntityType;
import com.negeso.framework.io.xls.ImportService;
import com.negeso.framework.log.LogEvent;
import com.negeso.framework.log.SystemLogService;
import com.negeso.module.imp.extension.ImportException;
import com.negeso.module.search.BuildIndexExecutor;
import com.negeso.module.search.SearchProducer;
import com.negeso.module.webshop.entity.*;
import com.negeso.framework.HttpException;
import com.negeso.module.webshop.util.ModuleConstants;
import org.apache.poi.ss.usermodel.Workbook;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

@Service
public class WebshopImportService {

	private final SessionFactory sessionFactory;
	private final FriendlyUrlForWebshop friendlyUrlForWebshop;

	@Autowired
	public WebshopImportService(@Qualifier("sessionFactory") SessionFactory sessionFactory, FriendlyUrlForWebshop friendlyUrlForWebshop) {
		this.sessionFactory = sessionFactory;
		this.friendlyUrlForWebshop = friendlyUrlForWebshop;
	}


	public Thread[] doImport(HttpServletRequest req, MultipartFile file, SystemLogService systemLogger, String serviceType) throws ImportException {
		Class clazz;
		Thread[] threads = null;

		SystemLogService.resetCounter();
		try {
			if (file == null) {
				throw new FileNotFoundException("Unable to resolve file");
			}

			String type = ServletRequestUtils.getRequiredStringParameter(req, "importType");
			clazz = resolveClass(type);
			serviceType += "_" + type.toUpperCase();

			ImportService<Workbook> service = new ImportService<Workbook>(systemLogger, ModuleConstants.MODULE_KEY, serviceType);
			if (Product.class.isAssignableFrom(clazz)){
				Session session = sessionFactory.getCurrentSession();
				Transaction transaction = session.beginTransaction();
				SQLQuery sqlQuery = session.createSQLQuery("UPDATE ws_products SET visible = FALSE");
				sqlQuery.executeUpdate();
				transaction.commit();
			}
			Workbook workbook = service.resolveFile(file);
			if (workbook == null) {
				throw new FileNotFoundException("Workbook is null");
			}
			boolean removeOld = PriceListProduct.class.isAssignableFrom(clazz) || PriceListStandard.class.isAssignableFrom(clazz)
					|| PriceListProductGroup2Customer.class.isAssignableFrom(clazz) || PriceListProduct2Customer.class.isAssignableFrom(clazz);
			threads = service.export(workbook, clazz, sessionFactory, removeOld);

			threads[0].join();
			threads[1].join();

			if (Product.class.isAssignableFrom(clazz)) {
				friendlyUrlForWebshop.deleteAllUrlsByType(UrlEntityType.PRODUCT_CATEGORY);
				friendlyUrlForWebshop.deleteAllUrlsByType(UrlEntityType.PRODUCT);
				friendlyUrlForWebshop.insertAllCategoryUrls();
				friendlyUrlForWebshop.insertAllProductUrls();
				if (!SearchProducer.isBuildingIndex())
					BuildIndexExecutor.build();
			}
		} catch (FileNotFoundException e) {
			throw new ImportException(e);
		} catch (UnsupportedOperationException e) {
			throw new ImportException(e);
		} catch (InterruptedException e) {
			throw new ImportException(e);
		} catch (ServletRequestBindingException e) {
			throw new ImportException(e);
		} catch (IOException e) {
			throw new ImportException(e);
		}
		return threads;
	}

	private Class resolveClass(String type) {
		Class clazz;

		if (type.equals("users")) {
			clazz = Customer.class;
		} else if (type.equals("products")) {
			clazz = Product.class;
		} else if (type.equals("dis-product")) {
			clazz = PriceListProduct.class;
		} else if (type.equals("dis-standard")) {
			clazz = PriceListStandard.class;
		} else if (type.equals("dis-group_client")) {
			clazz = PriceListProductGroup2Customer.class;
		} else if (type.equals("dis-product_client")) {
			clazz = PriceListProduct2Customer.class;
		} else
			throw new UnsupportedOperationException("Unsupported class type");

		return clazz;
	}

	public List<LogEvent> getLogs(String[] logIdsArr) throws HttpException {
		List<LogEvent> all = null;
		try {
			all = SystemLogService.getAll(logIdsArr);
		} catch (Exception e) {
			throw new HttpException(404, "Unable to find logs", e);
		}
		return all;
	}

	public JsonObject progress() {
		JsonObject obj = new JsonObject();
		if (SystemLogService.progress == null) SystemLogService.resetCounter();
		obj.addProperty("total", SystemLogService.rows);
		obj.addProperty("progress", SystemLogService.progress.intValue());
		obj.addProperty("buffer", SystemLogService.buffer.intValue());

		return obj;
	}
}
