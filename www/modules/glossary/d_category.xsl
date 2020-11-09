<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)$Id$
 
  Copyright (c) 2005 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.
 
  
 
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

<!-- NEGESO JAVASCRIPT Temaplate -->    
<xsl:template name="java-script">
    <script language="JavaScript">
    var s_DeleteCategoryConfirmation = "<xsl:value-of select="java:getString($dict_glossary, 'DELETE_CATEGORY_CONFIRMATION')"/>";

    <xsl:text disable-output-escaping="yes">
        <![CDATA[
        // PUBLICATION MANAGEMENT
        function addCategory() {
            document.operateForm.action.value = "add_category";
        }

        function editCategory(targetId) {
            document.operateForm.action.value = "show_category";
            document.operateForm.categoryId.value = targetId;
            document.operateForm.submit();
        }

        function deleteCategory(targetId) {
            if (confirm(s_DeleteCategoryConfirmation)) {
                document.operateForm.action.value = "remove_category";
	            document.operateForm.categoryId.value = targetId;
                document.operateForm.submit();
                return true;
            }
            return false;
        }
        
function resetForm(){
$("#operateForm2")[0].reset();
 }

        function updateCategory( targetId ) {
            if ( !validateForm(operateForm2) ){
                return false;
            }
          document.operateForm2.submit();     
        	return true;
		}
    
        
        ]]>
        </xsl:text>
    </script>
</xsl:template>

<!-- MAIN ENTRY -->
<xsl:template match="/">
<html>
<head>
    <title><xsl:value-of select="java:getString($dict_glossary, 'DICTIONARY_CATEGORIES')"/></title>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <script type="text/javascript" src="/script/jquery.min.js"></script>
	<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
  <script type="text/javascript" src="/script/cufon-yui.js"></script>
  <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
    <script language="JavaScript1.2" src="/script/common_functions.js" type="text/javascript"/>
    
    <xsl:call-template name="java-script"/>
    <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
</head>
<body>
    <!-- NEGESO HEADER -->    
  <xsl:choose>
    <xsl:when test="count(descendant::negeso:page) = 0">
      <xsl:if test="@view='edit_category' or @view='add_category'">
        <xsl:call-template name="NegesoBody">
          <xsl:with-param name="helpLink" select="''"/>
          <xsl:with-param name="backLink" select='"glossary_category"' />
        </xsl:call-template> 
      </xsl:if>
      <xsl:if test="@view='category_list'">
        <xsl:call-template name="NegesoBody">
          <xsl:with-param name="helpLink" select="''"/>
          <xsl:with-param name="backLink" select='"glossary"' />
        </xsl:call-template>
      </xsl:if>
    </xsl:when>
    <xsl:otherwise>
      <xsl:call-template name="NegesoBody">
        <xsl:with-param name="helpLink" select="''"/>
        <xsl:with-param name="backLink" select="'glossary'"/>
      </xsl:call-template>
    </xsl:otherwise>
  </xsl:choose>
  <xsl:if test="negeso:page/@view='edit_category' or negeso:page/@view='add_category'">
    <xsl:call-template name="buttons"/>
  </xsl:if>
  <script>
    Cufon.now();
    Cufon.replace('.admNavPanelFont', { fontFamily: 'Orator Std' });
    Cufon.replace('.admTDtitles', { fontFamily: 'Orator Std' });
  </script>
</body>
</html>
</xsl:template>

  <xsl:template match="negeso:page"  mode="admContent">
    <table align="center"  border="0" cellpadding="0" cellspacing="0" class="admTable">

    <!-- PATH -->
      <tr>
        <td class="admTableTDLast" colspan="3">
	<xsl:call-template name="cb.categoryPath"/>
        </td>
      </tr>
    <!-- TITLE -->
      <tr>
        <td colspan="3" align="center" class="admNavPanelFont">
          <xsl:if test="@view='edit_category'">
            <xsl:call-template name="tableTitle">
              <xsl:with-param name="headtext">
                <xsl:value-of select="java:getString($dict_common, 'EDIT_CATEGORY')"/>
              </xsl:with-param>
            </xsl:call-template>
          </xsl:if>
          <xsl:if test="@view='add_category'">
            <xsl:call-template name="tableTitle">
              <xsl:with-param name="headtext">
                <xsl:value-of select="java:getString($dict_glossary, 'ADD_CATEGORY')"/>
              </xsl:with-param>
            </xsl:call-template>
          </xsl:if>
          <xsl:if test="@view='category_list'">
            <xsl:call-template name="tableTitle">
              <xsl:with-param name="headtext">
                <xsl:value-of select="java:getString($dict_glossary, 'DICTIONARY_CATEGORIES')"/>
              </xsl:with-param>
            </xsl:call-template>
          </xsl:if>
        </td>
      </tr>
   
    <!-- Content -->
       	<xsl:if test="@view='edit_category' or @view='add_category'">
        	<xsl:apply-templates select="negeso:glossary_category" mode="edit"/>
	    </xsl:if>
   		<xsl:if test="@view='category_list'">
	    	<xsl:apply-templates select="negeso:glossary_category_list"/>
	    </xsl:if>
      <tr>
        <td class="admTableFooter" >&#160;</td>
      </tr>
    </table>   

</xsl:template>

<!-- ********************************** Category *********************************** -->
<xsl:template match="negeso:glossary_category_list">

        <!-- Render HEADER -->
        <form method="POST" name="operateForm" action="">
        <input type="hidden" name="command" value="cb-manage-groups"></input>
        <input type="hidden" name="action" value=""></input>
        <input type="hidden" name="categoryId" value="-1"></input>      
                <tr>
                  <td class="admTableTDLast" colspan="3">
                    <div class="admNavPanelInp" style="padding-left:5px">
                      <div class="imgL"></div>
                      <div>
                        <input class="admNavbarInp" type="submit" onClick = "addCategory()">
                          <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_common, 'ADD_NEW_CATEGORY')"/></xsl:attribute>
                        </input>
                      </div>
                      <div class="imgR"></div>
                    </div>	                   
                   </td>
                </tr>       
            <tr>
              <td class="admTDtitles" colspan="3" style="padding:0; text-align:center;"><xsl:value-of select="java:getString($dict_common, 'TITLE')"/></td>
                
            </tr>
            <xsl:apply-templates select="negeso:glossary_category" mode="list"/>       
        </form>

</xsl:template>


<!-- ********************************** Location *********************************** -->
<xsl:template match="negeso:glossary_category" mode="list">
    <tr>
      <th class="admTableTD" width="89%">
            <a class="admAnchor" href="#" onClick = "return editCategory({@id})">
                <xsl:value-of select="@name"/>&#160;
            </a>
        </th>
        <td class="admTableTDLast" width="1%">
            <img src="/images/edit.png" class="admHand" onClick="return editCategory({@id})">
                <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'EDIT')"/></xsl:attribute>
            </img>
        </td>
        <td class="admTableTDLast" width="1%">
            <img src="/images/delete.png" class="admHand" onClick="return deleteCategory({@id})">
                <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'DELETE')"/></xsl:attribute>
         </img>
        </td>
    </tr>
</xsl:template>

<!-- ********************************** Location *********************************** -->
<xsl:template match="negeso:glossary_category" mode="edit">
    <form method="POST" name="operateForm2" action="" id="operateForm2">
	
	    <tr>
	        <td>
		        <input type="hidden" name="categoryId" value="{@id}"></input>
		        <input type="hidden" name="command" value="cb-manage-categories"></input>
		        <input type="hidden" name="action" value="save_category"></input>
		        <input type="hidden" name="categoryId" value="{@id}"></input>
			    <input type="hidden" name="back-link" value="{/page/@back-link}"/>
			
					<!-- Title -->
    				<tr>
              <th class="admTableTD admWidth150"><xsl:value-of select="java:getString($dict_common, 'TITLE')"/>*</th>
              <td class="admTableTDLast">
			    			<input class="admTextArea admWidth200" 
			    				type="text" 
			    				name="title" 
			    				data_type="text"
			    				required="true" 
			    				uname="Title"
			    			>
						    	<xsl:attribute name="value"><xsl:value-of select="@name"/></xsl:attribute>
							</input>
    					</td>
	    			</tr>
				
		</td></tr>	
	
    </form>
	
</xsl:template>

<xsl:template name="cb.categoryPath">
    <!-- Path -->
   
				<span class="admZero admLocation">
			    <a class="admLocation" href="glossary">
			    	<xsl:value-of select="java:getString($dict_glossary, 'DICTIONARY')"/>
			    </a>
			    &#160;&gt;
				</span>

				<xsl:choose>
				<xsl:when test="@view='edit_category'">
					<span class="admZero admLocation">
				    <a class="admLocation" href="glossary_category">
				    	<xsl:value-of select="java:getString($dict_common, 'CATEGORIES')"/>
				    </a>
				    &#160;&gt;
					</span>
					<!-- Active pathe element - just print it-->
					<span class="admSecurity admLocation">
						<xsl:value-of select="negeso:glossary_category/@name"/>	
					</span>
				</xsl:when>
				<xsl:when test="@view='add_category'">
					<span class="admZero admLocation">
				    <a class="admLocation" href="glossary_category">
				    	<xsl:value-of select="java:getString($dict_common, 'CATEGORIES')"/>
				    </a>
				    &#160;&gt;
					</span>
					<!-- Active pathe element - just print it-->
					<span class="admSecurity admLocation">
				    	<xsl:value-of select="java:getString($dict_glossary, 'ADD_CATEGORY')"/>
					</span>
				</xsl:when>
				<xsl:otherwise>
					<!-- Active pathe element - just print it-->
					<span class="admSecurity admLocation">
				    	<xsl:value-of select="java:getString($dict_common, 'CATEGORIES')"/>
					</span>
				</xsl:otherwise>
				</xsl:choose>
           
</xsl:template>
  <xsl:template name="buttons">
    <table cellpadding="0" cellspacing="0"  width="764px" align="center" class="Buttons">
      <tr>
        <td>
          <div class="admBtnGreenb">
            <div class="imgL"></div>
            <div>

              <xsl:choose>
                <xsl:when test="count(@id) = 0">                 
                  <a focus="blur()" class="admBtnText" onClick="updateCategory('-1');" href="#updateCategory('-1')">
                    <xsl:value-of select="java:getString($dict_common, 'ADD')"/>
                  </a>
                </xsl:when>
                <xsl:otherwise>                
                  <a focus="blur()" class="admBtnText" onClick="updateCategory('{@id}');" href="#updateCategory('{@id}'">
                    <xsl:value-of select="java:getString($dict_common, 'SAVE')"/>
                  </a>
                </xsl:otherwise>
              </xsl:choose>

            </div>
            <div class="imgR"></div>
          </div>

          <div class="admBtnGreenb">
            <div class="imgL"></div>
            <div>            
              <input type="button" onclick="resetForm();" value="Reset form"/>
            </div>
            <div class="imgR"></div>
          </div>

        </td>
      </tr>
    </table>
  </xsl:template>
</xsl:stylesheet>