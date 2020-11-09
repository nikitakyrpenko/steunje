function init() {
	tinyMCEPopup.resizeToInnerSize();
}

function insertYoutube() {
	try {
		var link = document.forms[0].link.value;
		var height = document.forms[0].height.value;
		var width = document.forms[0].width.value;
		if (link.indexOf('v=') > 0) {
			link = link.substring(link.indexOf('v=') + 2, link.length);
			if (link.indexOf('&')) {
				link = link.substring(0, link.indexOf('&'));
			}
			link = 'http://www.youtube.com/embed/' + link + '?rel=0';
		} else if (link.indexOf('/embed/') == -1) {
			link = link.replace('youtu.be/', 'www.youtube.com/embed/');
		}
		
		var html = '<iframe width="' + width + '" height="' + height + '" frameborder="0" scrolling="auto" src="' + link + '"></iframe>';
		
		tinyMCEPopup.execCommand("mceInsertContent", true, html);
	} catch(e) {alert("ERROR")}

	tinyMCEPopup.close();
}