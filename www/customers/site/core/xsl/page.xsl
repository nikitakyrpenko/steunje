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
                xmlns:java="http://xml.apache.org/xslt/java"
                exclude-result-prefixes="java"
>

    <xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>
    <xsl:variable name="dict_news_module"
                  select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('news_module', $lang)"/>
    <xsl:variable name="dict_job_module"
                  select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('job_module', $lang)"/>
    <xsl:variable name="dict_contact_module"
                  select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('contact_book', $lang)"/>
    <xsl:variable name="google_analytic"
                  select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('google_analytics', 'en')"/>

    <xsl:variable name="class" select="/negeso:page/@class"/>
    <xsl:variable name="pageid" select="/negeso:page/@id"/>
    <xsl:variable name="adminPath">
        <xsl:choose>
            <xsl:when test="/negeso:page/@admin-path='true'">/admin</xsl:when>
            <xsl:otherwise></xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="param" select="/negeso:page/negeso:request/negeso:parameter"/>
    <xsl:variable name="images" select="/negeso:page/negeso:wcms_attributes/negeso:image_set[@class='animation']"/>
    <xsl:variable name="selectedMenuId" select="//negeso:main_menu/negeso:menu/@selectedMenuId"/>


    <xsl:template name="page_head">
        <!-- current tag: negeso:page -->
        <head>
            <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
            <meta name="viewport" content="width=device-width, initial-scale=1, minimum-scale=1, maximum-scale=1"/>


            <meta http-equiv="cache-control" content="max-age=0"/>
            <meta http-equiv="cache-control" content="no-cache"/>
            <meta http-equiv="expires" content="0"/>
            <meta http-equiv="expires" content="Tue, 01 Jan 1980 1:00:00 GMT"/>
            <meta http-equiv="pragma" content="no-cache"/>


            <!-- GOOGLE VERIFICATION CODE -->
            <xsl:if test="/negeso:page/negeso:google/negeso:parameter[@name='verification_code']!=''">
                <meta name="google-site-verification"
                      content="{/negeso:page/negeso:google/negeso:parameter[@name='verification_code']}"/>
            </xsl:if>
            <xsl:if test="/negeso:page/negeso:google/negeso:parameter[@name='tag_manager_code']!=''">
                <script type="text/javascript">
                    (function(w,d,s,l,i){w[l]=w[l]||[];w[l].push({'gtm.start':new Date().getTime(),event:'gtm.js'});
                    var f=d.getElementsByTagName(s)[0],j=d.createElement(s),dl=l!='dataLayer'?'&#38;l='+l:'';
                    j.async=true;j.src='//www.googletagmanager.com/gtm.js?id='+i+dl;f.parentNode.insertBefore(j,f);
                    })(window,document,'script','dataLayer','<xsl:value-of
                        select="/negeso:page/negeso:google/negeso:parameter[@name='tag_manager_code']"/>');
                </script>
            </xsl:if>
            <xsl:if test="/negeso:page/negeso:google/negeso:parameter[@name='bing_code']!=''">
                <meta name="msvalidate.01" content="{/negeso:page/negeso:google/negeso:parameter[@name='bing_code']}"/>
            </xsl:if>
            <title>
                <xsl:value-of select="/negeso:page/negeso:title_prefix"/>
                <xsl:value-of select="/negeso:page/negeso:meta_title"/>
                <xsl:value-of select="/negeso:page/negeso:title_suffix"/>
            </title>
            <xsl:if test="$outputType = 'admin'">
                <meta http-equiv="X-Accel-Expires" content="0"/>
            </xsl:if>
            <meta name="generator" content="Negeso Website/CMS"/>
            <!-- {/negeso:page/negeso:generator} -->
            <meta name="author" content="{/negeso:page/negeso:author}"/>
            <meta name="copyright" content="{/negeso:page/negeso:copyright}"/>
            <xsl:if test="/negeso:page/negeso:robots">
                <meta name="robots" content="{/negeso:page/negeso:robots}"/>
            </xsl:if>

            <xsl:text disable-output-escaping="yes">
		  <![CDATA[
			     <!--	This website is designed, built and hosted by Negeso			-->
			     <!--	This website is based on the Negeso W/CMS				-->
			     <!--	For more information about Negeso look at our website www.negeso.com 	-->
		  ]]>
        </xsl:text>
            <!--<xsl:if test="/negeso:page/@canonical">
                <link rel="canonical" href="{/negeso:page/@canonical}"/>
            </xsl:if>-->

            <xsl:if test="/negeso:page/negeso:favicon and not(/negeso:page/negeso:favicon='')">
                <link rel="icon" type="image/x-icon">
                    <xsl:attribute name="href">/<xsl:value-of select="/negeso:page/negeso:favicon"/>
                    </xsl:attribute>
                </link>
                <link rel="shortcut icon" type="image/x-icon">
                    <xsl:attribute name="href">/<xsl:value-of select="/negeso:page/negeso:favicon"/>
                    </xsl:attribute>
                </link>
                <!--				<link type="image/x-icon" rel="icon" href="/media/favicon.ico" />-->
            </xsl:if>

            <xsl:choose>
                <xsl:when test="$class='flip_book'">
                    <script type="text/javascript" src="/script/jquery.min.js"/>
                </xsl:when>
                <xsl:otherwise>
                    <script type="text/javascript" src="/script/jquery.min.js"/>
                    <script type="text/javascript" src="/script/jquery-ui.custom.min.js"/>
                </xsl:otherwise>
            </xsl:choose>
            <script type="text/javascript" src="//code.jquery.com/jquery-1.11.0.min.js"></script>
            <script type="text/javascript" src="//code.jquery.com/jquery-migrate-1.2.1.min.js"></script>

            <link rel="stylesheet" type="text/css" href="/css/jquery-ui.custom.css"/>
            <script type="text/javascript" src="/script/slick.min.js"/>

            <!--<script src="https://cdnjs.cloudflare.com/ajax/libs/wow/1.1.2/wow.min.js"></script>-->
            <script src="https://cdnjs.cloudflare.com/ajax/libs/aos/2.3.4/aos.js"></script>
            <script type="text/javascript"
                    src="//cdn.jsdelivr.net/npm/slick-carousel@1.8.1/slick/slick.min.js"></script>

            <!--<script>
				new WOW().init();
			</script>-->

            <script type="text/javascript" src="/site/core/script/common.js?v=23"/>
            <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>

            <xsl:call-template name="validationConstants"/>
            <xsl:call-template name="modules_heads"/>
            <xsl:call-template name="meta_tags"/>
            <link rel="stylesheet" type="text/css" href="slick/slick.css"/>
            <link rel="stylesheet" type="text/css" href="slick/slick-theme.css"/>
            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/aos/2.3.4/aos.css"/>
            <!--<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/animate.css/3.7.0/animate.min.css"/>-->
            <link rel="stylesheet" type="text/css"
                  href="/site/core/css/fonts/font-awesome-4.7.0/css/font-awesome.min.css"/>
            <link rel="stylesheet" type="text/css"
                  href="/site/core/css/fonts/iconic/css/material-design-iconic-font.min.css"/>

			<link rel="stylesheet" type="text/css" href="/site/core/css/reset.css"/>
			<link rel="stylesheet" type="text/css" href="/site/core/css/default_styles.css"/>
			<link rel="stylesheet" type="text/css" href="/site/core/css/page.css?v=29"/>
			<link rel="stylesheet" type="text/css" href="/site/core/css/util.css"/>
			<link rel="stylesheet" type="text/css" href="/site/core/css/menu.css?v=4"/>
			<link rel="stylesheet" type="text/css" href="/site/modules/search_module/css/search.css"/>
			<script type="text/javascript" src="/site/core/script/menu.js">/**/</script>
			<link rel="stylesheet" type="text/css" href="/site/core/css/languageBox.css"/>
		<!--	<link rel="stylesheet" type="text/css" href="/site/core/css/hamburger.css"/>-->
			<link rel="stylesheet" type="text/css" href="/css/slick-theme.css"/>
			<script type="text/javascript" src="/site/core/script/languageBox.js">/**/</script>

            <xsl:if test="$outputType = 'admin'">
                <link rel="stylesheet" type="text/css" href="/css/thickbox.css"/>
                <link rel="stylesheet" type="text/css" href="/css/widget.css"/>
            </xsl:if>
            <script language="JavaScript1.2" src="/script/md5.js" type="text/javascript"/>
            <script type='text/javascript' src='/script/prng4.js' language='JavaScript1.2'></script>
            <script type='text/javascript' src='/script/jsnb.js' language='JavaScript1.2'></script>
            <script type='text/javascript' src='/script/rnb.js' language='JavaScript1.2'></script>
            <script type='text/javascript' src='/script/rsa.js' language='JavaScript1.2'></script>

            <!-- Special redirection, if we are changing page language,
                      and current page does not exists in the selected language.
            -->

            <script type="text/javascript">

                function changeLanguage(el) {
                window.location.href = "/index_" + $(el).find("option:selected").val() + ".html";
                }

                var lang="<xsl:value-of select="$lang"/>";
                var pfConfigurator = null;
                <xsl:if test="$images">
                    pfConfigurator = {
                    pictures : [
                    <xsl:for-each select="$images/negeso:image">
                        {src: '<xsl:value-of select="@src"/>',
                        link: '<xsl:value-of select="@link"/>',
                        target: '<xsl:value-of select="@target"/>',
                        alt: '<xsl:value-of select="@alt"/>'}
                        <xsl:if test="position() != last()">,</xsl:if>
                    </xsl:for-each>
                    ],
                    delay:<xsl:value-of select="$images/@delay"/>,
                    step:<xsl:value-of select="translate($images/@step div 100,',','.')"/>,
                    speedOfAnimation:<xsl:value-of select="$images/@speed_of_animation"/>,
                    typeOfAnimation:'opacity',
                    frame : ''
                    }
                </xsl:if>
            </script>


            <xsl:if test="$param[@name='switchToLang'] and negeso:filename/text()=$error_link">
                <script type="text/javascript">
                    window.location.href = '
                    <xsl:call-template name="home_link">
                        <xsl:with-param name="lang" select="$param[@name='switchToLang']/negeso:value"/>
                    </xsl:call-template>
                    ';
                </script>

                <META HTTP-EQUIV="Refresh">
                    <xsl:attribute name="content">0; url=
                        <xsl:call-template name="home_link">
                            <xsl:with-param name="lang" select="$param[@name='switchToLang']/negeso:value"/>
                        </xsl:call-template>
                    </xsl:attribute>
                </META>
            </xsl:if>

            <script type="text/javascript" src="/site/core/script/change_font/change_font.js">/**/</script>
            <script type="text/javascript" src="/site/modules/print_module/script/print_module.js">/**/</script>

            <!-- Page Language definition for JavaScripts -->
            <!--			<script src="//widgets.twimg.com/j/2/widget.js"></script>-->
            <script type="text/javascript">
                var pfSetting = null;
                var isPfRuning = false;
                $(document).ready(function(){

                $(".formatDate").each(function(){
                var date=$(this).text();
                var dateS = date.substr(0, 10);
                var dateAr = dateS.split("-");
                dateAr.reverse();
                var newdate = dateAr.join('-');
                $(this).text(newdate);
                });

                <!--!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0],p=/^http:/.test(d.location)?'http':'https';if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src=p+"://platform.twitter.com/widgets.js";fjs.parentNode.insertBefore(js,fjs);}}(document,"script","twitter-wjs");-->


                <xsl:if test="$images">
                    try{
                    var $pics = $('div[type=slideshow]');
                    pfSetting = {
                    pics : $pics,
                    picsLength : $pics.length,
                    curPic : 0,
                    delay:<xsl:value-of select="$images/@delay"/>,
                    speed:<xsl:value-of select="$images/@speed_of_animation"/>,
                    step:<xsl:value-of select="$images/@step"/>
                    }
                    isPfRuning = true;
                    setTimeout('showImgFunc()',pfSetting.delay);
                    }catch(e){}
                </xsl:if>
                });
            </script>
            <script type="text/javascript" src="/script/common_functions.js?v=2">/**/</script>

            <xsl:if test="/negeso:page/@browser = 'MSIE' and (starts-with(/negeso:page/@browserVersion,'6') or /negeso:page/@browserVersion = '5.5')">
                <script defer="defer" type="text/javascript" src="/script/fix_png.js"/>
            </xsl:if>

            <xsl:if test="$outputType = 'admin'">
                <script type="text/javascript" src="/script/widget.js">/**/</script>

                <script type="text/javascript" src="/script/Constants.js"/>
                <link rel="stylesheet" type="text/css" href="/css/constants-list.css"/>

                <script type="text/javascript" src="/script/RTE_Adapter.js">/**/</script>
                <script type="text/javascript" src="/script/media_catalog.js">/**/</script>
                <script type="text/javascript" src="/site/core/script/picture_frame.js">/**/</script>
                <script type="text/javascript">
                    RTE_DEFAULT_SAVE_MODE = 3;
                </script>
                <xsl:call-template name="adminhead"/>
            </xsl:if>

            <!-- New validation script: please, try to use IT instead of OTHER validations -->
            <script type="text/javascript" src="/site/core/script/validation.js?v=7">/**/</script>
            <link href="https://fonts.googleapis.com/css?family=Lato:300,400,500,700" rel="stylesheet"/>
            <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@700" rel="stylesheet"/>
            <xsl:if test="$class='contact-page'">
                <script src="https://polyfill.io/v3/polyfill.min.js?features=default"></script>
                <script defer="defer" src="https://tinyurl.com/yb5mqwt3">
                </script>
                <!--<script defer="defer"src="https://maps.googleapis.com/maps/api/js?key=AIzaSyAgdVx1nD4kXqqJjYiAur4NYHfftl0DeQs&callback=initMap"></script>-->
                <!--iam api key= AIzaSyAgdVx1nD4kXqqJjYiAur4NYHfftl0DeQs-->
            </xsl:if>
            <xsl:if test="$class='ingredient'">
                <link href="https://tinyurl.com/yd268j94" rel="stylesheet"/>
                <link href="https://tinyurl.com/yamxo89n" rel="stylesheet"/>
            </xsl:if>
            <xsl:if test="$class='ingred-prof'">
                <link href="https://tinyurl.com/yd268j94" rel="stylesheet"/>
                <link href="https://tinyurl.com/yamxo89n" rel="stylesheet"/>
            </xsl:if>


            <xsl:if test="$class='contacts-prof'">
                <script src="https://polyfill.io/v3/polyfill.min.js?features=default"></script>
                <script defer="defer" src="https://tinyurl.com/yb5mqwt3"></script>
            </xsl:if>


            <xsl:variable name="outlogin"
                          select="not(/negeso:page/@role-id = 'guest') and (count(/negeso:page/@role-id) = 1)"/>
            <script type="text/javascript">
                $(document).ready(function(){
                <xsl:if test="$outlogin">
                    $('#negeso_main_menu a[href*="inloggen"]').text('UITLOGGEN')
                </xsl:if>
                });
            </script>
            <link rel="stylesheet" type="text/css" href="/site/core/css/login-form.css"/>
            <link rel="stylesheet" type="text/css" href="//cdn.jsdelivr.net/npm/slick-carousel@1.8.1/slick/slick.css"/>

        </head>
    </xsl:template>

    <xsl:template match="negeso:contents" mode="page_body">
        <xsl:param name="loginMode" select="''"/>
        <!-- current tag: negeso:contents -->
        <body>
            <!-- Google Tag Manager (noscript) -->
            <xsl:if test="/negeso:page/negeso:google/negeso:parameter[@name='tag_manager_code']!=''">
                <noscript>
                    <iframe height="0" width="0" style="display:none;visibility:hidden;">
                        <xsl:attribute name="src">//www.googletagmanager.com/ns.html?id=<xsl:value-of
                                select="/negeso:page/negeso:google/negeso:parameter[@name='tag_manager_code']"/>
                        </xsl:attribute>
                    </iframe>
                </noscript>
            </xsl:if>
            <!-- End Google Tag Manager (noscript) -->

            <xsl:choose>
                <xsl:when test="$loginMode='admin'">
                    <xsl:call-template name="loginform_body"/>
                </xsl:when>
                <xsl:when test="$loginMode='su'">
                    <xsl:call-template name="loginform_su_body"/>
                </xsl:when>
            </xsl:choose>
            <xsl:if test="$outputType = 'admin'">
                <xsl:call-template name="negeso_widget"/>
                <xsl:call-template name="pageproperties"/>
            </xsl:if>
            <xsl:call-template name="google-conversion-additional-page-property"/>

            <!--Main content block-->
            <xsl:call-template name="page_container"/>

            <script language="javascript" src="/script/fix_flash_ie.js" type="text/javascript">/**/</script>

            <xsl:if test="/negeso:page/negeso:google/negeso:parameter[@name='analytic_script']!=''">
                <script type="text/javascript">
                    var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
                    document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js'
                    type='text/javascript'%3E%3C/script%3E"));
                </script>
                <script type="text/javascript">
                    try {
                    var pageTracker = _gat._getTracker("<xsl:value-of
                        select="/negeso:page/negeso:google/negeso:parameter[@name='analytic_script']"/>");
                    pageTracker._trackPageview();
                    } catch(err) {}
                </script>
            </xsl:if>
            <!-- GOOGLE ANALYTIC SCRIPT -->
            <xsl:if test="$class='frontpage'">
                <script type="text/javascript">
                    $(document).ready(function(){
                       $('.header__wrapper').addClass('main');
                    });
                </script>
                <!--				<script src="/script/swiper.min.js"></script>-->
                <!--<script type="text/javascript" src="//cdn.jsdelivr.net/npm/slick-carousel@1.8.1/slick/slick.min.js"></script>-->
            </xsl:if>

            <xsl:if test="$class='webshop_order'">
                <script type="text/javascript"
                        src="/site/modules/webshop_module/webshop_order/script/jquery_tablesorter.js"/>
                <script type="text/javascript"
                        src="/site/modules/webshop_module/webshop_order/script/jquery_tablesorter_pager.js"/>
                <script type="text/javascript"
                        src="/site/modules/webshop_module/webshop_order/script/jquery_tablesorter_widgets.js"/>
                <script type="text/javascript" src="/site/modules/webshop_module/webshop_order/script/tablesorter.js"/>
            </xsl:if>
            <xsl:if test="$class='account'">
                <script type="text/javascript" src="/site/core/script/account.js"></script>
            </xsl:if>

            <!--			htm script here-->
            <xsl:if test="not(/negeso:page/@role-id = 'guest') and not($outputType = 'admin')">
                <script>
                    $('a[href]').each(function(){
                    this.href = this.href.replace('html', 'htm');
                    })
                </script>
            </xsl:if>
            <script src="https://unpkg.com/axios/dist/axios.min.js"></script>
            <script type="text/javascript" src="/site/core/script/login-script.js"></script>
            <script>

                var modal = document.getElementById("myModal");

                var btn = document.getElementById("myBtn");

                var span = document.getElementsByClassName("close")[0];

                btn.onclick = function () {
                modal.style.display = "block";
                }

                span.onclick = function () {
                modal.style.display = "none";
                }

                window.onclick = function (event) {
                if (event.target == modal) {
                modal.style.display = "none";
                }
                }

            </script>
            <script>
                AOS.init();
            </script>
            <xsl:if test="$class='customers'">
                <script src="/site/core/script/customerList.js"></script>
            </xsl:if>
        </body>
    </xsl:template>

    <!-- Main site "skeleton" - container for all site contents -->
    <xsl:template name="page_container">
        <!-- current tag: negeso:contents -->
        <!-- <div id="elmTop" class="container">-->

        <!--<div class="header__block">
			<div class="headerts">







					<xsl:if test="not(/negeso:page/@role-id = 'guest')">
                        <script>
                            if(localStorage.getItem('linkToProduct')){
                            document.location = localStorage.getItem('linkToProduct');
                            localStorage.removeItem('linkToProduct');
                            }
                        </script>
                        <div class="cart_wishList">
                            <a href="/order_nl.html">
                                <xsl:if test="/negeso:page/@admin-path='true'">
                                    <xsl:attribute name="href">/admin/order_nl.html</xsl:attribute>
                                </xsl:if>
                                <img src="/site/core/images/order.png" alt=""/>
                            </a>
                            <a href="/gebruiker_nl.html">
                                <xsl:if test="/negeso:page/@admin-path='true'">
                                    <xsl:attribute name="href">/admin/gebruiker_nl.html</xsl:attribute>
                                </xsl:if>
                                <img src="/site/core/images/user.png" alt=""/>
                            </a>
                            <a class="icons" href="/wenslijst_nl.html">
                                <xsl:if test="/negeso:page/@admin-path='true'">
                                    <xsl:attribute name="href">/admin/wenslijst_nl.html</xsl:attribute>
                                </xsl:if>
                                <img src="/site/core/images/wishlist-icon.png" alt=""/>
                                <span id="wishlist_counter">
                                    <xsl:value-of select="/negeso:page/negeso:contents/negeso:icons_count/@wish"/>
                                </span>
                            </a>
                            <a class="icons" href="/webshop_cart_nl.html">
                                <xsl:if test="/negeso:page/@admin-path='true'">
                                    <xsl:attribute name="href">/admin/webshop_cart_nl.html</xsl:attribute>
                                </xsl:if>
                                <img src="/site/core/images/cart-icon.png" alt=""/>
                                <span id="webshop_cart_counter">
                                    <xsl:value-of select="/negeso:page/negeso:contents/negeso:icons_count/@cart"/>
                                </span>
                            </a>

                            <a href="/webshop_cart_nl.html"  class="total_cart">
                                <xsl:if test="/negeso:page/@admin-path='true'">
                                    <xsl:attribute name="href">/admin/webshop_cart_nl.html</xsl:attribute>
                                </xsl:if>
                                <span class="total_cart-curr">
                                    &#8364;
                                </span>
                                <span class="total_cart-sum">
                                    &lt;!&ndash;<xsl:choose>&ndash;&gt;
                                    &lt;!&ndash;<xsl:when test="//negeso:icons_count/negeso:cart_details/@orderPriceIncDiscountIncVat > 250 and /negeso:page/negeso:contents/negeso:icons_count/@cart > 0">&ndash;&gt;
                                    &lt;!&ndash;<xsl:attribute name="class">total_cart-sum total_cart-sum&#45;&#45;green</xsl:attribute>&ndash;&gt;
                                    &lt;!&ndash;</xsl:when>&ndash;&gt;
                                    &lt;!&ndash;<xsl:when test="//negeso:icons_count/negeso:cart_details/@orderPriceIncDiscountIncVat &lt; 250 and /negeso:page/negeso:contents/negeso:icons_count/@cart > 0">&ndash;&gt;
                                    &lt;!&ndash;<xsl:attribute name="class">total_cart-sum total_cart-sum&#45;&#45;p10</xsl:attribute>&ndash;&gt;
                                    &lt;!&ndash;</xsl:when>&ndash;&gt;
                                    &lt;!&ndash;<xsl:otherwise>&ndash;&gt;
                                    &lt;!&ndash;<xsl:attribute name="class">total_cart-sum total_cart-sum&#45;&#45;p10</xsl:attribute>&ndash;&gt;
                                    &lt;!&ndash;<xsl:value-of select="translate(/negeso:page/negeso:contents/negeso:icons_count/negeso:cart_details/@orderPriceIncDiscountIncVat, ',', '.')"/>&ndash;&gt;
                                    &lt;!&ndash;</xsl:otherwise>&ndash;&gt;
                                    &lt;!&ndash;</xsl:choose>&ndash;&gt;
                                    <xsl:if test="//negeso:icons_count/negeso:cart_details/@orderPriceIncDiscountExVat > 250 and /negeso:page/negeso:contents/negeso:icons_count/@cart > 0">
                                        <xsl:attribute name="class">total_cart-sum total_cart-sum&#45;&#45;green total_cart-sum&#45;&#45;p10</xsl:attribute>
                                        <xsl:value-of select="translate(/negeso:page/negeso:contents/negeso:icons_count/negeso:cart_details/@orderPriceIncDiscountExVat, ',', '.')"/>
                                    </xsl:if>
                                    <xsl:if test="//negeso:icons_count/negeso:cart_details/@orderPriceIncDiscountExVat &lt; 250 and /negeso:page/negeso:contents/negeso:icons_count/@cart > 0">
                                        <xsl:attribute name="class">total_cart-sum total_cart-sum&#45;&#45;p10</xsl:attribute>
                                        <xsl:value-of select="translate(/negeso:page/negeso:contents/negeso:icons_count/negeso:cart_details/@orderPriceIncDiscountExVat, ',', '.')"/>
                                    </xsl:if>
                                </span>
                            </a>

                            <div class="aanmelden-btn aanmelden-btn-out">
                                <form  method="post" enctype="multipart/form-data">
                                    <input type="hidden" name="logout" value="1" />
                                    <button class="submit" type="submit" value="Uitloggen" title="Uitloggen"></button>
                                </form>
                                <svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" aria-hidden="true" width="448" height="512" style="-ms-transform: rotate(360deg); -webkit-transform: rotate(360deg); transform: rotate(360deg);" preserveAspectRatio="xMidYMid meet" viewBox="0 0 448 512"><path d="M313.941 216H12c-6.627 0-12 5.373-12 12v56c0 6.627 5.373 12 12 12h301.941v46.059c0 21.382 25.851 32.09 40.971 16.971l86.059-86.059c9.373-9.373 9.373-24.569 0-33.941l-86.059-86.059c-15.119-15.119-40.971-4.411-40.971 16.971V216z" fill="#920629"/></svg>
                            </div>
                        </div>
                    </xsl:if>

			</div>-->
        <!--<div class="top-banner">
            <div class="ban-item">
                <xsl:apply-templates select="negeso:article[negeso:head='top-banner_1']"/>
            </div>
            <div class="ban-item">
                <xsl:apply-templates select="negeso:article[negeso:head='top-banner_2']"/>
            </div>
            <div class="ban-item">
                <xsl:apply-templates select="negeso:article[negeso:head='top-banner_3']"/>
            </div>
        </div>-->
        <header>

            <div class="header__wrapper">

            <div class="container">
           <!--     <div class="header__logo">
                    <a class="header__link" href="/index_{$lang}.html" onfocus="blur();">
                        <xsl:if test="/negeso:page/@admin-path='true'">
                            <xsl:attribute name="href">/admin/index_<xsl:value-of select="$lang"/>.html
                            </xsl:attribute>
                        </xsl:if>
                        <xsl:if test="$class='frontpage'">
                            <xsl:attribute name="class">noneLink logo-link</xsl:attribute>
                        </xsl:if>
                        <img src="#"
                             alt="{java:getString($dict_common, 'CORE.TOP_LINK_HOME')}"
                             title="{java:getString($dict_common, 'CORE.TOP_LINK_HOME')}"/>


                    </a>
                </div>-->
              <div class="left_header"><a href="/index_nl.html"><img src="/media/logo/logo3-white.png"/></a></div>
                <div class="menu">
                    <xsl:apply-templates select="negeso:main_menu/negeso:menu"/>
                    <div class="brg_btn">
                        <svg class="ham hamRotate ham1" viewBox="0 0 100 100" width="80"
                             onclick="this.classList.toggle('active')">
                            <path
                                    class="line top"
                                    d="m 30,33 h 40 c 0,0 9.044436,-0.654587 9.044436,-8.508902 0,-7.854315 -8.024349,-11.958003 -14.89975,-10.85914 -6.875401,1.098863 -13.637059,4.171617 -13.637059,16.368042 v 40"/>
                            <path
                                    class="line middle"
                                    d="m 30,50 h 40"/>
                            <path
                                    class="line bottom"
                                    d="m 30,67 h 40 c 12.796276,0 15.357889,-11.717785 15.357889,-26.851538 0,-15.133752 -4.786586,-27.274118 -16.667516,-27.274118 -11.88093,0 -18.499247,6.994427 -18.435284,17.125656 l 0.252538,40"/>
                        </svg>
                    </div>


                </div>



            </div>
                <!--<div class="header__wrapper">

                    <div class="header__logo">
                        <a class="header__link" href="/index_{$lang}.html" onfocus="blur();">
                            <xsl:if test="/negeso:page/@admin-path='true'">
                                <xsl:attribute name="href">/admin/index_<xsl:value-of select="$lang"/>.html
                                </xsl:attribute>
                            </xsl:if>
                            <xsl:if test="$class='frontpage'">
                                <xsl:attribute name="class">noneLink logo-link</xsl:attribute>
                            </xsl:if>
                            <img src="/media/logo_white.png"
                                 alt="{java:getString($dict_common, 'CORE.TOP_LINK_HOME')}"
                                 title="{java:getString($dict_common, 'CORE.TOP_LINK_HOME')}"/>
                        </a>
                    </div>
                    <div class="header__breadcrumbs__left">
                        &lt;!&ndash;<button class="hamburger hamburger&#45;&#45;spring" type="button">
							<span class="hamburger-box">
								<span class="hamburger-inner"></span>
							</span>
						</button>&ndash;&gt;
                        <svg class="ham hamRotate ham1" viewBox="0 0 100 100" width="80"
                             onclick="this.classList.toggle('active')">
                            <path
                                    class="line top"
                                    d="m 30,33 h 40 c 0,0 9.044436,-0.654587 9.044436,-8.508902 0,-7.854315 -8.024349,-11.958003 -14.89975,-10.85914 -6.875401,1.098863 -13.637059,4.171617 -13.637059,16.368042 v 40"/>
                            <path
                                    class="line middle"
                                    d="m 30,50 h 40"/>
                            <path
                                    class="line bottom"
                                    d="m 30,67 h 40 c 12.796276,0 15.357889,-11.717785 15.357889,-26.851538 0,-15.133752 -4.786586,-27.274118 -16.667516,-27.274118 -11.88093,0 -18.499247,6.994427 -18.435284,17.125656 l 0.252538,40"/>
                        </svg>



                        <div class="change-lang">
                            <xsl:call-template name="languages"/>
                        </div>
                    </div>

                    <div class="menu-type">
                        <p class="menu_cons_btn  active">
                            <a>
                                <xsl:choose>
                                    <xsl:when test="//negeso:page/@admin-path='true'">
                                        <xsl:attribute name="href">/admin/index_<xsl:value-of select="$lang"/>.html
                                        </xsl:attribute>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:attribute name="href">/index_<xsl:value-of select="$lang"/>.html
                                        </xsl:attribute>
                                    </xsl:otherwise>
                                </xsl:choose>
                                <xsl:call-template name="add-constant-info">
                                    <xsl:with-param name="name" select="'CORE.MENU_CONSUMER'"/>
                                </xsl:call-template>
                                <xsl:value-of select="java:getString($dict_common, 'CORE.MENU_CONSUMER')"/>

                            </a>
                        </p>
                        <p class="menu_prof_btn">
                            <a>
                                <xsl:choose>
                                    <xsl:when test="//negeso:page/@admin-path='true'">
                                        <xsl:attribute name="href">/admin/kapper-home_<xsl:value-of select="$lang"/>.html
                                        </xsl:attribute>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:attribute name="href">/kapper-home_<xsl:value-of select="$lang"/>.html
                                        </xsl:attribute>
                                    </xsl:otherwise>
                                </xsl:choose>
                                <xsl:call-template name="add-constant-info">
                                    <xsl:with-param name="name" select="'CORE.MENU_PROFF'"/>
                                </xsl:call-template>
                                <xsl:value-of select="java:getString($dict_common, 'CORE.MENU_PROFF')"/>

                            </a>
                        </p>
                    </div>

                    <div class="header__breadcrumbs">



                        <div class="header-search">
                            <a href="search_{$lang}.html" class="search-header">
                                &lt;!&ndash;<img src="/media/search_new.png"/>&ndash;&gt;
                                &lt;!&ndash;	<span>
									<xsl:call-template name="add-constant-info">
										<xsl:with-param name="name" select="'CORE.SEARCH'"/>
									</xsl:call-template>
									<xsl:value-of select="java:getString($dict_common, 'CORE.SEARCH')"/>
								</span>&ndash;&gt;
                            </a>
                        </div>
-->
                        <xsl:if test="not(/negeso:page/@role-id = 'guest')">
                            <div class="account-content">
                                <div class="account-content-inner">
                                    <div>
                                        <a href="#">
                                            <xsl:attribute name="title">
                                                <xsl:value-of select="java:getString($dict_common, 'CORE.MY_INFO')"/>
                                            </xsl:attribute>
                                            <img src="/media/Icon_information.svg"/>
                                        </a>
                                    </div>

									<div>
                                        <xsl:choose>
                                            <xsl:when test="/negeso:page/@role-id='visitor'">
                                                <a>
                                                    <xsl:choose>
                                                        <xsl:when test="//negeso:page/@admin-path='true'">
                                                            <xsl:attribute name="href">/admin/scan_results_<xsl:value-of select="$lang"/>.html</xsl:attribute>
                                                        </xsl:when>
                                                        <xsl:otherwise>
                                                            <xsl:attribute name="href">/scan_results_<xsl:value-of select="$lang"/>.html</xsl:attribute></xsl:otherwise>
                                                    </xsl:choose>
                                                    <xsl:attribute name="title">
                                                        <xsl:value-of select="java:getString($dict_common, 'CORE.MY_RESULTS')"/>
                                                    </xsl:attribute>
                                                    <img src="/media/scan_results6.png" width="20"/></a>
                                            </xsl:when>
                                            <xsl:when test="/negeso:page/@role-id='hairdresser'">
                                                <a>
                                                    <xsl:choose>
                                                        <xsl:when test="//negeso:page/@admin-path='true'">
                                                            <xsl:attribute name="href">/admin/customers_<xsl:value-of select="$lang"/>.html</xsl:attribute>
                                                        </xsl:when>
                                                        <xsl:otherwise>
                                                            <xsl:attribute name="href">/customers_<xsl:value-of select="$lang"/>.html</xsl:attribute></xsl:otherwise>
                                                    </xsl:choose>
                                                    <xsl:attribute name="title">
                                                        <xsl:value-of select="java:getString($dict_common, 'CORE.CUSTOMERS')"/>
                                                    </xsl:attribute>
                                                    <img src="/media/scan_results6.png" width="20"/></a>
                                            </xsl:when>
                                        </xsl:choose>


									</div>

									<div>
										<a href="#" >
											<xsl:attribute name="title">
												<xsl:value-of select="java:getString($dict_common, 'CORE.MY_ORDERS')"/>
											</xsl:attribute>
											<img src="/media/icon_orders.svg"/></a>
									</div>

                                    <div class="logout">
                                        <form method="post" enctype="multipart/form-data">
                                            <input type="hidden" name="logout" value="1"/>
                                            <button class="logout-btn" type="submit" value="Uitloggen">
                                                <xsl:attribute name="title">
                                                    <xsl:value-of select="java:getString($dict_common, 'CORE.UNLOG')"/>
                                                </xsl:attribute>
                                                <img src="/media/icon_log_out.svg"/>

                                            </button>
                                        </form>
                                    </div>
                                </div>

                            </div>
                        </xsl:if>
<!--
                        <xsl:choose>
                            <xsl:when test="/negeso:page/@role-id = 'guest'">
                                <div class="account">
                                    <button class="login-popap">
                                        &lt;!&ndash;	<img src="/media/account_new.png"/>&ndash;&gt;
                                        &lt;!&ndash;<span>Login</span>&ndash;&gt;
                                    </button>
                                </div>
                            </xsl:when>
                            <xsl:otherwise>
                                <div>
                                    <img class="acc-arrow" src="/images/acc-arrow.png"/>
                                    <button class="my-acc">
                                        <xsl:call-template name="add-constant-info">
                                            <xsl:with-param name="name" select="'CORE.MY_ACCOUNT'"/>
                                        </xsl:call-template>
                                        <xsl:value-of select="java:getString($dict_common, 'CORE.MY_ACCOUNT')"/>
                                    </button>
                                </div>

                            </xsl:otherwise>
                        </xsl:choose>

                        <div class="acnt hidden">
                            <button class="account-btn">
                                &lt;!&ndash;<img src="/media/account.png"/>&ndash;&gt;
                                <xsl:value-of select="java:getString($dict_common, 'CORE.MY_ACCOUNT')"/>

                            </button>
                            <div class="ac-sublist hidden">
                                <ul>
                                    <li class="ac-list">
                                        <a href="/customer_orders_{$lang}.html">
                                            <xsl:call-template name="add-constant-info">
                                                <xsl:with-param name="name" select="'CORE.MY_ORDERS'"/>
                                            </xsl:call-template>
                                            <xsl:value-of select="java:getString($dict_common, 'CORE.MY_ORDERS')"/>

                                        </a>
                                    </li>
                                    &lt;!&ndash;                        <li class="ac-list"><a href="#">Invoices</a></li>&ndash;&gt;
                                    <li class="ac-list">
                                        <a href="/customer_info_{$lang}.html">
                                            <xsl:call-template name="add-constant-info">
                                                <xsl:with-param name="name" select="'CORE.MY_INFO'"/>
                                            </xsl:call-template>
                                            <xsl:value-of select="java:getString($dict_common, 'CORE.MY_INFO')"/>

                                        </a>
                                    </li>
                                    <li class="ac-list">
                                        <button id="logout-btn" class="account-btn">
                                            <xsl:call-template name="add-constant-info">
                                                <xsl:with-param name="name" select="'CORE.UNLOG'"/>
                                            </xsl:call-template>
                                            <xsl:value-of select="java:getString($dict_common, 'CORE.UNLOG')"/>

                                        </button>
                                    </li>
                                </ul>
                            </div>
                        </div>

                        &lt;!&ndash;<div class="shopping-cart">
							<a onfocus="blur()">
								<xsl:call-template name="pm_shopping_cart_link"/>
								&lt;!&ndash;<xsl:value-of select="java:getString($dict_pm_module, 'PM_SHOPPING_CART')"/>&ndash;&gt;
								<span class="products-in-basket">
									&lt;!&ndash;<xsl:value-of select="count(/negeso:page/negeso:contents/negeso:pm/negeso:shopping-cart/negeso:pm-product)"/>&ndash;&gt;
									&lt;!&ndash; <xsl:value-of select="/negeso:page/negeso:contents/negeso:pm/negeso:shopping-cart/@amount"/>&ndash;&gt;
									&lt;!&ndash;	<xsl:value-of select="/negeso:page/negeso:contents/negeso:icons_count/@cart"/>&ndash;&gt;
									0
								</span>
								&lt;!&ndash;<img class="basket__image" src="/media/basket_new.png"/>&ndash;&gt;
								&lt;!&ndash;<span class="basket__description">
									<xsl:call-template name="add-constant-info">
										<xsl:with-param name="name" select="'CORE.CART'"/>
									</xsl:call-template>
									<xsl:value-of select="java:getString($dict_common, 'CORE.CART')"/>
								</span>&ndash;&gt;
							</a>
						</div>&ndash;&gt;

                        &lt;!&ndash;<div class="header__search">
							<form class="form form-search" id="form-search" action="/zoeken_nl.html">
								<xsl:if test="/negeso:page/@admin-path='true'">
									<xsl:attribute name="action">/admin/zoeken_nl.html</xsl:attribute>
								</xsl:if>
								<input placeholder="ZOEKEN" onclick="this.value=''" name="query" id="search-zoeken-top" class="search" type="text" />
								<button type="submit" class="searchbutton" id="searchButtonActive">Zoek</button>
							</form>
						</div>&ndash;&gt;


                    </div>
                </div>-->
            </div>
        </header>







       <div class="limiter hidden">
            <div class="container-login100">
                <div class="wrap-login100" id="logInPopap" onClick="event.stopPropagation()">
                    <div class="close-menu-mob close-popap">
                        <div class="close-r"></div>
                        <div class="close-l"></div>
                    </div>
                    <img class="login100-logo"/>
                    <xsl:if test="/negeso:page/negeso:request/negeso:parameter[@name = 'behavior']/negeso:value/text() = 'alreadySent' or /negeso:page/negeso:request/negeso:parameter[@name = 'behavior']/negeso:value/text() = 'WRONG'">
                        <div class="b-loginEmpty">
                            <xsl:call-template name="add-constant-info">
                                <xsl:with-param name="dict" select="$dict_login_module"/>
                                <xsl:with-param name="name" select="'LM_WRONG_LOGIN'"/>
                            </xsl:call-template>
                            <xsl:value-of select="java:getString($dict_login_module, 'LM_WRONG_LOGIN')"/>
                        </div>
                        <script>
                            $('.wrap-login100').addClass('overflow-scroll')
                            $('.limiter').removeClass('hidden')
                        </script>
                    </xsl:if>
                    <form id="loginFormMain" onsubmit="upassword.value=hex_md5(p.value); p.value='';" method="post"
                          enctype="multipart/form-data">
                        <input type="hidden" name="upassword"/>
                        <input type="hidden" name="behavior" value="alreadySent"/>
                        <div class="error-input auth hidden"></div>
                        <div class="wrap-input100 validate-input" data-validate="Valid email is: a@b.c">
                            <input class="input100" type="text" name="ulogin" id="ulogin"/>
                            <span class="focus-input100" data-placeholder="Email"></span>
                        </div>

                        <div class="wrap-input100 validate-input" data-validate="Enter password">
                            <span class="btn-show-pass">
                                <i class="zmdi zmdi-eye"></i>
                            </span>
                            <input class="input100 input100-pass" type="password" name="p" id="p"/>
                            <span class="focus-input100">
                                <xsl:attribute name="data-placeholder">
                                    <xsl:call-template name="add-constant-info">
                                        <xsl:with-param name="name" select="'CORE.PASS'"/>
                                    </xsl:call-template>
                                    <xsl:value-of select="java:getString($dict_common, 'CORE.PASS')"/>
                                </xsl:attribute>
                            </span>
                            <a href="#" class="fgt-pass">
                                <xsl:call-template name="add-constant-info">
                                    <xsl:with-param name="name" select="'CORE.FORGOT_PASS'"/>
                                </xsl:call-template>
                                <xsl:value-of select="java:getString($dict_common, 'CORE.FORGOT_PASS')"/>
                            </a>
                        </div>

                        <div class="container-login100-form-btn">
                            <div class="wrap-login100-form-btn">
                                <div class="login100-form-bgbtn"></div>
                                <button type="submit" class="login100-form-btn">
                                    Login
                                </button>
                            </div>
                        </div>

                        <div class="text-center">
                            <span class="txt1">
                                <xsl:call-template name="add-constant-info">
                                    <xsl:with-param name="name" select="'CORE.DONT_HAVE_ACC'"/>
                                </xsl:call-template>
                                <xsl:value-of select="java:getString($dict_common, 'CORE.DONT_HAVE_ACC')"/>
                            </span>

                            <a class="txt2" href="/registration_{$lang}.html">
                                <xsl:call-template name="add-constant-info">
                                    <xsl:with-param name="name" select="'CORE.SIGN_UP'"/>
                                </xsl:call-template>
                                <xsl:value-of select="java:getString($dict_common, 'CORE.SIGN_UP')"/>
                            </a>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <div class="content">
            <!-- Content part -->
            <xsl:choose>
                <xsl:when test="$class='frontpage' or $class='news_pics_big_li'">

                    <xsl:call-template name="main_img"/>
                    <xsl:call-template name="frontpage_content"/>

                </xsl:when>

                <xsl:when test="$class='club-deelnemen'">

                    <div class="b-content">
                      <!--  <xsl:apply-templates select="negeso:article[negeso:head='main-menu']"/>-->

                        <div class="container">
                            <div class="club-deelnemen--content">
                                <xsl:apply-templates select="negeso:article[negeso:head='club_form']"/>
                            </div>
                        </div>
                    </div>
                </xsl:when>
                

                <xsl:when test="$class='contact_page'">
                    <div class="b-content">
                        <!--<xsl:apply-templates select="negeso:article[negeso:head='main-menu']"/>-->

                        <div class="container">
                            <div class="contact_page--content">
                               <h5> Sorry! This page is under construction!</h5>
                            </div>
                        </div>
                    </div>
                </xsl:when>

                <xsl:when test="$class='over_onze_producten'">
                    <div class="b-content">
                        <xsl:apply-templates select="negeso:article[negeso:head='main-menu']"/>
                        <div class="container">
                            <div class="over_onze_producten--content">
                                <h5> Sorry! This page is under construction!</h5>
                            </div>
                        </div>
                    </div>
                </xsl:when>



                <xsl:otherwise>


                    <div class="b-content">

                        <div class="b-contentText">
                            <!--<xsl:apply-templates select="negeso:article[negeso:head='main-menu']"/>-->
                            <xsl:call-template name="page_content"/>
                        </div>

                    </div>
                    <!-- Picture frame: at all DemoSite pages except Frontpage -->
                    <!--xsl:if test="$class!='frontpage' and $class!='news_pics_big_li'">
                           <div class="smallPicsContainer"><xsl:call-template name="images_animation"/></div>
                       </xsl:if-->
                </xsl:otherwise>
            </xsl:choose>
            <xsl:if test="not(//negeso:pm-product/negeso:rich-snippets)">
                <xsl:apply-templates select="//negeso:page/negeso:rich-snippets" mode="moving-panel"/>
            </xsl:if>

        </div>
        <!--</div>-->

        <div class="footer">
            <!--			<div class="container">-->
            <!--				<div class="footer__wrapper">-->
            <xsl:apply-templates select="//negeso:article[negeso:head='footer']"/>

        </div>

    </xsl:template>

    <!-- ======================================= Frontpage templates: BEGIN ======================================= -->

    <xsl:template name="main_img">
        <xsl:apply-templates select="negeso:article[negeso:head='main-menu']"/>
        <div class="header_img">

            <div class="main_img">

                <div class="slaider1"></div>
                <div class="slaider2"></div>
                <div class="slaider3"></div>
                <div class="slaider4"></div>
                <div class="slaider5"></div>
                <div class="slaider6"></div>
                <div class="slaider7"></div>
                <div class="slaider8"></div>
                <div class="slaider9"></div>
                <div class="slaider10"></div>



            </div>




            <div class="bgrd-color">
                <div class="main_img_text">
                       <h5>Steunje.club levert compleet verzorgde clubacties voor uw vereniging</h5>
                       <a href="#">Lees meer</a>
                </div>
            </div>

        </div>


    </xsl:template>



    <xsl:template name="frontpage_content">


        <section class="contact_block">
            <div class="container">
                <div class="contact-wrapper">
                    <div class="left_article">
                        <xsl:apply-templates select="negeso:article[negeso:head='contact_left']"/>
                    </div>
                    <div class="form_article">
                        <xsl:apply-templates select="negeso:article[negeso:head='contact_form']"/>
                    </div>
                </div>
            </div>
        </section>



        <section class="product_sld">
            <div class="container">
                <h5><a href="/webshop_nl.htm?pmCatId=koekjes">Onze lekkere smulproducten</a></h5>
                <div class="product__wrapper">
                    <xsl:apply-templates select="negeso:article[negeso:head='homepage_slaider']"/>
                    <div class="arrow__left"></div>
                    <div class="arrow__right"></div>
                </div>

            </div>
        </section>


        <section class="product_info">
            <div class="container">
                <div class="product_info__wrapper">
                    <div class="info-block">
                        <img src="../media/Mask_Group.png" alt=""/>
                    </div>
                    <div class="info-contain">
                        <div class="info-contain-left">
                            <div class="contain-left__1 all_contain left_active" data-target="info1">
                                <xsl:apply-templates select="negeso:article[negeso:head='contain-left__1']"/>
                            </div>
                            <div class="contain-left__2 all_contain" data-target="info2">
                                <xsl:apply-templates select="negeso:article[negeso:head='contain-left__2']"/>
                            </div>
                            <div class="contain-left__3 all_contain" data-target="info3">
                                <xsl:apply-templates select="negeso:article[negeso:head='contain-left__3']"/>
                            </div>
                        </div>
                        <div class="info-contain-right">
                            <div class="contain-right__1 all_right" id="info1">
                                <xsl:apply-templates select="negeso:article[negeso:head='contain-right__1']"/>
                            </div>
                            <div class="contain-right__2 all_right hidden" id="info2">
                                <xsl:apply-templates select="negeso:article[negeso:head='contain-right__2']"/>
                            </div>
                            <div class="contain-right__3 all_right hidden" id="info3">
                                <xsl:apply-templates select="negeso:article[negeso:head='contain-right__3']"/>
                            </div>
                        </div>
                    </div>

                </div>
            </div>
        </section>

        <section class="news_block">
            <div class="container">
                <h5>Komende acties</h5>
                <div class="news__wrapper">


                        <xsl:apply-templates select="negeso:article[negeso:head='nieuws-article']"/>
                        <div class="nieuws">

                                <div class="img">
                                    <xsl:for-each select="(//negeso:list/negeso:listItem)">

                                        <div class="item-image hidden">
                                            <xsl:attribute name="id">
                                                <xsl:value-of select="@id"/>
                                            </xsl:attribute>
                                            <xsl:choose>
                                                <xsl:when test="@imageLink">

                                                    <img class="newsImg"  src="{@imageLink}"  alt=""/>
                                                    <!--width="{@img_width}" height="{@img_height}"-->
                                                </xsl:when>
                                                <xsl:otherwise>

                                                    <img class="newsImg"  src="/media/nieus1.png"  alt=""/>

                                                </xsl:otherwise>
                                           </xsl:choose>



                                            <div class="img_info_news">
                                                <div class="img_info_news-wrapper">

                                                    <div class="item-info-title">
                                                        <xsl:value-of select="@title"/>
                                                    </div>

                                                    <div class="item-info-teaser">
                                                        <xsl:value-of select="negeso:teaser/negeso:article/negeso:text"
                                                                      disable-output-escaping="yes"/>
                                                    </div>

                                                    <xsl:if test="@viewDate">
                                                        <div class="viewDateFront">
                                                            <xsl:value-of select="substring(@viewDate, 0,11)"/>
                                                        </div>
                                                    </xsl:if>

                                                </div>
                                            </div>


                                    </div>
                                </xsl:for-each>
                                </div>
                            <div class="info">
                                <xsl:for-each select="(//negeso:list/negeso:listItem)">

                                    <div class="item-info">
                                        <xsl:attribute name="data-target">
                                            <xsl:value-of select="@id"/>
                                        </xsl:attribute>

                                        <div class="item-info__content">

                                            <div class="item-line"></div>

                                            <div class="item-info-title">

                                                <xsl:value-of select="@title"/>
                                            </div>

                                            <div class="data_and_link">
                                            <xsl:if test="@viewDate">
                                                <div class="viewDateFront">
                                                    <xsl:value-of select="substring(@viewDate, 0,11)"/>
                                                </div>
                                            </xsl:if>
                                                <div class="news_link">
                                                    <a>
                                                        <xsl:attribute name="href">nieuws_<xsl:value-of select="$lang"/>.html?id=<xsl:value-of
                                                        select="@id"/>
                                                </xsl:attribute>
                                                    <xsl:call-template name="add-constant-info">
                                                        <xsl:with-param name="name" select="'CORE.READ_MORE'"/>
                                                    </xsl:call-template>
                                                    <xsl:value-of select="java:getString($dict_common, 'CORE.READ_MORE')"/>
                                                    </a>
                                                </div>

                                            </div>
                                            <!--<div class="item-info-teaser">
                                                <xsl:value-of select="negeso:teaser/negeso:article/negeso:text"
                                                              disable-output-escaping="yes"/>
                                            </div>-->


                                            <!--<div class="item-btn-container">
                                                <a class="btn_small fancy-btn">
                                                    <xsl:attribute name="href">nieuws_<xsl:value-of select="$lang"/>.html?id=<xsl:value-of
                                                            select="@id"/>
                                                    </xsl:attribute>
                                                    <xsl:call-template name="add-constant-info">
                                                        <xsl:with-param name="name" select="'CORE.READ_MORE'"/>
                                                    </xsl:call-template>
                                                    <xsl:value-of select="java:getString($dict_common, 'CORE.READ_MORE')"/>
                                                </a>
                                            </div>-->
                                        </div>
                                    </div>
                            </xsl:for-each>
                            </div>


                        </div>

                </div>

            </div>
        </section>

        <!--<section class="buy-product">
            <div class="container">
                &lt;!&ndash;<div class="buy-product__wrapper">
                    <div class="buy-product__images">
                        <img class="buy-product__photo aos" data-aos="fade-right" data-aos-duration="3000"
                             data-aos-offset="300" src="/media/fingerprintGarantie.png" alt="haircut"/>
                    </div>
                    <div class="buy-product__description">
                        <xsl:apply-templates select="negeso:article[negeso:head='buy-product__desc']"/>
                        <a class="buy-product__button button-main button-main&#45;&#45;black">
                            <xsl:choose>
                                <xsl:when test="//negeso:page/@admin-path='true'">
                                    <xsl:attribute name="href">/admin/tevredenheidsgarantie_<xsl:value-of
                                            select="$lang"/>.html
                                    </xsl:attribute>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:attribute name="href">/tevredenheidsgarantie_<xsl:value-of select="$lang"/>.html
                                    </xsl:attribute>
                                </xsl:otherwise>
                            </xsl:choose>
                            <xsl:call-template name="add-constant-info">
                                <xsl:with-param name="name" select="'CORE.READ_MORE'"/>
                            </xsl:call-template>
                            <xsl:value-of select="java:getString($dict_common, 'CORE.READ_MORE')"/>
                            <svg aria-hidden="true" focusable="false" data-prefix="fas" data-icon="book-reader"
                                 class="button__icon" role="img" xmlns="http://www.w3.org/2000/svg"
                                 viewBox="0 0 512 512">
                                <path fill="currentColor"
                                      d="M352 96c0-53.02-42.98-96-96-96s-96 42.98-96 96 42.98 96 96 96 96-42.98 96-96zM233.59 241.1c-59.33-36.32-155.43-46.3-203.79-49.05C13.55 191.13 0 203.51 0 219.14v222.8c0 14.33 11.59 26.28 26.49 27.05 43.66 2.29 131.99 10.68 193.04 41.43 9.37 4.72 20.48-1.71 20.48-11.87V252.56c-.01-4.67-2.32-8.95-6.42-11.46zm248.61-49.05c-48.35 2.74-144.46 12.73-203.78 49.05-4.1 2.51-6.41 6.96-6.41 11.63v245.79c0 10.19 11.14 16.63 20.54 11.9 61.04-30.72 149.32-39.11 192.97-41.4 14.9-.78 26.49-12.73 26.49-27.06V219.14c-.01-15.63-13.56-28.01-29.81-27.09z"></path>
                            </svg>
                        </a>
                    </div>
                </div>&ndash;&gt;
            </div>
        </section>-->

        <!--<section class="contact-us">
            <div class="container">
                &lt;!&ndash;<div class="contact-us__wrapper">
                    <div class="contact-us__form form aos" data-aos="fade-down-right" data-aos-duration="2000"
                         data-aos-offset="10">
                        <xsl:apply-templates select="negeso:article[negeso:head='contactus__form']"/>
                    </div>
                    &lt;!&ndash;					<div class="contact-us__info">&ndash;&gt;
                    <div class="contact-us__info-inner">

                        <xsl:apply-templates select="negeso:article[negeso:head='contactus__desc']"/>

                        <xsl:apply-templates select="negeso:article[negeso:head='contactus__contacts']"/>

                        <div class="social contact-us__social">
                            <xsl:apply-templates select="negeso:article[negeso:head='contactus__social']"/>
                        </div>
                        &lt;!&ndash;						</div>&ndash;&gt;
                    </div>
                </div>&ndash;&gt;
            </div>
        </section>-->

    </xsl:template>
    <!-- ======================================= Frontpage templates: END ======================================= -->


    <!-- ======================================= Page content templates: BEGIN =======================================
    -->
    <xsl:template name="page_content">
        <!-- Current tag: negeso:contents -->
        <xsl:choose>
            <!-- Search news page -->
            <xsl:when test="$class='search_news'">
                <xsl:apply-templates select="negeso:article[negeso:head!='footer']"/>
                <xsl:apply-templates select="negeso:listItem" mode="news_details_search"/>
            </xsl:when>
            <!-- Newsline -->
            <xsl:when test="$class='newsline'">
                <xsl:choose>
                    <xsl:when test="negeso:listItem/negeso:details">
                        <xsl:apply-templates select="negeso:listItem" mode="newsline_details"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:apply-templates select="negeso:list" mode="newsline"/>
                        <xsl:call-template name="paging">
                            <xsl:with-param name="paging_style" select="'new'"/>
                        </xsl:call-template>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <!-- Job module -->
            <xsl:when test="$class='job'">
                <xsl:apply-templates select="negeso:job_module_component" mode="job"/>
            </xsl:when>
            <!-- Glossary module -->
            <xsl:when test="$class='glossary_module'">
                <xsl:apply-templates select="negeso:glossary_module"/>
            </xsl:when>
            <!-- Lite event -->
            <xsl:when test="$class='lite-event'">
                <!-- shortcut "em" == Event Module -->
                <xsl:apply-templates select="negeso:EventViewComponent" mode="em"/>
                <!--
                    Now this template is called from [xsl:template match="negeso:EventViewComponent" mode="em"].
                    But you may easy use it separated.
                <xsl:apply-templates select="negeso:NearEventsComponent" mode="em" />
                -->
            </xsl:when>
            <!-- Lite event calendar -->
            <xsl:when test="$class='lite-event-calendar'">
                <xsl:apply-templates select="negeso:EventCalendarsComponent" mode="em"/>
            </xsl:when>
            <!-- Sitemap -->
            <xsl:when test="$class='sitemap3'">
                <xsl:call-template name="sitemap">
                    <xsl:with-param name="template_type" select="3"/>
                    <xsl:with-param name="table_spacing" select="3"/>
                </xsl:call-template>
            </xsl:when>
            <!-- Marquee -->
            <xsl:when test="negeso:article[@class='marquee']">
                <xsl:apply-templates select="negeso:article[@class='marquee']"/>
            </xsl:when>
            <!-- FAQ module -->
            <xsl:when test="$class='faqsimple' or $class='faqlink'">
                <xsl:apply-templates select="negeso:list[@type='faqsimple' or @type='faqlink']"/>
            </xsl:when>
            <!-- Newsletter -->
            <xsl:when test="$class='newsletter'">
                <xsl:apply-templates select="negeso:newsletter" mode="newsletter"/>
            </xsl:when>
            <!-- Document module -->
            <xsl:when test="$class='document'">
                <xsl:call-template name="documentmodule"/>
            </xsl:when>
            <!-- Product module -->
            <xsl:when test="$class='pm'">
                <xsl:call-template name="pm"/>
            </xsl:when>
            <xsl:when test="$class= 'registration'">
                <xsl:call-template name="registration"/>
            </xsl:when>
            <xsl:when test="$class='ingredient'">
                <xsl:call-template name="ingredient-page"/>
            </xsl:when>
            <xsl:when test="$class='additional-info'">
                <xsl:call-template name="addition-info"/>
            </xsl:when>
            <xsl:when test="$class='oil-serum'">
                <xsl:call-template name="oil-serum_page"/>
            </xsl:when>
            <xsl:when test="$class='enviroment'">
                <xsl:call-template name="enviroment-page"/>
            </xsl:when>

            <xsl:when test="$class='contact-page'">
                <xsl:call-template name="contact-page"/>
            </xsl:when>

            <!--<xsl:when test="$class='contact-page'">
                <xsl:call-template name="partner-worden"/>
            </xsl:when>-->




            <xsl:when test="$class='webshop'">
                <xsl:call-template name="webshop"/>
            </xsl:when>
            <xsl:when test="$class='webshop_cart'">
                <xsl:call-template name="webshop_cart"/>
            </xsl:when>
            <xsl:when test="$class='webshop_checkout'">
                <xsl:call-template name="webshop_checkout"/>
            </xsl:when>
            <xsl:when test="$class='webshop_order'">
                <xsl:call-template name="webshop_order"/>
            </xsl:when>
            <xsl:when test="$class='kapper-concept-consumer'">
                <xsl:call-template name="kapper-concept-consumer"/>
            </xsl:when>

            <!-- Prossesional menu items -->
            <xsl:when test="$class='kapper-home'">
                <xsl:call-template name="kapper-home"/>
                <xsl:call-template name="prof_menu"/>
                <script>
                    $('.kapper-hp').addClass('active');
                </script>
            </xsl:when>
            <xsl:when test="$class='kapper-concept'">
                <xsl:call-template name="kapper-concept"/>
                <xsl:call-template name="prof_menu"/>
                <script>
                    $('.kapper-concept').addClass('active');
                </script>
            </xsl:when>

            <xsl:when test="$class='del_club'">
                <xsl:apply-templates select="negeso:article[negeso:head='club_form']"/>
                <xsl:call-template name="partner-worden"/>
            </xsl:when>

            <xsl:when test="$class='smart-hair-analyser'">
                <xsl:call-template name="smart-hair-analyser"/>
                <xsl:call-template name="prof_menu"/>
                <script>
                    $('.hair-analyser').addClass('active');
                </script>
            </xsl:when>

            <xsl:when test="$class='products-prof'">
                <xsl:call-template name="products-prof"/>
                <xsl:call-template name="prof_menu"/>
                <script>
                    $('.prod-prof').addClass('active');
                </script>
            </xsl:when>

            <xsl:when test="$class='environment'">
                <xsl:call-template name="enviroment-page"/>

            </xsl:when>

            <xsl:when test="$class='faq-prof'">
                <xsl:call-template name="faq-prof"/>
                <xsl:call-template name="prof_menu"/>
                <script>
                    $('.faq-prof').addClass('active');
                </script>
            </xsl:when>

            <xsl:when test="$class='garantie-prof'">
                <xsl:call-template name="garantie-prof"/>
                <xsl:call-template name="prof_menu"/>
                <script>
                    $('.garant-prof').addClass('active');
                </script>
            </xsl:when>

            <xsl:when test="$class='contacts-prof'">
                <xsl:call-template name="contacts-prof"/>
                <xsl:call-template name="prof_menu"/>
                <script>
                    $('.contact-prof').addClass('active');
                </script>
            </xsl:when>

			<xsl:when test="$class='ingred-prof'">
				<xsl:call-template name="ingred-prof"/>
				<xsl:call-template name="prof_menu"/>
				<script>
					$('.ingred-prof').addClass('active');
				</script>
			</xsl:when>

			<xsl:when test="$class='scan_results'">
				<xsl:call-template name="scan_user_results"/>
			</xsl:when>
			<!-- Mail to a friend -->
			<xsl:when test="$class='mail_to_a_friend'">
				<xsl:apply-templates select="negeso:mail_to_a_friend"/>
			</xsl:when>
			<!-- Contact book -->
			<xsl:when test="$class='contact_book'">
				<xsl:call-template name="contact_book" />
			</xsl:when>
			<!-- Photo album -->
			<xsl:when test="$class='photo_album'">
				<xsl:apply-templates select="negeso:photo_album" mode="pa"/>
				<xsl:if test="//negeso:photo[@selected = 'true']">
					<xsl:call-template name="paging">
						<xsl:with-param name="paging_style" select="'new'" />
					</xsl:call-template>
				</xsl:if>
			</xsl:when>
			<!-- Login module -->
			<xsl:when test="$class='login'">
				<xsl:call-template name="login" />
			</xsl:when>
			<!-- Gueset book -->
			<xsl:when test="$class='guestbook'">
				<xsl:apply-templates select="negeso:guestbook" mode="gb" />
				<xsl:if test="negeso:guestbook/negeso:PageNavigator">
					<xsl:call-template name="paging"><xsl:with-param name="paging_style" select="'new'" /></xsl:call-template>
				</xsl:if>
			</xsl:when>
			<!-- Gueset book - Post message form -->
			<xsl:when test="$class='post_gb_message'">
				<xsl:call-template name="gb_guestbook_form" />
			</xsl:when>
            <xsl:when test="$class='customers'">
                <xsl:choose>
                    <xsl:when test="/negeso:page/@role-id='hairdresser'">
                        <xsl:call-template name="customers"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <script>
                            window.location.href = '/'
                        </script>
                    </xsl:otherwise>
                </xsl:choose>

            </xsl:when>
			<!-- Search module -->
			<xsl:when test="$class='search'">
				<xsl:choose>
					<xsl:when test="negeso:search/@mode = 'advanced'">
						<xsl:call-template name="sm_advanced_form" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="sm_form" />
					</xsl:otherwise>
				</xsl:choose>
				<xsl:call-template name="paging"><xsl:with-param name="paging_style" select="'new'" /></xsl:call-template>
			</xsl:when>
			<!-- Inquiry module -->
			<xsl:when test="$class='inquiry'">
				<xsl:apply-templates select="negeso:inquiry" mode="inq" />
			</xsl:when>
			<!-- Store locator -->
			<xsl:when test="$class='store'">
				<xsl:apply-templates select="negeso:store_locator" />
			</xsl:when>
			<!-- Store locator - extended -->
			<xsl:when test="$class='ex_store'">
				<xsl:apply-templates select="negeso:ex_store_locator" />
			</xsl:when>
			<!-- Flip book -->
			<xsl:when test="$class='flip_book'">
				<xsl:call-template name="flip-book" />
			</xsl:when>
			<xsl:when test="$class='user_page'">
				<div class="user_page">
					<xsl:call-template name="user_page" />
				</div>
			</xsl:when>
			<xsl:when test="$class='webshop_wishlist'">
				<xsl:call-template name="webshop_wishlist" />
			</xsl:when>
			<xsl:when test="$class='articelenBesteling'">
				<div class="articelenBesteling">
					<xsl:call-template name="articelenBesteling" />
				</div>
			</xsl:when>
			<!-- One-article page -->
			<xsl:otherwise>
				<xsl:apply-templates select="negeso:article[negeso:head!='footer' and negeso:head!='contact-footer']"/>
			</xsl:otherwise>
		</xsl:choose>

    </xsl:template>

   <!-- <xsl:template name="contact-page">


    </xsl:template>-->
    <!-- ======================================= Page content templates: END =======================================
    -->

    <!-- ==================================== Container top templates: BEGIN ====================================
    -->























    <xsl:template name="languages">
        <div class="lang">

            <span class="lang-code">
                <xsl:value-of select="/negeso:page/negeso:languages/negeso:language[@selected='true']/@code"/>
            </span>
            <span class="lang-arrow-down"></span>

			<div class="languageBox hidden">
				<xsl:apply-templates select="/negeso:page/negeso:languages/negeso:language" mode="oneLanguage">
					<xsl:sort select="@name" order="ascending"/>
				</xsl:apply-templates>
			</div>
        </div>
    </xsl:template>
    <xsl:template name="customers">
        <div class="container customers">
            <h1 class="main-title"><xsl:value-of select="java:getString($dict_common, 'CORE.CUSTOMERS')"/></h1>
            <div>
                <ul id="customer-list"></ul>
            </div>
        </div>
    </xsl:template>
    <xsl:template name="account">
        <xsl:choose>
            <xsl:when test="not(/negeso:page/@role-id = 'guest')">
                <div class="container">
                    <div class="account-wrapper">
                        <div class="account-menu">
                            <ul class="account-list">
                                <li class="account-list-item active-link" data-target="info">My info</li>
                                <li id="acc-clients" class="account-list-item" data-target="clients">Clients</li>
                                <li id="acc-orders" class="account-list-item" data-target="orders">Orders</li>
                            </ul>
                        </div>
                        <div class="account-desc">
                            <div class="desc-item" id="info">
                                <h1 class="account-title">Information</h1>
                                <p id="acc-phone" class="account-text">Telephone: <span>989274837492</span>
                                </p>
                                <p id="acc-email" class="account-text">Email:
                                    <span>batsan@bob.com</span>
                                </p>
                                <p id="acc-name" class="account-text">Full name:
                                    <span>Dendy Braun</span>
                                </p>
                                <p id="acc-address" class="account-text">Address:
                                    <span>Shamrilo str. appar 131</span>
                                </p>
                                <p id="acc-city" class="account-text">City:
                                    <span>Kyiv</span>
                                </p>
                            </div>
                            <div class="desc-item hidden" id="clients">
                                <h1 class="account-title">Clients</h1>
                            </div>
                            <div class="desc-item hidden" id="orders">
                                <h1 class="account-title">Orders</h1>
                            </div>
                        </div>
                    </div>
                </div>
            </xsl:when>
            <xsl:otherwise>
                <h2>Please login first</h2>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template name="ingredient-page">
        <div class="ingridient_wrapper">
            <!--<xsl:call-template name="ingredient_block-2">
			<xsl:with-param name="img" select="'/media/ingredients/img_2.png'"/>
			<xsl:with-param name="background" select="'#F0DFD5'"/>
			<xsl:with-param name="article" select="'ingredient_box-2'"/>
		</xsl:call-template>-->
            <div class="ingr-block_fluid">
                <div class="wrapper">
                    <div class="ingr-text">
                        <xsl:apply-templates select="//negeso:article[negeso:head='ingredient_box-3']"/>
                    </div>
                </div>
            </div>

            <p class="ingr-title" id="ingredient-title">
                <xsl:apply-templates select="negeso:article[negeso:head='ingr-group-1']"/>
            </p>

            <xsl:call-template name="ingredient_block-1">
                <xsl:with-param name="img" select="'/media/ingredients/img_3.png'"/>
                <xsl:with-param name="article" select="'ingredient_box-4'"/>
            </xsl:call-template>
            <xsl:call-template name="ingredient_block-3">
                <xsl:with-param name="img" select="'/media/ingredients/img_4.png'"/>
                <xsl:with-param name="article" select="'ingredient_box-5'"/>
                <xsl:with-param name="type" select="'right'"/>
            </xsl:call-template>
            <xsl:call-template name="ingredient_block-3">
                <xsl:with-param name="img" select="'/media/ingredients/img_7.png'"/>
                <xsl:with-param name="article" select="'ingredient_box-8'"/>
                <xsl:with-param name="type" select="'left'"/>
            </xsl:call-template>
            <xsl:call-template name="ingredient_block-2">
                <xsl:with-param name="img" select="'/media/ingredients/chea_butter.jpg'"/>
                <xsl:with-param name="background" select="'white'"/>
                <xsl:with-param name="article" select="'ingredient_box-butter'"/>
            </xsl:call-template>

            <p class="ingr-title" id="ingr-group-2">
                <xsl:apply-templates select="negeso:article[negeso:head='ingr-group-2']"/>
            </p>

            <xsl:call-template name="ingredient_block-3">
                <xsl:with-param name="img" select="'/media/ingredients/img_8.png'"/>
                <xsl:with-param name="article" select="'ingredient_box-9'"/>
                <xsl:with-param name="type" select="'right'"/>
            </xsl:call-template>
            <xsl:call-template name="ingredient_block-2">
                <xsl:with-param name="img" select="'/media/ingredients/img_6.png'"/>
                <xsl:with-param name="background" select="'white'"/>
                <xsl:with-param name="article" select="'ingredient_box-7'"/>
            </xsl:call-template>
            <xsl:call-template name="ingredient_block-3">
                <xsl:with-param name="img" select="'/media/ingredients/cocoa_butter.jpg'"/>
                <xsl:with-param name="article" select="'ingredient_box-cocoa_butter'"/>
                <xsl:with-param name="type" select="'left'"/>
            </xsl:call-template>
            <xsl:call-template name="ingredient_block-2">
                <xsl:with-param name="img" select="'/media/ingredients/maca.jpg'"/>
                <xsl:with-param name="background" select="'white'"/>
                <xsl:with-param name="article" select="'ingredient_box-maca'"/>
            </xsl:call-template>

            <p class="ingr-title" id="ingr-group-2">
                <xsl:apply-templates select="negeso:article[negeso:head='ingr-group-3']"/>
            </p>

            <xsl:call-template name="ingredient_block-3">
                <xsl:with-param name="img" select="'/media/ingredients/img_11.png'"/>
                <xsl:with-param name="article" select="'ingredient_box-12'"/>
                <xsl:with-param name="type" select="'last'"/>
            </xsl:call-template>
            <xsl:call-template name="ingredient_block-3">
                <xsl:with-param name="img" select="'/media/ingredients/img_5.png'"/>
                <xsl:with-param name="alt" select="'/media/ingredients/img_2.png'"/>
                <xsl:with-param name="article" select="'ingredient_box-6'"/>
                <xsl:with-param name="type" select="'left'"/>
                <xsl:with-param name="changeimg" select="'true'"/>
            </xsl:call-template>
            <xsl:call-template name="ingredient_block-3">
                <xsl:with-param name="img" select="'/media/ingredients/img_9.png'"/>
                <xsl:with-param name="article" select="'ingredient_box-10'"/>
                <xsl:with-param name="type" select="'left'"/>
            </xsl:call-template>
            <xsl:call-template name="ingredient_block-4">
                <xsl:with-param name="img" select="'/media/ingredients/img_1.png'"/>
                <xsl:with-param name="article" select="'ingredient_box-1'"/>
                <xsl:with-param name="type" select="'right'"/>
            </xsl:call-template>
            <xsl:call-template name="ingredient_block-1">
                <xsl:with-param name="img" select="'/media/ingredients/wheat.jpg'"/>
                <xsl:with-param name="article" select="'ingredient_box-11'"/>
            </xsl:call-template>
            <xsl:call-template name="ingredient_block-4">
                <xsl:with-param name="img" select="'/media/ingredients/castor.jpg'"/>
                <xsl:with-param name="article" select="'ingred-castor-oil'"/>
                <xsl:with-param name="type" select="'right'"/>
            </xsl:call-template>


        </div>
    </xsl:template>

    <xsl:template name="ingredient-title">
        <xsl:param name="id"/>
        <xsl:param name="article"/>

        <p class="ingr-title" id="{$id}">
            <xsl:apply-templates select="negeso:article[negeso:head=$article]"/>
        </p>

    </xsl:template>

    <xsl:template name="ingredient_block-1">
        <xsl:param name="img"/>
        <xsl:param name="article"/>
        <div class="ingr-section">
            <div class="wrapper ingr">
                <img src="{$img}" class="block-1_img"/>
                <div class="ingr-section_main">
                    <div class="ingr-text ingr-block_text block_1-text">
                        <xsl:apply-templates select="//negeso:article[negeso:head=$article]"/>
                    </div>
                </div>

            </div>
        </div>
    </xsl:template>
    <xsl:template name="ingredient_block-2">
        <xsl:param name="img"/>
        <xsl:param name="background"/>
        <xsl:param name="article"/>
        <div class="ingr-section ">
            <xsl:attribute name="style">
                background-color:<xsl:value-of select="$background"/>
            </xsl:attribute>
            <div class="wrapper ingr ingr-rev">
                <img src="{$img}" class="block-2_img"/>
                <div class="ingr-section_main">
                    <div class="ingr-text ingr-block_text block_2-text">
                        <xsl:apply-templates select="//negeso:article[negeso:head=$article]"/>
                    </div>
                </div>

            </div>

        </div>
    </xsl:template>
    <xsl:template name="ingredient_block-3">
        <xsl:param name="img"/>
        <xsl:param name="alt"/>
        <xsl:param name="article"/>
        <xsl:param name="changeimg"/>
        <xsl:param name="type"/>
        <div class="ingr-section ">
            <div class="wrapper ingr ">
                <xsl:if test="$type='right'">
                    <xsl:attribute name="class">
                        wrapper ingr ingr-rev
                    </xsl:attribute>
                </xsl:if>
                <img src="{$img}" alt="{$alt}" class="block-1_img">
                    <xsl:attribute name="class">
                        <xsl:choose>
                            <xsl:when test="$type='right'">
                                block-2_img
                            </xsl:when>
                            <xsl:when test="$type='last'">
                                ingr_last-img
                            </xsl:when>
                            <xsl:when test="$changeimg='true'">
                                changed-img block-1_img
                            </xsl:when>
                            <xsl:otherwise>
                                block-1_img
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:attribute>
                </img>
                <div class="ingr-section_main">
                    <!--<xsl:if test="$type='last'">
						<xsl:attribute name="style">
							background-color:white
						</xsl:attribute>
					</xsl:if>-->
                    <div class="ingr-text ingr-block_text braun-text">
                        <xsl:attribute name="style">
                            <xsl:choose>
                                <xsl:when test="$type='right'">
                                    right:50%
                                </xsl:when>
                                <xsl:when test="$type='last'">
                                    left:50%; z-index:3
                                </xsl:when>
                                <xsl:otherwise>
                                    left:50%
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:attribute>
                        <xsl:apply-templates select="//negeso:article[negeso:head=$article]"/>
                    </div>
                </div>

            </div>
        </div>
    </xsl:template>
    <xsl:template name="ingredient_block-4">
        <xsl:param name="img"/>
        <xsl:param name="alt"/>
        <xsl:param name="article"/>
        <xsl:param name="changeimg"/>
        <xsl:param name="type"/>
        <div class="ingr_big_section ">
            <div class="wrapper ingr ">
                <xsl:if test="$type='right'">
                    <xsl:attribute name="class">
                        wrapper ingr ingr-rev
                    </xsl:attribute>
                </xsl:if>
                <img src="{$img}" alt="{$alt}" class="block-1_img">
                    <xsl:attribute name="class">
                        <xsl:choose>
                            <xsl:when test="$type='right'">
                                block-2_img
                            </xsl:when>
                            <xsl:when test="$type='last'">
                                ingr_last-img
                            </xsl:when>
                            <xsl:when test="$changeimg='true'">
                                changed-img block-1_img
                            </xsl:when>
                            <xsl:otherwise>
                                block-1_img
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:attribute>
                </img>
                <div class="ingr-section_main">
                    <xsl:if test="$type='last'">
                        <xsl:attribute name="style">
                            background-color:white
                        </xsl:attribute>
                    </xsl:if>
                    <div class="ingr-text ingr-block_text braun-text">
                        <xsl:attribute name="style">
                            <xsl:choose>
                                <xsl:when test="$type='right'">
                                    right:50%
                                </xsl:when>
                                <xsl:when test="$type='last'">
                                    left:50%; z-index:3
                                </xsl:when>
                                <xsl:otherwise>
                                    left:50%
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:attribute>
                        <xsl:apply-templates select="//negeso:article[negeso:head=$article]"/>
                    </div>
                </div>

            </div>
        </div>
    </xsl:template>

	<xsl:template name="oil-serum_page">
		<div class="serum-block">
		<div class="wrapper relative-wrapper">
			<img class="bg-img serum_img2" src="/media/oil-serum/eclipse.png"/>
			<xsl:apply-templates select="//negeso:article[negeso:head='oil-serum-title']"/>
			<div class="oil-serum">
				<img class="bg-img serum_img1" src="/media/oil-serum/fingerprint.png"/>
			<div class="oil-serum_item oil-serum-text">
				<xsl:apply-templates select="//negeso:article[negeso:head='oil-serum']"/>
			</div>
			<img class="oil-serum_item" src="/media/oil-serum/bottle.png"/>
		</div>
		</div>
		</div>
	</xsl:template>


	<xsl:template name="enviroment-page">

		<div class="env-blocks">
            <xsl:apply-templates select="//negeso:article[negeso:head='onnze_prod_text']"/>
			<div class="wrapper">
				<xsl:call-template name="env-block">
					<xsl:with-param name="type" select="'left'"/>
					<xsl:with-param name="pic" select="'/media/product1.png'"/>
					<xsl:with-param name="background" select="'#F8F7F2'"/>
					<xsl:with-param name="article" select="'enviroment-block1'"/>
				</xsl:call-template>
				<xsl:call-template name="env-block">
					<xsl:with-param name="type" select="'right'"/>
					<xsl:with-param name="pic" select="'/media/product2.png'"/>
					<xsl:with-param name="background" select="'#AD9677'"/>
					<xsl:with-param name="article" select="'enviroment-block2'"/>
				</xsl:call-template>
				<xsl:call-template name="env-block">
					<xsl:with-param name="type" select="'left'"/>
					<xsl:with-param name="pic" select="'/media/product3.png'"/>
					<xsl:with-param name="background" select="'#F8F7F2'"/>
					<xsl:with-param name="article" select="'enviroment-block3'"/>
				</xsl:call-template>
				<div class="onnze_prod_last_text">
					<xsl:apply-templates select="//negeso:article[negeso:head='onnze_prod_last_text']"/>
				</div>
			</div>
		</div>
	</xsl:template>


	<xsl:template name="env-block">
		<xsl:param name="type"/>
		<xsl:param name="background"/>
		<xsl:param name="article"/>
		<xsl:param name="pic"/>
		<div class="env-block">
			<xsl:choose>
				<xsl:when test="$type='right'">
					<div class="env-block-item white-text">
						<xsl:attribute name="style">
							background-color:<xsl:value-of select="$background"/>
						</xsl:attribute>
						<xsl:apply-templates select="//negeso:article[negeso:head=$article]"/>
					</div>
					<img class="env-block-img" src="{$pic}"/>
				</xsl:when>
				<xsl:otherwise>
					<img class="env-block-img" src="{$pic}"/>
					<div class="env-block-item">
						<xsl:attribute name="style">
							background-color:<xsl:value-of select="$background"/>
						</xsl:attribute>
						<xsl:apply-templates select="//negeso:article[negeso:head=$article]"/>
					</div>
				</xsl:otherwise>
			</xsl:choose>
		</div>
	</xsl:template>
	<xsl:template name="kapper-concept">
		<div class="kapper">
			<div class="wrapper">
				<xsl:apply-templates select="//negeso:article[negeso:head='kapper-head']"/>
			</div>

			<xsl:call-template name="ingredient_block-3">
				<xsl:with-param name="img" select="'/media/kapper-concept/image_3.jpg'"/>
				<xsl:with-param name="article" select="'kapper-block3'"/>
				<xsl:with-param name="type" select="'left'"/>
			</xsl:call-template>

			<xsl:call-template name="ingredient_block-3">
				<xsl:with-param name="img" select="'/media/kapper-concept/image_1.jpg'"/>
				<xsl:with-param name="article" select="'kapper-block1'"/>
				<xsl:with-param name="type" select="'left'"/>
			</xsl:call-template>

				<!--<xsl:call-template name="ingredient_block-1">
					<xsl:with-param name="img" select="'/media/kapper-concept/image_1.jpg'"/>
					<xsl:with-param name="article" select="'kapper-block1'"/>
				</xsl:call-template>-->

				<xsl:call-template name="ingredient_block-2">
					<xsl:with-param name="img" select="'/media/kapper-concept/image_2.jpg'"/>
					<xsl:with-param name="article" select="'kapper-block2'"/>
				</xsl:call-template>
			<!--<xsl:call-template name="ingredient_block-2">
				<xsl:with-param name="img" select="'/media/kapper-concept/image_4.jpg'"/>
				<xsl:with-param name="article" select="'kapper-block4'"/>
			</xsl:call-template>
			<xsl:call-template name="ingredient_block-3">
				<xsl:with-param name="img" select="'/media/kapper-concept/image_5.jpg'"/>
				<xsl:with-param name="article" select="'kapper-block5'"/>
				<xsl:with-param name="type" select="'left'"/>
			</xsl:call-template>-->
            <xsl:call-template name="ingredient_block-2">
                <xsl:with-param name="img" select="'/media/kapper-concept/image_6.png'"/>
                <xsl:with-param name="article" select="'kapper-block6'"/>
            </xsl:call-template>
            <!--<xsl:call-template name="ingredient_block-3">
				<xsl:with-param name="img" select="'/media/kapper-concept/image_7.jpg'"/>
				<xsl:with-param name="article" select="'kapper-block7'"/>
				<xsl:with-param name="type" select="'left'"/>
			</xsl:call-template>-->
            <xsl:call-template name="ingredient_block-2">
                <xsl:with-param name="img" select="'/media/kapper-concept/image_8.jpg'"/>
                <xsl:with-param name="article" select="'kapper-block8'"/>
            </xsl:call-template>
        </div>

    </xsl:template>
    <xsl:template name="kapper-concept-consumer">
        <div class="kapper">
            <div class="wrapper">
                <xsl:apply-templates select="//negeso:article[negeso:head='kapper-head-consumer']"/>
            </div>

			<xsl:call-template name="ingredient_block-3">
				<xsl:with-param name="img" select="'/media/kapper-concept/image_3.jpg'"/>
				<xsl:with-param name="article" select="'kapper-block-consum3'"/>
				<xsl:with-param name="type" select="'left'"/>
			</xsl:call-template>
			<xsl:call-template name="ingredient_block-3">
				<xsl:with-param name="img" select="'/media/kapper-concept/image_1.jpg'"/>
				<xsl:with-param name="article" select="'kapper-block-consum1'"/>
				<xsl:with-param name="type" select="'left'"/>
			</xsl:call-template>

			<!--<xsl:call-template name="ingredient_block-1">
				<xsl:with-param name="img" select="'/media/kapper-concept/image_1.jpg'"/>
				<xsl:with-param name="article" select="'kapper-block-consum1'"/>
			</xsl:call-template>-->
			<xsl:call-template name="ingredient_block-2">
				<xsl:with-param name="img" select="'/media/kapper-concept/image_2.jpg'"/>
				<xsl:with-param name="article" select="'kapper-block-consum2'"/>
			</xsl:call-template>
			<!--<xsl:call-template name="ingredient_block-2">
				<xsl:with-param name="img" select="'/media/kapper-concept/image_4.jpg'"/>
				<xsl:with-param name="article" select="'kapper-block-consum4'"/>
			</xsl:call-template>
			<xsl:call-template name="ingredient_block-3">
				<xsl:with-param name="img" select="'/media/kapper-concept/image_5.jpg'"/>
				<xsl:with-param name="article" select="'kapper-block-consum5'"/>
				<xsl:with-param name="type" select="'left'"/>
			</xsl:call-template>-->
            <xsl:call-template name="ingredient_block-2">
                <xsl:with-param name="img" select="'/media/kapper-concept/image_6.png'"/>
                <xsl:with-param name="article" select="'kapper-block-consum6'"/>
            </xsl:call-template>
            <!--<xsl:call-template name="ingredient_block-3">
				<xsl:with-param name="img" select="'/media/kapper-concept/image_7.jpg'"/>
				<xsl:with-param name="article" select="'kapper-block-consum7'"/>
				<xsl:with-param name="type" select="'left'"/>
			</xsl:call-template>-->
            <xsl:call-template name="ingredient_block-2">
                <xsl:with-param name="img" select="'/media/kapper-concept/image_8.jpg'"/>
                <xsl:with-param name="article" select="'kapper-block-consum8'"/>
            </xsl:call-template>
        </div>

    </xsl:template>





    <xsl:template name="kapper-home">
        <section>
            <div class="kapper-home__header">
                <div class="header__slider-wrapper">
                    <!--					&lt;!&ndash;slide 1&ndash;&gt;-->
                    <!--					<div class="header__slides">-->
                    <!--						<div class="slide-3 header__slide">-->
                    <!--							<div class="slide-3__image"></div>-->
                    <!--							<div class="container">-->
                    <!--								<div class="slide-3-inner">-->
                    <!--									<div class="slide-3__description">-->
                    <!--										<h1 class="slide-3__title">Gepersonaliseerde haarverzorgings- producten</h1>-->
                    <!--										<a class="slide-3__button button-main button-main&#45;&#45;white">-->
                    <!--											<xsl:choose>-->
                    <!--												<xsl:when test="//negeso:page/@admin-path='true'">-->
                    <!--													<xsl:attribute name="href">/products-prof_<xsl:value-of select="$lang"/>.html</xsl:attribute>-->
                    <!--												</xsl:when>-->
                    <!--												<xsl:otherwise><xsl:attribute name="href">/products-prof_<xsl:value-of select="$lang"/>.html</xsl:attribute></xsl:otherwise>-->
                    <!--											</xsl:choose>-->
                    <!--											Lees meer-->
                    <!--										</a>-->
                    <!--									</div>-->
                    <!--								</div>-->
                    <!--							</div>-->
                    <!--						</div>-->
                    <!--					</div>-->

                    <!--slide 1-->
                    <div class="header__slides">
                        <div class="slide-3 header__slide">
                            <div class="container">
                                <div class="slide-3-inner">
                                    <div class="slide-3__description">
                                        <xsl:apply-templates
                                                select="negeso:article[negeso:head='header__slides_kapper_article_1']"/>
                                        <!--		<h1 class="slide-3__title">Gepersonaliseerde haarverzorgings-<br/>producten</h1>-->
                                        <a class="slide-3__button button-main button-main--black">
                                            <xsl:choose>
                                                <xsl:when test="//negeso:page/@admin-path='true'">
                                                    <xsl:attribute name="href">/admin/products-prof_<xsl:value-of
                                                            select="$lang"/>.html
                                                    </xsl:attribute>
                                                </xsl:when>
                                                <xsl:otherwise>
                                                    <xsl:attribute name="href">/products-prof_<xsl:value-of
                                                            select="$lang"/>.html
                                                    </xsl:attribute>
                                                </xsl:otherwise>
                                            </xsl:choose>
                                            <xsl:call-template name="add-constant-info">
                                                <xsl:with-param name="name" select="'CORE.READ_MORE'"/>
                                            </xsl:call-template>
                                            <xsl:value-of select="java:getString($dict_common, 'CORE.READ_MORE')"/>
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>



					<!--slide 2-->
					<div class="header__slides">
						<div class="slide-2__wrapper header__slide ">
							<!--Second slider -->
							<div class="slide-2 steps__slider">
								<!--slide 1-->
								<div class="slide-2__item">
									<div class="slide-2__img">
										<img src="/media/dermopico.png"/>
									</div>
									<div class="slide-2__description">
										<xsl:apply-templates select="negeso:article[negeso:head='header__slides_kapper_article_2_1']"/>
									<!--	<span>1) Interview</span>
										<a class="slide-2__link" href="#">Wat zijn je haardoelen?</a>-->
									</div>
								</div>
								<!--slide 2-->
								<div class="slide-2__item">
									<div class="slide-2__img">
										<img src="/media/interview1.jpg"/>
									</div>
									<div class="slide-2__description">
									<!--	<span>2) Haarscan</span>
										<a class="slide-2__link" href="#">Wij meten je haar</a>-->
                                        <xsl:apply-templates
                                                select="negeso:article[negeso:head='header__slides_kapper_article_2_2']"/>
                                    </div>
                                </div>
                                <!--slide 3-->
                                <div class="slide-2__item">
                                    <div class="slide-2__img">
                                        <img src="/media/mixture.jpg"/>
                                    </div>
                                    <div class="slide-2__description">
                                        <!--<span>3) Speciale mix</span>
										<a class="slide-2__link" href="#">Speciaal voor jouw haar gemaakt</a>-->
                                        <xsl:apply-templates
                                                select="negeso:article[negeso:head='header__slides_kapper_article_2_3']"/>
                                    </div>
                                </div>
                                <!--slide 4-->
                                <div class="slide-2__item">
                                    <div class="slide-2__img">
                                        <img src="/media/slide2-4.jpg"/>
                                    </div>
                                    <div class="slide-2__description">
                                        <!--<span>4) Levering</span>
										<a class="slide-2__link" href="#">Binnen paar dagen bij je thuisbezorgd</a>-->
                                        <xsl:apply-templates
                                                select="negeso:article[negeso:head='header__slides_kapper_article_2_4']"/>
                                    </div>
                                </div>
                                <!--slide 5-->
                                <div class="slide-2__item">
                                    <div class="slide-2__img">
                                        <img src="/media/slide2-5.jpg"/>
                                    </div>
                                    <div class="slide-2__description">
                                        <!--<span>5) Haar wassen</span>
										<a class="slide-2__link" href="#">Gebruik je nieuwe producten en je haar verbetert</a>-->
                                        <xsl:apply-templates
                                                select="negeso:article[negeso:head='header__slides_kapper_article_2_5']"/>
                                    </div>
                                </div>
                            </div>

                            <a class="slide-2__button button-main button-main--black">
                                <xsl:choose>
                                    <xsl:when test="//negeso:page/@admin-path='true'">
                                        <xsl:attribute name="href">/admin/kapper-concept_<xsl:value-of select="$lang"/>.html
                                        </xsl:attribute>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:attribute name="href">/kapper-concept_<xsl:value-of select="$lang"/>.html
                                        </xsl:attribute>
                                    </xsl:otherwise>
                                </xsl:choose>
                                <xsl:call-template name="add-constant-info">
                                    <xsl:with-param name="name" select="'CORE.READ_MORE'"/>
                                </xsl:call-template>
                                <xsl:value-of select="java:getString($dict_common, 'CORE.READ_MORE')"/>
                            </a>

                        </div>

                    </div>
                    <!--slide 3-->
                    <div class="header__slides">
                        <div class="slide-4 header__slide">
                            <div class="slide-4-inner">
                                <div class="slide-4__description">
                                    <!--<h3 class="slide-4__title">Wordt i am partner</h3>-->
                                    <xsl:apply-templates
                                            select="negeso:article[negeso:head='header__slides_kapper_article_3']"/>
                                    <a class="slide-4__button button-main button-main--black">
                                        <xsl:choose>
                                            <xsl:when test="//negeso:page/@admin-path='true'">
                                                <xsl:attribute name="href">/admin/partner-worden_<xsl:value-of
                                                        select="$lang"/>.html
                                                </xsl:attribute>
                                            </xsl:when>
                                            <xsl:otherwise>
                                                <xsl:attribute name="href">/partner-worden_<xsl:value-of
                                                        select="$lang"/>.html
                                                </xsl:attribute>
                                            </xsl:otherwise>
                                        </xsl:choose>
                                        <xsl:call-template name="add-constant-info">
                                            <xsl:with-param name="name" select="'CORE.READ_MORE'"/>
                                        </xsl:call-template>
                                        <xsl:value-of select="java:getString($dict_common, 'CORE.READ_MORE')"/>
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>
                    <!--slide 4-->
                    <div class="header__slides">
                        <div class="slide-5 header__slide">
                            <div class="container">
                                <div class="slide-5-inner">
                                    <div class="slide-5__description">
                                        <!--<h1 class="slide-5__title">Onze ingrediënten</h1>-->
                                        <xsl:apply-templates
                                                select="negeso:article[negeso:head='header__slides_kapper_article_4']"/>
                                        <a class="slide-5__button button-main button-main--black">
                                            <xsl:choose>
                                                <xsl:when test="//negeso:page/@admin-path='true'">
                                                    <xsl:attribute name="href">/admin/ingredient-prof_<xsl:value-of
                                                            select="$lang"/>.html
                                                    </xsl:attribute>
                                                </xsl:when>
                                                <xsl:otherwise>
                                                    <xsl:attribute name="href">/ingredient-prof_<xsl:value-of
                                                            select="$lang"/>.html
                                                    </xsl:attribute>
                                                </xsl:otherwise>
                                            </xsl:choose>
                                            <xsl:call-template name="add-constant-info">
                                                <xsl:with-param name="name" select="'CORE.READ_MORE'"/>
                                            </xsl:call-template>
                                            <xsl:value-of select="java:getString($dict_common, 'CORE.READ_MORE')"/>
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="sliderHeader__arrows sliderHeader__next-arrow">
                    <img alt="next button" src="/media/right-arrow.svg"/>
                </div>
                <div class="sliderHeader__arrows sliderHeader__back-arrow">
                    <img alt="next button" src="/media/left-arrow.svg"/>
                </div>
                <button id="get_started_kapper" class="get_started " type="button">
                    <span class="scroll_icon"></span>
                </button>
            </div>
            <div class="wrapper">
                <div class="kapper-home-block">
                    <div class="title-block">
                        <xsl:apply-templates select="//negeso:article[negeso:head='kapper-home-block_1']"/>
                        <!--<div class="steps-block">
							<div class="steps__image step-1">
								<img src="/media/interview1.jpg" />
								<p class="steps">01<span>Interview</span></p>
							</div>
							<div class="steps__image step-2">
								<img src="/media/dermopico.png" />
								<p class="steps">02<span>Scan</span></p>
							</div>
							<div class="steps__image step-3">
								<img src="/media/slide2-4.jpg" />
								<p class="steps">03<span>Sell</span></p>
							</div>
						</div>-->
                        <a class="button-main button-main--black-border">
                            <xsl:choose>
                                <xsl:when test="//negeso:page/@admin-path='true'">
                                    <xsl:attribute name="href">/admin/kapper-concept_<xsl:value-of select="$lang"/>.html
                                    </xsl:attribute>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:attribute name="href">/kapper-concept_<xsl:value-of select="$lang"/>.html
                                    </xsl:attribute>
                                </xsl:otherwise>
                            </xsl:choose>
                            Lees meer
                            <span class="button-main-arrow">&#8594;</span>
                        </a>
                    </div>
                </div>
            </div>
            <div class="kapper-home-block kapper-home-block--bg-black ">
                <h2 class="big-title">iam</h2>
                <div class="wrapper">
                    <div class="title-block">
                        <xsl:apply-templates select="//negeso:article[negeso:head='kapper-home-block_2']"/>
                        <a class="button-main button-main--white button-main--margin-top">
                            <xsl:choose>
                                <xsl:when test="//negeso:page/@admin-path='true'">
                                    <xsl:attribute name="href">/admin/smart-hair-analyser_<xsl:value-of select="$lang"/>.html
                                    </xsl:attribute>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:attribute name="href">/smart-hair-analyser_<xsl:value-of select="$lang"/>.html
                                    </xsl:attribute>
                                </xsl:otherwise>
                            </xsl:choose>
                            Lees meer
                            <span class="button-main-arrow">&#8594;</span>
                        </a>
                    </div>
                </div>
            </div>
            <div class="kapper-home-block">

                <div class="wrapper">
                    <div class="title-block">
                        <xsl:apply-templates select="//negeso:article[negeso:head='kapper-home-block_6']"/>
                        <img class="kapper-bloc-img4" src="/media/kapper-home/img_4.jpg"/>
                        <a class="button-main button-main--black-border button-main--margin-top">
                            <xsl:choose>
                                <xsl:when test="//negeso:page/@admin-path='true'">
                                    <xsl:attribute name="href">/admin/products-prof_<xsl:value-of select="$lang"/>.html
                                    </xsl:attribute>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:attribute name="href">/products-prof_<xsl:value-of select="$lang"/>.html
                                    </xsl:attribute>
                                </xsl:otherwise>
                            </xsl:choose>
                            Lees meer
                            <span class="button-main-arrow">&#8594;</span>
                        </a>
                    </div>
                </div>
            </div>
            <div class="kapper-home-block kapper-home-block--bg-cream aos" data-aos="fade-right"
                 data-aos-duration="3000" data-aos-offset="300">
                <div class="wrapper wrapper--overflow ">
                    <img class="kapper-block-img5" src="/media/kapper-home/img_5.png"/>
                    <div class="title-block title-block--position">
                        <xsl:apply-templates select="//negeso:article[negeso:head='kapper-home-block_3']"/>
                        <a class="button-main button-main--black-border button-main--margin-top">
                            <xsl:choose>
                                <xsl:when test="//negeso:page/@admin-path='true'">
                                    <xsl:attribute name="href">/admin/partner-worden_<xsl:value-of select="$lang"/>.html
                                    </xsl:attribute>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:attribute name="href">/partner-worden_<xsl:value-of select="$lang"/>.html
                                    </xsl:attribute>
                                </xsl:otherwise>
                            </xsl:choose>
                            Lees meer
                            <span class="button-main-arrow">&#8594;</span>
                        </a>
                    </div>
                </div>
            </div>
            <!--	<div class="kapper-home-block kapper-home-block&#45;&#45;bg-gradient">
				<div class="wrapper">
					<div class="title-block">
					<xsl:apply-templates select="//negeso:article[negeso:head='kapper-home-block_4']"/>
						<img class="kapper-block-img7" src="/media/kapper-home/img_7.png"/>
						<a href="/enviroment-prof_{$lang}.html" class="button-main button-main&#45;&#45;white button-main&#45;&#45;margin-top">Lees meer <span class="button-main-arrow">&#8594;</span></a>
					</div>
				</div>
			</div>-->
            <section class="better-world">
                <div class="container">
                    <div class="better-world__wrapper">
                        <div class="better-world__description">
                            <xsl:apply-templates select="negeso:article[negeso:head='kapper-home-block_4']"/>
                            <a class="better-world__button button-main button-main--white">
                                <xsl:choose>
                                    <xsl:when test="//negeso:page/@admin-path='true'">
                                        <xsl:attribute name="href">/admin/environment-prof_<xsl:value-of
                                                select="$lang"/>.html
                                        </xsl:attribute>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:attribute name="href">/environment-prof_<xsl:value-of select="$lang"/>.html
                                        </xsl:attribute>
                                    </xsl:otherwise>
                                </xsl:choose>
                                <xsl:call-template name="add-constant-info">
                                    <xsl:with-param name="name" select="'CORE.READ_MORE'"/>
                                </xsl:call-template>
                                <xsl:value-of select="java:getString($dict_common, 'CORE.READ_MORE')"/>
                                <svg aria-hidden="true" focusable="false" data-prefix="fas" data-icon="book-reader"
                                     class="button__icon" role="img" xmlns="http://www.w3.org/2000/svg"
                                     viewBox="0 0 512 512">
                                    <path fill="currentColor"
                                          d="M352 96c0-53.02-42.98-96-96-96s-96 42.98-96 96 42.98 96 96 96 96-42.98 96-96zM233.59 241.1c-59.33-36.32-155.43-46.3-203.79-49.05C13.55 191.13 0 203.51 0 219.14v222.8c0 14.33 11.59 26.28 26.49 27.05 43.66 2.29 131.99 10.68 193.04 41.43 9.37 4.72 20.48-1.71 20.48-11.87V252.56c-.01-4.67-2.32-8.95-6.42-11.46zm248.61-49.05c-48.35 2.74-144.46 12.73-203.78 49.05-4.1 2.51-6.41 6.96-6.41 11.63v245.79c0 10.19 11.14 16.63 20.54 11.9 61.04-30.72 149.32-39.11 192.97-41.4 14.9-.78 26.49-12.73 26.49-27.06V219.14c-.01-15.63-13.56-28.01-29.81-27.09z"></path>
                                </svg>
                            </a>
                        </div>
                    </div>
                </div>
            </section>
            <section class="buy-product">
                <div class="container">
                    <div class="buy-product__wrapper">
                        <div class="buy-product__images">
                            <img class="buy-product__photo aos" data-aos="fade-right" data-aos-duration="3000"
                                 data-aos-offset="300" src="/media/fingerprintGarantie.png" alt="haircut"/>
                        </div>
                        <div class="buy-product__description">
                            <xsl:apply-templates select="negeso:article[negeso:head='kapper-home-block_5']"/>
                            <a class="buy-product__button button-main button-main--black button-main--margin-top">
                                <xsl:choose>
                                    <xsl:when test="//negeso:page/@admin-path='true'">
                                        <xsl:attribute name="href">/admin/tevredenheidsgarantie-prof_<xsl:value-of
                                                select="$lang"/>.html
                                        </xsl:attribute>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:attribute name="href">/tevredenheidsgarantie-prof_<xsl:value-of
                                                select="$lang"/>.html
                                        </xsl:attribute>
                                    </xsl:otherwise>
                                </xsl:choose>
                                <xsl:call-template name="add-constant-info">
                                    <xsl:with-param name="name" select="'CORE.READ_MORE'"/>
                                </xsl:call-template>
                                <xsl:value-of select="java:getString($dict_common, 'CORE.READ_MORE')"/>
                                <svg aria-hidden="true" focusable="false" data-prefix="fas" data-icon="book-reader"
                                     class="button__icon" role="img" xmlns="http://www.w3.org/2000/svg"
                                     viewBox="0 0 512 512">
                                    <path fill="currentColor"
                                          d="M352 96c0-53.02-42.98-96-96-96s-96 42.98-96 96 42.98 96 96 96 96-42.98 96-96zM233.59 241.1c-59.33-36.32-155.43-46.3-203.79-49.05C13.55 191.13 0 203.51 0 219.14v222.8c0 14.33 11.59 26.28 26.49 27.05 43.66 2.29 131.99 10.68 193.04 41.43 9.37 4.72 20.48-1.71 20.48-11.87V252.56c-.01-4.67-2.32-8.95-6.42-11.46zm248.61-49.05c-48.35 2.74-144.46 12.73-203.78 49.05-4.1 2.51-6.41 6.96-6.41 11.63v245.79c0 10.19 11.14 16.63 20.54 11.9 61.04-30.72 149.32-39.11 192.97-41.4 14.9-.78 26.49-12.73 26.49-27.06V219.14c-.01-15.63-13.56-28.01-29.81-27.09z"></path>
                                </svg>
                            </a>
                        </div>
                    </div>
                </div>
            </section>
            <!--<div class="kapper-home-block kapper-home-block&#45;&#45;bg-gray">
				<div class="wrapper">
					<div class="title-block title-block&#45;&#45;position40">
						<xsl:apply-templates select="//negeso:article[negeso:head='c']"/>
						<a href="/tevredenheidsgarantie-prof_{$lang}.html" class="button-main button-main&#45;&#45;black-border button-main&#45;&#45;margin-top">Lees meer <span class="button-main-arrow">&#8594;</span></a>
					</div>
				</div>
			</div>-->


        </section>


        <!--		<div class="kapper-home">-->
        <!--			<div class="wrapper">-->
        <!--				<xsl:apply-templates select="//negeso:article[negeso:head='kapper-home_head']"/>-->
        <!--				<div class="kapper-home_block">-->
        <!--					<img class="kpr-home_img" src="/media/kapper-home/img_1.jpg"/>-->
        <!--					<div class="kpr_block-item"></div>-->
        <!--				</div>-->
        <!--				<xsl:call-template name="env-block">-->
        <!--					<xsl:with-param name="type" select="'left'"/>-->
        <!--					<xsl:with-param name="pic" select="'/media/kapper-home/img_2.jpg'"/>-->
        <!--					<xsl:with-param name="background" select="'#F8F7F2'"/>-->
        <!--					<xsl:with-param name="article" select="'kapper-home_block2'"/>-->
        <!--				</xsl:call-template>-->
        <!--				<div class="text-wrapper">-->
        <!--					<xsl:apply-templates select="//negeso:article[negeso:head='kapper_home-text']"/>-->
        <!--				</div>-->
        <!--			</div>-->
        <!--		</div>-->
    </xsl:template>




    <xsl:template name="partner-worden">
        <div class="partner-worden">
            <div class="partner__content">
                <!--				<div class="header__slider-wrapper">-->
                <!--					<div class="header__slider">-->
                <!--						&lt;!&ndash;slide 1&ndash;&gt;-->
                <!--						<div class="header__image slide">-->
                <!--							<img class="slider__image" src="/media/partner-worden_1.jpg" alt="slide 1"/>-->
                <!--								<div class="partner-worden__description">-->
                <!--									<h1 class="partner-worden__title">Registratieformulier</h1>-->
                <!--								</div>-->
                <!--						</div>-->
                <!--						&lt;!&ndash;slide 2&ndash;&gt;-->
                <!--						<div class="header__image slide">-->
                <!--							<img class="slider__image" src="/media/partner-worden_2.jpg" alt="slide 2"/>-->
                <!--							<div class="partner-worden__description">-->
                <!--								<h1 class="partner-worden__title">Registratieformulier</h1>-->
                <!--							</div>-->
                <!--						</div>-->
                <!--						&lt;!&ndash;slide 3&ndash;&gt;-->
                <!--						<div class="header__image slide">-->
                <!--							<img class="slider__image" src="/media/partner-worden_3.jpg" alt="slide 3"/>-->
                <!--							<div class="partner-worden__description">-->
                <!--								<h1 class="partner-worden__title">Registratieformulier</h1>-->
                <!--							</div>-->
                <!--						</div>-->
                <!--						&lt;!&ndash;slide 4&ndash;&gt;-->
                <!--						<div class="header__image slide">-->
                <!--							<img class="slider__image" src="/media/partner-worden_4.jpg" alt="slide 4"/>-->
                <!--							<div class="partner-worden__description">-->
                <!--								<h1 class="partner-worden__title">Registratieformulier</h1>-->
                <!--							</div>-->
                <!--						</div>-->
                <!--					</div>-->
                <!--					&lt;!&ndash;slider arrows&ndash;&gt;-->
                <!--				</div>-->

                <!--<div class="partner__slider-wrapper">
                    &lt;!&ndash;slide 1&ndash;&gt;
                    <div class="partner__slides">
                        <div class="partner__slide-1 header__slide">
                            <div class="partner-worden__description">
                                <h1 class="partner-worden__title">Registratieformulier</h1>
                            </div>
                        </div>
                    </div>
                    &lt;!&ndash;slide 2&ndash;&gt;
                    <div class="partner__slides">
                        <div class="partner__slide-2 header__slide">
                            <div class="partner-worden__description">
                                <h1 class="partner-worden__title">Registratieformulier</h1>
                            </div>
                        </div>
                    </div>
                    &lt;!&ndash;slide 3&ndash;&gt;
                    <div class="partner__slides">
                        <div class="partner__slide-3 header__slide">
                            <div class="partner-worden__description">
                                <h1 class="partner-worden__title">Registratieformulier</h1>
                            </div>
                        </div>
                    </div>
                    &lt;!&ndash;slide 4&ndash;&gt;
                    <div class="partner__slides">
                        <div class="partner__slide-4 header__slide">
                            <div class="partner-worden__description">
                                <h1 class="partner-worden__title">Registratieformulier</h1>
                            </div>
                        </div>
                    </div>
                </div>-->
                <!--				<div class="sliderHeader__arrows sliderHeader__next-arrow">-->
                <!--					<img alt="next button" src="/media/right-arrow.svg"/>-->
                <!--				</div>-->
                <!--				<div class="sliderHeader__arrows sliderHeader__back-arrow">-->
                <!--					<img alt="next button" src="/media/left-arrow.svg"/>-->
                <!--				</div>-->
            </div>

            <!--			<div class="partner-worden__header">-->
            <!--				<div class="container">-->
            <!--					<div class="partner-worden__description">-->
            <!--						<h1 class="partner-worden__title">Registratieformulier</h1>-->
            <!--					</div>-->
            <!--				</div>-->
            <!--			</div>-->


            <!--<div class="partner-worden__form-wrapper">
                <div class="container">
                    <div class="partner-worden__registration">
                        &lt;!&ndash;							<xsl:apply-templates select="negeso:article[negeso:head='']"/>&ndash;&gt;
                        <h1 class="registration__title"><xsl:value-of select="java:getString($dict_contact_module, 'CB_PERSONAL_DATA')"/></h1>
                        <form class="contact" id="partnerForm">
                            <label class="registration__label"><xsl:value-of select="java:getString($dict_pm_module, 'PM_FIRST_NAME')"/>
                                <input name="name" class="registration__input" type="text" required="true"/>
                            </label>
                            <label class="registration__label"><xsl:value-of select="java:getString($dict_pm_module, 'PM_LAST_NAME')"/>
                                <input name="last-name" class="registration__input" type="text" required="true"/>
                            </label>
                            <label class="registration__label"><xsl:value-of select="java:getString($dict_pm_module, 'PM_PHONE')"/>
                                <input name="phone" class="registration__input" type="tel" required="true"/>
                                <span class="input-error" id="phone-error"></span>
                            </label>
                            <label class="registration__label">Email
                                <input name="email" class="registration__input" type="email" required="true"/>
                            </label>
                            <label class="registration__label"><xsl:value-of select="java:getString($dict_contact_module, 'CB_BIRTHDAY')"/>
                                <input name="birthday" class="registration__input" type="date" />
                            </label>
                            <label class="registration__label"><xsl:value-of select="java:getString($dict_contact_module, 'CB_B_PHONE')"/>
                                <input name="business-phone" class="registration__input" type="tel" required="true"/>
                                <span class="input-error" id="b_phone-error"></span>
                            </label>
                            <label class="registration__label"><xsl:value-of select="java:getString($dict_contact_module, 'CB_KVK')"/>
                                <input name="kvk-number" class="registration__input" type="text" required="true"/>
                            </label>
                            <label class="registration__label"><xsl:value-of select="java:getString($dict_contact_module, 'CB_BTW')"/>
                                <input name="btw-number" class="registration__input" type="text" required="true"/>
                            </label>
                            <label class="registration__label">Website
                                <input name="website" class="registration__input" type="text"/>
                            </label>
                            <label class="registration__label"><xsl:value-of select="java:getString($dict_contact_module, 'CB_COMMENTS')"/>
                                <textarea name="comments" class="registration__textarea" rows="7"></textarea>
                            </label>
                            <input class="registration__button button-main button-main&#45;&#45;black" type="submit">
                                <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_contact_module, 'CB_SEND')"/></xsl:attribute>
                            </input>
                            <span id="alert"></span>
                        </form>
                    </div>
                </div>
            </div>-->



            <div class="partner-worden__form-wrapper">
                <div class="container">
                    <div class="partner-worden__registration">
                        <!--							<xsl:apply-templates select="negeso:article[negeso:head='']"/>-->
						<h1 class="registration__title">Registratieformulier</h1>
                        <form class="contact" id="partnerForm">
							<label class="registration__label"><xsl:value-of select="java:getString($dict_pm_module, 'PM_FIRST_NAME')"/>
								<input name="name" class="registration__input" type="text" required="true"/>
							</label>
							<label class="registration__label"><xsl:value-of select="java:getString($dict_pm_module, 'PM_LAST_NAME')"/>
								<input name="last-name" class="registration__input" type="text" required="true"/>
							</label>
							<label class="registration__label"><xsl:value-of select="java:getString($dict_pm_module, 'PM_PHONE')"/>
								<input name="phone" class="registration__input" type="tel" required="true"/>
                                <span class="input-error" id="phone-error"></span>
							</label>
							<label class="registration__label">Email
								<input name="email" class="registration__input" type="email" required="true"/>
							</label>
                            <label class="registration__label">Soort sport
                                <input name="type_sport" class="registration__input" type="text" required="true"/>
                            </label>
                            <label class="registration__label">Het adres
                                <input name="address" class="registration__input" type="text" required="true"/>
                            </label>
                            <label class="registration__label">Bezorgadres
                                <input name="delivery_address" class="registration__input" type="text" required="true"/>
                            </label>
                            <label class="registration__label">Wachtwoord
                                <input name="password" class="registration__input" type="text" required="true"/>
                            </label>
                            <label class="registration__label">Bevestig wachtwoord
                                <input name="confirm_password" class="registration__input" type="text" required="true"/>
                                <span class="input-error" id="password-error"></span>
                            </label>

							<label class="registration__label">Website
								<input name="website" class="registration__input" type="text"/>
							</label>
                            <input class="registration__button button-main button-main--black" type="submit">
                                <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_contact_module, 'CB_SEND')"/></xsl:attribute>
                            </input>
                            <span id="alert"></span>
						</form>
                    </div>
                </div>
            </div>

        </div>
    </xsl:template>







    <xsl:template name="contact-page">
        <div class="contact-page">
            <div class="maps" id="map"></div>
            <div class="contact-page__block">

                <div class="contact-page_address">
                    <xsl:apply-templates select="//negeso:article[negeso:head='contact-page_address']"/>
                </div>
                <div class="contact-page_descr">
                    <xsl:apply-templates select="//negeso:article[negeso:head='contact-page_descr']"/>
                </div>
            </div>
        </div>
    </xsl:template>

    <xsl:template name="smart-hair-analyser">
        <xsl:apply-templates select="//negeso:article[negeso:head='smart-hair-analyser']"/>
    </xsl:template>

    <xsl:template name="products-prof">
        <xsl:apply-templates select="//negeso:article[negeso:head='products-prof']"/>
    </xsl:template>

    <xsl:template name="environment">
        <xsl:apply-templates select="//negeso:article[negeso:head='environment']"/>
    </xsl:template>
    <xsl:template name="faq-prof">
        <xsl:apply-templates select="//negeso:article[negeso:head='faq-prof']"/>
    </xsl:template>

    <xsl:template name="garantie-prof">
        <xsl:apply-templates select="//negeso:article[negeso:head='garantie-prof']"/>
    </xsl:template>

    <xsl:template name="contacts-prof">
        <xsl:call-template name="contact-page"/>
    </xsl:template>

    <xsl:template name="ingred-prof">
        <xsl:call-template name="ingredient-page"/>
    </xsl:template>

	<xsl:template name="scan_user_results">
	     <h1 style="text-align:center; font-size:22px; padding: 20px; font-weight:bold;">
		<xsl:call-template name ="add-constant-info">
			<xsl:with-param name="name" select="'CORE.SCAN_RESULTS'" />
		</xsl:call-template>
		<xsl:value-of select="java:getString($dict_common, 'CORE.SCAN_RESULTS')"/>
		</h1>

		<div class="scan-results-wrapper"></div>
		<script>
			getUserScanResults();
		</script>
	</xsl:template>

	<xsl:template name="prof_menu">
		<ul id="negeso_main_menu_prof" class="negeso_menu">
			<li><a  class="kapper-hp">
				<xsl:choose>
					<xsl:when test="//negeso:page/@admin-path='true'">
						<xsl:attribute name="href">/admin/kapper-home_<xsl:value-of select="$lang"/>.html</xsl:attribute>
					</xsl:when>
					<xsl:otherwise><xsl:attribute name="href">/kapper-home_<xsl:value-of select="$lang"/>.html</xsl:attribute></xsl:otherwise>
				</xsl:choose>

                    <xsl:call-template name="add-constant-info">
                        <xsl:with-param name="name" select="'CORE.KAPPER_HOME'"/>
                    </xsl:call-template>
                    <xsl:value-of select="java:getString($dict_common, 'CORE.KAPPER_HOME')"/>
                </a>
            </li>
            <li>
                <a class="kapper-concept">
                    <xsl:choose>
                        <xsl:when test="//negeso:page/@admin-path='true'">
                            <xsl:attribute name="href">/admin/kapper-concept_<xsl:value-of select="$lang"/>.html
                            </xsl:attribute>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:attribute name="href">/kapper-concept_<xsl:value-of select="$lang"/>.html
                            </xsl:attribute>
                        </xsl:otherwise>
                    </xsl:choose>

                    <xsl:call-template name="add-constant-info">
                        <xsl:with-param name="name" select="'CORE.KAPPER_CONCEPT'"/>
                    </xsl:call-template>
                    <xsl:value-of select="java:getString($dict_common, 'CORE.KAPPER_CONCEPT')"/>
                </a>
            </li>
            <li>
                <a class="hair-analyser">
                    <xsl:choose>
                        <xsl:when test="//negeso:page/@admin-path='true'">
                            <xsl:attribute name="href">/admin/smart-hair-analyser_<xsl:value-of select="$lang"/>.html
                            </xsl:attribute>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:attribute name="href">/smart-hair-analyser_<xsl:value-of select="$lang"/>.html
                            </xsl:attribute>
                        </xsl:otherwise>
                    </xsl:choose>

                    <xsl:call-template name="add-constant-info">
                        <xsl:with-param name="name" select="'CORE.KAPPER_SM_ANALYSER'"/>
                    </xsl:call-template>
                    <xsl:value-of select="java:getString($dict_common, 'CORE.KAPPER_SM_ANALYSER')"/>
                </a>
            </li>
            <li>
                <a class="partn-word">
                    <xsl:choose>
                        <xsl:when test="//negeso:page/@admin-path='true'">
                            <xsl:attribute name="href">/admin/partner-worden_<xsl:value-of select="$lang"/>.html
                            </xsl:attribute>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:attribute name="href">/partner-worden_<xsl:value-of select="$lang"/>.html
                            </xsl:attribute>
                        </xsl:otherwise>
                    </xsl:choose>

                    <xsl:call-template name="add-constant-info">
                        <xsl:with-param name="name" select="'CORE.KAPPER_PARTNER'"/>
                    </xsl:call-template>
                    <xsl:value-of select="java:getString($dict_common, 'CORE.KAPPER_PARTNER')"/>
                </a>
            </li>
            <li>
                <a class="prod-prof">
                    <xsl:choose>
                        <xsl:when test="//negeso:page/@admin-path='true'">
                            <xsl:attribute name="href">/admin/products-prof_<xsl:value-of select="$lang"/>.html
                            </xsl:attribute>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:attribute name="href">/products-prof_<xsl:value-of select="$lang"/>.html
                            </xsl:attribute>
                        </xsl:otherwise>
                    </xsl:choose>

                    <xsl:call-template name="add-constant-info">
                        <xsl:with-param name="name" select="'CORE.KAPPER_PRODUCTS'"/>
                    </xsl:call-template>
                    <xsl:value-of select="java:getString($dict_common, 'CORE.KAPPER_PRODUCTS')"/>
                </a>
            </li>
            <li>
                <a class="ingred-prof">
                    <xsl:choose>
                        <xsl:when test="//negeso:page/@admin-path='true'">
                            <xsl:attribute name="href">/admin/ingredient-prof_<xsl:value-of select="$lang"/>.html
                            </xsl:attribute>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:attribute name="href">/ingredient-prof_<xsl:value-of select="$lang"/>.html
                            </xsl:attribute>
                        </xsl:otherwise>
                    </xsl:choose>

                    <xsl:call-template name="add-constant-info">
                        <xsl:with-param name="name" select="'CORE.KAPPER_INGRED'"/>
                    </xsl:call-template>
                    <xsl:value-of select="java:getString($dict_common, 'CORE.KAPPER_INGRED')"/>
                </a>
            </li>
            <li>
                <a class="envir-prof">
                    <xsl:choose>
                        <xsl:when test="//negeso:page/@admin-path='true'">
                            <xsl:attribute name="href">/admin/environment-prof_<xsl:value-of select="$lang"/>.html
                            </xsl:attribute>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:attribute name="href">/environment-prof_<xsl:value-of select="$lang"/>.html
                            </xsl:attribute>
                        </xsl:otherwise>
                    </xsl:choose>

                    <xsl:call-template name="add-constant-info">
                        <xsl:with-param name="name" select="'CORE.KAPPER_ENVIRONMENT'"/>
                    </xsl:call-template>
                    <xsl:value-of select="java:getString($dict_common, 'CORE.KAPPER_ENVIRONMENT')"/>
                </a>
            </li>
            <li>
                <a class="garant-prof">
                    <xsl:choose>
                        <xsl:when test="//negeso:page/@admin-path='true'">
                            <xsl:attribute name="href">/admin/tevredenheidsgarantie-prof_<xsl:value-of select="$lang"/>.html
                            </xsl:attribute>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:attribute name="href">/tevredenheidsgarantie-prof_<xsl:value-of select="$lang"/>.html
                            </xsl:attribute>
                        </xsl:otherwise>
                    </xsl:choose>

                    <xsl:call-template name="add-constant-info">
                        <xsl:with-param name="name" select="'CORE.KAPPER_GUARANTEE'"/>
                    </xsl:call-template>
                    <xsl:value-of select="java:getString($dict_common, 'CORE.KAPPER_GUARANTEE')"/>
                </a>
            </li>
            <li>
                <a class="faq-prof">
                    <xsl:choose>
                        <xsl:when test="//negeso:page/@admin-path='true'">
                            <xsl:attribute name="href">/admin/faq-prof_<xsl:value-of select="$lang"/>.html
                            </xsl:attribute>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:attribute name="href">/faq-prof_<xsl:value-of select="$lang"/>.html
                            </xsl:attribute>
                        </xsl:otherwise>
                    </xsl:choose>

                    <xsl:call-template name="add-constant-info">
                        <xsl:with-param name="name" select="'CORE.KAPPER_FAQ'"/>
                    </xsl:call-template>
                    <xsl:value-of select="java:getString($dict_common, 'CORE.KAPPER_FAQ')"/>
                </a>
            </li>
            <li>
                <a href="/contacts_prof_{$lang}.html" class="contacts-prof">
                    <xsl:choose>
                        <xsl:when test="//negeso:page/@admin-path='true'">
                            <xsl:attribute name="href">/admin/contacts_prof_<xsl:value-of select="$lang"/>.html
                            </xsl:attribute>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:attribute name="href">/contacts_prof_<xsl:value-of select="$lang"/>.html
                            </xsl:attribute>
                        </xsl:otherwise>
                    </xsl:choose>

                    <xsl:call-template name="add-constant-info">
                        <xsl:with-param name="name" select="'CORE.KAPPER_CONTACTS'"/>
                    </xsl:call-template>
                    <xsl:value-of select="java:getString($dict_common, 'CORE.KAPPER_CONTACTS')"/>
                </a>
            </li>
        </ul>

        <script>
            $('#negeso_main_menu').empty();
            $('#negeso_main_menu_prof').appendTo( $( "#negeso_main_menu" ) );
            $('.menu-type p').removeClass('active');
            $('.menu_prof_btn').addClass('active');
        </script>
    </xsl:template>

    <xsl:template match="negeso:language" mode="oneLanguage">
        <a href="#" langCode="{@code}">

            <!--<xsl:variable name="interface-lang" select="/negeso:page/@interface-language"/>-->

            <xsl:if test="@selected">
                <xsl:attribute name="class">selectedLang</xsl:attribute>
            </xsl:if>
            <xsl:value-of select="@name"/>
        </a>
    </xsl:template>

    <xsl:template name="user_page">
        <form method="POST" name="user_form" id="user_form_js" class="user_form">
            <h2 class="title">Gebruikerspagina</h2>
            <p>Gebruiker email:</p>
            <p>
                <input type="email" id="user_form_email">
                    <xsl:attribute name="value">
                        <xsl:value-of select="//negeso:customer/@email"/>
                    </xsl:attribute>
                </input>
            </p>
            <div class="type_delivery">
                <h3 class="title">Prijstypen</h3>
                <ul class="type_payment_list">
                    <li class="payment_item">
                        <input value="Inkoop" name="display_price" id="inkoop" type="radio">
                            <xsl:if test="//negeso:customer/@displayPrice = 'Inkoop'">
                                <xsl:attribute name="checked">
                                    <xsl:value-of select="checked"/>
                                </xsl:attribute>
                            </xsl:if>
                        </input>
                        <label for="inkoop">Inkoop</label>
                        <div class="check"/>
                    </li>
                    <li class="payment_item">
                        <input value="Verkoop" name="display_price" id="verkoop" type="radio">
                            <xsl:if test="//negeso:customer/@displayPrice = 'Verkoop'">
                                <xsl:attribute name="checked">
                                    <xsl:value-of select="checked"/>
                                </xsl:attribute>
                            </xsl:if>
                        </input>
                        <label for="verkoop">Verkoop</label>
                        <div class="check"/>
                    </li>
                    <li class="payment_item ">
                        <input value="Inkoop_Verkoop" name="display_price" id="inkoop_verkoop" type="radio">
                            <xsl:if test="//negeso:customer/@displayPrice = 'Inkoop_Verkoop'">
                                <xsl:attribute name="checked">
                                    <xsl:value-of select="checked"/>
                                </xsl:attribute>
                            </xsl:if>
                        </input>
                        <label for="inkoop_verkoop">Inkoop &#38; Verkoop</label>
                        <div class="check">
                            <div class="inside"/>
                        </div>
                    </li>
                </ul>
            </div>
            <div class="form_button_wrapper">
                <button class="form_button" type="submit">
                    OPSLAAN
                </button>
            </div>
        </form>
    </xsl:template>

    <xsl:template name="paging">
        <!-- current tag: negeso:contents -->
        <!-- 'paging_style' can be 'old' or 'new' -->
        <xsl:param name="paging_style" select="'old'"/>
        <xsl:choose>
            <!-- Newsline paging -->
            <xsl:when test="$class='newsline'">
                <xsl:if test="not(negeso:listItem/negeso:details)">
                    <xsl:apply-templates select="negeso:list[count(@type) = 0]" mode="paging">
                        <xsl:with-param name="paging_style" select="$paging_style"/>
                    </xsl:apply-templates>
                </xsl:if>
            </xsl:when>
            <!-- Photo Album paging -->
            <xsl:when test="descendant::negeso:album/negeso:photo[@selected]">
                <xsl:apply-templates select="descendant::negeso:album[negeso:photo[@selected]]" mode="paging">
                    <xsl:with-param name="paging_style" select="$paging_style"/>
                </xsl:apply-templates>
            </xsl:when>
            <!-- Search results paging -->
            <xsl:when test="$class='search'">
                <xsl:apply-templates select="negeso:search/negeso:search_result/negeso:search_pages" mode="paging">
                    <xsl:with-param name="paging_style" select="$paging_style"/>
                </xsl:apply-templates>
            </xsl:when>
            <!-- Guestbook records paging -->
            <xsl:when test="$class='guestbook'">
                <xsl:apply-templates select="negeso:guestbook/negeso:PageNavigator" mode="paging">
                    <xsl:with-param name="paging_style" select="$paging_style"/>
                </xsl:apply-templates>
            </xsl:when>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="registration">

        <div class="form-wrapper">
            <img class="logo-img" src="/media/fingerprint-logo.png"/>
            <h1 class="page-title">
                <xsl:value-of select="//negeso:title"/>
            </h1>
            <form id="register-form">
                <div class="input-wrapper">
                    <label class="form-label" for="reg-login">Email</label>
                    <img src="/media/message.png"/>
                    <input id="reg-login" name="login" class="form-input" type="email" required="true"></input>
                    <span class="error-input reg-alrt"></span>
                </div>
                <div class="input-wrapper">
                    <label class="form-label" for="reg-password">
                        <xsl:value-of select="java:getString($dict_common, 'CORE.PASS')"/>
                    </label>
                    <img src="/media/password.png"/>
                    <span class="btn-show-pass">
                        <i class="zmdi zmdi-eye"></i>
                    </span>
                    <input id="reg-password" name="p" class="form-input" type="password"></input>

                    <span class="error-input pass"></span>
                </div>
                <div class="input-wrapper">
                    <label class="form-label" for="reg-repassword">
                        <xsl:value-of select="java:getString($dict_pm_module, 'PM_RETYPE_PASSWORD')"/>
                    </label>
                    <img src="/media/password.png"/>
                    <span class="btn-show-pass">
                        <i class="zmdi zmdi-eye"></i>
                    </span>
                    <input id="reg-repassword" name="pret" class="form-input" type="password"></input>

                    <span class="error-input pret"></span>
                </div>
                <div class="container-login100-form-btn m-t-30 w-full">
                    <div class="wrap-login100-form-btn">
                        <div class="login100-form-bgbtn"></div>
                        <button type="submit" class="login100-form-btn">
                            <xsl:value-of select="java:getString($dict_pm_module, 'PM_REGISTER')"/>
                        </button>
                    </div>
                </div>
            </form>
        </div>
    </xsl:template>

    <xsl:template name="addition-info">
        <div class="container m-b-70">
            <img class="logo-img" src="/media/fingerprint-logo.png"/>
            <h1 class="page-title">
                <xsl:value-of select="//negeso:title"/>
            </h1>
            <form id="edit-info" class="flex-center m-t-40 pos-relative">
                <div class="spec-options">
                    <div>
                        <span class="main-col spec-item">Account type:</span>
                        <div>
                            <input class="main-col m-l-15" id="personal" name="type" type="radio" checked="true"
                                   value="c"/>
                            <label class="main-col" for="personal">Personal</label>

                            <input class="main-col m-l-15" id="business" name="type" type="radio" value="b"/>
                            <label class="main-col" for="business">Business</label>

                        </div>


                    </div>

                    <div>
                        <span class="main-col spec-item">
                            <xsl:value-of select="java:getString($dict_pm_module, 'PM_BILLING_EQUAL_SHIPPING')"/>
                        </span>
                        <div>
                            <input class="main-col m-l-15" id="no-different" name="different-delivery" type="radio"
                                   checked="true" value='no'/>
                            <label class="main-col" for="no-different">No</label>

                            <input class="main-col m-l-15" id="yes-different" name="different-delivery" type="radio"
                                   value="yes"/>
                            <label class="main-col" for="yes-differen">Yes</label>

                        </div>

                    </div>
                </div>
                <div class="info-item m-t-40">
                    <h2 class="main-col text-center fs-22">
                        <xsl:value-of select="java:getString($dict_pm_module, 'PM_BILLING_ADDRESS')"/>
                    </h2>
                    <div class="input-wrapper company-name hidden">
                        <label class="form-label" for="company_name">
                            <xsl:value-of select="java:getString($dict_pm_module, 'PM_COMPANY_NAME')"/>*
                        </label>
                        <input id="company_name" name="company_name" class="form-input" type="text"></input>
                    </div>

                    <div class="input-wrapper">
                        <label class="form-label" for="first_name">
                            <xsl:value-of select="java:getString($dict_pm_module, 'PM_FIRST_NAME')"/>
                        </label>
                        <input id="first_name" name="first_name" class="form-input" type="text"></input>
                    </div>

                    <div class="input-wrapper">
                        <label class="form-label" for="last_name">
                            <xsl:value-of select="java:getString($dict_pm_module, 'PM_LAST_NAME')"/>
                        </label>
                        <input id="last_name" name="last_name" class="form-input" type="text"></input>
                    </div>

                    <div class="input-wrapper">
                        <label class="form-label" for="coutr-list">
                            <xsl:value-of select="java:getString($dict_pm_module, 'PM_COUNTRY')"/>
                        </label>
                        <select style="border:none; outline:none" id="coutr-list" name="country" class="form-input"
                                type="text">
                            <option>Choose</option>
                        </select>
                    </div>
                    <div class="input-wrapper">
                        <label class="form-label" for="town">
                            <xsl:value-of select="java:getString($dict_pm_module, 'PM_CITY')"/>
                        </label>
                        <input id="town" name="town" class="form-input" type="text"></input>
                    </div>
                    <div class="input-wrapper">
                        <label class="form-label" for="street">
                            <xsl:value-of select="java:getString($dict_pm_module, 'PM_ADDRESS')"/>
                        </label>
                        <input id="street" name="street" class="form-input" type="text"></input>
                    </div>
                    <div class="input-wrapper">
                        <label class="form-label" for="house">
                            House
                        </label>
                        <input id="house" name="house" class="form-input" type="text"></input>
                    </div>
                    <div class="input-wrapper">
                        <label class="form-label" for="postcode">
                            <xsl:value-of select="java:getString($dict_pm_module, 'PM_ZIP_POSTCODE')"/>
                        </label>
                        <input id="postcode" name="postcode" class="form-input" type="text"></input>
                    </div>
                    <div class="input-wrapper">
                        <label class="form-label" for="phone">
                            <xsl:value-of select="java:getString($dict_pm_module, 'PM_PHONE')"/>
                        </label>
                        <input id="phone" name="phone" class="form-input" type="tel"></input>
                    </div>


                </div>

                <div class="info-item ship-info hidden m-t-40">
                    <h2 class="main-col text-center fs-22">
                        <xsl:value-of select="java:getString($dict_pm_module, 'PM_SHIPPING_ADDRESS')"/>
                    </h2>
                    <div class="input-wrapper company-name hidden">
                        <label class="form-label" for="company_name-delivery">
                            <xsl:value-of select="java:getString($dict_pm_module, 'PM_COMPANY_NAME')"/>*
                        </label>
                        <input id="company_name-delivery" name="company_name-delivery" class="form-input"
                               type="text"></input>
                    </div>

                    <div class="input-wrapper">
                        <label class="form-label" for="first_name-delivery">
                            <xsl:value-of select="java:getString($dict_pm_module, 'PM_FIRST_NAME')"/>
                        </label>
                        <input id="first_name-delivery" name="first_name-delivery" class="form-input"
                               type="text"></input>
                    </div>

                    <div class="input-wrapper">
                        <label class="form-label" for="last_name-delivery">
                            <xsl:value-of select="java:getString($dict_pm_module, 'PM_LAST_NAME')"/>
                        </label>
                        <input id="last_name-delivery" name="last_name-delivery" class="form-input" type="text"></input>
                    </div>

                    <div class="input-wrapper">
                        <label class="form-label" for="coutr-list-delivery">
                            <xsl:value-of select="java:getString($dict_pm_module, 'PM_COUNTRY')"/>
                        </label>
                        <select style="border:none; outline:none" id="coutr-list-delivery" name="country-delivery"
                                class="form-input" type="text">
                            <option>Choose</option>
                        </select>
                    </div>
                    <div class="input-wrapper">
                        <label class="form-label" for="town-delivery">
                            <xsl:value-of select="java:getString($dict_pm_module, 'PM_CITY')"/>
                        </label>
                        <input id="town-delivery" name="town-delivery" class="form-input" type="text"></input>
                    </div>
                    <div class="input-wrapper">
                        <label class="form-label" for="street-delivery">
                            <xsl:value-of select="java:getString($dict_pm_module, 'PM_ADDRESS')"/>
                        </label>
                        <input id="street-delivery" name="street-delivery" class="form-input" type="text"></input>
                    </div>
                    <div class="input-wrapper">
                        <label class="form-label" for="house-delivery">
                            House
                        </label>
                        <input id="house-delivery" name="house-delivery" class="form-input" type="text"></input>
                    </div>
                    <div class="input-wrapper">
                        <label class="form-label" for="postcode-delivery">
                            <xsl:value-of select="java:getString($dict_pm_module, 'PM_ZIP_POSTCODE')"/>
                        </label>
                        <input id="postcode-delivery" name="postcode-delivery" class="form-input" type="text"></input>
                    </div>
                    <div class="input-wrapper">
                        <label class="form-label" for="phone-delivery">
                            <xsl:value-of select="java:getString($dict_pm_module, 'PM_PHONE')"/>
                        </label>
                        <input id="phone-delivery" name="phone-delivery" class="form-input" type="tel"></input>
                    </div>

                </div>
                <div class="container-login100-form-btn pos-absolute b--70 trans-center">
                    <div class="wrap-login100-form-btn">
                        <div class="login100-form-bgbtn"></div>
                        <button type="submit" class="login100-form-btn">
                            <xsl:value-of select="java:getString($dict_pm_module, 'PM_REGISTER')"/>
                        </button>
                    </div>
                </div>

            </form>
        </div>
    </xsl:template>

    <xsl:template name="copyright">
        <div class="copyright">
            <a class="b-negeso-link" href="{java:getString($dict_common, 'NEGESO_WEB_DESIGN_LINK')}"
               title="{java:getString($dict_common, 'NEGESO_WEB_DESIGN_TITLE')}" target="_blank" onFocus="blur()">
                <xsl:value-of select="java:getString($dict_common, 'NEGESO_WEB_DESIGN')"/>
            </a>&#160;&#160;&amp;&#160;
            <a href="{java:getString($dict_common, 'NEGESO_CMS_LINK')}"
               title="{java:getString($dict_common, 'NEGESO_CMS_TITLE')}" target="_blank" onFocus="blur()">
                <xsl:value-of select="java:getString($dict_common, 'NEGESO_CMS')"/>
            </a>
            &#160;<xsl:value-of select="java:getString($dict_common, 'NEGESO_NEGESO_BY')"/>&#160;
            <a href="{java:getString($dict_common, 'NEGESO_NEGESO_LINK')}"
               title="{java:getString($dict_common, 'NEGESO_NEGESO_TITLE')}" target="_blank" onFocus="blur()">
                <xsl:value-of select="java:getString($dict_common, 'NEGESO_NEGESO')"/>
            </a>&#160;&#174;
        </div>
    </xsl:template>

    <!-- LONG GRAY BLOCK: Begin -->
    <xsl:template name="page_title_line_block">
        <xsl:param name="title">&#160;</xsl:param>
        <h1>
            <xsl:copy-of select="$title"/>
        </h1>
    </xsl:template>

    <xsl:template name="page_title">
        <xsl:param name="title">&#160;</xsl:param>
        <h1>
            <xsl:copy-of select="$title"/>
        </h1>
    </xsl:template>
    <!-- LONG GRAY BLOCK: End -->
    <xsl:template name="frontpage_category-list">
        <div class="category-list-front__item scroller-el swiper-slide">
            <a>
                <xsl:attribute name="href">
                    <xsl:value-of select="@url"/>
                </xsl:attribute>
                <!--				<img alt="" onerror="imgError(this)" class="img">-->
                <!--					<xsl:attribute name="src">/media/productsCategory/WL_<xsl:value-of select="translate(@title, ' ABCDEFGHIJKLMNOPQRSTUVWXYZ', '_abcdefghijklmnopqrstuvwxyz')"/>.png</xsl:attribute>-->
                <!--					&lt;!&ndash;<xsl:attribute name="onerror">this.src='/media/cap.jpg';this.src='/media/productsCategory/WL_<xsl:value-of select="translate(@title, ' ABCDEFGHIJKLMNOPQRSTUVWXYZ', '_abcdefghijklmnopqrstuvwxyz')"/>.jpg'</xsl:attribute>&ndash;&gt;-->
                <!--				</img>-->
            </a>
            <p class="name">
                <a>
                    <xsl:attribute name="href">
                        <xsl:value-of select="@url"/>
                    </xsl:attribute>
                    <xsl:value-of select="@title"/>
                </a>
            </p>
        </div>

    </xsl:template>
    <xsl:template name="webshop_wishlist">
        <div class="webshop_wish_wrapper responsive-wrapper clearfix">
            <div class="clearfix wish_header_wrapper">
                <xsl:call-template name="webshop_wish_header"/>
            </div>
            <xsl:call-template name="webshop_wish_items"/>
        </div>
        <!--<xsl:call-template name="webshop_wish_footer" />-->
    </xsl:template>

    <xsl:template name="webshop_wish_header">
        <div class="wish_header wish_header_first">
            <xsl:call-template name="add-constant-info">
                <xsl:with-param name="dict" select="$dict_webshop_module"/>
                <xsl:with-param name="name" select="'WEBSHOP_PRODUCT_PRODUCT_NAME'"/>
            </xsl:call-template>
            <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_PRODUCT_NAME')"/>
        </div>
        <div class="wish_header">
            <xsl:call-template name="add-constant-info">
                <xsl:with-param name="dict" select="$dict_webshop_module"/>
                <xsl:with-param name="name" select="'WEBSHOP_PRODUCT_PRODUCT_PRICE'"/>
            </xsl:call-template>
            <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_PRODUCT_PRICE')"/>
        </div>
        <div class="wish_header">
            <xsl:call-template name="add-constant-info">
                <xsl:with-param name="dict" select="$dict_webshop_module"/>
                <xsl:with-param name="name" select="'WEBSHOP_PRODUCT_HOEVEELHEID'"/>
            </xsl:call-template>
            <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_HOEVEELHEID')"/>
        </div>
        <div class="wish_header">
            <xsl:call-template name="add-constant-info">
                <xsl:with-param name="dict" select="$dict_webshop_module"/>
                <xsl:with-param name="name" select="'WEBSHOP_PRODUCT_ARTICLE_NUMBER'"/>
            </xsl:call-template>
            <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_ARTICLE_NUMBER')"/>
        </div>
        <div class="wish_header">
            <xsl:call-template name="add-constant-info">
                <xsl:with-param name="dict" select="$dict_webshop_module"/>
                <xsl:with-param name="name" select="'WEBSHOP_PRODUCT_DELETE'"/>
            </xsl:call-template>
            <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_DELETE')"/>
        </div>
        <div class="wish_header">TOEVOEGEN</div>
    </xsl:template>

    <xsl:template name="webshop_wish_items">
        <xsl:for-each select="//negeso:customer/negeso:products/negeso:product">
            <xsl:choose>
                <xsl:when test="@visible = 'true'">
                    <div class="wish_items_wrapper clearfix">
                        <div class="wish_items wish_items_first">
                            <a href="webshop_nl/{@url}">
                                <img alt="" class="img" onerror="this.src='../media/cap.jpg'">
                                    <xsl:attribute name="src">/media/productsImages/<xsl:value-of select="@id"/>.jpg
                                    </xsl:attribute>
                                </img>
                                <span class="title">
                                    <xsl:value-of select="@title"/>
                                </span>
                                <br/>
                                <br/>
                                <xsl:if test="not(/negeso:page/@role-id = 'guest')">
                                    <img alt="">
                                        <xsl:attribute name="src">
                                            /site/modules/webshop_module/images/stock_<xsl:value-of
                                                select="@stockColor"/>.png
                                        </xsl:attribute>
                                    </img>
                                </xsl:if>
                            </a>
                        </div>
                        <div class="wish_items price">
                            <xsl:value-of select="@priceExcludeVat"/>
                        </div>
                        <div class="wish_items">
                            <button class="js_minus"/>
                            <input id="js_counter" class="quantity js_counter_wishlist" name="product_amount"
                                   type="text">
                                <xsl:attribute name="value">
                                    <xsl:choose>
                                        <xsl:when test="@multipleOf = '0' or not(@multipleOf)">
                                            <xsl:value-of select="1"/>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <xsl:value-of select="@multipleOf"/>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </xsl:attribute>
                            </input>
                            <span style="display: none" class="multipleOf">
                                <xsl:choose>
                                    <xsl:when test="@multipleOf = '0' or not(@multipleOf)">
                                        <xsl:value-of select="1"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:value-of select="@multipleOf"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </span>
                            <button class="js_plus"/>
                        </div>
                        <div class="wish_items">
                            <span class="orderCode">
                                <xsl:value-of select="@id"/>
                            </span>
                        </div>
                        <!--<div class="wish_items">-->
                        <!--<button class="js_minus" />-->
                        <!--<input id="js_counter" class="quantity" name="product_amount" type="text">-->
                        <!--<xsl:attribute name="value"><xsl:value-of select="@quantity"/></xsl:attribute>-->
                        <!--</input>-->
                        <!--<span style="display: none" class="multipleOf">-->
                        <!--<xsl:choose>-->
                        <!--<xsl:when test="@multipleOf = '0' or not(@multipleOf)" >-->
                        <!--<xsl:value-of select="1"/>-->
                        <!--</xsl:when>-->
                        <!--<xsl:otherwise>-->
                        <!--<xsl:value-of select="@multipleOf"/>-->
                        <!--</xsl:otherwise>-->
                        <!--</xsl:choose>-->
                        <!--</span>-->
                        <!--<button class="js_plus" />-->
                        <!--</div>-->
                        <div class="wish_items">
                            <button class="delete_wish_js delete" type="button">x</button>
                        </div>
                        <div class="wish_items">
                            <span style="display: none;">
                                <xsl:choose>
                                    <xsl:when test="@multipleOf = '0' or not(@multipleOf)">1</xsl:when>
                                    <xsl:otherwise>
                                        <xsl:value-of select="@multipleOf"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </span>
                            <span style="display: none;">
                                <xsl:value-of select="@id"/>
                            </span>
                            <span class="wish_js_product-cart" onclick="addPtoductWishInCart(event, this) "/>
                        </div>
                    </div>
                </xsl:when>
                <xsl:otherwise>
                    <div class="wish_items_wrapper clearfix">
                        <div class="wish_items wish_items_first">
                            <a href="webshop_nl/{@url}">
                                <img alt="" class="img" onerror="this.src='../media/cap.jpg'">
                                    <xsl:attribute name="src">/media/productsImages/<xsl:value-of select="@id"/>.jpg
                                    </xsl:attribute>
                                </img>
                                <span class="title">
                                    <xsl:value-of select="@title"/>
                                </span>
                                <br/>
                                <br/>
                                <xsl:if test="not(/negeso:page/@role-id = 'guest')">
                                    <img alt="">
                                        <xsl:attribute name="src">
                                            /site/modules/webshop_module/images/stock_<xsl:value-of
                                                select="@stockColor"/>.png
                                        </xsl:attribute>
                                    </img>
                                </xsl:if>
                            </a>
                            <p class="product-message">
                                <xsl:value-of select="java:getString($dict_webshop_module, 'PRODUCT_NOT_AVAILABLE')"/>
                            </p>
                        </div>
                        <div class="wish_items price">
                        </div>
                        <div class="wish_items">
                        </div>
                        <div class="wish_items">
                            <span class="orderCode">
                                <xsl:value-of select="@id"/>
                            </span>
                        </div>
                        <div class="wish_items">
                            <button class="delete_wish_js delete" type="button">x</button>
                        </div>
                        <div class="wish_items">
                        </div>
                    </div>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
    </xsl:template>

    <xsl:template name="articelenBesteling">
        <div class="clearfix breadcrumps-wrapper">
            <a class="breadcrumps-links" href="/webshop_nl.html">
                <xsl:choose>
                    <xsl:when test="/negeso:page/@admin-path='true'">
                        <xsl:attribute name="href">/admin/webshop_nl.html</xsl:attribute>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:attribute name="href">/webshop_nl.html</xsl:attribute>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:call-template name="add-constant-info">
                    <xsl:with-param name="name" select="'CORE.ARTIKELEN_OP_BESTELING'"/>
                </xsl:call-template>
                <xsl:value-of select="java:getString($dict_common, 'CORE.ARTIKELEN_OP_BESTELING')"/>
            </a>
        </div>
        <xsl:for-each select="//negeso:sales/negeso:matrix/negeso:product">
            <div class="webshop-products-view">
                <span class="img-wrapper">
                    <a class="loupeImg_js">
                        <xsl:attribute name="href">webshop_nl/<xsl:value-of select="@url"/>
                        </xsl:attribute>
                        <img alt="" class="img" onerror="this.src='/media/cap.jpg'">
                            <xsl:attribute name="src">/media/productsImages/<xsl:value-of select="@id"/>.jpg
                            </xsl:attribute>
                        </img>
                        <span class="img-bg"/>
                        <span class="loupeImg"/>
                    </a>
                    <a class="addToCart">
                        <xsl:attribute name="href">webshop_nl/<xsl:value-of select="@url"/>
                        </xsl:attribute>
                    </a>
                </span>
                <p class="name">
                    <a class="link">
                        <xsl:attribute name="href">webshop_nl/<xsl:value-of select="@url"/>
                        </xsl:attribute>
                        <xsl:value-of select="@title"/>
                    </a>
                </p>
                <p class="price-block">
                    <a class="link">
                        <xsl:attribute name="href">webshop_nl/<xsl:value-of select="@url"/>
                        </xsl:attribute>
                        <xsl:if test="not(/negeso:page/@role-id = 'guest')">
                            <xsl:choose>
                                <xsl:when test="/negeso:page/negeso:contents/negeso:customer/@displayPrice = 'Inkoop'">
                                    <span class="price">
                                        <xsl:value-of select="@priceExcludeVat"/>
                                    </span>
                                </xsl:when>
                                <xsl:when test="/negeso:page/negeso:contents/negeso:customer/@displayPrice = 'Verkoop'">
                                    <span class="price">
                                        <xsl:value-of select="@retailPriceExcludeVat"/>
                                    </span>
                                </xsl:when>
                                <xsl:when
                                        test="/negeso:page/negeso:contents/negeso:customer/@displayPrice = 'Inkoop_Verkoop'">
                                    <span class="price">
                                        <xsl:value-of select="@priceExcludeVat"/>
                                        <br/>
                                        <xsl:value-of select="@retailPriceExcludeVat"/>
                                    </span>
                                </xsl:when>
                                <xsl:otherwise>
                                    <span class="price">
                                        <xsl:value-of select="@priceExcludeVat"/>
                                    </span>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:if>
                        <span class="product-cart"/>
                        <span class="buy">
                            <xsl:call-template name="add-constant-info">
                                <xsl:with-param name="dict" select="$dict_webshop_module"/>
                                <xsl:with-param name="name" select="'WEBSHOP_PRODUCT_ORDER'"/>
                            </xsl:call-template>
                            <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_ORDER')"/>
                        </span>
                    </a>
                </p>
            </div>
        </xsl:for-each>
    </xsl:template>
</xsl:stylesheet>