var tree = function() {
    return {
        id: null,
        smallIconSize: { width: 16, height: 16 },
        
        bigImageSize: { width: 32, height: 32 },
        
        iconSrc: '',
        
        parent: null,
        
        ref: $('<li ><span class="directory collapsed">Product module</span></li>')[0],
        
        url:'/admin/?command=pm-browse-category&ajax=json',
        
        type: null,
        
        title: 'Product module',
        
        setTitle:function(title){
			this.title = title;
			$('span',this.ref).text(title);
        },
        
        children: [],

        showTree: function(){

			$('img', this.ref).remove();
			var newCat = $('<ul class=""></ul>');
			$(this.ref).append(newCat);
			
			for(var i=0;i < this.children.length; i++){
				var o = this;
				(function(i){
					if(o.children[i].canBeSelected == 'false'){
						$(o.children[i].ref).one('click',o.children[i].loadContent);
					}else{
						$(o.children[i].ref).click(o.children[i].handlerClick);
					}
				})(i)

				this.children[i].ref.parentObjectRef = this.children[i];
				$(newCat).append(this.children[i].ref)
			}
        },

        handlerClick: function(event)
        {
			event.stopPropagation();

			var li = (event.target.nodeName=='LI')? event.target : event.target.parentNode;

			if(li.parentObjectRef && li.parentObjectRef.canBeSelected == 'true'){
				$('SPAN.selected', category.ref).removeClass('selected');
				$('SPAN', li).addClass('selected');
				category.setSelectedCat(li.parentObjectRef);
			}
			
			if(li.parentObjectRef && li.parentObjectRef.canBeSelected =='false'){		
				if($('span:eq(0)', li).hasClass('collapsed')){$('span:eq(0)', li).removeClass('collapsed').addClass('expanded');}
				else if($('span:eq(0)', li).hasClass('expanded')){$('span:eq(0)', li).removeClass('expanded').addClass('collapsed');}
			}
			$('UL:eq(0)', li).toggle();
        },

        handlerJSONLoaded: function(data, obj)
        {
			for (var i = 0; i < data.length; i++) {
				obj.children.push(new tree());
				obj.children[i].id = data[i].id;
				obj.children[i].type = data[i].type;
				obj.children[i].url = unescape(data[i].url);
				obj.children[i].canBeSelected = data[i].canBeSelected;
				obj.children[i].setTitle(data[i].title);
				obj.children[i].parent = obj;
			}

			this.showTree();
        },
        
        loadContent: function() {
            return function(obj) {
				obj = obj.parentObjectRef || obj;
				url = obj.url;
		
				$(obj.ref).append('<img src="/images/itemTree/spinner.gif" />');
				

                $.getJSON(url.replace(/&amp;/g,'&'), null, function(data){
					obj.handlerJSONLoaded(data, obj);
					$(obj.ref).click(obj.handlerClick);
                });
                
            } (this)
        }
    }

}







