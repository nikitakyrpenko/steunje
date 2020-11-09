import React from 'react';
import {Route, Link, Switch} from 'react-router-dom';

import Grid from 'material-ui/Grid';
import List, {ListItem, ListItemIcon, ListItemText} from 'material-ui/List';
import Paper from 'material-ui/Paper';
import AccountBox from 'material-ui-icons/AccountBox';
import ImportExport from 'material-ui-icons/ImportExport';
import ListIcon from 'material-ui-icons/List';
import Settings from 'material-ui-icons/Settings';
import Store from 'material-ui-icons/Store';
import Search from 'material-ui-icons/Search';


import ImportComponent from './io/import';
import GeneralSettings from './settings/settings';
import WebshopOrders from './orders/orders';
import UsersComponent from './users/users_list';
import UserComponent from './users/user_page';
import CategoryListComponent from './categories/categories';
import CategoryComponent from './categories/category';
import ProductListComponent from './products/products';
import ProductComponent from './products/product';
import SalesListComponent from './sales/sales_products';
import SearchProduct from './products/searchProduct'
import Language from '../commons/language';
import dict, {importExport, settings, orders, users, products} from './i18n';

const lang = Language.getLangCode();

const Export = () => (
    <div>{dict.emptyPage[lang]}</div>
);

const ModuleActions = () => (
    <Paper>
        <List>
            <ListItem button component={Link} to="/admin/webshop/io" >
                <ListItemIcon>
                    <ImportExport/>
                </ListItemIcon>
                <ListItemText primary={importExport.title[lang]} />
            </ListItem>
            <ListItem button component={Link} to="/admin/webshop/categories" >
                <ListItemIcon>
                    <ListIcon/>
                </ListItemIcon>
                <ListItemText primary={dict.browseCategory[lang]} />
            </ListItem>
            <ListItem button component={Link} to="/admin/webshop/products" >
                <ListItemIcon>
                    <Search/>
                </ListItemIcon>
                <ListItemText primary={products.searchProduct[lang]} />
            </ListItem>
            <ListItem button component={Link} to="/admin/webshop/sales" >
                <ListItemIcon>
                    <ListIcon/>
                </ListItemIcon>
                <ListItemText primary={'Sale'} />
            </ListItem>
            <ListItem button component={Link} to="/admin/webshop/orders" >
                <ListItemIcon>
                    <Store/>
                </ListItemIcon>
                <ListItemText primary={orders.title[lang]} />
            </ListItem>
            <ListItem button component={Link} to="/admin/webshop/users" >
                <ListItemIcon>
                    <AccountBox/>
                </ListItemIcon>
                <ListItemText primary={users.title[lang]} />
            </ListItem>
            <ListItem button component={Link} to="/admin/webshop/settings" >
                <ListItemIcon>
                    <Settings/>
                </ListItemIcon>
                <ListItemText primary={settings.settings[lang]} />
            </ListItem>
        </List>
    </Paper>
);

const IOActions = () => (
    <Paper>
        <List>
            <ListItem button component={Link} to="/admin/webshop/io/import" >
                <ListItemText primary={importExport.import[lang]} />
            </ListItem>
            <ListItem button component={Link} to="/admin/webshop/io/export" >
                <ListItemText primary={importExport.export[lang]} />
            </ListItem>
        </List>
    </Paper>
);

const Body = (props) => (
    <Grid container>
        <Grid item xs>
            <Switch>
                <Route exact path='/admin/webshop' component={ModuleActions}/>
                <Route exact path='/admin/webshop/io' component={IOActions}/>
                <Route exact path='/admin/webshop/products' component={SearchProduct}/>
                <Route path='/admin/webshop/io/import' component={ImportComponent}/>
                <Route path='/admin/webshop/io/export' component={Export}/>
                <Route exact path='/admin/webshop/categories' component={CategoryListComponent}/>
                <Route path='/admin/webshop/categories/:name' component={CategoryListComponent}/>
                <Route path='/admin/webshop/category/:name' component={CategoryComponent}/>
                <Route path='/admin/webshop/products/:name' component={ProductListComponent}/>
                <Route path='/admin/webshop/product/:name' component={ProductComponent}/>
                <Route exact path='/admin/webshop/settings' component={GeneralSettings}/>
                <Route exact path='/admin/webshop/orders' component={WebshopOrders}/>
                <Route path='/admin/webshop/orders/:id' component={WebshopOrders}/>
                <Route exact path='/admin/webshop/users' component={UsersComponent}/>
                <Route path='/admin/webshop/users/:id' component={UserComponent}/>
                <Route exact path='/admin/webshop/sales' component={SalesListComponent}/>
            </Switch>
        </Grid>
    </Grid>
);

export default Body