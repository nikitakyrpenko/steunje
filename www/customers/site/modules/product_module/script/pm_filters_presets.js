function showFilterPreset(filter_id) {
	for (var i=0; i<filter_ids.length; i++)
		document.getElementById('div_filter_' + filter_ids[i]).style.display = 'none';
	document.getElementById('div_filter_' + filter_id).style.display = 'block';
	current_filter_id = filter_id;
}

function filterGo(filter_id) {
	var formObj = document.forms["pmFilterFormName_"+filter_id];
	formObj.elements["view"].value = 'filterResults'; 
	formObj.submit();
}

var filter_ids = [];
var current_filter_id = -1;