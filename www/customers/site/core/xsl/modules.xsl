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

<!--====================== ADMIN TEMPLATES ==========================-->
<xsl:include href="/xsl/admin_templates.xsl"/>
<xsl:include href="/xsl/enduser_admin_templates.xsl"/>

<!--=========================== LOGIN ===============================-->
<xsl:include href="/site/modules/login_module/xsl/login.xsl"/>

<!--====================== SEARCH ARTICLE ===========================-->
<xsl:include href="/site/modules/search_module/xsl/search.xsl"/>

<!--=========================== GLOSSARY ================================-->
<xsl:include href="/site/modules/glossary_module/xsl/glossary.xsl"/>

<!--=========================== NEWS ================================-->
<xsl:include href="/site/modules/news/xsl/news.xsl"/>

<!--========================= NEWSLINE ==============================-->
<xsl:include href="/site/modules/news/xsl/newsline.xsl"/>

<!--===================== NEWS LETTER MODULE ========================-->
<xsl:include href="/site/modules/newsletter/xsl/newsletter.xsl"/>

<!--====================== PRODUCT MODULE ===========================-->
<xsl:include href="/site/modules/product_module/xsl/pm_module.xsl"/>
<xsl:include href="/site/modules/product_module/xsl/pm_links.xsl"/>
<xsl:include href="/site/modules/product_module/xsl/pm_search.xsl"/>
<xsl:include href="/site/modules/product_module/xsl/pm_filters.xsl"/>
<xsl:include href="/site/modules/product_module/xsl/pm_shopping_cart.xsl"/>
<xsl:include href="/site/modules/product_module/xsl/pm_mail_to_friend.xsl"/>

<!--====================== WEBSHOP MODULE ===========================-->
<xsl:include href="/site/modules/webshop_module/xsl/webshop_module.xsl"/>

<!--====================== WEBSHOP CART MODULE ===========================-->
<xsl:include href="/site/modules/webshop_module/webshop_cart/xsl/webshop_cart.xsl"/>

<!--====================== WEBSHOP CART MODULE ===========================-->
<xsl:include href="/site/modules/webshop_module/webshop_checkout/xsl/webshop_checkout.xsl"/>

<!--====================== WEBSHOP ORDER MODULE ===========================-->
<xsl:include href="/site/modules/webshop_module/webshop_order/xsl/webshop_order.xsl"/>

<!--========================= JOB MODULE ============================-->
<xsl:include href="/site/modules/job_module/xsl/job.xsl"/>
<xsl:include href="/site/modules/job_module/xsl/job_consts.xsl"/>

<!--========================== SITEMAP ==============================-->
<xsl:include href="/site/modules/sitemap/xsl/sitemap.xsl"/>

<!--========================== MARQUEE ==============================-->
<xsl:include href="/site/modules/marquee/xsl/marquee.xsl"/>

<!--========================== MARQUEE ==============================-->
<xsl:include href="/site/modules/marquee/xsl/marquee.xsl"/>

<!--======================== PHOTO ALBUM ============================-->
<xsl:include href="/site/modules/photo_album/xsl/photo_album.xsl"/>

<!--=================== SIMPLE AND LINKED FAQ =======================-->
<xsl:include href="/site/modules/faq_module/xsl/faq.xsl"/>

<!--========================= WEB POLL ==============================-->
<xsl:include href="/site/modules/webpoll/xsl/webpoll.xsl"/>

<!--======================= EVENT MODULE ============================-->
<xsl:include href="/site/modules/lite_event/xsl/lite_event.xsl"/>

<!--============ CONTACT BOOK (+BIRTHDAY REMINDER) ==================-->
<xsl:include href="/site/modules/contact_book/xsl/contact_book.xsl"/>

<!--======================= INQUIRY MODULE ==========================-->
<xsl:include href="/site/modules/inquiry_module/xsl/inquiry_module.xsl" />

<!--======================= DOCUMENT MODULE =========================-->
<xsl:include href="/site/modules/document_module/xsl/document.xsl"/>

<!--==================== EXTENDED STORE LOCATOR =====================-->
<xsl:include href="/site/modules/extended_store_locator/xsl/extended_store_locator.xsl"/>

<!--======================== STORE LOCATOR ==========================-->
<xsl:include href="/site/modules/store_locator/xsl/store_locator.xsl"/>

<!--========================= GUEST BOOK ============================-->
<xsl:include href="/site/modules/guest_book/xsl/guest_book.xsl"/>
<xsl:include href="/site/modules/guest_book/xsl/guest_book_links.xsl"/>

<!--======================== MAIL TO A FRIEND ==========================-->
<xsl:include href="/site/modules/mail_to_a_friend/xsl/mail_to_a_friend.xsl"/>

<!--======================== BANNER MODULE ==========================-->
<xsl:include href="/site/modules/banner_module/xsl/banner_module.xsl"/>

<!--======================== FLIP BOOK MODULE ==========================-->
<xsl:include href="/site/modules/flip_book/xsl/flip_book.xsl"/>

<!--======================== RICH SNIPPET MODULE ==========================-->
<xsl:include href="/site/modules/rich_snippet/xsl/rs_module.xsl"/>

<xsl:template name="modules_heads">
	<!-- LIGHT EVENT MODULE -->
	<xsl:apply-templates select="//negeso:EventCalendarsComponent[1]" mode="page_head"/>
	<xsl:apply-templates select="//negeso:EventViewComponent[1]" mode="page_head"/>
	<xsl:apply-templates select="//negeso:NearEventsComponent[1]" mode="page_head"/>

	<!-- GLOSSARY MODULE -->
	<xsl:apply-templates select="//negeso:glossary_module[1]" mode="page_head"/>
	
	<!-- NEWS MODULE -->
	<xsl:apply-templates select="//negeso:list[@type='newslist'][1]" mode="page_head"/>
	<xsl:apply-templates select="//negeso:contents/negeso:listItem" mode="page_head"/>
	
	<!-- NEWSLINE MODULE -->
	<xsl:apply-templates select="//negeso:list[1]" mode="page_head"/>
	
	<!-- PRODUCT MODULE-->
	<xsl:apply-templates select="//negeso:pm[1]" mode="page_head"/>
	<xsl:apply-templates select="//negeso:pm-filter[1]" mode="page_head"/>


	<!-- WEBSHOP MODULE -->
	<xsl:apply-templates select="//negeso:webshop" mode="page_head"/>

	<!-- WEBSHOP CART MODULE -->
	<xsl:apply-templates select="/negeso:page[@class='webshop_cart']" mode="page_head"/>

	<!-- WEBSHOP CHECKOUT MODULE -->
	<xsl:apply-templates select="/negeso:page[@class='webshop_checkout']" mode="page_head"/>

	<!-- WEBSHOP ORDER MODULE -->
	<xsl:apply-templates select="/negeso:page[@class='webshop_order']" mode="page_head"/>
	
	<!-- FAQ MODULE -->
	<xsl:apply-templates select="//negeso:list[@type='faqsimple' or @type='faqlink'][1]" mode="page_head"/>

	<!-- PHOTO ALBUM MODULE -->
	<xsl:apply-templates select="//negeso:photo_album[1]" mode="page_head"/>

	<!-- NEWSLETTER MODULE -->
	<xsl:apply-templates select="//negeso:newsletter[1]" mode="page_head"/>

	<!-- LOGIN MODULE -->
	<xsl:if test="/negeso:page/@class='login'">
		<link rel="stylesheet" type="text/css" href="/site/modules/login_module/css/login.css"/>
	</xsl:if>
	<xsl:if test="(/negeso:page/@class='login') or (//negeso:customer-login) or (//negeso:customer-account)or (//negeso:customer-register)">
		<script type="text/javascript" src="/script/md5.js">/**/</script>
	</xsl:if>

	<!-- MAIL TO A FRIEND -->
	<xsl:apply-templates select="//negeso:mail_to_a_friend[1]" mode="page_head"/>

	<!-- WEB POLL MODULE -->
	<xsl:apply-templates select="//negeso:list[@type='webpoll' or @type='webpollresult'][1]" mode="page_head"/>

	<!-- CONTACT BOOK MODULE -->
	<xsl:apply-templates select="//negeso:contact-book[1]" mode="page_head"/>

	<!-- INQUIRY MODULE -->
	<xsl:apply-templates select="//negeso:inquiry[1]" mode="page_head"/>

	<!-- JOB MODULE -->
	<xsl:apply-templates select="//negeso:job_module_component[1]" mode="page_head"/>

	<!-- DOCUMENTS MODULE -->
	<xsl:apply-templates select="//negeso:document_catalog_component[1]" mode="page_head"/>

	<!-- GUEST BOOK MODULE -->
	<xsl:choose>
		<xsl:when test="//negeso:guestbook">
			<xsl:apply-templates select="//negeso:guestbook[1]" mode="page_head"/>
		</xsl:when>
		<xsl:otherwise>
			<xsl:apply-templates select="/negeso:page[@class='post_gb_message'][1]" mode="page_head" />
		</xsl:otherwise>
	</xsl:choose>
	
	<!-- SEARCH MODULE -->
	<xsl:apply-templates select="//negeso:search[1]" mode="page_head"/>


	<!-- SITEMAP MODULE -->
	<xsl:if test="$class='sitemap3'">
		<link rel="stylesheet" type="text/css" href="/site/modules/sitemap/css/sitemap.css"/>
	</xsl:if>

	<!-- MARQUEE MODULE -->
	<xsl:if test="//negeso:article[@class='marquee']">
		<xsl:apply-templates select="//negeso:article[@class='marquee'][1]" mode="page_head"/>
	</xsl:if>
	
	<!-- RICH SNIPPET MODULE -->
	<xsl:if test="//negeso:rich-snippets">
		<xsl:apply-templates select="//negeso:rich-snippets[1]" mode="page_head"/>
	</xsl:if>

	<!-- STORE LOCATOR MODULE -->
	<xsl:if test="$class='ex_store'">
		<link rel="stylesheet" type="text/css" href="/site/modules/extended_store_locator/css/ex_store.css"/>
		<script type="text/javascript" src="/site/modules/extended_store_locator/script/ex_store.js">/**/</script>
	</xsl:if>
	<xsl:if test="$class='store'">
		<script type="text/javascript" src="/site/modules/store_locator/script/store_locator.js">/**/</script>
	</xsl:if>
	
</xsl:template>
</xsl:stylesheet>