var jm_MUST_FILL_REQUIRED = {
    "nl" : "U moet alle verplichte velden invoeren!\n",
    "en" : "You must fill all obligatory fields!\n"
};

var jm_NUMBER_NOT_VALID = {
    "nl" : "Voer een juist telefoonnummer in!\n",
    "en" : "Number is not valid!\n"
};

var  jm_EMAIL_NOT_VALID = {
    "nl" : "Voer een juist emailadres in!\n",
    "en" : "Email is not valid!\n"
};

var jm_DATE_NOT_VALID = {
    "nl" : "Voer een correcte datum in!\n",
    "en" : "Date is not valid!\n"
};

var jm_CHOOSE_DEPARTMENT = {
    "nl" : "U moet minstens een departement kiezen\n",
    "en" : "You must choose at least one department\n"
};

var jm_ENTER_WORD_SALARY = {
    "nl" : "Vul een zoekwoord in\n",
    "en" : "You must enter a search word\n"
};

var err_mess = "";
var rt = new Object();

function department_vacancies_submit(id){
	document.forms['job_form'].elements['mode'].value = "department_vacancies";
	document.forms['job_form'].elements['dep_id'].value = id;
	document.forms['job_form'].submit();
}

function backToGeneral(){
	document.forms['job_form'].elements['mode'].value = "general";
	document.forms['job_form'].submit();
}

function postDepApplication(department_id, vacancy_id){
	document.forms['job_form'].elements['mode'].value = "post_application";
	document.forms['job_form'].elements['dep_id'].value = department_id;
	document.forms['job_form'].elements['vac_id'].value = vacancy_id;
	document.forms['job_form'].submit();
}

function postDepApplicationBack(department_id){
	document.forms['job_form'].elements['mode'].value = "department_vacancies";
	document.forms['job_form'].elements['dep_id'].value = department_id;
	document.forms['job_form'].submit();
}

function sendApplication(department_id, vacancy_id){
	rt.req = true;
	rt.num = true;
	rt.email = true;
	rt.date = true;
	rt.req = validate(document.forms['job_form']);
    err = createErrorMessage();
	if (err){
	    document.forms['job_form'].elements['mode'].value = "save_application";
	    document.forms['job_form'].elements['dep_id'].value = department_id;
	    document.forms['job_form'].elements['vac_id'].value = vacancy_id;
	    document.forms['job_form'].submit();
	}
	else{
	    alert(err_mess);
	    return false;
	}
}

function createErrorMessage(){
	err_mess = "";
	if (rt.req == false){
	    err_mess += jm_MUST_FILL_REQUIRED[lang];
	}
	if (rt.num == false){
	    err_mess += jm_NUMBER_NOT_VALID[lang];
	}
	if (rt.email == false){
	    err_mess += jm_EMAIL_NOT_VALID[lang];
	}
	if (rt.date == false){
	    err_mess += jm_DATE_NOT_VALID[lang];
	}
	
	if (err_mess == ""){
	    return true;
	}
	else{
	    return false;
	}
}

function showAllVacs(){
	document.forms['job_form'].elements['mode'].value = "show_all_vacancies";
	document.forms['job_form'].submit();
}

function backToGeneral(){
	document.forms['job_form'].elements['mode'].value = "general";
	document.forms['job_form'].submit();
}

function vacancyDetailedView(vac_id, dep_id){
	document.forms['job_form'].elements['mode'].value = "vacancy_details";
	document.forms['job_form'].elements['dep_id'].value = dep_id;
	document.forms['job_form'].elements['vac_id'].value = vac_id;
	document.forms['job_form'].submit();
}

function postVacApplication(dep_id, vac_id){
	document.forms['job_form'].elements['mode'].value = "post_vac_application";
	document.forms['job_form'].elements['dep_id'].value = dep_id;
	document.forms['job_form'].elements['vac_id'].value = vac_id;
	document.forms['job_form'].submit();
}

function postVacApplicationBack(dep_id, vac_id){
	document.forms['job_form'].elements['mode'].value = "vacancy_details";
	document.forms['job_form'].elements['dep_id'].value = dep_id;
	document.forms['job_form'].elements['vac_id'].value = vac_id;
	document.forms['job_form'].submit();
}

function saveVacApplication(dep_id, vac_id){
	rt.req = true;
	rt.num = true;
	rt.email = true;
	rt.date = true;
	rt.req = validate(document.forms['job_form']);
    err = createErrorMessage();
	if (err){
	    document.forms['job_form'].elements['mode'].value = "save_vac_application";
	    document.forms['job_form'].elements['dep_id'].value = dep_id;
	    document.forms['job_form'].elements['vac_id'].value = vac_id;
	    document.forms['job_form'].submit();
	}
	else{
	    alert(err_mess);
	    return false;
	}

}

function postGeneralApplication(){
	document.forms['job_form'].elements['mode'].value = "post_gen_application";
	document.forms['job_form'].submit();
}

function postGeneralForm(vac_id){

	if (validate_at_least(document.getElementById('choose_dep_td'))){
	    rt.req = true;
	    rt.num = true;
	    rt.email = true;
	    rt.date = true;
	    rt.req = validate(document.forms['job_form']);
	    err = createErrorMessage();
	    if (err){
	        document.forms['job_form'].elements['mode'].value = "save_gen_application";
	        document.forms['job_form'].elements['vac_id'].value = vac_id;
	        try{
		        document.forms['job_form'].submit();
		    }
		    catch(e){
		    	alert('ERROR: Cannot find file!');
		    }
	    }
	    else{
	        alert(err_mess);
	        return false;
	    }
	}
	else{
		alert(jm_CHOOSE_DEPARTMENT[lang]);
		return false;
	}
}

function goToSearch(){
	document.forms['job_form'].elements['mode'].value = "search";
	document.forms['job_form'].submit();
}

function doSearch(){
	if (validate(document.forms['job_form'])){
	    document.forms['job_form'].elements['mode'].value = "do_search";
	    document.forms['job_form'].submit();
	}
	else
		alert(jm_ENTER_WORD_SALARY[lang]);
}

function searchDetails(vac_id){
	document.forms['job_form'].elements['mode'].value = "search_result";
	document.forms['job_form'].elements['vac_id'].value = vac_id;
	document.forms['job_form'].submit();
}

function goToDoSearch(){
	document.forms['job_form'].elements['mode'].value = "do_search";
	document.forms['job_form'].submit();
}

function postSearchResult(vac_id){
	document.forms['job_form'].elements['mode'].value = "post_search_result";
	document.forms['job_form'].elements['vac_id'].value = vac_id;
	document.forms['job_form'].submit();
}

function goToSearchResult(vac_id){
	document.forms['job_form'].elements['mode'].value = "search_result";
	document.forms['job_form'].elements['vac_id'].value = vac_id;
	document.forms['job_form'].submit();
}

function saveSearchApplication(vac_id, dep_id){
	rt.req = validate(document.forms['job_form']);
    err = createErrorMessage();
	if(err){
		document.forms['job_form'].elements['mode'].value = "save_search_result";
		document.forms['job_form'].elements['dep_id'].value = dep_id;
		document.forms['job_form'].elements['vac_id'].value = vac_id;
		document.forms['job_form'].submit();
	}
	else
		alert(err_mess)
}