<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<table class="admNavPanel " align="center" cellpadding="0" cellspacing="0" border="0">
	<tr>
		<c:choose>
			<c:when test="${prod.resellerId!=null}">
				<td class="admLightTD admLeft" width="30%"><fmt:message
					key="addProduct.serial" /></td>
				<td class="admMainTD admLeft" colspan="2"><input
					type="text" width="150px" name="serial" maxlength="50"
					class="admTextArea" required="true" value="<c:out value="${prod.oldId}"/>" /></td>
			</c:when>
			<c:otherwise>
				<td class="admLightTD admLeft" width="30%" style="text-align:left"><fmt:message
					key="addProduct.serial" /></td>
				<td class="admMainTD admLeft" style="text-align:left" colspan="2"><input
					disabled="disabled" type="text" width="150px" name="serial"
					maxlength="50" class="admTextArea"
					value="<c:out value="${prod.oldId}"/>" /></td>
			</c:otherwise>
		</c:choose>
	</tr>

	<input type="hidden" id="name_thlink_id" name="name_thlink_id"
		value='<c:out value="${linkSmall}"/>' />
	<input type="hidden" id="name_link_id" name="name_link_id"
		value='<c:out value="${linkBig}"/>' />

	<tr>
		<td class="admLightTD admLeft" width="30%"><fmt:message
			key="addProduct.image" /></td>
		
		<td class="admMainTD admLeft"><nobr>&#160;&#160;<img id="name_img_id"
			src='../<c:out value="${linkSmall}"/>' width="100" height="100"
			class="admBorder admHand"
			onClick="return openImage(name_link_id.value, 800, 600)" /></nobr>
		<table cellpadding="0" cellspacing="0" border="0">
			<tr>
				<c:choose>
					<c:when test="${ linkSmall!=null }">
						<td width="80%"><nobr>&#160;&#160;<a class="admAnchor" id="name_a_id"
							href='<c:out value="${linkSmall}"/>'
							onClick="return openImage(name_link_id.value, 800, 600)" target="_blank"><c:out value="${prod.imageURL }" /></a>
							</nobr>
				    	</td>
			    	</c:when>
			    	<c:otherwise>
			    		<td width="80%">
							<nobr>&#160;&#160;<a class="admAnchor" id="name_a_id"
			    			href='#'
							disabled='true'
							><fmt:message key="addProduct.notLoad"/></a></nobr>
				    	</td>
			    	</c:otherwise>
			    </c:choose>
				<td>
				<c:choose>
					<c:when test="${prod.resellerId==null}">
						<input class="admMainInp" disabled="disabled"
							name="selectImageButton"
							onClick='selectThumbnailImageDialog(name_link_id, name_thlink_id, name_a_id, name_img_id, "<c:out value="${media}"/>")'
							type="button" value='<<fmt:message key="button.Select"/>>'>
					</c:when>
					<c:otherwise>
						<input class="admMainInp" name="selectImageButton"
							onClick='selectThumbnailImageDialog(name_link_id, 
															    name_thlink_id, 
																name_a_id, 
																name_img_id, 
																"<c:out value="${media}"/>");
									 '
							type="button" value='<<fmt:message key="button.Select"/>>'>
					</c:otherwise>
					
				</c:choose>
				</td>
			</tr>
		</table>
		</td>
	</tr>


	<c:choose>
		<c:when test="${prod.resellerId!=null}">
			<tr>
				<td class="admLightTD admLeft" width="30%"><fmt:message
					key="addProduct.brand" /></td>
				<td class="admMainTD admLeft" colspan="2"><input
					type="text" width="150px" name="brand" maxlength="50"
					class="admTextArea" value="<c:out value="${prod.brand}"/>" /></td>
			</tr>

			<tr>
				<td class="admLightTD admLeft" width="30%"><fmt:message
					key="addProduct.prodTitle" /></td>
				<td class="admMainTD admLeft" colspan="2"><input
					type="text" width="150px" name="title" maxlength="50"
					class="admTextArea" required="true" value="<c:out value="${prod.description}"/>" /></td>
			</tr>

			<tr>
				<td class="admLightTD admLeft" width="30%" style="text-align:left"><fmt:message
					key="addProduct.description" /></td>
				<td class="admMainTD admLeft" colspan="2" style="text-align:left">
					<textarea 
						class="admTextArea" type="text" width="350px" name="descr" style="height:100px;" 
						size="180"><c:out value="${descr}"/></textarea>
				</td>
			</tr>
			<!--tr>
				<td class=" admLightTD" width="30%" style="text-align:left"><fmt:message
					key="productProperties.price" /></td>
				<td class=" admMainTD " colspan="2" style="text-align:left">
					<input class="admTextArea" type="text" width="150px" name="price"
						value="<c:out value="${price}" />" 
					/> &nbsp;<c:out value="${currencySymbol}"/>
				</td>
			</tr-->
		</c:when>
		<c:otherwise>
			<tr>
				<td class=" admLightTD" width="30%" style="text-align:left"><fmt:message
					key="addProduct.brand" /></td>
				<td class=" admMainTD " colspan="2" style="text-align:left"><input
					disabled="disabled" type="text" width="150px" name="brand"
					maxlength="50" class="admTextArea"
					value="<c:out value="${prod.brand}"/>" /></td>
			</tr>

			<tr>
				<td class=" admLightTD" width="30%" style="text-align:left"><fmt:message
					key="addProduct.prodTitle" /></td>
				<td class=" admMainTD " colspan="2" style="text-align:left"><input
					disabled="disabled" type="text" width="150px" name="title"
					maxlength="50" class="admTextArea"
					value="<c:out value="${prod.description}"/>" /></td>
			</tr>

			<tr>
				<td class=" admLightTD" width="30%" style="text-align:left"><fmt:message
					key="addProduct.description" /></td>
				<td class=" admMainTD " colspan="2" style="text-align:left">
					<textarea 
						class="admTextArea" type="text" width="350px" name="descr" style="height:100px;" 
						size="180"><c:out value="${descr}"/></textarea>
				</td>
			</tr>
		</c:otherwise>
	</c:choose>
</table>
	
</c:choose> 