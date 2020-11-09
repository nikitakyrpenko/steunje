function disableWebpoll(currentPollId) {
	/* currentPollId is webpoll question ID from XML */
	var voted = true;	
	if(navigator.cookieEnabled) {
		if(getCookie(WEBPOLL_COOKIE_IS_VOTED)&&(getCookie(WEBPOLL_COOKIE_ID) == currentPollId))
			{ voted = true; }
		else { voted = false; }
	}

	/* Here do whatever you wish depending on allow or not allow to vote */
	var btn = new Object();
	btn = document.getElementById('voteSubmit');
	
	if (voted == true) {
// please change here how to disable voting
		btn.value="Sorry, I've voted already.";
		btn.disabled=true;
	}
	else {
// please change here how to enable voting
		btn.value="Vote, please!";
	    btn.disabled=false;
	}
	return voted;
}


// variables for getCookie() and setCookie() functions
var WEBPOLL_COOKIE_IS_VOTED = "isVoted";
var WEBPOLL_COOKIE_ID = "webpollId";

function getCookie(name) {
 var result = false;
 var myCookie = " " + document.cookie + ";";
 var searchName = " " + name + "=";
 var startOfCookie = myCookie.indexOf(searchName);
 var endOfCookie;
 if(startOfCookie != -1) {
 	startOfCookie += searchName.length;
 	endOfCookie = myCookie.indexOf(";", startOfCookie);
 	result = unescape(myCookie.substring(startOfCookie,endOfCookie));
 }
 return result;
}

function setCookie(webpollId) {
 var expires = new Date();
 expires.setTime(expires.getTime() + WEB_POLL_PERIOD);
 var expString = ((expires == null)? "" : ("; expires=" + expires.toGMTString()) );
 document.cookie = " path=/; domain=http://" + document.location.host + ";";
 document.cookie = WEBPOLL_COOKIE_IS_VOTED + "=" + escape('true') + expString;
 document.cookie = WEBPOLL_COOKIE_ID + "=" + webpollId + expString;
}
/* WEB POLL Cookie - Finish */