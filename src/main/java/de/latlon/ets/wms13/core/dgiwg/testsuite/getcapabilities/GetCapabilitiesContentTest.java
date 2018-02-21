package de.latlon.ets.wms13.core.dgiwg.testsuite.getcapabilities;

import static de.latlon.ets.core.assertion.ETSAssert.assertXPath;

import org.testng.SkipException;
import org.testng.annotations.Test;

import de.latlon.ets.core.assertion.ETSAssert;

/**
 * Tests if the capabilities provides all mandatory and optional service metadata elements.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class GetCapabilitiesContentTest extends AbstractBaseGetCapabilitiesFixture {

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.6.2.2., S.13, Requirement 9")
    public void wmsCapabilitiesNameExists() {
        String xPathXml = "//wms:WMS_Capabilities/wms:Service/wms:Name/text() != ''";
        assertXPath( xPathXml, wmsCapabilities, NS_BINDINGS );
    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.6.2.2., S.13, Requirement 9")
    public void wmsCapabilitiesTitleExists() {
        String xPathXml = "//wms:WMS_Capabilities/wms:Service/wms:Title/text() != ''";
        assertXPath( xPathXml, wmsCapabilities, NS_BINDINGS );
    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.6.2.2., S.13, Requirement 9")
    public void wmsCapabilitiesOnlineResourceExists() {
        String xPathXml = "//wms:WMS_Capabilities/wms:Service/wms:OnlineResource/@xlink:href != ''";
        assertXPath( xPathXml, wmsCapabilities, NS_BINDINGS );
    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.6.2.2., S.13, Requirement 9")
    public void wmsCapabilitiesAbstractExists() {
        String xPathXml = "//wms:WMS_Capabilities/wms:Service/wms:Abstract";
        skipIfNoOptionalMetadata( xPathXml );

        xPathXml += "/text() != ''";
        skipIfNoOptionalMetadata( xPathXml );

        assertXPath( xPathXml, wmsCapabilities, NS_BINDINGS );
    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.6.2.2., S.13, Requirement 9")
    public void wmsCapabilitiesKeywordListExists() {
        String xPathXml = "//wms:WMS_Capabilities/wms:Service/wms:KeywordList";
        skipIfNoOptionalMetadata( xPathXml );

        xPathXml += "/text() != ''";
        skipIfNoOptionalMetadata( xPathXml );

        assertXPath( xPathXml, wmsCapabilities, NS_BINDINGS );
    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.6.2.2., S.13, Requirement 9")
    public void wmsCapabilitiesContactInformationExists() {
        String xPathXml = "//wms:WMS_Capabilities/wms:Service/wms:ContactInformation";
        skipIfNoOptionalMetadata( xPathXml );

        xPathXml += "/text() != ''";
        skipIfNoOptionalMetadata( xPathXml );

        assertXPath( xPathXml, wmsCapabilities, NS_BINDINGS );
    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.6.2.2., S.13, Requirement 9")
    public void wmsCapabilitiesFeesExists() {
        String xPathXml = "//wms:WMS_Capabilities/wms:Service/wms:Fees";
        skipIfNoOptionalMetadata( xPathXml );

        xPathXml += "/text() != ''";
        skipIfNoOptionalMetadata( xPathXml );

        assertXPath( xPathXml, wmsCapabilities, NS_BINDINGS );
    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.6.2.2., S.13, Requirement 9")
    public void wmsCapabilitiesAccessConstraintsExists() {
        String xPathXml = "//wms:WMS_Capabilities/wms:Service/wms:AccessConstraints";
        skipIfNoOptionalMetadata( xPathXml );

        xPathXml += "/text() != ''";
        skipIfNoOptionalMetadata( xPathXml );

        assertXPath( xPathXml, wmsCapabilities, NS_BINDINGS );
    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.6.2.2., S.13, Requirement 9")
    public void wmsCapabilitiesLayerLimitExists() {
        String xPathXml = "//wms:WMS_Capabilities/wms:Service/wms:LayerLimit";
        skipIfNoOptionalMetadata( xPathXml );

        xPathXml += "/text() != ''";
        skipIfNoOptionalMetadata( xPathXml );

        assertXPath( xPathXml, wmsCapabilities, NS_BINDINGS );
    }

    private void skipIfNoOptionalMetadata( String xPath ) {
        boolean metadataEvaluates = ETSAssert.checkXPath( xPath, wmsCapabilities, NS_BINDINGS );
        if ( !metadataEvaluates ) {
            if ( xPath.endsWith( "''" ) )
                throw new SkipException(
                                         "The WMS contains the optional capabilities metadata but is blank, tests are skipped!" );
            else
                throw new SkipException( "The WMS does not contain optional capabilities metadata, tests are skipped!" );
        }
    }

}