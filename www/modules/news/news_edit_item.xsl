<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)${edit_item.xsl}

  Copyright (c) 2004 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential
  Information and shall use it only in accordance with the terms of the
  license agreement you entered into with Negeso.

  View of a list item edit interface.

  @version      2003.12.23
  @author       Olexiy.Strashko
  @author       Sergiy Oliynyk
  @author       Volodymyr Snigur
-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">

<xsl:output method="html"/>
<xsl:include href="/xsl/negeso_body.xsl"/>
<xsl:include href="/xsl/admin_templates.xsl"/>

<xsl:variable name="lang" select="/*/@interface-language"/>
<xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>
<xsl:variable name="dict_news_module" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_news_module.xsl', $lang)"/>
<xsl:variable name="dict_news_module_custom" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('news_module', $lang)"/>
    <xsl:variable name="dict_dialogs" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_dialogs.js',$lang)"/>

<xsl:template match="/">
<html>
<head>
    <title><xsl:value-of select="java:getString($dict_news_module, 'EDIT_NEWS')"/></title>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link rel="stylesheet" href="/css/admin_style.css" type="text/css"/>
    <link href="/site/core/css/default_styles.css" rel="stylesheet" type="text/css"/>
    <link rel="stylesheet" type="text/css" href="/css/jquery-ui.custom.css"/>    
        
    <script type="text/javascript" src="/script/jquery.min.js"></script>
    <script type="text/javascript" src="/script/jquery-ui.custom.min.js" />
    <script type="text/javascript" src="/script/cufon-yui.js"></script>
    <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>        
    <script type="text/javascript" src="/script/common_functions.js"></script>
    <script language="JavaScript1.2" src="/script/media_catalog.js" type="text/javascript"/>
    <script type="text/javascript" src="/script/conf.js"/>
    
    <script language="JavaScript">        
        var s_InvalidPublishDate = "<xsl:value-of select="java:getString($dict_news_module, 'INVALID_PUBLISH_DATE')"/>";
        var s_InvalidExpiredDate = "<xsl:value-of select="java:getString($dict_news_module, 'INVALID_EXPIRED_DATE')"/>";
        var s_InvalidViewableDate = "<xsl:value-of select="java:getString($dict_news_module, 'INVALID_VIEWABLE_DATE')"/>";
        var s_ImageIsNotChosen = "<xsl:value-of select="java:getString($dict_news_module, 'IMAGE_NOT_CHOSEN')"/>";
        var s_DocumentIsNotChosen = "<xsl:value-of select="java:getString($dict_news_module, 'DOCUMENT_NOT_CHOSEN')"/>";
        var s_WishToRemove = "<xsl:value-of select="java:getString($dict_news_module, 'WISH_TO_REMOVE')"/>";
        var s_NoPresentInOtherLanguages = "<xsl:value-of select="java:getString($dict_news_module, 'NOT_PRESENT_IN_OTHER_LANGUAGES')"/>";

        // Image link value for restore if form reset
        var imageLink = "<xsl:value-of select='negeso:listItem/@imageLink'/>";
        if (imageLink == "")
            imageLink = "/images/0.gif";

        var itemIsPublic = "<xsl:value-of select="negeso:listItem/@public"/>";
        var summaryIsPublic = "<xsl:value-of select="negeso:listItem/@public"/>";

        <xsl:text disable-output-escaping="yes">
        <![CDATA[

        function onSubmit(close) {
            if (document.mainForm.publishDateField.value != '' &&
                checkDate(document.mainForm.publishDateField.value) == false)
            {
                alert(s_InvalidPublishDate);
                return;
            }
            if (document.mainForm.expiredDateField.value != '' &&
                checkDate(document.mainForm.expiredDateField.value) == false)
            {
                alert(s_InvalidExpiredDate);
                return;
            }
            if (document.mainForm.viewableDateField.value != '' &&
                checkDate(document.mainForm.viewableDateField.value) == false)
            {
                alert(s_InvalidViewableDate);
                return;
            }
            setItemVisibility();
            if (close == null)
                mainForm.listId.value = "";
            mainForm.submit();
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

        // URL prefix for page explorer
        var urlPrefix = "";

        function openPageExplorer() {
            var answer =
                window.showModalDialog(
                    "?command=get-pages-list-command",
                     this,
                    "dialogHeight: 570px; dialogWidth: 625px; help: No; scroll: Yes; status: No;"
                );
            if (typeof(answer) != "undefined" && answer != null) {
                document.getElementById('showLinkId').value = answer[0];
                document.getElementById('pageLinkId').value = answer[1];
               
            }
        }

        function clearPageLink() {
            document.getElementById('showLinkId').value = "";
            document.getElementById('pageLinkId').value = "";
            
        }

       
function prependZero(num){
            return num > 9 ? "" + num : "0" + num;
        }
  var today = new Date();
        var today = new Date(today.getFullYear(), today.getMonth(), today.getDate());

  function addExpiredDate() {    
 document.getElementById("publishDatepicker").value = "";         
		document.mainForm.expiredDateField.value = 
               prependZero(today.getDate())  + "-" + 
                prependZero(today.getMonth() + 1) + "-" + 
                prependZero(today.getFullYear());            
        }

  function addpublishDateField() {  
 document.getElementById("expiredDatepicker").value = "";          
		document.mainForm.publishDateField.value = 
                   prependZero(today.getDate())  + "-" + 
                prependZero(today.getMonth() + 1) + "-" + 
                prependZero(today.getFullYear());          
        }
		

function addviewableDateField() {            
		document.mainForm.viewableDateField.value = 
                   prependZero(today.getDate())  + "-" + 
                prependZero(today.getMonth() + 1) + "-" + 
                prependZero(today.getFullYear());        
        }
		
 function clearviewableDateField() {            
            document.mainForm.viewableDateField.value = "";
      }

     

        function clearImageLink() {
            $('input[name="thumbnailLink"]').val('');
            $('input[name="imageLink"]').val('');
            $('#photoImage').attr('src','/images/0.gif');
          <!--document.mainForm.clearImageButton.disabled = true;-->  
        }

        function clearDocumentLink() {
            document.mainForm.documentLink.value = "";
            <!--document.mainForm.clearDocumentButton.disabled = true;-->
        }

        function clearTopNewsTheme() {
            document.mainForm.topNewsTheme.value = "";
            document.mainForm.clearTopNewsThemeButton.disabled = true;
        }

          function resultUploadImage(){
            var result = returnValue;
            if (result != null){
              if (result.resCode == "OK"){

                var obj_img = document.getElementById('photoImage');
                obj_img.src=result.realImage;
                document.forms[0].elements['imageLink'].value = result.realImage;
              }
            }
          }

      
        function resultUploadDocument(){                        
            var result = returnValue;

            if (result != null) {
                if (result.resCode == "OK"){
					document.forms[0].elements['documentLink'].value = result.fileUrl;
                    document.mainForm.documentLink.value = result.fileUrl;
                    document.mainForm.clearDocumentButton.disabled = false;
                }
            }
        }

        function selectDocumentDialog(){                        
            result = MediaCatalog.selectDocumentDialog();            

            if (result != null) {
                if (result.resCode == "OK"){
                    document.mainForm.documentLink.value = result.fileUrl;
                    document.mainForm.clearDocumentButton.disabled = false;
                }
            }
        }

        function selectImageDialog(thWidth, thHeight, isThumb){
			result = isThumb ? MediaCatalog.selectThumbnailImageDialog(thWidth, thHeight) : MediaCatalog.selectImageDialog(thWidth, thHeight);            
            if (result != null){
                if (result.resCode == "OK") {                    
                    document.mainForm.imageLink.value = result.realImage;
                    if (isThumb) {
                        document.mainForm.thumbnailLink.value = result.thumbnailImage;
                    }
                    document.all.photoImage.outerHTML = 
                        "<img hspace='5' vspace='5' id='photoImage' " + 
                             "src='../" + result.realImage +"'>";
 					document.mainForm.clearImageButton.disabled = false;                    
                }
            }
        }

        function setLanguages() {
            strPage = "?command=create-links-command&listItemId=" + mainForm.listItemId.value;
            strAttr = "resizable:on;scroll:on;status:off;dialogWidth:625px;dialogHeight:820px";
            var languages = showModalDialog(strPage, null , strAttr);
            if (languages != null) {
                var callObj = ClientManager.CMSCreateAuxCallOptions();
                callObj.setCommand("create-links-command");
                callObj.setParam("listItemId", mainForm.listItemId.value);
                callObj.setParam("languages", languages[0]);
                result = ClientManager.CMSUpdateEntity(callObj);
                if (!result.error) {
                    mainForm.itemsLink.value = languages[1];
                }
            }
        }

        // Removes link to item from default language
        function unlinkItem() {
            var callObj = ClientManager.CMSCreateAuxCallOptions();
            callObj.setCommand("unlink-command");
            callObj.setParam("listItemId", mainForm.listItemId.value);
            callObj.setParam("action", "unlink");

            var result = ClientManager.CMSUpdateEntity(callObj);
            if (!result.error) {
                mainForm.itemsLink.value = s_NoPresentInOtherLanguages;
                window.location.reload();
            }
        }

        // Enable or disable text fields and buttons
        function setModifiable(state) {
            mainForm.titleField.readOnly = !state;
            mainForm.publishDateField.disabled = !state;
            mainForm.expiredDateField.disabled = !state;
            mainForm.viewableDateField.disabled = !state;
            
            mainForm.selectPageButton.disabled = !state;
            mainForm.clearPageButton.disabled = !state;
            mainForm.selectImageButton.disabled = !state;
            mainForm.clearImageButton.disabled = !state;
            mainForm.selectDocumentButton.disabled = !state;
            mainForm.clearDocumentButton.disabled = !state;
            publicCheckbox.disabled = !state;
            mainForm.saveButton.disabled = !state;
            mainForm.saveAndCloseButton.disabled = !state;
            mainForm.resetButton.disabled = !state;
            saveButton.disabled = !state;
            saveAndCloseButton.disabled = !state;
            resetButton.disabled = !state;
        }
        
        function onReset() {
            if (imageLink != '/images/0.gif') {
                document.all.photoImage.outerHTML = 
                    "<img hspace='5' vspace='5' id='photoImage' " + 
                         "src='../" + imageLink +"'>";
            }
            else {
                 $('#photoImage').attr('src','/images/0.gif');
            }
            
        }

        function setItemVisibility() {
            /* // OLDDD
            var callObj = ClientManager.CMSCreateAuxCallOptions();
            var isPublic = $('#publicCheckbox').is(":checked");
            callObj.setCommand("set-list-item-visibility-command");
            callObj.setParam("listItemId", mainForm.listItemId.value);
            callObj.setParam("public", isPublic);
            var result = ClientManager.CMSUpdateEntity(callObj);
            */
            var isPublic = $('#publicCheckbox').is(":checked");
			AJAX_Send("set-list-item-visibility-command", {listItemId: mainForm.listItemId.value, public: isPublic})            
        }
        
          var summary=' ';
          var text=' ';
        
        function setSummary(val){
        	summary=val;
        }
        function getSummary(){
        	return summary;
        }

       
        ]]>
       </xsl:text>
    </script>
    <xsl:call-template name="adminhead"/>
</head>
    <body style="behavior: url(/script/webservice.htc) url(/script/clientmanager.htc)"
        id="ClientManager">
        <xsl:attribute name="onload">
            <xsl:if test="negeso:listItem/@linked or negeso:listItem/@canContribute != 'true'">
                <script>setModifiable(false);</script>
            </xsl:if>
        </xsl:attribute>
        <!-- CONTENT -->
        <xsl:call-template name="NegesoBody">
            <xsl:with-param name="helpLink">
                <xsl:text>/admin/help/cnw1_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>
            </xsl:with-param>            
            <xsl:with-param name="backLink" select="concat('?command=get-list-command&amp;listId=', //negeso:listItem/@listId)"/>
        </xsl:call-template>        
        <xsl:call-template name="buttons"/>
        <script>
            Cufon.now();
            Cufon.replace('.admNavPanelFont', { fontFamily: 'Orator Std' });
            Cufon.replace('.admTDtitles', { fontFamily: 'Orator Std' });
        </script>
</body>
</html>
</xsl:template>

<xsl:template match="negeso:listItem" mode="admContent">
        <!-- Update/reset fields -->      
        <div class="admCenter">
            <font color="#FF0000">
                <xsl:value-of select="errorMessage"/>
            </font>
        </div>
        <table cellspacing="0" cellpadding="0" width="100%" class="admTable">
            <form method="POST" name="mainForm" enctype="multipart/form-data" action="">
                <xsl:attribute name="onReset">
                    onReset();
                    <xsl:for-each select="/negeso:listItem/negeso:teaser/negeso:article">
                        <script>
                            document.all.article_text<xsl:value-of select="@id"/>.innerHTML=getSummary();
                            document.all.article_text<xsl:value-of select="@id"/>.SaveArticle('true');
                        </script>
                    </xsl:for-each>
                    <xsl:for-each select="/negeso:listItem/negeso:details/negeso:article">
                        <script>
                            document.all.article_text<xsl:value-of select="@id"/>.innerHTML=getSummary();
                            document.all.article_text<xsl:value-of select="@id"/>.SaveArticle('true');
                        </script>
                    </xsl:for-each>
                </xsl:attribute>
                <input type="hidden" name="command" value="update-list-item-command"/>
                <input type="hidden" name="listId" value="{@listId}"/>
                <input type="hidden" name="listItemId" value="{@id}"/>
            <tr>
                <td align="center" class="admNavPanelFont"  colspan="6">
                    <!-- TITLE -->                    
                    <xsl:value-of select="java:getString($dict_news_module, 'EDIT_NEWS')"/>                    
                </td>
            </tr>            
            <!-- Title Field -->
            <tr>
                <th class="admTableTD admWidth200"><xsl:value-of select="java:getString($dict_common, 'TITLE')"/></th>
                <td class="admTableTDLast">
                    <input class="admTextArea" type="text" name="titleField">
                        <xsl:attribute name="value"><xsl:value-of select="@title"/></xsl:attribute>
                    </input>
                </td>
            </tr>
            <!-- Publish Date -->
            <tr>
                <th class="admTableTD"><xsl:value-of select="java:getString($dict_common, 'PUBLISH_DATE')"/></th>
                <td class="admTableTDLast">
                    <table cellspacing="0" cellpadding="0">
                        <tr>
                            <td>                         
                                <input type="text" class="admTextArea" readonly="readonly" timedate="true" name="publishDateField" id="publishDatepicker">
                                    <xsl:attribute name="value">
                                        <xsl:value-of select="@publishDate"/>
                                    </xsl:attribute>
                                </input>                               
                            </td>
                            <td>&#160;(dd-mm-yyyy)</td>                
                <td>

                    <div class="admNavPanelInp" style="padding-left:5px">
                        <div class="imgL"></div>
                        <div>
                            <input onClick="addpublishDateField()" type="button"  class="admNavPanelInp" style="width:83px;">
                                <xsl:attribute name="value">
                                    <xsl:value-of select="java:getString($dict_dialogs, 'SHOW_PAGE')"/>
                                </xsl:attribute>
                            </input>
                        </div>
                        <div class="imgR"></div>
                    </div>
                </td>
                        </tr>
                    </table>
                </td>
            </tr>
            <!-- Expired Date -->
            <tr>
                <th class="admTableTD"><xsl:value-of select="java:getString($dict_common, 'EXPIRED_DATE')"/></th>
                <td class="admTableTDLast">
                    <table cellspacing="0" cellpadding="0">
                        <tr>

                            <td>
                                <input type="text" class="admTextArea" readonly="readonly" timedate="true" name="expiredDateField" id="expiredDatepicker" value="">
                                    <xsl:attribute name="value">
                                        <xsl:value-of select="@expiredDate"/>
                                    </xsl:attribute>
                                </input>
                            </td>                          
                            
                            <td>&#160;(dd-mm-yyyy)</td>
                            <td>                                
                            
                                <div class="admNavPanelInp" style="padding-left:5px">
                                    <div class="imgL"></div>
                                    <div>
                                        <input id="addexpiredDateButton" onClick="addExpiredDate()" type="button"  class="admNavPanelInp" style="width:83px;">
                                            <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_dialogs, 'HIDE_PAGE')"/>  </xsl:attribute>
                                        </input>
                                    </div>
                                    <div class="imgR"></div>
                                </div>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
            <!-- Viewable Date -->
            <tr>
                <th class="admTableTD"><xsl:value-of select="java:getString($dict_common, 'VIEWABLE_DATE')"/></th>
                <td class="admTableTDLast">
                    <table cellspacing="0" cellpadding="0">
                        <tr>
                            <td xwidth="325">
                                <input type="text" class="admTextArea" readonly="readonly" timedate="true" name="viewableDateField" id="viewableDatepicker" value="">
                                    <xsl:attribute name="value">
                                        <xsl:value-of select="@viewDate"/>
                                    </xsl:attribute>
                                </input>
                            </td>
                            <td>&#160;(dd-mm-yyyy)</td>
                            <td>
                                <div class="admNavPanelInp" style="padding-left:10px;">
                                    <div class="imgL"></div>
                                    <div>
                                        <input  onClick="clearviewableDateField()" type="button"  class="admNavPanelInp" style="width:50px;">
                                            <xsl:attribute name="value">
                                                <xsl:value-of select="java:getString($dict_common, 'CLEAR')"/>
                                            </xsl:attribute>
                                        </input>
                                    </div>
                                    <div class="imgR"></div>
                                </div>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>          
            <!-- Link to page -->
            <xsl:if test="not(@moduleName = 'dynamic_item')">
	            <tr>
	                <th class="admTableTD"><xsl:value-of select="java:getString($dict_news_module, 'LINK_TO_PAGE')"/></th>
	                <td class="admTableTDLast">
	                    <table cellspacing="0" cellpadding="0" width="100%">
	                        <tr>
	                            <td width="300px;">
	                                <input type="hidden" name="pageLink" id="pageLinkId">
	                                    <xsl:attribute name="value"><xsl:value-of select="@href"/></xsl:attribute>
	                                </input>
	                                <input class="admTextArea" type="text" name="showLink" id="showLinkId" readonly="true">
	                                    <xsl:attribute name="value"><xsl:value-of select="@link-page-name"/></xsl:attribute>
	                                </input>
	                            </td>
	                            <td>
	                                <div class="admNavPanelInp">
	                                    <div class="imgL"></div>
	                                    <div>
	                                        <input id="selectPageButton" onClick="openPageExplorer()" type="button" class="admNavPanelInp">
	                                            <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_common, 'SELECT')"/></xsl:attribute>
	                                        </input>
	                                    </div>
	                                    <div class="imgR"></div>
	                                </div>
	                                <div class="admNavPanelInp" style="padding-left:32px;">
	                                    <div class="imgL"></div>
	                                    <div>
	                                        <input id="clearPageButton" onClick="clearPageLink()" type="button" class="admNavPanelInp"  style="width:50px;">
	                                            <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_common, 'CLEAR')"/></xsl:attribute>
	                                        </input>
	                                    </div>
	                                    <div class="imgR"></div>
	                                </div>
	                            </td>
	                        </tr>
	                    </table>                   
	                </td>
	            </tr>
            </xsl:if>
            <!-- Another languages -->
            <tr>
                <th class="admTableTD"><xsl:value-of select="java:getString($dict_news_module, 'LANGUAGES')"/></th>
                <td class="admTableTDLast">
                    <table cellspacing="0" cellpadding="0" width="100%">
                        <tr>
                            <td width="300px;">
                                <input class="admTextArea" type="text" name="itemsLink" readonly="true">
                                    <xsl:attribute name="value">
                                        <xsl:choose>
                                            <xsl:when test="@linked"><xsl:value-of select="java:getString($dict_news_module, 'LINKED_TO_ORIGINAL')"/>: <xsl:value-of select="@linked"/></xsl:when>
                                            <xsl:when test="@mirrored"><xsl:value-of select="@mirrored"/></xsl:when>
                                            <xsl:otherwise><xsl:value-of select="java:getString($dict_news_module, 'NOT_PRESENT_IN_OTHER_LANGUAGES')"/></xsl:otherwise>
                                        </xsl:choose>
                                    </xsl:attribute>
                                </input>
                            </td>
                            <td>
                                <div class="admNavPanelInp">
                                    <div class="imgL"></div>
                                    <div>
                                        <input name="selectLanguagesButton" type="button" class="admNavPanelInp">
                                            <xsl:attribute name="value">
                                                <xsl:choose>
                                                    <xsl:when test="@linked"><xsl:value-of select="java:getString($dict_news_module, 'UNLINK')"/></xsl:when>
                                                    <xsl:otherwise><xsl:value-of select="java:getString($dict_common, 'SELECT')"/></xsl:otherwise>
                                                </xsl:choose>
                                            </xsl:attribute>
                                            <xsl:attribute name="onClick">
                                                <xsl:choose>
                                                    <xsl:when test="@linked">unlinkItem()</xsl:when>
                                                    <xsl:otherwise>setLanguages()</xsl:otherwise>
                                                </xsl:choose>
                                            </xsl:attribute>
                                            <xsl:if test="@linked and @canContribute != 'true'">
                                                <xsl:attribute name="disabled">true</xsl:attribute>
                                            </xsl:if>
                                        </input>
                                    </div>
                                    <div class="imgR"></div>
                                </div>                                
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
            <!-- Image -->
            <tr>
                <th class="admTableTD" height="23"><xsl:value-of select="java:getString($dict_common, 'IMAGE')"/></th>
                <td class="admTableTDLast">
                    <table cellspacing="0" cellpadding="0" width="100%">
                        <tr>
                            <td width="300px;">
                                <div class="admLeft">
                                    <img id="photoImage">
                                        <xsl:attribute name="src">
                                            <xsl:choose>
                                                <xsl:when test="@imageLink">
                                                    <xsl:text>../</xsl:text><xsl:value-of select="@imageLink"/>
                                                </xsl:when>
                                                <xsl:otherwise>
                                                    <xsl:text>/images/0.gif</xsl:text>
                                                </xsl:otherwise>
                                            </xsl:choose>
                                        </xsl:attribute>
                                    </img>
                                </div>
                               
                                <input type="text" name="imageLink" readonly="true" class="admTextArea">                                   
                                   <xsl:attribute name="value"><xsl:value-of select="//negeso:listItem/@imageLink"/></xsl:attribute>
                                </input>
                                <input type="hidden" name="thumbnailLink" value="{@thumbnailLink}"/>
                            </td>
                            <td>
                                <div class="admNavPanelInp">
                                    <div class="imgL"></div>
                                    <div>
                                        <input name="selectImageButton" 
                                              onClick="selectImageDialog({@thumbnail-width}, {@thumbnail-height}, {@moduleName='dynamic_item'})"
                                             type="button"
                                             class="admNavPanelInp">
                                            <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_common, 'SELECT')"/></xsl:attribute>
                                        </input>
                                    </div>
                                    <div class="imgR"></div>
                                </div>
                                <div class="admNavPanelInp" style="padding-left:32px;">
                                    <xsl:if test="not(@imageLink='')">
                                        <xsl:attribute name="class">admNavPanelInp</xsl:attribute>
                                    </xsl:if>
                                    <div class="imgL"></div>
                                    <div>
                                        <input name="clearImageButton" onClick="clearImageLink()" type="button" class="admNavPanelInp"  style="width:50px;">
                                            <xsl:attribute name="value"> <xsl:value-of select="java:getString($dict_common, 'CLEAR')"/> </xsl:attribute>
                                            <!--<xsl:choose>
                                                <xsl:when test="@imageLink!=''"></xsl:when>
                                                --><!-- @documentLink --><!--
                                                <xsl:otherwise>
                                                    <xsl:attribute name="disabled">true</xsl:attribute>
                                                </xsl:otherwise>
                                            </xsl:choose>-->
                                        </input>
                                    </div>
                                    <div class="imgR"></div>
                                </div>
                            </td>
                        </tr>
                    </table>                 
                </td>
            </tr>
            <!-- Document -->
            <xsl:if test="not(@moduleName = 'dynamic_item')">
	            <tr>
	                <th class="admTableTD" height="23"><xsl:value-of select="java:getString($dict_common, 'DOCUMENT')"/></th>
	                <td class="admTableTDLast">
	                    <table cellspacing="0" cellpadding="0" width="100%">
	                        <tr>
	                            <td width="300px;">
	                                
	                               <input type="text" name="documentLink" readonly="true" class="admtextArea" style="width:300px;">
	                                    <xsl:choose>
	                                        <xsl:when test="@documentLink">
	                                            <xsl:attribute name="value"><xsl:value-of select="//negeso:listItem/@documentLink"/></xsl:attribute>
	                                        </xsl:when>                                      
	                                    </xsl:choose>
	                                </input>
	                            </td>
	                            <td>
	                                <div class="admNavPanelInp">
	                                    <div class="imgL"></div>
	                                    <div>
	                                        <input name="selectDocumentButton" onClick="selectDocumentDialog()" type="button" class="admNavPanelInp" >
	                                            <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_common, 'SELECT')"/></xsl:attribute>
	                                        </input>
	                                    </div>
	                                    <div class="imgR"></div>
	                                </div>
	                                <div class="admNavPanelInp" style="padding-left:32px;">
	                                    <xsl:if test="not(@documentLink='')">
	                                        <xsl:attribute name="class">admNavPanelInp</xsl:attribute>
	                                    </xsl:if>
	                                    <div class="imgL"></div>
	                                    <div>
	                                        <input name="clearDocumentButton" onClick="clearDocumentLink()" type="button" class="admNavPanelInp"  style="width:50px;">
	                                            <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_common, 'CLEAR')"/></xsl:attribute>
	                                            <!--<xsl:choose>
	                                                <xsl:when test="@documentLink!=''"></xsl:when>
	                                                --><!-- @documentLink --><!--
	                                                <xsl:otherwise>
	                                                    <xsl:attribute name="disabled">true</xsl:attribute>
	                                                </xsl:otherwise>
	                                            </xsl:choose>-->
	                                        </input>
	                                    </div>
	                                    <div class="imgR"></div>
	                                </div>
	                            </td>
	                        </tr>
	                    </table>
	                </td>
	            </tr>
            </xsl:if>
            <tr>
                <th class="admTableTD admWidth200"><xsl:value-of select="java:getString($dict_news_module_custom, 'ID_PER_LANGUAGE')"/></th>
                <td class="admTableTDLast">
                    <input class="admTextArea" type="text" name="perLangId">
                        <xsl:attribute name="value"><xsl:value-of select="@perLangId"/></xsl:attribute>
                    </input>
                </td>
            </tr>
            <!-- Parameters -->
            <tr id="showListItemParameters" style="display: none;">
                <th class="admTableTD admWidth200"><xsl:value-of select="java:getString($dict_news_module, 'PARAMETERS')"/></th>
                <td class="admTableTDLast">
                    <input class="admTextArea admWidth95perc" type="text" name="parameters">
                        <xsl:attribute name="value"><xsl:value-of select="@parameters"/></xsl:attribute>
                    </input>
                </td>
            </tr>
        
       
    
	        <!-- Summary article -->
	        <tr>
	            <td class="admTableTD admWidth200">
	                <xsl:value-of select="java:getString($dict_news_module, 'SUMMARY_ARTICLE')"/><br/><br/>
	                <table cellpadding="0" cellspacing="0"  style="width: 100%;">
                        <tr>
                            <td>
                                <xsl:value-of select="java:getString($dict_news_module, 'MAKE_IT_PUBLIC')"/>&#160;
                            </td>
                            <td>
                                <input type="checkbox" name="publicCheckbox" id="publicCheckbox" value="{@id}" style="width: auto;">
				                    <xsl:if test="@publicSummary = 'true'">
				                        <xsl:attribute name="checked">true</xsl:attribute>
				                    </xsl:if>
				                    <xsl:if test="@linked or @public = 'true'">
				                       <xsl:attribute name="disabled">true</xsl:attribute>
				                    </xsl:if>
				                </input>
                            </td>
                        </tr>
		                <xsl:for-each select="./negeso:socialNetworks/negeso:socialNetwork">
			                <tr>
			                   <td>
				                    <xsl:value-of select="java:getString($dict_news_module_custom, 'POST_TO_')"/>&#160;<xsl:value-of select="@title"/>&#160;
			                   </td>
			                   <td>
				                    <input type="checkbox" name="{@title}" value="true" style="width: auto;">
					                    <xsl:if test="@checked = 'true'">
					                        <xsl:attribute name="checked">true</xsl:attribute>
					                        <xsl:attribute name="disabled">true</xsl:attribute>
					                    </xsl:if>
					                </input>
			                   </td>
			                </tr>
		                </xsl:for-each>
	                </table>
	            </td>
	            <td class="admTableTDLast">
	                <xsl:apply-templates select="negeso:teaser">
	                    <xsl:with-param name="classType" select="'shortNews'"/>
	                </xsl:apply-templates>
	            </td>
	        </tr>
         </form>
        <!-- Text of article -->
        <tr>
            <th class="admTableTD">
                <xsl:value-of select="java:getString($dict_news_module, 'TEXT_OF_ARTICLE')"/>
            </th>
            <td class="admTableTDLast">
                <xsl:apply-templates select="negeso:details"/>
            </td>
        </tr>
        <tr>
            <td class="admTableFooter" colspan="2" >&#160;</td>
        </tr>
    </table>        
    <!-- Update/reset fields -->
</xsl:template>
    
<!--******************** Bottom buttons ********************-->
<xsl:template name="buttons">
    <table cellpadding="0" cellspacing="0"  width="764px" align="center" class="Buttons">
        <tr>
            <td>
                <div class="admBtnGreenb">
                    <div class="imgL"></div>
                    <div >
                        <a focus="blur()" class="admBtnText" onClick="onSubmit()" href="#onSubmit()">
                            <xsl:value-of select="java:getString($dict_common, 'SAVE')"/>
                        </a>
                    </div>
                    <div class="imgR"></div>
                </div>
                <div class="admBtnGreenb">
                    <div class="imgL"></div>
                    <div >
                        <a focus="blur()" class="admBtnText" onClick="onSubmit('close')" href="#onSubmit('close')">
                            <xsl:value-of select="java:getString($dict_common, 'SAVE_AND_CLOSE')"/>
                        </a>
                    </div>
                    <div class="imgR"></div>
                </div>                
                <div class="admBtnGreenb" style="padding-right:20px;float:right;">
                    <div class="imgL"></div>
                    <div>
                        <a class="admBtnText" id="deleteItems" focus="blur()" onClick="DeleteItems()" href="#DeleteItems();">
                            <xsl:choose>
                                <xsl:when test="@canManage = 'true'">
                                    <xsl:attribute name="href">javaScript:DeleteItems()</xsl:attribute>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:attribute name="href">#</xsl:attribute>
                                </xsl:otherwise>
                            </xsl:choose>
                            <xsl:value-of select="java:getString($dict_common, 'DELETE')"/>
                        </a>
                    </div>
                    <div class="imgR"></div>
                </div>
                <div class="admBtnGreenb" style="float:right;">
                    <div class="imgL"></div>
                    <div>
                        <a class="admBtnText" onClick="mainForm.reset();" href="#mainForm.reset();" focus="blur()">                            
                            <xsl:value-of select="java:getString($dict_common, 'RESET')"/>                            
                        </a>
                    </div>
                    <div class="imgR"></div>
                </div>
            </td>
        </tr>
    </table>
</xsl:template>
    
<xsl:template match="negeso:teaser">
<xsl:param name="classType"/>
    <xsl:apply-templates select="negeso:article">
    	<xsl:with-param name="classType" select="$classType"/>
    </xsl:apply-templates>
</xsl:template>
<xsl:template match="negeso:details">
    <xsl:apply-templates select="negeso:article"/>
</xsl:template>
<xsl:template match="negeso:article" >
<xsl:param name="classType"/>
    <xsl:choose>
        <xsl:when test="/negeso:listItem/@linked or /negeso:listItem/@canContribute != 'true'"/>
        <xsl:otherwise>
            <xsl:choose>
                <xsl:when test="$classType = 'shortNews'">
                    <div style="display: none" id="content_1">
                        <xsl:value-of select="negeso:text" disable-output-escaping="yes"/>
                    </div>
                    <script>setSummary(document.getElementById('content_1').innerHTML);</script>
                    <img src="/images/mark_1.gif" onclick="edit_text('article_text{@id}', 'shortNews', 595);" class="admBorder admHand" style="margin-left: 5px;">
                        <xsl:attribute name="alt"><xsl:value-of select="java:getString($dict_news_module, 'EDIT_ARTICLE')"/></xsl:attribute>
                    </img>
                </xsl:when>
                <xsl:otherwise>
                    <div style="display: none" id="content_2">
                        <xsl:value-of select="negeso:text" disable-output-escaping="yes"/>
                    </div>
                    <script>setSummary(document.getElementById('content_2').innerHTML);</script>
                    <img src="/images/mark_1.gif" onclick="edit_text('article_text{@id}', 'contentStyle', 595);" class="admBorder admHand" style="margin-left: 5px;">
                        <xsl:attribute name="alt"><xsl:value-of select="java:getString($dict_news_module, 'EDIT_ARTICLE')"/></xsl:attribute>
                    </img>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:otherwise>
    </xsl:choose>
	<div id="article_text{@id}" class="contentStyle">
		<xsl:if test="$classType = 'shortNews'">
			<xsl:attribute name="class">shortNews</xsl:attribute>
		</xsl:if>
	   <xsl:attribute name="style">behavior:url(/script/article3.htc); margin: 5px;border: 1px solid #848484;</xsl:attribute>
		<xsl:choose>
			<xsl:when test="negeso:text/text()">
				<xsl:value-of select="negeso:text" disable-output-escaping="yes"/>
			</xsl:when>
			<xsl:otherwise>&#160;</xsl:otherwise>
		</xsl:choose>
	</div>
    <script>
        makeReadonly(article_text<xsl:value-of select="@id" />, true);
    </script>
</xsl:template>
</xsl:stylesheet>
