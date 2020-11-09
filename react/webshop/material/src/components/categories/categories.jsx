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
import ArrowDownwardIcon from 'material-ui-icons/ArrowDownward';
import ArrowUpwardIcon from 'material-ui-icons/ArrowUpward';
import ViewList from 'material-ui-icons/ViewList';

import withCrud from '../../hoc/withCrud';
import AdminList from '../../commons/list';
import CategoryComponent from './category'
import Language from '../../commons/language';
import dict,{categories} from '../i18n';

class CategoryListComponent extends React.Component{
    constructor(props){
        super(props);
        this.state = {
            data: [],
            overlapped: false,
            thisName: 'new',
            parentName: this.props.match.params.id || this.props.match.params.name
        };
        this.onEdit = this.onEdit.bind(this);
        this.goToProducts = this.goToProducts.bind(this);
        this.toggleOverlap = this.toggleOverlap.bind(this);
        this.handleCreate = this.handleCreate.bind(this);
        this.handleUpdate = this.handleUpdate.bind(this);
        this.handleDelete = this.handleDelete.bind(this);
        this.moveUp = this.moveUp.bind(this);
        this.moveDown = this.moveDown.bind(this);
    }

    componentWillReceiveProps(nextProps){
        this.setState({overlapped: false, parentName: this.props.match.params.id || this.props.match.params.name});
    }

    onEdit(id){
        this.setState({
            thisName: id,
            overlapped: !this.state.overlapped
        });
    }

    goToProducts(id){
        this.props.history.push(`/admin/webshop/products/${id}`);
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

    moveUp(id){
        this.props.changeOrder(id, 'UP');
    }

    moveDown(id){
        this.props.changeOrder(id, 'DOWN');
    }

    render(){
        const lang = Language.getLangCode();
        return (
            <Grid container>
            <Grid xs={12} item>
                <Grid container>
                <Grid xs={12} item style={{position: 'relative'}}>
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
                                {categories.title[lang]}
                            </Typography>
                            <List>
                                {this.props.data.map(category => (
                                    <AdminList 
                                        name={category.id} 
                                        title={category.title} 
                                        baseUrl='/admin/webshop/categories'
                                        onDelete={this.handleDelete} 
                                        icons={[
                                            {action:'MOVE_UP', icon: ArrowUpwardIcon, click: this.moveUp},
                                            {action:'MOVE_DOWN', icon: ArrowDownwardIcon, click: this.moveDown},
                                            {action:'EDIT', icon: EditIcon, click: this.onEdit},
                                            {action:'GOTOPRODUCTS', icon: ViewList, click: this.goToProducts}
                                            ]}
                                        warningExplanation = {dict.warningExplanation[lang]}
                                        key={category.id}/>
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
                        {!this.state.overlapped ? null : 
                            <Grid xs={12} item  style={{position: 'absolute', width: '100%'}}>
                                <CategoryComponent name={this.state.thisName} onCreate={this.handleCreate} onUpdate={this.handleUpdate} parentName={this.state.parentName}/>
                            </Grid>
                        }
                    </CSSTransitionGroup>
                </Grid>
            </Grid>
            </Grid>
        )
    }
}

export default withCrud(CategoryListComponent, '/webshop/api/categories');