package de.latlon.ets.wms13.core.dgiwg.testsuite.getcapabilities;

import static de.latlon.ets.wms13.core.util.ServiceMetadataUtils.parseRequestableLayerNodes;
import static org.testng.Assert.assertTrue;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.testng.ITestContext;
import org.testng.SkipException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Tests if the Layer contains the expected attributes.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class GetCapabilitiesLayerAttributesTest extends AbstractBaseGetCapabilitiesFixture {

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

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.6.2.3., S.17, Requirement 22", dataProvider = "layerNodes")
    public void wmsCapabilitiesLayerAttributesExists( Node layerNode, String name, String title )
                            throws XPathExpressionException, XPathFactoryConfigurationException {
        // String attributes = "@queryable and @cascaded and @opaque and @noSubsets and @fixedWidth and @fixedHeight";
        // assertXPath( attributes, layerNode, NS_BINDINGS );

        NamedNodeMap nl = layerNode.getAttributes();
        int length = nl.getLength();
        for ( int i = 0; i < length; i++ ) {
            Attr attr = (Attr) nl.item( i );
            String attrName = attr.getName().toLowerCase();
            String attrValue = attr.getValue();

            assertTrue( ( attrName.equals( "queryable" ) || attrName.equals( "cascaded" ) || attrName.equals( "opaque" )
                          || attrName.equals( "nosubsets" ) || attrName.equals( "fixedwidth" ) || attrName.equals( "fixedheight" ) ),
                        String.format( "Unexpected Attribute %s in Layer %s.  Only allowable attributes are @queryable, @cascaded, @opaque, @noSubsets, @fixedWidt,h and @fixedHeight.",
                                       attrName, layerNode.getNodeName() ) );

            assertTrue( ( ( Integer.valueOf( attrValue ) == 0 ) || ( Integer.valueOf( attrValue ) == 1 ) ),
                        String.format( "Unexpected value of %s for Attribute %s in Layer %s.  Expecting values of  0 (default) or 1",
                                       attrValue, attrName, layerNode.getNodeName() ) );
        }
    }

    private XPath createXPath()
                            throws XPathFactoryConfigurationException {
        XPathFactory factory = XPathFactory.newInstance( XPathConstants.DOM_OBJECT_MODEL );
        XPath xpath = factory.newXPath();
        xpath.setNamespaceContext( NS_BINDINGS );
        return xpath;
    }

}