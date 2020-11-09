<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>    
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="negeso" %>
<%response.setHeader("Expires", "0");%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
   <head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title><fmt:message key="CM_HOST_NAMES"/></title>
    <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
    <link rel="stylesheet" type="text/css" href="/css/customized-jquery-ui.css"/>    
    
    <script type="text/javascript" src="/script/jquery.min.js">/**/</script>
    <script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
    <script type="text/javascript" src="/script/cufon-yui.js"></script>
    <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
				<script language="JavaScript1.2" src="/script/common_functions.js" type="text/javascript"></script>
    <script type="text/javascript" src="/site/core/script/validation.js"></script>    
</head>
<body>
    <script type="text/javascript">
    var domains = {<c:forEach varStatus="st" var="domain" items="${urls}">					 
			  ${domain.id} : 
			    {
			      bing : '<c:out value="${domain.bing}"/>',
			      analytic :  '<c:out value="${domain.analytic}"/>',
			      verification :  '<c:out value="${domain.verification}"/>'	,
                  tagManager :  '<c:out value="${domain.tagManager}"/>'
                }<c:if test="${st.index < fn:length(urls) - 1}">,</c:if></c:forEach>
		};
	
        var dialog = '<div id="editUrlDialog" class="editUrlDialog" title="Url settings:"> ' +
            '<label ><fmt:message key="TITLE"/>:</label><br/>' +
                '<input type="text" id="titleField" required="true"class="admWidth200"/>' +
            '<br/><br/>' +
            '<label><fmt:message key="LANGUAGE"/>:</label><br/>' +
            '<div id="langSelect"></div>' +
            '<br/>' +
            '<input type="checkbox" id="showLangSelector"/> <fmt:message key="CM_SHOW_LANG_SELECTOR"/><br/><br/>' +
            '<input type="checkbox" id="isSingleLang"/> <fmt:message key="CM_IS_SINGLE_LANGUAGE"/><br/><br/>' +
        '</div>';

        var googleCodesDialog = '<div id="editUrlDialog" class="editUrlDialog"> ' +       
	        '<label ><fmt:message key="GOOGLE.BING"/>:</label><br/>' +
	            '<input type="text" class="admWidth315" id="bing"/>' +
	        '<br/><br/>' +
	        '<label><fmt:message key="GOOGLE.ANALYTIC"/></label>' +
	        	'<input type="text" class="admWidth315" id="analytic"/>' +
	        '<br/><br/>' +
	        '<label><fmt:message key="GOOGLE.VERIFICATION"/></label><br/>' +
	    		'<input type="text" class="admWidth315" id="verification"/>' +
            '<br/><br/>' +
            '<label><fmt:message key="GOOGLE.TAG.MANAGER"/></label><br/>' +
            '<input type="text" class="admWidth315" id="tagManager"/>' +
	    '</div>';
    
        function addOrEditUrl(id) {
             $("[class^=editUrlDialog]").each(function() {
                 $(this).remove();
              });
             var command = id > 0 ? "update" : "add";
            $(document.body).append(dialog);
            $("#editUrlDialog").dialog({ buttons: {
                '<fmt:message key="Cancel"/>': function() { $(this).remove();  },
                '<fmt:message key="SAVE"/>': function() {
                    if (validate(document.getElementById('editUrlDialog'))) {
                        $.ajax({
                            type: "POST",
                            url: "/aliasservice",
                            data: {command: command, id: id, url: $("#titleField").val(), langId: $('#langSelect option:selected').val(), singleLanguage: $('#isSingleLang').is(':checked'), showLangSelector: $('#showLangSelector').is(':checked')},
                            dataType: "html",
                            success: function(html, stat) {  
                            window.location.reload();
                           //    $("#contentTable").html($('#contentTable',html).html());
                           //    $("#editUrlDialog").remove();
                            },
                            error: function(html, stat) {
                                alert("Internal server error occured");
                                window.location.reload();
                            }
                        });
                    }
                }} });
            $("#editUrlDialog").bind( "dialogclose", function(event, ui) {
                $(this).remove();
              })
            $("#editUrlDialog").dialog('open');
            var langTemplate = $('#langTemplate').clone();
            langTemplate.css('display', '');
            $("#langSelect").html(langTemplate);
            if (id > 0) {
                $("#titleField").val($('#title_'+id).html());
                if ($('#title_'+id).attr('ise')) {
                	 $("#titleField").attr('disabled', 'disabled');
                }
                $('#langSelect option[value=' + $('#langId_' + id).attr('langId') + ']').attr('selected', 'selected');
                if ($('#showLangSelector_' + id).is(':checked')) {
                    $('#showLangSelector').attr('checked', 'checked');
                }
                if ($('#isSingleLang_' + id).is(':checked')) {
                    $('#isSingleLang').attr('checked', 'checked');
                }
            }
        }

        function editGoogleCodes(id) {
        	$("[class^=editUrlDialog]").each(function() {
                $(this).remove();
             });
           $(document.body).append(googleCodesDialog);
           $("#editUrlDialog").dialog({ width: 350, buttons: {
               '<fmt:message key="Cancel"/>': function() { $(this).remove();  },
               '<fmt:message key="SAVE"/>': function() {
                   if (validate(document.getElementById('editUrlDialog'))) {
                       $.ajax({
                           type: "POST",
                           url: "/admin/site_settings",
                           data: {action: 'saveDomainGoogleCodes', id: id, bing: $("#bing").val(), analytic: $('#analytic').val(), verification: $('#verification').val(), tagManager: $("#tagManager").val() },
                           dataType: "html",
                           success: function(html, stat) {  
                           		window.location.reload();
                           },
                           error: function(html, stat) {
                               alert("Internal server error occured");
                               window.location.reload();
                           }
                       });
                   }
               }} });
           $("#editUrlDialog").bind( "dialogclose", function(event, ui) {
               $(this).remove();
             })
           $('#editUrlDialog').dialog('option', 'title', '<fmt:message key="CM_GOOGLE_CODES"/> (' + $('#title_'+id).html() + ')');
           $("#editUrlDialog").dialog('open');
           $("#bing").val(domains[id].bing);
           $("#analytic").val(domains[id].analytic);
           $("#verification").val(domains[id].verification);
           $("#tagManager").val(domains[id].tagManager);
        }

        function deleteUrl(id){
            if (confirm("Are you sure?")){
                $.ajax({
                    type: "POST",
                    url: "/aliasservice?command=delete&id=" + id + "&url=" + $('#title_'+id).html(),
                    dataType: "html",
                    success: function(html, stat) {
                	window.location.reload()
                       //$("#contentTable").html($(html).find('table:#contentTable').html());
                       ///$("#editUrlDialog").remove();
                    },
                    error: function(html, stat) {
                    	alert("Internal server error occured");
                        window.location.reload();
                    }
                });
            }
        }
    </script>        
       
<negeso:admin> 
	<table align="center"  border="0" cellpadding="0" cellspacing="0" class="admTable">
        <tr>
			<td style="width:auto; height:auto;" colspan="8"><jsp:include page="/WEB-INF/jsp/common/navig_bar.jsp"/></td>
		</tr>
        <tr>                
			<td align="center" class="admNavPanelFont"  style="height:auto;" colspan="8"><fmt:message key="CM_HOST_NAMES" /></td>
		</tr>
		<tr> 
			<td class="admTDtitles" width="30%" style="height:auto"><fmt:message key="TITLE"/></td>
            <td class="admTDtitles" width="10%" style="height:auto">Status</td>
            <td class="admTDtitles" width="20%" style="height:auto"><fmt:message key="LANGUAGE"/></td>
            <td class="admTDtitles" width="10%" style="height:auto"><fmt:message key="CM_SHOW_LANG_SELECTOR"/></td>
            <td class="admTDtitles" width="10%" style="height:auto"><fmt:message key="CM_IS_SINGLE_LANGUAGE"/></td>
            <td class="admTDtitles" width="10%" style="height:auto"><fmt:message key="CM_GOOGLE_CODES"/></td>
            <td class="admTDtitles" width="10%" colspan="2" style="height:auto"><fmt:message key="ACTION"/></td> 
        </tr>
		<c:forEach var="site_url" items="${urls}">
			<tr>
				<td class="admTableTD"  id="title_${site_url.id}" style="padding: 0 0 0 20px;" ise="${site_url.url == current || site_url.main}">${site_url.url}</td>
		        <td  class="admTableTD" id="admTableTDtext">
					<c:if test="${site_url.main}">main</c:if>&#160;<c:if test="${site_url.url == current}">current</c:if>
		        </td>
		        <c:set var="lan" />
				    <c:forEach items="${languages}" var="lang">
				        <c:if test="${lang.id == site_url.langId}"><c:set var="lan" value="${lang}"/></c:if>
				    </c:forEach>
				<c:choose>
				    <c:when test="${empty lan}">
				        <td class="admTableTD" id="langId_${site_url.id}" langId=""></td>
				    </c:when>
				    <c:otherwise>
						<td class="admTableTD" id="langId_${site_url.id}" langId="${lan.id}">
						    ${lan.language}
						</td>
				    </c:otherwise>
				</c:choose>
				<td class="admTableTD">
				    <input type="checkbox" id="showLangSelector_${site_url.id}" disabled="disabled"
				        <c:if test="${site_url.showLangSelector}">checked="checked"</c:if> 
				    />
				    
				</td>
				<td class="admTableTD">
                    <input type="checkbox" id="isSingleLang_${site_url.id}" disabled="disabled"
                        <c:if test="${site_url.singleLanguage}">checked="checked"</c:if> 
                    />
                    
                </td>
                <td class="admTableTD"  align="center">
                  <img src='/images/edit.png'class='admHand' title='edit'
                        onclick="editGoogleCodes(${site_url.id})"/>
                </td>
                <td class="admTableTDLast" id="admTableTDtext" align="right">
                     <img src='/images/edit.png'class='admHand' title='edit'
                        onclick="addOrEditUrl(${site_url.id})"/>
                 </td>
		        <c:choose>
					<c:when test="${site_url.url == current || site_url.main}">
						<td  class="admTableTDLast" id="admTableTDtext"></td>
                    </c:when>
		            <c:otherwise>						
                        <td class="admTableTDLast" id="admTableTDtext" align="right">
							<img src='/images/delete.png' class='admHand' title='Delete'
								onclick="deleteUrl(${site_url.id})"/></td>
		           </c:otherwise>
				</c:choose>
			</tr>
		</c:forEach> 
        <tr>
			<td class="admTableFooter" colspan="6" >&nbsp;</td>
        </tr>           
	</table>
	<select id="langTemplate" style="display: none;">
        <option value=""></option>
        <c:forEach items="${languages}" var="lang">
            <option value="${lang.id}">${lang.language}</option>
        </c:forEach>
    </select>
</negeso:admin>        
        
<table cellpadding="0" cellspacing="0"  width="764px"  align="center"  border="0" class="Buttons">
	<tr>
       <td>
			<div class="admBtnGreenb">
				<div class="imgL"></div>
                <div><input type="button" value='<fmt:message key="ADD"/>' onClick="addOrEditUrl(-1)"/></div>
                <div class="imgR"></div>
            </div>
            <div class="admBtnGreenb admBtnBlueb">
				<div class="imgL"></div>
                <div>
                	 <div><a class="admBtnText" href="<fmt:message key="CM_BUY_DOMAIN_HREF"/>" target="_blank" onfocus="blur()"><fmt:message key="CM_BUY_DOMAIN_NAME"/></a></div>
				</div>
				<div class="imgR"></div>
			</div>
		</td>
	</tr>
</table>

</body>
</html>