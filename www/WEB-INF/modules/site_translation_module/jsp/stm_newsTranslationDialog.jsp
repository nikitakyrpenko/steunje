<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>    
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%response.setHeader("Expires", "0");%>
<div id="translateDialog" class="translateDialog" title="Translating...">
    <table cellpadding="0" cellspacing="0" border="0">
       <c:forEach items="${currentLangs}" var="lang">
            <c:if test="${lang.id != fromLang.id}">
		        <tr>
		          <td>
		             <div style="margin-top: 10px;">
		                ${lang.code}: 
		             </div>
		          </td>
		          <td>
	                <input type="checkbox" name="translateToLang" value="${lang.id}" style="margin-top: 10px;"/>
		          </td>
		          <td>
	                &#160;&#160;<select id="listId_${lang.id}" style="width: 200px; margin-top: 10px;">
	                     <c:forEach items="${newsLists}" var="list">
	                     <c:if test="${list.langId == lang.id}">
	                        <option value="${list.id}">${list.name}</option>
	                     </c:if>
	                     </c:forEach>
	                </select>
		          </td>
		        </tr>
            </c:if>
       </c:forEach>
    </table>
</div>