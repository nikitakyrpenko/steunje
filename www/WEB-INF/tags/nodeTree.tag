<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@tag description="display the whole nodeTree" pageEncoding="UTF-8"%>
<%@attribute name="node" type="com.negeso.framework.site_map.PageDescriptor" required="true" %>
<%@taglib prefix="template" tagdir="/WEB-INF/tags" %>
<c:forEach var="descr" items="${node.content}">
<table><tr><td class="admChooserMenu">
    <div style="margin-left:15px; font: 11px Verdana, sans-serif;">
        <input type="checkbox" id="ch_${descr.pageId}" onClick="select_item(this)" class="checker"
        pageId="${descr.pageId}" entityType="${descr.type}" entityId="${descr.id}"
        <c:choose>
            <c:when test="${empty descr.friendlyHref}">
                link=${descr.href}
            </c:when>
            <c:otherwise>
                link=${descr.friendlyHref}
            </c:otherwise>
        </c:choose>
        <c:if test="${pageAlias.pageId == descr.pageId && pageAlias.entityId == descr.id && pageAlias.entityType == descr.type}">checked="checked"</c:if>
        />&nbsp;
        <c:choose>
            <c:when test="${descr.leaf}">
                <img  src="/images/doc.gif"/>&nbsp;
            </c:when>
            <c:otherwise>
                <img class="admPageChooserPointer" src="/images/folderclosed.gif" />&nbsp;
            </c:otherwise>
        </c:choose>
        <c:choose>
            <c:when test="${empty descr.friendlyHref}">
                <span>${descr.title} (${descr.href})</span>
            </c:when>
            <c:otherwise>
                <span>22${descr.title} (${descr.friendlyHref})</span>
            </c:otherwise>
        </c:choose>
     <template:nodeTree node="${descr}"/>
    </div>
    </td></tr>
    </table>

</c:forEach>

