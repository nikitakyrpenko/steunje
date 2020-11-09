/*  Rich Text Editor Adapter in JavaScript. *
 *		Author: Rostislav Brizgunov			*/

var RTE_DEFAULT_WIDTH = 1137;
var RTE_DEFAULT_HEIGHT = 750;
var RTE_DEFAULT_SAVE_MODE = 0;
var RTE_DEFAULT_FORM_MANAGE_MODE = 0;
var RTE_DEFAULT_LANGUAGE = "en";

function RTE(editor) {

	if (editor) this.editor = editor
	else this.editor = "TinyMCE";
	this.width = RTE_DEFAULT_WIDTH?RTE_DEFAULT_WIDTH:800;
	this.height = RTE_DEFAULT_HEIGHT?RTE_DEFAULT_HEIGHT:600;
	this.mode = "exact";
	this.elements = "RTE_Field";
	this.save_mode = RTE_DEFAULT_SAVE_MODE?RTE_DEFAULT_SAVE_MODE:0;
	this.form_manage_mode = RTE_DEFAULT_FORM_MANAGE_MODE?RTE_DEFAULT_FORM_MANAGE_MODE:0;
	this.css_classes_list = "";
	this.language = RTE_DEFAULT_LANGUAGE?RTE_DEFAULT_LANGUAGE:"en";
	this.absolute_urls = false;
	this.is_newsletter = false;
	
	this.init = function() {
		if (this.editor=="TinyMCE") {
			var save_mode_str = this.save_mode + "";
			var save_text = (save_mode_str == '0')?"":((save_mode_str == '1')?",negeso_local_save,negeso_local_save_and_exit":((save_mode_str == '2')?",negeso_local_save,negeso_local_save_and_exit,negeso_global_save,negeso_global_save_and_exit":((save_mode_str == '3')?",negeso_global_save,negeso_global_save_and_exit":"")));
			var form_manager_text_part_1 = ",|,form_list";
			var form_manager_text_part_2 = "form_text,form_password,form_submit,form_reset,form_checkbox,form_radio,form_radio_group,form_file,form_textarea,form_select,form_cleanup,form_captcha_input,form_captcha_image";
			
			switch (this.form_manage_mode) {
				case 0: form_manager_text_part_2 = ""; break;
				case 1: form_manager_text_part_1 = ""; break;
			}
			
			var newsletter_plugin = '';
			switch (this.is_newsletter) {
				case true: newsletter_plugin = ",|,negeso_auto_filled_attrs"; break;
			}
			tinyMCE.is_newsletter  = this.is_newsletter;
			save_text += ",negeso_exit";
			tinyMCE.init({
				init_instance_callback: function (t) {t.setContent(source_val, {source_view : true}); t.startContent=t.getContent({format : 'raw'});$(".mceIframeContainer iframe").webkitimageresize().webkittableresize().webkittdresize();t.dom.getRoot().focus();t.isNotDirty = true;},
				mode : this.mode?this.mode:"textareas",
				elements : this.elements?this.elements:null,
				width : '100%',
				height: '100%',
				theme : "advanced",
				forced_root_block : '',
				plugins : "autolink,lists,pagebreak,style,layer,table,negeso_save,advhr,negeso_advimage,negeso_social_networks,negeso_twitter_widget,negeso_template,negeso_youtube,negeso_google_map,negeso_revisions,negeso_form_manager,emotions,inlinepopups,insertdatetime,preview,media,searchreplace,print,contextmenu,paste,directionality,fullscreen,noneditable,visualchars,nonbreaking,xhtmlxtras,template,wordcount,advlist,visualblocks,negeso_advlink,negeso_rich_snippets,autosave",

				// Theme options
				theme_advanced_buttons1 : "newdocument,save,save_and_close,exit,|,print,preview,|,code,|,cut,copy,paste,pastetext,pasteword,|,cleanup,|,search,replace,|,undo,redo,|,link,negeso_advlink_to_page,negeso_advlink_to_file,anchor,unlink,|,image,negeso_advimage_add,negeso_advimage_media,|,negeso_sn,negeso_twitter_widget,negeso_template"+form_manager_text_part_1+",|,visualaid,visualblocks,negeso_rich_snippets,negeso_rich_prop",
				theme_advanced_buttons2 : "formatselect,fontselect,fontsizeselect,styleselect,styleprops,removeformat,|,bold,italic,underline,strikethrough,|,sub,sup,|,justifyleft,justifycenter,justifyright,justifyfull,|,bullist,numlist,|,outdent,indent,blockquote,|,forecolor,backcolor",
				theme_advanced_buttons3 : "tablecontrols,|,hr,charmap,emotions,media,negeso_youtube,negeso_google_map,advhr,|,insertdate,inserttime,|,ltr,rtl,|,insertlayer,moveforward,movebackward,absolute,|,cite,abbr,acronym,del,ins,attribs,|,visualchars,nonbreaking,template,pagebreak,|,negeso_revisions",
				theme_advanced_buttons4 : form_manager_text_part_2,
				theme_advanced_toolbar_location : "top",
				theme_advanced_toolbar_align : "left",
				theme_advanced_statusbar_location : "bottom",
				
				valid_elements : "@[itemscope|itemtype|itemprop],+a[id|utm_source|utm_medium|utm_campaign|def-styles|style|rel|rev|charset|hreflang|dir|lang|tabindex|accesskey|type|name|href|target|title|class|onfocus|onblur|onclick|ondblclick|onmousedown|onmouseup|onmouseover|onmousemove|onmouseout|onkeypress|onkeydown|onkeyup|width|height],-strong/-b[class|def-def-styles|styles|def-styles|style],-em/-i[class|def-def-styles|styles|def-styles|style],-strike[class|def-styles|style],-u[class|def-styles|style],#p[id|def-styles|style|dir|class|align],-ol[class|def-styles|style],-ul[class|def-styles|style],-li[class|def-styles|style],br,img[id|dir|lang|longdesc|usemap|def-styles|style|class|src|onmouseover|onmouseout|onclick|border|alt=|title|hspace|vspace|width|height|align],-sub[def-styles|style|class],-sup[def-styles|style|class],-blockquote[dir|def-styles|style],-table[border=0|cellspacing|cellpadding|width|height|class|align|summary|def-styles|style|dir|id|lang|bgcolor|background|bordercolor],-tr[id|lang|dir|class|rowspan|width|height|align|valign|def-styles|style|bgcolor|background|bordercolor],tbody[id|class],thead[id|class],tfoot[id|class],-td[id|lang|dir|class|colspan|rowspan|width|height|align|valign|def-styles|style|bgcolor|background|bordercolor|scope|link_to|cellcolor|onclick|onmouseover|onmouseout|backcolor|title],-th[id|lang|dir|class|colspan|rowspan|width|height|align|valign|def-styles|style|scope],caption[id|lang|dir|class|def-styles|style],-div[id|dir|class|align|def-styles|style|twtr_height|twtr_width|twtr_rpp|twtr_shell_background|twtr_shell_color|tweets_background|tweets_color|tweets_links|google_color_border|google_color_bg|google_color_link|google_color_text|google_color_url|google_client_number|google_w|google_h|google_ad_type|google_ui_features],-span[def-styles|style|class|align],-pre[class|align|def-styles|style],address[class|align|def-styles|style],-h1[id|def-styles|style|dir|class|align],-h2[id|def-styles|style|dir|class|align],-h3[id|def-styles|style|dir|class|align],-h4[id|def-styles|style|dir|class|align],-h5[id|def-styles|style|dir|class|align],-h6[id|def-styles|style|dir|class|align],hr[class|def-styles|style],-font[face|size|def-styles|style|id|class|dir|color],dd[id|class|title|def-styles|style|dir|lang],dl[id|class|title|def-styles|style|dir|lang],dt[id|class|title|def-styles|style|dir|lang],"+
				"cite[class|dir<ltr?rtl|id|lang|onclick|ondblclick|onkeydown|onkeypress|onkeyup|onmousedown|onmousemove|onmouseout|onmouseover|onmouseup|style|title],"+
				  "ins[cite|class|datetime|dir<ltr?rtl|id|lang|onclick|ondblclick|onkeydown|onkeypress|onkeyup|onmousedown|onmousemove|onmouseout|onmouseover|onmouseup|style|title],"+
				  "del[cite|class|datetime|dir<ltr?rtl|id|lang|onclick|ondblclick|onkeydown|onkeypress|onkeyup|onmousedown|onmousemove|onmouseout|onmouseover|onmouseup|style|title],"+
				  "abbr[class|dir<ltr?rtl|id|lang|onclick|ondblclick|onkeydown|onkeypress|onkeyup|onmousedown|onmousemove|onmouseout|onmouseover|onmouseup|style|title],"+
				  "acronym[class|dir<ltr?rtl|id|id|lang|onclick|ondblclick|onkeydown|onkeypress|onkeyup|onmousedown|onmousemove|onmouseout|onmouseover|onmouseup|style|title],"+
				  "time[datetime],meta[content],link[charset|href|media|rel|rev|target|type],label[class|for|id|style]",
				extended_valid_elements : "hr[class|width|size|noshade],font[face|size|color|def-styles|style],span[class|align|def-styles|style]",
				//extended_valid_elements : "img[id|dir|lang|longdesc|usemap|def-styles|style|class|src|onmousedown|onmouseover|onmouseout|onclick|border|alt=|title|hspace|vspace|width|height|align]",
				extended_valid_elements : "@[itemscope|itemtype|itemprop],input[name|placeholder|id|title|def-styles|style|class|type=text|value|checked|readonly|size|maxlength|tabindex|is_required|numeric_field_params|is_email|is_phone|timedate_field_format|onclick]," +
										  //"form[name|id|title|def-styles|style|enctype|action|method]," +
										  "iframe[width|height|name|frameborder|def-styles|style|class|scrolling|marginheight|marginwidth|src]," +
										  "textarea[name|placeholder|id|title|def-styles|style|class|rows|cols|readonly|tabindex|value|required]," +
										  "select[name|id|title|def-styles|style|class|size|multiple|tabindex]," +
										  "option[id|title|def-styles|style|class|selected|value|inner_text]," +
										  "-div[align<center?justify?left?right|class|dir<ltr?rtl|id|lang|onclick|ondblclick|onkeydown|onkeypress|onkeyup|onmousedown|onmousemove|onmouseout|onmouseover|onmouseup|def-styles|style|title|negeso_form_id|twtr_height|twtr_width|twtr_rpp|twtr_shell_background|twtr_shell_color|tweets_background|tweets_color|tweets_links|google_color_border|google_color_bg|google_color_link|google_color_text|google_color_url|google_client_number|google_h|google_w|google_ad_type|google_ui_features]",
										    


				// Skin options
				skin : "o2k7",
				skin_variant : "silver",
				save_enablewhendirty : true,
				convert_urls : (this.is_newsletter) ? true : false,


				language : this.language,
				content_css : "/site/core/css/default_styles.css",
				

				// Drop lists for link/image/media/template dialogs
				template_external_list_url : "/site/core/script/RTE_Templates.js",
				//external_link_list_url : "lists/link_list.js",
				//external_image_list_url : "lists/image_list.js",
				media_external_list_url : "lists/media_list.js",
				
				// Replace values for the template plugin
				template_replace_values : {
					username : "Some User",
					staffid : "991234"
				},
				
				setup : function(ed) {
				      ed.onInit.add(function(ed) {
				    	  ed.dom.getRoot().focus();
				      });				      
				   },
				   
			   urlconverter_callback : function(url, node, on_save) {
				   if (url.indexOf(window.opener.location.protocol+"//"+window.opener.location.host) == 0) {
					
					   var pos = url.indexOf('/media/');
					   if (pos >= 0) {
						   return url.substring(pos, url.length);
					   }
					   if (!tinyMCE.is_newsletter) {
					   url = url.replace(window.opener.location.protocol+"//"+window.opener.location.host, '');
					   }
					   url = url.replace('/admin/', '');
					   url = url.replace('/rte/', '');
					   return url;
				   }
				   return url;
			   },
			   
			   cleanup_callback : function (type, value) {
				   switch (type) {
				   	case "get_from_editor":
				   		value = value.replace(/location.mcehref/gim, "location.href");
				   		break;
				   	case "insert_to_editor":
				   		value = value.replace(/location.href/gim, "location.mcehref");
				   		break;			                
			        }
				   return value;
			   }


			});

			switch (this.form_manage_mode) {
				case 0: tinyMCE.negeso_save_form_mode = "as_form_insert"; break;
				case 1: tinyMCE.negeso_save_form_mode = "as_form_manager"; break;
			}
			
			tinyMCE.negeso_absolute_urls = (this.is_newsletter) ? true : this.absolute_urls;
			tinyMCE.negeso_css_classes_list = this.css_classes_list;
			this.registerPreRequestFunction(RTE_Form_Request);
			return true
		}
		return false
	};
	
	this.setDestination = function(destination_IDs) {
		if (typeof(destination_IDs)=="undefined") destination_IDs = "RTE_Field";
		if (this.editor=="TinyMCE") {
			tinyMCE.dest_ids = destination_IDs;
			return true
		}
		return false
	};

	this.setSource = function(source_ID) {
		if (typeof(source_ID)=="undefined") source_ID = "RTE_Field";
		if (this.editor=="TinyMCE") {
			tinyMCE.source_id = source_ID;
			return true
		}
		return false
	}

	this.setArticle = function(article_ID) {
		if (typeof(article_ID)=="undefined") article_ID = -1;
		if (this.editor=="TinyMCE") {
			tinyMCE.article_id = article_ID;
			return true
		}
		return false		
	}
	
	// Privat part of class. It is almost useless for directly calls
	this.registerPreRequestFunction = function(func_name) {
		if (this.editor=="TinyMCE") {
			tinyMCE.negeso_pre_request = function(content,id) { return func_name(content,id) };
			return true
		}
		return false
	}
	
}

// CONSTANT OBJECT: Need for making some ID-transport between site and pop-up RTE window
var RTE_Transporter = {
	source_id : "",
	dest_ids : "",
	article_id : "",
	save_mode : 0,
	form_manage_mode : 0,
	language : "en",
	css_classes_list : "",
	absolute_urls : false,
	is_newsletter: false
};
var allowedLanguages= ['en','nl'];
function contains(a, obj) {
    var i = a.length;
    while (i--) {
       if (a[i] === obj) {
           return true;
       }
    }
    return false;
}
function RTE_Init(source_id, dest_ids, article_id, save_mode, form_manage_mode, css_classes_list, language, absolute_urls, is_newsletter) {

	if (typeof(source_id) == "undefined" || source_id == null) source_id = "RTE_Field";
	if (typeof(dest_ids) == "undefined" || dest_ids == null) dest_ids = source_id;
	if (typeof(article_id) == "undefined" || article_id == null) article_id = source_id.substr(12,source_id.length-1);
	if (typeof(save_mode) == "undefined" || save_mode == null) save_mode = RTE_DEFAULT_SAVE_MODE?RTE_DEFAULT_SAVE_MODE:1;
	if (typeof(form_manage_mode) == "undefined" || form_manage_mode == null) form_manage_mode = RTE_DEFAULT_FORM_MANAGE_MODE?RTE_DEFAULT_FORM_MANAGE_MODE:0;	
	if (typeof(css_classes_list) == "undefined" || css_classes_list == null) css_classes_list = '';
	if (typeof(language) == "undefined" || language == null || !contains(allowedLanguages, language)) language = RTE_DEFAULT_LANGUAGE;
	if (typeof(absolute_urls) == "undefined" || absolute_urls == null) absolute_urls = false;
	if (typeof(is_newsletter) == "undefined" || is_newsletter == null) is_newsletter = false;

	RTE_Transporter.source_id = source_id;
	RTE_Transporter.dest_ids = dest_ids;
	RTE_Transporter.article_id = article_id;
	RTE_Transporter.save_mode = save_mode;
	RTE_Transporter.form_manage_mode = form_manage_mode;
	RTE_Transporter.css_classes_list = css_classes_list;
	RTE_Transporter.language = language;
	RTE_Transporter.absolute_urls = absolute_urls;
	RTE_Transporter.is_newsletter = is_newsletter;

	var win_width = RTE_DEFAULT_WIDTH?RTE_DEFAULT_WIDTH:800;
	var win_height = RTE_DEFAULT_HEIGHT?RTE_DEFAULT_HEIGHT:600;
	var win_left = (screen.availWidth - win_width)/2;
	var win_top = (screen.availHeight - win_height)/2;
	var new_window = window.open("/rte/RTE_Template.html","Negeso_RTE_Popup_Window","directories=0,width="+win_width+",height="+win_height+",left="+win_left+",top="+win_top+",location=no,menubar=no,resizable=yes,scrollbars=no,status=no,toolbar=no,modal=no");
	new_window.focus();
}

function RTE_Form_Request(content,id) {
	if (content == null || typeof(content) == "undefined") content = "";
	var temp = ''+
		'<SOAP-ENV:Envelope'+'\n'+
		'		xmlns=""'+'\n'+
		'		xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/"'+'\n'+
		'		SOAP-ENV:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/"'+'\n'+
		'		xmlns:xsd="http://www.w3.org/2001/XMLSchema"'+'\n'+
		'		xmlns:tns="http://negeso.com/2003/Framework-v.1.1/wsdl/serviceAdmin/serviceAdmin"'+'\n'+
		'		xmlns:ns2="http://negeso.com/2003/Framework-v.1.1/wsdl/serviceAdmin/types/serviceAdmin"'+'\n'+
		'		xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/"'+'\n'+
		'		xmlns:soapenc="http://schemas.xmlsoap.org/soap/encoding/"'+'\n'+
		'		xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/"'+'\n'+
		'		xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">'+'\n'+
		'	<SOAP-ENV:Header/>'+'\n'+
		'	<SOAP-ENV:Body>'+'\n'+
		'		<mswsb:auxOperation xmlns:mswsb="http://negeso.com/2003/Framework-v.1.1/wsdl/serviceAdmin/serviceAdmin">'+'\n'+
		'			<operationKind xsi:type="xsd:string">update-article-text-command</operationKind>'+'\n'+
		'			<parameterNames xsi:type="soapenc:Array" soapenc:arrayType="xsd:string[2]">'+'\n'+
		'				<string xsi:type="xsd:string">id</string>'+'\n'+
		'				<string xsi:type="xsd:string">text</string>'+'\n'+
		'			</parameterNames>'+'\n'+
		'			<parameterValues xsi:type="soapenc:Array" soapenc:arrayType="xsd:string[2]">'+'\n'+
		'				<string xsi:type="xsd:string">'+id+'</string>'+'\n'+
		'				<string xsi:type="xsd:string"><![CDATA['+content+']]></string>'+'\n'+
		'			</parameterValues>'+'\n'+
		'		</mswsb:auxOperation>'+'\n'+
		'	</SOAP-ENV:Body>'+'\n'+
		'</SOAP-ENV:Envelope>';
	return temp;
}