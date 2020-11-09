<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ page session="true"%>
<%response.setHeader("Expires", "0");%>
<html>
<head>
	<META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
	<title><fmt:message key="eventDetails.title"/></title>
	<script type="text/javascript" src="/script/jquery.min.js">/**/</script>
	<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
<script language="JavaScript1.2" src="/script/common_functions.js" type="text/javascript"></script>
	<script language="JavaScript1.2" src="/script/calendar_picker.js" type="text/javascript"></script>
	<script language="JavaScript1.2" src="/site/core/script/validation.js" type="text/javascript"></script>
	<script type="text/javascript" src="/script/RTE_Adapter.js">/**/</script>
	<script id="localization"></script>
	<script language="JavaScript">
		document.all["localization"].src =
            "/dictionaries/dict_rte_" + getInterfaceLanguage() + ".js";
	</script>
	<script language="JavaScript1.2">
		var current="";
        var div = null;
        var fontFamily = null;
        var fontSize = null;
        var fontColor = null;
        var wdth = 505;
        var wn = null;
        var win =null;
        window.onfocus=fc;
        attachEvent("onbeforeunload",lld);
        attachEvent("onunload",ulld);
        
        function lld()
        {
            if (win!=null) {
                event.returnValue="The Editor is opened now! It is recommended to press Cancel and close the Editor first!";
            }
        }
        
        function ulld()
        {
            if (win!=null) {
                win.fl=true;
                win.close();
            }
        }
        
        function fc()
        {
            if (win!=null) {
                event.cancelBubble= true;
                win.focus();	
                return false;		
            }
        }
        
		var textDirection = null;
		var defaultStyle = null;
        
		/*
		function edit_form(id, default_style, div_width , text_dir)
        {
		  	if (win == null)
			{
	            div= document.all[id];
	            var str = div.innerHTML;
			  	wdth = div_width + 35; // width of content div
				textDirection = text_dir;
				defaultStyle = default_style;
	            win = open("rtecom/rfe.html", null, "height=562,width="+(3+wdth)+",status=no,toolbar=no,menubar=no,location=no"//, div, "dialogHeight: 460px; dialogWidth: 570px; dialogTop: 100px; dialogLeft: 100px; edge: Raised; center: Yes; help: Yes; resizable: No; status: Yes;");
	        }
        }
		
        function edit_text(id, default_style, div_width , text_dir)
        {
		    if (win == null) {
                div= document.all[id];
                var str = div.innerHTML;
                wdth = div_width + 35; // width of content div
                textDirection = text_dir;
                defaultStyle = default_style;
                win = open("rtecom/text_editor.html", null, "height=562,width="+(3+wdth)+",status=no,toolbar=no,menubar=no,location=no"//, div, "dialogHeight: 460px; dialogWidth: 570px; dialogTop: 100px; dialogLeft: 100px; edge: Raised; center: Yes; help: Yes; resizable: No; status: Yes;");
            }
        }
        */
        
        function edit_form(inp_id, default_style, div_width , text_dir)
        {
		  	var art_id = inp_id.substr(12, inp_id.length-1);
		  	RTE_Init(inp_id, inp_id, art_id, 3, 1, default_style, getInterfaceLanguage());
        }
        
        function edit_text(inp_id, default_style, div_width , text_dir)
        {
		  	var art_id = inp_id.substr(12, inp_id.length-1);
		  	RTE_Init(inp_id, inp_id, art_id, 3, 0, default_style, getInterfaceLanguage());
        }
        
        function getDiv()
        {
            return div;
        }
        
        function show(what)
        {
            if (current!="") document.all[current].style.visibility="hidden";
            current=what;
            if (current!="") document.all[current].style.visibility="visible";
        }
	</script>
	<?IMPORT namespace="negeso" implementation="/script/negesowidget.htc" >
</head>
<body id="ClientManager" style="behavior: url(/script/webservice.htc) url(/script/clientmanager.htc)">
	<br/>
	<jsp:include page="/WEB-INF/jsp/common/header.jsp"/>
	<jsp:include page="/WEB-INF/jsp/common/back_bar.jsp"/>
	<jsp:include page="/WEB-INF/jsp/common/navig_bar.jsp"/>
	<!-- Title table -->
	<table cellpadding="0" cellspacing="0" class="admNavPanel admSpaceSmall" border="0" align="center">
		<tr>
			<td colspan="3" style="text-align: right; height: 20px">
				<c:forEach var="language_item" items="${languageFilter}">
					<c:choose>
						<c:when test="${language_item.id == event.langId}">
							<span style="font-weight: bold; color: black; margin-right: 4px" id="lang_${language_item.id}"></span>
						</c:when>
						<c:otherwise>
							<a id="lang_${language_item.id}" style="font-weight: bold; margin-right: 4px; color: black; text-decoration: underline" href="?langId=${language_item.id}&id=${event.id}&categoryId=${param.categoryId}"></a>
						</c:otherwise>
					</c:choose>
					<script>document.getElementById('lang_${language_item.id}').innerHTML = '${language_item.code}'.toUpperCase();</script>
				</c:forEach>
			</td>
		</tr>
		<tr>
			<td style="background: #7FB577 url('../../images/titleLeft.gif') left top no-repeat; width: 10px; height: 25px">&#160;</td>
			<td class="admTitle"><fmt:message key="eventDetails.title"/></td>
			<td style="background: #7FB577 url('../../images/titleRight.gif') right top no-repeat; width: 10px; height: 25px">&#160;</td>
		</tr>
	</table>
	<form method="post" enctype="multipart/form-data"><!--onSubmit="return full_validate(this);"-->
		<input type="hidden" name="act" id="act" value="" />
		<input type="hidden" name="id" value="${event.id}" />
		<input type="hidden" name="categoryId" value="${param.categoryId}" />
 
		<table class="admNavPanel" cellspacing="0" cellpadding="0" align="center" border="0">
			<c:forEach var="fieldConf" items="${fieldConfigurations}">
		    	<tr>
		    		<td class="admMainTD admWidth100">
		    			${event.fields[fieldConf.id].title}
		    			<c:if test="${fieldConf.requiredField}">*</c:if>
		    		</td>
		    		<td class="admLightTD admLeft" style="padding-left: 4px">
		    			<c:choose>
		    				<c:when test="${fieldConf.type == 'RteArticle'}">
		    					<input type="hidden" name="${fieldConf.name}" value="${event.fields[fieldConf.id].value}" />
	    						<img src="/images/mark_1.gif" style="cursor: pointer;" onclick="edit_text('article_text${event.fields[fieldConf.id].value}', 'contentStyle', 585);"/>
	    						<div onclick="event.cancelBubble='true'; return false;" id="article_text${event.fields[fieldConf.id].value}" style="width: 99%; height: auto; margin-left: 2px; padding: 10px; behavior: url(/script/article3.htc);" class="contentStyle admTextArea">${articles[event.fields[fieldConf.id].value]}</div>
		    				</c:when>
		    				<c:otherwise>
			    				<input style="margin-left: 2px;" type="text" name="${fieldConf.name}" 
				  					<c:if test="${fieldConf.requiredField}">required="true"</c:if> 
		 							class="admTextArea" style="width: 200px" value="${event.fields[fieldConf.id].value}"/>
		    				</c:otherwise>
		    			</c:choose>
		    		</td>
		    	</tr>
			</c:forEach>
			<tr>
				<td class="admMainTD">
					<fmt:message key="eventDetails.publishingDate"/>
				</td>
				<td class="admLightTD" align="left" style="text-align: left">
					<input type="text" name="publishDate" id="publishDateId" readonly="true" class="admTextArea" style="width: 100px" value="<fmt:formatDate value="${event.publishDate}" type="both" pattern="dd-MM-yyyy"/>" />
					<img class="admHand" src="/images/calendar.gif" width="16px" height="16px" alt="Pick a date" onclick="loadNewCalendar('/dialogs/dialog_calendar_picker.html','publishDateId','ddmmyyyy',false)"/>
					(dd-MM-yyyy)
				</td>
	    	</tr>
	    	<tr>
				<td class="admMainTD">
					<fmt:message key="eventDetails.date"/>
				</td>
				<td class="admLightTD" align="left" style="text-align: left">
					<input type="text" name="date" id="dateId" readonly="true" class="admTextArea" style="width: 100px" required="true" value="<fmt:formatDate value="${event.date}" type="both" pattern="dd-MM-yyyy"/>" />
					<img class="admHand" src="/images/calendar.gif" width="16px" height="16px" alt="Pick a date" onclick="loadNewCalendar('/dialogs/dialog_calendar_picker.html','dateId','ddmmyyyy',false)"/>
					(dd-MM-yyyy)
				</td>
	    	</tr>
	    	<tr>
				<td class="admMainTD">
					<fmt:message key="LIST_EXPIRED_DATE"/>
				</td>
				<td class="admLightTD" align="left" style="text-align: left">
					<input type="text" name="expiredDate" id="expiredDateId" readonly="true" class="admTextArea" style="width: 100px" value="<fmt:formatDate value="${event.expiredDate}" type="both" pattern="dd-MM-yyyy"/>" />
					<img class="admHand" src="/images/calendar.gif" width="16px" height="16px" alt="Pick a date" onclick="loadNewCalendar('/dialogs/dialog_calendar_picker.html','expiredDateId','ddmmyyyy',false)"/>
					(dd-MM-yyyy)
				</td>
	    	</tr>
			<tr>
				<td class="admNavbar" align="center" colspan="9">
					<input class="admNavbarInp" type="button" onclick="this.form.act.value='save'; if (full_validate(this.form)) this.form.submit()" value="<&nbsp;<fmt:message key="SAVE"/>&nbsp;>"/>
					<input class="admNavbarInp" type="button" onclick="this.form.act.value='saveAndClose'; if (full_validate(this.form)) this.form.submit()" value="<&nbsp;<fmt:message key="SAVE_AND_CLOSE"/>&nbsp;>"/>
				</td>
			</tr>
		</table>
	</form>

<jsp:include page="/WEB-INF/jsp/common/back_bar.jsp"/>
  </BODY>
</HTML>
