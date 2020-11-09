<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%response.setHeader("Expires", "0");%>
<c:forEach var="articleRevision" items="${articleRevisions}">
    <tr onclick="loadArticleText(this, 'leftText')" revisionId="${articleRevision.id}">
        <th><fmt:formatNumber value="${articleRevision.id}" pattern="000000"/>
        </th>
        <td><fmt:formatDate value="${articleRevision.date}" pattern="HH:mm:ss, EEEE, MMMM dd, yyyy"/></td>
        <td>
	        <c:choose>
		        <c:when test="${ empty articleRevision.author}">
                    admin
		        </c:when>
		        <c:otherwise>
                    ${articleRevision.author}
		        </c:otherwise>
	        </c:choose>
        </td>
    </tr>
</c:forEach>
<c:if test="${fn:length(articleRevisions) < 6}"> 
    <c:forEach begin="1" step="1" end="${6 - fn:length(articleRevisions)}">
	    <tr>
	        <th>&#160;</th>
	        <td>&#160;</td>
	        <td>&#160;</td>
	    </tr>
    </c:forEach>
</c:if>
    