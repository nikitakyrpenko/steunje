<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<!-- Indexing table -->
<table class="admNavPanel admTableMargin"  border="0" cellpadding="0" cellspacing="0" align="center" >
  <tr valign="middle" height="25">
  	  <td width="60%"></td>
      <c:if test="${navigator.previousChunkExists}">
      	<td ><a class="admNone" style="color: #0e4a9e;" href='<%= request.getParameter("link") %>?goChunk=previous&chunk=<c:out value="${navigator.currentChunk}"/>'>Previous</a>&nbsp;&nbsp;</td>
      </c:if>
           
<c:forEach var="chunk" items="${navigator.chunks}" varStatus="status"> 
 
    
    <c:choose>
      <c:when test="${navigator.currentChunk==chunk-1}">
       <td ><a class="admNone admBold" style="color: #000000; background-color: #E2F2E0; " href='<%= request.getParameter("link") %>?goChunk=<c:out value="${chunk-1}"/>'>'<c:out value="${chunk}"/>'</a>&nbsp;&nbsp;</td>
      </c:when>
      <c:otherwise>
         <td ><a class="admNone" style="color: #0e4a9e; " href='<%= request.getParameter("link") %>?goChunk=<c:out value="${chunk-1}"/>'><c:out value="${chunk}"/></a>&nbsp;&nbsp;</td>
      </c:otherwise>  
    </c:choose>
 
 
</c:forEach>
      <c:if test="${navigator.nextChunkExists}">
       <td ><a class="admNone" style="color: #0e4a9e;" href='<%= request.getParameter("link") %>?goChunk=next&chunk=<c:out value="${navigator.currentChunk}"/>'>Next</a>&nbsp;&nbsp;
      </c:if>
 </tr>
</table>

