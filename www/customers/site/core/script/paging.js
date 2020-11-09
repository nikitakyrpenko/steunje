/** 
 *         Paging functionality
 *
 *          (c) 2007 NEGESO
 *    Created by Rostislav Brizgunov
 *
 */
 
function handle_paging(e, obj, max_val) {
	var cur_val = parseInt(obj.value);
	if (!isNaN(cur_val)) {
		if (e.keyCode == "39") {
			cur_val++;
			obj.value = "" + cur_val;
			
		}
		if (e.keyCode == "37") {
			cur_val--;
			obj.value = "" + cur_val;
		}
		if (cur_val > max_val) 
			obj.value = "" + max_val;
		else if (cur_val < 1)
			obj.value = "1";
	}
}