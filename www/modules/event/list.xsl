<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)${list.xsl}

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
            categoriesForm.startDate.value = centerCalendar.Year + "-" +
                (centerCalendar.Month+1) + "-" + centerCalendar.Date;
            categoriesForm.categories.value = getSelectedCategories();
            categoriesForm.period.value = "month";
            categoriesForm.list.value = "true";
            categoriesForm.submit();
        }
        
        function prevMonth() {
            centerCalendar.decMonth();
            categoriesForm.startDate.value = centerCalendar.Year + "-" +
                (centerCalendar.Month+1) + "-" + centerCalendar.Date;
            categoriesForm.categories.value = getSelectedCategories();
            categoriesForm.period.value = "month";
            categoriesForm.list.value = "true";
            categoriesForm.submit();
        }

        function selectMonth(year, month) {
            if (year != null && month != null)
                categoriesForm.startDate.value = year + "-" + month + "-01";
            else
                categoriesForm.startDate.value = centerCalendar.Year + "-" + (centerCalendar.Month+1) + "-01";
            categoriesForm.categories.value = getSelectedCategories();
            categoriesForm.period.value = "month";
            categoriesForm.submit();
        }

        function selectWeek(day) {
            centerCalendar.setWeekStart();
            categoriesForm.startDate.value = centerCalendar.formatDate();
            categoriesForm.period.value = "week";
            categoriesForm.categories.value = getSelectedCategories();
            categoriesForm.submit();
        }

        function selectDate(year, month, day) {
            categoriesForm.startDate.value = year + "-" + month + "-" + day;
            categoriesForm.period.value = "day";
            categoriesForm.submit();
        }

        function selectYear() {
            categoriesForm.startDate.value = centerCalendar.Year + "-01-01";
            categoriesForm.period.value = "year";
            categoriesForm.submit();
        }

        function addEvent() {
            window.location.href = "/admin/add_event?startDate=" + 
                centerCalendar.Year + "-" + (centerCalendar.Month+1) + "-01";
        }

        function searchEvents() {
            window.location.href="/admin/search_events";
        }

        function refreshCategories() {
            categoriesForm.categories.value = getSelectedCategories();
            categoriesForm.list.value = "true";
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
    <table width="750"  border="0">
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
                        <a href="JavaScript:selectMonth()"><xsl:value-of select="java:getString($dict_event_module, 'MONTHLY_VIEW')"/></a>&#160;|&#160;
                        <a href="JavaScript:selectWeek()"><xsl:value-of select="java:getString($dict_event_module, 'WEEKLY_VIEW')"/></a>&#160;|&#160;
                        <xsl:value-of select="java:getString($dict_event_module, 'LIST_VIEW')"/>&#160;|&#160;
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
        <td align="center" colspan="3">
            <table height="100%" width="100%" border="0" cellpadding="0" cellspacing="3" class="admGrnBorder">
                <tr height="16">
                    <td colspan="8" valign="top">
                        <table border="0" width="100%" cellpadding="0" cellspacing="0">
                        <tr>
                            <td class="admNavbarImg"><img src="/images/titleLeft.gif" alt="" border="0"/></td>
                            <td class="admTitle admLeft" align="center" style="font-size: 12px;">
                                <a class="admNone" href='javascript:prevMonth();'><small>&lt;&lt;&lt;</small> Prev</a>
                            </td>
                            <td class="admTitle">
                                <b>
                                    <script>
                                        centerCalendar=new Calendar(dtFirst);
                                        document.write(centerCalendar.getMonthName(), " ", centerCalendar.Year);
                                    </script>
                                </b>
                            </td>
                            <td class="admTitle admRight" align="center" style="font-size: 12px;">
                                <a class="admNone" href="javascript:nextMonth();">
                                    Next <small>&gt;&gt;&gt;</small>
                                </a>
                            </td>
                            <td class="admNavbarImg">
                                <img src="/images/titleRight.gif" alt="" border="0"/>
                            </td>
                        </tr>
                        </table>
                    </td>
                </tr>
                <tr height="24" bgColor="#9D9D9D">
                    <td align="center" width="80"><font face="Verdana" size="2" color='white'><b><xsl:value-of select="java:getString($dict_event_module, 'EVENT')"/></b></font></td>
                    <td align="center"><font face="Verdana" size="2" color="white"><b><xsl:value-of select="java:getString($dict_common, 'DATE')"/> / <xsl:value-of select="java:getString($dict_common, 'TIME')"/></b></font></td>
                    <td align="center"><font face="Verdana" size="2" color="white"><b><xsl:value-of select="java:getString($dict_common, 'CATEGORY')"/></b></font></td>
                </tr>
                <xsl:apply-templates select="negeso:day/negeso:event"/>
                <xsl:if test="not(negeso:day/negeso:event)">
                    <tr height="100%"/>
                </xsl:if>
            </table>
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

<xsl:template match="negeso:event">
    <tr bgcolor="#C9E7C4">
        <xsl:variable name="category" select="@categoryId"/>
        <td><a style="color : blue;" href="/admin/event_details?id={@id}"><xsl:value-of select="@name"/></a></td>
        <td>
            <script>
                document.write(centerCalendar.getMonthName(), ", ");
            </script>
            <xsl:value-of select="../@id"/>
            <xsl:if test="@startTime != ''">
                &#160;<xsl:value-of select="@startTime"/>-<xsl:value-of select="@endTime"/>
            </xsl:if>
        </td>
        <td>
            <xsl:value-of select="/negeso:events/negeso:categories/negeso:category[@id = $category]/@name"/>
        </td>
    </tr>
    <tr bgcolor="#EFEFEF" height="32">
        <td colspan="3" align="center">
            <xsl:value-of select="@text" disable-output-escaping="yes"/>
        </td>
    </tr>
</xsl:template>

</xsl:stylesheet>
