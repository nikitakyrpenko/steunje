function switchAlternativeAnswerOnOff(inpElm, alterElmName){
	
	/* Defining <form> object */
	var formElm = inpElm.form;
	
	/* Re-defining element for case of collections of radiobuttons or checkboxes */
	inpElm = formElm.elements[inpElm.name];
	
	/* Defining alternative text object */
	var alterElm = formElm.elements[alterElmName];
	
	/* Returning if there is no alternative text object */
	if(typeof(alterElm) == "undefined" || alterElm == null)
		return;
	
	/* Processing of <select> element */
	if(typeof(inpElm.tagName)!="undefined" && inpElm.tagName!="" && inpElm.tagName.toUpperCase() == "SELECT") {
		if (inpElm.value == -1)
			alterElm.disabled = false;
		else
			alterElm.disabled = true;
		return;
	}
	
	/* Processing of <input type="text"> element */
	if (typeof(inpElm.tagName) != "undefined" && inpElm.tagName != "" && 
		(
			inpElm.tagName.toUpperCase() == "INPUT" && 
			(typeof(inpElm.type) == "undefined" || inpElm.type==null || inpElm.type=="" || inpElm.type.toUpperCase()=="TEXT")
			|| 
			inpElm.tagName.toUpperCase() == "TEXTAREA"
		)
	) {
		if (/^\s*$/i.test(inpElm.value))
			alterElm.disabled = false;
		else
			alterElm.disabled = true;
		return;
	}
	
	/* Processing of <input type="radio"> and <input type="checkbox"> element */
	if (typeof(inpElm.tagName)=="undefined" && typeof(inpElm.length)!="undefined") {
		var smth_checked = true;
		for (var i = 0; i <= inpElm.length; i++) {
			if (typeof(inpElm[i]) != "undefined" && inpElm[i].value == "-1" && (inpElm[i].checked || inpElm[i].selected))
				smth_checked = false;
		}
		alterElm.disabled = smth_checked;
	}
	
	if (!alterElm.disabled)
		alterElm.focus();
	
}
