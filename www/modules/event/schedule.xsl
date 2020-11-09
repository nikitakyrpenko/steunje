<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)${schedule.xsl}
 
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

        function getSelectedCategories() {
            var items = "";
            for (el in categoriesForm.elements.tags("INPUT")) {
                if (categoriesForm.elements[el].type == "checkbox" &&
                    categoriesForm.elements[el].checked)
                {
                    if (items != "")
                       items = items + ",";
                    items = items + categoriesForm.elements[el].value;
                }
            }
            return items != "" ? items : "none";
        }

        function checkBoxOnClick(value) {
            if (value == 'any') {
                for (el in categoriesForm.elements.tags("INPUT")) {
                    if (categoriesForm.elements[el].type == "checkbox" &&
                        categoriesForm.elements[el].value != 'any')
                        categoriesForm.elements[el].checked = false;
                }
            }
            else categoriesForm.checkbox_any.checked = false;
        }

        function nextMonth() {
            centerCalendar.incMonth();
            categoriesForm.startDate.value = centerCalendar.formatDate();
            categoriesForm.categories.value = getSelectedCategories();
            categoriesForm.period.value = "month";
            categoriesForm.submit();
        }

        function prevMonth() {
            centerCalendar.decMonth();
            categoriesForm.startDate.value = centerCalendar.formatDate();
            categoriesForm.categories.value = getSelectedCategories();
            categoriesForm.period.value = "month";
            categoriesForm.submit();
        }

        function nextWeek() {
            centerCalendar.incWeek();
            categoriesForm.startDate.value = centerCalendar.formatDate();
            categoriesForm.categories.value = getSelectedCategories();
            categoriesForm.period.value = "week";
            categoriesForm.submit();
        }

        function prevWeek() {
            centerCalendar.decWeek();
            categoriesForm.startDate.value = centerCalendar.formatDate();
            categoriesForm.categories.value = getSelectedCategories();
            categoriesForm.period.value = "week";
            categoriesForm.submit();
        }

        function selectMonth(year, month) {
            if (year != null && month != null)
                categoriesForm.startDate.value = year + "-" + month + "-01";
            else {
                if (centerCalendar.getMonDays() - centerCalendar.Date < 3)
                    centerCalendar.incMonth();
                categoriesForm.startDate.value = centerCalendar.Year + "-" +
                    (centerCalendar.Month+1) + "-01";
            }
            categoriesForm.period.value = "month";
            categoriesForm.categories.value = getSelectedCategories();
            categoriesForm.submit();
        }

        function selectWeek(year, month, date) {
            if (year == null || month == null || date == null) {
                centerCalendar.setWeekStart();
                year = centerCalendar.Year;
                month = centerCalendar.Month+1;
                date = centerCalendar.Date;
            }
            categoriesForm.startDate.value = year + "-" + month + "-" + date;
            categoriesForm.period.value = "week";
            categoriesForm.categories.value = getSelectedCategories();
            categoriesForm.submit();
        }

        function selectDate(year, month, day) {
            categoriesForm.startDate.value = year + "-" + month + "-" + day;
            categoriesForm.period.value = "day";
            categoriesForm.submit();
        }

        function listView() {
            if (centerCalendar.getMonDays() - centerCalendar.Date < 3)
                centerCalendar.incMonth();
            categoriesForm.startDate.value = centerCalendar.Year + "-" +
                (centerCalendar.Month+1) + "-01";
            categoriesForm.period.value = "month";
            categoriesForm.categories.value = getSelectedCategories();
            categoriesForm.list.value = "true";
            categoriesForm.submit();
        }

        function selectYear() {
            categoriesForm.startDate.value = year + "-01-01";
            categoriesForm.period.value = "year";
            categoriesForm.submit();
        }

        function addEvent() {
            window.location.href = "/admin/add_event?startDate=" +
                centerCalendar.Year + "-" + (centerCalendar.Month+1) + "-" +
                centerCalendar.Date;
        }

        function searchEvents() {
            window.location.href="/admin/search_events";
        }

        function refreshCategories() {
            categoriesForm.categories.value = getSelectedCategories();
            categoriesForm.submit();
        }
    ]]>
    </xsl:text>
    </script>

    <style type="text/css">
        a:link{color:#000000;}
        a:visited{color:#000000;}
        .weekTitle {
            background-color: #7FB577;
            color: white;
            font-weight: bold;
            font-size: 14px;
            vertical-align: middle;
            text-align: center;
        }
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
    <table width="750" height="100%" border="0">
    <tr height="100">
        <td id="leftCalendarTD">
            <script>
                leftCalendar=new Calendar(dtFirst);
                leftCalendar.decMonth();
                leftCalendarTD.innerHTML = leftCalendar.renderMonth();
            </script>
        </td>
        <td valign="top" width="600">
            <table width="100%">
                <tr>
                    <td class="admTitle" style="font-size: 14px;"><xsl:value-of select="java:getString($dict_event_module, 'EVENT_CATEGORY_FILTER')"/></td>
                </tr>
                <tr height="100">
                    <td align="center" style="background-color: #EAEAEA">
                        <form method="POST" name="categoriesForm" enctype="multipart/form-data">
                            <input type="hidden" name="command" value="get-event-schedule-command"/>
                            <input type="hidden" name="categories"/>
                            <input type="hidden" name="startDate"/>
                            <input type="hidden" name="period"/>
                            <input type="hidden" name="list" value="false"/>
                            <nobr>
                                <xsl:apply-templates select="negeso:categories"/>
                            </nobr>
                            <p align="right" style="margin-top: 0">
                            <input name="refreshButton" class="admNavbarInp" type="button" onClick="refreshCategories()">
                                <xsl:attribute name="value">&lt;&#160;<xsl:value-of select="java:getString($dict_common, 'REFRESH')"/>&#160;&gt;</xsl:attribute>
                            </input>
                            </p>
                        </form>
                    </td>
                </tr>
                <tr>
                    <td align="center" valign="middle">
                        <xsl:choose>
                            <xsl:when test="@period != 'month'">
                                <a href="JavaScript:selectMonth()"><xsl:value-of select="java:getString($dict_event_module, 'MONTHLY_VIEW')"/></a>&#160;|&#160;
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="java:getString($dict_event_module, 'MONTHLY_VIEW')"/>&#160;|&#160;
                            </xsl:otherwise>
                        </xsl:choose>
                        <xsl:choose>
                            <xsl:when test="@period != 'week'">
                                <a href="JavaScript:selectWeek()"><xsl:value-of select="java:getString($dict_event_module, 'WEEKLY_VIEW')"/></a>&#160;|&#160;
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="java:getString($dict_event_module, 'WEEKLY_VIEW')"/>&#160;|&#160;
                            </xsl:otherwise>
                        </xsl:choose>
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
        </td>
        <td id="rightCalendarTD" align="right" width="100">
            <script>
                rightCalendar=new Calendar(dtFirst);
                rightCalendar.incMonth();
                rightCalendarTD.innerHTML = rightCalendar.renderMonth();
            </script>
        </td>
    </tr>
    <tr height="100%">
        <td id="centerCalendarTD" align="center" colspan="3">
            <script>
                var centerCalendar=new Calendar(dtFirst);
                centerCalendar.showMonthScroller = true;
                centerCalendar.WeekChar = 3;
                centerCalendar.align = "left";
                centerCalendar.showWeek = true;
                centerCalendar.background = LightGrayColor;
                centerCalendar.sundayColor = LightGreenColor;
                centerCalendar.saturdayColor = LightGreenColor;
                centerCalendar.weekDayColor = LightGreenColor;
                centerCalendar.weekHeadColor = DarkGrayColor;
                centerCalendar.dayOfTheWeekColor = "white";
                centerCalendar.weekBarHeight = 24;
                centerCalendar.fontIsBold = true;
                <xsl:if test="not(negeso:categories/negeso:category)">
                    centerCalendar.showPlusIcon = false;
                </xsl:if>
                <xsl:choose>
                    <xsl:when test="@period = 'month'">
                        centerCalendarTD.innerHTML = centerCalendar.renderMonth();
                    </xsl:when>
                    <xsl:when test="@period = 'week'">
                        centerCalendar.textHeight = -1;
                        centerCalendarTD.innerHTML = centerCalendar.renderWeek();
                    </xsl:when>
                </xsl:choose>
                <xsl:apply-templates select="negeso:day"/>
            </script>
        </td>
    </tr>
</table>
</xsl:template>

<xsl:template match="negeso:categories">
    <input type="checkbox" name="checkbox_any" value="any" onClick="checkBoxOnClick('any')">
        <xsl:if test="@any = 'true'">
            <xsl:attribute name="checked">true</xsl:attribute>
        </xsl:if>
        <xsl:value-of select="java:getString($dict_event_module, 'ANY')"/>
    </input>
    <xsl:apply-templates select="negeso:category"/>
</xsl:template>

<xsl:template match="negeso:category">
    <input type="checkbox" name="checkbox_{@id}" value="{@id}" onClick="checkBoxOnClick({@id})">
        <xsl:if test="@selected = 'true'">
            <xsl:attribute name="checked">true</xsl:attribute>
        </xsl:if>
        <xsl:value-of select="@name"/>
    </input>
</xsl:template>

<xsl:template match="negeso:day">
    var events = '<xsl:apply-templates select="negeso:event"/>';
    day_<xsl:value-of select="@id"/>.innerHTML = events;
    var color = !centerCalendar.isToday(<xsl:value-of select="@id"/>) ?
        DarkGreenColor : centerCalendar.todayColor;
    cell_<xsl:value-of select="@id"/>.style.background = color;
</xsl:template>

<xsl:template match="negeso:event">
    <p style="margin-top: 0; margin-bottom: 5">
        <a style="color: blue" href="/admin/event_details?id={@id}">
            <xsl:value-of select="@name"/>
        </a>
        <xsl:if test="@startTime != ''">
            <br/>
            <xsl:value-of select="@startTime"/>-<xsl:value-of select="@endTime"/>
        </xsl:if>
    </p>
</xsl:template>

</xsl:stylesheet>
