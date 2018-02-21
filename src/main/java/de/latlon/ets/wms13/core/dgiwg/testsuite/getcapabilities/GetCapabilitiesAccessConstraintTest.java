package de.latlon.ets.wms13.core.dgiwg.testsuite.getcapabilities;

import static de.latlon.ets.core.assertion.ETSAssert.assertXPath;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.GET_CAPABILITIES;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.testng.SkipException;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import com.sun.jersey.api.client.ClientResponse;

import de.latlon.ets.core.assertion.ETSAssert;
import de.latlon.ets.wms13.core.domain.ProtocolBinding;
import de.latlon.ets.wms13.core.util.ServiceMetadataUtils;

/**
 * Tests if the capabilities contains a valid value for AccessConstraint.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class GetCapabilitiesAccessConstraintTest extends AbstractBaseGetCapabilitiesFixture {

    private static final List<String> EXPECTED_ACCESS_CONSTRAINTS = Arrays.asList( "none", "unclassified",
                                                                                   "restricted", "confidential",
                                                                                   "secret", "topsecret" );

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.6.2.2., S.13, Requirement 10")
    public void wmsCapabilitiesAccessConstraintsExists() {
        String xPathXml = "//wms:WMS_Capabilities/wms:Service/wms:AccessConstraints";
        skipIfNoOptionalMetadata( xPathXml );
        assertXPath( xPathXml, wmsCapabilities, NS_BINDINGS );
    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.6.2.2., S.13, Requirement 10", dependsOnMethods = "wmsCapabilitiesAccessConstraintsExists")
    public void wmsCapabilitiesAccessConstraintsContainsValue()
                            throws XPathFactoryConfigurationException, XPathExpressionException {
        URI endpoint = ServiceMetadataUtils.getOperationEndpoint( this.wmsCapabilities, GET_CAPABILITIES,
                                                                  ProtocolBinding.GET );
        ClientResponse rsp = wmsClient.submitRequest( this.reqEntity, endpoint );

        String accessConstraints = parseAccessConstraints( rsp ).toLowerCase().trim();

        assertNotNull( accessConstraints,
                       "AccessConstraints are not valid, shall use the reserve word 'none' when present and no AccessConstraints applicable" );
        assertNotEquals( accessConstraints, "",
                         "AccessConstraints are not valid, shall use the reserve word 'none' when present and no AccessConstraints applicable" );
        assertTrue( EXPECTED_ACCESS_CONSTRAINTS.contains( accessConstraints ),
                    "AccessConstraints are not valid, must be one of " + EXPECTED_ACCESS_CONSTRAINTS + " but was "
                                            + accessConstraints );
    }

    private void skipIfNoOptionalMetadata( String xPath ) {
        boolean metadataEvaluates = ETSAssert.checkXPath( xPath, wmsCapabilities, NS_BINDINGS );
        if ( !metadataEvaluates ) {
            // if ( xPath.endsWith("''") )
            // throw new
            // SkipException("The WMS contains the optional capabilities metadata but is blank, tests are skipped!" );
            // else
            throw new SkipException( "The WMS does not contain optional capabilities metadata, tests are skipped!" );
        }
    }

    private String parseAccessConstraints( ClientResponse rsp )
                            throws XPathFactoryConfigurationException, XPathExpressionException {
        String xPathAccessConstraints = "//wms:WMS_Capabilities/wms:Service/wms:AccessConstraints";
        return (String) createXPath().evaluate( xPathAccessConstraints, rsp.getEntity( Document.class ),
                                                XPathConstants.STRING );
    }

    private XPath createXPath()
                            throws XPathFactoryConfigurationException {
        XPathFactory factory = XPathFactory.newInstance( XPathConstants.DOM_OBJECT_MODEL );
        XPath xpath = factory.newXPath();
        xpath.setNamespaceContext( NS_BINDINGS );
        return xpath;
    }

}