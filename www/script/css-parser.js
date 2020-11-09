var currentRule;
function  parseCSS(RTE){
    var styleSheet;    

    for(var i =0; i < RTE.styleSheets.length;i++) {
        if (RTE.styleSheets[i].href && (RTE.styleSheets[i].href.indexOf('content_style') != -1 || RTE.styleSheets[i].href.indexOf('default_styles.css') != -1)) {
            styleSheet = RTE.styleSheets[i]; break;
        }
    }
    
    if(!styleSheet) exit;

    var rules;
    if (navigator.appVersion.indexOf("MSIE") != -1)
        rules = styleSheet.rules;
    else
        rules = styleSheet.cssRules;

    for(var i = rules.length-1; i >=0 ; i--) {
        currentRule = rules[i];
        if(currentRule.selectorText.indexOf(":")==-1)$(RTE).find(currentRule.selectorText).each(function(){
        if(!$(this).attr('def-styles'))$(this).attr('def-styles', $(this).attr("style")+ ' ')
            var curStyle = currentRule.style.cssText+";"+ $(this).attr("style");
            $(this).attr("style",curStyle);});
    };
}

function returnDefStyles(RTE){
    
    $(RTE.body).find('*').each(function(){
        var ce = $(this)[0];
        if($(this).attr('def-styles')){
            $(this).attr('style',$(this).attr('def-styles'));
            $(this).removeAttr('def-styles');
            for(var i = 0; i<ce.attributes.length; i++)
            {if(ce.attributes[i].name.indexOf('jQuery')!=-1)ce.removeAttribute(ce.attributes[i].name);}
        }
    });
}