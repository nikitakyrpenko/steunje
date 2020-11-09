<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)$Id$        
 
  Copyright (c) 2004 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.
 
  Select page template form. Shows list of templates with default selected
 
  @author        Stanislav Demchenko
  @version        $Revision$
-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">

<!-- MAIN ENTRY -->
<xsl:template match="/">
<html>
<head>
    <title>Change page properties</title>
    <META http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link rel="stylesheet" type="text/css" href="/css/admin_style.css"/>
    <script language="JavaScript" src="/script/jquery.min.js"/>
    <script type="text/javascript" src="/script/cufon-yui.js"></script>
    <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
    <script language="JavaScript" src="/script/common_functions.js"/>
    <script language="JavaScript" src="/script/calendar_picker.js"/>
    <script language="JavaScript" src="/script/security.js"/>
    
    <!-- script block for localization mechanism -->
    <script id="localization"/>
    <script language="JavaScript">
        document.getElementById("localization").src =
            "/dictionaries/dict_dialogs_" + getInterfaceLanguage() + ".js";
    </script>
    

    <script disable-output-escaping="true">
    <![CDATA[
	       var caller = new Object();	
	       if(window.dialogArguments) {
		          caller = window.dialogArguments;
	       } else {
	           caller = window.opener;
	       }

        document.onkeypress = function() {
            try {
                if(event.keyCode == 27)
            		      window.close();
            } catch(e) {}
        }

        window.onload = function() {
            document.getElementById("buttonYes").blur();
            //document.getElementById("buttonCancel").blur();
        }

		function translatePage(){
			var isPageNameTranslatable = $('input[name=isPageNameTranslatable]').is(':checked');
			var translateToLang = "";
			$('input[name=translateToLang]:checked').each( function(){
				if(translateToLang != "") {
				 translateToLang += ",";
				}
			 	translateToLang += $(this).val();
			});
			
			var page_id = (caller.id) ? caller.id.replace(/^mi_div/,""): caller.curPageId;
			if(!page_id){
				page_id = caller.newPageId;
			}
			if(translateToLang != "") {			
			 $.ajax({
                  url: "/admin/stm_settings.html?act=translateSimplePage" + "&id=" + page_id + "&translateToLang=" + translateToLang + "&isPageNameTranslatable=" + isPageNameTranslatable +"&fromLang=" + $("#fromLang").val(),
                  dataType: "html",
                  success: function(html,stat) {                               
                  	  	$("#translateMessage").html($('#translatedPages',html).html());
                  },
                  error: function(html, stat) {
                    	$("#translateMessage").html("Internal server error occured");
                  }
              });
			}	
				
				
		}


        function returnYes(){			
			         caller.oldLink = caller.newLink;
            caller.newPageTitle = document.getElementById("pgTitle").value;
            caller.newMetaTitle = document.getElementById("metaTitle").value;
			         caller.newLink = document.getElementById("newLink").value+document.getElementById("newLinkLang").innerHTML;
			         try {
                if(caller.metatags) {
	            	      caller.newPageMetaDescr = document.getElementById("pgMetaDescr").value;
		                  caller.newPageMetaKeywords = document.getElementById("pgMetaKeywords").value;
					               if(caller.curPagePropertyName!=null && caller.curPagePropertyName!="") {
	    	        	         caller.newPagePropertyValue = document.getElementById("pgPropertyValue").value;
		                  }
		              }
		          }
			         catch(e){}
	               try {
				                if(caller.google_script_enabled) {
	            	          caller.newPageGoogleScript = document.getElementById("pgGoogleScript").value;
				                }
			             }
			             catch(e){}
			
            selContainerObj = document.getElementById("sel_container");
            caller.newContainer = selContainerObj.options[selContainerObj.selectedIndex].value;
            caller.newContainerTitle = selContainerObj.options[selContainerObj.selectedIndex].text;
            document.getElementById('container').value = caller.newContainer;
            document.getElementById('containerTitle').value = caller.newContainerTitle;
			         if (document.getElementById("container").value != caller.defaultContainerId) {
					           document.getElementById("forceVisibility").disabled = false;
            } else {
  					         document.getElementById("forceVisibility").disabled = true;
            }

            if( ("" + caller.showCheckBoxForceVisibility) == "true") {
                caller.newForceVisibility = document.getElementById("forceVisibility").checked ? "true" : "false";
            }

            if( ("" + caller.showCheckBoxIsSearch) == "true") {
                caller.newIsSearch = document.getElementById("isSearch").checked ? "true" : "false";
            }

            caller.newPublish =
                getCanonicalDate(
                     document.getElementById("publish").value.substring(0, 2),
                     document.getElementById("publish").value.substring(3, 5),
                     document.getElementById("publish").value.substring(6, 10)
                    );
            caller.newExpire =
                getCanonicalDate(
                    document.getElementById("expire").value.substring(0, 2),
                    document.getElementById("expire").value.substring(3, 5),
                    document.getElementById("expire").value.substring(6, 10)
                    );
            //window.returnValue='yes';
            try{
                caller.editProperties_respone(); 
            } 
            catch(e){
                window.returnValue='yes';
            }			

			$.get('/admin/?command=update-menu-item-command', {id : (caller.id)? caller.id.replace(/^mi_div/,""):caller.curPageId, link : caller.newLink, oldLink : caller.oldLink, "action": "checkFileName"}, checkTrue);
			
        }

        
       
		function checkTrue(d, b){
			//debugger;
			var attr=d.documentElement.attributes;
			check = true;
			for (i=0; i<attr.length;i++){
				if( (attr[i].nodeName=='check_file_name') && (attr[i].nodeValue=='failure')) {
					alert("File name '"+document.getElementById("newLink").value+document.getElementById("newLinkLang").innerHTML+"' already existed");
					check = false;
					window.returnValue='no';
				}
			}			
			if (check == true) window.close();
        }
		
        /** Returns XSD canonical date representation (CCYY-MM-DDThh:mm:ssZ) */
        function getCanonicalDate(d, m, y){
            if(d == "" || m == "" || y == "")  return "";
            return y + "-" + m + "-" + d + "T00:00:00Z";
        }
		
        /**
         * Formats XSD canonical date representation (CCYY-MM-DDThh:mm:ssZ)
         * into European date presentation (dd-mm-yyyy)
         */
        function formatDate(canonDate){
            canonDate = "" + canonDate;
            if(canonDate.charAt(4) != '-' || canonDate.charAt(7) != '-'){
                return "";
            }
            return (canonDate.substring(8, 10) + "-" +
                    canonDate.substring(5, 7) + "-" +
                    canonDate.substring(0, 4)
                    );
        }
        
        /** Returns '1' as '01', ..., '9' as '09'. Values beyond these bounds
        * are returned unchanged.
        */
        function prependZero(num){
            return num > 9 ? "" + num : "0" + num;
        }
 
        var today = new Date();
        var yesterday = new Date(today.getFullYear(), today.getMonth(), today.getDate() - 2);
        //attachEvent ("onload", resizeDialogWindow); //resize dialog window height
        
        function showPage(){
            document.getElementById("expire").value = "";
            document.getElementById("publish").value = 
                prependZero(yesterday.getDate()) + "-" + 
                prependZero(yesterday.getMonth() + 1) + "-" + 
                prependZero(yesterday.getFullYear());
        }

        function hidePage(){
            document.getElementById("expire").value =
                prependZero(yesterday.getDate()) + "-" +
                prependZero(yesterday.getMonth() + 1) + "-" +
                prependZero(yesterday.getFullYear());
            document.getElementById("publish").value = "";
        }

        // Choose container: Begin
		function setSecurity(){
			NewSecurity.setContainer = setSecurity_responseOK;
			NewSecurity.selectContainerDialog(document.getElementById("container").value, "manager");
			if (document.getElementById("container").value != caller.defaultContainerId) {
					document.getElementById("forceVisibility").disabled = false;
                } else {
  					document.getElementById("forceVisibility").disabled = true;
            }
		}

		function setSecurity_responseOK() {
			document.getElementById("container").value = NewSecurity.return_value.containerId;
			document.getElementById("containerTitle").value = NewSecurity.return_value.containerTitle;
		}
		// Choose container: End
    ]]>        
    </script>
</head>
<body style="padding-left:12px">
    <table class="admMain"  align="center" border="0" cellpadding="0" cellspacing="0" >
        <tr>
            <td colspan="3" class="admMainLogo"><img src="/images/logo.png" /></td>
        </tr>
        <tr >
            <td class="admConnerLeft"></td>
            <td class="admTopBtn">
                <div class="admBtnGreen">
                    <div class="imgL"></div>
                    <div><a class="admBtnText" href="#" onclick="return window.close()" id="BACK_TOP" onfocus="blur()"/></div>
                    <div class="imgR"></div>
                </div>
                <div class="admBtnGreen">
                    <div class="imgL"></div>
                    <div><a class="admBtnText" href="#" onclick="return window.close()" id="CLOSE_TOP" onfocus="blur()"/></div>
                    <div class="imgR"></div>
                </div>               
            </td>
            <td class="admConnerRight"></td>
        </tr>
        <tr>
            <td class="admMainLeft"><img src="/images/left_bot.png" /></td>
            <td>
                <table align="center" border="0" cellpadding="0" cellspacing="0" class="admTable">                    
                    <tr>
                        <td align="center" class="admNavPanelFont" id="CHANGE_PAGE_PROPERTIES" colspan="2"></td>
                    </tr>
                    <xsl:if test="count(//negeso:language) > 1">
                        <tr>
                            <td colspan="1"  class="admTableTDLast" style="padding: 0 0 0 20px;">Translate this page to unlinked page:
                            <xsl:for-each select="//negeso:language">
                               <xsl:if test="not(@selected)">                           
                                   <br/><input type="checkbox" name="translateToLang" value="{@id}"/>&#160;<xsl:value-of select="@name"/>
                               </xsl:if>
                            </xsl:for-each>
                            <input type="hidden" id="fromLang" value="{//negeso:language[@selected='true']/@id}"></input>
                            </td>
                            <td class="admTableTDLast">
                            	<div id="translateMessage"></div>
                            </td>
                        </tr>
                        
                        <tr>
                            <td  class="admTableTDLast" style="padding: 0 0 0 20px;">
                               <input type="checkbox" name="isPageNameTranslatable" />&#160;
                               <label id="INCLUDING_PAGE_URL" ></label>
                            </td>
                           
                            <td class="admTableTDLast">
                            	 <div class="admNavPanelInp" style="float: left; padding-left: 0px;">
                                         <div class="imgL"></div>
                                          <div align="center">
                                         	<a class="admNavPanelInp" style="width:83px;" focus="blur()" href="#" onclick="translatePage();return false;" id="buttonTranslate" onfocus="blur()"/>
                                         </div>
                                         <div class="imgR"></div>
                                     </div>
                            </td>
                            
                        </tr>
                    </xsl:if>
                    <tr>
                        <td class="admTableTDLast" style="padding: 0 0 0 20px;" id="PAGE_TITLE" width="384"></td>
                        <td class="admTableTDLast" id="admTableTDtext" width="380">
                            <input type="text" id="pgTitle" class="admTextArea"/>
                            <script>
                                document.getElementById("pgTitle").value = caller.newPageTitle;
                            </script>
                        </td>
                    </tr>
                    <tr>
                        <td class="admTableTDLast" style="padding: 0 0 0 20px;" id="META_TITLE"/>
                        <td class="admTableTDLast" id="admTableTDtext">
                            <input type="text" id="metaTitle" class="admTextArea"/>
                            <script>
                                document.getElementById("metaTitle").value = caller.newMetaTitle;
                            </script>
                        </td>
                    </tr>
                    <tr>
                        <td class="admTableTDLast" style="padding: 0 0 0 20px;" id="FILE_NAME"/>
                        <td class="admTableTDLast" id="admTableTDtext">
                            <input type="text" id="newLink" class="admTextArea"/>
                            <span id="newLinkLang"></span>
                            <span id="protected" style="display:none;font-size:11px;">
                                <br/>You can't rename file name for this page!</span>
                            <script>
                                //debugger;
                                var filename = caller.newLink.replace(/(.html)|(_[a-z][a-z].html)$/,"");
                                document.getElementById("newLink").value = filename;
                                document.getElementById("newLinkLang").innerHTML=caller.newLink.replace(filename,"");
                                if(caller.pageProtected=='nodelete'){
                                document.getElementById("newLink").disabled = true;
                                document.getElementById("protected").style.display="block";
                                }
                            </script>
                        </td>
                    </tr>
                    <tr>
                        <td class="admTableTDLast" style="padding: 0 0 0 20px;" id="PAGE_META_DESCRIPTION"/>
                        <td class="admTableTDLast" id="admTableTDtext">
                            <script>
                                if(caller.metatags=='true') {
                                document.write('<textarea id="pgMetaDescr" class="admTextArea" rows="3">'+caller.newPageMetaDescr+'</textarea>');
                                } else {
                                document.write('<textarea id="pgMetaDescr" class="admTextArea" rows="3" disabled="true" readonly="true">'+caller.newPageMetaDescr+'</textarea>');
                                }
                            </script>
                        </td>
                    </tr>
                    <tr>
                        <td class="admTableTDLast" style="padding: 0 0 0 20px;" id="PAGE_META_KEYWORDS"/>
                        <td class="admTableTDLast" id="admTableTDtext">
                            <script>
                                if(caller.metatags=='true') {
                                document.write('<textarea id="pgMetaKeywords" class="admTextArea" rows="3">'+caller.newPageMetaKeywords+'</textarea>');
                                } else {
                                document.write('<textarea id="pgMetaKeywords" class="admTextArea" rows="3" disabled="true" readonly="true">'+caller.newPageMetaKeywords+'</textarea>');
                                }
                            </script>
                        </td>
                    </tr>
                    <tr>
                        <td class="admTableTDLast" style="padding: 0 0 0 20px;" id="PAGE_GOOGLE_SCRIPT"/>
                        <td class="admTableTDLast" id="admTableTDtext">
                            <script>
                                if(caller.google_script_enabled=='true') {
                                document.write('<textarea id="pgGoogleScript" class="admTextArea" rows="3">'+caller.newPageGoogleScript+'</textarea>');
                                } else {
                                document.write('<textarea id="pgGoogleScript" class="admTextArea" rows="3" disabled="true" readonly="true">'+caller.newPageGoogleScript+'</textarea>');
                                }
                            </script>
                        </td>
                    </tr>
                    <xsl:text disable-output-escaping="yes"><![CDATA[
                        <script>
                        if(caller.metatags=='true') {
	                        if(caller.curPagePropertyName!=null && caller.curPagePropertyName!="") {
		                        document.write('<tr><td class="admMainTD admRight admWidth150">'+caller.curPagePropertyName+'</td>'+
		                        '<td class="admTableTDLast" id="admTableTDtext"><textarea id="pgPropertyValue" class="admTextArea" rows="3">'+
		                        caller.newPagePropertyValue + '</textarea></td></tr>');
	                        }
                        }
                        </script>
                    ]]></xsl:text>
                    <tr>
                        <td class="admTableTDLast" style="padding: 0 0 0 20px;" id="PUBLISH_ON"/>
                        <td class="admTableTDLast" id="admTableTDtext">
                            <table width="100%">
                                <tr>
                                    <td>
                                        <input class="admTextArea admWidth100" type="text" id="publish" 
                                               readonly="true"/><script>document.getElementById("publish").value = formatDate(caller.newPublish);</script><img 
                                               class="admHand" src="/images/calendar.gif" width="16" height="16" hspace="7" vspace="2" 
                                               onclick="loadNewCalendar('/dialogs/dialog_calendar_picker.html','publish','ddmmyyyy',false)" 
                                               align="absmiddle" onfocus="blur()"/><span class="admLocation">(dd-mm-yyyy)</span>
                                    </td>
                                    <td  align="left">
                                        <div class="admNavPanelInp" style="padding:0;">
                                            <div class="imgL"></div>
                                            <div align="center">
                                                <a class="admNavPanelInp" style="width:83px;" focus="blur()" href="#" onclick="showPage();return false;" id="buttonShow" onfocus="blur()"/>
                                            </div>
                                            <div class="imgR"></div>
                                        </div>
                                    </td>
                                </tr>
                            </table>                            
                        </td>
                    </tr>
                    <tr>
                        <td class="admTableTDLast" style="padding: 0 0 0 20px;" id="EXPIRES_ON"/>
                        <td class="admTableTDLast" id="admTableTDtext">
                            <table width="100%">
                                <tr>
                                    <td>
                                        <input class="admTextArea admWidth100" type="text" id="expire" 
                                               readonly="true"/><script> document.getElementById("expire").value = formatDate(caller.newExpire); </script><img 
                                               class="admHand" src="/images/calendar.gif" width="16" height="16" hspace="7" vspace="2" 
                                               onclick="loadNewCalendar('/dialogs/dialog_calendar_picker.html','expire','ddmmyyyy',false)" 
                                               align="absmiddle"/><span class="admLocation">(dd-mm-yyyy)</span>
                                    </td>
                                    <td  align="left">
                                        <div class="admNavPanelInp" style="padding:0;">
                                            <div class="imgL"></div>
                                            <div align="center"><a class="admNavPanelInp" focus="blur()" style="width:83px;" href="#" onclick="hidePage()" id="buttonHide" onfocus="blur()"/>
                                            </div>
                                            <div class="imgR"></div>
                                        </div>
                                    </td>
                                </tr>
                            </table>                           
                        </td>
                    </tr>
                    <tr>
                        <td class="admTableTDLast" style="padding: 0 0 0 20px;" id="LAST_EDIT_DATE"/>
                        <td class="admTableTDLast" id="admTableTDtext">
                            <input class="admTextArea admWidth150" type="text" id="edit_date" readonly="true" disabled="disabled"/>
                            <script> document.getElementById("edit_date").value = caller.newEditDate; </script>
                        </td>
                    </tr>
                    <tr>
                        <td class="admTableTDLast" style="padding: 0 0 0 20px;" id="LAST_EDIT_USER"/>
                        <td class="admTableTDLast" id="admTableTDtext">
                            <input class="admTextArea admWidth150" type="text" id="edit_user" readonly="true" disabled="disabled"/>
                            <script> document.getElementById("edit_user").value = caller.newEditUser; </script>
                        </td>
                    </tr>
                    <tr>
                        <td class="admTableTDLast" style="padding: 0 0 0 20px;" id="PAGE_TEMPLATE"/>
                        <td class="admTableTDLast" id="admTableTDtext">
                            <input class="admTextArea admWidth150" type="text" id="page_class" readonly="true" disabled="disabled"/>
                            <script> document.getElementById("page_class").value = caller.newPageClass; </script>
                        </td>
                    </tr>
                    <tr>
                        <td class="admTableTDLast" style="padding: 0 0 0 20px;" id="CONTAINER"/>
                        <td class="admTableTDLast" id="admTableTDtext">
                            <select class="admTextArea" id="sel_container">
                                <script language="JavaScript">
                                    for(i = 0; i&lt; caller.newArrContainer.length; i++){
                                    if("" + caller.newArrContainer[i][0] == "" + caller.newContainer) opt_sel = 'selected';
                                    else opt_sel = '';
                                    document.write('&lt;option ' +opt_sel+ ' value="'+caller.newArrContainer[i][0]+'" &gt;'+caller.newArrContainer[i][1]+'&lt;/option&gt;');
                                    }
                                    if(caller.newContainerTotal &lt; 2 || caller.newArrContainer.length == 1)
                                    document.getElementById('sel_container').disabled="true";
                                </script>
                            </select>
                            <input class="admText" type="hidden" id="containerTitle" readonly="true"/>
                            <input class="admText" type="hidden" id="container" readonly="true"/>
                        </td>
                    </tr>
                    <tr id="containerOfForceVisibility" style="display: none;">
                        <td class="admTableTDLast" style="padding: 0 0 0 20px;" id="FORCE_VISIBILITY"/>
                        <td class="admTableTDLast" id="admTableTDtext">
                            <input type="checkbox" id="forceVisibility" class="radio"/>
                            <script>
                                if( ("" + caller.showCheckBoxForceVisibility) == "true") {
                                document.getElementById("containerOfForceVisibility").style.display = "block";
                                document.getElementById("forceVisibility").checked = caller.newForceVisibility == "true" ? true : false;
                                document.getElementById("forceVisibility").disabled = caller.newContainer == caller.defaultContainerId;
                                }
                            </script>
                        </td>
                    </tr>
                    <tr id="containerOfIsSearch" style="display: none;">
                        <td class="admTableTDLast" style="padding: 0 0 0 20px;" id="IS_SEARCH"/>
                        <td class="admTableTDLast" id="admTableTDtext">
                            <input type="checkbox" id="isSearch" class="radio"/>
                            <script>
                                if( ("" + caller.showCheckBoxIsSearch) == "true") {
                                    document.getElementById("containerOfIsSearch").style.display = "block";
                                    document.getElementById("isSearch").checked = caller.newIsSearch == "true" ? true : false;
                                }
                            </script>
                        </td>
                    </tr>
                    <tr id="containerOfImages" style="display: none; margin: 0; padding: 0">
                        <td class="admTableTDLast">
                            <table>
                                <tr>
                                    <td style="padding: 0 0 0 15px;" id="IMAGES"  />
                                    <td class="admNavChoose" style="padding:0 0 0 5px;">
                                        <script>
                                            // TODO !!!
                                            function selectImages(){
                                            //window.showModalDialog(
                                            window.open(
                                            "/admin/browse_wcms_attrs?page_id=" + caller.newPageId,
                                            "Imagesediting",                                            
                                            "height=700px,width=850px,help=No,scroll=yes,scroll=on,scrollbars=yes,status=No,resizable=yes"
                                            );
                                            }
                                            function selectAliases(){
                                            //window.showModalDialog(
                                            window.open(
                                            "/admin/site_settings?action=getUrlAliases&amp;page_id=" + caller.newPageId,
                                            "Imagesediting",
                                            //"height=700px,width=850px,help=No,scroll=yes,scroll=yes,scrollbars=yes,status=yes,resizable=yes"
                                            "height=700px,width=850px,help=No,scroll=yes,scroll=on,scrollbars=yes,status=No,resizable=yes"
                                            );
                                            }
                                            if(typeof(caller.newPageId) != "undefined") {
                                            document.getElementById("containerOfImages").style.display = "block";
                                            }
                                        </script>
                                        <div align="center" >
                                            <input type="button" value="..." style="width: 24px; height: 21px;" onclick="selectImages()" onfocus="blur()"/>
                                        </div>
                                    </td>
                                </tr>
                            </table>
                        </td>
                        <td class="admTableTDLast">
                            <table>
                                <tr>
                                    <td style="padding: 0 0 0 5px;"  id="Aliases" > Edit Aliases </td>
                                    <td class="admNavChoose" >
                                        <div align="center">
                                            <input type="button" value="..." style="width: 24px; height: 21px;" onclick="selectAliases()" onfocus="blur()"/>
                                        </div>
                                    </td>
                                </tr>
                            </table>
                        </td>                        
                    </tr>
                    <tr>
                        <td colspan="2" class="admTableFooter"></td>
                    </tr>
                </table>
            </td>
            <td class="admMaiRight">
                <img src="/images/right_bot.png" />
            </td>
        </tr>
        <tr >
            <td colspan="3"  style="padding:0 0 0 20px;" >
                <div class="admBtnGreenb" >
                    <div class="imgL"></div>
                    <div>
                        <input type="button" onclick="returnYes()" id="buttonYes" class="admNavbarInp"/>
                    </div>
                    <div class="imgR"></div>
                </div>
                
                <div class="admBtnGreenb" >
                    <div class="imgL"></div>
                    <div>
                        <input type="button" onclick="returnYes(); window.close();" id="buttonSaveAndClose" class="admNavbarInp"/>
                    </div>
                    <div class="imgR"></div>
                </div>
                
            </td>
        </tr>
    </table> 
</body>

<script disable-output-escaping="true">
    <![CDATA[
    window.document.title = Strings.CHANGE_PAGE_PROPERTIES;
    window.document.getElementById("BACK_TOP").innerHTML = Strings.BACK;
    window.document.getElementById("CLOSE_TOP").innerHTML = Strings.CLOSE;
   
    window.document.getElementById("CHANGE_PAGE_PROPERTIES").innerHTML = Strings.CHANGE_PAGE_PROPERTIES;
    window.document.getElementById("PAGE_TITLE").innerHTML = Strings.PAGE_TITLE;
	window.document.getElementById("FILE_NAME").innerHTML = Strings.FILE_NAME;
    window.document.getElementById("pgTitle").title = Strings.PAGE_TITLE_IS_DISPLAYED;
    window.document.getElementById("PUBLISH_ON").innerHTML = Strings.PUBLISH_ON;
    window.document.getElementById("EXPIRES_ON").innerHTML = Strings.EXPIRES_ON;
    window.document.getElementById("CONTAINER").innerHTML = Strings.CONTAINER;
    window.document.getElementById("IS_SEARCH").innerHTML = "Include this<br>page in search";
    window.document.getElementById("INCLUDING_PAGE_URL").innerHTML = Strings.INCLUDING_PAGE_URL;
    window.document.getElementById("buttonTranslate").innerHTML = Strings.STM_TRANSLATE;
    
    if (caller.new_wcms_attributes != ""){
       window.document.getElementById("IMAGES").innerHTML = "Edit images"; 
    }else{
        var containerOfImages = window.document.getElementById("containerOfImages");
        containerOfImages.parentNode.removeChild(window.document.getElementById("containerOfImages"));     	
    }			  
    
    window.document.getElementById("FORCE_VISIBILITY").innerHTML  = Strings.FORCE_VISIBILITY;
    window.document.getElementById("buttonShow").innerHTML = Strings.SHOW_PAGE;
    window.document.getElementById("buttonHide").innerHTML =Strings.HIDE_PAGE ;
    window.document.getElementById("buttonSaveAndClose").value = Strings.SAVE_AND_CLOSE ;
    window.document.getElementById("buttonYes").value = Strings.SAVE ;
    window.document.getElementById("buttonShow").title = Strings.SET_PUBLICATION_DATE_TO;
    window.document.getElementById("buttonTranslate").title = Strings.STM_TRANSLATE;
    window.document.getElementById("buttonHide").title = Strings.SET_EXPIRATION_DATE_TO;

    window.document.getElementById("PAGE_META_DESCRIPTION").innerHTML = Strings.PAGE_META_DESCRIPTION;
    window.document.getElementById("PAGE_META_KEYWORDS").innerHTML = Strings.PAGE_META_KEYWORDS;
    window.document.getElementById("LAST_EDIT_DATE").innerHTML = Strings.LAST_EDIT_DATE;
    window.document.getElementById("LAST_EDIT_USER").innerHTML = Strings.LAST_EDIT_USER;
	window.document.getElementById("PAGE_TEMPLATE").innerHTML = Strings.PAGE_TEMPLATE;
	window.document.getElementById("META_TITLE").innerHTML = Strings.META_TITLE;
	if(caller.google_script_enabled=='true') {
    	window.document.getElementById("PAGE_GOOGLE_SCRIPT").innerHTML = Strings.PAGE_GOOGLE_SCRIPT;
    } else {
    	window.document.getElementById("PAGE_GOOGLE_SCRIPT").innerHTML = "<span>" + Strings.PAGE_GOOGLE_SCRIPT + "</span>";
    }
    ]]>
</script>

</html>
</xsl:template>


</xsl:stylesheet>