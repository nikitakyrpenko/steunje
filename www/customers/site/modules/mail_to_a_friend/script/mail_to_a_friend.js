function submitmtfForm(){	

	var mtfForm = document.forms['mtfForm'];
	if (typeof(mtfForm) == "undefined" || mtfForm == null)
		return false;
		
	if ( !validate(mtfForm) )
		return false;
		
	mtfForm.submit();
	return true;
}