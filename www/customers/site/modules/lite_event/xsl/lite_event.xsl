<?xml version="1.0" encoding="UTF-8"?>
<!--
  Copyright (c) 2008 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information"). You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.


  XSL templates for Lite Event components:
  	* Event View Component
  	* Nearest Events Component
  	* Event Calendar Component
 
  @version		2008.01.25
  @author		Rostislav 'KOTT' Brizgunov
  @author		Oleg 'germes' Shvedov
-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">

<xsl:variable name="dict_event_module" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('lite_event', $lang)"/>

<xsl:template match="negeso:EventViewComponent" mode="page_head">
	<xsl:call-template name="em_page_head" />
</xsl:template>

<xsl:template match="negeso:NearEventsComponent" mode="page_head">
	<xsl:call-template name="em_page_head" />
</xsl:template>

<xsl:template match="negeso:EventCalendarsComponent" mode="page_head">
	<!-- Redirection for Lite Event Calendar -->
	<script type="text/javascript">
		if (!/startDate=\d{4}-\d{1,2}-\d{1,2}/gi.test(window.location.href) ||
			!/period=(month|year|day|week)/gi.test(window.location.href)
		) {
			var today = new Date();
			var adds = "startDate="+today.getFullYear()+"-"+(today.getMonth()+1)+"-"+today.getDate()+"&amp;period=month";
			if (window.location.href.indexOf("?") == -1) adds = "?" + adds;
			else adds = "&amp;" + adds;
			window.location.href += adds;
		}
	</script>
	<xsl:call-template name="em_page_head" />
</xsl:template>

<xsl:template name="em_page_head">
	<link rel="stylesheet" type="text/css" href="/site/modules/lite_event/css/lite_event.css" />
</xsl:template>

<!-- Main template for Lite Event View Component -->
<xsl:template match="negeso:EventViewComponent" mode="em">
								<!-- Module title line text -->
								<xsl:if	test="$outputType =	'admin' and not(/negeso:page/@role-id = 'visitor')">
									<img alt="" align="absMiddle" class="hand" src="/images/mark_1.gif" onClick="window.open('/admin/categorieslist.html', 'modules', 'top=50,left=50,height=600,width=800,status=0,toolbar=0,menubar=0,location=0,resizable=1,scrollbars=yes')"/>
									<xsl:text>&#160;</xsl:text>
								</xsl:if>
    <h1>
								<xsl:choose>
									<xsl:when test="@view='eventDetails'">
										<span>
											<xsl:call-template name ="add-constant-info">
												<xsl:with-param name ="dict"  select="$dict_event_module"/>
												<xsl:with-param name ="name"  select="'EM_EVENT_DETAILS'"/>
											</xsl:call-template>
											<xsl:value-of select="java:getString($dict_event_module, 'EM_EVENT_DETAILS')"/>
										</span>
										<xsl:call-template name="em_details_messages" />
									</xsl:when>
									<xsl:otherwise>
										<span>
											<xsl:call-template name ="add-constant-info">
												<xsl:with-param name ="dict"  select="$dict_event_module"/>
												<xsl:with-param name ="name"  select="'EM_TITLE'"/>
											</xsl:call-template>
											<xsl:value-of select="java:getString($dict_event_module, 'EM_TITLE')"/>
										</span>
									</xsl:otherwise>
								</xsl:choose>
    </h1>
								<xsl:choose>
									<xsl:when test="@view='eventDetails'">
										<xsl:apply-templates select="negeso:Event" mode="em_details"/>
									</xsl:when>
									<xsl:otherwise>
            <div class="bl-left">
													<xsl:call-template name="em_categories" />
            </div>
            <div class="bl-right">
													<xsl:apply-templates select="." mode="em_events_list"/>
													<!-- Next template may be used as separated -->
													<xsl:apply-templates select="../negeso:NearEventsComponent" mode="em_events_list" />
            </div>
									</xsl:otherwise>
								</xsl:choose>
</xsl:template>

<!-- ERROR and SUCCESS messages for Event Details page -->
<xsl:template name="em_details_messages">
	<!-- Current node: negeso:EventViewComponent -->
	<xsl:choose>
		<xsl:when test="@status = 'systemUser'">
			<xsl:text>:&#160;</xsl:text>
			<span class="red bold">
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_event_module"/>
					<xsl:with-param name ="name"  select="'EM_SYSTEM_USER_ERROR'"/>
				</xsl:call-template>
				<xsl:value-of select="java:getString($dict_event_module, 'EM_SYSTEM_USER_ERROR')"/>
			</span>
		</xsl:when>		
		<xsl:when test="@status = 'error'">
			<xsl:text>:&#160;</xsl:text>
			<span class="red bold">
				<!-- OLD ERROR MESSAGE: -->
				<!--<xsl:value-of select="java:getString($dict_event_module, 'EM_UNKNOWN_ERROR')"/>-->
				<xsl:value-of select="negeso:Event/@errorDetails"/>
			</span>
		</xsl:when>
		<xsl:when test="@status = 'subscribed'">
			<xsl:text>:&#160;</xsl:text>
			<span class="bold dark_blue">
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_event_module"/>
					<xsl:with-param name ="name"  select="'EM_SUBSCRIBE_SUCCESS'"/>
				</xsl:call-template>
				<xsl:value-of select="java:getString($dict_event_module, 'EM_SUBSCRIBE_SUCCESS')"/></span>
		</xsl:when>
		<xsl:when test="@status = 'unauthorizedVisitor'">
			<xsl:text>:&#160;</xsl:text>
			<span class="red">
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_event_module"/>
					<xsl:with-param name ="name"  select="'EM_ONLY_FOR_REGISTERED'"/>
				</xsl:call-template>
				<xsl:value-of select="java:getString($dict_event_module, 'EM_ONLY_FOR_REGISTERED')"/>.
				<a href="login.html" class="red_link bold">
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_event_module"/>
						<xsl:with-param name ="name"  select="'EM_PLEASE_REGISTER'"/>
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_event_module, 'EM_PLEASE_REGISTER')"/>
				</a>
			</span>
		</xsl:when>
	</xsl:choose>
</xsl:template>

<!-- Event details: for Event List and Event Calendar -->
<xsl:template match="negeso:Event" mode="em_details">
	<form name="eventSubscribeForm" method="get">

		<input type="hidden" name="id" value="{@id}" />
		<input type="hidden" name="view" value="eventDetails" />

		<!-- Case of event details - from calendar -->
		<xsl:if test="$class='lite-event-calendar'">
			<input type="hidden" name="startDate" value="{$param[@name='startDate']/negeso:value/text()}" />
			<input type="hidden" name="period" value="{$param[@name='period']/negeso:value/text()}" />
		</xsl:if>

			<xsl:for-each select="negeso:FieldList">
				<xsl:for-each select="negeso:Field">
                <h2><xsl:value-of select="@title" />:&#160;</h2>
                <div>
							<xsl:choose>
                        <xsl:when test="@value"><xsl:value-of select="@value" /></xsl:when>
								<xsl:when test="negeso:article">
									<xsl:apply-templates select="negeso:article">
										<xsl:with-param name="mode" select="'user'" />
									</xsl:apply-templates>
								</xsl:when>
							</xsl:choose>
                </div>
				</xsl:for-each>
			</xsl:for-each>
        <h2>
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_event_module"/>
						<xsl:with-param name ="name"  select="'EM_EVENT_DATE'"/>
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_event_module, 'EM_EVENT_DATE')" />:&#160;
        </h2>
        <div><xsl:call-template name="em_formatted_date" /></div>
        <div>
            <div class="bl-left">
					<input type="button" class="submit" onclick="if (validate(this.form)) this.form.submit()" value="{java:getString($dict_event_module, 'EM_SUBSCRIBE_TO_EVENT')}"/>
            </div>
            <div class="bl-left">
					<input type="text" class="width_30" name="amount" value="1" numeric_field_params="na;0;1;na" /> (<xsl:value-of select="java:getString($dict_event_module, 'EM_SUBSCRIBE_FIELD_DESCR')" />)
            </div>
        </div>
        <div>
					<xsl:choose>
						<xsl:when test="position()=last()">
							<a href="?view=eventList">
								<xsl:call-template name ="add-constant-info">
									<xsl:with-param name ="dict"  select="$dict_event_module"/>
									<xsl:with-param name ="name"  select="'EM_BACK'"/>
								</xsl:call-template>
								<xsl:if test="$class='lite-event-calendar'">
									<xsl:attribute name="href">?startDate=<xsl:value-of select="$param[@name='startDate']/negeso:value/text()" />&amp;period=month</xsl:attribute>
								</xsl:if>
								&lt;&lt;&#160;<xsl:value-of select="java:getString($dict_event_module, 'EM_BACK')"/>
							</a>
						</xsl:when>
                <xsl:otherwise>&#160;</xsl:otherwise>
					</xsl:choose>
        </div>
	</form>
</xsl:template>

<!-- Category navigator (list) -->
<xsl:template name="em_categories">
	<!-- Current node is negeso:EventViewComponent -->
		<xsl:apply-templates select="negeso:Category" mode="em_category_browser" />
</xsl:template>

<xsl:variable name="emCatId" select="$param[@name='catId']/negeso:value" />

<xsl:template match="negeso:Category" mode="em_category_browser">
	<xsl:param name="paddingLeft" select="10" />
    <div style="padding-left:{$paddingLeft}px;">
			<xsl:choose>
				<xsl:when test="ancestor::negeso:Category and not(descendant-or-self::negeso:Category[@id=$emCatId])">
					<xsl:attribute name="class">emCatLev2</xsl:attribute>
				</xsl:when>
				<xsl:when test="ancestor::negeso:Category and descendant-or-self::negeso:Category[@id=$emCatId]">
					<xsl:attribute name="class">emCatActiveSub emCatLev2</xsl:attribute>
				</xsl:when>
				<xsl:when test="descendant-or-self::negeso:Category[@id=$emCatId]">
					<xsl:attribute name="class">emCatActive</xsl:attribute>
				</xsl:when>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="negeso:EventList/negeso:Event">
                <a href="?catId={@id}">
                    <xsl:value-of select="@title" />
                    <span class="red bold">&#160;&#187;</span>
                </a>
				</xsl:when>
				<xsl:otherwise>
					<span><xsl:value-of select="@title" /></span>
				</xsl:otherwise>
			</xsl:choose>
    </div>

	<xsl:apply-templates select="negeso:Category" mode="em_category_browser">
		<xsl:with-param name ="paddingLeft">
			<xsl:choose>
                <xsl:when test="ancestor::negeso:Category">
                    <xsl:value-of select="number($paddingLeft)+15" />
                </xsl:when>
				<xsl:otherwise>10</xsl:otherwise>
			</xsl:choose>
		</xsl:with-param>
	</xsl:apply-templates>
	
</xsl:template>

<!-- Event list of selected category -->
<xsl:template match="negeso:EventViewComponent" mode="em_events_list">
    <h2>
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_event_module"/>
					<xsl:with-param name ="name"  select="'EM_EVENTS'"/>
				</xsl:call-template>
				<xsl:value-of select="java:getString($dict_event_module, 'EM_EVENTS')"/>
				<xsl:if test="$emCatId and descendant::negeso:Category[@id=$emCatId]">
				<xsl:text>&#160;</xsl:text>
				(<xsl:value-of select="descendant::negeso:Category[@id=$emCatId]/@title"/>)
				</xsl:if>
    </h2>
		<xsl:choose>
			<xsl:when test="$emCatId and descendant::negeso:Category[@id=$emCatId]/negeso:EventList/negeso:Event">
            <div>
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_event_module"/>
							<xsl:with-param name ="name"  select="'EM_DATE'"/>
						</xsl:call-template>
						<xsl:value-of select="java:getString($dict_event_module, 'EM_DATE')"/>
            </div>
            <div>
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_event_module"/>
							<xsl:with-param name ="name"  select="'EM_SHORT_DESCRIPTION'"/>
						</xsl:call-template>
						<xsl:value-of select="java:getString($dict_event_module, 'EM_SHORT_DESCRIPTION')"/>
            </div>
            <div>&#160;</div>
				<xsl:apply-templates select="descendant::negeso:Category[@id=$emCatId]/negeso:EventList/negeso:Event" mode="em_events_list" />
			</xsl:when>
			<xsl:otherwise>
            <div>
						<xsl:value-of select="java:getString($dict_event_module, 'EM_NO_EVENTS')"/>
            </div>
			</xsl:otherwise>
		</xsl:choose>
</xsl:template>

<!-- Event list of nearest events -->
<xsl:template match="negeso:NearEventsComponent" mode="em">
	<xsl:apply-templates select="." mode="em_events_list" />
</xsl:template>

<xsl:template match="negeso:NearEventsComponent" mode="em_events_list">
    <h2>
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_event_module"/>
					<xsl:with-param name ="name"  select="'EM_LAST_EVENTS'"/>
				</xsl:call-template>
				<xsl:value-of select="java:getString($dict_event_module, 'EM_LAST_EVENTS')"/>
    </h2>
		<xsl:choose>
			<xsl:when test="negeso:Event">
            <div>
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_event_module"/>
							<xsl:with-param name ="name"  select="'EM_DATE'"/>
						</xsl:call-template>
						<xsl:value-of select="java:getString($dict_event_module, 'EM_DATE')"/>
            </div>
            <div>
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_event_module"/>
							<xsl:with-param name ="name"  select="'EM_SHORT_DESCRIPTION'"/>
						</xsl:call-template>
						<xsl:value-of select="java:getString($dict_event_module, 'EM_SHORT_DESCRIPTION')"/>
            </div>
            <div>&#160;</div>
				<xsl:apply-templates select="negeso:Event[@current]" mode="em_events_list" />
				<xsl:apply-templates select="negeso:Event[not(@current)]" mode="em_events_list" />
			</xsl:when>
			<xsl:otherwise>
            <div>
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_event_module"/>
							<xsl:with-param name ="name"  select="'EM_NO_EVENTS'"/>
						</xsl:call-template>
						<xsl:value-of select="java:getString($dict_event_module, 'EM_NO_EVENTS')"/>
            </div>
			</xsl:otherwise>
		</xsl:choose>
</xsl:template>

<xsl:template match="negeso:Event" mode="em_events_list">
    <div><xsl:call-template name="em_formatted_date" /></div>
    <div><xsl:call-template name="em_short_description" />    </div>
    <div>
			<a href="?view=eventDetails&amp;id={@id}">
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_event_module"/>
					<xsl:with-param name ="name"  select="'EM_MORE'"/>
				</xsl:call-template>
            <xsl:value-of select="java:getString($dict_event_module, 'EM_MORE')"/>&#187;
        </a>&#160;
    </div>
</xsl:template>

<xsl:template name="em_formatted_date">
	<xsl:value-of select="negeso:Date/@dayOfWeek" />, <xsl:value-of select="negeso:Date/@day" />-<xsl:value-of select="negeso:Date/@month" />-<xsl:value-of select="negeso:Date/@year" />
</xsl:template>

<xsl:template name="em_short_description">
	<xsl:choose>
		<xsl:when test="string-length(negeso:FieldList/negeso:Field[@order = 1]/@value) &gt; 50">
			<xsl:value-of select="substring(negeso:FieldList/negeso:Field[@order = 1]/@value,0,50)" />...
		</xsl:when>
		<xsl:when test="string-length(negeso:FieldList/negeso:Field[@order = 1]/@value) &gt; 0">
			<xsl:value-of select="negeso:FieldList/negeso:Field[@order = 1]/@value" />
		</xsl:when>
		<xsl:when test="string-length(negeso:FieldList/negeso:Field[@order = 1]/negeso:article/negeso:text/text()) &gt; 50">
			<xsl:value-of select="substring(negeso:FieldList/negeso:Field[@order = 1]/negeso:article/negeso:text/text(),0,50)" />...
		</xsl:when>
		<xsl:when test="string-length(negeso:FieldList/negeso:Field[@order = 1]/negeso:article/negeso:text/text()) &gt; 0">
			<xsl:value-of select="negeso:FieldList/negeso:Field[@order = 1]/negeso:article/negeso:text/text()" />
		</xsl:when>
		<xsl:otherwise>&#160;</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<!--===================== Events calendar =====================-->
<xsl:template match="negeso:EventCalendarsComponent" mode="em">

	<xsl:variable name="em_current_day" select="negeso:Month/negeso:Day/negeso:Event/negeso:Date/@day" />
	<xsl:variable name="em_current_month" select="negeso:Month/negeso:Day/negeso:Event/negeso:Date/@month" />
	<xsl:variable name="em_current_year" select="negeso:Month/negeso:Day/negeso:Event/negeso:Date/@year" />
						<!-- If we are processing Calendar, not Calendar Event Datails, we are making it narrow -->
								<!-- Module title line text -->
								<xsl:if	test="$outputType =	'admin' and not(/negeso:page/@role-id = 'visitor')">
									<img alt="" align="absMiddle" class="hand" src="/images/mark_1.gif" onClick="window.open('/admin/categorieslist.html', 'modules', 'top=50,left=50,height=600,width=800,status=0,toolbar=0,menubar=0,location=0,resizable=1,scrollbars=yes')"/>
									<xsl:text>&#160;</xsl:text>
								</xsl:if>
    <h2>
								<xsl:choose>
									<xsl:when test="count(negeso:Month/negeso:Day) &gt; 1">
										<span>
											<xsl:call-template name ="add-constant-info">
												<xsl:with-param name ="dict"  select="$dict_event_module"/>
												<xsl:with-param name ="name"  select="'EM_TITLE'"/>
											</xsl:call-template>
											<xsl:value-of select="java:getString($dict_event_module, 'EM_TITLE')"/>
										</span>

									</xsl:when>
									<xsl:otherwise>
										<span>
											<xsl:call-template name ="add-constant-info">
												<xsl:with-param name ="dict"  select="$dict_event_module"/>
												<xsl:with-param name ="name"  select="'EM_EVENT_DETAILS'"/>
											</xsl:call-template>
											<xsl:value-of select="java:getString($dict_event_module, 'EM_EVENT_DETAILS')"/>
										</span>
										<xsl:for-each select="//negeso:EventViewComponent[negeso:Event/negeso:Date[@day=$em_current_day and @month=$em_current_month and @year=$em_current_year]]">
											<xsl:call-template name="em_details_messages" />
										</xsl:for-each>
									</xsl:otherwise>
								</xsl:choose>
    </h2>
								<xsl:choose>
									<xsl:when test="count(negeso:Month/negeso:Day) &gt; 1">
										<xsl:call-template name="em_calendar_table"/>
									</xsl:when>
									<xsl:otherwise>
										<!--<xsl:apply-templates select="negeso:Month/negeso:Day/negeso:Event" mode="em_details" />-->
										<xsl:apply-templates select="//negeso:EventViewComponent//negeso:Event[negeso:Date[@day=$em_current_day and @month=$em_current_month and @year=$em_current_year]]" mode="em_details" />
									</xsl:otherwise>
								</xsl:choose>
</xsl:template>

<!-- Lite Event Calendar representation -->
<xsl:template name="em_calendar_table">
	<!-- Current XML tag: negeso:EventCalendarsComponent -->
	<br/>
	<xsl:variable name="prevYear">
		<xsl:choose>
			<xsl:when test="@startDateMonth = '1'"><xsl:value-of select="number(@startDateYear)-1"/></xsl:when>
			<xsl:otherwise><xsl:value-of select="@startDateYear"/></xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<xsl:variable name="nextYear">
		<xsl:choose>
			<xsl:when test="@startDateMonth = '12'"><xsl:value-of select="number(@startDateYear)+1"/></xsl:when>
			<xsl:otherwise><xsl:value-of select="@startDateYear"/></xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<xsl:variable name="prevMonth"><xsl:value-of select="(number(@startDateMonth)+10) mod 12 + 1"/></xsl:variable>
	<xsl:variable name="nextMonth"><xsl:value-of select="(number(@startDateMonth)) mod 12 + 1"/></xsl:variable>
	<xsl:variable name="startDayNum"><xsl:value-of select="negeso:Month/negeso:Day/@weekDayNumber"/></xsl:variable>
	<table cellpadding="0" cellspacing="0" border="0" align="center" class="emCalendarTitle">
		<tr>
			<td class="emPrev">
				<a href="?startDate={number(@startDateYear)-1}-{@startDateMonth}-{@startDateDay}&amp;period=month">&lt;&lt;&lt;</a>
			</td>
			<td class="emPrev">
				<a href="?startDate={$prevYear}-{$prevMonth}-{@startDateDay}&amp;period=month">&lt;&lt;</a>
			</td>
			<td>
				<xsl:value-of select="negeso:Month/@name"/>&#160;<xsl:value-of select="@startDateYear"/>
			</td>
			<td class="emNext">
				<a href="?startDate={$nextYear}-{$nextMonth}-{@startDateDay}&amp;period=month">&gt;&gt;</a>
			</td>
			<td class="emNext">
				<a href="?startDate={number(@startDateYear)+1}-{@startDateMonth}-{@startDateDay}&amp;period=month">&gt;&gt;&gt;</a>
			</td>
		</tr>
	</table>
	<table cellpadding="0" cellspacing="2" border="0" align="center" class="emCalendar">
		<tr>
			<th><xsl:value-of select="substring(negeso:Month/negeso:Day[@weekDayNumber='1']/@weekDay,0,4)"/></th>
			<th><xsl:value-of select="substring(negeso:Month/negeso:Day[@weekDayNumber='2']/@weekDay,0,4)"/></th>
			<th><xsl:value-of select="substring(negeso:Month/negeso:Day[@weekDayNumber='3']/@weekDay,0,4)"/></th>
			<th><xsl:value-of select="substring(negeso:Month/negeso:Day[@weekDayNumber='4']/@weekDay,0,4)"/></th>
			<th><xsl:value-of select="substring(negeso:Month/negeso:Day[@weekDayNumber='5']/@weekDay,0,4)"/></th>
			<th><xsl:value-of select="substring(negeso:Month/negeso:Day[@weekDayNumber='6']/@weekDay,0,4)"/></th>
			<th><xsl:value-of select="substring(negeso:Month/negeso:Day[@weekDayNumber='7']/@weekDay,0,4)"/></th>
		</tr>
		<!-- Opening row: setting first day in month -->
		<xsl:if test="number($startDayNum) &gt; 1">
			<xsl:text disable-output-escaping="yes"><![CDATA[<tr>]]></xsl:text>
			<td colspan="{number($startDayNum)-1}">&#160;</td>
		</xsl:if>
		<xsl:for-each select="negeso:Month/negeso:Day">
			<!-- Opening row: inside month -->
			<xsl:if test="number((position()+number($startDayNum)-2) mod 7) = 0"><xsl:text disable-output-escaping="yes"><![CDATA[<tr>]]></xsl:text></xsl:if>
			<td>
				<xsl:if test="count(negeso:Event) &gt; 0">
					<xsl:attribute name="class">emHasEvents</xsl:attribute>
				</xsl:if>
				<xsl:if test="number(@number) = number(../../@startDateDay) and not(negeso:Event)">
					<xsl:attribute name="class">emCurrent</xsl:attribute>
				</xsl:if>
				<xsl:if test="number(@number) = number(../../@startDateDay) and negeso:Event">
					<xsl:attribute name="class">emCurrent emHasEvents</xsl:attribute>
				</xsl:if>
				
				<xsl:choose>
					<xsl:when test="negeso:Event">
						<a href="?startDate={../../@startDateYear}-{../@number}-{@number}&amp;period=day" title="{substring(negeso:Event/negeso:FieldList/negeso:Field[@order=1]/@value,0,50)}...">
							<xsl:value-of select="@number"/>
						</a>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="@number"/>
					</xsl:otherwise>
				</xsl:choose>
			</td>
			<!-- Closing row -->
			<xsl:if test="number((position()+number($startDayNum)-2) mod 7) = 6"><xsl:text disable-output-escaping="yes"><![CDATA[</tr>]]></xsl:text></xsl:if>
			<xsl:if test="(position() = last()) and number((position()+number($startDayNum)-2) mod 7) &lt; 6">
				<td colspan="7">&#160;</td>
				<xsl:text disable-output-escaping="yes"><![CDATA[</tr>]]></xsl:text>
			</xsl:if>
		</xsl:for-each>
	</table>
	<br/>
</xsl:template>

</xsl:stylesheet>
