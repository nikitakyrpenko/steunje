<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)$Id$
 
  Copyright (c) 2005 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.
 
  Location management interface
 
  @version      $Revision$
  @author       Olexiy.Strashko
-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">

<!-- Include/Import -->
  <xsl:include href="/xsl/negeso_body.xsl"/>

<xsl:variable name="lang" select="/*/@interface-language"/>
<xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>
<xsl:variable name="dict_glossary" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_glossary.xsl', $lang)"/>    	
<xsl:variable name="dict_glossary_custom" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dictionary', $lang)"/>

<!-- NEGESO JAVASCRIPT Temaplate -->    
<xsl:template name="java-script">
    <script language="JavaScript">

    var s_DeleteWordConfirmation = "<xsl:value-of select="java:getString($dict_glossary, 'DELETE_WORD_CONFIRMATION')"/>";
    var s_YouMustEnterSearchWord = "<xsl:value-of select="java:getString($dict_glossary, 'YOU_MUST_ENTER_SEARCH_WORD')"/>";

    <xsl:text disable-output-escaping="yes">
        <![CDATA[
        
        function findWords() {
			if (operateForm.findW.value == null ||
				    operateForm.findW.value=="" 
				    )  {
					alert(s_YouMustEnterSearchWord);
					return;
				}
           	window.location="?show=find&find=" + document.getElementById("findWord").value;
		}

        function handleKeyPressed() {
			if (window.event.keyCode == 13) {
				if (operateForm.findW.value==null ||
			        operateForm.findW.value=="" ) {
		       		alert(s_YouMustEnterSearchWord);
			        return false;
			 	} else {
				operateForm.show.value = 'find';
				operateForm.find.value = document.getElementById("findWord").value;
				operateForm.submit
				}
			 }		 
		}
        
        function update() {
        	if ( !validateForm(operateForm) ){
                return false;
            }
            document.operateForm.action.value = "save_word";
            document.operateForm.submit();     
        	return true;
        }

        function back() {
            document.operateForm.action.value = "";
        }

        function addWord() {
            document.operateForm.action.value = "add_word";
        }

        function deleteWord(targetId) {
        	if (confirm(s_DeleteWordConfirmation)) {
	            document.operateForm.action.value = "delete_word";
	            document.operateForm.removingWordId.value = targetId;
	            document.operateForm.submit();
	            return true;
	        }
	        return false;
        }

        function editWord(targetId) {
            document.operateForm.wid.value = targetId;
            document.operateForm.submit();
        }


        ]]>
        </xsl:text>
    </script>   

    
</xsl:template>

<!-- MAIN ENTRY -->
<xsl:template match="/">
<html>
<head>
    <title><xsl:value-of select="java:getString($dict_glossary, 'DICTIONARY')"/></title>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <xsl:call-template name="java-script"/>
    <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" src="/script/jquery.min.js"></script>
	<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
  <script type="text/javascript" src="/script/cufon-yui.js"></script>
  <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
    <script language="JavaScript1.2" src="/script/common_functions.js" type="text/javascript"/>
</head>
<body
    style="behavior: url(/script/webservice.htc) url(/script/clientmanager.htc)"
    id="ClientManager" xmlID="332"
>

    <!-- NEGESO HEADER -->
  <xsl:call-template name="NegesoBody">
    <xsl:with-param name="helpLink" select="''"/>
   <xsl:with-param name="backLink" select="'glossary'"/>   
  </xsl:call-template>

  <xsl:choose>
    <xsl:when test="//negeso:glossary_header/@action='add_word'">
    <xsl:call-template name="buttons"/>
    </xsl:when>
    <xsl:when test="//negeso:glossary_word_details">
   <xsl:call-template name="buttons"/>
    </xsl:when>
    <xsl:otherwise>
      &#160;
    </xsl:otherwise>
  </xsl:choose>
  <script>
    Cufon.now();
    Cufon.replace('.admNavPanelFont', { fontFamily: 'Orator Std' });
    Cufon.replace('.admTDtitles', { fontFamily: 'Orator Std' });
  </script>

</body>
</html>
</xsl:template>



  <xsl:template match="negeso:page"  mode="admContent">
    <!-- NavBar -->


    <!-- PATH --><!--
	<xsl:call-template name="cb.wordPath"/>-->

    <!-- TITLE -->
    <table align="center"  border="0" cellpadding="0" cellspacing="0" class="admTable">
      <tr>
        <td align="center" class="admNavPanelFont"  colspan="2">
          <xsl:call-template name="tableTitle">
            <xsl:with-param name="headtext">
              <xsl:value-of select="java:getString($dict_glossary, 'DICTIONARY')"/>
            </xsl:with-param>
          </xsl:call-template>
        </td>
      </tr>
     
      <!-- Content -->
      <form method="POST" name="operateForm" action="">
        <input type="hidden" name="command" value="d-manage-glossary"></input>
        <input type="hidden" name="action" value=""/>
        <input type="hidden" name="show" value="{negeso:glossary_header/@show}"/>
        <input type="hidden" name="cat" value="{negeso:glossary_header/@cat}"/>
        <input type="hidden" name="find" value="{negeso:glossary_header/@find}"/>
        <input type="hidden" name="id" value="{negeso:glossary_header/@id}"/>
        <input type="hidden" name="removingWordId" value=""/>
        <input type="hidden" name="back-link" value="words"/>
        <input type="hidden" name="wid" value=""/>

        <xsl:apply-templates select="negeso:glossary_header"/>
        <xsl:apply-templates select="negeso:glossary_search_results"/>
        <xsl:apply-templates select="negeso:glossary_word_details"/>
      </form>
      <tr>
        <td class="admTableFooter" >&#160;</td>
      </tr>
    </table>
</xsl:template>


<xsl:template match="negeso:glossary_header">

    <tr>
      <th class="admTableTD admWidth150"><xsl:value-of select="java:getString($dict_glossary, 'SHOW_BY_LETTER')"/></th>
      <td class="admTableTDLast">
                <span style="margin-left: 5px; margin-right: 5px;">
	                <xsl:apply-templates select="negeso:glossary_alphabet"/> 
	                <a href="?show=all" class="admBlue">
	                    <xsl:if test="@show='all'">
	                    	<xsl:attribute name="style"> color: #007fd5; text-decoration : none; align: left; font-size: 12px;
                        </xsl:attribute>
	                    </xsl:if>
	                <xsl:value-of select="java:getString($dict_glossary, 'SHOW_ALL')"/></a> |
                </span>
            </td>
        </tr>
        <tr>
          <th class="admTableTD admWidth150"><xsl:value-of select="java:getString($dict_glossary, 'SHOW_BY_CATEGORY')"/></th>
          <td class="admTableTDLast">
                <!-- select style="width: 200;" id="cat" name="cat" -->
                <span style="margin-left: 5px; margin-right: 5px;">
	                <xsl:apply-templates select="negeso:glossary_category_list/negeso:glossary_category"/>
	                | <a href="glossary_category" class="admBlue">
	                	<xsl:value-of select="java:getString($dict_glossary, 'MANAGE_CATEGORIES')"/>
	                </a> 	                
                </span>
            </td>
        </tr>
        <tr>
          <th class="admTableTD admWidth150"><xsl:value-of select="java:getString($dict_glossary, 'FIND')"/></th>
          <td class="admTableTDLast">
                <input class="admTextArea" name="findW" id="findWord" style="margin:8px 0 0 0;">
             		<xsl:attribute name="onkeypress">
                  handleKeyPressed()               
       	            </xsl:attribute>
               		<xsl:if test="@show='find'">
                		<xsl:attribute name="value">
    	            		<xsl:value-of select="@field"/>
	                	</xsl:attribute>
                	</xsl:if>
               	</input>

            <div class="admNavPanelInp" style="padding-left:5px">
              <div class="imgL"></div>
              <div>
                <a  onclick="findWords();">
                  <xsl:if test="@show='find'">
                    <xsl:attribute name="style">color: #fffff; font-size: 13px; cursor: hand;</xsl:attribute>
                  </xsl:if>
                  show
                </a>
              </div>
              <div class="imgR"></div>
            </div>
            <div class="admNavPanelInp" style="padding-left:5px">
              <div class="imgL"></div>
              <div>
                <input class="admNavbarInp" type="submit" onClick = "addWord()">
                  <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_glossary, 'ADD_NEW_WORD')"/></xsl:attribute>
                </input>
              </div>
              <div class="imgR"></div>
            </div>
            
              
            </td>
        </tr>   
    
    
</xsl:template>


<xsl:template match="negeso:glossary_alphabet">
    <xsl:apply-templates select="negeso:glossary_letter"/>
</xsl:template>


<xsl:template match="negeso:glossary_letter">
    <xsl:choose>
        <xsl:when test="@amount=0">
            <xsl:attribute name="style">color: #848484;</xsl:attribute><xsl:value-of select="@letter"/>
        </xsl:when>
        <xsl:otherwise>
            <a href="?" style="color: #007fd5; text-decoration : none;">
                <xsl:if test="@selected"><xsl:attribute name="style">color: #007fd5; text-decoration : none;</xsl:attribute></xsl:if>
                <xsl:attribute name="href"><xsl:text disable-output-escaping="yes"><![CDATA[?show=letter&id=]]></xsl:text><xsl:value-of select="@id"/></xsl:attribute>
                <xsl:value-of select="@letter"/>
            </a>
        </xsl:otherwise>
    </xsl:choose> |
</xsl:template>


<xsl:template match="negeso:glossary_category">
	<a href="?show=category&amp;cat={@id}" style="color: #007fd5; font-size: 12px; cursor: hand;">
        <xsl:if test="@selected='true'"><xsl:attribute name="style">color: #007fd5; font-size: 12px; cursor: hand;</xsl:attribute></xsl:if>
		<xsl:value-of select="@name"/>
	</a>
	&#160;
</xsl:template>


<xsl:template match="negeso:glossary_search_results">
  <!-- TITLE -->
  
   
      <tr>
       <td class="admTDtitles" colspan="2" style="padding:0; text-align:center;">
        <xsl:call-template name="tableTitle">
        <xsl:with-param name="headtext">
          <xsl:choose>
            <xsl:when test="//negeso:glossary_header/@show='letter'">
              <xsl:value-of select="@letter"/>&#160;<xsl:value-of select="java:getString($dict_glossary, 'WORDS')"/>
            </xsl:when>
            <xsl:when test="//negeso:glossary_header/@show='find'">
              <xsl:value-of select="java:getString($dict_glossary, 'SEARCH_RESULTS_FOR')"/>

              &quot;<xsl:value-of select="@search_word"/>&quot;
            </xsl:when>
            <xsl:when test="//negeso:glossary_header/@show='category'">
              <xsl:value-of select="@category"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="java:getString($dict_glossary, 'WORDS')"/>&#160;
            </xsl:otherwise>
          </xsl:choose>
        </xsl:with-param>
      </xsl:call-template>
      </td>
      </tr>
  <tr>
    <td colspan="2">
      <table width="100%">
        <xsl:apply-templates select="negeso:glossary_word"/>
      </table>
    </td>
  </tr>
</xsl:template>


<xsl:template match="negeso:glossary_word">
 
	<xsl:if test="(@letter) and (//negeso:glossary_header/@show!='letter') ">
	    <tr><td class="admTDtitles" colspan="3">
    <xsl:value-of select="@letter"/>
	    	</td>
	    </tr>
	</xsl:if>

    <tr>
      <th class="admTableTD" width="89%">
            <a>
            <xsl:choose>
                <xsl:when test="//negeso:glossary_header/@show='all'">
                    <xsl:attribute name="href">?wid=<xsl:value-of select="@id"/><xsl:text disable-output-escaping="yes"><![CDATA[&show=all]]></xsl:text></xsl:attribute>
                </xsl:when>
                <xsl:when test="//negeso:glossary_header/@show='letter'">
                    <xsl:attribute name="href">?wid=<xsl:value-of select="@id"/><xsl:text disable-output-escaping="yes"><![CDATA[&show=letter&]]></xsl:text>id=<xsl:value-of select="//negeso:glossary_header/@field"/></xsl:attribute>
                </xsl:when>
                <xsl:when test="//negeso:glossary_header/@show='category'">
                    <xsl:attribute name="href">?wid=<xsl:value-of select="@id"/><xsl:text disable-output-escaping="yes"><![CDATA[&show=category&]]></xsl:text>cat=<xsl:value-of select="//negeso:glossary_header/@field"/></xsl:attribute>
                </xsl:when>
                <xsl:when test="//negeso:glossary_header/@show='find'">
                    <xsl:attribute name="href">?wid=<xsl:value-of select="@id"/><xsl:text disable-output-escaping="yes"><![CDATA[&show=find&]]></xsl:text>find=<xsl:value-of select="//negeso:glossary_header/@field"/></xsl:attribute>
                </xsl:when>
            </xsl:choose>
            <xsl:value-of select="@word"/>
    	</a>
    	
    	</th>
      <td class="admTableTDLast">
        <a href="#" onClick="editWord({@id})">
          <img src="/images/edit.png" border="0"/>
        </a>

      </td>
      <td class="admTableTDLast">    	
	        <a href="#" onClick="deleteWord({@id})">
	        	<img src="/images/delete.png" border="0"/>
	    	</a>
    	</td>
    </tr>
     
</xsl:template>


<xsl:template match="negeso:glossary_word_details">
    <!-- TITLE -->
   
	<!-- ERROR STRING -->
	
	<xsl:choose>
        <xsl:when test="//negeso:error-clause/@caused-by='word_is_not_uniq'">
        	<font color="#FF0000">
            	<xsl:value-of select="java:getString($dict_glossary_custom, 'WORD_IS_NOT_UNIQ')"/>
            </font>
        </xsl:when>
        <xsl:otherwise>
        </xsl:otherwise>
   </xsl:choose>

    <input type="hidden" name="wordId" value="{@id}"/>    
      <tr>
        <td align="center" class="admNavPanelFont"  colspan="2">
          <xsl:choose>
            <xsl:when test="//negeso:glossary_header/@action='add_word'">
              <xsl:call-template name="tableTitle">
                <xsl:with-param name="headtext">
                  <xsl:value-of select="java:getString($dict_glossary, 'ADD_NEW_WORD')"/>
                </xsl:with-param>
              </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
              <xsl:call-template name="tableTitle">
                <xsl:with-param name="headtext">
                  <xsl:value-of select="java:getString($dict_glossary, 'WORD_DETAILS')"/>
                </xsl:with-param>
              </xsl:call-template>
            </xsl:otherwise>
          </xsl:choose>
        </td>
      </tr>
	    <tr>
        <td class="admTDtitles" colspan="2" style="padding:0; text-align:center;">
          <xsl:value-of select="java:getString($dict_glossary, 'WORD_PROPERTIES')"/>
        </td>
        </tr>
  <tr>
    <td>
    <!-- Title -->
    <tr>
      <th class="admTableTD admWidth150">
        <xsl:value-of select="java:getString($dict_glossary, 'WORD')"/>*
      </th>
      <td class="admTableTDLast">
        <input class="admTextArea admWidth200"
          type="text"
          name="title"
          data_type="text"
          required="true"
          uname="Title"
			    			>
          <xsl:attribute name="value"><xsl:value-of select="@word"/></xsl:attribute>
        </input>
      </td>
    </tr>
    <!-- Text -->
    <tr>
      <th class="admTableTD admWidth150">
        <xsl:value-of select="java:getString($dict_common, 'DESCRIPTION')"/>*
      </th>
      <td class="admTableTDLast">
        &#160;<img src="/images/mark_1.gif" onclick="edit_text('article_text{@article_id}', 'contentStyle', 595);" class="admBorder admHand" alt="Edit a description"/>
        <div id="article_text{@article_id}" class="contentStyle">
          <xsl:attribute name="style">behavior:url(/script/article3.htc); margin: 5px;border: 1px solid #848484;</xsl:attribute>
          <xsl:choose>
            <xsl:when test="text()">
              <xsl:value-of select="text()" disable-output-escaping="yes"/>
            </xsl:when>
            <xsl:otherwise>&#160;</xsl:otherwise>
          </xsl:choose>
        </div>
      </td>
    </tr>
    <!-- Text -->
    <tr>
      <th class="admTableTD admWidth150">
        <xsl:value-of select="java:getString($dict_common, 'CATEGORIES')"/>
      </th>
      <td class="admTableTDLast">
        <select name="categories" class="admTextArea admWidth200" size="5" multiple="yes">
          <xsl:for-each select="negeso:glossary_category_list">
            <xsl:for-each select="negeso:glossary_category">
              <option value="{@id}">
                <xsl:if test="@selected">
                  <xsl:attribute name="selected">true</xsl:attribute>
                </xsl:if>
                <xsl:value-of select="@name"/>
              </option>
            </xsl:for-each>
          </xsl:for-each>
        </select>
      </td>
    </tr>
    </td>
  </tr> 
    
	<!-- Update/reset fields -->
    <!--<table cellpadding="0" cellspacing="0" class="admNavPanel">
		<tr>
			<td class="admNavPanel admNavbar admCenter">
                <input class="admNavbarInp" name="saveButton" onClick="return update()" type="submit">
                    <xsl:attribute name="value">&lt;&#160;<xsl:value-of select="java:getString($dict_common, 'SAVE')"/>&#160;&gt;</xsl:attribute>
                </input>              

               
			</td>
		</tr>
	</table>--></xsl:template>

<xsl:template name="cb.wordPath">
    <!-- Path -->
    <table cellpadding="0" cellspacing="0" class="admNavPanel admSpaceSmall">
	   	<tr>
            <td class="admBold">
				<!-- Active pathe element - just print it-->
				<span class="admSecurity admLocation">
			    	<xsl:value-of select="java:getString($dict_glossary, 'DICTIONARY')"/>
				</span>
            </td>
        </tr>
    </table>
</xsl:template>
  <xsl:template name="buttons">
    <table cellpadding="0" cellspacing="0"  width="764px" align="center" class="Buttons">
      <tr>
        <td>
          <div class="admBtnGreenb">
            <div class="imgL"></div>
            <div>
              
            <a focus="blur()" class="admBtnText" onClick="update()" href="#update()">              
                <xsl:value-of select="java:getString($dict_common, 'SAVE')"/>
              </a>

            
            </div>
            <div class="imgR"></div>
          </div>

        </td>
      </tr>
    </table>
  </xsl:template>

</xsl:stylesheet>