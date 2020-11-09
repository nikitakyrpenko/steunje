<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)${edit_item.xsl}

  Copyright (c) 2003 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential
  Information and shall use it only in accordance with the terms of the
  license agreement you entered into with Negeso.

  View of a list item edit interface.

  @version      2003.12.23
  @author       Olexiy.Strashko
  @author       Volodymyr Snigur
-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">

<xsl:include href="/xsl/negeso_body.xsl"/>
<xsl:include href="/xsl/admin_templates.xsl"/>

<xsl:variable name="lang" select="/*/@interface-language"/>
<xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>
<xsl:variable name="dict_news_module" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_news_module.xsl', $lang)"/>
<xsl:variable name="dict_faq" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_faq.xsl', $lang)"/>

<xsl:output method="html"/>
<xsl:template match="/" >
<html>
<head>
    <title>
    	<xsl:choose>
        	<xsl:when test="//negeso:listItem/@rootListId = //negeso:listItem/@listId">
       			<xsl:value-of select="java:getString($dict_common, 'EDIT_CATEGORY')"/>
       		</xsl:when>
       		<xsl:otherwise>
       			<xsl:value-of select="java:getString($dict_faq, 'EDIT_QUESTION')"/>
       		</xsl:otherwise>
        </xsl:choose>
    	<!-- <xsl:value-of select="java:getString($dict_faq, 'EDIT_QUESTION')"/> -->
    </title>
    <link rel="stylesheet" href="/css/admin_style.css" type="text/css"/>
    <link href="/site/core/css/default_styles.css" rel="stylesheet" type="text/css"/>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>    

    <script type="text/javascript" src="/script/jquery.min.js"></script>
    <script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
    <script type="text/javascript" src="/script/cufon-yui.js"></script>
    <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
    <script type="text/javascript" src="/script/common_functions.js"></script>
    
    <script language="JavaScript">
        var s_ImageIsNotChosen = "<xsl:value-of select="java:getString($dict_news_module, 'IMAGE_NOT_CHOSEN')"/>";
        var s_DocumentIsNotChosen = "<xsl:value-of select="java:getString($dict_news_module, 'DOCUMENT_NOT_CHOSEN')"/>";
        var s_WishToRemove = "<xsl:value-of select="java:getString($dict_news_module, 'WISH_TO_REMOVE')"/>";
        var s_ArticleNotSaved = "<xsl:value-of select="java:getString($dict_news_module, 'ARTICLE_NOT_SAVED')"/>";
        var s_ActiveElement = "<xsl:value-of select="java:getString($dict_news_module, 'ACTIVE_ELEMENT')"/>";
        var s_TitleIsEmpty = "<xsl:value-of select="java:getString($dict_faq, 'QUESTION_TITLE_IS_EMPTY')"/>";
        <xsl:text disable-output-escaping="yes">
        <![CDATA[

        function checkUploadImageForm() {
            if ((document.imageForm.imageField.value == null) || (document.imageForm.imageField.value == "")){
                return confirm(s_ImageIsNotChosen + ' ' + s_WishToRemove);
            }
            return true;
        }

        function checkUploadDocumentForm() {
            if (
                (document.documentForm.documentField.value == null) || 
                (document.documentForm.documentField.value == ""))
            {
                return confirm(s_DocumentIsNotChosen + ' ' + s_WishToRemove);
            }
            return true;
        }

        function checkDate(val){
            if(/^(\d{1,4})\-(\d{1,2})\-(\d{1,2})$/.test(val)){
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
        
        function saveCategory() {
          if (window.document.getElementById("titleField").value == "") {
	       	alert(s_TitleIsEmpty);
          } else mainForm.submit();
        }
        
        function resetForm()
		{
			var backupArticle = $("input[name^='backup']"); //doing backup of article before changes
			var resetForm = document.getElementById('operateFormId'); //select main form
			var realArticle = $("div[id^='article_text']");  //select div with article text
			var articleId = $("input[name^='article_text']");  //select id of the article
			resetForm.reset();  //reset the other fields of the form
			for (var i=0; i < backupArticle.length; i++)
			{
				realArticle[i].innerHTML = backupArticle[i].value; //on click to "reset" button change value of article's div to beginning value
				AJAX_Send("update-article-text-command", {id: articleId[i].id, text: realArticle[i].innerHTML});//send beginning value of article to DB	
			}
		}
        
        ]]>
        </xsl:text>
    </script>
    <xsl:call-template name="adminhead"/>
</head>
    <body
        style="behavior: url(/script/webservice.htc) url(/script/clientmanager.htc)"
        id="ClientManager" xmlID="{@id}">
        
        <!-- NEGESO HEADER -->
        <xsl:call-template name="NegesoBody">
            <xsl:with-param name="backLink" select="concat('?command=get-list-command&amp;listId=', negeso:listItem/@rootListId, '&amp;listPath=', negeso:listItem/@listPath, '#l', negeso:listItem/@id)"/>
            <xsl:with-param name="helpLink">
                <xsl:text>/admin/help/cfq1_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>
            </xsl:with-param>
        </xsl:call-template>
        <xsl:call-template name="buttons"/>
    </body>
</html>
</xsl:template>
    

<xsl:template match="negeso:listItem"  mode="admContent">
    <form method="POST" name="mainForm" enctype="multipart/form-data" id="operateFormId">
        <input type="hidden" name="command" value="update-list-item-command"/>
        <input type="hidden" name="listId" value="{@listId}"/>
        <input type="hidden" name="listItemId" value="{@id}"/>
        <input type="hidden" name="publishDateField">
            <xsl:attribute name="value"><xsl:value-of select="@publishDate"/></xsl:attribute>
        </input>
        <input type="hidden" name="listPath" value="{@listPath}"/>
        <input type="hidden" name="rootListId" value="{@rootListId}"/>

        <table align="center" border="0" cellpadding="0" cellspacing="0" class="admTable">
            <tr>
                <td class="admNavPanelFont" colspan="2" align="center">
                    <xsl:call-template name="tableTitle">
                        <xsl:with-param name="headtext">
                            <xsl:choose>
                                <xsl:when test="@rootListId = @listId">
                                    <xsl:value-of select="java:getString($dict_common, 'EDIT_CATEGORY')"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="java:getString($dict_faq, 'EDIT_QUESTION')"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:with-param>
                    </xsl:call-template>        
                </td>
            </tr>
            <tr>
                <td  colspan="2">
                    <div class="admCenter">
                        <font color="#FF0000">
                            <xsl:value-of select="errorMessage"/>
                        </font>
                    </div>
                </td>
            </tr>
            <!-- Title Field -->
            <tr>
                <th class="admTableTD admWidth100">
                    <xsl:choose>
                        <xsl:when test="@rootListId = @listId">
                            <xsl:value-of select="java:getString($dict_common, 'TITLE')"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="java:getString($dict_faq, 'QUESTION')"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </th>
                <td class="admTableTDLast">
                    <input class="admTextArea" type="text" name="titleField" id="titleField" style="margin-left:5px; width:620px;">
                        <xsl:attribute name="value">
                            <xsl:value-of select="@title"/>
                        </xsl:attribute>
                    </input>
                </td>
            </tr>
            <!-- Teaser article text -->
            <tr>
                <th class="admTableTD admWidth100">
                    <xsl:choose>
                        <xsl:when test="@rootListId = @listId">
                            <xsl:value-of select="java:getString($dict_faq, 'COMMENTS')"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="java:getString($dict_faq, 'ANSWER')"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </th>
                <td class="admTableTDLast">
                    <input type="hidden" class="admTextArea" name="backup" value="{negeso:teaser/negeso:article/negeso:text/text()}"/>
                    <xsl:apply-templates select="negeso:teaser"/>
                </td>
            </tr>
            <tr>
                <td class="admTableFooter" colspan="2">&#160;</td>
            </tr>
        </table>
    </form>
</xsl:template>

<xsl:template match="negeso:teaser">
    <xsl:apply-templates select="negeso:article"/>
</xsl:template>

<xsl:template match="negeso:article" >
	<input id="{@id}" name="article_text" type="hidden"/>
    <img src="/images/mark_1.gif" onclick="edit_text('article_text{@id}', 'contentStyle', 595);" class="admBorder admHand" style="margin-left: 5px;" alt="Edit the article"/>
	<div id="article_text{@id}" class="contentStyle">
		<xsl:attribute name="style">behavior:url(/script/article3.htc); margin: 5px;border: 1px solid #848484;</xsl:attribute>
		<xsl:choose>
			<xsl:when test="negeso:text/text()">
				<xsl:value-of select="negeso:text" disable-output-escaping="yes"/>
			</xsl:when>
			<xsl:otherwise>&#160;</xsl:otherwise>
		</xsl:choose>
	</div>
    <script>makeReadonly(document.getElementById('article_text<xsl:value-of select="@id" />'), true);</script>
</xsl:template>

<!--******************** Bottom buttons ********************-->
<xsl:template name="buttons">
    <table cellpadding="0" cellspacing="0"  width="764px" align="center" class="Buttons">
        <tr>
            <td>
                <div class="admBtnGreenb">
                    <div class="imgL"></div>
                    <div>
                        <input class="admBtnText" type="button" onClick="saveCategory()">
                            <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_common, 'SAVE')"/></xsl:attribute>
                        </input>
                    </div>
                    <div class="imgR"></div>
                </div>                
                <div class="admBtnGreenb" >
                    <div class="imgL"></div>
                    <div>
                        <input class="admBtnText" type="button" onClick="resetForm();">
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
