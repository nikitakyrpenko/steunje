function prependZero(num){
    return num > 9 ? ("" + num) : ("0" + num);
}

function showPage(){        	
    var today = new Date();
    var yesterday = new Date(today.getFullYear(), today.getMonth(), today.getDate());
        	
    document.getElementById("expiredDateId").value = "";
    document.getElementById("publishDateId").value = 
        prependZero(yesterday.getDate()) + "-" + 
        prependZero(yesterday.getMonth() + 1) + "-" +                 
		prependZero(yesterday.getFullYear());
}

function hidePage(){
    var today = new Date();
    var yesterday = new Date(today.getFullYear(), today.getMonth(), today.getDate());
    		
    document.getElementById("expiredDateId").value =
        prependZero(yesterday.getDate()) + "-" +
        prependZero(yesterday.getMonth() + 1) + "-" +
        prependZero(yesterday.getFullYear());
    document.getElementById("publishDateId").value = "";
}
        
        
function validPageInfo(){       
    var pageTitle = document.getElementById("title").value;
    if(pageTitle.length < 1){
        alert(s_PageNameCannotBeEmpty);
        pageTitle.focus();
        return false;
    }
          
    var onlySpaces = true;
    for(i = 0; i < pageTitle.length; i++){
        if(pageTitle.charAt(i) != ' '){
            onlySpaces = false;
            break;
        }
    }
            
    if(onlySpaces) {
        alert(s_PageNameCannotConsistOfSpacesAlone);
        document.getElementById("title").focus();
        return false;
    }
            
    /* check page filename*/
            
    var name = document.getElementById("filename_s").value;
    if(name.length < 1){
        alert(s_FileNameCannotBeEmpty);
        return false;
    }
    var length = 256;
    if(name.length > length){
        alert(s_FileNameIsTooLong + ".\n" + s_MaximumCharacters.replace(/26/, length));
        return false;
    }
            
    for(var i=0; i < name.length; i++){
        if( "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_:".indexOf(name.charAt(i)) == -1 ){
            alert(spacesAreNotAllowed);
            document.getElementById("filename_s").focus();
            return false;
        }
    }
        
    return true;
}
        
function createNewPage(){        	
	if(!validPageInfo()){
	    return;
	}

	var fullFileName =  document.getElementById("filename_s").value + "_" + langCode + ".html";
	$.ajax({ url : '/admin/create_page?act=check_filename&filename=' + fullFileName, 
	    success: function(){
				    var name = document.getElementById("filename_s").value;
				    document.getElementById("filename").value = name + "_" + langCode + ".html";  
				    document.forms["createPage"].submit();
				    	  		
	    },
	    error:function(xhr) {alert(s_InvalidFileName);return;}
	});    
}
        
      
function sleep(milliSeconds) {
    var startTime = new Date().getTime(); // get the current time
    while (new Date().getTime() < startTime + milliSeconds); 
}


function deletePageById(pageId,title) {            
    var warning = DELETE_CONFIRMATION;            
    var answer = "";
    var dialog = $('<DIV id="delete_dialog" title="Delete the ' + title + '"><p>' + DELETE_CONFIRMATION + '</p></DIV>');

    $(dialog).dialog({
        width: 500,
        height: 100,
        modal: true,
        open: function () { var dialog = this; $('#delete_dialog').css('height', '80px') },
        buttons: [
            {
                text: DELETE_NO,
                click: function () { $(this).remove(); }
            },
	        {
		        text: DELETE_YES,
                click: function () {                    
                    $.ajax({
                        type: "GET",
                        url: "/admin/special_pages_editor?act=delete&id=" + pageId,
                        dataType: "html",
                        success: function (date) {
                            $('tr[pageId=' + pageId + ']').remove();                            
                        },
                        error: function (error) {
                            alert("Cannot delete the page " + title);
                        }
                    });                    
                    $(this).remove();
                }
            }
        ],
        close: function () { $(this).remove(); }
    });        	
}
        
        
function goToPage(theUrl,isPopup) {        	
    if(isPopup) {
		displayPopup(theUrl);
	} else {
		try{
		    window.opener.location.href = theUrl;
		    window.opener.focus();
		}catch(e){
		    window.open(
		        theUrl,
		        "mainWindow",
		        "height=" + (screen.availHeight - 155)+ "px"
		        + ", width=" + (screen.availWidth - 12) + "px"
		        + ", menubar=yes, location=yes, resizable=yes"
		        + ", scrollbars=yes, status=yes, titlebar=yes, toolbar=yes"
		        + ", left=0, top=0");
		}
	}		
}
        
        
function selectTemplate() {
    window.open("?command=select-page-template-command&special=",null,"height=350px,width=650px,help=No,scroll=yes,scroll=on,scrollbars=yes,status=No,resizable=yes");
}      
       
function setTemplate(choice){
	if(choice != null && choice.resCode == "OK"){
	    document.getElementById("template").value = choice.templateId;
	    document.getElementById("templateTitle").value = choice.templateTitle;
	}
}
		
		
function translatePage(pageId){
	var isPageNameTranslatable = $('input[name=isPageNameTranslatable]').is(':checked');
	var translateToLang = "";

	$('input[name=translateToLang]:checked').each( function(){
		if(translateToLang != "") {
			translateToLang += ",";
		}
		translateToLang += $(this).val();
	});			
			
	if(translateToLang != "") {			
		$.ajax({
            url: "/admin/stm_settings.html?act=translateSimplePage" + "&id=" + pageId + "&translateToLang=" + translateToLang + "&isPageNameTranslatable=" + isPageNameTranslatable +"&fromLang=" + $("#fromLang").val(),
            dataType: "html",
            success: function(html,stat) {                               
                $("#translateMessage").html($('#translatedPages',html).html());
            },
            error: function(html, stat) {
                $("#translateMessage").html("Internal server error occured");
            }
        });
	}		
}
		
		
function savePage(closeOnSave){
	if(!validPageInfo()){
	        return;
	}  
	var name = document.getElementById("filename_s").value;
	document.getElementById("filename").value = name + "_" + langCode + ".html";
	var obj = new Object();
	obj.title = $("#title").val();
	obj.expiredDate = $("#expiredDateId").val();
	obj.publishDate = $("#publishDateId").val();
	obj.container = $("#containerId option:selected").text();
	obj.menuId = $("#menuId").val();
	if (obj.menuId !="")
		window.opener.editPropertiesAnswer(obj);
	sleep(500);
		 	
	if(closeOnSave){
		document.getElementById("closeOnSave").value="true";
	}
	document.forms["page"].submit();
	sleep(1000);
	window.opener.location = window.opener.location;
}
		
		
function selectImages(pageId){
	window.open("/admin/browse_wcms_attrs?page_id=" + pageId,
				"Imagesediting",
				"height=700px,width=850px,help=No,scroll=yes,scroll=on,scrollbars=yes,status=No,resizable=yes"
				);
}
	
		
		
function selectAliases(pageId){
window.open("/admin/site_settings?action=getUrlAliases&amp;page_id=" + pageId,
			"Imagesediting",
			"height=700px,width=850px,help=No,scroll=yes,scroll=on,scrollbars=yes,status=No,resizable=yes"
			);
}
		
function editPageProperties(pageId) {
    window.open("/admin/edit_page_info?act=edit&id=" + pageId ,"page_properties_"+new Date().getTime(),"height=920, width=825, menubar=no, resizable=yes,  status=no, titlebar=yes, toolbar=no, scrollbars=yes");
}
         
function addPage(){
    var t_win = window.open("/admin/create_page?act=add","","height=780px,width=650px,scrollbars=yes,status=no");
}
        
        

        
   
