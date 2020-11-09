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
	tinymce.PluginManager.requireLangPack('negeso_flash');

	tinymce.create('tinymce.plugins.NegesoFlash', {
		init : function(ed, url) {
			var t = this;
			t.url = url;
			t.editor = ed;

			// Register commands
			ed.addCommand('mceAdvLink', t._processLink, t);
			ed.addCommand('mceNegesoAdvLinkToPage', t._processLinkToPage, t);
			ed.addCommand('mceNegesoAdvLinkToFile', t._processLinkToFile, t);

			// Register buttons
			ed.addButton('link', {title : 'advlink.link_desc', cmd : 'mceAdvLink'});
			ed.addButton('negeso_advlink_to_page', {title : 'negeso_advlink.to_page_desc', cmd : 'mceNegesoAdvLinkToPage'});
			ed.addButton('negeso_advlink_to_file', {title : 'negeso_advlink.to_file_desc', cmd : 'mceNegesoAdvLinkToFile'});
			ed.addShortcut('ctrl+k', 'advlink.advlink_desc', 'mceAdvLink');
			

			ed.onNodeChange.add(t._nodeChange, t);
		},

		getInfo : function() {
			return {
				longname : 'Negeso RTE Insert Flash Module',
				author : 'Mykola Lyhozhon',
				authorurl : 'http://www.negeso.com',
				infourl : 'http://www.negeso.com',
				version : '1.0'
			};
		},

		_nodeChange : function(ed, cm, n, co) {
			cm.setDisabled('link', co && n.nodeName != 'A');
			cm.setActive('link', n.nodeName == 'A' && !n.name);
			cm.setDisabled('negeso_advlink_to_page', co && n.nodeName != 'A');
			cm.setActive('negeso_advlink_to_page', n.nodeName == 'A' && !n.name);
			cm.setDisabled('negeso_advlink_to_file', co && n.nodeName != 'A');
			cm.setActive('negeso_advlink_to_file', n.nodeName == 'A' && !n.name);
		},
		
		_processLink : function() {
			this._openLinkSettingsDialog('');
		},
		
		_processLinkToPage : function() {
			this._openLinkSettingsDialog('page');
		},
		
		_processLinkToFile : function() {
			this._openLinkSettingsDialog('file');
		},
		
		_openLinkSettingsDialog : function(link_type) {
			var ed = this.editor;
			var se = ed.selection;

			// No selection and not in link
			if (se.isCollapsed() && !ed.dom.getParent(se.getNode(), 'A'))
				return;

			ed.windowManager.open({
				file : this.url + '/link.htm',
				width : 480 + parseInt(ed.getLang('advlink.delta_width', 0)),
				height : 400 + parseInt(ed.getLang('advlink.delta_height', 0)),
				inline : 1
			}, {
				plugin_url : this.url,
				link_type : link_type
			});
		}
		
		});

	// Register plugin
	tinymce.PluginManager.add('negeso_advlink', tinymce.plugins.NegesoAdvLink);
})();