var esl_NAME_MUST_BE_NOT_BLANK = {
    "nl" : "Vul een zoekwoord in",
    "en" : "Name must be not blank"
};

var esl_POSTCODE_VALID = {
    "nl" : "Geen geldige postcode",
    "en" : "Postcode must be valid"
};

function doNameSearch(){
	if (store_locator_form.name.value==null || store_locator_form.name.value==""){
		alert(esl_NAME_MUST_BE_NOT_BLANK[lang]);
	}
	else{
		store_locator_form.submit();
	}
}

function disableAll(){
	store_locator_form.sshop_type.disabled = true;
	store_locator_form.shop_type.value=store_locator_form.sshop_type.value;
	
	if(store_locator_form.slevel1!=null){
		store_locator_form.slevel1.disabled = true;
		store_locator_form.level1.value=store_locator_form.slevel1.value;
	}
	
	if(store_locator_form.slevel2!=null){
		store_locator_form.slevel2.disabled = true;
		store_locator_form.level2.value=store_locator_form.slevel2.value;
	}
	
	if(store_locator_form.slevel3!=null){
		store_locator_form.slevel3.disabled = true;
		store_locator_form.level3.value=store_locator_form.slevel3.value;
	}
}

function shopTypeChanged(){
	if(store_locator_form.slevel1!=null){
		store_locator_form.level1.value = 0;
	}
	if(store_locator_form.slevel2!=null){
		store_locator_form.level2.value = 0;
	}
	if(store_locator_form.slevel3!=null){
		store_locator_form.level3.value = 0;
	}
}

function refreshParameters(){
	store_locator_form.mode.value="detailed_search";
	store_locator_form.submit();
}

function doFind(){
	if(validatePostCode(store_locator_form.postcode.value)){
		disableAll();
		store_locator_form.mode.value="do_detailed_search";
		store_locator_form.submit();
		return true;
	}
	else{
		return false;
	}
}

function validatePostCode(code){
	if(code==null || code==""){
		return true;
	}
	var num = new Number(code);
    if (num.toString()=="" || num.toString()=="NaN" || num.toString()=="0"){
        alert(esl_POSTCODE_VALID[lang]);
        return false;
    }
    if (num.valueOf()<1000 || num.valueOf()>9999){
        alert(esl_POSTCODE_VALID[lang]);
        return false;
    }
    return true;
}

function showShopDetails(sid){
	store_locator_form.shop_id.value=sid;
	store_locator_form.submit();
}