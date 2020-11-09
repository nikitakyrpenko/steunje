<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>    
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%response.setHeader("Expires", "0");%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="negeso" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
	<title><fmt:message key="CUSTOM_CONSTS"/></title>
	
	<script type="text/javascript" src="/script/jquery.min.js">/**/</script>	
	<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
	<script type="text/javascript" src="/script/cufon-yui.js"></script>
 <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
	<script language="JavaScript1.2" src="/script/common_functions.js" type="text/javascript"></script>

	<script type="text/javascript">
		function try_to_delete(id, moduleId) {
			var answ = confirm('Are you sure you want to delete this const?');
			if (answ==true)
				window.location.href="?act=deleteConst&id=" + id + "&moduleId=" + moduleId;
		}
	</script>
    
    

    <script type="text/javascript">
	function try_delete(id){
		if(confirm('<fmt:message key="DELETE_CONFIRMATION"/>'))
			document.location='/admin/nl_categorylist?action=deleteSubscriptionCategory&categoryId='+id;
	}
	</script>
</head>
<body>
	<negeso:admin>
	<table align="center" border="0" cellpadding="0" cellspacing="0" class="admTable"  width="764px">
	<tr>
			<td style="width:auto; height:auto;" colspan="4" >  
				<jsp:include page="/WEB-INF/jsp/common/navig_bar.jsp" />
			</td>
		</tr>                      
    	<tr>        	
            <td  align="center" class="admNavPanelFont"  style="height:auto;" colspan="4" >
			    <c:choose>
			    	<c:when test="${module==null}">
						<fmt:message key="COMMON_CONSTS"/>
			    	</c:when>
			    	<c:otherwise>'${module.title}'.<fmt:message key="CUSTOM_CONSTS"/></c:otherwise>
			    </c:choose>
			</td>
			
		</tr>
	
		<tr> 
			<td class="admTDtitles" width="40%"><fmt:message key="CONST"/></td> 
			<td class="admTDtitles" width="50%"><fmt:message key="ENGLISH_TRANSLATION"/></td>
			<td class="admTDtitles" style="padding:0px;" ><fmt:message key="CONSTS.ACTIONS"/></td>
		</tr>
		<c:forEach var="const" items="${consts}">				
		<tr>
			<th class="admTableTD">
				<b>
				   <a class="admAnchor" href="/admin/module_consts?act=updateConst&id=${const.id}">
					 ${const.key}
				  </a>
				</b>
			</th> 
			<th class="admTableTD">${const.englishTranslation}</th> 
			<td class="admTableTDLast" colspan="2" width="30px;" style="padding-left:20px;">
				<c:if test="${not visitorMode}">
				    <a href="/admin/module_consts?act=updateConst&id=${const.id}">
				  	 	 <img class="admImg" style="cursor: pointer" src="/images/edit.png" width="37px" height="36px" alt="Edit parameter"/>
				   </a>
			   </c:if>
			   <c:if test="${visitorMode}">
				   <a href="/admin/module_consts?act=updateConst&id=${const.id}&visitorMode=true">
				  	 	 <img class="admImg" style="cursor: pointer" src="/images/edit.png"  width="37px" height="36px" alt="Edit parameter"/>
				   </a>
			   </c:if>
		  	<c:if test="${not visitorMode}">
				   <a 	href="<c:choose>
								<c:when test="${module == null}">
									javascript:try_to_delete(${const.id}, '')									
								</c:when>
								<c:otherwise>
									javascript:try_to_delete(${const.id}, ${module.id})																		
								</c:otherwise>
								</c:choose>"
				   >
				      <img class="admImg" style="cursor: pointer" src="/images/delete.png"  width="37px" height="36px" alt="Delete parameter"/>
				   </a>
			</c:if>	   
			</td>
		</tr>
		</c:forEach>
		
         <tr>
			<td class="admTableFooter" colspan="2" >&nbsp;</td>
        </tr> 
	</table>
	</negeso:admin>
      <c:if test="${not visitorMode}">
    <table  cellpadding="0" cellspacing="0"  width="764px"  align="center"  border="0" class="Buttons">
            <tr >
                <td>
                    <div class="admBtnGreenb">
                                <div class="imgL"></div>
                                <div>
			
					<input type="button" class="admNavbarInp" value='Add parameter' 
						onclick="window.location.href='?act=addConst&moduleId=${module.id}'"/> 
		
                                </div>
                                <div class="imgR"></div>
                            </div>
                    
                </td>
            </tr>
        </table>
    
    </c:if>
</body>
</html>