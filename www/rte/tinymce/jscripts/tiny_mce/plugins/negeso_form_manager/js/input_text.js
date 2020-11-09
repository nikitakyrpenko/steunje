var InputTextDialog = {
	preInit : function() {
		tinyMCEPopup.requireLangPack();
	},

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
				
		// Check action
		if (elm != null && elm.nodeName == 'INPUT' && ('' + dom.getAttrib(elm,'type')) == 'text')
			action = 'update';

		formObj.insert.value = inst.getLang(action);		
		if (action == "update") {
			var style = dom.parseStyle(dom.getAttrib(elm, 'style'));
			formObj.name.value      = dom.getAttrib(elm, 'name');
			//formObj.field_id.value  = dom.getAttrib(elm, 'id');
			formObj.value.value     = dom.getAttrib(elm, 'value');
			formObj.title.value     = dom.getAttrib(elm, 'title');
			formObj.size.value      = dom.getAttrib(elm, 'size');
			formObj.maxlength.value = dom.getAttrib(elm, 'maxlength');
			formObj.tabindex.value  = dom.getAttrib(elm, 'tabindex');
			formObj.style.value      = dom.serializeStyle(style);
			formObj.readonly.checked = ('' + dom.getAttrib(elm, 'readonly') == 'true' ||
										dom.getAttrib(elm, 'readonly') == 'readonly');

			/*
			remark #readonly      begin
			'readonly' flag disable 'validation' tab
			*/
			if (formObj.readonly.checked){
				document.getElementById('validation_tab').style.display = 'none';
			} else{
				document.getElementById('validation_tab').style.display = '';
			}
			/*
			remark #readonly      end
			*/
			
			if (dom.getAttrib(elm, 'is_email') == 'true')
				document.getElementById('email_valid').checked = true;
			else if (dom.getAttrib(elm, 'numeric_field_params') != '')
				document.getElementById('numeric_valid').checked = true;
			else if (dom.getAttrib(elm, 'is_phone') == 'true')
				document.getElementById('phone_valid').checked = true;
			else if (dom.getAttrib(elm, 'timedate_field_format') != '')
				document.getElementById('timedate_valid').checked = true;
			this.setFieldType(formObj.elements["t_d_params"], dom.getAttrib(elm, 'timedate_field_format'));
			formObj.required.checked = ('' + dom.getAttrib(elm, 'is_required') == 'true' || dom.getAttrib(elm, 'is_required') == 'required');
		}
	},
	
	setFieldType : function (selectObj, type) {
		for (var i = 0; i < selectObj.options.length; i++) {
			if (selectObj.options[i].value == type) {
				selectObj.options[i].selected = true;
				break;
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

	if (value != "" || attrib == "value") {
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
	formObj.elements['type'].disabled = false;
		
	if (elm != null && elm.nodeName == "INPUT") {
		setAttrib(elm, 'type');
		setAttrib(elm, 'name');
		//setAttrib(elm, 'id', formObj.elements['field_id'].value);
		setAttrib(elm, 'value');
		setAttrib(elm, 'title');
		setAttrib(elm, 'style');
		setAttrib(elm, 'tabindex');
		setAttrib(elm, 'tabIndex',  formObj.elements['tabindex'].value);

		if(!tinyMCE.isOpera)
			setAttrib(elm, 'class', formObj.elements['class_list'].value);
		//setAttrib(elm, 'class', getSelectValue(formObj, 'classlist'));

		//setAttrib(elm, 'checked');
		setAttrib(elm, 'size');
		setAttrib(elm, 'maxlength');
		setAttrib(elm, 'maxLength',formObj.elements['maxlength'].value);
		setAttrib(elm, 'readonly', formObj.elements['readonly'].checked == true ? 'true' : '');
		setAttrib(elm, 'readOnly', formObj.elements['readonly'].checked == true ? 'true' : '');
		
		elm.removeAttribute('is_email');
		elm.removeAttribute('is_required');
		elm.removeAttribute('numeric_field_params');
		elm.removeAttribute('is_phone');
		elm.removeAttribute('timedate_field_format');
		if (formObj.elements['no_valid'].checked == false) {
			if (formObj.elements['email_valid'].checked == true)
				setAttrib(elm, 'is_email', 'true');
			else if (formObj.elements['numeric_valid'].checked == true)
				setAttrib(elm, 'numeric_field_params', formObj.elements['numeric_validparams'].value);
			else if (formObj.elements['phone_valid'].checked == true)
				setAttrib(elm, 'is_phone', 'true');
			else if (formObj.elements['timedate_valid'].checked == true)
				setAttrib(elm, 'timedate_field_format', formObj.elements["t_d_params"].value);
		}
		
		/*
		remark #insert field              begin
		*/
		if(formObj.elements['readonly'].checked == true)
			setAttrib(elm, 'is_required', '');
		else
			setAttrib(elm, 'is_required', formObj.elements['required'].checked == true ? 'true' : '');
		/*
		remark #insert field              end
		*/

		inst.execCommand('mceRepaint');

		// Refresh in old MSIE
		if (tinyMCE.isMSIE5)
			elm.outerHTML = elm.outerHTML;

	} else {
		var html = "<input ";
		html += makeAttrib('type');
		html += makeAttrib('name');
		//html += makeAttrib('id', formObj.elements['field_id'].value);
		html += makeAttrib('value');
		html += makeAttrib('title');
		html += makeAttrib('style');
		html += makeAttrib('tabindex');
		html += makeAttrib('class', formObj.elements['class_list'].value);
		html += makeAttrib('size');
		html += makeAttrib('maxlength');
		html += makeAttrib('readonly', formObj.elements['readonly'].checked == true ? 'true' : '');
		//html += makeAttrib('is_email', 'true'); break;
		//html += makeAttrib('required', formObj.elements['required'].checked == true ? 'true' : '');
		/*
		if (element_type == 'radio' || element_type == 'checkbox') {
			html += makeAttrib('checked', formObj.elements['check'].checked == true ? 'true' : '');
		}
		*/
		if (formObj.elements['no_valid'].checked == false) {
			if (formObj.elements['email_valid'].checked == true)
				html += makeAttrib('is_email', 'true');
			else if (formObj.elements['numeric_valid'].checked == true)
				html += makeAttrib('numeric_field_params', formObj.elements['numeric_validparams'].value);
			else if (formObj.elements['phone_valid'].checked == true)
				html += makeAttrib('is_phone', 'true');			
			else if (formObj.elements['timedate_valid'].checked == true)
				html += makeAttrib('timedate_field_format', formObj.elements["t_d_params"].value);
		}
		html += makeAttrib('is_required', formObj.elements['required'].checked == true ? 'true' : '');
		
		html += " />";
		tinyMCEPopup.execCommand("mceInsertContent", false, html);
	}

	//tinyMCE._setEventsEnabled(inst.getBody(), false);
	tinyMCEPopup.close();
}

function cancelAction() {
	tinyMCEPopup.close();
}

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
	var inst = tinyMCEPopup.editor;
	var elm = tinyMCEPopup.editor.windowManager.params.selectedNode;
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
	var desc = tinyMCEPopup.editor.getLang('negeso_form_manager.validation_' + type + '_desc', '', true);
	document.getElementById("help_valid_type").innerHTML = text;
	document.getElementById("help_valid_text").innerHTML = desc;
	var param_field = document.getElementById("validparams");
	if (type == "empty" || type == "email") param_field.disabled = true
	else param_field.disabled = false;
}

function change_readonly(obj){

	if(obj.checked){
		document.getElementById('validation_tab').style.display = 'none';
		document.getElementById('no_valid').checked = true;
		document.getElementById('required').checked = false;
	}
	else{
		document.getElementById('validation_tab').style.display = '';
	}
	return true;
}

tinyMCEPopup.onInit.add(InputTextDialog.init, InputTextDialog);