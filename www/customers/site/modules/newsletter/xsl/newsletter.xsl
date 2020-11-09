<?xml version="1.0" encoding="UTF-8"?>
<!--
  Copyright (c) 2007 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information"). You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.


  XSL templates for Newsletter Module
 
  @version		2007.11.27
  @author		Oleg 'germes' Shvedov
  @author		Rostislav 'KOTT' Brizgunov

-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">

<xsl:variable name="dict_newsletter_module" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('newsletter_module', $lang)"/>
<xsl:variable name="admin_dict_newsletter_module" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_newsletter.xsl', $lang)"/>

<xsl:template match="negeso:newsletter" mode="page_head">
	<link rel="stylesheet" type="text/css" href="/site/modules/newsletter/css/newsletter.css"/>
	<script type="text/javascript" src="/site/modules/newsletter/script/newsletter.js">/**/</script>
</xsl:template>
<!--===================================== NEWSLETTER MODULE BEGIN ======================================-->
<xsl:template match="negeso:newsletter" mode="newsletter">
    <xsl:choose>
        <!-- Newsletter main page -->
        <xsl:when test="@view-type='nl_simple_view'">
            <form method="post" action="" id="nlSimpleSubscribeFormId" name="nlSimpleSubscribeForm" enctype="multipart/form-data">
                <input type="hidden" name="action" value="subscribe"/>
                <input type="hidden" name="language_code" value="{/negeso:page/@lang}"/>
                <input type="hidden" name="pageLink" value="{$param[@name='REQUEST_URL']}"/>
                <input type="hidden" name="siteLink" value=""/>

                <xsl:call-template name="nl_content_block" />

            </form>
        </xsl:when>
        <!-- Newsletter subscribe/unsubscribe result -->
        <xsl:when test="@view-type='nl_result_view'">
            <xsl:apply-templates select="negeso:newsletter-message" mode="simpleMode"/>
        </xsl:when>
        <!-- Newsletter archive -->
        <xsl:when test="@view-type='archive'">
            <xsl:apply-templates select="negeso:Publications"/>
        </xsl:when>
        <!-- Newsletter publication -->
        <xsl:when test="@view-type='publication'">
            <xsl:apply-templates select="negeso:Publication"/>
        </xsl:when>
        <!-- Newsletter errors -->
        <xsl:when test="not(@view-type) and @response = 'error'">
            <h2>
                <xsl:call-template name ="add-constant-info">
                    <xsl:with-param name ="dict"  select="$dict_newsletter_module"/>
                    <xsl:with-param name ="name"  select="'NL_ERROR'"/>
                </xsl:call-template>
                <xsl:value-of select="java:getString($dict_newsletter_module, 'NL_ERROR')"/>
            </h2>

            <xsl:call-template name="nl_white_area">
                <xsl:with-param name="button_back" select="'yes'"/>
                <xsl:with-param name="body" select="'NL_COMMON_ERROR'" />
            </xsl:call-template>
        </xsl:when>
    </xsl:choose>
</xsl:template>

<!-- *************** MAIN CONTENT TEMPLATES START *************** -->

<xsl:template match="negeso:newsletter-attribute" mode="simpleMode">
    <tr>
        <th>
            <span><xsl:if test="@required='true'">*</xsl:if></span>
            <xsl:value-of select="@value"/>:
        </th>
        <td class="right">
            <input class="text" type="text" name="attribute_{@id}" value="" uname="{@key}" required="{@required}">
                <xsl:if test="@key='EMAIL'">
                    <xsl:attribute name="is_email">true</xsl:attribute>
                </xsl:if>
            </input>
        </td>
    </tr>
</xsl:template>

<xsl:template name="nl_subscibe_form">
	<!-- Current tag is negeso:newsletter -->
	<table class="b-nlTable" cellspacing="0" cellpadding="0">
        <xsl:apply-templates select="negeso:newsletter-attributes/negeso:newsletter-attribute" mode="simpleMode"/>
        <tr>
            <th>
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_newsletter_module"/>
					<xsl:with-param name ="name"  select="'NL_TEXT_TYPE'"/>
				</xsl:call-template>
                <xsl:value-of select="java:getString($dict_newsletter_module, 'NL_TEXT_TYPE')"/>
            </th>
            <td class="right">
                <xsl:call-template name="nl_select_text_type" />
            </td>
        </tr>
        <tr>
            <th>
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_newsletter_module"/>
					<xsl:with-param name ="name"  select="'NL_PREF_LANG'"/>
				</xsl:call-template>
                <xsl:value-of select="java:getString($dict_newsletter_module, 'NL_PREF_LANG')"/>
            </th>
            <td class="right">
                <xsl:apply-templates select="negeso:languages" mode="simpleMode"/>
            </td>
        </tr>
	</table>
</xsl:template>

<!-- *************** MAIN CONTENT TEMPLATES END *************** -->

<xsl:template match="negeso:Publications">
    <h2>
		<xsl:call-template name ="add-constant-info">
			<xsl:with-param name ="dict"  select="$dict_newsletter_module"/>
			<xsl:with-param name ="name"  select="'NL_NEWSLETTER_ARCHIVE'"/>
		</xsl:call-template>
    </h2>
    <div class="b-nlContent">
		<xsl:for-each select="negeso:Year">
            <fieldset class="l-nlArchive contentStyle">
				<legend><xsl:value-of select="@value"/></legend>
                <div class="legendBlock">
				<xsl:for-each select="negeso:Month">
                        <h3><xsl:value-of select="@title"/></h3>
                        <ul>
					<xsl:for-each select="negeso:Publication">
                                <li><a class="link" href="?action=publication&amp;pubId={@id}"><xsl:value-of select="@title"/>...</a></li>
					</xsl:for-each>
                        </ul>
				</xsl:for-each>
                </div>
			</fieldset>
		</xsl:for-each>
        <div class="btn">
            <button onfocus="blur()">
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_newsletter_module"/>
					<xsl:with-param name ="name"  select="'NL_BACK'"/>
				</xsl:call-template>
                <xsl:attribute name="class">submit<xsl:if test="count(//negeso:Publication) = 0">_disable</xsl:if></xsl:attribute>
				<xsl:choose>
					<xsl:when test="count(//negeso:Publication) = 0">
						<xsl:attribute name="disabled">
							<xsl:text>true</xsl:text>
						</xsl:attribute>
                        <xsl:attribute name="onClick"><xsl:text>javascript:return false;</xsl:text></xsl:attribute>
					</xsl:when>
					<xsl:otherwise>
                        <xsl:attribute name="onClick"><xsl:text>javascript:window.location='?action'</xsl:text></xsl:attribute>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:value-of select="java:getString($dict_newsletter_module, 'NL_BACK')"/>
			</button>
		</div>
	</div>
</xsl:template>

<xsl:template match="negeso:Publication">
	<xsl:call-template name="page_title_line_block">
		<xsl:with-param name="width" select="'100%'"/>
		<xsl:with-param name="title">
			<xsl:value-of select="@year"/>.<xsl:value-of select="@month"/>.<xsl:value-of select="@day"/>
		</xsl:with-param>
	</xsl:call-template>
	<div class="nlContent">
		<fieldset class="nlArchive contentStyle">
			<legend><xsl:value-of select="@title"/></legend>
			<div class="legendBlock pad10">
				<xsl:value-of select="text()" disable-output-escaping="yes"/>
			</div>
		</fieldset>
		<div>
			<BUTTON class="submit" onClick="javascript:window.location='?action=archive'">
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_newsletter_module"/>
					<xsl:with-param name ="name"  select="'NL_BACK_TO_ARCHIVE'"/>
				</xsl:call-template>
			   <xsl:value-of select="java:getString($dict_newsletter_module, 'NL_BACK_TO_ARCHIVE')"/>
			</BUTTON>
		</div>
	</div>
</xsl:template>

<xsl:template name="nl_white_area">
<xsl:param name="button_back">no</xsl:param>
<xsl:param name="body">&#160;</xsl:param>
	<table class="b-nlTable" cellspacing="0" cellpadding="0">
		<tr>
			<td>				
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_newsletter_module"/>
						<xsl:with-param name ="name"  select="$body"/>
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_newsletter_module, $body)"/>
					<br/><br/>
					<xsl:if test="$button_back = 'yes'">
						<xsl:call-template name="nl_back_link" />
					</xsl:if>
			</td>
		</tr>
	</table>
</xsl:template>

<xsl:template name="nl_content_block">
	<!-- HEAD: begin -->
<h1>
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_newsletter_module"/>
						<xsl:with-param name ="name"  select="'NL_SUBSCRIBE_TITLE'"/>
					</xsl:call-template>
					<xsl:if	test="$outputType =	'admin' and not(/negeso:page/@role-id = 'visitor')">
						<img alt="" align="absMiddle" class="hand" src="/images/mark_1.gif" onClick="window.open('/admin/nl_settings', 'modules', 'top=50,left=50,height=600,width=800,status=0,toolbar=0,menubar=0,location=0,resizable=1,scrollbars=yes')"/>
						<xsl:text>&#160;</xsl:text>
					</xsl:if>
					<xsl:value-of select="java:getString($dict_newsletter_module, 'NL_SUBSCRIBE_TITLE')"/>
</h1>
	<!-- HEAD: end -->

	<!-- BODY: begin -->
	<xsl:call-template name="nl_subscibe_form" />
	<!-- BODY: end -->
	
	<!-- HEAD: begin -->
	<xsl:call-template name="page_title_line_block">
		<xsl:with-param name="width" select="'100%'"/>
		<xsl:with-param name="title">
			<xsl:choose>
				<xsl:when test="count(negeso:newsletter-groups/negeso:newsletter-group) &gt; 0">
					<strong>
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_newsletter_module"/>
							<xsl:with-param name ="name"  select="'NL_SEL_GROUP'"/>
						</xsl:call-template>
						<xsl:value-of select="java:getString($dict_newsletter_module, 'NL_SEL_GROUP')"/></strong>
				</xsl:when>
				<xsl:otherwise>
					<strong>
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_newsletter_module"/>
							<xsl:with-param name ="name"  select="'NL_NO_GROUP'"/>
						</xsl:call-template>
						<xsl:value-of select="java:getString($dict_newsletter_module, 'NL_NO_GROUP')"/></strong>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:with-param>
	</xsl:call-template>
	<!-- HEAD: end -->
	
	<!-- GROUPS: begin -->
	<xsl:call-template name="nl_sel_group_section" />
	<!-- GROUPS: end -->
</xsl:template>

<xsl:template name="nl_select_text_type">
	<!-- Current tag is negeso:newsletter -->
	<select name="input_text_type" class="nl_select">
		<xsl:call-template name ="add-constant-info">
			<xsl:with-param name ="dict"  select="$dict_newsletter_module"/>
			<xsl:with-param name ="name"  select="'NL_PLAIN'"/>
		</xsl:call-template>
		<option value="html" selected="true">html</option>
		<option value="plain"><xsl:value-of select="java:getString($dict_newsletter_module, 'NL_PLAIN')"/></option>
	</select>
</xsl:template>

<xsl:template name="nl_sel_group_section">
	<!-- Current tag is negeso:newsletter -->
	<xsl:if test="count(negeso:newsletter-groups/negeso:newsletter-group) &gt; 0">
	<table class="b-nlTable" cellspacing="0" cellpadding="0">
        <cols>
            <col width="170"/>
            <col width="*"/>
        </cols>
		<xsl:apply-templates select="negeso:newsletter-groups/negeso:newsletter-group" mode="simpleMode"/>
		<tr>
			<td colspan="2">				
				<xsl:call-template name="nl_button_section" />
			</td>
		</tr>
	</table>
	</xsl:if>
</xsl:template>

<xsl:template name="nl_button_section">
	<!-- Current tag is negeso:newsletter -->
	<div class="btn">
	    <button class="submit" onClick="return submitSubscribeForm()">
		    <xsl:call-template name ="add-constant-info">
			    <xsl:with-param name ="dict"  select="$dict_newsletter_module"/>
			    <xsl:with-param name ="name"  select="'NL_SUBSCRIBE'"/>
		    </xsl:call-template>
		    <xsl:value-of select="java:getString($dict_newsletter_module, 'NL_SUBSCRIBE')"/>
	    </button>
    </div>
	    <div class="btn">
	    <button class="submit" onClick="return submitUnsubscribeForm()">
		    <xsl:call-template name ="add-constant-info">
			    <xsl:with-param name ="dict"  select="$dict_newsletter_module"/>
			    <xsl:with-param name ="name"  select="'NL_UNSUBSCRIBE'"/>
		    </xsl:call-template>
	       <xsl:value-of select="java:getString($dict_newsletter_module, 'NL_UNSUBSCRIBE')"/>
	    </button>
    </div>
    <div class="btn lastBtn">
	    <button type="button" class="submit" onClick="javascript:window.location = '?action=archive'">
		    <xsl:call-template name ="add-constant-info">
			    <xsl:with-param name ="dict"  select="$dict_newsletter_module"/>
			    <xsl:with-param name ="name"  select="'NL_OPEN_ARCHIVE'"/>
		    </xsl:call-template>
	       <xsl:value-of select="java:getString($dict_newsletter_module, 'NL_OPEN_ARCHIVE')"/>
	    </button>
    </div>
</xsl:template>

<xsl:template match="negeso:newsletter-message" mode="simpleMode">
	<xsl:choose>
		<xsl:when test="text() = 'SUBSCRIPTION_NOT_SENT_MAIL_UNREACHABLE'" >
            <h1>
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_newsletter_module"/>
					<xsl:with-param name ="name"  select="'NL_ERROR'"/>
				</xsl:call-template>
				<xsl:value-of select="java:getString($dict_newsletter_module, 'NL_ERROR')"/>
            </h1>
			<xsl:call-template name="nl_white_area">
				<xsl:with-param name="button_back" select="'yes'"/>
				<xsl:with-param name="body" select="'NL_SUBSCR_MAIL_UNREACHABLE'" />
			</xsl:call-template>
		</xsl:when>

		<xsl:when test="text() = 'NO_GROUP_CHOSEN'" >
            <xsl:call-template name="page_title_line_block">
                <xsl:with-param name="width" select="'100%'"/>
                <xsl:with-param name="title">
                    <xsl:value-of select="java:getString($dict_newsletter_module, 'NL_ERROR')"/>
                </xsl:with-param>
            </xsl:call-template>
			<xsl:call-template name="nl_white_area">
				<xsl:with-param name="button_back" select="'yes'"/>
                <xsl:with-param name="body" select="'NL_NO_GROUP_CHOSEN'"/>                
			</xsl:call-template>
		</xsl:when>

		<xsl:when test="text() = 'UNSUBSCRIBE_CONFIRMED'">
            <h1>
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_newsletter_module"/>
					<xsl:with-param name ="name"  select="'NL_INFORMATION'"/>
				</xsl:call-template>
				<xsl:value-of select="java:getString($dict_newsletter_module, 'NL_INFORMATION')"/>
            </h1>
			<xsl:call-template name="nl_white_area">
                <xsl:with-param name="body" select="'NL_UNSUBSCR_REQUEST_CONFIRMED'"/>
			</xsl:call-template>
            <br/>
            <br/>
            <xsl:call-template name="nl_back_link" />
		</xsl:when>
        
		<xsl:when test="text() = 'BAD_SUBSCRIBER_ID'">
           <h1>
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_newsletter_module"/>
					<xsl:with-param name ="name"  select="'NL_ERROR'"/>
				</xsl:call-template>
				<xsl:value-of select="java:getString($dict_newsletter_module, 'NL_ERROR')"/>
            </h1>
			<xsl:call-template name="nl_white_area">
                <xsl:with-param name="body" select="'NL_BAD_SUBSCRIBER_ID'" />
			</xsl:call-template>
		</xsl:when>
        
		<xsl:when test="text() = 'SUBSCRIPTION_CONFIRMED'">
            <h1>
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_newsletter_module"/>
					<xsl:with-param name ="name"  select="'NL_INFORMATION'"/>
				</xsl:call-template>
				<xsl:value-of select="java:getString($dict_newsletter_module, 'NL_INFORMATION')"/>
            </h1>
			<xsl:call-template name="nl_white_area">
				<xsl:with-param name="body" select="'NL_SUBSCR_REQUEST_CONFIRMED'"/>
			</xsl:call-template>
            <br/>
            <br/>
            <xsl:call-template name="nl_back_link" />
        </xsl:when>
        
		<xsl:when test="text() = 'SUBSCRIBER_NOT_FOUND'">
            <h1>
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_newsletter_module"/>
					<xsl:with-param name ="name"  select="'NL_INFORMATION'"/>
				</xsl:call-template>
				<xsl:value-of select="java:getString($dict_newsletter_module, 'NL_INFORMATION')"/>
            </h1>
			<xsl:call-template name="nl_white_area">
				<xsl:with-param name="button_back" select="'yes'"/>
				<xsl:with-param name="body" select="'NL_SUBSCRIBER_NOT_FOUND'" />
			</xsl:call-template>
		</xsl:when>

		<xsl:when test="text() = 'SUBSCRIBER_ALREADY_EXISTS'">
            <h1>
                <xsl:call-template name ="add-constant-info">
				    <xsl:with-param name ="dict"  select="$dict_newsletter_module"/>
                    <xsl:with-param name ="name"  select="'NL_SUBSCRIBER_ALREADY_EXISTS'"/>
                </xsl:call-template>
                <xsl:value-of select="java:getString($dict_newsletter_module, 'NL_SUBSCRIBER_ALREADY_EXISTS')"/>
            </h1>
			<xsl:call-template name="nl_white_area">
				<xsl:with-param name="button_back" select="'yes'"/>
                <xsl:with-param name="body" select="'NL_SUBSCRIBER_ALREADY_EXISTS'" />
			</xsl:call-template>
		</xsl:when>

		<xsl:when test="text() = 'MAX_NUMBER_SUBSCRIBER'">
            <h1>
                <xsl:call-template name ="add-constant-info">
				    <xsl:with-param name ="dict"  select="$admin_dict_newsletter_module"/>
                    <xsl:with-param name ="name"  select="'NL_MAX_NUMBER_SUBSCRIBER'"/>
			    </xsl:call-template>
                <xsl:value-of select="java:getString($admin_dict_newsletter_module, 'NL_MAX_NUMBER_SUBSCRIBER')"/>
            </h1>
			<xsl:call-template name="nl_white_area">
				<xsl:with-param name="button_back" select="'yes'"/>
                <xsl:with-param name="body" select="'NL_MAX_NUMBER_SUBSCRIBER'" />
			</xsl:call-template>
		</xsl:when>

		<xsl:when test="@status='success'">
            <h1>
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_newsletter_module"/>
					<xsl:with-param name ="name"  select="'NL_INFORMATION'"/>
				</xsl:call-template>
				<xsl:value-of select="java:getString($dict_newsletter_module, 'NL_INFORMATION')"/>
            </h1>
            
			<xsl:call-template name ="add-constant-info">
				<xsl:with-param name ="dict"  select="$dict_newsletter_module"/>
				<xsl:with-param name ="name"  select="'NL_SUBSCR_SENT'"/>
			</xsl:call-template>            
			<xsl:value-of select="java:getString($dict_newsletter_module, 'NL_SUBSCR_SENT')" disable-output-escaping="yes"/>&#160;<a href="mailto:{@email}">
			<xsl:value-of select="@email" />
			</a>
            <br/>
            <br/>
            <xsl:call-template name="nl_back_link" />
		</xsl:when>
	</xsl:choose>
    
</xsl:template>

<xsl:template name="nl_back_link">
	<button class="submit" onclick="javascript:history.back();">
		<xsl:call-template name ="add-constant-info">
			<xsl:with-param name ="dict"  select="$dict_newsletter_module"/>
			<xsl:with-param name ="name"  select="'NL_BACK'"/>
		</xsl:call-template>
		<xsl:value-of select="java:getString($dict_newsletter_module, 'NL_BACK')" />
    </button>
</xsl:template>

<xsl:template match="negeso:newsletter-group" mode="simpleMode">
	<tr>
		<th class="categorie_column">
			<input type="checkbox" name="group_{@id}" value="{@id}" class="nl_mr10 form_checkbox" />
			<xsl:value-of select="@title"/>
		</th>
		<td class="left">
			<xsl:value-of select="@description"/>
		</td>
	</tr>
</xsl:template>

<xsl:template match="negeso:languages" mode="simpleMode">
	<select name="input_languageId" class="nl_select">
		<xsl:apply-templates select="negeso:language" mode="simpleMode"/>
	</select>
</xsl:template>

<xsl:template match="negeso:language" mode="simpleMode">
	<option value="{@id}">
		<xsl:attribute name="value"><xsl:value-of select="@id"/></xsl:attribute>
		<xsl:if test="@code=/negeso:page/@lang">
			<xsl:attribute name="selected">true</xsl:attribute>
		</xsl:if>
		<xsl:value-of select="@code"/>
	</option>
</xsl:template>

<!--===================================== NEWSLETTER MODULE END ======================================-->

</xsl:stylesheet>
