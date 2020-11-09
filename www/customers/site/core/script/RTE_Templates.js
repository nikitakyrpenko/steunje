// @author: Rostislav Brizgunov

function HTMLTemplate() {
	this.title = "";
	this.body = "";
}

function HTMLTemplates() {
	
	this.templates = new Array();
	this.current = -1;
	
	this.addTemplate = function(title,text) {
		this.current++;
		this.templates[this.current] = new HTMLTemplate();
		this.templates[this.current].title = title;
		this.templates[this.current].body = text;
	}
}

var curTitle = "";
var curHTML = "";
var RTE_Templates = new HTMLTemplates();

// First template: two columns text
// curTitle = 'Two columns text';
// curHTML = '<table border="0" cellspacing="10" width="100%">' +
// 		'<tr>' +
// 			'<td width="45%" style="vertical-align: top; text-align: left;">This is first column text</td>' +
// 			'<td style="vertical-align: top; text-align: left;">This is second column text</td>' +
// 		'</tr>' +
// 	'</table>';
// RTE_Templates.addTemplate(curTitle, curHTML);
//
// // First template: three columns text
// curTitle = 'Three columns text';
// curHTML = '<table border="0" cellspacing="10" width="100%">' +
// 		'<tr>' +
// 			'<td width="30%" style="vertical-align: top; text-align: left;">This is first column text</td>' +
// 			'<td width="30%" style="vertical-align: top; text-align: left;">This is second column text</td>' +
// 			'<td width="30%" style="vertical-align: top; text-align: left;">This is third column text</td>' +
// 		'</tr>' +
// 	'</table>';
// RTE_Templates.addTemplate(curTitle, curHTML);
//
//
// curTitle = 'Social networks';
// curHTML = '<DIV style="TEXT-ALIGN: center; MARGIN: 30px auto 0px; WIDTH: 220px; CLEAR: both">'+
// 			'<SPAN style="DISPLAY: block; MARGIN-BOTTOM: 4px; COLOR: #0060b6; FONT-SIZE: 13px; FONT-WEIGHT: bold">'+
// 				'Deel dit product met anderen:'+
// 			'</SPAN>'+
// 			'<A style="TEXT-DECORATION: none" class=iframe title="Tip op Hyves" '+
//             	'href="http://hyves-share.nl/button/tip/?tipcategoryid=12&amp;rating=5&amp;body=[%core.page.url%]%core.page.title%">'+
//             '<IMG style="BORDER-BOTTOM: medium none; BORDER-LEFT: medium none; BORDER-TOP: medium none; BORDER-RIGHT: medium none" title="Tip op Hyves" alt=Hy'+
//             	' src="/images/hyves.png">'+
//             '</A>'+
//             '<A style="TEXT-DECORATION: none" title="Deel op Windows Live"'+
//             	'href="http://favorites.live.com/quickadd.aspx?url=%core.page.url%&amp;title=%core.page.title%&amp;text=%core.page.title%" target=_blank>'+
//             '<IMG style="BORDER-BOTTOM: medium none; BORDER-LEFT: medium none; BORDER-TOP: medium none; BORDER-RIGHT: medium none" title="Deel op Windows Live" alt=Wl'+
//             	' src="/images/livemessenger.gif">'+
//             '</A>'+
//             '<A style="TEXT-DECORATION: none" title="Plaats op Twitter"'+
//              	'href="http://twitter.com/home/?status=Check dit product: %core.page.url%" target=_blank>'+
//             '<IMG style="BORDER-BOTTOM: medium none; BORDER-LEFT: medium none; BORDER-TOP: medium none; BORDER-RIGHT: medium none" title="Plaats op Twitter" alt=Tw'+
//             	' src="/images/twitter.png">'+
//             '</A>'+
//             '<A style="TEXT-DECORATION: none" title="Deel op Facebook"'+
//             	'href="http://www.facebook.com/sharer.php?u=%core.page.url%&amp;t=%core.page.title%" target=_blank>'+
//             '<IMG style="BORDER-BOTTOM: medium none; BORDER-LEFT: medium none; BORDER-TOP: medium none; BORDER-RIGHT: medium none" title="Deel op Facebook" alt=Fb'+
//             	' src="/images/facebook.png">'+
//             '</A>'+
//             '<A style="TEXT-DECORATION: none" title="Deel op MySpace"'+
//             	'href="http://www.myspace.com/Modules/PostTo/Pages/?u=%core.page.url%&amp;t=%core.page.title%&amp;c=&amp;l=2" target=_blank>'+
//             '<IMG style="BORDER-BOTTOM: medium none; BORDER-LEFT: medium none; BORDER-TOP: medium none; BORDER-RIGHT: medium none" title="Deel op MySpace" alt=My'+
//             	' src="/images/myspace.gif">'+
//             '</A>'+
//             '<A style="TEXT-DECORATION: none" title="Email naar een vriend(in)"'+
//             	'href="mailto:?subject=%core.page.title%&amp;body=%core.page.title% %core.page.url%">'+
//             '<IMG style="BORDER-BOTTOM: medium none; BORDER-LEFT: medium none; BORDER-TOP: medium none; BORDER-RIGHT: medium none" title="Email naar een vriend(in)" alt=Em'+
//             	' src="/images/mail.png">'+
//             '</A></DIV>';
// RTE_Templates.addTemplate(curTitle, curHTML);


//
// curTitle = 'Template for left RTR';
// curHTML = '<h2>Title</h2>'+
//         '<p>Text</p>'+
// 		'<img src="images/pic1.png" width="93" height="97" class="framePic"/>';
// RTE_Templates.addTemplate(curTitle, curHTML);


curTitle = 'Template for information block on index page';
curHTML = '<table border="0" class="swiper-slide" style="width: 100%;">' +
    		'<tbody>' +
    			'<tr>' +
    				'<td><img src="/media/information-pic/information1.jpg" alt="" /></td>' +
    				'<td>' +
    					'<h3>Lorem ipsum dolor sit amet</h3>' +
						'<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer nec odio. Praesent libero. Sed cursus ante dapibus diam. Sed nisi. Nulla quis sem at nibh elementum imperdiet. Duis sagittis ipsum. Praesent mauris. Fusce nec tellus sed augue semper porta.</p>' +
						'<p>Fusce nec tellus sed augue semper porta. Vestibulum lacinia arcu eget nulla.</p>' +
					'</td>' +
				'</tr>' +
			'</tbody>' +
		'</table>';
RTE_Templates.addTemplate(curTitle, curHTML);

// curTitle = 'Picture for left RTR';
// curHTML = '<img src="images/pic1.png" width="93" height="97" class="framePic"/>';
// RTE_Templates.addTemplate(curTitle, curHTML);