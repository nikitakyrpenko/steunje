<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="negeso" %>
<%@ page session="true"%>
<html>
	<head>
				<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	   <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>

				<c:choose>
								<c:when test="${group.id > 0}">
												<title>${group.title}</title>
								</c:when>
								<c:otherwise>
												<title>
																<fmt:message key="NL.ADD_NEW_GROUP"/>
												</title>	
								</c:otherwise>
				</c:choose>
	   
				<script type="text/javascript" src="/script/jquery.min.js">/**/</script>				
				<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
				<script type="text/javascript" src="/script/cufon-yui.js"></script>
				<script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
				<script type="text/javascript" src="/script/common_functions.js"></script>
				<script type="text/javascript" src="/site/core/script/validation.js" type="text/javascript">/**/</script>

				<script type="text/javascript">	
			var multilingual_flag = ${group.i18n};
			
			function save(){
				if(!document.forms['nl_form_group'].elements['ch_i18n'].checked){
					document.getElementById('i18n').value = 'false';
				}
				if(!document.forms['nl_form_group'].elements['ch_internal'].checked){
					document.getElementById('internal').value = 'false';
				}
				
				if(!document.forms['nl_form_group'].elements['ch_i18n'].checked && multilingual_flag){
					if(confirm(document.getElementById('unselect_confirm').innerHTML))
		                document.forms['nl_form_group'].submit();
				}
				else
	                document.forms['nl_form_group'].submit();
				
				
				return false;
			}	
	
		</script>
	</head>
	<body>	    
	<form name="nl_form_group" method="POST" enctype="multipart/form-data">			
		<spring:bind path="group.id">
			<input type="hidden" name="<c:out value="${status.expression}"/>" value="<c:out value="${status.value}"/>" />
		</spring:bind>

		<negeso:admin>
		<!-- Title table -->
		<table align="center" border="0" cellpadding="0" cellspacing="0" class="admTable">
			<tr>
				<td style="width:auto; height:auto;" colspan="2">  
					<jsp:include page="/WEB-INF/jsp/common/navig_bar.jsp" />
				</td>
			</tr>
			<tr>	
				<td align="center" class="admNavPanelFont"  style="height:auto;" colspan="2" >
					<c:choose>
						<c:when test="${(group.id > 0) && (fn:length(group.title)>50)}">
							${fn:substring(group.title,0,50)}...
						</c:when>
						<c:when test="${group.id > 0}">
							${group.title}
						</c:when>
						<c:otherwise>
							<fmt:message key="NL.ADD_NEW_GROUP"/>
						</c:otherwise>
					</c:choose>
				</td>            
			</tr>
			<!--CONTENT -->			
			<tr>
				<td class="admTableTDLast admRight" colspan="2">
					<table cellspacing="0" cellpadding="0" style="margin:0 10px 5px 0;">
						<tr>
							<td width="80%" class="admRight">
								<fmt:message key="LANGUAGE"/>
							</td>
							<td width="20%" class="admRight">
								<select class="admTextArea admWidth150"
									<c:if test="${group.i18n == false}">
										<c:out value="disabled" />
									</c:if> 
									onchange="window.location.href = '/admin/nl_editgroup?gid=${group.id}&lang_id='+this.options[this.selectedIndex].value">
									<c:forEach var="lang" items="${languages}">
										<option value="${lang.id}" id="lang_id"
											<c:if test="${group.lang_id == lang.id}"><c:out value="selected"/></c:if>
										>
											<c:out value="${lang.code}" />
										</option>
									</c:forEach>
								</select>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>					
            	<th class="admTableTD" width="250">
					<fmt:message key="TITLE"/>*
				</th>
				<td class="admTableTDLast">
					<spring:bind path="group.title">
						<input type="text" name="${status.expression}" id="${status.expression}" class="admTextArea admWidth465"
							value="${status.value}" title="${status.value}" MAXLENGTH="256"
	            			<c:if test="${group.id==1}">
								disabled="disabled"
	            			</c:if>
                         />
                    </spring:bind>
				</td>
			</tr>
			<tr>
				<th class="admTableTD">
					<fmt:message key="DESCRIPTION"/>
				</th>
				<td class="admTableTDLast">
					<spring:bind path="group.description">
			        	<textarea rows="2" class="admTextArea admWidth465" type="text" name="${status.expression}" data_type="text" uname="Description"><c:out value="${status.value}"/></textarea>
					</spring:bind>
 				</td>
			</tr>
			<tr>
				<th class="admTableTD">
					<fmt:message key="IS_MULTILINGUAL"/>
				</th>
				<td class="admTableTDLast">
					<spring:bind path="group.i18n">
						<input type="checkbox" id="ch_i18n" value="true"
							<c:if test="${group.i18n == true}">
								<c:out value="checked=checked"/>
							</c:if>
						/>
						<input type="hidden" id="i18n" name="i18n" value="true"/>
					</spring:bind>
 				</td>
			</tr>
			<tr>
				<th class="admTableTD">
					<fmt:message key="NL.INTERNAL"/>
				</th>
				<td class="admTableTDLast">
					<spring:bind path="group.internal">
						<input type="checkbox" id="ch_internal" value="true"
							<c:if test="${group.internal == true}">
								<c:out value="checked='checked'"/>
							</c:if>
						/>
						<input type="hidden" id="internal" name="internal" value="true"/>
					</spring:bind>
 				</td>
			</tr>
			<tr>
				<td class="admTableFooter" colspan="2">&nbsp;</td>
			</tr>
		</table>
		<div id="unselect_confirm" style="display:none"><fmt:message key="NL.UNSELECT_CONFIRM"/></div>		
		</negeso:admin>

		<table cellpadding="0" cellspacing="0"  width="764px"  align="center"  border="0" class="Buttons">
			<tr>
				<td>
					<div class="admBtnGreenb">
						<div class="imgL"></div>
						<div><input type="button" class="admNavbarInp" value='<fmt:message key="SAVE"/>' onClick="save(); return false;"/></div>
						<div class="imgR"></div>
					</div>
					<div class="admBtnGreenb admBtnBlueb">
						<div class="imgL"></div>
						<div><input type="reset" name="resetform" value='<fmt:message key="RESET"/>'></div>
						<div class="imgR"></div>
					</div>
				</td>
			</tr>
		</table>			
	</form>
	</body>
</html>