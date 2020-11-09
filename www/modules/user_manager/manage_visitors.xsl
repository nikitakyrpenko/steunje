<?xml version="1.0" encoding="utf-8"?>
<!--
  @(#)$Id: manage_visitors.xsl,v 1.32, 2005-06-03 14:37:53Z, Sergey Oleynik$       
 
  Copyright (c) 2004 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.
 
  TODO: file description here  

  @author       Stanislav Demchenko
  @version      $Revision: 33$
-->


<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">
		
<xsl:variable name="lang" select="/*/@interface-language"/>
<xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>
<xsl:variable name="dict_security_module" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_security_module.xsl', $lang)"/>
<xsl:variable name="dict_modules" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_modules.xsl', $lang)"/>
<xsl:variable name="dict_dialogs" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_dialogs.js',$lang)"/>

<xsl:variable name="action">
    <xsl:choose>
        <xsl:when test="/negeso:page/negeso:contents/negeso:users">list</xsl:when>
        <xsl:when test="/negeso:page/negeso:context/negeso:action">
            <xsl:value-of select="/negeso:page/negeso:context/negeso:action"/>
        </xsl:when>
        <xsl:otherwise>list</xsl:otherwise>
    </xsl:choose>
</xsl:variable>
<xsl:variable name="title">
    
    <xsl:choose>
        <xsl:when test="$action='add'"><xsl:value-of select="java:getString($dict_security_module, 'ADD_NEW_ACCOUNT')"/></xsl:when>
        <xsl:when test="$action='edit'"><xsl:value-of select="java:getString($dict_security_module, 'EDIT_ACCOUNT')"/></xsl:when>
        <xsl:when test="$action='edit_password'"><xsl:value-of select="java:getString($dict_security_module, 'SET_NEW_PASSWORD')"/></xsl:when>
        <xsl:when test="$action='save'"><xsl:value-of select="java:getString($dict_security_module, 'SAVING_CHANGES')"/></xsl:when>
        <xsl:when test="$action='delete'"><xsl:value-of select="java:getString($dict_modules, 'AUTHORIZED_VISITORS')"/></xsl:when>
        <xsl:otherwise><xsl:value-of select="java:getString($dict_modules, 'AUTHORIZED_VISITORS')"/></xsl:otherwise>
    </xsl:choose>
</xsl:variable>
<xsl:include href="/xsl/negeso_body.xsl"/>
<!-- **************** BEGIN MAIN TEMPLATE (COMMON FOR ALL MODES) *********** -->
<xsl:template match="/negeso:page/negeso:contents">
    <html>
    <head>
            <title><xsl:value-of select="$title"/></title>
        <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
        <link rel="stylesheet" type="text/css" href="/css/jquery-ui.custom.css"/>

        <script type="text/javascript" src="/script/jquery.min.js"></script>
        <script type="text/javascript" src="/script/jquery-ui.custom.min.js" />
        <script type="text/javascript" src="/script/special_pages.js"></script>
        <script type="text/javascript" src="/script/cufon-yui.js"></script>        
        <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>        
        <script language="JavaScript1.2" src="/script/common_functions.js" type="text/javascript"/>        
        <script language="JavaScript1.2" src="/script/md5.js" type="text/javascript"/>      
	    <script language="JavaScript1.2" src="/script/calendar_picker.js" type="text/javascript"></script>
        <script type="text/javascript" src="/script/conf.js"/>
        
        <script language="JavaScript">
            var s_DeleteAccountConfirmation = "<xsl:value-of select="java:getString($dict_security_module, 'DELETE_ACCOUNT_CONFIRMATION')"/>";
            var s_IllegalCharactersInLogin = "<xsl:value-of select="java:getString($dict_security_module, 'ILLEGAL_CHARACTERS_IN_LOGIN')"/>";
            var s_UseEnglishCharacters = "<xsl:value-of select="java:getString($dict_security_module, 'USE_ENGLISH_CHARACTERS')"/>";
            var s_FillInFullVisitorName = "<xsl:value-of select="java:getString($dict_security_module, 'FILL_IN_FULL_VISITOR_NAME')"/>";
            var s_FillInLogin = "<xsl:value-of select="java:getString($dict_security_module, 'FILL_IN_LOGIN')"/>";
            var s_FillInPassword = "<xsl:value-of select="java:getString($dict_security_module, 'FILL_IN_PASSWORD')"/>";
            var s_PasswordsDiffer = "<xsl:value-of select="java:getString($dict_security_module, 'PASSWORDS_DIFFER')"/>";
            var s_IllegalCharactersInPassword = "<xsl:value-of select="java:getString($dict_security_module, 'ILLEGAL_CHARACTERS_IN_PASSWORD')"/>";
			         var s_CheckGroup = "<xsl:value-of select="java:getString($dict_security_module, 'CHECK_AT_LEAST_ONE_GROUP')"/>";
			         var s_NoGroups = "<xsl:value-of select="java:getString($dict_security_module, 'THERE_ARE_NO_GROUP')"/>";


            function addAccount() {
            window.location.href = "?action=add";
            }
            function editAccount(id) {
            window.location.href = "?action=edit&amp;id="+id;
            }
            function deleteAccount(id) {
                if(!confirm(s_DeleteAccountConfirmation)) {
                    return;
                }
                window.location.href = "?action=delete&amp;id="+id;
            }
            function editPassword(id) {
                window.location.href = "?action=edit_password&amp;id="+id;
            }


            function validateAddEditForm(userId,formObj){
            if(add_edit_form.name.value == ''){
            alert(s_FillInFullVisitorName);
            add_edit_form.name.focus();
            return;
            }
            if(add_edit_form.login.value == ''){
            alert(s_FillInLogin);
            add_edit_form.login.focus();
            return;
            }
            var loginOk = StringUtil.isTokenValid(
            add_edit_form.login.value,
            StringUtil.getSafeCharacters());
            if(!loginOk){
            alert(s_IllegalCharactersInLogin + ".\n" +
            s_UseEnglishCharacters);
            add_edit_form.login.focus();
            return;
            }
            if(userId == ''){
            if(add_edit_form.password.value == ''){
            alert(s_FillInPassword);
            add_edit_form.password.focus();
            return;
            }
            if(add_edit_form.password.value != add_edit_form.pwd.value){
            alert(s_PasswordsDiffer);
            add_edit_form.password.focus();
            return;
            }
            var passwordOk = StringUtil.isTokenValid(
            add_edit_form.password.value,
            StringUtil.getSafeCharacters());
            if(!passwordOk){
            alert(s_IllegalCharactersInPassword + ".\n" +
            s_UseEnglishCharacters);
            add_edit_form.password.focus();
            return;
            }
            add_edit_form.password.value=hex_md5(add_edit_form.password.value);
            add_edit_form.pwd.value='';
            }
            if (typeof(formObj)!="undefined" &amp;&amp; formObj!=null) {
                	var is_ok=false;
                	var no_groups=true;
                	for (var i=0; i&lt;formObj.elements.length; i++) {
                		var elm=formObj.elements[i];
                		if (elm.tagName.toLowerCase()=="input" 
                			&amp;&amp; elm.type.toLowerCase()=="checkbox"
                			&amp;&amp; elm.name.toLowerCase()=="groups")
            {
            no_groups=false;
            if (elm.checked==true)
            is_ok=true;
            }
            }
            if (no_groups)
            alert(s_NoGroups);
            else if (!is_ok) {
            alert(s_CheckGroup);
            }
            }
            add_edit_form.submit();
            }

            function validatePasswordForm(){
            if(add_edit_form.password.value == ''){
            alert(s_FillInPassword);
            add_edit_form.password.focus();
            return;
            }
            if(add_edit_form.password.value != add_edit_form.pwd.value){
            alert(s_PasswordsDiffer);
            add_edit_form.password.focus();
            return;
            }
            var passwordOk = StringUtil.isTokenValid(
            add_edit_form.password.value,
            StringUtil.getSafeCharacters());
            if(!passwordOk){
            alert(s_IllegalCharactersInPassword + ".\n" +
            s_UseEnglishCharacters);
            add_edit_form.password.focus();
            return;
            }
            add_edit_form.password.value=hex_md5(add_edit_form.password.value);
            add_edit_form.pwd.value='';
            add_edit_form.submit();
            }

            window.focus();

            function prependZero(num){
            return num > 9 ? "" + num : "0" + num;
            }
            var currentDate = new Date()
            var day = currentDate.getDate()
            var month = currentDate.getMonth()+1
            var year = currentDate.getFullYear()

            function addExpiredDate() {
            document.getElementById("publishDateId").value = "";
            var currDate =   prependZero(day) + "-" +prependZero(month) + "-" +prependZero(year) ;
            add_edit_form.expiredDate.value = currDate;
            };
            
            function clearExpiredDate() {
            	document.getElementById("expiredDateId").value = "";
            };
            
            
            function addpublishDate() {
            document.getElementById("expiredDateId").value = "";
            var currDate =   prependZero(day) + "-" +prependZero(month) + "-" +prependZero(year) ;
            add_edit_form.publishDate.value = currDate;
            };
            
             function clearPublishDate() {
            	document.getElementById("publishDateId").value = "";
            };
            
        </script>

    </head>
    <body>
        
    <!-- NEGESO HEADER -->
     <xsl:call-template name="NegesoBody">
         <xsl:with-param name="helpLink">
             <xsl:text>/admin/help/csc1_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>
         </xsl:with-param>
         <xsl:with-param name="backLink" select='"?action=list"' />
     </xsl:call-template>
        <xsl:call-template name="buttons"> </xsl:call-template>

    </body>
</html>
</xsl:template>
    
    <xsl:template match="negeso:contents"  mode="admContent">
        <table cellpadding="0" cellspacing="0" class="admTable">
            <tr>
                <td>
          
            <table cellpadding="0" cellspacing="0">
                <tr>
                    <td>
                        <ul>
                            <xsl:for-each select="//negeso:context/negeso:error">
                                <li style="text-align: left; color: #ff0000;">
                                    <xsl:value-of select="."/>
                                </li>
                            </xsl:for-each>
                        </ul>
                    </td>
                </tr>
            </table>
                    <table cellpadding="0" cellspacing="0" width="100%">
                        <tr>
                            <td align="center" class="admNavPanelFont">
                                <xsl:call-template name="tableTitle">
                                    <xsl:with-param name="headtext">
                                        <xsl:value-of select="$title"/>
                                    </xsl:with-param>
                                </xsl:call-template>
                            </td>
                        </tr>
                        <tr>
                            <td class="admNavPanel" style="padding: 0 0 0 20px;" >
                                <a class="admAnchor" href="visitor_mngr">
                                    <xsl:value-of select="java:getString($dict_modules, 'AUTHORIZED_VISITORS')"/>
                                </a>&#160;&#160;&#160;
                                <a class="admAnchor" href="visitor_group_mngr">
                                    <xsl:value-of select="java:getString($dict_security_module, 'VISITOR_GROUPS')"/>
                                </a>&#160;&#160;&#160;
                                <xsl:call-template name="consts">
                                    <xsl:with-param name="id" select="@id"/>
                                </xsl:call-template>
                            </td>
                        </tr>
                    </table>

                    <!-- CONTENT -->
            <xsl:choose>
                <xsl:when test="$action='add'">
                    <xsl:apply-templates select="negeso:user" mode="add_edit"/>
                </xsl:when>
                <xsl:when test="$action='edit'">
                    <xsl:apply-templates select="negeso:user" mode="add_edit"/>
                </xsl:when>
                <xsl:when test="$action='edit_password'">
                    <xsl:apply-templates select="negeso:user" mode="edit_password"/>
                </xsl:when>
                <xsl:when test="$action='save'">
                    <!-- ************ DIRTY IGNORING BUGS: FIX THIS *********** -->
                    <xsl:apply-templates select="negeso:users" mode="list"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:apply-templates select="negeso:users" mode="list"/>
                </xsl:otherwise>
            </xsl:choose>

                </td>
            </tr>
         <tr>
                <td class="admTableFooter" >&#160;</td>
            </tr>            
        </table>
       
    </xsl:template>

<!-- *********************** BEGIN MODE "ADD_EDIT" ***************************** -->
<xsl:template match="negeso:user" mode="add_edit">
        <form method="POST" enctype="multipart/form-data" id="add_edit_form">
        <input type="hidden" name="action" value="save"/>
        <xsl:if test="@id">
            <input type="hidden" name="id" value="{@id}"/>
        </xsl:if>
        <table cellpadding="0" cellspacing="0" width="100%">
                <tr>
                    <th class="admTableTD admWidth150"><xsl:value-of select="java:getString($dict_security_module, 'FULL_NAME')"/>*</th>
                    <td class="admTableTDLast"><input class="admTextArea" type="text" value="{@name}" name="name"/></td>
                </tr>
                <tr>
                    <th class="admTableTD admWidth150"><xsl:value-of select="java:getString($dict_security_module, 'LOGIN')"/>*</th>
                    <td class="admTableTDLast"><input class="admTextArea" type="text" value="{@login}" name="login"/></td>
                </tr>
           <xsl:if test="not(@id)">
                <tr>
                    <th class="admTableTD admWidth150"><xsl:value-of select="java:getString($dict_security_module, 'PASSWORD')"/>*</th>
                    <td class="admTableTDLast"><input class="admTextArea" type="password" value="" name="password"/></td>
                 </tr>
                 <tr>
                     <th class="admTableTD admWidth150"><xsl:value-of select="java:getString($dict_security_module, 'RETYPE_PASSWORD')"/>*</th>
                     <td class="admTableTDLast"><input class="admTextArea" type="password" value="" name="pwd"/></td>
                 </tr>
           </xsl:if>
                <tr>
                    <th class="admTableTD admWidth150"><xsl:value-of select="java:getString($dict_common, 'PUBLISH_DATE')"/></th>
                    <td class="admTableTDLast">
                        <table cellspacing="0" cellpadding="0">
                            <tr>
                                <td>
                                    <input class="admTextArea" type="text" value="{@publishDate}" name="publishDate" id="publishDateId" readonly="true" style="width:150px;"/>
                                </td>
                                <td>&#160;(dd-mm-yyyy)</td>
                                <td>
                                    <div class="admNavPanelInp">
                                        <div class="imgL"></div>
                                        <div>
                                            <input onClick="addpublishDate()" type="button"  class="admNavPanelInp" style="width:83px;">
                                                <xsl:attribute name="value">
                                                    <xsl:value-of select="java:getString($dict_dialogs, 'SHOW')"/>
                                                </xsl:attribute>
                                            </input>
                                        </div>
                                        <div class="imgR"></div>
                                    </div>   
                                    
                                    <div class="admNavPanelInp">
                                        <div class="imgL"></div>
                                        <div>
                                            <input onClick="clearPublishDate()" type="button" class="admNavPanelInp" >
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
                <tr>
                    <th class="admTableTD admWidth150"><xsl:value-of select="java:getString($dict_common, 'EXPIRED_DATE')"/></th>
                    <td class="admTableTDLast">

                        <table cellspacing="0" cellpadding="0">
                            <tr>
                                <td>                                   
                                    <input class="admTextArea" type="text" value="{@expiredDate}" name="expiredDate" id="expiredDateId" readonly="true"  style="width:150px;"/>                                    
                                    </td>
                                <td>&#160;(dd-mm-yyyy)</td>
                                <td>
                                    <div class="admNavPanelInp">
                                        <div class="imgL"></div>
                                        <div>
                                            <input id="addexpiredDateButton" onClick="addExpiredDate()" type="button"  class="admNavPanelInp" style="width:83px;">
                                                <xsl:attribute name="value">
                                                    <xsl:value-of select="java:getString($dict_dialogs, 'HIDE')"/>
                                                </xsl:attribute>
                                            </input>
                                        </div>
                                        <div class="imgR"></div>
                                    </div>
                                    
                                     <div class="admNavPanelInp">
                                        <div class="imgL"></div>
                                        <div>
                                            <input onClick="clearExpiredDate()" type="button" class="admNavPanelInp">
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
                <tr>
                    <th class="admTableTD admWidth150"><xsl:value-of select="java:getString($dict_security_module, 'SINGLE_USER')"/></th>
                    <th class="admTableTDLast">
                    <input type="radio" name="singleUser" id="singleUserId" value="singleUser"  class="radio">
				      <xsl:if test="@singleUser = 'true'">
		  	                <xsl:attribute name="checked">true</xsl:attribute>    	                                             
	  			      </xsl:if>
                    </input></th>
                </tr>
                <tr>
                    <th class="admTableTD admWidth150"><xsl:value-of select="java:getString($dict_security_module, 'MULTI_USER')"/></th>
                    <th class="admTableTDLast">
                    <input type="radio" name="singleUser" id="singleUserId" value="multiUser" class="radio">
				      <xsl:if test="@singleUser = 'false'">
		  	                <xsl:attribute name="checked">true</xsl:attribute>    	                                             
	  			      </xsl:if>
                    </input></th>
                </tr>
                
        </table>
            <div class="admTDtitles">
       <xsl:call-template name="tableTitle">
            <xsl:with-param name="headtext">
                <xsl:value-of select="java:getString($dict_security_module, 'VISITOR_IS_MEMBER_OF_GROUPS')"/>:
            </xsl:with-param>
      </xsl:call-template>
            </div>
      <table cellpadding="0" cellspacing="0"  class="admNavPanel">
            <tr>
                <td class="admTDtitles"><xsl:value-of select="java:getString($dict_security_module, 'GROUP_TITLE')"/></td>
                <td class="admTDtitles"><xsl:value-of select="java:getString($dict_security_module, 'ROLE')"/></td>
                <td class="admTDtitles admOrder">&#160;</td>
            </tr>
            <xsl:for-each select="negeso:groups/negeso:group">
                 <tr>
                    <th class="admTableTD"><xsl:value-of select="@name"/></th>
                    <th class="admTableTD" width="100px"><xsl:value-of select="@role-id"/></th>
                    <td class="admTableTDLast">
                   <input type="checkbox" name="groups" value="{@id}">
                      <xsl:if test="@linked='true'">
                         <xsl:attribute name="checked"/>
                      </xsl:if>
                   </input>
                </td>
                </tr>
            </xsl:for-each>          
      </table>
            
    </form>
   
</xsl:template>
<!-- *********************** END MODE "EDIT" *********************** -->

<xsl:template match="negeso:context/*"/><!-- DIRTY PATCH -->

<!-- *********************** BEGIN MODE "EDIT_PASSWORD" ***************************** -->
<xsl:template match="negeso:user" mode="edit_password">
    <form method="POST" enctype="multipart/form-data" id="add_edit_form">
      <input type="hidden" name="action" value="save"/>
      <xsl:if test="@id">
         <input type="hidden" name="id" value="{@id}"/>
      </xsl:if>
        <table cellpadding="0" cellspacing="0" width="100%">
         <tr>
                <th class="admTableTD admWidth150"><xsl:value-of select="java:getString($dict_security_module, 'FULL_NAME')"/>*</th>
                <th class="admTableTDLast"><xsl:value-of select="@name"/></th>
            </tr>
                <tr>
                    <th class="admTableTD admWidth150"><xsl:value-of select="java:getString($dict_security_module, 'LOGIN')"/></th>
                    <th class="admTableTDLast"><xsl:value-of select="@login"/></th>
                </tr>
                <tr>
                    <th class="admTableTD admWidth150"><xsl:value-of select="java:getString($dict_security_module, 'PASSWORD')"/>*</th>
                    <th class="admTableTDLast"><input class="admTextArea" type="password" value="" name="password"/></th>
                </tr>
                <tr>
                    <th class="admTableTD admWidth150"><xsl:value-of select="java:getString($dict_security_module, 'RETYPE_PASSWORD')"/>*</th>
                    <th class="admTableTDLast"><input class="admTextArea " type="password" value="" name="pwd"/></th>
                </tr>
            
            </table>
    </form>
</xsl:template>

<!-- *********************** BEGIN MODE "LIST" ***************************** -->
<xsl:template match="negeso:users" mode="list">
    <form method="POST" enctype="multipart/form-data">

        <table cellpadding="0" cellspacing="0" width="100%">
                <tr>
                    <td class="admTDtitles" width="20%" align="center" style="padding-left:0px;"><xsl:value-of select="java:getString($dict_security_module, 'FULL_NAME')"/></td>
                    <td class="admTDtitles" align="center" style="padding-left:0px;"><xsl:value-of select="java:getString($dict_security_module, 'LOGIN')"/></td>
                    <td class="admTDtitles" align="center" style="padding-left:0px;"><xsl:value-of select="java:getString($dict_security_module, 'USER_STATUS')"/></td>
                    <td class="admTDtitles" align="center" style="padding-left:0px;"><xsl:value-of select="java:getString($dict_common, 'PUBLISH_DATE')"/></td>
                    <td class="admTDtitles" align="center" style="padding-left:0px;"><xsl:value-of select="java:getString($dict_common, 'EXPIRED_DATE')"/></td>
                    <td class="admTDtitles" colspan="3" style="padding-left:0px;">&#160;</td>
                </tr>
           <xsl:for-each select="negeso:user">
              <tr>
                <xsl:if test="@expired='false'">
	                <th class="admTableTD"><a class="admAnchor" 
					href='#' onClick="editAccount({@id})"><xsl:value-of select="@name"/></a></th>
	                <th class="admTableTD"><xsl:value-of select="@login"/></th>                
	                <th class="admTableTD"></th>                
                </xsl:if>
                <xsl:if test="@expired='true'">
                    <th class="admTableTD">
                        <a class="admAnchorGray"
 href='#' onClick="editAccount({@id})">
                            <xsl:value-of select="@name"/></a></th>
	                <th class="admTableTD"><xsl:value-of select="@login"/></th>                
	                <th class="admTableTD"><xsl:value-of select="java:getString($dict_security_module, 'USER_EXPIRED')"/></th>                
                </xsl:if>                
                <th class="admTableTD"><xsl:value-of select="@publishDate"/></th>
                <th class="admTableTD"><xsl:value-of select="@expiredDate"/></th>                
                
                <td class="admTableTD">
                    <img src="/images/edit.png" class="admHand" onClick="editAccount({@id})">
                        <xsl:attribute name="alt"><xsl:value-of select="java:getString($dict_security_module, 'EDIT_ACCOUNT')"/></xsl:attribute>
                    </img>
                </td>
                <td class="admTableTD">
                    <img src="/images/password.png" class="admHand" onClick="editPassword({@id})">
                        <xsl:attribute name="alt"><xsl:value-of select="java:getString($dict_security_module, 'CHANGE_PASSWORD')"/></xsl:attribute>
                    </img>
                </td>
                <td class="admTableTDLast">
                    <img src="/images/delete.png" class="admHand" onClick="deleteAccount({@id})">
                        <xsl:attribute name="alt"><xsl:value-of select="java:getString($dict_security_module, 'DELETE_ACCOUNT')"/></xsl:attribute>
                    </img>
                </td>
              </tr>
           </xsl:for-each>
            </table>
    </form>
</xsl:template>
    <xsl:template name="consts">
        <xsl:param name="id"/>
        <a href=""  title="Consts" class="admAnchor" target="_blank" >
            <xsl:attribute name="href">/admin/module_consts?moduleId=<xsl:value-of select="$id"/>&amp;visitorMode=true</xsl:attribute>
            <xsl:value-of select="java:getString($dict_modules, 'SETTINGS')"/>
        </a>
    </xsl:template>
<!-- *********************** END MODE "LIST" *********************** -->
    <!--******************** Bottom buttons ********************-->
    <xsl:template name="buttons">
        <xsl:if test="/negeso:page/negeso:contents/negeso:users">
            <table cellpadding="0" cellspacing="0"  width="764px" align="center" class="Buttons">
                <tr>
                    <td>

                        <div class="admBtnGreenb">
                            <div class="imgL"></div>
                            <div>
                                <input class="admNavbarInp" type="button" onClick="addAccount()">
                                    <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_security_module, 'ADD_NEW_ACCOUNT')"/></xsl:attribute>
                                </input>
                            </div>
                            <div class="imgR"></div>
                        </div>
                    </td>
                </tr>
            </table>
        </xsl:if>
        <xsl:if test="$action='edit'">
            <table cellpadding="0" cellspacing="0"  width="764px" align="center" class="Buttons">
                <tr>
                    <td >
                        <div class="admBtnGreenb">
                            <div class="imgL"></div>
                            <div>
                                <input class="admNavbarInp" type="button" style="width:135px;" onclick="validateAddEditForm('/{@id}',add_edit_form.form)">
                                    <xsl:attribute name="value">
                                        <xsl:value-of select="java:getString($dict_common, 'SAVE_AND_CLOSE')"/>
                                    </xsl:attribute>
                                </input>
                            </div>
                            <div class="imgR"></div>
                        </div>
                    </td>
                </tr>
            </table>
        </xsl:if>
        
        <xsl:if test="$action='add'">
            <table cellpadding="0" cellspacing="0"  width="764px" align="center" class="Buttons">

                <tr>
                    <td >
                        <div class="admBtnGreenb">
                            <div class="imgL"></div>
                            <div>
                                <input class="admNavbarInp" type="button" style="width:135px;" onclick="validateAddEditForm('{@id}',this.form)">
                                    <xsl:attribute name="value">
                                        <xsl:value-of select="java:getString($dict_common, 'SAVE_AND_CLOSE')"/>
                                    </xsl:attribute>
                                </input>
                            </div>
                            <div class="imgR"></div>
                        </div>
                    </td>
                </tr>
            </table>
        </xsl:if>
        
       <xsl:if test="$action='edit_password'">
            <table cellpadding="0" cellspacing="0"  width="764px" align="center" class="Buttons">

                <tr>
                    <td >
                        <div class="admBtnGreenb">
                            <div class="imgL"></div>
                            <div>
                                <input class="admNavbarInp" type="button"  style="width:135px;" onclick="validatePasswordForm()">
                                    <xsl:attribute name="value"> <xsl:value-of select="java:getString($dict_common, 'SAVE_AND_CLOSE')"/>  </xsl:attribute>
                                </input>
                            </div>
                            <div class="imgR"></div>
                        </div>
                    </td>
                </tr>
            </table>
        </xsl:if>
    </xsl:template>
<!-- *********************** TEMP *********************** -->

</xsl:stylesheet>


