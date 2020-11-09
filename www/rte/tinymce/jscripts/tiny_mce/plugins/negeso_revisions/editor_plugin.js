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
	tinymce.PluginManager.requireLangPack('negeso_revisions');

	tinymce.create('tinymce.plugins.NegesoRevisions', {
		init : function(ed, url) {
			var t = this;
			t.url = url;
			t.editor = ed;

			// Register commands
			ed.addCommand('mceNegesoRevisions', t._openDialog, t);

			// Register buttons
			ed.addButton('negeso_revisions', {title : 'negeso_revisions.desc', cmd : 'mceNegesoRevisions'});			

			ed.onNodeChange.add(t._nodeChange, t);
		},

		getInfo : function() {
			return {
				longname : 'Negeso RTE Revisions Module',
				author : 'Mykola Lyhozhon',
				authorurl : 'http://www.negeso.com',
				infourl : 'http://www.negeso.com',
				version : '1.0'
			};
		},

		_nodeChange : function(ed, cm, n, co) {
			return true;
		},
		
		_openDialog : function(link_type) {
			var ed = this.editor;
			var se = ed.selection;

			ed.windowManager.open({
				file : this.url + '/revisions.htm',
				width : 900 + parseInt(ed.getLang('negeso_revisions.delta_width', 0)),
				height : 700 + parseInt(ed.getLang('negeso_revisions.delta_height', 0)),
				inline : 1
			}, {
				plugin_url : this.url
			});
		}
		
		});

	// Register plugin
	tinymce.PluginManager.add('negeso_revisions', tinymce.plugins.NegesoRevisions);
})();