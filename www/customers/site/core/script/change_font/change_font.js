/** 
 *          Font Size changer
 *          (c) 2007 NEGESO
 *    Changed by Rostislav Brizgunov
 *
 * In some sites we need to apply font changing mechanism.
 * You can see it on all our demosites, e.g.: demo258.dev.negeso.com
 *
 * Example of including this mechanism:
 *
 * <head>
 * ...
 * <link rel="stylesheet" type="text/css" href="/site/core/core/change_font/change_font.css"/>
 * <script type="text/javascript" src="/site/core/script/change_font/change_font.js"></script>
 * ...
 * <body>
 * ...
 * <div class="fontSize">
 *     <a href="javascript:changeFontSize('first', 0);" class="fontSize_sm"><img src="/site/core/images/1x1.gif" alt="Small font" id="fontSmal"/></a>
 *     <a href="javascript:changeFontSize('second', 0);" class="fontSize_mid"><img src="/site/core/images/1x1.gif" width="21" height="22" alt="Middle font" id="fontMed"/></a>
 *     <a href="javascript:changeFontSize('third', 0);" class="fontSize_big"><img src="/site/core/images/1x1.gif" width="21" height="22" alt="Big font" id="fontBig"/></a>
 *     <script type="text/javascript">changeImages();</script>
 * </div> 
 *
 */

/* Pathes for default font size IMAGES */
var fontBigIMGPath = "/site/core/images/change_font/fontBigr.gif";
var fontMediumIMGPath = "/site/core/images/change_font/fontMedr.gif";
var fontSmallIMGPath = "/site/core/images/change_font/fontSmalr.gif";
var fakeIMGPath = "/site/core/images/spacer.gif";

/* Pathes for default font size CSS files */
var fontBigCSSPath = "/site/core/css/change_font/fontBig.css";
var fontMediumCSSPath = "/site/core/css/change_font/fontMedium.css";
var fontSmallCSSPath = "/site/core/css/change_font/fontSmall.css";

/* ===== changeFontSize(...): Main function for default page fonts changing. ===== 
---------------------------
Description:
---------------------------
This function changes default font sizes on the page, if such functionality is presented on the page.
For correct work you need three CSS and IMAGE files, defined upwards, to be presented.
---------------------------
Parameters:
---------------------------
 -    new_size: alias of font size. Values: 'first', 'second', 'third'. Otherwise: interpreted as 'first'.
 - with_reload: method of styles changing (with page reload, or without it). Values: 0 and 1. Otherwise: interpreted as 0.
                If with_reload==1 then page will be reloaded after function executing.
*/
function changeFontSize(new_size, with_reload) {
	
	var css = "";
	if (typeof(new_size) != "undefined" && new_size != null) {
		setFontCookie(new_size);
		css = new_size;
	} else {
		css = getCookie("cssCookie");
	}

	if (typeof(with_reload) != "undefined" && with_reload == 1) {
		window.location.reload();
		return;
	}
	
	var font_size_css_link = document.getElementById("default_page_font_size");

	if (css && font_size_css_link) {
		if (css == 'third')
			font_size_css_link.href = fontBigCSSPath;
		else if (css == 'second')
			font_size_css_link.href = fontMediumCSSPath;
		else
			font_size_css_link.href = fontSmallCSSPath;

		try {
			changeImages();
		} catch(e) {/* when this function is called in the HEAD of the page, images are not loaded yet, thus an error appears */}
	}
}

function getCookie(sName) {
    var aCookie = document.cookie.split("; ");
    for (var i=0; i < aCookie.length; i++) {
        var aCrumb = aCookie[i].split("=");
        if (sName == aCrumb[0]) return unescape(aCrumb[1]);
    }
    return null;
}

function setFontCookie(sValue) {
	var name = "cssCookie";
	var oneMonth = 31*24*60*60*1000;
	var expDate = new Date();
	expDate.setTime(expDate.getTime() + oneMonth);
	document.cookie = name + "=" + escape(sValue) + "; expires=" + expDate.toGMTString();
}

function changeImages() {
	document.getElementById('fontBig').src = fakeIMGPath;
	document.getElementById('fontMed').src = fakeIMGPath;
	document.getElementById('fontSmal').src = fakeIMGPath;
	var css = getCookie("cssCookie");
	if (css) {
		if (css == 'third')
			document.getElementById('fontBig').src = fontBigIMGPath;
		else if (css == 'second')
			document.getElementById('fontMed').src = fontMediumIMGPath;
		else
			document.getElementById('fontSmal').src = fontSmallIMGPath;
	}
}

function changeFontSizeInit() {
	var css = getCookie("cssCookie");
	var def_css = "";
	if (css == 'third')
		def_css = fontBigCSSPath;
	else if (css == 'second')
		def_css = fontMediumCSSPath;
	else
		def_css = fontSmallCSSPath;
	// Pre-loading of Font Size images
	var img1 = new Image();
	img1.src = fontSmallIMGPath;
	var img2 = new Image();
	img2.src = fontSmallIMGPath;
	var img3 = new Image();
	img3.src = fontSmallIMGPath;
	document.write('<link rel="stylesheet" type="text/css" href="/site/core/css/change_font/change_font.css" />');
	document.write('<link rel="stylesheet" type="text/css" href="'+def_css+'" id="default_page_font_size" />');
}

changeFontSizeInit();