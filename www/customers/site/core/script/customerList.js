let _paymentMethod = 'MOLLIE';
$(document).ready(() => {
    let paymentMethod = 'MOLLIE'
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

    let data = null
    axios('/webshop/api/hairdresser/customers')
        .then(res => showList(res.data))

    function loadScans(host) {
        axios(`/webshop/api/hairdresser/customer/${host.id}/details`)
            .then(res => showScans(res.data, host))
    }

    function showList(data) {
        if(data.length < 1) {
            const title = `<h2 class="alert-title">No records</h2>`
            $('#customer-list').parent().append(title)
        }else {
            data.map(item => {
                const li = `<li class="customer-list_item-wrapper">
                 <div class="customer-list_item">
                 <div class="customer-list_item-head">
                      <div>
                            <h5 class="customer-list_title">${item.billingContact.firstName} ${item.billingContact.secondName}</h5>
                            <p>${item.billingContact.email}</p>
                      </div>
                      <div>
                        <button  class="customer-list_button" id=${item.id}><img class="close-btn hidden" src="/media/close-icon.png"/>Scans</button>
                      </div>
                </div>
                    
                 </div>
             </li>`
                $('#customer-list').append(li)
            })
            $('.customer-list_button').click((event) => {
                if( $(event.target).parents('.customer-list_item').find('.scan-results-wrapper').length > 0) {
                    $(event.target).parents('.customer-list_item').find('.scan-results-wrapper').toggleClass("hidden")
                    $(event.target).find('img').toggleClass('hidden')
                    return
                }
                $(event.target).parents('.customer-list_item').append(`<div class="scan-results-wrapper"></div>`)
                $(event.target).find('img').toggleClass('hidden')
                loadScans(event.target)
            })
        }

    }

    function showScans(result, host) {
        let data = result;

        if (data.success == false || data.length == 0) {
            $(host).parents('.customer-list_item').find('.scan-results-wrapper').append(`<p class="no-result-notification">${messagesScanResult.noScan[lang]}</p>`);

        } else {

            data.sort((a,b) => (a.id < b.id) ? 1 : ((b.id < a.id) ? -1 : 0));

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

                $(host).parents('.customer-list_item').find('.scan-results-wrapper').append(`
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
                    <button onclick="showOrderPage(${host.id}, ${elem.id})" class="order-btn">Make order</button>
               </div>
               </div>
               
              `);
            });
        }
    }
})
function showOrderPage(userId, scanId) {
    const req_data = {customerId: userId, scan_id: scanId}
    axios.post('/webshop/api/hairdresser/customer/order', req_data)
        .then(() => {
            const popap = `
                 <div  class="popap-wrapper">
                     <div class="popap-block"></div>
                   </div>
    `
            $('body').append(popap)
            getProducts()
        })


}

function removeNode(node) {
    $(node).remove()
}

function getProducts() {
showSpiner('.popap-block')
    axios('/webshop/api/hairdresser/products')
        .then(res => {
            removeSpiner('.popap-block')
           renderProducts(res.data, '.popap-block')
        })
}

function renderProducts(data, node) {
    const html = `
    <h2 class="op_title">Order</h2>
    <h3 class="op_block-title">Product</h3>
    <div class="op_product-wrapper">
        ${data.map(item => {
            if(item.addon === false) {
                return `<div class="op_product">
            <div class="op_product-title">
                <img class="op_product-img" src="/media/337.png"/>
                <h3 class="op_product-title">${item.title}</h3>
            </div>
            <div class="op_controls">
                <p class="op_price">EUR ${item.priceInc}</p>
                <button onclick="addProduct(this.id,this)" id=${item.productCode} class="op_btn-add"><img class="op_icon" src="/media/add.png"/></button>
                <button id=${item.productCode} onclick="deleteProduct(this.id,this)" class="op_btn-add hidden"><img class="op_icon" src="/media/delete-icon.png"/></button>
            </div>
        </div>`;
            }
    })}
        
    </div>
    <div>
        <h3 class="op_block-title">Options</h3>
        <div class="op_options-wrapper">
            ${data.map(item => {
                if(item.addon) return `<div class="op_product">
                     <div class="op_product-title">
                        <img class="op_product-img" src="/media/337.png"/>
                         <h3 class="op_product-title">${item.title}</h3>
                    </div>
                    <div class="op_controls">
                        <p class="op_price">EUR ${item.priceInc}</p>
                        <button onclick="addProduct(this.id, this)" id=${item.productCode} class="op_btn-add"><img class="op_icon" src="/media/add.png"/></button>
                        <button id=${item.productCode} onclick="deleteProduct(this.id,this)" class="op_btn-add hidden"><img class="op_icon" src="/media/delete-icon.png"/></button>
                      </div>
                </div>`
    } 
    )}
        </div>
    </div>
    <div class="op_total">
        <h3>Total Price: EUR <span id="total-price">0</span></h3>   
    </div>
    <div class="op_payment">
        <h3 class="op_block-title">Pay by</h3>
        <button onclick="choosePayment(this.id, this)" id="CASH" class="op_btn-pay"><img src="/media/cash.png" class="op_icon"/> Cash</button>
        <button onclick="choosePayment(this.id, this)" id="MOLLIE" class="op_btn-pay op_btn-pay-active"><img src="/media/mollie.png" class="op_icon"/> Mollie</button>
    </div>
    <div class="op_order-controls">
    <button onclick="removeNode('.popap-wrapper')" class="op_btn-control cancell-btn">Cancel</button>
    <button onclick="sendOrder()" class="op_btn-control success-btn">Order</button>
</div>
    `
    $(node).append(html)
}

function choosePayment(id, elem) {
    id === "MOLLIE" ? _paymentMethod = 'MOLLIE' : _paymentMethod = 'CASH'
    $('.op_btn-pay').removeClass('op_btn-pay-active')
    $(elem).addClass('op_btn-pay-active')
}

function sendOrder() {
    axios(`/webshop/api/hairdresser/order/confirm?payment_method=${_paymentMethod}`)
        .then(() => showConfirm())
}

function addProduct(id, target) {
axios(`/webshop/api/hairdresser/order/order_item?productId=${id}&quantity=1`)
    .then(res => {
        $(target).toggleClass('hidden').next().toggleClass('hidden')
        calculatePrice(res.data)
    })
}

function deleteProduct(id, target) {
    axios(`/webshop/api/hairdresser/order/delete_item?productId=${id}`)
        .then(() => {
            $(target).toggleClass('hidden').prev().toggleClass('hidden')
            calculatePrice(res.data)
        })
}

function showConfirm() {
    const html = `
    <div class="op_confirm">
    <img src="/media/success.png"/>
    <h3 class="op_title">Thank you for your order</h3>
    <button onclick="removeNode('.popap-wrapper')" class="op_btn-control success-btn">OK</button>
</div> 
    `
    $('.popap-block').html(html)
}

function calculatePrice(data) {
    let initialValue = 0
    let sum = data.orderItems.reduce((accum, current) => accum + current.price, initialValue)
    $('#total-price').text(sum)
}
