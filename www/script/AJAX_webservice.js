// ==================**************** MAIN FUNCTIONS: BEGIN *******************====================

function SOAP_Request(opName, opParameters) {
	if (typeof(opName) == "undefined" || opName == null) opName = "";
	if (typeof(opParameters) == "undefined" || opParameters == null) opParameters = "";
	var prm_len = 0;
	for (i in opParameters)
		prm_len++;
	var cmd = ''+
		'<SOAP-ENV:Envelope'+'\n'+
		'		xmlns=""'+'\n'+
		'		xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/"\n'+
		'		SOAP-ENV:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/"\n'+
		'		xmlns:xsd="http://www.w3.org/2001/XMLSchema"\n'+
		'		xmlns:tns="http://negeso.com/2003/Framework-v.1.1/wsdl/serviceAdmin/serviceAdmin"\n'+
		'		xmlns:ns2="http://negeso.com/2003/Framework-v.1.1/wsdl/serviceAdmin/types/serviceAdmin"\n'+
		'		xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/"\n'+
		'		xmlns:soapenc="http://schemas.xmlsoap.org/soap/encoding/"\n'+
		'		xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/"\n'+
		'		xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">\n'+
		'	<SOAP-ENV:Header/>\n'+
		'	<SOAP-ENV:Body>\n'+
		'		<mswsb:auxOperation xmlns:mswsb="http://negeso.com/2003/Framework-v.1.1/wsdl/serviceAdmin/serviceAdmin">\n'+
		'			<operationKind xsi:type="xsd:string">'+opName+'</operationKind>\n'+
		'			<parameterNames xsi:type="soapenc:Array" soapenc:arrayType="xsd:string['+prm_len+']">\n';
	for (c in opParameters)
		cmd+='				<string xsi:type="xsd:string">'+c+'</string>\n';
	cmd+='			</parameterNames>\n'+
		'			<parameterValues xsi:type="soapenc:Array" soapenc:arrayType="xsd:string['+prm_len+']">\n';
	for (c in opParameters)
		cmd += '				<string xsi:type="xsd:string"><![CDATA['+opParameters[c]+']]></string>\n';
	cmd+='			</parameterValues>'+'\n'+
		'		</mswsb:auxOperation>'+'\n'+
		'	</SOAP-ENV:Body>'+'\n'+
		'</SOAP-ENV:Envelope>';
	return cmd;
}

function find_node(node,name) {
	var res=null;
	if (node.nodeType==1 && node.nodeName.toLowerCase()==name.toLowerCase()) {
		res=node;
	} else {
		for (var i=0; i<node.childNodes.length; i++) {
			res=find_node(node.childNodes[i],name);
			if (res) break;
		}
	}
	return res;
}


// AJAX
function AJAX_respone(http_request, eventOK, eventNotOK) {

	if (http_request.readyState==4) {
		
		// Trying to catch internal Firefox-AJAX error
		try {http_request.status}
		catch(e){
			alert("Browser error was occured. Please, retry.")
			eventNotOK();
			return false;
		};

		// Processing server's answer 
		if (http_request.status==200) {
			err=find_node(http_request.responseXML,"env:fault");
		}
		if (http_request.status!=200||err) {
			if (err) {
				err=find_node(err,"faultstring").firstChild.nodeValue;
				err_msg='<b>ERROR:</b><br/>'+err;
				try {negeso_alert(err_msg);} catch(e) {alert('ERROR:\n'+err)}

			}
			eventNotOK();
			return false;
		}

		eventOK();
		return true;
	}

}

function AJAX_Send(opName, opParameters, eventOK, eventNotOK) {
	if (typeof(opName) == "undefined" || opName == null) opName = "";
	if (typeof(opParameters) == "undefined" || opParameters == null) opParameters = "";
	if (typeof(eventOK) == "undefined" || eventOK == null) eventOK = function() {};
	if (typeof(eventNotOK) == "undefined" || eventNotOK == null) eventNotOK = function() {};

	var http_request = false;
	if (window.XMLHttpRequest)
	{ // Mozilla, Safari, IE7+ ...
		http_request = new XMLHttpRequest();
		try { http_request.overrideMimeType('text/xml'); } catch(e) {}
	} else if (window.ActiveXObject)
	{ // IE6-
		try	{ http_request = new ActiveXObject("Msxml2.XMLHTTP"); /* or Msxml3.XMLHTTP, Msxml4.XMLHTTP etc. */ }
		catch (e) {
			try	{ http_request = new ActiveXObject("Microsoft.XMLHTTP"); /* Very old IE :) */ }
			catch (e) {}
		}
	}
	if (!http_request) {
		alert('AJAX error: cannot create an XMLHTTP instance. Operation could not be completed.');
		return true;
	}
	http_request.onreadystatechange = function() { return AJAX_respone(http_request, eventOK, eventNotOK); };
	http_request.open("POST", "/adminwebservice?WSDL", true);
	http_request.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	var req = SOAP_Request(opName, opParameters);
	setTimeout(function(){http_request.send(req)},500);
}


// AJAX
function AJAX_response_url(http_request) {

	var err = null;
	
	if (http_request.readyState==4) {
		
		// Trying to catch internal Firefox-AJAX error
		try {http_request.status}
		catch(e){
			alert("Browser error was occured. Please, retry.")
			return false;
		};

		// Processing server's answer 
		if (http_request.status == 200) {
			err = find_node(http_request.responseXML,"env:fault");
		}
		
		if (http_request.status != 200 || err || http_request.responseText == '') {
			if (err) {
				err=find_node(err,"faultstring").firstChild.nodeValue;
				err_msg='<b>ERROR:</b><br/>'+err;
				try {negeso_alert(err_msg);} catch(e) {alert('ERROR:\n'+err)}

			}
			
			eventNotOK();
			return false;
		}

		//eventOK(http_request.responseXML, http_request.responseText);
		eventOK(http_request.responseXML);
		
		return true;
	}

}

function AJAX_send_url(opURL) {

	var http_request = false;
	if (window.XMLHttpRequest)
	{ // Mozilla, Safari, IE7+ ...
		http_request = new XMLHttpRequest();
		try { http_request.overrideMimeType('text/xml'); } catch(e) {}
	} else if (window.ActiveXObject)
	{ // IE6-
		try	{ http_request = new ActiveXObject("Msxml2.XMLHTTP"); /* or Msxml3.XMLHTTP, Msxml4.XMLHTTP etc. */ }
		catch (e) {
			try	{ http_request = new ActiveXObject("Microsoft.XMLHTTP"); /* Very old IE :) */ }
			catch (e) {}
		}
	}
	if (!http_request) {
		alert('AJAX error: cannot create an XMLHTTP instance. Operation could not be completed.');
		return true;
	}
	
	http_request.onreadystatechange = function() { return AJAX_response_url(http_request); };
	http_request.open("GET", opURL, true);
	// Special for FF
	setTimeout(function(){http_request.send(null)},500);
}

function loadXMLDoc(dname)
{
	var xmlDoc;
	// code for IE
	if (window.ActiveXObject){
		xmlDoc=new ActiveXObject("Microsoft.XMLDOM");
	}
	// code for Mozilla, Firefox, Opera, etc.
	else if (document.implementation && document.implementation.createDocument)	{
		xmlDoc=document.implementation.createDocument("","",null);
	}
	else{
		alert('Your browser cannot handle this script');
	}
	xmlDoc.async=false;
	xmlDoc.load(dname);
	return(xmlDoc);
}
// ==================**************** MAIN FUNCTIONS: END *******************====================

