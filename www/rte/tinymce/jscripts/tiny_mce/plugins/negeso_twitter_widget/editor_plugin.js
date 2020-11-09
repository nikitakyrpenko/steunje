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
	tinymce.PluginManager.requireLangPack('negeso_twitter_widget');

	tinymce.create('tinymce.plugins.NegesoTwitterWidget', {
		init : function(ed, url) {
			var t = this;
			t.url = url;
			t.editor = ed;

			// Register commands
			ed.addCommand('mceNegesoTwitterWidget', t._openDialog, t);

			// Register buttons
			ed.addButton('negeso_twitter_widget', {title : 'negeso_twitter_widget.desc', cmd : 'mceNegesoTwitterWidget'});			

			ed.onNodeChange.add(t._nodeChange, t);
		},

		getInfo : function() {
			return {
				longname : 'Negeso RTE Insert twitter widget Module',
				author : 'Mykola Lyhozhon',
				authorurl : 'http://www.negeso.com',
				infourl : 'http://www.negeso.com',
				version : '1.0'
			};
		},

		_nodeChange : function(ed, cm, node, co) {
			this.selectedNode = node;
			var n = node;
			if (n != null) {
				do {
					if (n.nodeName == "DIV" && n.className == 'negesoTwitterWidget') {
						this.selectedNode = n;
						break;
					}
				} while ((n = n.parentNode));
			}
			cm.setActive('negeso_twitter_widget', n && n.nodeName == 'DIV' && !n.name && n.className == 'negesoTwitterWidget');
			return true;
		},
		
		_openDialog : function(link_type) {
			var ed = this.editor;
			var se = ed.selection;

			ed.windowManager.open({
				file : this.url + '/twitter_widget.htm',
				width : 430 + parseInt(ed.getLang('negeso_twitter_widget.delta_width', 0)),
				height : 450 + parseInt(ed.getLang('negeso_twitter_widget.delta_height', 0)),
				inline : 1
			}, {
				plugin_url : this.url,
				selectedNode : this.selectedNode
			});
		}
		
		});

	// Register plugin
	tinymce.PluginManager.add('negeso_twitter_widget', tinymce.plugins.NegesoTwitterWidget);
})();