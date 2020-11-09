<?xml version="1.0" encoding="UTF-8"?>
<!--
  Copyright (c) 2007 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information"). You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.


  XSL templates for Site Core
 
  @version		2007.11.27
  @author		Oleg 'germes' Shvedov
  @author		Rostislav 'KOTT' Brizgunov
-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        >

<!-- ADMIN PART ENTRY POINT FROM END-USER PART OF PRODUCT MODULE --> 
<xsl:template match="negeso:pm" mode="admin_part_entry_point">
<div id="list{@id}" 
        style="z-index:0;" 
        width="100%" 
        title="Edit product module"
>
        <table>
                <tr><td style="padding-left: 10px;">
           		<xsl:if test="($outputType = 'admin') and not(/negeso:page/@role-id = 'visitor')">	
                <a href="#">
                    <img id="organize_widget_img" border="0" align="center" src="/images/mark_1.gif"
                        onClick="window.open(
                            '?command=pm-browse-category',
                            'ProductModule', 
                            'top=50,left=50,height=600,width=800,status=0,toolbar=0,menubar=0,location=0,resizable=1,scrollbars=yes'
                        )"
                    />
                </a>
                </xsl:if>
                </td>
                </tr>
        </table>
</div>
</xsl:template>

<!-- ADMIN PART ENTRY POINT FROM END-USER PART OF NEWS, NEWSLINE, FAQ, WEBPOLL MODULES (etc.) --> 
<xsl:template match="negeso:list" mode="admin_part_entry_point">
<xsl:param name="type" select="2" />
<xsl:param name="listPath" select="0" />
<div id="list{@id}" width="100%" title="Edit news list">
    <table>
    <xsl:choose>
        <xsl:when test="$type=2">
                <tr><td style="padding-left: 10px; vertical-align: top; background-color: transparent;" height="23">
                <a href="#">
                	<xsl:choose>
                		<xsl:when test="number($listPath) = 0">
		               		<xsl:if test="($outputType = 'admin') and not(/negeso:page/@role-id = 'visitor')">	
								<img id="organize_widget_img" border="0" align="center" src="/images/mark_1.gif"
								onClick="window.open(
								    '?command=get-list-command&amp;listId={@id}',
								    '{@type}', 
								    'top=50,left=50,height=600,width=800,status=0,toolbar=0,menubar=0,location=0,resizable=1,scrollbars=yes'
								)"
								/>
							</xsl:if>
                		</xsl:when>
                		<xsl:otherwise>
		               		<xsl:if test="($outputType = 'admin') and not(/negeso:page/@role-id = 'visitor')">	
								<img id="organize_widget_img" border="0" align="center" src="/images/mark_1.gif"
									onClick="window.open(
									    '?command=get-list-command&amp;listId={@id}&amp;listPath=0',
									    '{@type}',
									    'top=50,left=50,height=600,width=800,status=0,toolbar=0,menubar=0,location=0,resizable=1,scrollbars=yes'
										)"
									/>
							</xsl:if>		
                		</xsl:otherwise>
                	</xsl:choose>
                </a>
                </td>
                </tr>
        </xsl:when>
        <xsl:when test="$type=4">
                <tr><td style="padding-left: 10px; vertical-align: top;" height="23">
           		<xsl:if test="($outputType = 'admin') and not(/negeso:page/@role-id = 'visitor')">	
	                <a href="#">
		                    <img id="organize_widget_img" border="0" align="center" src="/images/mark_1.gif"
		                        onClick="window.open(
		                            '?command=get-list-command&amp;listId={@id}',
		                            '{@type}', 
		                            'top=50,left=50,height=600,width=800,status=0,toolbar=0,menubar=0,location=0,resizable=1,scrollbars=yes'
		                        )"
		                    />
	                </a>
	            </xsl:if>   
                </td>
                </tr>
        </xsl:when>
        <xsl:when test="$type=1">
            <tr><td style="color:red;">
                <h2><xsl:value-of select="@name"/>:</h2>
            </td></tr>
                <tr><td style="padding-left: 10px;">
           		<xsl:if test="($outputType = 'admin') and not(/negeso:page/@role-id = 'visitor')">	
	                <a href="#">
	                    <img id="organize_widget_img" border="0" align="center" src="/images/mark_1.gif" 
	                        onClick="window.open(
	                            '?command=get-list-command&amp;listId={@id}', 
	                            'first', 
	                            'top=50,left=50,height=600,width=800,status=0,toolbar=0,menubar=0,location=0,resizable=1,scrollbars=yes'
	                        )"
	                    />
	                </a>
                </xsl:if>
                </td>
                </tr>
        </xsl:when>
        <xsl:when test="$type=3">
                <tr><td style="padding-left: 10px;">
           		<xsl:if test="($outputType = 'admin') and not(/negeso:page/@role-id = 'visitor')">	
	                <a href="#">
	                    <img id="organize_widget_img" border="0" align="center" src="/images/mark_1.gif"
	                        onClick="window.open(
	                            '?command=get-list-command&amp;listId={@id}&amp;listPath=0;0',
	                            '{@type}', 
	                            'top=50,left=50,height=600,width=800,status=0,toolbar=0,menubar=0,location=0,resizable=1,scrollbars=yes'
	                        )"
	                    />
	                </a>
	            </xsl:if>    
                </td>
                </tr>
        </xsl:when>
        <xsl:when test="$type=0">
            <tr><td style="color:red;">
                <h3><xsl:value-of select="@name"/>:</h3>
            </td></tr>
                <tr><td style="padding-left: 20px;">
           		<xsl:if test="($outputType = 'admin') and not(/negeso:page/@role-id = 'visitor')">	
	                <a href="#">
	                    <img id="organize_widget_img" border="0" align="center" src="/images/mark_1.gif" 
	                        onClick="window.open(
	                            '?command=get-list-command&amp;listId={@id}', 
	                            'third', 
	                            'top=50,left=50,height=600,width=800,status=0,toolbar=0,menubar=0,location=0,resizable=1,scrollbars=yes',
	                            true
	                        )"
	                    />
	                </a>
	            </xsl:if>    
                </td>
                </tr>
        </xsl:when>
    </xsl:choose>
    </table>
</div>
</xsl:template>

<!-- UNKNOWN PURPOSE -->
<!--<xsl:template match="negeso:tree_list" >
    <xsl:param name="type" select="2" />
    <div id="list{@id}" width="100%" title="Edit news list">
        <table>
            <tr>
            <td style="padding-left: 10px;">
           		<xsl:if test="($outputType = 'admin') and not(/negeso:page/@role-id = 'visitor')">	
	                <a href="#">
	                    <img id="organize_widget_img" border="0" align="center" src="/images/mark_1.gif"
	                        onClick="window.open(
	                            '?command=get-list-command&amp;listId={@id}&amp;listPath=0;0',
	                            '{@type}', 
	                            'top=50,left=50,height=600,width=800,status=0,toolbar=0,menubar=0,location=0,resizable=1,scrollbars=yes'
	                        )"
	                    />
	                </a>
	           </xsl:if>     
            </td>
            </tr>
        </table>
    </div>
</xsl:template>-->

<!-- UNKNOWN PURPOSE -->
<!--<xsl:template match="negeso:listItem" >
    <tr><td style="border: solid 2px; font: bold 12px verdana, sans-serif, arial; background-color: #bfcfbf">
        <xsl:choose>
            <xsl:when test="@imageLink">
                <img hspace="5" vspace="5" align="left">
                        <xsl:attribute name="src">../<xsl:value-of select="@imageLink"/>
                        </xsl:attribute>
                </img>
            </xsl:when>
        </xsl:choose>
    
        <xsl:choose>
            <xsl:when test="@viewDate">
                <xsl:value-of select="@viewDate"/> :
            </xsl:when>
        </xsl:choose>
        <xsl:value-of select="@title"/>
    </td></tr>
    <tr><td>
        <xsl:apply-templates select="negeso:teaser"/>
    </td></tr>
    <tr><td>
        <xsl:choose>
            <xsl:when test="@href">
                <a><xsl:attribute name="href"><xsl:value-of select="@href" disable-output-escaping="yes"/></xsl:attribute>
                    more...
                </a>
            </xsl:when>
        </xsl:choose>
    </td></tr>
</xsl:template>-->

</xsl:stylesheet>
