<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">

    <xsl:include href="/xsl/negeso_body.xsl"/>

    <xsl:variable name="lang" select="/*/@interface-language"/>
    <xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>
    <xsl:variable name="dict_news_module" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_news_module.xsl', $lang)"/>

    <xsl:template match="/">
        <html>
            <head>
                <title>
                    <xsl:value-of select="java:getString($dict_news_module, 'SELECT_NEWS_LIST')"/>
                </title>
                <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
                <script type="text/javascript" src="/script/jquery.min.js"></script>
				<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
                <script type="text/javascript" src="/script/cufon-yui.js"></script>
                <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
                <script type="text/javascript" src="/script/common_functions.js"></script>
                <script language="JavaScript">
                    function returnChosenList() {
                    var result = new Array();
                    result[0] = mainForm.listId.value;
                    result[1] = mainForm.copy.checked;
                    window.returnValue = result;
                    window.close();
                    }
                </script>
            </head>
            <body class="dialogSmall" style="padding-left:5px;">
                <xsl:call-template name="NegesoBody">
                    <xsl:with-param name="showHelp" select="'no'"/>
                </xsl:call-template>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="negeso:listsroll" mode="admContent">
        <form method="POST" name="mainForm">
            <table  border="0" cellpadding="0" cellspacing="0" width="100%" class="admTable">
                <tr>
                    <td align="center" class="admNavPanelFont" colspan="2">
                        <xsl:value-of select="java:getString($dict_news_module, 'SELECT_NEWS_LIST')"/>
                    </td>
                </tr>
                <tr>
                    <th class="admTableTDLast"  colspan="2">
                        <select name="listId" style="width: 80%; font-size: 14px;">
                            <xsl:apply-templates select="negeso:list"/>
                        </select>
                    </th>
                </tr>
                <tr>
                    <td class="admTableTDLast" style="color: #FF2B04; font-size: 14px; padding-left:20px;" colspan="2">
                        <input type="checkbox" name="copy">
                            <xsl:value-of select="java:getString($dict_common, 'COPY')"/>
                        </input>
                    </td>
                </tr>
                <tr>
                    <td class="admTableTDLast">
                        <div class="admNavPanelInp"  style="padding-left:20px;" >
                            <div class="imgL"></div>
                            <div align="center">
                                <button class="admNavbarInp" onClick="returnChosenList();">
                                    <xsl:value-of select="java:getString($dict_common, 'MOVE')"/>
                                </button>
                            </div>
                            <div class="imgR"></div>
                        </div>

                        <div class="admNavPanelInp" style="padding-left:20px;">
                            <div class="imgL"></div>
                            <div align="center">
                                <button class="admNavbarInp" name="submitBtn" onclick="window.close();">
                                    <xsl:value-of select="java:getString($dict_common, 'CANCEL')"/>
                                </button>
                            </div>
                            <div class="imgR"></div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td class="admTableFooter">&#160;</td>
                </tr>
            </table>
        </form>
    </xsl:template>

    <xsl:template match="negeso:list">
        <option value="{@id}">
            <xsl:value-of select="@name"/>
        </option>
    </xsl:template>

</xsl:stylesheet>
