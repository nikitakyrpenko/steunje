
package com.negeso.framework.module.ws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.negeso.framework.module.ws package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _IsModuleEnabled_QNAME = new QName("http://ws.module.framework.negeso.com/", "isModuleEnabled");
    private final static QName _GetModules_QNAME = new QName("http://ws.module.framework.negeso.com/", "getModules");
    private final static QName _ResetCacheResponse_QNAME = new QName("http://ws.module.framework.negeso.com/", "resetCacheResponse");
    private final static QName _GetAllModulesResponse_QNAME = new QName("http://ws.module.framework.negeso.com/", "getAllModulesResponse");
    private final static QName _GetAllModules_QNAME = new QName("http://ws.module.framework.negeso.com/", "getAllModules");
    private final static QName _GetModulesResponse_QNAME = new QName("http://ws.module.framework.negeso.com/", "getModulesResponse");
    private final static QName _ResetCache_QNAME = new QName("http://ws.module.framework.negeso.com/", "resetCache");
    private final static QName _IsModuleEnabledResponse_QNAME = new QName("http://ws.module.framework.negeso.com/", "isModuleEnabledResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.negeso.framework.module.ws
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CentralModuleItem }
     * 
     */
    public CentralModuleItem createCentralModuleItem() {
        return new CentralModuleItem();
    }

    /**
     * Create an instance of {@link GetAllModules }
     * 
     */
    public GetAllModules createGetAllModules() {
        return new GetAllModules();
    }

    /**
     * Create an instance of {@link GetAllModulesResponse }
     * 
     */
    public GetAllModulesResponse createGetAllModulesResponse() {
        return new GetAllModulesResponse();
    }

    /**
     * Create an instance of {@link ResetCache }
     * 
     */
    public ResetCache createResetCache() {
        return new ResetCache();
    }

    /**
     * Create an instance of {@link CentralModule }
     * 
     */
    public CentralModule createCentralModule() {
        return new CentralModule();
    }

    /**
     * Create an instance of {@link IsModuleEnabledResponse }
     * 
     */
    public IsModuleEnabledResponse createIsModuleEnabledResponse() {
        return new IsModuleEnabledResponse();
    }

    /**
     * Create an instance of {@link ResetCacheResponse }
     * 
     */
    public ResetCacheResponse createResetCacheResponse() {
        return new ResetCacheResponse();
    }

    /**
     * Create an instance of {@link GetModulesResponse }
     * 
     */
    public GetModulesResponse createGetModulesResponse() {
        return new GetModulesResponse();
    }

    /**
     * Create an instance of {@link GetModules }
     * 
     */
    public GetModules createGetModules() {
        return new GetModules();
    }

    /**
     * Create an instance of {@link IsModuleEnabled }
     * 
     */
    public IsModuleEnabled createIsModuleEnabled() {
        return new IsModuleEnabled();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IsModuleEnabled }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.module.framework.negeso.com/", name = "isModuleEnabled")
    public JAXBElement<IsModuleEnabled> createIsModuleEnabled(IsModuleEnabled value) {
        return new JAXBElement<IsModuleEnabled>(_IsModuleEnabled_QNAME, IsModuleEnabled.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetModules }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.module.framework.negeso.com/", name = "getModules")
    public JAXBElement<GetModules> createGetModules(GetModules value) {
        return new JAXBElement<GetModules>(_GetModules_QNAME, GetModules.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ResetCacheResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.module.framework.negeso.com/", name = "resetCacheResponse")
    public JAXBElement<ResetCacheResponse> createResetCacheResponse(ResetCacheResponse value) {
        return new JAXBElement<ResetCacheResponse>(_ResetCacheResponse_QNAME, ResetCacheResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAllModulesResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.module.framework.negeso.com/", name = "getAllModulesResponse")
    public JAXBElement<GetAllModulesResponse> createGetAllModulesResponse(GetAllModulesResponse value) {
        return new JAXBElement<GetAllModulesResponse>(_GetAllModulesResponse_QNAME, GetAllModulesResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAllModules }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.module.framework.negeso.com/", name = "getAllModules")
    public JAXBElement<GetAllModules> createGetAllModules(GetAllModules value) {
        return new JAXBElement<GetAllModules>(_GetAllModules_QNAME, GetAllModules.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetModulesResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.module.framework.negeso.com/", name = "getModulesResponse")
    public JAXBElement<GetModulesResponse> createGetModulesResponse(GetModulesResponse value) {
        return new JAXBElement<GetModulesResponse>(_GetModulesResponse_QNAME, GetModulesResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ResetCache }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.module.framework.negeso.com/", name = "resetCache")
    public JAXBElement<ResetCache> createResetCache(ResetCache value) {
        return new JAXBElement<ResetCache>(_ResetCache_QNAME, ResetCache.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IsModuleEnabledResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.module.framework.negeso.com/", name = "isModuleEnabledResponse")
    public JAXBElement<IsModuleEnabledResponse> createIsModuleEnabledResponse(IsModuleEnabledResponse value) {
        return new JAXBElement<IsModuleEnabledResponse>(_IsModuleEnabledResponse_QNAME, IsModuleEnabledResponse.class, null, value);
    }

}
