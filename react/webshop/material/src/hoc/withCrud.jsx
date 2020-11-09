import React from 'react';
import axios from 'axios';

const devStage = {
    headers: {
        'accept': 'application/json',
        'content-type': 'application/json',
    }
};

function withCrud(Component, apiUrl) {
    class WithCrud extends React.Component {
        state = {
            data: []
        };

        componentDidMount() {
            let name = this.props.name || this.props.match.params.id || this.props.match.params.name;
            if (name !== 'new')
                this.read(name);
        };

        componentWillReceiveProps(nextProps){
            if (this.props.name !== 'new' && this.props.match)
                this.read(nextProps.match.params.name);
        };

        read = (id) => {
            let url = id === undefined ? apiUrl : `${apiUrl}/${id}`;

            axios.get(url, devStage)
                .then(response => response.data)
                .then(data => this.setState({data}));
        };
        
        create = data => {
            axios.post(apiUrl, data, devStage)
                .then(response => response.data)
                .then(createdItem => {
                    if(createdItem === '')
                        return;
                    const data = [...this.state.data, createdItem];
                    this.setState({data});
                });
        };

        update = data =>{
            axios.put(`${apiUrl}/${data.id || data.name}`, data, devStage)
                .then(response => response.data)
                .then(updatedItem => {
                    if(Array.isArray(updatedItem)){
                        const data = this.state.data.map(item => {
                            return item.id !== updatedItem.id ? item : {...item, ...updatedItem};
                        });

                        this.setState({data});
                    }
            });
        };

        delete = (id, specialIdName) => {
            axios.delete(`${apiUrl}/${id}`, devStage)
                .then(response => response.data)
                .then(() => {
                    const data = specialIdName ? this.state.data.filter(item => item[specialIdName] !== id) : this.state.data.filter(item => item.id !== id);
                    this.setState({data});
                });
        };

        changeOrder = (id, direction) => {
            axios.put(`${apiUrl}?action=changeOrder&objId=${id}&direction=${direction}`, devStage)
                .then(response => response.data)
                .then(data => {
                    this.setState({data});
                })
        };

        render() {
            return <Component data={this.state.data}
                            read={this.read}
                            create={this.create}
                            update={this.update}
                            delete={this.delete}
                            changeOrder={this.changeOrder}
                            {...this.props} />
        }
    }

    WithCrud.displayName = `WithCrud(${Component.displayName || Component.name || 'Component'})`;

    return WithCrud;
}

export default withCrud;
export {devStage};