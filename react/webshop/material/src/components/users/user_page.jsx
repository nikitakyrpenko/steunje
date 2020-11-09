import React from 'react'
import {ValidatorForm, TextValidator} from 'react-material-ui-form-validator'

import Button from 'material-ui/Button'
import {FormControlLabel} from 'material-ui/Form'
import Grid from 'material-ui/Grid'
import Paper from 'material-ui/Paper'
import {withStyles} from 'material-ui/styles'
import Switch from 'material-ui/Switch'
import Typography from 'material-ui/Typography'

import withCrud from '../../hoc/withCrud'
import Language from '../../commons/language'
import dict, {users} from '../i18n';

const lang = Language.getLangCode();
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
    }
});

class UserComponent extends React.Component{
    constructor(props){
        super(props);
        this.state = {
            email: '',
            userCode: '',
            postPayAllowed: false,
            user: {guid: ''}
        };

        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handlePasswordChange = this.handlePasswordChange.bind(this);
    }
   
    componentWillReceiveProps(nextProps){
        this.setState({...nextProps.data});
    }

    handleSubmit(event){
        event.preventDefault();
        const id = this.props.match.params.id;
        if(id === 'new'){
            this.props.create(this.state);
        }else{
            this.setState({id: this.props.match.params.id}, () => this.props.update(this.state));
        }
    }

    handleChange = event => {
        this.setState({[event.target.id]: event.target.value });
    };

    handlePasswordChange = e => this.setState({user: {guid: e.target.value}})

    render(){
        const {classes} = this.props;
        return (
            <Grid container justify="flex-end">
                <Grid xs={12} item>
                    <Paper className={classes.root} >
                        <Typography type="display1" component="h4" align="center">
                            {users.user_title[lang]}
                        </Typography>
                        <ValidatorForm
                            ref="form"
                            onSubmit={this.handleSubmit}
                            onError={errors => console.log(errors)}
                        >
                            <Grid container justify="flex-end">
                                <Grid xs={4} item>
                                    <Typography style={{lineHeight: '32px'}} type="body1" component="p">
                                        {users.user_email[lang]}
                                    </Typography>
                                </Grid>
                                <Grid xs={8} item>
                                    <TextValidator
                                        id="email"
                                        name="email"
                                        InputProps={{ placeholder: 'E-mail'}}
                                        value={this.state.email}
                                        onChange={this.handleChange}
                                        validators={['required', 'isEmail']}
                                        errorMessages={[dict.requiredFld[lang], 'email is not valid']}
                                        fullWidth
                                    />
                                </Grid>
                                <Grid xs={4} item>
                                    <Typography style={{lineHeight: '32px'}} type="body1" component="p">
                                        {users.user_login[lang]}
                                    </Typography>
                                </Grid>
                                <Grid xs={8} item>
                                    <TextValidator
                                        id="userCode"
                                        name="userCode"
                                        InputProps={{ placeholder: users.user_code[lang]}}
                                        value={this.state.userCode}
                                        onChange={this.handleChange}
                                        validators={['required']}
                                        errorMessages={[dict.requiredFld[lang]]}
                                        fullWidth
                                        disabled={this.props.match && this.props.match.params.id !== 'new'}
                                    />
                                </Grid>
                                {this.props.match && this.props.match.params.id === 'new' &&
                                <Grid xs={12} item>
                                    <Grid container>
                                        <Grid xs={4} item>
                                            <Typography style={{lineHeight: '32px'}} type="body1" component="p">
                                                {users.user_pwd[lang]}
                                            </Typography>
                                        </Grid>
                                        <Grid xs={8} item>
                                            <TextValidator
                                                type="password"
                                                name="pwd"
                                                onChange={this.handlePasswordChange}
                                                value={this.state.user.guid}
                                                validators={['required']}
                                                errorMessages={[dict.requiredFld[lang]]}
                                                fullWidth
                                            />
                                        </Grid>
                                    </Grid>
                                </Grid>
                                }
                                <Grid xs={4} item>
                                    <Typography style={{lineHeight: '48px'}} type="body1" component="p">
                                        {users.user_paymentType[lang]}
                                    </Typography>
                                </Grid>
                                <Grid xs={8} item>
                                    <FormControlLabel
                                        label=''
                                        control={
                                            <Switch
                                                checked={this.state.postPayAllowed}
                                                onChange={(event, checked) => this.setState({ postPayAllowed: checked })}
                                            />
                                        }
                                    />
                                </Grid>
                                <Grid item xs={2}>
                                    <input style={{display:'none'}} type="submit" id="sbm" />
                                    <label htmlFor="sbm">
                                        <Button component="span" color="primary">
                                            {dict.save[lang]}
                                        </Button>
                                    </label>
                                </Grid>
                            </Grid>
                        </ValidatorForm>
                    </Paper>
                </Grid>
            </Grid>
        )
    }
}

export default withStyles(styles)(withCrud(UserComponent, `/webshop/api/user`));