<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri = "http://www.springframework.org/tags/form" prefix = "form"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="negeso"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%
    response.setHeader("Expires", "0");
%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>Product details</title>

    <link href="/css/admin_style.css" rel="stylesheet" type="text/css" />
    <link href="/css/customized-jquery-ui.css" rel="stylesheet" type="text/css" />

    <script type="text/javascript" src="/script/jquery.min.js"></script>
    <script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
    <script type="text/javascript" src="/script/cufon-yui.js"></script>
    <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
    <script type="text/javascript" src="/script/common_functions.js"></script>
    <script type="text/javascript" src="/script/media_catalog.js"></script>
    <script type="text/javascript">

    </script>
</head>

<body>
<negeso:admin>
    <form:form action="webshop_products.html" method="post" modelAttribute="product">
    <input name="action" type="hidden" value="save" />

    <table align="center" border="0" cellpadding="0" cellspacing="0" class="admTable" style="width: 100%;">
        <tr>
            <td class="admTableTD" style="width: 25%"><form:label path="available">available</form:label></td>
            <td class="admTableTDLast admFormValidationErrorMessage">
                <form:select path="available" cssClass="admTextArea" >
                    <form:option value="true">yes</form:option>
                    <form:option value="false">no</form:option>
                </form:select>
            </td>
        </tr>
        <tr>
            <td class="admTableTD" style="width: 25%"><form:label path="id">EAN*</form:label></td>
            <td class="admTableTDLast admFormValidationErrorMessage">
                <form:input path="id" cssClass="admTextArea" readonly="${!newInstance}" />
            </td>
        </tr>
        <tr>
            <td class="admTableTD" style="width: 25%"><form:label path="stock">Stock</form:label></td>
            <td class="admTableTDLast admFormValidationErrorMessage">
                <form:input path="stock" cssClass="admTextArea" />
            </td>
        </tr>
        <tr>
            <td class="admTableTD" style="width: 25%"><form:label path="image">Image</form:label></td>
            <td class="admTableTDLast admFormValidationErrorMessage">
                <form:input path="image" cssClass="admTextArea" />
                <img src="${product.image}">
            </td>
        </tr>
        <tr>
            <td class="admTableTD" style="width: 25%"><form:label path="description">Description</form:label></td>
            <td class="admTableTDLast admFormValidationErrorMessage">
                <form:textarea path="description" cssClass="admTextArea" />
            </td>
        </tr>
        <tr>
            <td class="admTableTD" style="width: 25%"><form:label path="price">Price*</form:label></td>
            <td class="admTableTDLast admFormValidationErrorMessage">
                <form:input path="price" cssClass="admTextArea" />
            </td>
        </tr>
        <tr>
            <td class="admTableTD" style="width: 25%"><form:label path="purchaseprice">Purchase price</form:label></td>
            <td class="admTableTDLast admFormValidationErrorMessage">
                <form:input path="purchaseprice" cssClass="admTextArea" />
            </td>
        </tr>
        <tr>
            <td class="admTableTD" style="width: 25%"><form:label path="mincount">Min count</form:label></td>
            <td class="admTableTDLast admFormValidationErrorMessage">
                <form:input path="mincount" cssClass="admTextArea" />
            </td>
        </tr>
        <tr>
            <td class="admTableTD" style="width: 25%"><form:label path="increment">Increment</form:label></td>
            <td class="admTableTDLast admFormValidationErrorMessage">
                <form:input path="increment" cssClass="admTextArea" />
            </td>
        </tr>
        <tr>
            <td class="admTableTD" style="width: 25%"><form:label path="lockedminount">Locked min count</form:label></td>
            <td class="admTableTDLast admFormValidationErrorMessage">
                <form:select path="lockedminount" cssClass="admTextArea" >
                    <form:option value="true">yes</form:option>
                    <form:option value="false">no</form:option>
                </form:select>
            </td>
        </tr>
        <tr>
            <td>
                <div class="admNavPanelInp">
                    <div class="imgL"></div>
                    <div>
                        <input type="submit"/>
                    </div>
                    <div class="imgR"></div>
                </div>
            </td>
        </tr>
    <tr>
        <td class="" colspan="2">&nbsp;</td>
    </tr>
    </table>
    </form:form>
</negeso:admin>
</body>
</html>
