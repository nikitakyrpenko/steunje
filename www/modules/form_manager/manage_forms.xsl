<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)${list.xsl}		
 
  Copyright (c) 2004 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.
 
  View of a list item edit interface.
 
  @version		2004.09.27
  @author		Alexander G. Shkabarnya
-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">

    <!-- Include/Import -->
    <xsl:include href="/xsl/negeso_body.xsl"/>
    <xsl:variable name="lang" select="/*/@interface-language"/>
    <xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>
    <xsl:variable name="dict_form_manager" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_form_manager.xsl', $lang)"/>

    <xsl:include href="/xsl/admin_templates.xsl"/>

    <xsl:template match="/">
        <html>
            <head>
                <title>
                    <xsl:value-of select="java:getString($dict_form_manager, 'FORM_MANAGER')"/>
                </title>
                <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
                <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
                <link href="/site/core/css/default_styles.css" rel="stylesheet" type="text/css"/>
                
                <script type="text/javascript" src="/script/jquery.min.js"></script>                
				<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
                <script type="text/javascript" src="/script/cufon-yui.js"></script>
                <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
                <script type="text/javascript" src="/script/common_functions.js"></script>

                <!-- Changes by Rostislav Brizgunov, 16.02.2007 -->
                <script type="text/javascript" src="/script/RTE_Adapter.js"></script>

                <!-- script block for localization mechanism -->
                <script id="localization"></script>
                <script language="JavaScript">
                    document.getElementById("localization").src =
                    "/dictionaries/dict_rte_" + getInterfaceLanguage() + ".js";
                </script>

                <script>
                    var s_DeleteFormConfirmation = "<xsl:value-of select="java:getString($dict_form_manager, 'DO_YOU_REALLY_WANT_TO_DELETE_THIS_FORM')"/>";
                    var s_InvaliEmail = "<xsl:value-of select="java:getString($dict_form_manager, 'EMAIL_IS_NOT_VALID')"/>";

                    function ChangeLanguage(){
                        main_form.command.value="manage-forms";
                        main_form.submit();
                    }

                    function DeleteForm(fid){
                        if (confirm(s_DeleteFormConfirmation)) {
                            main_form.command.value="manage-forms";
                            main_form.todo.value="delete";
                            main_form.form_id.value=fid;
                            main_form.submit();
                        }
                    }

                    function EditForm(fid){
                        main_form.command.value="manage-forms";
                        main_form.todo.value="edit";
                        main_form.form_id.value=fid;
                        main_form.submit();
                    }

                    function openFormArchive(fid){
                        main_form.action="forms_archive";
                        main_form.form_id.value=fid;
                        main_form.todo.value="viewArchive";
                        main_form.submit();
                    }

                    function AddNewForm(){
                        main_form.command.value="manage-forms";
                        main_form.todo.value="add";
                        main_form.submit();
                    }
                    
                    function SaveForm(close){
                        if (isEmailAddr(main_form.email.value)){
                            main_form.command.value="manage-forms";
                            main_form.todo.value="save";
                            main_form.form_text.value = article_text<xsl:value-of select="//negeso:form_manager/negeso:form/@article_id"/>.innerHTML;
                            article_text<xsl:value-of select="//negeso:form_manager/negeso:form/@article_id"/>.innerHTML = "";
                            
                            if (close != null) main_form.close.value = "close";                            
                            
                            main_form.submit();
                        }
                        else{
                            alert(s_InvaliEmail);
                        }                        
                    }
                    
                                        
                    function specialValidate() {
                        //return validate(document.getElementById("name")) &amp;&amp; validate(document.getElementById("email"));
                        return validate(document.getElementById("name")) &amp;&amp; isEmailAddr(document.getElementById("email").value);
                    }
                    
                    function resetForm(){
                        document.main_form.reset();
                    }
                </script>

                <xsl:if test="//negeso:form_manager/@mode='edit_form'">
                    <xsl:call-template name="adminhead"/>
                </xsl:if>
            </head>
            <body >
                <xsl:attribute name="id">ClientManager</xsl:attribute>
                <xsl:attribute name="style">behavior: url(/script/webservice.htc) url(/script/clientmanager.htc) </xsl:attribute>

                <xsl:call-template name="NegesoBody">
                    <xsl:with-param name="backLink">
                        <xsl:text>?command=manage-forms&amp;form_language=</xsl:text>
                        <xsl:value-of select="//negeso:form_manager/@lang"/>
                    </xsl:with-param>
                    <xsl:with-param name="helpLink">
                        <xsl:text>/admin/help/cfr1_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>
                    </xsl:with-param>
                </xsl:call-template>
                <xsl:if test="negeso:form_manager[@mode='edit_form']">
                    <xsl:call-template name="buttons"/>
                </xsl:if>
                <script>
                    Cufon.now();
                    Cufon.replace('.admNavPanelFont', { fontFamily: 'Orator Std' });
                    Cufon.replace('.admTDtitles', { fontFamily: 'Orator Std' });
                </script>
            </body>
        </html>
    </xsl:template>


    <xsl:template match="negeso:form_manager" mode="admContent">
        <!-- NEGESO HEADER -->
        <ul >
            <xsl:for-each select="//negeso:error">
                <li>
                    <xsl:value-of select="."/>
                </li>
            </xsl:for-each>
        </ul>
        <xsl:choose>
            <xsl:when test="@mode='show_form'">
                <form id="main_form" method="POST" enctype="multipart/form-data" action="">
                    <input id="command" name="command" value="" type="hidden"/>
                    <input id="form_id" name="form_id" value="" type="hidden"/>
                    <input id="todo" name="todo" value="" type="hidden"/>
                    <table align="center"  border="0" cellpadding="0" cellspacing="0" class="admTable">
                        <tr>
                            <td align="center" class="admNavPanelFont" >                               
                                <xsl:value-of select="java:getString($dict_form_manager, 'FORM_MANAGER')"/>                                
                            </td>
                        </tr>
                        <tr>
                            <td class="admNavPanel">
                                <table>
                                    <tr>
                                        <td style="padding:0 0 0 20px;" >
                                           <xsl:value-of select="java:getString($dict_common, 'LANGUAGE')"/>
                                            &#160; <select name="form_language" style="background-color: white;" onchange="ChangeLanguage();">
                                                <xsl:apply-templates select="negeso:languages/negeso:language" />
                                            </select>
                                        </td>
                                        <td>
                                            <div class="admNavPanelInp" style="width:300px;" >
                                                <div class="imgL"></div>
                                                <div>
                                                    <a class="admNavPanelInp" focus="blur()" onClick="AddNewForm()" href="#AddNewForm(); ">
                                                        <xsl:value-of select="java:getString($dict_form_manager, 'ADD_NEW_FORM')"/>
                                                    </a>
                                                </div>
                                                <div class="imgR"></div>
                                            </div>                                            
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                        <tr>
                            <td >
                                <table width="100%">
                                    <tr>
                                        <td class="admTDtitles" colspan="2">
                                            <xsl:value-of select="java:getString($dict_common, 'NAME')"/>
                                            <xsl:apply-templates select="negeso:forms/negeso:form"/>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                        <tr>
                            <td class="admTableFooter" colspan="2">&#160;</td>
                        </tr>
                    </table>
                </form>
            </xsl:when>
        
            <xsl:when test="@mode='edit_form'">               
               <form action="" method="post" enctype="multipart/form-data" name="main_form" id="main_form">
                   <input id="close" name="close" value="" type="hidden"/> 
                   <input type="hidden" name="form_language">
                        <xsl:attribute name="value">
                            <xsl:value-of select="@lang"/>
                        </xsl:attribute>
                    </input>
                    <input type="hidden" name="article_id">
                        <xsl:attribute name="value">
                            <xsl:value-of select="negeso:form/@article_id"/>
                        </xsl:attribute>
                    </input>
                    <input type="hidden" name="form_text" id="form_text" />
                    <input type="hidden" name="form_id" id="form_id">
                        <xsl:attribute name="value">
                            <xsl:value-of select="negeso:form/@id"/>
                        </xsl:attribute>
                    </input>
                    <input id="command" name="command" value="" type="hidden"/>
                    <input id="todo" name="todo" value="" type="hidden"/>                   
                    <table align="center"  border="0" cellpadding="0" cellspacing="0" class="admTable">
                        <tr>
                            <td align="center" class="admNavPanelFont" colspan="2" >
                                <!-- TITLE -->
                                <xsl:call-template name="tableTitle">
                                    <xsl:with-param name="headtext">
                                        <xsl:value-of select="java:getString($dict_form_manager, 'EDIT_FORM')"/>
                                    </xsl:with-param>
                                </xsl:call-template>
                            </td>
                        </tr>
                        <tr>
                            <td class="admTableTDLast" id="admTableTDtext">
                                <xsl:value-of select="java:getString($dict_common, 'NAME')"/>
                            </td>
                            <td class="admTableTDLast" id="admTableTDtext">
                                 <input name="name" id="name" class="admTextArea" type="text"  required="true">
                                    <xsl:attribute name="value">
                                        <xsl:value-of select="negeso:form/@name"/>
                                    </xsl:attribute>
                                </input>
                            </td>
                        </tr>
                        <tr>
                            <td class="admTableTDLast" id="admTableTDtext" >
                                <xsl:value-of select="java:getString($dict_common, 'EMAIL')"/>
                            </td>
                            <td class="admTableTDLast" id="admTableTDtext">
                                <input name="email" id="email" class="admTextArea" type="text"  is_email="true">
                                    <xsl:attribute name="value">
                                        <xsl:value-of select="negeso:form/@email"/>
                                    </xsl:attribute>
                                </input>
                                <input name="ex" type="hidden" class="admTextArea">
                                    <xsl:attribute name="value">
                                        <xsl:value-of select="negeso:form/@ex"/>
                                    </xsl:attribute>
                                </input>
                            </td>
                        </tr>
                        <tr>
                            <td class="admTableTDLast" id="admTableTDtext">
                                <xsl:value-of select="java:getString($dict_form_manager, 'MAIL_SUCCESS_PAGE')"/>
                            </td>
                            <td class="admTableTDLast" id="admTableTDtext" style="text-align: left;">
                                <select class="admTextArea" name="page_id">
                                    <option></option>
                                    <xsl:for-each select="negeso:pages/negeso:page">
                                        <option value="{@id}">
                                            <xsl:if test="//negeso:form/@page_id = @id">
                                                <xsl:attribute name="selected">true</xsl:attribute>
                                            </xsl:if>
                                            <xsl:value-of select="@title"/>&#160;(<xsl:value-of select="@filename"/>)
                                        </option>
                                    </xsl:for-each>
                                </select>
                            </td>
                        </tr>
                        <tr>
                            <td class="admTableTDLast" id="admTableTDtext">
                                <xsl:value-of select="java:getString($dict_common, 'DESCRIPTION')"/>:
                            </td>
                            <td class="admTableTDLast" id="admTableTDtext">
                                <TEXTAREA name="description" class="admTextArea" style="height: 50;">
                                    <xsl:value-of select="negeso:form/@description"/>
                                </TEXTAREA>
                            </td>
                        </tr>
                        <tr>
                            <td class="admTableTDLast" id="admTableTDtext">
                                <xsl:value-of select="java:getString($dict_common, 'PREVIEW')"/>
                            </td>
                            <td class="admTableTDLast" id="admTableTDtext">
                                <img alt="Editor" title="Editor" src="/images/mark_1.gif" style="cursor: pointer" onclick="RTE_Init('article_text{negeso:form/@article_id}', 'article_text{negeso:form/@article_id}', {negeso:form/@article_id}, 3, 1, 'contentStyle', getInterfaceLanguage());"/>
                                <div id="article_text{negeso:form/@article_id}" onclick="event.cancelBubble='true'; return false;" class="contentStyle">
                                    <xsl:attribute name="style">height: auto; margin-top: 6px; margin-left: 5px; margin-right: 5px; margin-bottom: 5px; border: 1px solid #CCCCCC; padding: 10px;</xsl:attribute>
                                    <xsl:value-of select="negeso:form" disable-output-escaping="yes"/>
                                </div>
                                <script>
                                    makeReadonly(document.getElementById('article_text<xsl:value-of select="negeso:form/@article_id" />'), true);
                                </script>
                            </td>
                        </tr>
                        <tr>
                            <td class="admTableTDLast" id="admTableTDtext">
                                <xsl:value-of select="java:getString($dict_form_manager, 'CREATION_DATE')"/>
                            </td>
                            <td class="admTableTDLast" id="admTableTDtext" style="text-align: left;">
                                <xsl:value-of select="negeso:form/@creation_date"/>
                            </td>
                        </tr>
                        <tr>
                            <td class="admTableTDLast" id="admTableTDtext" >
                                <xsl:value-of select="java:getString($dict_form_manager, 'LAST_MODIFICATION_DATE')"/>
                            </td>
                            <td class="admTableTDLast" id="admTableTDtext" style="text-align: left;">
                                <xsl:value-of select="negeso:form/@last_modification_date"/>
                            </td>
                        </tr>                        
                        <tr>
                            <td class="admTableFooter" >&#160;</td>
                        </tr>
                    </table>
                </form>
            </xsl:when>
            <xsl:otherwise>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!--******************** Bottom buttons ********************-->
    <xsl:template name="buttons">
        <table  cellpadding="0" cellspacing="0"  width="764px"  align="center"  border="0" class="Buttons">
            <tr >
                <td>
                    <div class="admBtnGreenb">
                        <div class="imgL"></div>
                        <div>
                            <a class="admBtnText" focus="blur()" onClick="SaveForm(); return false;" href="#SaveForm();">
                                <xsl:value-of select="java:getString($dict_common, 'SAVE')"/>
                            </a>
                        </div>
                        <div class="imgR"></div>
                    </div>
                    <div class="admBtnGreenb">
                        <div class="imgL"></div>
                        <div>
                            <a class="admBtnText" focus="blur()" onClick="SaveForm(close); return false;" href="#SaveForm();">
                                <xsl:value-of select="java:getString($dict_common, 'SAVE_AND_CLOSE')"/>
                            </a>
                        </div>
                        <div class="imgR"></div>
                    </div>                    
                </td>
            </tr>
        </table>
    </xsl:template>

    <xsl:template match="negeso:form">
        <tr>
            <td class="admTableTD" id="admTableTDtext">
                <b>
                    <xsl:value-of select="@name"/>
                </b>
            </td>
            <td class="admTableTDLast" style="width:115px;cursor:pointer;" >
                <img src="/images/edit.png" class="admHand" onClick="EditForm({@id})">
                    <xsl:attribute name="title">
                        <xsl:value-of select="java:getString($dict_common, 'EDIT')"/>
                    </xsl:attribute>
                </img>

                <img src="/images/submenu.png" class="admHand" onClick="openFormArchive('{@id}')">
                    <xsl:attribute name="title">
                        <xsl:value-of select="java:getString($dict_form_manager, 'FM.FORM_ARCHIVE')"/>
                    </xsl:attribute>
                </img>

                <img src="/images/delete.png" class="admHand" onClick="DeleteForm({@id})">
                    <xsl:attribute name="title">
                        <xsl:value-of select="java:getString($dict_common, 'DELETE')"/>
                    </xsl:attribute>
                </img>
            </td>
        </tr>

    </xsl:template>

    <xsl:template match="negeso:language">
        <option>
            <xsl:attribute name="value">
                <xsl:value-of select="@code" />
            </xsl:attribute>
            <xsl:if test="@current='true'">
                <xsl:attribute name="selected">
                    <xsl:value-of select="true"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:value-of select="@code"/>
        </option>

    </xsl:template>
    
</xsl:stylesheet>
