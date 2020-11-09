package com.negeso.framework.util.json;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.negeso.framework.domain.User;
import com.negeso.framework.HttpException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class JsonSupportForController {
	protected final static String ROLE_ADMIN = "administrator";

	protected void writeToResponse(HttpServletResponse response, JsonObject obj) throws IOException {
		this.writeToResponse(response, new Gson().toJson(obj));
	}

	protected void writeToResponse(PrintWriter writer, JsonObject obj) throws IOException {
		this.writeToResponse(writer, new Gson().toJson(obj));
	}

	public void writeToResponse(HttpServletResponse response, String body) throws IOException {
		PrintWriter out = response.getWriter();
		out.print(body);
		out.close();
	}

	public static void write(HttpServletResponse response, String body) throws IOException {
		PrintWriter out = response.getWriter();
		out.print(body);
		out.close();
	}

	public void writeToResponse(PrintWriter writer, String body) throws IOException {
		writer.print(body);
	}

	public void writeErrorToResponse(HttpServletResponse response, String body, int status) throws IOException {
		response.setStatus(status);
		writeToResponse(response, body);
	}
	public static void writeError(HttpServletResponse response, String body, int status) throws IOException {
		response.setStatus(status);
		write(response, body);
	}

	public void writeErrorToResponse(HttpServletResponse response, String message) throws IOException {
		response.setStatus(400);
		JsonObject json = new JsonObject();
		json.addProperty("success", false);
		json.addProperty("message", message);
		writeToResponse(response, message);
	}

	public <T> T buildPojoFromRequest(HttpServletRequest req, Class<T> clazz) throws IOException {
		final BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()));
		return new Gson().fromJson(reader, clazz);
	}

	public void writeToResponseExcBody(HttpServletResponse res, int statusCode) {
		res.setStatus(statusCode);
	}

	protected void hasRole(User user, String role) throws HttpException {
		if (!role.equals(user.getType()) && !user.isSuperUser() && !"administrator".equals(user.getType())){
			throw new HttpException(403, "Secured resource");
		}

	}
}
