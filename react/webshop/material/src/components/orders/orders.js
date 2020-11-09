import React from 'react';
import PropTypes from 'prop-types'
import NumberFormat from 'react-number-format';

import Grid from 'material-ui/Grid';
import Paper from 'material-ui/Paper';
import dateFormat from 'dateformat'
import Table, {
    TableBody,
    TableCell,
    TableFooter,
    TableHead,
    TablePagination,
    TableRow,
    TableSortLabel,
} from 'material-ui/Table';
import {withStyles} from 'material-ui/styles';

import withCrud from '../../hoc/withCrud';
import OrderDetailsComponent from './order';

const columnData = [
    { id: 'orderDate', numeric: false, disablePadding: true, label: 'Date' },
    { id: 'transactionId', numeric: false, disablePadding: false, label: 'Transaction id' },
    { id: 'price', numeric: false, disablePadding: false, label: 'Price' },
    { id: 'status', numeric: false, disablePadding: false, label: 'Status' },
    { id: 'paymentMethod', numeric: false, disablePadding: false, label: 'Payment method' },
];

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

class OrdersComponent extends React.Component{
    constructor(props) {
        super(props);
        this.state = {
            order: 'asc',
            orderBy: 'orderDate',
            page: 0,
            rowsPerPage: 20,
            rowsPerPageOptions: [20, 50, 100],
            data: [],
        };
        this.handleChangePage = this.handleChangePage.bind(this)
        this.handleChangeRowsPerPage = this.handleChangeRowsPerPage.bind(this)
        this.createSortHandler = this.createSortHandler.bind(this)
        this.handleRequestSort = this.handleRequestSort.bind(this)
    }
    static propTypes = {
        numSelected: PropTypes.number,
        order: PropTypes.string,
        orderBy: PropTypes.string,
        rowCount: PropTypes.number,
    };

    componentWillReceiveProps(n){
        let data = n.data.map(el => {
            el.orderDate = new Date(el.orderDate);
            return el;
        });
        data = data.sort((a, b) => (b.orderDate < a.orderDate ? -1 : 1));
        this.setState({data:data})
    }

    createSortHandler = property => event => {
        this.handleRequestSort(event, property);
    };

    handleChangePage = (event, page) => {
        this.setState({ page });
    };

    handleChangeRowsPerPage = event => {
        this.setState({ rowsPerPage: event.target.value });
    };

    handleRequestSort = (event, property) => {
        const orderBy = property;
        let order = 'desc';
        if (this.state.orderBy === property && this.state.order === 'desc') {
            order = 'asc';
        }
        const data = order === 'desc'
                ? this.state.data.sort((a, b) => (a[orderBy] < b[orderBy] ? -1 : 1))
                : this.state.data.sort((a, b) => (b[orderBy] < a[orderBy] ? -1 : 1));
        this.setState({ data, order, orderBy });
    };

    render(){
        const {classes} = this.props;
        const { order, orderBy, rowsPerPage, page, data } = this.state;
        return (
            <Paper className={classes.root} >
                <Table>
                    <TableHead>
                        <TableRow>
                            {columnData.map(column => {
                                return (
                                    <TableCell
                                        key={column.id}
                                        numeric={column.numeric}
                                        onClick={this.createSortHandler(column.id)}
                                    >
                                        <TableSortLabel
                                            active={orderBy === column.id}
                                            direction={order}
                                        >
                                            {column.label}
                                        </TableSortLabel>
                                    </TableCell>
                                );
                            }, this)}
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {data.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage).map(n => {
                            return(
                                (this.state.selectedKey === n.id ?[
                                    <TableRow
                                        hover
                                        selected key={n.id}
                                        onClick={() => this.setState({selectedKey: null})}
                                    >
                                        <TableCell>{dateFormat(new Date(n.orderDate),"yyyy-mm-dd HH:MM")} </TableCell>
                                        <TableCell>{n.transactionId}</TableCell>
                                        <TableCell>
                                            <NumberFormat
                                                value={n.price}
                                                displayType='text'
                                                prefix={'€ '}
                                                decimalSeparator={','}
                                                decimalPrecision={2}
                                            />
                                        </TableCell>
                                        <TableCell>{n.status}</TableCell>
                                        <TableCell>{n.paymentMethod}</TableCell>
                                    </TableRow>,
                                    <TableRow >
                                        <TableCell colSpan={5}>
                                            <Grid container >
                                                <Grid item xs style={{margin: '10px 0'}}>
                                                    <OrderDetailsComponent id={n.id}/>
                                                </Grid>
                                            </Grid>
                                        </TableCell>
                                    </TableRow>
                                    ]:
                                    <TableRow
                                        hover
                                        key={n.id}
                                        onClick={() => this.setState({selectedKey: n.id})}
                                        tabIndex={-1}
                                    >
                                    <TableCell>{dateFormat(new Date(n.orderDate),"yyyy-mm-dd HH:MM")} </TableCell>
                                    <TableCell>{n.transactionId}</TableCell>
                                    <TableCell>
                                        <NumberFormat
                                            value={n.price}
                                            displayType='text'
                                            prefix={'€ '}
                                            decimalSeparator={','}
                                            decimalPrecision={2}
                                        />
                                    </TableCell>
                                        <TableCell>{n.status}</TableCell>
                                        <TableCell>{n.paymentMethod}</TableCell>
                                    </TableRow>
                                )
                            )
                        })
                      }
                    </TableBody>
                    <TableFooter>
                        <TableRow>
                            <TablePagination
                                count={data.length}
                                rowsPerPage={rowsPerPage}
                                rowsPerPageOptions={this.state.rowsPerPageOptions}
                                page={page}
                                onChangePage={this.handleChangePage}
                                onChangeRowsPerPage={this.handleChangeRowsPerPage}
                            />
                        </TableRow>
                    </TableFooter>
                </Table>
            </Paper>
        )
    }
}



export default withStyles(styles)(withCrud(OrdersComponent, '/webshop/api/orders'))