import React from 'react'

import Button from 'material-ui/Button'
import Grid from 'material-ui/Grid'
import List from 'material-ui/List'
import Paper from 'material-ui/Paper'
import {withStyles} from 'material-ui/styles'
import TextField from 'material-ui/TextField'
import Typography from 'material-ui/Typography'
import AddIcon from 'material-ui-icons/Add'
import EditIcon from 'material-ui-icons/Edit'
import ExpandMoreIcon from 'material-ui-icons/ExpandMore'

import withCrud from '../../hoc/withCrud'
import AdminList from '../../commons/list'
import Language from '../../commons/language'
import dic, {users} from '../i18n';

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

class UsersComponent extends React.Component{
    constructor(props){
        super(props);
        this.state = {
            data: [],
            searchString: '',
            thisName: 'new',
            i: 1
        }
        this.goToUsers = this.goToUsers.bind(this);
        this.handleDelete = this.handleDelete.bind(this);
        this.handleChange = this.handleChange.bind(this);
        this.handleCreate = this.handleCreate.bind(this);
        this.handleUpdate = this.handleUpdate.bind(this);
    }

    goToUsers(id){
        this.props.history.push(`/admin/webshop/users/${id}`);
    }

    handleDelete(userCode){
        this.props.delete(userCode, 'userCode');
    }

    handleChange(event) {
        this.setState({searchString: event.target.value});
    }

    handleCreate(data){
        this.props.create(data);
        this.setState({overlapped: !this.state.overlapped});
    }

    handleUpdate(data){
        this.props.update(data);
        this.setState({overlapped: !this.state.overlapped});
    }

    render(){
        const {classes} = this.props;
        let libraries = this.props.data,
            searchString = this.state.searchString.trim();
        if(searchString.length > 0) {
            libraries = libraries.filter((el) => el.userCode.match(searchString))
        }
        libraries = libraries.slice(0,20*this.state.i);
        const articleElements = libraries.map(user =>
            <AdminList
                name = {user.userCode}
                title = {user.userCode}
                baseUrl='/admin/webshop/users'
                onDelete={this.handleDelete}
                icons={[
                    {action:'EDIT', icon: EditIcon, click: this.goToUsers}
                ]}
                key={user.userCode}
                warningExplanation = {users.warningExplanation[lang]}
            />
        )
        return (
            <Grid item xs>
                <Grid xs={12} item style={{position: 'relative'}} >
                    <Button fab color="accent" aria-label="add" className='fab' onClick={() => this.goToUsers('new')}>
                        <AddIcon />
                    </Button>
                </Grid>
                <Grid xs={12} item>
                    <Paper className={classes.root}>
                        <Grid container justify='flex-end'>
                            <Grid xs={12} item>
                                <Typography type="display1" component="h4" align="center">
                                    {users.title[lang]}
                                </Typography>
                            </Grid>
                            <Grid xs={12} item>
                                <TextField
                                    id="search"
                                    type="text"
                                    label={users.user_search[lang]}
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

export default withStyles(styles)(withCrud(UsersComponent, `/webshop/api/users`));