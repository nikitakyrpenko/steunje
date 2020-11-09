<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)${j_departments.xsl}       
 
  Copyright (c) 2004 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.
 
  View of a job module.
 
  @version    2005/03/18
  @author     Volodymyr Snigur
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
    <xsl:variable name="dict_job_module" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_job_module.xsl', $lang)"/>
    <xsl:variable name="job_module" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('job_module', $lang)"/>

    <!-- NEGESO JAVASCRIPT Temaplate -->
    <xsl:template name="java-script">
        <script type="text/javascript" src="/script/jquery.min.js"></script>
		<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
        <script type="text/javascript" src="/script/cufon-yui.js"></script>
        <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
        <script language="JavaScript" src="/script/common_functions.js" type="text/javascript">/**/</script>

        <script language="JavaScript">
            var s_DeleteDepartmentConfirmation = "<xsl:value-of select="java:getString($dict_job_module, 'DELETE_DEPARTMENT_CONFIRMATION')"/>";
            var s_DeleteVacancyConfirmation = "<xsl:value-of select="java:getString($dict_job_module, 'DELETE_VACANCY_CONFIRMATION')"/>";
            var s_DeleteApplicantConfirmation = "<xsl:value-of select="java:getString($dict_job_module, 'DELETE_APPLICANT_CONFIRMATION')"/>";

            <xsl:text disable-output-escaping="yes">
		<![CDATA[

        var openImg = new Image();
        openImg.src = "/images/down2_white.gif";
        var closedImg = new Image();
        closedImg.src = "/images/right_white.gif";
        
        function swapFolder(img) {
        objImg = document.getElementById(img);
            if (objImg.src.indexOf('right_white.gif')>-1)
                objImg.src = openImg.src;
            else
                objImg.src = closedImg.src;
        }

        function clickHandler(event) {
          var targetId, srcElement, targetElement;

          srcElement = event.srcElement ? event.srcElement : event.target;
          if (srcElement.className == "admAnchor" || srcElement.className == "admImg admAnchor admHand" || srcElement.className == "admBold" || srcElement.className == "admGrey") {
             if (srcElement.className == "admAnchor" || srcElement.className == "admBold" || srcElement.className == "admGrey") targetId = srcElement.id + "table";
             if (srcElement.className == "admImg admAnchor admHand") {
                 var data = new String (srcElement.id);
                 lengthId = data.lenght;
					   resId = data.substring(7,lengthId);
                 targetId = resId + "table";
             }
             targetElement = document.all(targetId);
             if (targetElement.style.display == "none") {
                targetElement.style.display = "";
             } else {
                targetElement.style.display = "none";
             }
          }
        }

        function addDepartment() {
            document.operateForm.action.value = "add_department";
            document.operateForm.submit();            
        }

        function deleteDepartment(targetId) {
            if (confirm(s_DeleteDepartmentConfirmation)) {
                document.operateForm.action.value = "delete_department";
                document.operateForm.department_id.value = targetId;
                document.operateForm.submit();
                return true;
            }
            return false;
        }

        function editDepartment(targetId) {
            document.operateForm.action.value = "department_details";
            document.operateForm.department_id.value = targetId;
            document.operateForm.submit();
        }
        
        //under construction functions START
        
        //IT SEEMS WORK START
        function addVacancy(departmentId) {
            document.operateForm.action.value = "add_vacancy";
            document.operateForm.department_id.value = departmentId;
            document.operateForm.submit();
        }
        
        function deleteVacancy(targetId) {
            if (confirm(s_DeleteVacancyConfirmation)) {
                document.operateForm.action.value = "delete_vacancy";
                document.operateForm.vacancy_id.value = targetId;
                document.operateForm.submit();
                return true;
            }
            return false;
        }

        function editVacancy(targetId) {
            document.operateForm.action.value = "vacancy_details";
            document.operateForm.vacancy_id.value = targetId;
            document.operateForm.submit();
        }
        
        function deleteApplicant(targetId) {
            if (confirm(s_DeleteApplicantConfirmation)) {
                document.operateForm.action.value = "delete_applicant";
                document.operateForm.dva_id.value = targetId;
                document.operateForm.submit();
                return true;
            }
            return false;
        }

        function editApplicant(targetId, departmentId) {
            document.operateForm.action.value = "applicant_details";
            document.operateForm.dva_id.value = targetId;
            document.operateForm.department_id.value = departmentId;
            document.operateForm.submit();
        }
        
        function editDefaultForm() {
            document.operateForm.action.value = "application_form";
            document.operateForm.vacancy_id.value = 1;
            document.operateForm.submit();
        }
        
        //IT SEEMS WORK END

        //under construction functions END
        ]]>
		</xsl:text>
        </script>


    </xsl:template>

    <!-- MAIN ENTRY -->
    <xsl:template match="/negeso:page">
        <html>
            <head>
                <title>Job module - departments</title>
                <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
                <xsl:call-template name="java-script"/>
                <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
            </head>
            <body>
                <!-- NEGESO BODY -->
                <xsl:call-template name="NegesoBody">
                    <xsl:with-param name="helpLink" select="''"/>
                    <xsl:with-param name="backLink">
                        <xsl:choose>
                            <xsl:when test="@view='department-vacancies'">
                                <xsl:text>j_module</xsl:text>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:text></xsl:text>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:with-param>
                </xsl:call-template>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="negeso:page"  mode="admContent">
        <!-- Content -->
        <form method="post" name="operateForm" action="" id="operateFormId">
            <input type="hidden" name="vacancy_id" value=""></input>
            <input type="hidden" name="command" value="job-manage-departments"></input>
            <input type="hidden" name="department_id" value=""></input>
            <input type="hidden" name="dva_id" value=""></input>
            <input type="hidden" name="action" value=""></input>

            <table align="center"  border="0" cellpadding="0" cellspacing="0" class="admTable">
                <tr>
                    <td align="center" class="admNavPanelFont" >
                        <!-- TITLE -->
                        <xsl:call-template name="tableTitle">
                            <xsl:with-param name="headtext">
                                <xsl:choose>
                                    <xsl:when test="@view='department-vacancies'">
                                        <xsl:value-of select="negeso:department/@title" />
                                        <xsl:text>. </xsl:text>
                                        <xsl:value-of select="java:getString($dict_job_module, 'VACANCIES')"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:value-of select="java:getString($dict_job_module, 'DEPARTMENTS')"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </xsl:with-param>
                        </xsl:call-template>

                    </td>
                </tr>
                <tr>
                    <td class="admNavPanel" colspan="2">
                        <xsl:choose>
                            <xsl:when test="@view='department-list'" >
                                <div class="admNavPanelInp">
                                    <div class="imgL"></div>
                                    <div>
                                        <a  class="admNavPanelInp" focus="blur()" onClick="addDepartment()" href="#addDepartment(); ">
                                            <xsl:value-of select="java:getString($dict_job_module, 'ADD_NEW_DEPARTMENT')"/>
                                        </a>
                                    </div>
                                    <div class="imgR"></div>
                                </div>

                                <div class="admNavPanelInp">
                                    <div class="imgL"></div>
                                    <div>
                                        <a class="admNavPanelInp" focus="blur()" onClick="editDefaultForm()" href="#editDefaultForm(); ">
                                              <xsl:value-of select="java:getString($dict_job_module, 'EDIT_DEFAULT_APPLICATION_FORM')"/>
                                        </a>
                                    </div>
                                    <div class="imgR"></div>
                                </div>
                                <div class="admNavPanelInp">
                                    <div class="imgL"></div>
                                    <div>
                                        <a class="admNavPanelInp" focus="blur()" href="/admin/job_regions.html">
                                              <xsl:value-of select="java:getString($job_module, 'JOB_REGIONS')"/>
                                        </a>
                                    </div>
                                    <div class="imgR"></div>
                                </div>
                            </xsl:when>

                            <xsl:when test="@view='department-vacancies'">
                                <div class="admNavPanelInp">
                                    <div class="imgL"></div>
                                    <div>
                                        <a class="admNavPanelInp" focus="blur()" onClick="addVacancy({negeso:department/@id})" href="#addVacancy({negeso:department/@id}); ">                                                                                   
                                                <xsl:value-of select="java:getString($dict_job_module, 'ADD_NEW_VACANCY')"/>                                           
                                        </a>
                                    </div>
                                    <div class="imgR"></div>
                                </div>
                            </xsl:when>

                        </xsl:choose>
                    </td>
                </tr>
                <tr>
                    <td  width="100%"  colspan="2">
                        <xsl:choose>
                            <xsl:when test="@view='department-vacancies'">
                                <xsl:apply-templates select="negeso:department/negeso:vacancies"/>
                                <xsl:if test="negeso:department/negeso:general_applications">
                                    <xsl:apply-templates select="negeso:department/negeso:general_applications" />
                                </xsl:if>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:apply-templates select="negeso:departments"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </td>
                </tr>
                <tr>
                    <td class="admTableFooter" >&#160;</td>
                </tr>
            </table>
        </form>

    </xsl:template>

    <xsl:template match="negeso:departments">
        <table  cellspacing="0" cellpadding="0"  width="100%" >
            <tr>
                <td class="admTDtitles">Department title</td>
                <td class="admTDtitles">Vacancies</td>
                <td class="admTDtitles">Applications</td>
                <td class="admTDtitles" colspan="2">&#160;</td>
            </tr>
            <xsl:call-template name="render_departments" />
        </table>
    </xsl:template>

    <xsl:template name="render_departments">
        <xsl:if test="count(negeso:department) > 0">
            <xsl:for-each select="negeso:department">
                <tr>
                    <td class="admTableTD" id="admTableTDtext">
                        <a class="admTableTDlink" href="?action=department_vacancies&amp;department_id={@id}">
                            <b>
                                <xsl:value-of select="@title" />
                            </b>
                        </a>
                    </td>
                    <td class="admTableTD" id="admTableTDtext">
                        <xsl:value-of select="@vacancies"/>
                    </td>
                    <td class="admTableTD" id="admTableTDtext">
                        <xsl:value-of select="@applications" />
                        <xsl:if test="@new_applications">
                            &#160;
                            <span class="admBlue admBold">
                                (<xsl:value-of select="@new_applications" />&#160;<xsl:value-of select="java:getString($dict_job_module, 'NEW')"/>)
                            </span>
                        </xsl:if>
                    </td>
                    <td class="admTableTDLast"  style="width:80px;cursor:pointer;">
                        <img src="/images/edit.png" class="admHand" onClick="editDepartment({@id})">
                            <xsl:attribute name="title">
                                <xsl:value-of select="java:getString($dict_common, 'EDIT')"/>
                            </xsl:attribute>
                        </img>

                        <img src="/images/delete.png" class="admHand" onClick="deleteDepartment({@id})">
                            <xsl:attribute name="title">
                                <xsl:value-of select="java:getString($dict_common, 'DELETE')"/>
                            </xsl:attribute>
                        </img>
                    </td>
                </tr>
            </xsl:for-each>
        </xsl:if>
    </xsl:template>

    <!--******************** BROWSE VACANCIES/APPLICATIONS ********************-->

    <xsl:template match="negeso:vacancies">
        <xsl:apply-templates select="negeso:vacancy" />
    </xsl:template>

    <xsl:template match="negeso:vacancy" >
        <table cellpadding="0" cellspacing="0"  width="100%">
            <tr>
                <td class="admTableTD" id="admTableTDtext" width="70%" >
                    <img  src="/images/right_white.gif" id="folder_{@id}" style="margin-right: 5px;" onclick="clickHandler(event); swapFolder('folder_{@id}');" />
                    <a href="#" class="admTableTDlink">
                        <span id="{@id}" onclick="clickHandler(event); swapFolder('folder_{@id}');">
                            <xsl:choose>
                                <xsl:when test="@person_needed > '0'">
                                    <xsl:attribute name="class">admBold</xsl:attribute>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:attribute name="class">admGray</xsl:attribute>
                                </xsl:otherwise>
                            </xsl:choose>
                            <xsl:value-of select="@title"/>
                            <xsl:if test="@person_needed = '0'">
                                &#160;(<xsl:value-of select="java:getString($dict_job_module, 'COMPLETE')"/>)
                            </xsl:if>
                        </span>
                    </a>
                    <br/>
                    <xsl:value-of select="java:getString($dict_job_module, 'REQUIRED_EMPLOYEES')"/>: <xsl:value-of select="@person_needed"/>,
                    <xsl:value-of select="java:getString($dict_job_module, 'APPLICANTS')"/>:
                    <xsl:value-of select="@applications"/>
                    <xsl:if test="number(@new_applications) > 0" >
                        <span class="admBlue admBold">
                            (<xsl:value-of select="@new_applications"/>&#160;<xsl:value-of select="java:getString($dict_job_module, 'NEW')"/>)
                        </span>
                    </xsl:if>
                </td>
                <td class="admTableTD" id="admTableTDtext">
                    <xsl:if test="@publish_date">
                        <xsl:value-of select="java:getString($dict_common, 'PUBLISH_DATE')"/>:
                        <xsl:value-of select="@publish_date"/>
                    </xsl:if>
                    <xsl:if test="@expired_date">
                        <br/>
                        <xsl:value-of select="java:getString($dict_common, 'EXPIRED_DATE')"/>:
                        <xsl:value-of select="@expired_date"/>
                    </xsl:if>
                </td>
                <td class="admTableTDLast" style="width:80px;cursor:pointer;">
                    <img src="/images/edit.png" class="admHand" onClick="editVacancy({@id})">
                        <xsl:attribute name="title">
                            <xsl:value-of select="java:getString($dict_common, 'EDIT')"/>
                        </xsl:attribute>
                    </img>

                    <img src="/images/delete.png" class="admHand" onClick="deleteVacancy({@id})">
                        <xsl:attribute name="title">
                            <xsl:value-of select="java:getString($dict_common, 'DELETE')"/>
                        </xsl:attribute>
                    </img>
                </td>
            </tr>
        </table>
        <div id="{@id}table" >
            <xsl:choose>
                <xsl:when test="number(@new_applications) > 0" >
                    <xsl:attribute name = "style">display:;</xsl:attribute>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:attribute name = "style">display: none;</xsl:attribute>
                </xsl:otherwise>
            </xsl:choose>

            <xsl:call-template name="render_applicants" />
        </div>
    </xsl:template>

    <xsl:template match="negeso:general_applications" >
        <xsl:if test="count(negeso:application) > 0">
            <table   cellpadding="0" cellspacing="0" width="100%">
                <tr>
                    <td class="admTableTDLast" id="admTableTDtext" >
                        <img class="admImg admAnchor admHand" src="/images/right_white.gif" id="folder_{@title}" style="margin-right: 5px;" onclick="clickHandler(event); swapFolder('folder_{@title}');" />
                        <a class="admTableTDlink" href="#" id="{@title}" onclick="clickHandler(event); swapFolder('folder_{@title}');">
                            <xsl:value-of select="java:getString($dict_job_module, 'GENERAL_APPLICANTS')"/>
                        </a>
                        <xsl:text>&#160;&#160;&#160;&#160;</xsl:text><xsl:value-of select="java:getString($dict_job_module, 'APPLICANTS')"/>:
                        <xsl:value-of select="@applications"/> <xsl:if test="number(@new_applications) > 0" >
                            &#160;<span >
                                (<xsl:value-of select="@new_applications"/>&#160;<xsl:value-of select="java:getString($dict_job_module, 'NEW')"/>)
                            </span>
                        </xsl:if>
                    </td>

                </tr>
            </table>
            <div id="{@title}table">
                <xsl:choose>
                    <xsl:when test="number(@new_applications) > 0" >
                        <xsl:attribute name = "style">display:;</xsl:attribute>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:attribute name = "style">display: none;</xsl:attribute>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:call-template name="render_applicants" />
            </div>
        </xsl:if>
    </xsl:template>

    <xsl:template name="render_applicants" >
        <xsl:if test="count(negeso:application) > 0">
            <table cellpadding="0" cellspacing="0"  width="100%">
                <xsl:for-each select="negeso:application">
                    <tr>
                        <td class="admTableTD" id="admTableTDtext"  colspan="2" >

                            &#160; &#160;  <a href="?action=applicant_details&amp;application_id={@id}&amp;department_id={@department_id}&amp;dva_id={@dva_id}" class="admTableTDlink">
                                <xsl:if test="@new='true'" >
                                </xsl:if>
                                <xsl:value-of select="@title" />
                            </a>
                            &#160;<xsl:value-of select="@status"/>, posted <xsl:value-of select="@post_date"/>
                        </td>
                        <td class="admTableTDLast" style="width:80px;cursor:pointer;">
                            <img src="/images/edit.png" class="admHand" onClick="editApplicant({@dva_id}, {@department_id})">
                                <xsl:attribute name="title">
                                    <xsl:value-of select="java:getString($dict_common, 'EDIT')"/>
                                </xsl:attribute>
                            </img>

                            <img src="/images/delete.png" class="admHand" onClick="deleteApplicant({@dva_id})">
                                <xsl:attribute name="title">
                                    <xsl:value-of select="java:getString($dict_common, 'DELETE')"/>
                                </xsl:attribute>
                            </img>
                        </td>
                    </tr>
                </xsl:for-each>
            </table>
        </xsl:if>
    </xsl:template>

</xsl:stylesheet>

