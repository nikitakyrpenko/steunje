import React from 'react'

import Button from 'material-ui/Button'
import Grid from 'material-ui/Grid'
import List,{ListItem, ListItemText} from 'material-ui/List';
import Paper from 'material-ui/Paper'
import {withStyles} from 'material-ui/styles'
import TextField from 'material-ui/TextField'
import Typography from 'material-ui/Typography'
import {Link} from 'react-router-dom';
import ExpandMoreIcon from 'material-ui-icons/ExpandMore'

import withCrud from '../../hoc/withCrud'
import Language from '../../commons/language'
import dic, {products} from '../i18n';

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

class SearchProductComponent extends React.Component{
    constructor(props){
        super(props);
        this.state = {
            data: [],
            searchString: '',
            i: 1
        }

        this.handleChange = this.handleChange.bind(this);
    }

    handleChange(event) {
        this.setState({searchString: event.target.value});
    }

    render(){
        const {classes} = this.props;
        let libraries = this.props.data,
            searchString = this.state.searchString.trim();
        if(searchString.length > 0) {
            libraries = libraries.filter((el) => el.productNumber.match(searchString) || el.title.match(new RegExp(searchString, 'i')))
        }
        libraries = libraries.slice(0,20*this.state.i);
        const articleElements = libraries.map(product =>
            <ListItem
                button
                component={Link}
                to={`/admin/webshop/product/${product.id}`}
                key={product.id}>
                <ListItemText primary={product.title} secondary={product.productNumber}/>
            </ListItem>
        )
        return (
            <Grid item xs>
                <Grid xs={12} item>
                    <Paper className={classes.root}>
                        <Grid container justify='flex-end'>
                            <Grid xs={12} item>
                                <Typography type="display1" component="h4" align="center">
                                    {products.search_title[lang]}
                                </Typography>
                            </Grid>
                            <Grid xs={12} item>
                                <TextField
                                    id="search"
                                    type="text"
                                    label={products.search_title[lang]}
                                    value={this.state.searchString}
                                    fullWidth
                                    margin="dense"
                                    onChange={this.handleChange}
                                />
                            </Grid>
                            <Grid xs={12} item>
                                <List>
                                    {articleElements}
                                </List>
                            </Grid>
                            <Button onClick={()=>this.setState({i: this.state.i+1})} color='primary' raised>
                                <ExpandMoreIcon/>{dic.loadMore[lang]}<ExpandMoreIcon/>
                            </Button>
                        </Grid>
                    </Paper>
                </Grid>
            </Grid>
        )
    }
}

export default withStyles(styles)(withCrud(SearchProductComponent, `/webshop/api/product_codes`));
