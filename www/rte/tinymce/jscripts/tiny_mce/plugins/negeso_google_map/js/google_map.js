function init() {
	tinyMCEPopup.resizeToInnerSize();
}

function insertData() {
	try {
		var link = document.forms[0].link.value;
		var height = document.forms[0].height.value;
		var width = document.forms[0].width.value;
		var mid = gup(link, 'mid');
		link = 'https://mapsengine.google.com/map/embed?mid=' + mid;
		var html = '<iframe width="' + width + '" height="' + height
				+ '" frameborder="0" scrolling="auto" src="' + link
				+ '"></iframe>';

		tinyMCEPopup.execCommand("mceInsertContent", true, html);
	} catch (e) {
		alert("ERROR")
	}

	tinyMCEPopup.close();
}

function gup(link, name) {
	name = name.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");
	var regexS = "[\\?&]" + name + "=([^&#]*)";
	var regex = new RegExp(regexS);
	var results = regex.exec(link);
	if (results == null)
		return null;
	else
		return results[1];
}