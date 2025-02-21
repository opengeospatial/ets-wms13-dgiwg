package de.latlon.ets.wms13.core.dgiwg.testsuite.getcapabilities;

import static de.latlon.ets.core.assertion.ETSAssert.assertContentType;
import static de.latlon.ets.core.assertion.ETSAssert.assertXPath;
import static de.latlon.ets.wms13.core.assertion.WmsAssertion.assertSimpleWMSCapabilities;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.FORMAT_PARAM;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.GET_CAPABILITIES;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.TEXT_HTML;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.TEXT_XML;
import static org.testng.Assert.assertTrue;

import java.net.URI;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import de.latlon.ets.core.error.ErrorMessage;
import de.latlon.ets.core.error.ErrorMessageKey;
import de.latlon.ets.wms13.core.domain.ProtocolBinding;
import de.latlon.ets.wms13.core.util.ServiceMetadataUtils;
import jakarta.ws.rs.core.Response;
import jakarta.xml.soap.SOAPException;

/**
 * Tests if the expected formats for GetCapabilites requests (text/xml and text/html) are supported.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class GetCapabilitiesOutputFormatTest extends AbstractBaseGetCapabilitiesFixture {

    @BeforeMethod
    public void clearRequest() {
        this.reqEntity.removeKvp( FORMAT_PARAM );
    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.5.3.1., S.9, Requirement 3")
    public void wmsCapabilitiesOutputFormatXmlSupported()
                    throws SOAPException {
        String xPathXml = "//wms:WMS_Capabilities/wms:Capability/wms:Request/wms:GetCapabilities/wms:Format/text() = 'text/xml'";
        assertXPath( xPathXml, wmsCapabilities, NS_BINDINGS );

    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.5.3.1., S.9, Requirement 3")
    public void wmsCapabilitiesOutputFormatHtmlSupported()
                    throws SOAPException {
        String xPathHtml = "//wms:WMS_Capabilities/wms:Capability/wms:Request/wms:GetCapabilities/wms:Format/text() = 'text/html'";
        assertXPath( xPathHtml, wmsCapabilities, NS_BINDINGS );

    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.5.3.1., S.9, Requirement 3", dependsOnMethods = "wmsCapabilitiesOutputFormatXmlSupported")
    public
                    void wmsGetCapabilitiesOutputFormatXmlSupported()
                                    throws SOAPException {
        URI endpoint = ServiceMetadataUtils.getOperationEndpoint( this.wmsCapabilities, GET_CAPABILITIES,
                                                                  ProtocolBinding.GET );
        this.reqEntity.addKvp( FORMAT_PARAM, TEXT_XML );
        Response rsp = wmsClient.submitRequest( this.reqEntity, endpoint );

        assertTrue( rsp.hasEntity(), ErrorMessage.get( ErrorMessageKey.MISSING_XML_ENTITY ) );
        assertSimpleWMSCapabilities( rsp.readEntity( Document.class ) );

        assertContentType( rsp.getHeaders(), TEXT_XML );
    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.5.3.1., S.9, Requirement 3", dependsOnMethods = "wmsCapabilitiesOutputFormatHtmlSupported")
    public
                    void wmsGetCapabilitiesOutputFormatHtmlSupported()
                                    throws SOAPException {
        URI endpoint = ServiceMetadataUtils.getOperationEndpoint( this.wmsCapabilities, GET_CAPABILITIES,
                                                                  ProtocolBinding.GET );
        this.reqEntity.addKvp( FORMAT_PARAM, TEXT_HTML );
        Response rsp = wmsClient.submitRequest( this.reqEntity, endpoint );

        assertTrue( rsp.hasEntity(), ErrorMessage.get( ErrorMessageKey.MISSING_XML_ENTITY ) );

        assertContentType( rsp.getHeaders(), TEXT_HTML );
    }

}