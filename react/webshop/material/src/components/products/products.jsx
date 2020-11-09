import React from 'react';
import CSSTransitionGroup from 'react-transition-group/CSSTransitionGroup';

import Button from 'material-ui/Button';
import Grid from 'material-ui/Grid';
import List from 'material-ui/List';
import Paper from 'material-ui/Paper';
import Typography from 'material-ui/Typography';
import AddIcon from 'material-ui-icons/Add';
import CloseIcon from 'material-ui-icons/Close';
import EditIcon from 'material-ui-icons/Edit';

import '../../commons/component.css'
import {products} from '../i18n'
import withCrud from '../../hoc/withCrud';
import AdminList from '../../commons/list';
import ProductComponent from './product';
import Language from '../../commons/language'

class ProductListComponent extends React.Component{
    constructor(props){
        super(props);
        this.state = {
            data: [],
            overlapped: false,
            thisName: 'new',
            categoryName: this.props.match.params.name
        };
        this.onEdit = this.onEdit.bind(this);
        this.toggleOverlap = this.toggleOverlap.bind(this);
        this.handleCreate = this.handleCreate.bind(this);
        this.handleUpdate = this.handleUpdate.bind(this);
        this.handleDelete = this.handleDelete.bind(this);
    }

    onEdit(id){
        this.setState({
            thisName: id,
            overlapped: !this.state.overlapped
        });
    }

    toggleOverlap(){
        this.setState({thisName: 'new', overlapped: !this.state.overlapped});
    }
    
    handleCreate(data){
        this.props.create(data);
        this.setState({overlapped: !this.state.overlapped});
    }
    
    handleUpdate(data){
        this.props.update(data);
        this.setState({overlapped: !this.state.overlapped});
    }

    handleDelete(id){
        this.props.delete(id)
    }

    render(){
        const lang = Language.getLangCode();
        return(
            <Grid container>
            <Grid xs={12} item>
                <Grid container >
                <Grid xs={12} item style={{position: 'relative'}} >
                    <Button fab color="accent" aria-label="add" className='fab' onClick={this.toggleOverlap}>
                        {!this.state.overlapped ? <AddIcon /> : <CloseIcon />}
                    </Button>
                </Grid>
                </Grid>
                <Grid xs={12} item style={{position: 'relative'}}>
                    {this.state.overlapped ? null :
                    <Grid xs={12} item  style={{position: 'absolute', width: '100%'}}>
                        <Paper>
                            <Typography type="display1" component="h4" align="center">
                                {products.title[lang]}
                            </Typography>
                            <List>
                                {this.props.data.map(product => (
                                    <AdminList 
                                        name={product.orderCode || product.id} 
                                        title={product.title}
                                        baseUrl='/admin/webshop/product'
                                        onDelete={this.handleDelete} 
                                        icons={[
                                                {action:'EDIT', icon: EditIcon, click: this.onEdit}
                                            ]}
                                        warningExplanation = {products.warningExplanation[lang]}
                                        key={product.orderCode || product.id}/>
                                ))}
                            </List>
                        </Paper>
                    </Grid>
                    }
                    <CSSTransitionGroup
                        transitionName="component"
                        transitionAppear={false}
                        transitionEnter={true}
                        transitionLeave={true}
                        transitionLeaveTimeout={400}
                        transitionEnterTimeout={400}
                        >
                        {!this.state.overlapped ? null
                            :
                            <Grid xs={12} item style={{position: 'absolute', width: '100%'}}>
                                <ProductComponent name={this.state.thisName} onCreate={this.handleCreate} onUpdate={this.handleUpdate} parentName={this.state.categoryName}/>
                            </Grid>
                        }
                    </CSSTransitionGroup>
                </Grid>
            </Grid>
            </Grid>
        )
    }
}

export default withCrud(ProductListComponent, '/webshop/api/products');