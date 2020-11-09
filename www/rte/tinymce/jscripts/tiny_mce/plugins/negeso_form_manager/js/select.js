var SelectDialog = {
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
		if (elm != null && elm.nodeName == "SELECT")
			action = "update";

		formObj.insert.value = inst.getLang('lang_' + action, 'Insert', true); 

		if (action == "update") {
			// Setup form data
			var style = dom.parseStyle(dom.getAttrib(elm, 'style'));
			formObj.name.value       = dom.getAttrib(elm, 'name');
			//formObj.field_id.value   = dom.getAttrib(elm, 'id');
			formObj.title.value      = dom.getAttrib(elm, 'title');
			formObj.size.value       = dom.getAttrib(elm, 'size');
			formObj.tabindex.value   = dom.getAttrib(elm, 'tabindex');
			formObj.style.value      = dom.serializeStyle(style);
			formObj.multiple.checked = ('' + elm.multiple == 'true' ||
										'' + elm.multiple == 'multiple') ? true : false;
			
			for (var i = 0; i < formObj.field_options.options.length; i++) formObj.field_options.options[formObj.field_options.options.length-1] = null;
			formObj.option_id.value = '';
			formObj.option_title.value = '';
			formObj.option_value.value = '';
			formObj.option_text.value = '';
			formObj.option_selected.checked = false;
			
			var len = elm.options.length;
			for (var i = 0; i < len; i++) {
				var content = elm.options[i].text;
				if (typeof(content) == "undefined" || content == null || content == '') content = elm.options[i].getAttribute('inner_text');
				var opt = new Option(content, elm.options[i].value);
				opt.real_selected = elm.options[i].selected;
				opt.id = elm.options[i].id;
				opt.title = elm.options[i].title;
				formObj.field_options.options[formObj.field_options.options.length] = opt;
			}
			if (elm.selectedIndex >= 0) {
				var indx = elm.selectedIndex;
				formObj.field_options.options[indx].selected = true;
				formObj.option_id.value = elm.options[indx].getAttribute('id');
				formObj.option_title.value = elm.options[indx].getAttribute('title');
				formObj.option_value.value = elm.options[indx].getAttribute('value');
				formObj.option_text.value = elm.options[indx].getAttribute('inner_text');
				formObj.option_selected.checked = true;
			};
		}
	}
};
function refresh_option_params() {
	var formObj = document.forms[0];
		
	if (formObj.field_options.selectedIndex == -1)
	{
		formObj.option_id.value = '';
		formObj.option_title.value = '';
		formObj.option_value.value = '';
		formObj.option_text.value = '';
		return;
	}
	
	var elm = formObj.field_options.options[formObj.field_options.selectedIndex];
	formObj.option_id.value = elm.id;
	formObj.option_title.value = elm.title;
	formObj.option_value.value = elm.value;
	formObj.option_text.value = elm.text;
	formObj.option_selected.checked = ('' + elm.real_selected == 'true') ? true : false;
}

function func_update_option_params() {
	var formObj = document.forms[0];
	if (formObj.field_options.selectedIndex == -1)
		return;
	var elm = formObj.field_options.options[formObj.field_options.selectedIndex];
	elm.id = formObj.option_id.value;
	elm.title = formObj.option_title.value;
	elm.value = formObj.option_value.value;
	elm.text = formObj.option_text.value;
	elm.real_selected = formObj.option_selected.checked;
}

function delete_option() {
	var formObj = document.forms[0];
	var num = formObj.field_options.selectedIndex;
	if (num == -1)
		return;
	formObj.field_options.options[num] = null;
	formObj.field_options.selectedIndex = num > formObj.field_options.options.length - 1 ? formObj.field_options.options.length - 1 : num;
	refresh_option_params();
}

function add_option() {
	var formObj = document.forms[0];
	var num = formObj.field_options.options.length;
	formObj.field_options.options[num] = new Option('new'+(num+1),'new'+(num+1));
	formObj.field_options.options[num].selected = true;
	formObj.field_options.selectedIndex = num;
	refresh_option_params();
}

function move_option(mode) {
	var formObj = document.forms[0];
	var num = formObj.field_options.selectedIndex;
	if (num == -1)
		return;	
	var dn = 0;
	if (mode == 'up') {
		if (num == 0)
			return;
		dn = -1;
	}
	if (mode == 'down')  {
		if (num == formObj.field_options.options.length - 1)
			return;
		dn = +1;
	}
	var opt1 = formObj.field_options.options[num];
	var opt2 = formObj.field_options.options[num + dn];
	var id1 = opt1.id;
	var title1 = opt1.title;
	var value1 = opt1.value;
	var text1 = opt1.text;
	var selected1 = opt1.real_selected;
	opt1.id = opt2.id;
	opt1.title = opt2.title;
	opt1.value = opt2.value;
	opt1.text = opt2.text;
	opt1.real_selected = opt2.real_selected;
	opt2.id = id1;
	opt2.title = title1;
	opt2.value = value1;
	opt2.text = text1;
	opt2.real_selected = selected1;
	formObj.field_options.selectedIndex += dn;
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
	
	/*	
	if (elm != null && elm.nodeName.toUpperCase() == "SELECT") {
		setAttrib(elm, 'name');
		setAttrib(elm, 'id', formObj.elements['field_id'].value);
		setAttrib(elm, 'title');
		setAttrib(elm, 'size');
		setAttrib(elm, 'style');
		setAttrib(elm, 'tabindex');
		setAttrib(elm, 'tabIndex',  formObj.elements['tabindex'].value);
		setAttrib(elm, 'multiple', formObj.elements['multiple'].checked == true ? 'true' : '');
		//setAttrib(elm, 'class', getSelectValue(formObj, 'classlist'));
		var len = elm.options.length;
		var n = 0;
		for (var i = 0; i < len; i++) {
			n = elm.options.length - 1;
			elm.options[n] = null;
		}
		var opt, opt_id, opt_title, opt_value, opt_text, opt_selected;
		len = formObj.elements['field_options'].options.length;
		for (var i = 0; i < len; i++) {
			opt_id       = formObj.elements['field_options'].options[i].id;
			opt_title    = formObj.elements['field_options'].options[i].title;
			opt_value    = formObj.elements['field_options'].options[i].value;
			opt_text     = formObj.elements['field_options'].options[i].text;
			opt_selected = formObj.elements['field_options'].options[i].real_selected;
			opt = new Option(opt_text, opt_value);
			opt.id = opt_id;
			opt.title = opt_title;
			opt.value = opt_value;
			opt.selected = opt_selected;
			opt.setAttribute('inner_text', opt_text);
			n = elm.options.length;
			elm.options[n] = opt;
		}		
		// Repaint if dimensions changed
		inst.repaint();
		
		// Refresh in old MSIE
		if (tinyMCE.isMSIE5)
			elm.outerHTML = elm.outerHTML;

	} else {
	*/
	var html = '<select ';
	html += makeAttrib('name');
	//html += makeAttrib('id', formObj.elements['field_id'].value);
	html += makeAttrib('title');
	html += makeAttrib('size');
	html += makeAttrib('style');
	html += makeAttrib('tabindex');
	html += makeAttrib('multiple', formObj.elements['multiple'].checked == true ? 'true' : '');
	if(!tinyMCE.isOpera)	
		html += makeAttrib('class', formObj.elements['class_list'].value);
	html += '>';
	var opt_id = '';
	var opt_title = '';
	var opt_value = '';
	var opt_text = '';
	var opt_selected = '';
	for (var i = 0; i < formObj.elements['field_options'].options.length; i++) {
		html += '<option ';
		opt_id       = formObj.elements['field_options'].options[i].id;
		opt_title    = formObj.elements['field_options'].options[i].title;
		opt_value    = formObj.elements['field_options'].options[i].value;
		opt_text     = formObj.elements['field_options'].options[i].text;
		opt_selected = '' + formObj.elements['field_options'].options[i].real_selected == 'true' ? 'true' : '';
		html += 'id="' + opt_id + '" ';
		html += 'title="' + opt_title + '" ';
		html += 'value="' + opt_value + '" ';
		html += 'inner_text="' + opt_text + '" ';
		if (opt_selected != '') html += 'selected="' + opt_selected + '" ';
		html += '>';
		html += opt_text;
		html += '</option>';
	}
	html += '</select>';
	tinyMCEPopup.execCommand("mceInsertContent", false, html);
	tinyMCEPopup.close();
}

function cancelAction() {
	tinyMCEPopup.close();
}

tinyMCEPopup.onInit.add(SelectDialog.init, SelectDialog);