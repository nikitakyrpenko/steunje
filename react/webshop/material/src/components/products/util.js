const buildBackEndProduct = (props) => {
    const product = {};
    product.id = props.id;
    product.orderCode = props.id;
    product.productNumber = props.productNumber;
    product.title = props.title;
    product.ean = props.EAN;
    product.articleGroup = {group: props.group};
    product.priceIncludeVat = formatPrice(props.priceIncVat);
    product.priceExcludeVat = formatPrice(props.priceExcVat);
    product.stock = props.stock;
    product.stockMin = props.stockMin;
    product.visible = props.visible;
    product.retailPriceExcludeVat = formatPrice(props.retailPrice);
    product.content = props.content;
    product.color = props.color;
    product.brand = props.brand;
    product.keepStock = props.keepStock;
    product.multipleOf = props.multipleOf;
    product.matrixCategory = {title: props.matrix};
    product.matrixValue = props.matrixValue;
    product.sale = props.sale;
    product.description = props.description;

    if (props.category)
        product.category = props.category;

    return product;
}

const formatPrice = str => (!str || str === '') ? null : typeof str === 'number' ? str : parseFloat(str.replace(',', '.'));


export {buildBackEndProduct};