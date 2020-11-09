<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %><c:forEach var="page" items="${pages}">
	<li>
	    <a href="${url}${page.filename}">${page.title}</a>
	</li></c:forEach>
<%response.setHeader("Expires", "0");%>