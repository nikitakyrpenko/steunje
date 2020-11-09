var validConstName,
	validConstText;
var constArray = [];

$(document).ready(function(){
	$('*[constName]').each(function(){
		new Constants(this, 'common', 0);
	});
	for (var key in fmConsts) {
		validConstName = "VALIDATION."+key;
		validConstText = fmConsts[key];
		new Constants(this, 'validation', 1);
	}
});

function Constants(item, module, constCell){
	var self = this;
		constArray[constCell] = module;

		this.constName = $(item).attr('constName') || validConstName;


		if(!isNaN(Constants.namesMAP[this.constName])){
			Constants.list[Constants.namesMAP[this.constName]].rel.push(item);
			return;
		}

		this.constText = $(item).attr('constText')|| validConstText;
		this.rel = [item];
		this.lang = lang;

			Constants.list[module].push(this);
			Constants.namesMAP[this.constName] = Constants.list[module].length-1;
}

Constants.prototype.editMe = function(){

	var self = this;
	var dialog = $('<DIV id="transl_dialog" title="Edit translation"><img class="loader" src="/images/loadingAnimation.gif" /></DIV>');
	$(dialog).dialog({
		width: 330,
		height: 260,
		modal: true,
		open: function(){
			var dialog = this
			$.get(Constants.editHref + self.constName, function(data, textStatus){
				$('IMG.loader', dialog).remove();
				$('#transl_dialog').html('<FORM>' + $(data).find('[class^=admNavPanelKey]').parent().html() + '</FORM>' );
			})
		},
		buttons: {
			'< Save and close >': function(){
				if (full_validate($('FORM',this)[0])) {
					var url = new Object();
					$('FORM input[name]:hidden,FORM input[name]:text,FORM textarea[name]',
						this).each(function(index){
						        url[$(this).attr('name')] = $(this).attr('value');
				    });

				    self.constText = url[self.lang];
				    $('#constantsList SPAN[constName="' + self.constName + '"]').text(self.constText);
				    $('SPAN[constName="' + self.constName + '"]', Constants.preparedList).text(self.constText);

				    $.post('module_consts',url, function(){$(dialog).dialog('close')});
				}else{
					return false;
				}
			},
			'< Cancel >': function() {$(this).dialog('close');}
		},
		close: function() {
				$(this).remove();
		}
	});
}

Constants.prototype.showMe = function(){
var self = this;
	for(var i = 0; i < this.rel.length; i++){
		if((this.rel[i].nodeName == 'A' || this.rel[i].nodeName == 'SPAN') && $(this.rel[i]).css('display')=='inline'){
			$(this.rel[i]).css('display', 'inline-block');
			$(this.rel[i]).attr('restoreInline','true');
		}
	}

	$(this.rel).effect('pulsate',{},500,
		function(){
			for(var i = 0; i < self.rel.length; i++){
				if($(self.rel[i]).attr('restoreInline')=='true'){
					$(self.rel[i]).css('display', 'inline');
					$(self.rel[i]).removeAttr('restoreInline');
				}
			}
		});
}

Constants.prepareList = function()
{
	if(Constants.preparedList) return Constants.preparedList;

	for (var k=0; k<constArray.length; k++) {
		moduleName = constArray[k];


		if(moduleName == 'common') {
				var list = $('<ul id ="constantsList" class="constantsList"></ul>');
					for(var i=0;i < Constants.list[moduleName].length; i++){

							var item = '<li class="item"><span class="find">[find]</span>&#160;<span class="edit">[edit]</span>&#160;&#160;'
								+ '<span class="title">' + Constants.list[moduleName][i].constText + '</span></li>';
							item = $(item);
						$('SPAN.title' ,item).attr('constName', Constants.list[moduleName][i].constName);

						$('span.find', item).click((function(self){
								return function () {self.showMe()}
							})(Constants.list[moduleName][i]))

						$('span.edit', item).click((function(self){
							return function () {self.editMe()}
						})(Constants.list[moduleName][i]))


						$(list).append(item);
					}

			} else if(moduleName == 'validation') {
				var list_new = $('<ul id ="constantsList" class="constantsList"></ul>');
					for(var i=0;i < Constants.list[moduleName].length; i++){

							var item = '<li class="item">&#160;<span class="edit">[edit]</span>&#160;&#160;'
								+ '<span class="title">' + Constants.list[moduleName][i].constText + '</span></li>';
							item = $(item);
						$('SPAN.title' ,item).attr('constName', Constants.list[moduleName][i].constName);

						$('span.find', item).click((function(self){
								return function () {self.showMe()}
							})(Constants.list[moduleName][i]))

						$('span.edit', item).click((function(self){
							return function () {self.editMe()}
						})(Constants.list[moduleName][i]))


						$(list_new).append(item);
					}
			}



	}


	Constants.preparedList = list;
	Constants.preparedListNew = list_new;
	return list;
	return list_new;
}

Constants.showList = function(){
	var list = Constants.preparedList || Constants.prepareList();
	var list_new = Constants.preparedListNew || Constants.prepareList();
	var dialog = $('<DIV id="dialog" title="Translations list"></DIV>');
	var tabsHead = $('<ul class="localization-tabs"><li><a href="#fistTab">Common constans</a></li><li><a href="#secondTab">Validation constants</a></li></ul>')
	var commonConsts = $('<DIV class="common-consts" id="fistTab"></DIV>');
	var validationConsts = $('<DIV class="validation-consts" id="secondTab"></DIV>');

	$(commonConsts).append($(list).clone(true));
	$(validationConsts).append($(list_new).clone(true));

	$(dialog).append(tabsHead);
	$(dialog).append(validationConsts);

	$(dialog).append(commonConsts);
	$(dialog).tabs();

	$(dialog).dialog({
		width:325,
		height:400,
		close: function(){$(this).remove()}
	})

}

Constants.editHref = '/admin/module_consts?act=updateConstKey&key=';

Constants.list = [];

Constants.list['common'] = [];
Constants.list['validation'] = [];

Constants.namesMAP = {};

Constants.preparedList = null;
