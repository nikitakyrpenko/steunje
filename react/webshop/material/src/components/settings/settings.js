import React from 'react'
import {ValidatorForm, TextValidator} from 'react-material-ui-form-validator';

import Button from 'material-ui/Button';
import {FormControlLabel} from 'material-ui/Form';
import Grid from 'material-ui/Grid';
import Paper from 'material-ui/Paper';
import { withStyles } from 'material-ui/styles';
import Switch from 'material-ui/Switch';
import Typography from 'material-ui/Typography';

import Language from '../../commons/language';
import dict, {settings} from '../i18n';
import withCrud from '../../hoc/withCrud';

const styles = theme => ({
    root: theme.mixins.gutters({
        paddingTop: 16,
        paddingBottom: 16,
    }),
    button: {
        margin: theme.spacing.unit,
    }
});

class GeneralSettings extends React.Component{
    constructor(props) {
        super(props);

        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleChange = this.handleChange.bind(this);
        this.handleSwitchChange = this.handleSwitchChange.bind(this);
    }

    componentWillReceiveProps(nextProps){
        nextProps.data.forEach(el => {
            if (el.value === 'true' || el.value === 'false')
                el.value = el.value ==='true';

             this.setState({[el.name]: el});
        });
    }

    handleChange = (event, ob) => {
        ob.value = event.target.value;
        this.setState({[event.target.name]: ob});
     };

    handleSwitchChange = el => (event, checked) => {
        // console.log(name);
        // console.log(checked);
        el.value = !el.value;
        this.setState({ [event.target.name]: el });
    };

    handleSubmit(event){
        event.preventDefault();
        this.props.create(Object.values(this.state));
    }

    render(){
        const classes = this.props.classes;
        const lang = Language.getLangCode();

        return (
            <Paper className={classes.root} >
                <ValidatorForm
                    ref="form"
                    onSubmit={this.handleSubmit}
                    onError={errors => console.log(errors)}
                >
                    <Grid container justify="flex-end">
                        <Grid xs={12} item>
                            <Typography type="display1" component="h4" align="center">
                                {settings.settings[lang]}
                            </Typography>
                        </Grid>
                        {this.state && Object.keys(this.state).map(s => {
                            return(
                            (this.state[s].value === true || this.state[s].value === false ?  [
                                        <Grid container key={this.state[s].id}>
                                            <Grid xs={3} item>
                                                <Typography  type="body1" component="p" style={{lineHeight: '48px'}}>
                                                    {settings[this.state[s].title] ? settings[this.state[s].title][lang] : this.state[s].description}
                                                </Typography>
                                            </Grid>
                                            <Grid xs={9} item>
                                                <FormControlLabel
                                                    label=''
                                                    control={
                                                        <Switch
                                                            name = {this.state[s].name}
                                                            checked={this.state[s].value}
                                                            onChange={this.handleSwitchChange(this.state[s])}
                                                        />
                                                    }
                                                />
                                            </Grid>
                                        </Grid>
                                    ]:
                                    <Grid container key={this.state[s].id}>
                                        <Grid xs={3} item>
                                            <Typography  type="body1" component="p" style={{lineHeight: '32px'}}>
                                                {settings[this.state[s].title] ? settings[this.state[s].title][lang] : this.state[s].description}
                                            </Typography>
                                        </Grid>
                                        <Grid xs={9} item>
                                            <TextValidator
                                                key={this.state[s].name}
                                                name={this.state[s].name}
                                                InputProps={{ placeholder: 'Value'}}
                                                value={this.state[s].value}
                                                onChange={ev => this.handleChange(ev, this.state[s])}
                                                validators={['required']}
                                                errorMessages={[dict.requiredFld[lang]]}
                                                fullWidth
                                            />
                                        </Grid>
                                    </Grid>
                                 )
                            )
                        })
                        }
                      
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
        )
    }
}

export default withStyles(styles)(withCrud(GeneralSettings, '/webshop/api/settings'))