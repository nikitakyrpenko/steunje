<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)$Id$
 
  Copyright (c) 2005 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.
 
  Location management interface
 
  @version      $Revision$
  @author       Olexiy.Strashko
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
<xsl:variable name="dict_contact_book" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_contact_book.xsl', $lang)"/>

<!-- NEGESO JAVASCRIPT Temaplate -->    
<xsl:template name="java-script">
    <script language="JavaScript">

    var s_SearchStringIsEmpty = "<xsl:value-of select="java:getString($dict_contact_book, 'SEARCH_STRING_IS_EMPTY')"/>";
    var s_DeleteContactConfirmation = "<xsl:value-of select="java:getString($dict_contact_book, 'DELETE_CONTACT_CONFIRMATION')"/>";

    <xsl:text disable-output-escaping="yes">
        <![CDATA[
        // PUBLICATION MANAGEMENT
        function addContact() {
            document.operateForm.action.value = "add_contact";
        }

        function editContact(targetId) {
            document.operateForm.action.value = "show_contact";
            document.operateForm.contactId.value = targetId;
            document.operateForm.submit();
        }

        function deleteContact(targetId) {
            if (confirm(s_DeleteContactConfirmation)) {
                document.operateForm.action.value = "remove_contact";
	            document.operateForm.contactId.value = targetId;
                document.operateForm.submit();
                return true;
            }
            return false;
        }
        
        function submitIt(e) {
        	if (e.keyCode == 13) {
        		searchContacts();
        	}
        }
        
        function searchContacts(targetId) {
        	if ( document.operateForm.search_string.value == "" ){
        		alert(s_SearchStringIsEmpty);
        		return;
        	}
            document.operateForm.command.value = "cb-manage-contacts";
            document.operateForm.action.value = "search_contacts";
            document.operateForm.submit();
        }
        
        function browseGroup(targetId) {
            document.operateForm.command.value = "cb-manage-contacts";
            document.operateForm.action.value = "";
            document.operateForm.groupId.value = targetId;
            document.operateForm.submit();
        }       
		
        ]]>
        </xsl:text>
    </script>
</xsl:template>

<!-- MAIN ENTRY -->
<xsl:template match="/">
<html>
    <head>
        <title><xsl:value-of select="java:getString($dict_contact_book, 'BROWSE_CONTACTS')"/></title>
        <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
        <script type="text/javascript" src="/script/jquery.min.js"></script>
        <script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
        <script type="text/javascript" src="/script/cufon-yui.js"></script>
        <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
        <script language="JavaScript" src="/script/common_functions.js" type="text/javascript">/**/</script>
        <xsl:call-template name="java-script"/>
        <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
    </head>
    <body>
        <!-- NEGESO HEADER -->
        <xsl:call-template name="NegesoBody">
            <xsl:with-param name="helpLink" select="''"/>
            <xsl:with-param name="backLink" select="'contact_book'"/>
        </xsl:call-template>
    </body>
</html>
</xsl:template>

<xsl:template match="negeso:page" mode="admContent">
    <table cellspacing="0" cellpadding="0" border="0" align="center" width="100%" class="admTable">
        <tr>
            <td style="width:auto; height:auto; padding-left:20px;">
                <!-- PATH -->
                <xsl:call-template name="cb.contactPath"/>
            </td>
        </tr>

        <tr>
            <td class="admNavPanelFont" align="center">
                <!-- TITLE -->
                <xsl:call-template name="tableTitle">
                    <xsl:with-param name="headtext">
                        <xsl:value-of select="java:getString($dict_contact_book, 'BROWSE_CONTACTS')"/>
                    </xsl:with-param>
                </xsl:call-template>
            </td>
        </tr>        

        <tr>
            <td>
                <!-- Content -->
                <xsl:apply-templates select="negeso:cb-group"/>
                <xsl:apply-templates select="negeso:cb-search-results"/>
            
            </td>
        </tr>
        <tr>
            <td class="admTableFooter" >&#160;</td>
        </tr>
    </table>
</xsl:template>

<!-- ********************************** Category *********************************** -->
<xsl:template match="negeso:cb-group">
    <!-- Render HEADER -->
    <form method="POST" name="operateForm" action="">
        <input type="hidden" name="command" value="cb-manage-contacts"></input>
        <input type="hidden" name="action" value=""></input>
        <input type="hidden" name="groupId" value="{@id}"></input>
        <input type="hidden" name="contactId" value="{@id}"></input>

        <xsl:call-template name="cb_results"/>        
    </form>
</xsl:template>

<!-- ********************************** Category *********************************** -->
<xsl:template match="negeso:cb-search-results">
    <!-- Render HEADER -->
    <form method="POST" name="operateForm" action="">
        <input type="hidden" name="command" value="cb-manage-contacts"></input>
        <input type="hidden" name="action" value=""></input>
        <input type="hidden" name="groupId" value=""></input>
        <input type="hidden" name="contactId" value=""></input>
        <input type="hidden" name="back_link" value="search"></input>

        <xsl:call-template name="cb_results"/>
    </form>
</xsl:template>

<xsl:template name="cb_results">
    <table cellspacing="0" cellpadding="0" width="100%" border="0">
        <tr>
            <th class="admTableTDLast admWidth150">Find contact</th>
            <td class="admTableTDLast admWidth150">
                <input class="admTextArea admWidth150" type="text" name="search_string" onkeypress="submitIt(event)"/>
            </td>
            <td class="admTableTDLast">
                <div style="padding:0;margin:0;" class="admNavPanelInp">
                    <div class="imgL"></div>
                    <div>
                        <button class="admNavbarInp" onClick = "searchContacts(); return false;">
                            <xsl:value-of select="java:getString($dict_common, 'SEARCH')"/>
                        </button>
                    </div>
                    <div class="imgR"></div>
                </div>
            </td>

            <td class="admTableTDLast admWidth150">
                <div style="padding:0;margin:0;" class="admNavPanelInp">
                    <div class="imgL"></div>
                    <div>
                        <input class="admNavbarInp" type="submit" onClick = "addContact()">
                            <xsl:attribute name="value">
                                <xsl:value-of select="java:getString($dict_contact_book, 'ADD_CONTACT')"/>
                            </xsl:attribute>
                        </input>
                    </div>
                    <div class="imgR"></div>
                </div>
            </td>
        </tr>
    </table>

    <table class="admNavPanel" cellspacing="0" cellpadding="0">
        <tr>
            <td class="admTDtitles">
                <xsl:value-of select="java:getString($dict_common, 'IMAGE')"/>
            </td>
            <td class="admTDtitles">
                <xsl:value-of select="java:getString($dict_common, 'TITLE')"/>
            </td>
            <td class="admTDtitles">
                <xsl:value-of select="java:getString($dict_common, 'DETAILS')"/>
            </td>
            <td class="admTDtitles" colspan="2">&#160;</td>
        </tr>
        <xsl:apply-templates select="negeso:contact"/>
    </table>     
</xsl:template>

<!-- ********************************** Contact *********************************** -->
<xsl:template match="negeso:contact">
    <tr>
        <td class="admTableTD admWidth100">
            <a class="admAnchor" href="#" onClick = "return editContact({@id})">
            	<xsl:choose>
	            	<xsl:when test="@image-link">
		            	<img class="admImg" width="100px" height="100px" src="{@image-link}" alt="{@title}"/>
			        </xsl:when>
			        <xsl:otherwise>
		            	<img class="admImg" width="100px" height="100px" src="/images/noImage.gif" alt="{@title}"/>
			        </xsl:otherwise>
		        </xsl:choose>
            </a>
        </td>
        <th class="admTableTD">
            <a class="admAnchor" href="#" onClick = "return editContact({@id})">
                <xsl:value-of select="@title"/>&#160;
            </a>
            <xsl:if test="@birthday">
	            <br/>
	            <span style="font-size: 10px;">
		  			<b><xsl:value-of select="java:getString($dict_contact_book, 'BIRTHDAY')"/>:</b>&#160;<xsl:value-of select="@birthday"/>
	    		</span>
	    	</xsl:if>
        </th>
        <th class="admTableTD">
        	<table style="font-size: 11px; margin:10px 0;">
        		<xsl:if test="@group-title">
	        		<tr>
                        <td>
	        			    <b><xsl:value-of select="java:getString($dict_common, 'GROUP')"/>:</b>&#160;
	        				<xsl:if test="@email">
					            <a class="admAnchor" href="#" onClick = "return browseGroup({@group-id})">
			        				<xsl:value-of select="@group-title"/>
					            </a>
		        			</xsl:if>
	        			</td>
                    </tr>
	        	</xsl:if>
                
        		<tr><td><b><xsl:value-of select="java:getString($dict_common, 'PHONE')"/>:</b>&#160;<xsl:value-of select="@phone"/></td></tr>
        		<tr>
                    <td>
        			    <b><xsl:value-of select="java:getString($dict_common, 'EMAIL')"/>:</b>&#160;
        				<xsl:if test="@email">
	        				<a href="mailto:{@email}"><xsl:value-of select="@email"/></a>
	        			</xsl:if>
        			</td>
                </tr>
        		<tr><td><b><xsl:value-of select="java:getString($dict_contact_book, 'DEPARTMENT')"/>:</b>&#160;<xsl:value-of select="@department"/></td></tr>
        		<tr><td><b><xsl:value-of select="java:getString($dict_contact_book, 'JOB_TITLE')"/>:</b>&#160;<xsl:value-of select="@job-title"/></td></tr>
        		<tr>
                    <td>
        			    <b><xsl:value-of select="java:getString($dict_common, 'WEB_ADDRESS')"/>:</b>&#160;
        				<xsl:if test="@web-link">
	        				<a href="{@web-link}"><xsl:value-of select="@web-link"/></a>
	        			</xsl:if>
        			</td>
                </tr>                
        	</table>
        </th>
        <td class="admTableTDLast">
            <img src="/images/edit.png" class="admHand" onClick="return editContact({@id})">
                <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'EDIT')"/></xsl:attribute>
            </img>
            <br/>
            <img src="/images/delete.png" class="admHand" onClick="return deleteContact({@id})">
                <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'DELETE')"/></xsl:attribute>
         </img>
        </td>
    </tr>
</xsl:template>

<xsl:template name="cb.contactPath">
    <!-- Path -->
    <table border="0" cellpadding="0" cellspacing="0" align="left">
        <tr>
            <td style="padding:8px 0 0 5px;" valign="middle" class="admNavigation">
                <a class="admLocation" href="contact_book">
                    <xsl:value-of select="java:getString($dict_contact_book, 'CONTACT_BOOK')"/>
                </a>
            </td>

            <xsl:if test="count(/negeso:page/negeso:cb-search-results)>0">
                <!-- Active pathe element - just print it-->
                <td valign="middle" class="admNavigation" style="padding:8px 0 0 0; text-decoration:none;">
                    &#160; <img src="/images/navig_arrow.png"  style="vertical-align:middle;"/>&#160;
                    <span >
                        <xsl:value-of select="java:getString($dict_common, 'SEARCH_RESULTS')"/>
                    </span>
                </td>
            </xsl:if>

            <xsl:if test="negeso:cb-group/@title">
                <!-- Active pathe element - just print it-->
                <td valign="middle" class="admNavigation" style="padding:8px 0 0 0;text-decoration:none;">
                    &#160; <img src="/images/navig_arrow.png"  style="vertical-align:middle;"/>&#160;
                    <span >
                        <xsl:value-of select="negeso:cb-group/@title"/>
                    </span>
                </td>
            </xsl:if>            
        </tr>
    </table>
</xsl:template>

</xsl:stylesheet>