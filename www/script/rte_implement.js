		var current="";
        var div = null;
        var fontFamily = null;  
        var fontSize = null;
        var fontColor = null;
        var wdth = 505;
        var wn = null;
        var win =null;
	 var textDirection = null;
	 var defaultStyle = null;
	 
	  window.onfocus=fc;
	  if (window.attachEvent) {
		  attachEvent("onbeforeunload",lld);
		  attachEvent("onunload",ulld);
	  }
        
        function lld()
        {
            if (win!=null) {
                event.returnValue="The Editor is opened now! It is recommended to press Cancel and close the Editor first!";
            }
        }
        
        function ulld()
        {
            if (win!=null) {
                win.fl=true;
                win.close();
            }
        }
        
        function fc(){
            if (win!=null) {
                event.cancelBubble= true;
                win.focus();	
                return false;		
            }
        }
	 
       function getDiv()
        {
            return div;
        }  
      
	 
	   function show(what)
        {
            if (current!="") document.all[current].style.visibility="hidden";
            current=what;
            if (current!="") document.all[current].style.visibility="visible";
        }
		/*
		function edit_text(id, default_style, div_width , text_dir)
        {
		    if (win == null) {
                div = document.getElementById(id);
                var str = div.innerHTML;
                wdth = div_width + 35; 
                textDirection = text_dir;
                defaultStyle = default_style;
				win = open("/rtecom/text_editor.html", null, "height=562,width="+(3+wdth)+",status=no,toolbar=no,menubar=no,location=no");
            }
        }
        */
        
        function edit_text(inp_id, default_style, div_width , text_dir, save_mode, is_newsletter) {
        	var art_id = inp_id.substr(12, inp_id.length-1);
        	if(save_mode == null || save_mode == 'undefined') save_mode = 3;
        	if(is_newsletter == null || is_newsletter == 'undefined') is_newsletter = false;
        	
		  	RTE_Init(inp_id, inp_id, art_id, save_mode, 0, default_style, getInterfaceLanguage(), null, is_newsletter);
        }
