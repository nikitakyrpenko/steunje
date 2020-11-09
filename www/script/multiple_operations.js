
		function doMultipleOperation(form, actName, confirmationMessage, noItemsMessage) {
			var ids = getSelectedItems(form);		
			if (ids == '') {
				  alert(noItemsMessage);	
				  return;
			}
			var answ = confirm(confirmationMessage);
			if (answ==true) {
				form.act.value = actName;
				form.ids.value = getSelectedItems(form);
				form.submit();
			}
		}

       function getSelectedItems(form) {
            var items = "";
            for (el in form.elements.tags("INPUT")) {
                if (form.elements[el].type == "checkbox" &&
                    form.elements[el].checked)
                    items = items + form.elements[el].value + ';';
            }
            return items;
        }
