$(document).ready(function () {
    setUnchecked();
    var NDS_VALUE = 0.21;
    var firstPrice = $('#total').text();
    var firstPrice2 = firstPrice.replace('.', '');
    var pricesBlock = document.querySelector('.total_delivery');

    var priceElemsObj = {
        orderPrice: pricesBlock.querySelector('#orderPrice'),
        deliveryPrice: pricesBlock.querySelector('#deliveryPrice'),
        totalVat: pricesBlock.querySelector('#totalVat'),
        total: pricesBlock.querySelector('#total'),
    };

    checkCountry();

    function checkCountry() {
        var nlNames = [
            'NL',
            'NETHERLANDS',
            'Netherlands',
            'Netherland',
            'Nederland',
            'Nederlands'
        ];
        var billingCountry = document.querySelector('#billing_countryId');
        if ((billingCountry && (nlNames.indexOf(billingCountry.value) !== -1)) || billingCountry.value === "" ) {
            return true
        }
        else {
            NDS_VALUE = 0;
            let fieldBtv = document.querySelector('#totalVat').parentNode;
            $(fieldBtv).children('span').hide();
            pricesDiscount.total = getFormattedPrice((getPriceNum(pricesDiscount.total) - getPriceNum(pricesDiscount.vat)).toFixed(2));
            pricesPure.total = getFormattedPrice((getPriceNum(pricesPure.total) - getPriceNum(pricesPure.vat)).toFixed(2));
            pricesDiscount.vat = 0;
            pricesPure.vat = 0;
            setPricesInFields(pricesPure, priceElemsObj);
        }
    }


    $('#factuuradres_afleveradres').click(function () {
        var agreed = document.querySelector('#factuuradres_afleveradres').checked;
        var inputs = $('.checkout_form_right p input');
        agreed ? inputs.attr('disabled', true) : inputs.attr('disabled', false);
    });

    var delivery = true;
    var discount = false;
    var currentPrices = pricesPure;

    function setPricesInFields(priceObj, elementsObj) {
        elementsObj.orderPrice.innerHTML = priceObj.orderPrice;
        elementsObj.deliveryPrice.innerHTML = priceObj.deliveryPrice;
        elementsObj.totalVat.innerHTML = priceObj.vat;
        elementsObj.total.innerHTML = priceObj.total;
    }

    function deleteDelivery(priceObj) {
        let newPriceObj = {};
        newPriceObj.orderPrice = priceObj.orderPrice;
        newPriceObj.deliveryPrice = getFormattedPrice(0);
        newPriceObj.vat = getFormattedPrice((getPriceNum(priceObj.orderPrice) * NDS_VALUE).toFixed(2));
        newPriceObj.total = getFormattedPrice((+getPriceNum(newPriceObj.orderPrice) + +getPriceNum(newPriceObj.vat)).toFixed(2));
        return newPriceObj;
    }

    function getPriceNum(str) {
        return str.substring(2).replace(',', '.');
    }

    function getFormattedPrice(num) {
        num = +num;
        num = num.toFixed(2);
        num = num.toString();
        return '€ ' + num.replace('.', ',')
    }

    $('#afhalen').click(function () {
        if (discount) {
            setPricesInFields(deleteDelivery(pricesDiscount), priceElemsObj);
        }
        else {
            setPricesInFields(deleteDelivery(pricesPure), priceElemsObj);
        }
        delivery = false;
    });
    $('#bezorgFactuuradres').click(function () {
        if (discount) {
            setPricesInFields(pricesDiscount, priceElemsObj);
        }
        else {
            setPricesInFields(pricesPure, priceElemsObj);
        }
        delivery = true;
    });
    $('.ideal-paypal').click(function (event) {
        discount = true;
        if (delivery) {
            currentPrices = pricesDiscount;
            setPricesInFields(currentPrices, priceElemsObj);
        }
        else {
            setPricesInFields(deleteDelivery(pricesDiscount), priceElemsObj)
        }
    });
    $('#opRekening').click(function (e) {
        discount = false;
        if (delivery) {
            currentPrices = pricesPure;
            setPricesInFields(pricesPure, priceElemsObj);
        }
        else {
            setPricesInFields(deleteDelivery(pricesPure), priceElemsObj);
        }


    });


    function toogleProgressIndicator() {
        $('#form_submit_button_js').toggleClass('indicator-active');
    }


    $("#checkout_form_js").submit(function (event) {
        if (!validate_advanced(this, 1)) {
            return false;
        }
        if ($('.type_payment .delivery_item input').length == 1 && $('#ideal-options select option').length == 1) {
            alert('Selecteer het type betaling');
            return false;
        } else {
            if (!$("#opRekening").attr('checked')) {
                toogleProgressIndicator();
            }
            $('#form_submit_button_js').attr('disabled', 'disabled')
        }

        var js_comment_block = $('#js_comment_block').val();
        js_comment_block = js_comment_block.replace(/\n/g, ', ').replace(/"/g, "'");
        $('#js_comment_block').val(js_comment_block);

        var data = $(this).serializeArray();

        event.preventDefault();
        $.ajax({
            type: 'POST',
            url: '/webshop/api/order',
            data: data,
            success: function (data, textStatus, request) {
                window.location.href = request.getResponseHeader('Location');
            },
            error: function () {
                alert('Product kan niet worden verwijderd');
                toogleProgressIndicator();
            }
        });
    });


    function setUnchecked() {
        let ideal = document.querySelector('#Ideal');
        let op = document.querySelector('#opRekening');
        let addr_delivery = document.querySelector('#factuuradres_afleveradres');
        if (ideal.checked && op && addr_delivery.checked) {
            ideal.checked = false;
            op.checked = true;
            addr_delivery.checked = false;
        }
    }

    $('input[type="radio"]').click(function () {
        var idealElement = $('#ideal-options');
        if ($(this).attr('id') === 'Ideal') {
            $.ajax({
                url: "/webshop/api/ideal/issuers", success: function (result) {
                    var select = idealElement.find('select');
                    select.empty();
                    result.forEach(function (item) {
                        select.append(new Option(item.name, item.id));
                    });
                    idealElement.show();
                }
            });

        } else {
            idealElement.hide();
        }
    });
});

