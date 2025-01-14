package com.avereon.xenon.resource;

import java.net.URLConnection;
import java.util.List;

/**
 * The Scheme class represents a URI scheme in the context of a {@link Resource} . See <a href="http://en.wikipedia.org/wiki/URI_scheme">URI_scheme on Wikipedia</a> for more information regarding URI schemes. Scheme, {@link ResourceType}
 * and {@link Codec} are used together by the resource manager to manage resources.
 * <p>
 * The scheme is responsible for defining and implementing how a resource is handled for connection and transport purposes. The scheme is solely responsible for the connection and data transfer of the resource, not for interpreting the
 * content of the resource.
 *
 * @author SoderquistMV
 */
public interface Scheme {

	String RESOURCE_LAST_SAVED_KEY = "resource.last.saved";

	/**
	 * Get the scheme name. The scheme name is equivalent to the URI scheme defined in <a href="http://tools.ietf.org/html/rfc3986">RFC-3986</a>.
	 *
	 * @return The resource name
	 */
	String getName();

	/**
	 * Get the root resources associated with this scheme.
	 *
	 * @return The list of root resources for this scheme.
	 * @throws ResourceException If an error occurs
	 */
	List<Resource> getRoots() throws ResourceException;

	/**
	 * Determines whether the specified resource can be loaded.
	 *
	 * @param resource The resource to check
	 * @return If the resource can be loaded
	 * @throws ResourceException If an error occurs
	 */
	boolean canLoad( Resource resource ) throws ResourceException;

	/**
	 * Determines whether the specified resource can be saved.
	 *
	 * @param resource The resource to check
	 * @return If the resource can be saved
	 * @throws ResourceException If an error occurs
	 */
	boolean canSave( Resource resource ) throws ResourceException;

	/**
	 * Initialize the {@link Resource}. This is called from a <code>Resource</code> when the <code>Scheme</code> is set.
	 *
	 * @param resource The resource to init.
	 * @throws ResourceException If an error occurs
	 */
	void init( Resource resource ) throws ResourceException;

	/**
	 * Open the {@link Resource}.
	 *
	 * @param resource The resource to open
	 * @throws ResourceException If an error occurs
	 */
	void open( Resource resource ) throws ResourceException;

	/**
	 * Load the {@link Resource}.
	 *
	 * @param resource The resource to load
	 * @param codec The codec to use to load the resource
	 * @throws ResourceException If an error occurs
	 */
	void load( Resource resource, Codec codec ) throws ResourceException;

	/**
	 * Save the {@link Resource}.
	 *
	 * @param resource The resource to save
	 * @param codec The codec to use to save the resource
	 * @throws ResourceException If an error occurs
	 */
	void save( Resource resource, Codec codec ) throws ResourceException;

	/**
	 * Close the {@link Resource}.
	 *
	 * @param resource The resource to close
	 * @throws ResourceException If an error occurs
	 */
	void close( Resource resource ) throws ResourceException;

	/**
	 * Determine if the resource exists. If the correct value cannot be determined an exception is thrown.
	 *
	 * @param resource The resource to verify exists
	 * @return true If the resource exists, false otherwise
	 * @throws ResourceException If the correct value cannot be determined
	 */
	boolean exists( Resource resource ) throws ResourceException;

	/**
	 * Create the external resource that the resource represents.
	 *
	 * @param resource The resource to create
	 * @return true If the external source is created, false otherwise
	 * @throws ResourceException If an error occurs during the operation
	 */
	boolean create( Resource resource ) throws ResourceException;

	/**
	 * Save the resource as a different resource.
	 *
	 * @param resource The resource to save
	 * @param target The destination resource
	 * @throws ResourceException If the resource can not be saved
	 */
	void saveAs( Resource resource, Resource target ) throws ResourceException;

	/**
	 * Rename the resource as a different resource.
	 *
	 * @param resource The resource to rename
	 * @param target The destination resource
	 * @throws ResourceException If the resource can not be renamed
	 */
	boolean rename( Resource resource, Resource target ) throws ResourceException;

	/**
	 * Delete the resource.
	 *
	 * @param resource The resource to delete
	 * @return true If and only if the resource is successfully deleted, false otherwise
	 * @throws ResourceException If an error occurred during deletion
	 */
	boolean delete( Resource resource ) throws ResourceException;

	/**
	 * Determine if the resource a folder for other resources.
	 */
	boolean isFolder( Resource resource ) throws ResourceException;

	/**
	 * Determine if the resource is hidden.
	 */
	boolean isHidden( Resource resource ) throws ResourceException;

	/**
	 * Get the child resources if this resource is a folder.
	 */
	List<Resource> listResources( Resource resource ) throws ResourceException;

	/**
	 * Get the size of the resource in bytes.
	 *
	 * @param resource The resource from which to get the size
	 * @return The size of the resource in bytes
	 * @throws ResourceException If the size can not be determined
	 */
	long getSize( Resource resource ) throws ResourceException;

	/**
	 * Get the modified date.
	 *
	 * @return The last date the resource was modified
	 * @throws ResourceException If the date can not be determined
	 */
	long getModifiedDate( Resource resource ) throws ResourceException;

	/**
	 * Get a connection for a resource
	 */
	URLConnection getConnection( Resource resource );

}
