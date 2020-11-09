<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)${edit_event.xsl}
 
  Copyright (c) 2003 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.
 
  @author       Sergiy Oliynyk
-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">

<xsl:include href="/xsl/negeso_header.xsl"/>
<xsl:include href="/xsl/admin_templates.xsl"/>

<xsl:variable name="lang" select="/*/@interface-language"/>
<xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>
<xsl:variable name="dict_event_module" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_event_module.xsl', $lang)"/>

<xsl:template match="/">
<html>
<head>
    <title><xsl:value-of select="java:getString($dict_event_module, 'EVENT_DETAILS')"/></title>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link rel="stylesheet" href="/css/admin_style.css" type="text/css"/>
    <link href="/site/core/css/default_styles.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" src="/script/jquery.min.js" />
	<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
    <script type="text/javascript" src="/script/common_functions.js"/>
    <script language="JavaScript1.2" src="/script/calendar_picker.js" type="text/javascript"/>

    <script language="JavaScript">
        var s_FillEventName = "<xsl:value-of select="java:getString($dict_event_module, 'FILL_EVENT_NAME')"/>";
        var s_FillStartDate = "<xsl:value-of select="java:getString($dict_event_module, 'FILL_START_DATE')"/>";
        var s_FillEndDate = "<xsl:value-of select="java:getString($dict_event_module, 'FILL_END_DATE')"/>";
        var s_InvalidStartDate = "<xsl:value-of select="java:getString($dict_event_module, 'INVALID_START_DATE')"/>";
        var s_InvalidEndDate = "<xsl:value-of select="java:getString($dict_event_module, 'INVALID_END_DATE')"/>";
        var s_InvalidEmailAddress = "<xsl:value-of select="java:getString($dict_event_module, 'INVALID_EMAIL_ADDRESS')"/>";
        var s_DeleteEventConfirmation = "<xsl:value-of select="java:getString($dict_event_module, 'DELETE_EVENT_CONFIRMATION')"/>";
        var s_SelectStartTime = "<xsl:value-of select="java:getString($dict_event_module, 'SELECT_START_TIME')"/>";
        var s_EndDateMustBeBigger = "<xsl:value-of select="java:getString($dict_event_module, 'END_DATE_MUST_BE_LATER')"/>";
        var s_EndTimeMustBeBigger = "<xsl:value-of select="java:getString($dict_event_module, 'END_TIME_MUST_BE_LATER')"/>";

        <xsl:text disable-output-escaping="yes">
        <![CDATA[
        function getDate(dateStr) {
            if(/^(\d{1,4})\-(\d{1,2})\-(\d{1,2})$/.test(dateStr)){
                year=parseInt(RegExp.$1,10);
                if(year<100) year=(year<70)?2000+year:1900+year;
                month=RegExp.$2-1;
                date=parseInt(RegExp.$3,10);
                d1=new Date(year, month, date);
                return d1;
            }
        }

        function submitChanges(close) {
            if (mainForm.eventName.value == '') {
                alert(s_FillEventName);
                return;
            }
            if (mainForm.eventStartDate.value == '') {
                alert(s_FillStartDate);
                return;
            }
            if (mainForm.eventEndDate.value == '') {
                alert(s_FillEndDate);
                return;
            }
            if (!StringUtil.validateDate(mainForm.eventStartDate.value)) {
                alert(s_InvalidStartDate);
                return;
            }
            if (!StringUtil.validateDate(mainForm.eventEndDate.value)) {
                alert(s_InvalidEndDate);
                return;
            }
            var startDate = getDate(mainForm.eventStartDate.value).getTime();
            var endDate = getDate(mainForm.eventEndDate.value).getTime();
            if (startDate > endDate) {
                alert(s_EndDateMustBeBigger);
                return;
            }
            if (mainForm.email.value != "" &&
                !StringUtil.validateEmail(mainForm.email.value))
            {
                alert(s_InvalidEmailAddress);
                return;
            }
            if (!mainForm.allDayCheckbox.checked) {
                var startTimeIndex = mainForm.startTime.selectedIndex;
                var endTimeIndex = mainForm.endTime.selectedIndex;
                if (startTimeIndex < 0) {
                    alert(s_SelectStartTime);
                    return;
                }
                if (startTimeIndex >= endTimeIndex) {
                    alert(s_EndTimeMustBeBigger);
                    return;
                }
            }
            else {
                mainForm.startTime.value = "";
                mainForm.endTime.value = "";
            }
            if (close != null)
                mainForm.back.value = "true";
            mainForm.submit();
        }

        function deleteEvent() {
            if (confirm(s_DeleteEventConfirmation)) {
                mainForm.command.value = "delete-event-command";
                mainForm.submit();
            }
        }

        function timeSchedule() {
            if (mainForm.allDayCheckbox.checked) {
                mainForm.startTime.disabled = true;
                mainForm.endTime.disabled = true;
            }
            else {
                mainForm.startTime.disabled = false;
                mainForm.endTime.disabled = false;
            }
        }
        ]]>
       </xsl:text>
    </script>
    <xsl:call-template name="adminhead"/>
</head>
    <body style="behavior: url(/script/webservice.htc) url(/script/clientmanager.htc)"
        id="ClientManager">
    <!-- NEGESO HEADER -->
 	<xsl:call-template name="NegesoHeader">
      <xsl:with-param name="helpLink">
          <xsl:text>/admin/help/cev1_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>
      </xsl:with-param>
	</xsl:call-template>
    <!-- CONTENT -->
    <div align="center">
         <xsl:apply-templates select="negeso:event"/>
    </div>
</body>
</html>
</xsl:template>

<xsl:template match="negeso:event" >
    <form method="POST" name="mainForm" enctype="multipart/form-data" action="">
        <input type="hidden" name="command" value="update-event-command"/>
        <input type="hidden" name="id" value="{@id}"/>
        <input type="hidden" name="back" value="false"/>
        <xsl:choose>
            <xsl:when test="not(@fromSearch)">
            	<xsl:call-template name="NavBar">
                    <xsl:with-param name="backLink" select="'?command=get-event-schedule-command'"/>
        		</xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
            	<xsl:call-template name="NavBar">
                    <xsl:with-param name="backLink" select="'/admin/search_events'"/>
        		</xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>

        <xsl:call-template name="tableTitle">
            <xsl:with-param name="headtext">
                <xsl:value-of select="java:getString($dict_event_module, 'EVENT_DETAILS')"/>
            </xsl:with-param>
        </xsl:call-template>
        <table class="admNavPanel" cellspacing="0" cellpadding="0">
            <!-- Name Field -->
            <tr>
                <td class="admMainTD admWidth150"><xsl:value-of select="java:getString($dict_common, 'NAME')"/></td>
                <td class="admLightTD" colspan="2">
                    <input class="admTextArea admWidth95perc" type="text" name="eventName">
                        <xsl:attribute name="value"><xsl:value-of select="@name"/></xsl:attribute>
                    </input>
                </td>
            </tr>
            <!-- Category -->
            <tr>
                <td class="admMainTD"><xsl:value-of select="java:getString($dict_common, 'CATEGORY')"/></td>
                <td class="admLightTD" style="text-align: left" colspan="2">
                    <select name="categoryId" style="float: none; margin-left: 5">
                        <xsl:apply-templates select="negeso:categories/negeso:category">
                            <xsl:with-param name="selected" select="@categoryId"/>
                        </xsl:apply-templates>
                    </select>
                </td>
            </tr>
            <!-- Start date -->
            <tr>
                <td class="admMainTD"><xsl:value-of select="java:getString($dict_event_module, 'START_DATE')"/> / <xsl:value-of select="java:getString($dict_event_module, 'START_TIME')"/></td>
                <td class="admLightTD">
                    <input class="admTextArea admWidth200" type="text" name="eventStartDate" id="startDateFieldId" readonly="true">
                        <xsl:attribute name="value"><xsl:value-of select="@startDate"/></xsl:attribute>
                        <img class="admHand" src="/images/calendar.gif" width="16" height="16" onclick="loadNewCalendar('/dialogs/dialog_calendar_picker.html','startDateFieldId','yyyymmdd',false)">
                            <xsl:attribute name="alt"><xsl:value-of select="java:getString($dict_common, 'PICK_DATE')"/></xsl:attribute>
                        </img>
                    </input>
                    (yyyy-mm-dd)&#160;&#160;
                    <input type="checkbox" name="allDayCheckbox" onClick="timeSchedule()">
                        <xsl:if test="not(@startTime)">
                            <xsl:attribute name="checked">true</xsl:attribute>
                        </xsl:if>
                    </input>
                    <xsl:value-of select="java:getString($dict_event_module, 'ALL_DAY')"/>
                </td>
                <td class="admLightTD" style="text-align: left">
                    <xsl:call-template name="negeso:schedule">
                        <xsl:with-param name="name" select="'startTime'"/>
                        <xsl:with-param name="time" select="@startTime"/>
                    </xsl:call-template>
                </td>
            </tr>
            <!-- End date -->
            <tr>
                <td class="admMainTD"><xsl:value-of select="java:getString($dict_event_module, 'END_DATE')"/> / <xsl:value-of select="java:getString($dict_event_module, 'END_TIME')"/></td>
                <td class="admLightTD">
                    <input class="admTextArea admWidth200" type="text" name="eventEndDate" id="endDateFieldId" readonly="true">
                        <xsl:attribute name="value"><xsl:value-of select="@endDate"/></xsl:attribute>
                        <img class="admHand" src="/images/calendar.gif" width="16" height="16" onclick="loadNewCalendar('/dialogs/dialog_calendar_picker.html','endDateFieldId','yyyymmdd',false)">
                            <xsl:attribute name="alt"><xsl:value-of select="java:getString($dict_common, 'PICK_DATE')"/></xsl:attribute>
                        </img>
                    </input>
                    (yyyy-mm-dd)
                </td>
                <td class="admLightTD" style="text-align: left">
                    <xsl:call-template name="negeso:schedule">
                        <xsl:with-param name="name" select="'endTime'"/>
                        <xsl:with-param name="time" select="@endTime"/>
                    </xsl:call-template>
                </td>
            </tr>
            <!-- Event owner -->
            <tr>
                <td class="admMainTD admWidth150"><xsl:value-of select="java:getString($dict_event_module, 'EVENT_OWNER')"/></td>
                <td class="admLightTD" colspan="2">
                    <input class="admTextArea admWidth95perc" type="text" name="eventOwner">
                        <xsl:attribute name="value"><xsl:value-of select="@owner"/></xsl:attribute>
                    </input>
                </td>
            </tr>
            <!-- Email -->
            <tr>
                <td class="admMainTD admWidth150"><xsl:value-of select="java:getString($dict_common, 'EMAIL')"/></td>
                <td class="admLightTD" colspan="2">
                    <input class="admTextArea admWidth95perc" type="text" name="email">
                        <xsl:attribute name="value"><xsl:value-of select="@email"/></xsl:attribute>
                    </input>
                </td>
            </tr>
            <!-- URL -->
            <tr>
                <td class="admMainTD admWidth150">URL</td>
                <td class="admLightTD" colspan="2">
                    <input class="admTextArea admWidth95perc" type="text" name="url">
                        <xsl:attribute name="value"><xsl:value-of select="@url"/></xsl:attribute>
                    </input>
                </td>
            </tr>
            <!-- Phones -->
            <tr>
                <td class="admMainTD admWidth150"><xsl:value-of select="java:getString($dict_common, 'PHONES')"/></td>
                <td class="admLightTD" colspan="2">
                    <input class="admTextArea admWidth95perc" type="text" name="phones">
                        <xsl:attribute name="value"><xsl:value-of select="@phones"/></xsl:attribute>
                    </input>
                </td>
            </tr>
        </table>  
    </form>

    <table class="admNavPanel" cellspacing="0" cellpadding="0">
        <!-- Description -->
        <tr>
            <td class="admMainTD admWidth150">
                <xsl:value-of select="java:getString($dict_common, 'DESCRIPTION')"/>
            </td>
            <td class="admLightTD admLeft admZero">
                <xsl:apply-templates select="negeso:teaser">
                    <xsl:with-param name="classType" select="'shortNews'"/>
                </xsl:apply-templates>
            </td>
        </tr>
    </table>

    <!-- Update/reset fields -->
    <table cellpadding="0" cellspacing="0"  class="admNavPanel">
        <tr>
            <td class="admNavPanel admNavbar admCenter">
                <input name="saveButton" class="admNavbarInp" type="button" onClick="submitChanges()">
                    <xsl:attribute name="value">&lt;&#160;<xsl:value-of select="java:getString($dict_common, 'SAVE')"/>&#160;&gt;</xsl:attribute>
                </input>
                <input name="saveAndCloseButton" class="admNavbarInp" type="button" onClick="submitChanges('close')">
                    <xsl:attribute name="value">&lt;&#160;<xsl:value-of select="java:getString($dict_common, 'SAVE_AND_CLOSE')"/>&#160;&gt;</xsl:attribute>
                </input>
                <input name="resetButton" class="admNavbarInp" type="button" onClick="mainForm.reset();">
                    <xsl:attribute name="value">&lt;&#160;<xsl:value-of select="java:getString($dict_common, 'RESET')"/>&#160;&gt;</xsl:attribute>
                </input>
                <input name="deleteButton" class="admNavbarInp" type="button" onClick="deleteEvent()">
                    <xsl:attribute name="value">&lt;&#160;<xsl:value-of select="java:getString($dict_common, 'DELETE')"/>&#160;&gt;</xsl:attribute>
                </input>
            </td>
        </tr>
    </table>
    <xsl:call-template name="NavBar">
        <xsl:with-param name="backLink" select="'?command=get-event-schedule-command'"/>
        <xsl:with-param name="backId" select="'backLinkBottom'"/>
    </xsl:call-template>
</xsl:template>

<xsl:template name="negeso:schedule">
    <xsl:param name="name"/>
    <xsl:param name="time"/>
    <select name="{$name}" style="float: none; margin-left: 5">
        <option value="06:00:00">06:00 AM</option>
        <option value="06:30:00">06:30 AM</option>
        <option value="07:00:00">07:00 AM</option>
        <option value="07:30:00">07:30 AM</option>
        <option value="08:00:00">08:00 AM</option>
        <option value="08:30:00">08:30 AM</option>
        <option value="09:00:00">09:00 AM</option>
        <option value="09:30:00">09:30 AM</option>
        <option value="10:00:00">10:00 AM</option>
        <option value="10:30:00">10:30 AM</option>
        <option value="11:00:00">11:00 AM</option>
        <option value="11:30:00">11:30 AM</option>
        <option value="12:00:00">12:00 PM</option>
        <option value="12:30:00">12:30 PM</option>
        <option value="13:00:00">01:00 PM</option>
        <option value="13:30:00">01:30 PM</option>
        <option value="14:00:00">02:00 PM</option>
        <option value="14:30:00">02:30 PM</option>
        <option value="15:00:00">03:00 PM</option>
        <option value="15:30:00">03:30 PM</option>
        <option value="16:00:00">04:00 PM</option>
        <option value="16:30:00">04:30 PM</option>
        <option value="17:00:00">05:00 PM</option>
        <option value="17:30:00">05:30 PM</option>
        <option value="18:00:00">06:00 PM</option>
        <option value="18:30:00">06:30 PM</option>
        <option value="19:00:00">07:00 PM</option>
        <option value="19:30:00">07:30 PM</option>
        <option value="20:00:00">08:00 PM</option>
        <option value="20:30:00">08:30 PM</option>
        <option value="21:00:00">09:00 PM</option>
        <option value="21:30:00">09:30 PM</option>
        <option value="22:00:00">10:00 PM</option>
        <option value="22:30:00">10:30 PM</option>
        <option value="23:00:00">11:00 PM</option>
    </select>
    <script>
        var time = '<xsl:value-of select="$time"/>';
        if (time != '') {
            var hours = time.charAt(0) != '0' ? parseInt(time.substring(0, 2)) : 
                parseInt(time.charAt(1));
            var minutes = time.substring(time.indexOf(":")+1, time.indexOf(" "));
            <xsl:text disable-output-escaping="yes">
            <![CDATA[
            if (time.indexOf("PM") > 0 && hours < 12)
                hours += 12;
            time = hours < 10 ? time = "0" : "";
            ]]>
            </xsl:text>
            time += hours + ":" + minutes + ":00";
            mainForm.<xsl:value-of select="$name"/>.value=time;
        }
        else {
            mainForm.<xsl:value-of select="$name"/>.value="";
            mainForm.<xsl:value-of select="$name"/>.disabled=true;
        }
    </script>
</xsl:template>

<xsl:template match="negeso:category">
    <xsl:param name="selected"/>
    <option value="{@id}">
        <xsl:if test="@id = $selected">
            <xsl:attribute name="selected">true</xsl:attribute>
        </xsl:if>
        <xsl:value-of select="@name"/>
    </option>
</xsl:template>

<xsl:template match="negeso:teaser">
    <xsl:param name="classType"/>
    <xsl:apply-templates select="negeso:article">
    	<xsl:with-param name="classType" select="$classType"/>
    </xsl:apply-templates>
</xsl:template>

<xsl:template match="negeso:article" >
    <xsl:param name="classType"/>
    <xsl:choose>
        <xsl:when test="false"/>
        <xsl:otherwise>
            <img src="/images/mark_1.gif" onclick="edit_text('article_text{@id}', 'shortNews', 595);" class="admBorder admHand" style="margin-left: 5px;">
                <xsl:attribute name="alt"><xsl:value-of select="java:getString($dict_event_module, 'EDIT_EVENT_TEXT')"/></xsl:attribute>
            </img>
        </xsl:otherwise>
    </xsl:choose>
	<div id="article_text{@id}" class="contentStyle">
		<xsl:if test="$classType = 'shortNews'">
			<xsl:attribute name="class">shortNews</xsl:attribute>
		</xsl:if>
	   <xsl:attribute name="style">behavior:url(/script/article3.htc); margin: 5px;border: 1px solid #848484;</xsl:attribute>
		<xsl:choose>
			<xsl:when test="negeso:text/text()">
				<xsl:value-of select="negeso:text" disable-output-escaping="yes"/>
			</xsl:when>
			<xsl:otherwise>&#160;</xsl:otherwise>
		</xsl:choose>
	</div>
    <script>
        makeReadonly(article_text<xsl:value-of select="@id" />, true);
    </script>
</xsl:template>

</xsl:stylesheet>
