$(document).ready(function(){
    var $input;
    var multipleOf = parseInt($('#multipleOf').text());
    $('.js_minus').click(function (event) {
        event.preventDefault();
        $input = $(event.target.parentNode).children('input');
        var count = multipleOf == '1' ? parseInt($input.val()) - 1 : parseInt($input.val()) - multipleOf;
        count = count < multipleOf ? multipleOf : count;
        $input.val(count);
    });

    $('.js_plus').click(function (event) {
        event.preventDefault();
        $input = $(event.target.parentNode).children('input');
        multipleOf == '1' ? $input.val(parseInt($input.val()) + 1) : $input.val(parseInt($input.val()) + multipleOf);
    });

    $('.js_minus_matrix').click(function (event) {
        event.preventDefault();
        var $input = $(event.target.parentNode.childNodes[1]);
        var $multipleOf = parseInt($(event.target.parentNode.childNodes[2]).text());
        var count = $multipleOf == '1' ? parseInt($input.val()) - 1 : parseInt($input.val()) - $multipleOf;
        count = count < $multipleOf ? $multipleOf : count;
        $input.val(count);
    });

    $('.js_plus_matrix').click(function (event) {
        event.preventDefault();
        var $input = $(event.target.parentNode.childNodes[1]);
        var $multipleOf = parseInt($(event.target.parentNode.childNodes[2]).text());
        $multipleOf == '1' ? $input.val(parseInt($input.val()) + 1) : $input.val(parseInt($input.val()) + $multipleOf);
    });

    $('.js_counter_matrix').blur(function (event) {
        $input = $(this).val();
        var multipleOf = parseInt($(event.target.parentNode.childNodes[2]).text());
        var temp = $input % multipleOf;
        $input < multipleOf ? $(this).val(multipleOf) : $(this).val($input - temp);
    })

    $('#js_counter').blur(function (e) {
        $input = $('#js_counter').val();
        var temp = $input % multipleOf;
        $input < multipleOf ? $('#js_counter').val(multipleOf) : $('#js_counter').val($input - temp);
    });

    $('.wish_list_add_js').click(addToWishListProductPage);

    $('.wish_list_added_js').click(removeFromWishList);

    function addToWishListProductPage() {
        console.log('add');
        // var orderCode = $('#pr_number').text();
        // var data = {id: orderCode};
        // $(".container").append("<div class='byu_item'>"+
        //    "<div class='byu_item_bg'></div>"+
        //    "<h3 id='byu_item_header'><p>Item al toegevoegd aan verlanglijstje</p><img src='/site/core/images/success.png' alt=''></h3>"+
        //    "</div>");
        // $(".byu_item").fadeIn(1);
        // setTimeout(function() {
        //    $(".byu_item").fadeOut(1);
        //    $(".byu_item").remove();
        // }, 500);
        var target = this;
        var orderCode = $('#pr_number').text();
        if (orderCode===''){
            var r = target.getBoundingClientRect().top + pageYOffset;
            orderCode = $(this).children('.product-code').text();
        }
        var data = {id: orderCode};
        $.ajax({
            type    : 'POST',
            url     : '/webshop/api/wishlist',
            data    :  data,
            success: function(res){
                $(".container").append("<div class='byu_item'>"+
                    "<div class='byu_item_bg'></div>"+
                    "<h3 id='byu_item_header'>Succes!<p>Toegevoegd aan verlanglijstje</p><img src='/site/core/images/success.png' alt=''></h3>"+
                    "</div>");
                $('#byu_item_header').css('top', r + 'px');
                $(".byu_item").fadeIn(1);
                setTimeout(function() {
                    $(".byu_item").fadeOut(1);
                    $(".byu_item").remove();
                    // $('#webshop_cart_counter').text(res.items);
                    var wishlist_counter = $('#wishlist_counter').html();
                    var wishlist_counter_total = +wishlist_counter + 1;
                    $('#wishlist_counter').html(wishlist_counter_total)
                }, 1000);
                $(target).unbind();
                $(target).addClass('wish_list_added_js').removeClass('wish_list_add_js');
                $(target).click(removeFromWishList);
            },
            error: function(){
                alert('Product niet toegevoegd aan wenslijst')
            }
        });
    }

    function removeFromWishList() {
        console.log('remove');
        var target = this;
        var orderCode = $('#pr_number').text();

        if (orderCode===''){
            orderCode = $(this).children('.product-code').text();
            var r = target.getBoundingClientRect().top + pageYOffset;
        }
        var data = {id: orderCode};
        $.ajax({
            type: 'DELETE',
            url: '/webshop/api/wishlist?id=' + data.id,
            success: function () {
                $(".container").append("<div class='byu_item'>"+
                    "<div class='byu_item_bg'></div>"+
                    "<h3 id='byu_item_header'><p>Verwijderd van verlanglijstje</p><img src='/site/core/images/removed.png' alt=''></h3>"+
                    "</div>");
                $('#byu_item_header').css('top', r + 'px');
                $(".byu_item").fadeIn(1);

                setTimeout(function() {
                    $(".byu_item").fadeOut(1);
                    $(".byu_item").remove();
                    // $('#webshop_cart_counter').text(res.items);
                    var wishlist_counter = $('#wishlist_counter').html();
                    var wishlist_counter_total = +wishlist_counter-1;
                    $('#wishlist_counter').html(wishlist_counter_total)
                }, 1000);
                $(target).unbind();
                $(target).addClass('wish_list_add_js').removeClass('wish_list_added_js');
                $(target).click(addToWishListProductPage);
            },
            error: function () {
                alert('Product kan niet worden verwijderd')
            }
        });
    }
});



function ajaxResponse (data, r) {
    $.ajax({
        type    : 'PUT',
        url     : '/webshop/api/cart?strategy=ADD',
        data    : JSON.stringify(data),
        dataType: 'json',
        success: function(res){

            if (res.orderPriceIncDiscountExVat > 250){
                $('.total_cart-sum').addClass('total_cart-sum--green')
            }

            $('.total_cart-sum').text(res.orderPriceIncDiscountExVat);
            var number = res.orderPriceIncDiscountExVat;
            var fixNumber = (+number).toFixed(2);
            var rpls = fixNumber.replace('.', ',');
            $('.total_cart-sum').text(rpls);
            if ($('.total_cart-sum').text()) {
                $('.total_cart-sum').css('padding', '0 10px');
            };
            $(".webshop-category-wrapper").append("<div class='byu_item'>"+
                "<div class='byu_item_bg'></div>"+
                "<h3 id='byu_item_header'>Succes!<p>Toegevoegd aan winkelwagen</p><img src='/site/core/images/success.png' alt=''></h3>"+
                "</div>");
          /*  $('#byu_item_header').css('top', r + 'px');*/
            $(".byu_item").fadeIn(1);
            setTimeout(function() {
                $(".byu_item").fadeOut(1);
                $(".byu_item").remove();
                $('#webshop_cart_counter').text(res.items);
            }, 600);
        },
        error: function(res){

            $(".webshop-category-wrapper").append("<div class='byu_item'>"+
                "<div class='byu_item_bg'></div>"+
                "<h3 id='byu_item_header'>Succes!<p>Toegevoegd aan winkelwagen</p><img src='/site/core/images/success.png' alt=''></h3>"+
                "</div>");
           /* $('#byu_item_header').css('top', r + 'px');*/
            $(".byu_item").fadeIn(1);
            setTimeout(function() {
                $(".byu_item").fadeOut(1);
                $(".byu_item").remove();
            }, 600);
        }
    })
}

function addPtoductInCart () {
    var value = $('#js_counter').val();
    var id = $('#pr_number').text();
    var data = {quantity: value, product: {id: id}};
    ajaxResponse(data)
}

function addPtoductInCartMatrix (event, elem) {
    var r = elem.getBoundingClientRect().top + pageYOffset;
    var value = event.target.parentNode.childNodes[1].value;
    var id = event.target.parentNode.parentNode.childNodes[2].childNodes[0].innerHTML;
    var data = {quantity: value, product: {id: id}};
    ajaxResponse(data, r)
}

function addPtoductListInCart (event, elem) {
    var value = $('#js_counter').val();
    var id = $('#pr_number').text();
    var data = {quantity: value, product: {id: id}};
    ajaxResponse(data)
}

function addToWishlist(event, elem){
    var r = elem.getBoundingClientRect().top + pageYOffset;
    event.preventDefault();
    var orderCode = $(event.target.childNodes[0]);
    var data = {id: orderCode.html()};
    $.ajax({
        type    : 'POST',
        url     : '/webshop/api/wishlist',
        data    :  data,
        success: function(res){
            $(".container").append("<div class='byu_item'>"+
                "<div class='byu_item_bg'></div>"+
                "<h3 id='byu_item_header'>Succes!<p>Toegevoegd aan verlanglijstje</p><img src='/site/core/images/success.png' alt=''></h3>"+
                "</div>");
            $(".byu_item").fadeIn(1);
            $('#byu_item_header').css('top', r + 'px');
            // $(".byu_item").fadeIn(1);
            setTimeout(function() {
                $(".byu_item").fadeOut(1);
                $(".byu_item").remove();
                var wishlist_counter = $('#wishlist_counter').html();
                var wishlist_counter_total = +wishlist_counter + 1;
                $('#wishlist_counter').html(wishlist_counter_total);
            }, 1000);
            $(elem).addClass('addedToWishList')
        },
        error: function(){
            alert('Product niet toegevoegd aan wenslijst')
        }
    });
}