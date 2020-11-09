function html_encode(html) {
	html = html.replace(new RegExp("<","gi"),"&lt;");
	html = html.replace(new RegExp(">","gi"),"&gt;");
	html = html.replace(new RegExp("\"","gi"),"&quot;");
	return html;	
}

function html_decode(html) {
	html = html.replace(new RegExp("&lt;","gi"),"<");
	html = html.replace(new RegExp("&gt;","gi"),">");
	html = html.replace(new RegExp("&quot;","gi"),"\"");
	return html;
}

var localSelectedNode = null;

function insertTwitterWidget() {
	try {		
		var data_widget_id = document.forms[0].data_widget_id.value; 
		var height = document.forms[0].height.value; 
		var width = document.forms[0].width.value; 
		var data_theme = document.forms[0].data_theme.options[document.forms[0].data_theme.selectedIndex].value;
		var data_tweet_limit = document.forms[0].data_tweet_limit.value; 
		var data_link_color = document.forms[0].data_link_color.value; 
		var data_border_color = document.forms[0].data_border_color.value;
		var data_lang = document.forms[0].data_lang.value;
		var data_chrome = '';
		if (!(document.forms[0].display_header.checked)) {
			data_chrome = addWitespace(data_chrome, 'noheader');
		}
		if (!(document.forms[0].display_footer.checked)) {
			data_chrome = addWitespace(data_chrome, 'nofooter');
		}
		if (!(document.forms[0].display_borders.checked)) {
			data_chrome = addWitespace(data_chrome, 'noborders');
		}
		if (!(document.forms[0].display_scrollbar.checked)) {
			data_chrome = addWitespace(data_chrome, 'noscrollbar');
		}
		if (document.forms[0].is_transparent.checked) {
			data_chrome = addWitespace(data_chrome, 'transparent');
		}
		
		
		
		var html = '<div class="negesoTwitterWidget" ' +
						' style="' + putAsStyle('width',width) + putAsStyle('height',height) + '"' +
						' data-widget-id="' + data_widget_id + '" ' +
						' data-theme="' + data_theme + '" ' +
							putAsAttr('data-width',width) +
							putAsAttr('data-height',height) +
							putAsAttr('data-tweet-limit',data_tweet_limit) +
							putAsAttr('data-link-color',data_link_color) +
							putAsAttr('data-border-color',data_border_color) +
							putAsAttr('data-chrome',data_chrome) +
							putAsAttr('data-lang',data_lang) +
						'> ' +
						'<a class="twitter-timeline" href="https://twitter.com/twitterapi" '+
						' data-widget-id="' + data_widget_id + '" ' +
						' data-theme="' + data_theme + '" ' +
							putAsAttr('width',width) +
							putAsAttr('height',height) +
							putAsAttr('lang',data_lang) +
							putAsAttr('data-tweet-limit',data_tweet_limit) +
							putAsAttr('data-link-color',data_link_color, '#') +
							putAsAttr('data-border-color',data_border_color, '#') +
							putAsAttr('data-chrome',data_chrome) +
						'></a>'
						'</div>';
		if (localSelectedNode) {
			$(localSelectedNode).replaceWith(function () {
			    return $(html);
			});
			$(localSelectedNode).after(html);
			$(localSelectedNode).remove();
		}else {
			tinyMCEPopup.execCommand("mceInsertContent", true, html);
		}
	} catch(e) {alert("ERROR")}

	tinyMCEPopup.close();
}

function addWitespace(text, tail) {
	if (!text == '') {
		text += ' ';
	}
	return text + tail;
}
function putAsStyle (key, val) {
	if (val != '') {
		return key+':'+val+'px;';
	}
	return '';
}

function putAsAttr (key, val, pref) {
	if (!pref) {
		pref='';
	}
	if (val != '') {
		return ' ' + key+'="'+pref+val+'" ';
	}
	return '';
}



function init() {
	var action = 'insert';
	tinyMCEPopup.resizeToInnerSize();
	var inst = tinyMCE.activeEditor;
	var elm = tinyMCEPopup.editor.windowManager.params.selectedNode;
	localSelectedNode = elm;
	elm = inst.dom.getParent(elm, "DIV");
	
	if (elm != null && elm.className == 'negesoTwitterWidget') {
		document.forms[0].data_widget_id.value = elm.getAttribute('data-widget-id');
		document.forms[0].height.value ='';
		if (elm.getAttribute('data-height')) {
			document.forms[0].height.value = elm.getAttribute('data-height');			
		}		
		if (elm.getAttribute('data-width')) {
			document.forms[0].width.value = elm.getAttribute('data-width');
		}
		document.forms[0].data_tweet_limit.value='';
		if (elm.getAttribute('data-tweet-limit')) {
			document.forms[0].data_tweet_limit.value = elm.getAttribute('data-tweet-limit');
		}
		if (elm.getAttribute('data-link-color')) {
			document.forms[0].data_link_color.value = elm.getAttribute('data-link-color');
		}
		if (elm.getAttribute('data-border-color')) {
			document.forms[0].data_border_color.value = elm.getAttribute('data-border-color');
		}
		if (elm.getAttribute('data-lang')) {
			document.forms[0].data_lang.value = elm.getAttribute('data-lang');
		}
		var data_chrome = elm.getAttribute('data-chrome');
		if (data_chrome) {
			if (data_chrome.indexOf('noheader') != -1) {
				document.forms[0].display_header.removeAttribute('checked');
			}
			if (data_chrome.indexOf('nofooter') != -1) {
				document.forms[0].display_footer.removeAttribute('checked');
			}
			if (data_chrome.indexOf('noborders') != -1) {
				document.forms[0].display_borders.removeAttribute('checked');
			}
			if (data_chrome.indexOf('noscrollbar') != -1) {
				document.forms[0].display_scrollbar.removeAttribute('checked');
			}
			if (data_chrome.indexOf('transparent') != -1) {
				document.forms[0].is_transparent.setAttribute('checked','checked'); 
			}
		}
		var select = document.getElementById("data_theme");
        for(var i = 0;i < select.options.length;i++){
            if(select.options[i].value == elm.getAttribute('data-theme') ){
                select.options[i].selected = true;
            }
        }
        action = 'update';
		
	}
	document.forms[0].insert.value = inst.getLang(action);
}
