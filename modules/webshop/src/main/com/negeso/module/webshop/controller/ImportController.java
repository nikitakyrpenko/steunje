package com.negeso.module.webshop.controller;

import com.google.gson.*;
import com.negeso.framework.controller.SessionData;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.User;
import com.negeso.framework.log.LogEvent;
import com.negeso.framework.log.SystemLogConstants;
import com.negeso.framework.log.SystemLogService;
import com.negeso.module.imp.extension.ImportException;
import com.negeso.framework.HttpException;
import com.negeso.module.webshop.service.WebshopImportService;
import com.negeso.framework.util.json.JsonSupportForController;
import com.negeso.module.webshop.util.ModuleConstants;
import com.negeso.module.webshop.util.PathVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Controller
@SessionAttributes(SessionData.USER_ATTR_NAME)
public class ImportController extends JsonSupportForController {

	private final WebshopImportService webshopImportService;

	@Autowired
	public ImportController(WebshopImportService webshopImportService) {
		this.webshopImportService = webshopImportService;
	}

	@RequestMapping(value = {"/webshop/api/import", "/webshop/api/import/*"})
	public void get(HttpServletRequest req, HttpServletResponse res, @ModelAttribute(SessionData.USER_ATTR_NAME) User user) throws IOException, HttpException{
		super.hasRole(user, ROLE_ADMIN);
		String type = PathVariable.getStringParameter(req, "/webshop/api/import/");

		if (type == null || "progress".equals(type)){
			JsonObject jsonObject = this.webshopImportService.progress();
			super.writeToResponse(res, jsonObject.toString());
		}else if ("logs".equals(type)){
			String[] logIds = ServletRequestUtils.getStringParameters(req, "logId");
			List<LogEvent> logs = this.webshopImportService.getLogs(logIds);
			super.writeToResponse(res, new Gson().toJson(logs));
		}
	}

	@RequestMapping(value = "/webshop/api/import", method = RequestMethod.POST)
	public void importFile(HttpServletRequest req, HttpServletResponse res, @ModelAttribute(SessionData.USER_ATTR_NAME) User user, @RequestParam(value = "file", required = false) MultipartFile file) throws IOException, HttpException {
		super.hasRole(user, ROLE_ADMIN);

		SystemLogService systemLogger = new SystemLogService(null, null);
		String serviceType = "IMPORT";
		try {
			Thread[] threads = this.webshopImportService.doImport(req, file, systemLogger, serviceType);
			JsonObject json = this.buildJsonResponse(threads);
			super.writeToResponse(res, json.toString());
		} catch (ImportException e) {
			String msg = e.getMessage();
			super.writeErrorToResponse(res, msg);
			systemLogger.addEvent(SystemLogConstants.Event.INFO, Thread.currentThread().getName(), msg, SystemLogConstants.Result.ERROR, ModuleConstants.MODULE_KEY, serviceType);
		}

		this.saveLogs(systemLogger);
	}

	private JsonObject buildJsonResponse(Thread[] threads) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("success", true);
		JsonArray logKeys = new JsonArray();
		for (Thread thread : threads) {
			logKeys.add(new JsonPrimitive(thread.getName()));
		}
		jsonObject.add("logKeys", logKeys);

		return jsonObject;
	}

	private void saveLogs(SystemLogService systemLogger) {
		Connection conn = null;
		try {
			conn = DBHelper.getConnection();
			systemLogger.setConnection(conn);
			systemLogger.flush();
		} catch (SQLException e) {
			systemLogger.getEvents().clear();
		}finally {
			DBHelper.close(conn);
		}
	}

}
