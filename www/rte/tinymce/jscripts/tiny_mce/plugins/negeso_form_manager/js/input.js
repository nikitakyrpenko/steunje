function preinit() {
	// Initialize
	tinyMCE.setWindowArg('mce_windowresize', false);
}

function setFieldType(selectObj, type) {
	for (var i = 0; i < selectObj.options.length; i++) {
		if (selectObj.options[i].value == type) {
			selectObj.options[i].selected = true;
			break;
		}
	}
}

function init() {

	tinyMCEPopup.resizeToInnerSize();

	var formObj = document.forms[0];
	var inst = tinyMCE.getInstanceById(tinyMCE.getWindowArg('editor_id'));
	var elm = inst.getFocusElement();
	var action = "insert";
	var html = "";
	// Standart function: adds classes into class-list
	addClassesToList('class_list');
	selectByValue(formObj, 'class_list', tinyMCE.getAttrib(elm, 'class'));
	if (tinyMCE.isOpera)
		formObj.elements['class_list'].disabled = true;
			
	// Check action
	if (elm != null && elm.nodeName == 'INPUT')
		action = 'update';

	formObj.insert.value = tinyMCE.getLang('lang_' + action, 'Insert', true); 

	if (action == "update") {
		// Setup form data
		//// TODELETE: //// alert('HTML is '+elm.outerHTML+'\nelm.type = '+elm.type+'\nelm.readOnly = '+elm.readOnly+'\nelm.readonly = '+tinyMCE.getAttrib(elm, 'readonly')+'\nelm.size = '+elm.size+'\nelm.maxlength = '+elm.maxLength);
		var style = tinyMCE.parseStyle(tinyMCE.getAttrib(elm, 'style'));
		setFieldType(formObj.elements['type'], tinyMCE.getAttrib(elm, 'type'));
		formObj.name.value      = tinyMCE.getAttrib(elm, 'name');
		formObj.field_id.value  = tinyMCE.getAttrib(elm, 'id');
		formObj.value.value     = tinyMCE.getAttrib(elm, 'value');
		formObj.title.value     = tinyMCE.getAttrib(elm, 'title');
		formObj.size.value      = tinyMCE.getAttrib(elm, 'size');
		formObj.maxlength.value = tinyMCE.getAttrib(elm, 'maxlength');
		formObj.tabindex.value  = tinyMCE.getAttrib(elm, 'tabindex');
		setFieldType(formObj.elements['validtype'], 'empty');
		formObj.validparams.value = '';
		formObj.elements['type'].disabled = true;
		if (tinyMCE.getAttrib(elm, 'is_email') == 'true') setFieldType(formObj.elements['validtype'], 'email')
		else if (tinyMCE.getAttrib(elm, 'is_phone') == 'true') setFieldType(formObj.elements['validtype'], 'phone')
		else if (tinyMCE.getAttrib(elm, 'numeric_field_params') != '') {
			setFieldType(formObj.elements['validtype'], 'numeric');
			formObj.validparams.value = tinyMCE.getAttrib(elm, 'numeric_field_params');
		}
		else if (tinyMCE.getAttrib(elm, 'timedate_field_format') != '') {
			setFieldType(formObj.elements['validtype'], 'timedate');
			formObj.validparams.value = tinyMCE.getAttrib(elm, 'timedate_field_format');
		}
		formObj.style.value      = tinyMCE.serializeStyle(style);
		formObj.check.checked    = ('' + tinyMCE.getAttrib(elm, 'checked') == 'true') ? true : false;
		formObj.readonly.checked = ('' + tinyMCE.getAttrib(elm, 'readonly') == 'true' ||
									tinyMCE.getAttrib(elm, 'readonly') == 'readonly') ? true : false;
		formObj.required.checked = ('' + tinyMCE.getAttrib(elm, 'is_required') == 'true') ? true : false;
	
		// Tiny MCE developers part: I do not know - what for it
		//updateStyle();
		//changeAppearance();
		checkIt();
		
		window.focus();
	}
}

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

		if (attrib == "longdesc")
			attrib = "longDesc";

		if (attrib == "width") {
			attrib = "style.width";
			value = value + "px";
		}

		if (attrib == "height") {
			attrib = "style.height";
			value = value + "px";
		}

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

	var inst = tinyMCE.getInstanceById(tinyMCE.getWindowArg('editor_id'));
	var elm = inst.getFocusElement();
	var formObj = document.forms[0];
	var element_type = formObj.elements['type'].value;
	formObj.elements['type'].disabled = false;
		
	if (elm != null && elm.nodeName == "INPUT") {
		setAttrib(elm, 'type');
		setAttrib(elm, 'name');
		setAttrib(elm, 'id', formObj.elements['field_id'].value);
		setAttrib(elm, 'value');
		setAttrib(elm, 'title');
		setAttrib(elm, 'style');
		setAttrib(elm, 'tabindex');
		setAttrib(elm, 'tabIndex',  formObj.elements['tabindex'].value);
		//Empty filling, if type was changed
		setAttrib(elm, 'size', '');
		setAttrib(elm, 'maxlength', '');
		setAttrib(elm, 'maxLength', '');
		setAttrib(elm, 'readonly', '');
		setAttrib(elm, 'readOnly', '');
		setAttrib(elm, 'checked', '');
		if(!tinyMCE.isOpera)
			setAttrib(elm, 'class', formObj.elements['class_list'].value);
		//setAttrib(elm, 'class', getSelectValue(formObj, 'classlist'));
		if (element_type == 'text' || element_type == 'password') {
			setAttrib(elm, 'size');
			setAttrib(elm, 'maxlength');
			setAttrib(elm, 'maxLength',formObj.elements['maxlength'].value);
			setAttrib(elm, 'readonly', formObj.elements['readonly'].checked == true ? 'true' : '');
			setAttrib(elm, 'readOnly', formObj.elements['readonly'].checked == true ? 'true' : '');
			var valid_type = formObj.elements['validtype'].value;
			var valid_params = formObj.elements['validparams'].value;
			elm.removeAttribute('is_email');
			elm.removeAttribute('is_required');
			elm.removeAttribute('is_phone');
			elm.removeAttribute('numeric_field_params');
			elm.removeAttribute('timedate_field_format');
			//alert(elm.is_email);
			switch (valid_type) {
				case 'empty'    : break;
				case 'email'    : setAttrib(elm, 'is_email', 'true'); break;
				case 'phone'  	: setAttrib(elm, 'is_phone', 'true'); break;
				case 'numeric'  : setAttrib(elm, 'numeric_field_params', valid_params); break;
				case 'timedate' : setAttrib(elm, 'timedate_field_format', valid_params); break;
			}
			setAttrib(elm, 'is_required', formObj.elements['required'].checked == true ? 'true' : '');
		}
		if (element_type == 'radio' || element_type == 'checkbox') {
			setAttrib(elm, 'checked', formObj.elements['check'].checked == true ? 'true' : '');
		}
		
		//tinyMCEPopup.execCommand("mceRepaint");
		
		// Repaint if dimensions changed
		inst.repaint();

		// Refresh in old MSIE
		if (tinyMCE.isMSIE5)
			elm.outerHTML = elm.outerHTML;
		//// TODELETE: //// alert(elm.outerHTML);
		
	} else {
		var html = "<input ";
		html += makeAttrib('type');
		html += makeAttrib('name');
		html += makeAttrib('id', formObj.elements['field_id'].value);
		html += makeAttrib('value');
		html += makeAttrib('title');
		html += makeAttrib('style');
		html += makeAttrib('tabindex');
		html += makeAttrib('class', formObj.elements['class_list'].value);
		if (element_type == 'text' || element_type == 'password') {
			html += makeAttrib('size');
			html += makeAttrib('maxlength');
			html += makeAttrib('readonly', formObj.elements['readonly'].checked == true ? 'true' : '');
			var valid_type = formObj.elements['validtype'].value;
			var valid_params = formObj.elements['validparams'].value;
			switch (valid_type) {
				case 'empty'    : break;
				case 'email'    : html += makeAttrib('is_email', 'true'); break;
				case 'phone'  	: html += makeAttrib('is_phone', 'true'); break;
				case 'numeric'  : html += makeAttrib('numeric_field_params', valid_params); break;
				case 'timedate' : html += makeAttrib('timedate_field_format', valid_params); break;
			}
			html += makeAttrib('is_required', formObj.elements['required'].checked == true ? 'true' : '');
		}
		if (element_type == 'radio' || element_type == 'checkbox') {
			html += makeAttrib('checked', formObj.elements['check'].checked == true ? 'true' : '');
		}
		html += " />";
		tinyMCEPopup.execCommand("mceInsertContent", false, html);
	}

	tinyMCE._setEventsEnabled(inst.getBody(), false);
	tinyMCEPopup.close();
}

function cancelAction() {
	tinyMCEPopup.close();
}

// Tiny MCE developers part: I do not know - what for it
/*
function changeAppearance() {
	var formObj = document.forms[0];
	var img = document.getElementById('alignSampleImg');

	if (img) {
		img.align = formObj.align.value;
		img.border = formObj.border.value;
		img.hspace = formObj.hspace.value;
		img.vspace = formObj.vspace.value;
	}
}
*/
// Tiny MCE developers part: I do not know - what for it
/*
function updateStyle() {
	var formObj = document.forms[0];
	var st = tinyMCE.parseStyle(formObj.style.value);

	if (tinyMCE.getParam('inline_styles', false)) {
		st['width'] = formObj.width.value == '' ? '' : formObj.width.value + "px";
		st['height'] = formObj.height.value == '' ? '' : formObj.height.value + "px";
		st['border-width'] = formObj.border.value == '' ? '' : formObj.border.value + "px";
		st['margin-top'] = formObj.vspace.value == '' ? '' : formObj.vspace.value + "px";
		st['margin-bottom'] = formObj.vspace.value == '' ? '' : formObj.vspace.value + "px";
		st['margin-left'] = formObj.hspace.value == '' ? '' : formObj.hspace.value + "px";
		st['margin-right'] = formObj.hspace.value == '' ? '' : formObj.hspace.value + "px";
	} else {
		st['width'] = st['height'] = st['border-width'] = null;

		if (st['margin-top'] == st['margin-bottom'])
			st['margin-top'] = st['margin-bottom'] = null;

		if (st['margin-left'] == st['margin-right'])
			st['margin-left'] = st['margin-right'] = null;
	}

	formObj.style.value = tinyMCE.serializeStyle(st);
}
*/
// Tiny MCE developers part: I do not know - what for it
/*
function getSelectValue(form_obj, field_name) {
	var elm = form_obj.elements[field_name];

	if (elm == null || elm.options == null)
		return "";

	return elm.options[elm.selectedIndex].value;
}
*/

// Custom part for HTML
function checkIt() {
	var obj1 = document.getElementById("check");
	var obj2 = document.getElementById("readonly");
	var obj3 = document.getElementById("size");
	var obj4 = document.getElementById("maxlength");
	var obj5 = document.getElementById("validtype");
	var obj6 = document.getElementById("validparams");
	var obj7 = document.getElementById("required");
	var type = document.getElementById("type").value;
	
	if (type != "checkbox" && type != "radio")
		obj1.disabled = true
	else 
		obj1.disabled = false;
	
	if (type != "text" && type != "password") {
		obj2.disabled = true;
		obj3.disabled = true;
		obj4.disabled = true;
		obj5.disabled = true;
		obj6.disabled = true;
		obj7.disabled = true;
	} else {
		obj2.disabled = false;
		obj3.disabled = false;
		obj4.disabled = false;
		obj5.disabled = false;
		obj6.disabled = false;
		obj7.disabled = false;
	}
	
	// Set default styles
	var formObj = document.forms[0];
	var inst = tinyMCE.getInstanceById(tinyMCE.getWindowArg('editor_id'));
	var elm = inst.getFocusElement();
	if (elm == null || elm.nodeName != 'INPUT') {
		if (type == "submit")
			selectByValue(formObj, 'class_list', 'form_submit');
		else if (type == "checkbox")
			selectByValue(formObj, 'class_list', 'form_checkbox');
		else if (type == "radio")
			selectByValue(formObj, 'class_list', 'form_radio');
		else selectByValue(formObj, 'class_list', '');
	}
}

function showValidateHelp() {
	document.getElementById("help_topic").style.visibility = "visible";
}

function hideValidateHelp() {
	document.getElementById("help_topic").style.visibility = "hidden";
}

function changeHelpType() {
	var obj = document.getElementById("validtype");
	var num = obj.selectedIndex;
	var text = obj.options[num].text;
	var type = obj.value;
	var desc = tinyMCE.getLang('lang_negeso_form_manager_validation_' + type + '_desc', '', true);
	document.getElementById("help_valid_type").innerHTML = text;
	document.getElementById("help_valid_text").innerHTML = desc;
	var param_field = document.getElementById("validparams");
	if (type == "empty" || type == "email") param_field.disabled = true
	else param_field.disabled = false;
}
// While loading
preinit();
