package de.latlon.ets.wms13.core.dgiwg.testsuite.getcapabilities;

import static de.latlon.ets.core.assertion.ETSAssert.assertContentType;
import static de.latlon.ets.core.assertion.ETSAssert.assertStatusCode;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.BBOX_PARAM;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.CRS_PARAM;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.FORMAT_PARAM;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.GET_MAP;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.HEIGHT_PARAM;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.IMAGE_PNG;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.LAYERS_PARAM;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.REQUEST_PARAM;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.SERVICE_PARAM;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.SERVICE_TYPE_CODE;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.STYLES_PARAM;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.VERSION;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.VERSION_PARAM;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.WIDTH_PARAM;
import static de.latlon.ets.wms13.core.domain.ProtocolBinding.GET;
import static de.latlon.ets.wms13.core.util.ServiceMetadataUtils.getOperationEndpoint;
import static de.latlon.ets.wms13.core.util.ServiceMetadataUtils.parseRequestableLayerNodes;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.text.DecimalFormat;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.testng.ITestContext;
import org.testng.SkipException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.jersey.api.client.ClientResponse;

import de.latlon.ets.core.error.ErrorMessage;
import de.latlon.ets.core.error.ErrorMessageKey;
import de.latlon.ets.wms13.core.client.WmsKvpRequest;
import de.latlon.ets.wms13.core.crs.CrsMatcher;
import de.latlon.ets.wms13.core.domain.BoundingBox;
import de.latlon.ets.wms13.core.util.ServiceMetadataUtils;

/**
 * Tests if the Layer supports the required CRS.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class GetCapabilitiesLayerCrsTest extends AbstractBaseGetCapabilitiesFixture {

    private static final CrsMatcher CRS_MATCHER = new CrsMatcher();

    @DataProvider(name = "layerNodes")
    public Object[][] parseLayerNodes( ITestContext testContext )
                            throws XPathFactoryConfigurationException, XPathExpressionException {
        if ( this.wmsCapabilities == null )
            initBaseFixture( testContext );
        NodeList layerNodes = parseRequestableLayerNodes( wmsCapabilities );
        Object[][] layers = new Object[layerNodes.getLength()][];
        for ( int layerNodeIndex = 0; layerNodeIndex < layerNodes.getLength(); layerNodeIndex++ ) {
            Node layerNode = layerNodes.item( layerNodeIndex );
            String name = (String) createXPath().evaluate( "wms:Name", layerNode, XPathConstants.STRING );
            String title = (String) createXPath().evaluate( "wms:Title", layerNode, XPathConstants.STRING );
            layers[layerNodeIndex] = new Object[] { layerNode, name, title };
        }

        if ( layerNodes.getLength() <= 0 ) {
            throw new SkipException( "There are no Layers; tests skipped" );
        }

        return layers;
    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.5.4.2., S.11, Requirement 6", dataProvider = "layerNodes")
    public void wmsCapabilitiesLayerCrs_Mandatory_CRS_84_Supported( Node layerNode, String name, String title )
                            throws XPathExpressionException, XPathFactoryConfigurationException {
        assertCrs( layerNode, "CRS:84" );
    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.5.4.2., S.11, Requirement 6", dataProvider = "layerNodes")
    public void wmsCapabilitiesLayerCrs_Mandatory_EPSG_4326_Supported( Node layerNode, String name, String title )
                            throws XPathExpressionException, XPathFactoryConfigurationException {
        assertCrs( layerNode, "EPSG:4326" );
    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.5.4.2., S.11, Requirement 6", dataProvider = "layerNodes")
    public void wmsCapabilitiesLayerCrs_Mandatory_EPSG_3395_Supported( Node layerNode, String name, String title )
                            throws XPathExpressionException, XPathFactoryConfigurationException {
        assertCrs( layerNode, "EPSG:3395" );
    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.5.4.2., S.11, Requirement 6", dataProvider = "layerNodes")
    public void wmsCapabilitiesLayerCrs_Conditional_Supported( Node layerNode, String name, String title )
                            throws XPathExpressionException, XPathFactoryConfigurationException {
        BoundingBox geographicBoundingBox = ServiceMetadataUtils.parseGeographicBoundingBox( layerNode );
        List<String> conditionalExpectedCrs = CRS_MATCHER.retrieveOverlappingCrs( geographicBoundingBox );

        for ( String expectedCrs : conditionalExpectedCrs ) {
            assertCrs( layerNode, expectedCrs );
        }
    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.5.4.2., S.11, Requirement 6", dataProvider = "layerNodes")
    public void wmsCapabilitiesLayerCrs_AsAdvertised( Node layerNode, String name, String title )
                            throws XPathExpressionException, XPathFactoryConfigurationException {
        String REQUEST_FORMAT = IMAGE_PNG;
        List<String> supportedFormats = ServiceMetadataUtils.parseSupportedFormats( wmsCapabilities, GET_MAP );
        if ( supportedFormats.size() > 0 ) {
            REQUEST_FORMAT = supportedFormats.get( 0 );
        }
        String xPath = "ancestor-or-self::wms:Layer/wms:CRS";
        NodeList crsList = (NodeList) createXPath().evaluate( xPath, layerNode, XPathConstants.NODESET );

        for ( int nodeIndx = 0; nodeIndx < crsList.getLength(); nodeIndx++ ) {
            Node crsNode = crsList.item( nodeIndx );
            String crs = crsNode.getTextContent();
            // assertCrs(layerNode, crs);

            ClientResponse rsp = sendGetMapCrsRequest( layerNode, name, crs, REQUEST_FORMAT );

            assertTrue( rsp.hasEntity(), ErrorMessage.get( ErrorMessageKey.MISSING_XML_ENTITY ) );
            assertStatusCode( rsp.getStatus(), 200 );
            assertContentType( rsp.getHeaders(), REQUEST_FORMAT );
        }
    }

    private ClientResponse sendGetMapCrsRequest( Node layerNode, String layerName, String crs, String REQUEST_FORMAT )
                            throws XPathExpressionException, XPathFactoryConfigurationException {
        String bbox = "-0.1, -0.1, 0.1, 0.1";
        // String layerName = layerNode.getNodeName();

        String xPathCriteria = String.format( "wms:BoundingBox[@CRS = '%s']", crs );
        Node bbxNode = (Node) createXPath().evaluate( xPathCriteria, layerNode, XPathConstants.NODE );
        // String crsFound = null;
        if ( bbxNode != null ) {
            XPath xPath = createXPath();
            // crsFound = (String)xPath.evaluate("@CRS", bbxNode, XPathConstants.STRING);

            double minx = Double.parseDouble( (String) xPath.evaluate( "@minx", bbxNode, XPathConstants.STRING ) );
            double miny = Double.parseDouble( (String) xPath.evaluate( "@miny", bbxNode, XPathConstants.STRING ) );
            double maxx = Double.parseDouble( (String) xPath.evaluate( "@maxx", bbxNode, XPathConstants.STRING ) );
            double maxy = Double.parseDouble( (String) xPath.evaluate( "@maxy", bbxNode, XPathConstants.STRING ) );

            double midx = ( minx + maxx ) / 2.0;
            double midy = ( miny + maxy ) / 2.0;

            double offsetx = Double.max( ( maxx - minx ) * 0.01, 0.1 );
            double offsety = Double.max( ( maxy - miny ) * 0.01, 0.1 );

            DecimalFormat decimalFormat = new DecimalFormat( "#.0" );
            bbox = decimalFormat.format( midx - offsetx ) + "," + decimalFormat.format( midy - offsety ) + ","
                   + decimalFormat.format( midx + offsetx ) + "," + decimalFormat.format( midy + offsety );
        }

        WmsKvpRequest kvp = new WmsKvpRequest();

        kvp.addKvp( SERVICE_PARAM, SERVICE_TYPE_CODE );
        kvp.addKvp( REQUEST_PARAM, GET_MAP );
        kvp.addKvp( VERSION_PARAM, VERSION );
        kvp.addKvp( LAYERS_PARAM, layerName );
        kvp.addKvp( CRS_PARAM, crs );
        kvp.addKvp( BBOX_PARAM, bbox );
        kvp.addKvp( FORMAT_PARAM, REQUEST_FORMAT );
        kvp.addKvp( STYLES_PARAM, "" );
        kvp.addKvp( WIDTH_PARAM, "100" );
        kvp.addKvp( HEIGHT_PARAM, "100" );

        URI endpoint = getOperationEndpoint( this.wmsCapabilities, GET_MAP, GET );
        return wmsClient.submitRequest( kvp, endpoint );
    }

    private void assertCrs( Node layerNode, String crs )
                            throws XPathExpressionException, XPathFactoryConfigurationException {
        String xPath = String.format( "ancestor-or-self::wms:Layer/wms:CRS[text() = '%s']", crs );
        Node crsNode = (Node) createXPath().evaluate( xPath, layerNode, XPathConstants.NODE );
        assertNotNull( crsNode, "CRS " + crs + " is not supported." );
    }

    private XPath createXPath()
                            throws XPathFactoryConfigurationException {
        XPathFactory factory = XPathFactory.newInstance( XPathConstants.DOM_OBJECT_MODEL );
        XPath xpath = factory.newXPath();
        xpath.setNamespaceContext( NS_BINDINGS );
        return xpath;
    }

}