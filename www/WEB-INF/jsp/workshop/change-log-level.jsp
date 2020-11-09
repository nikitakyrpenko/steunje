<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page contentType="text/html; charset=UTF-8"
	import="
		com.negeso.framework.domain.User,
		com.negeso.framework.security.SecurityGuard,
		org.apache.log4j.Logger,
		org.apache.log4j.Level,
		org.apache.commons.lang.StringUtils		
		"
 %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
response.setDateHeader("Expires", 0);
if (!SecurityGuard.isAdministrator((User)request.getSession().getAttribute("user_object"))) {
	response.sendRedirect("/admin/");
}
String clazz = request.getParameter("clazz");
if (StringUtils.isNotBlank(clazz)) {
	Level level = StringUtils.isBlank(request.getParameter("level"))
			? null
			: Level.toLevel(request.getParameter("level"));
	Logger.getInstance(clazz).setLevel(level);
}
pageContext.setAttribute("loggers", Logger.getRoot().getLoggerRepository().getCurrentLoggers());
%>

<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <title>Change Log Level</title>
</head>
<body>
<h1>Set new log level</h1>
<form method="GET" action="">
	<table>
		<tr>
			<td>Class / package: </td>
			<td><input type="text" name="clazz" value="${param.clazz}" style="width: 500"/></td>
		</tr>
		<tr>
			<td>Log level: </td>
			<td>
				<select name="level">
					<option value="">[Remove logger]</option>
					<option value="DEBUG">DEBUG</option>
					<option value="INFO">INFO</option>
					<option value="WARN">WARN</option>
					<option value="ERROR">ERROR</option>
					<option value="FATAL">FATAL</option>
				</select>
			</td>
		</tr>
		<tr>
			<td><input type="submit"/></td>
			<td>&nbsp;</td>
		</tr>
	</table>
</form>
<hr/>
<h1>Current levels</h1>
[Root logger] &ndash; <%= Logger.getRootLogger().getLevel() %><br/>
<c:forEach items="${loggers}" var="logger">
	<c:if test="${ not empty logger.level }">${logger.name} &ndash; ${logger.level}<br/></c:if>
</c:forEach>

</body>
</html>