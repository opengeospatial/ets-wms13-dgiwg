package de.latlon.ets.wms13.core.dgiwg.testsuite.getfeatureinfo;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class BaseGetFeatureInfoFixtureTest {

    @Test
    public void testParseFeatureMemberNodes_esri()
                            throws Exception {
        BaseGetFeatureInfoFixture baseGetFeatureInfoFixture = new BaseGetFeatureInfoFixture();
        Document documentEntity = parseDocument( "gfi-response_esri.xml" );
        NodeList nodeList = baseGetFeatureInfoFixture.parseFeatureMemberNodes( documentEntity );

        assertThat( nodeList.getLength(), is( 1 ) );
    }

    @Test
    public void testParseFeatureMemberNodes_gml()
                            throws Exception {
        BaseGetFeatureInfoFixture baseGetFeatureInfoFixture = new BaseGetFeatureInfoFixture();
        Document documentEntity = parseDocument( "gfi-response_gml.xml" );
        NodeList nodeList = baseGetFeatureInfoFixture.parseFeatureMemberNodes( documentEntity );

        assertThat( nodeList.getLength(), is( 2 ) );
    }

    @Test
    public void testParseFeatureMemberNodes_gml32()
                            throws Exception {
        BaseGetFeatureInfoFixture baseGetFeatureInfoFixture = new BaseGetFeatureInfoFixture();
        Document documentEntity = parseDocument( "gfi-response_gml32.xml" );
        NodeList nodeList = baseGetFeatureInfoFixture.parseFeatureMemberNodes( documentEntity );

        assertThat( nodeList.getLength(), is( 2 ) );
    }

    private Document parseDocument( String resource )
                            throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware( true );
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputStream wmsCapabilities = getClass().getResourceAsStream( resource );
        return builder.parse( new InputSource( wmsCapabilities ) );
    }
}
