<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>    
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%response.setHeader("Expires", "0");%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="negeso" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
   <head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title><fmt:message key="CM_REDIRECTS"/></title>
    <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
    <link rel="stylesheet" type="text/css" href="/css/customized-jquery-ui.css"/>
    <script type="text/javascript" src="/script/jquery.min.js">/**/</script>
    <script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
    <script language="JavaScript1.2" src="/script/common_functions.js" type="text/javascript"></script>
    <script type="text/javascript" src="/site/core/script/validation.js"></script>    
    <script language="JavaScript" src="/script/rte_implement.js"></script>  
    <script type="text/javascript" src="/script/RTE_Adapter.js">/**/</script>
     <script type="text/javascript" src="/script/cufon-yui.js"></script>
        <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>

        <script>
            window.onload = function(){
            Cufon.now();
            Cufon.replace('.admNavPanelFont', { fontFamily: 'Orator Std' });
            Cufon.replace('.admTDtitles', { fontFamily: 'Orator Std' });
            }


			function save(){
				if( validate( document.forms["addRedirectForm"])){
				 document.forms["addRedirectForm"].submit();
				}	
			}
            
        </script>
</head>
<body>
    <script type="text/javascript">
			
	   
        
    </script>
	<negeso:admin>
    
    
    
    <table align="center"  border="0" cellpadding="0" cellspacing="0" class="admTable">
        	<tr>
        		<td  style="width:auto; height:auto;" colspan="4">        
        			<jsp:include page="/WEB-INF/jsp/common/navig_bar.jsp" />	</td></tr>
                <tr>                
                <td align="center" class="admNavPanelFont"  style="height:auto;" colspan="6">
                    <fmt:message key="CM_REDIRECTS" />
                </td>
            </tr>
            <tr> 
                
                <td class="admTDtitles" style="height:auto" width="15%">Language</td>
                <td class="admTDtitles" style="height:auto" width="15%">Country</td>
                <td class="admTDtitles" style="height:auto" width="28%">URL mask</td>
                <td class="admTDtitles" style="height:auto" width="28%">Redirect to</td> 
                <td class="admTDtitles" style="height:auto" width="14%" colspan="3"><fmt:message key="ACTION"/></td> 
            </tr>
        
            <c:forEach var="redirect" items="${redirects}">
                <tr>
                    <td  class="admTableTD"  style="padding: 0 0 0 20px;">${redirect.langCode}</td>
                     <td  class="admTableTD" >
                     	<c:forEach items="${countries}" var="country">
                     		<c:if test="${country.alpha2 == redirect.countryCode}">
                     				${country.name}
                     		</c:if>
                     	</c:forEach>
                     	<c:if test="${redirect.countryCode == '*'}">
                     		*
                     	</c:if>
                   	</td>
                    <td  class="admTableTD" >${redirect.maskUrlFrom}</td>
                    <td class="admTableTD" >${redirect.redirectUrl}</td>
                    <td class="admTableTDLast">
	                    <a href="?action=redirects&todo=delete&id=${redirect.id}" style="text-decoration:none;">
	                    		<img src='/images/delete.png' class='admHand' title='Delete'/>
	                    </a>  
                    </td> 
                    <td class="admTableTDLast">
	                    <a href="?action=redirects&todo=up&id=${redirect.id}" style="text-decoration:none;">
	                    	<img src='/images/up.png' class='admHand' title='Up'/>
	                    </a>  
                  	</td> 
                   <td class="admTableTDLast"> 
	                    <a href="?action=redirects&todo=down&id=${redirect.id}" style="text-decoration:none;">
	                    	<img src='/images/down.png' class='admHand' title='Down'/>
	                    </a>  
                  </td> 
        
                </tr>
            </c:forEach>
            <form method="post" name="addRedirectForm" id="addRedirectForm" action="?action=redirects">
            <input type="hidden" name="todo" value="save">
            <input type="hidden" name="action" value="redirects">
            <tr>
                  <td  class="admTableTD">
                  	<select class="admWidth100" id="langSelect" name="langId">
                        <option value="0">*</option>
                        <c:forEach var="lang" items="${languages}">
                            <option value="${lang.id}">
                                <c:out value="${lang.code}" />
                            </option>
                        </c:forEach>
                     </select>
                  </td>
                  <td  class="admTableTD">
                  	<select class="admWidth100" id="countryCode" name="countryCode">
                        <option value="*">*</option>
                        <c:forEach var="country" items="${countries}">
                            <option value="${country.alpha2}">
                                <c:out value="${country.name}" />
                            </option>
                        </c:forEach>
                     </select>
                  </td>
                  <td  class="admTableTD">
                  		<input type="text" name="maskUrlFrom" value="" size="30" required="required">
                  </td>
                  <td class="admTableTD">
                  		<input type="text" name="redirectUrl" value="" size="30" required="required">
                  </td>
                  <td class="admTableTDLast">
	                    <a href="javascript:save();" style="text-decoration:none;">
	                    	<img src='/images/nl_proof.png' class='admHand' title='save'/>
	                    </a>  
                  </td> 
                  <td class="admTableTDLast"></td> 
                  <td class="admTableTDLast"></td> 
            </tr>
            </form>
            <tr>
                    <td class="admTableTDLast" colspan="6" >
                    	<c:if test="${isSU}">
                    		<img class="admBorder admHand" onclick="edit_text('article_text${redirectExplainArticle.id}', 'contentStyle','','',3, false);" src="/images/mark_1.gif"/>
                    	</c:if>
                    	<div id="article_text${redirectExplainArticle.id}">		
                    		${redirectExplainArticle.text}
                    	</div>	
                    </td>
            </tr>      
            <tr>
                    <td class="admTableFooter" colspan="6" >&nbsp;</td>
            </tr>      
        </table>        
</negeso:admin>
</body>
</html>