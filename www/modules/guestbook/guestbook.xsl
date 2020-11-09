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
<xsl:include href="/xsl/negeso_header.xsl"/>
<xsl:include href="/xsl/admin_templates.xsl"/>


<xsl:variable name="lang" select="/*/@interface-language"/>
<xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>
<xsl:variable name="dict_guestbook" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_guestbook.xsl', $lang)"/>


<!-- NEGESO JAVASCRIPT Temaplate -->    

<xsl:template name="java-script">
    <script language="JavaScript">

    var s_DeleteWordConfirmation = "<xsl:value-of select="java:getString($dict_guestbook, 'GB_DELETE_MESSAGE_CONFIRMATION')"/>";

    <xsl:text disable-output-escaping="yes">
        <![CDATA[
        function findWords() {
        	window.location="?show=find&find=" + operateForm.findW.value;
        }

        function deleteMessage(targetId, gbId) {
        	 if(confirm(s_DeleteWordConfirmation)){
	        	 document.operateForm.action.value = "gb_delete";
		         document.operateForm.id.value = targetId;
		         document.operateForm.gb_id.value = gbId;
		         document.operateForm.submit();
		         return true;
	         }
        }
		
		function publishMessage(targetId, gbId) {
        
        	 document.operateForm.action.value = "gb_publish";
	         document.operateForm.id.value = targetId;
	         document.operateForm.gb_id.value = gbId;
	         document.operateForm.submit();
	         return true;
        }
		
        function update() {
        	if ( !validateForm(operateForm) ){
                return false;
            }
            document.operateForm.action.value = "save_word";
        	return true;
        }

        function back() {
            document.operateForm.action.value = "";
        }

        function addWord() {
            document.operateForm.action.value = "add_word";
        }

        function deleteWord(targetId) {
        	if (confirm(s_DeleteWordonfirmation)) {
	            document.operateForm.action.value = "delete_word";
	            document.operateForm.removingMessageId.value = targetId;
	            document.operateForm.submit();
	            return true;
	        }
	        return false;
        }

        function editWord(targetId) {
            document.operateForm.wid.value = targetId;
            document.operateForm.submit();
        }

        ]]>
        </xsl:text>
    </script>
    
    <xsl:call-template name="adminhead"/>
    
</xsl:template>

<!-- MAIN ENTRY -->
<xsl:template match="/">
<html>
<head>
    <title><xsl:value-of select="java:getString($dict_guestbook, 'GB_GUESTBOOK')"/></title>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <xsl:call-template name="java-script"/>
    <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" src="/script/jquery.min.js"></script>
	<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
    <script language="JavaScript1.2" src="/script/common_functions.js" type="text/javascript"/>
</head>
<body
    style="behavior: url(/script/webservice.htc) url(/script/clientmanager.htc)"
    id="ClientManager" xmlID="332"
>

    <!-- NEGESO HEADER -->
     <xsl:call-template name="NegesoHeader"/>
     <div align="center">
        <!-- CONTENT -->
        <xsl:apply-templates select="negeso:page"/>
    </div>
</body>
</html>
</xsl:template>


<xsl:template match="//negeso:guestbooks">
<br/>
<table width="100%" class="admNavPanel" cellspacing="7" cellpadding="0" border="0">
	<tr>
		<td colspan="5" class="admNamebar" width="*"><xsl:value-of select="java:getString($dict_guestbook, 'GB_FULL_NAME')"/></td>
		<!-- td colspan="5" class="admNamebar" width="*">Full name</td -->
	</tr>
	<xsl:apply-templates select="negeso:guestbook"/>

</table>
</xsl:template>

<xsl:template match="//negeso:guestbook">
    	<tr valign="bottom">
    		<td valign="top" class="admLightTD admLeft">
			<a class="admNone" style="color: #000000">
				<xsl:attribute name="href">?gb_id=<xsl:value-of select="@id"/></xsl:attribute>
				<b>
				<xsl:value-of select="@name"/>
			</b></a>
		</td>
<!-- ICON "RENAME" -->
<!-- ICON "EDIT PAGE" -->
<!-- ICON "PAGE PROPERTIES" -->
<!-- ICON "DELETE" -->
<!-- ICON "DELETE" -->
<!--		
		<td class="admLightTD" align="center" width="31">
                    <img src="/images/rename.gif" height="27" class="admHand icon"/>                    
                </td>
                <td class="admLightTD" align="center" width="31">
                        <img src="/images/edit.gif" width="31" height="27" onclick="" class="admHand icon" alt=""/>
                </td>
                <td class="admLightTD" align="center" width="31">
	               <img src="/images/properties.gif" width="31" height="27" onclick="" class="admHand icon" alt=""/>
		</td>
                <td class="admLightTD" align="center" width="31">
                    <img src="/images/delete.gif" width="31" height="27" onclick="" class="admHand icon" alt=""/>
                </td>
-->
	</tr>
</xsl:template>

<xsl:template match="//negeso:guestbook" mode="old">
<br/>
<table width="100%" class="admNavPanel" cellspacing="0" cellpadding="0" border="0">
	<tr>
	    <td width="*" class="admNamebar">
	        <xsl:value-of select="java:getString($dict_guestbook, 'GB_MESSAGE')"/>
	    </td>
	    <td class="admNamebar" width="100">
	        <xsl:value-of select="java:getString($dict_guestbook, 'GB_POSTED')"/>
	    </td>
	    <xsl:if test="@publishedOnly != 'false'">
	    <td class="admNamebar" width="100">
	        <xsl:value-of select="java:getString($dict_guestbook, 'GB_PUBLISHED')"/>
	    </td>
	    <td class="admNamebar" width="125">
	        &#160;
	    </td>
	    </xsl:if>
	    <td class="admNamebar" width="40">
	        &#160;
	    </td>
	</tr>
	<xsl:apply-templates select="negeso:record"/>
	<tr>
	<td colspan="3">
	    <xsl:if test="@publishedOnly != 'false'">
	    	<xsl:attribute name="colspan">5</xsl:attribute>
	    </xsl:if>
	<xsl:apply-templates select="negeso:page-navigator" mode="pm"/>
	</td>
	</tr>
</table>
<br/>
</xsl:template>


<xsl:template match="negeso:record">

<tr valign="middle">
    <td class="admMainTD" align="left">
            <b><xsl:value-of select="@name"/></b>
            <br/>
            <i><xsl:value-of select="@email"/></i>
            <br/>
            <div class="guestbookMessage"><xsl:value-of select="@message"/></div>
    </td>
    <td class="admMainTD" width="80" align="center">
        <xsl:value-of select="@posted"/>
    </td>
    <xsl:if test="../@publishedOnly != 'false'">
	    <td class="admMainTD" width="80" align="center">
	        <xsl:value-of select="@published"/>
	    </td>
	    <td class="admMainTD" width="40" align="center">
	    <xsl:if test="not(@published)">
	        <button onClick="publishMessage({@id},{../@id});" name="selectImageButton" class="admMainInp admWidth150">&lt;&#160;<xsl:value-of select="java:getString($dict_guestbook, 'GB_PUBLISH')"/>&#160;&gt;</button>
	    </xsl:if>
	    </td>
    </xsl:if>
    <td width="40" class="admMainTD" align="center">
        <img src="/images/delete.gif" class="admHand" onClick="deleteMessage({@id},{../@id});" alt="Delete"/>
    </td>
</tr>

</xsl:template>

<xsl:template match="negeso:page">
    <!-- NavBar -->
    <xsl:choose>
        <xsl:when test="//negeso:guestbooks">
            <xsl:call-template name="NavBar"/>
        </xsl:when>
        <xsl:when test="1">
            <xsl:call-template name="NavBar">
            	<xsl:with-param name="backLink" select='"guest_book_xsl"' />
            </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>
              <xsl:call-template name="NavBar"/>
        </xsl:otherwise>
   </xsl:choose>


    <!-- TITLE -->
    <xsl:choose>
		<xsl:when test="//negeso:guestbooks">
			<xsl:call-template name="tableTitle">
			  <xsl:with-param name="headtext">
			      	<xsl:value-of select="java:getString($dict_guestbook, 'GB_GUESTBOOKS_LIST')"/>
			  </xsl:with-param>
			</xsl:call-template>
		</xsl:when>
		<xsl:otherwise>
			<xsl:call-template name="tableTitle">
			  <xsl:with-param name="headtext">
			      	<xsl:value-of select="java:getString($dict_guestbook, 'GB_GUESTBOOK')"/>: <xsl:value-of select="//negeso:guestbook/@name" />
			  </xsl:with-param>
			</xsl:call-template>
		</xsl:otherwise>
	</xsl:choose>    

    <!-- Content -->
    <form method="POST" name="operateForm" action="guest_book_xsl">
	    <input type="hidden" name="command" value="gb-manage-guestbook"></input>
	    <input type="hidden" name="action" value=""/>
	    <input type="hidden" name="id" value=""/>
	    <input type="hidden" name="removingMessageId" value=""/>
	    <input type="hidden" name="back-link" value="words"/>
	    <input type="hidden" name="wid" value=""/>
	    <input type="hidden" name="gb_id" value=""/>
	    
    	<!--xsl:apply-templates select="negeso:glossary_header"/>
    	<xsl:apply-templates select="negeso:glossary_search_results"/>
    	<xsl:apply-templates select="negeso:glossary_word_details"/-->
    </form>
	<xsl:choose>
		<xsl:when test="//negeso:guestbooks">
			<xsl:apply-templates select="//negeso:guestbooks"/>
		</xsl:when>
		<xsl:otherwise>
			<xsl:apply-templates select="//negeso:guestbook" mode="old"/>
		</xsl:otherwise>
	</xsl:choose>

    <!-- NavBar -->
    <xsl:choose>
        <xsl:when test="//negeso:guestbooks">
            <xsl:call-template name="NavBar"/>                    
        </xsl:when>
        <xsl:otherwise>
              <xsl:call-template name="NavBar">
                    <xsl:with-param name="backLink" select='"guest_book_xsl"' />
            </xsl:call-template>
        </xsl:otherwise>
   </xsl:choose>

</xsl:template>




<!--========================================== Page navigator =================================-->
<xsl:template match="negeso:page-navigator" mode="pm">
	<table width="100%" class="cartTable" border="0">
		<tr>
			<td class="cartTableCell" align="left" colspan="5">
				<xsl:if test="count(negeso:prev) != 0">
					<a href="?gb_id={//negeso:guestbook/@id}&amp;page_id={negeso:prev/@page-id}">
					<xsl:value-of select="java:getString($dict_guestbook, 'PREVIOUS')"/>
					</a>
				</xsl:if>
				<xsl:for-each select="negeso:page">
					&#160;<a href="?gb_id={//negeso:guestbook/@id}&amp;page_id={@page-id}">
					<xsl:if test="count(@current) > 0">[</xsl:if>
					<xsl:value-of select="@page-id"/>
					<xsl:if test="count(@current) > 0">]</xsl:if>
					</a>&#160;
				</xsl:for-each>
				<xsl:if test="count(negeso:next) != 0">
					<a href="?gb_id={//negeso:guestbook/@id}&amp;page_id={negeso:next/@page-id}">
					<xsl:value-of select="java:getString($dict_guestbook, 'GB_NEXT')"/>
					</a>
				</xsl:if>
			</td>
			<td width="24%" align="left">
				&#160;<xsl:value-of select="java:getString($dict_guestbook, 'GB_MESSAGES')"/>: <xsl:value-of select="@result-size"/>&#160;
			</td>
		</tr>
	</table>
</xsl:template>


</xsl:stylesheet>