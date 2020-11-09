$(document).ready(function () {
    $('#folderContentTable input[subType=file]').live('click', floatMenuPos);

    $('#folderContentTable TD.checkBoxC IMG').live('click', function () {
        $(this).prev('INPUT').click();
        if ($(this).attr('src') == '/images/checkbox_checked.gif') {
            $(this).attr('src', '/images/checkbox_unchecked.gif');
        } else {
            $(this).attr('src', '/images/checkbox_checked.gif');
        }
    });

    $('.sortAZ').click(function () { setSortDirection(); return false; });
});

function setSortDirection() {    
    var sortDirection = 'desc';
    var link = location.href;
    if (currSortDirection != null && currSortDirection != undefined) {
        if (currSortDirection == 'desc')
            sortDirection = 'asc';
    }
    if (location.href.indexOf('&sort=') != -1)
        link = location.href.substring(0, location.href.indexOf('&sort='));
    window.location = link + '&sort=' + sortDirection;
}

function floatMenuPos(evenObj)
{
    var floatMenu = $('#floatMenu');

	if($('#folderContentTable input[subType=file]:checked').size() > 0){
	    $(floatMenu).show();
		$(floatMenu).css('top', evenObj.target.offsetTop + 'px');
		$(floatMenu).css('left', evenObj.target.offsetLeft - $(floatMenu).width() - 30 + 'px');
	}
	else{
		$(floatMenu).hide();
	}
}

function deleteSelectedFiles(files, curentIndex){

	var curentIndex = curentIndex||0;
	var files = files||[];

	if(files.length==0){
		$('#folderContentTable INPUT[subType=file]:checked').each(function(){
			files.push($(this).attr('fileName'));
		});
	}

	if(files.length>0){
		document.operateForm.command.value = "remove-resource-command";
		document.operateForm.victim.value = files[curentIndex];
		var form = $('form[name=operateForm]').serialize();
		$('#floatMenu').css('width', 'auto').append('<div class="fileUploadQueueItem"><span class="fileName">' + files[curentIndex] + '</span>\
		<span class="percentage">Deleting</span>\
		</div>');
		$('#listOfDeletingFiles').show();
		
		$.post(location.href,form, function(data, textStatus){
			if(data.indexOf('has external links and cannot be deleted')!=-1)
			{
				$('#floatMenu .fileUploadQueueItem:eq(' + curentIndex + ') span.percentage').text(' has external links and cannot be deleted');
				$('#floatMenu .fileUploadQueueItem:eq(' + curentIndex + ')').attr('error','true')
			}
			else
			{
				$('#floatMenu .fileUploadQueueItem:eq(' + curentIndex + ') span.percentage').text('Deleted');
			}
			curentIndex++;
			if(files[curentIndex]){
				deleteSelectedFiles(files, curentIndex);
			}
			else{
				if($('#floatMenu .fileUploadQueueItem[error=true]').size()>0)
				{
					$('#floatMenu .fileUploadQueueItem:not([error=true])').fadeOut('slow');
					
					document.forms["operateFormId"].command.value = 'list-directory-command';
					var formToSubmit = $('form[name=operateForm]').serialize();
						
					var tempHTML = $.post(location.href + ' #folderContentTable', formToSubmit, function(){
						$('#folderContentTable').html($(tempHTML.responseText).find('#folderContentTable').html());
					});
				}
				else
				{
					$('#floatMenu').fadeOut('slow', function(){
					document.forms["operateFormId"].action=location.href;
					document.forms["operateFormId"].command.value = 'list-directory-command';
					document.forms["operateFormId"].submit();
					});
				}

			}
		});
	}
}

var selectedCat = 0;
var command;
function groupProductOperation(com)
{
	command = com;
	if(com=='delete' )
	{
		if (confirm("Are you sure you want to delete products?")) {
			excecuteCommand();
			return true;
		}
		return false;
	}

	var o = window.open('/dialogs/itemTree.html','product_module', 'left='+ (screen.availWidth/2 - 225)  +', top='+ (screen.availHeight/2 - 235) + ',width=570, height=550, resizable=yes,scrollbars=yes,toolbar=no,location=no,directories=no,status=no,menubar=no,copyhistory=no');
}

function excecuteCommand()
{
	var ids = [];
	var params = {};
	$('#folderContentTable input[subType=file]:checked').each(function(){
		ids.push($(this).attr('fileId'));	
	});
	
	$('#floatMenu').append('<br /><img src="/images/itemTree/spinner.gif" />');
	
	params.command = 'pm-manage-product';
	params.actionField = command;
	params.productIdsField = ids.join(',');
	
	params.destCategoryIdField = selectedCat;
	if (command=='move'){ 
		if(confirm("Are you sure you want to move products?")) {
			$.post('/admin/' + location.search, params, function(){
				location.reload();
			})
		}else{
			location.reload();
		}
	}else{
		$.post('/admin/' + location.search, params, function(){
			location.reload();
		})
	}
}