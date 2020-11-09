import React from 'react';

function withLang(Component){
    class WithLang extends React.Component {

        state = {
            langCode: this.determineLang()
        }

        determineLang(){
            const language = (navigator.languages && navigator.languages[0]) ||
                        navigator.language || navigator.userLanguage;
        
            return language ? language.substring(0, 2) : 'en';
        }
               
        componentDidMount(){
            let langCode = 'nl';
            this.setState({langCode: langCode})
        }

        render() {
            return <Component lang={this.state.langCode}
                            {...this.props} />
        }
    }

    WithLang.displayName = `WithLang(${Component.displayName || Component.name || 'Component'})`;

    return WithLang;
}

export default withLang;