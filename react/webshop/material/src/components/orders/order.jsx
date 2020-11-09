import React from 'react';
import axios from 'axios';
import NumberFormat from 'react-number-format';

import Button from 'material-ui/Button';
import Card, { CardActions, CardContent } from 'material-ui/Card';
import Grid from 'material-ui/Grid';
import Typography from 'material-ui/Typography';

import {devStage} from '../../hoc/withCrud';
import Language from '../../commons/language'
import {order} from '../i18n';


const lang = Language.getLangCode();
class OrderDetailsComponent extends React.Component{
    constructor(props) {
        super(props);
        this.state = {
            customer: {
                userCode: '',
                email: ''
            },
            deliveryContact: {
                addressLine: '',
                city: '',
                companyName: '',
                country: '',
                fax: '',
                firstName: '',
                id: '',
                phone: '',
                secondName: '',
                zipCode: ''
            }
        }
        this.handleChangeStatus = this.handleChangeStatus.bind(this)
    }

    componentDidMount(){
        axios.get('/webshop/api/order/' + this.props.id, devStage)
        .then(response => response.data)
        .then(data => this.setState({...data}));

    }

    handleChangeStatus(status){
        axios.get(`/webshop/api/order_status?order_id=${this.state.id}&order_status=${status}`, devStage)
            .then(response => response.data)
            .then(data => this.setState({status: status}));
    }

    render(){
        return (
            <Grid container>
                <Grid item xs={12}>
                    <Grid container align={'stretch'}>
                        <Grid item xs>
                            <Card>
                                <CardContent>
                                    <Typography type="caption" align="center" gutterBottom>
                                        {order.title[lang]}
                                    </Typography>
                                    <Typography type="headline" component="h2">
                                        {order.status[lang]}: {this.state.status}
                                    </Typography>
                                    <Typography type="body1">
                                        {order.transcationId[lang]}: {this.state.transactionId}
                                    </Typography>
                                
                                    <Typography type="body1" >
                                        {order.price[lang]}:<NumberFormat
                                                                value={this.state.price}
                                                                displayType='text'
                                                                prefix={'€ '}
                                                                decimalSeparator={','}
                                                                decimalPrecision={2}
                                                            />
                                    </Typography>
                                    <Typography type="body1" >
                                        {order.order_date[lang]}: {this.state.orderDate}
                                    </Typography>
                                    <Typography type="body1" >
                                        {order.deliveryType[lang]}: {this.state.deliveryType}
                                    </Typography>
                                    <Typography type="body1" >
                                        {order.paymentMethod[lang]}: {this.state.paymentMethod}
                                    </Typography>
                                    <Typography type="body1" >
                                        {order.comment[lang]}: {this.state.comment}
                                    </Typography>
                                </CardContent>
                                {this.state.status !== 'CLOSED' &&
                                    <CardActions>
                                        <Button onClick={() => this.handleChangeStatus('PAYED')} dense color='primary'>{order.product_payed[lang]}</Button>
                                        <Button onClick={() => this.handleChangeStatus('CLOSED')} dense color='primary'>{order.product_declained[lang]}</Button>
                                    </CardActions>
                                }
                            </Card>
                        </Grid>
                        <Grid item xs>
                            <DeliveryContactDetails data={this.state.deliveryContact} userCode={this.state.customer.userCode} email={this.state.customer.email}/>
                        </Grid>
                    </Grid>

                    <Grid container justify="flex-end">
                        <Grid item xs={12}>
                            <Typography type="caption" align="center" gutterBottom>
                                {order.items[lang]}
                            </Typography>
                            <Grid container>
                                {this.state.orderItems && this.state.orderItems.map(i => (
                                    <Grid item xs={12} key={i.product.id}>
                                        <OrderItemDetails {...i}/>
                                    </Grid>
                                ))}
                            </Grid>
                        </Grid>
                    </Grid>
                </Grid>
            </Grid>
        )
    }
}

export default OrderDetailsComponent;

const DeliveryContactDetails = (props) => {
    //console.log(props);
return (
<Card>
    <CardContent>
    <Typography type="caption" align="center" gutterBottom>
        {order.product_delivery[lang]}
    </Typography>

    <Typography type="body1">
        {order.product_user_code[lang]}: {props.userCode}
    </Typography>

    <Typography type="body1">
        {order.delivery_email[lang]}: {props.email}
    </Typography>

    <Typography type="body1">
        {order.delivery_firstName[lang]}: {props.data.firstName}
    </Typography>

    <Typography type="body1">
        {order.delivery_secondName[lang]}: {props.data.secondName}
    </Typography>

    <Typography type="body1">
        {order.delivery_companyName[lang]}: {props.data.companyName}
    </Typography>

    <Typography type="body1">
        {order.delivery_address[lang]}: {props.data.addressLine}
    </Typography>

    <Typography type="body1">
        {order.delivery_zipCode[lang]}: {props.data.zipCode}
    </Typography>

    <Typography type="body1">
        {order.delivery_country[lang]}: {props.data.country}
    </Typography>

    <Typography type="body1">
        {order.delivery_city[lang]}: {props.data.city}
    </Typography>

    <Typography type="body1">
        {order.delivery_fax[lang]}: {props.data.fax}
    </Typography>

    <Typography type="body1">
        {order.delivery_phone[lang]}: {props.data.phone}
    </Typography>
    </CardContent>
</Card>
)};

const OrderItemDetails = (props) => (
<Card>
    <CardContent>
    <Typography>
        {order.product_code[lang]}: 
        {props.product.id !== 'Not found' ? props.product.id : order.order_code_warning[lang]}
    </Typography>
    <Typography noWrap>
        {order.product_title[lang]}: 
        { props.product.title }
    </Typography>
    <Typography>
        {order.product_quantity[lang]}: 
        { props.quantity }
    </Typography>
    <Typography >
        {order.product_price[lang]}: 
        <NumberFormat
            value={props.price }
            displayType='text'
            prefix={'€ '}
            decimalSeparator={','}
            decimalPrecision={2}
        />
    </Typography>
    <Typography>
        {order.product_discount[lang]}: 
        <NumberFormat
            value={props.discount *100 }
            displayType='text'
            suffix={' %'}
            decimalSeparator={','}
            decimalPrecision={2}
        />
    </Typography>
    </CardContent>
</Card>
)