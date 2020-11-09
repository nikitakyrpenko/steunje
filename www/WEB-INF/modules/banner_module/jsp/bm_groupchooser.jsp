<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ page session="true"%>
<%
	response.setHeader("Expires", "0");
	String parentId = (String)request.getAttribute("parentId");
	Boolean activated = (Boolean) request.getAttribute("activated");
%>
<html>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
    <title><fmt:message key="BM.GROUP_CHOOSER"/></title>
	<script type="text/javascript" src="/script/conf.js"/>
	<link rel="stylesheet" type="text/css" href="/css/admin_style.css"/>
    <link rel="stylesheet" type="text/css" href="/css/customized-jquery-ui.css"/>
    <link rel="stylesheet" type="text/css" href="/css/jquery-ui.css"/>
    <link rel="stylesheet" type="text/css" href="/css/jquery-ui.custom.css"/>
    <script language="JavaScript" src="/script/jquery.min.js">/**/</script>
    <script type="text/javascript" src="/script/jquery-ui.custom.min.js">/**/</script>
    <script language="JavaScript" src="/script/common_functions.js">/**/</script>
</head>

<body class="dialogSmall">
    <%@ page import="com.negeso.framework.controller.SessionData" %>
    
    <script type="text/javascript">

	var caller = new Object();
	if(window.dialogArguments) {
		caller = window.dialogArguments;
	} else {
	   caller = window.opener;
	}
	
    function save(){
    	var str_groups = '';
    	
    	var arr_items = document.getElementsByTagName("input");
    	for(var i = 0; i < arr_items.length; i++){
    		if(arr_items[i].type == "checkbox" && arr_items[i].checked){
   				str_groups = str_groups + arr_items[i].value + ';';
    		}
    	}
    	
    	if(str_groups != '')
    		caller.document.getElementById('div_groups').innerHTML = '<fmt:message key="BM.SOME_SELECTED_GROUPS"/>';
    	else
    		caller.document.getElementById('div_groups').innerHTML = '<fmt:message key="BM.NO_SELECTED_GROUPS"/>';
    		
    	caller.document.getElementById('bm_groups').value = str_groups;

		caller.focus();    	
    	window.close();
    };
    
    function groups_init(){
    	str_groups = caller.document.getElementById('bm_groups').value;
    	
    	var arr_items = document.getElementsByTagName("input");
    	for(var i = 0; i < arr_items.length; i++){
    		if(arr_items[i].type == "checkbox"){
    			if(str_groups.indexOf(arr_items[i].id+";") != -1)
    				arr_items[i].checked = true;	
    		}
    	}
    };
    </script>
    
    <table class="admMain"  align="center" border="0" cellpadding="0" cellspacing="0">
      <tr>
          <td class="admConnerLeft"></td>
          <td class="admTopBtn">
              <div><img src="/images/logo.png"  class="admMainLogo" /></div>
              <br></br>
              <jsp:include page="/WEB-INF/jsp/common/back_bar.jsp"/>
          </td>
          <td class="admConnerRight"></td>
      </tr>
      <tr>
          <td class="admMainLeft"><img src="/images/left_bot.png" /></td>
          <td>
    
    
    <!-- TITLE: begin -->
    <table align="center" border="0" cellpadding="0" cellspacing="0" class="admTable">
    	<tr>
        	<td align="center" class="admNavPanelFont"  style="height:auto;" colspan="2" >
            	<fmt:message key="BM.GROUPS_LIST"/>
            </td>
        </tr>
    <!-- TITLE: end -->

	<!-- CONTENT: begin  -->
		<tr>
			<th class="admTableTDLast">
				<c:forEach var="bannergroups" items="${bannergroups}">
					<div class="admPageChooserMenuItem">
						<input type="checkbox" id="${bannergroups.id}" value="${bannergroups.id}">&nbsp;<span>${bannergroups.name}</span>
					</div>
				</c:forEach>				
			</th>
		</tr>
		<%
		if(!activated){
			%>
				<tr>
					<th class="admTableTDLast" align="center">
	                    <div class="admNavPanelInp" style="padding:0;margin:0;">
							<div class="imgL"></div>
                            <div><input type="button" class="admNavbarInp" value="<fmt:message key="SAVE"/>" onClick="save();"/></div>
                            <div class="imgR"></div>
                        </div>	
					</th>
				</tr>
			<%
    	}
		%>
        <tr>
			<td class="admTableFooter" colspan="2">&nbsp;</td>
	    </tr>
	</table>
	<!-- CONTENT: end  -->
	<script type="text/javascript">
		groups_init();
	</script>

		</td>
    	<td class="admMaiRight"><img src="/images/right_bot.png" /></td>
	</tr>
</table>


</body>
</html>