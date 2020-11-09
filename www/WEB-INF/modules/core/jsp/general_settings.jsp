<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>    
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="negeso" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%response.setHeader("Expires", "0");%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
	<title><fmt:message key="CM_GENERAL_SETTINGS" /></title>
	
	<script type="text/javascript" src="/script/jquery.min.js">/**/</script>	
	<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
	<script type="text/javascript" src="/script/cufon-yui.js"></script>
 <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
	<script language="JavaScript1.2" src="/script/common_functions.js" type="text/javascript"></script>

	<script type="text/javascript">
	
		function setFavIcon(){
	     	var strPage = "/?command=get-file-uploader-face&mode=doc" ;
            var strAttr = "resizable:on;scroll:on;status:off;dialogWidth:650px;dialogHeight:401px";
            showModalDialog(strPage, null , strAttr).then(function(result){
                if (result !=null && result.resCode == "OK"){
                    document.forms["editForm"].favicon.value = result.fileUrl;
                }
            });
		}
		
		function saveform (){
			document.forms["editForm"].submit();
		}

		function resetform (){
			document.forms["editForm"].reset();
		}

		function editPagePropertiesByFilename(filename) {
         	window.open("/admin/edit_page_info?act=edit&filename=" + filename ,"page_properties_"+new Date().getTime(),"height=920, width=825, menubar=no, resizable=yes,  status=no, titlebar=yes, toolbar=no, scrollbars=yes");
         }
	
	</script>
</head>
<body>

	<negeso:admin>
	
	<form name="editForm" method="POST" action="/admin/site_settings">
	<input type="hidden" name="action" value="save" />
	<table align="center"  border="0" cellpadding="0" cellspacing="0" class="admTable">		
        <tr>
			<td style="width:auto; height:auto;"><jsp:include page="/WEB-INF/jsp/common/navig_bar.jsp"/></td>
		</tr>
        <tr>			
            <td align="center" class="admNavPanelFont"  style="height:auto;" colspan="2" ><fmt:message key="CM_GENERAL_SETTINGS" /></td>		
		</tr>	
		<tr> 
			<td class="admTDtitles" width="50%" style="height:auto;"><fmt:message key="TITLE"/></td> 
			<td class="admTDtitles" width="50%" style="height:auto;"><fmt:message key="SS.VALUE"/></td> 
		</tr>
		<tr>
			<td class="admTableTD" id="admTableTDtext"><fmt:message key="SS.FAVICON"/>			</td> 
			<td class="admTableTDLast" id="admTableTDtext">
				<table border="0" cellpadding="0" cellspacing="0" >
					<tr>
						<td style="width:220px;">
							<input class="admTextArea admWidth200" readonly="true" id="favicon"	type="text" name="FAVICON_PATH" data_type="text" value="${FAVICON_PATH}"/>
						</td>
						<td>
							<div class="admNavPanelInp">
								<div class="imgL"></div>
								<div><input type="button" value="Select" name="selectImageButton"	onclick="setFavIcon();"></div>
								<div class="imgR"></div>
							</div>
						</td>
					</tr>
				</table>
			</td> 
		</tr>
		<tr>
			<td class="admTableTD" id="admTableTDtext"><fmt:message key="SS.BROWSER_PREFIX"/></td> 
			<td class="admTableTDLast" id="admTableTDtext">
				<input class="admTextArea admWidth200" 	type="text" name="BROWSER_PREFIX" data_type="text" value="${BROWSER_PREFIX}"/>
			</td> 
		</tr>
		<tr>
			<td class="admTableTD" id="admTableTDtext"><fmt:message key="SS.BROWSER_SUFFIX"/></td> 
			<td class="admTableTDLast" id="admTableTDtext">
				<input class="admTextArea admWidth200" 	type="text" name="BROWSER_SUFFIX" data_type="text" value="${BROWSER_SUFFIX}"/>
			</td> 
		</tr>	
		<tr>
			<td class="admTableTD" id="admTableTDtext"><fmt:message key="SS.COPYRIGHT"/></td> 
			<td class="admTableTDLast" id="admTableTDtext">
				<input class="admTextArea admWidth200" 	type="text" name="COPYRIGHT" data_type="text" value="${COPYRIGHT}"/>
			</td> 
		</tr>	
		<tr>
			<td class="admTableTD" id="admTableTDtext"><fmt:message key="SS.AUTHOR"/></td> 
			<td class="admTableTDLast" id="admTableTDtext">
				<input class="admTextArea admWidth200" 	type="text" name="AUTHOR" data_type="text" value="${AUTHOR}"/>
			</td> 
		</tr>			
		<tr>
            <td class="admTableTD" id="admTableTDtext"><fmt:message key="SS.NOODP"/></td> 
            <td class="admTableTDLast" id="admTableTDtext">
				<input style="float: left;" type="checkbox" name="NOODP"  value="true"
					<c:if test="${NOODP == 'true'}">checked="checked"</c:if>
                 />
			</td> 
		</tr>
		<tr>
            <td class="admTableTD" id="admTableTDtext"><fmt:message key="SS.IS_PARTICIPATE_OF_RANK_BOOSTER"/></td> 
            <td class="admTableTDLast" id="admTableTDtext">
				<input style="float: left;" type="checkbox" name="isParticipleOfRankBooster"  value="true"
					<c:if test="${isParticipleOfRankBooster == 'true'}">checked="checked"</c:if>
                 />
			</td> 
		</tr>
		<tr>
            <td class="admTableTD" id="admTableTDtext">Use canonical meta tags</td> 
            <td class="admTableTDLast" id="admTableTDtext">
                <input style="float: left;" type="checkbox" name="canonicalMetaTags"  value="true"
                    <c:if test="${canonicalMetaTags == 'true'}">checked="checked"</c:if>
                 />
            </td> 
        </tr>
		<tr>
			<td class="admTableTD" id="admTableTDtext"><fmt:message key="SS.IS_MULTI_DOMAINS_LINKS"/></td> 
			<td class="admTableTDLast" id="admTableTDtext">
				<input style="float: left;" type="checkbox" name="IS_MULTI_DOMAINS_LINKS"  value="true"
					<c:if test="${IS_MULTI_DOMAINS_LINKS == 'true'}">checked="checked"</c:if>
                />
            </td> 
		</tr>
		
		<tr>
			<td class="admTableTD" id="admTableTDtext"><fmt:message key="SS.BROWSER_LANG_DETERMINATION"/></td> 
			<td class="admTableTDLast" id="admTableTDtext">
				<input style="float: left;" type="radio" name="LANG_DETERMINATION_STRATEGY"  value="browser"
					<c:if test="${LANG_DETERMINATION_STRATEGY == 'browser'}">checked="checked"</c:if>
                />
				
            </td> 
		</tr>
		<tr>
			<td class="admTableTD" id="admTableTDtext"><fmt:message key="SS.IP4_LANG_DETERMINATION"/></td> 
			<td class="admTableTDLast" id="admTableTDtext">
				<div style="float: left;">
					<input style="float: left;" type="radio" name="LANG_DETERMINATION_STRATEGY"  value="ip4"
						<c:if test="${LANG_DETERMINATION_STRATEGY == 'ip4'}">checked="checked"</c:if>
	                />&#160;&#160;
				    </div>
	                <div style="float: left;">
                <b><fmt:message key="CM_ABOUT_COUNTRY_LANG_MESSAGE"/></b>
                </div>
            </td> 
		</tr>
		<tr>
			<td class="admTableTD" id="admTableTDtext"><fmt:message key="SS.NO_LANG_DETERMINATION"/></td> 
			<td class="admTableTDLast" id="admTableTDtext">
				<input style="float: left;" type="radio" name="LANG_DETERMINATION_STRATEGY"  value="defaultLang"
					<c:if test="${LANG_DETERMINATION_STRATEGY == 'defaultLang'}">checked="checked"</c:if>
                />
				
            </td> 
		</tr>

		<tr>
			<td class="admTableTD" id="admTableTDtext"><fmt:message key="SS.DEFAULT_LANGUAGE"/></td> 
			<td class="admTableTDLast" id="admTableTDtext">
				<select name="DEFAULT_LANGUAGE">
					<%--<c:choose>--%>
						<%--<c:when test="${fn:length(languages) gt 1}">--%>
							<%--<c:forEach var="lang" items="${languages}" varStatus="status">--%>
									<%--<option value="${lang.code}"--%>
										<%--<c:if test="${lang.code == DEFAULT_LANGUAGE}">--%>
											<%--selected="selected"--%>
										<%--</c:if>--%>
									<%-->${lang.code}</option>						--%>
							<%--</c:forEach>--%>
						<%--</c:when>--%>
						<%--<c:otherwise>--%>
							<%--<input type="hidden" name="DEFAULT_LANGUAGE"  value="${languages[0].code}"/>--%>
						<%--</c:otherwise>--%>
					<%--</c:choose>--%>
					<c:forEach var="lang" items="${languages}" varStatus="status">
						<option value="${lang.code}"
						<c:if test="${lang.code == DEFAULT_LANGUAGE}">
							selected="selected"
						</c:if>
						>${lang.code}</option>
					</c:forEach>
				</select>
            </td> 
		</tr>
		
		
		
		
		
        <!--
        <tr>
			<td class="admMainTD"><fmt:message key="SS.TITLE_IN_RANK_BOOSTER"/></td> 
			<td class="admLightTD">
				<input class="admTextArea admWidth200"  type="text" name="titleInRankBooster" data_type="text" value="${titleInRankBooster}"/>
            </td> 
		</tr>
		-->
		<tr>
            <td class="admTableFooter" >&nbsp;</td>
        </tr>
	</table>
	<table align="center"  border="0" cellpadding="0" cellspacing="0" class="admTable">    
		<tr>
			<td class="admTDtitles" style="height:auto;"><fmt:message key="SS.LANGUAGE"/></td>
			<td  class="admTDtitles" style="height:auto;" >Main domain</td>
			<td  class="admTDtitles" style="height:auto;" ><fmt:message key="SS.FRONT_PAGE"/></td>
			<td  class="admTDtitles" style="height:auto;" ></td>
		</tr>	

		<c:forEach var="entry" items="${langFrontPages}" varStatus="status">
			<tr>
				<td class="admTableTD" id="admTableTDtext">
					${entry.key.language}
				</td>
				<td class="admTableTD" id="admTableTDtext">
					<div style="float: right;">
					   <select name="site_url_id_for_lang_code_${entry.key.code}">
					       <optgroup label="-Language specific-">
					       <c:forEach var="siteUrl" items="${siteUrls}">
					           <c:if test="${siteUrl.langId ==  entry.key.id}">
					               <c:set var="used" value="false" scope="request" />
					               <c:forEach var="langIdIter" items="${siteUrl.langIds}">
					                   <c:if test="${langIdIter == entry.key.id}">
					                       <c:set var="used" value="true" scope="request" />
					                   </c:if>
					               </c:forEach>
					               <option
					               <c:if test="${used == true}">selected="selected"</c:if> 
					               value="${siteUrl.id}">${siteUrl.url}</option>
					           </c:if>
					       </c:forEach>
					       </optgroup>
					       <optgroup label="-Common-">
                           <c:forEach var="siteUrl" items="${siteUrls}">
                               <c:if test="${empty siteUrl.langId}">
                               <c:set var="used" value="false" scope="request" />
                                   <c:forEach var="langIdIter" items="${siteUrl.langIds}">
                                       <c:if test="${langIdIter == entry.key.id}">
                                           <c:set var="used" value="true" scope="request" />
                                       </c:if>
                                   </c:forEach>
                                   <option
                                   <c:if test="${used == true}">selected="selected"</c:if> 
                                   value="${siteUrl.id}">${siteUrl.url}</option>
                               </c:if>
                           </c:forEach>
                           </optgroup>
					   </select>
					</div>
				</td>
				<td class="admTableTDLast">
					<select class="admTextArea admWidth200" name="lang_${entry.key.code}" >
						<c:forEach var="page" items="${avialeblePages}" varStatus="status">
							<c:if test="${entry.key.id == page.langId}">
								<option value="${page.filename}" <c:if test="${entry.value == page.filename }">selected</c:if> >${page.filename}</option>
							</c:if>
						</c:forEach>
					</select>
				</td>
				<td class="admTableTDLast" id="admTableTDtext" style="width: 10px;">
					<img src="/images/properties.png" width="37" height="36" style="float:right;" class="admHand" alt="Edit frontpage prooperties" onclick="editPagePropertiesByFilename('${entry.value}')"/>
				</td>
			</tr>	
    	</c:forEach>
		<tr>
			<td class="admTableFooter" >&nbsp;</td>
        </tr>
	</table>
</form>	
</negeso:admin>

<table cellpadding="0" cellspacing="0"  width="764px"  align="center"  border="0" class="Buttons">
	<tr>
		<td>
			<div class="admBtnGreenb">
				<div class="imgL"></div>
                <div><input type="submit" onClick="saveform();" name="saveform" value='<fmt:message key="SAVE"/>'/></div>
				<div class="imgR"></div>
            </div>
            <div class="admBtnGreenb admBtnBlueb">
				<div class="imgL"></div>
                <div><input type="reset" onClick="resetform();"  name="resetform" value='<fmt:message key="RESET"/>'></div>
				<div class="imgR"></div>
            </div>
        </td>
	</tr>
</table>

</body>
</html>