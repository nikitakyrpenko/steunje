<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>    
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="negeso" %>
<%response.setHeader("Expires", "0");%>
<div id="chooseRichSnippetDialog" class="editUrlDialog" title="<fmt:message key="RS_CHOOSE_RICH_SNIPPETS" />">
<form id="chooseRichSnippetDialogForm">
                <fmt:message key="RS_REVIEW_RICH_SNIPPETS" />
<ul>
            	<c:forEach items="${richSnippets}" var="richSnippent">
            		<c:if test="${richSnippent.discrim == 'review'}">
            			<li style="margin-left: 20px;">
            				<input type="checkbox" value="${richSnippent.id}" name="ids"
             				<c:forEach items="${richSnippetable.richSnippets}" var="checkedRichSnippent">
             					<c:if test="${checkedRichSnippent == richSnippent}">
             						checked="true"
             					</c:if>
             				</c:forEach>
            				/> <c:out value="${richSnippent.name}"></c:out>
            			</li>
            		</c:if>
            	</c:forEach>
</ul>
<fmt:message key="RS_AGGREGATE_REVIEW_RICH_SNIPPETS" />
<ul>
            	<c:forEach items="${richSnippets}" var="richSnippent">
            		<c:if test="${richSnippent.discrim == 'aggregate_review'}">
            			<li style="margin-left: 20px;">
            				<input type="checkbox" value="${richSnippent.id}" name="ids"
             				<c:forEach items="${richSnippetable.richSnippets}" var="checkedRichSnippent">
             					<c:if test="${checkedRichSnippent == richSnippent}">
             						checked="true"
             					</c:if>
             				</c:forEach>
            				/> <c:out value="${richSnippent.name}"></c:out>
            			</li>
            		</c:if>
            	</c:forEach>
</ul>
            <fmt:message key="RS_PRODUCT_RICH_SNIPPETS" />
            <ul>
            	<c:forEach items="${richSnippets}" var="richSnippent">
            		<c:if test="${richSnippent.discrim=='product'}">
            			<li style="margin-left: 20px;">
            				<input type="checkbox" value="${richSnippent.id}" name="ids"
             				<c:forEach items="${richSnippetable.richSnippets}" var="checkedRichSnippent">
             					<c:if test="${checkedRichSnippent == richSnippent}">
             						checked="true"
             					</c:if>
             				</c:forEach>
            				/> <c:out value="${richSnippent.name}"></c:out>
            			</li>
            		</c:if>
            	</c:forEach>
</ul>
            <fmt:message key="RS_VIDEO_RICH_SNIPPETS" />
            <ul>
            	<c:forEach items="${richSnippets}" var="richSnippent">
            		<c:if test="${richSnippent.discrim=='video'}">
            			<li style="margin-left: 20px;">
            				<input type="checkbox" value="${richSnippent.id}" name="ids"
            					<c:forEach items="${richSnippetable.richSnippets}" var="checkedRichSnippent">
             					<c:if test="${checkedRichSnippent == richSnippent}">
             						checked="true"
             					</c:if>
             				</c:forEach>
            				/> <c:out value="${richSnippent.name}"></c:out>
            			</li>
            		</c:if>
            	</c:forEach>
</ul>
</form>
</div>