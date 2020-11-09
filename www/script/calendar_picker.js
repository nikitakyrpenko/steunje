function loadCalendar(path,idNumber,dateType,dateTime)
{
	var arguments = new Object ();
	arguments.calParam=[idNumber,dateType,window,dateTime]; //dateTime specify as FALSE (without viewing time) or TRUE (viewing time)
	var cnTop="200";// vertical coord
	var cnLeft="500";// horizontal coord
	//winCal=window.showModalDialog("dialog_calendar_picker.html",arguments,"dialogHeight: 275px; dialogWidth: 220px; dialogTop:" +cnTop+ "px; DialogLeft:" +cnLeft+"px; edge: raised; center: no; help: no; resizable: no; scroll: no; status: no;");
	winCal=window.showModalDialog(path,arguments,"dialogHeight: 301px; dialogWidth: 220px; dialogTop:" +cnTop+ "px; DialogLeft:" +cnLeft+"px; edge: raised; center: no; help: no; resizable: no; scroll: no; status: no;");
}

// New crossbrowser calendar picker
var calendar_arguments = new Object();

function loadNewCalendar(path, idNumber, dateType, dateTime)
{
	calendar_arguments.calParam = [idNumber, dateType, window, dateTime]; //dateTime specify as FALSE (without viewing time) or TRUE (viewing time)
	var cnTop="200";// vertical coord
	var cnLeft="500";// horizontal coord
	var popup_window_params = "height=250, width=220, top="+cnTop+", left="+cnLeft+", menubar=no, resizable=no, scrollbars=no, status=no, titlebar=yes, toolbar=no";
	window.open(path, "", popup_window_params);
}