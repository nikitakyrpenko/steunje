<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)$Id$

  Copyright (c) 2005 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential
  Information and shall use it only in accordance with the terms of the
  license agreement you entered into with Negeso.

  Edit location

  @version      $Revision$
  @author       Olexiy.Strashko
-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">

<xsl:include href="/xsl/negeso_body.xsl"/>

<xsl:variable name="lang"><xsl:value-of select="/*/@interface-language"/></xsl:variable>
<xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>
<xsl:variable name="dict_contact_book" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_contact_book.xsl', $lang)"/>

<!-- NEGESO JAVASCRIPT Temaplate -->	
<xsl:template name="java-script">
	<script language="JavaScript">
	<xsl:text disable-output-escaping="yes">
	    <![CDATA[
        $(function () {
            $("#birthdayId").datepicker({
                dateFormat: 'dd-mm-yy',                
                showOn: 'button',
                buttonImage: '/images/calendar.gif',
                buttonImageOnly: true
            });	

            $('input[name="clearImage"]').click(function(){                
                $('#imageDivId img').remove();
                $('#no_image').show();
                $('#imageFieldId').val('');

                return false;
            });
        });

		function update(targetId) {
            if ( !validateForm(operateFormId) ){
                return false;
            }
        	return true;
		}

        function selectImageDialog(imageFieldObject, imgTag, width, height){
            result = MediaCatalog.selectImageDialog(width, height);
            if (result != null){
                if (result.resCode == "OK"){                    
                    document.mainForm.imageLink.value = result.realImage;
                    document.all.photoImage.outerHTML = 
                        "<img hspace='5' vspace='5' id='photoImage' " + 
                             "src='../" + result.realImage +"'>";
 					document.mainForm.clearImageButton.disabled = false;                    
                }
            }			
		}
		
		function resultUploadImage(){
	        var result = returnValue;
	        var imageFieldObject = document.getElementById('imageFieldId');
	        var imgTag = document.getElementById('imgTagId');
	        imageFieldObject.value = result.realImage;
	        imgTag.src = result.realImage;
			imgTag.width = result.realImageWidth;
			imgTag.height = result.realImageHeight;
			document.getElementById('imageDivId').style.display = "inline";
			try{
				document.getElementById('no_image').style.display = "none";
			}
			catch(e){}
		}
		]]>
		</xsl:text>

<!-- START OF 1 of 3: script for <textarea> maxlength -->
<!--
	<xsl:text disable-output-escaping="yes">
	    <![CDATA[
function setMaxLength() {
	var x = document.getElementsByTagName('textarea');
	var counter = document.createElement('div');
	counter.className = 'counter';
	for (var i=0;i<x.length;i++) {
		if (x[i].getAttribute('maxlength')) {
			var counterClone = counter.cloneNode(true);
			counterClone.relatedElement = x[i];
			counterClone.innerHTML = '<span>0</span>/'+x[i].getAttribute('maxlength');
			x[i].parentNode.insertBefore(counterClone,x[i].nextSibling);
			x[i].relatedElement = counterClone.getElementsByTagName('span')[0];

			x[i].onkeyup = x[i].onchange = checkMaxLength;
			x[i].onkeyup();
			x[i].onpaste = x[i].onchange = checkMaxLength;
			x[i].onpaste();
		}
	}
}

function checkMaxLength() {
	var maxLength = this.getAttribute('maxlength');
	var currentLength = this.value.length;
	if (currentLength > maxLength)
		this.value=this.value.substr(0, (maxLength));
	this.relatedElement.firstChild.nodeValue = currentLength;
	// not innerHTML
}
		]]>
		</xsl:text>
-->
<!-- FINISH OF 1 of 3: Example for <textarea> -->

	</script>
</xsl:template>
	
<xsl:template match="/" >
<html>
    <head>
        <title><xsl:value-of select="java:getString($dict_contact_book, 'EDIT_CONTACT')"/></title>
        <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
        <link rel="stylesheet" type="text/css" href="/css/jquery-ui.custom.css"/>
        <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
        <script type="text/javascript" src="/script/jquery.min.js"></script>
        <script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
        <script type="text/javascript" src="/script/cufon-yui.js"></script>
        <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
        <script language="JavaScript" src="/script/common_functions.js" type="text/javascript">/**/</script>
        <script language="JavaScript1.2" src="/script/media_catalog.js" type="text/javascript"/>
	    <xsl:call-template name="java-script"/>                
    </head>
    <body>
        <!-- START OF 2 of 3: script for <textarea> maxlength -->
        <!-- <xsl:attribute name="onload">setMaxLength()</xsl:attribute> -->
        <!-- FINISH OF 2 of 3: Example for <textarea> -->
        
        <xsl:call-template name="NegesoBody">
            <xsl:with-param name="helpLink" select="''"/>
            <xsl:with-param name="backLink">
                <xsl:choose>
                    <xsl:when test="count(descendant::negeso:page/negeso:page) = 0 and negeso:page/@back-link='search'">
                        <xsl:text>'contact_book'</xsl:text>
                    </xsl:when>
                    <xsl:when test="count(descendant::negeso:page/negeso:page) = 0">
                        <xsl:value-of select="concat('/admin/?command=cb-manage-contacts&amp;groupId=', negeso:page/negeso:contact/@group-id)"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text>''</xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:with-param>
        </xsl:call-template>
    </body>
</html>
</xsl:template>

<xsl:template match="negeso:page" mode="admContent">    
    <form method="POST" name="operateForm" id="operateFormId" action="" enctype="multipart/form-data" >
        <table cellspacing="0" cellpadding="0" border="0" align="center" width="100%" class="admTable">
            <tr>
                <td style="width:auto; height:auto; padding-left:20px;">
                    <!-- PATH -->
                    <xsl:call-template name="cb.groupPath"/>
                </td>
            </tr>
            <tr>
                <td class="admNavPanelFont" align="center">
                    <!-- TITLE -->
                    <xsl:call-template name="tableTitle">
                        <xsl:with-param name="headtext">
                            <xsl:choose>
                                <xsl:when test="count(negeso:contact/@id) = 0">
                                    <xsl:value-of select="java:getString($dict_contact_book, 'ADD_CONTACT')"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="java:getString($dict_contact_book, 'EDIT_CONTACT')"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:with-param>
                    </xsl:call-template>
                </td>
            </tr>
            <tr>
                <td>
                    <div class="admCenter">
                        <font color="#FF0000">
                            <xsl:value-of select="errorMessage"/>
                        </font>
                    </div>
                </td>
            </tr>
            <tr>
                <td>
                    <xsl:apply-templates select="negeso:contact"/>
                </td>
            </tr>           
            <tr>
                <td class="admTableFooter">
                    <xsl:call-template name="buttons"/>
                </td>
            </tr>
        </table>
        
    </form>   
</xsl:template>

<xsl:template name="buttons">
    <!-- Update/reset fields -->
    <table cellpadding="0" cellspacing="0"  width="764px"  align="center" border="0" class="Buttons">
        <tr>
            <td>
                <div class="admBtnGreenb" style="margin-top:15px; position:absolute;">
                    <div class="imgL"></div>
                    <div>
                        <xsl:choose>
                            <xsl:when test="count(negeso:contact/@id) = 0">
                                <input class="admBtnText" name="saveButton" onClick="return update('-1');" type="submit">
                                    <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_common, 'ADD')"/></xsl:attribute>
                                </input>                                
                            </xsl:when>
                            <xsl:otherwise>
                                <input class="admBtnText" name="saveButton" onClick="return update('{negeso:contact/@id}')" type="submit">
                                    <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_common, 'SAVE')"/></xsl:attribute>
                                </input>                                
                            </xsl:otherwise>
                        </xsl:choose>
                    </div>
                    <div class="imgR"></div>
                </div>
                
                <div class="admBtnGreenb" style="margin:15px 0 0 93px; position:absolute;">
                    <div class="imgL"></div>
                    <div>
                        <input class="admBtnText" type="reset">
                            <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_common, 'RESET')"/></xsl:attribute>
                        </input>
                    </div>
                    <div class="imgR"></div>
                </div>
            </td>
        </tr>
    </table>
</xsl:template>
	
<!-- ********************************** Category *********************************** -->
<xsl:template match="negeso:contact">
    <table cellspacing="0" cellpadding="0" border="0" width="100%">
        <tr>
            <td colspan="2" class="admTDtitles">
                <input type="hidden" name="command" value="cb-manage-contacts"></input>
                <input type="hidden" name="action" value="save_contact"></input>
                <input type="hidden" name="contactId" value="{@id}"></input>
                <input type="hidden" name="groupId" value="{@group-id}"></input>

                <h1>
                    <xsl:value-of select="java:getString($dict_contact_book, 'CONTACT_PROPERTIES')"/>:
                </h1>
            </td>
        </tr>
        <!-- FIRST_NAME -->
        <tr>
            <th class="admTableTD admWidth150">
                <xsl:value-of select="java:getString($dict_common, 'FIRST_NAME')"/>*
            </th>
            <th class="admTableTDLast admLeft">
                <input class="admTextArea admWidth200"
                    type="text"
                    name="firstName"
                    data_type="text"
                    required="true"
                    MAXLENGTH="50"
			>
                    <xsl:attribute name="uname">
                        <xsl:value-of select="java:getString($dict_common, 'FIRST_NAME')"/>
                    </xsl:attribute>
                    <xsl:attribute name="value">
                        <xsl:value-of select="@first-name"/>
                    </xsl:attribute>
                </input>
            </th>
        </tr>
        <!-- SECOND_NAME -->
        <tr>
            <th class="admTableTD admWidth150">
                <xsl:value-of select="java:getString($dict_common, 'SECOND_NAME')"/>*
            </th>
            <th class="admTableTDLast admLeft">
                <input class="admTextArea admWidth200"
                    type="text"
                    name="secondName"
                    data_type="text"
                    required="true"
                    MAXLENGTH="50"
			>
                    <xsl:attribute name="uname">
                        <xsl:value-of select="java:getString($dict_common, 'SECOND_NAME')"/>
                    </xsl:attribute>
                    <xsl:attribute name="value">
                        <xsl:value-of select="@second-name"/>
                    </xsl:attribute>
                </input>
            </th>
        </tr>
        <!-- IMAGE -->
        <tr>
            <th class="admTableTD">
                <xsl:value-of select="java:getString($dict_common, 'IMAGE')"/>
            </th>
            <th class="admTableTDLast admLeft">
                <table>                   
                    <tr>
                        <td class="admWidth200">                            
                            <div id="no_image">
                                <xsl:if test="count(@image-link) != 0">
                                    <xsl:attribute name="style">display:none;</xsl:attribute>
                                </xsl:if>
                                No image</div>                            
                            <div id="imageDivId">
                                <xsl:if test="count(@image-link) = 0">
                                    <xsl:attribute name="style">display:none</xsl:attribute>
                                </xsl:if>
                                <img id="imgTagId" src="{@image-link}" class="admBorder admHand"/>
                            </div>
                        </td>
                        <td aligh="left">
                            <input type="hidden" id="imageFieldId" name="imageLink" value="{@image-link}" MAXLENGTH="100"></input>
                            
                            <div style="padding:0;margin:0;" class="admNavPanelInp">
                                <div class="imgL"></div>
                                <div>
                                    <input class="admNavbarInp" name="selectImageButton" type="button"
                                           onClick="selectImageDialog(imageFieldId, imgTagId, {@image-width}, {@image-height})">
                                        <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_common, 'SELECT')"/></xsl:attribute>
                                    </input>
                                </div>
                                <div class="imgR"></div>
                            </div>                            
                            
                            <div style="padding:0;margin:0;" class="admNavPanelInp">                                
                                <div class="imgL"></div>
                                <div>
                                    <input class="admNavbarInp" name="clearImage" type="button">
                                        <xsl:attribute name="value">
                                            <xsl:value-of select="java:getString($dict_common, 'CLEAR')"/>
                                        </xsl:attribute>
                                    </input>
                                </div>
                                <div class="imgR"></div>
                            </div>                            
                        </td>
                    </tr>
                </table>
            </th>        
        </tr>
        <!-- DEPARTMENT -->
        <tr>
            <th class="admTableTD">
                <xsl:value-of select="java:getString($dict_contact_book, 'DEPARTMENT')"/>*
            </th>
            <th class="admTableTDLast admLeft">
                <input class="admTextArea admWidth200"
                    type="text"
                    name="department"
                    data_type="text"
                    required="true"
                    MAXLENGTH="200"
			>
                    <xsl:attribute name="uname">
                        <xsl:value-of select="java:getString($dict_contact_book, 'DEPARTMENT')"/>
                    </xsl:attribute>
                    <xsl:attribute name="value">
                        <xsl:value-of select="@department"/>
                    </xsl:attribute>
                </input>
            </th>
        </tr>
        <!-- JOB -->
        <tr>
            <th class="admTableTD">
                <xsl:value-of select="java:getString($dict_contact_book, 'JOB_TITLE')"/>*
            </th>
            <th class="admTableTDLast admLeft">
                <input class="admTextArea admWidth200"
                    type="text"
                    name="jobTitle"
                    data_type="text"
                    required="true"
                    MAXLENGTH="50"
		    >
                    <xsl:attribute name="uname">
                        <xsl:value-of select="java:getString($dict_contact_book, 'JOB_TITLE')"/>
                    </xsl:attribute>
                    <xsl:attribute name="value">
                        <xsl:value-of select="@job-title"/>
                    </xsl:attribute>
                </input>
            </th>
        </tr>
        <!-- BIRTHDAY -->
        <tr>
            <th class="admTableTD">
                <xsl:value-of select="java:getString($dict_contact_book, 'BIRTHDAY')"/>*
            </th>
            <th class="admTableTDLast admLeft">
                <input class="admTextArea admWidth200"
                    type="text"
                    name="birthday"
                    id="birthdayId"
                    data_type="text"
                    required="true"
                    readonly="true"
	                >
                    <xsl:attribute name="uname">
                        <xsl:value-of select="java:getString($dict_contact_book, 'BIRTHDAY')"/>
                    </xsl:attribute>
                    <xsl:attribute name="value">
                        <xsl:value-of select="@birthday"/>
                    </xsl:attribute>                    
                </input>
                (dd-mm-yyyy)
            </th>
        </tr>
        <!-- Phone -->
        <tr>
            <th class="admTableTD">
                <xsl:value-of select="java:getString($dict_common, 'PHONE')"/>
            </th>
            <th class="admTableTDLast admLeft">
                <input class="admTextArea admWidth200"
                    type="text"
                    name="phone"
                    data_type="text"
                    MAXLENGTH="50"
			    >
                    <xsl:attribute name="uname">
                        <xsl:value-of select="java:getString($dict_common, 'PHONE')"/>
                    </xsl:attribute>
                    <xsl:attribute name="value">
                        <xsl:value-of select="@phone"/>
                    </xsl:attribute>
                </input>
            </th>
        </tr>
        <!-- Email -->
        <tr>
            <th class="admTableTD">
                <xsl:value-of select="java:getString($dict_common, 'EMAIL')"/>
            </th>
            <th class="admTableTDLast admLeft">
                <input class="admTextArea admWidth200"
                    type="text"
                    name="email"
                    data_type="email"
                    MAXLENGTH="100"
			        >
                    <xsl:attribute name="uname">
                        <xsl:value-of select="java:getString($dict_common, 'EMAIL')"/>
                    </xsl:attribute>
                    <xsl:attribute name="value">
                        <xsl:value-of select="@email"/>
                    </xsl:attribute>
                </input>
                &#160;
                <b>
                    <xsl:value-of select="java:getString($dict_common, 'EXAMPLE')"/>:
                </b> support@negeso.com
            </th>
        </tr>
        <!-- Weblink -->
        <tr>
            <th class="admTableTD">
                <xsl:value-of select="java:getString($dict_common, 'WEB_ADDRESS')"/>
            </th>
            <th class="admTableTDLast admLeft">
                <input class="admTextArea admWidth200"
                    type="text"
                    name="weblink"
                    data_type="text"
                    MAXLENGTH="255"
			        >
                    <xsl:attribute name="uname">
                        <xsl:value-of select="java:getString($dict_common, 'WEB_ADDRESS')"/>
                    </xsl:attribute>
                    <xsl:attribute name="value">
                        <xsl:value-of select="@web-link"/>
                    </xsl:attribute>
                </input>
                &#160;
                <b>
                    <xsl:value-of select="java:getString($dict_common, 'EXAMPLE')"/>:
                </b> http://www.negeso.com
            </th>
        </tr>
        <!-- ALIAS -->
        <tr>
            <th class="admTableTD">
                <xsl:value-of select="java:getString($dict_contact_book, 'ALIAS')"/>
            </th>
            <th class="admTableTDLast admLeft">
                <input class="admTextArea admWidth200"
                    type="text"
                    name="nickname"
                    data_type="text"
                    MAXLENGTH="255"
		            >
                    <xsl:attribute name="uname">
                        <xsl:value-of select="java:getString($dict_contact_book, 'ALIAS')"/>
                    </xsl:attribute>
                    <xsl:attribute name="value">
                        <xsl:value-of select="@nickname"/>
                    </xsl:attribute>
                </input>
            </th>
        </tr>
    </table>
    
    <!-- START OF 3 of 3: Example of <textarea> -->
	<!-- MEMO1 field -->
    <!-- 
   	    <tr>
		    <td class="admMainTD"><xsl:call-template name="MEMO12"/></td>
		    <td class="admLightTD admLeft">
			    <textarea class="admTextArea admWidth265" 
				    name="memo1" rows="5" maxlength="200"
			    ><xsl:value-of select="@memo1"/></textarea>
		    </td>
	    </tr>
    -->
    <!-- END OF 3 of 3: Example of <textarea> -->
</xsl:template>

<xsl:template name="cb.groupPath">
    <!-- Path -->    
    <table border="0" cellpadding="0" cellspacing="0" align="left">
        <tr>
            <!-- Unactive pathe element - make it link-->
            <td style="padding:8px 0 0 5px;" valign="middle" class="admNavigation">
                <a class="admLocation" href="contact_book">
                    <xsl:value-of select="java:getString($dict_contact_book, 'CONTACT_BOOK')"/>
                </a>
            </td>

            <!-- Unactive pathe element - make it link-->
            <td style="padding:8px 0 0 5px; text-decoration:none;" valign="middle" class="admNavigation">                
                &#160;<img src="/images/navig_arrow.png"  style="vertical-align:middle;"/>&#160;
                <a class="admLocation" href="/admin/?command=cb-manage-contacts&amp;groupId={negeso:contact/@group-id}">
                    <xsl:value-of select="negeso:contact/@group-title"/>
                </a>
            </td>

            <!-- Active pathe element - just print it-->            
            <td valign="middle" class="admNavigation" style="padding:8px 0 0 0;text-decoration:none;">
                &#160; <img src="/images/navig_arrow.png"  style="vertical-align:middle;"/>&#160;
                <span>
                    <xsl:choose>
                        <xsl:when test="negeso:contact/@id">
                            <xsl:value-of select="negeso:contact/@title"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="java:getString($dict_contact_book, 'ADD_CONTACT')"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </span>
            </td>            
        </tr>
    </table>    
</xsl:template>


<!-- ******************************* Error message ********************************** -->
<xsl:template match="errorMessage">
    <xsl:value-of select="errorMessage"/>
</xsl:template>

</xsl:stylesheet>
