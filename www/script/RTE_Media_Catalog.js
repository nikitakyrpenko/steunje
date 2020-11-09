/* Changes for multy-browser support.          *
 * Made by Rostislav Brizgunov, Negeso-UA 2006 *
 * 20/07/2006                                  */

/* 
 * Set up select box by string value,
 * if value not exists - returns - false. Else - true.
 *
 */
function setUpSelectBox(select_box_id, value){
	var found = false;
	var select_box = document.getElementById(select_box_id);
	for (var i = 0; i < select_box.length; i++) {
		var sel = select_box.options[i];
		if (sel.value == value) {
			sel.selected = true;
			found = true;
			break;
		}
	}
	return found;
}

 
/**
 *	Media catalog utility functions
 *
 **/   
var MediaCatalog = new function() {
	
	// ID of specific select-box or some other HTML element
	this.return_id = "";
	this.win = window;
	this.is_active = true;
	this.insert_mode = "select_box";
	
	/*
	 * Special for insert link directly to the RTE
	 * Variants:
	 *  - image
	 *  - link_to_image
	 */
	this.insert_link_mode = "image";

	this.set_up_select_box = function(select_box_id, value) {
		var found = false;
		var select_box = this.win.document.getElementById(select_box_id);
		if (select_box == null || typeof(select_box) == "undefined")
			return false;
		var sel;
		for (var i = 0; i < select_box.length; i++) {
			sel = select_box.options[i];
			if (sel.value == value || sel.value == "/" + value) {
				sel.selected = true;
				found = true;
				break;
			}
		}
		return found;
	};

	this.set_up_field = function(field_id, value) {
		var found = false;
		var field = this.win.document.getElementById(field_id);
		if (field == null || typeof(field) == "undefined")
			return false;
		field.value = '/' + value;
		return true;
	};

    /**
    * Show media catalog image chooser and set up selected string 
    * in select box (given by parameter and not null).
    * Return link to choosen resource.
    */
    this.chooseImage = function(dest_id) {
		this.return_id = dest_id;
		this.insert_mode = "select_box";
		this.choose("image_gallery");
    };

    this.chooseFlash = function(dest_id) {
		this.return_id = dest_id;
		this.insert_mode = "field";
		this.choose("flash");
    };

    this.chooseFile = function(dest_id) {
		this.return_id = dest_id;
		this.insert_mode = "select_box";
		this.choose("file_manager");
    };

	this.choosePage = function(dest_id) {
		this.return_id = dest_id;
		this.insert_mode = "field";
		var strPage = "?command=get-pages-list-command";
		var strAttr = "width=800px, height=576px, directories=no, location=no, menubar=no, resizable=yes, scrollbars=yes, status=no, titlebar=yes, toolbar=no, modal=no, dialog=no, minimizable=yes, left=300px, top=200px";
		var modal_win = window.open(strPage, null , strAttr);
    };	
    
   /*
    * Show media catalog resource chooser in mode given as a parameter.
    * Available modes:
    *	- image_gallery;
    *	- file_managaer;
    *	- image_list;
    *   - flash
    * Return link to choosen resource.
    */
    this.choose = function(mode) {
		var strPage = "?command=list-directory-command&actionMode=chooser&viewMode=" + mode;
		var strAttr = "width=800px, height=576px, directories=no, location=no, menubar=no, resizable=yes, scrollbars=yes, status=no, titlebar=yes, toolbar=no, modal=yes";
		window.open(strPage, "chooser"+new Date().getTime() , strAttr);
    };

	/* This function returns URL to file from media catalog to the specific field with ID = this.return_id
	 * It is called usually from Media Catalog
	 */
	this.set_file_URL = function(new_rURL, mode) {
		if (mode == null || typeof(mode) == "undefined") mode = "select_box";
		var rURL = (new_rURL != null && typeof(new_rURL) != "undefined") ? new_rURL : "";
		var vURL = rURL;
		if (mode == "select_box") {
			if (!this.set_up_select_box(this.return_id, vURL)) {
				alert("Error: selected file '" + vURL + "' not found in list.");
			}
		} else if (mode == "field") {
			if (!this.set_up_field(this.return_id, vURL)) {
				alert("Error: destination element with ID = " + this.return_id + " not found.");
			}
		} else { alert("Develope error: unknown return mode"); }
	};

};

var MediaCatalog2 = new function() {
    /**
    * Show media catalog image chooser and set up selected string 
    * in select box (given by parameter and not null).
    * Return link to choosen resource.
    */
    this.chooseImage = function(select_box_id) {
		
		var resource = this.choose("image_gallery");
		if (resource != null && typeof(resource) != "undefined") {
			if (select_box_id!= null && typeof(select_box_id) != "undefined") {
				if (!setUpSelectBox(select_box_id, resource)) {
					alert("Error: selected file '" +  resource + "' not found in list.");
				}
			}
		}
		return resource;
    }

    /**
    * Show media catalog file chooser and set up selected string 
    * in select box (given by parameter and not null).
    * Return link to choosen resource.
    */
    this.chooseFile = function(select_box_id){
		var resource = this.choose("file_manager");
		if (resource != null && typeof(resource) != "undefined") {
			if (select_box_id!= null && typeof(select_box_id) != "undefined") {
				if (!setUpSelectBox(select_box_id, resource)) {
					alert("Error: selected file '" +  resource + "' not found in list.");
				}
			}	
		}
		return resource;
    }

   /**
    * Show media catalog resource chooser in mode given as a parameter.
    * Available modes:
    *	- image_gallery;
    *	- file_managaer;
    *	- image_list;
    * Return link to choosen resource.
    */
    this.choose = function(mode) {
		var lArg = new Object();
		lArg.vURL = "";
		lArg.rURL = "";
		
		var strPage = "?command=list-directory-command&actionMode=chooser&viewMode=" + mode;
		var strAttr = "width=800px, height=576px, directories=no, location=no, menubar=no, resizable=yes, scrollbars=yes, status=no, titlebar=yes, toolbar=no, modal=yes";
		var fileLink = window.open(strPage, null/*lArg*/ , strAttr);
		if (fileLink=='OK')
			return lArg.rURL
		return null;
    }
    
   /**
    * Show select file dialog.
    * Return complex object:
    * 	- result.resCode : "OK" or null - bad result
    * 	- result.fileUrl : link to selected document 
    */
	this.selectFileDialog = function () {
		var strPage = "?command=get-file-uploader-face";
		var strAttr = "width=650px, height=401px, directories=no, location=no, menubar=no, resizable=yes, scrollbars=yes, status=no, titlebar=yes, toolbar=no, modal=yes";
		var result = window.open(strPage, null , strAttr);
        return result;
    }

   /**
    * Show select document dialog.
    * Return complex object:
    * 	- result.resCode : "OK" or null - bad result
    * 	- result.fileUrl : link to selected document
    */
	this.selectDocumentDialog = function (workFolder) {
		var strPage = "/admin/?command=get-file-uploader-face&mode=doc";
		if (workFolder != null && workFolder != "") {
			strPage += "&workfolder=" + workFolder;
		}
		var strAttr = "width=650px, height=401px, directories=no, location=no, menubar=no, resizable=yes, scrollbars=yes, status=no, titlebar=yes, toolbar=no, modal=yes";
        var result = window.open(strPage, null , strAttr);
        return result;
    }

    /**
    * Show select flash dialog.
    * Return complex object:
    * 	- result.resCode : "OK" or null - bad result
    * 	- result.fileUrl : link to selected document
    */
	this.selectFlashDialog = function () {
		var resource = this.choose("flash");
		if (resource != null && typeof(resource) != "undefined") {
			var result = new Object();
			result.resCode = "OK";
			result.fileUrl = resource;
			return result;
		}
		result = new Object();
		result.resCode = "BAD";
		result.fileUrl = resource;
		return result;
    }

   /**
    * Show select document dialog.
    * Return complex object:
    * 	- result.resCode : "OK" or null - bad result
    * 	- result.realImage : link to selected image 
    */
	this.selectImageDialog = function (width, height) {
		if (width == 0) width = null;
		if (height == 0) height = null;
		var strPage;
		if (height == null && width != null) {
	        strPage = "?command=get-file-uploader-face&mode=image" + 
	            "&type=by_width" +
	            "&force_resize_mode=proportional" +
	            "&width=" + width;
		} else if (height != null && width == null) {
	        strPage = "?command=get-file-uploader-face&mode=image" + 
	            "&type=by_height" +
	            "&force_resize_mode=proportional" +
	            "&height=" + height;
		} else {
	        strPage = "?command=get-file-uploader-face&mode=image" + 
	            "&type=by_width" +
	            "&force_resize_mode=proportional" +
	            "&width=" + width +
	            "&height=" + height;
		}	
		var strAttr = "width=650px, height=551px, directories=no, location=no, menubar=no, resizable=yes, scrollbars=yes, status=no, titlebar=yes, toolbar=no, modal=yes";
		var result = window.open(strPage, null , strAttr);
		return result;
	}
    
   /**
    * Show select image dialog. Image uploading is stricted by:
    *   - required image height;
    *   - required image width;
    * Return complex object:
    * 	- result.resCode : "OK" or null - bad result
    * 	- result.realImage : link to selected image 
    */
	this.selectStrictImageDialog = function (width, height, profile_id){
		if (width == 0) width = null;
		if (height == 0) height = null;
		var strPage = "?command=get-file-uploader-face&mode=image" + 
			"&type=strict" +
			"&force_resize_mode=proportional" +
			"&width=" + width + 
			"&height=" + height +
			"&profile_id=" + profile_id;
		var strAttr = "width=605px, height=551px, directories=no, location=no, menubar=no, resizable=yes, scrollbars=yes, status=no, titlebar=yes, toolbar=no, modal=yes";
		var result = window.open(strPage, null , strAttr);
		return result;
    }

   /**
    * Show select image dialog. Image uploading is stricted by height:
    *   - requiredHeight;
    *   - maxWidth (parameter not required);
    * Return complex object:
    * 	- result.resCode : "OK" or null - bad result
    * 	- result.realImage : link to selected image 
    */
	this.selectStrictByHeightImageDialog = function (height, maxWidth, profile_id) {
		if (maxWidth == 0) maxWidth = null;
		if (height == 0) height = null;
        var strPage = "?command=get-file-uploader-face&mode=image" + 
            "&type=by_height" +
            "&is_strict=yes" +
            "&force_resize_mode=proportional" +
            "&height=" + height +
            "&profile_id=" + profile_id;
        if (maxWidth != null) {
            strPage = strPage + "&width=" + maxWidth;
        }
        var strAttr = "width=605px, height=551px, directories=no, location=no, menubar=no, resizable=yes, scrollbars=yes, status=no, titlebar=yes, toolbar=no, modal=yes";
        var result = window.open(strPage, null , strAttr);
        return result;
    }

   /**
    * Show select image dialog. Image uploading is stricted by height:
    *   - requiredWidth;
    *   - maxHeight (parameter not required);
    * Return complex object:
    * 	- result.resCode : "OK" or null - bad result
    * 	- result.realImage : link to selected image 
    */
	this.selectStrictByWidthImageDialog = function (width, maxHeight){
		if (width == 0) width = null;
		if (maxHeight == 0) maxHeight = null;
        var strPage = "?command=get-file-uploader-face&mode=image" + 
            "&type=by_width" +
            "&is_strict=yes" +
            "&force_resize_mode=proportional" +
            "&width=" + width;
        if (maxHeight != null) {
            strPage = strPage + "&height=" + maxHeight;
        }
        var strAttr = "width=605px, height=451px, directories=no, location=no, menubar=no, resizable=yes, scrollbars=yes, status=no, titlebar=yes, toolbar=no, modal=yes";
        var result = window.open(strPage, null , strAttr);
        return result;
    }


   /**
    * Show select document dialog.
    * Return complex object:
    * 	- result.resCode : "OK" or null - bad result
    * 	- result.realImage : link to selected image 
    * 	- result.thumbnailImage : link to thumbnail image
    */
	this.selectThumbnailImageDialog = function (thWidth, thHeight){
		if (thWidth == 0) thWidth = null;
		if (thHeight == 0) thHeight = null;
        var strPage = "?command=get-file-uploader-face&mode=thumbnail" + 
            "&type=by_width" +
            "&force_resize_mode=proportional" +
            "&width=" + thWidth +
            "&height=" + thHeight;
        var strAttr = "width=605px, height=451px, directories=no, location=no, menubar=no, resizable=yes, scrollbars=yes, status=no, titlebar=yes, toolbar=no, modal=yes";
        var result = window.open(strPage, null , strAttr);
        return result;
    }   

   /**
    * Show select mass image upoloader dialog.
    * Input parameters:
    * 	- width 				thumbnail Width    
    * 	- height				thumbnail Height
    * 	- folderId				Id of mc_folder domain object
    * Return complex object:
    * 	- result.resCode : "OK" or null - bad result
    * 	- result.images : Array of objects:
    * 		- imageObj.imageName
    * 		- imageObj.imageWidth
    * 		- imageObj.imageHeight
    * 		- imageObj.thumbnailName
    * 		- imageObj.thumbnailWidth
    * 		- imageObj.thumbnailHeight
    */
	this.selectMassThumbnailImageDialog = function (thWidth, thHeight, folderId){
		if (thWidth == 0) thWidth = null;
		if (thHeight == 0) thHeight = null;
        var strPage = "mass_th_uploader" + 
            "?width=" + thWidth +
            "&height=" + thHeight +
            "&working_folder_id=" + folderId;
        var strAttr = "width=740px, height=570px, directories=no, location=no, menubar=no, resizable=yes, scrollbars=yes, status=no, titlebar=yes, toolbar=no, modal=yes";
        var result = window.open(strPage, null , strAttr);
        return result;
    }   


   /**
    * Show select mass image upoloader dialog.
    * Input parameters:
    * 	- folderId				Id of mc_folder domain object
    * 	- uploadParamsId		Image upload parameters id (for maxSize, etc)
    * Return complex object:
    * 	- result.resCode : "OK" or null - bad result
    * 	- result.images : Array of objects:
    * 		- imageObj.imageName
    * 		- imageObj.imageWidth
    * 		- imageObj.imageHeight
    * 		- imageObj.thumbnailName
    * 		- imageObj.thumbnailWidth
    * 		- imageObj.thumbnailHeight
    */
	this.selectMassThUploaderByParamsId = function (uploadParamsId, folderId){
        var strPage = "mass_th_uploader" + 
            "?working_folder_id=" + folderId  +
            "&upload_params_id=" + uploadParamsId;
        var strAttr = "width=740px, height=570px, directories=no, location=no, menubar=no, resizable=yes, scrollbars=yes, status=no, titlebar=yes, toolbar=no, modal=yes";
        var result = window.open(strPage, null , strAttr);
        return result;
    }   

   /**
    * Show select mass file upoloader dialog.
    * Input parameters:
    * 	- folderId				Id of mc_folder domain object
    * 	- uploadParamsId		Image upload parameters id (for maxSize, etc)
    * Return complex object:
    * 	- result.resCode : "OK" or null - bad result
    * 	- result.images : Array of objects:
    * 		- fileObj.fileName
    */
	this.selectMassFileUploader = function(folderId){
        var strPage = "mass_file_uploader" + 
            "?working_folder_id=" + folderId;
        var strAttr = "width=740px, height=570px, directories=no, location=no, menubar=no, resizable=yes, scrollbars=yes, status=no, titlebar=yes, toolbar=no, modal=yes";
        var result = window.open(strPage, null , strAttr);
        return result;
    }
}