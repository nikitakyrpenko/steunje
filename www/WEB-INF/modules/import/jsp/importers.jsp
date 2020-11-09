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
    <title>Importers</title>

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
                <fmt:message key="imp.Importers" />
            </td>
            <td style="background: #7FB577 url('../../images/titleRight.gif') right top no-repeat; width: 10px; height: 25px">&#160;</td>
        </tr>
    </table>

    <table cellpadding="0" cellspacing="0" align="center" class="admNavPanel" border="0">
        <tr class="admLightTD">
            <td class="admNamebar">Name</td>
            <td class="admNamebar">Module</td>
        </tr>    
        <c:forEach var="description" items="${descriptions}">
            <tr>
                <td class="admLightTD">
                    <a href="/admin/importer.html?action=getImporter&importerId=${description.importerId}">${description.importerName}</a>
                </td>
                <td class="admLightTD">
                    ${description.moduleId}
                </td>
            </tr>
        </c:forEach>
    </table>
    
    <jsp:include page="/WEB-INF/jsp/common/back_bar.jsp"/>

</body>
</html>