<!--
  @(#)$Id$		
 
  Copyright (c) 2003 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.
 
  View of a list item edit interface.
 
  @author		Olexiy.Strashko
  @version		$Revision$
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:negeso="http://negeso.com/2003/Framework"
	xmlns:java="http://xml.apache.org/xslt/java"
	exclude-result-prefixes="java">

<xsl:include href="/xsl/negeso_header.xsl"/>
<xsl:template name="BACK"><!-- To prevent errors --></xsl:template>
<xsl:template name="CLOSE"><!-- To prevent errors --></xsl:template>
<xsl:template match="negeso:page">
    <html>
    <head>
	<title>NEGESO ::: Version info :::</title>
        <META http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
    </head>
    <body>
        <!-- NEGESO HEADER -->
        <xsl:call-template name="NegesoHeader"/>
        <div class="admCenter">
            <!-- CONTENT -->
        	    <table cellspacing="0" cellpadding="0">
        	        <<tr>
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
                			    <tr class="admBold" >
                				      <td class="admRight" style="font-size: 11px; color: #999999; font-style: italic;">
	                				      Version 
	                				      <span class="admLeft admBlue" style="font-size:14px;">
	                				           <xsl:value-of select="negeso:version-info/@id"/> 			
	                				      </span>
	               				      </td>
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
