<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%
	response.setHeader("Expires", "0");
%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="negeso"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title><fmt:message key="FLIP_BOOK_MODULE" />
</title>
<link href="/css/admin_style.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="/script/jquery.min.js"></script>
<script type="text/javascript" src="/script/cufon-yui.js"></script>
<script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
<script language="JavaScript1.2" src="/script/common_functions.js" type="text/javascript"></script>
</head>



<body>
	<negeso:admin>
		<table align="center" border="0" cellpadding="0" cellspacing="0" class="admTable">
            <tr>
                <td align="center" class="admNavPanelFont"  style="height:auto;"  colspan="5">
                    <fmt:message key="FLIP_BOOK_MODULE" />
                </td>                
            </tr>
		</table>
	</negeso:admin>
	<table cellpadding="0" cellspacing="0" width="764px" align="center" border="0" class="Buttons">
		<tr>
			<td>
				<form method="POST">
					<input type="hidden" name="act" value="resetCache">
					<div class="admBtnGreenb">
						<div class="imgL"></div>
						<div>
							<input type="submit" name="saveform" value='<fmt:message key="CM_CLEAR_CACHE"/>' />
						</div>
						<div class="imgR"></div>
					</div>
				</form>
            </td>
		</tr>
	</table>
</body>
</html>