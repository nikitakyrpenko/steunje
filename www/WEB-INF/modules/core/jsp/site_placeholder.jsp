<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>    
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%response.setHeader("Expires", "0");%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="negeso" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
   <head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title><fmt:message key="CM_PLACEHOLDERS"/></title>
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
        
    var dialog = '<div id="editPlaceHolderDialog" class="editPlaceHolderDialog"> ' +
		    '&nbsp;&nbsp;<label ><fmt:message key="PLACE_HOLDER"/>:</label><br/>' +
		        '<b>%</b><input type="text" id="placeHolder" required="true" class="admWidth200"/><b>%</b>' + 
		    '<br/><br/>' +
		    '&nbsp;&nbsp;<label ><fmt:message key="TEXT"/>:</label><br/>' +
            '&nbsp;&nbsp;<input type="text" id="placeHolderValue" required="true" class="admWidth200"/>' +
            '<br/><br/>' +
		'</div>';
		
		function addOrEditPlaceHolder(id_param) {
		     $("[class^=editPlaceHolderDialog]").each(function() {
		         $(this).remove();
		      });
		    $(document.body).append(dialog);
		    $("#editPlaceHolderDialog").dialog({ buttons: {
		        '<fmt:message key="Cancel"/>': function() { $(this).remove();  },
		        '<fmt:message key="SAVE"/>': function() {
		            if (validate(document.getElementById('editPlaceHolderDialog'))) {
		                $.ajax({
		                    type: "POST",
		                    url: "/admin/site_placeholder.html",
		                    data: ({action : 'edit', id : id_param, key : '%' + $('#placeHolder').val() + '%', value : $('#placeHolderValue').val()}),
		                    dataType: "html",
		                    success: function(html, stat) {  
		                    	  window.location.reload();
		                    },
		                    error: function(html, stat) {
		                        alert(stat, html);
		                        window.location.reload();
		                    }
		                });
		            }
		        }} });
		    $("#editPlaceHolderDialog").bind( "dialogclose", function(event, ui) {
		        $(this).remove();
		      })
		    $("#editPlaceHolderDialog").dialog('open');
		    if (id_param > 0) {
		        $("#placeHolder").val($('#placeHolder_'+id_param).html().replace(/%/g, ''));
		        $("#placeHolderValue").val($('#placeHolderValue_'+id_param).html());
		    }
		}
		
		function deletePlaceHolder(id){
		    if (confirm("Are you sure?")){
		        $.ajax({
		            type: "POST",
		            url: "/admin/site_placeholder.html?action=delete&id=" + id,
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
		}
        
    </script>
    <negeso:admin>
    <table align="center"  border="0" cellpadding="0" cellspacing="0" class="admTable">
        <tr><td  style="width:auto; height:auto;" colspan="4">        <jsp:include page="/WEB-INF/jsp/common/navig_bar.jsp" />  </td></tr>
                   <tr>                
                <td align="center" class="admNavPanelFont"  style="height:auto;" colspan="4">
                    <fmt:message key="CM_PLACEHOLDERS" />
                </td>
                
            </tr>
       
        
        <input type="hidden" id="pageId" value="${pageId}" />
      
            <tr> 
                
                <td class="admTDtitles" style="height:auto" width="30%"><fmt:message key="PLACE_HOLDER"/></td>
                <td class="admTDtitles" style="height:auto" width="30%"><fmt:message key="TEXT"/></td> 
                <td class="admTDtitles" style="height:auto" width="10%" colspan="2"><fmt:message key="ACTION"/></td> 
            </tr>
        
            <c:forEach var="placeHolder" items="${placeHolders}">
                <tr>
                    <td  class="admTableTD" id="placeHolder_${placeHolder.id}" style="padding: 0 0 0 20px;">${placeHolder.key}</td>
                    <td class="admTableTD" id="placeHolderValue_${placeHolder.id}">${placeHolder.value}</td>
                    <td  class="admTableTDLast">                    
                    <img src='/images/edit.png'
                           class='admHand' title='Edit'
                           onclick="addOrEditPlaceHolder(${placeHolder.id})"/></td>
                    <td  class="admTableTDLast" id="admTableTDtext">
                    <img src='/images/delete.png'
                        class='admHand' title='Delete'
                        onclick="deletePlaceHolder(${placeHolder.id})"/></td>
                </tr>
            </c:forEach>
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
                        <input type="button"  value='<fmt:message key="ADD"/>' onClick="addOrEditPlaceHolder(-1)"/>
                    </div>
                    <div class="imgR"></div>
                </div>
            </td>
        </tr>
    </table>
</body>
</html>