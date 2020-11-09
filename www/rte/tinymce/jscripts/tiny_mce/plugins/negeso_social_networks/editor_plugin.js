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
	tinymce.PluginManager.requireLangPack('negeso_social_networks');

	tinymce.create('tinymce.plugins.NegesoSocialNetworks', {
		init : function(ed, url) {
			var t = this;
			t.url = url;
			t.editor = ed;

			// Register commands
			ed.addCommand('mceNegesoSN', t._openSNDialog, t);

			// Register buttons
			ed.addButton('negeso_sn', {title : 'negeso_social_networks.desc', cmd : 'mceNegesoSN'});			

			ed.onNodeChange.add(t._nodeChange, t);
		},

		getInfo : function() {
			return {
				longname : 'Negeso RTE Insert Social Networks Module',
				author : 'Mykola Lyhozhon',
				authorurl : 'http://www.negeso.com',
				infourl : 'http://www.negeso.com',
				version : '1.0'
			};
		},

		_nodeChange : function(ed, cm, n, co) {
			return true;
		},
		
		_openSNDialog : function(link_type) {
			var ed = this.editor;
			var se = ed.selection;

			ed.windowManager.open({
				file : this.url + '/social_networks.htm',
				width : 430 + parseInt(ed.getLang('negeso_social_networks.delta_width', 0)),
				height : 450 + parseInt(ed.getLang('negeso_social_networks.delta_height', 0)),
				inline : 1
			}, {
				plugin_url : this.url
			});
		}
		
		});

	// Register plugin
	tinymce.PluginManager.add('negeso_social_networks', tinymce.plugins.NegesoSocialNetworks);
})();