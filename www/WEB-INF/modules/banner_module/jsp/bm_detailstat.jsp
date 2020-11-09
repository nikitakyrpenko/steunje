<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ page session="true"%>
<%
	response.setHeader("Expires", "0");
	String period = (String)request.getSession().getAttribute("bm_stat_period");
	String startDate = (String)request.getSession().getAttribute("stat_start");
	String finishDate = (String)request.getSession().getAttribute("stat_finish");
	Banner banner = (Banner)request.getAttribute("banner");
	ArrayList errors = new ArrayList();
	Object o = request.getAttribute("errors");
	if (o != null)
		errors = (ArrayList)o;
%>
	
<%@page import="java.util.ArrayList"%>
<%@page import="com.negeso.module.banner_module.bo.Banner"%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
   	<title><fmt:message key="BM.BANNER_STATISTICS"/></title>
    <link rel="stylesheet" type="text/css" href="/css/admin_style.css"/>
    <link rel="stylesheet" type="text/css" href="/css/customized-jquery-ui.css"/>
    <link rel="stylesheet" type="text/css" href="/css/jquery-ui.css"/>
    <link rel="stylesheet" type="text/css" href="/css/jquery-ui.custom.css"/>
    <script language="JavaScript" src="/script/jquery.min.js">/**/</script>
    <script type="text/javascript" src="/script/jquery-ui.custom.min.js">/**/</script>
    <script language="JavaScript" src="/script/common_functions.js">/**/</script>
	<script type="text/javascript" src="/script/conf.js"/> 
	<script type="text/javascript" src="/script/media_catalog.js" type="text/javascript">/**/</script>
	<script type="text/javascript" src="/site/core/script/validation.js" type="text/javascript">/**/</script>
	<script type="text/javascript">    
	    $(function () {
			$("#stat_start,#stat_finish").datepicker({
        		dateFormat: 'yy-mm-dd',        
	    	    showOn: 'button',
		        buttonImage: '/images/calendar.gif',
        		buttonImageOnly: true
		    });	
	    });	
  	</script>
</head>
<body>
<form method = "post" enctype="multipart/form-data" action="/admin/bm_bannerlist.html?action=showDetailStatistics">
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


	
    <table align="center" border="0" cellpadding="0" cellspacing="0" class="admTable">
    	<tr>
        	<td align="center" class="admNavPanelFont"  style="height:auto;" colspan="2" >
				<% out.write(banner.getTitle()); %>
            </td>
        </tr>


	<%
		if (errors != null && errors.size() > 0){
			for (int i = 0; i < errors.size(); i++){
				out.write("<tr style=\"height: 12px;\">");				
				out.write("<td colspan=\"2\" class=\"admTableTDLast\" style=\"font-size: 10px; color: red; border: 0; padding: 0; text-align: left; vertical-align: middle; height: 12px;\"><c:out value=\"${error}\"/>");
				out.write(String.valueOf(errors.get(i)));
				out.write("</td>");
				out.write("</tr>");
			}
		}
	%>


    	<input type="hidden" id="id" name="id" <%out.write("value=\"" + banner.getId() + "\"");%> />

    		<tr>
    			<th class="admTableTD">
    				From :
    			</th>
    			<td class="admTableTDLast">
	                <input type="text" id="stat_start" name="stat_start"
    					<% out.write("value=\"" + startDate + "\""); %> class="admTextArea admWidth200" readonly="readonly"/>
    			</td>
    		</tr>
    		<tr>
    			<th class="admTableTD">
    				To :
    			</th>
    			<td class="admTableTDLast">
    				<input type="text" id="stat_finish" name="stat_finish"
    					<% out.write("value=\"" + finishDate + "\""); %> class="admTextArea admWidth200" readonly="readonly"/>
    			</td>
    		</tr>
            <tr>
            	<th class="admTableTD">
   					Period:
   				</th>
   				<td class="admTableTDLast">
   					<select class="admTextArea admWidth100" name="period" onChange="this.form.submit(); ">
    					<option value="month" 
	    					<% 
	    						if (period.equals("month"))out.write("selected");
	    					%>>
    						month
    					</option>
    					<option value="week" 
	    					<% 
	    						if (period.equals("week"))out.write("selected");
	    					%>>
    						week
    					</option>
   					</select>
   				</td>
            </tr>
            <tr>
            	<td colspan="2">
        
        <c:if test="${bannerstat != null}">
	        <table cellspacing="0" cellpadding="0"  align="center" border="0" width="100%">
	        	<tr>
	       			<td class="admTDtitles" style="height:auto">
	       				Period number
	       			</td>
					<td class="admTDtitles" style="height:auto">
	       				From date
	       			</td>
	       			<td class="admTDtitles" style="height:auto">
	       				To date
	       			</td>
	       			<td class="admTDtitles" style="height:auto">
	       				Clicks
	       			</td>
	       			<td class="admTDtitles" style="height:auto">
	       				Views
	       			</td>
	       		</tr>
	       		
	        	<c:forEach var="stats" items="${bannerstat}">
	        		<tr>
		       			<th class="admTableTD">
		       				${stats.number}
		       			</td>
		       			<td class="admTableTD">
		       				<fmt:formatDate value="${stats.startDate}" pattern="yyyy-MM-dd"/>
		       			</td>
		       			<td class="admTableTD">
		       				<fmt:formatDate value="${stats.finishDate}" pattern="yyyy-MM-dd"/>
		       			</td>
		       			<td class="admTableTD">
		       				${stats.clicks}
		       			</td>
		       			<td class="admTableTDLast">
		       				${stats.views}
		       			</td>
		       		</tr>
	        	</c:forEach>
	        </table>
        </c:if>
        		
                
                </td>
        	</tr>
			<tr>
				<td class="admTableFooter" colspan="2">&nbsp;</td>
	        </tr>
       </table>

    </td>
    	<td class="admMaiRight"><img src="/images/right_bot.png" /></td>
	</tr>
</table>

<table cellpadding="0" cellspacing="0"  width="764px"  align="center"  border="0" class="Buttons">
    <tr>
        <td>
        	<c:if test="${banner.activated == true}">
                <div class="admBtnGreenb">
                    <div class="imgL"></div>
                    <div>                    	                        
	                    <input type="button" class="admNavbarInp" value='View' onClick="this.form.submit();"/>
                    </div>
                    <div class="imgR"></div>
                </div>
			</c:if>
            
        </td>
    </tr>
</table>
    </form>
</body>
</html>