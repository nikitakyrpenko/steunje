<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
  
<!--  Navigation table -->
<div class="admBtnGreen">
				<div class="imgL"></div>
				<div><c:choose><c:when test="${sessionScope.historyStack.size==1}"><a href="javascript: window.close();" class="admBtnText"><fmt:message key="BACK"/></a></c:when><c:otherwise> <a href="back.html" class="admBtnText"><fmt:message key="BACK"/></a></c:otherwise></c:choose></div>
				<div class="imgR"></div>
</div>

<div class="admBtnGreen">
				<div class="imgL"></div>
				<div> <a href="javascript: window.close();" class="admBtnText"><fmt:message key="CLOSE"/></a></div>
				<div class="imgR"></div>
</div>        

<div class="admBtnGreen admBtnBlue">
				<div class="imgL"></div>
				<div><a class="admBtnText callHelp" target="_blank" href="/admin/help/cms-help_nl.html">Help</a></div>
				<div class="imgR"></div>
</div>