/* 
 * @(#)$Id: common_functions.js,v 1.28, 2007-03-15 19:26:37Z, Rostislav Brizgunov$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 * 
 * A collection of utility methods that are extensively used in admin-mode.
 * 
 * @author		Stanislav Demchenko
 * @author		Olexiy Strashko
 * @author		Sergey Oleynik
 * @author		Volodymyr Snigur
 * @version		$Revision: 29$
 */

/* Set background height and login form position */
function setLoginFormPosition() {
    $('.admBackLogo').css('height', $(window).height() + 'px');
    $('.admBackLogoDiv').css('height', $(document).height() + 'px');
}

$(document).ready(function () {
    setLoginFormPosition();


    /*$(".loupeImg").click(function(event){
        event.preventDefault();
        var img = $(this).siblings('img');
        var src = img.attr('src');
        $(".webshop-products-details").append("<div class='popup'>"+
            "<div class='popup_bg'></div>"+
            "<div id='close-img' class='popup_img'><img src='"+src+"' class='' /></div>"+
            "</div>");
        $(".popup").fadeIn(1);
        $(".popup").click(function(){
            $(".popup").fadeOut(1);
            setTimeout(function() {
                $(".popup").remove();
            }, 1);
        });
        var element_height = ($(window).height() - $('.popup_img').height()) / 2;
        var dif_height = window.pageYOffset + element_height;
        $('.popup_img').css('top', dif_height);
    });*/

    $(".webshop-products-details .left-side").click(function(){
        var img = $(this).find('img');
        var src = img.attr('src');
        $(".webshop-products-details").append("<div class='popup'>"+
            "<div id='close-img' class='popup_img'><img src='"+src+"' class='' /></div>"+
            "</div>");
        $(".popup").fadeIn(1);
        $(".popup").click(function(){
            $(".popup").fadeOut(1);
            setTimeout(function() {
                $(".popup").remove();
            }, 1);
        });
      /*  var element_height = ($(window).height() - $('.popup_img').height()) / 2;
        var dif_height = window.pageYOffset + element_height;*/
        $('.popup_img').css('top', 0);
    });
});

function imgError(image) {
    image.onerror = "";
    var newImage = image.src.replace('.png', '.jpg');
    var jqxhr = $.get(newImage)
        .success(function() {
            image.src = newImage
        })
        .error(function() {
            image.src = "/media/cap.jpg";
        });
    return true;
}



$(window).resize(function () { setLoginFormPosition() });

/* Set cufon styles */
window.onload = function () {
    if( typeof Cufon != "undefined" ){
        Cufon.now();
        Cufon.replace('.admNavPanelFont', { fontFamily: 'Orator Std' });
        Cufon.replace('.btext', { fontFamily: 'Orator Std' });

    }
}

/* Help links */
function getHelpLink(defaultLink) {
    var str = document.URL;
    str = str.replace('http://', '');
    str = str.replace(location.host, '');
    $.ajax({
        url: "/admin/getHelpLink?moduleUrl=" + str + '&defaultLink=' + defaultLink,
        dataType: "html",
        data: "moduleUrl1=" + str,
        success: function (data) {
            window.open(data);
        }
    });
}

$(document).ready(function () {
    $('.callHelp').click(function () {
        getHelpLink($(this).attr('href'));
        return false;
    });
});


/**
 * String utility. Its methods can be called this way:
 * <code>
 *  var filenameChars = StringUtil.getReliableCharacters();
 * </code>
 */
var StringUtil = new function(){

    /**
     * Character, which file names typically consist of:
     * small and capital Enlish letter, digits, and underscore ("_")
     */
    this.getSafeCharacters = function(){
        return "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_0123456789~!@#$%^&?()";
    }

    /** Formats values 1...9 as '01' ...'09'. Other values return unchanged. */
    this.prependZero = function(num){
        return num > 9 ? num : "0" + num;
    }


    /**
     * Checks that stringToCheck consists only of characters which are in
     * present in setOfCharacters. If stringToCheck contains characters that are not
     * in setOfCharacters, returns false. If any of the parameters is null,
     * returns false.
     */
    this.isTokenValid = function(stringToCheck, setOfCharacters)
    {
        stringToCheck = "" + stringToCheck;
        setOfCharacters = "" + setOfCharacters;
        if(stringToCheck == "" || setOfCharacters == ""){
            return false;
        }
        var len = stringToCheck.length;
        for(var i=0; i < len; i++){
            if(setOfCharacters.indexOf( stringToCheck.charAt(i)) == -1 ){
                return false;
            }
        }
        return true;
    }

    /******************************************************************************
     DESCRIPTION: Validates that a string contains only valid numbers.
     PARAMETERS:
     strValue - String to be tested for validity
     RETURNS:
     True if valid, otherwise false.
     ******************************************************************************/
    this.validateNumeric = function( strValue ) {
        var objRegExp  =  /(^-?\d\d*\.\d*$)|(^-?\d\d*$)|(^-?\.\d\d*$)/;

        //check for numeric characters
        return objRegExp.test(strValue);
    }


    /************************************************
     DESCRIPTION: Validates that a string is not all
     blank (whitespace) characters.
     PARAMETERS:
     strValue - String to be tested for validity

     RETURNS:
     True if valid, otherwise false.
     *************************************************/
    this.validateNotEmpty = function( strValue ) {
        var strTemp = strValue;
        strTemp = this.trimAll(strTemp);
        if(strTemp.length > 0){
            return true;
        }
        return false;
    }

    /************************************************
     DESCRIPTION: Validates that a string contains only
     valid integer number.

     PARAMETERS:
     strValue - String to be tested for validity

     RETURNS:
     True if valid, otherwise false.
     ******************************************************************************/
    this.validateInteger = function( strValue ) {
        var objRegExp  = /(^-?\d\d*$)/;
        //check for integer characters
        return objRegExp.test(strValue);
    }

    /************************************************
     DESCRIPTION: Removes leading and trailing spaces.

     PARAMETERS: Source string from which spaces will
     be removed;

     RETURNS: Source string with whitespaces removed.
     *************************************************/
    this.trimAll = function ( strValue ) {
        var objRegExp = /^(\s*)$/;

        //check for all spaces
        if(objRegExp.test(strValue)) {
            strValue = strValue.replace(objRegExp, '');
            if( strValue.length == 0)
                return strValue;
        }

        //check for leading & trailing spaces
        objRegExp = /^(\s*)([\W\w]*)(\b\s*$)/;
        if(objRegExp.test(strValue)) {
            //remove leading and trailing whitespace characters
            strValue = strValue.replace(objRegExp, '$2');
        }
        return strValue;
    }

    this.validateDate = function(val){
        if( /^(\d{1,4})\-(\d{1,2})\-(\d{1,2})$/.test(val) ){
            year = parseInt(RegExp.$1,10);
            if( year<100 ) year = ( year < 70 ) ? 2000 + year : 1900 + year;
            month = RegExp.$2-1;
            date = parseInt(RegExp.$3,10);
            d1 = new Date(year, month, date);
            newYear = d1.getYear();
            if( newYear < 100 ) newYear = ( newYear < 70 ) ? 2000 + newYear : 1900 + newYear;
            if( month == d1.getMonth() && date == d1.getDate() && year == newYear){
                month ++;
                return (date < 10? '0' + date : '' + date) + (month < 10 ? '.0' + month : '.' + month) + '.' + newYear;
            }
        }
        //return false
    }


    this.validateEmail = function(checkThisEmail){
        var myEMailIsValid = true;
        var myAtSymbolAt = checkThisEmail.indexOf('@');
        var myLastDotAt = checkThisEmail.lastIndexOf('.');
        var mySpaceAt = checkThisEmail.indexOf(' ');
        var myLength = checkThisEmail.length;

        // at least one @ must be present and not before position 2
        // @yellow.com : NOT valid
        // x@yellow.com : VALID

        if (myAtSymbolAt < 1 ) myEMailIsValid = false;

        // at least one . (dot) afer the @ is required
        // x@yellow : NOT valid
        // x.y@yellow : NOT valid
        // x@yellow.org : VALID

        if (myLastDotAt < myAtSymbolAt) myEMailIsValid = false;

        // at least two characters [com, uk, fr, ...] must occur after the last . (dot)
        // x.y@yellow. : NOT valid
        // x.y@yellow.a : NOT valid
        // x.y@yellow.ca : VALID

        if (myLength - myLastDotAt <= 2) myEMailIsValid = false;


        // no empty space " " is permitted (one may trim the email)
        // x.y@yell ow.com : NOT valid

        if (mySpaceAt != -1) myEMailIsValid = false;

        return myEMailIsValid
    }


    // get now date
    this.now = function(){
        d = new Date();
        mon = 0;
        day = 0;
        if (d.getMonth() < 10){
            mon = "0" + (d.getMonth() + 1);
        }
        else{
            mon = "" + (d.getMonth() + 1);
        }
        if (d.getDate() < 10){
            day = "0" + d.getDate();
        }
        else{
            day = "" + d.getDate();
        }

        return d.getYear() + "-" + mon + "-" + day;
    }


    /**
     * Encodes an arbitrary string into XML which can be passed as a parameter
     * in CallObject to ClientManager.
     */
    this.str2xml = function(stringToEncode) {
        if (typeof(this.XmlHelper) != "object") {
            try {
                // MSDN says, this way we call the most current XSLT processor
                this.XmlHelper = new ActiveXObject("MSXML2.DOMDocument");
            } catch (e) {
                alert("MSXML is not present");
                return null;
            }
        }
        return this.XmlHelper.createTextNode(stringToEncode).xml;
    }


    /**
     * Creates XML tree from a string.
     *
     * Example 1:
     * ...
     * var result = ClientManager.CMSUpdateEntity(callObj);
     * if(!result.error) {
     *      var tree = StringUtil.asTree(result.value);
     *      var resName = tree.selectSingleNode("/negeso:result-name").text;
     *      ...
     * }
     *
     * Test:
     * alert( StringUtil.asTree("<test>hello</test>").selectSingleNode("/test").text );
     */
    this.asTree = function(serializedXml) {
        try {
            var doc = new ActiveXObject("MSXML2.DOMDocument");
            doc.loadXML(serializedXml);
            return doc;
        } catch (e) {
            alert("MSXML is not present");
            return null;
        }
    }


}

/**  Validate form. Special inut attrributes:
 *
 *
 **/
function validateForm(formObject)
{
    var fields;
    var validated = true;
    var errorText = "Please, fill required fields:";

    var elementTypes = new Array("INPUT", "TEXTAREA");
    var error_fields = new Array();

    for ( var j = 0; j < elementTypes.length; j++ ) {

        fields = formObject.elements;

        for ( var i = 0; i < fields.length; i++) {
            // run through all INPUT and TEXTAREA fields
            if (!fields[i].tagName || fields[i].tagName != elementTypes[j])
                continue;

            var is_validated = 1;

            //alert(StringUtil.validateNotEmpty(fields[i].value)+'\n'+fields[i].value+'\n'+fields[i].tagName+'\n'+fields[i].name);
            if (!StringUtil.validateNotEmpty(fields[i].value)) {
                // validate required field
                if (fields[i].getAttribute("required") == "true" || fields[i].getAttribute("required") == "required") {
                    errorText += "\n" + fields[i].uname + " : is blank";
                    error_fields.push(new Array(fields[i], RequiredError1));
                    is_validated = 0;
                } else continue;
            }

            if ((is_validated == 1) && ((fields[i].getAttribute && fields[i].getAttribute("data_type")) || (fields[i].data_type))) {
                // validate data type
                var d_type = fields[i].getAttribute ? fields[i].getAttribute("data_type") : fields[i].data_type ? fields[i].data_type : "n/a";
                if ((d_type == "number") && StringUtil.validateInteger(fields[i].value) == false){
                    is_validated = 0;
                    errorText += "\n" + fields[i].uname + " : wrong number value";
                    error_fields.push(new Array(fields[i], NumericError1));
                }

                if ((d_type == "currency") && StringUtil.validateNumeric(fields[i].value) == false){
                    is_validated = 0;
                    errorText += "\n" + fields[i].uname + " : wrong currency value";
                    error_fields.push(new Array(fields[i], CurrencyError1));
                }

                if ((d_type == "date") && StringUtil.validateDate(fields[i].value) == false){
                    is_validated = 0;
                    errorText += "\n" + fields[i].uname + " : wrong date value";
                    error_fields.push(new Array(fields[i], TimeDateError1));
                }

                if ((d_type == "email") && isEmailAddr(fields[i].value) == false){
                    is_validated = 0;
                    errorText += "\n" + fields[i].uname + " : wrong email value";
                    error_fields.push(new Array(fields[i], EmailError1));
                }

            }

            if (is_validated == 0) {
                /*fields[i].runtimeStyle.backgroundColor = "C3332C";
                fields[i].runtimeStyle.color = "ffffff";*/
                validated = false;
            } else {
                //required field is ok
                /*fields[i].runtimeStyle.backgroundColor = "";
                fields[i].runtimeStyle.color = "";*/
            }

        }
    }

    if (!validated) {

        //alert(errorText);
        error_fields[0][0].focus();

        var divs = document.getElementsByTagName("div");
        for (var i = 0; i<divs.length; i++) {
            if (divs[i].id.indexOf("not_valid") == 0) {
                divs[i].style.display = "none";
            }
        }

        for (var i = 0; i<error_fields.length; i++) {
            var obj = error_fields[i][0];
            var msg = error_fields[i][1];
            try {
                var el = document.createElement("div");
                var tid = "not_valid" + new Date().getTime();
                try{
                    el.setAttribute("id", tid);
                }catch(e){
                    try {
                        el.id = tid;
                    } catch(e) {}
                }
                el.innerHTML = "<table><tr><td style='font-size: 10px; color: red;'>" + msg + "</td></tr></table>";
                if (obj.nextSibling)
                    obj.parentNode.insertBefore(el,obj.nextSibling);
                else
                    obj.parentNode.appendChild(el);
            } catch(e) {}
        }

        return false;
    }
    return true;
}


/**
 * Displayes properties of an object in a convenient way.
 * Properties are displayed in a "prompt" box, where they can be copied and
 * pasted, say, in Notepad.
 *
 * @param htmlElement   JavaScript object (typically HTML DOM element),
 *                      whose properties (width, innerHTML etc)
 *                      you want to explore.
 */
function exploreProperties(obj)
{
    var arr = new Array();
    var i = 0;
    var prop = null;
    for(prop in obj){
        arr[i++] = prop;
    }
    arr = arr.sort();
    var str = "";
    for(var j = 0; j < arr.length; j++){
        prop = arr[j];
        str += prop + " = " + obj[prop ] + "\n";
    }
    prompt("", str);
}

/**
 * getCookie
 * Gets the cookie value
 * @param name the cookie name
 */
function getCookie(name) {
    var cookie = document.cookie;
    var index = cookie.indexOf(name + "=");
    if (index == -1) return null;
    index = cookie.indexOf("=", index) + 1; // first character
    var endstr = cookie.indexOf(";", index);
    if (endstr == -1) endstr = cookie.length; // last character
    return unescape(cookie.substring(index, endstr));
}

/**
 * setCookie
 * Sets the cookie value
 * @param name the cookie name
 * @param value the cookie value
 */
function setCookie(name, value) {
    var today = new Date();
    var expiry = new Date(today.getTime() + 60*60*24*365 * 1000); // plus 1 year
    var cookie = document.cookie;
    document.cookie = name + "=" + escape(value) + "; expires=" + expiry.toGMTString();
}

/**
 * Returns language of interface
 */
function getInterfaceLanguage() {
    var interfaceLanguage = getCookie("interface_language");
    if (interfaceLanguage == null || interfaceLanguage  == '')
        interfaceLanguage = "en";
    return interfaceLanguage;
}

/**
 * Resize the webpages dialog window on onload
 * For using in dialogs print
 *  attachEvent ("onload", resizeDialogWindow);
 * until <BODY> tag will start
 */

function resizeDialogWindow ()
{
    var clientH = document.body.clientHeight;
    var offsetH = document.body.offsetHeight;
    var plusHeight = clientH - offsetH;
    if (offsetH>=screen.availHeight)
    {
        var openH = screen.availHeight - 10;
        parent.dialogHeight = openH + "px";
    }
    else
    {
        var openH = offsetH + plusHeight;
        openH += 85;//+ title, statusbar height
        parent.dialogHeight = openH + "px";
    }
}

//Makes all form fields in current object readonly/disabled
function makeReadonly( obj, readonly)
{
    if (obj.nodeType != 1)
        return;

    if ((obj.tagName.toUpperCase() == "INPUT" && (obj.type.toLowerCase() == "text" || obj.type.toLowerCase() == "password" || obj.type.toLowerCase() == "checkbox" || obj.type.toLowerCase() == "file" || obj.type.toLowerCase() == "submit" || obj.type.toLowerCase() == "radio" || obj.type.toLowerCase() == "reset"))
        || obj.tagName.toUpperCase() == "SELECT" || obj.tagName.toUpperCase() == "TEXTAREA")
    {
        obj.disabled = readonly;
    }
    for (var i=0; i<obj.childNodes.length; i++ )
    {
        makeReadonly(obj.childNodes[i], readonly );
    }
}

function positionAllRTE()
{
    var divRTE = document.getElementsByTagName('div');
    for(var i =0; i<divRTE.length; i++)    {
        if(divRTE[i].getAttribute('rte')){
            positionRTE(divRTE[i],divRTE[i].id.replace(/\D?/gi,""));
        }
    }
}

function positionRTE(obj,objId)
{
    objTd=document.getElementById("article_text_td"+objId);
    obj.style.top=objTd.offsetTop+'px';
    obj.style.left=objTd.parentElement.parentElement.parentElement.parentElement.offsetLeft+168+'px';
    objTd.style.height=obj.offsetHeight+5+'px';
}

function savePrices(){
    if (!validatePrice(main_form, true)){
        alert(validPriceAlert);
    }
    else{
        main_form.todo.value="save_label_prices";
        main_form.submit();
    }
}

function validatePrice(elt, ret){
    if (elt.tagName=="INPUT" && elt.def=="price"){
        if (priceIsValid(elt)){
            elt.style.backgroundColor = "white";
        }
        else{
            ret = false;
            elt.style.backgroundColor = "red";
        }
    }
    for (var i=0; i<elt.childNodes.length; i++){
        ret = validatePrice(elt.childNodes(i), ret);
    }
    return ret;
}

function priceIsValid(elt){
    var num = new Number(elt.value);
    if (num.toString()=="" || num.toString()=="NaN" || num.toString()=="0"){
        return false;
    }
    if (num.valueOf()<=0){
        return false;
    }
    return true;
}

function addTestFormButton(obj)
{
    var form = $('FORM.contact[form_id]');
    form.append('&#160;&#160;<INPUT onclick="fillTestForm()" class="test_form" value="Fill form" type="button" />');
}

function loadImage(imgName, imgWidth, imgHeight)
{
    imgWidth += 12;
    imgHeight += 15;
    var myt = "newWin = window.open(\"\", \"\", \"height="+ imgHeight + ", width=" + imgWidth + ",resizable=yes\")";
    eval(myt);
    newWin.document.write("<body style=\"padding: 0px; margin: 0px;\">");
    newWin.document.write("<table>");
    newWin.document.write("<tr>");
    newWin.document.write("<td style=\"background-color: #FFFFFF; padding: 5px;\">");
    newWin.document.write("<img src=\"" + imgName + "\" border=\"0\">");
    newWin.document.write("</td>");
    newWin.document.write("</tr>");
    newWin.document.write("</table>");
    newWin.document.write("</body>");
    newWin.document.close();
}

// ----------------[ GREAT VALIDATION: BEGIN ]------------------
// Creating language-vocabularies: begin
try {
    var language="en";
} catch(e) {
    try {
        var language=getInterfaceLanguage();
    } catch(e) {language="en";}
}

// 1. Final error message vocabularies
var FinalRequiredErrorMessage = {
    "en" : "Please fill in all required information",
    "nl" : "Vult u a.u.b. de verplichte velden in",
    "es" : "Por favor rellene la siguiente informaciyn",
    "de" : "Bitte tragen Sie alle erforderlichen Daten ein",
    "fr" : "Merci de completer toutes les informations",
    "it" : "Per favore compilare n'informazione richiesta",
    "pt" : "Complete toda a informa??o solicitada"
};

var FinalEmailErrorMessage = {
    "en" : "Some e-mails are not valid",
    "nl" : "Dit is geen geldig email adres",
    "de" : "Ung?ltige E-mailadresse"
};

var FinalNumericErrorMessage = {
    "en" : "Some numeric fields has invalid values",
    "nl" : "Een van de velden heeft een verkeerde waarde"
}

var FinalTimeDateErrorMessage = {
    "en" : "Some time-date fields has invalid values",
    "nl" : "Een van de tijd velden heeft een verkeerde waarde"
}

var FinalPhoneErrorMessage = {
    "en" : "Some phone fields has invalid values",
    "nl" : "Some phone fields has invalid values"
}

var FinalCurrencyErrorMessage = {
    "en" : "Some currency fields has invalid values"
}
// 2. Temporary (local) error message vocabularies

var LocalRequiredErrorMessage = {
    "en" : "Field require some value",
    "nl" : "Een veld heeft een waarde nodig"
}

var LocalEmailErrorMessage = {
    "en" : "Field-value has wrong e-mail format",
    "nl" : "Er is een foutief e-mail adres ingevuld"
}

var LocalNumericErrorMessage0 = {
    "en" : "TO DEVELOPERS: wrong scripting-syntax",
    "nl" : "Er is een probleem ontstaan. Controleer alle invoervelden"
}

var LocalNumericErrorMessage1 = {
    "en" : "Field has wrong numeric format",
    "nl" : "Een veld heeft een verkeerd numeriek formaat"
}

var LocalNumericErrorMessage2 = {
    "en" : "Field-value has too big integer-part",
    "nl" : "Er is een probleem ontstaan. Controleer alle invoervelden"
}

var LocalNumericErrorMessage3 = {
    "en" : "Field-value has too big fractal-part",
    "nl" : "Er is een probleem ontstaan. Controleer alle invoervelden"
}

var LocalNumericErrorMessage4 = {
    "en" : "Field-value is less than minimum",
    "nl" : "Een veld voldoet niet aan het minimum"
}

var LocalNumericErrorMessage5 = {
    "en" : "Field-value is more than maximum",
    "nl" : "Een veld heeft het maximum overschreden"
}

var LocalTimeDateErrorMessage = {
    "en" : "Field-value has wrong time-date format",
    "nl" : "Een veld heeft een verkeerde tijd en/of datum formaat"
}

var LocalPhoneErrorMessage = {
    "en" : "Field-value has wrong phone format",
    "nl" : "Field-value has wrong phone format"
}

var LocalCurrencyErrorMessage = {
    "en" : "Field-value has wrong currency format"
}

var EmailReservedFieldNameErrorMessage = {
    "en" : "You cannot use field name 'email'. This name is reserved. Please use another.",
    "nl" : "You cannot use field name 'email'. This name is reserved. Please use another."
}


// Creating language-vocabularies: end
// -------------------------------------

// Implementing vocabularies

// 1. Final error messages

var FinalRequiredError = FinalRequiredErrorMessage["en"];
for (var temp_language in FinalRequiredErrorMessage)
{
    if (language==temp_language) {
        FinalRequiredError = FinalRequiredErrorMessage[language];
    }
}

var FinalEmailError = FinalEmailErrorMessage["en"];
for (var temp_language in FinalEmailErrorMessage)
{
    if (language==temp_language) {
        FinalEmailError = FinalEmailErrorMessage[language];
    }
}

var FinalNumericError = FinalNumericErrorMessage["en"];;
for (var temp_language in FinalNumericErrorMessage)
{
    if (language==temp_language) {
        FinalNumericError = FinalNumericErrorMessage[language];
    }
}

var FinalTimeDateError = FinalTimeDateErrorMessage["en"];;
for (var temp_language in FinalTimeDateErrorMessage)
{
    if (language==temp_language) {
        FinalTimeDateError = FinalTimeDateErrorMessage[language];
    }
}

var FinalPhoneError = FinalPhoneErrorMessage["en"];;
for (var temp_language in FinalPhoneErrorMessage)
{
    if (language==temp_language) {
        FinalPhoneError = FinalPhoneErrorMessage[language];
    }
}

var FinalCurrencyError = FinalCurrencyErrorMessage["en"];;
for (var temp_language in FinalCurrencyErrorMessage)
{
    if (language==temp_language) {
        FinalCurrencyError = FinalCurrencyErrorMessage[language];
    }
}
// 2. Temporary (local) error messages

var EmailError1 = LocalEmailErrorMessage["en"];
for (var temp_language in LocalEmailErrorMessage)
{
    if (language==temp_language) {
        EmailError1 = LocalEmailErrorMessage[language];
    }
}

var RequiredError1 = LocalRequiredErrorMessage["en"];
for (var temp_language in LocalRequiredErrorMessage)
{
    if (language==temp_language) {
        RequiredError1 = LocalRequiredErrorMessage[language];
    }
}

var NumericError0 = LocalNumericErrorMessage0["en"];
for (var temp_language in LocalNumericErrorMessage0)
{
    if (language==temp_language) {
        NumericError0 = LocalNumericErrorMessage0[language];
    }
}

var NumericError1 = LocalNumericErrorMessage1["en"];
for (var temp_language in LocalNumericErrorMessage1)
{
    if (language==temp_language) {
        NumericError1 = LocalNumericErrorMessage1[language];
    }
}

var NumericError2 = LocalNumericErrorMessage2["en"];
for (var temp_language in LocalNumericErrorMessage2)
{
    if (language==temp_language) {
        NumericError2 = LocalNumericErrorMessage2[language];
    }
}

var NumericError3 = LocalNumericErrorMessage3["en"];
for (var temp_language in LocalNumericErrorMessage3)
{
    if (language==temp_language) {
        NumericError3 = LocalNumericErrorMessage3[language];
    }
}

var NumericError4 = LocalNumericErrorMessage4["en"];
for (var temp_language in LocalNumericErrorMessage4)
{
    if (language==temp_language) {
        NumericError4 = LocalNumericErrorMessage4[language];
    }
}

var NumericError5 = LocalNumericErrorMessage5["en"];
for (var temp_language in LocalNumericErrorMessage5)
{
    if (language==temp_language) {
        NumericError5 = LocalNumericErrorMessage5[language];
    }
}

var TimeDateError1 = LocalTimeDateErrorMessage["en"];
for (var temp_language in LocalTimeDateErrorMessage)
{
    if (language==temp_language) {
        TimeDateError1 = LocalTimeDateErrorMessage[language];
    }
}

var PhoneError1 = LocalPhoneErrorMessage["en"];
for (var temp_language in LocalPhoneErrorMessage)
{
    if (language==temp_language) {
        PhoneError1 = LocalPhoneErrorMessage[language];
    }
}

var CurrencyError1 = LocalCurrencyErrorMessage["en"];
for (var temp_language in LocalCurrencyErrorMessage)
{
    if (language==temp_language) {
        CurrencyError1 = LocalCurrencyErrorMessage[language];
    }
}

var EmailReservedFieldNameError = EmailReservedFieldNameErrorMessage["en"];
for (var temp_language in EmailReservedFieldNameErrorMessage)
{
    if (language==temp_language) {
        EmailReservedFieldNameError = EmailReservedFieldNameErrorMessage[language];
    }
}

//------------------------ Form validator -----------------------------
function validate(frm, obligatory_text, email_text, numeric_text, show_not_valid_messages)
{
    var rt = new Object();
    rt.email = true;
    rt.required = true;
    rt.numbers = true;
    rt.timedate = true;
    rt.phone = true;
    rt.error_fields = new Array();

    // Unknown but maybe needed
    if (frm.first_obligatory_email_field) {
        frm.first_obligatory_email_field.value = "";
    }

    if (typeof(obligatory_text) == "undefined" || obligatory_text == null)
        obligatory_text = FinalRequiredError;

    if (typeof(email_text) == "undefined" || email_text == null)
        email_text = FinalEmailError;

    if (typeof(numeric_text) == "undefined" || numeric_text == null)
        numeric_text = FinalNumericError;

    if (typeof(show_not_valid_messages) == "undefined" || show_not_valid_messages == null)
        show_not_valid_messages = true;

    //return:
    //rt.email - true if all emails are valid
    //rt.required - if all required fields are filled in
    //rt.numbers - true if all number fields are valid
    //rt.timedate - true if all time-date fields are valid
    //rt.phone - true if all phone fields are valid

    rt = walkForm(frm, rt, frm);

    if (rt.email == true &&  rt.required == true && rt.numbers == true && rt.timedate == true && rt.phone == true) // OK
        return true;

    var tempAlertMessage = "";
    if (rt.email==false) {tempAlertMessage += email_text + "\n";}
    if (rt.required==false) {tempAlertMessage += obligatory_text + "\n";}
    if (rt.numbers==false) {tempAlertMessage += numeric_text + "\n";}
    if (rt.timedate==false) {tempAlertMessage += FinalTimeDateError + "\n";}
    if (rt.phone==false) {tempAlertMessage += FinalPhoneError + "\n";}

    // Changes by Rostislav Brizgunov, Negeso-UA, 2007-11-28
    //if (show_not_valid_messages)
    //	alert(tempAlertMessage);

    var divs = document.getElementsByTagName("div");
    for (var i = 0; i<divs.length; i++) {
        if (divs[i].id.indexOf("not_valid") == 0) {
            divs[i].style.display = "none";
        }
    }

    for (var i = 0; i<rt.error_fields.length; i++) {
        var obj = rt.error_fields[i][0];
        var msg = rt.error_fields[i][1];
        try {
            var el = document.createElement("div");
            var tid = "not_valid" + new Date().getTime();
            try{
                el.setAttribute("id", tid);
            }catch(e){
                try {
                    el.id = tid;
                } catch(e) {}
            }
            el.innerHTML = "<table><tr><td style='font-size: 10px; color: red;'>" + msg + "</td></tr></table>";
            if (obj.nextSibling)
                obj.parentNode.insertBefore(el,obj.nextSibling);
            else
                obj.parentNode.appendChild(el);
        } catch(e) {}
    }

    return false;
}

function isEmailAddr(email)
{
    var tiny_email_regexp="^[a-zA-Z0-9][a-zA-Z0-9_.-]*[a-zA-Z0-9]@[a-zA-Z0-9][a-zA-Z0-9_.-]*[a-zA-Z0-9]$";
    return (new RegExp(tiny_email_regexp).test(email));
}
//Copies temp_name attribute to name attribute of all fields,
//Copies the first required email field to first_obligatory_email_field,
//returns validation results:
//ret.email - true if all emails are valid
//ret.required - if all required fields are filled in
//-----------------------------------------------------
// Addings: 10.05.2006 by Rostislav Brizgunov
// ret.numbers - if all numeric fields are filled correct
function walkForm( obj, ret , frm)
{
    if ( (obj.tagName == "INPUT" && (obj.type == "text" || obj.type == "password" || obj.type == "file" ))
        || obj.tagName == "TEXTAREA")
    {
        obj.style.backgroundColor = "";
        if (obj.getAttribute("temp_name") != null )
        {
            obj.setAttribute("name", obj.getAttribute("temp_name"), 0);
        }
        if ((obj.getAttribute("required") == "true" || obj.getAttribute("required") == "required") && obj.value!=null && obj.value=="" && obj.disabled != true)
        {
            ret.required = false;
            ret.error_fields.push(new Array(obj, RequiredError1));
        }
        if (""+obj.getAttribute("is_email") == "true" && obj.value!=null && obj.value!="" && obj.disabled != true && !isEmailAddr(obj.value) /*&& obj.value!=""*/)
        {
            ret.email = false;
            ret.error_fields.push(new Array(obj, EmailError1));
        }
        if ((obj.type == "text" || obj.type == "password") && obj.getAttribute("numeric_field_params")!=null && obj.getAttribute("numeric_field_params")!="" && obj.disabled != true /*&& obj.value!=null*/ )
        {
            // format of attribute "numeric_field_params": a;b;c;d
            // a = integer; - integer part limitation (quantity of symbols); < 0 or some non-integer, if unlimited
            // b = integer; - fractal part limitation (quantity of symbols); < 0 or some non-integer, if unlimited
            // c = float;   - minimum value limitation; non-integer, if unlimited
            // d = float;   - maximum value limitation; non-integer, if unlimited
            var params = obj.getAttribute("numeric_field_params");
            var arr1 = params.match('^([+-]?[\\d\\w_]*);([+-]?[\\d\\w_]*);([+-]?[\\d\\w\._]*);([+-]?[\\d\\w\._]*)$');
            if (arr1==null) {alert(NumericError0);}
            else
            {
                var int_part = parseInt(arr1[1]);
                if (isNaN(int_part)) {int_part = 'nothing';}
                var fract_part = parseInt(arr1[2]);
                if (isNaN(fract_part)) {fract_part = 'nothing';}
                var min_val = parseFloat(arr1[3]);
                if (isNaN(min_val)) {min_val = 'nothing';}
                var max_val = parseFloat(arr1[4]);
                if (isNaN(max_val)) {max_val = 'nothing';}
                // Here - commented checking for DEVELOPERS: is attribute "numeric_field_params" has valid format???
                // alert('Integer part = '+int_part+"\nFractal part = "+fract_part+"\nMinimum value = "+min_val+"\nMaximum value = "+max_val);
                var valid_result = NumericFormatValid(obj,int_part,fract_part,min_val,max_val);
                if (valid_result != 0) {
                    ret.numbers = false;
                }
                if (valid_result == -1) {ret.error_fields.push(new Array(obj, NumericError1));}
                else if (valid_result == 1) {ret.error_fields.push(new Array(obj, NumericError2));}
                else if (valid_result == 2) {ret.error_fields.push(new Array(obj, NumericError3));}
                else if (valid_result == 3) {
                    if (obj.getAttribute("num_err_msg_min")==null)
                        ret.error_fields.push(new Array(obj, NumericError4+'\n(min == '+min_val+')'));
                    else
                        ret.error_fields.push(new Array(obj, obj.getAttribute("num_err_msg_min")));
                }
                else if (valid_result == 4) {ret.error_fields.push(new Array(obj, NumericError5+'\n(max == '+max_val+')'));}
                if (valid_result != 0) {
                    //obj.style.backgroundColor="#FF8888";
                }
            }
        }
        if ((obj.type == "text" || obj.type == "password") && obj.getAttribute("timedate_field_format")!=null && obj.getAttribute("timedate_field_format")!="" && obj.value!=null && obj.disabled != true /*&& obj.value!=""*/)
        {
            // format of attribute "timedate_field_format": (DD)? (MM)? (YY|YYYY)? (hh)? (mm)? (ss)?
            //                                              - in any order with any separators
            //                                              - Examples: hh:mm;  MM-DD-YY hh'mm''ss; MMdfgrdtYY etc.
            var format = obj.getAttribute("timedate_field_format");
            var valid_result;
            if (format == "new_timedate_validation")
                valid_result = SimpleTimeDateFormatValid(obj);
            else
                valid_result = TimeDateFormatValid(obj,format);
            if (valid_result != 0) {
                ret.error_fields.push(new Array(obj, TimeDateError1+"\n("+format+")"));
                ret.timedate = false;
            }
        }
        if ((obj.type == "text" || obj.type == "password") && obj.getAttribute("is_phone") != null && obj.getAttribute("is_phone") == "true" && obj.value!=null && obj.value!="" && obj.disabled != true)
        {
            var valid_result = PhoneFormatValid(obj);
            if (valid_result != 0) {
                ret.error_fields.push(new Array(obj, PhoneError1));
                ret.phone = false;
            }
        }
        if (obj.tagName == "INPUT" && obj.getAttribute("is_email") == "true" && (obj.getAttribute("required") == "true" || obj.getAttribute("required") == "required") && obj.value!=null && obj.value!="" && frm.first_obligatory_email_field!=null && frm.first_obligatory_email_field.value == "" && obj.disabled != true)
        {
            frm.first_obligatory_email_field.value = obj.value;
        }
    }
    else if ( (obj.tagName == "INPUT" && (obj.type == "radio" || obj.type == "checkbox" ))
        || obj.tagName=="SELECT")
    {
        if (obj.getAttribute("temp_name") != null )
        {
            obj.setAttribute("name", obj.getAttribute("temp_name"), 0);
        }
    }

    for (var i=0; i<obj.childNodes.length; i++)
    {
        ret = walkForm( obj.childNodes[i], ret , frm);
    }
    return ret;
}

// New Adds: 10.05.2006: Rostislav Brizgunov: begin
// Numeric format validating: used in validate(frm)
// How it works:
//             obj: must be object, or ID of object: INPUT type=("text"|"password")
//       int_limit: if int_limit is integer number, and int_limit>=1, then, for example:
//                  int_limit=8: .0 - valid
//                               123.0 - valid
//                               12345678.0 - valid
//                               123456789.0 - NOT VALID
//                  otherwise: valid will be numbers with integer part of any size
//     fract_limit: if fract_limit is integer number, and fract_limit>=0, then, for example:
//                  fract_limit=8: 0. -valid
//                                 0.123 - valid
//                                 0.12345678 - valid
//                                 0.123456789 - NOT VALID
//                  fract_limit=0: 65 - valid
//                                 65.1 - NOT VALID
//                  otherwise: valid will be numbers with fractal part of any size
//   val_limit_min: minimum limitation of value.
//                  if not number (for example: val_limit_min=='none' <-- string), then
//                  limit is OFF (or == -INF)
//   val_limit_max: maximum limitation of value.
//                  if not number (for example: val_limit_max=='none' <-- string), then
//                  limit is OFF (or == +INF)
// ---------------------------------------------------------------------------
// Function returns: 0 - validete success!
//                   1 - int part overload
//                   2 - fractal part overload
//                   3 - value less than minimum
//                   4 - value more than maximum
//                  -1 - wrong numeric format
// ---------------------------------------------------------------------------
// Last added feature: you can enter 0.4356  34543.  .65765   123.6741e+12  3.5672E-12   67.35e+123aBbRaCaDabBra
//                                   0,4356  34543,  ,65765   123,6741e+12  3,5672E-12   67,35e+123aBbRaCaDabBra
function NumericFormatValid(obj,int_limit,fract_limit,val_limit_min,val_limit_max)
{
    if ( obj.tagName=="INPUT" && ( obj.type=="text" || obj.type=="password" ) )
    {
        var val = obj.value;
        // apererva -> TLNC-156: number more 999 in the text field contains
        // dots, i.e. "1.000.234,45". It should be normalized
        // excluding dots from number.
        val = val.replace(new RegExp("\\.","gi"), "");

        // For our calculations we have to convert our value to corresponding format:
        // 5,67 --> 5.67
        val = val.replace(",",".");

        // 5.0E+6 --> 5000000
        val = parseFloat(val);

        // !!!:FIRST CHECK: value in the field has absolutely incorrect numeric-format
        if ( (typeof(val) != 'number') || (isNaN(val)) )
            return -1;

        // !!!:SECOND CHECK: value is more than maximum or less than minimum
        if ((typeof(val_limit_min) == 'number')&&(val < val_limit_min))
            return 3;
        if ((typeof(val_limit_max) == 'number')&&(val > val_limit_max))
            return 4;

        // Beginning creating formated output
        var int_part = "";
        var fract_part = "";
        var sign = (val < 0) ? "-" : "";

        // !!!:THIRD CHECK: integer validation (i.e. 2345678 or 1234,00000, but not 123.4567)
        val = val + "";
        var re = '^[+-]?\\d+$';
        var t1 = val.search(re);
        if (t1==0) {
            // Continue creating formated output
            int_part = val;
            if (typeof(fract_limit) == 'number') {
                for (var i = 0; i<fract_limit; i++)
                    fract_part = fract_part + "0";
            }

            t1 = val;
            if ((typeof(int_limit) == 'number')&&(int_limit >= 1)&&(t1.length > int_limit))
                return 1;
        }
        // !!!:FOURTH CHECK: floating-point validation (i.e. 123.456 or .456)
        else if (val.search('^[+-]?(\\d*)\\.(\\d*)$')==0)
        {
            re = '^[+-]?(\\d*)\\.(\\d*)$';
            t1 = val.match(re);
            int_part = t1[1];
            fract_part = t1[2];

            // Continue creating formated output
            if (typeof(fract_limit) == 'number') {
                for (var i = fract_part.length; i<fract_limit; i++)
                    fract_part = fract_part + "0";
            }

            if ((typeof int_limit == 'number')&&(int_limit >= 1)&&(int_part.length > int_limit))
                return 1;
            else if ((typeof fract_limit == 'number')&&(fract_limit >= 0)&&(fract_part.length > fract_limit))
                return 2;
        }

        // Here we are returning parsed and correct value into the field
        var val_as_string = int_part;
        if (fract_part && fract_part.length>0)
            val_as_string = sign + val_as_string + "," + fract_part;
        val_as_string = val_as_string.replace("--","-");
        obj.value = val_as_string;
        return 0;
    }
}

// New Adds: 12.05.2006: Rostislav Brizgunov: begin
// Time-date format validating: used in validate(frm)
// ---------------------------------------------------------------------------
// format_string FORMAT: (DD)? (MM)? (YY|YYYY)? (hh)? (mm)? (ss)? - in any order with any separators
//                       Example: hh:mm;  MM-DD-YY hh'mm''ss; MMdfgrdtYY etc.
//                       hh - 0-24 or 00-24
//                       mm - 00-59
//                       ss - 00-59
//                       YY - 00-99
//                       YYYY - 0000-9999
//                       MM - 01-12
//                       DD - 01-31
// ---------------------------------------------------------------------------
// Function returns: 0 - validete success!
//                  -1 - wrong time-date format
// ---------------------------------------------------------------------------
function TimeDateFormatValid(obj,format_string) {
    // Creating regular expression template from format_string
    // Step 1: monitoring special-expression-symbols
    var expression_template = format_string;
    var re = /\\/g;
    expression_template = expression_template.replace(re,"\\\\");
    re = /\./g;
    expression_template = expression_template.replace(re,"\\.");
    re = /\(/g;
    expression_template = expression_template.replace(re,"\\(");
    re = /\)/g;
    expression_template = expression_template.replace(re,"\\)");
    re = /\[/g;
    expression_template = expression_template.replace(re,"\\[");
    re = /\]/g;
    expression_template = expression_template.replace(re,"\\]");
    re = /\{/g;
    expression_template = expression_template.replace(re,"\\{");
    re = /\}/g;
    expression_template = expression_template.replace(re,"\\}");
    // Step 2: transforming to the main template
    re = /hh/g;
    expression_template = expression_template.replace(re,"(?:(0|1|)(?=[0-9])|2(?=[0-4]))[0-9]");
    re = /mm/g;
    expression_template = expression_template.replace(re,"[0-5][0-9]");
    re = /ss/g;
    expression_template = expression_template.replace(re,"[0-5][0-9]");
    re = /YYYY/g;
    expression_template = expression_template.replace(re,"[0-9]{4}");
    re = /YY/g;
    expression_template = expression_template.replace(re,"[0-9]{2}");
    re = /MM/g;
    expression_template = expression_template.replace(re,"(?:0(?=[1-9])|1(?=[0-2]))[0-9]");
    re = /DD/g;
    expression_template = expression_template.replace(re,"(?:0(?=[1-9])|[1-2](?=[0-9])|3(?=[0-1]))[0-9]");
    expression_template = "^" + expression_template + "$";
    // Step 3: Checking
    if (obj.value.search(expression_template) != 0)
        return -1;
    return 0;
}

function displayPopup(url){
    newPopup = window.open(
        url,
        "",
        "height=300, width=400, menubar=no, resizable=yes, " +
        "scrollbars=yes, status=no, titlebar=yes, toolbar=no, " +
        "left=" + (screen.availWidth/5*2) + ", " +
        "top=" + (screen.availHeight/5*1)
    );
}

function SimpleTimeDateFormatValid(obj) {
    if (obj.value.search("^[a-zA-Z\\d\\-/:()\\\\]+$") != 0)
        return -1;
    return 0;
}

// New Adds: 04.10.2006: Rostislav Brizgunov
// Phone format validating: used in validate(frm)
function PhoneFormatValid(obj) {
    if (obj.value.search(new RegExp("^[\\d\+)(-]{1,20}$", "i")) != 0)
        return -1;
    return 0;
}
// ----------------[ GREAT VALIDATION: END ]------------------

function no_image(obj)
{

    var myTable = document.createElement('table')
    var tempTR = document.createElement('tr');
    var tempTD = document.createElement('td');
    var tempTBODY = document.createElement('tbody');

    tempTD.innerHTML = 'no image<br />available';
    tempTD.setAttribute('src', obj.src);
    tempTR.appendChild(tempTD);
    tempTBODY.appendChild(tempTR);
    myTable.appendChild(tempTBODY);

    myTable.className = 'noImageTable'
    myTable.setAttribute('cellpadding',0);
    myTable.setAttribute('cellspacing',0);
    myTable.setAttribute('border',0);
    myTable.setAttribute('src', obj.src);

    if(obj.currentStyle){
        myTable.style.dispaly = obj.currentStyle.display;
        myTable.style.width = obj.currentStyle.width;
        myTable.style.height = obj.currentStyle.height;}
    else if(obj.height || obj.width){
        myTable.style.width = obj.width+'px';
        myTable.style.height = obj.height-1+'px';
    }
    else{
        myTable.style.width = '100%';
        myTable.style.height = '100%';
    }

    obj.parentNode.replaceChild(myTable,obj);
}



function pushContent(art){

    var tempDiv = document.createElement('DIV');
    tempDiv.innerHTML = art.innerHTML;
    var forms = $('form', tempDiv);
    $('INPUT, TEXTAREA, SELECT' ,tempDiv).attr("disabled", "disabled");
    $('INPUT, TEXTAREA, SELECT' ,tempDiv).removeAttr("required");
    for(var i = 0; i < forms.length; i++){
        var form = forms[i];
        $(form).replaceWith('<div>' + $(form).html() +  '</div>')

    }

    document.getElementById(art.id + '_temp').innerHTML = tempDiv.innerHTML;

}


$(function () {
    if ($().datepicker) {
        $("#publishDatepicker").datepicker({
            dateFormat: 'dd-mm-yy',
            //showAnim: 'slideDown',
            showOn: 'button',
            buttonImage: '/images/calendar.gif',
            buttonImageOnly: true
        });
        $("#expiredDatepicker").datepicker({
            dateFormat: 'dd-mm-yy',
            //showAnim: 'slideDown',
            showOn: 'button',
            buttonImage: '/images/calendar.gif',
            buttonImageOnly: true
        });
        $("#viewableDatepicker").datepicker({
            dateFormat: 'dd-mm-yy',
            //showAnim: 'slideDown',
            showOn: 'button',
            buttonImage: '/images/calendar.gif',
            buttonImageOnly: true
        });
        $("#expiredDateId").datepicker({
            dateFormat: 'dd-mm-yy',
            //showAnim: 'slideDown',
            showOn: 'button',
            buttonImage: '/images/calendar.gif',
            buttonImageOnly: true
        });
        $("#publishDateId").datepicker({
            dateFormat: 'dd-mm-yy',
            //showAnim: 'slideDown',
            showOn: 'button',
            buttonImage: '/images/calendar.gif',
            buttonImageOnly: true
        });
        $("#publishDateFieldId").datepicker({
            dateFormat: 'dd-mm-yy',
            //showAnim: 'slideDown',
            showOn: 'button',
            buttonImage: '/images/calendar.gif',
            buttonImageOnly: true
        });
        $("#expiredDateFieldId").datepicker({
            dateFormat: 'dd-mm-yy',
            //showAnim: 'slideDown',
            showOn: 'button',
            buttonImage: '/images/calendar.gif',
            buttonImageOnly: true
        });
    }
});

function chooseRichSnippets(id, entity) {
    $.ajax({
        url: "/admin/rs_choose_rich_snippet.html?entity=" + entity + "&id=" + id,
        dataType: "html",
        success: function (data) {
            $("[class^=chooseRichSnippetDialog]").each(function() {
                $(this).remove();
            });
            var command = id > 0 ? "update" : "add";
            $(document.body).append(data);
            $("#chooseRichSnippetDialog").dialog({ buttons: {
                    'Cancel': function() { $(this).remove();  },
                    'Save': function() {
                        $.ajax({
                            type: "POST",
                            url: "/admin/rs_choose_rich_snippet.html?entity=" + entity + "&id=" + id + "&action=save",
                            data: $("#chooseRichSnippetDialogForm").serialize(),
                            dataType: "html",
                            success: function(html, stat) {
                                $("#chooseRichSnippetDialog").remove();
                            },
                            error: function(html, stat) {
                                $("#chooseRichSnippetDialog").remove();
                                alert("Internal server error occured: " + html);
                            }
                        });
                    }} });
        }
    });
}




/* Adapter for showModalDialog */
/* examples:
 * function ex1(args){
 *     doSomething(args);
 *     showModalDialog(url, params, options).then(function(smdResult){
 *         processSMDResult(smdResult);
 *     });
 * }
 *
 * function ex2(callback){
 *     showModalDialog(url, params, options).then(function(smdResult){
 *         callback(smdResult);
 *     });
 * }
 *
 * function ex3(){
 *     var that = this;
 *     showModalDialog(url, params, options).then(function(smdResult){
 *         this.processResult(smdResult);
 *     }.bind(that));
 * }
 **/
if (typeof window.showModalDialog === 'function'){
    (function(){
        var smd = showModalDialog;
        window.showModalDialog = function(a,b,c){
            var res,
                res = smd(a, b, c);
            if (typeof res === 'undefined'){
                res = {};
            }
            if (typeof res === 'object' || typeof res === 'function'){
                res.then = function(fn){
                    return fn(res);
                };
            }
            return res;
        };
    })();
}

window.showModalDialog = window.showModalDialog || {isFake: true};

if (window.showModalDialog.isFake){
    window.showModalDialog = (function(){
        var _modal = null;

        function _fakeShowModalDialog(uri, args, params) {
            var promise = new Promise(function(resolve, reject){
                _modal = modalWindow(uri, args, params);
                _modal.onbeforeunload = function(){
                    var modData = getRetVal(_modal.returnValue);
                    resolve(modData);
                    return null;
                };
                _modal.dialogArguments = args;
            });
            return promise;
        }

        function getRetVal(obj){
            var res = {},
                key;
            for (key in obj){
                res[key] = obj[key];
            }
            return res
        }
        return _fakeShowModalDialog;
    })();
}

modalWindow = (function(){
    var _winObjRef = null,
        _previousUrl;
    function _openSinglePopup(strUrl, args, params) {
        if(_winObjRef === null || _winObjRef.closed) {
            _winObjRef = window.open(strUrl, null, params);
        } else if(_previousUrl != strUrl) {
            _winObjRef = window.open(strUrl, null, params);
            _winObjRef.focus();
        } else {
            _winObjRef.focus();
        }
        _previousUrl = strUrl;
        return _winObjRef;
    }
    return _openSinglePopup;
})();
