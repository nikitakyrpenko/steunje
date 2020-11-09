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
        <meta name="SKYPE_TOOLBAR" content ="SKYPE_TOOLBAR_PARSER_COMPATIBLE" />
		<title>
			<c:choose>
				<c:when test="${publication.id == 0 || publication.id == null}">New publication</c:when>
				<c:otherwise>Edit publication</c:otherwise>
			</c:choose>
   		</title>
		<link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>				    
		<link rel="stylesheet" type="text/css" href="/css/jquery-ui.custom.css"/>    
								
		<script type="text/javascript" src="/script/jquery.min.js">/**/</script>				
		<script type="text/javascript" src="/script/jquery-ui.custom.min.js">/**/</script>	
		<script type="text/javascript" src="/script/jquery-ui-timepicker-addon.js">/**/</script>	
		<script type="text/javascript" src="/script/cufon-yui.js"></script>
		<script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>    
    
		<script type="text/javascript" src="/script/common_functions.js"></script>
		<script type="text/javascript" src="/site/core/script/validation.js"></script>				
		<script type="text/javascript" src="/script/media_catalog.js">/**/</script>		    
		<script type="text/javascript" src="/script/conf.js">/**/</script>		
		<script type="text/javascript" src="/script/RTE_Adapter.js">/**/</script>
		<script language="JavaScript" src="/script/rte_implement.js">/**/</script>	  
				
		<script type="text/javascript">    
			var joinedList = null;
			var multilingual_flag = ${publication.i18n};
	   
			function attachmentsInit(){
				var att_list = '<c:out value="${attachments}"/>';
				var arr_att_list = att_list.split(';');
				for(var i = 0; i < arr_att_list.length; i++)
					if(arr_att_list[i] != ''){
						insertTableRowLast(arr_att_list[i]);
						updateAttachmentLink(arr_att_list[i]);
					}
			}

			$(document).ready(function() {
				if (${publication.id > 0}) {
				    validate_();
				}
			})
	   
			function createOpenAttachmentLink(link) {
				return '<a href="javascript:openDocument(\'' + link + '\')\">' + link + '</a>';
			}
	   
			function createRemoveAttachmentLink() {
				return '<img src="/images/delete.png" class="admHand" ' +
					   'title="<fmt:message key='DELETE'/>"'+
					   'onClick="return removeAttachment(this.parentNode.parentNode)"></img>';
			}
	
			function createTable() {
				var div = document.getElementById("dynTable");
				table = document.createElement("table");
				table.setAttribute("id", "attachment_table");
				table.setAttribute("width", "100%");
				table.setAttribute("class", "admTable");		   
				table.setAttribute("cellspacing", 0);
				table.setAttribute("cellpadding", 0);
				table.setAttribute("border", 0);		   
				div.appendChild(table);
				return table;
			}
	   
			function insertTableRowLast(link) {
				var att = document.getElementById("attachment_table").insertRow(0);
			
				var td1= att.insertCell(0);
				td1.setAttribute("class", "admTableTD");
				td1.className = "admTableTD";
				var td2= att.insertCell(1);
				td2.className = "admTableTDLast admWidth30";
			
				td1.innerHTML = createOpenAttachmentLink(link);
				td2.innerHTML = createRemoveAttachmentLink();
			} 

			/* function addAttachment(id) {
				result = MediaCatalog.selectFileDialog();
				if (result == null) {
					alert('<fmt:message key='UNKNOW_ERROR'/>');
           		}else{
					resultUploadImage(result);
				}
			} */
			
			function addAttachment(id) {
				window.returnValue = undefined;
				MediaCatalog.selectFileDialog(function(result){
				if (result == undefined) result = window.returnValue;
				if (result == null) {
					alert('<fmt:message key='UNKNOW_ERROR'/>');
           		}else{
					resultUploadImage(result);
				}
				});
			}
       
			function resultUploadImage(returnValue){
				var att_list = document.forms["nl_publication_form"].elements["attachmentLink"];
				if(att_list.value.indexOf(returnValue.fileUrl) == -1){
					insertTableRowLast(returnValue.fileUrl);
					updateAttachmentLink(returnValue.fileUrl);
				}else{
					alert('<fmt:message key='NL.FILE_ALWAYS_ATTACHED'/>');
				}
			}

			function removeAttachment(obj) {
				if (confirm("<fmt:message key='NL.DELETE_ATTACHMENT_CONFIRMATION'/>")) {
					var attachment_to_remove = obj.firstChild.firstChild.innerHTML;
					obj.parentNode.removeChild(obj);
	            
					var attachments_list = document.forms["nl_publication_form"].elements["attachmentLink"];
					attachments_list.value = attachments_list.value.replace(attachment_to_remove+';','');
				}
			}
       
			function updateAttachmentLink(joinedList) {
				document.forms["nl_publication_form"].elements["attachmentLink"].value += joinedList + ';';
			}       
       
			function openDocument(link) {
				if (link != "undefined") {
					window.open(link);
				} else {
					alert ("<fmt:message key='NL.DOCUMENT_IS_UNDEFINED'/>");
				}
			}
       
			function save(){
				if (!validate_()) {
					return;
				}
				document.getElementById('text').value = document.getElementById('article_text${publication.article.id}').innerHTML.replace(new RegExp("(/admin)",'g'),"");
				document.getElementById('plainText').value = document.getElementById('article_plain_text').innerHTML.replace(new RegExp("(/admin)",'g'),"");
			
				if(!document.forms['nl_publication_form'].elements['ch_i18n'].checked){
					document.getElementById('i18n').value = 'false';
				}
			
				if(!document.forms['nl_publication_form'].elements['ch_i18n'].checked && multilingual_flag){
					if(confirm(document.getElementById('unselect_confirm').innerHTML)){
						document.forms['nl_publication_form'].submit();
					}
				}else{
					document.forms['nl_publication_form'].submit();			
				}
			}
		
			function saveAndClose(param){												
				if (param == 'false')
					document.forms["nl_publication_form"].elements["save"].value='save_and_close';
				save();
			}

			function resetform (){
				document.forms["nl_publication_form"].reset();
				validate_();
			}
		
			function duplicate(id){
				if(confirm(document.getElementById('duplicate_confirm').innerHTML)){
												window.location.href='/admin/nl_editpublication?duplicate=true&id=' + id;
						}
				}

			function proof(id, langId){
				if (validate_()) {
				    proofAjax(id,langId);
				}
			}

			function send(id,langId){
				if (validate_()) {
				    sendAjax(id,langId);
				}
			}

			function pageLink(link){
				attr = "resizable:yes,scroll:yes,scrollbars=yes,status:yes,dialogWidth:900px,dialogHeight:500px";
   				dp = window.open(link, 'duplicate_banner', attr);
   				dp.focus();
			}				

								function sendAjax(id,langId) {
									<c:if test="${publication.article.id == null}">
										document.getElementById('text').value = document.getElementById('article_text').innerHTML.replace(new RegExp("(/admin)",'g'),"");
									</c:if>
	                                document.getElementById('plainText').value = document.getElementById('article_plain_text').innerHTML.replace(new RegExp("(/admin)",'g'),"");

                                    if(!document.forms['nl_publication_form'].elements['ch_i18n'].checked){
                                                    document.getElementById('i18n').value = 'false';
			}
		
                                    if(!document.forms['nl_publication_form'].elements['ch_i18n'].checked && multilingual_flag){
                                        }
										$.ajax({
						                    type: "POST",
						                    url: "/admin/nl_editpublication",
						                    data: $(document.forms["nl_publication_form"]).serialize(),
						                    dataType: "html",
						                    success: function(html, stat) {
			    if(confirm(document.getElementById('send_confirm').innerHTML)){
    setTimeout(function () {
						                    	    window.location.href='/admin/nl_editpublication?send=true&id=' + id+'&lang_id='+langId;
						                    	    }, 90);
						                    	}
						                    },
						                    error: function(html, stat) {
						                        alert('Internal server error');
    }
    });
									}

								function proofAjax(id,langId) {
									<c:if test="${publication.article.id == null}">
										document.getElementById('text').value = document.getElementById('article_text').innerHTML.replace(new RegExp("(/admin)",'g'),"");
									</c:if>
                                    document.getElementById('plainText').value = document.getElementById('article_plain_text').innerHTML.replace(new RegExp("(/admin)",'g'),"");

                                    if(!document.forms['nl_publication_form'].elements['ch_i18n'].checked){
                                                    document.getElementById('i18n').value = 'false';
                                    }

                                    if(!document.forms['nl_publication_form'].elements['ch_i18n'].checked && multilingual_flag){
                                        }
                                        $.ajax({
                                            type: "POST",
                                            url: "/admin/nl_editpublication",
                                            data: $(document.forms["nl_publication_form"]).serialize(),
                                            dataType: "html",
                                            success: function(html, stat) {
                                            	if(confirm(document.getElementById('proof_confirm').innerHTML)){
                                            	    setTimeout(function () {
                                            	    document.location.href='/admin/nl_editpublication?proof=true&id=' + id+'&lang_id='+langId;
                                            	    document.getElementById('proof_td'+id).className="admDarkTD";
                                            	    document.getElementById('proof_button'+id).href="#";
                                            	    document.getElementById('proof_image'+id).title="<fmt:message key="NL.PROOF_DISABLED"/>";
                                            	    document.getElementById('proof_image'+id).alt="<fmt:message key="NL.PROOF_DISABLED"/>";
    }, 90);

				}
                                            },
                                            error: function(html, stat) {
                                                alert('Internal server error');
			}
                                        });
			}
			function validate_() {
				var is_valid = true;
				if ($.trim($('input[name=feedbackEmail]').val())== '') {
					$('input[name=feedbackEmail]').addClass('error');
					is_valid = false;
					}else {
						$('input[name=feedbackEmail]').removeClass('error');	
					}
			if ($.trim($('input[name=title]').val()) == '') {
				is_valid = false;
                $('input[name=title]').addClass('error');
                }else {
                    $('input[name=title]').removeClass('error');    
                }
            return is_valid;
            }
		</script>
	</head>

	<body>
		<div id="duplicate_confirm" style="display:none"><fmt:message key="NL.DUPLICATE_CONFIRM"/></div>  
        <div id="send_confirm" style="display:none"><fmt:message key="NL.SEND_CONFIRM"/></div>  
		<div id="proof_confirm" style="display:none"><fmt:message key="NL.PROOF_CONFIRM"/></div>
		<div id="unselect_confirm" style="display:none"><fmt:message key="NL.UNSELECT_CONFIRM"/></div>
 
		<negeso:admin>
			<!-- Title table -->
			<table align="center"  border="0" cellpadding="0" cellspacing="0" class="admTable" >
				<tr>
					<td  style="width:auto; height:auto;"  colspan="6">  
						<jsp:include page="/WEB-INF/jsp/common/navig_bar.jsp" />
					</td>  
				</tr>
    			<tr>        	
					<td align="center" class="admNavPanelFont"  style="height:auto;">
						<c:choose>
							<c:when test="${publication.id <= 0}">
								<fmt:message key="NL.ADD_PUBLICATION"/>
							</c:when>
							<c:otherwise>
								<fmt:message key="NL.EDIT_PUBLICATION"/>
							</c:otherwise>
						</c:choose>
					</td>           
				</tr>
				<tr>
					<td colspan="3">
						<!-- Content table -->
						<form name="nl_publication_form" action="/admin/nl_editpublication" method="POST" enctype="multipart/form-data" >
    						<input type="hidden" name="id" value="${publication.id}">
							<input type="hidden" name="lang_id" value="${publication.langId}">
    						<input type="hidden" id="text" name="text">
    						<input type="hidden" id="plainText" name="plainText">
							<input type="hidden" name="attachmentLink" value=""></input>
							<input type="hidden" name="save"></input>    	
    						<!-- <input type="hidden" name="pbl_mailTemplateId" value="${mailTemplate.id}">-->
							
							<table cellspacing="0" cellpadding="0"  align="center" border="0" style="width:764px;">
								<tr>
									<td class="admTableTDLast" id="admTableTDtext" align="right" colspan="2">
										<table  cellspacing="0" cellpadding="0" align="right">
											<tr>
												<td>
													<fmt:message key="LANGUAGE"/>
												</td>
												<td>
													<select class="admWidth100"
														<c:if test="${publication.i18n == false}">
															<c:out value="disabled" />
														</c:if> 
														onchange="window.location.href = '/admin/nl_editpublication?id=${publication.id}&lang_id='+
																						  this.options[this.selectedIndex].value + 
																						 '&mail_template_id=${publication.mailTemplate.id}&title=${publication.title}' + 
																						 '&category_id=${publication.subscriptionCategory.id}'">
													
														<c:forEach var="lang" items="${languages}">
															<option value="${lang.id}" id="lang_id"
																<c:if test="${publication.langId == lang.id}">
																	<c:out value="selected"/>
																</c:if>
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
									<th class="admTableTDLast">
										<fmt:message key="NL.SUBJECT"/>*
									</th>
									<td class="admTableTDLast">
										<spring:bind path="publication.title">
											<input class="admTextArea" type="text" name="${status.expression}" value="${status.value}"/>
											<c:if test="${status.error}">
		               							<c:forEach var="error" items="${status.errorCodes}">
			                  						<table cellspacing="0" cellpadding="0" style="height: 12px; margin: 0; padding: 0; border: 0;">
			            								<tr style="height: 12px;">
			                  								<td style="font-size: 10px; color: red; border: 0; padding: 0; text-align: left; vertical-align: middle; height: 12px;">
				                  								<c:out value="${error}"/>				                  																																						
			                  								</td>
			                  							</tr>
			               							</table>
			           							</c:forEach>							
											</c:if>	
										</spring:bind>
									</td>
								</tr>
								<tr>
									<th class="admTableTDLast">
										<fmt:message key="DESCRIPTION"/>
									</th>
									<td class="admTableTDLast">
										<input type="text" class="admTextArea" name="description" value="${publication.description}"/>
									</td>
								</tr>
								<tr>
									<th class="admTableTDLast">
										<fmt:message key="NL.CONTENT_HTML"/>
									</th>
									<td class="admTableTDLast">
										<img src="/images/mark_1.gif" class="RTEEntryPoint" alt="Editor"
											onclick="RTE_Init('article_text${publication.article.id}','article_text${publication.article.id}', 
											<c:choose>
												<c:when test="${publication.article.id == null}">0</c:when>
												<c:otherwise>${publication.article.id}</c:otherwise>
											</c:choose>, 3, 0, 'contentStyle', getInterfaceLanguage(), null, true);" 
											alt="Editor (last modified: ${publication.article.lastModified})"/>
										<div id="article_text${publication.article.id}" class="admRTEDiv" dir="ltr" style="margin-right:0;">
											<c:choose>
												<c:when test="${publication.article.text == null}">&#160;</c:when>
												<c:otherwise>
													<c:out value="${publication.article.text}" escapeXml="yes"/>
												</c:otherwise>
											</c:choose>
										</div>
									</td>
								</tr>
								<tr>
									<th class="admTableTDLast">
										<fmt:message key="NL.CONTENT_PLAIN"/>
									</th>
									<td  class="admTableTDLast">
										<img class="admBorder admHand" alt="<fmt:message key="EDIT"/>" title="<fmt:message key="EDIT"/>" onclick="edit_text('article_plain_text', 'contentStyle', '', '',1, true);" src="/images/mark_1.gif"/>
										<div id="article_plain_text" class="admRTEDiv" dir="ltr" style="margin-right:0;">
											<c:choose>
												<c:when test="${publication.plainText == null}">&#160;</c:when>
												<c:otherwise>
													<c:out value="${publication.plainText}" escapeXml="yes"/>
												</c:otherwise>
											</c:choose>
										</div>
									</td>
								</tr>
								<tr>
									<th class="admTableTDLast">
										<fmt:message key="NL.SEND_TO"/>
									</th>
									<td class="admTableTDLast">
										<table cellpadding="0" cellspacing="0" width="100%" border="0">
											<c:forEach var="group" items="${groups}">
												<tr>
													<td class="admLeft admWidth30">
														<input type="checkbox" name="group_${group.id}"
															<c:forEach var="g1" items="${publication.subscriberGroups}">
																<c:if test="${group.id == g1.id}">
																	<c:out value="checked='checked'"/>
																</c:if> 
															</c:forEach>
														/>
													</td>
													<td class="admLeft">
														<c:out value="${group.title}" />
													</td>
					    						</tr>
											</c:forEach>
										</table>
									</td>
								</tr>
								<tr>
									<th class="admTableTDLast">
										<fmt:message key="NL.CHOOSE_ACTION"/>
									</th>
									<td class="admTableTDLast">
										<select class="admInpText" name="state">
											<c:forEach var="state" items="${states}">
												<c:if test="${state.name == 'scheduled' or state.name == 'suspended'}">
													<option value="${state.id}"
														<c:if test="${publication.publicationState.id == state.id}">
															<c:out value="selected"/>
														</c:if>
													>
													<c:if test="${state.name == 'scheduled'}">
														<fmt:message key="NL.SCHEDULE"/>
													</c:if>
													<c:if test="${state.name == 'suspended'}">
														<fmt:message key="NL.SUSPEND"/>
													</c:if>
												</option>
											</c:if>
										</c:forEach>
									</select>
								</td>
							</tr>
					 		<tr>
								<th class="admTableTDLast">
									<fmt:message key="NL.SCHEDULE"/>
								</th>
								<td class="admTableTDLast">	
									<input type="text" class="admTextArea" required="true" type="text"  name="publishDate" id="publishDate"
									       value="${publication.formattedPublishDate}"/>	
									<script>
                						$(function () {				
											$("#publishDate").datetimepicker({
													dateFormat: 'dd-mm-yy',
													//showAnim: 'slideDown',
													showSecond: true,
													timeFormat: 'hh:mm:ss',
													showOn: 'button',
													buttonImage: '/images/calendar.gif',
													buttonImageOnly: true
											});
										});		
									</script>																																									
                    															
									<spring:bind path="publication.publishDate">
										<c:if test="${status.error}">
		               						<c:forEach var="error" items="${status.errorCodes}">
			                  					<table cellspacing="0" cellpadding="0" style="height: 12px; margin: 0; padding: 0; border: 0;">
			            							<tr style="height: 12px;">
			                  							<td style="font-size: 10px; color: red; border: 0; padding: 0; text-align: left; vertical-align: middle; height: 12px;">
				                  							<c:out value="${error}"/>
			                  							</td>
			                  						</tr>
			               						</table>
			           						</c:forEach>							
										</c:if>	
									</spring:bind>
								</td>				              
							</tr>
                            <tr>
								<th class="admTableTDLast">
									<fmt:message key="NL.FEEDBACK_NAME"/>
								</th>
								<td class="admTableTDLast">
									<input class="admTextArea" type="text" name="feedbackName" 
										value="<c:out value="${publication.feedbackName}"/>"/>
								</td>
							</tr>
							<tr>
								<th class="admTableTDLast">
									<fmt:message key="NL.FEEDBACK_EMAIL"/>*
								</th>
								<td class="admTableTDLast">
								    <spring:bind path="publication.feedbackEmail">
									   <input class="admTextArea" type="text" name="${status.expression}" value="${status.value}"/>
									   <c:if test="${status.error}">
                                           <c:forEach var="error" items="${status.errorCodes}">
                                               <table cellspacing="0" cellpadding="0" style="height: 12px; margin: 0; padding: 0; border: 0;">
                                                   <tr style="height: 12px;">
                                                       <td style="font-size: 10px; color: red; border: 0; padding: 0; text-align: left; vertical-align: middle; height: 12px;">
                                                           <c:out value="${error}"/>                                                                                                                                                                                       
                                                       </td>
                                                   </tr>
                                               </table>
                                           </c:forEach>                            
                                       </c:if> 
									</spring:bind>
								</td>
							</tr>
							<tr>
								<th class="admTableTDLast">
									<fmt:message key="IS_MULTILINGUAL"/>
								</th>
								<td class="admTableTDLast">
									<input type="checkbox" id="ch_i18n" value="true"
										<c:if test="${publication.i18n == true}">
											<c:out value="checked=checked"/>
										</c:if>
									/>
									<input type="hidden" id="i18n" name="i18n" value="true"/>
								</td>
							</tr>
							<tr>
								<th class="admTableTDLast">
									<fmt:message key="NL.PUBLICATION_ACCESS_CODE"/>
								</th>
								<td class="admTableTDLast">
									<input readonly="readonly" class="admTextArea" type="text" name="accessCode" value="<c:out value="${publication.accessCode}"/>" readonly="readonly"/>
								</td>
							</tr>
							<tr>
								<th class="admTableTDLast">
									<fmt:message key="NL.PUBLICATION_PAGE_LINK"/>
								</th>
								<td class="admTableTDLast">
									<input readonly="readonly" class="admTextArea" type="text" name="pageLink" value="${publication.pageLink}" readonly="readonly" />
									<c:if test="${publication.pageLink != null}">
										<a onClick="pageLink('${publication.pageLink}');"><fmt:message key="NL.LINK_TO_PAGE_ETC"/></a>	
									</c:if>
								</td>
							</tr>
							<tr>
								<td align="center" class="admTableTDLast" colspan="2">
									<table cellpadding="0" cellspacing="3" border="0">
										<tr>
											<td>
												<div class="admTDtitles"><fmt:message key="NL.ATTACHMENTS"/></div>
											</td>
											<td>
												<img class="admHand" title="Add" onClick="return addAttachment(${publication.id})" src="/images/new_attachment.png"/>
											</td>
										</tr>
									</table>												
									<div id="dynTable"></div>				
								</td>
							</tr>				
						</table>
					</form>
				</td>
			</tr>
			<tr>
				<td class="admTableFooter" colspan="2" >&nbsp;</td>
			</tr> 
		</table>
	
		<script type="text/javascript">
			// create dynamic table for attachments
			createTable();
			// init attached files
			attachmentsInit();
		</script>
	
	</negeso:admin>  
  
  
    <table cellpadding="0" cellspacing="0" width="764px" align="center" border="0" class="Buttons">
        <tr>
            <td>
                <div class="admBtnGreenb">
                    <div class="imgL"></div>
                    <div>
                        <a href="#" class="admNavbarInp" onClick="saveAndClose('true'); return false;" onFocus="blur()"><fmt:message key="SAVE"/></a>                            
                    </div>
                    <div class="imgR"></div>
                </div>
                
				<div class="admBtnGreenb" style="padding-left:5px;">
                    <div class="imgL"></div>
                    <div>
                        <a href="#" class="admNavbarInp" onClick="saveAndClose('false'); return false;" onFocus="blur()"><fmt:message key="SAVE_AND_CLOSE"/></a>
                    </div>
                    <div class="imgR"></div>
                </div>

                <c:if test="${publication.id>0}">
					<div class="admBtnGreenb" style="padding-left:5px;">
						<div class="imgL"></div>
                        <div>
							<a href="#" class="admNavbarInp" onClick="proof(${publication.id},${publication.langId}); return false;" onFocus="blur()"><fmt:message key="NL.PROOF"/></a>								
                        </div>
                        <div class="imgR"></div>
                    </div>
																				
					<div class="admBtnGreenb admBtnBlueb">
						<div class="imgL"></div>
                        <div>
							<a href="#" class="admNavbarInp" onClick="send(${publication.id},${publication.langId}); return false;" onFocus="blur()"><fmt:message key="SEND"/></a>								
                        </div>
                        <div class="imgR"></div>
					</div>

					<div class="admBtnGreenb admBtnBlueb" style="padding-right:5px;">
						<div class="imgL"></div>
                        <div>
							<a href="#" class="admNavbarInp" onClick="duplicate(${publication.id}); return false;" onFocus="blur()"><fmt:message key="NL.DUPLICATE"/></a>								
						</div>
                        <div class="imgR"></div>
                    </div>
				</c:if>

				<c:if test="${publication.id==0}">
					<div class="admBtnGreenb admBtnGreyb" style="padding-left:5px; float:left;">
						<div class="imgL"></div>
                        <div>
							<a style="color:#b2b2b2;" disabled="disabled" class="admNavbarInp" onFocus="blur()"><fmt:message key="NL.PROOF"/></a>
						</div>
                        <div class="imgR"></div>
                    </div>

                    <div class="admBtnGreenb admBtnGreyb">
						<div class="imgL"></div>
                        <div>
							<a style="color:#b2b2b2;" disabled="disabled" class="admNavbarInp" onFocus="blur()"><fmt:message key="SEND"/></a>
						</div>
                        <div class="imgR"></div>
                    </div>
                    
                    <div class="admBtnGreenb admBtnGreyb" style="padding-right:5px;">
						<div class="imgL"></div>
                        <div>
							<a style="color:#b2b2b2;" disabled="disabled" class="admNavbarInp" onFocus="blur()"><fmt:message key="NL.DUPLICATE"/></a>
						</div>
                        <div class="imgR"></div>
                    </div>                
				</c:if>

                <div class="admBtnGreenb admBtnBlueb" style="padding-right:5px;">
                    <div class="imgL"></div>
                    <div>
                        <a href="#" class="admNavbarInp" onClick="resetform();" onFocus="blur()"><fmt:message key="RESET"/></a>
                    </div>
                    <div class="imgR"></div>
                </div>
            </td>
        </tr>
    </table>  
</body>
</html>	