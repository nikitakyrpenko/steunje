// JavaScript Document
/* Widget functionality */
/* @author: Ekaterina Dzhentemirova */
/* @date: 20.09.2010 */

widget.init = function(){	
	widget['controller'] = $('#widget_controller');
	widget['isActive'] = false;
	widget['stopMoving'] = false;
	widget.resize();
	window.onresize = widget.resize;
	
	var widgetSwitchController = getCookie('widgetSwitchController');
	if(widgetSwitchController == 'on'){
		widget.ajaxFunc();		
	}else{
		$(widget.controller).addClass('widget_switch_off').css('visibility','visible');
	}
}

widget.ajaxFunc = function () {
    $.ajax({
        type: "GET",
        url: "?command=get-modules-command",
        dataType: "html",
        success: function (data) {
            $('.widget_modules').html(data);
            new widget();
            widget.showModuleParams('widget_modules');
            widget.navigation();
            widget.setSwitch("on");
            setTimeout(function () {
                $(widget.controller).css('visibility', 'visible');
            }, 300);
        }
    })
}
function widget() {
	widget.isActive = true;
   widget.resize();
	 
	var curHeight = 75;
	var widgetCurPanel = getCookie('widgetCurPanel');
   var curPanel = (widgetCurPanel == '' || widgetCurPanel == null || widgetCurPanel == 'undefined') ? 1 : widgetCurPanel;	 
   var $container = $('#widget_modules');
   var $panels = $container.children('ul').children('li'); 
    
	widget.panels = $panels.css({ 'float': 'right', 'position': 'relative' });
	widget.vars = {
	 	switchController : 'on',
    	movingDistance : curHeight, 
    	showlPanels : Math.floor((parseInt($('.widget_block').height()) - 50 )/ parseInt(curHeight)) - 1,  
	   curPanel : curPanel,
		panelsLength : widget.panels.length
	 };
	 widget.vars['showArrow'] = (widget.vars.panelsLength > widget.vars.showlPanels) ? true : false;
	 
    setCookie('widgetSwitchController', "on");
    setCookie('widgetCurPanel', curPanel);
	 widget.arrow();
 
    widget.container = $container
		.css('height', (parseInt(curHeight) * parseInt(widget.vars.panelsLength)) + 100)
		.css('top', "85px");
    widget.setCurPanel(curPanel);	 
}

widget.resize = function () {
	if (widget.vars){
		widget.vars['showlPanels'] = 
			Math.floor(
				(parseInt($('.widget_block').height()) - 50 )/ 
				parseInt(widget.vars.movingDistance)
			) - 1; 
		widget.arrow();		
	}
}

widget.arrow = function (){
	var widgetPrev = $('#widgetPrev');
	var widgetNext = $('#widgetNext');

	if( widget.vars.curPanel == 1 )
		$(widgetPrev).removeClass('widget_arrow_top_active');	
	else
		$(widgetPrev).addClass('widget_arrow_top_active');
		
	if ( (parseInt(widget.vars.curPanel) + parseInt(widget.vars.showlPanels)) > widget.vars.panelsLength){
		$(widgetNext).removeClass('widget_arrow_right_active');
	} else {
		$(widgetNext).addClass('widget_arrow_right_active');
		var heighBlock = $(widget.controller).height() - widget.vars.showlPanels * widget.vars.movingDistance - 95;
		$(widgetNext).css('height',heighBlock+'px');
	}
		
}

/* moving curPanel: true -  left; false - right; */
widget.moving = function (direction, n) {
    n = (n < 1) ? 1 : n;    
    if (direction) {
        if ((parseInt(widget.vars.curPanel) + parseInt(widget.vars.showlPanels) + n - 1) > parseInt(widget.vars.panelsLength)) 
            n = parseInt(widget.vars.panelsLength) - parseInt(widget.vars.curPanel) - parseInt(widget.vars.showlPanels) + 1;
    }else {
        if ((parseInt(widget.vars.curPanel) - n) < 1)
            n = parseInt(widget.vars.curPanel)-1;
    }
    
    var movingDistanceTo = parseInt(widget.vars.movingDistance) * parseInt(n);
    var isLast = (direction && ((parseInt(widget.vars.curPanel) + parseInt(widget.vars.showlPanels) - 1) == parseInt(widget.panels.length))) ? true : false;
    //if not at the first or last panel

    if ((direction && !(parseInt(widget.vars.curPanel) < parseInt(widget.panels.length)))
		|| (!direction && (widget.vars.curPanel <= 1))
		|| (isLast)) {
        return false;
    }

	 if (!widget.stopMoving) {
        widget.stopMoving = true;
        var next = (direction) ? parseInt(widget.vars.curPanel) + parseInt(n) : parseInt(widget.vars.curPanel) - parseInt(n);
        var topValue = widget.container.css("top");
        var movement = direction ? parseFloat(topValue) - parseFloat(movingDistanceTo) : parseFloat(topValue) + parseFloat(movingDistanceTo);
        widget.vars.curPanel = next;
        setCookie('widgetCurPanel', next);
        if (!direction) widget.setFirstPanel(next);
		  widget.container
			.stop()
			.animate({
			    "top": movement
			}, 300,
			function (){
			    widget.stopMoving = false;
			    if (direction) widget.setFirstPanel(next);
			}
		);
      if (widget.vars.showArrow) widget.arrow();
    }
    return true;
}

var tNext;
var tPrev;
widget.navigation = function(){
    $("#widgetNext")
	   .click(function () {
   	   clearInterval(tPrev);
      	clearInterval(tNext);
	      widget.moving(true, widget.vars.showlPanels); 
      })
		.hover(
			function () {
			    clearInterval(tPrev);
      		tNext = setInterval("widget.moving(true, 1);", 300);
		   },
        	function () {
          	clearInterval(tNext);
        	    clearInterval(tPrev);        	    
    		}
	 );

    $("#widgetPrev")
		.click(function () {
      	clearInterval(tPrev);
        	clearInterval(tNext);
        	widget.moving(false, widget.vars.showlPanels); 
      })
		.hover(
			function () {
			    clearInterval(tNext);
         	tPrev = setInterval("widget.moving(false, 1);", 300);
        	},
			function () {
            clearInterval(tPrev);
			    clearInterval(tNext);
			}
		);
}

/* Set a last current panel on loading page */
widget.setCurPanel = function (n) {
	 widget.setSwitch(widget.vars.switchController);
    var movingDistanceTo = parseInt(widget.vars.movingDistance) * parseInt(n - 1);
    var topValue = widget.container.css("top");
    var movement = parseFloat(topValue) - parseFloat(movingDistanceTo);
    widget.vars['curPanel'] = n;
    setCookie('widgetCurPanel', n);
    widget.container.css("top", movement + 'px');
    widget.setFirstPanel(n);
    return true;
}

widget.setFirstPanel = function (next) {
    widget.panels.removeClass('first firstSub');
    var selfFirst = widget.panels.parent().find('li[curPanel=' + next + ']');
    var subClass = (selfFirst.hasClass('sub')) ? 'Sub' : '';
    selfFirst.addClass('first' + subClass);

	 widget.panels.removeClass('last lastSub');
	 var nextLast = parseInt(next) - 1 + parseInt(widget.vars.showlPanels);
    var selfLast = widget.panels.parent().find('li[curPanel=' + nextLast + ']');
    var subClass = (selfLast.hasClass('sub')) ? 'Sub' : '';
    selfLast.addClass('last' + subClass);
}

widget.widgetSwitch = function (){	
	 if(widget.isActive == false) {
		 widget.ajaxFunc();
	}else{
	    var switchClass = (widget.vars.switchController == 'on') ? 'off' : 'on';
   	 widget.vars['switchController'] = switchClass;
	    setCookie('widgetSwitchController', switchClass);
		 widget.setSwitch(switchClass);
	}
}

widget.setSwitch = function (switchClass){	
	$(widget.controller)
	 	.removeClass('widget_switch_on widget_switch_off')
		.addClass('widget_switch_' + switchClass);
}


widget.widget_item_to_close = null;
widget.checkHover = function () {
    var $ = jQuery;
    if (widget.widget_item_to_close) {
        widget.widget_item_to_close
			.removeClass('item_over item_last_over')
			.find('li')
			.removeClass('item_over item_last_over');
        var uls = widget.widget_item_to_close.find('ul').get();
        for (var i = 0; i < uls.length; i++)
            $(uls[uls.length - 1 - i]).hide();
        widget.widget_item_to_close = null;
    }
}

widget.showModuleParams = function(ul_id) {
    var $ = jQuery;
    var widgetWidth= $('.widget_block');
    $('#' + ul_id).find('li').hover(
		function () {
		    $(widgetWidth).css('width', '300px');
		    widget.checkHover();
		    if ($(this).hasClass('item_last') || $(this).hasClass('item_last_selected')) 
				$(this).addClass('item_last_over');
		    else 
				$(this).addClass('item_over');
		    $(this).find('ul').show();
		},
		function () {
		    widget.widget_item_to_close = $(this);
		    widget.checkHover();
			//$(widgetWidth).css('width', '85px'); 
		}
	);
    $('#'+ul_id+'> ul').hover(
		function () { $(widgetWidth).css('width', '300px'); 
		},
		function () {
		    $(widgetWidth).css('width', '85px'); 
		}
	)
}


widget.openUrl = function(url,width,height) {
	if ( width == null ) width = 1100;
	if ( height == null ) height = parseInt(screen.height)-200;	
	//widgetWindow=window.open(url,'modules','width='+width+',height='+height+',toolbar=no,status=no,resizable=yes,scrollbars=yes,top=10');
	widgetWindow=window.open(url,'modules','width='+width+',height='+height+',toolbar=yes,status=yes,resizable=yes,scrollbars=yes,top=10');
	if (window.focus) {widgetWindow.focus()}
	return false;
}


/* Other */
// Determine browser and version.
var browser = new Browser();
function Browser() {
	var ua = navigator.userAgent;
	this.isIE = ua.indexOf('MSIE') != -1;
	this.isNS = ua.indexOf('Netscape') != -1 || ua.indexOf('Gecko') != -1;
	this.isOpera = ua.indexOf('Opera') != -1;

	// Fake IE on Opera. If Opera fakes IE, Netscape cancel those
	if (this.isOpera) {
		this.isIE = true;
		this.isNS = false;
	}

}