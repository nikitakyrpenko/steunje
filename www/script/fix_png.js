// Originally from: http://homepage.ntlworld.com/bobosola
// Modified for Negeso W/CMS by Halyasovskyy Andriy, 01.04.2007

function fixAllPNG() {
	try {
		if (browserType() == "MSIE" && (browserVersion() == "5.5" || browserVersion() == "6.0")) {
			for (var i = 0; i < window.document.images.length; i++) {
				var img1 = window.document.images[i];
				if (img1.style.backgroundImage && img1.style.backgroundImage.toUpperCase().contains(".PNG") || img1.src.toUpperCase().contains(".PNG")) {
					fixPNG(window.document.images[i]);
				}
			}
		}
	} catch(e) {}
}

function fixPNG(myImage) {
	if (
			myImage.tagName.toUpperCase() != "IMG" &&
			myImage.style.backgroundImage &&
			myImage.style.backgroundImage.toUpperCase().contains(".PNG")
		) {
		var indexL = myImage.style.backgroundImage.toUpperCase().indexOf("URL(") + 4;
 		var indexR = myImage.style.backgroundImage.toUpperCase().indexOf(".PNG") + 4;
		var src = myImage.style.backgroundImage.substring(indexL, indexR);
		myImage.style.backgroundImage = "url()";
		myImage.style.cssText = "filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src='" + src + "', sizingMethod='scale');" + myImage.style.cssText;
		return -1;
	}
	var transparentImage = "0.gif";
	var imgID = (myImage.id) ? "id=\"" + myImage.id + "\" " : "";
	var imgName = (myImage.name) ? "name=\"" + myImage.name + "\" " : "";
	var imgClass = (myImage.className) ? "class=\"" + myImage.className + "\" " : "";
	var imgTitle = (myImage.title) ? "title=\"" + myImage.title  + "\" " : "title=\"" + myImage.alt + "\" ";
	var imgTitleMap = (myImage.title) ? myImage.title : myImage.alt;
	var imgStyle = "display: inline-block;" + myImage.style.cssText;
	if (myImage.align == "left") imgStyle += "float: left;";
	if (myImage.align == "right") imgStyle += "float: right;";
	if (myImage.parentElement.href) imgStyle += "cursor: hand;";
	if (myImage.useMap) {
		strAddMap = "<img "
							+ "style=\""
								+ "position:relative;"
								+ "left:-" + myImage.width + "px;"
								+ "width:" + myImage.width + "px;"
								+ "height:" + myImage.height + "px;"
							+ "\" "
							+ "src=\"" + transparentImage + "\" "
							+ "usemap=\"" + myImage.useMap + "\" "
							+ "border=\"" + myImage.border + "\" "
							+ "alt=\"" + imgTitleMap + "\" "
						+ ">";
	}
	var strNewHTML = "<span "
							+ imgID + imgName + imgClass + imgTitle
							+ "style=\""
								+ "width:" + myImage.width + "px; "
								+ "height:" + myImage.height + "px; "
								+ imgStyle + "; "
								+ "filter:progid:DXImageTransform.Microsoft.AlphaImageLoader"
								+ "(src='" + myImage.src + "', sizingMethod='scale');\""
						+ ">"
						+ "</span>";
	if (myImage.useMap) strNewHTML += strAddMap;
	myImage.outerHTML = strNewHTML;
	return 0;
}

fixAllPNG ();