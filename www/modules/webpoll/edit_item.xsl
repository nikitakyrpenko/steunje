<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)${edit_item.xsl}

  Copyright (c) 2004 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential
  Information and shall use it only in accordance with the terms of the
  license agreement you entered into with Negeso.

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

<xsl:variable name="lang" select="/*/@interface-language"/>
<xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>
<xsl:variable name="dict_news_module" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_news_module.xsl', $lang)"/>
<xsl:variable name="dict_webpoll" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_webpoll.xsl', $lang)"/>

<xsl:variable name="isPoll">
    <xsl:choose>
        <xsl:when test="/negeso:listItem/negeso:teaser/negeso:article">true</xsl:when>
        <xsl:otherwise>false</xsl:otherwise>
    </xsl:choose>
</xsl:variable>

<xsl:template match="/">
<html>
<head>
    <title>
        <xsl:choose>
            <xsl:when test="$isPoll = 'true'"><xsl:value-of select="java:getString($dict_webpoll, 'EDIT_POLL_QUESTION')"/></xsl:when>
            <xsl:otherwise><xsl:value-of select="java:getString($dict_webpoll, 'EDIT_OPTION')"/></xsl:otherwise>
        </xsl:choose>
    </title>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link rel="stylesheet" href="/css/admin_style.css" type="text/css"/>
    <link href="/site/core/css/default_styles.css" rel="stylesheet" type="text/css"/>
    <link rel="stylesheet" type="text/css" href="/css/jquery-ui.custom.css"/>

    <script type="text/javascript" src="/script/jquery.min.js"></script>
    <script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
    <script type="text/javascript" src="/script/cufon-yui.js"></script>
    <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
    <script language="JavaScript" src="/script/common_functions.js" type="text/javascript">/**/</script>
    <script language="JavaScript1.2" src="/script/media_catalog.js" type="text/javascript"/>
    <script language="JavaScript1.2" src="/script/calendar_picker.js" type="text/javascript"/>
    <script type="text/javascript" src="/script/RTE_Adapter.js">/**/</script>

    <script language="JavaScript">
        var s_InvalidPublishDate = "<xsl:value-of select="java:getString($dict_news_module, 'INVALID_PUBLISH_DATE')"/>";
        var s_InvalidExpiredDate = "<xsl:value-of select="java:getString($dict_news_module, 'INVALID_EXPIRED_DATE')"/>";
        var s_WishToRemove = "<xsl:value-of select="java:getString($dict_news_module, 'WISH_TO_REMOVE')"/>";

        // Image link value for restore if form reset
        var imageLink = "<xsl:value-of select='negeso:listItem/@thumbnailLink'/>";
        if (imageLink == "")
            imageLink = "/images/0.gif";

        var imageHtml = "";

        <xsl:text disable-output-escaping="yes">
        <![CDATA[
        function selectImageDialog(thWidth, thHeight){
            MediaCatalog.selectThumbnailImageDialog(thWidth, thHeight, function(result){
            if (result != null){
                if (result.resCode == "OK"){
                    document.mainForm.imageLink.value = result.realImage;
                    document.mainForm.thumbnailLink.value = result.thumbnailImage;

                    document.all.photoImage.outerHTML = 
                        "<img hspace='5' vspace='5' id='photoImage' " + 
                        "src='../" + result.thumbnailImage + "' class='admBorder admHand' onClick=\"openImage('" + result.realImage + "');\">";
                }
            }
            })
        }

        function openImage(link, width, height) {
			if ((width == null) || (width == '')){
				width='800';
			}
			if ((height == null) || (height == '')){
				height='600';
			}
			strAttr = "resizable:yes;scroll:no;status:no;dialogWidth:" + width + "px;dialogHeight:" + height + "px;";
			window.showModalDialog(link, null , strAttr);
			return false;
		}

        function checkDate(val) {
            if(/^(\d{1,4})\-(\d{1,2})\-(\d{1,2})$/.test(val)) {
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
            }
        }

        function formSubmit(close) {
            if (document.mainForm.publishDateField == null) {
                if (close == null)
                   mainForm.listId.value = null;
                mainForm.submit();
                return;
            }
            /*if (!checkDate(document.mainForm.publishDateField.value)) {
            	alert(document.mainForm.publishDateField.value);
                alert(s_InvalidPublishDate);
                return;
            }
            if (!checkDate(document.mainForm.expiredDateField.value)) {
                alert(s_InvalidExpiredDate);
                return;
            }*/
            if (close == null)
                mainForm.listId.value = null;
            mainForm.submit();
        }

        function formReset() {
            if (imageLink != '/images/0.gif')
                document.all.photoImage.outerHTML = imageHtml;
        }
        ]]>
        </xsl:text>
    </script>    
</head>
<body style="behavior: url(/script/webservice.htc) url(/script/clientmanager.htc)" id="ClientManager">
    
    <xsl:call-template name="NegesoBody">
        <xsl:with-param name="helpLink" select="'/admin/help/cwp1_{$lang}.html'"/>
        <xsl:with-param name="backLink" select="concat('?command=get-list-command&amp;listId=', negeso:listItem/@rootListId, '&amp;listPath=', negeso:listItem/@listPath, '#l', negeso:listItem/@id)"/>
    </xsl:call-template>
    <xsl:call-template name="buttons"/>
    
    <script language="JavaScript">
        if (window.name == 'MediaCatalogPreviewWindow') {
            document.all.backLinkTop.href = 'javascript: window.close()';
            document.all.backLinkBottom.href = 'javascript: window.close()';
        }
        
         $(function () {
            $('input[name="clearImage"]').click(function(){                
                $('#imageDivId img').remove();
                $('#no_image').show();                
                $('input[name="thumbnailLink"]').val('');
                $('input[name="imageLink"]').val('');

                return false;
            });
        });
    </script>    
    <script>
        if(document.all.photoImage != undefined){
            imageHtml = document.all.photoImage.outerHTML;
        }
    </script>
    <script>
        Cufon.now();
        Cufon.replace('.admNavPanelFont', { fontFamily: 'Orator Std' });
        Cufon.replace('.admTDtitles', { fontFamily: 'Orator Std' });
    </script>
</body>
</html>
</xsl:template>

<xsl:template match="negeso:listItem" mode="admContent">
    <form method="POST" name="mainForm" onReset="formReset()" enctype="multipart/form-data">
        <input type="hidden" name="command" value="update-list-item-command"/>
        <input type="hidden" name="listId" value="{@listId}"/>
        <input type="hidden" name="listItemId" value="{@id}"/>
        <input type="hidden" name="listPath" value="{@listPath}"/>
        <input type="hidden" name="rootListId" value="{@rootListId}"/>
        <xsl:choose>
            <xsl:when test="$isPoll = 'true'">
                <input type="hidden" name="summary" value="true"/>
                <input type="hidden" name="details" value="false"/>
            </xsl:when>
            <xsl:otherwise>
                <input type="hidden" name="summary" value="false"/>
                <input type="hidden" name="details" value="false"/>
            </xsl:otherwise>
        </xsl:choose>

        <table cellspacing="0" cellpadding="0" width="100%" class="admTable">
            <tr>
                <td class="admNavPanelFont" align="center">
                    <xsl:choose>
                        <xsl:when test="$isPoll = 'true'">
                            <xsl:value-of select="java:getString($dict_webpoll, 'EDIT_POLL_QUESTION')"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="java:getString($dict_webpoll, 'EDIT_OPTION')"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </td>
            </tr>
            
            <tr>
                <td>                    
                    <table cellspacing="0" cellpadding="0" width="100%">
                        <!-- Title Field -->
                        <tr>
                            <th class="admTableTD admWidth150"><xsl:value-of select="java:getString($dict_common, 'TITLE')"/></th>
                            <td class="admTableTDLast">
                                <input class="admTextArea admWidth385" type="text" name="titleField" required="true" >
                                    <xsl:attribute name="value"><xsl:value-of select="@title"/></xsl:attribute>
                                </input>
                            </td>
                        </tr>
                        <xsl:if test="$isPoll = 'true'">
                            <!-- Publish Date -->
                            <tr>
                                <th class="admTableTD"><xsl:value-of select="java:getString($dict_common, 'PUBLISH_DATE')"/></th>
                                <td class="admTableTDLast">
                                    <input class="admTextArea admWidth200" type="text" name="publishDateField" id="publishDateFieldId">
                                        <xsl:attribute name="value"><xsl:value-of select="@publishDate"/></xsl:attribute>                                        
                                    </input>
                                    (dd-mm-yyyy)
                                </td>
                            </tr>
                            <!-- Expired Date -->
                            <tr>
                                <th class="admTableTD"><xsl:value-of select="java:getString($dict_common, 'EXPIRED_DATE')"/></th>
                                <td class="admTableTDLast">
                                    <input class="admTextArea admWidth200" type="text" name="expiredDateField" id="expiredDateFieldId">
                                        <xsl:attribute name="value"><xsl:value-of select="@expiredDate"/></xsl:attribute>                                        
                                    </input>
                                    (dd-mm-yyyy)
                                </td>
                            </tr>
                        </xsl:if>
                        <!-- Image -->
                        <tr>
                            <th class="admTableTD" height="23">
                                <xsl:value-of select="java:getString($dict_common, 'IMAGE')"/>
                            </th>
                            <td class="admTableTDLast AdmLeft admPadLeft5">
                                <div id="no_image">
                                    <xsl:if test="count(@imageLink) != 0">
                                        <xsl:attribute name="style">display:none</xsl:attribute>
                                    </xsl:if>
                                    No image
                                </div>                                
                                <xsl:if test="count(@imageLink) != 0">
                                    <div id="imageDivId">                                            
                                        <img hspace="5" vspace="5" id="photoImage" class="admBorder admHand" src="{@thumbnailLink}"
                                                onclick="openImage('{@imageLink}', {@img_width}, {@img_height});"/>
                                    </div>
                                </xsl:if>                                
                                
                                <table width="100%" cellpadding="0" cellspacing="0">
                                    <tr>
                                        <td class="admWidth265">
                                            <input class="admTextArea admWidth265" type="text" name="imageLink" readonly="true">
                                                <xsl:choose>
                                                    <xsl:when test="@imageLink">
                                                        <xsl:attribute name="value">
                                                            <xsl:value-of select="@imageLink"/>
                                                        </xsl:attribute>
                                                    </xsl:when>
                                                </xsl:choose>
                                            </input>
                                            <input type="hidden" name="thumbnailLink" value="{@thumbnailLink}"/>
                                        </td>
                                        <td class="admWidth100">
                                            <div class="admNavPanelInp">
                                                <div class="imgL"></div>
                                                <div>
                                                    <input
                                                        class="admNavPanelInp admWidth150"
                                                        onClick="selectImageDialog({@thumbnail-width}, {@thumbnail-height})"
                                                        type="button">
                                                        <xsl:attribute name="value">
                                                            <xsl:value-of select="java:getString($dict_common, 'SELECT')"/>
                                                        </xsl:attribute>
                                                    </input>
                                                </div>
                                                <div class="imgR"></div>
                                            </div>
                                        </td>
                                        <td>
                                            <div class="admNavPanelInp">
                                                <div class="imgL"></div>
                                                <div>
                                                    <input class="admNavbarInp" name="clearImage" type="button">
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
                        <xsl:if test="$isPoll = 'true'">                        
                            <!-- Teaser article text -->
                            <tr>
                                <th class="admTableTD admWidth150"><xsl:value-of select="java:getString($dict_webpoll, 'TEXT_OF_THE_QUESTION')"/></th>
                                <td class="admTableTDLast">
                                    <xsl:apply-templates select="negeso:teaser"/>
                                </td>
                            </tr>
                        </xsl:if>
                    </table>
                </td>
            </tr>
            <tr>
                <td class="admTableFooter" colspan="2" >&#160;</td>
            </tr>
        </table>
    </form>
</xsl:template>

<xsl:template match="negeso:teaser">
    <xsl:apply-templates select="negeso:article"/>
</xsl:template>

<xsl:template match="negeso:article">    
    <img src="/images/mark_1.gif" onclick="RTE_Init('article_text{@id}','article_text{@id}', {@id}, 3, 0, 'contentStyle', getInterfaceLanguage());" style="margin-left: 5px;"  class="RTEEntryPoint" alt="Editor">
        <xsl:if test="@last_modified">
            <xsl:attribute name ="alt">Editor (last modified: <xsl:value-of select="@last_modified"/>)</xsl:attribute>
        </xsl:if>
    </img>
	<div id="article_text{@id}" class="contentStyle">		
		<xsl:choose>
			<xsl:when test="negeso:text/text()">
				<xsl:value-of select="negeso:text" disable-output-escaping="yes"/>
			</xsl:when>
			<xsl:otherwise>&#160;</xsl:otherwise>
		</xsl:choose>
	</div>    
</xsl:template>

<!--******************** Bottom buttons ********************-->
<xsl:template name="buttons">
    <table cellpadding="0" cellspacing="0"  width="764px" align="center" class="Buttons">
        <tr>
            <td>
                <div class="admBtnGreenb">
                    <div class="imgL"></div>
                    <div>
                        <input class="admBtnText" type="button" onClick="if (validate(document.forms['mainForm'])) formSubmit();">
                            <xsl:attribute name="value">
                                <xsl:value-of select="java:getString($dict_common, 'SAVE')"/>
                            </xsl:attribute>
                        </input>
                    </div>
                    <div class="imgR"></div>
                </div>
                <div class="admBtnGreenb">
                    <div class="imgL"></div>
                    <div>
                        <input class="admBtnText" type="button" onClick="if (validate(document.forms['mainForm'])) formSubmit('close');">
                            <xsl:attribute name="value">
                                <xsl:value-of select="java:getString($dict_common, 'SAVE_AND_CLOSE')"/>
                            </xsl:attribute>
                        </input>
                    </div>
                    <div class="imgR"></div>
                </div>
                
                <div class="admBtnGreenb" style="padding-right:20px;float:right;">
                    <div class="imgL"></div>
                    <div>
                        <input class="admBtnText" type="button" onClick="mainForm.reset();">
                            <xsl:attribute name="value">
                                <xsl:value-of select="java:getString($dict_common, 'RESET')"/>
                            </xsl:attribute>
                        </input>
                    </div>
                    <div class="imgR"></div>
                </div>                
            </td>
        </tr>
    </table>
</xsl:template>

</xsl:stylesheet>
