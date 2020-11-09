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
								<fmt:message key="NL.IMPORT_SUBSCRIBERS"/>
				</title>	
			
	   <script type="text/javascript" src="/script/jquery.min.js">/**/</script>				
	   <script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
				<script type="text/javascript" src="/script/cufon-yui.js"></script>
				<script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
				<script type="text/javascript" src="/script/common_functions.js"></script>
				<script type="text/javascript" src="/site/core/script/validation.js" type="text/javascript">/**/</script>	    
	</head>
	<body>
    <script type="text/javascript">
	function try_delete(id){
		if(confirm('<fmt:message key="DELETE_CONFIRMATION"/>'))
			document.location='/admin/nl_categorylist?action=deleteSubscriptionCategory&categoryId='+id;
	}
	</script>
		<script type="text/javascript">
            function isEMailCorrect(email){
                var reg = /^([A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,4})$/;
                return reg.test(email);
            }

            function save_form(){
				var obj_form = document.forms['nl_form_import'];
				var err_str = '';
				var check_group = false;
				

				// check file name if upload method is 'add' or 'overwrite'
				if(obj_form.elements['importFile'].value == '' && !obj_form.elements['upload_method'][2].checked ){
					err_str = err_str + '<fmt:message key="NL.SELECT_IMPORT_FILE"/>' +'\n';
				}

                // check for single e-mail
                if (obj_form.elements['upload_method'][2].checked && (StringUtil.trimAll(obj_form.elements['singleEmail'].value) == '' || !isEMailCorrect(obj_form.elements['singleEmail'].value))){
                    err_str = err_str + '<fmt:message key="INVALID_EMAIL_ADDRESS"/>' +'\n';

                }

                // check for groups
				var checkbox_list = obj_form.getElementsByTagName('input');
				for (var i = 0; i < checkbox_list.length; i++){
					if(checkbox_list[i].getAttribute('type') == 'checkbox' && checkbox_list[i].checked){
						check_group = true;
						break;
					}
				}
				if(!check_group){
					err_str = err_str + '<fmt:message key="NL.SELECT_AT_LEAST_ONE_GROUP"/>' +'\n';
				}
				
					
				if(err_str != ''){
					alert(err_str);
				}
				else{
					obj_form.submit();
				}
			}
		</script>
<negeso:admin>		
<!-- Title table -->
<form name="nl_form_import" method="POST" enctype="multipart/form-data" action="/admin/nl_import?action=importDetails">
<table align="center" border="0" cellpadding="0" cellspacing="0" class="admTable"  width="764px">
    <tr>
        <td style="width: auto; height: auto;" colspan="2">
            <jsp:include page="/WEB-INF/jsp/common/navig_bar.jsp" />
        </td>
    </tr>
    <tr>
        <td align="center" class="admNavPanelFont" style="height: auto;" colspan="2">
            <fmt:message key="NL.IMPORT_SUBSCRIBERS" />
        </td>
    </tr>
    <!--CONTENT -->
    <tr>
        <td class="admTableTD">
            <nobr>
			    <input type="radio" name="upload_method" value="add" checked="true"> <fmt:message key="NL.ADD_AND_UPDATE_SUBSRIBERS"/><br/>
				<input type="radio" name="upload_method" value="overwrite"> <fmt:message key="NL.ADD_AND_SKIP_SUBSCRIBERS"/><br/>
			</nobr>
        </td>
        <td class="admTableTDLast">
            <fmt:message key="NL.IMPORT_SUBSCRIBERS_FILE" />&#160;
            <input type="file" class="admInpButton" name="importFile"><br />
            <span class="admBold"><fmt:message key="NL.NOTE" />:</span>
            <fmt:message key="NL.OPERATION_CAN_TAKE_LONG_TIME" />
        </td>
    </tr>
    <tr>
        <td class="admTableTDLast">
            <nobr><fmt:message key="NL.USE_DELIMITER"/></nobr>
        </td>
        <td class="admTableTDLast">
            <input type="text" class="admInpText admWidth_50" name="delimiter" value=";">
        </td>
    </tr>
    <!--begin single email-->
    <tr>
        <td class="admTableTDLast">
            <input type="radio" name="upload_method" value="single">
            <fmt:message key="NL.ADD_SINGLE_EMAIL" />
        </td>
        <td class="admTableTDLast">
            <input class="admTextArea" type="text" name="singleEmail" data_type="email" value="" />
        </td>
    </tr>
    <!--end single email-->
    <c:if test="${fn:length(languages) > 1}">
	    <tr>
		    <td class="admTableTDLast">
			    <nobr>Subscriber language:</nobr>
		    </td>
			<td class="admTableTDLast" >
				<select class="admInpText admWidth150" name="langId" >
					<c:forEach var="lang" items="${languages}">
						<option value="${lang.id}" name="langId">
							<c:out value="${lang.code}" />
						</option>
					</c:forEach>
				</select>
			</td>
		</tr>
	</c:if>
    <tr>
	    <td class="admTableTDLast" colspan="2" >
            <div class="admNavPanelInp">
				<div class="imgL"></div>
                <div>
					<input type="button" class="admNavbarInp" name="submit_button" value="<fmt:message key="NL.IMPORT"/>" onclick="save_form()" >
						<c:if test="${fn:length(groups) == 0}"><c:out value="disabled=disabled"/></c:if>
					</input>
                </div>
                <div class="imgR"></div>
             </div>
         </td>
	</tr>
	<!-- Validation errors -->					   
		<c:if test="${isImportRunning}">
		<tr style="height: 12px;">
            <td style="font-size: 10px; color: red; border: 0; padding: 0; text-align: left; vertical-align: middle; height: 12px;" >
                Import is running!
            </td>
        </tr>
		</c:if>
	<c:forEach items="${errors}" var="error">
		<tr style="height: 12px;">
			<td style="font-size: 10px; color: red; border: 0; padding: 0; text-align: left; vertical-align: middle; height: 12px;" >
				<c:out value="${error}"/>				
			</td>
		</tr>
	</c:forEach>	
	<tr>
        <td colspan="2" >
            <table width="100%">
                <tr>
                    <td align="center" class="admNavPanelFont"  style="height:auto;" >&nbsp;</td>
		            <td align="center" class="admNavPanelFont"  style="height:auto;">
			            <fmt:message key="NL.GROUPS"/>
		            </td>
		            <td align="center" class="admNavPanelFont"  style="height:auto;">
			            <fmt:message key="IS_MULTILINGUAL"/>
		            </td>
	            </tr>			
	            <c:choose>
		            <c:when test="${fn:length(groups) > 0}">
			            <c:forEach var="group" items="${groups}">
				            <tr>
					            <td  class="admTableTDLast">
						            <input type="checkbox" name="group_<c:out value="${group.id}"/>" value="group_<c:out value="${group.id}"/>">
					            </td>
					            <td  class="admTableTD">
						            <c:choose>
					   		            <c:when test="${fn:length(group.title)>50}">
					   			            <c:out value="${fn:substring(group.title,0,50)}"></c:out>...
					   		            </c:when>
					   		            <c:otherwise>
					   			            <c:out value="${group.title}"></c:out>
					   		            </c:otherwise>
						            </c:choose>								
					            </td>
					            <td  class="admTableTDLast">
						            <c:choose>
					   		            <c:when test="${group.i18n}">
					   			            <fmt:message key="NL.YES"/>
					   		            </c:when>
					   		            <c:otherwise>
					   			            <fmt:message key="NL.NO"/>
					   		            </c:otherwise>
						            </c:choose>								
					            </td>
				            </tr>
			            </c:forEach>
		            </c:when>
		            <c:otherwise>
			            <tr>
				            <td  class="admTableTDLast" colspan="3">
					            <fmt:message key="NL.NO_GROUPS"/>
				            </td>
			            </tr>
		            </c:otherwise>
	            </c:choose>
            </table>
        </td>
    </tr>
    <tr>
            <c:choose>
                <c:when test="${isImportRunning}">
			        <td colspan="1" align="center">
			            <b>Running import log...</b>
			        </td>
	                
	                <td>
		                <div class="admNavPanelInp">
		                    <div class="imgL"></div>
		                    <div>
		                        <input type="button" class="admNavbarInp" name="submit_button" value="Refresh" onclick="window.location.href='/admin/nl_import'"></input>
		                    </div>
		                    <div class="imgR"></div>
		                 </div> 
	                </td>
                </c:when>
                <c:otherwise>
                    <td colspan="2" align="center"><b>Previous import log:</b></td>
                </c:otherwise>
            </c:choose>
    </tr>
    <tr>
        <td colspan="2">
        <table align="center"  border="0" cellpadding="0" cellspacing="0" class="admTable">         
            <tr>
                <td class="admTDtitles">
                    <fmt:message key="DATE"/>
                </td>
                <td class="admTDtitles">
                    <fmt:message key="NL.AUTHOR"/>
                </td>
                <td class="admTDtitles">
                    <fmt:message key="NL.EVENT"/>
                </td>
                <td class="admTDtitles">
                    <fmt:message key="DESCRIPTION"/>
                </td>
                <td class="admTDtitles">
                    <fmt:message key="NL.RESULT"/>
                </td>
            </tr>
            <c:choose>
                <c:when test="${fn:length(events) > 0}">
                    <c:forEach var="event" items="${events}">
                        <tr>
                            <td class="admTableTD" align="center">
                                <fmt:formatDate value="${event.date}" type="both" pattern="yyyy-MM-dd" />
                            </td>
                            <td class="admTableTD" align="center">
                                <c:out value="${event.author}"></c:out>
                            </td>
                            <td class="admTableTD" align="center">
                                <c:out value="${event.event}"></c:out>
                            </td>
                            <th class="admTableTD">
                                <c:out value="${event.description}"></c:out>
                            </th>
                            <td class="admTableTDLast" align="center">
                                <c:choose>
                                    <c:when test="${event.result != 'OK'}">
                                        <span class="admRed admBold">
                                    </c:when>
                                    <c:otherwise>
                                        <span class="admBold">
                                    </c:otherwise>
                                </c:choose>
                                    <c:out value="${event.result}"></c:out>
                                </span>
                            </td>
                        </tr>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <tr>
                        <td class="admTableTDLast" colspan="5">
                            <fmt:message key="NL.NO_EVENTS"/>
                        </td>
                    </tr>
                </c:otherwise>
            </c:choose>
        </table>
        </td>
    </tr>
    <tr>
        <td class="admTableFooter" colspan="2">&nbsp;</td>
    </tr>
</table>
</form>
</negeso:admin>
</body>
</html>




	<!--  a href="/admin/nl_import?action=importDetails" -->
