var cb_MUST_FILL = {
    "nl" : "U moet een minstens gebied vullen!\n",
    "en" : "At least one filed should be specified for searching!\n"
};

function reorderBy(orderBy){
	document.forms['cb_contact_form'].elements["order_by"].value = orderBy;
	document.forms['cb_contact_form'].submit();
	return true;
}

function reorder(orderBy, order){
	document.forms['cb_contact_form'].elements["order_by"].value = orderBy;
    document.forms['cb_contact_form'].elements["order"].value = order;
	document.forms['cb_contact_form'].submit();
	return true;
}

function showAll(){
	document.forms['cb_contact_form'].elements["action"].value = "show_all";
	document.forms['cb_contact_form'].submit();
	return true;
}

function searchContacts(){
	document.forms['cb_contact_form'].elements["action"].value = "search";
	document.forms['cb_contact_form'].submit();
	return true;
}

function groupContacts(groupId){
	document.forms['cb_contact_form'].elements["action"].value = "show_group";
	document.forms['cb_contact_form'].elements["groupId"].value = groupId;
	document.forms['cb_contact_form'].submit();
	return true;
}

function showAdvancedSearch(){
	document.forms['cb_contact_form'].elements["action"].value = "advanced_search";
	document.forms['cb_contact_form'].submit();
}

function advancedSearch(){
	if(validate_at_least(document.forms['cb_contact_form'])){
		document.forms['cb_contact_form'].elements["action"].value = "advanced_search";
		document.forms['cb_contact_form'].submit();
	} else {
		alert(cb_MUST_FILL[lang]);
	}
}

function mainPage(){
	document.forms['cb_contact_form'].elements["action"].value = "";
	document.forms['cb_contact_form'].submit();
	return true;
}