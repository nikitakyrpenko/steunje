<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page session="true"%>
<%
	response.setHeader("Expires", "0");
	Object o1 = request.getAttribute("bannerclicks");
	Object o2 = request.getAttribute("bannerviews");
	Map<Long, Long> bannerClicks = new LinkedHashMap<Long, Long>();
	Map<Long, Long> bannerViews = new LinkedHashMap<Long, Long>();
	try{
		if (o1 != null)
			bannerClicks = (Map<Long, Long>) o1;
		if (o2 != null)
			bannerViews = (Map<Long, Long>) o2;
	}catch(Exception e){}
%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.negeso.module.banner_module.bo.Banner"%>
<html>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
    <title><fmt:message key="BM.BANNER_STATISTICS"/></title>
	<script type="text/javascript" src="/script/conf.js"/>
	<script type="text/javascript" src="/script/jquery.min.js"></script>
	<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
    <script type="text/javascript" src="/script/cufon-yui.js"></script>
    <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
	<script type="text/javascript" src="/script/common_functions.js"></script>    
    
    <link rel="stylesheet" type="text/css" href="/css/admin_style.css"/>
    <link rel="stylesheet" type="text/css" href="/css/customized-jquery-ui.css"/>
    <link rel="stylesheet" type="text/css" href="/css/jquery-ui.css"/>
    <link rel="stylesheet" type="text/css" href="/css/jquery-ui.custom.css"/>
</head>

<body>
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
        	<td align="center" class="admNavPanelFont"  style="height:auto;" colspan="6" >
            	<fmt:message key="BM.STATISTIC"/>
            </td>
        </tr>
    <!-- TITLE: end -->
    
    <script type="text/javascript">
		function preview(img){
			p = window.open(img);
			p.focus();
		};
    </script>
    <!-- CONTENT: begin  -->
		<tr> 
			<td class="admTDtitles">&nbsp;</td>
			<td class="admTDtitles"><fmt:message key="BM.TITLE"/></td>
			<td class="admTDtitles"><fmt:message key="BM.VIEWS"/></td>
			<td class="admTDtitles"><fmt:message key="BM.CLICKS"/></td>
			<td class="admTDtitles"><fmt:message key="BM.PUBLISH_DATE"/></td>
			<td class="admTDtitles"><fmt:message key="BM.EXPIRED_DATE"/></td>
		</tr>
		<c:forEach var="banner" items="${banners}">
			<tr>
				<td class="admTableTD">
					<a href="javascript:preview('${bannerlist.imageUrl}')"><c:choose><c:when test="${banner.imageType == 1}"><img 
    	                	src="${banner.imageUrl}" border="0" alt=""></c:when><c:when test="${banner.imageType == 2}"><embed 
	                        wmode="opaque" width="50px" height="50px" type="application/x-shockwave-flash"  src="${banner.imageUrl}"
							scale="showall" quality="high" pluginspage="http://www.macromedia.com/go/getflashplayer" menu="false"/></c:when></c:choose></a>
				</td>
				<td class="admTableTD">
					<a href="/admin/bm_bannerlist.html?action=showStatistics&bannerId=${banner.id}">${banner.title}</a>
				</td>
				<td class="admTableTD">
					<%
						out.write(bannerViews.get(((Banner)pageContext.getAttribute("banner")).getId()).toString());
					%>
					 /
					<c:choose>
						<c:when test="${banner.maxViews == 0}">
							~
						</c:when>
						<c:otherwise>
							${banner.maxViews}
						</c:otherwise>
					</c:choose>
				</td>
				<td class="admTableTD">
					<%
						out.write(bannerClicks.get(((Banner)pageContext.getAttribute("banner")).getId()).toString());
					%>
					/
					<c:choose>
						<c:when test="${banner.maxClicks == 0}">
							~
						</c:when>
						<c:otherwise>
							${banner.maxClicks}
						</c:otherwise>
					</c:choose>
				</td>
				<td class="admTableTD">
					<c:choose>
						<c:when test="${banner.publishDate != null}">
							<fmt:formatDate value="${banner.publishDate}" type="both" pattern="yyyy-MM-dd" />
						</c:when>
						<c:otherwise>
							-
						</c:otherwise>
					</c:choose>
				</td>
				<td class="admTableTDLast">
					<c:choose>
						<c:when test="${banner.expiredDate != null}">
							<fmt:formatDate value="${banner.expiredDate}" type="both" pattern="yyyy-MM-dd" />
						</c:when>
						<c:otherwise>
							-
						</c:otherwise>
					</c:choose>
				</td>
			</tr>
		</c:forEach>
        <tr>
			<td class="admTableFooter" colspan="6">&nbsp;</td>
        </tr>
	</table>
	<!-- CONTENT: end  -->
    
    </td>
    	<td class="admMaiRight"><img src="/images/right_bot.png" /></td>
	</tr>
</table>
    
</body>
</html>