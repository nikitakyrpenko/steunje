var langItemOver = false;
var heightLB;

function prepareLanguageBox() {
    $('.languageBox').css('visibility', 'hidden');

    
    //you need this to direct dropdowm up
   /* heightLB = $('.languageBox').attr('clientHeight') + 9;
    $('.languageBox').css('margin-top', -heightLB + 'px');
    
    var widthLB = $('.languageBox').attr('clientWidth');
    $('.languageBox a').css('width', widthLB + 'px');*/
    $('.languageBox').hide();
   $('.languageBox').css('visibility', 'visible');
    $('.languageBox').show();
}

$(document).ready(function () {
    prepareLanguageBox();

/*   $('body').click(function () {
        if (!(langItemOver)) {
            $('.languageBox').hide();
        }
    });*/

    $('body').keydown(function (e) {
        if (e.keyCode == 27)
            $('.languageBox').hide('slow');
    });

    $('.languages > a')
        .click(function () {
            $('.languageBox').toggle('slow');
            return false;
        });

    $('.languageBox > a')
        .mouseover(function () { langItemOver = true; })
        .mouseout(function () { langItemOver = false; })
        .click(function (e) {
            var langCode = $(this).attr('langCode');
            if (langCode != undefined && !($(this).hasClass('selectedLang'))) {
                $('.languageBox').hide('slow');

                if (location.href.indexOf('/admin') != -1)
                    window.location.href = "/admin/index_" + langCode + ".html";
                else
                    window.location.href = "/index_" + langCode + ".html";
            }
            return false;
        });

    $('.change-lang').click(function (e) {
        $('.languageBox').toggleClass('hidden');
    });
});