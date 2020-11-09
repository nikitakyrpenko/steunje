<?xml version="1.0" encoding="UTF-8"?>
<!--
  Copyright (c) 2007 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information"). You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.


  XSL templates for Site Core: Picture Frame
 
  @version		2007.11.27
  @author		Oleg 'germes' Shvedov
  @author		Rostislav 'KOTT' Brizgunov

-->


<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
>

    <xsl:template name="big_image">
        <xsl:choose>
            <xsl:when test="/negeso:page/negeso:wcms_attributes/negeso:image_set[@class='fixed size']/negeso:image/@src != ''">
                <xsl:apply-templates select="/negeso:page/negeso:wcms_attributes/negeso:image_set[@class='fixed size']/negeso:image" mode="big_image" />
            </xsl:when>
            <xsl:otherwise>
                <img src="/site/core/images/big_image.jpg" width="596" height="340" alt="" border="0" vspace="0" hspace="0" onerror="no_image(this);"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="negeso:image" mode="big_image">
        <xsl:if test="$outputType = 'admin' and parent::negeso:image_set[@class='fixed size']">
            <xsl:apply-templates select ="self::negeso:image" mode ="admin-widget" />
        </xsl:if>
        <xsl:choose>
            <xsl:when test="@extension='swf'">
                <object codeBase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,29,0"
                        classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"
                        height="{@max_height}"
                        width="{@max_width}">
                    <param NAME="_cx" VALUE="24553" />
                    <param NAME="_cy" VALUE="1376" />
                    <param NAME="Movie" VALUE="{@src}" />
                    <param NAME="Src" VALUE="{@src}" />
                    <param NAME="WMode" VALUE="Opaque" />
                    <param NAME="Quality" VALUE="High" />
                    <param NAME="Menu" VALUE="-1" />
                    <param NAME="Scale" VALUE="ShowAll" />
                    <embed type="application/x-shockwave-flash"
                        pluginspage="http://www.macromedia.com/go/getflashplayer"
                        src="{@src}"
                        wmode="opaque"
                        quality="High"
                        menu="false"
                        scale="ShowAll"
                        width="{@max_width}"
                        height="{@max_height}">
                    </embed>
                </object>
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates select="self::negeso:image" mode="image"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="images">
        <div class="smallPics">
            <xsl:if test="$outputType = 'admin'">
                <xsl:call-template name="admin-widget-animation" />
            </xsl:if>
            <xsl:choose>
                <xsl:when test="/negeso:page/negeso:wcms_attributes/negeso:image_set[@class='fixed size']/negeso:image/@src != ''">
                    <xsl:apply-templates select="/negeso:page/negeso:wcms_attributes/negeso:image_set[@class='fixed size']/negeso:image" mode="images" />
                </xsl:when>
                <xsl:otherwise>
                    <img src="/site/core/images/photo1.jpg" />
                </xsl:otherwise>
            </xsl:choose>
        </div>
    </xsl:template>

    <xsl:template match="negeso:image" mode="admin-widget">
        <div class="pf-admin-widget">
            <form id="main_form" method="POST" enctype="multipart/form-data" action="update_wcsm_attributes">
                <input type="hidden" id="action_field" name="action_field"/>
                <input type="hidden" id="src_field" name="src_field"/>
                <input type="hidden" id="image_set_id_field" name="image_set_id_field"/>
                <input type="hidden" id="image_id_field" name="image_id_field"/>
                <input type="hidden" id="attribute_set_id_field" name="attribute_set_id_field"/>
                <input type="hidden" id="attribute_class_field" name="attribute_class_field"/>
                <input type="hidden" id="flash_width" name="flash_width"/>
                <input type="hidden" id="flash_height" name="flash_height"/>
                <img src="/images/pf/insert-edit-image.gif" onclick="changeMainImage('{@id}', '{../../@id}', '{@width}', '{@height}');" alt="Edit image" />
                <img src="/images/pf/insert-edit-flash.gif" alt="Edit flash"  onclick="changeMainFlash('{@id}', '{../../@id}', '{@width}', '{@height}');"/>
                <img src="/images/pf/insert-edit-link.gif" alt="Edit link"  onclick="setLink('{@id}');"/>
            </form>
        </div>
    </xsl:template>

    <xsl:template match="negeso:image" mode="images">
        <div class="smallPic">
            <xsl:if test="$outputType = 'admin' and parent::negeso:image_set[@class='fixed size']">
                <xsl:apply-templates select ="self::negeso:image" mode ="admin-widget" />
            </xsl:if>
            <xsl:choose>
                <xsl:when test="@extension='swf'">
                    <object codeBase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,29,0"
                            classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"
                            height="{@max_height}"
                            width="{@max_width}">
                        <param NAME="_cx" VALUE="24553" />
                        <param NAME="_cy" VALUE="1376" />
                        <param NAME="Movie" VALUE="{@src}" />
                        <param NAME="Src" VALUE="{@src}" />
                        <param NAME="WMode" VALUE="Opaque" />
                        <param NAME="Quality" VALUE="High" />
                        <param NAME="Menu" VALUE="-1" />
                        <param NAME="Scale" VALUE="ShowAll" />
                        <embed type="application/x-shockwave-flash"
                            pluginspage="http://www.macromedia.com/go/getflashplayer"
                            src="{@src}"
                            wmode="opaque"
                            quality="high"
                            menu="false"
                            scale="showall"
                            width="{@max_width}"
                            height="{@max_height}">
                        </embed>
                    </object>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:apply-templates select="self::negeso:image" mode="image"/>
                </xsl:otherwise>
            </xsl:choose>
        </div>
    </xsl:template>

    <xsl:template name="admin-widget-animation">
        <div class="pf-admin-widget widget-animation">
            <img src="/images/pf/insert-edit-images.gif" onclick="changeAnimationImages('{/negeso:page/@id}');" alt="Edit images" />
            <xsl:if test="(count(/negeso:page/negeso:wcms_attributes/negeso:image_set[@class='animation']/negeso:image) &gt; 1)">
                <img src="/images/pf/insert-edit-properties.gif" onclick="changeAnimationProperties('{/negeso:page/negeso:wcms_attributes/negeso:image_set[@class='animation']/@id}');" alt="Change properties of animation" />
            </xsl:if>
        </div>
    </xsl:template>

    <xsl:template name="images_animation">
        <xsl:if test="$outputType = 'admin'">
            <xsl:call-template name="admin-widget-animation" />
        </xsl:if>
        <xsl:choose>
            <xsl:when test="/negeso:page/negeso:wcms_attributes/negeso:image_set[@class='animation']/negeso:image">
                <xsl:choose>
                    <xsl:when test="(count(/negeso:page/negeso:wcms_attributes/negeso:image_set[@class='animation']/negeso:image) &gt; 1)">
                        <xsl:apply-templates select="/negeso:page/negeso:wcms_attributes/negeso:image_set[@class='animation']/negeso:image" mode="image_animation" />
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:apply-templates select="/negeso:page/negeso:wcms_attributes/negeso:image_set[@class='animation']/negeso:image" mode="image"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                <img src="/site/core/images/image.jpg" />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="negeso:image" mode="image_animation">
        <div type="slideshow" class="slideshow">
            <!--<xsl:if test="position()=1">-->
                <!--<xsl:attribute name="style">z-index:1;filter: alpha(opacity=100);-moz-opacity: 1;opacity:1;-khtml-opacity: 1;</xsl:attribute>-->
            <!--</xsl:if>-->
            <xsl:choose>
                <xsl:when test="position()=1">
                    <xsl:attribute name="style">z-index:1;filter: alpha(opacity=100);-moz-opacity: 1;opacity:1;-khtml-opacity: 1;</xsl:attribute>
                    <div class="picframe-article">
                        <xsl:apply-templates select="//negeso:article[negeso:head='picframe-article']"/>
                    </div>
                </xsl:when>
                <xsl:when test="position()=2">
                    <div class="picframe-article">
                        <xsl:apply-templates select="//negeso:article[negeso:head='picframe-article2']"/>
                    </div>
                </xsl:when>
                <xsl:when test="position()=3">
                    <div class="picframe-article">
                        <xsl:apply-templates select="//negeso:article[negeso:head='picframe-article3']"/>
                    </div>
                </xsl:when>
                <xsl:when test="position()=4">
                    <div class="picframe-article">
                        <xsl:apply-templates select="//negeso:article[negeso:head='picframe-article4']"/>
                    </div>
                </xsl:when>
                <xsl:when test="position()=5">
                    <div class="picframe-article">
                        <xsl:apply-templates select="//negeso:article[negeso:head='picframe-article5']"/>
                    </div>
                </xsl:when>
                <xsl:when test="position()=6">
                    <div class="picframe-article">
                        <xsl:apply-templates select="//negeso:article[negeso:head='picframe-article6']"/>
                    </div>
                </xsl:when>
                <xsl:when test="position()=7">
                    <div class="picframe-article">
                        <xsl:apply-templates select="//negeso:article[negeso:head='picframe-article7']"/>
                    </div>
                </xsl:when>
                <xsl:when test="position()=8">
                    <div class="picframe-article">
                        <xsl:apply-templates select="//negeso:article[negeso:head='picframe-article8']"/>
                    </div>
                </xsl:when>
                <xsl:when test="position()=9">
                    <div class="picframe-article">
                        <xsl:apply-templates select="//negeso:article[negeso:head='picframe-article9']"/>
                    </div>
                </xsl:when>
                <xsl:otherwise>
                    <div class="picframe-article">
                        <xsl:apply-templates select="//negeso:article[negeso:head='picframe-article10']"/>
                    </div>
                </xsl:otherwise>
            </xsl:choose>
            <a onfocus="blur()" target="_self">
                <xsl:if test="@link">
                    <xsl:attribute name="href">
                        <xsl:value-of select="@link" disable-output-escaping="yes"/>
                    </xsl:attribute>
                    <xsl:if test="@target='_blank'">
                        <xsl:attribute name="target">_blank</xsl:attribute>
                    </xsl:if>
                </xsl:if>
                <img src="{@src}" width="{@width}" height="{@height}"  alt="{@alt}" border="0" vspace="0" hspace="0" />
            </a>
        </div>
    </xsl:template>

    <xsl:template match="negeso:image" mode="image">
        <a onfocus="blur()" target="_self">
            <xsl:if test="@link">
                <xsl:attribute name="href">
                    <xsl:value-of select="@link" disable-output-escaping="yes"/>
                </xsl:attribute>
                <xsl:if test="@target='_blank'">
                    <xsl:attribute name="target">_blank</xsl:attribute>
                </xsl:if>
            </xsl:if>
            <img src="{@src}" width="{@width}" height="{@height}" alt="{@alt}" border="0" vspace="0" hspace="0" />
        </a>
        <div class="picframe-article">
            <xsl:apply-templates select="//negeso:article[negeso:head='picframe-article']"/>
        </div>
    </xsl:template>

</xsl:stylesheet>