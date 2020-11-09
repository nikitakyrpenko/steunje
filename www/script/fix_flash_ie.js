try { // Works only in IE
	var objectElms = document.getElementsByTagName("object");
	var embedElms = document.getElementsByTagName("embed");
	for (var i = 0; i < embedElms.length; i++)
	    embedElms[i].outerHTML = embedElms[i].outerHTML;
	for (var i = 0; i < objectElms.length; i++)
	    objectElms[i].outerHTML = objectElms[i].outerHTML;
} catch(e) {}