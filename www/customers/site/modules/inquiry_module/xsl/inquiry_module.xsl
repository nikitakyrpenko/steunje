<?xml version="1.0" encoding="UTF-8"?>
<!--
  Copyright (c) 2008 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information"). You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.


  XSL templates for Photo Album
 
  @version		2008.01.11
  @author		Rostislav 'KOTT' Brizgunov

-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">

<xsl:variable name="dict_inquiry_module" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('inquiry_module', $lang)"/>

<xsl:template match="negeso:inquiry" mode="page_head">
		<link rel="stylesheet" type="text/css" href="/site/modules/inquiry_module/css/inquiry_module.css"/>
		<script type="text/javascript" src="/site/modules/inquiry_module/script/inquiry.js">/**/</script>
</xsl:template>

<xsl:template match="negeso:inquiry" mode="inq">
    <div class="b-inquiryMain">
		<h1><xsl:call-template name="inq_title" /></h1>
								<xsl:call-template name="inq_body" />
				</div>
</xsl:template>

<!--======== Inquiry Module title line: Begin ========-->
<xsl:template name="inq_title">
	<xsl:call-template name ="add-constant-info">
		<xsl:with-param name ="dict"  select="$dict_inquiry_module"/>
		<xsl:with-param name ="name"  select="'INQ_MODULE_TITLE'"/>
	</xsl:call-template>
	<xsl:if	test="$outputType =	'admin' and not(/negeso:page/@role-id = 'visitor')">
		<img alt="" align="absMiddle" class="hand" src="/images/mark_1.gif" onClick="window.open('inquiry', '_blank', 'top=50,left=50,height=600,width=800,status=0,toolbar=0,menubar=0,location=0,resizable=1,scrollbars=yes')"/>
		&#160;
	</xsl:if>
	<xsl:value-of select="java:getString($dict_inquiry_module, 'INQ_MODULE_TITLE')"/>
	<xsl:choose>
		<xsl:when test="@status='access_denied'">
			<div>
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_inquiry_module"/>
					<xsl:with-param name ="name"  select="'INQ_MODULE_TITLE'"/>
				</xsl:call-template>
				<xsl:text>:&#160;</xsl:text>
				<xsl:value-of select="java:getString($dict_inquiry_module, 'INQ_MUST_LOGIN')"/>
			</div>
		</xsl:when>
		<xsl:otherwise>
			<xsl:apply-templates select="negeso:inquiry_user" mode="inq_title" />
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template match="negeso:inquiry_user" mode="inq_title">
	<xsl:call-template name ="add-constant-info">
		<xsl:with-param name ="dict"  select="$dict_inquiry_module"/>
		<xsl:with-param name ="name"  select="'INQ_USER'"/>
	</xsl:call-template>
	<xsl:text>&#160;&#160;&#160;</xsl:text><xsl:value-of select="java:getString($dict_inquiry_module, 'INQ_USER')"/>: <xsl:value-of	select="@email"/>
</xsl:template>
<!--========= Inquiry Module title line: End =========-->

<!--======= Inquiry Module body contents: Begin ======-->
<xsl:template name="inq_body">
	<!-- Current XML node: negeso:inquiry -->
	<xsl:choose>
		<xsl:when test="@status='access_denied'">
			<form name="inqLoginForm" method="post" enctype="multipart/form-data">
				<input type="hidden" name="mode" value="questionnaire"/>
				<input type="hidden" name="inquiry_questionnaire_id" value="{@quiz_id}"/>
				<xsl:call-template name="inq_show_login_form" />
			</form>
		</xsl:when>
		<xsl:when test="@status='questionnaire_list' and count(negeso:questionnaire) = 0">
			<div>
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_inquiry_module"/>
					<xsl:with-param name ="name"  select="'INQ_NO_ACTIVE_QUESTIONNAIRE'"/>
				</xsl:call-template>
			<xsl:value-of select="java:getString($dict_inquiry_module, 'INQ_NO_ACTIVE_QUESTIONNAIRE')"/>
			</div>
		</xsl:when>
		<xsl:when test="@status='questionnaire_list' and count(negeso:questionnaire) &gt; 0">
			<xsl:call-template name="inq_show_questionnaire_list" />
		</xsl:when>
		<xsl:otherwise>
			<xsl:apply-templates select="negeso:questionnaire" />
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<!--========== Inquiry Login Form: Begin ==========-->
<xsl:template name="inq_show_login_form">
	<!-- current XML tag is negeso:inquiry -->
	<table cellspacing="0" cellpadding="0" border="0" class="inqLoginForm">
		<tr>
			<th class="inqBorderBottom">
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_inquiry_module"/>
					<xsl:with-param name ="name"  select="'INQ_EMAIL'"/>
				</xsl:call-template>
				<xsl:value-of select="java:getString($dict_inquiry_module, 'INQ_EMAIL')"/>:</th>
			<td class="inqBorderBottom"><input class="inqWidth_200" name="inquiry_email" required="true" is_email="true" /></td>
		</tr>
		<tr>
			<th class="inqBorderBottom">
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_inquiry_module"/>
					<xsl:with-param name ="name"  select="'INQ_PASSWORD'"/>
				</xsl:call-template>
				<xsl:value-of select="java:getString($dict_inquiry_module, 'INQ_PASSWORD')"/>:</th>
			<td class="inqBorderBottom"><input class="inqWidth_200" type="password" name="inquiry_password"/></td>
		</tr>
		<tr>
			<th>&#160;</th>
			<td>
				<input type="button"
					class="submit hand"
					onclick="if (validate(this.form)) this.form.submit();"
					value="{java:getString($dict_inquiry_module, 'INQ_SUBMIT')}" >
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_inquiry_module"/>
						<xsl:with-param name ="name"  select="'INQ_SUBMIT'"/>
					</xsl:call-template>
				</input>
			</td>
		</tr>
	</table>
</xsl:template>
<!--=========== Inquiry Login Form: End ===========-->

<!--===== Inquiry questions titles list: Begin ====-->
<xsl:template name="inq_show_questionnaire_list">
	<!-- Current XML node: negeso:inquiry -->
	<h2>
		<xsl:call-template name ="add-constant-info">
			<xsl:with-param name ="dict"  select="$dict_inquiry_module"/>
			<xsl:with-param name ="name"  select="'INQ_SELECT_QUESTIONNAIRE'"/>
		</xsl:call-template>
		<xsl:value-of select="java:getString($dict_inquiry_module, 'INQ_SELECT_QUESTIONNAIRE')"/>
	</h2>
	<ul class="b-inquiryList">
		<xsl:apply-templates select="negeso:questionnaire" mode="inq_question_list_item" />
	</ul>
	<br/>
</xsl:template>

<xsl:template match="negeso:questionnaire" mode="inq_question_list_item">
	<li><a href="?inquiry_questionnaire_id={@id}&amp;mode=questionnaire" onfocus="blur()"><xsl:value-of select="@title"/></a></li>
</xsl:template>
<!--===== Inquiry questions titles list: End ======-->

<!--========= Inquiry questionnaire: Begin ========-->
<xsl:template match="negeso:questionnaire[../@status='blank' or	../@status='incomplete'	or ../@status='storage_error']">
    <br/>
	<form name="inqForm" method="get" class="b-inquiryForm">

		<input type="hidden" name="mode" value="questionnaire"/>
		<input type="hidden" name="inquiry_questionnaire_id" value="{@id}"/>
		<input type="hidden" name="inquiry_submit" value="true"/>

		<!-- Inquiry errors -->
		<xsl:call-template name="inq_answer_error" />

		<div class="b-inquiryStep">
			<h2>
				<xsl:value-of select="@title"/>
			<xsl:if	test="(@multipage =	'true')	and	count(negeso:question)">
				<xsl:text>:&#160;</xsl:text>
				<input type="hidden" name="inquiry_question_id"	value="{negeso:question/@id}"/>
				<span>
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_inquiry_module"/>
						<xsl:with-param name ="name"  select="'INQ_QUESTION'"/>
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_inquiry_module, 'INQ_QUESTION')"/>
					<xsl:text>&#160;</xsl:text>
				</span>
				<xsl:value-of select="negeso:question/@position"/><xsl:text>&#160;</xsl:text>
				<span>
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_inquiry_module"/>
						<xsl:with-param name ="name"  select="'INQ_OF_TOTAL'"/>
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_inquiry_module, 'INQ_OF_TOTAL')"/>
				</span>
				<xsl:text>&#160;</xsl:text>
				<xsl:value-of select="@total_questions"/>
			</xsl:if>
		</h2>
		</div>
		
		<br/>

		<!-- Inquiry introduction article -->
		<xsl:if test="@multipage != 'true' or negeso:question/@position	= 1">
			<xsl:call-template name="inq_print_intro"/>
		</xsl:if>

		<br/>
		
		<!-- Show question(s) -->
		<xsl:apply-templates select="negeso:question" />
		
		<br/>
		
		<!-- show submit button -->
		<div>
			<xsl:call-template name="inq_submit_button" />
		</div>
		
	</form>
	
</xsl:template>

<xsl:template name="inq_print_intro">
	<!-- current XML tag is negeso:questionnaire -->
	<div class="b-inquiryIntro">
		<xsl:value-of select="negeso:intro/text()" disable-output-escaping="yes"/>
	</div>
</xsl:template>

<xsl:template name="inq_answer_error">
	<!-- current XML tag is negeso:questionnaire -->
	<xsl:if	test="../@status = 'incomplete' or ../@status = 'storage_error'">
		<div class="red">
			<xsl:choose>
				<xsl:when test="../@status = 'incomplete' and @multipage = 'true'">
					<span>
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_inquiry_module"/>
							<xsl:with-param name ="name"  select="'INQ_ANSWER_REQUIRED_QUESTION'"/>
						</xsl:call-template>
						<xsl:value-of select="java:getString($dict_inquiry_module, 'INQ_ANSWER_REQUIRED_QUESTION')"/>
					</span>
				</xsl:when>
				<xsl:when test="../@status = 'incomplete' and @multipage != 'true'">
					<span>
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_inquiry_module"/>
							<xsl:with-param name ="name"  select="'INQ_ANSWER_ALL_QUESTIONS'"/>
						</xsl:call-template>
						<xsl:value-of select="java:getString($dict_inquiry_module, 'INQ_ANSWER_ALL_QUESTIONS')"/>
					</span>
				</xsl:when>
				<xsl:when test="../@status = 'storage_error'">
					<span>
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_inquiry_module"/>
							<xsl:with-param name ="name"  select="'INQ_CANNOT_SAVE_ANSWERS'"/>
						</xsl:call-template>
						<xsl:value-of select="java:getString($dict_inquiry_module, 'INQ_CANNOT_SAVE_ANSWERS')"/>
					</span>
				</xsl:when>
			</xsl:choose>
		</div>
	</xsl:if>
</xsl:template>

<xsl:template name="inq_submit_button">
	<!-- current XML tag is negeso:questionnaire -->
	<xsl:if	test="@multipage = 'true' and negeso:question/@position	!= 1">
		<input type="submit" class="submit" name="inquiry_previous_question">
			<xsl:call-template name ="add-constant-info">
				<xsl:with-param name ="dict"  select="$dict_inquiry_module"/>
				<xsl:with-param name ="name"  select="'INQ_BACK'"/>
			</xsl:call-template>
			<xsl:attribute name="value"><xsl:value-of select="java:getString($dict_inquiry_module, 'INQ_BACK')"/></xsl:attribute>
		</input>&#160;
	</xsl:if>
	<input type="submit" class="submit">

		<xsl:choose>
			<xsl:when test="@multipage != 'true' or negeso:question/@position = @total_questions">
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_inquiry_module"/>
					<xsl:with-param name ="name"  select="'INQ_SUBMIT'"/>
				</xsl:call-template>
				<xsl:attribute name="value">
					<xsl:value-of select="java:getString($dict_inquiry_module, 'INQ_SUBMIT')"/>
				</xsl:attribute>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_inquiry_module"/>
					<xsl:with-param name ="name"  select="'INQ_NEXT'"/>
				</xsl:call-template>
				<xsl:attribute name="value">
					<xsl:value-of select="java:getString($dict_inquiry_module, 'INQ_NEXT')"/>
				</xsl:attribute>
			</xsl:otherwise>
		</xsl:choose>
	</input>
</xsl:template>

<xsl:template match="negeso:question">
	<!-- Question title -->
	<table cellpadding="3" cellspacing="0" border="0" class="b-inquiryQuest">
		<tr>
			<td>
				<xsl:value-of select="@title"/>
				<xsl:if	test="@required='true'"><span class="red bold">*</span></xsl:if>
				<xsl:text>:</xsl:text>
			</td>
		</tr>
	</table>
	<!-- Answer options -->
	<table cellpadding="0" cellspacing="0" border="0" class="b-inquiryAnswer">
		<tr>
			<th>
				<xsl:value-of select="@explanation"/>
			</th>
			<td>
				<xsl:call-template name="inq_show_questions_fields" />
			</td>
		</tr>
	</table>
</xsl:template>

<xsl:template name="inq_show_questions_fields">
	<!-- current XML tag is negeso:question -->
	<xsl:variable name="paramName" select="concat('inquiry_question_', @id)"/>
	<xsl:choose>
		
		<!-- Radiobuttons -->
		<xsl:when test="@type='radio'">
			<xsl:for-each select="negeso:option">
				<input class="form_radio" type="radio" name="{$paramName}" value="{@id}" onClick="switchAlternativeAnswerOnOff(this, 'inquiry_alternative_{../@id}')">
					<xsl:if	test="@checked='true'"><xsl:attribute name="checked"/></xsl:if>
				</input>&#160;<xsl:value-of select="@title"/>&#160;
				<xsl:if	test="../@options_layout='vertical'">
					<br/>
				</xsl:if>
			</xsl:for-each>
			<xsl:if	test="string-length(@alternative) != 0">
				<input class="form_radio" type="radio" name="{$paramName}" value="-1" onClick="switchAlternativeAnswerOnOff(this, 'inquiry_alternative_{@id}')">
					<xsl:if	test="string-length(@alternative_answer) !=	0"><xsl:attribute name="checked"/></xsl:if>
				</input>
				<xsl:text>&#160;</xsl:text>
			</xsl:if> 
		</xsl:when>
		
		<!-- Checkboxes -->
		<xsl:when test="@type='checkbox'">
			<xsl:for-each select="negeso:option">
				<input class="form_checkbox" type="checkbox" name="{$paramName}" value="{@id}" onClick="switchAlternativeAnswerOnOff(this, 'inquiry_alternative_{../@id}')">
					<xsl:if	test="@checked='true'"><xsl:attribute name="checked"/></xsl:if>
				</input>&#160;<xsl:value-of select="@title"/>&#160;&#160;
				<xsl:if	test="../@options_layout='vertical'">
					<br/>
				</xsl:if>
			</xsl:for-each>
			<xsl:if	test="string-length(@alternative) != 0">
				<input class="form_checkbox" type="checkbox" name="{$paramName}" value="-1" onClick="switchAlternativeAnswerOnOff(this, 'inquiry_alternative_{@id}')">
					<xsl:if	test="string-length(@alternative_answer) !=	0"><xsl:attribute name="checked"/></xsl:if>
				</input>
				<xsl:text>&#160;</xsl:text>
			</xsl:if> 
		</xsl:when>
		
		<!-- Dropdowns -->
		<xsl:when test="@type='dropdown'">
			<select class="b-inquirySelect" name="{$paramName}" onchange="switchAlternativeAnswerOnOff(this, 'inquiry_alternative_{@id}')">
				<xsl:for-each select="negeso:option">
					<option	value="{@id}">
						<xsl:if	test="@checked='true'"><xsl:attribute name="selected"/></xsl:if>
						<xsl:value-of select="@title"/>
					</option>
				</xsl:for-each>
				<xsl:if	test="string-length(@alternative) != 0">
					<option	value="-1">
						<xsl:if	test="string-length(@alternative_answer) !=	0"><xsl:attribute name="selected"/></xsl:if>
						<xsl:value-of select="@alternative"/>
					</option>
				</xsl:if>
			</select>
		</xsl:when>
		
		<!-- Input Text field -->
		<xsl:when test="@type='text'">
			<input type="text" class="text" name="{$paramName}" value="{@answer}" onkeyup="switchAlternativeAnswerOnOff(this, 'inquiry_alternative_{@id}')"/>
			<xsl:text>&#160;</xsl:text>
		</xsl:when>
		
		<!-- Textarea field -->
		<xsl:when test="@type='textarea'">
			<textarea name="{$paramName}" class="textarea" onkeyup="switchAlternativeAnswerOnOff(this, 'inquiry_alternative_{@id}')">
				<xsl:value-of select="@answer"/>
			</textarea>
			<xsl:text>&#160;</xsl:text>
		</xsl:when>
		
		<xsl:otherwise>UNKNOWN QUESTION	TYPE</xsl:otherwise>
		
	</xsl:choose>
	
	<!-- If there is an alternative variant of answer enabled -->
	<xsl:if	test="(@type='radio' or @type='checkbox' or @type='dropdown' or @type='textarea' or @type='text') and string-length(@alternative) != 0">
		
		<xsl:if test="@type != 'radio' and @type != 'checkbox'">
			<xsl:call-template name="inq_if_vertical" />
		</xsl:if>
		<xsl:if test="@type!='dropdown'">
			<xsl:value-of select="@alternative"/>:
		</xsl:if>
		<xsl:if test="@type!='dropdown' and @type != 'radio' and @type != 'checkbox'">
			<xsl:call-template name="inq_if_vertical" />
		</xsl:if>
		
		<xsl:choose>
			<xsl:when test="@aoMultiline='true'">
				<xsl:call-template name="inq_alter_textarea" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="inq_alter_input" />
			</xsl:otherwise>
		</xsl:choose>
		
		<!-- Enable/disable alternative answer field -->
		<script type="text/javascript">
			try {
				var elm = document.forms["inqForm"].elements["<xsl:call-template name="escapeQuote"><xsl:with-param name="string" select="$paramName" /></xsl:call-template>"];
				if (typeof(elm.tagName) == 'undefined' &amp;&amp; typeof(elm.length) != 'undefined')
					elm = elm[0];
				if (elm)
					switchAlternativeAnswerOnOff(elm, 'inquiry_alternative_<xsl:value-of select="@id" />');
			} catch(e) {}
		</script>
		
	</xsl:if>
	
	<!-- If there is remarks enabled -->
	<xsl:if	test="@allowRemark = 'true'">
		<br/>
		<span>
			<xsl:call-template name ="add-constant-info">
				<xsl:with-param name ="dict"  select="$dict_inquiry_module"/>
				<xsl:with-param name ="name"  select="'INQ_REMARK'"/>
			</xsl:call-template>
			<xsl:value-of select="java:getString($dict_inquiry_module, 'INQ_REMARK')"/>:<xsl:call-template name="inq_if_vertical" />
		</span>

		<xsl:choose>
			<xsl:when test="../@rfMultiline='true'">
				<textarea name="inquiry_remark_{@id}" style="height: {../@rfHeight}px; width: {../@rfWidth}px;"><xsl:value-of select="@remark"/></textarea>
			</xsl:when>
			<xsl:otherwise>
				<input name="inquiry_remark_{@id}" type="text" style="height: {../@rfHeight}px;	width: {../@rfWidth}px;" value="{@remark}"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:if>
	
</xsl:template>

<xsl:template name="inq_alter_input">
	<!-- current XML tag is negeso:question -->
	<input type="text" name="inquiry_alternative_{@id}"	value="{@alternative_answer}" style="height: {@aoHeight}px;	width: {@aoWidth}px;" />
</xsl:template>

<xsl:template name="inq_alter_textarea">
	<!-- current XML tag is negeso:question -->
	<textarea name="inquiry_alternative_{@id}" style="height: {@aoHeight}px; width:	{@aoWidth}px;">
		<xsl:value-of select="@alternative_answer"/>
	</textarea>
</xsl:template>

<xsl:template name="inq_if_vertical">
	<!-- current XML tag is negeso:question -->
	<xsl:if	test="@options_layout='vertical'">
		<br/>
	</xsl:if>
</xsl:template>
<!--========== Inquiry questionnaire: End =========-->

<!-- Show summary and filled answers -->
<xsl:template match="negeso:questionnaire[../@status='complete']">
	<h2>
		<xsl:call-template name ="add-constant-info">
			<xsl:with-param name ="dict"  select="$dict_inquiry_module"/>
			<xsl:with-param name ="name"  select="'INQ_THANKS_FOR_ANSWERES'"/>
		</xsl:call-template>
		<xsl:value-of select="java:getString($dict_inquiry_module, 'INQ_THANKS_FOR_ANSWERES')"/>
	</h2>

    <div class="b-inquiryBack">
				<!-- There are two anchors because in other case whitespace(&#160;) is also underlined-->
				&#160;
        <a href="?" onfocus="blur()">
					<img src="/site/modules/inquiry_module/images/left.gif" alt="" align="absMiddle" />
				</a>&#160;
        <a href="?" onfocus="blur()">
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_inquiry_module"/>
						<xsl:with-param name ="name"  select="'INQ_BACK'"/>
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_inquiry_module, 'INQ_BACK')"/>
				</a>
				&#160;
				<span>
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_inquiry_module"/>
						<xsl:with-param name ="name"  select="'INQ_TO_QUESTIONNAIRES_LIST'"/>
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_inquiry_module, 'INQ_TO_QUESTIONNAIRES_LIST')"/>
				</span>

    </div>
	<xsl:call-template name="inq_print_conclusion" />
	<xsl:if	test="@showAnswers = 'true'">
		<table cellpadding="0" cellspacing="5" border="0" class="b-inquiryResult-Answers">
			<xsl:apply-templates select="negeso:question" mode="inq_filled" />
		</table>
		<br/>
	</xsl:if>
</xsl:template>

<xsl:template name="inq_print_conclusion">
	<!-- current XML tag is negeso:questionnaire -->
	<div class="b-inquiryConclusion">
		<xsl:value-of select="negeso:conclusion/text()" disable-output-escaping="yes"/>
	</div>
</xsl:template>

<!-- show filled questionarrie -->
<xsl:template match="negeso:question" mode="inq_filled">
	<tr>
		<th>
			<xsl:value-of select="@title"/>:
		</th>
		<td>
			<xsl:if test="negeso:option/@checked='true' or string-length(@answer)!=0 or string-length(@alternative_answer)">
				<xsl:choose>
					<xsl:when test="@type='text' or	@type='textarea'">
						<div><xsl:value-of select="@answer"/></div>
					</xsl:when>
					<xsl:when test="@type='radio' or @type='checkbox' or @type='dropdown'">
						<xsl:for-each select="negeso:option">
							<xsl:if	test="@checked='true'">
								<div><xsl:value-of select="@title"/></div>
							</xsl:if>
						</xsl:for-each>
					</xsl:when>
				</xsl:choose>
				<xsl:if	test="string-length(@alternative_answer) !=	0">
					<div><xsl:value-of select="@alternative"/>:	<xsl:value-of select="@alternative_answer"/></div>
				</xsl:if>
				<xsl:if	test="string-length(@remark) !=	0">
					<div>
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_inquiry_module"/>
							<xsl:with-param name ="name"  select="'INQ_REMARK'"/>
						</xsl:call-template>
						<xsl:value-of select="java:getString($dict_inquiry_module, 'INQ_REMARK')"/>: <xsl:value-of select="@remark"/></div>
				</xsl:if>
			</xsl:if>
		</td>
	</tr>
</xsl:template>

<!--======== Inquiry Module body contents: End =======-->

</xsl:stylesheet>