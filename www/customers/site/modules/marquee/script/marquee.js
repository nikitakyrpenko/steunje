// JavaScript Document
/* Marquee maker */
/* @author: Rostislav Brizgunov */
/* @date: 22.01.2008 */
/* @version: 1.0 */

/*
var log = new Logger();
window.onload=function(){log.show()};
*/

var marquees = {}

var Marquee = function(initElm) {

	this.uid = "marquee_" + new Date().getTime();
	this.playing = false;
	
	this.init = function(initElm) {
		
		this.container=initElm;
		this.container_width=this.container.clientWidth;
		this.init_content=this.container.innerHTML;
		this.init_content_escaped='<div style="float: left; white-space: nowrap;">'+this.init_content+'</div>';
		this.container.innerHTML=this.init_content_escaped;
		this.init_width=this.container.firstChild.scrollWidth;
		this.refilled_content=this.init_content_escaped;
		this.refilled_width=this.init_width;
		while (this.refilled_width<this.container_width) {
			this.refilled_width+=this.init_width;
			this.refilled_content+=this.init_content_escaped;
		}
		this.container.innerHTML=''+
		'<div style="width: '+(2*this.refilled_width+10)+'px; position: relative;">'+
		'	<img width="16px" height="16px" src="/site/modules/marquee/images/marquee_pause.gif" style="position: absolute; left: 6px; top: 6px; cursor: pointer;" onclick="marquees[\''+this.uid+'\'].play_toggle()"/>'+
		'	<div style="float: left; margin-left: 0;">'+this.refilled_content+'</div>'+
		'	<div style="float: left;">'+this.refilled_content+'</div>'+
		'</div>';
		
		// Defining 2 big subcontainers
		this.play_button=null;
		this.subcontainer_1=null;
		this.subcontainer_2=null;
		for (var i=0; i<this.container.childNodes.length; i++) {
			
			if (this.container.childNodes[i].nodeType==1) {
				
				for (var j=0; j<this.container.childNodes[i].childNodes.length; j++) {
				
					if (this.container.childNodes[i].childNodes[j].nodeType==1) 
					{
						if (/img/i.test(this.container.childNodes[i].childNodes[j].tagName)) 
						{
							this.play_button = this.container.childNodes[i].childNodes[j];
						}
						else if (/div/i.test(this.container.childNodes[i].childNodes[j].tagName)) 
						{
							if (this.subcontainer_1==null)
								this.subcontainer_1=this.container.childNodes[i].childNodes[j];
							else if (this.subcontainer_2==null)
								this.subcontainer_2=this.container.childNodes[i].childNodes[j];
							else
								break;
						}
					}
					
				}
				
				break;
			}
			
		}
		
		this.playing = true;
		this.go();
	}
	
	this.play_toggle = function() {
		if (this.playing) {
			this.stop();
			if (this.play_button)
				this.play_button.src="/site/modules/marquee/images/marquee_play.gif";
		} else {
			this.playing = true;
			this.go();
			if (this.play_button)
				this.play_button.src="/site/modules/marquee/images/marquee_pause.gif";
		}
	}
	
	this.stop = function() {
		this.playing = false;
	}
	
	this.go = function() {
		var cur_left = parseInt(this.subcontainer_1.style.marginLeft);
		var new_left = cur_left-2;
		if (new_left < -this.refilled_width)
			this.subcontainer_1.style.marginLeft = 0;
		else
			this.subcontainer_1.style.marginLeft = new_left;
		if (this.playing){
			setTimeout("marquees['"+this.uid+"'].go()",10);
		}
	}
	
	marquees[this.uid] = this;
	this.init(initElm);
}