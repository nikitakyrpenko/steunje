(function() {
	tinymce.PluginManager.requireLangPack('negeso_advimage');
	
	tinymce.create('tinymce.plugins.NegesoAdvancedImagePlugin', {
		init : function(ed, url) {
			var t = this;
			t.url = url;
			t.editor = ed;
			
			ed.addCommand('mceNegesoAdvImage', t._processImage, t);
			ed.addCommand('mceNegesoAdvImageAdd', t._addImage, t);
			ed.addCommand('mceNegesoAdvImageMedia', t._processMediaImage, t);
			ed.addCommand('mceNegesoAdvImageMediaHandler', t._handleMediaImage, t)

			ed.addButton('image', {title : 'negeso_advimage.image_desc', cmd : 'mceNegesoAdvImage'});
			ed.addButton('negeso_advimage_add', {title : 'negeso_advimage.image_add_desc', cmd : 'mceNegesoAdvImageAdd'});
			ed.addButton('negeso_advimage_media', {title : 'negeso_advimage.image_media_desc', cmd : 'mceNegesoAdvImageMedia'});
		},

		getInfo : function() {
			return {
				longname : 'Negeso RTE Advanced image module',
				author : 'Mykola Lyhozhon',
				authorurl : 'http://www.negeso.com',
				infourl : 'http://www.negeso.com',
				version : '1.0'
			};
		},
		
		_processImage : function() {
			var ed = this.editor;
			if (ed.dom.getAttrib(ed.selection.getNode(), 'class', '').indexOf('mceItem') != -1)
				return;

			ed.windowManager.open({
				file : this.url + '/image.htm',
				width : 480 + parseInt(ed.getLang('negeso_advimage.delta_width', 0)),
				height : 385 + parseInt(ed.getLang('negeso_advimage.delta_height', 0)),
				inline : 1
			}, {
				plugin_url :  this.url,
				mode: 'advanced'
			});
		},
		
		_addImage : function() {
			var ed = this.editor;
			if (ed.dom.getAttrib(ed.selection.getNode(), 'class', '').indexOf('mceItem') != -1)
				return;

			ed.windowManager.open({
				file : '/?command=get-second-image-uploader-face',
				width : 480 + parseInt(ed.getLang('negeso_advimage.delta_width', 0)),
				height : 460 + parseInt(ed.getLang('negeso_advimage.delta_height', 0)),
				inline : 1
			}, {
				plugin_url :  this.url
			});
		},
		
		_processMediaImage : function() {
			var ed = this.editor;
			if (ed.dom.getAttrib(ed.selection.getNode(), 'class', '').indexOf('mceItem') != -1)
				return;
			MediaCatalog.insert_link_mode = "image";
			MediaCatalog.is_active=false;
			var strPage = "/?command=list-directory-command&actionMode=chooser&viewMode=image_gallery";
			var strAttr = "width=800px, height=576px, directories=no, location=no, menubar=no, resizable=yes, scrollbars=yes, status=no, titlebar=yes, toolbar=no, modal=yes";
			var fileLink = window.open(strPage, null/*lArg*/ , strAttr);
		
		},
		
		_handleMediaImage : function(unknownOldparam, src) {
			var inst = this.editor;
			var elm = inst.selection.getNode();
			var m = '/media/';
			if (src.indexOf(m) != -1) {
				src = src.substring(src.indexOf(m));
			}

			if (elm != null && elm.nodeName == "IMG") {
				inst.dom.setAttrib(elm, 'src', src);
				inst.dom.setAttrib(elm, 'mce_src', src);
				if (tinyMCE.isMSIE5) {
					elm.outerHTML = elm.outerHTML;
				}
			} else {
				var html = "<img";
				html += this.makeAttrib('src', src);
				html += this.makeAttrib('mce_src', src);				
				html += " />";
				inst.execCommand("mceInsertContent", false, html);
			}
		},
		
		makeAttrib : function(attrib, value) {
			if (typeof(value) == "undefined" || value == null || value == "")
				return "";

			value = value.replace(/&/g, '&amp;');
			value = value.replace(/\"/g, '&quot;');
			value = value.replace(/</g, '&lt;');
			value = value.replace(/>/g, '&gt;');

			return ' ' + attrib + '="' + value + '"';
		}
	});

	tinymce.PluginManager.add('negeso_advimage', tinymce.plugins.NegesoAdvancedImagePlugin);
})();