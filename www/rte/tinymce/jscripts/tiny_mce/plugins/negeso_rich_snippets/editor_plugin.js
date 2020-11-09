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
	tinymce.PluginManager.requireLangPack('negeso_rich_snippets');

	tinymce.create('tinymce.plugins.NegesoRichSnippets', {
		init : function(ed, url) {
			var t = this;
			t.url = url;
			t.editor = ed;

			// Register commands
			ed.addCommand('mceNegesoRichSnippets', t._openDialog, t);
			ed.addCommand('mceNegesoRichSnippetsProp', t._openPropDialog, t);
			// Register buttons
			ed.addButton('negeso_rich_snippets', {title : 'negeso_rich_snippets.desc', cmd : 'mceNegesoRichSnippets'});			
			ed.addButton('negeso_rich_prop', {title : 'negeso_rich_snippets.desc_prop', cmd : 'mceNegesoRichSnippetsProp'});			

			ed.onNodeChange.add(t._nodeChange, t);
		},

		getInfo : function() {
			return {
				longname : 'Negeso RTE Rich Snippets Module',
				author : 'Mykola Lyhozhon',
				authorurl : 'http://www.negeso.com',
				infourl : 'http://www.negeso.com',
				version : '1.0'
			};
		},

		_nodeChange : function(ed, cm, node, co) {
			var ed = this.editor;
			var se = ed.selection;
			var n = node;
			if (n != null) {
				do {
					if (n.nodeName == "DIV" && ed.dom.getAttrib(n, 'itemtype') != null) {
						break;
					}
				} while ((n = n.parentNode));
			}
			cm.setDisabled('negeso_rich_snippets', co && (n == null || n.nodeName != 'DIV'));
			cm.setActive('negeso_rich_snippets', n != null && n.nodeName == 'DIV');
			var par = n;
			var n = node;
			if (n != null) {
				do {
					if (ed.dom.getAttrib(n, 'itemprop') != null) {
						break;
					}
				} while ((n = n.parentNode));
			}
			var isItemProp = (n != null && ed.dom.getAttrib(n, 'itemprop') != null && ed.dom.getAttrib(n, 'itemprop') != undefined && ed.dom.getAttrib(n, 'itemprop') != "");
			cm.setDisabled('negeso_rich_prop', co && !isItemProp || !co && (par == null || par.nodeName != 'DIV'));
			cm.setActive('negeso_rich_prop', isItemProp);
			return true;
		},
		
		_openDialog : function() {
			var ed = this.editor;
			var se = ed.selection;
			var getDiv = function(node) {
				var temp = node;
				do {
					if (node.nodeName == "DIV" && ed.dom.getAttrib(node, 'itemtype') != null) {
						return node;
					}
				} while ((node = node.parentNode));
				return temp;
			}
			var focusElm = getDiv(se.getNode());

			// No selection and not in link
			if (se.isCollapsed() && !(focusElm != null && focusElm.nodeName == "DIV" && ed.dom.getAttrib(focusElm, 'itemtype') != null))
				return;

			ed.windowManager.open({
				file : this.url + '/rich_snippets.htm',
				width : 430 + parseInt(ed.getLang('negeso_rich_snippets.delta_width', 0)),
				height : 200 + parseInt(ed.getLang('negeso_rich_snippets.delta_height', 0)),
				inline : 1
			}, {
				plugin_url : this.url,
				isProp: false
			});
		},
		
		_openPropDialog : function() {
			var ed = this.editor;
			var se = ed.selection;
			ed.windowManager.open({
				file : this.url + '/rich_snippets_prop.htm',
				width : 430 + parseInt(ed.getLang('negeso_rich_snippets.delta_width', 0)),
				height : 200 + parseInt(ed.getLang('negeso_rich_snippets.delta_height', 0)),
				inline : 1
			}, {
				plugin_url : this.url,
				isProp: true
			});
		}
		
		});

	// Register plugin
	tinymce.PluginManager.add('negeso_rich_snippets', tinymce.plugins.NegesoRichSnippets);
})();