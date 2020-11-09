<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
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
<title><fmt:message key="THR_PUSH_MARKETING" />
</title>

<link href="/css/admin_style.css" rel="stylesheet" type="text/css" />

<link href="/css/customized-jquery-ui.css" rel="stylesheet"
	type="text/css" />
<link href="/css/jqx.base.css" rel="stylesheet" type="text/css" />
<link href="/css/jqx.classic.css" rel="stylesheet" type="text/css" />

<script type="text/javascript" src="/script/jquery.min.js"></script>
<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
<script type="text/javascript" src="/script/jqxcore.js"></script>
<script type="text/javascript" src="/script/jqxswitchbutton.js"></script>
<script type="text/javascript" src="/script/cufon-yui.js"></script>
<script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
<script type="text/javascript" src="/script/common_functions.js"></script>
<script type="text/javascript" src="/script/RTE_Adapter.js">/**/</script>
<script type="text/javascript" src="/script/rte_implement.js">/**/</script>
<script type="text/javascript" src="/script/media_catalog.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		// Create Switch Button.
		$("#jqxButton").jqxSwitchButton({
			theme : 'classic',
			width : '100',
			height : '30',
			checked : ${thrPushMarketingEnabled.value},
			onLabel: '<fmt:message key="THR_PUSH_MARKETING_ENABLED_LABEL" />',
			offLabel: '<fmt:message key="THR_PUSH_MARKETING_DISABLED_LABEL" />'
		});
		$("#jqxButton").bind('change', function(event) {
			var checked = event.args.check;
			$.post("/admin/thr_push_marketing.html", {
				action : "setEnabled",
                value : checked
            }, function(data) {
            });
		});
	});

	function addOrEditProduct(id) {
		$.get("/admin/thr_push_marketing.html", {
			action : "addOrEditProduct",
            id : id
        }, function(data) {
        	$("[class^=editProductDialog]").each(function() {
                $(this).remove();
            });
           $(data).dialog({
        	   width:'450',
        	   height:'auto',
               buttons: {
               '<fmt:message key="Cancel"/>': function() { $(this).remove();  },
               '<fmt:message key="SAVE"/>': function() {
                   	   if (validateForm(document.getElementById("editForm"))) {
	                       $.ajax({                    	  
	                           type: "POST",
	                           url: "/admin/thr_push_marketing.html",
	                           data: $('#editForm').serialize(),
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
               }} });
        });
	}

	function deleteById(id, clickedObj){
    	if (!confirm('<fmt:message key="THR_PRODUCT_CONFIRM_DELETION"/>')) {
            return;
        }
    	 $.ajax({
    	        type: "POST",
    	        url: "/admin/thr_push_marketing.html",
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

    function move(id, direction) {
    	$.ajax({
	        type: "POST",
	        url: "/admin/thr_push_marketing.html",
	        dataType: "html",
	        data: {id: id, action: 'move', direction: direction},
	        success: function (data, stat) {
	        	window.location.reload();
	        },
	        error: function (html, stat) {
	        	alert(html.response);
	        }
	    });
    }

	function selectThumbnailImageDialog() {
		result = MediaCatalog.selectThumbnailImageDialog(${thrImageWidth}, ${thrImageHeight});
		if (result != null){
			if (result.resCode == "OK") {
				$('#hiddenImageField').val(result.thumbnailImage);
				$('#img_id').attr('src', result.thumbnailImage);
			}
		}
	}
</script>
</head>

<body>
	<form method="POST" name="opperateForm">
		<input type="hidden" name="action" value="save" />
		<negeso:admin>
			<table align="center" border="0" cellpadding="0" cellspacing="0"
				class="admTable">
				<tr>
					<td align="center" class="admNavPanelFont" style="height: auto;" colspan="2">
						<fmt:message key="THR_PUSH_MARKETING" />
					</td>
				</tr>
				<tr>
					<td class="admTableTD" style="width: 25%">
						<fmt:message key="THR_SHOW_PUSH_MARKETING" />
					</td>
					<td class="admTableTDLast">
						<div id='jqxButton'></div>
					</td>
				</tr>
				<tr>
					<td class="admTableTD" style="width: 25%">
						<fmt:message key="THR_PUSH_MARKETING_TEXT" />
					</td>
					<td class="admTableTDLast">
						<img src="/images/mark_1.gif" class="RTEEntryPoint" alt="Editor"
											onclick="RTE_Init('article_text${article.id}','article_text${article.id}',${article.id}, 3, 0, 'contentStyle', getInterfaceLanguage(), null, false);" 
											alt="Text editor"/>
										<div id="article_text${article.id}" class="admRTEDiv" dir="ltr" style="margin-right:0;">
											<c:out value="${article.text}" escapeXml="yes"/>
										</div>
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<table align="center" border="0" cellpadding="0" cellspacing="0" class="admTable">
							<tr>
								<td align="center" class="admTDtitles" style="height: auto;" colspan="9">
									<fmt:message key="THR_PUSH_MARKETING_PRODUCTS" />
								</td>
							</tr>
							<c:forEach items="${products}" var="product">
								<tr>
									<td class="admTableTD" width="10%">
										<img alt="" src="${product.image}" width="${thrImageWidth}" height="${thrImageHeight}" class="admBorder">
									</td>
									<td class="admTableTD">
										<c:out value="${product.barCode}"></c:out>
									</td>
									<td class="admTableTD">
										<c:out value="${product.title}"></c:out>
									</td>
									<td class="admTableTD">
										&euro;<fmt:formatNumber type="number" pattern=" #0.00" value="${product.price}" />
									</td>
									<td class="admTableTD">
										<c:choose>
											<c:when test="${product.show == 'y'}">
												<fmt:message key="THR_PRODUCT_SHOW_YES" />
											</c:when>
											<c:when test="${product.show == 't'}">
												<span style="color: blue;"><fmt:message key="THR_PRODUCT_SHOW_TEST" /></span>
											</c:when>
											<c:otherwise>
												<span style="color: red;"><fmt:message key="THR_PRODUCT_SHOW_NO" /></span>
											</c:otherwise>
										</c:choose>
									</td>
									<td class="admTableTDLast" style="padding: 0px 0px 0px 3px;" width="3%">
										<img onclick="move(${product.id}, true)" title="<fmt:message key="UP" />" class="admHand" src="/images/up.png">
									</td>
									<td class="admTableTDLast" style="padding: 0;" width="3%">
										<img onclick="move(${product.id}, false)" title="<fmt:message key="DOWN" />" class="admHand" src="/images/down.png">
									</td>
									<td class="admTableTDLast" style="padding: 0;" width="3%">
										<img onclick="addOrEditProduct(${product.id})" title="<fmt:message key="EDIT" />" class="admHand" src="/images/edit.png">
									</td>
									<td class="admTableTDLast" style="padding: 0;" width="3%">
										<img onclick="deleteById(${product.id}, this)" title="<fmt:message key="DELETE" />" class="admHand" src="/images/delete.png">
									</td>
								</tr>
							</c:forEach>
							<tr>
								<td class="admTableFooter" colspan="9">&nbsp;</td>
							</tr>
						</table>
					</td>
				</tr>
				
			</table>
		</negeso:admin>
		<table cellpadding="0" cellspacing="0" width="764px" align="center"
			border="0" class="Buttons">
			<tr>
				<td>
					<div class="admBtnGreenb admBtnBlueb">
						<div class="imgL"></div>
						<div>
							<input type="button" name="saveform"
								value='<fmt:message key="ADD"/>' onclick="addOrEditProduct(0);" />
						</div>
						<div class="imgR"></div>
					</div>
				</td>
			</tr>
		</table>
	</form>
</body>
</html>
