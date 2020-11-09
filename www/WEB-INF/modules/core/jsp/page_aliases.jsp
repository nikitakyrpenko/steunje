<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>    
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%response.setHeader("Expires", "0");%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="negeso" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
   <head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title><fmt:message key="CM_URL_ALIASES"/></title>
    <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
    <link rel="stylesheet" type="text/css" href="/css/jquery-ui.custom.css"/>	
    
    <script type="text/javascript" src="/script/jquery.min.js">/**/</script>
    <script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
    <script type="text/javascript" src="/script/cufon-yui.js"></script>
    <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>

				<script language="JavaScript1.2" src="/script/common_functions.js" type="text/javascript"></script>
    <script type="text/javascript" src="/site/core/script/validation.js"></script>
</head>
<body>
    <script type="text/javascript">

        var CM_ERROR_SUCH_ALIAS_OR_PAGE_ALREADY_EXIST = '<fmt:message key="CM_ERROR_SUCH_ALIAS_OR_PAGE_ALREADY_EXIST" />';
        var CM_ERROR_INCORRECT_ALIAS_NAME = '<fmt:message key="CM_ERROR_INCORRECT_ALIAS_NAME" />';
        var CM_ERROR_CHOOSE_PAGE = '<fmt:message key="CM_ERROR_CHOOSE_PAGE" />';
        
	    function addOrEditAlias(id) {
	        $("[class^=editAliasDialog]").each(function() {
	            $(this).remove();
	         });
	        var dialog = "";
	        var langId = id > 0  ? $("#title_" + id).attr("langId") : $("#langSelect option:selected").val();
	        $.ajax({
                type: "POST",
                url: "/admin/site_settings?action=getEditAliasDialog&id=" + id + "&lang_id=" + langId + "&page_id=" + $("#pageId").val(),
                dataType: "html",
                async: false,
                success: function(html, stat) {  
	        	  dialog = html;				  
                },
                error: function(html, stat) { }
            });
	       $(document.body).append(dialog);		  
		   var heightDialog = $("#pageId").val() > 0 ? 160: 400;
		   var widthDialog =  $("#pageId").val() > 0 ? 270: 600;
	       $("#editAliasDialog").dialog({
	    	   height: heightDialog,
	    	   width: widthDialog,
	    	   resizable: false,
			   open: function(){ var dialog = this; $(dialog).css({'height':heightDialog+'px', 'width':widthDialog-26+'px'});},
		       buttons: {
	          
	           '<fmt:message key="SAVE"/>': function() {
	        	   if ( !checkRegexp($("#alias"), /^([0-9a-zA-Z_-])+$/) ) {
		        	   $("#error_message").html(CM_ERROR_INCORRECT_ALIAS_NAME);
		        	   return;
		           }
		           var params = "";
		           var canBeSaved = false;
	        	   $("INPUT[class^=checker][type='checkbox']").each(function() {
	        		   if ($(this).is(':checked')) {
	        			   params = "&pageId=" + $(this).attr("pageId") 
		        		        + "&entityType=" + $(this).attr("entityType")
		        		        + "&entityId=" + $(this).attr("entityId")
		        		        + "&link=" + $(this).attr("link")
		        		        + "&langId=" + langId;
	        			   canBeSaved= true;
		        		   }
		        	   });
	        	   if ($("#pageId").val() > 0) {
	        		   params = "&pageId=" + $("#pageId").val()
	        		     + "&entityId=" + $("#pageId").val();
	        		   canBeSaved= true;
		           }
	        	   if (!canBeSaved) {
	        		   $("#error_message").html(CM_ERROR_CHOOSE_PAGE);
                       return;
		           }
	        	   $.ajax({
                       type: "POST",
                       url: "/admin/site_settings?action=getUrlAliases&todo=checkOnUniqueness&id=" + id +"&fileName=" + $("#alias").val() + '.html' + params,
                       dataType: "html",
                       async: false,
                       success: function(html, stat) {
	        		       canBeSaved = html;
                       },
                       error: function(html, stat) { }
                   });
	        	   if (canBeSaved == 'false') {
                       $("#error_message").html(CM_ERROR_SUCH_ALIAS_OR_PAGE_ALREADY_EXIST);
                       return;
                   } else {
	                   $.ajax({
	                       type: "POST",
	                       url: "/admin/site_settings?action=getUrlAliases&todo=save&id=" + id +"&fileName=" + $("#alias").val()+ '.html'+ params + "&inSiteMap=" + $("#isInSiteMap").is(":checked") + "&params=" + $("#paramsField").val().replace(/&/g, 'amp;'),
	                       dataType: "html",
	                       success: function(html, stat) {  
	                       window.location.reload();
	                      //    $("#contentTable").html($('#contentTable',html).html());
	                      //    $("#editAliasDialog").remove();
	                       },
	                       error: function(html, stat) { }
	                   });
		           }
		           
	           },
	           '<fmt:message key="Cancel"/>': function() { $(this).remove(); }
	           } });
	       $("#editAliasDialog").bind( "dialogclose", function(event, ui) {
	           $(this).remove();
	         })
	       $("#editAliasDialog").dialog('open');
	       if (id > 0) {
		       $("#alias").val($("#title_" + id).html().replace('.html', ''));
		       $("#paramsField").val($("#params_" + id).attr('link').replace(/.*\.html/, ''));
		       if ($("#isInSiteMap_" + id).val() == 'true') {
		    	   $("#isInSiteMap").attr("checked", "checked");
			   }
		   }
	       //$("#alias").addClass( "ui-state-error" );
	   }

	    function select_item (obj) {
		    if ($(obj).is(':checked')) {
		      	$("INPUT[class^=checker][type='checkbox']").each(function() {
		      		$(this).attr('checked', false);
			      	});
		      	$(obj).attr('checked', true);
			 }
		}

	    function deleteAlias(id){
            if (confirm("Are you sure?")){
                $.ajax({
                    type: "POST",
                    url: "/admin/site_settings?action=getUrlAliases&todo=delete&id=" + id,
                    dataType: "html",
                    success: function(html, stat) {
                    window.location.reload()
                       //$("#contentTable").html($(html).find('table:#contentTable').html());
                       ///$("#editUrlDialog").remove();
                    },
                    error: function(html, stat) { }
                });
            }
        }

	    function checkRegexp( o, regexp, n ) {
            if ( !( regexp.test( o.val() ) ) ) {
                //o.addClass( "ui-state-error" );
                return false;
            } else {
                return true;
            }
        }
        
    </script>
	<negeso:admin>
    <table align="center"  border="0" cellpadding="0" cellspacing="0" class="admTable">
        <tr><td  style="width:auto; height:auto;" colspan="4">        <jsp:include page="/WEB-INF/jsp/common/navig_bar.jsp" />	</td></tr>
                   <tr>                
                <td align="center" class="admNavPanelFont"  style="height:auto;" colspan="4">
                    <fmt:message key="CM_URL_ALIASES" />
                </td>
                
            </tr>
       
        
        <input type="hidden" id="pageId" value="${pageId}" />
      
            <tr> 
                
                <td class="admTDtitles" style="height:auto" width="30%">Alias</td>
                <td class="admTDtitles" style="height:auto" width="30%">Page</td>
                <td class="admTDtitles" style="height:auto" width="20%">Language</td> 
                <td class="admTDtitles" style="height:auto" width="10%" colspan="2"><fmt:message key="ACTION"/></td> 
            </tr>
        
            <c:forEach var="alias" items="${aliases}">
                <tr>
                    <td  class="admTableTD" id="title_${alias.id}" langId="${alias.langId}" style="padding: 0 0 0 20px;">${alias.fileName}</td>
                    <td class="admTableTD" id="params_${alias.id}" link="${alias.link}">${alias.link}</td>
                    <td  class="admTableTD">${alias.langCode}</td>
                    <td  class="admTableTDLast">
                    <input type="hidden" id="isInSiteMap_${alias.id}" value="${alias.inSiteMap}"/>
                    <img src='/images/edit.png'
                           class='admHand' title='Edit'
                           onclick="addOrEditAlias(${alias.id})"/></td>
                    <td  class="admTableTDLast" id="admTableTDtext">
                    <img src='/images/delete.png'
                        class='admHand' title='Delete'
                        onclick="deleteAlias(${alias.id})"/></td>
                </tr>
            </c:forEach>
            <tr>
                 <td colspan="5" class="admTableTDLast" id="admTableTDtext" align="left" style="padding: 0 0 0 20px;">
                    <select class="admWidth150" id="langSelect">
                        <c:forEach var="lang" items="${languages}">
                            <option value="${lang.id}" id="lang_id">
                                <c:out value="${lang.code}" />
                            </option>
                        </c:forEach>
                    </select>
                </td>
            </tr>
            <tr>
                <td class="admTableFooter" colspan="4" >&nbsp;</td>
            </tr>
        </table>
    </negeso:admin>
    <table  cellpadding="0" cellspacing="0"  width="764px"  align="center"  border="0" class="Buttons">
	    <tr >
	        <td>
	            <div class="admBtnGreenb">
	                <div class="imgL"></div>
	                <div>
	                    <input type="button"  value='<fmt:message key="ADD"/>' onClick="addOrEditAlias(-1)"/>
	                </div>
	                <div class="imgR"></div>
	            </div>
	        </td>
	    </tr>
	</table>
</body>
</html>