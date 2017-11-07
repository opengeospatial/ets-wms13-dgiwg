package de.latlon.ets.wms13.core.dgiwg.testsuite.getmap;

import static java.nio.file.Files.createTempDirectory;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.junit.Test;
import org.testng.ISuite;
import org.testng.ITestContext;

import com.sun.jersey.api.client.ClientResponse;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class BaseGetMapFixtureTest {

    private static final String TEST_CLASS = "testClass";

    private static final String TEST_NAME = "testName";

    @Test
    public void test()
                            throws Exception {
        BaseGetMapFixture baseGetMapFixture = new BaseGetMapFixture();
        Path reportDirectory = createReportDirectory();
        baseGetMapFixture.setResultDirectory( testContext( reportDirectory ) );

        ClientResponse rsp = createResponse();
        baseGetMapFixture.storeResponseImage( rsp, TEST_CLASS, TEST_NAME, "image/png" );

        Path imageDirectory = reportDirectory.resolve( BaseGetMapFixture.SUBDIRECTORY );
        assertTrue( Files.exists( imageDirectory ) );
        assertTrue( Files.isDirectory( imageDirectory ) );

        Path testClassDirectory = imageDirectory.resolve( TEST_CLASS );
        assertTrue( Files.exists( testClassDirectory ) );
        assertTrue( Files.isDirectory( testClassDirectory ) );

        Path imageFile = testClassDirectory.resolve( TEST_NAME + ".png" );
        assertTrue( Files.exists( imageFile ) );
        assertTrue( Files.isRegularFile( imageFile ) );
    }

    private ClientResponse createResponse() {
        ClientResponse mockedResponse = mock( ClientResponse.class );
        InputStream imageStream = BaseGetMapFixtureTest.class.getResourceAsStream( "latlon_bg4.png" );
        when( mockedResponse.getEntityInputStream() ).thenReturn( imageStream );
        return mockedResponse;
    }

    private ITestContext testContext( Path testNgReportDir )
                            throws IOException {
        ISuite mockedSuite = mock( ISuite.class );
        String testNgReportDirFile = testNgReportDir.toFile().getAbsolutePath();
        when( mockedSuite.getOutputDirectory() ).thenReturn( testNgReportDirFile );
        ITestContext mockedTestContext = mock( ITestContext.class );
        when( mockedTestContext.getSuite() ).thenReturn( mockedSuite );
        when( mockedTestContext.getOutputDirectory() ).thenReturn( testNgReportDirFile );
        return mockedTestContext;
    }

    private Path createReportDirectory()
                            throws IOException {
        Path tempDirectory = createTempDirectory( "BaseGetMapFixtureTest" );
        return tempDirectory.resolve( UUID.randomUUID().toString() );
    }
}