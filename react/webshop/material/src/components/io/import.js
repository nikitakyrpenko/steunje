import React from 'react';
import axios from 'axios';

import Button from 'material-ui/Button';
import { FormLabel, FormControl, FormControlLabel } from 'material-ui/Form';
import Grid from 'material-ui/Grid';
import Paper from 'material-ui/Paper';
import { CircularProgress, LinearProgress } from 'material-ui/Progress';
import Radio, { RadioGroup } from 'material-ui/Radio';
import { withStyles } from 'material-ui/styles';
import Table, { TableBody, TableCell, TableHead, TableRow } from 'material-ui/Table';
import Typography from 'material-ui/Typography';
import CheckIcon from 'material-ui-icons/Check';
import FileUploadIcon from 'material-ui-icons/FileUpload';
import green from 'material-ui/colors/green';
import red from 'material-ui/colors/red';


import withCrud, {devStage} from '../../hoc/withCrud';
import Language from '../../commons/language';
import dict, {importExport} from '../i18n';

const styles = theme => ({
    root: theme.mixins.gutters({
        paddingTop: 16,
        paddingBottom: 16,
    }),
    button: {
        margin: theme.spacing.unit,
    },
    input: {
        display: 'none',
    },
    wrapper: {
        position: 'relative',
    },
    successButton: {
        backgroundColor: green[500],
        '&:hover': {
          backgroundColor: green[700],
        },
    },
    failButton: {
        backgroundColor: red[500],
        '&:hover': {
          backgroundColor: red[700],
        },
    },
    progress: {
        color: green[500],
        position: 'absolute',
        top: -2,
        left: -2,
      },
});

class ImportComponent extends React.Component{
    constructor(props){
        super(props);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleChange = this.handleChange.bind(this);
        this.handleFileChange = this.handleFileChange.bind(this);
        this.intervalRun = this.intervalRun.bind(this);
        this.state = {
            file: {name: ''}, 
            type: '',
            success: false,
            loading: false,
            failed: false,
            timer: 0
        };
       
    }

    intervalRun() {
        this.setState({timer: setInterval(this.props.read, 600)})
    }
    
    intervalStop() {
        clearInterval(this.state.timer);
    }

    handleChange(event){
        this.setState({[event.target.name]: event.target.value, success: false});
    }

    handleFileChange(event){
        this.setState({[event.target.name]: event.target.files[0], success: false});
    }

    handleSubmit(event){
        event.preventDefault();
        const {loading, success, file, type} = this.state;
        if(success && !loading)
            return;
        this.setState({loading: true});

        let data = new FormData();
        data.append('file', file);
        data.append('importType', type);
        this.intervalRun();
        axios
            .request({
                method: 'post',
                url: '/webshop/api/import',
                headers: {
                    'accept': 'application/json',
                    'content-type': 'multipart/form-data',
                },
                auth: devStage.auth,
                data: data
            })
            .then(res => res.data)
            .then(data => data.logKeys.join('&logId='))
            .then(path => this.props.read(`logs?logId=${path}`))
            .then(() => this.setState({loading: false, success:true}))
            .then(() => this.intervalStop())
            .catch(err => {
                this.intervalStop();
                this.setState({loading: false, success:false, failed: true});
            })
    }

    render(){
        const lang = Language.getLangCode();
        const classes = this.props.classes;
        const {loading, success, failed} = this.state;
        let buttonClass = '';
        if (success) {
            buttonClass = classes.successButton;
        }else if(failed){
            buttonClass = classes.failButton;
        }
        return(
        <Grid container>
            <Grid xs={12} item>
                <Paper className={classes.root}>
                        <Typography type="display1" component="h4" align="center">
                            {importExport.header[lang]}
                        </Typography>
                        <form onSubmit={this.handleSubmit} className="import-form">
                            <Grid item xs>
                                <FormControl component="fieldset" >
                                    <FormLabel component="legend" required>{importExport.importType[lang]}</FormLabel>
                                        <RadioGroup
                                            aria-label="type"
                                            name="type"
                                            value={this.state.type}
                                            onChange={this.handleChange}
                                            >
                                            <FormControlLabel value="products" control={<Radio />} label={importExport.imProducts[lang]} />
                                            <FormControlLabel value="users" control={<Radio />} label={importExport.imUsers[lang]} />
                                            <FormControlLabel value="dis-standard" control={<Radio />} label={importExport.imPriseStandart[lang]} />
                                            <FormControlLabel value="dis-product" control={<Radio />} label={importExport.imPriseProduct[lang]} />
                                            <FormControlLabel value="dis-group_client" control={<Radio />} label={importExport.imPriseGroup[lang]} />
                                            <FormControlLabel value="dis-product_client" control={<Radio />} label={importExport.imProduct2Client[lang]} />
                                        </RadioGroup>
                                </FormControl>
                            </Grid>
                            <Grid item xs={12}>
                                <input type="file" accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" onChange={this.handleFileChange} name="file" id="file" className={classes.input}/>
                                <label htmlFor="file">
                                    <Button raised component="span" required color="primary">
                                        {dict.choose[lang]}
                                    </Button>
                                </label>
                            </Grid>
                            <Grid container justify="flex-end" align="center">
                                <Grid item xs={11}>
                                    <LinearProgress 
                                        mode="determinate" 
                                        value={!this.props.data.total || this.props.data.total === 0 ? 0 : 100 * this.props.data.progress / this.props.data.total} 
                                        //valueBuffer={this.props.data.total === 0 ? 0 : 100 * this.props.data.buffer / this.props.data.total} 
                                        />
                                </Grid>
                                <Grid item xs={1} >
                                    <div className={classes.wrapper}>
                                            <Button fab color="primary" className={buttonClass} disabled={this.state.type==='' || this.state.file === undefined ||this.state.file.name === ''} type='submit'>
                                                {success ? <CheckIcon /> : <FileUploadIcon />}
                                            </Button>
                                        {loading && <CircularProgress size={60} className={classes.progress}/>}
                                    </div>
                                </Grid>
                            </Grid>
                        </form>
                </Paper>
            </Grid>
            {this.props.data && Array.isArray(this.props.data) &&
                <Grid xs={12} item>
                    <Paper className={classes.root}>
                        <Table>
                            <TableHead>
                            <TableRow>
                                <TableCell>Action</TableCell>
                                <TableCell>Result</TableCell>
                                <TableCell>Details</TableCell>
                            </TableRow>
                            </TableHead>
                            <TableBody>
                            {this.props.data.map((n, index) => {
                                return (
                                <TableRow key={index}>
                                    <TableCell>{n.event}</TableCell>
                                    <TableCell>{n.result}</TableCell>
                                    <TableCell>{n.description}</TableCell>
                                </TableRow>
                                );
                            })}
                            </TableBody>
                        </Table>
                    </Paper>
                </Grid>
            }
        </Grid>
        )
    }
}

export default withStyles(styles)(withCrud(ImportComponent, '/webshop/api/import'))