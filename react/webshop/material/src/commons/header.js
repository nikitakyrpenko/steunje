import React from 'react';
import {withStyles} from 'material-ui/styles';

import AppBar from 'material-ui/AppBar';
import Button from 'material-ui/Button';
import Grid from 'material-ui/Grid';
import Toolbar from 'material-ui/Toolbar';
import Typography from 'material-ui/Typography';

import Language from './language';
import dict from '../components/i18n';

const styles = theme => ({
    btn: {
        position: 'absolute',
        top: '50%',
        transform: 'translateY(-50%)',
        zIndex: '10'
    },
    title: {
        color: 'white'
    }
})

const lang = Language.getLangCode();
const Header  = (props) =>(
        <Grid container>
            <Grid item xs>
                <AppBar position="static">
                    <Toolbar>
                        <Grid item xs>
                            <Button onClick={() => window.history.back()} className={props.classes.btn} >
                                {dict.back[lang]}
                            </Button>
                            <Typography type="title" align="center" className={props.classes.title}>
                                {props.title}
                            </Typography>
                        </Grid>
                    </Toolbar>
                </AppBar>
            </Grid>
        </Grid>
    );

export default withStyles(styles)(Header)