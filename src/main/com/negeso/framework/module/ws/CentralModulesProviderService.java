
package com.negeso.framework.module.ws;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.1 in JDK 6
 * Generated source version: 2.1
 * 
 */
@WebServiceClient(name = "CentralModulesProviderService", targetNamespace = "http://ws.module.framework.negeso.com/", wsdlLocation = "http://cjs:8084/centralModules?wsdl")
public class CentralModulesProviderService
    extends Service
{

  
    
    public CentralModulesProviderService(URL wsdlLocation) {
        super(wsdlLocation, new QName("http://ws.module.framework.negeso.com/", "CentralModulesProviderService"));
    }

    public CentralModulesProviderService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }
   

    /**
     * 
     * @return
     *     returns CentralModulesProvider
     */
    @WebEndpoint(name = "CentralModulesProviderPort")
    public CentralModulesProvider getCentralModulesProviderPort() {
        return (CentralModulesProvider)super.getPort(new QName("http://ws.module.framework.negeso.com/", "CentralModulesProviderPort"), CentralModulesProvider.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns CentralModulesProvider
     */
    @WebEndpoint(name = "CentralModulesProviderPort")
    public CentralModulesProvider getCentralModulesProviderPort(WebServiceFeature... features) {
        return (CentralModulesProvider)super.getPort(new QName("http://ws.module.framework.negeso.com/", "CentralModulesProviderPort"), CentralModulesProvider.class, features);
    }

}
