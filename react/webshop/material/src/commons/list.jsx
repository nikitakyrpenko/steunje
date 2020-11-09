import React from 'react';
import {Link} from 'react-router-dom';
import Language from './language';
import dict from '../components/i18n';

import { 
    ListItem, 
    ListItemText, 
    ListItemSecondaryAction, 
} from 'material-ui/List';
import IconButton from 'material-ui/IconButton';
import Button from 'material-ui/Button';
import Dialog, {
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
} from 'material-ui/Dialog';
import DeleteIcon from 'material-ui-icons/Delete';

class AdminList extends React.Component{
    constructor(props){
        super(props);
        this.state = {open: false};
        this.handleRequestClose = this.handleRequestClose.bind(this);
    }

    handleRequestClose = () => {
        this.setState({open: false});
    };

    handleRequestAccept = (name) => {
        this.props.onDelete(name);
    }

    render(){
        const lang = Language.getLangCode();
        return (
            <ListItem button component={this.props.baseUrl ? Link : ''} to={`${this.props.baseUrl}/${this.props.name}`} >
                <ListItemText primary={this.props.title} secondary={this.props.secondary}/>
                <ListItemSecondaryAction>
                    {this.props.icons.map(i => 
                        <IconButton aria-label={i.action} onClick={() => i.click(this.props.name)} key={i.action}>
                            {<i.icon/>}
                        </IconButton>
                    )}
                    <IconButton aria-label="Delete" onClick={() => this.setState({ open: true })}>
                        <Dialog open={this.state.open} onRequestClose={this.handleRequestClose}>
                            <DialogTitle>
                                {dict.warning[lang]}
                            </DialogTitle>
                            <DialogContent>
                                <DialogContentText>
                                    {this.props.warningExplanation}
                                </DialogContentText>
                            </DialogContent>
                            <DialogActions>
                                <Button onClick={this.handleRequestClose} color="primary">
                                    {dict.disagree[lang]}
                                </Button>
                                <Button onClick={() => this.handleRequestAccept(this.props.name)} color="primary">
                                    {dict.agree[lang]}
                                </Button>
                            </DialogActions>
                        </Dialog>
                        <DeleteIcon />
                    </IconButton>
                </ListItemSecondaryAction>
            </ListItem>
        )
    }
} 

export default AdminList