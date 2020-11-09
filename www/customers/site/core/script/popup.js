/* Read last saved size of popup */
function getSavedDimensions()
{
	var client_width;
	var client_height;
	try {
		client_width =  parseInt(popupSize.split(' ')[0]);
		client_height =  parseInt(popupSize.split(' ')[1]);
	} catch(e) { }
	if(isNaN(client_width) || isNaN(client_height)){
		client_width = 400;
		client_height = 300;
	}
	return new Array(client_width, client_height);
}

/* Resizes the article when an editor resizes the popup */
function resizeContent()
{
	document.getElementById('editableDiv').style.width = document.body.clientWidth + "px";
	document.getElementById('editableDiv').style.height = document.body.clientHeight + "px";
}

/* Save new size of popup on server and in variable "popupSize" */
function saveNewSize()
{
	var saved_dimensions = getSavedDimensions();
	var isResized = (saved_dimensions[0] != document.body.clientWidth || saved_dimensions[1] != document.body.clientHeight);
	if (!isResized) {
		return true;
	}
        
	var response = confirm("Save this size of popup?");
	if (response == false) {
		return true;
	}
	
	var newSize = document.body.clientWidth + " " + document.body.clientHeight;

	window.opener.AJAX_Send("update-article-info-command", {"id" : popupArticleId, "head" : newSize});
	return false;
}

// Already defined in scripts/common_functions.js
var isMSIE = (navigator.appName == "Microsoft Internet Explorer");
var isGecko = navigator.userAgent.indexOf('Gecko') != -1;
var isOpera = navigator.userAgent.indexOf('Opera') != -1;

if (isMSIE && !isOpera)
	window.attachEvent("onresize", resizeContent);
if (isOpera || isGecko)
	window.onresize = resizeContent;

	
function attachSaveNewSize() {
	if (isMSIE && !isOpera)
		window.attachEvent("onbeforeunload", saveNewSize);
	if (isGecko)
		window.onbeforeunload = saveNewSize;
	if (isOpera) 
		window.onunload = saveNewSize;
}

window.resizeBy(getSavedDimensions()[0] - document.body.clientWidth,
				getSavedDimensions()[1] - document.body.clientHeight );

resizeContent();