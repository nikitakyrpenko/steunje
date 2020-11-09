<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>

<table class="admNavPanel" align="center" cellpadding="0" cellspacing="0" border="0">
 <tr>
	<td class="admNavbarImg"><img src="/images/titleLeft.gif" alt="" border="0"/></td>
	<td class="admTitle">&#160;<%= request.getParameter("msg") %>&#160;</td>
	<td class="admNavbarImg"><img src="/images/titleRight.gif" alt="" border="0"/></td>
 </tr>
</table>
