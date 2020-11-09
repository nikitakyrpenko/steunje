/*This script uses /site/core/script/validation.js */

function submitSubscribeForm(){
	var newsletterForm = document.forms['nlSimpleSubscribeForm'];
	if (typeof(newsletterForm) == "undefined" || newsletterForm == null)
		return false;
	if ( !validate(newsletterForm) )
		return false;
	newsletterForm.action.value = "subscribe";
	newsletterForm.elements["siteLink"].value = "http://" + window.location.host;
	newsletterForm.submit();
	return true;
}

function submitUnsubscribeForm(){
	var newsletterForm = document.forms['nlSimpleSubscribeForm'];
	if (typeof(newsletterForm) == "undefined" || newsletterForm == null)
		return false;
	if ( !validate(newsletterForm) )
		return false;
	newsletterForm.action.value = "unsubscribe";
	newsletterForm.submit();
	return true;
}