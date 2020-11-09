<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="negeso" %>
<%@ page session="true"%>
<%
    response.setHeader("Expires", "0");
%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
    <link href="/site/core/css/default_styles.css" rel="stylesheet" type="text/css"/>
    
    <script type="text/javascript" src="/script/jquery.min.js">/**/</script>
    <script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
                <script type="text/javascript" src="/script/cufon-yui.js"></script>
                <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script> 
                <script type="text/javascript" src="/script/common_functions.js"></script>
    <script type="text/javascript" src="/site/core/script/validation.js"></script>
    <script language="JavaScript" src="/script/rte_implement.js"></script>  
    <script type="text/javascript" src="/script/RTE_Adapter.js">/**/</script>

                <script type="text/javascript">
                                var multilingual_flag = true;
    
        function save_social_networks(){          
            document.forms['form_social_network'].submit();
        }
        function copy_all_template(){
            document.forms['form_social_network'].elements['copyToAllLanguages'].value = true;
            save_social_networks();
        }
    </script>
    <title>
        <fmt:message key="CORE.SOCIAL_NETWORK"/>
    </title>
</head>
<body>

<div id="unselect_confirm" style="display:none"><fmt:message key="NL.UNSELECT_CONFIRM"/></div>

<form name="form_social_network" method="POST" enctype="multipart/form-data" action="/admin/edit_social_network">
    <input type="hidden" name="action" value="edit" />
    <input type="hidden" name="copyToAllLanguages" value="false"/>        
    
    <negeso:admin>
    <table align="center" border="0" cellpadding="0" cellspacing="0" class="admTable">
        <tr>
            <td style="width:auto; height:auto;" colspan="2">  
                <jsp:include page="/WEB-INF/jsp/common/navig_bar.jsp" />
            </td>
        </tr>
        <!-- Title table -->
        <tr>    
            <td align="center" class="admNavPanelFont" style="height:auto;" colspan="2" >
                <fmt:message key="CORE.SOCIAL_NETWORK"/>
            </td>            
        </tr>
        <tr>
            <td class="admTableTDLast admRight" colspan="2">                
                <table cellspacing="0" cellpadding="0" style="margin:0 10px 5px 0;">
                    <tr>
                        <td width="80%" class="admRight">
                            <fmt:message key="LANGUAGE"/>
                        </td>
                        <td width="20%" class="admRight">
                            <select class="admWidth150" name="lang_id"
                                onchange="window.location.href = '/admin/edit_social_network?lang_id='+this.options[this.selectedIndex].value">
                                <c:forEach var="lang" items="${languages}">
                                    <option value="${lang.id}" id="lang_id"
                                        <c:if test="${curLang == lang.id}">
                                            <c:out value="selected"/>
                                        </c:if>
                                    ><c:out value="${lang.code}" />
                                    </option>
                                </c:forEach>
                            </select>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
        <!-- Content table -->
        <c:forEach var="socialNetwork" items="${socialNetworks}">
	        <tr>
	            <td class="admTDtitles" colspan="2" style="height:auto;">${socialNetwork.title}</td> 
	        </tr>
	        <c:forEach var="entry" items="${socialNetwork.params}">
		        <tr>
		            <th class="admTableTD" width="160">
		                ${entry.key}
		            </th>
		            <td class="admTableTDLast">
                        <input type="text" class="admTextArea" style="width:550px" name='<c:out value="${entry.value.code}"/>_<c:out value="${entry.value.id}"/>' value="${entry.value.value}"/>		                        
		            </td>
		        </tr>
	           
	        </c:forEach>
        </c:forEach>
        <tr>
            <td class="admTableFooter" colspan="2">&nbsp;</td>
        </tr>
    </table>    
    </negeso:admin>

    <table cellpadding="0" cellspacing="0"  width="764px"  align="center"  border="0" class="Buttons">
        <tr>
            <td>
                <div class="admBtnGreenb">
                    <div class="imgL"></div>
                    <div><input type="button" class="admNavbarInp" value='<fmt:message key="SAVE"/>' name="saveButton" onclick="save_social_networks();"/></div>
                    <div class="imgR"></div>
                </div>
                <div class="admBtnGreenb admBtnBlueb">
                    <div class="imgL"></div>
                    <div><input type="reset" value='<fmt:message key="RESET"/>'></div>
                    <div class="imgR"></div>
                </div>
            </td>
        </tr>
    </table>            
    </form>
</body>
</html>