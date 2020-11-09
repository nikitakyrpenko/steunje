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
    <meta name="SKYPE_TOOLBAR" content ="SKYPE_TOOLBAR_PARSER_COMPATIBLE" />
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
								var multilingual_flag = ${mailTemplate.i18n};
    
        function save_template(){
            if(validate(document.forms['nl_form_mailtemplate'])) {
                document.forms['nl_form_mailtemplate'].elements['text'].value = document.getElementById('article_text').innerHTML;
                if(!document.forms['nl_form_mailtemplate'].elements['ch_i18n'].checked) {
                    document.getElementById('i18n').value = 'false';
                }
                
				if(!document.forms['nl_form_mailtemplate'].elements['ch_i18n'].checked && multilingual_flag){
					if(confirm(document.getElementById('unselect_confirm').innerHTML))
		                document.forms['nl_form_mailtemplate'].submit();
				}
				else
	                document.forms['nl_form_mailtemplate'].submit();
            }
        }
        function copy_all_template(){
            document.forms['nl_form_mailtemplate'].elements['copyToAllLanguages'].value = true;
            save_template();
        }
    </script>
    <title>
        <c:choose>
            <c:when test="${mailTemplate.id == null}">
            	<fmt:message key="NL.NEW_MAIL_TEMPLATE"/>
            </c:when>
            <c:otherwise>
            	<fmt:message key="NL.EDIT_MAIL_TEMPLATE"/>
            </c:otherwise>
        </c:choose>
    </title>
</head>
<body>

<div id="unselect_confirm" style="display:none"><fmt:message key="NL.UNSELECT_CONFIRM"/></div>

<form name="nl_form_mailtemplate" method="POST" enctype="multipart/form-data" action="/admin/nl_editmailtemplate">
    <spring:hasBindErrors name="mailTemplate">
        <c:forEach var="err" items="${errors.allErrors}">
            <span class="error"><c:out value="${err.defaultMessage}" /></span>
            <br />
        </c:forEach>
    </spring:hasBindErrors>
    <input type="hidden" name="action" 
        <c:choose>
            <c:when test="${mailTemplate.id == '' || template.id == null}">
                value="add"
            </c:when>
            <c:otherwise>
                value="edit"
            </c:otherwise>
        </c:choose>
    >
    <spring:bind path="mailTemplate.id">
        <input type="hidden" name="<c:out value="${status.expression}"/>" value="<c:out value="${status.value}"/>" />
    </spring:bind>      
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
				<c:choose>
                    <c:when test="${mailTemplate.id == null}">
                        <fmt:message key="NL.NEW_MAIL_TEMPLATE"/>
                    </c:when>
                    <c:otherwise>
                        <fmt:message key="NL.EDIT_MAIL_TEMPLATE"/>
                    </c:otherwise>
                </c:choose>
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
							<select class="admWidth150"
                                <c:if test="${mailTemplate.i18n == false}">
                                    <c:out value="disabled" />
                                </c:if> 
                                onchange="window.location.href = '/admin/nl_editmailtemplate?id=${mailTemplate.id}&lang_id='+this.options[this.selectedIndex].value">
                                <c:forEach var="lang" items="${languages}">
                                    <option value="${lang.id}" id="lang_id"
                                        <c:if test="${mailTemplate.currentLangId == lang.id}">
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
		<tr>
            <th class="admTableTD" width="160">
                <fmt:message key="TITLE"/>*
            </th>
            <td class="admTableTDLast">
                <spring:bind path="mailTemplate.title">
                    <input type="text" class="admTextArea" style="width:550px" name="<c:out value="${status.expression}"/>" value="<c:out value="${status.value}"/>">
                       	<c:if test="${status.error}">                   		
					</c:if>	
                </spring:bind>
            </td>
        </tr>
        <tr>
            <th class="admTableTD">
                <fmt:message key="NL.TEXT"/>
            </th>
            <td class="admTableTDLast" style="padding-right:27px;">
                <img class="admBorder admHand" alt="<fmt:message key="EDIT"/>" title="<fmt:message key="EDIT"/>" onclick="edit_text('article_text', 'contentStyle', '', '',1, true);" src="/images/mark_1.gif"/>
                <spring:bind path="mailTemplate.text">
                    <input type="hidden" name="<c:out value="${status.expression}"/>" value="<c:out value="${status.value}"/>" />
                    <div id="article_text" class="admRTEDiv" dir="ltr" >
                        <c:choose>
                            <c:when test="${status.value == null}">
                                &#160;
                            </c:when>
                            <c:otherwise>
                                <c:out value="${status.value}" escapeXml="yes"/>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </spring:bind>
            </td>
        </tr>
        <tr>
            <th class="admTableTD">
                <fmt:message key="IS_MULTILINGUAL"/>
            </th>
            <td class="admTableTDLast">
                <spring:bind path="mailTemplate.i18n">
                    <input type="checkbox" id="ch_i18n" value="<c:out value="${status.value}"/>"
                    name="ch_i18n"
                    <c:if test="${status.value == true}">
                    <c:out value="checked=checked"/>
                    </c:if> />
                <input type="hidden" id="i18n" name="i18n" value="true" />
                </spring:bind>
            </td>
        </tr>
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
					<div><input type="button" class="admNavbarInp" value='<fmt:message key="SAVE"/>' name="saveButton" onClick="save_template();"/></div>
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