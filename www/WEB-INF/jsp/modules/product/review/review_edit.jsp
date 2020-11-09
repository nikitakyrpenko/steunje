<!--
  @(#)$Id$		
 
  Copyright (c) 2003 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.
 
  View of a list item edit interface.
 
  @author		Dmitry Dzifuta
  @version		$Revision$
-->

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ page import="java.util.*" %>
<%@ page import="com.negeso.framework.i18n.DatabaseResourceBundle,
		         com.negeso.module.product.review.ProductReview" %>

<%@ page session="true" %>
<%
  response.setHeader("Expires", "0");
  String langCode = (String) request.getAttribute("lang");
  DatabaseResourceBundle commonBundle = DatabaseResourceBundle.getInstance("dict_common.xsl", langCode);
  DatabaseResourceBundle productBundle = DatabaseResourceBundle.getInstance("dict_product.xsl", langCode);
  DatabaseResourceBundle rteBundle = DatabaseResourceBundle.getInstance("dict_newsletter.xsl", langCode);
%>

<HTML>
  <HEAD>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
    <TITLE><%=productBundle.getString("REVIEW_EDIT_TITLE")%></TITLE>
    <script language="JavaScript1.2" src="/script/jquery.min.js" type="text/javascript"/>
				<script type="text/javascript" src="/script/common_functions.js" language="JavaScript1.2"></script>
    <script language="JavaScript1.2" src="/script/calendar_picker.js" type="text/javascript"></script>
    <script language="JavaScript1.2" src="/script/media_catalog.js" type="text/javascript"/></script>
				<script type="text/javascript" src="/script/rtecom.js"></script>
				<script language="JavaScript" src="/script/rte_implement.js"></script>  
				<script type="text/javascript" src="/script/RTE_Adapter.js">/**/</script>     
				<script language="JavaScript">
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
	            win = open("rtecom/rfe.html", null, "height=562,width="+(3+wdth)+",status=no,toolbar=no,menubar=no,location=no");//, div, "dialogHeight: 460px; dialogWidth: 570px; dialogTop: 100px; dialogLeft: 100px; edge: Raised; center: Yes; help: Yes; resizable: No; status: Yes;");
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
                win = open("rtecom/text_editor.html", null, "height=562,width="+(3+wdth)+",status=no,toolbar=no,menubar=no,location=no");//, div, "dialogHeight: 460px; dialogWidth: 570px; dialogTop: 100px; dialogLeft: 100px; edge: Raised; center: Yes; help: Yes; resizable: No; status: Yes;");
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
  </HEAD>
  <BODY
    style="behavior: url(/script/webservice.htc) url(/script/clientmanager.htc)"
    id="ClientManager" xmlID="332"
  >

<br>

 <jsp:include page="/WEB-INF/jsp/common/header.jsp"/>
  <!--  Navigation table -->
  <div>
	  <table class="admNavPanel admPanelSpace"  border="0" cellpadding="0" cellspacing="0" align="center" >
	  <tr>
	    <td class="admNamebarLeft admNavbarImg "><img  src="/images/navBarLeft.gif" alt=""></td>
	    <td class="admNavbar admLeft"><a href="listreview?product_id=${review.productId}" class="admNone"><b>&lt;&lt;&lt;&nbsp;<%=commonBundle.getString("BACK")%></b></a></td>
	    <td class="admNavbar admLeft" align="right" style="text-align: right;"><a href="javascript: window.close();" class="admNone"><b>&lt;&nbsp;<%=commonBundle.getString("CLOSE")%>&nbsp;&gt;</b></a></td>
	    <td class="admNamebarLeft admNavbarImg "><img src="/images/navBarRight.gif" alt=""></td>
	  </tr>
	  </table>
  </div>
   
<!-- Title table -->
<table class="admNavPanel admPanelSpace admTableMarginMini" border="0" cellspacing="0" cellpadding="0" align="center">
    <tr>
      <td class="admNavbarImg"><img border="0" alt="" src="/images/titleLeft.gif"></td>
      <td class="admTitle"><%=productBundle.getString("REVIEW_EDIT_TITLE")%></td>
      <td class="admNavbarImg"><img border="0" alt="" src="/images/titleRight.gif"></td>
    </tr>
</table>

<form onSubmit="return validateForm(this);" method="GET" name="operateForm" enctype="multipart/form-data">
<input type="hidden" name="review_id" value='${review.id}'/>
<input type="hidden" name="action" value=""></input>
<input type="hidden" name="closeForm" value=""></input>
<input type="hidden" name="product_id" value='${review.productId}'></input>
<input type="hidden" name="article_id" value='${review.article.id}'></input>

<script>if('${review.id}'=='') document.title = '<%=productBundle.getString("ADD_REVIEW")%>';</script>

<table class="admNavPanel admTableMargin" align="center" cellpadding="0" cellspacing="0" border="0">

	<tr><td class="admNavbar" align="center" colspan=2>
	<c:choose>
		<c:when test="${review.id == '' || review.id == null}">
			<input class="admNavbarInp" type="button" name="add" value="<<%=commonBundle.getString("ADD")%>>"
			onClick="closeForm.value='false';if (validateForm(operateForm)) {operateForm.submit();}"/>
			<script>document.operateForm.action.value = "add"; </script>
		</c:when>
		<c:when test="${review.id != ''}">
			<input class="admNavbarInp" type="button" name="add" value="<<%=commonBundle.getString("SAVE")%>>"
			onClick="closeForm.value='false';if (validateForm(operateForm)) {operateForm.submit();}"/>
			<script>document.operateForm.action.value = "update"; </script>
		</c:when>                
	</c:choose>
	</td></tr>
	
    <tr><td class="admMainTD" width="30%"><%=productBundle.getString("AUTHOR")%>*</td>
    <td class="admLightTD" style="text-align: left;"><input type="text" name="author" class="admTextArea admWidth335" value="${review.author}" required="true"/></td></tr>

	<tr><td class="admMainTD"><%=commonBundle.getString("DATE")%>*</td>
	<td class="admLightTD"><input type="text" name="date" id="dateId" class="admTextArea admWidth200" readonly value="${review.formattedDate}" required="true"><img width="16" src="/images/calendar.gif" onclick="loadNewCalendar('/dialogs/dialog_calendar_picker.html','dateId','ddmmyyyy',false)" height="16" class="admHand" alt="Pick a date"></input>(dd-mm-yyyy)</td></tr>

    <tr><td class="admMainTD" width="30%"><%=productBundle.getString("RATING")%>*</td>
    <td class="admLightTD" style="text-align: left;">
	
	<select name="rating" class="admTextArea admWidth200">
	<% 
	   ProductReview review = (ProductReview)request.getAttribute("review");
	   for (int i=1; i<=10; i++) {
		   if (review.getRating() == i) {
			   pageContext.getOut().println("<option selected>" + i + "</option>");
		   }
		   else {
			   pageContext.getOut().println("<option>" + i + "</option>");   
		   }
	   }
	   pageContext.getOut().flush();
	%>
	</select>
	</td></tr>
    
    <tr><td class="admMainTD" width="30%"><%=rteBundle.getString("TEXT")%>*</td>
    <td class="admLightTD" style="text-align: left;">
		<img src="/images/mark_1.gif" 
				onclick="edit_text('article_text${review.article.id}','contentStyle', 595);"
				class="admBorder admHand" 
				alt="<%=commonBundle.getString("TEXT")%>"
		/>
		<div style="text-align:left;" id='article_text${review.article.id}' class="contentStyle"
			style="behavior:url(/script/article3.htc); margin: 0px;border: 1px solid #848484;">
			<c:out value="${review.article.text}" escapeXml="false"/>
		</div>
    </td></tr>
    
	<tr><td class="admNavbar" align="center" colspan=2>
	<c:choose>
		<c:when test="${review.id == '' || review.id == null}">
			<input class="admNavbarInp" type="button" name="add" value="<<%=commonBundle.getString("ADD")%>>"
			onClick="closeForm.value='false';if (validateForm(operateForm)) {operateForm.submit();}"/>
			<script>document.operateForm.action.value = "add"; </script>
		</c:when>
		<c:when test="${review.id != ''}">
			<input class="admNavbarInp" type="button" name="add" value="<<%=commonBundle.getString("SAVE")%>>"
			onClick="closeForm.value='false';if (validateForm(operateForm)) {operateForm.submit();}"/>
			<script>document.operateForm.action.value = "update"; </script>
		</c:when>                
	</c:choose>
	</td></tr>
    
</table>
</form>
  <!--  Navigation table -->
  <div>
	  <table class="admNavPanel admPanelSpace"  border="0" cellpadding="0" cellspacing="0" align="center" >
	  <tr>
	    <td class="admNamebarLeft admNavbarImg "><img  src="/images/navBarLeft.gif" alt=""></td>
	    <td class="admNavbar admLeft"><a href="listreview?product_id=${review.productId}" class="admNone"><b>&lt;&lt;&lt;&nbsp;<%=commonBundle.getString("BACK")%></b></a></td>
	    <td class="admNavbar admLeft" align="right" style="text-align: right;"><a href="javascript: window.close();" class="admNone"><b>&lt;&nbsp;<%=commonBundle.getString("CLOSE")%>&nbsp;&gt;</b></a></td>
	    <td class="admNamebarLeft admNavbarImg "><img src="/images/navBarRight.gif" alt=""></td>
	  </tr>
	  </table>
  </div>
</BODY>
</HTML>