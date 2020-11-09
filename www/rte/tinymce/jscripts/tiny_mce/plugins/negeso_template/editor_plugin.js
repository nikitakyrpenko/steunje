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
	tinymce.PluginManager.requireLangPack('negeso_template');

	tinymce.create('tinymce.plugins.NegesoTemplate', {
		init : function(ed, url) {
			var t = this;
			t.url = url;
			t.editor = ed;

			// Register commands
			ed.addCommand('mceNegesoTemplate', t._openDialog, t);

			// Register buttons
			ed.addButton('negeso_template', {title : 'negeso_template.desc', cmd : 'mceNegesoTemplate'});
			

			ed.onNodeChange.add(t._nodeChange, t);
		},

		getInfo : function() {
			return {
				longname : 'Negeso RTE Insert HTML Template Module',
				author : 'Mykola Lyhozhon',
				authorurl : 'http://www.negeso.com',
				infourl : 'http://www.negeso.com',
				version : '1.0'
			};
		},

		_nodeChange : function(ed, cm, n, co) {
			return true;
		},
		
		_openDialog : function() {
			var ed = this.editor;
			var se = ed.selection;

			ed.windowManager.open({
				file : this.url + '/template.htm',
				width : 480 + parseInt(ed.getLang('negeso_template.delta_width', 0)),
				height : 400 + parseInt(ed.getLang('negeso_template.delta_height', 0)),
				inline : 1
			}, {
				plugin_url : this.url
			});
		}
		
		});

	// Register plugin
	tinymce.PluginManager.add('negeso_template', tinymce.plugins.NegesoTemplate);
})();