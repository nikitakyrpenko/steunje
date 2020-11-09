import React from 'react';
import axios from 'axios';
import NumberFormat from 'react-number-format';
import {ValidatorForm, TextValidator} from 'react-material-ui-form-validator';

import Button from 'material-ui/Button';
import {FormControlLabel} from 'material-ui/Form';
import Grid from 'material-ui/Grid';
import Menu, { MenuItem } from 'material-ui/Menu';
import Paper from 'material-ui/Paper';
import {withStyles } from 'material-ui/styles';
import Switch from 'material-ui/Switch';
import TextField from 'material-ui/TextField';
import Typography from 'material-ui/Typography';

import withCrud, {devStage} from '../../hoc/withCrud';
import {buildBackEndProduct} from './util';
import Language from '../../commons/language';
import dict, {product} from '../i18n';

const styles = theme => ({
    root: theme.mixins.gutters({
      paddingTop: 16,
      paddingBottom: 16,
      marginBottom: 40
    })
});

class ProductComponent extends React.Component{
    constructor(props){
        super(props);
        this.state = {
            id: '',
            productNumber: '',
            title: '',
            EAN: '',
            group: '',
            priceIncVat: '',
            priceExcVat: '',
            stock: 1,
            stockMin: 1,
            visible: true,
            retailPrice: '',
            content: '',
            color: '',
            brand: '',
            keepStock: true,
            multipleOf: 1,
            matrix: '',
            matrixValue: '',
            category: undefined,
            sale: false,
            description: '',

            anchorEl: undefined,
            open: false,
            dataForMenu: [],
            menuState: '',

            fileImageName: '',
            filePdfName: '',
            fileImage: null,
            filePdf: null,
            image: 0
        };

        this.handleChange = this.handleChange.bind(this);
        this.handleFileChange = this.handleFileChange.bind(this);
        this.handleSendFile = this.handleSendFile.bind(this);
        this.handlePriceChange = this.handlePriceChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.writeBytesToPreview = this.writeBytesToPreview.bind(this);
    }

    componentWillReceiveProps(nextProps){
        if(nextProps.data.title !== this.title)
            this.setState({...nextProps.data});
    }

    handleChange = event => {
       this.setState({[event.target.id]: event.target.value }
            // , () => console.log(this.state)
        );
    };

    handlePriceChange = (event, values) => {
        const {value} = values;
        this.setState({[event.target.id]: value}
        );
    };

    handleClick = (event, type) => {
        const target = event.currentTarget;
        axios
        .get(`/webshop/api/product_attribute?type=${type}`, devStage)
        .then(response => response.data)
        .then(data => {
            this.setState({ open: true, anchorEl: target, dataForMenu: data, menuState: type});
        });
    };

    handleRequestClose = () => {
        this.setState({ open: false,  anchorEl: undefined});
    };

    handleRequestSelect = (ev, index) => {
        this.setState({[this.state.menuState]: ev.target.attributes.name.value}
        );
        this.handleRequestClose();
    };

    handleSubmit(event){
        event.preventDefault();
        if(this.state.fileImage)
            this.handleSendFile(this.state.fileImage, this.state.id);
        if(this.state.filePdf)
            this.handleSendFile(this.state.filePdf, this.state.id);
        this.props.name === 'new'
        ? this.setState({category: {id: this.props.parentName}}, () => this.props.onCreate(buildBackEndProduct(this.state)))
        : this.props.onUpdate ? this.props.onUpdate(buildBackEndProduct(this.state)) : this.props.update(buildBackEndProduct(this.state));
    }

    writeBytesToPreview(imgBytes){
        this.setState({image: imgBytes})
    }

    handleFileChange(event){
        let file = event.target.files[0];
        const inputName = event.target.name;
        if (inputName === 'fileImage'){
            const preview = this.writeBytesToPreview;
            const reader = new FileReader();
            reader.onload = function (e) {
                preview(e.target.result);
            };
            reader.readAsDataURL(file);
        }
        this.setState({
            [inputName]: file,
            [inputName + 'Name']: file.name
        });
    }

    handleSendFile(file, name){
        let obj = {};
        obj.auth = devStage.auth;
        obj.headers = {'accept': 'application/json', 'content-type': 'application/x-www-form-urlencoded'};

        let data = new FormData();
        data.append('file', file);
        data.append('filename', name);
        axios
            .post('/webshop/api/products/attachment', data, obj)
            .then(response => response.data)
            .then(data => {});
    }


    render(){
        const classes = this.props.classes;
        const lang = Language.getLangCode();
        return (
            <Grid container justify="flex-end">
                <Grid xs={12} item>
                    <Paper className={classes.root} >
                        <ValidatorForm
                            ref="form"
                            onSubmit={this.handleSubmit}
                            onError={errors => console.log(errors)}
                        >
                        <Grid container justify="flex-end">
                            <Grid xs={12} item>
                                <Typography type="display1" component="h4" align="center">
                                    {product.edit[lang]}
                                </Typography>
                            </Grid>
                            <Grid xs={12} item>
                                <Menu
                                    id="simple-menu"
                                    anchorEl={this.state.anchorEl}
                                    open={this.state.open}
                                    onRequestClose={this.handleRequestClose}
                                    >
                                    {this.state.dataForMenu.map(option => (<MenuItem key={option} name={option} onClick={this.handleRequestSelect}>{option}</MenuItem>))}
                                </Menu>
                                <TextValidator
                                    id="id"
                                    name="id"
                                    label={product.orderCode[lang] + '*'}
                                    InputProps={{ placeholder: dict.uniqueValue[lang] }}
                                    value={this.state.id}
                                    fullWidth
                                    margin="dense"
                                    onChange={this.handleChange}
                                    validators={['required']}
                                    errorMessages={[dict.requiredFld[lang]]}
                                    />
                                <TextValidator
                                    id="productNumber"
                                    name="productNumber"
                                    label={product.productNumber[lang] + '*'}
                                    value={this.state.productNumber}
                                    fullWidth
                                    margin="dense"
                                    onChange={this.handleChange}
                                    validators={['required']}
                                    errorMessages={[dict.requiredFld[lang]]}
                                    />
                                <TextValidator
                                    id="title"
                                    name="title"
                                    label={product.title[lang] + '*'}
                                    value={this.state.title}
                                    fullWidth
                                    margin="dense"
                                    onChange={this.handleChange}
                                    validators={['required']}
                                    errorMessages={[dict.requiredFld[lang]]}
                                    />
                                <TextValidator
                                    id="EAN"
                                    name="EA123N"
                                    label={product.ean[lang] + '*'}
                                    value={this.state.EAN}
                                    fullWidth
                                    margin="dense"
                                    onChange={this.handleChange}
                                    validators={['matchRegexp:^[0-9]{13,14}$', 'required']}
                                    errorMessages={[product.wrongEAN[lang], dict.requiredFld[lang]]}
                                    />
                                <TextField
                                    id="fileName"
                                    label={product.changeUrl[lang] + `${this.state.id}.jpg`}
                                    value={`${this.state.fileImageName}`}
                                    margin="dense"
                                    disabled
                                    fullWidth
                                />
                                <input
                                    style={{display:'none'}}
                                    accept="image/jpeg, image/png"
                                    className={classes.input}
                                    name="fileImage"
                                    id="fileImage"
                                    type="file"
                                    onChange={this.handleFileChange}
                                    value={this.state.file}
                                />
                                <label htmlFor="fileImage">
                                    <Button
                                        raised
                                        component="span"
                                        color="primary"
                                        className={classes.button}
                                    >
                                        {dict.choose[lang]}
                                    </Button>
                                </label>
                                <br/>
                                <img
                                    src={this.state.image === 0 ? '' : this.state.image}
                                    alt=""
                                    style={{maxWidth: '400px'}}
                                />
                                {/*<br/>*/}
                                {/*<TextField*/}
                                    {/*id="fileName"*/}
                                    {/*label={product.urlPDF[lang]}*/}
                                    {/*value={this.state.filePdfName}*/}
                                    {/*margin="dense"*/}
                                    {/*disabled*/}
                                    {/*fullWidth*/}
                                {/*/>*/}
                                {/*<input*/}
                                    {/*style={{display:'none'}}*/}
                                    {/*type="file"*/}
                                    {/*accept="application/pdf"*/}
                                    {/*onChange={this.handleFileChange}*/}
                                    {/*name="filePdf"*/}
                                    {/*id="filePDF"*/}
                                    {/*value={this.state.file}*/}
                                {/*/>*/}
                                {/*<label htmlFor="filePDF">*/}
                                    {/*<Button*/}
                                        {/*type="button"*/}
                                        {/*raised*/}
                                        {/*component="span"*/}
                                        {/*color="primary"*/}
                                        {/*className={classes.button}>*/}
                                        {/*{product.uploadPDF[lang]}*/}
                                    {/*</Button>*/}
                                {/*</label>*/}
                                <br/>
                                <TextValidator
                                    id="group"
                                    name="group"
                                    label={product.group[lang]}
                                    value={this.state.group}
                                    margin="dense"
                                    disabled
                                    validators={['required']}
                                    errorMessages={[dict.requiredFld[lang]]}
                                    />&nbsp;&nbsp;&nbsp;
                                <Button
                                    aria-owns={this.state.open ? 'simple-menu' : null}
                                    aria-haspopup="true"
                                    color="primary"
                                    raised
                                    onClick={(ev) => this.handleClick(ev, 'group')}
                                    >
                                    {dict.choose[lang]}
                                </Button>
                                <NumberFormat
                                    value={this.state.priceIncVat}
                                    prefix={'€'}
                                    decimalSeparator={','}
                                    customInput={TextValidator}
                                    id="priceIncVat"
                                    name="priceIncVat"
                                    label={product.priceWithVAT[lang] + '*'}
                                    fullWidth
                                    margin="dense"
                                    onChange={this.handlePriceChange}
                                    validators={['required']}
                                    errorMessages={[dict.requiredFld[lang]]}
                                    />
                                <NumberFormat
                                    value={this.state.priceExcVat}
                                    prefix={'€'}
                                    decimalSeparator={','}
                                    customInput={TextValidator}
                                    id="priceExcVat"
                                    label={product.priceExVAT[lang] + '*'}
                                    fullWidth
                                    name="priceExcVat"
                                    margin="dense"
                                    onChange={this.handlePriceChange}
                                    validators={['required']}
                                    errorMessages={[dict.requiredFld[lang]]}
                                    />
                                <NumberFormat
                                    value={this.state.retailPrice}
                                    prefix={'€'}
                                    type="text"
                                    decimalSeparator={','}
                                    customInput={TextField}
                                    id="retailPrice"
                                    label={product.retailPrice[lang]}
                                    fullWidth
                                    margin="dense"
                                    onChange={this.handlePriceChange}
                                    />
                                <TextValidator
                                    id="stock"
                                    name="stock"
                                    label={product.stock[lang]}
                                    value={this.state.stock}
                                    fullWidth
                                    margin="dense"
                                    onChange={this.handleChange}
                                    validators={['required']}
                                    errorMessages={[dict.requiredFld[lang]]}
                                    />
                                <TextValidator
                                    id="stockMin"
                                    name="stockMin"
                                    label={product.minStock[lang]}
                                    value={this.state.stockMin}
                                    fullWidth
                                    margin="dense"
                                    onChange={this.handleChange}
                                    validators={['required']}
                                    errorMessages={[dict.requiredFld[lang]]}
                                    />
                                <TextField
                                    name="description"
                                    label={product.productDescription[lang]}
                                    value={this.state.description}
                                    id="description"
                                    rows="6"
                                    fullWidth
                                    onChange={this.handleChange}
                                    multiline
                                />
                                <FormControlLabel
                                    label={product.productAvailable[lang]}
                                    control={
                                        <Switch
                                        checked={this.state.visible}
                                        onChange={(event, checked) => this.setState({ visible: checked })}
                                        />
                                    }
                                    />
                                <br/>
                                <FormControlLabel
                                    label={product.keepStock[lang]}
                                    control={
                                        <Switch
                                        checked={this.state.keepStock}
                                        onChange={(event, checked) => this.setState({ keepStock: checked })}
                                        />
                                    }
                                    />
                                <br/>
                                <FormControlLabel
                                    label={product.sale[lang]}
                                    control={
                                        <Switch
                                        checked={this.state.sale}
                                        onChange={(event, checked) => this.setState({ sale: checked })}
                                        />
                                    }
                                    />
                                <TextField
                                    id="content"
                                    label={product.content[lang]}
                                    value={this.state.content}
                                    fullWidth
                                    margin="dense"
                                    onChange={this.handleChange}
                                    />
                                <TextField
                                    id="color"
                                    label={product.color[lang]}
                                    value={this.state.color}
                                    fullWidth
                                    margin="dense"
                                    onChange={this.handleChange}
                                    />
                                <TextField
                                    id="brand"
                                    label={product.brand[lang]}
                                    value={this.state.brand}
                                    fullWidth
                                    margin="dense"
                                    onChange={this.handleChange}
                                    />
                                <TextField
                                    id="multipleOf"
                                    label={product.multiple[lang]}
                                    value={this.state.multipleOf}
                                    fullWidth
                                    margin="dense"
                                    onChange={this.handleChange}
                                    />
                                <TextField
                                    id="matrix"
                                    label={product.matrixCategory[lang]}
                                    value={this.state.matrix}
                                    margin="dense"
                                    disabled
                                    />
                                <Button
                                    aria-owns={this.state.open ? 'simple-menu' : null}
                                    aria-haspopup="true"
                                    onClick={(ev) => this.handleClick(ev, 'matrix')}
                                    >
                                    {dict.choose[lang]}
                                </Button>
                                <br/>
                                <TextField
                                    id="matrixValue"
                                    label={product.matrixValue[lang]}
                                    value={this.state.matrixValue}
                                    margin="dense"
                                    fullWidth
                                    onChange={this.handleChange}
                                    />
                            </Grid>
                                <input style={{display:'none'}} type="submit" id="sbm" />
                                <label htmlFor="sbm">
                                    <Button component="span" raised color="primary">
                                        {dict.save[lang]}
                                    </Button>
                                </label>
                        </Grid>
                    </ValidatorForm>
                    </Paper>
                </Grid>
            </Grid>
        )
    }
}


export default withStyles(styles)(withCrud(ProductComponent, `/webshop/api/product`));