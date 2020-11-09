<?xml version="1.0" encoding="UTF-8"?>
<!--
  Copyright (c) 2007 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information"). You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.


  XSL templates for Site Webpoll
 
  @version		2007.12.04
  @author		Oleg 'germes' Shvedov
  @author		Rostislav 'KOTT' Brizgunov
-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:xlink="http://www.w3.org/1999/xlink"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">


<xsl:variable name="dict_webpoll_module" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('web_poll', $lang)"/>

<!--========================================== POLL BEGIN===========================================-->

<xsl:template match="negeso:list[@type='webpoll' or @type='webpollresult']" mode="page_head">
	<link rel="stylesheet" type="text/css" href="/site/modules/webpoll/css/webpoll.css"/>
	<script type="text/javascript" src="/site/modules/webpoll/script/webpoll_cookie.js">/**/</script>
</xsl:template>

<!-- Webpoll form -->
<xsl:template match="negeso:list" mode="webpoll">
	
  	<div class="pollMargin">
  		<!-- Webpoll image: i18n included -->
  		<img src="/site/modules/webpoll/images/poll_{$lang}.gif" width="60" height="31" alt="" />
		<!-- Webpoll question: from Article -->
		<xsl:choose>
			<xsl:when test="negeso:listItem"><xsl:apply-templates select="negeso:listItem[negeso:list]" mode="webpoll_question" /></xsl:when>
			<xsl:otherwise>&#160;</xsl:otherwise>
		</xsl:choose>
	</div>
	<!-- Webpoll voting form -->
	<form action="" method="GET" id="voteForm">
		<table cellspacing="0" cellpadding="0" border="0">
			<!-- Vote values -->
			<xsl:apply-templates select="negeso:listItem/negeso:list" mode="pollfirst">
				<xsl:with-param name="disabled" select="@disabled"/>
			</xsl:apply-templates>
			<!-- Submit button and links -->
			<tr>
				<td class="pollSubmitTd left">
					<xsl:if test="@type='webpoll' and @selected">
						<a href="index_{$lang}.html?pollResultId={negeso:listItem[negeso:list/negeso:listItem]/@id}">
							<xsl:call-template name ="add-constant-info">
								<xsl:with-param name ="dict"  select="$dict_webpoll_module"/>
								<xsl:with-param name ="name"  select="'WP_VIEW_RESULTS'"/>
							</xsl:call-template>
							<xsl:value-of select="java:getString($dict_webpoll_module, 'WP_VIEW_RESULTS')"/>
						</a>
					</xsl:if>
					<xsl:if test="@type='webpoll' and not(@selected)">
						<span>
							<xsl:call-template name ="add-constant-info">
								<xsl:with-param name ="dict"  select="$dict_webpoll_module"/>
								<xsl:with-param name ="name"  select="'WP_ABSENT'"/>
							</xsl:call-template>
							<xsl:value-of select="java:getString($dict_webpoll_module, 'WP_ABSENT')"/>
						</span>
					</xsl:if>
				</td>
				<td class="pollSubmitTd right">
					<input name="pollResultId" type="hidden" value="{negeso:listItem[negeso:list/negeso:listItem]/@id}"/>
                   	<xsl:choose>
                   		<xsl:when test="@disabled = 'true'">
							<span>
								<xsl:call-template name ="add-constant-info">
									<xsl:with-param name ="dict"  select="$dict_webpoll_module"/>
									<xsl:with-param name ="name"  select="'WP_ALREADY_VOTED'"/>
								</xsl:call-template>

								<xsl:value-of select="java:getString($dict_webpoll_module, 'WP_ALREADY_VOTED')"/>
							</span>
                   		</xsl:when>
                   		<xsl:otherwise>
                   			<input name="submitButton" class="submit" type="submit" disabled="true">
								<xsl:call-template name ="add-constant-info">
									<xsl:with-param name ="dict"  select="$dict_webpoll_module"/>
									<xsl:with-param name ="name"  select="'WP_SEND'"/>
								</xsl:call-template>
                   				<xsl:attribute name="value">
									<xsl:value-of select="java:getString($dict_webpoll_module, 'WP_SEND')"/>
								</xsl:attribute>
                   			</input>
                   		</xsl:otherwise>
                   	</xsl:choose>
				</td>
			</tr>
		</table>
	</form>
	
	<!-- *************** Only for admin - do not change - begin ******************** -->
	<xsl:if test="$outputType = 'admin'">
		<div style="position: absolute; z-index: 10; left: -10px; top: -2px; text-align: left">
			<xsl:apply-templates select="self::node()" mode="admin_part_entry_point">
				<xsl:with-param name="type" select="2"/>
				<xsl:with-param name="listPath" select="1"/>
			</xsl:apply-templates>
		</div>
	</xsl:if>
	<!-- *************** Only for admin - do not change - end ******************** -->
	
</xsl:template>

<xsl:template match="negeso:listItem" mode="webpoll_question">
    <xsl:value-of select="negeso:teaser/negeso:article/negeso:text" disable-output-escaping="yes"/>
</xsl:template>

<xsl:template match="negeso:list" mode="pollfirst">
    <xsl:param name="disabled" select="false"/>
    <xsl:apply-templates select="negeso:listItem" mode="pollfirst">
        <xsl:with-param name="disabled" select="$disabled"/>
    </xsl:apply-templates>
</xsl:template>

<xsl:template match="negeso:listItem" mode="pollfirst">
	<xsl:param name="disabled" select="false"/>
	<tr>
		<td class="answerItem left">
			<label class="answerLabel">
				<xsl:value-of select="@title" disable-output-escaping="yes"/>
			</label>
		</td>
		<td class="answerItem right">
			<input type="radio" class="form_radio" name="optionId" value="{@id}" onClick="submitButton.disabled=false">
				<xsl:if test="$disabled = 'true'">
					<xsl:attribute name="onClick"></xsl:attribute>
				</xsl:if>
			</input>
		</td>
	</tr>
</xsl:template>

<xsl:template match="negeso:list" mode="webpoll_result">
	<div class="mainContentTop">
		<img src="/site/modules/webpoll/images/poll_{$lang}.gif" width="60" height="31" alt="Poll" border="0" vspace="0" hspace="0" />
		<xsl:choose>
			<xsl:when test="@accepted='false'">
				<div class="PollResQuestion">
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_webpoll_module"/>
						<xsl:with-param name ="name"  select="'WP_ALREADY_VOTED'"/>
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_webpoll_module, 'WP_ALREADY_VOTED')"/>
				</div>
			</xsl:when>
			<xsl:when test="number(@totalVotes) &lt; 5">
				<div class="PollResQuestion">
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_webpoll_module"/>
						<xsl:with-param name ="name"  select="'WP_TOO_LITTLE_VOTED'"/>
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_webpoll_module, 'WP_TOO_LITTLE_VOTED')"/>
				</div>
			</xsl:when>
			<xsl:otherwise>
				<div class="PollResQuestion">
					<xsl:value-of select="negeso:listItem[negeso:list]/@title"/>
				</div>
				<xsl:if test="negeso:listItem">
					<div class="PollResQuestion">
						<xsl:apply-templates select="negeso:listItem[negeso:list]" mode="webpoll_question" />
					</div>
				</xsl:if>
				<xsl:choose>
					<xsl:when test="negeso:listItem/negeso:list/negeso:listItem">
						<xsl:apply-templates select="negeso:listItem/negeso:list" mode="pollLines"/>
					</xsl:when>
					<xsl:otherwise>&#160;</xsl:otherwise>
				</xsl:choose>
				<table cellspacing="0" cellpadding="0" class="PollResultLine">
					<tr>
						<td width="80%" class="PollOther PollTotal">
							<span>
								<xsl:call-template name ="add-constant-info">
									<xsl:with-param name ="dict"  select="$dict_webpoll_module"/>
									<xsl:with-param name ="name"  select="'WP_TOTALLY'"/>
								</xsl:call-template>
							<xsl:value-of select="java:getString($dict_webpoll_module, 'WP_TOTALLY')"/>
							</span>
							:&#160;<xsl:value-of select="@totalVotes" />
							&#160;
							<span>
								<xsl:call-template name ="add-constant-info">
									<xsl:with-param name ="dict"  select="$dict_webpoll_module"/>
									<xsl:with-param name ="name"  select="'WP_VOTES'"/>
								</xsl:call-template>
								<xsl:value-of select="java:getString($dict_webpoll_module, 'WP_VOTES')"/>
							</span>
								&#160;
						</td>
					</tr>
				</table>
				<div class="PollText">
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_webpoll_module"/>
						<xsl:with-param name ="name"  select="'WP_HAVE_NO_STATISTICS'"/>
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_webpoll_module, 'WP_HAVE_NO_STATISTICS')"/>
				</div>
			</xsl:otherwise>
		</xsl:choose>
	</div>
</xsl:template>

<xsl:template match="negeso:list" mode="pollLines">
        <xsl:apply-templates select="negeso:listItem" mode="pollLines"/>
</xsl:template>

<xsl:template match="negeso:listItem" mode="pollLines">
        <table class="PollResultLine" width="100%" height="20" border="0" cellpadding="0" cellspacing="0">
            <tr class="pollAnswers">
                <td class="PollOther" width="{100 - number(@rating)}%">
                        <xsl:value-of select="@title" disable-output-escaping="yes"/>
                </td>
                <td class="PollYes" width="{@rating}%">
                    <xsl:choose>
                        <xsl:when test="count(preceding-sibling::negeso:listItem) = 1">
                            <xsl:attribute name="class">PollNo</xsl:attribute>
                        </xsl:when>
                        <xsl:when test="count(preceding-sibling::negeso:listItem) = 2">
                            <xsl:attribute name="class">PollNeutral</xsl:attribute>
                        </xsl:when>
                    </xsl:choose>
                    <img src="/site/modules/webpoll/images/space.gif" width="1" height="20" alt="Yes" border="0" vspace="0" hspace="0" />
                </td>
            </tr>
        </table>
        <div class="PollYesNumbers">
		<xsl:choose>
			<xsl:when test="count(preceding-sibling::negeso:listItem) = 1">
				<xsl:attribute name="class">PollNoNumbers</xsl:attribute>
			</xsl:when>
			<xsl:when test="count(preceding-sibling::negeso:listItem) = 2">
				<xsl:attribute name="class">PollNeutralNumbers</xsl:attribute>
			</xsl:when>
		</xsl:choose>
		<xsl:value-of select="@rating" />
		<xsl:text>%&#160;&#160;</xsl:text>
		<xsl:value-of select="@votes" />&#160;
			<span>
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_webpoll_module"/>
					<xsl:with-param name ="name"  select="'WP_VOTES'"/>
				</xsl:call-template>
				<xsl:value-of select="java:getString($dict_webpoll_module, 'WP_VOTES')"/>
			</span>
	
	</div>
</xsl:template>

<!--========================================== POLL END===========================================-->

</xsl:stylesheet>