function html_encode(html) {
	html = html.replace(new RegExp("<","gi"),"&lt;");
	html = html.replace(new RegExp(">","gi"),"&gt;");
	html = html.replace(new RegExp("\"","gi"),"&quot;");
	return html;	
}

function html_decode(html) {
	html = html.replace(new RegExp("&lt;","gi"),"<");
	html = html.replace(new RegExp("&gt;","gi"),">");
	html = html.replace(new RegExp("&quot;","gi"),"\"");
	return html;
}

function insertSocialNetworks() {
	try {
		var html = '<DIV style="TEXT-ALIGN: left; MARGIN: 30px auto 0px;  CLEAR: both">'+
		'<SPAN style="DISPLAY: block; MARGIN-BOTTOM: 4px; COLOR: #0060b6; FONT-SIZE: 13px; FONT-WEIGHT: bold">'+
			document.forms[0].title.value +
		'</SPAN>';
		
		if(document.forms[0].twitter.checked){
			html+='<A style="TEXT-DECORATION: none" title="Plaats op Twitter"'+ 
             	'href="http://twitter.com/home/?status=Check dit product: %core.page.url%" target=_blank>'+
            '<IMG style="BORDER-BOTTOM: medium none; BORDER-LEFT: medium none; BORDER-TOP: medium none; BORDER-RIGHT: medium none" title="Plaats op Twitter" alt=Tw'+ 
            	' src="/images/twitter.png">'+ 
            '</A>';
		}
		
		if(document.forms[0].hyves.checked){
			html+='<A style="TEXT-DECORATION: none" class=iframe title="Tip op Hyves" '+ 
            	'href="http://hyves-share.nl/button/tip/?tipcategoryid=12&amp;rating=5&amp;body=[%core.page.url%]%core.page.title%">'+
            '<IMG style="BORDER-BOTTOM: medium none; BORDER-LEFT: medium none; BORDER-TOP: medium none; BORDER-RIGHT: medium none" title="Tip op Hyves" alt=Hy'+ 
            	' src="/images/hyves.png">'+ 
            '</A>';
		}
		if(document.forms[0].wl.checked){
			html+= '<A style="TEXT-DECORATION: none" title="Deel op Windows Live"'+ 
            	'href="http://favorites.live.com/quickadd.aspx?url=%core.page.url%&amp;title=%core.page.title%&amp;text=%core.page.title%" target=_blank>'+
            '<IMG style="BORDER-BOTTOM: medium none; BORDER-LEFT: medium none; BORDER-TOP: medium none; BORDER-RIGHT: medium none" title="Deel op Windows Live" alt=Wl'+ 
            	' src="/images/livemessenger.gif">'+ 
            '</A>';
		}
		
		if(document.forms[0].myspace.checked){
			html+= '<A style="TEXT-DECORATION: none" title="Deel op MySpace"'+ 
            	'href="http://www.myspace.com/Modules/PostTo/Pages/?u=%core.page.url%&amp;t=%core.page.title%&amp;c=&amp;l=2" target=_blank>'+
            '<IMG style="BORDER-BOTTOM: medium none; BORDER-LEFT: medium none; BORDER-TOP: medium none; BORDER-RIGHT: medium none" title="Deel op MySpace" alt=My'+ 
            	' src="/images/myspace.gif">'+ 
            '</A>';
		}
		
		if(document.forms[0].facebook.checked){
			html+='<A style="TEXT-DECORATION: none" title="Deel op Facebook"'+ 
            	'href="http://www.facebook.com/sharer.php?u=%core.page.url%&amp;t=%core.page.title%" target=_blank>'+
            '<IMG style="BORDER-BOTTOM: medium none; BORDER-LEFT: medium none; BORDER-TOP: medium none; BORDER-RIGHT: medium none" title="Deel op Facebook" alt=Fb'+ 
            	' src="/images/facebook.png">'+ 
            '</A>';
		}
		
		if(document.forms[0].mail_to.checked){
			html+='<A style="TEXT-DECORATION: none" title="Email naar een vriend(in)"'+ 
            	'href="mailto:?subject=%core.page.title%&amp;body=%core.page.title% %core.page.url%">'+
            '<IMG style="BORDER-BOTTOM: medium none; BORDER-LEFT: medium none; BORDER-TOP: medium none; BORDER-RIGHT: medium none" title="Email naar een vriend(in)" alt=Em'+ 
            	' src="/images/mail.png">'+ 
            '</A>';
		}
		
		
		if(document.forms[0].facebook_like.checked){
			html+='<iframe src="http://www.facebook.com/plugins/like.php?href=%core.page.url%&amp;layout=standard&amp;show_faces=true&amp;width=450&amp;action=like&amp;colorscheme=light&amp;height=30" scrolling="no" frameborder="0"  allowTransparency="true" style="border:none; overflow:hidden; width:450px; height:30px;background: transparent;"></iframe>';
			
		}
		html+='</DIV>';
		tinyMCEPopup.execCommand("mceInsertContent", true, html);
	} catch(e) {alert("ERROR")}

	tinyMCEPopup.close();
}

/*
 * <iframe src="http://www.facebook.com/plugins/like.php?href=http%3A%2F%2Ftest.com%2Ftest&amp;layout=standard&amp;show_faces=true&amp;width=450&amp;action=like&amp;colorscheme=light&amp;height=80" scrolling="no" frameborder="0" style="border:none; overflow:hidden; width:450px; height:80px;" allowTransparency="true"></iframe>
 * */


function init() {
	tinyMCEPopup.resizeToInnerSize();
}
