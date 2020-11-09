import includes from 'array-includes';

export default class Language{

    static langCode = Language.determineLanguage();

    static getLangCode(){
        
        return Language.langCode;
    }

    static determineLanguage(){
        const language = (navigator.languages && navigator.languages[0]) ||
                        navigator.language || navigator.userLanguage;
        const langCode = language ? language.substring(0, 2) : 'en';
        const availableLanguages = ['nl', 'en'];
        
        return includes(availableLanguages, langCode) ? langCode : 'nl';
    }
}