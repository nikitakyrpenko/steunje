import React from 'react';

import List,{ListItem, ListItemText} from 'material-ui/List';
import Paper from 'material-ui/Paper';
import Typography from 'material-ui/Typography';
import {Link} from 'react-router-dom';

import {products} from '../i18n'
import withCrud from '../../hoc/withCrud';
import Language from '../../commons/language';

class SalesListComponent extends React.Component{
    constructor(props){
        super(props);
        this.state = {
            overlapped: false,
            thisName: 'new',
        };

        this.handleDelete = this.handleDelete.bind(this);

    }

    handleDelete(id){
        this.props.delete(id)
    }

    render(){
        const lang = Language.getLangCode();
        return(
            <Paper>
                <Typography type="display1" component="h4" align="center">
                    {products.title[lang]}
                </Typography>
                <List>
                    {this.props.data.map(product => (

                    <ListItem button component={Link} to={`/admin/webshop/product/${product.id}`} key={product.id}>
                        <ListItemText primary={product.title}/>
                    </ListItem>
                    ))}
                </List>
            </Paper>
        )
    }
}

export default withCrud(SalesListComponent, '/webshop/api/sales');