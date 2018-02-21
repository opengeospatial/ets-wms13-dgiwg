package de.latlon.ets.wms13.core.dgiwg.testsuite.getfeatureinfo;

import static de.latlon.ets.wms13.core.domain.DGIWGWMS.FEATURE_COUNT_PARAM;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.GET_FEATURE_INFO;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.testng.SkipException;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.sun.jersey.api.client.ClientResponse;

import de.latlon.ets.core.error.ErrorMessage;
import de.latlon.ets.core.error.ErrorMessageKey;
import de.latlon.ets.wms13.core.domain.DGIWGWMS;
import de.latlon.ets.wms13.core.domain.ProtocolBinding;
import de.latlon.ets.wms13.core.util.ServiceMetadataUtils;

/**
 * Tests feature count functionality of GetFeatureInfo.
 * 
 * @author <a href="mailto:stenger@lat-lon.de">Dirk Stenger</a>
 */
public class GetFeatureInfoFeatureCountTest extends BaseGetFeatureInfoFixture {

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.6.5.6, S.22, Requirement 29")
    public void wmsGetFeatureInfoFeatureCountWithValueOfOne()
                            throws XPathExpressionException, XPathFactoryConfigurationException {
        URI endpoint = ServiceMetadataUtils.getOperationEndpoint( this.wmsCapabilities, GET_FEATURE_INFO,
                                                                  ProtocolBinding.GET );
        this.reqEntity.addKvp( FEATURE_COUNT_PARAM, "1" );
        ClientResponse rsp = wmsClient.submitRequest( this.reqEntity, endpoint );
        assertTrue( rsp.hasEntity(), ErrorMessage.get( ErrorMessageKey.MISSING_XML_ENTITY ) );

        if ( isXML( rsp.getHeaders() ) ) {
            Document entity = rsp.getEntity( Document.class );
            NodeList featureMemberNodes = parseFeatureMemberNodes( entity );

            assertTrue( featureMemberNodes.getLength() == 1,
                        "FEATURE_COUNT is set to 1, but the returned number of feature members is not exactly one!" );
        } else {
            throw new SkipException( "Non-XML Format cannot be parsed here." );
        }
    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.6.5.6, S.22, Requirement 29")
    public void wmsGetFeatureInfoFeatureCountWithValueOfTen()
                            throws XPathExpressionException, XPathFactoryConfigurationException {
        URI endpoint = ServiceMetadataUtils.getOperationEndpoint( this.wmsCapabilities, GET_FEATURE_INFO,
                                                                  ProtocolBinding.GET );
        this.reqEntity.addKvp( FEATURE_COUNT_PARAM, "10" );
        ClientResponse rsp = wmsClient.submitRequest( this.reqEntity, endpoint );
        assertTrue( rsp.hasEntity(), ErrorMessage.get( ErrorMessageKey.MISSING_XML_ENTITY ) );

        if ( isXML( rsp.getHeaders() ) ) {
            Document entity = rsp.getEntity( Document.class );
            NodeList featureMemberNodes = parseFeatureMemberNodes( entity );

            assertTrue( featureMemberNodes.getLength() >= 1,
                        "FEATURE_COUNT is set to 10, but less than one feature member is returned!" );
            assertTrue( featureMemberNodes.getLength() <= 10,
                        "FEATURE_COUNT is set to 10, but more than ten feature members are returned!" );
        } else {
            throw new SkipException( "Non-XML Format cannot be parsed here." );
        }
    }

    private boolean isXML( MultivaluedMap<String, String> headers ) {
        return containsContentType( headers.get( "Content-Type" ), DGIWGWMS.TEXT_XML );
    }

    private static boolean containsContentType( List<String> contentTypes, String expectedContentType ) {
        if ( contentTypes != null )
            for ( String contentType : contentTypes ) {
                if ( contentType.contains( expectedContentType ) )
                    return true;
            }
        return false;
    }

}
