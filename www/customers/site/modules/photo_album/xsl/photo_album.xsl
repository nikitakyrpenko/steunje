<?xml version="1.0" encoding="UTF-8"?>
<!--
  Copyright (c) 2008 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information"). You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.


  XSL templates for Photo Album
 
  @version		2008.01.11
  @author		Oleg 'germes' Shvedov
  @author		Rostislav 'KOTT' Brizgunov

-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">

<xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>
<xsl:variable name="dict_photo_album" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('photo_album', $lang)"/>

<xsl:template match="negeso:photo_album" mode="page_head">
	<link rel="stylesheet" type="text/css" href="/site/modules/photo_album/css/photo_album.css"/>
</xsl:template>

<!--========================================== PHOTO ALBUM START =================================-->
<xsl:template match="negeso:photo_album" mode="pa">
	<!-- <xsl:value-of select="java:getString($dict_photo_album, 'PA_TITLE')"/> -->
								<xsl:if test="$outputType = 'admin' and not(/negeso:page/@role-id = 'visitor')">
		<div>
										<img id="organize_widget_img" border="0" class="hand" src="/images/mark_1.gif" onclick="window.open('?command=get-photo-album-command', 'album', 'top=50,left=50,height=600,width=800,status=0,toolbar=0,menubar=0,location=0,resizable=1,scrollbars=yes')" alt="" />
									</div>
								</xsl:if>
								<span>
									<xsl:call-template name ="add-constant-info">
										<xsl:with-param name ="dict"  select="$dict_photo_album"/>
										<xsl:with-param name ="name"  select="'PA_TITLE'"/>
									</xsl:call-template>
									&#160;<xsl:value-of select="java:getString($dict_photo_album, 'PA_TITLE')"/>
								</span>
								<xsl:choose>
									<xsl:when test="descendant::negeso:photo[@selected = 'true']">
										<xsl:apply-templates select="descendant::negeso:photo[@selected = 'true']" mode="pa_photo" />
									</xsl:when>
									<xsl:when test="negeso:album[@selected = 'true']">
										<xsl:for-each select="descendant::negeso:album[@selected = 'true' and not(negeso:album[@selected = 'true'])]">
											<xsl:call-template name="pa_contents" />
										</xsl:for-each>
										&#160;
									</xsl:when>
									<xsl:otherwise>
										<xsl:call-template name="pa_contents" />
										&#160;
									</xsl:otherwise>
								</xsl:choose>
	
</xsl:template>

<!-- Contents of the Photo Album -->
<xsl:template name="pa_contents">
	<!-- Current node: negeso:photo_album -->
	<xsl:if test="@selected='true'">
		<div class="b-photoBack">
			<a href="?albumId={parent::negeso:album/@id}">
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_photo_album"/>
					<xsl:with-param name ="name"  select="'PA_BACK'"/>
				</xsl:call-template>
				<xsl:attribute name="title"><xsl:value-of select="java:getString($dict_photo_album, 'PA_BACK')"/></xsl:attribute>
				<img src="/site/modules/photo_album/images/photoBack.gif" width="147" height="114" border="0" >
					<xsl:attribute name="alt"><xsl:value-of select="java:getString($dict_photo_album, 'PA_BACK')"/></xsl:attribute>
				</img>
			</a>
		</div>
	</xsl:if>
	<xsl:apply-templates select="negeso:album" mode="pa_icon"/>
	<xsl:apply-templates select="negeso:photo" mode="pa_icon"/>
</xsl:template>

<!-- Album icon with description -->
<xsl:template match="negeso:album" mode="pa_icon">
	<div class="b-photoAlbum">
		<!-- Album thumbnail -->
		<xsl:choose>
			<xsl:when test="@th_src != ''">
				<a href="?albumId={@id}" onfocus="blur()" class="b-photoAlbum-Thumb">
						<img src="{@th_src}" width="{@th_width}" height="{@th_height}" alt="{@name}" title="{@name}" border="0" />
					</a>
			</xsl:when>
			<xsl:otherwise>
				<a href="?albumId={@id}" title="{@name}" onfocus="blur()" class="b-photoAlbum-Thumb">&#160;</a>
			</xsl:otherwise>
		</xsl:choose>
		
		<br/>
		
		<!-- Album name -->
		<a href="?albumId={@id}" title="{@name}" id="album_href_{@id}" onfocus="blur()"  class="b-photoAlbum-Name">
				<xsl:value-of select="@name" />
		</a>
		
		<br/>
		
		<!-- Album HINT -->
		<div style="display:none" id="alb_div_{@id}"><xsl:value-of select="negeso:article/@text" /></div>
	
		<script type="text/javascript">
			var alb_id = '<xsl:value-of select="@id" />';
			try{
				var alb_name = '<xsl:call-template name="string-replace-quotes">
										      		<xsl:with-param name="text" select="@name" />
								</xsl:call-template>';
				var alb_descr = document.getElementById('alb_div_' + alb_id).innerHTML;
				document.getElementById('album_href_'+alb_id).title = alb_name + '\n --- \n' + alb_descr;
			}
			catch(e){}
		</script>
		
		<!-- Album description -->
		<div class="b-photoAlbum-Descr">
			<xsl:value-of select="negeso:article/@text" disable-output-escaping="yes"/>
	</div>
	</div>
		
	<br/>
</xsl:template>

<!-- Photo icon with description -->
<xsl:template match="negeso:photo" mode="pa_icon">
	<div class="b-photoPhoto">
		<!-- Photo thumbnail -->
		<a href="?albumId={@id}" target="_blank" title="" onfocus="blur()" class="b-photoPhoto-Thumb">
				<img src="{@th_src}" width="{@th_width}" height="{@th_height}"  alt="{@name}" border="0" />
			</a>
		
		<br/>
		
		<!-- Photo name -->
		<a href="?albumId={@id}" target="_blank" title="" id="pht_href_{@id}" onfocus="blur()" class="b-photoPhoto-Name">
				<xsl:value-of select="@name" />
			</a>
		
		<!-- Photo HINT -->
		<div style="display:none" id="pht_div_{@id}"><xsl:value-of select="negeso:article/@text" /></div>
		
		<script type="text/javascript">
			var pht_id = '<xsl:value-of select="@id" />';
			try{
				var pht_name = '<xsl:call-template name="string-replace-quotes">
										      		<xsl:with-param name="text" select="@name" />
								</xsl:call-template>';
				var pht_descr = document.getElementById('pht_div_' + pht_id).innerHTML;
				document.getElementById('pht_href_'+pht_id).title = pht_name + '\n --- \n' + pht_descr;
			}
			catch(e){e.description;}
		</script>
		
		<!-- Photo description -->
		<div class="b-photoPhoto-Descr">
			<xsl:value-of select="negeso:article/@text" disable-output-escaping="yes"/>
	</div>
	</div>
</xsl:template>

<!-- Photo - full size -->
<xsl:template match="negeso:photo" mode="pa_photo">
	<div class="b-photoFull">
		<img src="{@src}" width="{@img_width}" height="{@img_height}"  alt="Image name" border="0" class="b-photoFull-Img"/>
	</div>
</xsl:template>

<!-- PAGE NAVIGATOR START -->
<xsl:template match="negeso:album" mode="paging">
	<xsl:param name="paging_style" select="'old'" />
	<!-- CSS classes see in site.css -->
	<xsl:choose>
		<xsl:when test="$paging_style='old'">
			<div class="b-photoPaging-Old">
				<xsl:apply-templates select="negeso:photo" mode="paging">
					<xsl:with-param name="selected_numb" select="count(negeso:photo[@selected]/preceding-sibling::negeso:photo)" />
				</xsl:apply-templates>
			</div>
		</xsl:when>
		<xsl:when test="$paging_style='new'">
			<xsl:if test="count(negeso:photo) &gt; 1">
				<script type="text/javascript" src="/site/core/script/paging.js">/**/</script>
				<table cellpadding="0" cellspacing="0" border="0" class="b-photoPaging-New">
						<tr>
							<xsl:if test="count(negeso:photo[@selected]/preceding-sibling::negeso:photo) &gt; 0">
								<td>
									<a href="?albumId={negeso:photo[@selected]/preceding-sibling::negeso:photo[1]/@id}">
										<xsl:call-template name ="add-constant-info">
											<xsl:with-param name ="name"  select="'CORE.PREV'"/>
										</xsl:call-template>
										&lt;&lt;&#160;<xsl:value-of select="java:getString($dict_common, 'CORE.PREV')"/>
									</a>
								</td>
							</xsl:if>
							<td>
								
								<form method="get" name="paPagingForm">
									<input type="text" 
										name="albumId_fake" 
										disabled="disabled" 
										value="{count(negeso:photo[@selected]/preceding-sibling::negeso:photo)+1}" 
										onkeyup="handle_paging(event, this, {count(negeso:photo)}); handle_next_photo(this.form);" 
										title="LEFT ARROW = page-1; RIGHT ARROW = page+1; ENTER BUTTON = Go!"
									/>
									<input type="hidden" name="albumId" value="negeso:photo[@selected]/@id" />
								</form>
								
								<!-- Addition for Photo Album paging -->
								<script type="text/javascript">
									
									function handle_next_photo(paPagingForm) {
										paPagingForm.albumId.value=photos_array[parseInt(paPagingForm.albumId_fake.value)-1];
									}
									
									var photos_array = [<xsl:for-each select="negeso:photo">'<xsl:value-of 
									select="@id" />'<xsl:if test="position()!=last()">,</xsl:if></xsl:for-each>];
									
									document.forms['paPagingForm'].elements['albumId_fake'].disabled=false;
									document.forms['paPagingForm'].elements['albumId_fake'].focus();
									
								</script>
								
							</td>
							<td>/&#160;<xsl:value-of select="count(negeso:photo)" /></td>
							<xsl:if test="count(negeso:photo[@selected]/preceding-sibling::negeso:photo)+1 &lt; count(negeso:photo)">
								<td>
									<a href="?albumId={negeso:photo[@selected]/following-sibling::negeso:photo[1]/@id}">
										<xsl:call-template name ="add-constant-info">
											<xsl:with-param name ="name"  select="'CORE.NEXT'"/>
										</xsl:call-template>
										<xsl:value-of select="java:getString($dict_common, 'CORE.NEXT')"/>&#160;&gt;&gt;
									</a>
								</td>
							</xsl:if>
						</tr>
					</table>
			</xsl:if>
		</xsl:when>
	</xsl:choose>
</xsl:template>

<!-- XSL Template for OLD style of paging -->
<xsl:template match="negeso:photo" mode="paging">
	<xsl:param name="selected_numb" select="1" />
	<xsl:choose>
		<xsl:when test="(((count(preceding-sibling::negeso:photo)+1) &gt; (number($selected_numb)-5)) and (count(preceding-sibling::negeso:photo) &lt; (number($selected_numb) + 5)))">
			<a href="?albumId={@id}">
				<xsl:if test="@selected">
					<xsl:attribute name="class">current_page</xsl:attribute>
				</xsl:if>
				<xsl:value-of select="count(preceding-sibling::negeso:photo)+1" />
			</a>
		</xsl:when>
		<xsl:otherwise>
			<xsl:if test="(count(preceding-sibling::negeso:photo)+1) = (number($selected_numb)-5)">
				<a href="?albumId={@id}">
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_photo_album"/>
						<xsl:with-param name ="name"  select="'PA_PREV'"/>
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_photo_album, 'PA_PREV')"/>
				</a>
			</xsl:if>
			<xsl:if test="count(preceding-sibling::negeso:photo) = (number($selected_numb)+5)">
				<a href="?albumId={@id}">
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_photo_album"/>
						<xsl:with-param name ="name"  select="'PA_NEXT'"/>
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_photo_album, 'PA_NEXT')"/>
				</a>
			</xsl:if>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>
<!-- PAGE NAVIGATOR END -->

<!-- WHERE AM I: BEGIN -->
<xsl:template match="negeso:photo_album" mode="where_am_i">
	<xsl:choose>
		<xsl:when test="not(negeso:album[@selected='true'])">
			<div>
				<xsl:value-of select="//negeso:page/negeso:title" />
			</div>
		</xsl:when>
		<xsl:otherwise>
			<div>
				<a href="{/negeso:page/negeso:filename/text()}">
					<xsl:value-of select="//negeso:page/negeso:title" />
				</a>
				&#160;&#160;&gt;&gt;&gt;&#160;&#160;
			</div>
			<xsl:apply-templates select="negeso:album[@selected='true']" mode="where_am_i" />
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template match="negeso:album" mode="where_am_i">
	<xsl:choose>
		<xsl:when test="not(negeso:album[@selected='true']) and not(negeso:photo[@selected='true'])">
			<div class="here_am_i">
				<xsl:value-of select="@name" />
			</div>
		</xsl:when>
		<xsl:when test="negeso:photo[@selected='true']">
			<div>
				<a href="?albumId={@id}">
					<xsl:value-of select="@name" />
				</a>
				&#160;&#160;&gt;&gt;&gt;&#160;&#160;
			</div>
			<div class="here_am_i">
				<xsl:value-of select="negeso:photo[@selected='true']/@name" />
			</div>
		</xsl:when>
		<xsl:when test="negeso:album[@selected='true']">
			<div>
				<a href="?albumId={@id}">
					<xsl:value-of select="@name" />
				</a>
				&#160;&#160;&gt;&gt;&gt;&#160;&#160;
			</div>
			<xsl:apply-templates select="negeso:album[@selected='true']" mode="where_am_i" />
		</xsl:when>
	</xsl:choose>
</xsl:template>
<!-- WHERE AM I: END -->

<!--************************************* replace quotes for javaScript variables ************************************* -->
 <xsl:template name="string-replace-all">
    <xsl:param name="text" />
    <xsl:param name="replace" />
    <xsl:param name="by" />
    <xsl:choose>
      <xsl:when test="contains($text, $replace)">
        <xsl:value-of select="substring-before($text,$replace)" />
        <xsl:value-of select="$by" />
        <xsl:call-template name="string-replace-all">
          <xsl:with-param name="text"
          select="substring-after($text,$replace)" />
          <xsl:with-param name="replace" select="$replace" />
          <xsl:with-param name="by" select="$by" />
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$text" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template name="string-replace-quotes">
  		<xsl:param name="text" />
  		<xsl:variable name="quotes">'</xsl:variable>
  		<xsl:variable name="quotes_to">\'</xsl:variable>
  		<xsl:call-template name="string-replace-all">
      		<xsl:with-param name="text" select="$text" />
      		<xsl:with-param name="replace" select="$quotes" />
    		<xsl:with-param name="by" select="$quotes_to" />
 		</xsl:call-template>
  </xsl:template>

<!--========================================== PHOTO ALBUM END =================================-->

</xsl:stylesheet>
