<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="negeso"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%
    response.setHeader("Expires", "0");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title><fmt:message key="BATEN_ARTICLE_INFO_MODULE" /></title>

    <link href="/css/admin_style.css" rel="stylesheet" type="text/css" />

    <link href="/css/customized-jquery-ui.css" rel="stylesheet" type="text/css" />
    <link href="/css/jqx.base.css" rel="stylesheet" type="text/css" />
    <link href="/css/jqx.classic.css" rel="stylesheet" type="text/css" />

    <script type="text/javascript" src="/script/jquery.min.js"></script>
    <script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
    <script type="text/javascript" src="/script/cufon-yui.js"></script>
    <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
    <script type="text/javascript" src="/script/common_functions.js"></script>
    <script type="text/javascript" src="/script/RTE_Adapter.js">/**/</script>
    <script type="text/javascript" src="/script/rte_implement.js">/**/</script>
    <script type="text/javascript" src="/script/media_catalog.js"></script>
    <script type="text/javascript">
        function deleteById(id, clickedObj){
            if (!confirm('Are you sure you want to delete this product')) {
                return;
            }
            $.ajax({
                type: "POST",
                url: "/admin/webshop_products.html",
                dataType: "html",
                data: {id: id, action: 'delete'},
                success: function (data, stat) {
                    var rowFordeletion = $(clickedObj).parents('tr:first');
                    $('td', rowFordeletion).animate( {backgroundColor:'red'}, 1000);
                    $(rowFordeletion).fadeOut(1000,function() {
                        $(rowFordeletion).remove();
                        window.location.reload();
                    });
                },
                error: function (html, stat) {
                    alert(html.response);
                }
            });
        }
    </script>
</head>

<body>
    <negeso:admin>
        <table align="center" border="0" cellpadding="0" cellspacing="0" class="admTable">
            <tr>
                <td colspan="2">
                    <table align="center" border="0" cellpadding="0" cellspacing="0" class="admTable">
                        <tr>
                            <td class="admTableTD">Image</td>
                            <td class="admTableTD">EAN</td>
                            <td class="admTableTD">Description</td>
                            <td class="admTableTD">Price</td>
                            <td class="admTableTD">Available</td>
                            <td class="admTableTD" colspan="2">Actions</td>
                        </tr>
                        <%--@elvariable id="products" type="java.util.List<com.negeso.module.baten.entity.ArticleInfo>"--%>
                        <c:forEach items="${products}" var="product">
                            <tr>
                                <td class="admTableTD">
                                    <img alt="" src="${product.image}" width="${imageWidth}" height="${imageHeight}" class="admBorder">
                                </td>
                                <td class="admTableTD">
                                    ${product.id}
                                </td>
                                <td class="admTableTD">
                                    ${product.description}
                                </td>
                                <td class="admTableTD">
                                    &euro;<fmt:formatNumber type="number" pattern=" #0.00" value="${product.price}" />
                                </td>
                                <td class="admTableTD">
                                    <c:choose>
                                        <c:when test="${product.available}">
                                            yes
                                        </c:when>
                                        <c:otherwise>
                                            no
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="admTableTDLast" style="padding: 0;">
                                    <a href="/admin/webshop_products.html?action=edit&id=${product.id}">
                                        <img title="<fmt:message key="EDIT" />" class="admHand" src="/images/edit.png">
                                    </a>
                                </td>
                                <td class="admTableTDLast" style="padding: 0;">
                                    <img onclick="deleteById(${product.id}, this)" title="<fmt:message key="DELETE" />" class="admHand" src="/images/delete.png">
                                </td>
                            </tr>
                        </c:forEach>
                        <tr>
                            <td class="admTableFooter" colspan="7">&nbsp;</td>
                        </tr>
                    </table>
                </td>
            </tr>

        </table>
    </negeso:admin>
    <table cellpadding="0" cellspacing="0" width="764px" align="center" border="0" class="Buttons">
        <tr>
            <td>
                <div class="admBtnGreenb admBtnBlueb">
                    <div class="imgL"></div>
                    <div>
                        <a href="/admin/webshop_products.html?action=add"><fmt:message key="ADD"/></a>
                    </div>
                    <div class="imgR"></div>
                </div>
            </td>
        </tr>
    </table>
</body>
</html>
