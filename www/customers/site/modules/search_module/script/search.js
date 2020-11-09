/*
  Copyright (c) 2007 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information"). You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.

  Author: oleg Shvedov
  
*/

function search(formObj){

	if(formObj.without.value != '' && formObj.atLeastOne.value == '' && formObj.exactPhrase.value == '' && formObj.allWords.value == ''){
		formObj.allWords.focus();
		alert("The 'Without the words' field should always go together with either \n'With all of the words' or \n'With the exact phrase' or \n'With at least one of the words' field.");
	 }
	 else{
	 	formObj.submit();
	 }
}

function form_min_max(){
	var cur_style = document.getElementById('search_block').style.display;
	var img = '';
	
	if(cur_style == 'block'){
		document.getElementById('search_block').style.display = "none";
		document.getElementById('search_block_control').src = document.getElementById('search_block_control').src.replace(/min/, 'max');
		var new_title = img_text['max'];
		cur_pic = 'max';
	}
	else{
		document.getElementById('search_block').style.display = "block";
		document.getElementById('search_block_control').src = document.getElementById('search_block_control').src.replace(/max/, 'min');
		var new_title = img_text['min'];
		cur_pic = 'min';
	}
	document.getElementById('search_block_control').alt = new_title;
	document.getElementById('search_block_control').title = new_title;
	document.getElementById('search_block_control_text').innerHTML = new_title;

	cur_style = document.getElementById('search_block').style.display;
	
	setCookie("searchForm", cur_style)
	setCookie("searchImg", cur_pic)
}

function search_form_init(){
	var search_form = getCookie("searchForm");
	var search_img = getCookie("searchImg");
	
	if(search_form == '' || search_form == null || search_form == 'undefined')
		search_form = 'block';
		
	if(search_img == '' || search_img== null || search_img== 'undefined')
		search_img = 'min';
	
	document.getElementById('search_block').style.display = search_form;
	document.getElementById('search_block_control').src = '/site/modules/search_module/images/search_'+search_img+'.gif';
	document.getElementById('search_block_control').alt = img_text[search_img];
	document.getElementById('search_block_control').title = img_text[search_img];
	document.getElementById('search_block_control_text').innerHTML = img_text[search_img];
	
};
