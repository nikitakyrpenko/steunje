/*
 * Set up select box by string value,
 * if value not exists - returns - false. Else - true.
 *
 */
function setUpSelectBox(selectBox, value) {
    found = true;
    for (i = 0; i < selectBox.length; i++) {
        sel = selectBox.options[i];
        if (sel.value == value) {
            sel.selected = true;
            found = true;
            break;
        }
    }
    return found;
}

/**
 *  Media catalog utility functions
 *
 **/
var MediaCatalog = new function() {
    /**
     * Show media catalog image chooser and set up selected string
     * in select box (given by parameter and not null).
     * Return link to choosen resource.
     */
    this.selectBox = null;

    this.chooseImage = function(selectBox, callback) {
        this.selectBox = selectBox;
        this.choose("image_gallery", function(resource){
            if (resource != null) {
                if (selectBox) {
                    if (!setUpSelectBox(selectBox, resource)) {
                        alert("Error: selected file '" + resource + "' not found in list.");
                    }
                }
            }
            if (typeof callback === 'function'){
                callback(resource);
            }
        });
    }


    /**
     * Used by FF, Chrome because this brousers don't  keep window.dialogArguments  through window  reload
     *
     *
     */
    this.chooseCallback = function(resource) {
        if (resource != null) {
            if (this.selectBox) {
                if (!setUpSelectBox(this.selectBox, resource)) {
                    alert("Error: selected file '" + resource + "' not found in list.");
                }
            }
        }
        this.selectBox = null;
        return resource;
    }




    /**
     * Show media catalog file chooser and set up selected string
     * in select box (given by parameter and not null).
     * Return link to choosen resource.
     */
    this.chooseFile = function(selectBox, callback) {
        this.choose("file_manager", function(resource){
            if (resource != null) {
                if (selectBox) {
                    if (!setUpSelectBox(selectBox, resource)) {
                        alert("Error: selected file '" + resource + "' not found in list.");
                    }
                }
            }
            if (typeof callback === 'function'){
                callback(resource);
            }
        });
    }

    this.chooseFlash = function(selectBox, callback) {
        var resource = this.choose("flash", function(resource){
            if (resource != null) {
                if (selectBox) {
                    if (!setUpSelectBox(selectBox, resource)) {
                        alert("Error: selected file '" + resource + "' not found in list.");
                    }
                }
            }
            if (typeof callback === 'function'){
                callback(resource);
            }
        });

    }

    /**
     * Show media catalog resource chooser in mode given as a parameter.
     * Available modes:
     *  - image_gallery;
     *  - file_managaer;
     *  - image_list;
     *  - flash_gallery;
     * Return link to choosen resource.
     */
    this.choose = function(mode, callback) {
        var lArg = new Object();
        lArg.vURL = "";
        lArg.rURL = "";

        strPage = "?command=list-directory-command&actionMode=chooser&viewMode=" + mode;
        strAttr = "resizable:on;scroll:on;status:off;dialogWidth:800px;dialogHeight:576px;help:on";
        showModalDialog(strPage, lArg, strAttr)
            .then(function(fileLink) {
                //console.log(fileLink);
                if (fileLink.text == 'OK') {
                    if (typeof callback === 'function'){

                        callback(lArg.rURL);
                    }
                }
                callback(null);
            });

    }


    /**
     * Show select file dialog.
     * Return complex object:
     *  - result.resCode : "OK" or null - bad result
     *  - result.fileUrl : link to selected document
     */
    this.selectFileDialog = function(callback) {
        strPage = "/admin/?command=get-file-uploader-face";
        strAttr = "resizable:on;scroll:on;status:off;dialogWidth:700px;dialogHeight:400px";
        showModalDialog(strPage, null, strAttr)
            .then(function(result){
                if (typeof callback === 'function'){
                    callback(result);
                }
            });
    }

    /**
     * Show select document dialog.
     * Return complex object:
     *  - result.resCode : "OK" or null - bad result
     *  - result.fileUrl : link to selected document
     */
    this.selectDocumentDialog = function(workFolder, callback) {
        strPage = "/admin/?command=get-file-uploader-face&mode=doc";
        if (workFolder != null && workFolder != "") {
            strPage += "&workfolder=" + workFolder;
        }
        strAttr = "resizable:on;scroll:on;status:off;dialogWidth:650px;dialogHeight:401px";
        showModalDialog(strPage, null, strAttr)
            .then(function(result){
                if (typeof callback === 'function'){
                    callback(result);
                }
            });
    }

    /**
     * DEPRECATED
     *
     * Show select flash dialog.
     * Return complex object:
     *  - result.resCode : "OK" or null - bad result
     *  - result.fileUrl : link to selected document
     */
    this.selectFlashDialog = function() {
        var resource = this.choose("flash");
        if (resource != null) {
            var result = new Object();
            result.resCode = "OK";
            result.fileUrl = resource;
            return result;
        }
        var result = new Object();
        result.resCode = "BAD";
        result.fileUrl = resource;
        return result;
    }

    /**
     * Show select document dialog.
     * Return complex object:
     *  - result.resCode : "OK" or null - bad result
     *  - result.realImage : link to selected image
     */
    this.selectImageDialog = function(width, height, callback) {
        if (width == 0) width = null;
        if (height == 0) height = null;

        if ((height == null) && (width != null)) {
            strPage = "/?command=get-file-uploader-face&mode=image" +
                "&type=by_width" +
                "&force_resize_mode=proportional" +
                "&width=" + width;
        } else if ((height != null) && (width == null)) {
            strPage = "/?command=get-file-uploader-face&mode=image" +
                "&type=by_height" +
                "&force_resize_mode=proportional" +
                "&height=" + height;
        } else {
            strPage = "/?command=get-file-uploader-face&mode=image" +
                "&type=by_width" +
                "&force_resize_mode=proportional" +
                "&width=" + width +
                "&height=" + height;
        }

        strAttr = "resizable:on;scroll:on;status:off;dialogWidth:800px;dialogHeight:600px;";
        // strAttr = "resizable=on,scroll=on,status=off,width=605,height=551";
        //result = showModalDialog(strPage, null , strAttr);
        showModalDialog(strPage, null, strAttr).then(function(result){
            if (typeof callback === 'function'){
                callback(result);
            }
        });
    }

    this.selectBannerDialog = function(width, height, profile_id, window_width, window_height) {
        if (width == 0) width = null;
        if (height == 0) height = null;
        strPage = "/?command=get-file-uploader-face&mode=banner" +
            "&type=strict" +
            "&force_resize_mode=none" +
            "&width=" + width +
            "&height=" + height +
            "&profile_id=" + profile_id;
        if (window_width == null) window_width = 605;
        if (window_height == null) window_height = 550;

        strAttr = "resizable=no, scroll=no, status=no, width=" + window_width + ", height=" + window_height;
        result = window.open(strPage, null, strAttr);
        return result;
    }

    /**
     * Show select image dialog. Image uploading is stricted by:
     *   - required image height;
     *   - required image width;
     * Return complex object:
     *  - result.resCode : "OK" or null - bad result
     *  - result.realImage : link to selected image
     */
    this.selectStrictImageDialog = function(width, height, profile_id, show, callback) {
        if (width == 0) width = null;
        if (height == 0) height = null;
        strPage = "/?command=get-file-uploader-face&mode=image" +
            "&type=strict" +
            "&force_resize_mode=proportional" +
            "&width=" + width +
            "&height=" + height +
            "&profile_id=" + profile_id +
            "&show=" + show;
        strAttr = "resizable=on,scroll=on,status=off,width=620,height=525",
            //strAttr = "height=551,width=785,status=yes,resizable=yes,scrollbars=yes,toolbar=yes,menubar=yes,location=yes,directories=yes";
            showModalDialog(strPage, null, strAttr).then(function(result){
                if (typeof callback === 'function'){
                    callback(result);
                }
            });
    }

    this.selectLinkImageDialog = function(imId, callback) {
        strPage = "/?command=update-wcsm-link&idImg=" + imId;
        strAttr = "resizable=on,scroll=on,status=off,width=620,height=405",
            showModalDialog(strPage, null, strAttr).then(function(result){
                if (typeof callback === 'function'){
                    callback(result);
                }
            });
        return ;
    }

    this.selectAnimationProperties = function(atrSetId) {
        strPage = "/admin/?command=browse-animation-properties&image_set_id=" + atrSetId;
        strAttr = "resizable=on,scroll=on,status=off,width=620,height=395",
            result = window.open(strPage, null, strAttr);
        return result;
    }

    this.selectStrictFlashDialog = function(width, height, profile_id, show, callback) {

        if (width == 0) width = null;
        if (height == 0) height = null;

        strPage = "/?command=get-file-uploader-face&mode=flash" +
            "&type=strict" +
            "&force_resize_mode=proportional" +
            "&width=" + width +
            "&height=" + height +
            "&profile_id=" + profile_id +
            "&show=" + show;
        // strAttr = "resizable=on,scroll=on,status=off,width=620,height=490",
        //result = window.open(strPage, null, strAttr);
        strAttr = "resizable:on;scroll:on;status:off;dialogWidth:680px;dialogHeight:400px;";
        showModalDialog(strPage, null, strAttr)
            .then(function(result){
                if(typeof callback === 'function'){
                    callback(result);
                }
            });
    }

    /**
     * Show select image dialog. Image uploading is stricted by height:
     *   - requiredHeight;
     *   - maxWidth (parameter not required);
     * Return complex object:
     *  - result.resCode : "OK" or null - bad result
     *  - result.realImage : link to selected image
     */
    this.selectStrictByHeightImageDialog = function(current_width, height, maxWidth, flash_quantity, profile_id, callback) {
        var fq_req = "&flash_quantity=";
        if (typeof(flash_quantity) == "undefined" || flash_quantity == null)
            fq_req = "";
        else
            fq_req += flash_quantity;

        //      if ( maxWidth == 0 ) maxWidth = null;
        if (height == 0) height = null;
        strPage = "?command=get-file-uploader-face&mode=image" +
            "&type=by_height" +
            "&is_strict=yes" +
            "&force_resize_mode=proportional" +
            "&height=" + height +
            "&profile_id=" + profile_id;
        if (typeof(current_width) != "undefined" && current_width != null) {
            maxWidth = parseInt(maxWidth) + parseInt(current_width);
        }

        if (maxWidth != null) {
            strPage = strPage + "&width=" + maxWidth;
        }
        strPage += fq_req;
        strAttr = "resizable=on,scroll=on,status=off,width=605,height=551",
            showModalDialog(strPage, null, strAttr)
                .then(function(result){
                    if (typeof callback === 'function'){
                        callback(result);
                    }
                });
    }

    this.selectStrictByHeightFlashDialog = function(height, maxWidth, flash_quantity, profile_id, current_width, callback) {
        var fq_req = "&flash_quantity=";
        if (typeof(flash_quantity) == "undefined" || flash_quantity == null)
            fq_req = "";
        else
            fq_req += flash_quantity;

        if (height == 0) height = null;
        strPage = "?command=get-file-uploader-face&mode=flash" +
            "&type=by_height" +
            "&is_strict=yes" +
            "&force_resize_mode=proportional" +
            "&height=" + height +
            "&profile_id=" + profile_id;

        if (typeof(current_width) != "undefined" && current_width != null) {
            if ((maxWidth == null) || (maxWidth == 0)) maxWidth = current_width;
            else maxWidth = parseInt(maxWidth) + parseInt(current_width);
        }
        if (maxWidth != null) {
            strPage = strPage + "&width=" + maxWidth;
        }
        strPage += fq_req;

        strAttr = "resizable:on;scroll:on;status:off;dialogWidth:605px;dialogHeight:551px";
        showModalDialog(strPage, null, strAttr).then(function(result){
            if(typeof callback === 'function'){
                callback(result);
            }
        });
    }

    /**
     * DEPRECATED
     * Show select image dialog. Image uploading is stricted by height:
     *   - requiredWidth;
     *   - maxHeight (parameter not required);
     * Return complex object:
     *  - result.resCode : "OK" or null - bad result
     *  - result.realImage : link to selected image
     */
    this.selectStrictByWidthImageDialog = function(width, maxHeight) {
        if (width == 0) width = null;
        if (maxHeight == 0) maxHeight = null;
        strPage = "?command=get-file-uploader-face&mode=image" +
            "&type=by_width" +
            "&is_strict=yes" +
            "&force_resize_mode=proportional" +
            "&width=" + width;
        if (maxHeight != null) {
            strPage = strPage + "&height=" + maxHeight;
        }
        strAttr = "resizable:on;scroll:on;status:off;dialogWidth:605px;dialogHeight:451px";
        result = showModalDialog(strPage, null, strAttr);
        return result;
    }


    /**
     * Show select document dialog.
     * Return complex object:
     *  - result.resCode : "OK" or null - bad result
     *  - result.realImage : link to selected image
     *  - result.thumbnailImage : link to thumbnail image
     */
    this.selectThumbnailImageDialog = function(thWidth, thHeight, callback) {
        if (thWidth == 0) thWidth = null;
        if (thHeight == 0) thHeight = null;
        strPage = "?command=get-file-uploader-face&mode=thumbnail" +
            "&type=by_width" +
            "&force_resize_mode=proportional" +
            "&width=" + thWidth +
            "&height=" + thHeight;
        strAttr = "resizable:on;scroll:on;status:off;dialogWidth:740px;dialogHeight:383px";
        showModalDialog(strPage, null, strAttr).then(function(result){
            if(typeof callback === 'function'){
                callback(result);
            }
        });
    }

    /**
     * DEPRECATED
     * Show select mass image upoloader dialog.
     * Input parameters:
     *  - width                 thumbnail Width
     *  - height                thumbnail Height
     *  - folderId              Id of mc_folder domain object
     * Return complex object:
     *  - result.resCode : "OK" or null - bad result
     *  - result.images : Array of objects:
     *      - imageObj.imageName
     *      - imageObj.imageWidth
     *      - imageObj.imageHeight
     *      - imageObj.thumbnailName
     *      - imageObj.thumbnailWidth
     *      - imageObj.thumbnailHeight
     */
    this.selectMassThumbnailImageDialog = function(thWidth, thHeight, folderId) {
        if (thWidth == 0) thWidth = null;
        if (thHeight == 0) thHeight = null;
        strPage = "mass_th_uploader" +
            "?width=" + thWidth +
            "&height=" + thHeight +
            "&working_folder_id=" + folderId;
        strAttr = "resizable:on;scroll:on;status:off;dialogWidth:740px;dialogHeight:570px";
        result = showModalDialog(strPage, null, strAttr);
        return result;
    }


    /**
     * Show select mass image upoloader dialog.
     * Input parameters:
     *  - folderId              Id of mc_folder domain object
     *  - uploadParamsId        Image upload parameters id (for maxSize, etc)
     * Return complex object:
     *  - result.resCode : "OK" or null - bad result
     *  - result.images : Array of objects:
     *      - imageObj.imageName
     *      - imageObj.imageWidth
     *      - imageObj.imageHeight
     *      - imageObj.thumbnailName
     *      - imageObj.thumbnailWidth
     *      - imageObj.thumbnailHeight
     */
    this.selectMassThUploaderByParamsId = function(uploadParamsId, folderId) {
        strPage = "mass_th_uploader" +
            "?working_folder_id=" + folderId +
            "&upload_params_id=" + uploadParamsId;
        strAttr = "resizable:on;scroll:on;status:off;dialogWidth:820px;dialogHeight:600px";
        showModalDialog(strPage, null, strAttr);
    }

    /**
     * Show select mass file upoloader dialog.
     * Input parameters:
     *  - folderId              Id of mc_folder domain object
     *  - uploadParamsId        Image upload parameters id (for maxSize, etc)
     * Return complex object:
     *  - result.resCode : "OK" or null - bad result
     *  - result.images : Array of objects:
     *      - fileObj.fileName
     */
    this.selectMassFileUploader = function(folderId, categoryId) {
        strPage = "mass_file_uploader" +
            "?working_folder_id=" + folderId + "&category_id=" + categoryId;
        strAttr = "resizable=on,scroll=on,status=off,width=740,height=570";
        result = window.open(strPage, null, strAttr);
        return result;
    }
};
