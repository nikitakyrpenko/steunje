/**
 *	Security utility functions
 *
 **/   
var Security = new function(){
	/*
	* Show select container dialog
	* Return complex object:
	* 	- result.resCode : "OK" or null - bad result
	* 	- result.containerId : container id
	* 	- result.containerTitle : container title
	*/
	this.selectContainerDialog = function (selectedId, roleId, callback)  {
		var params = "?";
		if (selectedId != null) {
			params += "selectedId=" + selectedId + "&";
		}
		if (roleId != null){
			params += "roleId=" + roleId;
		}
		var strPage = "/admin/select_container" + params;
		var strAttr = "resizable:on;scroll:on;status:off;dialogWidth:605px;dialogHeight:351px";
		showModalDialog(strPage, null , strAttr).then(function(res){
			if (typeof callback === 'function'){
				callback(res);
			}
		})
	}
}

// Adds for implement crossbrowsering
var NewSecurity = new function(){
	this.active = false;
	this.return_value = null;
	this.selectContainerDialog = function(selectedId, roleId) {
		var that = this;
		this.active = true;
		this.return_value = null;
		var params = "?";
		if (selectedId != null) {
			params += "selectedId=" + selectedId + "&";
		}
		if (roleId != null){
			params += "roleId=" + roleId;
		}
		var strPage = "/admin/select_container" + params;
		var strAttr = "height=351, width=605, menubar=no, resizable=yes, scrollbars=no, status=no, titlebar=yes, toolbar=no";
		var modalStrAttr = "resizable:on;scroll:on;status:off;dialogWidth:605px;dialogHeight:351px";
		window.showModalDialog(strPage, null, modalStrAttr).then(function(res){
			this.return_value = res;
			this.active = false;
			if (this.return_value && this.return_value.resCode && this.return_value.resCode == "OK"){
				this.setContainer();
			}
		}.bind(that));
	}
	this.setContainer = function() {}
}
