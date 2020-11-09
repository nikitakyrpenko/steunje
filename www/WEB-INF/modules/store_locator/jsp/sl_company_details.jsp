<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ page session="true"%>
<%response.setHeader("Expires", "0");%>
<HTML>
<HEAD>
<TITLE><fmt:message key="companyDetails.title"/></TITLE>
<META http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="/css/admin_style.css" rel="stylesheet" type="text/css" />

<script type="text/javascript" src="/script/jquery.min.js">/**/</script>
<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
<script type="text/javascript" src="/script/common_functions.js"></script>
<script language="JavaScript1.2" src="/script/media_catalog.js" type="text/javascript"></script>
<script language="JavaScript">

	function selectImageDialog(width, height)
	{
		if ( width == 0 ) width = null;
		if ( height == 0 ) height = null;
		var	absLocation = window.location.href.replace(/(admin\/).*/,"\$1");
		if ( (height == null) && (width != null) ){
	        strPage = "?command=get-file-uploader-face&mode=image" + 
	            "&type=by_width" +
	            "&force_resize_mode=proportional" +
	            "&width=" + width;
		}
		else if ( (height != null) && (width == null) ){
	        strPage = "?command=get-file-uploader-face&mode=image" + 
	            "&type=by_height" +
	            "&force_resize_mode=proportional" +
	            "&height=" + height;
		}
		else{
	        strPage = "?command=get-file-uploader-face&mode=image" + 
	            "&type=by_width" +
	            "&force_resize_mode=proportional" +
	            "&width=" + width +
	            "&height=" + height;
		}	
		strPage = absLocation + strPage;
		strAttr = "resizable:on; scroll:on; status:off; dialogWidth:605px; dialogHeight:551px";
		showModalDialog(strPage, null, strAttr).then(function(){
			if (result != null){
				if (result.resCode == "OK"){
					document.mainForm.imageLink.value = result.realImage;
					document.mainForm.photoImage.outerHTML = "<img hspace='5' vspace='5' id='photoImage' " + "src='../" + result.realImage +"'>";
				} else alert("null!!!!");
		    }
		});
	}

</script>
</HEAD>
<BODY>
<br/>
<jsp:include page="/WEB-INF/jsp/common/header.jsp"/>
<jsp:include page="/WEB-INF/jsp/common/back_bar.jsp"/>
<jsp:include page="/WEB-INF/jsp/common/navig_bar.jsp"/>
<table cellpadding="0" cellspacing="0" class="admNavPanel admSpaceSmall" border="0" align="center">
<tr>
	<td style="background: #7FB577 url('../../images/titleLeft.gif') left top no-repeat; width: 10px; height: 25px">&#160;</td>
	<td class="admTitle">
		<c:choose>
			<c:when test="${shops!=null}"><fmt:message key="companyDetails.linkEdit"/></c:when>
        	<c:otherwise><fmt:message key="companyDetails.linkAdd"/></c:otherwise>
        </c:choose>
	</td>
	<td style="background: #7FB577 url('../../images/titleRight.gif') right top no-repeat; width: 10px; height: 25px">&#160;</td>
</tr>
</table>
<form method="GET" name="mainForm">
<table cellpadding="0" cellspacing="0" align="center" class="admNavPanel" border="0">
 <tr>
	<td class="admNavbar">
		<table cellpadding="0" cellspacing="0" width="100%" border="0">
			<tr>
				<td class="admLightTD admLeft" width="30%">
					<label for="title"><fmt:message key="listTitle"/></label>		
				</td>
				<td class="admMainTD admLeft">
					<input class="admTextArea" type="text" maxlength="50" name="title" required="true" value="${company.title}"/>
				</td>
			</tr>
			<tr>
				<td class="admLightTD admLeft" width="30%"><fmt:message key="listImage"/></td>
				<td class="admMainTD admLeft">
					<table width="100%" height="100%" cellspacing="0" cellpadding="0" border="0">
						<tr>
							<td>
								&#160;&#160;<img id="photoImage" 
									src="<c:choose><c:when test="${company.image!=null&&company.image!=''}">${company.image}</c:when><c:otherwise>/images/noImage.gif</c:otherwise></c:choose>" 
									width="100px" 
									height="100px" 
									class="admBorder admHand" 
									onClick="selectImageDialog(200,200)"/>
							</td>
							<td style="text-align: left; vertical-align: middle">
								<input name="selectImageButton" 
			                    	onClick="selectImageDialog(200,200)" 
			                    	type="button" 
			                    	class="admMainInp admWidth125" value="&lt;&#160;<fmt:message key="companyDetails.selectImage"/>&#160;&gt;"/>
			                    <br/>
			                    <input type="checkbox" name="clear_image" value="on"/>&#160;<strong><fmt:message key="companyDetails.clearImage"/></strong>
								<input type="hidden" 
									name="image" 
									id="imageLink" 
									value="<c:choose><c:when test="${company.image!=null&&company.image!=''}">${company.image}</c:when><c:otherwise></c:otherwise></c:choose>"/>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td class="admLightTD admLeft" width="30%">
					<label for="title"><fmt:message key="listLink"/></label>		
				</td>
				<td class="admMainTD admLeft">
					<input class="admTextArea" type="text" maxlength="50" name="link" required="true" value="${company.link}"/>
				</td>
			</tr>
		</table>
	</td>
 </tr>
</table>
<c:choose>
<c:when test="${shops!=null}">
	<input type="hidden" name="act" value="save_company"/>
	<input type="hidden" name="id" value="${company.id}"/>
	<table cellpadding="0" cellspacing="0" align="center" class="admNavPanel" border="0">
		<tr>
			<td class="admNavbar">
				<table cellpadding="0" cellspacing="0" width="100%" border="0">
					<tr>
						<td class="admVertPadding" align="center" style="border-top: 3px solid white">
							<input type="button" class="admNavbarInp" value='&lt;&#160;<fmt:message key="companyDetails.save"/>&#160;&gt;' onClick="this.form.submit()"/>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
	<table cellpadding="0" cellspacing="0" align="center" class="admNavPanel" border="0">
		<tr> 
			<td class="admNamebar"><fmt:message key="companyDetails.listTitle"/></td>
			<td class="admNamebar" colspan="2"><fmt:message key="companyDetails.listAction"/></td>
		</tr>
		<c:forEach var="shop" items="${shops}">
			<tr>
				<td class="admMainTD"><a href="?act=shop_details&id=${shop.id}" style="font-weight: bold; cursor: hand; color: black">${shop.country}</a></td>
				<td class="admLightTD" width="30px"><a href="?act=shop_details&id=${shop.id}"><img class="admImg" style="cursor: hand" src="/images/edit.gif" width="31px" height="27px" alt="<fmt:message key="button.alt.edit"/>"/></a></td>
				<td class="admDarkTD" width="30px"><a href="?act=remove_shop&id=${shop.id}"><img class="admImg" style="cursor: hand" src="/images/delete.gif" width="31px" height="27px" alt="<fmt:message key="button.alt.delete"/>"/></a></td>
			</tr>
		</c:forEach>
	</table>
	<table cellpadding="0" cellspacing="0" align="center" class="admNavPanel" border="0">
		<tr>
			<td class="admNavbar">
				<table cellpadding="0" cellspacing="0" width="100%" border="0">
					<tr>
						<td class="admVertPadding" align="center" style="border-top: 3px solid white">
							<input type="button" class="admNavbarInp" value='&lt;&#160;<fmt:message key="companyDetails.addShop"/>&#160;&gt;' onClick="window.location.href='?act=shop_details&companyId=${company.id}'"/>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</c:when>
<c:otherwise>
	<input type="hidden" name="act" value="add_company"/>
	<input type="hidden" name="regionId" value="${param.regionId}"/>
	<table cellpadding="0" cellspacing="0" align="center" class="admNavPanel" border="0">
		<tr>
			<td class="admNavbar">
				<table cellpadding="0" cellspacing="0" width="100%" border="0">
					<tr>
						<td class="admVertPadding" align="center" style="border-top: 3px solid white">
							<input type="button" class="admNavbarInp" value='&lt;&#160;<fmt:message key="companyDetails.addCompany"/>&#160;&gt;' onClick="this.form.submit()"/>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</c:otherwise>
</c:choose>
</form>
<jsp:include page="/WEB-INF/jsp/common/back_bar.jsp"/>
</BODY>
</HTML>
