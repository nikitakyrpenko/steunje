tinyMCEPopup.requireLangPack();
tinyMCEPopup.onInit.add(onLoadInit);

var editor = null;
var use_code_mirror = !(tinymce.isIE);


function saveContent() {
	if (use_code_mirror) {
		html = editor.save();
	}
	tinyMCEPopup.editor.setContent(document.getElementById('htmlSource').value, {source_view : true});
	tinyMCEPopup.close();
}

function onLoadInit() {
	tinyMCEPopup.resizeToInnerSize();	
	// Remove Gecko spellchecking
	if (tinymce.isGecko)
	try{
		document.body.spellcheck = tinyMCEPopup.editor.getParam("gecko_spellcheck");
	}catch(e){}

	document.getElementById('htmlSource').value = tinyMCEPopup.editor.getContent();

	if (tinyMCEPopup.editor.getParam("theme_advanced_source_editor_wrap", true)) {
		turnWrapOn();
		document.getElementById('wraped').checked = true;
	}

	resizeInputs();
	
	if (use_code_mirror){
		initCodeMirror(true);
	}

}

function initCodeMirror(lineWrapping) {
	editor = CodeMirror.fromTextArea(document.getElementById("htmlSource"), {
        lineNumbers: true,
        lineWrapping : lineWrapping,
        mode: "htmlmixed"
      });
}

function setCodeMirrorWrap(){
	if (use_code_mirror && editor != null) {
		editor.setOption('lineWrapping', !editor.getOption('lineWrapping'));
	}
}

function setWrap(val) {
	var v, n, s = document.getElementById('htmlSource');

	s.wrap = val;

	if (!tinymce.isIE) {
		v = s.value;
		n = s.cloneNode(false);
		n.setAttribute("wrap", val);
		s.parentNode.replaceChild(n, s);
		n.value = v;
	}
	setCodeMirrorWrap();	
}

function setWhiteSpaceCss(value) {
	var el = document.getElementById('htmlSource');
	tinymce.DOM.setStyle(el, 'white-space', value);
	setCodeMirrorWrap();
}

function turnWrapOff() {
	if (tinymce.isWebKit) {
		setWhiteSpaceCss('pre');
	} else {
		setWrap('off');
	}
}

function turnWrapOn() {
	if (tinymce.isWebKit) {
		setWhiteSpaceCss('pre-wrap');
	} else {
		setWrap('soft');
	}
}

function toggleWordWrap(elm) {
	if (elm.checked) {
		turnWrapOn();
	} else {
		turnWrapOff();
	}
}

function resizeInputs() {
	var vp = tinyMCEPopup.dom.getViewPort(window), el;

	el = document.getElementById('htmlSource');

	if (el) {
		el.style.width = (vp.w - 20) + 'px';
		el.style.height = (vp.h - 65) + 'px';
	}
}
