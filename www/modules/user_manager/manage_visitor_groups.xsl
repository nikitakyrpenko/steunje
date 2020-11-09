<?xml version="1.0" encoding="utf-8"?>
<!--
  @(#)$Id$     
 
  Copyright (c) 2004 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.
 
  TODO: file description here  

  @author       Stanislav Demchenko
  @version      $Revision$
-->


<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">
		
<xsl:variable name="lang" select="/*/@interface-language"/>
<xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>
<xsl:variable name="dict_security_module" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_security_module.xsl', $lang)"/>
<xsl:variable name="dict_modules" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_modules.xsl', $lang)"/>


<xsl:variable name="action">
    <xsl:choose>
        <xsl:when test="/negeso:page/negeso:contents/negeso:groups">list</xsl:when>
        <xsl:when test="/negeso:page/negeso:context/negeso:action">
            <xsl:value-of select="/negeso:page/negeso:context/negeso:action"/>
        </xsl:when>
        <xsl:otherwise>list</xsl:otherwise>
    </xsl:choose>
</xsl:variable>
<xsl:variable name="title">
    <xsl:choose>
        <xsl:when test="$action='add'"><xsl:value-of select="java:getString($dict_security_module, 'NEW_GROUP')"/></xsl:when>
        <xsl:when test="$action='edit'"><xsl:value-of select="java:getString($dict_common, 'EDIT_GROUP')"/></xsl:when>
        <xsl:when test="$action='save'"><xsl:value-of select="java:getString($dict_security_module, 'SAVING_CHANGES')"/></xsl:when>
        <xsl:when test="$action='delete'"><xsl:value-of select="java:getString($dict_security_module, 'GROUPS_OF_AUTHORIZED_VISITORS')"/></xsl:when>
        <xsl:otherwise><xsl:value-of select="java:getString($dict_security_module, 'GROUPS_OF_AUTHORIZED_VISITORS')"/></xsl:otherwise>
    </xsl:choose>
</xsl:variable>
<xsl:include href="/xsl/negeso_body.xsl"/>
<!-- **************** BEGIN MAIN TEMPLATE (COMMON FOR ALL MODES) *********** -->
<xsl:template match="/negeso:page/negeso:contents">
    <html>
    <head>
        <title><xsl:value-of select="$title"/></title>
        <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
        
        <script type="text/javascript" src="/script/jquery.min.js"></script>
		<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
        <script type="text/javascript" src="/script/cufon-yui.js"></script>
        <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
        <script type="text/javascript" src="/script/common_functions.js"></script>
        
        <script language="JavaScript">
            var s_DeleteGroupConfirmation = "<xsl:value-of select="java:getString($dict_common, 'DELETE_GROUP_CONFIRMATION')"/>";
            var s_FillInGroupName = "<xsl:value-of select="java:getString($dict_security_module, 'FILL_IN_GROUP_NAME')"/>";

            function add() {
                window.location.href = "?action=add";
            }
            
            function edit(id) {
                window.location.href = "?action=edit&amp;id="+id;
            }
            
            function del(id) {
                if(!confirm(s_DeleteGroupConfirmation)) {
                    return;
                }
                window.location.href = "?action=delete&amp;id="+id;
            }
            
            function validateForm(id){
                if(add_edit_form.name.value == ''){
                    alert(s_FillInGroupName);
                    add_edit_form.name.focus();
                    return;
                }
                add_edit_form.submit();
            }
            
            window.focus();            
        </script>
    </head>
    <body>
        
            <!-- NEGESO HEADER -->
     <xsl:call-template name="NegesoBody">
         <xsl:with-param name="helpLink">
             <xsl:text>/admin/help/clg1_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>
         </xsl:with-param>
         <xsl:with-param name="backLink" select='"?action=list"' />
     </xsl:call-template>
        <xsl:call-template name="buttons"> </xsl:call-template>
    
    </body>
    </html>
</xsl:template>
<!-- ******************* END MAIN TEMPLATE ********************************* -->
    <xsl:template match="negeso:contents"  mode="admContent">
        <table cellpadding="0" cellspacing="0" class="admTable">
            <tr>
                <td>
                    <table cellpadding="0" cellspacing="0" align="center">
                        <tr>
                            <td>
                                <ul>
                                    <xsl:for-each select="//negeso:context/negeso:error">
                                        <li style="text-align: left; color: #ff0000;">
                                            <xsl:value-of select="."/>
                                        </li>
                                    </xsl:for-each>
                                </ul>
                            </td>
                        </tr>
                    </table>
                    <table cellpadding="0" cellspacing="0" width="100%">
                        <tr>
                            <td align="center" class="admNavPanelFont">
                                <xsl:call-template name="tableTitle">
                                    <xsl:with-param name="headtext">
                                        <xsl:value-of select="$title"/>
                                    </xsl:with-param>
                                </xsl:call-template>
                            </td>
                        </tr>
                        <tr>
                            <td class="admNavPanel" style="padding: 0 0 0 20px;" >
                                <a class="admAnchor" href="visitor_mngr">
                                    <xsl:value-of select="java:getString($dict_modules, 'AUTHORIZED_VISITORS')"/>
                                </a>&#160;&#160;&#160;
                                <a class="admAnchor" href="visitor_group_mngr">
                                    <xsl:value-of select="java:getString($dict_security_module, 'VISITOR_GROUPS')"/>
                                </a>&#160;&#160;&#160;
                                <xsl:call-template name="consts">
                                    <xsl:with-param name="id" select="@id"/>
                                </xsl:call-template>
                            </td>
                        </tr>
                    </table>
                    
                    <!-- CONTENT -->
                    <xsl:choose>
                        <xsl:when test="$action='add'">
                            <xsl:apply-templates select="negeso:group" mode="add_edit"/>
                        </xsl:when>
                        <xsl:when test="$action='edit'">
                            <xsl:apply-templates select="negeso:group" mode="add_edit"/>
                        </xsl:when>
                        <xsl:when test="$action='save'">
                            <!-- ************ IGNORING ERRORS: FIXME *********** -->
                            <!--<xsl:apply-templates select="negeso:group" mode="list"/>-->
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:apply-templates select="negeso:groups" mode="list"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </td>
            </tr>
            <tr>
                <td class="admTableFooter" >&#160;</td>
            </tr>
        </table>
           
    </xsl:template>

<!-- *********************** BEGIN MODE "ADD_EDIT" ***************************** -->
<xsl:template match="negeso:group" mode="add_edit">
    <form method="POST" enctype="multipart/form-data" id="add_edit_form">
        <input type="hidden" name="action" value="save"/>
        <input type="hidden" name="role" value="visitor"/>
        <xsl:if test="@id">
            <input type="hidden" name="id" value="{@id}"/>
        </xsl:if>
         <table cellpadding="0" cellspacing="0"  width="100%">
            
                <tr>
                    <th class="admTableTD admWidth150"><xsl:value-of select="java:getString($dict_common, 'GROUP')"/>*</th>
                    <th class="admTableTDLast"><input class="admTextArea" type="text" value="{@name}" name="name"/></th>
                </tr>
                <tr>
                    <th class="admTableTD admWidth150"><xsl:value-of select="java:getString($dict_security_module, 'ROLE')"/></th>
                    <th class="admTableTDLast"><xsl:value-of select="java:getString($dict_security_module, 'VISITOR')"/></th>
                </tr>
            </table>
        <div class="admTDtitles">
            <xsl:call-template name="tableTitle">
                <xsl:with-param name="headtext">
                    <xsl:value-of select="java:getString($dict_security_module, 'GROUP_CAN_ACCESS_CONTAINERS')"/>:
                </xsl:with-param>
            </xsl:call-template>
        </div>
        <table cellpadding="0" cellspacing="0"  width="100%">
            <xsl:for-each select="negeso:containers/negeso:container">
                <tr>
                    <th class="admTableTD"><xsl:value-of select="@name"/></th>
                    <th class="admTableTDLast admOrder">
                        <input type="checkbox" name="containers" value="{@id}">
                            <xsl:if test="@linked='true'">
                                <xsl:attribute name="checked"/>
                            </xsl:if>
                        </input>
                    </th>
                </tr>
            </xsl:for-each>
        </table>
        <div class="admTDtitles">
            <xsl:call-template name="tableTitle">
                <xsl:with-param name="headtext">
                    <xsl:value-of select="java:getString($dict_security_module, 'GROUP_INCLUDES_VISITORS')"/>:
                </xsl:with-param>
            </xsl:call-template>
        </div>
       <table cellpadding="0" cellspacing="0" width="100%">
              <xsl:for-each select="negeso:users/negeso:user">
                    <tr>
                        <th class="admTableTD"><xsl:value-of select="@name"/></th>
                        <th class="admTableTDLast admOrder">
                        <input type="checkbox" name="users" value="{@id}">
                            <xsl:if test="@linked='true'">
                                <xsl:attribute name="checked"/>
                            </xsl:if>
                        </input>
                 </th>
                </tr>
               
          </xsl:for-each>
           
         </table>
      
    </form>

</xsl:template>
<!-- *********************** END MODE "ADD_EDIT" *********************** -->

<xsl:template match="negeso:context/*"/><!-- DIRTY PATCH -->

<!-- *********************** BEGIN MODE "LIST" ***************************** -->
<xsl:template match="negeso:groups" mode="list">
    <form method="POST" enctype="multipart/form-data">
        <table cellpadding="0" cellspacing="0" width="100%">
            
                <tr>
                    <td class="admTDtitles"><xsl:value-of select="java:getString($dict_common, 'GROUP')"/></td>
                    <td class="admTDtitles"><xsl:value-of select="java:getString($dict_security_module, 'BASED_ON_ROLE')"/></td>
                    <td class="admTDtitles" colspan="2">&#160;</td>
                </tr>
           <xsl:for-each select="negeso:group">
              <tr>
                <th class="admTableTD"><xsl:value-of select="@name"/></th>
                <th class="admTableTD"><xsl:value-of select="@role-id"/></th>
                <td class="admTableTD" width="1%">
                    <img src="/images/edit.png" class="admHand" onClick="edit({@id})">
                        <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'EDIT')"/></xsl:attribute>
                    </img>
                </td>
                <td class="admTableTDLast"  width="1%">
                    <img src="/images/delete.png" class="admHand" onClick="del({@id})">
                        <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'DELETE')"/></xsl:attribute>
                    </img>
                </td>
              </tr>
           </xsl:for-each>
               
          </table>
    </form> 
</xsl:template>
    <xsl:template name="consts">
        <xsl:param name="id"/>
        <a href=""  title="Consts" class="admAnchor" target="_blank" >
            <xsl:attribute name="href">/admin/module_consts?moduleId=<xsl:value-of select="$id"/>&amp;visitorMode=true</xsl:attribute>
            <xsl:value-of select="java:getString($dict_modules, 'SETTINGS')"/> 
        </a>
    </xsl:template>
<!-- *********************** END MODE "LIST" *********************** -->
    <!--******************** Bottom buttons ********************-->
    <xsl:template name="buttons">
        <xsl:if test="/negeso:page/negeso:contents/negeso:groups">
            <table cellpadding="0" cellspacing="0"  width="764px" align="center" class="Buttons">
                <tr>
                    <td >

                        <div class="admBtnGreenb">
                            <div class="imgL"></div>
                            <div>
                                <input class="admNavbarInp" type="button" style="width:145px;" onClick="add()">
                                    <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_common, 'ADD_NEW_GROUP')"/></xsl:attribute>
                                </input>
                            </div>
                            <div class="imgR"></div>
                        </div>
                    </td>
                </tr>
            </table>
        </xsl:if>
     <xsl:if test="$action='add' or $action='edit'">
            <table cellpadding="0" cellspacing="0"  width="764px" align="center" class="Buttons">
                
                <tr>
                    <td >
                        <div class="admBtnGreenb">
                            <div class="imgL"></div>
                            <div>
                                <input class="admNavbarInp" type="button" style="width:135px;" onclick="validateForm('{@id}')">
                                    <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_common, 'SAVE_AND_CLOSE')"/></xsl:attribute>
                                </input>
                                
                            </div>
                            <div class="imgR"></div>
                        </div>
                    </td>
                </tr>
            </table>
        </xsl:if>
    </xsl:template>
   
</xsl:stylesheet>


