// ----------------[ GREAT VALIDATION: BEGIN ]------------------

//------------------------ Form validator -----------------------------

function fillTestForm() {
    var frm = this.form;

    $('INPUT[type=text]:not([readOnly])', frm).each(function () {
        if ($(this).attr('is_email') == 'true') {
            $(this).val('test@email.com');
        }
        else if ($(this).attr('numeric_field_params')) {
            $(this).val('1234');
        }
        else if ($(this).attr('timedate_field_format')) {
            var curDate = new Date();
            var value = $(this).attr('timedate_field_format');
            value = value.replace(/hh/, (curDate.getHours() < 10) ? '0' + (curDate.getHours()) : curDate.getHours());
            value = value.replace(/mm/, (curDate.getMinutes() < 10) ? '0' + (curDate.getMinutes()) : curDate.getMinutes());
            value = value.replace(/DD/, (curDate.getDate() < 10) ? '0' + (curDate.getDate()) : curDate.getDate());

            value = value.replace(/MM/, (curDate.getMonth() + 1 < 10) ? '0' + (curDate.getMonth() + 1) : curDate.getMonth() + 1);
            value = value.replace(/YYYY/, curDate.getFullYear());
            $(this).val(value);
        }
        else {
            $(this).val('this is a test');
        }

        if ($(this).attr('maxLength') && Number($(this).attr('maxLength'))) {
            $(this).val($(this).val().substr(0, $(this).attr('maxLength')));
        }
    });

    $('INPUT[type=password]:not([readOnly])', frm).each(function () {
        $(this).val('password');
    });

    $('INPUT[type=checkbox]', frm).each(function () {
        $(this).attr('checked', 'true');
    });

    $('textarea', frm).each(function () {
        $(this).val('this is a test');
    });


}

function validate(frm) {
    if (frm['mailToFlag']) frm.action = 'send_email';
    return validate_advanced(frm, 1);
}

function full_validate(frm) {
    return validate_advanced(frm, 1);
}

function silent_validate(frm) {
    return validate_advanced(frm, 0);
}

function validate_advanced(frm, silent_mode) {
    var rt = new Object();
    rt.email = true;
    rt.required = true;
    rt.numbers = true;
    rt.number_multi = true;
    rt.timedate = true;
    rt.phone = true;
    // NEW!!!
    rt.error_fields = new Array();

    // Unknown but maybe needed
    if (frm.first_obligatory_email_field) {
        frm.first_obligatory_email_field.value = "";
    }

    //return:
    //rt.email - true if all emails are valid
    //rt.required - if all required fields are filled in
    //rt.numbers - true if all number fields are valid
    //rt.timedate - true if all time-date fields are valid

    rt = walkThroughForm1(frm, rt, frm);



    if (rt.email == true && rt.required == true && rt.numbers == true && rt.timedate == true && rt.phone == true && rt.number_multi == true && validatePaymentMethod() ) // OK
    {
        var arrTA = new Array();
        if (frm.form_id) {
            for (var i = 0; i < frm.length; i++) {

                if (frm[i].tagName == 'TEXTAREA') {
                    var newTextArea = document.createElement('textarea');
                    newTextArea.name = frm[i].name;
                    newTextArea.value = frm[i].getAttribute('BRValue');
                    newTextArea.style.display = 'none';
                    frm[i].name = ''
                    frm[i].disabled = true;
                    //document.insertBefore(newTextArea,frm[i]);
                    arrTA.push([newTextArea, frm[i]]);
                }
            }

            for (var i = 0; i < arrTA.length; i++) {
                arrTA[i][1].parentNode.insertBefore(arrTA[i][0], arrTA[i][1]);
            }
        }
        // if(document.querySelector('#countries')){
        // 	document.querySelector('#countries').value = getCountryCode(document.querySelector('#countries'),countriesObj);
        // }
        return true;
    }
    else if (silent_mode == 1) {
        var tempAlertMessage = "";
        if (rt.email == false) {
            tempAlertMessage = tempAlertMessage + fmConsts.FINAL_EMAIL + "\n";
        }
        if (rt.required == false) {
            tempAlertMessage = tempAlertMessage + fmConsts.FINAL_REQUIER + "\n";
        }
        if (rt.numbers == false) {
            tempAlertMessage = tempAlertMessage + fmConsts.NUMERIC + "\n";
        }
        if (rt.timedate == false) {
            tempAlertMessage = tempAlertMessage + fmConsts.TIME_DATE + "\n";
        }
        if (rt.phone == false) {
            tempAlertMessage = tempAlertMessage + fmConsts.PHONE + "\n";
        }
        if (rt.number_multi == false) {
            tempAlertMessage = tempAlertMessage + fmConsts.NUMERIC + "\n";
        }
        if(document.querySelector('#countries')){
            if (validatePaymentMethod()===false){
                console.log('pm error');
                var error = document.createElement('div');
                error.innerHTML = fmConsts.LOCAL;
                error.classList.add('error-payment');
                document.querySelector('.type_payment').appendChild(error);
            }
        }

        // New
        //alert(tempAlertMessage);
        /*
        var divs = document.getElementsByTagName("div");
        for (var i = 0; i<divs.length; i++) {
            if (divs[i].id.indexOf("not_valid") > -1) {
                $(divs[i]).remove();
    //      divs[i].style.display = "none";
            }
        }*/
        $('table.not_valid').parent().remove();

        for (var i = 0; i < rt.error_fields.length; i++) {
            var obj = rt.error_fields[i][0];
            var msg = rt.error_fields[i][1];
            try {
                var el = document.createElement("div");
                var tid = "not_valid" + new Date().getTime();
                try {
                    el.setAttribute("id", tid);
                } catch (e) {
                    try {
                        el.id = tid;
                    } catch (e) {
                    }
                }
                el.innerHTML = "<table class=\"not_valid\" cellspacing=\"0\" cellpadding=\"0\" style=\"height: 12px; margin: 0; padding: 0; border: 0;\"><tr style=\"height: 12px;\"><td style=\"font-size: 10px; color: red; border: 0; padding: 0; text-align: left; vertical-align: middle; height: 12px;\">" + msg + "</td></tr></table>";
                if (obj.nextSibling)
                    obj.parentNode.insertBefore(el, obj.nextSibling);
                else
                    obj.parentNode.appendChild(el);
            } catch (e) {
            }
        }
    }
    return false;
}

//Checks if String is valid email
function isEmailAddr(email) {
    /*var result = false;
    var theStr = new String(email);
    var index = theStr.indexOf("@");
    if (index > 0)
    {
        var pindex = theStr.indexOf(".",index);
        if ((pindex > index+1) && (theStr.length > pindex+1))
    result = true;
    }
    return result;*/
    var tiny_email_regexp = /^[a-zA-Z0-9][a-zA-Z0-9_.-]*[a-zA-Z0-9]@[a-zA-Z0-9][a-zA-Z0-9_.-]*[a-zA-Z0-9]\.[a-zA-Z0-9]{1,4}$/;
    return (new RegExp(tiny_email_regexp).test(email));
}

function isNumberMulti(numbers) {
    var tiny_numbers_regexp = /^([0-9][0-9];?)+$/;
    return (new RegExp(tiny_numbers_regexp).test(numbers));
}

//Copies temp_name attribute to name attribute of all fields,
//Copies the first required email field to first_obligatory_email_field,
//returns validation results:
//ret.email - true if all emails are valid
//ret.required - if all required fields are filled in
//-----------------------------------------------------
// Addings: 10.05.2006 by Rostislav Brizgunov
// ret.numbers - if all numeric fields are filled correct
function walkThroughForm1(obj, ret, frm) {


    if ((obj.tagName == "INPUT" && (obj.type == "text" || obj.type == "password" || obj.type == "file"))
        || obj.tagName == "TEXTAREA") {
        obj.style.backgroundColor = "";

        if (frm.form_id && obj.tagName == "TEXTAREA") {

            var codeString = '';
            for (var i = 0; i < obj.value.length; i++) {
                codeString += obj.value.charCodeAt(i) + ';';
            }
            codeString = codeString.replace(/;(13;)?10;/g, ';60;98;114;32;47;62;');
            var arrCodeCharset = new Array();
            arrCodeCharset = codeString.split(';');
            codeString = '';
            for (var i = 0; i < arrCodeCharset.length; i++) {
                codeString += String.fromCharCode(arrCodeCharset[i]);
            }

            obj.setAttribute('BRValue', codeString);
        }

        if (obj.getAttribute("temp_name") != null) {
            obj.setAttribute("name", obj.getAttribute("temp_name"), 0);
        }
        if ((obj.getAttribute("is_required") == "true" || obj.getAttribute("is_required") == "required") && obj.value != null && obj.value == "" && obj.disabled != true) {
            ret.required = false;
            ret.error_fields.push(new Array(obj, fmConsts.LOCAL));
        }
        if ("" + obj.getAttribute("is_email") == "true" && obj.value != null && obj.value != "" && obj.disabled != true && !isEmailAddr(obj.value) /*&& obj.value!=""*/) {
            ret.email = false;
            ret.error_fields.push(new Array(obj, fmConsts.LOCAL_EMAIL));
        }
        if ("" + obj.getAttribute("is_number_multi") == "true" && obj.value != null && obj.value != "" && obj.disabled != true && !isNumberMulti(obj.value)) {
            ret.number_multi = false;
            ret.error_fields.push(new Array(obj, fmConsts.NUMERIC));
        }

        if ((obj.type == "text" || obj.type == "password") && obj.value != null && obj.value != "" && obj.getAttribute("numeric_field_params") != null && obj.getAttribute("numeric_field_params") != "" && obj.disabled != true) {
            // format of attribute "numeric_field_params": a;b;c;d
            // a = integer; - integer part limitation (quantity of symbols); < 0 or some non-integer, if unlimited
            // b = integer; - fractal part limitation (quantity of symbols); < 0 or some non-integer, if unlimited
            // c = float;   - minimum value limitation; non-integer, if unlimited
            // d = float;   - maximum value limitation; non-integer, if unlimited
            var params = obj.getAttribute("numeric_field_params");
            var arr1 = params.match('^([+-]?[\\d\\w_]*);([+-]?[\\d\\w_]*);([+-]?[\\d\\w\._]*);([+-]?[\\d\\w\._]*)$');
            if (arr1 == null) {
                alert(fmConsts.NUMERIC_0);
            }
            else {
                var int_part = parseInt(arr1[1]);
                if (isNaN(int_part)) {
                    int_part = 'nothing';
                }
                var fract_part = parseInt(arr1[2]);
                if (isNaN(fract_part)) {
                    fract_part = 'nothing';
                }
                var min_val = parseFloat(arr1[3]);
                if (isNaN(min_val)) {
                    min_val = 'nothing';
                }
                var max_val = parseFloat(arr1[4]);
                if (isNaN(max_val)) {
                    max_val = 'nothing';
                }
                // Here - commented checking for DEVELOPERS: is attribute "numeric_field_params" has valid format???
                // alert('Integer part = '+int_part+"\nFractal part = "+fract_part+"\nMinimum value = "+min_val+"\nMaximum value = "+max_val);
                var valid_result = NumericFormatValid(obj, int_part, fract_part, min_val, max_val);
                if (valid_result != 0) {
                    ret.numbers = false;
                }
                if (valid_result == -1) {
                    ret.error_fields.push(new Array(obj, fmConsts.NUMERIC_1));
                }
                else if (valid_result == 1) {
                    ret.error_fields.push(new Array(obj, fmConsts.NUMERIC_2));
                }
                else if (valid_result == 2) {
                    ret.error_fields.push(new Array(obj, fmConsts.NUMERIC_3));
                }
                else if (valid_result == 3) {
                    if (obj.getAttribute("num_err_msg_min") == null)
                        ret.error_fields.push(new Array(obj, fmConsts.NUMERIC_4 + '\n(min == ' + min_val + ')'));
                    else
                        ret.error_fields.push(new Array(obj, obj.getAttribute("num_err_msg_min")));
                }
                else if (valid_result == 4) {
                    ret.error_fields.push(new Array(obj, fmConsts.NUMERIC_5 + '\n(max == ' + max_val + ')'));
                }
                if (valid_result != 0) {
                    //obj.style.backgroundColor="#FF8888";
                }
            }
        }
        if ((obj.type == "text" || obj.type == "password") && obj.getAttribute("timedate_field_format") != null && obj.getAttribute("timedate_field_format") != "" && obj.value != null && obj.disabled != true && obj.value != "") {
            // format of attribute "timedate_field_format": (DD)? (MM)? (YY|YYYY)? (hh)? (mm)? (ss)?
            //                                              - in any order with any separators
            //                                              - Examples: hh:mm;  MM-DD-YY hh'mm''ss; MMdfgrdtYY etc.
            var format = obj.getAttribute("timedate_field_format");
            var valid_result;
            if (format == "new_timedate_validation")
                valid_result = SimpvarimeDateFormatValid(obj);
            else
                valid_result = TimeDateFormatValid(obj, format);
            if (valid_result != 0) {
                ret.error_fields.push(new Array(obj, fmConsts.LOCAL_TIME_DATE + "\n(" + format + ")"));
                ret.timedate = false;
            }
        }
        if ((obj.type == "text" || obj.type == "password") && obj.getAttribute("is_phone") != null && obj.getAttribute("is_phone") == "true" && obj.value != null && obj.value != "" && obj.disabled != true) {
            var valid_result = PhoneFormatValid(obj);
            if (valid_result != 0) {
                ret.error_fields.push(new Array(obj, fmConsts.LOCAL_PHONE));
                ret.phone = false;
            }
        }
        if (obj.tagName == "INPUT" && obj.getAttribute("is_email") == "true" && (obj.getAttribute("is_required") == "true" || obj.getAttribute("is_required") == "required") && obj.value != null && obj.value != "" && frm.first_obligatory_email_field != null && frm.first_obligatory_email_field.value == "" && obj.disabled != true) {
            frm.first_obligatory_email_field.value = obj.value;
        }
    }
    else if ((obj.tagName == "INPUT" && (obj.type == "radio" || obj.type == "checkbox"))
        || obj.tagName == "SELECT") {
        if (obj.getAttribute("temp_name") != null) {
            obj.setAttribute("name", obj.getAttribute("temp_name"), 0);
        }
    }
    else if (obj.tagName == "INPUT" && (obj.type == "hidden" && obj.getAttribute("required1") == "true") && obj.value == "") {
        ret.required = false;
        ret.error_fields.push(new Array(obj, fmConsts.LOCAL));
    }


    for (var i = 0; i < obj.childNodes.length; i++) {
        ret = walkThroughForm1(obj.childNodes[i], ret, frm);
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
function NumericFormatValid(obj, int_limit, fract_limit, val_limit_min, val_limit_max) {
    if (obj.tagName == "INPUT" && (obj.type == "text" || obj.type == "password")) {
        var val = obj.value;
        // apererva -> TLNC-156: number more 999 in the text field contains
        // dots, i.e. "1.000.234,45". It should be normalized
        // excluding dots from number.
        val = val.replace(new RegExp("\\.", "gi"), "");

        // For our calculations we have to convert our value to corresponding format:
        // 5,67 --> 5.67
        val = val.replace(",", ".");

        // 5.0E+6 --> 5000000

        val = val.replace(/[^0-9]|-+/g, "");
        num0 = /^0/.test(val);
        val = parseFloat(val);

        // !!!:FIRST CHECK: value in the field has absolutely incorrect numeric-format
        if ((typeof(val) != 'number') || (isNaN(val)))
            return -1;

        // !!!:SECOND CHECK: value is more than maximum or less than minimum
        if ((typeof(val_limit_min) == 'number') && (val < val_limit_min))
            return 3;
        if ((typeof(val_limit_max) == 'number') && (val > val_limit_max))
            return 4;

        // Beginning creating formated output
        var int_part = "";
        var fract_part = "";
        var sign = (val < 0) ? "-" : "";

        // !!!:THIRD CHECK: integer validation (i.e. 2345678 or 1234,00000, but not 123.4567)
        (num0) ? val = '0' + val + "" : val = val + "";
        var re = '^[+-]?\\d+$';
        var t1 = val.search(re);
        if (t1 == 0) {
            // Continue creating formated output
            int_part = val;
            if (typeof(fract_limit) == 'number') {
                for (var i = 0; i < fract_limit; i++)
                    fract_part = fract_part + "0";
            }

            t1 = val;
            if ((typeof(int_limit) == 'number') && (int_limit >= 1) && (t1.length > int_limit))
                return 1;
        }
        // !!!:FOURTH CHECK: floating-point validation (i.e. 123.456 or .456)
        else if (val.search('^[+-]?(\\d*)\\.(\\d*)$') == 0) {
            re = '^[+-]?(\\d*)\\.(\\d*)$';
            t1 = val.match(re);
            int_part = t1[1];
            fract_part = t1[2];

            // Continue creating formated output
            if (typeof(fract_limit) == 'number') {
                for (var i = fract_part.length; i < fract_limit; i++)
                    fract_part = fract_part + "0";
            }

            if ((typeof int_limit == 'number') && (int_limit >= 1) && (int_part.length > int_limit))
                return 1;
            else if ((typeof fract_limit == 'number') && (fract_limit >= 0) && (fract_part.length > fract_limit))
                return 2;
        }

        // Here we are returning parsed and correct value into the field
        var val_as_string = int_part;
        if (fract_part && fract_part.length > 0) val_as_string = sign + val_as_string + "," + fract_part;
        val_as_string = val_as_string.replace("--", "-");
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
function TimeDateFormatValid(obj, format_string) {
    // Creating regular expression template from format_string
    // Step 1: monitoring special-expression-symbols
    var expression_template = format_string;
    var re = /\\/g;
    expression_template = expression_template.replace(re, "\\\\");
    re = /\./g;
    expression_template = expression_template.replace(re, "\\.");
    re = /\(/g;
    expression_template = expression_template.replace(re, "\\(");
    re = /\)/g;
    expression_template = expression_template.replace(re, "\\)");
    re = /\[/g;
    expression_template = expression_template.replace(re, "\\[");
    re = /\]/g;
    expression_template = expression_template.replace(re, "\\]");
    re = /\{/g;
    expression_template = expression_template.replace(re, "\\{");
    re = /\}/g;
    expression_template = expression_template.replace(re, "\\}");
    // Step 2: transforming to the main template
    re = /hh/g;
    expression_template = expression_template.replace(re, "(?:(0|1|)(?=[0-9])|2(?=[0-4]))[0-9]");
    re = /mm/g;
    expression_template = expression_template.replace(re, "[0-5][0-9]");
    re = /ss/g;
    expression_template = expression_template.replace(re, "[0-5][0-9]");
    re = /YYYY/g;
    expression_template = expression_template.replace(re, "[0-9]{4}");
    re = /YY/g;
    expression_template = expression_template.replace(re, "[0-9]{2}");
    re = /MM/g;
    expression_template = expression_template.replace(re, "(?:0(?=[1-9])|1(?=[0-2]))[0-9]");
    re = /DD/g;
    expression_template = expression_template.replace(re, "(?:0(?=[1-9])|[1-2](?=[0-9])|3(?=[0-1]))[0-9]");
    expression_template = "^" + expression_template + "$";
    // Step 3: Checking
    if (obj.value.search(expression_template) != 0)
        return -1;
    return 0;
}

function SimpvarimeDateFormatValid(obj) {
    if (obj.value.search("^[a-zA-Z\\d\\-/:()\\\\]+$") != 0)
        return -1;
    return 0;
}

// New Adds: 04.10.2006: Rostislav Brizgunov
// Phone format validating: used in validate(frm)
function PhoneFormatValid(obj) {
    return PhoneFormatValidCommon(obj);
    /*  if (obj.value.search(new RegExp("^[\\d\+)(-]{1,20}$", "i")) != 0)
            return -1;
        return 0;   */
}

function PhoneFormatValidCommon(obj) {
    if (obj.value.search(new RegExp(
        "^\\d{10,20}$" +
        "|^(\\d{2})[- ]?(\\d{8,18})$" +
        "|^(\\d{3})[- ]?(\\d{7,17})$" +
        "|^(\\d{4})[- ]?(\\d{6,16})$" +
        "|^[+]\\d{10,20}$" +
        "|^[0][0]\\d{10,20}$" +
        "|^[+]\\d{2,2}[(][0][)]\\d{9,20}$" +
        "|^[0][0]\\d{2,2}[(][0][)]\\d{9,20}$"
    )) != -1)
        return 0;
    return -1;
}

// ----------------[ GREAT VALIDATION: END ]------------------

/*
 * Checks if obj contains valid input.
 * For the list of valid objects see above.
 */
function validateBlock(obj) {
    if (obj.tagName == "INPUT") {
        if (obj.value) return true;
    } else if (obj.tagName == "TEXTAREA") {
        if (obj.value) return true;
    } else if (obj.tagName == "FIELDSET") {
        checkboxes = obj.getElementsByTagName("INPUT");
        checked = false;
        for (var i = 0; i < checkboxes.length; i++) {
            if (checkboxes[i].type == "checkbox" || checkboxes[i].type == "radio") {
                if (checkboxes[i].checked) checked = true;
            }
        }
        return checked;
    }
    return false;
}

/* Validate 'at least'*/
function validate_at_least(frm) {
    var inps = frm.getElementsByTagName("input");
    var validate_result = false;
    for (var i = 0; i < inps.length; i++) {
        // <input type="text" ...> OR <input type="checkbox" ...>
        if (
            (inps[i].type == "text" || inps[i].type == "checkbox" || typeof(inps[i].type) == "undefined" || inps[i].type == null)
            && (inps[i].getAttribute("is_required") == "true" || inps[i].getAttribute("required_at_least") == "true" || inps[i].getAttribute("required_at_least") == "required_at_least" || navigator.userAgent.indexOf('IE') != -1)
            && ((inps[i].value != null && inps[i].value != '' && inps[i].type != "checkbox") || inps[i].checked)
        ) {
            validate_result = true;
        }
    }
    return validate_result;
}

function validate_depatment(frm) {
    var inps = frm.getElementsByTagName("input");
    var validate_result = false;
    for (var i = 0; i < inps.length; i++) {
        if (inps[i].name == "_dep" && inps[i].checked) {
            validate_result = true;
        }
    }
    return validate_result;
}

function validate_categories() {
//  debugger;
    var cnt_checked = false;
    var n = document.forms['nlSimpleSubscribeForm'].length;
    for (var i = 0; i <= n; i++) {
        if ((document.forms['nlSimpleSubscribeForm'][i].type == 'checkbox') && document.forms['nlSimpleSubscribeForm'][i].status == true) {
            var cnt_checked = true;
            break;
        }
    }
    if (cnt_checked == false) {
        document.getElementById('error_checkbox').style.display = 'block';
    }
    return cnt_checked;
}


function validatePaymentMethod() {
    if (document.querySelector("#countries")){
        var oldError = document.querySelector('.error-payment');
        if (oldError){
            $(oldError).remove();
        }

        // var errorOld = document.querySelector('.error-payment');
        // if (errorOld) {
        // 	errorOld.remove();
        // }
        if ($('[name="payment_method"]:checked').length > 0){
            return true
        }
        return false;
    }
    else {
        return true
    }

}