function html_encode(html) {
	html = html.replace(new RegExp("<","gi"),"&lt;");
	html = html.replace(new RegExp(">","gi"),"&gt;");
	html = html.replace(new RegExp("\"","gi"),"&quot;");
	return html;	
}

function html_decode(html) {
	html = html.replace(new RegExp("&lt;","gi"),"<");
	html = html.replace(new RegExp("&gt;","gi"),">");
	html = html.replace(new RegExp("&quot;","gi"),"\"");
	return html;
}

function insertTemplate() {

	try {
		var html = document.forms[0].templates_list.value;
		html = html_decode(html);
		tinyMCEPopup.execCommand("mceInsertContent", true, html);
	} catch(e) {alert("ERROR")}

	tinyMCEPopup.close();
}

function init() {
	tinyMCEPopup.resizeToInnerSize();
}
