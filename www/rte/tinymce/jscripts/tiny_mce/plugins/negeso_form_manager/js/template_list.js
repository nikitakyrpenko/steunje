var TemplateDialog = {
	init : function(ed) {
		tinyMCEPopup.resizeToInnerSize();
	}
}

function insertAction() {

	var inst = tinyMCEPopup.editor;
	var elm = tinyMCEPopup.editor.windowManager.params.selectedNode;
	var formObj = document.forms[0];

	var html;
	var negeso_form_id;
	try {
		html = document.getElementById(current).innerHTML;
		negeso_form_id = current_id;
	} catch(e) {
		html = '';
		negeso_form_id = '';
	}

	var style = 'border: 1px dashed #555555; width: 90%; overflow: hidden; padding: 10px; background-color: #dddddd';
	html = '<div style="' + style + '" negeso_form_id="' + negeso_form_id + '">' + html + '</div>';

	tinyMCEPopup.execCommand("mceInsertContent", false, html);
	tinyMCEPopup.close();
}

function cancelAction() {
	tinyMCEPopup.close();
}

tinyMCEPopup.onInit.add(TemplateDialog.init, TemplateDialog);