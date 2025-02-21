package de.latlon.ets.wms13.core.client;

import java.net.URI;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.transform.Source;

import org.apache.tika.io.FilenameUtils;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.logging.LoggingFeature;
import org.w3c.dom.Document;

import de.latlon.ets.core.util.XMLUtils;
import de.latlon.ets.wms13.core.domain.DGIWGWMS;
import de.latlon.ets.wms13.core.domain.ProtocolBinding;
import de.latlon.ets.wms13.core.util.SOAPMessageConsumer;
import de.latlon.ets.wms13.core.util.ServiceMetadataUtils;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

/**
 * A WMS 1.3.0 client component supporting HTTP GET and POST.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class WmsClient {

    private static final Logger LOGR = Logger.getLogger( WmsClient.class.getPackage().getName() );

    private Client client;

    /** A Document that describes the service under test. */
    private Document wmsCapabilities;

    /**
     * Constructs a client that is aware of the capabilities of some WMS. The request and response may be logged to a
     * default JDK logger (in the namespace "com.sun.jersey.api.client").
     * 
     * @param wmsCapabilities
     *            the WMS capabilities document, never <code>null</code>
     */
    public WmsClient( Document wmsCapabilities ) {
        ClientConfig config = new ClientConfig();
        config.property(ClientProperties.FOLLOW_REDIRECTS, true);
        config.property(ClientProperties.CONNECT_TIMEOUT, 10000);
        config.register(new LoggingFeature(LOGR, Level.ALL, LoggingFeature.Verbosity.PAYLOAD_ANY, 5000));
        config.getClasses().add( SOAPMessageConsumer.class );
        this.client = ClientBuilder.newClient(config);
        this.wmsCapabilities = wmsCapabilities;
    }

    /**
     * Retrieves a complete representation of the capabilities document from the WMS implementation described by the
     * service metadata.
     * 
     * @return a document containing the response to a GetCapabilities request
     */
    public Document getCapabilities() {
        if ( null == this.wmsCapabilities ) {
            throw new IllegalStateException( "Service description is unavailable." );
        }
        URI endpoint = ServiceMetadataUtils.getOperationEndpoint( this.wmsCapabilities, DGIWGWMS.GET_CAPABILITIES,
                                                                  ProtocolBinding.GET );
        if (null == endpoint) {
            throw new RuntimeException("GetCapabilities (GET) endpoint not found in capabilities document.");
        }
        MultivaluedMap<String, String> queryParams = new MultivaluedHashMap<>();
        queryParams.add( DGIWGWMS.REQUEST_PARAM, DGIWGWMS.GET_CAPABILITIES );
        queryParams.add( DGIWGWMS.SERVICE_PARAM, DGIWGWMS.SERVICE_TYPE_CODE );
        queryParams.add( DGIWGWMS.VERSION_PARAM, DGIWGWMS.VERSION );
        UriBuilder uriBuilder = UriBuilder.fromUri(endpoint);
        if (null != queryParams) {
                for (Entry<String, List<String>> param : queryParams.entrySet()) {
                        uriBuilder.queryParam(param.getKey(), param.getValue());
                }
        }
        URI uri = uriBuilder.build();
        WebTarget target = this.client.target(uri);
        Builder reqBuilder = target.request();
        return reqBuilder.buildGet().invoke().readEntity(Document.class);
    }

    /**
     * Submits a HTTP GET request.
     * 
     * @param request
     *            the KVP encoded request, never <code>null</code>
     * @param endpoint
     *            the service endpoint, never <code>null</code>
     * @return the {@link Response} object representing the response message
     */
    public Response submitRequest( WmsKvpRequest request, URI endpoint ) {
        WebTarget target = this.client.target(endpoint);
        return submitGetRequest( target, request );
    }

    /**
     * Submits a HTTP POST request.
     * 
     * @param payload
     *            the payload in XML format
     * @param endpoint
     *            the service endpoint
     * @return the response message
     */
    public Response submitRequest( Source payload, URI endpoint ) {
        if ( payload == null || endpoint == null )
            throw new IllegalArgumentException( "Neither payload nor endpoint must be null" );
        WebTarget target = this.client.target(endpoint);
        return submitPostRequest( target, payload );
    }

    private Response submitPostRequest( WebTarget target, Source payload ) {
        LOGR.log( Level.FINE, String.format( "Submitting POST request to URI %s", target.getUri() ) );
        LOGR.log( Level.FINE, String.format( "Request Payload: %s", XMLUtils.transformToString( payload ) ) );
        Response response = null;
        try {            
            response = target.request(DGIWGWMS.SOAP_XML).buildPost(Entity.entity(payload, DGIWGWMS.SOAP_XML)).invoke();
            if ( LOGR.isLoggable( Level.FINE ) ) {
                LOGR.log( Level.FINE, String.format( "SOAP Response: %s", FilenameUtils.normalize(response.toString()) ) );
            }
        } catch ( ProcessingException ex ) {
            LOGR.log( Level.SEVERE, "Failed to process SOAP request/response: " + target.getUri(), ex );
        }
        return response;
    }

    private Response submitGetRequest( WebTarget target, WmsKvpRequest requestParameter ) {
        LOGR.log( Level.FINE, String.format( "Submitting GET request to URI %s", target.getUri() ) );
        String queryString = requestParameter.asQueryString();
        URI requestURI = UriBuilder.fromUri( target.getUri() ).replaceQuery( queryString ).build();
        LOGR.log( Level.FINE, String.format( "Request URI: %s", requestURI ) );
        target = this.client.target(requestURI);
        Builder reqBuilder = target.request();
        Invocation req = reqBuilder.buildGet();        
        return req.invoke();
    }

}