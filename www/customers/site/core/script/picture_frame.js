var atrSetId = null;
var imId = null;
var action = null;

function changeMainImage(imId_, atrSetId_, width, height){

	 atrSetId = atrSetId_;
	 imId = imId_;
	 action = "change_main_image";

        MediaCatalog.selectStrictImageDialog(width, height, "picture_frame", "", function(result){
    if (result != null){
        if (result.resCode == "OK"){
            main_form.action_field.value="change";
            main_form.src_field.value=result.realImage;
            main_form.image_id_field.value=imId;
            main_form.attribute_set_id_field.value = atrSetId;
            main_form.submit();
        }
    }
     });
}
function changeMainFlash(imId_, atrSetId_, width, height){

	 atrSetId = atrSetId_;
	 imId = imId_;
	 action = "change_main_flash";

    MediaCatalog.selectStrictFlashDialog(width, height, "picture_frame", "", function(result){
    if (result != null){
        if (result.resCode == "OK"){
//debugger;
           $('#main_form').find('input[name=action_field]').val("changeFlash");
           $('#main_form').find('input[name=src_field]').val(result.fileUrl);
           $('#main_form').find('input[name=flash_width]').val(result.flashWidth);
           $('#main_form').find('input[name=flash_height]').val(result.flashHeight);
           $('#main_form').find('input[name=image_id_field]').val(imId);
           $('#main_form').find('input[name=attribute_set_id_field]').val(atrSetId);

             $('<div id="dialog" title="Please wait..."><p>Flash is saving. Please wait.</p></div>')
			.dialog();


		   var params = $('#main_form').serialize();
        	$.post('/admin/update_wcsm_attributes', params, function(){location.reload();});



        }
    }
       });
}


function setLink(imId){
	   MediaCatalog.selectLinkImageDialog(imId, function(result){
	if (result != null){
		if (result.resCode == "OK"){
            main_form.action="update-wcsm-link";
            main_form.image_id_field.value=imId;
            main_form.submit();
         }
    }
        });
}

function resultUploadImage(){

	var result = returnValue;
    if (result != null){

    	var main_form = $('form#main_form').get(0);//because don't see  main_form in FF

        if (result.resCode == "OK" && main_form){


        	if(action == "add_image" ){
                main_form.action_field.value="add";
                main_form.src_field.value=result.realImage;
                main_form.image_set_id_field.value=isID;
                main_form.image_id_field.value="";
                main_form.attribute_set_id_field.value = atrSetId;
                main_form.attribute_class_field.value = attrClass;
            }

            if(action == "change_image" ){
                main_form.action_field.value="change";
                main_form.src_field.value=result.realImage;
                main_form.image_set_id_field.value=isID;
                main_form.image_id_field.value=imId;
                main_form.attribute_set_id_field.value = atrSetId;
            }

            if(action == "change_flash"){
                main_form.action_field.value="changeFlash";
                main_form.src_field.value=result.fileUrl;
                main_form.flash_width.value=result.flashWidth;
                main_form.flash_height.value=result.flashHeight;
                main_form.image_id_field.value=imId;
                main_form.attribute_set_id_field.value = atrSetId;
                main_form.image_set_id_field.value=isID;
            }

        	if(action == "change_main_image"){
                main_form.action_field.value="change";
                main_form.src_field.value=result.realImage;
                main_form.image_id_field.value=imId;
                main_form.attribute_set_id_field.value = atrSetId;
        	}

        	if(action == "change_main_flash"){
                main_form.action_field.value="changeFlash";
                main_form.src_field.value=result.fileUrl;
                main_form.flash_width.value=result.flashWidth;
                main_form.flash_height.value=result.flashHeight;
                main_form.image_id_field.value=imId;
                main_form.attribute_set_id_field.value = atrSetId;
        	}

			$('<div id="dialog" title="Please wait...">\
				<p>Picture is saving. Please wait.</p>\
			</div>')
			.dialog();

        	var params = $('#main_form').serialize();
        	$.post('/admin/update_wcsm_attributes', params, function(){location.reload();});
            //main_form.submit();
        }
	}
}


function changeAnimationImages(idPage){
	window.open(
		"/admin/browse_wcms_attrs?page_id=" + idPage+"&show=imagesAnimation",
		"Imagesediting",
		"height=700px,width=850px,help=No,scroll=yes,scroll=on,scrollbars=yes,status=No,resizable=yes"
	);
}
function changeAnimationProperties(atrSetId){
	window.open(
		"/admin/?command=browse-animation-properties&image_set_id=" + atrSetId,
		null,
		"resizable=on,scroll=no,status=off,width=755,height=351"
	);
}