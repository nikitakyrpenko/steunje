import React from 'react';
import {ValidatorForm, TextValidator} from 'react-material-ui-form-validator';
import PropTypes from 'prop-types';
import Autosuggest from 'react-autosuggest';
import match from 'autosuggest-highlight/match';
import parse from 'autosuggest-highlight/parse';

import Button from 'material-ui/Button';
import Chip from 'material-ui/Chip';
import {FormControlLabel} from 'material-ui/Form';
import Grid from 'material-ui/Grid';
import { MenuItem } from 'material-ui/Menu';
import Paper from 'material-ui/Paper';
import { withStyles } from 'material-ui/styles';
import Switch from 'material-ui/Switch';
import TextField from 'material-ui/TextField';
import Typography from 'material-ui/Typography';

import Language from '../../commons/language';
import dict,{category} from '../i18n';
import withCrud from '../../hoc/withCrud';

const styles = theme => ({
    root: theme.mixins.gutters({
        paddingTop: 16,
        paddingBottom: 16,
        marginBottom: 40,
    }),
    container: {
        flexGrow: 1,
        position: 'relative',
    },
    suggestionsContainerOpen: {
        position: 'absolute',
        marginTop: theme.spacing.unit,
        marginBottom: theme.spacing.unit * 3,
        left: 0,
        right: 0,
        zIndex: 10
    },
    suggestion: {
        display: 'block',
    },
    suggestionsList: {
        margin: 0,
        padding: 0,
        listStyleType: 'none',
    },
    textField: {
        width: '100%',
    },
    chip: {
        margin: theme.spacing.unit / 2,
    },
    row: {
        display: 'flex',
        justifyContent: 'center',
        flexWrap: 'wrap',
    }
});

function renderInput(inputProps) {
    const { classes, autoFocus, value, ref, ...other } = inputProps;
    return (
        <TextField
            autoFocus={autoFocus}
            className={classes.textField}
            value={value}
            inputRef={ref}
            InputProps={{
                classes: {
                    input: classes.input,
                },
                ...other,
            }}
        />
    );
}

function renderSuggestionsContainer(options) {
    const { containerProps, children } = options;
    return (
        <Paper {...containerProps} square>
            {children}
        </Paper>
    );
}

class CategoryComponent extends React.Component{
    constructor(props){
        super(props);
        this.state = {
            parentCategory: {},
            title: '',
            name: null,
            visible: true,
            value: '',
            suggestions: [],
            visibleTo: [],
            customers: []
        };

        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleChange = this.handleChange.bind(this);
        this.handleChangeSelect = this.handleChangeSelect.bind(this);
        this.handleSuggestionsFetchRequested = this.handleSuggestionsFetchRequested.bind(this);
        this.handleSuggestionsClearRequested = this.handleSuggestionsClearRequested.bind(this);
        this.onChange = this.onChange.bind(this);
        this.handleRequestDelete = this.handleRequestDelete.bind(this);
        this.onSuggestionSelected = this.onSuggestionSelected.bind(this);
        this.getSuggestions = this.getSuggestions.bind(this);
    }

    componentWillReceiveProps(nextProps){
        if(nextProps.data.title !== this.title){
            this.setState({
                title: nextProps.data.title,
                name: nextProps.data.id,
                visible: nextProps.data.visible,
                visibleTo: nextProps.data.visibleTo
            });
        }
    }

    renderSuggestion(suggestion, { query, isHighlighted }) {
        const matches = match(suggestion, query);
        const parts = parse(suggestion, matches);
        return (
            <MenuItem selected={isHighlighted} component="div">
                <div>
                    {parts.map((part, index) => {
                        return part.highlight ?
                            (<span key={index} style={{ fontWeight: 300 }}>{part.text}</span>)
                            :
                            (<strong key={index} style={{ fontWeight: 500 }}>{part.text}</strong>);
                    })}
                </div>
            </MenuItem>
        );
    }

    getSuggestions(value) {
        const inputValue = value.trim().toLowerCase();
        const inputLength = inputValue.length;
        let count = 0;
        return inputLength === 0
        ?[]
        :this.props.data.customers.filter(suggestion => {
            let keep = count < 20 && suggestion.toLowerCase().slice(0, inputLength) === inputValue;
            if (keep) {
                keep = !this.state.visibleTo.includes(suggestion);
                if (!keep)
                    return keep;
                count += 1;
            }
            return keep;
        });
    }

    getSuggestionValue(suggestion) {
        return suggestion
    }

    handleSuggestionsFetchRequested = ({ value }) => {
        this.setState({
            suggestions: this.getSuggestions(value),
        });
    };

    handleSuggestionsClearRequested = () => {
        this.setState({
            suggestions: [],
        });
    };

    onChange = (event, { newValue, method  }) => {
        this.setState({
            value: (method === 'click' || method === 'enter') ? '' : newValue
        });
    };

    onSuggestionSelected = (event, { suggestionValue}) => {
        this.state.visibleTo.push(suggestionValue)
    };

    handleChange = event => {
        this.setState({[event.target.name]: event.target.value});
    };

    handleChangeSelect = name => event => {
        this.setState({ [name]: Number(event.target.value) });
    };

    handleSubmit(event){
        event.preventDefault();
        this.props.name === 'new' 
            ? this.props.parentName 
                ? this.setState({parentCategory: {id: this.props.parentName}}, () => this.props.onCreate(this.state)) 
                : this.setState({parentCategory: null}, () => this.props.onCreate(this.state))
            : this.props.onUpdate(this.state);
    }

    handleRequestDelete = data => () => {
        const chipDatas = [...this.state.visibleTo];
        const chipToDelete = chipDatas.indexOf(data);
        chipDatas.splice(chipToDelete, 1);
        this.setState({
            visibleTo: chipDatas
        });
    };

    render(){
        const classes = this.props.classes;
        const lang = Language.getLangCode();

        return (
            <Paper className={classes.root} style={{height: '1000px'}}>
                <ValidatorForm
                    ref="form"
                    onSubmit={this.handleSubmit}
                    onError={errors => console.log(errors)}
                >
                    <Grid container justify="flex-end">
                        <Grid xs={12} item>
                            <Typography type="display1" component="h4" align="center">
                                {category.title[lang]}
                            </Typography>
                        </Grid>
                        <Grid xs={12} item>
                            <TextValidator
                                name="title"
                                label={category.nameCategory[lang]}
                                InputProps={{ placeholder: dict.uniqueValue[lang] }}
                                value={this.state.title}
                                fullWidth
                                margin="dense"
                                onChange={this.handleChange}
                                validators={['required']}
                                errorMessages={[dict.requiredFld[lang]]}
                                />
                            <FormControlLabel
                                label={category.visible[lang]}
                                control={
                                    <Switch
                                    checked={this.state.visible}
                                    onChange={(event, checked) => this.setState({ visible: checked })}
                                    />
                                }
                                />
                        </Grid>
                        {!this.state.visible &&
                            <Grid container>
                                <Grid xs={12} item>
                                    <Autosuggest
                                        theme={{
                                            container: classes.container,
                                            suggestionsContainerOpen: classes.suggestionsContainerOpen,
                                            suggestionsList: classes.suggestionsList,
                                            suggestion: classes.suggestion,
                                        }}
                                        renderInputComponent={renderInput}
                                        suggestions={this.state.suggestions}
                                        onSuggestionsFetchRequested={this.handleSuggestionsFetchRequested}
                                        onSuggestionsClearRequested={this.handleSuggestionsClearRequested}
                                        onSuggestionSelected={this.onSuggestionSelected}
                                        renderSuggestionsContainer={renderSuggestionsContainer}
                                        getSuggestionValue={this.getSuggestionValue}
                                        renderSuggestion={this.renderSuggestion}
                                        inputProps={{
                                            autoFocus: true,
                                            classes,
                                            placeholder: category.selectedUsers[lang],
                                            value: this.state.value,
                                            onChange: this.onChange,
                                        }}
                                    />
                                </Grid>
                                <Grid xs={12} item>
                                    <div className={classes.row}>
                                        {this.state.visibleTo.map(data => {
                                            return (
                                                <Chip
                                                    label={data}
                                                    key={data}
                                                    onRequestDelete={this.handleRequestDelete(data)}
                                                    className={classes.chip}
                                                />
                                            );
                                        })}
                                    </div>
                                </Grid>
                            </Grid>
                        }
                        <Grid item xs={2}>
                            <input type="button" id="sbm" style={{display: 'none'}} />
                            <label htmlFor="sbm">
                                <Button component="span" color="primary" onClick={this.handleSubmit}>
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

CategoryComponent.propTypes = {
    classes: PropTypes.object.isRequired,
};
  
export default withStyles(styles)(withCrud(CategoryComponent, `/webshop/api/category`));