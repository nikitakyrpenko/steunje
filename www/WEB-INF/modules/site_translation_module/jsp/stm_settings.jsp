<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%
    response.setHeader("Expires", "0");
%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="negeso"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title><fmt:message key="STM_SETTINGS" />
</title>
<link href="/css/admin_style.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" type="text/css" href="/css/jquery-ui.custom.css" />
<link rel="stylesheet" type="text/css"
    href="/css/customized-jquery-ui.css" />

<script type="text/javascript" src="/script/jquery.min.js">
    /**/
</script>
<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
<script type="text/javascript" src="/script/calendar_picker.js"
    type="text/javascript"></script>
<script type="text/javascript" src="/script/cufon-yui.js"></script>
<script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
<script language="JavaScript1.2" src="/script/common_functions.js"
    type="text/javascript"></script>
<script type="text/javascript" src="/site/core/script/validation.js"></script>

<script>
    var isExtraLangsAvailable = ${licenseLangsLimit > fn:length(currentLangs)};
    var dialog = '<div id="addLangDialog" class="addLangDialog" title="<fmt:message key="STM_ADD_LANG_TO_SITE"/>"> ' + 
       (isExtraLangsAvailable ? '<label ><fmt:message key="TITLE"/>:</label><br/>'
                        + '<input type="text" id="language" name required="true"class="admWidth200"/>'
                        + '<br/><br/>'
                        + '<label >Code:</label><br/>'
                        + '<select id="langsSelect"><option><option/><select/>'
                : addDomain('<br/><b><fmt:message key="LANGUAGE_LIMITATION_REACHED"/></b>', 'NEGESO_CMS_STM_NEW_LANGUAGE'))         
            + '</div>';
    var quotaDialog = addDomain('<div id="quotaDialog" class="quotaDialog"><br/><b><fmt:message key="STM_CHARS_QUOTA_REACHED_MESSAGE"/></b></div>', 'NEGESO_CMS_STM_CHARS_QUOTA');
    
    function addDomain(str, productType) {
        return str.replace(productType, productType + '&domainName=' + window.location.hostname.replace('www.', ''));
    }
    
    var busy = ${"busy" eq status};

    var STATUS_TRANSLATING = "Translating... ";
    var STATUS_DONE = "Done!";
    var ERROR_THE_SAME_LANGUAGES = '<fmt:message key="STM_ERROR_THE_SAME_LANGUAGES"/>'
    var DELETE_BUTTON = '<fmt:message key="DELETE"/>'
    var ADD_BUTTON = '<fmt:message key="ADD"/>'
    var delimiter = ';';

    if (busy) {
        setTimeout('checkStatus()', 2000);
    }

    function addLang() {
        removeDialogs();
        $(document.body).append(dialog);

        $("#addLangDialog").dialog({
            buttons : {
                '<fmt:message key="Cancel"/>' : function() {
                    $(this).remove();
                },
                '<fmt:message key="Ok"/>' : function() {
                    if (isExtraLangsAvailable) {
                        $.post("/admin/stm_settings.html", {
                            act : "addLang",
                            language : $("#language").val(),
                            code : $("#langsSelect option:selected").val()
                        }, function(data) {
                            window.location.reload();
                        });
                    } else {
                           $(this).remove();
                    }
                }
            }
        });

        $("#addLangDialog").bind("dialogclose", function(event, ui) {
            $(this).remove();
        })

        
        $("#addLangDialog").dialog('open');
        $("#hiddenLangsSelect option").each(function() {
            $("#langsSelect").append($('<option></option>').val($(this).val()).html($(this).html()));
        });
        $("#langsSelect option").each(function() {
            if($(this).val()=='') {
                $(this).remove();
            }
        });
    }
    
    function showWords() {
        removeDialogs();
        var dialog2 = '<div id="showWordsDialog" class="showWordsDialog" title="(' + $("select[name=from] option:selected").html() + ') <fmt:message key="STM_NON_TRANSLATABLE_WORDS"/>"><table></table></div>';
        var langIdValue = $("select[name=from] option:selected").val();
        $(document.body).append(dialog2);
        $.ajax({
            async : false,
            type : "GET",
            url : "/admin/stm_settings.html?method=listNonTranslatableWords&langId=" + langIdValue,
            dataType : "text",
            success : function(text, stat) {
                var words = text.split(delimiter);
                for (i = 0; i < words.length; i++) {
                    addWordLine(words[i]);
                }
                $('#showWordsDialog').append(buildButton("addWordLine('');", ADD_BUTTON));
            },
            error : function(text, stat) {
                alert('Internal server error');
            }
        });
        $("#showWordsDialog").dialog({
            width: 350,
            buttons : {
                '<fmt:message key="Cancel"/>' : function() {
                    $(this).remove();
                },
                '<fmt:message key="Ok"/>' : function() {
                    var nonTranslatableWordsValue = '';
                    $("input[name=nonTranslateWord]").each(function() {
                        if ($.trim($(this).val()) != '') {
                            nonTranslatableWordsValue = nonTranslatableWordsValue + $(this).val() + delimiter;
                        }
                    });
                    $.post("/admin/stm_settings.html", {
                        method : "listNonTranslatableWords",
                        act: "save",
                        langId : langIdValue,
                        nonTranslatableWords : nonTranslatableWordsValue
                    }, function(data) {
                        $("#showWordsDialog").remove();
                    });
                }
            }
        });

        $("#showWordsDialog").bind("dialogclose", function(event, ui) {
            $(this).remove();
        })

        $("#showWordsDialog").dialog('open');
    }
    
    function removeDialogs() {
        $("[class^=addLangDialog]").each(function() {
            $(this).remove();
        });
        $("[class^=showWordsDialog]").each(function() {
            $(this).remove();
        });
        $("[class^=quotaDialog]").each(function() {
            $(this).remove();
        });
    }
    
    function buildButton(functionName, value) {
        return '<div class="admNavPanelInp" style="float: left; padding-left: 0px;">' +
            '<div class="imgL"></div>' +
            '<div>' +
            '<input type="button" onclick="' + functionName + '" name="selectImageButton"' + 'value="'+value+'">' +
            '</div>' +
        '<div class="imgR"></div></div>';
    }
    
    function deleteWordLine(el){
        $(el).parents('tr[class^=wordLine]').remove();
    }
    
    function addWordLine(word) {
        $('#showWordsDialog table').append('<tr class="wordLine"><td><input type="text" class="admTextArea admWidth200" name="nonTranslateWord" value="' + word + '"/></td><td>' + 
                buildButton('deleteWordLine(this)', DELETE_BUTTON) + '</td></tr>')
    }

    function translateSite() {
        if ($("#stmCharsQuota").html() <= 0) {
            removeDialogs();
            $(document.body).append(quotaDialog);
            $("#quotaDialog").dialog({
                width: 350,
                buttons : {
                    '<fmt:message key="Cancel"/>' : function() {
                        $(this).remove();
                    }
                }
            });
            $("#quotaDialog").bind("dialogclose", function(event, ui) {
                $(this).remove();
            });
            $("#quotaDialog").dialog('open');
            return;
        }
        if (!validate()) {
            return;
        }

        if ($("#translate_all").attr("checked")
                && !confirm('<fmt:message key="STM_SURE_TRANSLATE_ALL" />')) {
            return;
        }

        $("#statusBlock").html(STATUS_TRANSLATING);

        $.ajax({
            type : "POST",
            url : "/admin/stm_settings.html?act=translate" + "&from="
                    + $("select[name=from] option:selected").val() + "&to="
                    + $("select[name=to] option:selected").val() + "&strategy="
                    + $("input[type=radio][name=strategy]:checked").val()
                    + '&isPageNameTranslatable='
                    + $("input[name=isPageNameTranslatable]").is(":checked")
                    + '&isTranslateLinks='
                    + $("input[name=isTranslateLinks]").is(":checked")
                    + '&isWithoutHtmlTags='
                    + $("input[name=isWithoutHtmlTags]").is(":checked"),
            dataType : "html",
            success : function(html, stat) {
            },
            error : function(html, stat) {
            }
        });
        busy = true;
        setTimeout('checkStatus()', 2000);
    }

    function checkStatus() {
        $.ajax({
            async : false,
            type : "POST",
            url : "/admin/stm_settings.html?act=isBusy",
            dataType : "html",
            success : function(html, stat) {
                if (html == "false" ) {
                    checkTranslatedChars();
                    busy = false;
                } else {
                    $("#statusBlock").html(STATUS_TRANSLATING + html);
                }
            },
            error : function(html, stat) {
            }
        });

        if (busy) {
            setTimeout('checkStatus()', 2000);
        }
    }
    
    function checkTranslatedChars() {
        $.ajax({
            async : false,
            type : "POST",
            url : "/admin/stm_settings.html?act=checkTranslatedCharsCount",
            dataType : "html",
            success : function(html, stat) {
               $("#statusBlock").html(STATUS_DONE + " " + html + " characters");
            },
            error : function(html, stat) {
            }
        });
    }

    function validate() {
        var divs = document.getElementsByTagName("div");

        for ( var i = 0; i < divs.length; i++) {
            if (divs[i].id.indexOf("not_valid") == 0) {
                divs[i].style.display = "none";
            }
        }

        if ($("select[name=from] option:selected").val() == $(
                "select[name=to] option:selected").val()) {
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

                el.innerHTML = "<table cellspacing=\"0\" cellpadding=\"0\" style=\"height: 12px; margin: 0; padding: 0; border: 0;\"><tr style=\"height: 12px;\"><td style=\"font-size: 10px; color: red; border: 0; padding: 0; text-align: left; vertical-align: middle; height: 12px;\">"
                        + ERROR_THE_SAME_LANGUAGES + "</td></tr></table>";
                form = document.forms['siteTranslationForm'];

                if (form.isPageNameTranslatable.nextSibling)
                    form.isPageNameTranslatable.parentNode.insertBefore(el,
                            form.isPageNameTranslatable.nextSibling);
                else
                    form.isPageNameTranslatable.parentNode.appendChild(el);

            } catch (e) {
            }
            return false;
        }
        return true;
    }
    
    function deleteLang() {
        if (confirm("Are you sure you want to delete language: " +  $("#deleteLangId option:selected").html())) {
            document.forms['deleteLangForm'].submit();
        }
    }
    
    function tempRemarkSave() {
        $('input[name=remarkForLanguage_' + $('#remarkForLanguageId').attr('savedValue') + ']').val($('#remarkForLanguage').val());
        $('#remarkForLanguage').val($('input[name=remarkForLanguage_' + $('#remarkForLanguageId option:selected').val() +']').val());
    }
    function goToGeoIpLanguages(){
        $("body").append('<form method="get" id="goToGeoIpLanguages" name="goToGeoIpLanguages" action="/admin/geoip_languages.html"></form>');
        document.forms['goToGeoIpLanguages'].submit();
    }
    
    $(function(){
        $("select#remarkForLanguageId").trigger("change");
    });
    
</script>
</head>

<body>
    <negeso:admin>
        <div id="translatedPages" style="display: none">
            <fmt:message key="STM_TRANSLATED_PAGES" />
            <c:if test="${translatedPages != null}">
                <c:forEach items="${translatedPages}" var="translatedPage">
                    ${translatedPage.filename};
                </c:forEach>
            </c:if>
        </div>

        <select id="hiddenLangsSelect" style="display: none;">
            <c:forEach var="lang" items="${supportedLangs}">
                <c:set var="isPresent" value="false"/>
                <c:forEach var="lang2" items="${currentLangs}">
                    <c:if test="${lang.code == lang2.code}"><c:set var="isPresent" value="true"/></c:if>
                </c:forEach>
                <c:if test="${not (isPresent)}">
                    <option value="${lang.code}">${lang.language}</option>
                </c:if>
            </c:forEach>
        </select>

        <table align="center" border="0" cellpadding="0" cellspacing="0"
            class="admTable">
            <tr>
                <td align="center" class="admNavPanelFont" style="height: auto;">
                    <fmt:message key="STM_SETTINGS" />
                </td>
            </tr>
            <tr>
                <td class="admTableTDLast">
                    <fmt:message key="STM_LICENCE_TEXT1" />&#160;<b>${licenseLangsLimit}</b>&#160;<fmt:message key="STM_LICENCE_TEXT2" />
                </td>
            </tr>
            <tr>
                <td class="admTableTDLast">
                    Chars quota&#160;<b><span id="stmCharsQuota">${stmCharsQuota}</span></b>
                </td>
            </tr>
            <tr>
                <td class="admTableTDLast">
                    <form action="" method="post" id="siteTranslationForm">
                        <table border="0" cellpadding="" cellspacing="0">
                            <tr>
                                <td>
                                    <fmt:message key="STM_FROM" />:
                                    <select name="from">
                                        <c:forEach var="lang" items="${currentLangs}">                                            
                                            <option value="${lang.id}">${lang.language}</option>
                                        </c:forEach>
                                    </select>
                                    <fmt:message key="STM_TO" />:
                                    <select name="to">
                                        <option></option>
                                        <c:forEach var="lang" items="${currentLangs}">
                                            <c:set var="locked" value="false"></c:set>
                                            <c:forEach var="entry" items="${lockedLanguage.customFields}">
                                                <c:if test="${entry.key == lang.id && entry.value.value == 'true'}">
                                                    <c:set var="locked" value="true"></c:set>
                                                </c:if>
                                            </c:forEach>
                                            <c:if test="${locked == 'false'}">
                                                <option value="${lang.id}">${lang.language}</option>
                                            </c:if> 
                                        </c:forEach>
                                    </select>
                                    &#160;&#160;
                                    <fmt:message key="STM_INCLUDING_PAGE_NAME" />:
                                    <input type="checkbox" name="isPageNameTranslatable"
                                        value="true"/>
                                     &#160;&#160;
                                    <fmt:message key="STM_TRANSLATE_LINKS" />:
                                    <input type="checkbox" name="isTranslateLinks"
                                        value="true"/>
                                     &#160;&#160;
                                    Without html tags:
                                    <input type="checkbox" name="isWithoutHtmlTags"
                                        value="true"/>
                                          <br>
                                            <br>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <input type="radio" id="translate_all" name="strategy"
                                        value="0" checked="checked" />
                                    &#160;
                                    <fmt:message key="STM_TRANSLATE_ALL" />
                                    <br>
                                        <br>
                                            <input type="radio" name="strategy" value="1" />
                                            &#160;
                                            <fmt:message key="STM_TRANSLATE_NEW_ONLY" />
                                            <br>
                                                <br>
                                                    <input type="radio" name="strategy" value="2" />
                                                    &#160;
                                                    <fmt:message key="STM_TRANSLATE_ALL_EXCEPT_NEW" />
                                                    <br>
                                                        <br>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <div class="admNavPanelInp" style="float: left; padding-left: 0px;">
                                        <div class="imgL"></div>
                                        <div>
                                            <input type="button" onclick="translateSite();" name="selectImageButton"
                                                value="<fmt:message key="STM_TRANSLATE" />">
                                        </div>
                                        <div class="imgR"></div>
                                    </div>
                                </td>
                            </tr>
                        </table>
                    </form>
                </td>
            </tr>

            <tr></tr>

            <tr>
                <td class="admTableTDLast" id="statusBlock">
                    <c:if test="${not empty status}">
                        <c:choose>
                            <c:when test="${status eq 'busy'}">Translating...</c:when>
                            <c:otherwise>
                                ${status}
                            </c:otherwise>
                        </c:choose>
                    </c:if>
                </td>
            </tr>
            <form method="POST" >
                <tr>
                    <td class="admTableTDLast">
                        <fmt:message key="STM_LOCK_LANGUAGE" />:
                    </td>
                </tr>
                 <tr>
                    <td class="admTableTDLast">
                        <c:forEach var="lang" items="${currentLangs}">
                            <c:set var="checked" value="false"></c:set>
                            <c:forEach var="entry" items="${lockedLanguage.customFields}">
                                <c:if test="${entry.key == lang.id && entry.value.value == 'true'}">
                                    <c:set var="checked" value="true"></c:set>
                                </c:if>
                            </c:forEach>
                            <div style="float: left; padding-left: 10px; width: 100px;">
                                <input type="checkbox" value="${lang.id}" name="lockedLanguage"
                                    <c:if test="${checked}">checked="checked"</c:if> 
                                >&#160;${lang.language}
                            </div>
                        </c:forEach>
                    </td>
                 </tr>
                <tr>
                   <input type="hidden" name="method" value="saveParams"/>
                    <td class="admTableTDLast">
                        <fmt:message key="STM_CHECK_GRAMMAR" />:
                    </td>
                </tr>
                <tr>
                    <td class="admTableTDLast">
                        <c:forEach var="lang" items="${currentLangs}">
                            <c:set var="checked" value="false"></c:set>
                            <c:forEach var="entry" items="${requestForGrammarCheck.customFields}">
                                <c:if test="${entry.key == lang.id && entry.value.value == 'true'}">
                                    <c:set var="checked" value="true"></c:set>
                                </c:if>
                            </c:forEach>
                            <div style="float: left; padding-left: 10px; width: 100px;">
                                <input type="checkbox" value="${lang.id}" name="requestForGrammarCheck"
                                    <c:if test="${checked == 'true'}">checked="checked" disabled="disabled"</c:if> 
                                >&#160;${lang.language}
                            </div>
                        </c:forEach>
                    </td>
                </tr>
                <tr>
                   <td class="admTableTDLast">
                       <fmt:message key="STM_REMARK_DESCRIPTION" />                   
                   </td>
                </tr>
                <tr>
                    <td class="admTableTDLast">
                        <div style="float: left; padding-top: 8px; width: 180px;">
                            <fmt:message key="STM_REMARK" />:
                            <select onchange="tempRemarkSave()" id="remarkForLanguageId" name="remarkForLanguageId" onclick="$(this).attr('savedValue', $(this, 'option:selected').val())">
                                <c:forEach var="lang" items="${currentLangs}" varStatus="index">
                                    <option value="${lang.id}"
                                       <c:if test="${index.index == 0}">selected="selected"</c:if> 
                                    >${lang.language}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div style="padding-top: 8px;">
                        <textarea rows="10" cols="65" name="remarkForLanguage" id="remarkForLanguage"></textarea>
                        <c:forEach var="lang" items="${currentLangs}">
                            <c:set var="paramValue" value=""></c:set>
                            <c:forEach var="entry" items="${remarkForLanguage.customFields}">
                                <c:if test="${entry.key == lang.id}">
                                    <c:set var="paramValue" value="${entry.value.value}"></c:set>
                                </c:if>
                            </c:forEach>
                                <input type="hidden" value="${paramValue}" name="remarkForLanguage_${lang.id}"/>
                        </c:forEach>
                        </div>
                    </td>
                </tr>
                 <tr>
                    <td>
                        <div class="admNavPanelInp" style="float: left; padding-left: 0px;">
                            <div class="imgL"></div>
                            <div>
                                <input type="submit" name="selectImageButton"
                                    value="<fmt:message key="SAVE" />">
                            </div>
                            <div class="imgR"></div>
                        </div>
                    </td>
                </tr>
            </form>
            <tr>
                <td class="admTableTDLast">&nbsp;
                </td>
            </tr>
            <form method="POST" name="deleteLangForm" >
                <tr>
                    <td>
                       <input type="hidden" name="method" value="deleteLang"/>
                        <div style="float: left; padding-top: 8px;">
                           <fmt:message key="STM_DELETE_LABEL" />:&#160;
                        </div>
                        <div style="float: left; padding-top: 8px;">
                            <select name="langId" id="deleteLangId">
                                 <option value="-1"></option>
                                <c:forEach var="lang" items="${currentLangs}">
                                    <c:set var="locked" value="false"></c:set>
                                    <c:forEach var="entry" items="${lockedLanguage.customFields}">
                                        <c:if test="${entry.key == lang.id && entry.value.value == 'true'}">
                                            <c:set var="locked" value="true"></c:set>
                                        </c:if>
                                    </c:forEach>
                                    <c:if test="${locked == 'false'}">
                                        <option value="${lang.id}">${lang.language}</option>
                                    </c:if> 
                                </c:forEach>
                        </select>
                        &#160;
                        </div>
                        <div class="admNavPanelInp" style="float: left; padding-left: 0px;">
                                <div class="imgL"></div>
                                <div>
                                    <input type="button" name="selectImageButton" onclick="deleteLang();"
                                        value="<fmt:message key="DELETE" />">
                                </div>
                                <div class="imgR"></div>
                            </div>
                    </td>
                </tr>
            </form>
            <tr>
                <td class="admTableFooter">&nbsp;
                </td>
            </tr>
        </table>
    </negeso:admin>

    <table cellpadding="0" cellspacing="0" width="764px" align="center"
        border="0" class="Buttons">
        <tr>
            <td>
                <div class="admBtnGreenb">
                    <div class="imgL"></div>
                    <div>
                        <input type="submit" onClick="addLang();" name="saveform"
                            value='<fmt:message key="STM_ADD_LANG_TO_SITE"/>' />
                    </div>
                    <div class="imgR"></div>
                </div>
            </td>
            <td>
                <div class="admBtnGreenb">
                    <div class="imgL"></div>
                    <div>
                        <input type="submit" onClick="showWords();" 
                            value='<fmt:message key="STM_NON_TRANSLATABLE_WORDS"/>' />
                    </div>
                    <div class="imgR"></div>
                </div>
            </td>
            <td>
                <div class="admBtnGreenb">
                    <div class="imgL"></div>
                    <div>
                        <input type="submit" onClick="goToGeoIpLanguages();" 
                            value='<fmt:message key="CM_GEO_IP_LANGUAGES"/>' />
                    </div>
                    <div class="imgR"></div>
                </div>
            </td>
        </tr>
    </table>
</body>
</html>