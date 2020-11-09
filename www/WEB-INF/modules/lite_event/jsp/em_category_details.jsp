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
				<title><fmt:message key="EVENT.CATEGORIES"/></title>

				<script type="text/javascript" src="/script/jquery.min.js">/**/</script>
				<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
				<script language="JavaScript1.2" src="/script/common_functions.js" type="text/javascript"></script>
				<script language="JavaScript1.2" src="/script/calendar_picker.js" type="text/javascript"></script>
				<script language="JavaScript1.2" src="/site/core/script/validation.js" type="text/javascript"></script>
				<script language="JavaScript1.2">
        function selectIsLeaf(){
												if(document.mainForm.isLeaf.value=='true')
																document.mainForm.isLeaf.value=false;
												else
																document.mainForm.isLeaf.value=true;				
								}
				</script>
</head>
<body>
	<br/>

	<jsp:include page="/WEB-INF/jsp/common/header.jsp"/>
	<jsp:include page="/WEB-INF/jsp/common/back_bar.jsp"/>
	<jsp:include page="/WEB-INF/jsp/common/navig_bar.jsp"/>

	<!-- Title table -->

	<table cellpadding="0" cellspacing="0" class="admNavPanel admSpaceSmall" border="0" align="center">
		<tr>
			<td style="background: #7FB577 url('../../images/titleLeft.gif') left top no-repeat; width: 10px; height: 25px">&#160;</td>
			<td class="admTitle">
<c:choose>
	<c:when test="${(category.id > 0) && (fn:length(category.defaultTitle)>50)}">${fn:substring(category.defaultTitle,0,50)}...</c:when>
	<c:when test="${category.id > 0}">${category.defaultTitle}</c:when>
	<c:otherwise><fmt:message key="NEW_CATEGORY.HEADER"/></c:otherwise>
</c:choose>
			</td>
			<td style="background: #7FB577 url('../../images/titleRight.gif') right top no-repeat; width: 10px; height: 25px">&#160;</td>
		</tr>
	</table>
	<form method="POST" enctype="multipart/form-data" action="category_details.html" name="mainForm">
		<input type="hidden" name="mode" value="save"/>
		<input type="hidden" name="categoryId" value="${category.id}"/>
		<input type="hidden" name="parentId" value="${category.parentCategory.id}"/>

		<table class="admNavPanel" cellspacing="0" cellpadding="0"  align="center" border="0">
			<tr>
				<td colspan="2" class="admNamebar"><fmt:message key="TITLE"/></td>
			</tr>
						<c:forEach var="language" items="${languageFilter}">
							<tr>
								<td class="admMainTD" width="200">(<fmt:message key="LANGUAGE"/>: ${language.language})</td>
								<td class="admLightTD">
									<input type="text" name="title_${language.code}" id="title_${language.code}" class="admTextArea"
									 style="width: 400px;" value="${category.titles[language.id]}"
									 title="${category.titles[language.id]}" required="true" MAXLENGTH="256" />
								</td>
							</tr>
						</c:forEach>
		</table>
		<br/>
		<table class="admNavPanel" cellspacing="0" cellpadding="0"  align="center" border="0">
			<tr>
				<td colspan="2" class="admNamebar"><fmt:message key="CATEGORY_PROPERTIES"/></td>
			</tr>
			<tr>
				<td class="admMainTD" width="200">
					<fmt:message key="eventList.listPublishDate"/>
				</td>
				<td class="admLightTD" style="text-align: left;">
					<input type="text" name="publishDate" id="publishDateId" readonly="true" class="admTextArea" style="width: 70px" value="<fmt:formatDate value="${category.publishDate}" type="both" pattern="dd-MM-yyyy"/>" />
					<img class="admImg" src="/images/calendar.gif" width="16px" height="16px" alt="Pick a date" onClick="loadNewCalendar('/dialogs/dialog_calendar_picker.html','publishDateId','ddmmyyyy',false)"/>
					<fmt:message key="DATE_FORMAT_DD_MM_YYYY.label"/>
				</td>
	    	</tr>
	    	<tr>
				<td class="admMainTD">
					<fmt:message key="LIST_EXPIRED_DATE"/>
				</td>
				<td class="admLightTD" style="text-align: left;">
					<input type="text" name="expiredDate" id="expiredDateId" readonly="true" class="admTextArea" style="width: 70px" value="<fmt:formatDate value="${category.expiredDate}" type="both" pattern="dd-MM-yyyy"/>" />
					<img class="admImg" src="/images/calendar.gif" width="16px" height="16px" align="bottom" alt="Pick a date" onClick="loadNewCalendar('/dialogs/dialog_calendar_picker.html','expiredDateId','ddmmyyyy',false)"/>
					<fmt:message key="DATE_FORMAT_DD_MM_YYYY.label"/>
				</td>
	    	</tr>
	    	<tr>
				<td class="admMainTD">
					<fmt:message key="IS_LEAF.LABEL"/>
				</td>
				<td class="admLightTD" style="text-align: left;">
					<input type="hidden" name="isLeaf" value="${category.leaf}" />
					<input type="checkbox"style="margin: 0px 0px 0px 3px;" name="isLeafId" id="isLeafId"  onClick="selectIsLeaf()"
						<c:if test="${category.leaf}">checked</c:if>
						<c:if test="${category.events[0]!=null}">disabled title="<fmt:message key="LEAF_DISABLED.MSG"/>"</c:if>
					 />
				</td>
	    	</tr>

			<tr>
				<td colspan="2" class="admNavbar" align="center"><nobr>
					<input type="button" class="admNavbarInp"
					 value='&lt;&#160;<fmt:message key="SAVE"/>&#160;&gt;' onClick="if(full_validate(this.form)) { this.form.mode.value='save';this.form.submit(); }"/>
					&#160;&#160;
					<input type="button" class="admNavbarInp"
					 value='&lt;&#160;<fmt:message key="SAVE_AND_CLOSE"/>&#160;&gt;' onClick="if(full_validate(this.form)) { this.form.mode.value='saveAndClose';this.form.submit(); }"/>
				</nobr></td>
			</tr>
		</table>
	</form>

	<jsp:include page="/WEB-INF/jsp/common/back_bar.jsp"/>

</body>
</html>