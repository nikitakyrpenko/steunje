<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>    
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>  
<%response.setHeader("Expires", "0");%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="negeso" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
   <head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title><fmt:message key="JOB_REGIONS"/></title>
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
        
    var dialog = '<div id="editDialog" class="editDialog"> ' +
            '&nbsp;&nbsp;<label ><fmt:message key="TITLE"/>:</label><br/>' +
                '<input type="text" id="title" required="true" class="admWidth200"/>' + 
            '<br/><br/>' +
        '</div>';
        
        function addOrEdit(id_param) {
             $("[class^=editDialog]").each(function() {
                 $(this).remove();
              });
            $(document.body).append(dialog);
            $("#editDialog").dialog({ buttons: {
                '<fmt:message key="Cancel"/>': function() { $(this).remove();  },
                '<fmt:message key="SAVE"/>': function() {
                    if (validate(document.getElementById('editDialog'))) {
                        $.ajax({
                            type: "POST",
                            url: "/admin/job_regions.html",
                            data: ({act : 'edit', id : id_param, title : $('#title').val()}),
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
            $("#editDialog").bind( "dialogclose", function(event, ui) {
                $(this).remove();
              })
            $("#editDialog").dialog('open');
            if (id_param > 0) {
                $("#title").val($('#title_'+id_param).html());
            }
        }
        
        function _delete(id){
            if (confirm("Are you sure?")){
                $.ajax({
                    type: "POST",
                    url: "/admin/job_regions.html?act=delete&id=" + id,
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
                    <fmt:message key="JOB_REGIONS" />
                </td>                
            </tr>
            
            <tr> 
                
                <td class="admTDtitles" style="height:auto" width="30%"><fmt:message key="TITLE"/></td>
                <td class="admTDtitles" style="height:auto" width="30%"><fmt:message key="VACANCIES"/></td> 
                <td class="admTDtitles" style="height:auto" width="10%" colspan="2"><fmt:message key="ACTION"/></td> 
            </tr>
        
            <c:forEach var="region" items="${regions}">
                <tr>
                    <td  class="admTableTD" id="title_${region.id}" style="padding: 0 0 0 20px;">${region.title}</td>
                    <td  class="admTableTD" style="padding: 0 0 0 20px;">${fn:length(region.vacancies)}</td>
                    <td  class="admTableTDLast">                    
                    <img src='/images/edit.png'
                           class='admHand' title='Edit'
                           onclick="addOrEdit(${region.id})"/></td>
                    <td  class="admTableTDLast" id="admTableTDtext">
	                    <c:if test="${fn:length(region.vacancies) == 0}">
		                    <img src='/images/delete.png'
		                        class='admHand' title='Delete'
		                        onclick="_delete(${region.id})"/>
	                    </c:if>
                    </td>
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
                        <input type="button"  value='<fmt:message key="ADD"/>' onClick="addOrEdit(-1)"/>
                    </div>
                    <div class="imgR"></div>
                </div>
            </td>
        </tr>
    </table>
</body>
</html>