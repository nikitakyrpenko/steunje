$(document).ready(function () {
    const cookieAuth = document.cookie.split('; ')
    window.onscroll = () => {
        if(pageYOffset < 50) {
            $('.header').css('top', `${50 - pageYOffset}px`)
        }
        if(pageYOffset > 50) {
            $('.header').css('top', '0')
        }
    }
    $('.my-acc').click(function () {
        $('.account-content-inner').toggleClass('visible-icons')
        $('.acc-arrow').toggleClass('opened-arrow')
    })




    $('.main_img').slick({
        autoplaySpeed: 3000,
        slidesToShow: 1,
        arrows: true,
        dots: false,
        autoplay: true,
        infinite: true,
        adaptiveHeight: true,
        pauseOnHover: true,
        prevArrow: '.arrow__left',
        nextArrow: '.arrow__right',

    });

    $('.slaider_main_block').slick({
        autoplaySpeed: 5000,
        slidesToShow: 3,
        arrows: true,
        dots: false,
        autoplay: true,
        infinite: true,
        pauseOnHover: true,
        prevArrow: '.arrow__left',
        nextArrow: '.arrow__right',
        responsive: [
            {
                breakpoint: 1000,
                settings: {
                    slidesToShow: 2.5,
                    slidesToScroll: 1,
                }
            },
            {
                breakpoint: 850,
                settings: {
                    slidesToShow: 2,
                    slidesToScroll: 1,
                }
            },
            {
                breakpoint:625,
                settings: {
                    slidesToShow: 1.5,
                    slidesToScroll: 1,
                }
            },
            {
                breakpoint: 450,
                settings: {
                    slidesToShow: 1,
                    slidesToScroll: 1,
                }
            }
        ]
    });


    $('.all_contain').click( function (e) {
        $(".all_contain").removeClass('left_active')
        if($(e.target).hasClass('all_contain')){
            $(e.target).addClass('left_active')
        } else {
            $(e.target).parents('.all_contain').addClass('left_active')
        }
    })




    const info = $('.all_contain');
    info.click( function () {
    const id = $(this).data('target');
    $('.all_right').addClass('hidden');
    $(`#${id}`).removeClass('hidden');
    });


    const  news = $('.item-info');
    $('.item-image:first').removeClass('hidden');
    news.first().addClass('arrow_news');
    news.click(function () {
        const id = $(this).data('target');
        news.removeClass('arrow_news');
        $(this).addClass('arrow_news');
        $('.item-image').addClass('hidden');
        $(`#${id}`).removeClass('hidden');
    });






    $('.ham').click(function () {
        $('body').toggleClass('no_scroll');
        $('.menu').toggleClass('show');
        $('.brg_btn').toggleClass('ham_show');
        $('.left_header').toggleClass('lh_hight');

    });



    /*$('.burger_block').click(function () {
        $('.hide_menu').toggleClass('hide_menu')
    });*/




    /*$('.header__slider-wrapper').slick({
        autoplaySpeed: 7000,
        dots: true,
        // arrows: true,
        autoplay: true,
        prevArrow: '.sliderHeader__back-arrow',
        nextArrow: '.sliderHeader__next-arrow',
    });

    $('.top-banner').slick({
        arrows: true,
        prevArrow: '<div class="prev-btn rotateBanerBtn"><img src="/media/left-arrow.svg"></div>',
        nextArrow: '<div class="next-btn rotateBanerBtn"><img src="/media/right-arrow.svg"></div>',
        dots: false,
        autoplay:true,
        pauseOnHover: true,
        autoplaySpeed: 7000,
    });

    $('.steps__slider').slick({
        slidesToShow: 5,
        arrows: false,
        responsive: [
            {
                breakpoint: 1280,
                settings: {
                    slidesToShow: 4,
                    slidesToScroll: 1,
                    autoplay: true,
                    autoplaySpeed: 1000,
                }
            },
            {
                breakpoint: 1024,
                settings: {
                    slidesToShow: 3,
                    slidesToScroll: 1,
                    autoplay: true,
                    autoplaySpeed: 1000,
                }
            },
        ]
    });

    $('.partner__slider-wrapper').slick({
        autoplaySpeed: 4000,
        autoplay: true,
    });

    $('#get_started').click(function() {

        $('html,body').animate({
            scrollTop: $('.header__content ').height()
        }, 1500);

    });

    $('#get_started_kapper').click(function() {

        $('html,body').animate({
            scrollTop: $('.kapper-home__header ').height()
        }, 1500);

    });

*/


    const alertMessage = {
        userExist: {
            en: 'User already exist',
            nl: 'Gebruiker bestaat al'
        },
        password: {
            en: 'Password must be at least 8 characters',
            nl: 'Wachtwoord moet minimaal 8 tekens bevatten'
        },
        retPassword: {
            en: 'Passwords do not match',
            nl: 'Wachtwoorden komen niet overeen'
        },
        tel: {
            en: 'Must containe "+" (optional) and at least 10-14 figures',
            nl: 'Moet "+" (optioneel) en minimaal 10-14 cijfers bevatten'
        },
        partnerFormSuccess: {
            en: "Your application has been successfully sent",
            nl: "Uw aanvraag is succesvol verzonden"
        },
        submitError: {
            en: "Something went wrong. Please try again later",
            nl: "Er is iets fout gegaan. Probeer het later opnieuw"
        }

    }

    if(window.innerWidth < 941 && window.innerWidth > 775 && $('.ingr-text')) {
        let count = 1
        $('.ingr-block_text p').each((index, el) => {
            if(el.textContent.length > 70) {
                let id = 'unicText' + count
                let text = el.textContent
                let texts = []
                let elem = {id, text }
                texts.push(elem)
                el.innerHTML = `${text.substr(0,50)}... <span class="read-more" data-target="${id}"  style="font-weight: bold; font-size: 18px">SHOW&nbsp;MORE</span>`
                count ++
                $(document).on('click', '.read-more', function () {
                    $(this).parent().parent().parent().css('overflow-y', 'scroll')
                    showDescr(this, texts)
                })
                $(document).on('click', '.show-less', function () {
                    let id = $(this).data('target')
                    let str = $(this).parent().text()
                    $(this).parent().parent().parent().css('overflow-y', 'auto')
                    $(this).parent().html(`${str.substr(0,  50 )}... <span class="read-more" data-target="${id}"  style="font-weight: bold; font-size: 18px">SHOW&nbsp;MORE</span>`)
                })
            }
        })
    }

    function showDescr(el, texts) {
        let id = $(el).data('target')
        texts.map(item => {
            if(item.id === id) {
                $(el).parent().html(`${item.text} <span data-target="${item.id}" class="show-less">SHOW&nbsp;LESS</span>`)
            }
        })

    }

    $('#register-form').submit(function (e) {
        $('.input-wrapper').css('border', '2px solid #AD9677')
        $('.error-input').addClass('hidden').text('')
        e.preventDefault()
        let pass = this.p.value;
        let login = this.login.value;
        let pret = this.pret.value;
        if(pass.length < 8) {
            $('.pass').removeClass('hidden').text(
                alertMessage.password[lang]).parent('div').css('border', '2px solid red')
            return false
        }
        if(pret !== pass) {
            $('.pret').removeClass('hidden').text(alertMessage.retPassword[lang]).parent('div').css('border', '2px solid red')
            return false
        }
        axios.post(`/webshop/api/auth&login=${login}&password=${pass} `)
            .then(res => {
                if(res.data.map.success !== false ) {
                    window.location.href=`/additional-info_${lang}.html`
                } else {
                    $('.reg-alrt').text(alertMessage.userExist[lang]).removeClass('hidden').parent('div').css('border', '2px solid red')
                }
            })

    });

    $('#edit-info input[type="radio"]').change(function () {
        if(this.value === "b") {
            $('.company-name').removeClass('hidden')
            $('#company_name ').attr('required', true)
            if($('#edit-info input[value="yes"]').attr('checked')) {
                $('#company_name-delivery ').attr('required', true)
            }
        }
        if(this.value === 'yes') {
            $('.ship-info').removeClass('hidden')
            $('#edit-info').addClass('flex-around').removeClass('flex-center')
            if($('#edit-info input[value="b"]').attr('checked')) {
                $('#company_name-delivery ').attr('required', true)
            }

        }
        if(this.value === 'no') {
            $('.ship-info').addClass('hidden')
            $('#company_name-delivery ').attr('required', false)
        }
        if(this.value === 'c') {
            $('.company-name').addClass('hidden')
            $('.company-name input').attr('required', false)
        }
        else {
            return
        }
    });

    function changeInfo(data) {
        let mode = 'register';
        cookieAuth.map(item => {
            if (item.includes('user_object')) {
                mode = 'update'
            }
        })
        // $('.submit').addClass('hidden')
        // let spiner = document.createElement('img')
        // spiner.src='/images/spiner.gif'
        // spiner.className='spiner'
        // $('.content').append(spiner)
        axios.post(`/webshop/api/auth/address?type=${data.type}&company=${data.company}&name=${data.name}&last_name=${data.lastName}&postcode=${data.postCode}&phone=${data.phone}&country=${data.country}&town=${data.town}&house=${data.house}&street=${data.street}&delivery_country=${data.countryDel}&delivery_town=${data.townDel}&delivery_name=${data.nameDel}&delivery_last_name=${data.lastNameDel}&delivery_postcode=${data.postCodeDel}&delivery_house=${data.houseDel}&delivery_street=${data.streetDel}&delivery_company=${data.companyDel}`)
            .then(res => {
                // spiner.remove()
                $('.submit').removeClass('hidden')
                $('.reg-alrt').addClass('hidden')
                let wrapper = document.createElement('div')
                wrapper.className="bg-wrapper"
                let div = document.createElement('div')
                if(res.data.map.success === true) {
                    if(mode === 'update') {
                        div.innerHTML = `
							<h2>Success</h2>
					<p>You information has been updated</p>
					<button class="submit" onClick="$(this).parent().remove(); window.location.href = '/customer_info_${lang}'">OK</button>
							`
                    }else {
                        div.innerHTML= `
					<h2>Success</h2>
					<p>Please check your email and confirm your registration</p>
					<button class="submit" onClick="$(this).parent().remove(); window.location.href = '/'">OK</button>
					`
                    }

                    $(div).addClass('confirm-alrt')
                    $(wrapper).append(div)
                    $('body').append(wrapper)
                    div.scrollIntoView({block: "center", behavior: "smooth"})
                }else {
                    $('.reg-alrt').text('Something went wrong. Please try again later ').removeClass('hidden')
                }
            })
    };


    $('#edit-info').submit(function (e) {
        e.preventDefault()
        const rex = /^[^\s](\+)?([- _():=+]?\d[- _():=+]?){10,14}$/
        if(rex.test(this.phone.value) === false) {
            this.phone.style.border='1px solid red'
            $('#phone-error').text(alertMessage.tel[lang])
            return
        }
        const data = {
            type: this.type.value,
            company:this.company_name ? this.company_name.value : "",
            name: this.first_name.value,
            lastName: this.last_name.value,
            country: this.country.value,
            town: this.town.value,
            postCode: this.postcode.value,
            street: this.street.value,
            house: this.house.value,
            phone: this.phone.value,
            // birth: this.birth.value,
            companyDel :this.company_name ? this.company_name.value : "",
            nameDel :this.first_name.value,
            lastNameDel:this.last_name.value,
            countryDel: this.country.value,
            townDel: this.town.value,
            postCodeDel: this.postcode.value,
            streetDel: this.street.value,
            houseDel :this.house.value

        };
        if(this['different-delivery'].value === 'yes') {
            data.companyDel = this["company_name-delivery"] ? this["company_name-delivery"].value : "",
                data.nameDel = this["first_name-delivery"].value
            data.lastNameDel = this["last_name-delivery"].value
            data.postCodeDel = this["postcode-delivery"].value
            data.countryDel = this["country-delivery"].value
            data.townDel = this['town-delivery'].value
            data.streetDel = this["street-delivery"].value
            data.houseDel = this["house-delivery"].value

        }
        changeInfo(data)

    });

    $('#partnerForm').submit(function (e) {
        e.preventDefault();
        $('input').removeClass('red-border')
        $('.input-error').text('')
        const data = {};
        const rex =/^(\+)?\d{10,14}$/;
        $(this).find('input, textarea').each((index, elem) => {
            if(elem.name) data[elem.name] = elem.value.trim();

        })
        if(rex.test(data.phone) === false) {
            $('input[name="phone"]').addClass('red-border')
            $('#phone-error').text(alertMessage.tel[lang])
            return
        }

        if(this.password.value != this.confirm_password.value ) {
            $('input[name="confirm_password"]').addClass('red-border')
            $('#password-error').text(alertMessage.retPassword.nl)
            return
        }


        axios.post('/webshop/api/hairdresser/save', data)
            .then(res => {
                $("#alert").removeClass('error-alert').removeClass('success-alert')
                res.data.id ?
                    $("#alert").addClass("success-alert").text(alertMessage.partnerFormSuccess[lang]) :
                    $("#alert").addClass("error-alert").text(alertMessage.userExist[lang])
                setTimeout(() => {
                    $('#alert').text("").removeClass('error-alert' && "success-alert")
                }, 3000)
            })
            .catch(() => {
                $("#alert").addClass("error-alert").text(alertMessage.userExist[lang])
                setTimeout(() => {
                    $('#alert').text("").removeClass('error-alert')
                }, 3000)
            })
    })





    var rpls = $('.total_cart-sum').text().replace('.', ',');
    $('.total_cart-sum').text(rpls);
    if (document.getElementById("countries")) {

    }
    function showLoginForm() {
        $('.aanmelden-btn-in').addClass('aaanmelden-btn-in--hide');
        $('.aanmelden-box').addClass('anmelden-box--show');
    }

    function hideLoginForm() {
        $('.aanmelden-btn-in').removeClass('aaanmelden-btn-in--hide');
        $('.aanmelden-box').removeClass('anmelden-box--show');
        localStorage.removeItem('linkToProduct');
    }

    $('.aanmelden-btn-in').click(function () {
        showLoginForm();
        localStorage.removeItem('linkToProduct');
    });

    //login form start
    $('.login-popap, .force-login').click(function () {
        $('.limiter').removeClass('hidden')
        $('#restorePass').remove()
        $('#logInPopap').removeClass('hidden')
    });
    $('.close-popap').click(function () {
        $('.input100').val('').removeClass('has-val')
        $('.limiter').addClass('hidden')
    });
    $('.container-login100').click(function () {
        $('.input100').val('').removeClass('has-val')
        $('.limiter').addClass('hidden')
    });
    //lodin form finish

    $(document).click(function (e) {
        var formEl = $('.aanmelden-box');
        var target = $(e.target);
        if (formEl !==target &&
            target[0] !== $('.aanmelden-btn-in')[0] &&
            formEl.has(target).length === 0 &&
            !target[0].classList.contains('buy')&&
            $($('.buy').parent()).has(target).length ===0
        ) {
            hideLoginForm();
        }
    });

    $($('.buy').parent().children('a')).click(function (e) {
        if ($('.aanmelden-box').length>0){
            localStorage.setItem('linkToProduct',this.href);
            e.preventDefault();
            showLoginForm();
        }
    });

    if (pfConfigurator) runPf('AnimatedPictureFrame', pfConfigurator);
    resizeBlock.init($('.bigPic'));

    $('.go_to').click(function () {
        var scroll_el = $(this).attr('href');
        if ($(scroll_el).length != 0) {
            $('html, body').animate({scrollTop: $(scroll_el).offset().top}, 500);
        }
        return false;
    });

    // $('#searchbutton').click(function () {
    // var input = $('#search-zoeken-top');
    //     $(input).animate({width:'toggle'}, 350);
    // $(this).hide();
    // $('#searchButtonActive').show();
    // });



    renderTableContent();

    $("#user_form_js").submit(function () {
        var $input = $('#user_form_email');
        var $prijstyp = $('.payment_item input:checked').val();
        var data = {email: $input.val(), display_price: $prijstyp};
        $.ajax({
            type: 'POST',
            url: '/webshop/api/profile',
            data: data,
            success: function () {
            },
            error: function () {
            }
        });
    });
    var $input,
        $multipleOf,
        $price,
        $priceAsNumber,
        $totalSum;

    $('.wish_items .js_minus').click(function (event) {
        event.preventDefault();
        $input = $(event.target.parentNode.childNodes[1]);
        $multipleOf = parseInt($(event.target.parentNode.childNodes[2]).text());
        var count = $multipleOf == '1' ? parseInt($input.val()) - 1 : parseInt($input.val()) - $multipleOf;
        count = count < $multipleOf ? $multipleOf : count;
        $input.val(count);
    });

    $('.wish_items .js_plus').click(function (event) {
        event.preventDefault();
        $input = $(event.target.parentNode.childNodes[1]);
        $multipleOf = parseInt($(event.target.parentNode.childNodes[2]).text());
        $multipleOf == '1' ? $input.val(parseInt($input.val()) + 1) : $input.val(parseInt($input.val()) + $multipleOf);
    });


    $('.js_counter_wishlist').blur(function (e) {
        var $input = event.target.value;
        var multipleOf = parseInt($(event.target.parentNode.childNodes[2]).text());
        var temp = $input % multipleOf;
        $input < multipleOf ? event.target.value = multipleOf : event.target.value = $input - temp;
    });

    $('.delete_wish_js').click(function (event) {
        var $id = event.target.parentNode.parentNode.childNodes[3].innerText;
        $.ajax({
            type: 'DELETE',
            url: '/webshop/api/wishlist?id=' + $id,
            success: function () {
                event.target.parentNode.parentNode.remove();
            },
            error: function () {
                alert('Product kan niet worden verwijderd')
            }
        });
    });

    $('.sm_items .js_minus').click(function (event) {
        event.preventDefault();
        $input = $(event.target.parentNode.childNodes[1]);
        $multipleOf = parseInt($(event.target.parentNode.childNodes[2]).text());
        var count = $multipleOf == '1' ? parseInt($input.val()) - 1 : parseInt($input.val()) - $multipleOf;
        count = count < $multipleOf ? $multipleOf : count;
        $input.val(count);
    });

    $('.sm_items .js_plus').click(function (event) {
        event.preventDefault();
        $input = $(event.target.parentNode.childNodes[1]);
        $multipleOf = parseInt($(event.target.parentNode.childNodes[2]).text());
        $multipleOf == '1' ? $input.val(parseInt($input.val()) + 1) : $input.val(parseInt($input.val()) + $multipleOf);
    });

    $('.js_counter_search').blur(function (e) {
        var $input = event.target.value;
        var multipleOf = parseInt($(event.target.parentNode.childNodes[2]).text());
        var temp = $input % multipleOf;
        $input < multipleOf ? event.target.value = multipleOf : event.target.value = $input - temp;
    });


    if (document.querySelector('.reviews-slide')) {
        var width = document.querySelector('.reviews-slide--block').clientWidth + 10,
            count = 3,
            carousel = document.querySelector('.reviews-slide--wrapper'),
            list = document.querySelector('.reviews-slide--wrapper .slide-wrapper'),
            listElems = document.querySelectorAll('.reviews-slide--block'),
            position = 0;
        if (window.innerWidth <= 1000) {
            var count = 1,
                width = document.querySelector('.reviews-slide--block').clientWidth;
        }
        if (document.getElementById('reviewsLeft') || document.getElementById('reviewsRight')) {
            document.getElementById('reviewsLeft').addEventListener('click', function () {
                position = Math.min(position + width * count, 0);
                list.style.marginLeft = position + 'px';
            });
            document.getElementById('reviewsRight').addEventListener('click', function () {
                position = Math.max(position - width * count, -width * (listElems.length - count));
                list.style.marginLeft = position + 'px';
            });
        }
    }




    $('.ham').click(function (event) {

     /*   let headerHeigh =  $('.header__wrapper').height();*/

        $('#negeso_main_menu').toggleClass('hidden');
    /*    $('#negeso_main_menu').css({top:`${headerHeigh}px`});*/
    });


    $('.menu-type').click(function(e){
        $('.menu-type p').removeClass('active');
    });



    if(window.innerWidth < 650) {
        $('.header__breadcrumbs__left').append($('.menu-type'));
    }

    $('.bigtext').addClass('hidden');
    $('.ingridient_wrapper .more-btn').show();
    $('.ingridient_wrapper .more-btn').click(function (e) {
        $(this).hide();
        var text_block = "#" + $(this).data("target");
        $(text_block).toggleClass('hidden');

        if($(this).data("target") === "m3"){

            let urlData =   $('.changed-img').attr("alt");
            $('.changed-img').attr("src", urlData);
        }

        if($(this).data("target") === "m9"){
            $(this).closest('.ingr-section_main').css({
                'position' : 'static',
                'width' : '100%'
            });
        }
    });

    let iOS = navigator.platform && /iPad|iPhone|iPod/.test(navigator.platform);
   if(iOS === true){
       $('.header').addClass('ios');
   }



 });





$(function() {
    $('#accordeon .acc-head').on('click', f_acc);
});
function f_acc(){
    $('#accordeon .acc-body').not($(this).next()).slideUp(500);
    $(this).next().slideToggle(500);
};




$(function() {
    $('#show_more .spoiler-trigger').on('click', f_acc_hiden);
});
function f_acc_hiden(){
    $('#show_more .spoiler-block').not($(this).next());
    $(this).next().slideToggle(500);
    $(this).hide();
};




function renderTableContent() {
    if ($(window).width() < 800) {
        var allContentTablesArray = $('.b-content .contentStyle table, .contentStyleFooter table'),
            specTables = $('table.b-nlTable');
        if (typeof specTables !== 'undefined') {
            allContentTablesArray.push(specTables);
        }
        for (var i = allContentTablesArray.length - 1; i >= 0; i--) {
            breakTable(allContentTablesArray[i]);
        }
        ;
    }
}

function breakTable(table) {
    var oldView = table,
        allCells = $(oldView).find('tr td'),
        allRows = $(oldView).find('tr');
    for (var i = allCells.length - 1; i >= 0; i--) {
        cutRow(allCells[i]);
        $(allCells[i]).wrap('<tr class="wrap_cell"></tr>');
    }
}

function cutRow(cell) {
    if ($(cell).parent().is('tr') && !($(cell).parent().hasClass('wrap_cell'))) {
        $(cell).unwrap();
    }
    if ($(cell).parent().is('tr') && $(cell).parent().hasClass('wrap_cell')) {
        return false;
    }
}

var resizeBlock = (function () {
    var init = function (element) {
        var choosenBlock = element,
            config = config || {};
        // if (typeof choosenBlock === 'undefined') {
        if (!choosenBlock.length) {
            return false;
        }

        function calculateElementParams() {
            config = {
                elWidth: choosenBlock.width(),
                elHeight: choosenBlock.height(),
                imgWidth: document.querySelector('.' + choosenBlock.attr('class').split(' ').join('.') + ' img').getAttribute('width'),
                imgHeight: document.querySelector('.' + choosenBlock.attr('class').split(' ').join('.') + ' img').getAttribute('height'),
            };
            config.proportion = config.imgWidth / config.imgHeight;
            config.newWidthOfElement = config.elWidth / config.proportion;
            return config;
        }

        function resizingWindow() {
            $(window).resize(function () {
                calculateElementParams();
                choosenBlock.css('height', config.newWidthOfElement);
            });
        }

        calculateElementParams();
        element.css('height', config.newWidthOfElement);
        resizingWindow();
    };
    return {
        init: init
    }
})();


/*function initMap() {
    let map = new google.maps.Map(document.getElementById("map"), {
        center: {lat: 52.361576, lng: 6.625663 },
        zoom: 17
    });

    let marker = new google.maps.Marker({
        position:{lat: 52.361576, lng: 6.625663 },
        map: map
    });


}*/

function runPf(name, conf) {
    try {
        if (document.getElementsByName(name).length > 1) {
            /* Give  params to embed for FF */
            document.getElementsByName(name)[1].init(conf);
        } else {
            /* Give  params to object for IE */
            document.getElementsByName(name)[0].init(conf);
        }
    }
    catch (e) {
        setTimeout(function () {
            runPf(name, conf)
        }, 200);
    }
}

/**
 * DEPRECATED
 */

function openDialog(params) {
    params = params || {};

    var dialog = window.showModalDialog('/dialogs/commonPopupWindow.html',
        params, 'center:yes; dialogHeight:550px;resizable:yes;dialogWidth:550px ')

    if (params.answer.resCode == 'OK') {
        return params.answer;
    }
}

function showImgFunc() {
    if (isPfRuning && pfSetting.picsLength > 1) {
        next_pic = pfSetting.curPic + 1;
        if (next_pic == pfSetting.picsLength) {
            next_pic = 0;
        }
        try {
            $(pfSetting
                .pics[pfSetting.curPic])
                .animate({opacity: 0},
                    parseInt(pfSetting.speed) * parseInt(pfSetting.step)
                ).css('z-index', 0);
            $(pfSetting
                .pics[next_pic])
                .animate({
                        opacity: 1
                    },
                    parseInt(pfSetting.speed) * parseInt(pfSetting.step)
                ).css('z-index', 1).end();
            pfSetting.curPic = next_pic;
            setTimeout('showImgFunc()', pfSetting.delay);
        } catch (e) {
        }
    }
}

// Lang images pre-load
var lang_en_selected = new Image();
var lang_nl_selected = new Image();
lang_en_selected.src = "/site/core/images/flag-enr.gif";
lang_nl_selected.src = "/site/core/images/flag-nlr.gif";
// Menu images pre-load
var menu_bg_1 = new Image();
menu_bg_1.src = "/site/core/images/menu_bg.gif";
var menu_bg_2 = new Image();
menu_bg_2.src = "/site/core/images/bg-td-menu.gif";
var menu_bg_3 = new Image();
menu_bg_3.src = "/site/core/images/menu_bgr.gif";

function addPtoductWishInCart(event, elem) {
    var r = elem.getBoundingClientRect().top + pageYOffset;
    var value = event.target.parentNode.parentNode.childNodes[2].childNodes[1].value;
    var id = event.target.parentNode.childNodes[1].innerHTML;
    var data = {quantity: value, product: {id: id}};
    ajaxResponseWishList(data, r)
}

function addPtoductSearchInCart(event, elem) {
    var r = elem.getBoundingClientRect().top + pageYOffset;
    var value = event.target.parentNode.childNodes[1].value;
    var id = event.target.parentNode.childNodes[4].dataset.ordercode;
    var data = {quantity: value, product: {id: id}};
    ajaxResponseWishList(data, r)
}

function ajaxResponseWishList(data, r) {
    $.ajax({
        type: 'PUT',
        url: '/webshop/api/cart?strategy=ADD',
        data: JSON.stringify(data),
        dataType: 'json',
        success: function (res) {
            if (res.orderPriceIncDiscountExVat > 250) {
                $('.total_cart-sum').addClass('total_cart-sum--green')
            }
            $('.total_cart-sum').text(res.orderPriceIncDiscountExVat);
            var number = res.orderPriceIncDiscountExVat;
            var fixNumber = (+number).toFixed(2);
            var rpls = fixNumber.replace('.', ',');
            $('.total_cart-sum').text(rpls);
            if ($('.total_cart-sum').text()) {
                $('.total_cart-sum').css('padding', '0 10px');
            }
            ;

            $(".container").append("<div class='byu_item'>" +
                "<div class='byu_item_bg'></div>" +
                "<h3 id='byu_item_header'>Succes!<p>Toegevoegd aan winkelwagen</p><img src='/site/core/images/success.png' alt=''></h3>" +
                "</div>");
            $('#byu_item_header').css('top', r + 'px');
            $(".byu_item").fadeIn(1);
            setTimeout(function () {
                $(".byu_item").fadeOut(1);
                $(".byu_item").remove();
                $('#webshop_cart_counter').text(res.items)
            }, 500);
        },
        error: function () {
            $(".container").append("<div class='byu_item'>" +
                "<div class='byu_item_bg'></div>" +
                "<h3 id='byu_item_header'>Fout!<p>Niet toegevoegd aan winkelwagen</p><p><a href='/inloggen_nl.html'>Log in </a></p></h3>" +
                "</div>");
            $('#byu_item_header').css('top', r + 'px');
            $(".byu_item").fadeIn(1);
            setTimeout(function () {
                $(".byu_item").fadeOut(1);
                $(".byu_item").remove();
            }, 500);
        }
    })
}



const messagesScanResult = {
    scanDetails: {
        en: "Scan details information:",
        nl: "Scan details informatie:"
    },
    hoverImageTitle: {
        en: "click&nbsp;to&nbsp;see&nbsp;large&nbsp;image",
        nl: "klik&nbsp;om&nbsp;een&nbsp;grote&nbsp;afbeelding&nbsp;te&nbsp;zien"
    },
    date:{
        en: "Date:",
        nl: "Datum:"
    },
    time:{
        en: "Time:",
        nl: "Tijd:"
    },
    noScan:{
        en: "no results found",
        nl: "Geen resultaten gevonden"
    }

};


function getUserScanResults(){
    fetch('webshop/api/customerdetails')
        .then((response) => {
            return response.json();
        })
        .then((result) => {
            let data = result;

            if (data.success == false || data.length == 0) {
                $('.scan-results-wrapper').append(`<p class="no-result-notification">${messagesScanResult.noScan[lang]}</p>`);

            } else {

                data.sort((a,b) => (a.id < b.id) ? 1 : ((b.id < a.id) ? -1 : 0));
console.log(data);

                data.forEach(function (elem) {
                let dateScan = elem["date"].substring(0, 8).split("");
                dateScan.splice(4, 0, ' - ');
                dateScan.splice(7, 0, ' - ');

                let timeScan = elem["time"].replace(/(\d)(?=(\d{2})+(\D|$))/g, '$1 : ');
                let comment;
                if (elem["comments"] === null) {
                    comment = ' - ';
                } else {
                    comment = elem["comments"]
                }

                $('.scan-results-wrapper').append(`
              <div class="result-item-wrapper">
              <div class="result-item" id = ${elem["id"]} onclick="showScanDetails(this, this.id)">
              <p class="dateScan arrow">${messagesScanResult.date[lang]} ${dateScan.join('')}</p>              
              <p>${messagesScanResult.time[lang]} ${timeScan}</p>             
              </div>
              <div class="result-item-extended hidden" data-target = ${elem["id"]}>
              <h2> ${messagesScanResult.scanDetails[lang]}</h2>
                    <p>skin condition: ${elem["skinCondition"]}</p> 
                    <p>temperature: ${elem["temperature"]}</p> 
                    <p>humidity: ${elem["humidity"]}</p> 
                    <p>uv index: ${elem["uvIndex"]}</p> 
                    <p>scalp Hydration Raw Value: ${elem["scalpHydrationRawValue"]}</p> 
                    <p>scalp Hydration Value: ${elem["scalpHydrationValue"]}</p> 
                    <p>scalp Hydration Level: ${elem["scalpHydrationLevel"]}</p> 
                    <p>scalp Hydration Desc: ${elem["scalpHydrationDesc"]}</p> 
                    <div class="imageScanResult">
                    <p><a data-title= ${messagesScanResult.hoverImageTitle[lang]} target="_blank" href=http://${elem["scalpHydrationOrgUrl"]} >scalp Hydration Org Url:  </a> <img  onerror="$(this).parent().remove();" src= http://${elem["scalpHydrationOrgUrl"]}/></p> 
                    <p><a data-title= ${messagesScanResult.hoverImageTitle[lang]} target="_blank" href=http://${elem["scalpHydrationRstUrl"]} >scalp Hydration Rst Url:  </a> <img  onerror="$(this).parent().remove()" src= http://${elem["scalpHydrationRstUrl"]}/></p>  
                    </div>
                    <p>scalp Sebum Raw Value: ${elem["scalpSebumRawValue"]}</p> 
                    <p>scalp Sebum Value: ${elem["scalpSebumValue"]}</p> 
                    <p>scalp Sebum Level: ${elem["scalpSebumLevel"]}</p> 
                    <p>scalp Sebum Desc: ${elem["scalpSebumDesc"]}</p> 
                    <div class="imageScanResult">
                    <p> <a data-title= ${messagesScanResult.hoverImageTitle[lang]} target="_blank" href=http://${elem["scalpSebumOrgUrl"]} > scalp Sebum Org Url </a> <img onerror="$(this).parent().remove();" src= http://${elem["scalpSebumOrgUrl"]}/></p> 
                    <p> <a data-title= ${messagesScanResult.hoverImageTitle[lang]} target="_blank" href=http://${elem["scalpSebumRstUrl"]} > scalp Sebum Rst Url </a> <img  onerror="$(this).parent().remove();" src= http://${elem["scalpSebumRstUrl"]}/></p> 
                    </div>
                    <p>hair Density Raw Value: ${elem["hairDensityRawValue"]}</p> 
                    <p>hair Density Value: ${elem["hairDensityValue"]}</p> 
                    <p>hair Density Level: ${elem["hairDensityLevel"]}</p> 
                    <p>hair Density Desc: ${elem["hairDensityDesc"]}</p> 
                    <div class="imageScanResult">
                    <p><a data-title= ${messagesScanResult.hoverImageTitle[lang]} target="_blank" href=http://${elem["hairDensityOrgUrl"]}>hair Density Org Url</a> <img  onerror="$(this).parent().remove();" src= http://${elem["hairDensityOrgUrl"]}/></p> 
                    <p> <a data-title= ${messagesScanResult.hoverImageTitle[lang]} target="_blank" href=http://${elem["hairDensityRstUrl"]}>hair Density Rst Url </a> <img  onerror="$(this).parent().remove();" src= http://${elem["hairDensityRstUrl"]}/></p>   
                             </div>             
                    <p>dead Skin Cells Raw Value: ${elem["deadSkinCellsRawValue"]}</p> 
                    <p>dead Skin Cells Value: ${elem["deadSkinCellsValue"]}</p>                      
                    <p>dead Skin Cells Level: ${elem["deadSkinCellsLevel"]}</p> 
                    <p>dead Skin Cells Desc: ${elem["deadSkinCellsDesc"]}</p>         
                                <div class="imageScanResult">
                    <p> <a data-title= ${messagesScanResult.hoverImageTitle[lang]} target="_blank" href=http://${elem["deadSkinCellsOrgUrl"]}>dead Skin Cells Org Url </a> <img onerror="$(this).parent().remove();"  src= http://${elem["deadSkinCellsOrgUrl"]}/></p> 
                    <p> <a data-title= ${messagesScanResult.hoverImageTitle[lang]} target="_blank" href=http://${elem["deadSkinCellsRstValue"]}>dead Skin Cells Rst Value </a> <img  onerror="$(this).parent().remove();" src= http://${elem["deadSkinCellsRstValue"]}/></p>     
                         </div>
                    <p>scalp Impurities Raw Value: ${elem["scalpImpuritiesRawValue"]}</p> 
                    <p>scalp Impurities Level: ${elem["scalpImpuritiesLevel"]}</p>
                    <p>scalp Impurities Desc: ${elem["scalpImpuritiesDesc"]}</p>   
                               <div class="imageScanResult">             
                    <p> <a data-title= ${messagesScanResult.hoverImageTitle[lang]} target="_blank" href= http://${elem["scalpImpuritiesOrgUrl"]}>scalp Impurities Org Url </a> <img  onerror="$(this).parent().remove();" src= http://${elem["scalpImpuritiesOrgUrl"]}/></p> 
                    <p> <a data-title= ${messagesScanResult.hoverImageTitle[lang]} target="_blank" href=http://${elem["scalpImpuritiesRstUrl"]}>scalp Impurities Rst Url </a> <img onerror="$(this).parent().remove();"  src= http://${elem["scalpImpuritiesRstUrl"]}/></p>  
                       </div>
                    <p>comments: ${comment}</p>
               </div>
               </div>
               
              `);
            });
        }
        })
        .catch(error => console.log('error', error));

}

function showScanDetails (elem, clicked_id){

    $(elem).find('.dateScan').toggleClass('arrow');
    $('.result-item-extended').each(function(){
        if($(this).data('target') == clicked_id){
            $(this).toggleClass('hidden');
            location.href = `#${clicked_id}`

        }
    });

};

function showSpiner(node) {
    $(node).append(`<div class="lds-default"><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div>`)
}

function removeSpiner(node) {
    $(node).find('.lds-default').remove()
}

