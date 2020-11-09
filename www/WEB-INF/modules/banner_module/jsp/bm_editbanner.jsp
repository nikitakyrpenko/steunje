<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="negeso" %>
<%@ page session="true"%>
<%@ page import="com.negeso.module.banner_module.bo.Banner"%>
<%@ page import="com.negeso.module.banner_module.bo.BannerType"%>
<%@ page import="java.util.Collection"%>
<%@ page import="java.util.Iterator"%>
<%
	response.setHeader("Expires", "0");
	Banner banner = (Banner) request.getAttribute("banner");
	String bannerPages = (String) request.getAttribute("bannerpages");
	String bannerGroups = (String) request.getAttribute("bannergroups");
	Object o = request.getAttribute("bannertype");
	BannerType bannerType = (o==null?null:(BannerType) o);
%>
<html>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
    <title>
        <c:choose>
            <c:when test="${banner.id == -1}">
                <fmt:message key="BM.ADD_BANNER"/>
            </c:when>
            <c:otherwise>
                <fmt:message key="BM.EDIT_BANNER"/>
            </c:otherwise>
        </c:choose>
    </title>
    
    <link rel="stylesheet" type="text/css" href="/css/admin_style.css"/>
    <link rel="stylesheet" type="text/css" href="/css/customized-jquery-ui.css"/>
    <link rel="stylesheet" type="text/css" href="/css/jquery-ui.css"/>
    <link rel="stylesheet" type="text/css" href="/css/jquery-ui.custom.css"/>
    <script language="JavaScript" src="/script/jquery.min.js">/**/</script>
    <script type="text/javascript" src="/script/jquery-ui.custom.min.js">/**/</script>
    <script language="JavaScript" src="/script/common_functions.js">/**/</script>
    <script language="JavaScript" src="/script/media_catalog.js">/**/</script>        
    
</head>

<body>
	<script type="text/javascript">
		function duplicate(banner_id){
			addr = "bm_bannerlist.html?action=duplicateBanner&bannerId=" + banner_id; 
	        attr = "resizable:yes,scroll:yes,scrollbars=yes,status:no,dialogWidth:365px,dialogHeight:500px";
        	dp = window.open(addr, 'duplicate_banner', attr);
        	dp.focus();
			
		}
		
		function activate(){
			if(document.getElementById('prev_activated_flag').value == "true" && !document.forms['bm_form_banner'].elements['activated'].checked){
				if(!confirm('<fmt:message key="BM.INACTIVATE_WARNING"/>'))
					document.forms['bm_form_banner'].reset();
			}
		}
		
		function saveAndCloseForm(){
			document.forms['bm_form_banner'].elements['save_and_close'].value = 'true';
			saveForm();	
		}
				
		function saveForm(){
			var err = '';
			if(document.forms['bm_form_banner'].elements['Views'].checked== true)
				document.forms['bm_form_banner'].elements['maxViews'].value = '0';
			if(document.forms['bm_form_banner'].elements['Clicks'].checked == true)
				document.forms['bm_form_banner'].elements['maxClicks'].value = '0';
			if(document.forms['bm_form_banner'].elements['imageUrl'].value == '')
				document.forms['bm_form_banner'].elements['imageUrl'].value = '/images/no_image_98.gif';
				
			if(
				document.forms['bm_form_banner'].elements['bm_pages'].value == '####' ||
				document.forms['bm_form_banner'].elements['bm_pages'].value == '' ||
				document.forms['bm_form_banner'].elements['bm_pages'].value == null
			)
				err = err + '<fmt:message key="BM.NO_SELECTED_PAGES"/>';
				
			if(
				document.forms['bm_form_banner'].elements['bm_groups'].value == '' ||
				document.forms['bm_form_banner'].elements['bm_groups'].value == null
			)
				err = err + '\n<fmt:message key="BM.NO_SELECTED_GROUPS"/>';

			try
			{
				var a = document.forms['bm_form_banner'].elements['maxClicks'].value;
				if (a >= 0 && a < 100000){}
				else{
					err = err + '\n<fmt:message key="BM.MAXCLICKS_ERROR"/>';
				}
				a = document.forms['bm_form_banner'].elements['maxViews'].value;
				if (a >= 0 && a < 100000){}
				else{
					err = err + '\n<fmt:message key="BM.MAXVIEWS_ERROR"/>';
				}
			}
			catch(err)
			{
				alert('error');
			}


			if(validate(document.forms['bm_form_banner'])){
				if(err == '')
					document.forms['bm_form_banner'].submit();
				else
					alert(err);
			}
			else{
				if(err == '') err = '<fmt:message key="BM.FILL_OBLIGATORY_FIELDS"/>';
				else err = err + "\n<fmt:message key="BM.FILL_OBLIGATORY_FIELDS"/>";
				alert(err);
			}
		};

		function page_chooser(banner_id){
			addr = "bm_bannerlist.html?action=pageChooser&bannerId=" + banner_id;
	        attr = "resizable=yes, scroll=yes, scrollbars=yes, status=no, height=550, width=800";
        	pc = window.open(addr, 'page_chooser', attr);
        	pc.focus();
		}
		
		function link_chooser(banner_id){
			addr = "bm_bannerlist.html?action=pageChooser&bannerId=" + banner_id + "&linkchooser=true"; 
	        attr = "resizable=yes, scroll=yes, scrollbars=yes, status=no, height=550, width=800";
        	pc = window.open(addr, 'link_chooser', attr);
        	pc.focus();
		}

		function group_chooser(banner_id){
			addr = "bm_bannerlist.html?action=groupChooser&bannerId="+banner_id;
	        attr = "resizable=yes, scroll=yes, scrollbars=yes, status=no, height=550, width=800";
        	pc = window.open(addr, 'group_chooser', attr);
        	pc.focus();
		}

		function view_stat(banner_id){
			addr = "bm_bannerlist.html?action=showStatistics&bannerId=" + banner_id; 
	        attr = "resizable=yes, scroll=yes, scrollbars=yes, status=no, height=550, width=800";
        	stat = window.open(addr, 'view_stat', attr);
        	stat.focus();
		}
		
		function maxViewsClicks(ch_name, block){
			if(block == null || block == 'undefined')
				block = false;
				
			if (block) flag = true;
			else flag = false;
				
			if(document.forms['bm_form_banner'].elements[ch_name].checked == flag )
				document.forms['bm_form_banner'].elements['max' + ch_name].disabled = false;
			else
				document.forms['bm_form_banner'].elements['max' + ch_name].disabled = true;
		}

        function selectBannerDialog(){
        	var bannertype = document.forms['bm_form_banner'].elements['bannerTypeId'].options[document.forms['bm_form_banner'].elements['bannerTypeId'].selectedIndex].text;
        	
        	var arr1 = bannertype.split("(");
        	var arr2 = arr1[1].split(")");
        	var arr3 = arr2[0].split("x");
        	var width = parseInt(arr3[0]);
        	var height = parseInt(arr3[1]);
			MediaCatalog.selectBannerDialog(width, height, "banner_module", 600, 550);
		}


		function resultUploadBanner() {
			var result = returnValue;
			if (result != null){
				if (result.resCode == "OK"){
					var obj_banner_img = document.getElementById('banner_image');
					
					// imageType = Image
					if(result.imageType == '1'){
						obj_banner_img.innerHTML = '<img id="imgTagId" class="admBorder"  src="'+result.realBanner+'"/>';
					}
					
					// imageType = Flash
					if(result.imageType == '2'){
						
						obj_banner_img.innerHTML = '\
							<object width="'+result.realBannerWidth+'" height="'+result.realBannerHeight+'" id="flashTagId" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codeBase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,29,0">\
							<param VALUE="24553" NAME="_cx">\
							<param VALUE="1376" NAME="_cy">\
							<param VALUE="'+result.realBanner+'" NAME="Movie"/> \
							<param VALUE="'+result.realBanner+'" NAME="Src" /> \
							<param VALUE="Opaque" NAME="WMode">\
							<param VALUE="High" NAME="Quality"><param VALUE="-1" NAME="Menu"><param VALUE="ShowAll" NAME="Scale">\
							<embed id="flashTagId2" wmode="opaque" type="application/x-shockwave-flash" scale="showall"\
								quality="high" pluginspage="http://www.macromedia.com/go/getflashplayer" menu="false"\
								width="'+result.realBannerWidth+'" height="'+result.realBannerHeight+'" src="'+result.realBanner+'"\
							></embed> \
						</object> \
						';
					}
					
					document.getElementById('imageFieldId').value = result.realBanner;
					document.forms['bm_form_banner'].elements['imageType'].value = result.imageType;
				}
			}
		}
		$(function () {
			$("#publishDateBanner,#expiredDateBanner").datepicker({
        		dateFormat: 'yy-mm-dd',        
	    	    showOn: 'button',
		        buttonImage: '/images/calendar.gif',
        		buttonImageOnly: true
		    });	
	    });	
		
	</script>

	<table class="admMain"  align="center" border="0" cellpadding="0" cellspacing="0">
      <tr>
          <td class="admConnerLeft"></td>
          <td class="admTopBtn">
              <div><img src="/images/logo.png"  class="admMainLogo" /></div>
              <br></br>
              <jsp:include page="/WEB-INF/jsp/common/back_bar.jsp"/>
          </td>
          <td class="admConnerRight"></td>
      </tr>
      <tr>
          <td class="admMainLeft"><img src="/images/left_bot.png" /></td>
          <td>
          
          
    <!-- TITLE: begin -->
    <table align="center" border="0" cellpadding="0" cellspacing="0" class="admTable">
    	<tr>
        	<td align="center" class="admNavPanelFont"  style="height:auto;" colspan="2" >
            	<c:choose>
            		<c:when test="${banner.id == -1}">
            			<fmt:message key="BM.ADD_BANNER"/>
            		</c:when>
            		<c:otherwise>
            			<fmt:message key="BM.EDIT_BANNER"/>
            		</c:otherwise>
            	</c:choose>
            </td>
        </tr>
        <c:if test="${errs ne null}">
          <c:forEach var="l" items="${errs}">
              <tr>
                  <td colspan="2" class="admTableTDLast" style="font-size: 10px; color: red; border: 0; padding: 0; text-align: left; vertical-align: middle; height: 12px;" >
                      <fmt:message key="${l}"/>
                  </td>
              </tr>
          </c:forEach>
        </c:if>
    <!-- TITLE: end -->
    	<form name="bm_form_banner" action="/admin/bm_banner.html" method="post">
	    <input type="hidden" name="id" value="${banner.id}">
	    <c:if test="${banner.categoryId != null}">
	    	<input type="hidden" name="categoryId" value="${banner.categoryId}">
	    </c:if>
			<tr>
				<th class="admTableTD">
					<fmt:message key="BM.TITLE"/>*
				</th>
				<td class="admTableTDLast">
					<spring:bind path="banner.title">
						<input required="true" class="admTextArea admWidth200" type="text" name="title" value="${banner.title}"
							<c:if test="${banner.activated == true}" >
			    				<c:out value="readonly='readonly'" />
			    			</c:if>
						/>
						<spring:hasBindErrors name="banner.title">
							<c:forEach items="${status.errorMessages}" var="error">
								<table cellspacing="0" cellpadding="0" style="height: 12px; margin: 0; padding: 0; border: 0;">
									<tr style="height: 12px;">
										<td style="font-size: 10px; color: red; border: 0; padding: 0; text-align: left; vertical-align: middle; height: 12px;">
											<c:out value="${error}"/>
										</td>
									</tr>
								</table>
			                </c:forEach>
						</spring:hasBindErrors>
					</spring:bind>
				</td>
	    	</tr>
	    	<tr>
	    		<th class="admTableTD">
	    			<fmt:message key="BM.BANNER_TYPE"/>
	    		</th>
	    		<td class="admTableTDLast">
	    			<select class="admTextArea admWidth200" name="bannerTypeId"
	    			<%
		    			if (banner.getActivated()){
		    				out.write("disabled=\"disabled\"");
		    			}
	    			%>
	    			>
	    			<%
		    			Collection bannertypes = (Collection) request.getAttribute("bannertypes");
	    				if (bannertypes != null){
		    				Iterator iter = bannertypes.iterator();
		    				while(iter.hasNext()){
		    					BannerType b = (BannerType)iter.next();
		    					out.write("<option value=\""+ b.getId()+"\"");
		    					if(banner.getBannerTypeId()!= null && b.getId().equals(banner.getBannerTypeId()))
		    						out.write(" selected=\"selected\"");
		    					out.write(">" + b.getTitle() + " ("+b.getWidth()+"x"+b.getHeight()+")");
		    					out.write("</option>");
		    				}
	    				}
	    			%>
	    			</select>
	    			<%
	    				if (banner.getActivated()){
	    					out.write("<input type=\"hidden\" name=\"bannerTypeId\" value=\"" + banner.getBannerTypeId() + "\" />");
	    				}
	    			%>
	    			<a href="/images/banner_module/banner_map.jpg" target="_blank"><fmt:message key="BM.BANNER_MAP"/></a>
	    		</td>
	    	</tr>
	    	<tr>
	    		<th class="admTableTD">
	    			<fmt:message key="BM.BANNER_IMAGE"/>*
	    		</th>
	    		<td class="admTableTDLast">
	    			<div style="width:600px; overflow:auto;">
						<input required="true" type="hidden" id="imageFieldId" name="imageUrl" value="${banner.imageUrl}" MAXLENGTH="100"></input>
						<div id="banner_image">
							<%
								if(banner.getImageType() == 1L){
								%>
									<img id="imgTagId" class="admBorder" 
										<c:choose>
											<c:when test="${banner.imageUrl != null}">
												<c:out value="src=${banner.imageUrl}"/>
											</c:when>
											<c:when test="${banner.imageUrl == null}">
												<c:out value="src=/images/no_image_98.gif"/>
											</c:when>
										</c:choose>
									/>
								<%
								}
							
								if(banner.getImageType() == 2L){
								%>
									<object id="flashTagId" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codeBase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,29,0"
											<%
												if(bannerType != null){
													out.write("width=\""+bannerType.getWidth()+"\""); 
													out.write("height=\""+bannerType.getHeight()+"\""); 
												}
											%>
									>
										<param VALUE="24553" NAME="_cx">
										<param VALUE="1376" NAME="_cy">
										<param VALUE="${banner.imageUrl}" NAME="Movie" id="flash_id_movie"/> 
										<param VALUE="${banner.imageUrl}" NAME="Src" id="flash_id_src" /> 
										<param VALUE="Opaque" NAME="WMode">
										<param VALUE="High" NAME="Quality"><param VALUE="-1" NAME="Menu"><param VALUE="ShowAll" NAME="Scale">
									
										<embed id="flashTagId2" wmode="opaque" type="application/x-shockwave-flash" scale="showall" quality="high" pluginspage="http://www.macromedia.com/go/getflashplayer" menu="false"
											<c:out value="src=${banner.imageUrl}"/>
											<%
												if(bannerType != null){
													out.write("WIDTH=\""+bannerType.getWidth()+"\" "); 
													out.write("HEIGHT=\""+bannerType.getHeight()+"\" "); 
												}
											%>
										></embed>
									</object>
								<%
								}
							%>
						</div>
						<input type="hidden" name="imageType" value="${banner.imageType}"/>
	    			</div>
                    <div class="admNavPanelInp" style="padding:0;margin:0;">
                        <div class="imgL"></div>
                        <div>
                        	<input type="button" class="admNavbarInp" value="<fmt:message key="BM.SELECT"/>" name="selectBannerButton"
									<%
										if (!banner.getActivated()){
											out.write("onclick=\"selectBannerDialog()\"");
										}

										if (banner.getActivated()){
											out.write("disabled=\"disabled\"");
										}
									%>
								/>
                        </div>
                        <div class="imgR"></div>
                    </div>						
				</td>
			</tr>
	    	<tr>
	    		<th class="admTableTD">
	    			<fmt:message key="BM.BANNER_PRIORITY"/>
	    		</th>
	    		<td class="admTableTDLast">
	    			<table cellpadding="0" cellspacing="0" align="left">
	    				<tr>
	    					<td>
		    					<select class="admTextArea admWidth50" name="priority"
				    				<c:if test="${banner.activated == true}" >
					    				<c:out value="disabled='disabled'" />
					    			</c:if>
			    				>
				    				<%
					    				String selected = "";
				    					for (int i = 1; i <= 10; i++){
				    						if(banner.getPriority() != null && Integer.parseInt(banner.getPriority().toString()) == i){
				    							selected = "selected";
				    						}
				    						else{
				    							selected = "";
				    						}
				    						%>
				    							<option <%out.write(selected);%> value="<%out.write(String.valueOf(i));%>">
			    									<%out.write(String.valueOf(i));%>
				    							</option>
				    						<%
				    					}
				    					if (banner.getActivated()){
					    						out.write("<input type=\"hidden\" name=\"priority\" value=\"" + banner.getPriority() + "\" />");
					    				}
					    			%>
				    			</select>
	    					</td>
	    					<td>
	    						<fmt:message key="BM.PRIORITY_HINT"/>
	    					</td>
	    				</tr>
	    			</table>
	    		</td>
	    	</tr>
			<tr>
				<th class="admTableTD">
					<fmt:message key="BM.BANNER_URL"/>
				</th>
				<td class="admTableTDLast">
					<table cellpadding="0" cellspacing="0" align="left" border="0" width="100%">
						<tr>
							<td>
								<input class="admTextArea admBannerModuleWidth250" id="url" type="text" name="url" value="${banner.url}" 
									<c:if test="${banner.activated == true}" >
					    				<c:out value="readonly=\"readonly\" disabled=\"disabled\"" />
					    			</c:if>
								/>		
							</td>
							<td align="right">
								<fmt:message key="BM.OPEN_IN"/>
							</td>
							<td align="left">
								<select class="admTextArea" name="inNewWindow">
					    			<option 
					    					<c:if test="${banner.inNewWindow == false}">
					    						<c:out value="selected='selected'"/>
					    					</c:if>
					    			 	value="false"><fmt:message key="BM.CURRENT_WINDOW"/>
									</option>
					    			<option 
					    					<c:if test="${banner.inNewWindow == true}">
					    						<c:out value="selected='selected'"/>
					    					</c:if>
					    			 	value="true"><fmt:message key="BM.NEW_WINDOW"/>
					    			</option>
								</select>
							</td>
                            </tr>
                            <tr>
							<td align="right" colspan="2">
                                <div class="admNavPanelInp" style="padding:0;margin:0;">
                                    <div class="imgL"></div>
                                    <div><input type="button" class="admNavbarInp" value="Browse" class="admLightInp admWidth150" name="selectLinkButton"
											<%
                                                if (!banner.getActivated()){
                                                    out.write("onclick=\"link_chooser(" + banner.getId() + ")\"");
                                                }
                                                if (banner.getActivated()){
                                                    out.write("disabled=\"disabled\"");
                                                }
                                            %>
                                        />
                                    </div>
                                    <div class="imgR"></div>
                                </div>
							</td>
						</tr>
					</table>
				</td>
	    	</tr>
			<tr>
				<th class="admTableTD">
					<fmt:message key="BM.BANNER_MAX_VIEWS"/>*
				</th>
				<td class="admTableTDLast">
					<input name="maxViews" value="${banner.maxViews}" type="text" required="true" class="admTextArea admBannerModuleWidth100"
		    			<%
			    			if (banner.getActivated()){
			    				out.write("disabled=\"disabled\"");
			    				out.write("readonly=\"readonly\"");
			    			}
		    			%>
					/>
					&nbsp;<input type="checkbox" name="Views" value="true" onClick="maxViewsClicks(this.name)"
						<c:if test="${banner.activated == true}" >
		    				<c:out value="disabled='disabled'" />
		    			</c:if>
					<%
						if(banner.getMaxViews() < 1){
							out.write("checked=true");
						}
					%>
					/>&nbsp;<fmt:message key="BM.BANNER_MAX_VIEW_UNLIMITED"/>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				</td>
	    	</tr>
			<tr>
				<th class="admTableTD">
					<fmt:message key="BM.BANNER_MAX_CLICKS"/>*
				</th>
				<td class="admTableTDLast">
					<input name="maxClicks" required="true" class="admTextArea admBannerModuleWidth100" type="text" value="${banner.maxClicks}" required="true"
		    			<%
			    			if (banner.getActivated()){
			    				out.write("disabled=\"disabled\"");
			    				out.write("readonly=\"readonly\"");
			    			}
		    			%>
					/>
					&nbsp;<input type="checkbox" name="Clicks" value="true" onClick="maxViewsClicks(this.name)"
						<c:if test="${banner.activated == true}" >
		    				<c:out value="disabled='disabled'" />
		    			</c:if>
					<%
						if(banner.getMaxClicks() < 1){
							out.write("checked=true");
						}
					%>
					>&nbsp;<fmt:message key="BM.BANNER_MAX_CLICK_UNLIMITED"/>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				</td>
	    	</tr>
            <tr>
				<th class="admTableTD">
					<fmt:message key="BM.BANNER_PAGES"/>*
				</th>
            	<td class="admTableTDLast">
					<table cellspacing="0" cellpadding="0">
						<tr>
							<td class="admLeft" width="80%">
								<div id="div_pages">
									<%
										if (bannerPages != null && bannerPages.length() > 0 && !bannerPages.equals("####")){
											%>
												<fmt:message key="BM.SOME_SELECTED_PAGES"/>
											<%
										}
										else{
											%>
												<fmt:message key="BM.NO_SELECTED_PAGES"/>
											<%
										}
									%>
								</div>
							</td>
							<td>
	                            <div class="admNavPanelInp" style="padding:0;margin:0;">
                                    <div class="imgL"></div>
                                    <div>
                                    	<input type="button" class="admNavbarInp" value="<fmt:message key="BM.SELECT"/>" name="selectPageButton" onclick="page_chooser(${banner.id})"/>
                                    </div>
                                    <div class="imgR"></div>
                                </div>	
								<input required="true" type="hidden" id="bm_pages" name="bm_pages" value="<%out.write(bannerPages);%>">
							</td>
						</tr>
					</table>
                </td>
            </tr>
            <tr>
				<th class="admTableTD">
					<fmt:message key="BM.BANNER_GROUPS"/>*
				</th>
            	<td class="admTableTDLast">
					<table cellspacing="0" cellpadding="0">
						<tr>
							<td class="admLeft" width="80%">
								<div id="div_groups">
									<%
										if (bannerGroups != null && bannerGroups.length() > 0){
											%>
												<fmt:message key="BM.SOME_SELECTED_GROUPS"/>
											<%
										}
										else{
											%>
												<fmt:message key="BM.NO_SELECTED_GROUPS"/>
											<%
										}
									%>
								</div>
							</td>
							<td>
                                <div class="admNavPanelInp" style="padding:0;margin:0;">
                                    <div class="imgL"></div>
                                    <div><input type="button" class="admNavbarInp" value="<fmt:message key="BM.SELECT"/>" name="selectGroupButton" onclick="group_chooser(${banner.id})" /></div>
                                    <div class="imgR"></div>
                                </div>	
								<input required="true" type="hidden" id="bm_groups" name="bm_groups" value="<%out.write(bannerGroups);%>">
							</td>
						</tr>
					</table>
                </td>
            </tr>
			<tr>
				<th class="admTableTD">
					<fmt:message key="BM.BANNER_PUBLISH_DATE"/>*
				</th>
	    		<td class="admTableTDLast">
	                <input class="admTextArea admWidth100" type="text" name="publishDate" id="publishDateBanner" readonly="true" value="<fmt:formatDate value="${banner.publishDate}" type="both" pattern="yyyy-MM-dd" />"/>
				</td>
	    	</tr>
			<tr>
				<th class="admTableTD">
					<fmt:message key="BM.BANNER_EXPIRED_DATE"/>
				</th>
	    		<td class="admTableTDLast">
	                <input class="admTextArea admWidth100" type="text" name="expiredDate" id="expiredDateBanner"  readonly="true" value="<fmt:formatDate value="${banner.expiredDate}" type="both" pattern="yyyy-MM-dd" />"/>
				</td>
	    	</tr>
			<tr>
				<th class="admTableTD">
					<fmt:message key="BM.BANNER_ACTIVATE"/>
				</th>
	    		<td class="admTableTDLast">
	    			<input type="checkbox" id="activated" name="activated" onClick="activate();" 
	    				<%
    						if (banner.getActivated()){
    							out.write("checked=\"checked\"");		
    						}
    					%>
	    			/>
	    			<input type="hidden" id="prev_activated_flag" value="<%
   						if (banner.getActivated()){
   							out.write("true");		
   						}
   					%>"/>
				</td>
	    	</tr>
            <tr>
				<td class="admTableFooter" colspan="2">&nbsp;</td>
	        </tr>
	    </table>
	    <input type="hidden" id="save_and_close" name="save_and_close" />	  
	</form>
	<c:if test="${banner.activated == false}">
		<script type="text/javascript">
			/*checkboxes init*/
			maxViewsClicks('Clicks', false);
			maxViewsClicks('Views', false);
		</script>
	</c:if>
	</td>
    	<td class="admMaiRight"><img src="/images/right_bot.png" /></td>
	</tr>
</table>
<table cellpadding="0" cellspacing="0"  width="764px"  align="center"  border="0" class="Buttons">
    <tr>
        <td>
        	<c:if test="${banner.activated == true}">
                <div class="admBtnGreenb">
                    <div class="imgL"></div>
                    <div>                    	                        
                        <input type="button" class="admNavbarInp" value='<fmt:message key="BM.DUPLICATE"/>' onClick="duplicate('${banner.id}'); return false;"/>
                    </div>
                    <div class="imgR"></div>
                </div>
			</c:if>
            <div class="admBtnGreenb">
                <div class="imgL"></div>
                <div>    
                	<input type="button" class="admNavbarInp" value='<fmt:message key="SAVE"/>' onClick="saveForm(); return false;"/> 
                </div>
                <div class="imgR"></div>
            </div>
            <div class="admBtnGreenb">
                <div class="imgL"></div>
                <div>                    	                        
   		        	<input type="button" class="admNavbarInp" value='<fmt:message key="SAVE_AND_CLOSE"/>' onClick="saveAndCloseForm(); return false;"/>
                </div>
                <div class="imgR"></div>
            </div>
            <div class="admBtnGreenb admBtnBlueb">
                <div class="imgL"></div>
                <div>
	                <input type="reset" class="admNavbarInp" value='<fmt:message key="RESET"/>'/>
                </div>
                <div class="imgR"></div>
            </div>
        </td>
    </tr>
</table>
</body>
</html>