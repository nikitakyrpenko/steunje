<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>    
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%response.setHeader("Expires", "0");%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="negeso" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<title><fmt:message key="ADD_UPDATE_CONST"/></title>
	<link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>

	<script type="text/javascript" src="/script/jquery.min.js">/**/</script>	
	<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
	<script language="JavaScript1.2" src="/site/core/script/validation.js" type="text/javascript"></script> 
 <script type="text/javascript" src="/script/cufon-yui.js"></script>
 <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
	<script language="JavaScript1.2" src="/script/common_functions.js" type="text/javascript"></script>
	<script type="text/javascript">
	function try_delete(id){
		if(confirm('<fmt:message key="DELETE_CONFIRMATION"/>'))
			document.location='/admin/nl_categorylist?action=deleteSubscriptionCategory&categoryId='+id;
	}
	</script>
</head>
<body>
	<negeso:admin>
	<table align="center"  border="0" cellpadding="0" cellspacing="0" class="admTable">
        <tr>
     <td  style="width:auto; height:auto;"  colspan="2">  
           <jsp:include page="/WEB-INF/jsp/common/navig_bar.jsp" />
           	</td></tr>  
			<tr>
				<td  align="center" class="admNavPanelFont"  style="height:auto;" colspan="2" >
			<c:if test="${param.act=='updateConst'}">
				<fmt:message key="EDIT_CONST"/>
			</c:if>	 
			<c:if test="${param.act=='addConst'}">
				<fmt:message key="ADD_CONST"/> 
			</c:if> 
		</td>
		
	</tr>
	
	<form method="POST" enctype="multipart/form-data" onSubmit="if (full_validate(this)) this.form.submit(); else return false;" name="edit_const" >
	
		<input type="hidden" name="act" id="act" value="saveConst" />
		<c:if test="${const.id != null}">
			<input type="hidden" name="id" value="${const.id}" />		
		</c:if>
		<c:if test="${const.moduleId != null}">
			<input type="hidden" name="moduleId" value="${const.moduleId}" />
		</c:if>
		
	 	
			<tr>
				<th width="20%" class="admTableTDLast" width="200"><b><fmt:message key="CONST_KEY"/>*</b></th>
				<td colspan="2" class="admTableTDLast">
					<input type="text" name="key" id="key" class="admTextArea"
					 style="width: 500px;" value="${const.key}" required="true" MAXLENGTH="255" />
				</td>
			</tr>
 			<c:forEach var="translation" items="${const.translations}">
	  			<tr>
					<th class="admTableTDLast" width="200">
						<b>${translation.language.name}
							<c:if test="${translation.language.code == 'en'}">*</c:if>
						</b>
					</th>
					<td  class="admTableTDLast">
						<textarea rows="3" 
						          class="admTextArea"
						          name="${translation.language.code}" 
						          id="${translation.language.code}"
						          style="width: 500px"
						          <c:if test="${translation.language.code == 'en'}">required="true"</c:if>
						          >${translation.translation}</textarea>			
					</td>
				</tr>

			</c:forEach>
			
	</form>
    <tr>
                    <td class="admTableFooter" colspan="2" >&nbsp;</td>
                </tr>  
    </table>
	</negeso:admin>
     <table  cellpadding="0" cellspacing="0"  width="764px"  align="center"  border="0" class="Buttons">
            <tr >
                <td>
                    <div class="admBtnGreenb">
                                <div class="imgL"></div>
                                <div>
                               <input class="admNavbarInp" type="button" style="width:140px;"
						  onclick="if (full_validate(edit_const)) edit_const.submit()" value="<fmt:message key="SAVE_AND_CLOSE"/>"
			    	  />
                                </div>
                                <div class="imgR"></div>
                            </div>
                    
                </td>
            </tr>
        </table>
    
</body>
</html>