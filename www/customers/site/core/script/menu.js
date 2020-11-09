var tmp_negeso_menu_item_to_close = null;

function checkHover() {
	var $=jQuery;
	if (tmp_negeso_menu_item_to_close) {
		tmp_negeso_menu_item_to_close
			.removeClass('item_over item_first_over item_last_over item_single_over')
			.find('li')
			.removeClass('item_over item_first_over item_last_over item_single_over');
		// Hiding ULs in reverse order - special for IE. Try to change to tmp_negeso_menu_item_to_close.find('ul').hide() and see what it will be in the IE
		var uls=tmp_negeso_menu_item_to_close.find('ul').get();
		for (var i=0; i<uls.length; i++)
			$(uls[uls.length-1-i]).hide(); //.fadeOut('fast');
		tmp_negeso_menu_item_to_close = null;
	} //if
} //checkHover

function negesoMenu(ul_id) {
	var $=jQuery;
	$('#'+ul_id+' li').hover(
		function() {
			checkHover();
			if ($(this).hasClass('item_first') || $(this).hasClass('item_first_selected')) $(this).addClass('item_first_over');
			else if ($(this).hasClass('item_last') || $(this).hasClass('item_last_selected')) $(this).addClass('item_last_over');
			else if ($(this).hasClass('item_single') || $(this).hasClass('item_single_selected')) $(this).addClass('item_single_over');
			else $(this).addClass('item_over');
			$(this).find('> ul').show();//slideDown('fast');
		}, 
		function() {
			tmp_negeso_menu_item_to_close = $(this);
			setTimeout("checkHover()",300);
		}
	).each(function(i,o){
		var anchor;
		if (anchor=$(this).find('> a').get(0)) {
			o.onclick=function(e){var ev=e||window.event||window.e; ev.cancelBubble=true; window.location.href=$(anchor).attr("href")};
			anchor.onclick=function(){return false;}
		} else {
			o.onclick=function(e){var ev=e||window.event||window.e; ev.cancelBubble=true;}
			$(o).css({'cursor':'default'});
		}
	})
}

function negesoMenuTouch(ul_id) {
    var $ = jQuery;
    $('#' + ul_id + ' li').click(
		function () {
			var isActiveLi = ($(this).find('> ul').length > 0 && !$(this).find('> ul').attr('selected')) ?  false : true ;
			var isSubMenu = ($(this).find('> ul').length > 0 && !$(this).parent('ul').hasClass('negeso_menu')) ?  false : true ;
				
			if (isSubMenu) {
				hideMenu(ul_id);
			}
		    //			debugger;
		    if ($(this).hasClass('item_first') || $(this).hasClass('item_first_selected')) $(this).addClass('item_first_over');
		    else if ($(this).hasClass('item_last') || $(this).hasClass('item_last_selected')) $(this).addClass('item_last_over');
		    else if ($(this).hasClass('item_single') || $(this).hasClass('item_single_selected')) $(this).addClass('item_single_over');
		    else $(this).addClass('item_over');			
		    $(this).find('> ul').show().attr('selected','true');
			if(isActiveLi){
				var anchor;
	   			if (anchor = $(this).find('> a').get(0)) {
	   		    	window.location.href = $(anchor).attr("href");
				}				
			}else{
				return false;
			}
		}
	).each(function (i, o) {
	    if (!$(this).find('> a').get(0)) {
	        o.onclick = function (e) { var ev = e || window.event || window.e; ev.cancelBubble = true; }
	        $(o).css({ 'cursor': 'default' });
	    }
	})
}

function negesoMenu2Click(ul_id) {
    var $ = jQuery;
	var $=jQuery;
	$('#'+ul_id+' li').hover(
		function() {
			checkHover();
			if ($(this).hasClass('item_first') || $(this).hasClass('item_first_selected')) $(this).addClass('item_first_over');
			else if ($(this).hasClass('item_last') || $(this).hasClass('item_last_selected')) $(this).addClass('item_last_over');
			else if ($(this).hasClass('item_single') || $(this).hasClass('item_single_selected')) $(this).addClass('item_single_over');
			else $(this).addClass('item_over');
			$(this).find('> ul').show();//slideDown('fast');
		}, 
		function() {
			tmp_negeso_menu_item_to_close = $(this);
			var isActiveLi = ($(this).find('> ul').length > 0 && !$(this).find('> ul').attr('selected')) ?  false : true ;
			if(!isActiveLi){
				setTimeout("checkHover()",300);
			}
		}
	).click(
        function () {
            var isActiveLi = ($(this).find('> ul').length > 0 && !$(this).find('> ul').attr('selected')) ?  false : true ;
            hideMenu(ul_id);
//			debugger;
            checkHover();
            if ($(this).hasClass('item_first') || $(this).hasClass('item_first_selected')) $(this).addClass('item_first_over');
            else if ($(this).hasClass('item_last') || $(this).hasClass('item_last_selected')) $(this).addClass('item_last_over');
            else if ($(this).hasClass('item_single') || $(this).hasClass('item_single_selected')) $(this).addClass('item_single_over');
            else $(this).addClass('item_over');
            $(this).find('> ul').show().attr('selected','true');
            if(isActiveLi){
                var anchor;
                if (anchor = $(this).find('> a').get(0)) {
                    window.location.href = $(anchor).attr("href");
                }
            }else{
                return false;
            }
        }
    ).each(function (i, o) {
            if (!$(this).find('> a').get(0)) {
                o.onclick = function (e) { var ev = e || window.event || window.e; ev.cancelBubble = true; }
                $(o).css({ 'cursor': 'default' });
            }
        })
	
	
}

function hideMenu(ul_id) {
    var $ = jQuery;
	$('#' + ul_id + ' li').each(function (i, o) {
	    $(this)
			.removeClass('item_over item_first_over item_last_over item_single_over')
			.find('li')
			.removeClass('item_over item_first_over item_last_over item_single_over');
			var uls = $(this).find('ul').get();
	        for (var i = 0; i < uls.length; i++)
    	        $(uls[uls.length - 1 - i]).hide().removeAttr('selected');
	})
} 