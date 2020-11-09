<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="negeso"%>
<%
	response.setHeader("Expires", "0");
%>
<div id="editProductDialog" class="editProductDialog"
	title='<c:choose><c:when test="${product.id > 0}"><fmt:message key="THR_EDIT_PRODUCT" /></c:when><c:otherwise><fmt:message key="THR_ADD_PRODUCT" /></c:otherwise></c:choose>'>
	<form id="editForm">
		<input name="action" type="hidden" value="save" />
		<input name="id" type="hidden" value="${product.id}" />
		<table align="center" border="0" cellpadding="0" cellspacing="0"
			class="admTable" style="width: 100%;">
			<tr>
				<td class="admTableTD" style="width: 25%"><fmt:message
						key="THR_PRODUCT_BARCODE" />*
				</td>
				<td class="admTableTDLast admFormValidationErrorMessage"><input name="barCode"
					value="${product.barCode}" type="text" class="admTextArea" required="true">
				</td>
			</tr>
			<tr>
				<td class="admTableTD" style="width: 25%"><fmt:message
						key="THR_PRODUCT_TITLE" />
				</td>
				<td class="admTableTDLast"><input name="title"
					value="${product.title}" type="text" class="admTextArea">
				</td>
			</tr>
			<tr>
				<td class="admTableTD" style="width: 25%"><fmt:message
						key="THR_PRODUCT_PRICE" />*
				</td>
				<td class="admTableTDLast admFormValidationErrorMessage"><input name="price"
					value='<fmt:formatNumber type="number" pattern="#0.00" value="${product.price}" />' type="text" class="admTextArea" required="true" data_type="currency">
				</td>
			</tr>
			<tr>
				<td class="admTableTD" style="width: 25%"><fmt:message
						key="THR_PRODUCT_DESCRIPTION" />
				</td>
				<td class="admTableTDLast"><textarea rows="3"
						class="admTextArea" name="description"><c:out value="${product.description}" /></textarea>
				</td>					
			</tr>
			<tr>
				<td class="admTableTD" style="width: 25%"><fmt:message
						key="THR_PRODUCT_SHOW" />
				</td>
				<td class="admTableTDLast">
					<select name="show">
						<option value="y"
							<c:if test="${product.show == 'y'}">selected="selected"</c:if> 
						><fmt:message key="THR_PRODUCT_SHOW_YES" /></option>
						<option value="n"
							<c:if test="${product.show == 'n'}">selected="selected"</c:if> 
						><fmt:message key="THR_PRODUCT_SHOW_NO" /></option>
						<option value="t"
							<c:if test="${product.show == 't'}">selected="selected"</c:if> 
						><fmt:message key="THR_PRODUCT_SHOW_TEST" /></option>
					</select>
				</td>					
			</tr>
			<tr>
				<td class="admTableTD" style="width: 25%"><fmt:message
						key="THR_PRODUCT_IMAGE" />
				</td>
				<td class="admTableTDLast">
					<table align="left" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td>
								<input type="hidden" name="image" id="hiddenImageField" value="${product.image}">
								<img id="img_id" src="${product.image}" class="admBorder admHand" width="${thrImageWidth}" height="${thrImageHeight}" />
							</td>
							<td>
								<div class="admNavPanelInp">
				                   <div class="imgL"></div>
				                   <div>
				               		<input onClick="selectThumbnailImageDialog() "type="button" class="admNavPanelInp" value='<fmt:message key="SELECT" />' />
				                   </div>
				                   <div class="imgR"></div>
				               </div>
							</td>
						</tr>
					</table>
				</td>					
			</tr>
			<tr>
				<td class="" colspan="2">&nbsp;</td>					
			</tr>
		</table>
	</form>
</div>