<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)${search.xsl}       
 
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

<xsl:variable name="lang" select="/*/@interface-language"/>
<xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>
<xsl:variable name="dict_event_module" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_event_module.xsl', $lang)"/>

<xsl:template match="/">
<html>
<head>
    <title><xsl:value-of select="java:getString($dict_event_module, 'EVENT_CALENDAR')"/></title>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" src="/script/jquery.min.js" />
	<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
    <script type="text/javascript" src="/dictionaries/dict_calendar_{$lang}.js"/>
    <script type="text/javascript" src="/script/event_calendar.js"/>
    <script type="text/javascript" src="/script/common_functions.js"/>
    <script language="JavaScript1.2" src="/script/calendar_picker.js" type="text/javascript"/>

    <style type="text/css">
        a:link{color:#000000;}
        a:visited{color:#000000;}
    </style>

    <script language="JavaScript">
        var s_FillStartDate = "<xsl:value-of select="java:getString($dict_event_module, 'FILL_START_DATE')"/>";
        var s_FillEndDate = "<xsl:value-of select="java:getString($dict_event_module, 'FILL_END_DATE')"/>";
        var s_InvalidStartDate = "<xsl:value-of select="java:getString($dict_event_module, 'INVALID_START_DATE')"/>";
        var s_InvalidEndDate = "<xsl:value-of select="java:getString($dict_event_module, 'INVALID_END_DATE')"/>";
        var s_EndDateMustBeBigger = "<xsl:value-of select="java:getString($dict_event_module, 'END_DATE_MUST_BE_LATER')"/>";
        var s_EndTimeMustBeBigger = "<xsl:value-of select="java:getString($dict_event_module, 'END_TIME_MUST_BE_LATER')"/>";
        var calendar = new Calendar(new Date());

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

            function onSubmit() {
                if (mainForm.searchStartDate.value == '') {
                    alert(s_FillStartDate);
                    return;
                }
                if (mainForm.searchEndDate.value == '') {
                    alert(s_FillEndDate);
                    return;
                }
                if (!StringUtil.validateDate(mainForm.searchStartDate.value)) {
                    alert(s_InvalidStartDate);
                    return;
                }
                if (!StringUtil.validateDate(mainForm.searchEndDate.value)) {
                    alert(s_InvalidEndDate);
                    return;
                }
                var startDate = getDate(mainForm.searchStartDate.value).getTime();
                var endDate = getDate(mainForm.searchEndDate.value).getTime();
                if (startDate > endDate) {
                    alert(s_EndDateMustBeBigger);
                    return;
                }
                mainForm.searchCategoryId.value = mainForm.category.value;
                if (mainForm.searchCategoryId.value == "any")
                    mainForm.searchCategoryId.value = 0;
                mainForm.submit();
            }
            ]]>
        </xsl:text>
    </script>
</head>
<body>
    <!-- NEGESO HEADER -->
    <xsl:call-template name="NegesoHeader">
        <xsl:with-param name="helpLink">
            <xsl:text>/admin/help/cev1_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>
        </xsl:with-param>
	</xsl:call-template>
    <div align="center">
        <xsl:call-template name="NavBar">
            <xsl:with-param name="backLink" select="'?command=get-event-schedule-command'"/>
        </xsl:call-template>
       <!-- CONTENT -->
        <xsl:apply-templates select="negeso:events_search"/>
        <xsl:call-template name="NavBar">
            <xsl:with-param name="backLink" select="'?command=get-event-schedule-command'"/>
        </xsl:call-template>
    </div>
</body>
</html>
</xsl:template>

<xsl:template match="/negeso:events_search">
    <br/>
    <table class="admNavPanel" border="0" cellpadding="0" cellspacing="0">
        <tr>
            <td class="admNavbarImg"><img src="/images/titleLeft.gif" alt="" border="0"/></td>
            <td class="admTitle">
                <b><xsl:value-of select="java:getString($dict_event_module, 'EVENT_SEARCH')"/></b>
            </td>
            <td class="admNavbarImg">
                <img src="/images/titleRight.gif" alt="" border="0"/>
            </td>
        </tr>
    </table>
    <form method="POST" name="mainForm" enctype="multipart/form-data" action="">
        <input type="hidden" name="command" value="search-events-command"/>
        <input type="hidden" name="searchCategoryId"/>

        <table width="757">
            <tr>
                <td class="admMainTD admWidth100"><xsl:value-of select="java:getString($dict_common, 'NAME')"/></td>
                <td class="admLightTD admWidth100">
                    <input class="admTextArea admWidth200" name="searchEventName" value="{@eventName}" type="text"/>
                </td>
                <td class="admMainTD admWidth100"><xsl:value-of select="java:getString($dict_common, 'CATEGORY')"/></td>
                <td class="admLightTD admWidth100">
                    <select style="float: none; margin-left: 5" name="category">
                        <option value="any"><xsl:value-of select="java:getString($dict_event_module, 'ANY')"/></option>
                        <xsl:apply-templates select="negeso:categories/negeso:category">
                            <xsl:with-param name="selected" select="@categoryId"/>
                        </xsl:apply-templates>
                    </select>
                </td>
            </tr>
            <tr>
                <td class="admMainTD admWidth100"><xsl:value-of select="java:getString($dict_event_module, 'START_DATE')"/></td>
                <td class="admLightTD">
                    <input class="admTextArea admWidth200" type="text" name="searchStartDate" id="startDateFieldId" value="{@startDate}" readonly="true">
                        <xsl:attribute name="value"><xsl:value-of select="@startDate"/></xsl:attribute>
                        <img class="admHand" src="/images/calendar.gif" width="16" height="16" onclick="loadNewCalendar('/dialogs/dialog_calendar_picker.html','startDateFieldId','yyyymmdd',false)">
                            <xsl:attribute name="alt"><xsl:value-of select="java:getString($dict_common, 'PICK_DATE')"/></xsl:attribute>
                        </img>
                    </input>
                </td>
                <td class="admMainTD admWidth100"><xsl:value-of select="java:getString($dict_event_module, 'END_DATE')"/></td>
                <td class="admLightTD">
                    <input class="admTextArea admWidth200" type="text" name="searchEndDate" id="endDateFieldId" value="{@endDate}" readonly="true">
                        <xsl:attribute name="value"><xsl:value-of select="@endDate"/></xsl:attribute>
                        <img class="admHand" src="/images/calendar.gif" width="16" height="16" onclick="loadNewCalendar('/dialogs/dialog_calendar_picker.html','endDateFieldId','yyyymmdd',false)">
                            <xsl:attribute name="alt"><xsl:value-of select="java:getString($dict_common, 'PICK_DATE')"/></xsl:attribute>
                        </img>
                    </input>
                </td>
            </tr>
            <tr>
                <td class="admNavPanel admNavbar admCenter" colspan="4">
                    <input name="saveButton" class="admNavbarInp" type="button" onClick="onSubmit()">
                        <xsl:attribute name="value">&lt;&#160;<xsl:value-of select="java:getString($dict_common, 'SEARCH')"/>&#160;&gt;</xsl:attribute>
                    </input>
                </td>
            </tr>
        </table>
    </form>
    <xsl:if test="not(@startDate)">
        <script>
            mainForm.searchStartDate.value = calendar.formatDate();
            mainForm.searchEndDate.value = calendar.formatDate();
       </script>
    </xsl:if>
    <xsl:if test="@results">
        <br/>
        <table class="admNavPanel" border="0" cellpadding="0" cellspacing="0">
            <tr>
                <td class="admNavbarImg"><img src="/images/titleLeft.gif" alt="" border="0"/></td>
                <td class="admTitle">
                    <b><xsl:value-of select="@results"/>&#160;<xsl:value-of select="java:getString($dict_common, 'SEARCH_RESULTS')"/></b>
                </td>
                <td class="admNavbarImg">
                    <img src="/images/titleRight.gif" alt="" border="0"/>
                </td>
            </tr>
        </table>
        <table width="757">
            <tr height="24" bgColor="#9D9D9D">
                <td align="center" width="80"><font face="Verdana" size="2" color='white'><b><xsl:value-of select="java:getString($dict_event_module, 'EVENT')"/></b></font></td>
                <td align="center"><font face="Verdana" size="2" color="white"><b><xsl:value-of select="java:getString($dict_common, 'DATE')"/> / <xsl:value-of select="java:getString($dict_common, 'TIME')"/></b></font></td>
                <td align="center"><font face="Verdana" size="2" color="white"><b><xsl:value-of select="java:getString($dict_common, 'CATEGORY')"/></b></font></td>
            </tr>
            <xsl:apply-templates select="negeso:event"/>
            <xsl:if test="not(negeso:event)">
                <tr height="100%"/>
            </xsl:if>
        </table>
    </xsl:if>
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

<xsl:template match="negeso:event">
    <tr bgcolor="#C9E7C4">
        <xsl:variable name="category" select="@categoryId"/>
        <td><a style="color : blue;" href="/admin/event_details?search&amp;id={@id}"><xsl:value-of select="@name"/></a></td>
        <td>
            <xsl:value-of select="@startYear"/>&#160;
            <script>
                document.write(calendar.getMonthName(<xsl:value-of select="@startMonth"/>-1), ", ");
            </script>
            <xsl:value-of select="@startDay"/>
            <xsl:if test="@startYear != @endYear">
                &#160;<xsl:value-of select="@endYear"/>&#160;
            </xsl:if>
            -
            <xsl:if test="@startMonth != @endMonth">
                <script>
                    document.write(' ', calendar.getMonthName(<xsl:value-of select="@endMonth"/>-1), ", ");
                </script>
            </xsl:if>
            <xsl:if test="@startDay != @endDay">
                <xsl:value-of select="@endDay"/>
            </xsl:if>
            <xsl:if test="@startTime != ''">
                &#160;
                <xsl:value-of select="@startTime"/>-<xsl:value-of select="@endTime"/>
            </xsl:if>            
        </td>
        <td>
            <xsl:value-of select="/negeso:events_search/negeso:categories/negeso:category[@id = $category]/@name"/>
        </td>
    </tr>
</xsl:template>

</xsl:stylesheet>
