package de.latlon.ets.wms13.core.domain.dimension;

/**
 * The parsed text value of a Dimension element, prepared to retrieve a value for a GetMap
 * request.
 *
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public interface RequestableDimension {

	/**
	 * @return a string representation of a requestable value, never <code>null</code>
	 */
	String retrieveRequestableValue();

}