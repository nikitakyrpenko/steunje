$(document).ready(function(){
    var $input,
        $multipleOf,
        $price,
        $priceWithoutCurrency,
        $priceAsNumber,
        $totalSum,
        $productId;
    var cartItemsTotalElements = $(".cart_items_wrapper .cart_items-total");
    var cartItemsTotalSum = 0;

    $('.js_minus').click(function (event) {
        event.preventDefault();
        $input = $(event.target.parentNode.childNodes[1]);
        $multipleOf = parseInt($(event.target.parentNode.childNodes[2]).text());
        $price = event.target.parentNode.parentNode.childNodes[1].innerText;
        $priceWithoutCurrency = $price.substring(2).replace(',','.');
        $priceAsNumber = parseFloat($priceWithoutCurrency);
        var count = $multipleOf == '1' ? parseInt($input.val()) - 1 : parseInt($input.val()) - $multipleOf;
        count = count < $multipleOf ? $multipleOf : count;
        $input.val(count);
        $totalSum = $input.val() * $priceAsNumber;
        event.target.parentNode.parentNode.childNodes[5].innerText = '€ ' + $totalSum.toFixed(2).replace('.',',');
        $productId = event.target.parentNode.parentNode.childNodes[4].innerText;
        var data = {quantity: count, product: {id: $productId}};
        changeCartTotalSum();
        $.ajax({
            type    : 'PUT',
            url     : '/webshop/api/cart',
            data    : JSON.stringify(data),
            success: function(){},
            error: function(){}
        })
    });

    $('.js_plus').click(function (event) {
        event.preventDefault();
        var productId = event.target.parentNode.parentNode.childNodes[3].innerText;
        $input = $(event.target.parentNode.childNodes[1]);
        $multipleOf = parseInt($(event.target.parentNode.childNodes[2]).text());
        $multipleOf == '1' ? $input.val(parseInt($input.val()) + 1) : $input.val(parseInt($input.val()) + $multipleOf);
        $price = event.target.parentNode.parentNode.childNodes[1].innerText;
        $priceWithoutCurrency = $price.substring(2).replace(',','.');
        $priceAsNumber = parseFloat($priceWithoutCurrency);
        $totalSum = $input.val() * $priceAsNumber;
        event.target.parentNode.parentNode.childNodes[5].innerText = '€ ' + $totalSum.toFixed(2).replace('.',',');
        $productId = event.target.parentNode.parentNode.childNodes[4].innerText;
        var data = {quantity: $input.val(), product: {id: $productId}};
        changeCartTotalSum();
        $.ajax({
            type    : 'PUT',
            url     : '/webshop/api/cart',
            data    : JSON.stringify(data),
            success: function(){},
            error: function(){}
        })
    });

    function changeCartTotalSum() {
        cartItemsTotalSum = 0;
        cartItemsTotalElements = $(".cart_items_wrapper .cart_items-total");
        if (cartItemsTotalElements) {
            for (var i = 0; i < cartItemsTotalElements.length; i++) {
                var itemValue = (cartItemsTotalElements[i].innerText).substring(2).replace(',','.');
                cartItemsTotalSum += +itemValue;
            }
            var itemsSumString = cartItemsTotalSum.toFixed(2).toString().replace('.',',');
        }
        $(".total_cart-sum").html(itemsSumString);
        if (cartItemsTotalSum > 250){
            $('.total_cart-sum').addClass('total_cart-sum--green')
        }
        else {
            $('.total_cart-sum').removeClass('total_cart-sum--green')
        }
    }

    $('.delete_product_js').click(function (event) {
        var $id = event.target.parentNode.parentNode.childNodes[4].innerText;
        $.ajax({
            type    : 'DELETE',
            url     : '/webshop/api/cart?id=' + $id,
            success: function(){
                if (!Element.prototype.remove) {
                    Element.prototype.remove = function remove() {
                        if (this.parentNode) {
                            this.parentNode.removeChild(this);
                        }
                    };
                }
                event.target.parentNode.parentNode.remove();
                changeCartTotalSum();
                var webshopCartCounter = $("#webshop_cart_counter").text();
                webshopCartCounter--;
                $("#webshop_cart_counter").html(webshopCartCounter);
            },
            error: function(){
                alert('Product kan niet worden verwijderd')
            }
        });
    });

    $('.clear_cart_js').click(function (event) {
        $.ajax({
            type    : 'DELETE',
            url     : '/webshop/api/cart',
            success: function(){
                $('.cart_items_wrapper').remove();
            },
            error: function(){
                alert('Product kan niet worden verwijderd')
            }
        });
    })

    $('.quantity').blur(function (event) {
        $input = $(event.target.parentNode.childNodes[1]).val();
        $multipleOf = parseInt($(event.target.parentNode.childNodes[2]).text());
        var temp = $input % $multipleOf;
        $input < $multipleOf ? event.target.value = $multipleOf : event.target.value = $input - temp;


        var data = {quantity: event.target.value, product: {id: $productId}};
        changeCartTotalSum();
        $.ajax({
            type    : 'PUT',
            url     : '/webshop/api/cart',
            data    : JSON.stringify(data),
            success: function(){},
            error: function(){}
        })
    });


    $( ".cart_items .quantity" ).change(function(event) {
        $input = $(event.target.parentNode.childNodes[1]);
        if (!isNaN($input.val())) {
            $multipleOf = parseInt($(event.target.parentNode.childNodes[2]).text());
            $productId = event.target.parentNode.parentNode.childNodes[4].innerText;
            var temp = $input.val() % $multipleOf;
            $input.val() < $multipleOf ? event.target.value = $multipleOf : event.target.value = $input.val() - temp;

            $price = event.target.parentNode.parentNode.childNodes[1].innerText;
            $priceWithoutCurrency = $price.substring(2).replace(',','.');
            $totalSum = $input.val() * $priceWithoutCurrency;
            event.target.parentNode.parentNode.childNodes[5].innerText = '€ ' + $totalSum.toFixed(2).replace('.',',');

            var data = {quantity: $input.val(), product: {id: $productId}};
            changeCartTotalSum();
            $.ajax({
                type    : 'PUT',
                url     : '/webshop/api/cart',
                data    : JSON.stringify(data),
                success: function(){},
                error: function(){}
            })
        }
    });

    $( ".cart_items .quantity" ).keyup(function(event) {
            $input = $(event.target.parentNode.childNodes[1]);
            if (!isNaN($input.val())) {
                $multipleOf = parseInt($(event.target.parentNode.childNodes[2]).text());
                $productId = event.target.parentNode.parentNode.childNodes[4].innerText;
                var temp = $input.val() % $multipleOf;
                $input.val() < $multipleOf ? event.target.value = $multipleOf : event.target.value = $input.val() - temp;

                $price = event.target.parentNode.parentNode.childNodes[1].innerText;
                $priceWithoutCurrency = $price.substring(2).replace(',','.');
                $totalSum = $input.val() * $priceWithoutCurrency;
                event.target.parentNode.parentNode.childNodes[5].innerText = '€ ' + $totalSum.toFixed(2).replace('.',',');

                var data = {quantity: $input.val(), product: {id: $productId}};
                changeCartTotalSum();
                $.ajax({
                    type    : 'PUT',
                    url     : '/webshop/api/cart',
                    data    : JSON.stringify(data),
                    success: function(){},
                    error: function(){}
            })
        }

    });


});
