import React from "react";

import Grid from 'material-ui/Grid';
import createPalette from 'material-ui/styles/createPalette';
import {MuiThemeProvider, createMuiTheme} from 'material-ui/styles';
import lightGreen from 'material-ui/colors/lightGreen';
import red from 'material-ui/colors/red';

import Header from './commons/header';
import Body from './components/body';
import Language from './commons/language';
import dict from './components/i18n';
//import "./App.css";

const theme = createMuiTheme({
    overrides: {
        MuiButton: {
          root: {color: 'white'},
          raised: {color: 'white'},
          raisedPrimary: {color: 'white'},
        },
      },
      palette: createPalette({
        primary: {
            ...lightGreen,
            400: red[500],
            contrastDefaultColor: 'light'
        }
      })
  });


const lang = Language.getLangCode();
const App = (props) => {
    return (
        <MuiThemeProvider theme={theme}>
            <Grid container direction="row" justify="center" align="flex-start">
                <Grid item xs={12} sm={10} md={10} lg={8}>
                    <Header title={dict.webshopTitle[lang]}/>
                    <Body/>
                </Grid>
            </Grid>
         </MuiThemeProvider>
    );
}
  
export default App;
