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
	   <title>
							<fmt:message key="NL.SUBSCRIBERS_DETAILS"/>
	   </title>
	    
				<script type="text/javascript" src="/site/core/script/validation.js" type="text/javascript">/**/</script>
				<script type="text/javascript" src="/script/jquery.min.js">/**/</script>				
				<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
				<script type="text/javascript" src="/script/cufon-yui.js"></script>
				<script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
				<script type="text/javascript" src="/script/common_functions.js"></script>
	</head>
	<body>
	<form name="nl_form_category" method="POST" enctype="multipart/form-data" action="/admin/nl_editsubscriber">			
		<spring:bind path="subscriber.id">
			<input type="hidden" name="<c:out value="${status.expression}"/>" value="<c:out value="${status.value}"/>" />
		</spring:bind>
		<negeso:admin>
		<table align="center" border="0" cellpadding="0" cellspacing="0" class="admTable">
			<tr>
				<td style="width:auto; height:auto;" colspan="2">  
					<jsp:include page="/WEB-INF/jsp/common/navig_bar.jsp" />
				</td>
			</tr>
			<!-- Title table -->
			<tr>	
				<td align="center" class="admNavPanelFont"  style="height:auto;" colspan="2" >
					<c:out value="${subscriber.email}"/> information:
				</td>            
			</tr>
			<!--TITLE -->
			<tr> 
        		<td class="admTDtitles" style="height:auto">
            		<c:out value="Name"/>
				</td> 
				<td class="admTDtitles" style="height:auto" >
            		<c:out value="Value"/>
				</td> 
			</tr>
			<!--CONTENT -->
			<c:forEach var="attribute" items="${subscriber.attributes}">
				<tr>
					<th class="admTableTD" width="215">
						<%-- TODO add constants into the dictionary --%>
                        <c:if test="${attribute.subscriberAttributeType.key == 'EMAIL'}">
							<fmt:message key="NL.EMAIL"/>
                        </c:if>
                        <c:if test="${attribute.subscriberAttributeType.key == 'FULL_NAME'}">
							<fmt:message key="NL.FULL_NAME"/>
                        </c:if>
                        <c:if test="${attribute.subscriberAttributeType.key == 'TITLE'}">
							<fmt:message key="NL.TITLE"/>
                        </c:if>
                        <c:if test="${attribute.subscriberAttributeType.key == 'FIRST_NAME'}">
							<fmt:message key="NL.FIRST_NAME"/>
                        </c:if>
                        <c:if test="${attribute.subscriberAttributeType.key == 'MIDDLE_NAME'}">
							<fmt:message key="NL.MIDDLE_NAME"/>
                        </c:if>
                        <c:if test="${attribute.subscriberAttributeType.key == 'LAST_NAME'}">
							<fmt:message key="NL.LAST_NAME"/>
                        </c:if>
                        <c:if test="${attribute.subscriberAttributeType.key == 'SUFFIX'}">
							<fmt:message key="NL.SUFFIX"/>
                        </c:if>
                        <c:if test="${attribute.subscriberAttributeType.key == 'GENDER'}">
							<fmt:message key="NL.GENDER"/>
                        </c:if>
                        <c:if test="${attribute.subscriberAttributeType.key == 'COMPANY_NAME'}">
							<fmt:message key="NL.COMPANY_NAME"/>
                        </c:if>
                        <c:if test="${attribute.subscriberAttributeType.key == 'ADDRESS'}">
							<fmt:message key="NL.ADDRESS"/>
                        </c:if>
                        <c:if test="${attribute.subscriberAttributeType.key == 'POSTAL_CODE'}">
							<fmt:message key="NL.POSTAL_CODE"/>
                        </c:if>
                        <c:if test="${attribute.subscriberAttributeType.key == 'TOWN'}">
							<fmt:message key="NL.TOWN"/>
                        </c:if>
                        <c:if test="${attribute.subscriberAttributeType.key == 'COUNTRY'}">
							<fmt:message key="NL.COUNTRY"/>
                        </c:if>
						<c:if test="${attribute.subscriberAttributeType.required == true}">*</c:if>
					</th>
					<th class="admTableTDLast">
						<input class="admTextArea admWidth465" type="text" 
							name="attribute_${attribute.subscriberAttributeType.key}" value="${attribute.value}"
							<c:if test="${attribute.subscriberAttributeType.required == true}">
							required="true"
							</c:if>
							<c:if test="${attribute.subscriberAttributeType.key == 'EMAIL'}">
							is_email="true"
							</c:if>
						/>
					</th>
			    </tr>
			</c:forEach>
			<tr>
				<th class="admTableTD">
                    <fmt:message key="LANGUAGE"/>
				</th>
				<th class="admTableTDLast">
					<select class="admTextArea admWidth150" name="subscriptionLangId">
						<c:forEach var="lang" items="${languages}">
							<option value="${lang.id}" id="lang_id"
								<c:if test="${subscriber.subscriptionLangId == lang.id}">
									<c:out value="selected"/>
								</c:if>
							><c:out value="${lang.code}" />
							</option>
						</c:forEach>
					</select>
				</th>
			</tr>
			<!--TITLE -->
			<tr> 
        		<td align="center" class="admNavPanelFont"  style="height:auto;" colspan="2" >
            		<c:out value="${subscriber.email}"/> <fmt:message key="NL.IS_MEMBER_OF_GROUPS"/>:
				</td> 				
			</tr>
			<tr> 
        		<td class="admTDtitles" style="height:auto" colspan="2">
            		<fmt:message key="NL.GROUP_TITLE"/>
				</td>
			</tr>
			<!--CONTENT -->
			<c:forEach var="group" items="${groups}">
				<tr>
					<th class="admTableTD">
						<c:out value="${group.title}" />
					</th>
					<th class="admTableTD">
						<input type="checkbox" name="group_${group.id}"
							<c:forEach var="g1" items="${subscriber.subscriberGroups}">
								<c:if test="${group.id == g1.id}"><c:out value="checked='checked'"/></c:if> 
							</c:forEach>
						/>
					</th>
			    </tr>
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
						<div><input type="button" class="admNavbarInp" value='<fmt:message key="SAVE_AND_CLOSE"/>' onClick="if (validate(this.form)) this.form.submit();"/></div>
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