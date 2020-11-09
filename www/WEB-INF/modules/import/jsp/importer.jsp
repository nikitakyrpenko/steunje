<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page session="true"%>
<%response.setHeader("Expires", "0");%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
    <title><fmt:message key="imp.importer"/> '${param.importerId}'</title>

				<script type="text/javascript" src="/script/jquery.min.js">/**/</script>
				<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
				<script language="JavaScript1.2" src="/script/common_functions.js" type="text/javascript"></script>
</head>
<body>
    <br/>

    <jsp:include page="/WEB-INF/jsp/common/header.jsp"/>
    <jsp:include page="/WEB-INF/jsp/common/back_bar.jsp"/>
    <jsp:include page="/WEB-INF/jsp/common/navig_bar.jsp"/>

    <table cellpadding="0" cellspacing="0" class="admNavPanel admSpaceSmall" border="0" align="center">
        <tr>
            <td style="background: #7FB577 url('../../images/titleLeft.gif') left top no-repeat; width: 10px; height: 25px">&#160;</td>
            <td class="admTitle">
                <fmt:message key="imp.import_data"/>
            </td>
            <td style="background: #7FB577 url('../../images/titleRight.gif') right top no-repeat; width: 10px; height: 25px">&#160;</td>
        </tr>
    </table>

    <form method="POST" name="importForm" action="/admin/import.html" enctype="multipart/form-data">
        <input type="hidden" name="importerId" value="${param.importerId}"/>
        <table cellpadding="0" cellspacing="0" align="center" class="admNavPanel" border="0">
            <c:if test="${!empty values['error']}">
                <tr>
                    <td class="admLightTD admLeft" colspan="3">
                        <font color="#FF0000"><b><fmt:message key="Error" />: ${values['error']}</b></font>
                    </td>
                </tr>
            </c:if>
            <tr>
                    <td class="admMainTD admWidth150">&#160;&#160;<fmt:message key="imp.select_file_for_import"/></td>
                    <td class="admLightTD admLeft">
                        <input class="admTextArea admWidth200" type="file" name="importFile" onKeyPress="return false;"/>
                    </td>
                    <td class="admLightTD admLeft">
                        <b><fmt:message key="Note"/>:</b> <fmt:message key="imp.operation_can_take_a_long_time" /> 
                    </td>
            </tr>
        <tr>
            <td colspan="3" class="admNavPanel admNavbar admCenter">
                <input class="admNavbarInp" name="saveButton" value="&lt; <fmt:message key="IMP.UPLOAD"/> &gt;" type="submit" />
            </td>
        </tr>
    </table>
        
    </form>   
    
    
<c:if test="${!empty values['events']}">    
    <table cellpadding="0" cellspacing="0" border="0" class="admNavPanel admPanelSpace" align="center">
        <tr>
            <td class="admNavbarImg"><img src="/images/titleLeft.gif" alt="" border="0"/></td>
            <td class="admTitle">
                <fmt:message key="imp.import_results" />
            </td>
            <td class="admNavbarImg"><img src="/images/titleRight.gif" alt="" border="0"/></td>
        </tr>
    </table>
    <table class="admNavPanel" cellspacing="0" cellpadding="0" align="center">
            <tr class="admLightTD">
                <td class="admNamebar"><b><fmt:message key="DATE"/></b></td>
                <td class="admNamebar"><b><fmt:message key="imp.author"/></b></td>
                <td class="admNamebar"><b><fmt:message key="imp.event"/></b></td>
                <td class="admNamebar"><b><fmt:message key="imp.description"/></b></td>
                <td class="admNamebar"><b><fmt:message key="imp.result"/></b></td>
            </tr>
            <c:forEach var="event" items="${values['events']}">
                <tr>
                    <td class="admLightTD">${event.formattedDate}</td>
                    <td class="admDarkTD">${event.author}</td>
                    <td class="admMainTD">${event.event}</td>
                    <td class="admLightTD">${event.description}</td>
                    <td class="admDarkTD">${event.result}</td>
                </tr>
            </c:forEach>
    </table>
</c:if>    
    <jsp:include page="/WEB-INF/jsp/common/back_bar.jsp"/>

</body>
</html>