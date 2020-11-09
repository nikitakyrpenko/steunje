/*
  Copyright (c) 2008 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information"). You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.

  Script for dynamic acting with news list. First approximation :)
  
  Script uses /www/script/AJAX_webservice.js
 
  @version		2008.01.03
  @author		Rostislav 'KOTT' Brizgunov
*/

/* This message should be re-defined in the XSL */
var s_DeleteConfirmation = "DELETE_CONFIRMATION";

function deleteNewsListItem(listId, listItemId) {
	if (confirm(s_DeleteConfirmation)) {
		var loc = location.href;
		AJAX_Send(
			"delete-list-item-command", 
			{listItemId: listItemId, listId: listId, summary: "true", details: "true", realClose: "true"},
			function(){
				var elm = document.getElementById("short_news_"+listId+"_"+listItemId);
				elm.parentNode.removeChild(elm);
			}
		)
	}
}

function editNewsList(listId) {
	var newWin = window.open("?command=get-list-command&listItemId=&listItems=&action=&listId="+listId+"&summary=true&details=true&moveToId=&isMove=&realClose=true", "newsAdminPart", "width=800, height=600, toolbar=0, menubar=0, location=0, scrollbars=1")
}

function editNewsListItem(listId, listItemId) {
	var newWin = window.open("?command=get-list-item-command&listId="+listId+"&listItemId="+listItemId, "newsAdminPart", "width=800, height=600, toolbar=0, menubar=0, location=0, scrollbars=1")
}

function addNewsListItem(listId) {
	var newWin = window.open("?command=create-list-item-command&listItemId=&listItems=&action=&listId="+listId+"&summary=true&details=true&moveToId=&isMove=&realClose=true", "newsAdminPart", "width=800, height=600, toolbar=0, menubar=0, location=0, scrollbars=1")
}