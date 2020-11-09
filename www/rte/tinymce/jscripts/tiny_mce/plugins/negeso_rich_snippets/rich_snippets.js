function init() {
	tinyMCEPopup.resizeToInnerSize();
}

function mixin(dst, src){
    var tobj = {};
    for(var x in src){
        if((typeof tobj[x] == "undefined") || (tobj[x] != src[x])){
            dst[x] = src[x];
        }
    }
    if(document.all && !document.isOpera){
        var p = src.toString;
        if(typeof p == "function" && p != dst.toString && p != tobj.toString &&
        		p != "\nfunction toString() {\n    [native code]\n}\n"){
            dst.toString = src.toString;
        }
    }
}

function extend(Child, Parent) {
	var F = function() {}
	F.prototype = Parent.prototype;
	Child.prototype = new F()
	Child.prototype.constructor = Child;
	Child.superclass = Parent.prototype;
}

function ItemProp(name) {
	this.tag = 'span';
	this.name = name;
	this.specificData = '';
	this.isSpecificDataNeeded = false;
	this.setDataFromElement = function(el) {}
	this.setDataToElement = function(el) {}
}

ItemProp.prototype.getHTML = function(value) {
	return '<' + this.tag + ' itemprop="' + this.name + '"' + this.specificData + '>' + value + '</' + this.tag + '>';
}

function ImgItemProp(name) {
	ImgItemProp.superclass.constructor.call(this, name);
	this.isSpecificDataNeeded = true;
	this.tag = 'img';
	this.setDataFromElement = function(el) {
		document.getElementById('data').value = el.getAttribute('src');
	}
	this.setDataToElement = function(el) {
		el.setAttribute('src', document.getElementById('data').value);
	}
}

TimeItemProp.prototype.getHTML = function(value) {
	return '<' + this.tag + ' itemprop="' + this.name + '" src="' + document.getElementById('data').value + '"></' + this.tag + '>';
}
extend(ImgItemProp, ItemProp);

function TimeItemProp(name) {
	TimeItemProp.superclass.constructor.call(this, name);
	this.isSpecificDataNeeded = true;
	this.tag = 'time';
	this.setDataFromElement = function(el) {
		document.getElementById('data').value = el.getAttribute('datetime');
	}
	this.setDataToElement = function(el) {
		el.setAttribute('datetime', document.getElementById('data').value);
	}
}
TimeItemProp.prototype.getHTML = function(value) {
	return '<' + this.tag + ' itemprop="' + this.name + '" datetime="' + document.getElementById('data').value + '">' + value + '</' + this.tag + '>';
}
extend(TimeItemProp, ItemProp);



function factory(itemType) {
	this.props = null;
	switch(itemType){
		case "http://data-vocabulary.org/Review": {
			this.props = [new ItemProp('itemreviewed'), new ItemProp('reviewer'), new TimeItemProp('dtreviewed'), new ItemProp('summary'), new ItemProp('description'), new ItemProp('rating')];
			return true;
		}
		case "http://data-vocabulary.org/Review-aggregate": {
			this.props = [new ItemProp('itemreviewed'), new ItemProp('rating'), new ItemProp('count'), new ItemProp('votes'), new ItemProp('summary'), new ImgItemProp('photo')];
			return true;
		}
		case "http://data-vocabulary.org/Recipe": {
			this.props = [new ItemProp('name'), new ItemProp('author'), new TimeItemProp('published'), new ItemProp('summary'), new ImgItemProp('photo')];
			return true;
		}
	}
}

factory.prototype.getPropByName = function(name) {
	for ( var i = 0; i < this.props.length; i++) {
		if (this.props[i].name == name) {
			return this.props[i];
		}
	}
	return null;
}

var Factory = null;



var isType = true;
var action = "insert";

function init() {
	tinyMCEPopup.resizeToInnerSize();

	var formObj = document.forms[0];
	var inst = tinyMCEPopup.editor;
	var elm = inst.selection.getNode();
	
	elm = inst.dom.getParent(elm, "div");
	if (elm != null && elm.nodeName == "DIV" && elm.getAttribute('itemtype') != null) {
		action = "update";
	}
	formObj.insert.value = inst.getLang(action, 'Insert', true);

	if (action == "update") {
		if (isType) {
			selectItemByValue(formObj.itemType, elm.getAttribute('itemtype'));
		} 
	}
}

function initProp() {
	tinyMCEPopup.resizeToInnerSize();
	var formObj = document.forms[0];
	var inst = tinyMCEPopup.editor;
	var elm = inst.selection.getNode();
	
	elm = inst.dom.getParent(elm, "div");
	if (elm != null && elm.nodeName == "DIV" && elm.getAttribute('itemtype') != null) {
		Factory = new factory(elm.getAttribute('itemtype'));
	}
	elm = getItemPropElement(inst.selection.getNode(), inst);
	
	fillDropDown(formObj.itemProp);
	var itemprop = inst.dom.getAttrib(elm, 'itemprop');
	if (itemprop != null) {
		action = "update";
		selectItemByValue(formObj.itemProp, itemprop);
		var prop = Factory.getPropByName(itemprop);
		showDataBlock(prop.isSpecificDataNeeded);
		prop.setDataFromElement(elm);
		document.getElementById('propertyText').innerText = elm.innerText;
	}
	formObj.insert.value = inst.getLang(action, 'Insert', true);
}

function onchangeActions(dropDown) {
	var prop = Factory.getPropByName(dropDown.options[dropDown.selectedIndex].value);
	showDataBlock(prop.isSpecificDataNeeded);
} 

function showDataBlock(isShow) {
	if (isShow) {
		document.getElementById('dataBlock').style.display='';
	} else {
		document.getElementById('dataBlock').style.display='none';
	}
}

function fillDropDown(dropDown){
	for ( var i = 0; i < Factory.props.length; i++) {
		dropDown.options[dropDown.options.length] = new Option(Factory.props[i].name, Factory.props[i].name);
	}
}

function getItemPropElement(node, inst) {
	var temp = node;
	do {
		if (inst.dom.getAttrib(node, 'itemprop') != null) {
			return node;
		}
	} while ((node = node.parentNode));
	return temp;
}

function selectItemByValue(elmnt, value){
    for(var i=0; i < elmnt.options.length; i++) {
    	if(elmnt.options[i].value == value) {
    		elmnt.selectedIndex = i;
    	}
    }
}


function insertOrUpdateType() {
	var inst = tinyMCEPopup.editor;
	var formObj = document.forms[0];
	var itemtype = formObj.itemType.options[formObj.itemType.selectedIndex].value;
	if (action == "update") {
		var elm = inst.selection.getNode();
		elm = inst.dom.getParent(elm, "div");
		elm.setAttribute('itemtype', itemtype);
	} else {
		inst.execCommand("mceReplaceContent", false, '<div itemscope="itemscope" itemtype="' + itemtype + '">' + inst.selection.getContent() + '</div>');
		inst.execCommand("mceRepaint");
	}
	tinyMCEPopup.close();
}

function insertOrUpdateProp() {
	var inst = tinyMCEPopup.editor;
	var formObj = document.forms[0];
	var itemProp = formObj.itemProp.options[formObj.itemProp.selectedIndex].value;
	var prop = Factory.getPropByName(itemProp);
	if (action == "update") {
		var elm = getItemPropElement(inst.selection.getNode(), inst);
		elm.setAttribute('itemprop', itemProp);
		prop.setDataToElement(elm);
	} else {
		inst.execCommand("mceReplaceContent", false, prop.getHTML(inst.selection.getContent()));
	}
	tinyMCEPopup.close();
}

if (tinyMCEPopup.editor.windowManager.params.isProp == true) {
	tinyMCEPopup.onInit.add(initProp);
} else {
	tinyMCEPopup.onInit.add(init);
}