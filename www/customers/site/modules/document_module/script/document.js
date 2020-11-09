function chooseFolder(curId) {
	document.forms['dcForm'].elements['current_folder_id'].value=curId;
	document.forms['dcForm'].submit();
}

function doFileSearch() {
	if (!validate(document.forms['dcForm']))
		return false;
	document.forms['dcForm'].elements['mode'].value = "search";
	document.forms['dcForm'].submit();
	return false;
}

function goBack() {
	window.location.href = "?param=1";
}
