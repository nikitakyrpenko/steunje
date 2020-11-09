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
	tinymce.PluginManager.requireLangPack('negeso_form_manager');

	tinymce.create('tinymce.plugins.NegesoFormManager', {
		init : function(ed, url) {
			var t = this;
			t.url = url;
			t.editor = ed;

			// Register commands
			ed.addCommand('mceNegesoInput', function() {
				this._openDialog(this.url + '/form_manager_input.htm', 480, 265);
			}, t);
			ed.addCommand('mceNegesoCleanup', function() {
				ed.execCommand("mceCleanup");return true;
			}, t);
			ed.addCommand('mceNegesoTextarea', function() {
				this._openDialog(this.url + '/form_manager_textarea.htm', 480, 285);
			}, t);
			ed.addCommand('mceNegesoSelect', function() {
				this._openDialog(this.url + '/form_manager_select.htm', 480, 365);
			}, t);
			ed.addCommand('mceNegesoList', function() {
				this._openDialog('/?command=second-choose-form', 620, (tinyMCE.isMSIE ? 575 : 565));
			}, t);
			ed.addCommand('mceNegesoInputText', function() {
				this._openDialog(this.url + '/form_manager_text.htm', 480, 265);
			}, t);
			ed.addCommand('mceNegesoInputPassword', function() {
				this._openDialog(this.url + '/form_manager_password.htm', 480, 265);
			}, t);
			ed.addCommand('mceNegesoInputSubmit', function() {
				this._openDialog(this.url + '/form_manager_submit.htm', 480, 265);
			}, t);
			ed.addCommand('mceNegesoInputReset', function() {
				this._openDialog(this.url + '/form_manager_reset.htm', 480, 265);
			}, t);
			ed.addCommand('mceNegesoInputCheckbox', function() {
				this._openDialog(this.url + '/form_manager_checkbox.htm', 480, 265);
			}, t);
			ed.addCommand('mceNegesoInputRadio', function() {
				this._openDialog(this.url + '/form_manager_radio.htm', 480, 265);
			}, t);
			ed.addCommand('mceNegesoInputFile', function() {
				this._openDialog(this.url + '/form_manager_file.htm', 480, 265);
			}, t);
			ed.addCommand('mceNegesoRadioGroup', function() {
				this._openDialog(this.url + '/form_manager_radiogroup.htm', 480, 365);
			}, t);
			ed.addCommand('mceNegesoCaptchaInput', function() {
				ed.execCommand("mceInsertContent", false, '<input name="captcha_input" type="text" required="true"/>');
			}, t);
			ed.addCommand('mceNegesoCaptchaImage', function() {
				ed.execCommand("mceInsertContent", false, '<div class="captchaImg"><img class="hand" src="../captcha.jpg" onclick="this.src=&#39;/captcha.jpg?&#39;+new Date().getTime()" alt="" width="145" height="76" /></div>');
			}, t);

			// Register buttons
			ed.addButton('form_input', {title : 'negeso_form_manager.desc_input', cmd : 'mceNegesoInput'});			
			ed.addButton('form_cleanup', {title : 'negeso_form_manager.desc_cleanup', cmd : 'mceNegesoCleanup'});			
			ed.addButton('form_textarea', {title : 'negeso_form_manager.desc_textarea', cmd : 'mceNegesoTextarea'});			
			ed.addButton('form_select', {title : 'negeso_form_manager.desc_select', cmd : 'mceNegesoSelect'});			
			ed.addButton('form_list', {title : 'negeso_form_manager.desc_list', cmd : 'mceNegesoList'});			
			ed.addButton('form_text', {title : 'negeso_form_manager.desc_input_text', cmd : 'mceNegesoInputText'});			
			ed.addButton('form_password', {title : 'negeso_form_manager.desc_input_password', cmd : 'mceNegesoInputPassword'});			
			ed.addButton('form_submit', {title : 'negeso_form_manager.desc_input_submit', cmd : 'mceNegesoInputSubmit'});			
			ed.addButton('form_reset', {title : 'negeso_form_manager.desc_input_reset', cmd : 'mceNegesoInputReset'});			
			ed.addButton('form_checkbox', {title : 'negeso_form_manager.desc_input_checkbox', cmd : 'mceNegesoInputCheckbox'});			
			ed.addButton('form_radio', {title : 'negeso_form_manager.desc_input_radio', cmd : 'mceNegesoInputRadio'});			
			ed.addButton('form_file', {title : 'negeso_form_manager.desc_input_file', cmd : 'mceNegesoInputFile'});			
			ed.addButton('form_radio_group', {title : 'negeso_form_manager.desc_radio_group', cmd : 'mceNegesoRadioGroup'});			
			ed.addButton('form_captcha_input', {title : 'negeso_form_manager.desc_captcha_input', cmd : 'mceNegesoCaptchaInput'});			
			ed.addButton('form_captcha_image', {title : 'negeso_form_manager.desc_captcha_image', cmd : 'mceNegesoCaptchaImage'});			

			ed.onNodeChange.add(t._nodeChange, t);
		},

		getInfo : function() {
			return {
				longname : 'Negeso RTE Form Manage Module',
				author : 'Mykola Lyhozhon',
				authorurl : 'http://www.negeso.com',
				infourl : 'http://www.negeso.com',
				version : '1.0'
			};
		},

		_nodeChange : function(ed, cm, n, co) {
			var mceItem = true;
			if (ed.dom.getAttrib(n, 'class')){
				mceItem = ed.dom.getAttrib(n, 'class').indexOf('mceItem') == -1;
			}
			var type = ed.dom.getAttrib(n, 'type');
			this.selectedNode = n;
			
			cm.setActive('form_textarea', n.nodeName == 'TEXTAREA' && mceItem );
			cm.setActive('form_select', n.nodeName == 'SELECT' && mceItem );
			cm.setActive('form_text', n.nodeName == 'INPUT' && mceItem  && type == 'text');
			cm.setActive('form_password', n.nodeName == 'INPUT' && mceItem  && type == 'password');
			cm.setActive('form_submit', n.nodeName == 'INPUT' && mceItem  && type == 'submit');
			cm.setActive('form_reset', n.nodeName == 'INPUT' && mceItem  && type == 'reset');
			cm.setActive('form_checkbox', n.nodeName == 'INPUT' && mceItem  && type == 'checkbox');
			cm.setActive('form_radio', n.nodeName == 'INPUT' && mceItem  && type == 'radio');
			cm.setActive('form_file', n.nodeName == 'INPUT' && mceItem  && type == 'file');
		},
		
		_openDialog : function(_file, _width, _height) {
			var ed = this.editor;
			var se = ed.selection;

			ed.windowManager.open({
				file : _file,
				width : _width + parseInt(ed.getLang('negeso_form_manager.delta_width', 0)),
				height : _height + parseInt(ed.getLang('negeso_form_manager.delta_height', 0)),
				inline : 1
			}, {
				plugin_url : this.url,
				selectedNode : this.selectedNode
			});						
//			var fileLink = window.open(_file, null, "width="+_width+"px, height"+_height+"px, directories=no, location=no, menubar=no, resizable=yes, scrollbars=yes, status=no, titlebar=yes, toolbar=no, modal=yes");
		}
		
		});

	// Register plugin
	tinymce.PluginManager.add('negeso_form_manager', tinymce.plugins.NegesoFormManager);
})();