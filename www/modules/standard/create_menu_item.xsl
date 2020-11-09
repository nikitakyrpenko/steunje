<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)$Id$		
 
  Copyright (c) 2004 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.
 
  Page properties dialog XSL.
 
  @author		Olexiy.Strashko
  @version		$Revision$
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:negeso="http://negeso.com/2003/Framework"
	   xmlns:java="http://xml.apache.org/xslt/java"
	   exclude-result-prefixes="java">


    <xsl:variable name="lang" select="/*/@interface-language"/>    
    <xsl:variable name="dict_dialogs" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_dialogs.js',$lang)"/>

    <!-- Include/Import -->
    <xsl:include href="/xsl/negeso_header.xsl"/>
    <!-- NEGESO JAVASCRIPT Temaplate -->
    <xsl:template name="java-script">

        <!-- script block for localization mechanism -->
        <script id="localization">/**/</script>
        <script language="JavaScript">
            var currLanguage = '<xsl:value-of select="/*/@lang" />'
            var cntLanguage = <xsl:value-of select="//negeso:page/@total-site-languages" />;

            <xsl:text disable-output-escaping="yes">
	            <![CDATA[
                var s_MenuItemTextCannotBeEmpty = "";
                var s_MenuItemTextCannotConsist = "";
                var s_PleaseSelectPage = "";
                var s_FileNameCannotBeEmpty = "";
                var s_FileNameIsTooLong = "";
                var s_MaximumCharacters = "";
                var s_InvalidFileName = "";
                var s_SpacesAreNotAllowed = "";
            
            
                var selectedLinkType = "page";
                var urlType = "url";
    
                document.onkeypress = function(){
                    if(window.event.keyCode == 27) returnCancel();
                }
        
                window.onload = function(){
                    document.getElementById('buttonYes').blur();
                    document.getElementById('buttonCancel').blur();
                }
        
                function returnYes(){
                    /* check if page title is empty */
                    var pageTitle = document.getElementById('title').value;
                        if(pageTitle.length < 1){
                        alert(s_MenuItemTextCannotBeEmpty);
                        document.getElementById('title').focus();
                        return;
                    }

                    var onlySpaces = true;
                    for(i = 0; i < pageTitle.length; i++){
                        if(pageTitle.charAt(i) != ' '){
                            onlySpaces = false;
                            break;
                        }
                    }

                    if(onlySpaces){
                        alert(s_MenuItemTextCannotConsist);
                        document.getElementById('title').focus();
                        return;
                    }                   
                    /* Check other parameters (and, probably, add menu item) */
                    if(selectedLinkType == "url"){
                	    if (/:\/\/($|\s)/i.test(document.getElementById('link_url').value)) {
                    	    alert(s_FileNameCannotBeEmpty);
                    	    document.getElementById('link_url').focus();
                    	    return false;
                        }                     
                    }else if(selectedLinkType == "page"){
                        /* check file name*/
                        if(!checkFilename(document.getElementById('link_filename').value)){
                            document.getElementById('link_filename').focus();
                            return;
                        }
                        document.getElementById('filename').value = document.getElementById('link_filename').value + '_' + currLanguage + '.html';
                    }else if (selectedLinkType == "alias"){                
                        if(document.getElementById('link').value == ""){
                            alert(s_PleaseSelectPage);
                            link_alias_btn.focus();
                            return;
                        }   
                    }

                    selContainerObj = document.getElementById("sel_container");
                    var container = selContainerObj.options[selContainerObj.selectedIndex];                    
                    var dataFrm = $('form[name=menuItem]').serializeArray();
			        window.opener.tryToCreateMenuAnswer(dataFrm);
                    window.close();
            }

        
        function returnCancel(){
            window.returnValue='cancel';
            window.close();
        }
        
        /**
        * Disables or enables all controls in array 'controls'
        * 
        * @param controls array of controls which belong to one logical group
        * @param disable boolean; true to disable controls, false to enable them
        */
        function setDisabled(controls, disable){
            for(i = 0; i < controls.length; i++){
                document.getElementById(controls[i]).disabled = disable;
            }
        }
        
        function typePageSelected(){
            selectedLinkType = "page";
            setDisabled(newPageControls, false);
            setDisabled(pageAliasControls, true);
            setDisabled(urlControls, true);
        }
        
        function typeAliasSelected(){
            selectedLinkType = "alias";
            setDisabled(newPageControls, true);
            setDisabled(pageAliasControls, false);
            setDisabled(urlControls, true);
        }
        
        function typeUrlSelected(){
            selectedLinkType = "url";
            urlType = "url";
            setDisabled(newPageControls, true);
            setDisabled(pageAliasControls, true);
            setDisabled(urlControls, false);
        }
        
        function typeTlnSelected(){
            selectedLinkType = "url";
            urlType = "tln_link";
            setDisabled(newPageControls, true);
            setDisabled(pageAliasControls, true);
            setDisabled(urlControls, true);
        }
        
        function selectTemplate(){        
            window.open("?command=select-page-template-command", null, "height=350px,width=650px,help=No,scroll=yes,scroll=on,scrollbars=yes,status=No,resizable=yes");
        }
        
        function setTemplate(choice){
			 if(choice != null && choice.resCode == "OK"){
	                document.getElementById("template").value = choice.templateId;
	                document.getElementById("templateTitle").value = choice.templateTitle;
	            }
		}

        function checkFilename(name){
            if(name.length < 1){
                alert(s_FileNameCannotBeEmpty);
                return false;
            }
            if(name.length > 50){
                alert(s_FileNameIsTooLong + "." + "\n" + s_MaximumCharacters);
                return false;
            }
            for(var i=0; i < name.length; i++){
                if( "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_:"
                        .indexOf( name.charAt(i) ) == -1 ){
                    alert(s_InvalidFileName + "_ - :'\n" + s_SpacesAreNotAllowed);
                    return false;
                }
            }
            return true;
        }
        
        /** Returns XSD canonical date representation (CCYY-MM-DDThh:mm:ssZ) */
        function getCanonicalDate(d, m, y){
            if(d == "" || m == "" || y == ""){
                return "";
            }
            return y + "-" + m + "-" + d + "T00:00:00Z";
        }
        
        /** Returns '1' as '01', ..., '9' as '09'. Values beyond these bounds
        * are returned unchanged.
        */
        function prependZero(num){
            return num > 9 ? num : "0" + num;
        }
        
        var today = new Date();
        var yesterday = new Date(today.getFullYear(), today.getMonth(), today.getDate() - 2);
        //attachEvent ("onload", resizeDialogWindow); //resize dialog window height

        function showPage(){            
            document.getElementById("expiredDateId").value = "";
            document.getElementById("publishDateId").value = 
                prependZero(yesterday.getDate()) + "-" + 
                prependZero(yesterday.getMonth() + 1) + "-" + 
                prependZero(yesterday.getFullYear());
        }

        function hidePage(){            
            document.getElementById("expiredDateId").value =
                prependZero(yesterday.getDate()) + "-" +
                prependZero(yesterday.getMonth() + 1) + "-" +
                prependZero(yesterday.getFullYear());
            document.getElementById("publishDateId").value = "";
        }

        function openPageExplorer() {
            var answer =
                window.showModalDialog(
                    "/dialogs/?command=get-pages-list-command",
                     this,
                    "dialogHeight: 590px; dialogWidth: 620px; help: No; scroll: Yes; status: No;"
                );
            try{
                if(typeof(answer) != "undefined" && answer != null){
                    document.getElementById('link_alias').value = answer[0];
                    document.getElementById('link').value = answer[1];
                    document.getElementById('pageId').value = answer[3];
                }
            }catch(e){}
        }
        
       
        var today = new Date();
        var twoDaysAgo = new Date(today.getFullYear(), today.getMonth(),today.getDate());
        //attachEvent ("onload", resizeDialogWindow); //resize dialog window height
        
		      ]]>
		      </xsl:text>
    </script>
    </xsl:template>


    <!-- MAIN ENTRY -->
    <xsl:template match="/">
        <html>
            <head>
                <META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=utf-8"/>
                <title>Negeso W/CMS</title>
                <link rel="stylesheet" type="text/css" href="/css/admin_style.css"/>
                <link rel="stylesheet" type="text/css" href="/css/customized-jquery-ui.css"/>
                <link rel="stylesheet" type="text/css" href="/css/jquery-ui.css"/>
                <link rel="stylesheet" type="text/css" href="/css/jquery-ui.custom.css"/>
                <script language="JavaScript" src="/script/jquery.min.js"/>
                <script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
                <script type="text/javascript" src="/script/cufon-yui.js"/>
                <script type="text/javascript" src="/script/Orator_Std_400.font.js"/>
                <script type="text/javascript" src="/script/common_functions.js"></script>
                <script language="JavaScript" src="/script/calendar_picker.js"/>
                <script type="text/javascript" src="/script/menu_iem.js"></script>
                <script language="JavaScript" src="/script/security.js"/>                
                
                <xsl:call-template name="java-script"/>
            </head>
            <body class="dialogSmall">                
                <table class="admMain"  align="center" border="0" cellpadding="0" cellspacing="0">
                    <tr>
                        <td class="admConnerLeft"></td>
                        <td class="admTopBtn"></td>
                        <td class="admConnerRight"></td>
                    </tr>
                    <tr>
                        <td class="admMainLeft">
                            <img src="/images/left_bot.png" />
                        </td>
                        <td>
                            <form name="menuItem" method="get">
                                <input type="hidden" name="parentId" id="parentId" value="{/negeso:page/@parentId}"/>
                                <input type="hidden" name="langId"  value="{/negeso:page/@lang-id}"/>                                
                            <xsl:apply-templates select="negeso:page"/>
                            </form>
                        </td>
                        <td class="admMaiRight">
                            <img src="/images/right_bot.png" />
                        </td>
                    </tr>
                </table>
                <script>
                var newPageControls = new Array(
                    'link_filename', 'sel_container',
                    'templateTitle', 'templateSelection'
                    );
                
                var pageAliasControls = new Array('link','link_alias', 'link_alias_btn', 'keep_current_menu_structure_for_alias');
                
                var urlControls = new Array('link_url', 'keep_current_menu_structure_for_url');
                  
                
                s_MenuItemTextCannotBeEmpty = '<xsl:value-of select="java:getString($dict_dialogs, 'MENU_ITEM_TEXT_CANNOT_BE_EMPTY')"/>';
                    s_MenuItemTextCannotConsist = '<xsl:value-of select="java:getString($dict_dialogs, 'MENU_ITEM_TEXT_CANNOT_CONSIST')"/>';
                    s_PleaseSelectPage      = '<xsl:value-of select="java:getString($dict_dialogs, 'PLEASE_SELECT_PAGE')"/>';
                    s_FileNameCannotBeEmpty = '<xsl:value-of select="java:getString($dict_dialogs, 'FILE_NAME_CANNOT_BE_EMPTY')"/>';
                    s_FileNameIsTooLong     = '<xsl:value-of select="java:getString($dict_dialogs, 'FILE_NAME_IS_TOO_LONG')"/>';
                    s_MaximumCharacters     = '<xsl:value-of select="java:getString($dict_dialogs, 'MAXIMUM_CHARACTERS')"/>';
                    s_InvalidFileName       = '<xsl:value-of select="java:getString($dict_dialogs, 'INVALID_FILE_NAME')"/>';
                    s_SpacesAreNotAllowed   = '<xsl:value-of select="java:getString($dict_dialogs, 'SPACES_ARE_NOT_ALLOWED')"/>';



                    window.document.all.link_filename.title  = '<xsl:value-of select="java:getString($dict_dialogs, 'INVENT_FILENAME')"/>';
                    //TODO translation MULTI_LANGUAGE
                    //window.document.all.MULTI_LANGUAGE.innerHTML = 'Multilanguage';
                    /*
                    window.document.all.link_url.title =
                    Strings.FOR_EXAMPLE_WEB_PAGE + " 'http://google.com/' " +
                    Strings.EMAIL_ADDRESS + " 'mailto:sales@acme.com'";
                    */
                    window.document.getElementById("buttonShow").title = '<xsl:value-of select="java:getString($dict_dialogs, 'SET_PUBLICATION_DATE_TO')"/>';
                    window.document.getElementById("buttonHide").title = '<xsl:value-of select="java:getString($dict_dialogs, 'SET_EXPIRATION_DATE_TO')"/>';
                    Cufon.now();
                    Cufon.replace('.admNavPanelFont', { fontFamily: 'Orator Std' });
                    Cufon.replace('.admTDtitles', { fontFamily: 'Orator Std' });

                </script>

            </body>
        </html>
    </xsl:template>

    <xsl:template match="negeso:page">                
        <table align="center"  border="0" cellpadding="0" cellspacing="0" class="admTable">
            <!-- BEGIN CONTENT -->
            <tr>
                <td class="admNavPanelFont" align="center">
                    <xsl:choose>
                        <xsl:when test="@isSubMenu = 'true'">
                            <xsl:call-template name="CREATE_SUBMENU_ITEM"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:call-template name="CREATE_MENU_ITEM"/>                            
                        </xsl:otherwise>
                    </xsl:choose>                    
                </td>
            </tr>
            <tr>
                <td class="admTableTDLast">
                    <table align="center" border="0" cellpadding="2" cellspacing="2" width="100%">
                        <tr>
                            <td class="admRight" width="180" id="pageTitle">
                                <xsl:value-of select="java:getString($dict_dialogs, 'TITLE')"/>
                            </td>
                            <td colspan="2">
                                <input type="text" name="title" id="title" value="" class="admTextArea"/>
                            </td>
                        </tr>
                        <tr>
                            <td class="admRight">
                                <xsl:value-of select="java:getString($dict_dialogs, 'PUBLISH_ON')"/>
                            </td>
                            <td>
                                <input style="width: 100px;" type="text" id="publishDateId" name="publishDate" readonly="true"/>
                                <span>&#160;(dd-mm-yyyy)</span>
                            </td>                            
                            <td  align="left">
                                <div class="admNavPanelInp" style="padding:0;">
                                    <div class="imgL"></div>
                                    <div align="center">
                                        <a class="admNavPanelInp"  focus="blur()" style="width:83px;  padding-right:0; padding-left:0;" href="#" onclick="showPage()" onfocus="blur()" id="buttonShow">
                                            <xsl:value-of select="java:getString($dict_dialogs, 'SHOW_PAGE')"/>                                            
                                        </a>
                                    </div>
                                    <div class="imgR"></div>
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td class="admRight">
                                <xsl:value-of select="java:getString($dict_dialogs, 'EXPIRES_ON')"/>
                            </td>
                            <td>
                                <input style="width: 100px;" type="text" name="expiredDate" id="expiredDateId" readonly="true"/>
                                <script>
                                    <xsl:text disable-output-escaping="yes">
	                            <![CDATA[
	                                document.all.expiredDateId.value =
	                                    prependZero(twoDaysAgo.getDate()) + "-" +
	                                    prependZero(twoDaysAgo.getMonth() + 1) + "-" +
	                                    twoDaysAgo.getFullYear()
	                            ]]>
	                            </xsl:text>
                                </script>
                                <span>&#160;(dd-mm-yyyy)</span>
                            </td>                            
                            <td  align="left">
                                <div class="admNavPanelInp" style="padding:0;">
                                    <div class="imgL"></div>
                                    <div align="center">
                                        <a class="admNavPanelInp" focus="blur()" style="width:83px; padding-right:0; padding-left:0;" href="#" onclick="hidePage()" onfocus="blur()" id="buttonHide">
                                            <xsl:value-of select="java:getString($dict_dialogs, 'HIDE_PAGE')"/>                                            
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
                <td class="admNavPanelFont" align="center">
                    <xsl:value-of select="java:getString($dict_dialogs, 'MENU_ITEM_TYPE')"/>                    
                </td>
            </tr>
            <tr>
                <td class="admTableTDLast">
                    <table align="center"  border="0" cellpadding="2" cellspacing="2" width="100%">
                        <tr>
                            <td width="145" height="30" style="padding:5px 0 10px 35px;font-weight:bold">                                
                                <input type="radio" name="link_type" id="link_type" value="page" onClick="typePageSelected()" class="radio" checked="true" onfocus="blur()"/>
                                <xsl:value-of select="java:getString($dict_dialogs, 'NEW_PAGE')"/>
                            </td>
                            <td>
                                <input type="text" id="link_filename" name="filename_link" value="" class="admTextArea" />_<xsl:value-of select="/*/@lang"/>.html
                                <input type="hidden" id="filename" name="filename" value="" class="admTextArea" />
                            </td>
                        </tr>
                        <tr>
                            <td height="30" class="admRight">                                
                                <xsl:value-of select="java:getString($dict_dialogs, 'CONTAINER')"/>
                            </td>
                            <td>
                                <select id="sel_container" class="admTextArea" name="containerId">
                                    <xsl:for-each select="negeso:containers/negeso:container">
                                        <option value="{@id}">
                                            <xsl:value-of select="@name" disable-output-escaping="yes"/>
                                        </option>
                                    </xsl:for-each>                                    
                                </select>
                            </td>
                        </tr>                        
                        <tr>
                            <td height="30" class="admRight">
                                <xsl:value-of select="java:getString($dict_dialogs, 'PAGE_TEMPLATE')"/>
                            </td>
                            <td>
                                <input type="hidden" id="template" name="template" value="{/negeso:page/@default-template-name}"/>
                                <input type="text" id="templateTitle" readonly="true"  value="{/negeso:page/@default-template-title}" class="admTextArea"/>
                                <input type="button" id="templateSelection" value="..." style="width: 24px; height: 21px;" onclick="selectTemplate()"/>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>

            <tr>
                <td class="admTableTDLast">
                    <table align="center"  border="0" cellpadding="2" cellspacing="2" width="100%">
                        <tr>
                            <td width="145" style="padding:5px 0 10px 35px;font-weight:bold">                                
                                <input type="radio" name="link_type" id="link_type" value="alias" onclick="typeAliasSelected()" class="radio" onfocus="blur()"/>
                                <xsl:value-of select="java:getString($dict_dialogs, 'EXISTING_PAGE')"/>
                            </td>
                            <td>
                                <input type="hidden" id="link" disabled="true" name="link" readonly="true" value=""/>
                                <input type="hidden" id="pageId" name="pageId" readonly="true" value=""/>                                
                                <input type="text" id="link_alias" text="" disabled="true" readonly="true" class="admTextArea"/>
                                <input type="button" id="link_alias_btn" value="..." disabled="true" class="admExplor" onclick="openPageExplorer()"/>
                            </td>
                        </tr>
                        <tr>
                            <td class="admRight"></td>
                            <td>
                                <input type="checkbox" name="keepMenu" id="keep_current_menu_structure_for_alias" class="radio" disabled="true" onfocus="blur()" checked="checked">Keep current menu-structure</input>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
            <tr>
                <td class="admTableTDLast">
                    <table align="center"  border="0" cellpadding="0" cellspacing="0" width="100%">
                        <tr>
                            <td width="145" style="padding:5px 0 10px 35px;font-weight:bold">                                
                                <input type="radio" name="link_type" id="link_type" value="url" onclick="typeUrlSelected()" class="radio" onfocus="blur()"/>
                                <xsl:value-of select="java:getString($dict_dialogs, 'LINK_TO_URL')"/>
                            </td>
                            <td>                                
                                <input type="text" id="link_url" value="http://" name="link" disabled="true" class="admTextArea"/>
                            </td>
                        </tr>
                        <tr>
                            <td></td>
                            <td>
                                <input type="checkbox" name="keepMenu" id="keep_current_menu_structure_for_url" class="radio" disabled="true" onfocus="blur()">Keep current menu-structure</input>
                            </td>
                        </tr>
                    </table>

                </td>
            </tr>


            <!-- END CONTENT -->
            <tr>
                <td class="admTableTDLast">
                    <div class="admNavPanelInp" style="padding-left:210px;">
                        <div class="imgL"></div>
                        <div><input type="button" onclick="returnYes()" id="buttonYes" value="{java:getString($dict_dialogs, 'OK')}" onfocus="blur()"/></div>
                        <div class="imgR"></div>
                    </div>
                    <div class="admNavPanelInp">
                        <div class="imgL"></div>
                        <div><input type="button" onclick="returnCancel()" id="buttonCancel" value="{java:getString($dict_dialogs, 'CANCEL')}" onfocus="blur()"/></div>
                        <div class="imgR"></div>
                    </div>
                </td>
            </tr>
            <tr>
                <td class="admTableFooter">&#160;</td>
            </tr>
        </table>

    </xsl:template>
    <xsl:template name="CREATE_MENU_ITEM">
        <xsl:choose>
            <xsl:when test="$lang='en'">Create menu item</xsl:when>
            <xsl:when test="$lang='nl'">Creeer een menu onderdeel</xsl:when>
            <xsl:when test="$lang='de'">Ein Menuunterteil erschaffen</xsl:when>
            <xsl:when test="$lang='fr'">Créer un élément de menu</xsl:when>
            <xsl:when test="$lang='es'">Crear elemento de menú</xsl:when>
            <xsl:when test="$lang='it'">Creare l'elemento del menu</xsl:when>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="CREATE_SUBMENU_ITEM">
        <xsl:choose>
            <xsl:when test="$lang='en'">Create submenu item</xsl:when>
            <xsl:when test="$lang='nl'">Creeer een submenu onderdeel</xsl:when>
            <xsl:when test="$lang='de'">Ein Submenuunterteil erschaffen</xsl:when>
            <xsl:when test="$lang='fr'">Créer un élément de sous-menu</xsl:when>
            <xsl:when test="$lang='es'">Crear elemento de submenú</xsl:when>
            <xsl:when test="$lang='it'">Creare l'elemento del sotto menu</xsl:when>
        </xsl:choose>
    </xsl:template>
</xsl:stylesheet>