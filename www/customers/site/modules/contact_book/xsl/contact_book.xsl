<?xml version="1.0" encoding="UTF-8"?>
<!--

  Copyright (c) 2003 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential
  Information and shall use it only in accordance with the terms of the
  license agreement you entered into with Negeso.

  Common xsl template for contact book module

  @version		2007.12.25
  @author		Oleg 'germes' Shvedov
  @author		Rostislav 'KOTT' Brizgunov
-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java"
>

<xsl:variable name="dict_contact_book" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('contact_book', $lang)"/>

<xsl:template match="negeso:contact-book" mode="page_head">
	<link rel="stylesheet" type="text/css" href="/site/modules/contact_book/css/contact_book.css"/>
	<script type="text/javascript" src="/site/modules/contact_book/script/contact_book.js">/**/</script>
</xsl:template>
        
<xsl:template name="contact_book" >
<!--  Current tag negeso:contents
		If we have birthdays then we should  show the content in two parts. In another way we'l have only one part.-->
	<div class="b-contactBody">
			<xsl:choose>
				<xsl:when test="negeso:contact-book/negeso:birthday-reminder/negeso:cb-search-results/negeso:contact">
				<div class="b-contactBirthday">
						<xsl:apply-templates select="negeso:contact-book/negeso:birthday-reminder"/>
				</div>
				<div class="b-contactBook">
						<xsl:apply-templates select="negeso:contact-book"/>
				</div>
				</xsl:when>
				<xsl:otherwise>
				<div class="b-contactBook">
				    	<xsl:apply-templates select="negeso:contact-book"/>
			    </div>
				</xsl:otherwise>
			</xsl:choose>
	</div>
</xsl:template>

<xsl:template match="negeso:contact-book" >
	<script language="JavaScript">
		lang = '<xsl:value-of select="$lang"/>';
	</script>
				<!-- main page template with search forms -->
				<xsl:apply-templates select="negeso:contact-form" />

	<!-- view search results -->
	<xsl:if test="not(descendant::negeso:birthday-reminder)">
	    <xsl:choose>
	    	<xsl:when test="//negeso:contact-form[@action='search'] or not(//negeso:contact-form/@action)">
	    		<xsl:apply-templates select="negeso:cb-search-results"/>
	    	</xsl:when>
	    	<xsl:when test="//negeso:contact-form[@action='advanced_search']">
	    		<xsl:choose>
	    			<xsl:when test="$param[@name='job'] and not(//negeso:contact)">
									<xsl:call-template name ="add-constant-info">
										<xsl:with-param name ="dict"  select="$dict_contact_book"/>
										<xsl:with-param name ="name"  select="'NO_MATCHES'"/>
									</xsl:call-template>
									<xsl:value-of select="java:getString($dict_contact_book, 'NO_MATCHES')"/>
	    			</xsl:when>
	    			<xsl:otherwise>
	    				<xsl:apply-templates select="negeso:cb-search-results"/>
	    			</xsl:otherwise>
	    		</xsl:choose>
	    	</xsl:when>
	    	<xsl:otherwise>
	    		<xsl:apply-templates select="negeso:cb-search-results"/>
	    	</xsl:otherwise>
	    </xsl:choose>
	</xsl:if>

	<xsl:apply-templates select="negeso:cb-group"/>
	
	<!-- view contact details -->
	<xsl:apply-templates select="negeso:contact"/>
</xsl:template>

<xsl:template match="negeso:contact-form">
	<form method="get" action="" name="cb_contact_form" class="b-contactForm">
		<input type="hidden" name="action" value="{@action}"/>
		<input type="hidden" name="contactId" value="{@contact-id}"/>
		<input type="hidden" name="groupId" value="{@group-id}"/>
		<input type="hidden" name="order_by" value="{@order-by}"/>
		<input type="hidden" name="order" value="{@order}"/>
		<input type="hidden" name="first_name" value="{@first-name}"/>
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_contact_book"/>
							<xsl:with-param name ="name"  select="'CB_CONTACTBOOK'"/>
						</xsl:call-template>
						<xsl:if test=" ($outputType = 'admin') and (position() = 1) and not(/negeso:page/@role-id = 'visitor')">
							<img src="/images/mark_1.gif" align="absMiddle" class="hand" onClick="window.open('contact_book', '_blank', 'top=50,left=50,height=600,width=800,status=0,toolbar=0,menubar=0,location=0,resizable=1,scrollbars=yes')"/>
							<xsl:text>&#160;</xsl:text>
						</xsl:if>
								<xsl:call-template name="page_title">
			<xsl:with-param name="title">
						<xsl:value-of select="java:getString($dict_contact_book, 'CB_CONTACTBOOK')"/>
			</xsl:with-param>
		</xsl:call-template>
		
		<xsl:choose>
			<xsl:when test="@action='advanced_search'">
		    	<xsl:call-template name="cb_advanced_search" />
		    </xsl:when>
		    <xsl:otherwise>
		    	<xsl:call-template name="cb_navigation" />
		    </xsl:otherwise>
		</xsl:choose>
	</form>
</xsl:template>

<xsl:template name="cb_advanced_search" >
	<!-- current tag negeso:contact-form
		  Advanced search form template
	 -->
	<table cellpadding="0" cellspacing="0" border="0">
	    <tr>
	        <th>
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_contact_book"/>
					<xsl:with-param name ="name"  select="'CB_FNAME'"/>
				</xsl:call-template>
        		<xsl:value-of select="java:getString($dict_contact_book, 'CB_FNAME')"/>
	        </th>
	        <td>
        	 	<input required_at_least="true" class="text" type="text" name="fname" value="{@first-name}"/>
	        </td>
	    </tr>
	    <tr>
	        <th>
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_contact_book"/>
					<xsl:with-param name ="name"  select="'CB_SNAME'"/>
				</xsl:call-template>
        		<xsl:value-of select="java:getString($dict_contact_book, 'CB_SNAME')"/>
	        </th>
	        <td>
        		<input required_at_least="true" class="text" type="text" name="sname" value="{@second-name}"/>
	        </td>
	    </tr>
	    <tr>
	        <th>
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_contact_book"/>
					<xsl:with-param name ="name"  select="'CB_EMAIL'"/>
				</xsl:call-template>
        		<xsl:value-of select="java:getString($dict_contact_book, 'CB_EMAIL')"/>
	        </th>
	        <td>
        	 	<input required_at_least="true" class="text" type="text" name="email" value="{@email}"/>
	       	</td>
	    </tr>
	    <tr>
	        <th>
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_contact_book"/>
					<xsl:with-param name ="name"  select="'CB_DEPARTMENT'"/>
				</xsl:call-template>
        		<xsl:value-of select="java:getString($dict_contact_book, 'CB_DEPARTMENT')"/>
	        </th>
	        <td>
        	 	<input required_at_least="true" class="text" type="text" name="dep" value="{@department}"/>
	       	</td>
	    </tr>
	    <tr>
	        <th>
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_contact_book"/>
					<xsl:with-param name ="name"  select="'CB_JOBTITLE'"/>
				</xsl:call-template>
        		<xsl:value-of select="java:getString($dict_contact_book, 'CB_JOBTITLE')"/>
	        </th>
	        <td>
        	 	<input required_at_least="true" class="text" type="text" name="job" value="{@job}"/>
	       	</td>
	    </tr>
		<tr>
			<th colspan="2">
				<input type="button" class="submit" onClick="javascript:mainPage()">
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_contact_book"/>
						<xsl:with-param name ="name"  select="'CB_BACK'"/>
					</xsl:call-template>
					<xsl:attribute name="value">
						<xsl:value-of select="java:getString($dict_contact_book, 'CB_BACK')"/>
					</xsl:attribute>
				</input>
				
				<input class="submit cbMarginLeft_10" type="button" onClick="advancedSearch();">
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_contact_book"/>
						<xsl:with-param name ="name"  select="'CB_SEARCH'"/>
					</xsl:call-template>
					<xsl:attribute name="value">
						<xsl:value-of select="java:getString($dict_contact_book, 'CB_SEARCH')"/>
					</xsl:attribute>
				</input>
			</th>
		</tr>
	</table>
</xsl:template>

<xsl:template name="cb_navigation" >
	<!-- current tag negeso:contact-form -->
	<table border="0" cellpadding="0" cellspacing="0" class="b-contactNavigation">
	    <tr>
	        <th>
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_contact_book"/>
					<xsl:with-param name ="name"  select="'CB_SEARCH'"/>
				</xsl:call-template>
	        	<xsl:value-of select="java:getString($dict_contact_book, 'CB_SEARCH')"/>
	        </th>
	        <td>
        		<input class="text" type="text" name="search_string" value="{@search-string}"/>&#160;
        	 	<input onclick="searchContacts();" class="submit" type="button">
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_contact_book"/>
						<xsl:with-param name ="name"  select="'CB_GO'"/>
					</xsl:call-template>
					
        	 		 <xsl:attribute name="value">
	        	 		 <xsl:value-of select="java:getString($dict_contact_book, 'CB_GO')"/>
        	 		 </xsl:attribute>
        	 	</input>&#160;
        	 	<input type="button" class="submit" onclick="showAdvancedSearch();">
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_contact_book"/>
						<xsl:with-param name ="name"  select="'CB_ADVSEARCH'"/>
					</xsl:call-template>
					
        	 		<xsl:attribute name="value">
	        	 		<xsl:value-of select="java:getString($dict_contact_book, 'CB_ADVSEARCH')"/>
	        	 	</xsl:attribute>
        	 	</input>
	        </td>
	    </tr>
	    <tr>
	        <th>
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_contact_book"/>
					<xsl:with-param name ="name"  select="'CB_BYGROUP'"/>
				</xsl:call-template>

				<xsl:value-of select="java:getString($dict_contact_book, 'CB_BYGROUP')"/>:
	        </th>
	        <td>
        		<!-- View CB groups as links -->
	        	<xsl:for-each select="negeso:cb-groups">
					<xsl:for-each select="negeso:cb-group">
						<a href="javascript:groupContacts({@id})"><xsl:value-of select="@title"/></a>&#160;
					</xsl:for-each>
	        	</xsl:for-each>
	        	<a href="javascript:showAll()">
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_contact_book"/>
						<xsl:with-param name ="name"  select="'CB_SHOWALL'"/>
					</xsl:call-template>

					<xsl:value-of select="java:getString($dict_contact_book, 'CB_SHOWALL')"/>
	        	</a>
	        </td>
	    </tr>
	</table>
</xsl:template>

<xsl:template match="negeso:contact">
	<!-- view contact details -->
	<br/>
	<table cellpadding="0" cellspacing="10" border="0" class="b-contactDetails">
		<tr>
		    <th class="b-contactDetails-Title" colspan="2">
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_contact_book"/>
					<xsl:with-param name ="name"  select="'CB_CONTACT'"/>
				</xsl:call-template>

				<xsl:value-of select="java:getString($dict_contact_book, 'CB_CONTACT')"/>
			</th>
		</tr>
		<xsl:if test="@image-link and @image-link != ''">
			<tr>
		    	<th colspan="2">
		     		<img class="cbDetailsImg" src="{@image-link}" />
		     	</th>
			</tr>
		</xsl:if>
		<tr>
		    <th>
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_contact_book"/>
					<xsl:with-param name ="name"  select="'CB_NAME'"/>
				</xsl:call-template>
    			<!-- checking for image for viewing correct table formatting -->
				<xsl:value-of select="java:getString($dict_contact_book, 'CB_NAME')"/>
			</th>
			<td>
				<!-- checking for image for viewing correct table formatting -->
				<xsl:value-of select="@title"/>
			</td>
		</tr>
		<tr>
			<th>
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_contact_book"/>
					<xsl:with-param name ="name"  select="'CB_BIRTHDAY'"/>
				</xsl:call-template>
				<xsl:value-of select="java:getString($dict_contact_book, 'CB_BIRTHDAY')"/>
			</th>
			<td><xsl:value-of select="@birthday"/></td>
		</tr>
		<xsl:if test="@email" >
			<tr>
				<th>
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_contact_book"/>
						<xsl:with-param name ="name"  select="'CB_EMAIL'"/>
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_contact_book, 'CB_EMAIL')"/>
				</th>
				<td><xsl:value-of select="@email"/></td>
			</tr>
		</xsl:if>
		<xsl:if test="@department" >
			<tr>
				<th>
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_contact_book"/>
						<xsl:with-param name ="name"  select="'CB_DEPARTMENT'"/>
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_contact_book, 'CB_DEPARTMENT')"/>
				</th>
				<td><xsl:value-of select="@department"/></td>
			</tr>
		</xsl:if>
		<tr>
			<th>
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_contact_book"/>
					<xsl:with-param name ="name"  select="'CB_JOBTITLE'"/>
				</xsl:call-template>
     			<xsl:value-of select="java:getString($dict_contact_book, 'CB_JOBTITLE')"/>
			</th>
			<td><xsl:value-of select="@job-title"/></td>
		</tr>
		<xsl:if test="@phone">
		<tr>
			<th>
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_contact_book"/>
					<xsl:with-param name ="name"  select="'CB_PHONE'"/>
				</xsl:call-template>
				<xsl:value-of select="java:getString($dict_contact_book, 'CB_PHONE')"/>
			</th>
			<td><xsl:value-of select="@phone"/></td>
		</tr>
		</xsl:if>
		<xsl:if test="@web-link">
			<tr>
				<th>
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_contact_book"/>
						<xsl:with-param name ="name"  select="'CB_WEBADDRESS'"/>
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_contact_book, 'CB_WEBADDRESS')"/>
				</th>
				<td><xsl:value-of select="@web-link"/></td>
			</tr>
		</xsl:if>
    </table>
</xsl:template>

<xsl:template match="negeso:cb-search-results">
	<br/>
	<xsl:call-template name="cb_render_contact_group"/>
</xsl:template>

<xsl:template match="negeso:cb-group">
	<xsl:call-template name="cb_render_contact_group"/>
</xsl:template>

<xsl:template name="cb_render_contact_group">
	<!-- current tag negeso:cb-search-results -->
    <xsl:choose>
	    <xsl:when test="count(negeso:contact) > 0">
		    <table border="0" cellpadding="0" cellspacing="10" class="b-contactSearchResult">
				<xsl:call-template name="cb_contact_head" />
				<xsl:apply-templates select="negeso:contact" mode="list" />
	    	</table>
	    </xsl:when>
	    <xsl:otherwise>
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_contact_book"/>
							<xsl:with-param name ="name"  select="'NO_MATCHES'"/>
						</xsl:call-template>
				    	<xsl:value-of select="java:getString($dict_contact_book, 'NO_MATCHES')"/>
	    </xsl:otherwise>
    </xsl:choose>
</xsl:template>

<xsl:template name="cb_contact_head">
	<!-- current tag negeso:cb-search-results -->
	<tr>
		<th nowrap="nowrap">
			<xsl:call-template name ="add-constant-info">
				<xsl:with-param name ="dict"  select="$dict_contact_book"/>
				<xsl:with-param name ="name"  select="'CB_IMAGE'"/>
			</xsl:call-template>
			<xsl:value-of select="java:getString($dict_contact_book, 'CB_IMAGE')"/>
		</th>
		<th nowrap="nowrap">
			<xsl:call-template name ="add-constant-info">
				<xsl:with-param name ="dict"  select="$dict_contact_book"/>
				<xsl:with-param name ="name"  select="'CB_NAME'"/>
			</xsl:call-template>
			<xsl:value-of select="java:getString($dict_contact_book, 'CB_NAME')"/>&#160;
			<a href="javascript:reorder('first_name, second_name','up')" onfocus="blur()">
				<img src="/site/modules/contact_book/images/up_white.gif" alt="up" />
			</a>
			<a href="javascript:reorder('first_name, second_name','down')" onfocus="blur()">
				<img src="/site/modules/contact_book/images/down_white.gif" alt="down" />
			</a>
		</th>
		<th nowrap="nowrap">
			<xsl:call-template name ="add-constant-info">
				<xsl:with-param name ="dict"  select="$dict_contact_book"/>
				<xsl:with-param name ="name"  select="'CB_BIRTHDAY'"/>
			</xsl:call-template>
			<xsl:value-of select="java:getString($dict_contact_book, 'CB_BIRTHDAY')"/>&#160;
			<a href="javascript:reorder('birth_date','up')" onfocus="blur()">
				<img src="/site/modules/contact_book/images/up_white.gif" alt="up" />
			</a>
			<a href="javascript:reorder('birth_date','down')" onfocus="blur()">
				<img src="/site/modules/contact_book/images/down_white.gif" alt="down" />
			</a>
		</th>
		<th nowrap="nowrap">
			<xsl:call-template name ="add-constant-info">
				<xsl:with-param name ="dict"  select="$dict_contact_book"/>
				<xsl:with-param name ="name"  select="'CB_EMAIL'"/>
			</xsl:call-template>
			<xsl:value-of select="java:getString($dict_contact_book, 'CB_EMAIL')"/>&#160;
			<a href="javascript:reorder('email','up')" onfocus="blur()">
				<img src="/site/modules/contact_book/images/up_white.gif" alt="up" />
			</a>
			<a href="javascript:reorder('email','down')" onfocus="blur()">
				<img src="/site/modules/contact_book/images/down_white.gif" alt="down" />
			</a>
		</th>
		<th nowrap="nowrap">
			<xsl:call-template name ="add-constant-info">
				<xsl:with-param name ="dict"  select="$dict_contact_book"/>
				<xsl:with-param name ="name"  select="'CB_DEPARTMENT'"/>
			</xsl:call-template>
			<xsl:value-of select="java:getString($dict_contact_book, 'CB_DEPARTMENT')"/>&#160;
			<a href="javascript:reorder('department','up')" onfocus="blur()">
				<img src="/site/modules/contact_book/images/up_white.gif" alt="up" />
			</a>
			<a href="javascript:reorder('department','down')" onfocus="blur()">
				<img src="/site/modules/contact_book/images/down_white.gif" alt="down" />
			</a>
		</th>
		<th nowrap="nowrap">
			<xsl:call-template name ="add-constant-info">
				<xsl:with-param name ="dict"  select="$dict_contact_book"/>
				<xsl:with-param name ="name"  select="'CB_JOBTITLE'"/>
			</xsl:call-template>
			<xsl:value-of select="java:getString($dict_contact_book, 'CB_JOBTITLE')"/>&#160;
			<a href="javascript:reorder('job_title','up')" onfocus="blur()">
				<img src="/site/modules/contact_book/images/up_white.gif" alt="up" />
			</a>
			<a href="javascript:reorder('job_title','down')" onfocus="blur()">
				<img src="/site/modules/contact_book/images/down_white.gif" alt="down" />
			</a>
		</th>
	</tr>
</xsl:template>

<xsl:template match="negeso:contact" mode="list">
    <tr class="b-contactHead">
        <td>
        	<xsl:choose>
	            <xsl:when test="@image-link and @image-link != ''">	
					<a onfocus="blur()">
						<xsl:attribute name="href">
							<xsl:call-template name="cb_link" />
							<xsl:text>?action=show_contact&amp;contactId=</xsl:text>
							<xsl:value-of select="@id" />
						</xsl:attribute>
						<img class="cbimg" src="{@image-link}"/>
					</a>
	            </xsl:when>
	            <xsl:otherwise>
	            	<xsl:text>&#160;</xsl:text>
	            </xsl:otherwise>
            </xsl:choose>
        </td>
        <td>
			<a onfocus="blur()">
				<xsl:attribute name="href">
					<xsl:call-template name="cb_link" />
					<xsl:text>?action=show_contact&amp;contactId=</xsl:text>
					<xsl:value-of select="@id" />
				</xsl:attribute>
				<xsl:if test="@title and @title!=''"><xsl:value-of select="@title"/></xsl:if><br/>
			</a>
        </td>
        <td>
            <xsl:if test="@birthday and @birthday!=''">
            	<xsl:call-template name="cb_format_birthday" />
            </xsl:if><br/>
        </td>
        <td>
            <xsl:if test="@email and @email!=''"><xsl:value-of select="@email"/></xsl:if><br/>
        </td>
        <td>
            <xsl:if test="@department and @department!=''"><xsl:value-of select="@department"/></xsl:if><br/>
        </td>
        <td>
            <xsl:value-of select="@job-title"/><br/>
        </td>
    </tr>
</xsl:template>

<xsl:template match="negeso:birthday-reminder">
	<div>
		<xsl:call-template name ="add-constant-info">
			<xsl:with-param name ="dict"  select="$dict_contact_book"/>
			<xsl:with-param name ="name"  select="'CB_NEXT_BIRTHDAYS'"/>
		</xsl:call-template>
	</div>
		<xsl:call-template name="page_title">
			<xsl:with-param name="title">
				<xsl:value-of select="java:getString($dict_contact_book, 'CB_NEXT_BIRTHDAYS')"/>
			</xsl:with-param>
		</xsl:call-template>
	
	<table class="b-contactBirthdayDetails" border="0">
		<xsl:apply-templates select="negeso:cb-search-results/negeso:contact" mode="birthday" />
	</table>
</xsl:template>

<xsl:template match="negeso:contact" mode="birthday">
	<tr>
		<td>
			<div>
				<xsl:value-of select="@birthday"/>
			</div>
			<a onfocus="blur()">
				<xsl:attribute name="href">
					<xsl:call-template name="cb_link" />
					<xsl:text>?action=show_contact&amp;contactId=</xsl:text>
					<xsl:value-of select="@id" />
				</xsl:attribute>
				<xsl:value-of select="@title"/>
			</a>			
		</td>
	</tr>
</xsl:template>

<!-- Formatting date: START -->
<xsl:template name="cb_format_birthday">
	<xsl:variable name="year" select="substring(@birthday,1,4)" />
	<xsl:variable name="month" select="substring(@birthday,6,2)" />
	<xsl:variable name="day" select="substring(@birthday,9,2)" />
	<xsl:value-of select="@birthday" />
	<xsl:if test="not($month = 'not date') and (1 = 2)">
		<xsl:value-of select="$day" />-<xsl:value-of select="$month" />-<xsl:value-of select="$year" />
	</xsl:if>
</xsl:template>
<!-- Formatting date: END -->

<xsl:template name="cb_link">
	<xsl:text>contact_book_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>
</xsl:template>
</xsl:stylesheet>