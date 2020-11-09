/**
 * editor_plugin_src.js
 *
 * Copyright (c) 2012 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

(function() {
	tinymce.PluginManager.requireLangPack('negeso_save');

	tinymce.create('tinymce.plugins.NegesoSavePlagin', {
		init : function(ed, url) {
			var t = this;

			t.editor = ed;

			// Register commands
			ed.addCommand('mceNegesoSave', t._save, t);
			ed.addCommand('mceNegesoSaveAndClose', t._save_and_exit, t);
			ed.addCommand('mceNegesoExit', t._exit, t);

			// Register buttons
			ed.addButton('save', {title : 'negeso_save.save_desc', cmd : 'mceNegesoSave'});
			ed.addButton('save_and_close', {title : 'negeso_save.save_and_close_desc', cmd : 'mceNegesoSaveAndClose'});
			ed.addButton('exit', {title : 'negeso_save.exit_desc', cmd : 'mceNegesoExit'});

			ed.onNodeChange.add(t._nodeChange, t);
			ed.addShortcut('ctrl+s', ed.getLang('negeso_save.save_desc'), 'mceSave');
			ed.addShortcut('ctrl+w', ed.getLang('negeso_save.exit_desc'), 'mceNegesoExit');
		},

		getInfo : function() {
			return {
				longname : 'Save',
				author : 'Mykola Lyhozhon'
			};
		},

		// Private methods

		_nodeChange : function(ed, cm, n) {
			var ed = this.editor;
//
//			if (ed.getParam('save_enablewhendirty')) {
//				cm.setDisabled('save', !ed.isDirty());
//				cm.setDisabled('save_and_close', !ed.isDirty());
//			}
		},

		_save_and_exit : function() {
			this._save(true);
		},
		
		_exit : function() {
            window.close();
		},
		
		_save : function(_exit) {
			var ed = this.editor;
            var p = this;
			
			if (confirm(ed.getLang('negeso_save.save_confirm'))) {
				p.updateLinksInsideClickableTableCells();
				var adsenseScripts = p.prepareAdsenseScripts();
				
				var inst = tinyMCE.selectedInstance;
				if (ed.getParam("save_enablewhendirty") && !ed.isDirty()) {
					if (_exit) {
						p._exit();
					}
					return;
				}
				if (typeof (tinyMCE.negeso_pre_request) != "undefined") {
					var RTE_Negeso_Source_ID = (tinyMCE.source_id != null && tinyMCE.source_id != "") ? tinyMCE.source_id : "RTE_Field";
					var RTE_Negeso_Content = "";
					
					if (typeof (tinyMCE.negeso_absolute_urls) != "undefined" && tinyMCE.negeso_absolute_urls == true) {
						tinyMCE.settings["relative_urls"] = false;
						var divObj = window.opener.document.createElement("div");
						if (tinyMCE.is_newsletter) {
							parseCSS(inst.getDoc());
							divObj.innerHTML = ed.getContent();
						} else {
							divObj.innerHTML = ed.getContent({format : 'raw'});
						}
						RTE_Negeso_Content = divObj.innerHTML;
						tinyMCE.settings["relative_urls"] = true;
						var relativeToAbsoluteUrlReplacer = function(str, p1, p2, p3, offset, s) {
							if (p3.indexOf('http') == 0) {
								return str;
							} else {
								return p1 + 'http://' + location.host + '/' + p3;
							}
						}
						RTE_Negeso_Content = RTE_Negeso_Content.replace(/(<img[^>]*src=")(\.?\.?\/?)([^"]*)/gi, relativeToAbsoluteUrlReplacer);
					} else {
						RTE_Negeso_Content = tinymce.trim(ed.getContent());
					}
					
					if (RTE_Negeso_Content == ""){ RTE_Negeso_Content = " ";}
					
					if (typeof(tinymce.PluginManager.get('negeso_form_manager')) != undefined) {
						RTE_Negeso_Content = p.processForm(RTE_Negeso_Content);
					}
					
					RTE_Negeso_Content = p.removeAdsenseScripts(RTE_Negeso_Content, adsenseScripts)							
					if (tinyMCE.article_id > 0) {
						$.ajax({
							url: "/adminwebservice?WSDL",
							data: tinyMCE.negeso_pre_request(RTE_Negeso_Content, (tinyMCE.article_id != null ? tinyMCE.article_id : -1)),
							dataType: "text",
							type: "POST",
							success: function(response1) {
								if (response1.search("fault") == -1) {
									p._updateContentInElement(RTE_Negeso_Content, RTE_Negeso_Source_ID, _exit);
								} else alert("You can not save content to the server.\nMay be you have no rights to do so,\nor document was not well formed.\nRe-login and try again later, or check the content.");
							},
							error : function() {
								alert("Internal server error");
							}
						});
					} else {
						p._updateContentInElement(RTE_Negeso_Content, RTE_Negeso_Source_ID, _exit);
					}
				} else {
					alert("Internal RTE error: Can not find necessary methods for AJAX save");
				}
				return true;
			}
		},
		
		_updateContentInElement : function(RTE_Negeso_Content, RTE_Negeso_Source_ID, _exit) {
			var ed = this.editor;
            var p = this;
            tinyMCE.negeso_backup_content = ed.getContent({format : 'raw'});
			var RTE_Negeso_Dest_IDs;
			if (tinyMCE.dest_ids)
				RTE_Negeso_Dest_IDs = tinyMCE.dest_ids.split(',');
			else
				RTE_Negeso_Dest_IDs = new Array(RTE_Negeso_Source_ID);
			for (var m = 0; m < RTE_Negeso_Dest_IDs.length; m++) {
				var temp_dest = window.opener.document.getElementById(RTE_Negeso_Dest_IDs[m]);
				// May be mistake:
				//var temp_dest = window.opener.document.getElementById(RTE_Negeso_Source_ID);
				if (temp_dest != null) {
					if (typeof (temp_dest.value) != "undefined") {
						temp_dest.value = RTE_Negeso_Content;
						if (_exit) {
							tinyMCE.triggerSave();
							ed.isNotDirty = 1;
							p._exit();
						}
					}
					else if (typeof (temp_dest.innerHTML) != "undefined") {
						temp_dest.innerHTML = RTE_Negeso_Content;
						makeReadonly(temp_dest, true);
						if (_exit) {
							tinyMCE.triggerSave();
							ed.isNotDirty = 1;
							p._exit();
						}
					}
					else alert("Can not save: destination element has no attributes \"value\" or \"innerHTML\"");
				} else alert("Sorry, but element with ID = " + RTE_Negeso_Source_ID + " can not be found");
			}
		},
		
		updateLinksInsideClickableTableCells : function() {
			// New adds: by Mykola Lyhozhon, 06.01.2012
            // Updating a links in TD elements which also can links to
            $('a', tinyMCE.selectedInstance.getDoc()).each(function(){
            	$(this).removeAttr("onclick");
            	var atag = this;
            	var tds = $(this).parents('td').each(function(){
            		var link_to = $(this).attr('link_to');
            		if (link_to != "undefined" && link_to != null && link_to.length > 0) {
            			$(atag).attr('onclick', "event.cancelBubble = true;if(event.stopPropagation) event.stopPropagation();location.href=this.href;return(false);");
            		}
            	});
            });
		},
		
		prepareAdsenseScripts : function() {
			var adsenseScripts = [];
			var adsCount = 0;
            $('div.googleAdSense', tinyMCE.selectedInstance.getDoc()).each(function(){
            	var width = $(this).attr('google_w');
            	var height = $(this).attr('google_h');
            	$(this).text('Saving...(' + (adsCount++) + ')');
            	adsenseScripts.push('<script type="text\/javascript">'
        				+ 'google_ad_client = "pub-' + $(this).attr('google_client_number') + '";'
        				+ 'google_ad_width=' + width + ';'
        				+ 'google_ad_height=' + height + ';'
//        				+ 'google_ad_format = "' + width + 'x' + height + '_as";'
        				+ ($(this).attr('google_color_border') == '' ? '' : 'google_color_border="' + $(this).attr('google_color_border') + '";')
        				+ ($(this).attr('google_color_bg') == '' ? '' : 'google_color_bg="' + $(this).attr('google_color_bg') + '";')
        				+ ($(this).attr('google_color_link') == '' ? '' : 'google_color_link="' + $(this).attr('google_color_link') + '";')
        				+ ($(this).attr('google_color_text') == '' ? '' : 'google_color_text="' + $(this).attr('google_color_text') + '";')
        				+ ($(this).attr('google_color_url') == '' ? '' : 'google_color_url="' + $(this).attr('google_color_url') + '";')
        				+ ($(this).attr('google_ad_type') == '' ? '' : 'google_ad_type="' + $(this).attr('google_ad_type') + '";')
        				+ ($(this).attr('google_ui_features') == '' ? '' : 'google_ui_features="rc:' + $(this).attr('google_ui_features') + '";')
        				+ '<\/script><script type="text\/javascript" src="http:\/\/pagead2.googlesyndication.com\/pagead\/show_ads.js"><\/script>')
            });
            return adsenseScripts;
		},
		
		removeAdsenseScripts : function(content, adsenseScripts) {
			for (i = 0; i< adsenseScripts.length; i++) {
				content = content.replace('Saving...(' + i + ')', adsenseScripts[i]);
			}
			$('div.googleAdSense', tinyMCE.selectedInstance.getDoc()).each(function(){
				$(this).html('<br/>');
			});
			return content;
		},
		
		processForm : function(content) {
            try {
                if (tinyMCE.negeso_save_form_mode == "as_form_manager") {
                    content = content.replace(new RegExp("(<option[^>]+)inner_text=\"([^\"]*)\"([^>]*>)[^<]*(</option>)", "gi"), "$1$3$2$4");
                } else if (tinyMCE.negeso_save_form_mode == "as_form_insert") {
                    var temp_content = content.toLowerCase();
                    var ind0, ind1, ind2, level;
                    var ind2_abs = 0, ind2_array = new Array();

                    while ((ind0 = temp_content.search(new RegExp("<div[^>]+negeso_form_id[^>]+>"))) != -1) {
                        ind1 = ind2 = ind0;
                        ind2_abs += ind2 + 1;
                        temp_content = temp_content.substr(ind1 + 1);
                        level = 1;
                        do {
                            ind1 = temp_content.search("<div");
                            ind2 = temp_content.search("</div");
                            if (ind1 >= 0 || ind2 >= 0) {
                                if ((ind1 > 0 && ind1 < ind2) || ind2 == -1) {
                                    level++;
                                    temp_content = temp_content.substr(ind1 + 1);
                                    ind2_abs += ind1 + 1;
                                } else if ((ind2 > 0 && ind2 < ind1) || ind1 == -1) {
                                    level--;
                                    temp_content = temp_content.substr(ind2 + 1);
                                    ind2_abs += ind2 + 1;
                                }
                            } else break;
                        } while (level > 0);
                        if (level == 0)
                            ind2_array.push(ind2_abs)
                        else {
                            alert("Negeso Form Manager: error during saving forms");
                            throw "NFM Error 1";
                        }
                    }

                    content = content.replace(new RegExp("<div([^>]+negeso_form_id[^>]+>)", "gi"), "<frm$1");

                    for (var i = 0; i < ind2_array.length; i++) {
                        content = content.substring(0, ind2_array[i]) + '/frm' + content.substring(ind2_array[i] + 4);
                    }

                    content = content.replace(new RegExp("(<frm[^>]+)style=\"[^\">]+\"", "gi"), "$1");
                    content = content.replace(new RegExp("<frm[^>]+negeso_form_id=\"([^\"]+)\"[^>]*>", "gi"), "<FORM class=contact onsubmit=\"return validate(this);\" method=post encType=multipart/form-data form_id=\"$1\">" + "<input type='hidden' name=\"mailToFlag\" value=\"$1\"/><input type='hidden' name='first_obligatory_email_field' value=''/>");
                    content = content.replace(new RegExp("</frm", "gi"), "</FORM");
                }
            } catch (e) {
            	return null; 
            }
            return content;
		}
	});

	// Register plugin
	tinymce.PluginManager.add('negeso_save', tinymce.plugins.NegesoSavePlagin);
})();