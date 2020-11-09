var InputRadiogroupDialog = {
	init : function(ed) {
		tinyMCEPopup.resizeToInnerSize();
		var formObj = document.forms[0];
		var action = "insert";
		// Standart function: adds classes into class-list
		addClassesToList('class_list');
		selectByValue(formObj, 'class_list', 'form_radio');
		if (tinyMCE.isOpera)
			formObj.elements['class_list'].disabled = true;
		formObj.insert.value = tinyMCEPopup.editor.getLang(action, 'Insert', true);
	}
};

function refresh_radiobutton_params() {
	var formObj = document.forms[0];
		
	if (formObj.field_options.selectedIndex == -1)
	{
		//formObj.option_id.value = '';
		//formObj.option_title.value = '';
		formObj.option_value.value = '';
		formObj.option_text.value = '';
		return;
	}
	
	var elm = formObj.field_options.options[formObj.field_options.selectedIndex];
	//formObj.option_id.value = elm.id;
	//formObj.option_title.value = elm.title;
	formObj.option_value.value = elm.value;
	formObj.option_text.value = elm.text;
	formObj.option_checked.checked = ('' + elm.real_selected == 'true') ? true : false;
}

function update_radiobutton_params() {
	var formObj = document.forms[0];
	if (formObj.field_options.selectedIndex == -1)
		return;
	var elm = formObj.field_options.options[formObj.field_options.selectedIndex];
	//elm.id = formObj.option_id.value;
	//elm.title = formObj.option_title.value;
	elm.value = formObj.option_value.value;
	elm.text = formObj.option_text.value;
	elm.real_selected = formObj.option_checked.checked;
}

function delete_radiobutton() {
	var formObj = document.forms[0];
	var num = formObj.field_options.selectedIndex;
	if (num == -1)
		return;
	formObj.field_options.options[num] = null;
	formObj.field_options.selectedIndex = num > formObj.field_options.options.length - 1 ? formObj.field_options.options.length - 1 : num;
	refresh_radiobutton_params();
}

function add_radiobutton() {
	var formObj = document.forms[0];
	var num = formObj.field_options.options.length;
	formObj.field_options.options[num] = new Option('new'+(num+1),'new'+(num+1));
	formObj.field_options.options[num].selected = true;
	formObj.field_options.selectedIndex = num;
	refresh_radiobutton_params();
}

function move_radiobutton(mode) {
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

	var formObj = document.forms[0];
	var html = '';
	//var opt_id = '';
	//var opt_title = '';
	var opt_value = '';
	var opt_text = '';
	var opt_selected = '';
	var t_name = makeAttrib('name');
	var t_title = makeAttrib('title');
	var t_style = makeAttrib('style');
	var t_tab_index = makeAttrib('tabindex');
	var t_class = makeAttrib('class', formObj.elements['class_list'].value);
	var isThereSelected = false;

	for (var i = 0; i < formObj.elements['field_options'].options.length; i++) {
	
		html += '<input type="radio"';
		//html += makeAttrib('id', formObj.elements['field_id'].value);
		html += t_name;
		html += t_title;
		html += t_style;
		html += t_tab_index;
		if(!tinyMCE.isOpera)	
			html += t_class;
		//opt_id       = formObj.elements['field_options'].options[i].id;
		//opt_title    = formObj.elements['field_options'].options[i].title;
		opt_value    = formObj.elements['field_options'].options[i].value;
		opt_text     = formObj.elements['field_options'].options[i].text;
		opt_selected = '' + formObj.elements['field_options'].options[i].real_selected == 'true' ? 'true' : '';
		
		if(i==0 && opt_selected==''){
			for(var j = 1;j < formObj.elements['field_options'].options.length; j++)
			{
				if(formObj.elements['field_options'].options[j].real_selected == 'true'){
					isThereSelected = true;
					break;
				}
			}
		}
		
		//html += makeAttrib('id',opt_id);
		//html += makeAttrib('title',opt_title);
		html += makeAttrib('value',opt_value);
		if (opt_selected != '')
			html += makeAttrib('checked',opt_selected);
			
		if(i==0 && isThereSelected==false)
		{
			html += makeAttrib('checked','true');
		}
		
		html += ' />';
		html += opt_text;
		html += '<br />';
	}

	tinyMCEPopup.execCommand("mceInsertContent", false, html);
	//tinyMCE.execCommand('mceCleanup');
	////tinyMCE._setEventsEnabled(inst.getBody(), false);
	tinyMCEPopup.close();
}

function cancelAction() {
	tinyMCEPopup.close();
}

tinyMCEPopup.onInit.add(InputRadiogroupDialog.init, InputRadiogroupDialog);