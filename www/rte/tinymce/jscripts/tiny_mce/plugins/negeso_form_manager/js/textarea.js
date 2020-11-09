var InputTextareaDialog = {
	init : function(ed) {
		tinyMCEPopup.resizeToInnerSize();

		var formObj = document.forms[0];
		var inst = tinyMCEPopup.editor;
		var elm = tinyMCEPopup.editor.windowManager.params.selectedNode;
		var dom = inst.dom;
		var action = "insert";
		var html = "";
		// Standart function: adds classes into class-list
		addClassesToList('class_list');
		selectByValue(formObj, 'class_list', dom.getAttrib(elm, 'class'));
		if (tinyMCE.isOpera)
			formObj.elements['class_list'].disabled = true;
			
		if (tinyMCE.isOpera == true) {
			formObj.text.disabled = true;
			formObj.text.value = inst.getLang('negeso_form_manager.opera_error_1', 'error', true);
		} else {
			formObj.text.disabled = false;
			//formObj.text.value = '';
		}
		
		// Check action
		if (elm != null && elm.nodeName == "TEXTAREA")
			action = "update";
		else
			formObj.text.disabled = true;
			
		formObj.insert.value = inst.getLang(action, 'Insert', true); 

		if (action == "update") {
			// Setup form data
			var style = dom.parseStyle(dom.getAttrib(elm, 'style'));
			formObj.name.value       = dom.getAttrib(elm, 'name');
			//formObj.field_id.value   = tinyMCE.getAttrib(elm, 'id');
			if (!tinyMCE.isOpera)
				formObj.text.value   = elm.innerHTML;
			formObj.title.value      = dom.getAttrib(elm, 'title');
			formObj.rows.value       = dom.getAttrib(elm, 'rows');
			formObj.cols.value       = dom.getAttrib(elm, 'cols');
			formObj.tabindex.value   = dom.getAttrib(elm, 'tabindex');
			formObj.style.value      = dom.serializeStyle(style);
			formObj.readonly.checked = ('' + dom.getAttrib(elm, 'readonly') == 'true' ||
					dom.getAttrib(elm, 'readonly') == 'readonly');
			formObj.required.checked = ('' + dom.getAttrib(elm, 'is_required') == 'true' ||
					dom.getAttrib(elm, 'is_required') == 'required');
			
			if (formObj.readonly.checked){
				document.getElementById('validation_tab').style.display='none';
			}
			else{
				document.getElementById('validation_tab').style.display='';
			}
		}
	}
};

function setAttrib(elm, attrib, value) {
	var formObj = document.forms[0];
	var valueElm = formObj.elements[attrib];

	if (typeof(value) == "undefined" || value == null) {
		value = "";

		if (valueElm)
			value = valueElm.value;
	}

	if (value != "") {
		elm.setAttribute(attrib, value);
		
		if (attrib == "style")
			attrib = "style.cssText";

		if (attrib == "class")
			attrib = "className";

		eval('elm.' + attrib + "=value;");
	} else
		elm.removeAttribute(attrib);
}

function makeAttrib(attrib, value) {
	var formObj = document.forms[0];
	var valueElm = formObj.elements[attrib];

	if (typeof(value) == "undefined" || value == null) {
		value = "";

		if (typeof(valueElm) != "undefined" && valueElm != null)
			value = valueElm.value;
	}

	if (value == "")
		return "";

	// XML encode it
	value = value.replace(/&/g, '&amp;');
	value = value.replace(/\"/g, '&quot;');
	value = value.replace(/</g, '&lt;');
	value = value.replace(/>/g, '&gt;');

	return ' ' + attrib + '="' + value + '"';
}

//function insertAction() {
function insertAction(){
	if(document.getElementById('name').value != 'email'){
		insertField();
	}
	else{
		alert(EmailReservedFieldNameError);
	}
}

function insertField() {


	var inst = tinyMCEPopup.editor;
	var elm = tinyMCEPopup.editor.windowManager.params.selectedNode;
	var formObj = document.forms[0];
		
	if (elm != null && elm.nodeName.toUpperCase() == "TEXTAREA") {
		setAttrib(elm, 'name');
		//setAttrib(elm, 'id', formObj.elements['field_id'].value);
		setAttrib(elm, 'title');
		if (!tinyMCE.isOpera)
			elm.innerHTML = formObj.elements['text'].value;
		// Opera - SUX
		//elm.value = formObj.elements['text'].value;
		//elm.outerHTML = elm.outerHTML.replace('</',formObj.elements['text'].value + '</');
		setAttrib(elm, 'style');
		setAttrib(elm, 'tabindex');
		setAttrib(elm, 'tabIndex',  formObj.elements['tabindex'].value);
		//setAttrib(elm, 'class', getSelectValue(formObj, 'classlist'));
		setAttrib(elm, 'rows');
		setAttrib(elm, 'cols');
		if (!tinyMCE.isOpera)
			setAttrib(elm, 'class', formObj.elements['class_list'].value);
		setAttrib(elm, 'readonly', formObj.elements['readonly'].checked == true ? 'true' : '' );
		elm.removeAttribute('is_required');
				
		if(formObj.readonly.checked == true) {
			setAttrib(elm, 'is_required', '');			
		} else {
			setAttrib(elm, 'is_required', formObj.required.checked == true ? 'true' : '');
		}
	
		
		inst.execCommand("mceRepaint");
		inst.execCommand('mceCleanup');
		
		// Refresh in old MSIE
		if (tinyMCE.isMSIE5)
			elm.outerHTML = elm.outerHTML;

	} else {
		var html = '<textarea ';
		html += makeAttrib('name');
		//html += makeAttrib('id', formObj.elements['field_id'].value);
		html += makeAttrib('title');
		html += makeAttrib('style');
		html += makeAttrib('tabindex');
		html += makeAttrib('rows');
		html += makeAttrib('cols');		
		html += makeAttrib('class', formObj.elements['class_list'].value);
		html += makeAttrib('readonly', formObj.elements['readonly'].checked == true ? 'true' : '');
				
		if(formObj.elements['readonly'].checked == 'true')
			html += makeAttrib('is_required', formObj.elements['required'].checked = '');
		else
			html += makeAttrib('is_required', formObj.elements['required'].checked == true ? 'true' : '');
		/*
		remark #insert field              end
		*/
		
		html += '>';
		//html += formObj.elements['text'].value;
		html += '</textarea>';
		tinyMCEPopup.execCommand("mceInsertContent", false, html);
	}

	//tinyMCE._setEventsEnabled(inst.getBody(), false);
	tinyMCEPopup.close();
}

function cancelAction() {
	tinyMCEPopup.close();
}

function change_readonly(obj){

	if(obj.checked){
		document.getElementById('validation_tab').style.display='none';
		document.getElementById('required').checked = false;
	}
	else{
		document.getElementById('validation_tab').style.display='';
	}
	return true;
}

tinyMCEPopup.onInit.add(InputTextareaDialog.init, InputTextareaDialog);