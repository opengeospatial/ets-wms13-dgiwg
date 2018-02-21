package de.latlon.ets.wms13.core.dgiwg.testsuite.getmap;

import static de.latlon.ets.wms13.core.domain.DGIWGWMS.GET_MAP;
import static de.latlon.ets.wms13.core.util.request.WmsRequestBuilder.getSupportedTransparentFormat;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;

import org.testng.ITestContext;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;

import com.sun.jersey.api.client.ClientResponse;

import de.latlon.ets.core.util.TestSuiteLogger;
import de.latlon.ets.wms13.core.client.WmsKvpRequest;
import de.latlon.ets.wms13.core.dgiwg.testsuite.AbstractBaseGetFixture;
import de.latlon.ets.wms13.core.util.request.WmsRequestBuilder;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class BaseGetMapFixture extends AbstractBaseGetFixture {

    private final String MIME_FILENAME = "mime.types";

    static final String SUBDIRECTORY = "GetMapTests";

    private Path imageDirectory;

    /**
     * Builds a {@link WmsKvpRequest} representing a GetMap request.
     */
    @BeforeClass
    public void buildGetMapRequest() {
        this.reqEntity = WmsRequestBuilder.buildGetMapRequest( wmsCapabilities, layerInfo );
    }

    @BeforeClass
    public void setResultDirectory( ITestContext testContext ) {
        String outputDirectory = retrieveSessionDir( testContext );
        TestSuiteLogger.log( Level.INFO, "Directory to store GetMap responses: " + outputDirectory );
        try {
            Path resultDir = Paths.get( outputDirectory );
            imageDirectory = createDirectory( resultDir, SUBDIRECTORY );
        } catch ( IOException e ) {
            TestSuiteLogger.log( Level.WARNING, "Could not create directory for GetMap response.", e );
        }
    }

    /**
     * If an image format supporting transparency is not supported by the WMS a {@link SkipException} is thrown.
     * 
     * @return image format supporting transparency, never <code>null</code>
     */
    protected String findRequiredImageFormatWithTransparencySupport() {
        String imageFormat = getSupportedTransparentFormat( wmsCapabilities, GET_MAP );
        if ( imageFormat == null )
            throw new SkipException( "WMS does not support an image format supporting transparency." );
        return imageFormat;
    }

    /**
     * Stores the image in a the output directory of the testsuite:
     * testSUiteOutputDirectory/testGroup/testName.extension
     * 
     * @param rsp
     *            containing the image, rsp.getEntityInputStream() is used to retrieve the content as stream, never
     *            <code>null</code>
     * @param testGroup
     *            name of the test group (will be the name of the directory to create), never <code>null</code>
     * @param testName
     *            name of the test (will be the name of the file to create), never <code>null</code>
     * @param requestFormat
     *            the mime type of the image, never <code>null</code>
     */

    protected void storeResponseImage( ClientResponse rsp, String testGroup, String testName, String requestFormat ) {
        if ( imageDirectory == null ) {
            TestSuiteLogger.log( Level.WARNING,
                                 "Directory to store GetMap responses is not set. GetMap response is not written!" );
            return;
        }
        writeIntoFile( rsp, testGroup, testName, requestFormat );
    }

    private void writeIntoFile( ClientResponse rsp, String testGroup, String testName, String requestFormat ) {
        try {
            Path testClassDirectory = createDirectory( imageDirectory, testGroup );
            InputStream imageStream = rsp.getEntityInputStream();

            String fileExtension = detectFileExtension( requestFormat );
            if ( ( fileExtension != null ) && ( !fileExtension.startsWith( "." ) ) ) {
                fileExtension = "." + fileExtension;
            }

            String fileName = testName + fileExtension;
            Path imageFile = testClassDirectory.resolve( fileName );
            Integer indx = -1;
            while ( Files.exists( imageFile, java.nio.file.LinkOption.NOFOLLOW_LINKS ) ) {
                fileName = testName + ( ++indx ).toString() + fileExtension;
                imageFile = testClassDirectory.resolve( fileName );
            }

            Files.copy( imageStream, imageFile );
        } catch ( IOException ioe ) {
            TestSuiteLogger.log( Level.WARNING, "IO:  Writing the GetMap response into file failed.", ioe );
        }
    }

    private String detectFileExtension( String requestFormat ) {
        String extension = null;
        try {
            BufferedReader br = new BufferedReader(
                                                    new InputStreamReader(
                                                                           this.getClass().getResourceAsStream( MIME_FILENAME ),
                                                                           "UTF-8" ) );
            String mimeLine = null;

            do {
                mimeLine = br.readLine();

                if ( ( mimeLine != null ) && ( mimeLine.indexOf( ':' ) > 0 ) ) {
                    int indx = mimeLine.indexOf( ':' );
                    String mime = mimeLine.substring( 0, indx );
                    String m_ext = mimeLine.substring( indx + 1 );

                    if ( mime.equalsIgnoreCase( requestFormat ) ) {
                        extension = m_ext;
                    }
                }
            } while ( ( mimeLine != null ) && ( extension == null ) );
            br.close();
        } catch ( IOException e ) {
            TestSuiteLogger.log( Level.WARNING, "Cannot find MIME Types.", e );
        }
        /*
         * --- these calls were part of the original code but do not work in this instance of TEAMEngine //- MimeTypes
         * allTypes = MimeTypes.getDefaultMimeTypes(this.getClass().getClassLoader()); //- MimeType mimeType =
         * allTypes.forName(requestFormat); //- result = mimeType.getExtension(); ---
         */
        return extension;
    }

    private Path createDirectory( Path parent, String child )
                            throws IOException {
        Path testClassDirectory = parent.resolve( child );
        Files.createDirectories( testClassDirectory );
        return testClassDirectory;
    }

    /**
     * Gets the location of the output directory from the test run context.
     * 
     * @param testContext
     *            Information about a test run.
     * @return A String that identifies the directory containing test run results.
     */
    String retrieveSessionDir( ITestContext testContext ) {
        File outputDir = new File( testContext.getOutputDirectory() );
        // --- the following code crashes in Eclipse environment since no UUID is established with the
        // directory/subdirectory name
        /*
         * UUID testRunId = null; try { testRunId = UUID.fromString( outputDir.getName() ); } catch (
         * IllegalArgumentException e ) { // test suite name was appended to path outputDir = //
         * outputDir.getParentFile(); testRunId = UUID.fromString( outputDir.getName() ); } if ( null == testRunId ) {
         * throw new RuntimeException( "Unable to locate test run output directory: " + testContext.getOutputDirectory()
         * ); }
         */
        return outputDir.getPath();
    }

}