function print_page(){

	var where_am_i = window.document.getElementById("where_am_i_row").innerHTML;
	var content = window.document.getElementById("content_row").innerHTML;
	var footer = window.document.getElementById("page_footer_row").innerHTML;
	
	var print_win=window.open("", "print_window_"+new Date().getTime(), "height=600px,width=980px,status=no,resizable=yes,scrollbars=yes,toolbar=no,menubar=no,location=no,directories=no,left=" + (screen.availWidth-940)/2 + ",top=" + (screen.availHeight-600)/2);
	print_win.document.open();
	
	var cont = '<html>';
	cont = cont + '<head>';
	cont = cont + '<link rel="stylesheet" type="text/css" href="/site/core/css/page.css"/>';
	cont = cont + '<link rel="stylesheet" type="text/css" href="/site/core/css/default_styles.css"/>';
	cont = cont + '<link rel="stylesheet" type="text/css" href="/site/modules/contact_book/css/contact_book.css"/>';
	cont = cont + '<link rel="stylesheet" type="text/css" href="/site/modules/document_module/css/document.css"/>';
	cont = cont + '<link rel="stylesheet" type="text/css" href="/site/modules/faq_module/css/faq.css"/>';
	cont = cont + '<link rel="stylesheet" type="text/css" href="/site/modules/guest_book/css/guest_book.css"/>';
	cont = cont + '<link rel="stylesheet" type="text/css" href="/site/modules/inquire_module/css/inquire_module.css"/>';
	cont = cont + '<link rel="stylesheet" type="text/css" href="/site/modules/job_module/css/job.css"/>';
	cont = cont + '<link rel="stylesheet" type="text/css" href="/site/modules/lite_event/css/lite_event.css"/>';
	cont = cont + '<link rel="stylesheet" type="text/css" href="/site/modules/login_module/css/login.css"/>';
	cont = cont + '<link rel="stylesheet" type="text/css" href="/site/modules/news/css/news.css"/>';
	cont = cont + '<link rel="stylesheet" type="text/css" href="/site/modules/news/css/newsline.css"/>';
	cont = cont + '<link rel="stylesheet" type="text/css" href="/site/modules/newsletter/css/newsletter.css"/>';
	cont = cont + '<link rel="stylesheet" type="text/css" href="/site/modules/photo_album/css/photo_album.css"/>';
	cont = cont + '<link rel="stylesheet" type="text/css" href="/site/modules/product_module/css/pm_filters.css"/>';
	cont = cont + '<link rel="stylesheet" type="text/css" href="/site/modules/product_module/css/pm_mail_to_friend.css"/>';
	cont = cont + '<link rel="stylesheet" type="text/css" href="/site/modules/product_module/css/pm_module.css"/>';
	cont = cont + '<link rel="stylesheet" type="text/css" href="/site/modules/product_module/css/pm_search.css"/>';
	cont = cont + '<link rel="stylesheet" type="text/css" href="/site/modules/product_module/css/pm_shopping_cart.css"/>';
	cont = cont + '<link rel="stylesheet" type="text/css" href="/site/modules/search_module/css/search.css"/>';
	cont = cont + '<link rel="stylesheet" type="text/css" href="/site/modules/webpoll/css/webpoll.css"/>';
	cont = cont + '<link rel="stylesheet" type="text/css" href="/site/modules/mail_to_a_friend/css/mail_to_a_friend.css"/>';
	cont = cont + '<link rel="stylesheet" type="text/css" href="/site/modules/sitemap/css/sitemap.css"/>';
	cont = cont + '</head>';
	
	cont = cont + '<body>';
	cont = cont + '<table Width="960px">';
	cont = cont + '<tr>' + where_am_i + '</tr>';
	cont = cont + '<tr>' + content + '</tr>';
	cont = cont + '<tr>' + footer + '</tr>';
	cont = cont + '</table>';
	cont = cont + '</body>';
	cont = cont + '</html>';
	
	// Removing <SCRIPT> entries:
	cont = cont.replace(/<script\s*.*?\/>/gi, '');
	cont = cont.replace(/<script(>|\s)(.|\s)*?<\/script\s*>/gi, '');
	
	print_win.document.write(cont);
	print_win.document.close();
	print_win.print();
	
};
