<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)${daily.xsl}
 
  Copyright (c) 2004 Negeso Ukraine
 
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

<xsl:variable name="lang" select="/*/@interface-language"/>
<xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>
<xsl:variable name="dict_event_module" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_event_module.xsl', $lang)"/>

<xsl:template match="/">
<html>
<head>
    <title><xsl:value-of select="java:getString($dict_event_module, 'EVENT_CALENDAR')"/></title>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" src="/dictionaries/dict_calendar_{$lang}.js">/**/</script>
    <script type="text/javascript" src="/script/event_calendar.js">/**/</script>

    <script language="JavaScript">
        var dtFirst = getDate('<xsl:value-of select="/negeso:events/@startDate"/>');
    <xsl:text disable-output-escaping="yes">
    <![CDATA[
        var leftCalendar;
        var rightCalendar;
        var centerCalendar;

        function selectMonth() {
            categoriesForm.startDate.value = centerCalendar.Year + "-" +
                (centerCalendar.Month+1) + "-01";
            categoriesForm.period.value = "month";
            categoriesForm.submit();
        }

        function selectWeek() {
            centerCalendar.setWeekStart();
            categoriesForm.startDate.value = centerCalendar.formatDate();
            categoriesForm.period.value = "week";
            categoriesForm.submit();
        }

        function selectDate(year, month, day) {
            categoriesForm.startDate.value = year + "-" + month + "-" + day;
            categoriesForm.period.value = "day";
            categoriesForm.submit();
        }

        function selectYear() {
            categoriesForm.startDate.value = year + "-01-01";
            categoriesForm.period.value = "year";
            categoriesForm.submit();
        }

        function listView() {
            categoriesForm.startDate.value = centerCalendar.Year + "-" + (centerCalendar.Month+1) + "-01";
            categoriesForm.period.value = "month";
            categoriesForm.list.value = "true";
            categoriesForm.submit();
        }
        
        function nextDate() {
            centerCalendar.incDate();
            categoriesForm.startDate.value = centerCalendar.Year + "-" +
                (centerCalendar.Month+1) + "-" + centerCalendar.Date;
            categoriesForm.period.value = "day";
            categoriesForm.submit();
        }

        function prevDate() {
            centerCalendar.decDate();
            categoriesForm.startDate.value = centerCalendar.Year + "-" +
                (centerCalendar.Month+1) + "-" + centerCalendar.Date;
            categoriesForm.period.value = "day";
            categoriesForm.submit();
        }
        
        function addEvent() {
            window.location.href = "/admin/add_event?startDate=" +
                centerCalendar.Year + "-" + (centerCalendar.Month+1) + "-01";
        }
        
        function searchEvents() {
            window.location.href="/admin/search_events";
        }
    ]]>
    </xsl:text>
    </script>

    <style type="text/css">
        a:link{color:#000000;}
        a:visited{color:#000000;}
    </style>
</head>
<body>
    <!-- NEGESO HEADER -->
    <xsl:call-template name="NegesoHeader">
        <xsl:with-param name="helpLink">
            <xsl:text>/admin/help/cev1_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>
        </xsl:with-param>
	   </xsl:call-template>
    <div align="center">
        <xsl:call-template name="NavBar"/>            
       <!-- CONTENT -->
        <xsl:apply-templates select="negeso:events"/>
        <xsl:call-template name="NavBar"/>
    </div>
</body>
</html>
</xsl:template>

<xsl:template match="/negeso:events">
    <table border="0">
        <tr height="100">
            <td width="100" id="leftCalendarTD">
                <script>
                    leftCalendar=new Calendar(dtFirst);
                    leftCalendar.decMonth();
                    leftCalendarTD.innerHTML = leftCalendar.renderMonth();
                </script>
            </td>
            <td id="centerCalendarTD" width="100" style="padding-bottom : 0px; padding-left : 20px; padding-right : 20px">
                <script>
                    centerCalendar=new Calendar(dtFirst);
                    centerCalendar.today = dtFirst;
                    centerCalendar.todayColor = DarkGreenColor;
                    centerCalendarTD.innerHTML = centerCalendar.renderMonth();
                </script>

            </td>
            <td id="rightCalendarTD" style="text-align: right; width: 100px">
                <script>
                    rightCalendar=new Calendar(dtFirst);
                    rightCalendar.incMonth();
                    rightCalendarTD.innerHTML = rightCalendar.renderMonth();
                </script>
            </td>
        </tr>
        <tr>
            <td align="center" valign="middle" colspan="3" style="padding-bottom : 5px;">
                <a href="JavaScript:selectMonth()"><xsl:value-of select="java:getString($dict_event_module, 'MONTHLY_VIEW')"/></a>&#160;|&#160;
                <a href="JavaScript:selectWeek()"><xsl:value-of select="java:getString($dict_event_module, 'WEEKLY_VIEW')"/></a>&#160;|&#160;
                <a href="JavaScript:listView()"><xsl:value-of select="java:getString($dict_event_module, 'LIST_VIEW')"/></a>&#160;|&#160;
                <a href="JavaScript:selectYear()"><xsl:value-of select="java:getString($dict_event_module, 'YEARLY_VIEW')"/></a>&#160;|&#160;
                <a href="JavaScript:addEvent()">
                    <xsl:if test="not(negeso:categories/negeso:category)">
                        <xsl:attribute name="href">#</xsl:attribute>
                        <xsl:attribute name="disabled">true</xsl:attribute>
                    </xsl:if>
                    <xsl:value-of select="java:getString($dict_event_module, 'ADD_EVENT')"/>
                </a>&#160;|&#160;
                <a href="JavaScript:searchEvents()"><xsl:value-of select="java:getString($dict_event_module, 'EVENT_SEARCH')"/></a>
            </td>
        </tr>
    </table>
    <table class="admNavPanel" border="0" cellpadding="0" cellspacing="0">
        <tr>
            <td class="admNavbarImg"><img src="/images/titleLeft.gif" alt="" border="0"/></td>
            <td class="admTitle admLeft" align="center" style="font-size: 12px;">
                <a class="admNone" href='javascript:prevDate();'><small>&lt;&lt;&lt;</small><xsl:value-of select="java:getString($dict_event_module, 'PREV')"/></a>
            </td>
            <td class="admTitle">
                <b>
                    <script>
                        document.write(centerCalendar.getDayName(), ", ", 
                            centerCalendar.getMonthName(), " ", centerCalendar.Date, ", ", 
                            centerCalendar.Year);
                    </script>
                </b>
            </td>
            <td class="admTitle admRight" align="center" style="font-size: 12px;">
                <a class="admNone" href="javascript:nextDate();">
                    <xsl:value-of select="java:getString($dict_event_module, 'NEXT')"/><small>&gt;&gt;&gt;&gt;</small>
                </a>
            </td>
            <td class="admNavbarImg">
                <img src="/images/titleRight.gif" alt="" border="0"/>
            </td>
        </tr>
    </table>
    <table border="0" cellpadding="0" cellspacing="3" valign="top" width="760">
        <tr height="24" bgColor="#9D9D9D">
            <td align='center' style='padding: 2px;' width="80"><font face='Verdana' size='2' color='white'><b><xsl:value-of select="java:getString($dict_common, 'TIME')"/></b></font></td>
            <td align='center' style='padding: 2px;'><font face='Verdana' size='2' color='white'><b><xsl:value-of select="java:getString($dict_event_module, 'EVENT_NAME_AND_DESCRIPTION')"/></b></font></td>
        </tr>
        <xsl:apply-templates select="negeso:day/negeso:event[not(@startTime)]"/>
        <!-- set mode to time if you want to get a time table -->
        <xsl:apply-templates select="negeso:day" mode="event"/>
    </table>
    <form method="POST" name="categoriesForm" enctype="multipart/form-data">
        <input type="hidden" name="command" value="get-event-schedule-command"/>
        <input type="hidden" name="startDate"/>
        <input type="hidden" name="period" value="day"/>
        <input type="hidden" name="list" value="false"/>
    </form>
</xsl:template>

<xsl:template match="negeso:day" mode="time">
    <tr bgcolor="#EFEFEF"><td align='center'>06:00 AM</td><td><xsl:apply-templates select="negeso:event[@startTime='06:00 AM']"/></td></tr>
    <tr bgcolor="#EFEFEF"><td align='center'>06:30 AM</td><td><xsl:apply-templates select="negeso:event[@startTime='06:30 AM']"/></td></tr>
    <tr bgcolor="#EFEFEF"><td align='center'>07:00 AM</td><td><xsl:apply-templates select="negeso:event[@startTime='07:00 AM']"/></td></tr>
    <tr bgcolor="#EFEFEF"><td align='center'>07:30 AM</td><td><xsl:apply-templates select="negeso:event[@startTime='07:30 AM']"/></td></tr>
    <tr bgcolor="#EFEFEF"><td align='center'>08:00 AM</td><td><xsl:apply-templates select="negeso:event[@startTime='08:00 AM']"/></td></tr>
    <tr bgcolor="#EFEFEF"><td align='center'>08:30 AM</td><td><xsl:apply-templates select="negeso:event[@startTime='08:30 AM']"/></td></tr>
    <tr bgcolor="#EFEFEF"><td align='center'>09:00 AM</td><td><xsl:apply-templates select="negeso:event[@startTime='09:00 AM']"/></td></tr>
    <tr bgcolor="#EFEFEF"><td align='center'>09:30 AM</td><td><xsl:apply-templates select="negeso:event[@startTime='09:30 AM']"/></td></tr>
    <tr bgcolor="#EFEFEF"><td align='center'>10:00 AM</td><td><xsl:apply-templates select="negeso:event[@startTime='10:00 AM']"/></td></tr>
    <tr bgcolor="#EFEFEF"><td align='center'>10:30 AM</td><td><xsl:apply-templates select="negeso:event[@startTime='10:30 AM']"/></td></tr>
    <tr bgcolor="#EFEFEF"><td align='center'>11:00 AM</td><td><xsl:apply-templates select="negeso:event[@startTime='11:00 AM']"/></td></tr>
    <tr bgcolor="#EFEFEF"><td align='center'>11:30 AM</td><td><xsl:apply-templates select="negeso:event[@startTime='11:30 AM']"/></td></tr>
    <tr bgcolor="#EFEFEF"><td align='center'>12:00 PM</td><td><xsl:apply-templates select="negeso:event[@startTime='12:00 PM']"/></td></tr>
    <tr bgcolor="#EFEFEF"><td align='center'>12:30 PM</td><td><xsl:apply-templates select="negeso:event[@startTime='12:30 PM']"/></td></tr>
    <tr bgcolor="#EFEFEF"><td align='center'>01:00 PM</td><td><xsl:apply-templates select="negeso:event[@startTime='01:00 PM']"/></td></tr>
    <tr bgcolor="#EFEFEF"><td align='center'>01:30 PM</td><td><xsl:apply-templates select="negeso:event[@startTime='01:30 PM']"/></td></tr>
    <tr bgcolor="#EFEFEF"><td align='center'>02:00 PM</td><td><xsl:apply-templates select="negeso:event[@startTime='02:00 PM']"/></td></tr>
    <tr bgcolor="#EFEFEF"><td align='center'>02:30 PM</td><td><xsl:apply-templates select="negeso:event[@startTime='02:30 PM']"/></td></tr>
    <tr bgcolor="#EFEFEF"><td align='center'>03:00 PM</td><td><xsl:apply-templates select="negeso:event[@startTime='03:00 PM']"/></td></tr>
    <tr bgcolor="#EFEFEF"><td align='center'>03:30 PM</td><td><xsl:apply-templates select="negeso:event[@startTime='03:30 PM']"/></td></tr>
    <tr bgcolor="#EFEFEF"><td align='center'>04:00 PM</td><td><xsl:apply-templates select="negeso:event[@startTime='04:00 PM']"/></td></tr>
    <tr bgcolor="#EFEFEF"><td align='center'>04:30 PM</td><td><xsl:apply-templates select="negeso:event[@startTime='04:30 PM']"/></td></tr>
    <tr bgcolor="#EFEFEF"><td align='center'>05:00 PM</td><td><xsl:apply-templates select="negeso:event[@startTime='05:00 PM']"/></td></tr>
    <tr bgcolor="#EFEFEF"><td align='center'>05:30 PM</td><td><xsl:apply-templates select="negeso:event[@startTime='05:30 PM']"/></td></tr>
    <tr bgcolor="#EFEFEF"><td align='center'>06:00 PM</td><td><xsl:apply-templates select="negeso:event[@startTime='06:00 PM']"/></td></tr>
    <tr bgcolor="#EFEFEF"><td align='center'>06:30 PM</td><td><xsl:apply-templates select="negeso:event[@startTime='06:30 PM']"/></td></tr>
    <tr bgcolor="#EFEFEF"><td align='center'>07:00 PM</td><td><xsl:apply-templates select="negeso:event[@startTime='07:00 PM']"/></td></tr>
    <tr bgcolor="#EFEFEF"><td align='center'>07:30 PM</td><td><xsl:apply-templates select="negeso:event[@startTime='07:30 PM']"/></td></tr>
    <tr bgcolor="#EFEFEF"><td align='center'>08:00 PM</td><td><xsl:apply-templates select="negeso:event[@startTime='08:00 PM']"/></td></tr>
    <tr bgcolor="#EFEFEF"><td align='center'>08:30 PM</td><td><xsl:apply-templates select="negeso:event[@startTime='08:30 PM']"/></td></tr>
    <tr bgcolor="#EFEFEF"><td align='center'>09:00 PM</td><td><xsl:apply-templates select="negeso:event[@startTime='09:00 PM']"/></td></tr>
    <tr bgcolor="#EFEFEF"><td align='center'>09:30 PM</td><td><xsl:apply-templates select="negeso:event[@startTime='09:30 PM']"/></td></tr>
    <tr bgcolor="#EFEFEF"><td align='center'>10:00 PM</td><td><xsl:apply-templates select="negeso:event[@startTime='10:00 PM']"/></td></tr>
    <tr bgcolor="#EFEFEF"><td align='center'>10:30 PM</td><td><xsl:apply-templates select="negeso:event[@startTime='10:30 PM']"/></td></tr>
    <tr bgcolor="#EFEFEF"><td align='center'>11:00 PM</td><td><xsl:apply-templates select="negeso:event[@startTime='11:00 PM']"/></td></tr>
</xsl:template>

<xsl:template match="negeso:day" mode="event">
    <xsl:apply-templates select="negeso:event" mode="row"/>
</xsl:template>

<xsl:template match="negeso:event">
    <xsl:attribute name="bgcolor">#C9E7C4</xsl:attribute>
    <a style="color : blue;" href="/admin/event_details?id={@id}"><xsl:value-of select="@name"/></a>&#160;&#160;<xsl:value-of select="@text" disable-output-escaping="yes"/>
</xsl:template>

<xsl:template match="negeso:event[not(@startTime)]">
    <tr bgcolor="#EFEFEF"><td align='center'><xsl:value-of select="java:getString($dict_event_module, 'ALL_DAY')"/></td><td bgcolor="#C9E7C4"><a style="color : blue;" href="/admin/event_details?id={@id}"><xsl:value-of select="@name"/></a>&#160;&#160;<xsl:value-of select="@text" disable-output-escaping="yes"/></td></tr>
    <xsl:apply-templates select="negeso:day/negeso:event[not(@startTime)]"/>
</xsl:template>

<xsl:template match="negeso:event[@startTime]" mode="row">
    <tr bgcolor="#EFEFEF" height="20">
        <td align='center'>
            <xsl:value-of select="@startTime"/>-<xsl:value-of select="@endTime"/>
        </td>
        <td bgcolor="#C9E7C4">
            <a style="color : blue;" href="/admin/event_details?id={@id}"><xsl:value-of select="@name"/></a>&#160;&#160;<xsl:value-of select="@text" disable-output-escaping="yes"/>
            <xsl:apply-templates select="negeso:day/negeso:event[@startTime='11:00 PM']"/>
        </td>
    </tr>
</xsl:template>

</xsl:stylesheet>
