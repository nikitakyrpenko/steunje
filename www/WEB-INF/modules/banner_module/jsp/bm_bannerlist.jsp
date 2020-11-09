<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@page import="java.util.Map"%>
<%@ page session="true"%>
<%response.setHeader("Expires", "0");%>
<%@page import="com.negeso.module.banner_module.bo.Banner"%>
<%@page import="java.util.LinkedHashMap"%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
    <title><fmt:message key="BM.BANNER_LIST"/></title>
    <script type="text/javascript" src="/script/jquery.min.js"></script>
	<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
    <script type="text/javascript" src="/script/cufon-yui.js"></script>
    <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
	<script type="text/javascript" src="/script/common_functions.js"></script>    
</head>
<body>
	<%
		String filterPriority = (String) session.getAttribute("filter_priority");
		String filterExpired = (String) session.getAttribute("filter_expired");
		String filterOrder = (String) session.getAttribute("order_field");
		String filterDirection = (String) session.getAttribute("order_direction");
		Map<Long, Long> bannerClicks = (Map<Long, Long>) request.getAttribute("bannerclicks");
		Map<Long, Long> bannerViews = (Map<Long, Long>) request.getAttribute("bannerviews");
	%>

	<script type="text/javascript">
		function addBanner(cat_id){
			document.location = 'bm_bannerlist.html?action=addBanner&parentId=' + cat_id;
		};
		
		function preview(img){
			p = window.open(img);
			p.focus();
		};
		
		function try_to_delete(banner, cat_id){
			if(confirm('Are you sure to delete this banner?')){
				document.location = 'bm_bannerlist.html?action=deleteBanner&bannerId='+banner+'&parentId=' + cat_id;
			}
		}
		
		function view(){
			new_location = 'bm_categorylist.html?action=categoryContent&parentId=${parentId}';
			new_location = new_location + '&filter_priority=' + document.getElementById('filter_priority').value;
			new_location = new_location + '&filter_expired=' + document.getElementById('filter_expired').value;
			new_location = new_location + '&order_field=' + document.getElementById('order_field').value;
			new_location = new_location + '&order_direction=' + document.getElementById('order_direction').value;
			document.location = new_location;
		}
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
			<td align="center" class="admNavPanelFont"  style="height:auto;" colspan="8">
				<fmt:message key="BM.BANNERS_IN_CATEGORY"/>
            </td>
        </tr>
    <!-- TITLE: end -->    
        <c:if test="${error_message ne null}">	    
            <tr>
                <td class="admTableTDLast" style="font-size: 10px; color: red; border: 0; padding: 0; text-align: left; vertical-align: middle; height: 12px;" colspan="8">
                    <fmt:message key="${error_message}"/>
                </td>
            </tr>
        </c:if>
    <!-- CONTENT: begin  -->
		<tr> 
			<th class="admTableTD" colspan="4">
				<fmt:message key="BM.FILTER"/>:
				&nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key="BM.BANNER_PRIORITY"/>&nbsp;
				<select id="filter_priority" name="filter_priority">
					<%
						if (filterPriority != null && !filterPriority.toLowerCase().equals("all")){
							out.write("<option value=\"All\">All</option>");
		   					for (int i = 1; i <= 10; i++){
		   						if (i == Integer.parseInt(filterPriority)){
			   						out.write("<option value=\"" + i + "\" selected>" + i + "</option>");
		   						}else{
			   						out.write("<option value=\"" + i + "\">" + i + "</option>");
		   						}
		   					}	
						}
						else
						{
							out.write("<option value=\"All\">All</option>");
		   					for (int i = 1; i <= 10; i++){
		   						out.write("<option value=\"" + i + "\">" + i + "</option>");
		   					}
	   					}
		    		%>
				</select>
				&nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key="BM.EXPIRED"/>&nbsp;
				<select id="filter_expired" name="filter_expired">
					<option value="all" <% if (filterExpired == null)out.write("selected"); %>>All</option>
					<option value="yes" <% if (filterExpired != null && filterExpired.equals("yes"))out.write("selected"); %>><fmt:message key="BM.YES"/></option>
					<option value="no"  <% if (filterExpired != null && filterExpired.equals("no"))out.write("selected"); %>><fmt:message key="BM.NO"/></option>
					
				</select>
			</td> 			 
			<td class="admTableTD" colspan="2">
				<fmt:message key="BM.ORDER"/>:&nbsp;&nbsp;&nbsp;&nbsp;
				<select id="order_field" name="order_field">
					<option value="title" 
					<%	if (filterOrder != null && filterOrder.toLowerCase().equals("title")){
							out.write("selected");
						}
					%>
					><fmt:message key="BM.TITLE"/></option>
					<option value="priority"
					<%	if (filterOrder != null && filterOrder.toLowerCase().equals("priority")){
							out.write("selected");
						}
					%>
					><fmt:message key="BM.BANNER_PRIORITY"/></option>
					<option value="clicks"
					<%	if (filterOrder != null && filterOrder.toLowerCase().equals("clicks")){
							out.write("selected");
						}
					%>
					><fmt:message key="BM.CLICKS"/></option>
					<option value="views"
					<%	if (filterOrder != null && filterOrder.toLowerCase().equals("views")){
							out.write("selected");
						}
					%>
					><fmt:message key="BM.VIEWS"/></option>
				</select>
				&nbsp;&nbsp;
				<select id="order_direction" name="order_direction">
					<option value="asc"
					<%
						if (filterDirection != null && filterDirection.toLowerCase().equals("asc")){
							out.write("selected");
						}
					%>
					><fmt:message key="BM.ASC"/></option>
					<option value="desc"
					<%
						if (filterDirection != null && filterDirection.toLowerCase().equals("desc")){
							out.write("selected");
						}
					%>
					><fmt:message key="BM.DESC"/></option>
				</select>
			</td>
			<td class="admTableTDLast" align="center" colspan="2">
                <div class="admNavPanelInp" style="padding:0;margin:0;">
					<div class="imgL"></div>
                    <div><input type="button" class="admNavbarInp" value='View' onClick="view();"/></div>
                    <div class="imgR"></div>
				</div>		
			</td>
		</tr>
		<tr> 
			<td class="admTDtitles" style="height:auto"><fmt:message key="BM.PREVIEW"/></td> 			 
			<td class="admTDtitles" style="height:auto"><fmt:message key="BM.TITLE"/></td> 
			<td class="admTDtitles" style="height:auto"><fmt:message key="BM.ACTIVE"/></td> 
			<td class="admTDtitles" style="height:auto"><fmt:message key="BM.BANNER_PRIORITY"/></td> 
			<td class="admTDtitles" style="height:auto"><fmt:message key="BM.GENERAL"/></td> 
			<td class="admTDtitles" style="height:auto"><fmt:message key="BM.DATES"/></td> 
			<td class="admTDtitles" style="height:auto" colspan="2"><fmt:message key="BM.ACTION"/></td> 
		</tr>
			<c:forEach var="bannerlist" items="${bannerlist}">
			<tr>
				<th class="admTableTD">
					<a href="javascript:preview('${bannerlist.imageUrl}')"><c:choose><c:when 
                    	test="${bannerlist.imageType == 1}"><img src="${bannerlist.imageUrl}" border="0" alt=""/></c:when><c:when 
                        	test="${bannerlist.imageType == 2}"><embed 
                            	wmode="opaque" width="50px" height="50px" type="application/x-shockwave-flash"  src="${bannerlist.imageUrl}"
									scale="showall" quality="high" pluginspage="http://www.macromedia.com/go/getflashplayer" menu="false"/></c:when></c:choose></a>
				</th>
				<td class="admTableTD">
					<a href="bm_bannerlist.html?action=editBanner&bannerId=${bannerlist.id}">${bannerlist.title}</a>
				</td>
				<td class="admTableTD">
					<c:choose>
						<c:when test="${bannerlist.activated == true}">
							<fmt:message key="BM.ACTIVE"/>
						</c:when>
						<c:otherwise>
							<fmt:message key="BM.INACTIVE"/>
						</c:otherwise>
					</c:choose>
				</td>
				<td class="admTableTD">
					${bannerlist.priority}
				</td>
				<td class="admTableTD">
					<fmt:message key="BM.VIEWS"/>:&#160; 
					<% 
						out.write(bannerViews.get(((Banner)pageContext.getAttribute("bannerlist")).getId()).toString()); 
					%> /
					
					<c:choose>
						<c:when test="${bannerlist.maxViews == 0}">
							~
						</c:when>
						<c:otherwise>
							${bannerlist.maxViews}
						</c:otherwise>
					</c:choose>
					<fmt:message key="BM.CLICKS"/>:&#160; 
					<% 
						out.write(bannerClicks.get(((Banner)pageContext.getAttribute("bannerlist")).getId()).toString()); 
					%> / 
					<c:choose>
						<c:when test="${bannerlist.maxClicks == 0}">
							~
						</c:when>
						<c:otherwise>
							${bannerlist.maxClicks}
						</c:otherwise>
					</c:choose>
					<br/>
				</td>
				<td class="admTableTD">
					<fmt:message key="BM.BANNER_PUBLISH_DATE"/>:&#160;
						<c:if test="${bannerlist.publishDate == null}">
							<fmt:message key="BM.NONE"/>
						</c:if>
						<c:if test="${bannerlist.publishDate != null}">
							<fmt:formatDate value="${bannerlist.publishDate}" type="both" pattern="dd.MM.yyyy" />
						</c:if>
					<br />
					<fmt:message key="BM.BANNER_EXPIRED_DATE"/>:&#160;
						<c:if test="${bannerlist.expiredDate == null}">
							<fmt:message key="BM.NONE"/>
						</c:if>
						<c:if test="${bannerlist.expiredDate != null}">
							<fmt:formatDate value="${bannerlist.expiredDate}" type="both" pattern="dd.MM.yyyy" />
						</c:if>
				</td>
				<td class="admTableTDLast" width="30">
					<a href="bm_bannerlist.html?action=editBanner&bannerId=${bannerlist.id}"><img 
                    	class="admImg" src="/images/edit.png" width="37px" height="36px" alt="<fmt:message key="EDIT"/>" title="<fmt:message key="EDIT"/>"/></a>
                </td>                        
                <td class="admTableTDLast" width="30">
                        <a href="javascript:try_to_delete('${bannerlist.id}')">
						<img class="admImg" src="/images/delete.png" width="37px" height="36px" alt="<fmt:message key="DELETE"/>" title="<fmt:message key="DELETE"/>"/>
					</a>
				</td>
			</tr>
		</c:forEach>
		<tr>
			<td class="admTableFooter" colspan="8">&nbsp;</td>
        </tr>
	</table>    
 
	<!-- CONTENT: end  -->
		</td>
    	<td class="admMaiRight"><img src="/images/right_bot.png" /></td>
	</tr>
</table>

<table cellpadding="0" cellspacing="0"  width="764px"  align="center"  border="0" class="Buttons">
    <tr>
        <td>
            <div class="admBtnGreenb">
                <div class="imgL"></div>
                <div>
                     <input type="button" class="admNavbarInp" value='<fmt:message key="BM.ADD_BANNER"/>' onClick="addBanner('${parentId}');"/>
                </div>
                <div class="imgR"></div>
            </div>
        </td>
    </tr>
</table>

</body>
</html>