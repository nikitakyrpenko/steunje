<!--
  @(#)${critical_error.xsl}		
 
  Copyright (c) 2003 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.
 
  View of a list item edit interface.
 
  @version		2004.01.20
  @author		Olexiy.Strashko
  @author     Volodymyr Snigur
-->
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:negeso="http://negeso.com/2003/Framework"
	xmlns:java="http://xml.apache.org/xslt/java"
	exclude-result-prefixes="java">
	
<xsl:include href="/xsl/negeso_header.xsl"/>
<xsl:template name="BACK"><!-- To prevent errors --></xsl:template>
<xsl:template name="CLOSE"><!-- To prevent errors --></xsl:template>
<xsl:template match="/">
    <html>
    <head>
	<title>NEGESO ::: Resource not found :::</title>
        <META http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
    </head>
    <body>
        <!-- NEGESO HEADER -->
        <xsl:call-template name="NegesoHeader"/>
        <div class="admCenter">
            <!-- CONTENT -->
        	    <table cellspacing="0" cellpadding="0">
                <tr>
                  <td colspan="2" class="admCenter admLocation">
                    Negeso w/CMS
                  </td>
                </tr>
                <tr class="admLocation">
                  <td class="admRight" width="50%">
                    a brand of ICT Waterproof B.V.<br/>
                    www.negeso.com www.waterproof.nl<br/>
                    support@waterproof.nl
                  </td>
                  <td class="admLeft">
                    support@waterproof.nl<br/>
                    030-6354058
                  </td>
                </tr>
                <tr>
                	 	<td class="admBackLogo" colspan="2">
                			<table cellspacing="0" cellpadding="0">
                			    <tr class="admBold">
                				      <td class="admCenter admBigFont" style="color: #7FB577"><br/><br/>Resource not found</td>
                			    </tr>
                		  	</table>
                		</td>
        	        </tr>
         	     <tr>
                 <td colspan="2" class="admCenter admCopyright">&#xA9; ICT Waterproof B.V. 2004-2010</td>
        	        </tr>
            </table>
        </div>
    </body>
    </html>
</xsl:template>
</xsl:stylesheet> 
