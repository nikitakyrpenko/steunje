var pm_passwords_not_equal = {
    "nl" : "De wachtwoorden zijn niet gelijk",
    "en" : "Passwords are not equal"
};

var agree_required = {
    "nl" : "Algemene voorwaarden zijn niet aangevinkt.",
    "en" : "Please agree the service conditions."
};

function findParentForm(obj) {
    while (obj && (obj != document)) {
        if (obj.tagName.toLowerCase() == "form") return obj;
        obj = (obj.parentElement) ? obj.parentElement : obj.parentNode;
    }
    return false;
}

function onAccountUpdate(form){
	if ( form.p.value != form.pret.value){
		alert(pm_passwords_not_equal[lang]);
		return false;
	}

    if ( form.p.value != ""){
		form.customer_password.value=hex_md5(form.p.value); 
	}       
	else{                                        
		form.customer_password.value="";
	}
	form.p.value='';
	form.pret.value='';
	return true;
}

function disableDiv(trigger_checkbox){
	if ( trigger_checkbox.checked ){
		document.getElementById("billing_address_div").style.display = "none";
	}
	else{
		document.getElementById("billing_address_div").style.display = "inline";
	}
}

function onCustomerRegisterSubmit(form){
	if ( form.p.value != form.pret.value){
		alert(pm_passwords_not_equal[lang]);
		return false;
	}
	
	if ( (form.customer_login.value == "") || (form.customer_email.value == "") || (form.p.value == "") ){
		alert(enter_required[lang]);
		return false;
	}
	
	form.customer_password.value=hex_md5(form.p.value); 
	form.p.value='';
	form.pret.value='';
	return true;
}

function doPrevious(clicked_element){
	var form = findParentForm(clicked_element);
	form.wizard_action.value = "prev";
	form.submit();
	return true;
}

function doNext(clicked_element){
	var form = findParentForm(clicked_element);
	
	var agreeConditions = document.getElementById('agreed');

	if (agreeConditions == null || 
	    agreeConditions != null && form.agreeConditions.checked)   {
		form.wizard_action.value = "next";
		form.submit();
		return true;
	} else {
		alert(agree_required[lang]);
		form.agreeConditions.focus();
		return false;
	}
}

function doFinish(clicked_element){
	var form = findParentForm(clicked_element);
	if(form.agreeConditions.checked) {
	form.wizard_action.value = "finish";
	form.submit();
	return true;
	} else {
		alert(agree_required[lang]);
		form.agreeConditions.focus();
		return false;
	}
}

function enableChoosingBank() {
	var bankChooser = document.getElementById('bankChooser');
	var radio = document.getElementById('iDeal');
	if (radio != null) {
		if (radio.checked) {
			bankChooser.disabled = false;
		} else {
			bankChooser.disabled = true;
		}
	}
}



