<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ page session="true"%>
<%
	response.setHeader("Expires", "0");
%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="negeso" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
   	<title>
   					<fmt:message key="NL.PUBLICATION_CATEGORIES"/>
   	</title>
				<script type="text/javascript" src="/script/jquery.min.js">/**/</script>				
				<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
				<script type="text/javascript" src="/script/cufon-yui.js"></script>
    <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
				<script type="text/javascript" src="/script/common_functions.js"></script>
</head>
<body>
    <script type="text/javascript">
	function try_delete(id){
		if(confirm('<fmt:message key="DELETE_CONFIRMATION"/>'))
			document.location='/admin/nl_categorylist?action=deleteSubscriptionCategory&categoryId='+id;
	}
	</script>
 
<negeso:admin>

	<table align="center" border="0" cellpadding="0" cellspacing="0" class="admTable"  width="764px">
		<tr>
			<td style="width:auto; height:auto;" colspan="2" >  
				<jsp:include page="/WEB-INF/jsp/common/navig_bar.jsp" />
			</td>
		</tr>                      
    	<tr>        	
            <td  align="center" class="admNavPanelFont"  style="height:auto;" colspan="2" >
				<fmt:message key="NL.PUBLICATION_CATEGORIES"/>
            </td>            
        </tr>    
    
    <!-- Validation errors -->
    
	    <c:forEach items="${errors}" var="error">
	    	<tr style="height: 12px;" >
	    		<td style="font-size: 10px; color: red; border: 0; padding: 0; text-align: left; vertical-align: middle; height: 12px;" >
	    			<c:out value="${error}"/>
	    			<!--fmt:message key="${error}"/-->
	    		</td>
	    	</tr>
    	</c:forEach>    
    
    <!-- Content table -->
		<tr> 
        	<td class="admTDtitles" style="height:auto">
            	<fmt:message key="TITLE"/>
            </td> 
            <td class="admTDtitles" style="height:auto" >
            	<fmt:message key="ACTION"/>
            </td> 
        </tr> 
        
        <c:forEach var="category" items="${categorylist}">
        	<tr>
            	<td class="admTableTD" id="admTableTDtext" style="width:614px;">
                	<a class="admNone" style="font-weight: bold; color: black" 
                		href='/admin/nl_publicationlist?categoryId=${category.id}'>
                 		<c:choose>
   							<c:when test="${fn:length(category.title)>50}">
   								${fn:substring(category.title,0,50)}...
   							</c:when>
   							<c:otherwise>
	   							${category.title}
   							</c:otherwise>
  						</c:choose>
					</a>
				</td>
	            <td class="admTableTDLast" id="admTableTDtext" style="width:150px;">
                <table style="width:150px;">
                <tr>
                <td style="width:37px;">
	            	<a href="/admin/nl_categorylist?action=changeDirection&id=${category.id}&direction=true"><img class="admHand" style="cursor: pointer" src="/images/up.png" width="37px" height="36px" alt="<fmt:message key="UP"/>"/></a>
	            </td>
	            <td  style="width:37px;" >
	                <a href="/admin/nl_categorylist?action=changeDirection&id=${category.id}&direction=false"><img class="admHand" style="cursor: pointer" src="/images/down.png" width="37px" height="36px" alt="<fmt:message key="DOWN"/>"/></a>
	            </td>
	            <td  style="width:37px;">
	                <a href="/admin/nl_editcategory?categoryId=${category.id}"><img class="admHand" style="cursor: pointer" src="/images/edit.png" width="37px" height="36px"" alt="<fmt:message key="EDIT"/>"/></a>
	            </td>
	            <td style="width:37px;" >
	                <img class="admHand" src="/images/delete.png" width="37px" height="36px" alt="<fmt:message key="DELETE"/>" onClick="try_delete('${category.id}')"/>
                    </td>
                    </tr>
                    </table>
	            </td>
	         </tr>
        </c:forEach>        
        <tr>
			<td class="admTableFooter" colspan="2" >&nbsp;</td>
        </tr>  
    </table>
    </negeso:admin>
    <!-- Add button -->
    
     <table cellpadding="0" cellspacing="0"  width="764px"  align="center"  border="0" class="Buttons">
            <tr >
                <td>
                    <div class="admBtnGreenb">
                                <div class="imgL"></div>
                                <div>
                                
                             
						 <input type="button" class="admNavbarInp" value='<fmt:message key="NL.ADD_CATEGORY"/>' 
                	onClick="window.location.href='/admin/nl_editcategory'">
					
                      
                                </div>
                                <div class="imgR"></div>
                            </div>
                    
                </td>
            </tr>
        </table>
</body>
</html>