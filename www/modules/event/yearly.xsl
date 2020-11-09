<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)${yearly.xsl}
 
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
        var calendar = new Array();
        for (i = 0; i < 12; i++) {
            calendar[i] = new Calendar(dtFirst);
            calendar[i].Month = i;
        }

        function selectMonth(year, month) {
            if (year != null && month != null)
                categoriesForm.startDate.value = year + "-" + month + "-01";
            categoriesForm.period.value = "month";
            categoriesForm.submit();
        }

        function selectWeek() {
            calendar[0].setWeekStart();
            categoriesForm.startDate.value = calendar[0].Year + "-" + 
                (calendar[0].Month+1) + "-" + calendar[0].Date;
            categoriesForm.period.value = "week";
            categoriesForm.submit();
        }

        function selectDate(year, month, day) {
            if (day != null) {
                categoriesForm.startDate.value = year + "-" + month + "-" + day;
            }
            categoriesForm.period.value = "day";
            categoriesForm.submit();
        }

        function listView() {
            categoriesForm.period.value = "month";
            categoriesForm.list.value = "true";
            categoriesForm.submit();
        }

        function nextYear() {
            for (i = 0; i < 12; i++)
                calendar[i].incYear();
            categoriesForm.startDate.value = calendar[0].Year + "-01-01";
            categoriesForm.submit();
        }

        function prevYear() {
            for (i = 0; i < 12; i++)
                calendar[i].decYear();
            categoriesForm.startDate.value = calendar[0].Year + "-01-01";
            categoriesForm.submit();
        }
        
        function addEvent() {
            window.location.href = "/admin/add_event?startDate=" + 
                calendar[0].Year + "-01-01";
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
        <tr height="40">
            <td align="center" valign="middle">
                <a href="JavaScript:selectMonth()"><xsl:value-of select="java:getString($dict_event_module, 'MONTHLY_VIEW')"/></a>&#160;|&#160;
                <a href="JavaScript:selectWeek()"><xsl:value-of select="java:getString($dict_event_module, 'WEEKLY_VIEW')"/></a>&#160;|&#160;
                <a href="JavaScript:listView()"><xsl:value-of select="java:getString($dict_event_module, 'LIST_VIEW')"/></a>&#160;|&#160;
                <xsl:value-of select="java:getString($dict_event_module, 'YEARLY_VIEW')"/>&#160;|&#160;
                <a href="JavaScript:addEvent()"><xsl:value-of select="java:getString($dict_event_module, 'ADD_EVENT')"/></a>&#160;|&#160;
                <a href="JavaScript:searchEvents()"><xsl:value-of select="java:getString($dict_event_module, 'EVENT_SEARCH')"/></a>
            </td>
        </tr>
    </table>
    <table class="admNavPanel" border="0" cellpadding="0" cellspacing="0">
        <tr>
            <td class="admNavbarImg"><img src="/images/titleLeft.gif" alt="" border="0"/></td>
            <td class="admTitle admLeft" align="center" style="font-size: 12px;">
                <a class="admNone" href='javascript:prevYear();'><small>&lt;&lt;&lt;</small><xsl:value-of select="java:getString($dict_event_module, 'PREV')"/></a>
            </td>
            <td class="admTitle">
                <b>Year View for <script>document.write(calendar[0].Year);</script></b>
            </td>
            <td class="admTitle admRight" align="center" style="font-size: 12px;">
                <a class="admNone" href="javascript:nextYear();">
                    <xsl:value-of select="java:getString($dict_event_module, 'NEXT')"/><small>&gt;&gt;&gt;&gt;</small>
                </a>
            </td>
            <td class="admNavbarImg">
                <img src="/images/titleRight.gif" alt="" border="0"/>
            </td>
        </tr>
    </table>
    <table border="0">
        <tr height="100">
            <td width="100" id="januaryTD">
                <script>
                    januaryTD.innerHTML = calendar[0].renderMonth();
                </script>
            </td>
            <td id="februaryTD" width="100" style="padding-bottom : 0px; padding-left : 20px; padding-right : 20px">
                <script>
                    februaryTD.innerHTML = calendar[1].renderMonth();
                </script>
            </td>
            <td id="marchTD" style="text-align: right; width: 100px">
                <script>
                    marchTD.innerHTML = calendar[2].renderMonth();
                </script>
            </td>
        </tr>
        <tr height="100">
            <td width="100" id="aprilTD">
                <script>
                    aprilTD.innerHTML = calendar[3].renderMonth();
                </script>
            </td>
            <td id="mayTD" width="100" style="padding-bottom : 0px; padding-left : 20px; padding-right : 20px">
                <script>
                    mayTD.innerHTML = calendar[4].renderMonth();
                </script>
            </td>
            <td id="juneTD" style="text-align: right; width: 100px">
                <script>
                    juneTD.innerHTML = calendar[5].renderMonth();
                </script>
            </td>
        </tr>
        <tr height="100">
            <td width="100" id="julyTD">
                <script>
                    julyTD.innerHTML = calendar[6].renderMonth();
                </script>
            </td>
            <td id="augustTD" width="100" style="padding-bottom : 0px; padding-left : 20px; padding-right : 20px">
                <script>
                    augustTD.innerHTML = calendar[7].renderMonth();
                </script>
            </td>
            <td id="septemberTD" style="text-align: right; width: 100px">
                <script>
                    septemberTD.innerHTML = calendar[8].renderMonth();
                </script>
            </td>
        </tr>
        <tr height="100">
            <td width="100" id="octoberTD">
                <script>
                    octoberTD.innerHTML = calendar[9].renderMonth();
                </script>
            </td>
            <td id="novemberTD" width="100" style="padding-bottom : 0px; padding-left : 20px; padding-right : 20px">
                <script>
                    novemberTD.innerHTML = calendar[10].renderMonth();
                </script>
            </td>
            <td id="decemberTD" style="text-align: right; width: 100px">
                <script>
                    decemberTD.innerHTML = calendar[11].renderMonth();
                </script>
            </td>
        </tr>
    </table>
    <form method="POST" name="categoriesForm" enctype="multipart/form-data">
        <input type="hidden" name="command" value="get-event-schedule-command"/>
        <input type="hidden" name="startDate"/>
        <input type="hidden" name="period" value="year"/>
        <input type="hidden" name="list" value="false"/>
    </form>
</xsl:template>

<xsl:template match="negeso:event">
    <xsl:attribute name="bgcolor">#C9E7C4</xsl:attribute>
    <a style="color : blue;" href="#"><xsl:value-of select="@name"/></a>&#160;&#160;<xsl:value-of select="@text"/>
</xsl:template>

</xsl:stylesheet>
