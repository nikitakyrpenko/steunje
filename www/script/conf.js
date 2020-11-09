
function displayPopup(url){
    newPopup = window.open(
        url,
        "",
        "height=300, width=400, menubar=no, resizable=yes, " +
        "scrollbars=yes, status=no, titlebar=yes, toolbar=no, " +
        "left=" + (screen.availWidth/5*2) + ", " +
        "top=" + (screen.availHeight/5*1)
        );
}
