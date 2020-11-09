function getSLChosenSelection(){
    var elts = document.getElementsByName('sl_chosen');
    if (elts[0].checked==true)
        return 1;
    else
        return 2;
}

function enableDisable(){
    sl_from.style.backgroundColor="white";
    //sl_to.style.backgroundColor="white";
    sl_city.style.backgroundColor="white";
    if (getSLChosenSelection()==1){
        sl_city.disabled=false;
        sltd.disabled=true;
        sl_from.disabled=true;
        //sl_to.disabled=true;
    }
    else{
        sl_city.disabled=true;
        sltd.disabled=false;
        sl_from.disabled=false;
        //sl_to.disabled=false;
    } 
}

function slFind(){
    if (getSLChosenSelection()==1){
        if (checkCity()){
            //valid
            sl_form.todo.value="show";
            sl_form.sl_criteria.value="city";
            sl_form.sl_chosen_1.value=sl_city.value;
            sl_form.sl_chosen_2.value="";
            sl_form.sl_extra.value="";
            sl_form.submit();
        }
    }
    else{
        if (checkPostCode()){
            //valid
            sl_form.todo.value="show";
            sl_form.sl_criteria.value="postcode";
            sl_form.sl_chosen_1.value=sl_from.value;
            sl_form.sl_chosen_2.value="";
            sl_form.sl_extra.value="";
            sl_form.submit();
        }
    }
}

function checkCity(){
    sl_city.style.backgroundColor="white";
    if (sl_city.value==null || sl_city.value==""){
        sl_city.style.backgroundColor="red";
        alert("The city was not chosen!");
        return false;
    }
    return true;
}

function checkPostCode(){
    sl_from.style.backgroundColor="white";
    //sl_to.style.backgroundColor="white";
    var fromZip = validateZip(sl_from);
    //var toZip = validateZip(sl_to);
    if (fromZip==-1 /*|| toZip==-1*/){
        if (fromZip==-1)
            sl_from.style.backgroundColor="red";
        //if (toZip==-1)
        //    sl_to.style.backgroundColor="red";
        alert("Postcode is not valid!");
        return false;
    }
    /*if (fromZip > toZip){
        sl_from.style.backgroundColor="red";
        sl_to.style.backgroundColor="red";
        alert("Postcode range is not valid!");
        return false;
    }*/
    return true;
}

function validateZip(elt){
    var num = new Number(elt.value);
    if (num.toString()=="" || num.toString()=="NaN" || num.toString()=="0"){
        return -1;
    }
    if (num.valueOf()<=999){
        return -1;
    }
    return num.valueOf();
}

function slBack1(){
    sl_form.todo.value="";
    sl_form.sl_criteria.value="";
    sl_form.sl_chosen_1.value="";
    sl_form.sl_chosen_2.value="";
    sl_form.sl_extra.value="";
    sl_form.submit();
}

function slBack2(){
    sl_form.todo.value="show";
    sl_form.sl_extra.value="";
    sl_form.submit();
}

function slDetail(shopId){
    sl_form.todo.value="detail";
    //sl_form.sl_criteria.value="";
    //sl_form.sl_chosen_1.value="";
    //sl_form.sl_chosen_2.value="";
    sl_form.sl_extra.value=shopId;
    sl_form.submit();
}