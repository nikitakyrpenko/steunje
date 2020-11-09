function categoryClick(){
    document.forms['gmForm'].elements['show'].value = "category";
    document.forms['gmForm'].submit();
}

function findClick(){
    if (!validate(document.forms['gmForm'].elements['find'])){
        return false;
    }
    document.forms['gmForm'].elements['show'].value = "find";
    document.forms['gmForm'].submit();
}