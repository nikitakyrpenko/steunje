<?xml version="1.0" encoding="utf-8"?>
<!--
  @(#)$Id$     
 
  Copyright (c) 2004 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.
 
  Administrative web-GUI of Inquiry Module.

  @author       Stanislav Demchenko
  @version      $Revision$
-->

<xsl:stylesheet version="1.1"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:negeso="http://negeso.com/2003/Framework"
    xmlns:java="http://xml.apache.org/xslt/java"
    exclude-result-prefixes="java">

<!-- Include/Import -->
<xsl:include href="/xsl/negeso_body.xsl"/>
<xsl:include href="/xsl/admin_templates.xsl"/>
    
<xsl:variable name="lang" select="/*/@interface-language"/>
<xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>
<xsl:variable name="dict_inquiry" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_inquiry.xsl', $lang)"/>
    <xsl:variable name="dict_news_module" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_news_module.xsl', $lang)"/>
<xsl:variable name="view" select="/negeso:model/@view"/>

<!-- **************** BEGIN MAIN TEMPLATE (COMMON FOR ALL MODES) *********** -->
<xsl:template match="/">
    <xsl:if test="not(negeso:model[@view='PREVIEW_QUESTIONNAIRE'])">
        <html>
            <head>
                <title><xsl:value-of select="java:getString($dict_inquiry, 'INQUIRY_MODULE')"/></title>
                <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
        
                <script src="/script/jquery.min.js" type="text/javascript"/>
              <link rel="stylesheet" type="text/css" href="/css/jquery-ui.custom.css"/>
              
              <script type="text/javascript" src="/script/jquery.min.js"></script>
              <script type="text/javascript" src="/script/jquery-ui.custom.min.js" />

                <script type="text/javascript" src="/script/cufon-yui.js"></script>
                <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
                <script type="text/javascript" src="/script/common_functions.js"></script>     
                <script type="text/javascript" src="/script/conf.js"/>
                <script src="/script/calendar_picker.js" type="text/javascript"/>
            
              <script>
                  var s_InvalidPublishDate = "<xsl:value-of select="java:getString($dict_news_module, 'INVALID_PUBLISH_DATE')"/>";
                  var s_InvalidExpiredDate = "<xsl:value-of select="java:getString($dict_news_module, 'INVALID_EXPIRED_DATE')"/>";

                function confirmDelete(targetUrl) {
                if (confirm("<xsl:value-of select="java:getString($dict_common, 'DELETE_CONFIRMATION')"/>")) {
                location.href = targetUrl;
                }
                }

                function copyQuestionnaire(title, id) {
                var res = prompt("<xsl:value-of select="java:getString($dict_inquiry, 'ENTER_NEW_NAME_FOR')"/> '" + title + "':", "Copy of " + title);
                if(res != null) {
                var url = "?action=copyQuestionnaire&amp;id=" + id +
                "&amp;title=" + res;
                location.href = url;
                }
                }

                function copyQuestion(title, id) {
                var res = prompt("<xsl:value-of select="java:getString($dict_inquiry, 'ENTER_NEW_NAME_FOR')"/> '" + title + "':", "Copy of " + title);
                if(res != null) {
                var url = "?action=copyQuestion&amp;id=" + id +
                "&amp;title=" + res;
                location.href = url;
                }
                }

                function getDateTimeFromXsl(xslDate) {
                if(xslDate == null || xslDate == "") {
                return null;
                } else {
                var date = new Date(
                xslDate.substring(0, 4),
                xslDate.substring(5, 7),
                xslDate.substring(8, 10),
                xslDate.substring(11, 13),
                xslDate.substring(14, 16),
                xslDate.substring(17, 19)
                );
                return (
                StringUtil.prependZero(date.getDate()) + "-" +
                StringUtil.prependZero(date.getMonth()) + "-" +
                StringUtil.prependZero(date.getYear()) + " " +
                StringUtil.prependZero(date.getHours()) + ":" +
                StringUtil.prependZero(date.getMinutes())
                );
                }
                }

                function moveQuestion(id1, id2) {
                if(id2 == "") return;
                location.href = "?action=moveQuestion&amp;id1=" + id1 + "&amp;id2=" + id2;
                }

                /** Returns date "CCYY-MM-DDThh:mm:ssZ" as dd-mm-yyyy */
                function formatDate(canonDate){
                canonDate = "" + canonDate;
                if(canonDate.charAt(4) != '-' || canonDate.charAt(7) != '-') return "";
                return (canonDate.substring(8, 10) + "-" +
                canonDate.substring(5, 7) + "-" +
                canonDate.substring(0, 4) );
                }

                function importUsers(){
                if (manageUsersForm.import_file.value == null || manageUsersForm.import_file.value == ""){
                alert("<xsl:value-of select="java:getString($dict_inquiry, 'YOU_MUST_ATTACH_A_VALID_FILE')"/>");
                } else {
                manageUsersForm.action.value = "importUsers";
                manageUsersForm.submit();
                }
                }

                function editSubject(divName, action) {
                var res = prompt("<xsl:value-of select="java:getString($dict_inquiry, 'EDIT_EMAIL_SUBJECT')"/>", document.getElementById(divName).getAttribute('emailSubject'));
                if(res != null) {
                formToUpdateEmailSubject.action.value = action;
                formToUpdateEmailSubject.emailSubject.value = res;
                formToUpdateEmailSubject.submit();
                }
                }

                function selectUsersToRemindpasswordsTo(){
                var args = new Array();
                args.userEmailList = new Array(
                <xsl:for-each select="//negeso:inquiry_user">
                  "<xsl:value-of select='@email'/>"<xsl:if test='position()!=last()'>,</xsl:if>
                </xsl:for-each>);
                args.userIdList = new Array(
                <xsl:for-each select="//negeso:inquiry_user">
                  "<xsl:value-of select='@id'/>"<xsl:if test='position()!=last()'>,</xsl:if>
                </xsl:for-each>);
                args.header= "<xsl:value-of select="java:getString($dict_inquiry, 'PASSWORD_REMINDER')"/>";
                args.select = "<xsl:value-of select="java:getString($dict_inquiry, 'SELECT_')"/>";
                args.checkAll = "<xsl:value-of select="java:getString($dict_inquiry, 'SELECT_ALL')"/>";
                args.uncheckAll = "<xsl:value-of select="java:getString($dict_inquiry, 'SELECT_NONE')"/>";
                args.save= "<xsl:value-of select="java:getString($dict_common, 'SEND')"/>";
                args.cancel= "<xsl:value-of select="java:getString($dict_common, 'CANCEL')"/>";
                window.showModalDialog("/dialogs/password_reminder.html", args, "dialogHeight: 400px; dialogWidth: 400px")
                    .then(function(ret){
                if (ret!=null ){
                if (ret!=""){
                tab1.style.display="none";
                tab2.style.display="none";
                div1.style.display="block";
                manageUsersForm.action.value = "sendPasswordReminder";
                manageUsersForm.remind_ids.value = ret;
                manageUsersForm.submit();
                }
                }
                    });
                }

                function sendQuestionaireReminder(quizId){
                if (confirm("<xsl:value-of select="java:getString($dict_inquiry, 'SEND_QUESTIONNARIE_REMINDER_EMAIL')"/>?")){
                tab1.style.display="none";
                tab2.style.display="none";
                div1.style.display="block";
                location.href = "?action=sendQuestionaireReminder&amp;id=" + quizId;
                }
                }

                <xsl:if test="$view='FORM_QUESTION'">
                  function addOption() {
                  var title = document.formEditQuestion.optionTitle.value;
                  if (title.length == 0) {
                  alert("<xsl:value-of select="java:getString($dict_inquiry, 'TITLE_IS_NOT_SET')"/>");
                  } else {
                  formEditQuestion.action.value='storeOption';
                  document.formEditQuestion.submit();
                  }
                  }

                  function editOption(id, titleValue) {
                  var res = prompt("<xsl:value-of select="java:getString($dict_common, 'TITLE')"/>", titleValue);
                  if (res != null) {
                  if (res.length == 0) {
                  alert("<xsl:value-of select="java:getString($dict_inquiry, 'TITLE_IS_NOT_SET')"/>");
                  editOption(id, res);
                  }
                  else {
                  formEditQuestion.action.value = 'storeOption';
                  addField(formEditQuestion, 'hidden', 'editableOptionId', id);
                  addField(formEditQuestion, 'hidden', 'newOptionTitle', res);
                  document.formEditQuestion.submit();
                  }
                  }
                  }

                  function addField(form, fieldType, fieldName, fieldValue) {
                  if (document.getElementById) {
                  var input = document.createElement('INPUT');
                  if (document.all) {
                  input.type = fieldType;
                  input.name = fieldName;
                  input.value = fieldValue;
                  }
                  else if (document.getElementById) {
                  input.setAttribute('type', fieldType);
                  input.setAttribute('name', fieldName);
                  input.setAttribute('value', fieldValue);
                  }
                  form.appendChild(input);
                  }
                  }

                  function moveOption(id1, id2) {
                  if(id2 == "") return;
                  formEditQuestion.action.value = 'moveOption';
                  addField(formEditQuestion, 'hidden', 'id1', id1);
                  addField(formEditQuestion, 'hidden', 'id2', id2);
                  formEditQuestion.submit();
                  }

                  function redirectQuestion() {
                  formEditQuestion.action.value='redirectQuestion';
                  formEditQuestion.submit();
                  }

                  function storeQuestion() {
                  formEditQuestion.action.value='storeQuestion';
                  formEditQuestion.submit();
                  }

                  var previewQuestionnaireURL = "{//negeso:questionnaire/@previewUrl}";

                  function previewQuestionnaire(questionnaireId) {
                  formEditQuestion.action.value = 'previewQuestionnaire';
                  formEditQuestion.target = "about:blank";
                  formEditQuestion.submit();
                  formEditQuestion.target = "";
                  }

                  function deleteOption(id) {
                  if (confirm("<xsl:value-of select="java:getString($dict_common, 'DELETE_CONFIRMATION')"/>")) {
                  formEditQuestion.action.value='deleteOption';
                  addField(formEditQuestion, 'hidden', 'optionId', id);
                  formEditQuestion.submit();
                  }
                  }

                </xsl:if>

              </script>
              
              
         <style>
            a.admAnchorBlue {
                text-decoration: underline;
                font : normal;
            }
        </style> 
              
<script language="JavaScript">  

        <xsl:text disable-output-escaping="yes">
        <![CDATA[

        function onSubmit(close) {

            if (document.formEditQuestionnaire.publish.value != '' &&
                checkDate(document.formEditQuestionnaire.publish.value) == false)
            {
                alert(s_InvalidPublishDate);
                return;
            }
            if (document.formEditQuestionnaire.expired.value != '' &&
                checkDate(document.formEditQuestionnaire.expired.value) == false)
            {
                alert(s_InvalidExpiredDate);
                return;
            }
            /*if (document.formEditQuestionnaire.viewableDateField.value != '' &&
                checkDate(document.formEditQuestionnaire.viewableDateField.value) == false)
            {
                alert(s_InvalidViewableDate);
                return;
            }
            setItemVisibility();*/
            if (close == null)
                //formEditQuestionnaire.listId.value = "";
            formEditQuestionnaire.submit();
        }

        function checkDate(val){
           
            if(/^(\d{1,2})\-(\d{1,2})\-(\d{1,4})$/.test(val)){
                year=parseInt(RegExp.$1,10)
                if(year<100) year=(year<70)?2000+year:1900+year
                month=RegExp.$2-1
                date=parseInt(RegExp.$3,10)
                d1=new Date(year, month, date)
                newYear=d1.getYear()
                if(newYear<100) newYear=(newYear<70)?2000+newYear:1900+newYear
                if(month==d1.getMonth() && date==d1.getDate() && year==newYear){
                    month ++
                    return (date<10?'0'+date:''+date)+(month<10?'.0'+month:'.'+month)+'.'+newYear
                }
            } else return false;
        }

function prependZero(num){
            return num > 9 ? "" + num : "0" + num;
        }
  var today = new Date();
        var today = new Date(today.getFullYear(), today.getMonth(), today.getDate());

  function addExpiredDate() {    
 document.getElementById("publishDatepicker").value = "";         
    document.formEditQuestionnaire.expired.value = 
               prependZero(today.getDate())  + "-" + 
                prependZero(today.getMonth() + 1) + "-" + 
                prependZero(today.getFullYear());            
        }

  function addpublishDateField() {  
 document.getElementById("expiredDatepicker").value = "";          
    document.formEditQuestionnaire.publish.value = 
                   prependZero(today.getDate())  + "-" + 
                prependZero(today.getMonth() + 1) + "-" + 
                prependZero(today.getFullYear());          
        }
    

function addviewableDateField() {            
    document.formEditQuestionnaire.viewableDateField.value = 
                   prependZero(today.getDate())  + "-" + 
                prependZero(today.getMonth() + 1) + "-" + 
                prependZero(today.getFullYear());        
        }
    
 function clearviewableDateField() {            
            document.formEditQuestionnaire.viewableDateField.value = "";
      }

       
        ]]>
       </xsl:text>
    </script>
              
                <xsl:call-template name="adminhead"/>
            </head>
          <body style="behavior: url(/script/webservice.htc) url(/script/clientmanager.htc)"
        id="ClientManager" xmlID="{@id}">
            <xsl:call-template name="NegesoBody">
                    <xsl:with-param name="helpLink" select="'/admin/help/cms-help_nl.html'"/>
                    <xsl:with-param name="backLink" select="'inquiry'"/>
                </xsl:call-template>           
              
              <xsl:if test="$view='FORM_QUESTIONNAIRE'">
                <xsl:call-template name="buttons_qustionnaire"/>
              </xsl:if>
              <xsl:if test="$view='FORM_USER'">
                <xsl:call-template name="buttons_form_user"/>
              </xsl:if>
              <xsl:if test="$view='FORM_QUESTION'">
                <xsl:call-template name="buttons_form_question"/>
              </xsl:if>              
              
                <script>
                    Cufon.now();
                    Cufon.replace('.admNavPanelFont', { fontFamily: 'Orator Std' });
                    Cufon.replace('.admTDtitles', { fontFamily: 'Orator Std' });
                </script>          
            </body>
        </html>
    </xsl:if>
  
  
    <xsl:if test="negeso:model[@view='PREVIEW_QUESTIONNAIRE']">
      <html>
        <head>
          <script language="JavaScript">
            var url = "/<xsl:value-of select='//negeso:questionnaire/@previewUrl'/>?" +
              "inquiry_questionnaire_id=<xsl:value-of select='//negeso:questionnaire/@id'/>"
              + "&amp;mode=questionnaire"
              + "&amp;preview=true"
              + "&amp;inquiry_question_id=<xsl:value-of select="//@inquiry_question_id"/>";
            location.href = url;
          </script>
        </head>
      </html>
    </xsl:if>  
</xsl:template>  
  
<xsl:template match="negeso:model" mode="admContent">
    <table align="center"  border="0" cellpadding="0" cellspacing="0" class="admTable">        
        <tr>
            <td style="width:auto; height:auto; padding-left:20px;">

                <ul style="color: red;">
                    <xsl:for-each select="//negeso:error">
                        <li>
                            <xsl:value-of select="."/>
                        </li>
                    </xsl:for-each>
                </ul>
            </td>
        </tr>
        <tr>
            <td class="admTableTDLast" colspan="7">    
                <xsl:call-template name="inq_path"/>
            </td>
        </tr>        
        <!-- CONTENT -->
        <xsl:apply-templates select="."/>
      <tr>
        <td class="admTableFooter" >&#160;</td>
      </tr>   
    </table>
</xsl:template>

<xsl:template name="inq_path">
    <!-- BEGIN INNER PATH OF THE MODULE -->  
                <a href="/admin/inquiry" class="admLocation">
                    <xsl:value-of select="java:getString($dict_inquiry, 'QUESTIONNAIRES')"/>
                </a> 
            <xsl:if test="$view='FORM_QUESTIONNAIRE'">               
                    &#160; <img src="/images/navig_arrow.png"  style="vertical-align:middle;"/>&#160;
                    <span>
                        <a href="#" class="admLocation">
                            <xsl:if test="//negeso:questionnaire/@id != 0">
                                <xsl:value-of select="java:getString($dict_inquiry, 'QUESTIONNAIRE_PROPERTIES')"/>
                            </xsl:if>
                            <xsl:if test="//negeso:questionnaire/@id = 0">
                                <xsl:value-of select="java:getString($dict_inquiry, 'NEW_QUESTIONNAIRE')"/>
                            </xsl:if>
                        </a>
                    </span>
                
            </xsl:if>
            <xsl:if test="$view='VIEW_QUESTIONS' or $view='FORM_QUESTION'">                
                    &#160; <img src="/images/navig_arrow.png"  style="vertical-align:middle;"/>&#160;
                    <span>
                        <a href="?action=viewQuestions&amp;id={//negeso:questionnaire/@id}" class="admLocation">
                            <xsl:value-of select="java:getString($dict_inquiry, 'QUESTIONS')"/>
                        </a>
                    </span>                
            </xsl:if>
            <xsl:if test="$view='VIEW_RESPONDENTS' or $view='VIEW_RESPONDENT'">              
                    &#160; <img src="/images/navig_arrow.png"  style="vertical-align:middle;"/>&#160;
                    <span>
                        <a href="?action=viewRespondents&amp;id={//negeso:questionnaire/@id}" class="admLocation">
                            <xsl:value-of select="java:getString($dict_inquiry, 'RESPONDENTS')"/>
                        </a>
                    </span>
              
            </xsl:if>
            <xsl:if test="$view='VIEW_STATISTICS'">             
                    &#160; <img src="/images/navig_arrow.png"  style="vertical-align:middle;"/>&#160;
                    <span>
                        <a href="?action=viewStatistics&amp;id={//negeso:questionnaire/@id}" class="admLocation">
                            <xsl:value-of select="java:getString($dict_inquiry, 'STATISTICS')"/>
                        </a>
                    </span>               
            </xsl:if>
            <xsl:if test="$view='VIEW_RESPONDENT'">               
                    &#160; <img src="/images/navig_arrow.png"  style="vertical-align:middle;"/>&#160;
                    <span>
                        <a href="?action=viewRespondent&amp;id={//negeso:respondent/@id}" class="admLocation">
                            <xsl:value-of select="java:getString($dict_inquiry, 'RESPONDENT')"/>
                        </a>
                    </span>
                           </xsl:if>

            <xsl:if test="$view=($view='FORM_QUESTION' and //negeso:question/@id != 0)">                
                    &#160; <img src="/images/navig_arrow.png"  style="vertical-align:middle;"/>&#160;
                    <span>
                        <a href="?action=editQuestion&amp;id={//negeso:question/@id}" class="admLocation">
                            <xsl:value-of select="java:getString($dict_inquiry, 'QUESTION')"/>
                        </a>
                    </span>             
            </xsl:if>

            <xsl:if test="$view='VIEW_USERS' or $view='FORM_USER'">              
                    &#160; <img src="/images/navig_arrow.png"  style="vertical-align:middle;"/>&#160;
                    <span>
                        <a href="?action=viewUsers&amp;id={//negeso:questionnaire/@id}" class="admLocation">
                            <xsl:value-of select="java:getString($dict_inquiry, 'USER_LIST')"/>
                        </a>
                    </span>              
            </xsl:if>

            <xsl:if test="$view='FORM_USER'">             
                    &#160; <img src="/images/navig_arrow.png"  style="vertical-align:middle;"/>&#160;
                    <span>
                        <a href="#" class="admLocation">
                            <xsl:if test="//negeso:inquiry_user/@id != 0">
                                <xsl:value-of select="java:getString($dict_inquiry, 'USER_PROPERTIES')"/>
                            </xsl:if>
                            <xsl:if test="//negeso:inquiry_user/@id = 0">
                                <xsl:value-of select="java:getString($dict_inquiry, 'NEW_USER')"/>
                            </xsl:if>
                        </a>
                    </span>                
            </xsl:if>        
    <!-- END INNER PATH OF THE MODULE -->
</xsl:template>
<!-- ******************* END MAIN TEMPLATE ********************************* -->

<xsl:template match="negeso:model[@view='VIEW_QUESTIONS']">

  <tr>
    <td align="center" class="admNavPanelFont"  colspan="7">
      <xsl:call-template name="tableTitle">
        <xsl:with-param name="headtext">
          Questions
        </xsl:with-param>
      </xsl:call-template>
    </td>
  </tr>
  <tr>
    <td class="admTableTDLast" colspan="7">
      <div class="admNavPanelInp">
        
        <div class="imgL"></div>
        <div>
          <input class="admNavbarInp" type="submit" onClick="location.href='?action=editQuestion&amp;parentId={negeso:questionnaire/@id}'" >
            <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_inquiry, 'ADD_NEW_QUESTION')"/></xsl:attribute>
          </input>
        </div>
        <div class="imgR"></div>
      </div>      
     
    </td>
  </tr>

  <tr>
            <td class="admTDtitles"><xsl:value-of select="java:getString($dict_inquiry, 'QUESTION')"/></td>
            <td class="admTDtitles"><xsl:value-of select="java:getString($dict_common, 'TYPE')"/></td>
            <td class="admTDtitles">&#160;</td>
            <td class="admTDtitles">&#160;</td>
            <td class="admTDtitles">&#160;</td>
            <td class="admTDtitles">&#160;</td>
            <td class="admTDtitles">&#160;</td>
        </tr>
        <xsl:for-each select="negeso:questionnaire/negeso:question">
            <tr>
              <th class="admTableTD">
          <xsl:value-of select="@title"/>
                </th>
              <th class="admTableTD">
                    <xsl:call-template name="qtypes">
                        <xsl:with-param name="name" select="@type"/>
                    </xsl:call-template>
                </th>
                <td class="admTableTDLast admWidth30">
                    <img src="/images/properties.png" class="admHand" onClick="location.href='?action=editQuestion&amp;id={@id}';">
                        <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_inquiry, 'EDIT_QUESTION')"/></xsl:attribute>
                    </img>
                </td>
                <td class="admTableTDLast admWidth30">
                    <img src="/images/move.png" class="admHand" onClick="copyQuestion('{@title}', {@id});">
                        <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'COPY')"/></xsl:attribute>
                    </img>
                </td>
                <td class="admTableTDLast admWidth30">
                    <img class="admHand" src="/images/up.png" onClick="moveQuestion('{@id}','{preceding-sibling::*[1]/@id}');">
                        <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'UP')"/></xsl:attribute>
                    </img>
                </td>
                <td class="admTableTDLast admWidth30">
                    <img class="admHand" src="/images/down.png" onClick="moveQuestion('{@id}','{following-sibling::*[1]/@id}');">            
                        <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'DOWN')"/></xsl:attribute>
                    </img>
                </td>
                <td class="admTableTDLast admWidth30">
                    <img src="/images/delete.png" class="admHand"
                        onClick="confirmDelete('?action=deleteQuestion&amp;id={@id}')"
                    >
                        <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'DELETE')"/></xsl:attribute>
                    </img>
                </td>
            </tr>
        </xsl:for-each>
   
</xsl:template>


<xsl:template match="negeso:model[@view='VIEW_RESPONDENTS']">
  <tr>
    <td align="center" class="admNavPanelFont"  colspan="3">
  <xsl:call-template name="tableTitle">
        <xsl:with-param name="headtext">
            Respondents
        </xsl:with-param>
    </xsl:call-template>
    </td>
  </tr>
        <tr>
            <td class="admNavbar admCenter" colspan="3">

              <div class="admNavPanelInp" >
                <div class="imgL"></div>
                <div>
                  <a target="_blank" href="/admin/inquiry_export.csv?id={negeso:questionnaire/@id}">
                   &#160;<xsl:value-of select="java:getString($dict_inquiry, 'EXPORT_RESULTS')"/>&#160;
                </a>
                </div>
                <div class="imgR"></div>
              </div>
              
               
            </td>
        </tr>
    
   
        <tr>
            <td class="admTDtitles"><xsl:value-of select="java:getString($dict_common, 'TITLE')"/></td>
            <td class="admTDtitles"><xsl:value-of select="java:getString($dict_common, 'DATE')"/></td>
            <td class="admTDtitles">&#160;</td>
        </tr>
        <xsl:for-each select="negeso:questionnaire/negeso:respondent">
            <tr>
                <th class="admTableTD admLeft">
                    <a class="admAnchor" href="?action=viewRespondent&amp;id={@id}">
                        <xsl:choose>
                          <xsl:when test="string-length(@email)">
                            <xsl:value-of select="@email"/>
                          </xsl:when>
                          <xsl:otherwise>
                            Respondent # <xsl:value-of select="@id"/>
                          </xsl:otherwise>
                        </xsl:choose>
                    </a>
                </th>
                <td class="admTableTD admWidth100">
                    <script>document.write(getDateTimeFromXsl("<xsl:value-of select="@submitTime"/>"));</script>
                </td>
                <td class="admTableTDLast admWidth30">
                    <img src="/images/delete.png" class="admHand"  onClick="confirmDelete('?action=deleteRespondent&amp;id={@id}')">
                        <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'DELETE')"/></xsl:attribute>
                    </img>
                </td>
            </tr>
        </xsl:for-each>
    
</xsl:template>

<xsl:template match="negeso:model[@view='VIEW_RESPONDENT']">
    
  <xsl:call-template name="tableTitle">
        <xsl:with-param name="headtext">
      <xsl:choose>
              <xsl:when test="string-length(//negeso:respondent/@email)">
                <xsl:value-of select="//negeso:respondent/@email"/>
              </xsl:when>
              <xsl:otherwise>
                  <xsl:value-of select="java:getString($dict_inquiry, 'RESPONDENT')"/> # <xsl:value-of select="//negeso:respondent/@id"/>
              </xsl:otherwise>
            </xsl:choose>
        </xsl:with-param>
    </xsl:call-template>
    <table cellpadding="0" cellspacing="0" class="admNavPanel">
        <tr>
            <td class="admNamebar" width="50%"><xsl:value-of select="java:getString($dict_inquiry, 'QUESTION')"/></td>
            <td class="admNamebar"><xsl:value-of select="java:getString($dict_inquiry, 'OPTION')"/></td>
        </tr>
        <tr>
            <th class="admTableTD admWidth150"><xsl:value-of select="java:getString($dict_common, 'DATE')"/></th>
            <td class="admTableTDLast admLeft">
                <script>document.write(getDateTimeFromXsl("<xsl:value-of select="//negeso:respondent/@submitTime"/>"));</script>
            </td>
        </tr>
        <xsl:for-each select="//negeso:question">
            <tr>
                <th class="admTableTD admWidth150"  style="vertical-align: top;">
                    <xsl:value-of select="@title"/>
                </th>
                <td class="admTableTDLast admLeft">
                    <div><xsl:value-of select="@answer"/></div>
                    <xsl:for-each select="negeso:option[@checked='true']">
                        <div><xsl:value-of select="@title"/></div>
                    </xsl:for-each>
                    <xsl:if test="string-length(@alternative_answer) != 0">
                      <div>
                        <xsl:value-of select="@alternative"/>:
                          <xsl:value-of select="@alternative_answer"/>
                        </div>
                    </xsl:if>
                    <br/>
                    <b><xsl:value-of select="java:getString($dict_inquiry, 'REMARK')"/>:</b><br/>
                    <xsl:value-of select="@remark"/>
                </td>
            </tr>
        </xsl:for-each>
    </table>
</xsl:template>


<xsl:template match="negeso:model[@view='VIEW_STATISTICS']">
  <tr>
    <td align="center" class="admNavPanelFont"  colspan="2">
    <xsl:call-template name="tableTitle">
        <xsl:with-param name="headtext">
            Statistics
        </xsl:with-param>
    </xsl:call-template>
    </td>
  </tr>
    <xsl:variable name="numRespondents" select="negeso:questionnaire/@total_respondents"/>
    <xsl:if test="$numRespondents=0">
        <tr>
          <td class="admTableTDLast" colspan="2">
            <xsl:value-of select="java:getString($dict_inquiry, 'NO_RESPONDENTS')"/>
        </td></tr>
    </xsl:if>
    <xsl:if test="not($numRespondents=0)">
       
            <tr>
                <td class="admNamebar" width="50%"><xsl:value-of select="java:getString($dict_inquiry, 'QUESTION')"/></td>
                <td class="admNamebar"><xsl:value-of select="java:getString($dict_inquiry, 'OPTION')"/></td>
            </tr>
            <xsl:for-each select="//negeso:question">
                <tr>
                    <xsl:choose>
                        <xsl:when test="@type='text' or @type='textarea'">
                          <th class="admTableTD admWidth200">
                              <xsl:value-of select="@title"/>
                              <!-- COMENTED OUT DUE TO KIRSTEN'S REQUEST. TO BE DELETED IF SHE DOES NOT CHANGE HER MIND IN A WEEK.
                                <table cellpadding="0" cellspacing="0" width="95%">
                                    <tr>
                                        <td><xsl:value-of select="@title"/></td>
                                        <td align="right">
                                            <xsl:call-template name="print_statistics_bar">
                                              <xsl:with-param name="percent" select="round(@anscnt div $numRespondents * 100)"/>
                                            </xsl:call-template>
                                        </td>
                                    </tr>
                                </table>
                                <xsl:if test="string-length(@alternative)">
                                    <table cellpadding="0" cellspacing="0" width="95%">
                                        <tr>
                                            <td><xsl:value-of select="@alternative"/></td>
                                            <td align="right">
                                              <xsl:call-template name="print_statistics_bar">
                                                <xsl:with-param name="percent" select="round(@altcnt div $numRespondents * 100)"/>
                                              </xsl:call-template>
                                            </td>
                                        </tr>
                                    </table>
                                </xsl:if>
                              -->
                            </th>
                          <td class="admTableTDLast">&#160;</td>
                        </xsl:when>
                        <xsl:otherwise>
                          <xsl:variable name="totalCount" select="sum(negeso:option/@count) + @altcnt"/>
                          <th class="admTableTD admWidth200"><xsl:value-of select="@title"/></th>
                          <td class="admTableTDLast">
                                <xsl:for-each select="negeso:option">
                                    <table cellpadding="0" cellspacing="0" width="95%">
                                        <tr>
                                            <td><xsl:value-of select="@title"/></td>
                                            <td align="right">
                                                <xsl:call-template name="print_statistics_bar">
                                                  <xsl:with-param name="percent" select="round(@count div $totalCount * 100)"/>
                                                </xsl:call-template>
                                            </td>
                                        </tr>
                                    </table>
                                </xsl:for-each>
                                <xsl:if test="string-length(@alternative)">
                                    <table cellpadding="0" cellspacing="0" width="95%">
                                        <tr>
                                            <td><xsl:value-of select="@alternative"/></td>
                                            <td align="right">
                                                <xsl:call-template name="print_statistics_bar">
                                                  <xsl:with-param name="percent" select="round(@altcnt div $totalCount * 100)"/>
                                                </xsl:call-template>
                                            </td>
                                        </tr>
                                    </table>
                                </xsl:if>
                            </td>
                        </xsl:otherwise>
                    </xsl:choose>
                </tr>
            </xsl:for-each>
     
    </xsl:if>
</xsl:template>

<xsl:template match="negeso:model[@view='FORM_QUESTIONNAIRE']">
  <tr>
    <td align="center" class="admNavPanelFont"  colspan="2">
      <xsl:call-template name="tableTitle">
        <xsl:with-param name="headtext">
          <xsl:value-of select="java:getString($dict_inquiry, 'QUESTIONNAIRE_PROPERTIES')"/>
        </xsl:with-param>
      </xsl:call-template>
    </td>
  </tr>
    
    <form method="POST" enctype="multipart/form-data" name="formEditQuestionnaire">
        <input type="hidden" name="action" value="storeQuestionnaire"/>
        <input type="hidden" name="id" value="{negeso:questionnaire/@id}"/>
        <input type="hidden" name="langId" value="{negeso:questionnaire/@langId}"/>
       
            <tr>
              <th class="admTableTD admWidth200"><xsl:value-of select="java:getString($dict_common, 'TITLE')"/>*</th>
              <td class="admTableTDLast"><input class="admTextArea admWidth200" type="text" value="{negeso:questionnaire/@title}" name="title"/></td>
            </tr>
            <tr>
              <th class="admTableTD admWidth200"><xsl:value-of select="java:getString($dict_common, 'PUBLISH_DATE')"/></th>
              <td class="admTableTDLast">
                <table cellspacing="0" cellpadding="0">
                  <tr>
                    <td>
                      <input type="text" class="admTextArea" readonly="readonly" timedate="true" name="publish" id="publishDatepicker">
                        <xsl:attribute name="value"><xsl:value-of select="negeso:questionnaire/@publish"/></xsl:attribute>
                      </input>
                    </td>
                    <td>&#160;(dd-mm-yyyy)</td>
                    <td>

                      <div class="admNavPanelInp" style="padding-left:5px">
                        <div class="imgL"></div>
                        <div>
                          <input onClick="addpublishDateField()" type="button"  class="admNavPanelInp" style="width:83px;">
                            <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_inquiry, 'EXPIRED_DATE_INQ')"/></xsl:attribute>
                          </input>
                        </div>
                        <div class="imgR"></div>
                      </div>
                    </td>
                  </tr>
                </table>
                </td>
            </tr>
            <tr>
              <th class="admTableTD admWidth200"><xsl:value-of select="java:getString($dict_common, 'EXPIRED_DATE')"/></th>
              <td class="admTableTDLast">
                <table cellspacing="0" cellpadding="0">
                  <tr>

                    <td>
                      <input type="text" class="admTextArea" readonly="readonly" timedate="true" name="expired" id="expiredDatepicker" value="">
                        <xsl:attribute name="value"> <xsl:value-of select="negeso:questionnaire/@expired"/></xsl:attribute>
                      </input>
                    </td>

                    <td>&#160;(dd-mm-yyyy)</td>
                    <td>

                      <div class="admNavPanelInp" style="padding-left:5px">
                        <div class="imgL"></div>
                        <div>
                          <input id="addexpiredDateButton" onClick="addExpiredDate()" type="button"  class="admNavPanelInp" style="width:83px;">
                           <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_inquiry, 'HIDE_PAGE_INQ')"/> </xsl:attribute>
                          </input>
                        </div>
                        <div class="imgR"></div>
                      </div>
                    </td>
                  </tr>
                </table>
                </td>
            </tr>
            <tr>
              <th class="admTableTD admWidth200"><xsl:value-of select="java:getString($dict_inquiry, 'PUBLIC')"/></th>
              <td class="admTableTDLast">
          <input type="checkbox" name="public">
                    <xsl:if test="negeso:questionnaire/@public='true'">
                      <xsl:attribute name="checked"/>
                    </xsl:if>
          </input>
                </td>
            </tr>
            <tr>
              <th class="admTableTD admWidth200"><xsl:value-of select="java:getString($dict_inquiry, 'MULTIPAGE')"/></th>
              <td class="admTableTDLast">
                  <input type="checkbox" name="multipage">
                    <xsl:if test="negeso:questionnaire/@multipage='true'">
                      <xsl:attribute name="checked"/>
                    </xsl:if>
                  </input>
                </td>
            </tr>
            <tr>
              <th class="admTableTD admWidth200"><xsl:value-of select="java:getString($dict_inquiry, 'SHOW_ANSWERS')"/></th>
              <td class="admTableTDLast">
          <input type="checkbox" name="showAnswers">
            <xsl:if test="negeso:questionnaire/@showAnswers='true'">
              <xsl:attribute name="checked"/>
            </xsl:if>
          </input>
                </td>
            </tr>
            <tr>
                <th class="admTableTD admWidth200"><xsl:value-of select="java:getString($dict_inquiry, 'INTRO')"/></th>
              <td class="admTableTDLast">         
            <!--Begin Articles block-->
            <xsl:call-template name="article_text">
              <xsl:with-param name="id" select="negeso:questionnaire/negeso:intro/@id"/>
              <xsl:with-param name="part_text" select="negeso:questionnaire/negeso:intro/text()"/>
            </xsl:call-template>
         
        </td>
            </tr>
            <tr>
              <th class="admTableTD admWidth200"><xsl:value-of select="java:getString($dict_inquiry, 'CONCLUSION')"/></th>
              <td class="admTableTDLast">             
            <xsl:call-template name="article_text">
              <xsl:with-param name="id" select="negeso:questionnaire/negeso:conclusion/@id"/>
              <xsl:with-param name="part_text" select="negeso:questionnaire/negeso:conclusion/text()"/>
            </xsl:call-template>         
        </td>
            </tr>
            <tr>
              <th class="admTableTD admWidth200"><xsl:value-of select="java:getString($dict_inquiry, 'REMARKS_FIELD')"/></th>
              <td class="admTableTDLast">
                  <table border="0">
                    <tr>
                        <td style="width: 70px;"><xsl:value-of select="java:getString($dict_inquiry, 'MULTILINE')"/></td>
                      <td>
                <input name="rf_multiline" type="checkbox">
                  <xsl:if test="negeso:questionnaire/@rfMultiline='true'">
                    <xsl:attribute name="checked"/>
                  </xsl:if>
                </input>
                      </td>
                    </tr>
                    <tr>
                        <td style="width: 70px;"><xsl:value-of select="java:getString($dict_inquiry, 'HEIGHT_')"/></td>
                      <td>
                        <select name="rf_height" style="width: 70px;">
                  <option value="18">18px</option>
                  <option value="22">22px</option>
                  <option value="50">50px</option>
                  <option value="100">100px</option>
                  <option value="150">150px</option>
                  <option value="200">200px</option>
                  <option value="250">250px</option>
                  <option value="300">300px</option>
                  <option value="350">350px</option>
                  <option value="400">400px</option>
                  <option value="450">450px</option>
                  <option value="500">500px</option>
                        </select>
                        <script>
                          document.all["rf_height"].value = "<xsl:value-of select="negeso:questionnaire/@rfHeight"/>";
                        </script>
                      </td>
                    </tr>
                    <tr>
                        <td style="width: 70px;"><xsl:value-of select="java:getString($dict_inquiry, 'WIDTH_')"/></td>
                      <td>
                <select name="rf_width" style="width: 70px;">
                  <option value="50">50px</option>
                  <option value="100">100px</option>
                  <option value="150">150px</option>
                  <option value="200">200px</option>
                  <option value="250">250px</option>
                  <option value="300">300px</option>
                  <option value="350">350px</option>
                  <option value="400">400px</option>
                  <option value="450">450px</option>
                  <option value="500">500px</option>
                        </select>
                      <script>
                        document.all["rf_width"].value = "<xsl:value-of select="negeso:questionnaire/@rfWidth"/>";
                      </script>
                      </td>
                    </tr>
                  </table>
                </td>
            </tr>
    </form>
    
  <!--End Articles block-->

 
</xsl:template>

<xsl:template name="article_text">
  <xsl:param name="id" select="1"/>
  <xsl:param name="part_text"/>   
  <div  id="article_text_table{$id}"  rte="true">
    &#160;<img src="/images/mark_1.gif" onclick="edit_text('article_text{$id}', 'contentStyle', 595);" class="admBorder admHand" alt="Edit intro"/>
    <div id="article_text{$id}" class="contentStyle">
      <xsl:attribute name="style">behavior:url(/script/article3.htc); margin: 5px;border: 1px solid #848484;</xsl:attribute>
      <xsl:choose>
        <xsl:when test="$part_text">
          <xsl:value-of select="$part_text" disable-output-escaping="yes"/>
        </xsl:when>
        <xsl:otherwise>&#160;</xsl:otherwise>
      </xsl:choose>
    </div>
  </div>
  <!--<script>
    positionRTE(document.getElementById('article_text_table<xsl:value-of select="$id" />'),'<xsl:value-of select="$id" />');
    makeReadonly(document.getElementById('article_text<xsl:value-of select="$id" />'), true);   
  </script>-->  
</xsl:template>
  
<xsl:template match="negeso:model[@view='FORM_QUESTION']">
    <form method="POST" enctype="multipart/form-data" name="formEditQuestion">
        <input type="hidden" name="action"/>
        <input type="hidden" name="id" value="{//negeso:question/@id}"/>
        <input type="hidden" name="parentId" value="{//negeso:question/@parentId}"/>
    <input type="hidden" name="inquiry_question_id" id="inquiry_question_id" value="{//negeso:question/@id}"/>
      <tr>
        <td align="center" class="admNavPanelFont" colspan="5">
          <xsl:call-template name="tableTitle">
            <xsl:with-param name="headtext">
              <xsl:value-of select="java:getString($dict_inquiry, 'QUESTION_PROPERTIES')"/>
            </xsl:with-param>
          </xsl:call-template>
        </td>
      </tr>
        
            <tr>
              <th class="admTableTD admWidth150"><xsl:value-of select="java:getString($dict_common, 'NAME')"/>*</th>
              <td class="admTableTDLast" colspan="4"><input class="admTextArea admWidth200" type="text" value="{//negeso:question/@title}" name="title"/></td>
            </tr>
            <tr>
              <th class="admTableTD admWidth150"><xsl:value-of select="java:getString($dict_common, 'TYPE')"/></th>
              <td class="admTableTDLast" colspan="4">
                    <select name="type" class="admTextArea admWidth200" onchange="redirectQuestion();">
                        <option value="text">
                            <xsl:call-template name="qtypes"><xsl:with-param name="name" select="'text'"/></xsl:call-template>
                        </option>
                        <option value="textarea">
                            <xsl:call-template name="qtypes"><xsl:with-param name="name" select="'textarea'"/></xsl:call-template>
                        </option>
                        <option value="radio">
                            <xsl:call-template name="qtypes"><xsl:with-param name="name" select="'radio'"/></xsl:call-template>
                        </option>
                        <option value="checkbox">
                            <xsl:call-template name="qtypes"><xsl:with-param name="name" select="'checkbox'"/></xsl:call-template>
                        </option>
                        <option value="dropdown">
                            <xsl:call-template name="qtypes"><xsl:with-param name="name" select="'dropdown'"/></xsl:call-template>
                        </option>
                    </select>
                    <script>document.all.type.value = "<xsl:value-of select="//negeso:question/@type"/>";</script>
            </td>
            </tr>
            <tr>
              <th class="admTableTD admWidth150"><xsl:value-of select="java:getString($dict_inquiry, 'REQUIRED')"/></th>
              <td class="admTableTDLast" colspan="4">
                    <input type="checkbox" name="required">
                        <xsl:if test="//negeso:question/@required='true'"><xsl:attribute name="checked"/></xsl:if>
                    </input>
                </td>
            </tr>
            <tr>
              <th class="admTableTD admWidth150"><xsl:value-of select="java:getString($dict_inquiry, 'ALLOW_REMARK')"/></th>
              <td class="admTableTDLast" colspan="4">
                    <input type="checkbox" name="allowRemark">
                        <xsl:if test="//negeso:question/@allowRemark='true'"><xsl:attribute name="checked"/></xsl:if>
                    </input>
                </td>
            </tr>
            <tr>
              <th class="admTableTD admWidth150"><xsl:value-of select="java:getString($dict_inquiry, 'ALTERNATIVE_OPTION')"/></th>
              <td class="admTableTDLast" colspan="4">
                    <input class="admTextArea admWidth200" type="text" value="{//negeso:question/@alternative}" name="alternative"/>
                    <i>(<xsl:value-of select="java:getString($dict_inquiry, 'CLEAR_TO_DISABLE')"/>)</i><br/><br/>
                    <table border="0">
                      <tr>
                          <td style="width: 70px;"><xsl:value-of select="java:getString($dict_inquiry, 'MULTILINE')"/></td>
                        <td>
              <input name="ao_multiline" type="checkbox">
                <xsl:if test="//negeso:question/@aoMultiline='true'"><xsl:attribute name="checked"/></xsl:if>
              </input>
                        </td>
                      </tr>
                      <tr>
                          <td style="width: 70px;"><xsl:value-of select="java:getString($dict_inquiry, 'HEIGHT_')"/></td>
                        <td>
                          <select name="ao_height" style="width: 70px;">
                    <option value="18">18px</option>
                    <option value="22">22px</option>
                    <option value="50">50px</option>
                    <option value="100">100px</option>
                    <option value="150">150px</option>
                    <option value="200">200px</option>
                    <option value="250">250px</option>
                    <option value="300">300px</option>
                    <option value="350">350px</option>
                    <option value="400">400px</option>
                    <option value="450">450px</option>
                    <option value="500">500px</option>
                        </select>
                    <script>
                      document.all["ao_height"].value = "<xsl:value-of select="//negeso:question/@aoHeight"/>";
                    </script>
                      </td>
                      </tr>
                      <tr>
                          <td style="width: 70px;"><xsl:value-of select="java:getString($dict_inquiry, 'WIDTH_')"/></td>
                        <td>
                          <select name="ao_width" style="width: 70px;">
                    <option value="50">50px</option>
                    <option value="100">100px</option>
                    <option value="150">150px</option>
                    <option value="200">200px</option>
                    <option value="250">250px</option>
                    <option value="300">300px</option>
                    <option value="350">350px</option>
                    <option value="400">400px</option>
                    <option value="450">450px</option>
                    <option value="500">500px</option>
                        </select>
                    <script>
                      document.all["ao_width"].value = "<xsl:value-of select="//negeso:question/@aoWidth"/>";
                    </script>
                        </td>
                      </tr>
                    </table>
                </td>
            </tr>
            <tr>
              <th class="admTableTD admWidth150"><xsl:value-of select="java:getString($dict_inquiry, 'OPTIONS_LAYOUT')"/></th>
              <td class="admTableTDLast" colspan="4">
                  <select class="admTextArea admWidth200" name="options_layout">
                  <option value="horizontal">
                    <xsl:if test="not(//negeso:question/@options_layout = 'vertical')"><xsl:attribute name="SELECTED"/></xsl:if>
                      <xsl:value-of select="java:getString($dict_inquiry, 'HORIZONTAL')"/>
                  </option>
                  <option value="vertical">
                    <xsl:if test="//negeso:question/@options_layout = 'vertical'"><xsl:attribute name="SELECTED"/></xsl:if>
                      <xsl:value-of select="java:getString($dict_inquiry, 'VERTICAL')"/>
                  </option>
                </select>
                </td>
            </tr>
            <tr>
              <th class="admTableTD admWidth150"><xsl:value-of select="java:getString($dict_inquiry, 'QUESTION')"/></th>
              <td class="admTableTDLast" colspan="4">
                    <textarea class="admTextArea admWidth200" style="height: 100;" name="explanation" ><xsl:value-of select="//negeso:question/@explanation"/></textarea>
                </td>
            </tr>
            <xsl:if test="not(//negeso:question/@id = 0)">
      <tr>
        <td align="center" class="admNavPanelFont" colspan="5">
          <xsl:call-template name="tableTitle">
            <xsl:with-param name="headtext">
              <xsl:value-of select="java:getString($dict_inquiry, 'OPTIONS_OF_THE_QUESTION')"/>
            </xsl:with-param>
          </xsl:call-template>
        </td>
      </tr>
      
      <xsl:variable name="requiresTextInput" select="//negeso:question/@type='text' or //negeso:question/@type='textarea'"></xsl:variable>
      <xsl:if test="not($requiresTextInput)">
      
          <tr>
            <th class="admTableTD admWidth150"><xsl:value-of select="java:getString($dict_common, 'TITLE')"/>*</th>
            <td class="admTableTDLast" colspan="4"><input class="admTextArea admWidth200" type="text" value="" name="optionTitle" style="margin:8px 0 0 0;"/>
              <div class="admNavPanelInp">
                <div class="imgL"></div>
                <div>
                  <input class="admNavbarInp" type="button" onClick="addOption();">
                    <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_inquiry, 'ADD_NEW_OPTION')"/></xsl:attribute>
                  </input>
                </div>
                <div class="imgR"></div>
              </div>              
            </td>
          </tr>
      
      </xsl:if>
  
        <xsl:choose>
          <xsl:when test="$requiresTextInput">
            <tr>
              <th class="admTableTD admLeft">
                  <xsl:value-of select="java:getString($dict_inquiry, 'REQUIRES_TEXT_INPUT')"/>
              </th>
            </tr>
          </xsl:when>
          <xsl:otherwise>
            <tr>
                <td class="admTDtitles"><xsl:value-of select="java:getString($dict_common, 'TITLE')"/></td>
              <td class="admTDtitles">&#160;</td>
              <td class="admTDtitles">&#160;</td>
              <td class="admTDtitles">&#160;</td>
              <td class="admTDtitles">&#160;</td>
            </tr>
            <tr>
              <td colspan="5">
           <table width="100%">
            <xsl:for-each select="//negeso:option">
              <tr>
                <th class="admTableTD" width="80%">
                  <a class="admAnchor" href="javascript: editOption({@id}, '{@title}');">
                    <xsl:value-of select="@title"/>
                  </a>
                </th>
                <td class="admTableTDLast admWidth30">
                  <img class="admHand" src="/images/rename.png" onClick="editOption({@id}, '{@title}');">
                      <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_inquiry, 'RENAME_OPTION')"/></xsl:attribute>
                  </img>
                </td>
                <td class="admTableTDLast admWidth30">
                  <img class="admHand" src="/images/up.png" onClick="moveOption('{@id}','{preceding-sibling::*[1]/@id}');">
                      <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'UP')"/></xsl:attribute>
                  </img>
                </td>
                <td class="admTableTDLast admWidth30">
                  <img class="admHand" src="/images/down.png" onClick="moveOption('{@id}','{following-sibling::*[1]/@id}');">            
                      <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'DOWN')"/></xsl:attribute>
                  </img>
                </td>
                <td class="admTableTDLast admWidth30">
                  <img src="/images/delete.png" class="admHand" onClick="deleteOption({@id});">
                      <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'DELETE')"/></xsl:attribute>
                  </img>
                </td>
              </tr>
            </xsl:for-each>
                </table>
              </td>
            </tr>
          </xsl:otherwise>
        </xsl:choose> 
    
    </xsl:if>
      
        
     
    </form>
</xsl:template>

<xsl:template match="negeso:model[@view='FORM_USER']">
  <tr>
    <td align="center" class="admNavPanelFont"  colspan="2">
      <xsl:call-template name="tableTitle">
        <xsl:with-param name="headtext">
          <xsl:value-of select="java:getString($dict_inquiry, 'USER_PROPERTIES')"/>
        </xsl:with-param>
      </xsl:call-template>
    </td>
  </tr>

  
    <form method="POST" enctype="multipart/form-data" name="formEditUser">
        <input type="hidden" name="action" value="storeUser"/>
        <input type="hidden" name="id" value="{//negeso:inquiry_user/@id}"/>
        <input type="hidden" name="questionnaire_id" value="{//negeso:inquiry_user/@questionnaire_id}"/>
       
            <tr>
              <th class="admTableTD admWidth200"><xsl:value-of select="java:getString($dict_inquiry, 'EMAIL_')"/>*</th>
              <td class="admTableTDLast">
                  <input class="admTextArea admWidth200" type="text" value="{//negeso:inquiry_user/@email}" name="user_email">
                    <xsl:if test="//negeso:inquiry_user/@id &gt; 0">
                      <xsl:attribute name="readOnly"/>
                      <xsl:attribute name="style">background: none; border: none;</xsl:attribute>
                    </xsl:if>
                  </input>
                </td>
            </tr>
            <tr>
              <th class="admTableTD admWidth200"><xsl:value-of select="java:getString($dict_inquiry, 'PASSWORD')"/>*</th>
              <td class="admTableTDLast"><input class="admTextArea admWidth200" type="text" value="{//negeso:inquiry_user/@password}" name="user_password"/></td>
            </tr>
            <xsl:if test="not(//negeso:inquiry_user/@id &gt; 0)">
              <tr>
                <th class="admTableTD admWidth200"><xsl:value-of select="java:getString($dict_inquiry, 'SEND_INVITATION_TO_NEW_USER')"/></th>
                <td class="admTableTDLast"><input type="checkbox" checked="" name="shouldInvite"/></td>
              </tr>
            </xsl:if>
       
      
          
       
    </form>
</xsl:template>


<xsl:template match="negeso:model[@view='VIEW_USERS']">
  <tr>
    <td align="center" class="admNavPanelFont"  colspan="2">
    <xsl:call-template name="tableTitle">
        <xsl:with-param name="headtext"><xsl:value-of select="java:getString($dict_inquiry, 'USER_LIST')"/></xsl:with-param>
    </xsl:call-template>
    </td>
  </tr>
    <form method="POST" enctype="multipart/form-data" name="manageUsersForm">
        <input type="hidden" name="action" value=""/>
        <input type="hidden" name="questionnaire_id" value="{//negeso:questionnaire/@id}"/>
        <input type="hidden" name="remind_ids" value=""/>
        <xsl:if test="//negeso:model/@error='emailError'">
            <b style="color: red;"><xsl:value-of select="java:getString($dict_inquiry, 'SUCH_EMAIL_ALREADY_EXISTS')"/> (<xsl:value-of select="//negeso:model/@email"/>)</b>
        </xsl:if>
         <xsl:if test="//negeso:model/@error='invalidFile'">
             <b style="color: red;"><xsl:value-of select="java:getString($dict_inquiry, 'INVALID_FILE')"/></b>
        </xsl:if>
      <tr>
        <td >
      <table id="tab1" cellpadding="0" cellspacing="0" width="100%">   
          <tr>
              <td class="admTableTDLast">
                <div class="admNavPanelInp" style="padding-left:5px">
                  <div class="imgL"></div>
                  <div>
                    <button class="admNavbarInp" onClick="location.href='?action=editUser&amp;questionnaire_id={//negeso:questionnaire/@id}'">
                     &#160;<xsl:value-of select="java:getString($dict_inquiry, 'ADD__USER')"/>&#160;
                    </button>
                  </div>
                  <div class="imgR"></div>
                </div>
                  
              </td>
          </tr>
          <tr>
              <td class="admTableTDLast">
          <table cellpadding="0" cellspacing="0" width="100%">
            <tr>
              <td >
                  <xsl:value-of select="java:getString($dict_inquiry, 'IMPORT_HINT')"/>:<br/>
                <tt>
                somebody@somewhere.com<br/>
                someone-else@somewhere.org<br/>
                ...<br/>
                </tt>
              </td>
            </tr>         
            <tr>
              <td align="left">
                        <input onKeyPress="return false;" type="file" name="import_file" />
              </td>
            <tr>
            </tr>
              <td align="left">
                        <input type="checkbox" name="shouldInvite" checked="" />
                  <xsl:value-of select="java:getString($dict_inquiry, 'SEND_INVITATION_TO_IMPORTED_USERS')"/>
              </td>
            </tr>
          </table>
                <div class="admNavPanelInp" style="padding-left:5px">
                  <div class="imgL"></div>
                  <div>
                    <button class="admNavbarInp" onClick="importUsers();">
                      <xsl:value-of select="java:getString($dict_inquiry, 'IMPORT_FROM_FILE')"/>
                    </button>
                  </div>
                  <div class="imgR"></div>
                </div>
                 
              </td>
          </tr>
          <tr>
            <td class="admTableTDLast">
              <div class="admNavPanelInp" style="padding-left:5px">
                <div class="imgL"></div>
                <div>
                  <button class="admNavbarInp" onClick="selectUsersToRemindpasswordsTo();">
                   <xsl:value-of select="java:getString($dict_inquiry, 'PASSWORD_REMINDER')"/>
                  </button>
                </div>
                <div class="imgR"></div>
              </div>
                 
              </td>
          </tr>
      </table>
        </td>
      </tr>
        <div id="div1" style="display: none; color: red; font-weight: bold; font-size: 14px;"><xsl:value-of select="java:getString($dict_inquiry, 'SENDING_EMAILS_PLEASE_WAIT')"/></div>
      <tr>
        <td >
          <table id="tab2"  cellspacing="0" cellpadding="0" width="100%">
            <tr>
              <td class="admTDtitles" style="text-align: left;">
               <xsl:value-of select="java:getString($dict_common, 'EMAIL')"/>
              </td>
              <td class="admTDtitles" style="width: 30;">&#160;</td>
              <td class="admTDtitles" style="width: 30;">&#160;</td>
            </tr>
            <xsl:for-each select="//negeso:questionnaire/negeso:inquiry_user">
              <tr>
                <th class="admTableTD">
                  <xsl:value-of select="@email"/>
                </th>
                <td class="admTableTDLast admWidth30">
                  <img src="/images/edit.png" class="admHand" onClick="location.href='?action=editUser&amp;questionnaire_id={//negeso:questionnaire/@id}&amp;id={@id}'">
                    <xsl:attribute name="title">
                      <xsl:value-of select="java:getString($dict_inquiry, '_CHANGE_PASSWORD')"/>
                    </xsl:attribute>
                  </img>
                </td>
                <td class="admTableTDLast admWidth30">
                  <img src="/images/delete.png" class="admHand" onClick="confirmDelete('?action=deleteUser&amp;id={@id}')">
                    <xsl:attribute name="title">
                      <xsl:value-of select="java:getString($dict_inquiry, 'DELETE_USER')"/>
                    </xsl:attribute>
                  </img>
                </td>
              </tr>
            </xsl:for-each>
          </table>
        </td>
      </tr>
    </form>
</xsl:template>


<!-- DEFAULT VIEW: LIST OF QUESTIONNAIRES -->
<xsl:template match="negeso:model">
    <tr>
        <td class="admNavPanelFont" align="center">
            <xsl:value-of select="java:getString($dict_inquiry, 'QUESTIONNAIRES')"/>
        </td>
    </tr>
    <tr>
        <td>
            <table id="tab1" cellpadding="0" cellspacing="0" width="100%" border="0">
              <form name="formToUpdateEmailSubject" method="POST" enctype="multipart/form-data" style="display: none" >
                <input type="hidden" name="action"/>
                <input type="hidden" name="emailSubject"/>
              </form>
                <tr>
            <th class="admTableTD" align="center" width="50%">
                        <div id="article_text{negeso:invitation/@id}" emailSubject="{negeso:invitation/@head}"
                  style="display: none; behavior:url(/script/article3.htc);">
                            <xsl:value-of select="negeso:invitation/@text" disable-output-escaping="yes"/>
                        </div>
                        <a class="admAnchor" href="javascript:edit_text('article_text{negeso:invitation/@id}', 'contentStyle', 595);">
                            <xsl:value-of select="java:getString($dict_inquiry, 'EDIT_INQUIRY_INVITATION_TEXT')"/>
                        </a>
            </th>
            <td class="admTableTDLast" align="center">
                        <a class="admAnchor" href="javascript:editSubject('article_text{negeso:invitation/@id}', 'storeInvitationSubject');">
                            <xsl:value-of select="java:getString($dict_inquiry, 'EDIT_EMAIL_SUBJECT')"/>
                        </a>
            </td>
                </tr>
                <tr>
                    <th class="admTableTD" align="center" width="50%">
                        <div id="article_text{negeso:qreminder/@id}" emailSubject="{negeso:qreminder/@head}"
                  style="display: none; behavior:url(/script/article3.htc);">
                            <xsl:value-of select="negeso:qreminder/@text" disable-output-escaping="yes"/>
                        </div>
                        <a class="admAnchor" href="javascript:edit_text('article_text{negeso:qreminder/@id}', 'contentStyle', 595);">
                            <xsl:value-of select="java:getString($dict_inquiry, 'EDIT_INQUIRY_REMINDER_TEXT')"/>
                        </a>
            </th>
                    <td class="admTableTDLast" align="center">
                        <a class="admAnchor" href="javascript:editSubject('article_text{negeso:qreminder/@id}', 'storeQuestionnaireReminderSubject');">
                            <xsl:value-of select="java:getString($dict_inquiry, 'EDIT_EMAIL_SUBJECT')"/>
                        </a>
            </td>
                </tr>
                <tr>
                    <th class="admTableTD" align="center" width="50%">
                        <div id="article_text{negeso:preminder/@id}" emailSubject="{negeso:preminder/@head}"
                  style="display: none; behavior:url(/script/article3.htc);">
                            <xsl:value-of select="negeso:preminder/@text" disable-output-escaping="yes"/>
                        </div>
                        <a class="admAnchor" href="javascript:edit_text('article_text{negeso:preminder/@id}', 'contentStyle', 595);">
                            <xsl:value-of select="java:getString($dict_inquiry, 'EDIT_PASSWORD_REMINDER_TEXT')"/>
                        </a>
            </th>
                    <td class="admTableTDLast" align="center">
                        <a class="admAnchor" href="javascript:editSubject('article_text{negeso:preminder/@id}', 'storePasswordReminderSubject');">
                            <xsl:value-of select="java:getString($dict_inquiry, 'EDIT_EMAIL_SUBJECT')"/>
                        </a>               
                    </td>
                </tr>
                <tr>
                    <td class="admTableTDLast" colspan="2">
                        <div class="admNavPanelInp" style="margin-left:270px;">
                            <div class="imgL"></div>
                            <div>
                                <input class="admNavPanelInp" type="submit">
                                    <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_inquiry, 'ADD_NEW_QUESTIONNAIRE')"/></xsl:attribute>
                                    <xsl:attribute name="onClick">
                                        <xsl:choose>
                                            <xsl:when test="//negeso:model/@qLimit='false'">location.href='?action=editQuestionnaire'</xsl:when>
                                            <xsl:otherwise>alert("<xsl:value-of select="java:getString($dict_inquiry, 'MAXIMUM_NUMBER_OF_QUESTIONNAIRES_ID')"/>&#160;<xsl:value-of select="//@qMaxLimit"/>")</xsl:otherwise>
                                        </xsl:choose>
                                    </xsl:attribute>
                                </input>
                            </div>
                            <div class="imgR"></div>
                        </div>
                   </td>
                </tr>
            </table>
            
            <div id="div1" style="display: none; color: red; font-weight: bold; font-size: 14px;"><xsl:value-of select="java:getString($dict_inquiry, 'SENDING_EMAILS_PLEASE_WAIT')"/></div>
            
            <table id="tab2" width="100%" cellspacing="0" cellpadding="0">
                <tr>
                    <th class="admTDtitles"><xsl:value-of select="java:getString($dict_common, 'NAME')"/></th>
                    <td class="admTDtitles"><xsl:value-of select="java:getString($dict_common, 'PUBLISH_DATE')"/></td>
                    <td class="admTDtitles"><xsl:value-of select="java:getString($dict_common, 'EXPIRED_DATE')"/></td>
                    <td class="admTDtitles">&#160;</td>
                    <td class="admTDtitles">&#160;</td>
                    <td class="admTDtitles">&#160;</td>
                    <td class="admTDtitles">&#160;</td>
                    <td class="admTDtitles">&#160;</td>
                    <td class="admTDtitles">&#160;</td>
                    <td class="admTDtitles">&#160;</td>
                </tr>
                <xsl:for-each select="negeso:questionnaire">
                    <tr>
                <th class="admTableTD">
                            <a class="admAnchor" href="?action=viewQuestions&amp;id={@id}">
                                <xsl:value-of select="@title"/>
                            </a>
                        </th>       
                        <td class="admTableTD admWidth100">
                            <xsl:value-of select="@publish"/>
                        </td>
                        <td class="admTableTD admWidth100">
                            <xsl:value-of select="@expired"/>
                        </td>
                        <td class="admTableTDLast admWidth30">
                            <!--<xsl:if test="@public='false'">-->
                              <img src="/images/send_mail_1.png" class="admHand" onClick="sendQuestionaireReminder({@id});">
                                  <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_inquiry, 'SEND_QUESTIONNARIE_REMINDER_EMAIL')"/></xsl:attribute>
                              </img>
                            <!--</xsl:if>-->
                        </td>
                <td class="admTableTDLast admWidth30">
                  <img src="/images/user.png" class="admHand" onClick="location.href='?action=viewUsers&amp;id={@id}'">
                      <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_inquiry, 'MANAGE__USERS')"/></xsl:attribute>
                  </img>
                </td>
                <td class="admTableTDLast admWidth30">
                  <img src="/images/view_respondents_1.png" class="admHand" onClick="location.href='?action=viewRespondents&amp;id={@id}'">
                      <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_inquiry, 'VIEW_RESPONDENTS')"/></xsl:attribute>
                  </img>
                </td>
                <td class="admTableTDLast admWidth30">
                  <img src="/images/view_statistics.png" class="admHand" onClick="location.href='?action=viewStatistics&amp;id={@id}'">
                      <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_inquiry, 'VIEW_STATISTICS')"/></xsl:attribute>
                  </img>
                </td>
                        <td class="admTableTDLast admWidth30">
                            <img src="/images/properties.png" class="admHand" onClick="location.href='?action=editQuestionnaire&amp;id={@id}'">
                                <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_inquiry, 'EDIT_PROPERTIES')"/></xsl:attribute>
                            </img>
                        </td>
                        <td class="admTableTDLast admWidth30">
                            <img src="/images/move.png" class="admHand" onClick="copyQuestionnaire('{@title}', {@id})">
                                <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'COPY')"/></xsl:attribute>
                            </img>
                        </td>
                        <td class="admTableTDLast admWidth30">
                            <img src="/images/delete.png" class="admHand" onClick="confirmDelete('?action=deleteQuestionnaire&amp;id={@id}')">
                                <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'DELETE')"/></xsl:attribute>
                            </img>
                        </td>
                    </tr>
                </xsl:for-each>
            </table>
        </td>
    </tr>
</xsl:template>


<!-- i18n -->
<xsl:template name="qtypes">
    <xsl:param name="name"/>
    <xsl:choose>
        <xsl:when test="$name='text'"><xsl:value-of select="java:getString($dict_inquiry, 'TYPE_TEXT')"/></xsl:when>
        <xsl:when test="$name='textarea'"><xsl:value-of select="java:getString($dict_inquiry, 'TYPE_TEXTAREA')"/></xsl:when>
        <xsl:when test="$name='radio'"><xsl:value-of select="java:getString($dict_inquiry, 'TYPE_RADIO')"/></xsl:when>
        <xsl:when test="$name='checkbox'"><xsl:value-of select="java:getString($dict_inquiry, 'TYPE_CHECKBOX')"/></xsl:when>
        <xsl:when test="$name='dropdown'"><xsl:value-of select="java:getString($dict_inquiry, 'TYPE_DROPDOWN')"/></xsl:when>
        <xsl:otherwise>BAD I18N PARAMETER</xsl:otherwise>
    </xsl:choose>
</xsl:template>


<!--<xsl:template name="backLink">
    <xsl:choose>
        <xsl:when test="$view='VIEW_QUESTIONNAIRE_LIST'"></xsl:when>
        <xsl:when test="$view='VIEW_RESPONDENT'">?action=viewRespondents&amp;id=<xsl:value-of select="//negeso:questionnaire/@id"/></xsl:when>
        <xsl:when test="$view='FORM_QUESTION'">?action=viewQuestions&amp;id=<xsl:value-of select="//negeso:questionnaire/@id"/></xsl:when>
        <xsl:when test="$view='FORM_USER'">
            <xsl:if test="//negeso:questionnaire/@id=0 or //negeso:questionnaire/@id=''">?</xsl:if>
            <xsl:if test="not(//negeso:questionnaire/@id=0 or //negeso:questionnaire/@id='')">?action=viewUsers&amp;id=<xsl:value-of select="//negeso:questionnaire/@id"/></xsl:if>
    </xsl:when>
        <xsl:otherwise>?</xsl:otherwise>
    </xsl:choose>
</xsl:template>-->

  <!--******************** Bottom buttons ********************-->
  <xsl:template name="buttons_qustionnaire">
    <table cellpadding="0" cellspacing="0"  width="764px" align="center" class="Buttons">
      <tr>
        <td>
          <div class="admBtnGreenb">
            <div class="imgL"></div>
            <div>
              <a focus="blur()" class="admBtnText" onClick="onSubmit()" href="#onSubmit()">
                <xsl:value-of select="java:getString($dict_common, 'SAVE')"/>
              </a>
            </div>
            <div class="imgR"></div>
          </div>
         
        </td>
      </tr>
    </table>
  </xsl:template>

  <xsl:template name="buttons_form_user">
    <table cellpadding="0" cellspacing="0"  width="764px" align="center" class="Buttons">
      <tr>
        <td>
          <div class="admBtnGreenb">
            <div class="imgL"></div>
            <div>
             <a focus="blur()" class="admBtnText" onClick="formEditUser.submit()" >
                <xsl:value-of select="java:getString($dict_common, 'SAVE')"/>
              </a>
              <!--<input class="admNavbarInp" type="button" onclick="formEditUser.submit()">
                <xsl:attribute name="value">
                  &lt;&#160;<xsl:value-of select="java:getString($dict_common, 'SAVE')"/>&#160;&gt;
                </xsl:attribute>
              </input>-->
            </div>
            <div class="imgR"></div>
          </div>

        </td>
      </tr>
    </table>
  </xsl:template>

  <xsl:template name="buttons_form_question">
    <table cellpadding="0" cellspacing="0"  width="764px" align="center" class="Buttons">
      <tr>
        <td>
          <div class="admBtnGreenb">
            <div class="imgL"></div>
            <div>
              <!--<a focus="blur()" class="admBtnText" onClick="onSubmit()" href="#onSubmit()">
                <xsl:value-of select="java:getString($dict_common, 'SAVE')"/>
              </a>-->
              <input class="admNavbarInp" type="button" onclick="storeQuestion();">
                <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_common, 'SAVE')"/></xsl:attribute>
              </input>
            </div>
            <div class="imgR"></div>
          </div>
          <div class="admBtnGreenb">
            <div class="imgL"></div>
            <div>
              <!--<a focus="blur()" class="admBtnText" onClick="onSubmit()" href="#onSubmit()">
                <xsl:value-of select="java:getString($dict_common, 'SAVE')"/>
              </a>-->
              <input class="admNavbarInp" type="button" onclick="previewQuestionnaire({//negeso:questionnaire/@id});">
                <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_common, 'PREVIEW')"/></xsl:attribute>
                <xsl:if test="not(//negeso:questionnaire/@previewUrl)">
                  <xsl:attribute name="disabled"/>
                </xsl:if>
              </input>
            </div>
            <div class="imgR"></div>
          </div>

        </td>
      </tr>
    </table>
  </xsl:template>
<xsl:template name="print_statistics_bar">
  <xsl:param name="percent"/>
  <xsl:choose>
    <xsl:when test="string($percent) = 'NaN'">
      --
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$percent"/>%
      <img src="/images/0.gif" style="width: {$percent}; height: 12px; background-color: #7FB577"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

  
  

</xsl:stylesheet>
