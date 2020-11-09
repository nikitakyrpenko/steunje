function expandOrCollapse(obj, id) {
    var parent = $(obj).parent()
    var isExpand = $(parent).attr('isExpand');
    var imgFolder = $(parent).find('img.admFolder');
    if (isExpand == 'true') {
        $(parent).attr('isExpand', 'false');
        $('div[parentId=' + id + ']').hide();
        $(imgFolder).attr('src', '/images/media_catalog/folderClear.png');
    } else {
        $(parent).attr('isExpand', 'true');
        $('div[parentId=' + id + ']').show();
        $(imgFolder).attr('src', '/images/media_catalog/openFolderClear.png');
    }

}

function showPanel(idPanel, act) {
    var left = parseInt($('#mi_div' + idPanel).width()) + 5;
    if (act == 'true') {
        $('#icons_panel_' + idPanel).css('left', left + 'px').show();
    } else {
        $('#icons_panel_' + idPanel).css('left', left + 'px').hide();
    }
}

function editProperties(pageId,menuId){
    var heightDialog = (parseInt(screen.height) < 940) ? parseInt(screen.height)-180 : 940;	                
    window.open(
        "/admin/edit_page_info?id=" + pageId + "&act=edit&menuId=" + menuId,
        "",
        'width=900,height=' + heightDialog + ',toolbar=yes,status=yes,resizable=yes,scrollbars=yes,top=10');        
}

function editPropertiesAnswer(resultPageInfo) {
    $('#expired_' + resultPageInfo.menuId).text(resultPageInfo.expiredDate);
    $('#publish_' + resultPageInfo.menuId).text(resultPageInfo.publishDate);
    $('#container_' + resultPageInfo.menuId).text(resultPageInfo.container);
}

function deleteMenuItem(obj,idMenu) {
    if( $('div[parentId='+idMenu+']').length < 0){
        alert(Strings.ALL_SUBITEMS_SHOULD_BE_DELETED_FIRST);
        return;
    }
    if( $('div[parentId]').length == 1 ){
        alert(Strings.LAST_ITEM_OF_SITEMAP_CANNOT_BE_DELETED);
        return;
    }                
    // Default warning - for 'alias' or 'url'
    var warning = "<BR/>" + Strings.DELETE_MENU_ITEM_CONFIRMATION;	                                
    try{
        var pageId = $(obj).attr('pageId');
        if(pageId != ""){			
			warning = Strings.MENU_ITEM_MAY_BE_LINKED_FROM_OTHER_PAGES + "<BR/>" +
	            Strings.DELETING_WILL_MAKE_LINKS_INVALID + "<BR/>" +
				_canMoveTo+".";                            
        }
    } catch (e) { }
    var answer = "";                
    var dialog = $('<DIV id="delete_dialog" title="Delete"></DIV>');	           
    $('#removeMenu').find('#warning').html(warning);
    $(dialog).append($('#removeMenu').clone(true).css('display','block'));
	$(dialog).dialog({
		width: 500,
		height: 100,
		modal: true,
		open: function(){ var dialog = this; $('#delete_dialog').css('height','80px')},          
        buttons: [
            {
                text: "Cancel",
                click: function () { $(this).remove(); }
            },
		    {
                text: _moveTo,
                click: function () {                                 
		            var urlAjax = "/?command=delete-menu-item-command&deletePage=false&id="+idMenu;
		            ajax_delete(urlAjax, idMenu);
                    $(this).remove(); 
                } 
            },                        
            {
                text: _removeTotaly,
                click: function () { 
                    var urlAjax = "/?command=delete-menu-item-command&deletePage=true&id="+idMenu;
                    ajax_delete(urlAjax,idMenu);
                    $(this).remove(); 
                }
            }                        
            ],
		    close: function() { $(this).remove(); }
	});
}


function ajax_delete(url, idMenu) {
    $.ajax({
        type: "GET",
        url: url,
        dataType: "html",
        success: function (date) {
            var obj = $('table[menuId=' + idMenu + ']').parent();            
            var parentId = $(obj).attr('parentId');
            $(obj).remove();
            var objMenu = $('div[parentId=' + parentId + '][sort]');
            var level = $('div[parentId=' + parentId + ']').parent('div[parentId]').length;
            if (objMenu.length == 0 && parseInt(level) > 0) {
                $('div[parentId=' + parentId + ']').remove();
            }
            //$(objParent).find('table > tr > td > img.admFolder').class('')
            //admFolder
            reloadPanels(parentId);
        },
        error: function (error) {
            alert("Cannot delete the menu item.");
        }
    });
}


function editMenuItem(id) {
    var serviceUrl = "/admin/menuEditor?action=editMenu&id=" + id;
    $("[class^=menuItemDialog]").each(function() {
        $(this).remove();
    });
    var dialog = "";
    $.ajax({
        type: "POST",
        url: serviceUrl,
        dataType: "html",
        async: false,
        success: function(html, stat) {
            dialog = html;
        },
        error: function(html, stat) { }
    });
    $(document.body).append(dialog);

    $("#menuItemDialog").dialog({
        width: 350,
        modal: true,
        open: function () { $('#menuItemDialog').css('height', '130px') },
        buttons: {
            'Cancel': function () { $(this).remove(); },
            'Save': function () {
                var params = ['title', 'publishDate', 'expiredDate', 'link'];
                values = { act : 'save' };
		    	for (i = 0; i < params.length; i++) {
					values[params[i]]=$("#mid_" + params[i] + "_val").val();
  		        }
                $.ajax({
                    type: "POST",
                    url: serviceUrl,
                    dataType: "html",
                    data : values,
                    async: false,
                    success: function (html, stat) {
                        $('#mi_div' + id).html($('#mid_title_val').val());                        
                        $("#publish_" + id).html($('#mid_publishDate_val').val());
                        $("#expired_" + id).html($('#mid_expiredDate_val').val());
                        $("[class^=menuItemDialog]").each(function () {
                            $(this).remove();
                        });
                    },
                    error: function (html, stat) { }
                });
            }
        }
    });
    $("#menuItemDialog").bind( "dialogclose", function(event, ui) {
        $(this).remove();
    })
    $("#menuItemDialog").dialog('open');
}


function openPageExplorer() {

	    window.showModalDialog(
	        "/dialogs/?command=get-pages-list-command",
	         this,
	        "dialogHeight: 570px; dialogWidth: 540px; help: No; scroll: Yes; status: No;"
    ).then(function(answer){
	try{
	    if(answer != null && typeof(answer) != "undefined"){
	        $("#mid_link_val").val(answer[1]);
	        $("#mid_link_val").attr('text', answer[0]);
	    }
        } catch(e) {}
    });
}

function move(id, up) {
    var serviceUrl = "/admin/menuEditor?action=moveMenu&id=" + id + "&up=" + up;    
    var dialog = "";
    $.ajax({
        type: "POST",
        url: serviceUrl,
        dataType: "html",
        async: false,
        success: function (html, stat) {
            var curObj = $('table[menuId=' + id + ']').parent();
            var parentId = $(curObj).attr('parentId');
            var i = 0;
            var sortObjs = new Object;
            $('div[parentId=' + parentId + '][sort]').each(function () {
                sortObjs[i] = $(this);
                if ($(sortObjs[i]).find('table[menuId]').attr('menuId') == id) {
                    oldPos = i;
                    if (up) {
                        newPos = i - 1;
                    } else {
                        newPos = i + 2;
                    }
                }
                i++;
            });            
            if (newPos == i) {                
                $(curObj).insertBefore($('div[parentId=' + parentId + ']:last'));
            } else {
                $(curObj).insertBefore($(sortObjs[newPos]));
            }
            reloadPanels(parentId);
        },
        error: function (html, stat) { }
    });
}

function reloadPanels(parentId) {
    $('div[parentId=' + parentId + '][sort]').find('td.moveUp,td.moveDown').show();
    $('div[parentId=' + parentId + '][sort]:first').find('td.moveUp').hide();
    $('div[parentId=' + parentId + '][sort]:last').find('td.moveDown').hide();
}


function tryToCreateMenu(parentId, isSubMenu) {
    var obj = $('div[parentId=' + parentId + ']');
    var level = $(obj).parent('div[parentId]').length;
    if (parseInt(level) == 0 && (parseInt($(obj).length) >= topItemsLimit)) {
        alert(s_MaximumTopItemsLimited);
        return;
    }
    if (isSubMenu && (parseInt($(obj).length) >= maxDepth)) {
        alert(s_SubmenuCannotBeDeeperThan + " " + maxDepth + " " + s_Levels);
        return;
    }                
    /*var dialog = $('<DIV id="transl_dialog" title="Edit title of content"><img class="loader" src="/images/loadingAnimation.gif" /></DIV>');	                           
    $(dialog).dialog({
		    width: 800,
		    height: 560,
		    modal: true,
		    open: function(){
			        var dialog = this;			
			        $.ajax({
		            type: "GET",
		            url: "create_menu_item?parentId="+parentId+"&level="+level,
		            dataType: "html",
		            success: function(html, stat) {				
			            //debugger;
                        $(dialog).html(html);		
		            },
                    error: function(html, stat) { alert(html);}
                })
		    },
		    close: function() {$(this).remove();}
    });*/

     window.open(
        "create_menu_item?parentId=" + parentId + "&isSubMenu="+isSubMenu,
		"",		
        'width=750,height=570,toolbar=yes,status=yes,resizable=yes,scrollbars=yes,top=10');	    
 }

 function createMenuItemAnswer(menu) {

 }

 function loadPage(href) {     
     try {
         window.opener.location.href = href;
     } catch (e) {
         window.open(
            href,
            "mainWindow",
            "height=" + (screen.availHeight - 155) + "px"
            + ", width=" + (screen.availWidth - 12) + "px"
            + ", menubar=yes, location=yes, resizable=yes"
            + ", scrollbars=yes, status=yes, titlebar=yes, toolbar=yes"
            + ", left=0, top=0");
     }
 }

 function tryToCreateMenuAnswer(dataFrm) {
     var paramStr = '';
     var n = dataFrm.length;     
     for (var i = 0; i < n; i++) {
         paramStr = paramStr + dataFrm[i].name + "=" + dataFrm[i].value + "&";        
    }    
    $.ajax({
         type: 'GET',
         url: "/admin/?command=create-menu-item-command&" + paramStr,
         dataType: "xml",         
         success: function (xml) {
             var errorMessege = find_node(xml, "negeso:pageExistsException");
             if (errorMessege != null) {
                 alert($(errorMessege).text());
             } else {
                 window.location.reload();
             }
         },
         error: function (xml) {
             alert("Page can not create");
         }
     });

    }

function find_node(node, name) {
    var res = null;
    if (node.nodeType == 1 && node.nodeName.toLowerCase() == name.toLowerCase()) {
        res = node;
    } else {
        for (var i = 0; i < node.childNodes.length; i++) {
            res = find_node(node.childNodes[i], name);
            if (res) break;
        }
    }
    return res;
}