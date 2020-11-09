function snippets() {
	snippets.isActive = true;
	var curWidth = 365;
    var curPanel = 1;	 
    var container = $('.snippetsContent');
	snippets.stopMoving = false;
    snippets.panels = $(container).find('.snipTop');

	snippets.vars={
		movingDistance:365,
		showlPanels:540,
		curPanel:1,
		panelsLength:snippets.panels.length
	};
	snippets.showArrow = true;	 
	snippets.arrow();
 
	snippets.container = $(container)
		.css('width', (parseInt(curWidth) * parseInt(snippets.vars.panelsLength)) +100)
		.css('left', "0");
    snippets.setCurPanel(1);
	snippets.setNavigation();
}

snippets.arrow = function (){
	var snippetsPrev = $('#snippetsPrev');
	var snippetsNext = $('#snippetsNext');
	
	if( snippets.vars.curPanel == 1 ){
		$(snippetsPrev).hide();
		if (snippets.vars.panelsLength > 1) {
			$(snippetsNext).show();
		}else {
			$(snippetsNext).hide();
		}
	} else if ( snippets.vars.curPanel >= snippets.vars.panelsLength){
		$(snippetsPrev).show();	
		$(snippetsNext).hide();	
	} else{
		$(snippetsPrev).show();	
		$(snippetsNext).show();	
	}		
}

/* moving curPanel: true -  left; false - right; */
snippets.moving = function (direction, n) {
    n = (n < 1) ? 1 : n;
    var movingDistanceTo = parseInt(snippets.vars.movingDistance) * parseInt(n);
    var isLast = (direction && ((parseInt(snippets.vars.curPanel) + parseInt(snippets.vars.showlPanels) - 1) == parseInt(snippets.vars.panelsLength))) ? true : false;
    //if not at the first or last panel
    if ((direction && !(parseInt(snippets.vars.curPanel) < parseInt(snippets.vars.panelsLength)))
		|| (!direction && (snippets.vars.curPanel <= 1))
		|| (isLast)) {
        return false;
    }

	if (!snippets.stopMoving){            
 		 snippets.stopMoving = true;	 
		 var next = (direction) ? parseInt(snippets.vars.curPanel) + parseInt(n) : parseInt(snippets.vars.curPanel) - parseInt(n);
		 var leftValue = snippets.container.css("left");
		 var movement = direction ? parseFloat(leftValue, 10) - parseFloat(movingDistanceTo) : parseFloat(leftValue, 10) + parseFloat(movingDistanceTo);
		 snippets.vars.curPanel = next;
		 snippets.container
			.stop()
			.animate({
				 "left": movement
			}, 300,
			function () {
				 snippets.stopMoving =  false;
			}
		);
		snippets.arrow();		
	 }
   return true;
}

snippets.setNavigation = function(){
	$("#snippetsNext img").click(function () { snippets.moving(true, 1);});
   	$("#snippetsPrev img").click(function () { snippets.moving(false,1);});
}

/* Set a last current panel on loading page */
snippets.setCurPanel = function (n) {
    var movingDistanceTo = parseInt(snippets.vars.movingDistance) * parseInt(n - 1);
    var leftValue = snippets.container.css("left");
    var movement = parseFloat(leftValue, 10) - parseFloat(movingDistanceTo);
    snippets.vars['curPanel'] = n;
    snippets.container.css("left", movement + 'px');
    return true;
}


function editRichSnippet(id) {
	widget.openUrl('/admin/rich_snippet.html?id='+id+'&act=edit',null,null);
}