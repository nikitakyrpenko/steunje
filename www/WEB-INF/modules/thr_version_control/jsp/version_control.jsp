<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="negeso"%>
<%
	response.setHeader("Expires", "0");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title><fmt:message key="THR_VERSION_CONTROL" />
</title>

<link href="/css/admin_style.css" rel="stylesheet" type="text/css" />

<link href="/css/customized-jquery-ui.css" rel="stylesheet"
	type="text/css" />
<!-- link href="/css/jquery-ui.custom.css" rel="stylesheet" type="text/css"/ -->

<script type="text/javascript" src="/script/jquery.min.js"></script>
<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
<script type="text/javascript" src="/script/cufon-yui.js"></script>
<script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
<script type="text/javascript" src="/script/common_functions.js"></script>
<script type="text/javascript">
	function saveValue() {
		var form = document.forms['opperateForm'];
		if (StringUtil.validateInteger($('input[name=thrVersionNumber]').val())) {
			form.submit();
		}else {
			alert("Wrong number format!");
		}
	}
</script>
</head>

<body>
	<form method="POST" name="opperateForm">
		<input type="hidden" name="action" value="save" />
		<negeso:admin>
			<table align="center" border="0" cellpadding="0" cellspacing="0"
				class="admTable">
				<tr>
					<td align="center" class="admNavPanelFont" style="height: auto;" colspan="2">
						<fmt:message key="THR_VERSION_CONTROL" />
					</td>
				</tr>
				<tr>
					<td class="admTableTDLast admWidth315" id="admTableTDtext"><fmt:message
							key="THR_MINIMUM_ALLOWED_VERSION_NUMBER" /> 
					</td>
					<td class="admTableTDLast" id="admTableTDtext">
						<input value="${thrVersionNumber}"
						name="thrVersionNumber" style="text-align: center; width: 50px;"
						data_type="number" />
					</td>
				</tr>
				<tr>
					<td class="admTableTDLast admWidth315" id="admTableTDtext"><fmt:message
							key="THR_MINIMUM_ALLOWED_VERSION_NUMBER_IOS" />
					</td>
					<td class="admTableTDLast" id="admTableTDtext">
						<input value="${thrVersionNumber_iOS}"
						name="thrVersionNumber_iOS" style="text-align: center; width: 50px;"
						data_type="number" />
					</td>
				</tr>
                <tr>
                    <td class="admTableTDLast admWidth315">Product info url</td>
                    <td class="admTableTDLast">
                        <input value="${url_to_get_product_info}"
                               name="url_to_get_product_info" style="width: 300px;" />
                    </td>
                </tr>
                <tr>
                    <td class="admTableTDLast admWidth315">Push marketing url</td>
                    <td class="admTableTDLast">
                        <input value="${url_to_get_push_marketing}"
                               name="url_to_get_push_marketing" style="width: 300px;" />
                    </td>
                </tr>
                <tr>
                    <td class="admTableTDLast admWidth315">Save order url</td>
                    <td class="admTableTDLast">
                        <input value="${url_to_save_order}"
                               name="url_to_save_order" style="width: 300px;" />
                    </td>
                </tr>
                <tr>
                    <td class="admTableTDLast admWidth315">Generate pdf url</td>
                    <td class="admTableTDLast">
                        <input value="${url_to_generate_pdf}"
                               name="url_to_generate_pdf" style="width: 300px;" />
                    </td>
                </tr>
                <tr>
                    <td class="admTableTDLast admWidth315">Help url</td>
                    <td class="admTableTDLast">
                        <input value="${url_to_help}"
                               name="url_to_help" style="width: 300px;" />
                    </td>
                </tr>
                <tr>
                    <td class="admTableTDLast admWidth315">Add to cart url</td>
                    <td class="admTableTDLast">
                        <input value="${url_to_add_to_cart}"
                               name="url_to_add_to_cart" style="width: 300px;" />
                    </td>
                </tr>
				<tr>
					<td class="admTableFooter" colspan="2">&nbsp;</td>
				</tr>
			</table>
		</negeso:admin>

		<table cellpadding="0" cellspacing="0" width="764px" align="center"
			border="0" class="Buttons">
			<tr>
				<td>
					<div class="admBtnGreenb">
						<div class="imgL"></div>
						<div>
							<input type="button" name="saveform"
								value='<fmt:message key="SAVE"/>' onclick="saveValue();" />
						</div>
						<div class="imgR"></div>
					</div>
			</tr>
		</table>

	</form>
</body>
</html>
