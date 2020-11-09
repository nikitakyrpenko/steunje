<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ page session="true"%>
<%@ page import="java.util.List" %>
<%@ page import="javax.servlet.jsp.JspWriter" %>
<%@ page import="com.negeso.framework.site_map.PageDescriptor" %>
<%
	response.setHeader("Expires", "0");
	PageDescriptor menu = (PageDescriptor)request.getAttribute("menu");
	PageDescriptor unlinked = (PageDescriptor)request.getAttribute("unlinked");
	PageDescriptor product = (PageDescriptor)request.getAttribute("product");
	String urlAddres = (String)request.getAttribute("url_address");
	Object obj = request.getAttribute("linkchooser");
	Boolean activated = (Boolean) request.getAttribute("activated");
%>
<%!
	public void create_page_list(JspWriter out, List arr_pages, String block ){
		try{
			String id_prefix = block.substring(0,1);
			StringBuffer page_str = new StringBuffer();
			PageDescriptor pages = null;
			String value_prefix = "";
			String class_expired = "";
			String show_class = "";
			
			for( int i = 0; i < arr_pages.size(); i++){
				pages = (PageDescriptor)arr_pages.get(i);
				if(block == "product"){
					value_prefix = "c";
					id_prefix = "c";
				}
				if(pages.isExpired())
					class_expired = " admPageChooserExpired";
				else
					class_expired = "";
					
				show_class = " admPageChooserHide";
				
				page_str.delete(0, page_str.length());
				page_str.append("<div class=\"admPageChooserMenuItem"+show_class+"\" id=\"div_ch_"+id_prefix+pages.getPageId()+"\">");
				page_str.append("<input type=\"checkbox\" id=\"ch_"+id_prefix+pages.getPageId()+"\" value=\""+value_prefix+pages.getPageId()+"\" onClick=\"select_item(this)\"/>&nbsp;");
					
					if(block == "product" || !pages.isLeaf()){
						page_str.append("<img class=\"admPageChooserPointer\" id=\"img_"+id_prefix+pages.getPageId()+"\" src=\"/images/folderclosed.gif\"");
						if(block == "product" ){
							page_str.append("onClick=\"view_category('"+id_prefix+pages.getPageId()+"')\">&nbsp;");
						}
						else{
							page_str.append(" onClick=\"show_hide_tree('"+id_prefix+pages.getPageId()+"')\">&nbsp;");
						}
					}
					else
						page_str.append("<img id=\"img_"+id_prefix+pages.getPageId()+"\" src=\"/images/doc.gif\">&nbsp;");
						
					if(block == "product") {
						if (pages.getContentStatus().equals("0")){
							page_str.append("<img src=\"/images/banner_module/icon_empty.gif\">&nbsp;");
						}else
						if (pages.getContentStatus().equals("1")){
							page_str.append("<img src=\"/images/banner_module/icon_partly.gif\">&nbsp;");
						}else
						if (pages.getContentStatus().equals("2")){
							page_str.append("<img src=\"/images/banner_module/icon_full.gif\">&nbsp;");
						}
					}
			
					page_str.append("<span class=\""+class_expired +"\">");
					
					if(block != "product" )
						page_str.append(pages.getTitle() + "   ( " + pages.getHref()+ " )");
					else
						page_str.append(pages.getTitle());
					
					page_str.append("</span>");
					out.write(page_str.toString());
				
				if(!pages.isLeaf() && pages.getContent() != null)
					create_page_list(out, pages.getContent(), block);
				
				out.write("</div>");
			}
		}
		catch(Exception e){e.printStackTrace();}
	}
	public void create_link_list(JspWriter out, List arr_pages, String block, String url_adress ){
		try{
			String id_prefix = block.substring(0,1);
			StringBuffer page_str = new StringBuffer();
			PageDescriptor pages = null;
			String value_prefix = "";
			String class_expired = "";
			for( int i = 0; i < arr_pages.size(); i++){
				pages = (PageDescriptor)arr_pages.get(i);
				
				if(block == "product"){
					value_prefix = "c";
					id_prefix = "c";
				}
				
				if(pages.isExpired())
					class_expired = " admPageChooserExpired";
				else
					class_expired = "";
					
				page_str.delete(0, page_str.length());
				page_str.append("<div class=\"admPageChooserMenuItem\" id=\"div_ch_"+id_prefix+pages.getPageId()+"\">");
				
				if(block == "product" || !pages.isLeaf()){
					page_str.append("<img id=\"img_"+id_prefix+pages.getPageId()+"\" src=\"/images/folderclosed.gif\"");
					if(block == "product"){
						page_str.append("class=\"admPageChooserPointer\" onClick=\"view_category('"+id_prefix+pages.getPageId()+"', 'list')\"");
					}
					page_str.append(">&nbsp;");
				}
				else
					page_str.append("<img id=\"img_"+id_prefix+pages.getPageId()+"\" src=\"/images/doc.gif\">&nbsp;");
				
				page_str.append("<span class=\"admPageChooserPointer admPageChooserCategory"+class_expired+"\"");
				
				if(block == "product"){
					page_str.append(" onClick=\"savepagelink('"+url_adress+pages.getHref());
					if(pages.isLeaf())
						page_str.append("?pmProductId=");
					else
						page_str.append("?pmCatId=");
					page_str.append(pages.getPageId()+"')\">");
				}
				else
					page_str.append(" onClick=\"savepagelink('"+url_adress+pages.getHref()+"')\">");
				
				if(block == "product")
					page_str.append(pages.getTitle());
				else
					page_str.append(pages.getTitle() + "  ( " + pages.getHref()+ " ) ");
				
				page_str.append("</span>");
				out.write(page_str.toString());
				
				if(!pages.isLeaf() && pages.getContent() != null)
					create_link_list(out, pages.getContent(), block, url_adress);
				
				out.write("</div>");
			}
		}
		catch(Exception e){e.printStackTrace();}
	}	
%>

<%@page import="com.negeso.framework.site_map.PageDescriptor"%><html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
    <title><fmt:message key="BM.PAGE_CHOOSER"/></title>
	<script type="text/javascript" src="/script/conf.js">/**/</script>
	<script type="text/javascript" src="/script/AJAX_webservice.js">/**/</script>
    <script type="text/javascript" src="/script/jquery.min.js"></script>
	<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
    <script type="text/javascript" src="/script/cufon-yui.js"></script>
    <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
	<script type="text/javascript" src="/script/common_functions.js"></script>    
</head>

<body style="position:relative">
    
    <script type="text/javascript">
	var caller = new Object();
    var arr_pages = new Array();
    var arr_checked_product = new Array();
    var node_collapse = new Array();
    var arr_response = new Array();
    var safe_parent_checked_status = null;
    var show_list_mode = null;
    
    
	if(window.dialogArguments) {
		caller = window.dialogArguments;
	} 
	else {
	   caller = window.opener;
	}

    /* AJAX request: BEGIN */
	function pages_loading(cat_id){
		var obj = null;
		
		if(document.getElementById("loading_"+cat_id)){
			obj = document.getElementById("loading_"+cat_id);
			obj.style.display="none";
		}
		else{
			obj = document.getElementById("div_ch_"+cat_id);
			obj.innerHTML  = obj.innerHTML + '<img id="loading_'+cat_id+'" class="admPageChooserMenuItem" style="display:block" src="/images/loading_animated4.gif">';
		}
	};
	
    function view_category(cat_id, mode){
    	var id_is_present = false;
    	show_list_mode = mode;
    	for(var i = 0; i < arr_response.length; i++)
    		if(arr_response[i] == cat_id){
    			id_is_present = true;
    			break;
    		}
    		
   		if(id_is_present){
   			show_hide_tree(cat_id);
   		}
   		else{

			// safe parent checked status
			if(show_list_mode != 'list'){
	   			var safe_parent_checked_id = "ch_"+cat_id;
				safe_parent_checked_status = document.getElementById(safe_parent_checked_id).checked;
			}
			
			pages_loading(cat_id);
			open_close_folder(cat_id);
			cat_id = cat_id.substr(1,cat_id.length);
			AJAX_send_url("bm_product_pages.html?action=getProductPages&id=" + cat_id + "&banner_id=" + caller.document.forms["bm_form_banner"].elements["id"].value);
		}
    };
    
    //function eventOK(response, rrr){
    function eventOK(response){
    	//alert(rrr);
		var firstChild = null;
		var arr_firstChild = response.childNodes;
		for(var i = 0; i < arr_firstChild.length; i++)
			if(arr_firstChild[i].nodeType == 1){
				firstChild = arr_firstChild[i];
				break;
			}
			
		var pages_type = firstChild.getAttribute("type");
		var current_cat = firstChild.getAttribute("category-id");
		var page = firstChild.getAttribute("page");
		current_cat = "c"+current_cat;

		pages_loading(current_cat);

    	var div_obj = document.getElementById("div_ch_" + current_cat);
    	arr_response.push(current_cat);
		
		var arr_nodes = firstChild.childNodes;
		var response_str = "";
		var checked = null;
		if(arr_nodes.length > 0){
			for( var i = 0; i < arr_nodes.length; i++){
			
				checked = null;
				id = arr_nodes[i].getAttribute("id");
				title = arr_nodes[i].getAttribute("title");
				checked = arr_nodes[i].getAttribute("selected");
				status = arr_nodes[i].getAttribute("status");
				url = arr_nodes[i].getAttribute("url");
				is_leaf = arr_nodes[i].getAttribute("is-leaf");
				
				// if some items cheked, but banner not saved yet: begin
				var str_arr = arr_checked_product.toString();
				if(pages_type == 'category') elem = "c"+id;
				else elem = "p"+id;
				// if some items cheked, but banner not saved yet: end
				
				if(show_list_mode != 'list')
					if(checked || str_arr.indexOf(elem) != -1 ) checked = "checked";
				
				if(pages_type == 'category'){
					response_str = response_str + '<div id="div_ch_c'+id+'" class="admPageChooserShow admPageChooserMenuItem">';
					
					if(show_list_mode != 'list'){
						response_str = response_str + '<input '+checked+' type="checkbox" onclick="select_item(this)" value="c'+id+'" id="ch_c'+id+'"/>';
						response_str = response_str + '<img id="img_c'+id+'" src="/images/folderclosed.gif"';
						response_str = response_str + 'class="admPageChooserPointer" onClick="view_category(\'c'+id+'\')"';
						response_str = response_str + '>&nbsp;';
						if(status == 0)
							response_str = response_str + "<img src='/images/banner_module/icon_empty.gif'>&nbsp;";
						if(status == 1)
							response_str = response_str + "<img src='/images/banner_module/icon_partly.gif'>&nbsp;";
						if(status == 2)
							response_str = response_str + "<img src='/images/banner_module/icon_full.gif'>&nbsp;";
						response_str = response_str + title+'</div>';
					}
					else{
						response_str = response_str + '<img id="img_c'+id+'" src="/images/folderclosed.gif"';
						response_str = response_str + 'class="admPageChooserPointer" onClick="view_category(\'c'+id+'\', \'list\')"';
						response_str = response_str + '>&nbsp;';
						response_str = response_str + '<span onclick="savepagelink(\''+page+'?pmCatId='+id+'\', \'list\')" class="admPageChooserPointer admPageChooserCategory">'+title+'</span></div>';
					}
				}
				else{
					response_str = response_str + '<div id="div_ch_p'+id+'" class="admPageChooserShow admPageChooserMenuItem">';
					
					if(show_list_mode != 'list'){
						response_str = response_str + '<input '+checked+' type="checkbox" onclick="select_item(this)" value="p'+id+'" id="ch_p'+id+'"/>';
						response_str = response_str + '<img src="/images/doc.gif">&nbsp;';
						response_str = response_str + title +'</div>';
					}
					else{
						response_str = response_str + '<img src="/images/doc.gif">&nbsp;';
						response_str = response_str + '<span onclick="savepagelink(\''+page+'?pmProductId='+id+'\')" class="admPageChooserPointer admPageChooserCategory">'+title+'</span></div>';
					}
					
				}
			}
		}
		else{
			response_str = response_str + '<div class="admPageChooserMenuItem"><b>no items in '+pages_type+'</b></div>';
		}
		div_obj.innerHTML = div_obj.innerHTML + response_str;
		
		if(show_list_mode != 'list'){
			// restore parent checked status
			var safe_parent_checked_id = "ch_"+current_cat;
			document.getElementById(safe_parent_checked_id).checked = safe_parent_checked_status;
		}
    };
    
    function eventNotOK(){
    	alert('no ok');
    };
    /* AJAX request: END */
    
    function arr_add_remove(command, arr, value){
    	var arr_new = new Array();
    	
    	if(command == 'remove'){
	    	for(var i = 0; i < arr.length; i++){
	    		if(arr[i] != value){
		    		arr_new.push(arr[i]);
		    	}
	    	}
	    }
	    
	    if(command == 'add'){
	    	arr_new = arr;
	    	arr_new.push(value);
	    }
	    
    	return arr_new;
    };
    
    function savepagelink(obj){
    	caller.document.getElementById('url').value = obj;
    	window.close();
    }
    
	function click_group(obj){
		if(obj.checked){
			arr_pages = arr_add_remove('add', arr_pages, obj.value);
		}
		else{
			arr_pages = arr_add_remove('remove', arr_pages, obj.value);
		}
	};
    
    function check_selected(obj){
    	// if some child of item 'obj' is checked - return TRUE 
    	// else - FALSE
    	var new_obj = document.getElementById(obj.id);
    	var arr_items = new_obj.getElementsByTagName("input");
    	for(var i = 0; i < arr_items.length; i++){
    		if(arr_items[i].type == "checkbox" && arr_items[i].checked){
    			return true;
    		}
    	}
    	return false;
    };
    
    function open_close_folder(obj_id){
    	if(document.getElementById("img_"+obj_id).src.indexOf('closed') != -1)
	    	document.getElementById("img_"+obj_id).src = document.getElementById("img_"+obj_id).src.replace('closed','open');
	    else
	    	document.getElementById("img_"+obj_id).src = document.getElementById("img_"+obj_id).src.replace('open','closed');
	};
    
    function show_hide(obj, state){
    	// show/hide some item 'obj'
    	if(state == null || state == 'undefined')
    		state = 'Show';
    		
		if(obj.className.indexOf(state) == -1 ){
 			// if hidden
 			obj.className = obj.className.replace(/Hide/, 'Show');
 		}
 		else{
			// if showed
 			obj.className = obj.className.replace(/Show/, 'Hide');
 		}
    };
    
    function show_hide_tree(obj_id){
    	// show/hide all 1-st level sub-items of item with ID obj_id
    	var div_obj = document.getElementById("div_ch_"+obj_id);

		open_close_folder(obj_id);
    	
    	if(div_obj.hasChildNodes()){
	    	var arr_items = div_obj.childNodes;
	    	for(var i = 0; i < arr_items.length; i++){
				if(arr_items[i].nodeName.toUpperCase() == "DIV")
			    	show_hide(arr_items[i]);
	    	}
    	}
    };
    
    // set 'checked' flag to selected item
    function select_item(obj){
    	// if (un)check some item - (un)check all sub-items
    	
    	var checkbox_flag = obj.checked;
    	var new_obj = document.getElementById("div_"+obj.id);
    	var arr_items = new_obj.getElementsByTagName("input");
    	
    	for(var i = 0; i < arr_items.length; i++){
    		if(arr_items[i].type == "checkbox"){
    			arr_items[i].checked = checkbox_flag;
					
				if(checkbox_flag)
					arr_pages = arr_add_remove('add', arr_pages, arr_items[i].value);
				else
					arr_pages = arr_add_remove('remove', arr_pages, arr_items[i].value);
    		}
    	}
    };
    
    // save all selected items
    function save(){
    	var str_pages = '';
    	var elem = '';
    	
    	/* collect saved later categories and products: begin */
    	var pages = caller.document.getElementById('bm_pages').value;
    	if(pages == '') pages = "####";
    	var arr_saved_items = new Array();
    	arr_saved_items = pages.split("#");
    	arr_saved_items[0] = arr_saved_items[2];
    	arr_saved_items[1] = arr_saved_items[3];
    	arr_saved_items.splice(2,3);
    	/* collect saved later categories and products: end */
    	
    	/* collect selected pages: begin*/
    	var str_sel_pages = new Array();
    	var arr_block_name = Array('menu','unlinked','product','product');
    	var arr_block_type = Array('m','u','c','p'); // m - menu, u - unlinked, c - category, p - product
	    for(var types = 0; types < arr_block_name.length; types++){
	    	str_sel_pages[types] = '';
	    	
        	if(document.getElementById(arr_block_name[types]+'_pages')){	

		    	var arr_input = document.getElementById(arr_block_name[types]+"_pages").getElementsByTagName("input");
		    	for(var i = 0; i < arr_input.length; i++){
		    		if(arr_block_type[types] == 'c' || arr_block_type[types] == 'p'){
		    			elem = arr_input[i].value.replace(arr_block_type[types],'');
		    			
			    		if(arr_input[i].getAttribute("type") == "checkbox" && arr_input[i].checked && arr_input[i].value.indexOf(arr_block_type[types]) != -1){
			    			str_sel_pages[types] = str_sel_pages[types] + arr_input[i].value.replace(arr_block_type[types],'') + ";";
			    		}
		    			// remove loaded ID's from saved ID's list: begin
		    			if(arr_input[i].value.substr(0,1) == arr_block_type[types]){
			    			elem = elem + ";";
			    			if(arr_block_type[types] == 'c'){
			    				arr_saved_items[0] = arr_saved_items[0].replace(elem,'');
			    			}
			    			if(arr_block_type[types] == 'p'){
			    				arr_saved_items[1] = arr_saved_items[1].replace(elem,'');
			    			}
			    		}
		    			// remove loaded ID's from saved ID's list: end
		    		}
		    		else
			    		if(arr_input[i].getAttribute("type") == "checkbox" && arr_input[i].checked)
			    			str_sel_pages[types] = str_sel_pages[types] + arr_input[i].value.replace(arr_block_type[types],'') + ";";
		    	}
		    	
		    	if(arr_block_type[types] == 'c')
		    		// add saved later, but not loaded now items: categories
		    		str_sel_pages[types] = str_sel_pages[types] + arr_saved_items[0] + "#";
		    	else
		    		if(arr_block_type[types] == 'p')
		    			// add saved later, but not loaded now items: products
			    		str_sel_pages[types] = str_sel_pages[types] + arr_saved_items[1] + "#";
			    	else
				    	str_sel_pages[types] = str_sel_pages[types] + "#";

        	}
        }
		for(var types = 0; types < arr_block_name.length; types++)
			str_pages = str_pages + str_sel_pages[types];
		/* collect selected pages: end*/
		
    	if(str_pages != '####')
    		caller.document.getElementById('div_pages').innerHTML = '<fmt:message key="BM.SOME_SELECTED_PAGES"/>';
    	else
    		caller.document.getElementById('div_pages').innerHTML = '<fmt:message key="BM.NO_SELECTED_PAGES"/>';
    	caller.document.getElementById('bm_pages').value = str_pages;
    	
    	window.close();
    	
    };
    
    // set 'checked' flag for selected items
    function pages_init(){
    	var obj = null;
    	var elem = '';

		/* show only top categories: begin*/    	
    	var arr_pages_block = Array('menu','unlinked','product');
    	for(var block = 0; block < arr_pages_block.length; block++){
    		obj = document.getElementById(arr_pages_block[block]+"_pages");
    		if(obj.hasChildNodes()){
    			for(var i = 0 ; i < obj.childNodes.length; i++){
    				try{
						obj.childNodes[i].className = obj.childNodes[i].className.replace('Hide','Show');
					}
					catch(e){};
    			}
    		}
    	}
		/* show only top categories: end*/    	
		
		/* set 'checked' flag for selected items: begin*/
    	var pages = caller.document.getElementById('bm_pages').value;
    	if(pages == '') pages = "####";

    	arr_pages_block = pages.split("#");
    	
    	// m - menu, u - unlinked, c - category, p - product
    	var arr_block_name = Array('menu','unlinked','product','product');
    	var arr_block_type = Array('m','u','c','p');
    	
    	for(var types = 0; types < arr_block_type.length; types++){
	    	if(document.getElementById(arr_block_name[types]+'_pages')){
		    	var arr_init_pages = arr_pages_block[types].split(";");
		    	for(var i = 0; i < arr_init_pages.length; i++){
		    		if(arr_init_pages[i] != '' && document.getElementById("ch_"+ arr_block_type[types] + arr_init_pages[i]))
		    			document.getElementById("ch_"+ arr_block_type[types] + arr_init_pages[i]).checked = true;
		    			
	    			// remember item id ONLY for checked categories and products
	    			if((arr_block_type[types] == 'p' || arr_block_type[types] == 'c') && arr_init_pages[i] != ''){
	    				elem = arr_block_type[types] + arr_init_pages[i];
		    			arr_checked_product.push(elem);
		    		}
		    	}
		    }
    	}
    	/* set 'checked' flag for selected items: end*/
    };
    </script>
    <table class="admMain"  align="center" border="0" cellpadding="0" cellspacing="0">
      <tr>
          <td class="admConnerLeft"></td>
          <td class="admTopBtn">
              <div><img src="/images/logo.png"  class="admMainLogo" /></div>
              <br></br>
             
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
				<fmt:message key="BM.PAGE_CHOOSER"/> (${langId})
            </td>
    <!-- TITLE: end -->

	<!-- CONTENT: begin  -->
		<c:if test="${menu != null}">
			<tr>
				<th class="admTableTD">
					<span class="admBold"><fmt:message key="BM.MENU_PAGES"/></span>
				</th>
			</tr>
			<tr>
				<td id="menu_pages" class="admTableTDLast">
					<%
						if (obj == null){
							create_page_list(out, menu.getContent(), "menu");
						}else{
							create_link_list(out, menu.getContent(), "menu", urlAddres);
						}
					%>
				</td>
			</tr>
		</c:if>
		
		<c:if test="${unlinked != null}">
		<tr>
			<th class="admTableTD" colspan="2">
				<span class="admBold"><fmt:message key="BM.UNLINKED_PAGES"/></span>
			</th>
		</tr>
		<tr>
			<td id="unlinked_pages" class="admTableTDLast" colspan="2">
				<%
						if (obj == null){
							create_page_list(out, unlinked.getContent(), "unlinked");
						}else{
							create_link_list(out, unlinked.getContent(), "unlinked", urlAddres);
						}
					%>
			</td>
		</tr>
		</c:if>
		
		<c:if test="${product != null}">
		<tr>
			<td class="admTableTDLast" colspan="2">
				<span class="admBold"><fmt:message key="BM.PRODUCT_MODULE_PAGES"/></span>
			</td>
		</tr>
		<tr>
			<td id="product_pages" class="admTableTDLast" colspan="2">
				<%
					if (obj == null){
						create_page_list(out, product.getContent(), "product");
					}else{
						create_link_list(out, product.getContent(), "product", urlAddres);
					}
				%>
			</td>
		</tr>
		</c:if>
		
        
        <tr>
			<td class="admTableFooter" colspan="5">&nbsp;</td>
        </tr>
	</table>
	<script type="text/javascript">
		pages_init();
	</script>
	<!-- CONTENT: end  -->

	</td>
    	<td class="admMaiRight"><img src="/images/right_bot.png" /></td>
	</tr>
</table>

<%
	if(!activated){
		%>
    <table cellpadding="0" cellspacing="0"  width="764px"  align="center"  border="0" class="Buttons">
        <tr>
            <td>
                <div class="admBtnGreenb">
                    <div class="imgL"></div>
                    <div>
                        <input type="button" class="admNavbarInp" value='<fmt:message key="SAVE"/>' onClick="save();"/>
                    </div>
                    <div class="imgR"></div>
                </div>
            </td>
        </tr>
    </table>
	<%
  	}
%>

    <!-- /div -->
</body>
</html>