import React from 'react';

const mockedData = {
    webshop:{
        api:{
            sales:[
                {
                    id: '000001',
                    title: 'TETETETETETETET'
                },
                {
                    id: '000002',
                    title: 'TETETETE2'
                },
                {
                    id: '000005',
                    title: 'TETETET3'
                },
            ]
        }
    }
};

function withMockedCrud(Component, apiUrl) {
    class WithMockedCrud extends React.Component {
        state = {
            data: []
        };

        componentDidMount() {
            let name = this.props.name || this.props.match.params.id || this.props.match.params.name;
            if (name !== 'new')
                this.read(name);
        }

        componentWillReceiveProps(nextProps){
            if (this.props.name !== 'new' && this.props.match)
                this.read(nextProps.match.params.name);
        }

        read = (id) => {
            let url = id === undefined ? apiUrl : `${apiUrl}/${id}`;
            const array = apiUrl.split('/');
            let data = mockedData;
            for(let i = 0; i<array.length; i++){
                let path = array[i];
                if(path !== ''){
                    data = data[array[i]]
                }
            }

            this.setState({data});
        };
        
        create = data => {
            // axios.post(apiUrl, data, devStage)
            //     .then(response => response.data)
            //     .then(createdItem => {
            //         if(createdItem === '')
            //             return;
            //         const data = [...this.state.data, createdItem];
            //         this.setState({data});
            //     });
        };

        update = data =>{
            // axios.put(`${apiUrl}/${data.id || data.name}`, data, devStage)
            //     .then(response => response.data)
            //     .then(updatedItem => {
            //         if(Array.isArray(updatedItem)){
            //             const data = this.state.data.map(item => {
            //                 return item.id !== updatedItem.id ? item : {...item, ...updatedItem};
            //             });

            //             this.setState({data});
            //         }
            // });
        };

        delete = (id, specialIdName) => {
            const data = specialIdName ? this.state.data.filter(item => item[specialIdName] !== id) : this.state.data.filter(item => item.id !== id);
            this.setState({data});
        };

        render() {
            return <Component data={this.state.data}
                            read={this.read}
                            create={this.create}
                            update={this.update}
                            delete={this.delete}
                            {...this.props} />
        }
    }

    return WithMockedCrud;
}

export default withMockedCrud;

