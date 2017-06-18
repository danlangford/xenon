package com.parallelsymmetry.essence.resource;

import com.parallelsymmetry.essence.ResourceManager;
import com.parallelsymmetry.essence.node.Node;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.tree.TreePath;
import javax.swing.undo.UndoManager;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class Resource extends Node {

	private static Logger log = LoggerFactory.getLogger( Resource.class );

	private static final String URI_VALUE_KEY = "value.uri";

	private static final String TYPE_VALUE_KEY = "value.type";

	private static final String CODEC_VALUE_KEY = "value.codec";

	private static final String SCHEME_VALUE_KEY = "value.scheme";

//	private static final String FILE_NAME_RESOURCE_KEY = "resource.file.name";
//
//	private static final String FIRST_LINE_RESOURCE_KEY = "resource.first.line";
//
//	private static final String CONTENT_TYPE_RESOURCE_KEY = "resource.content.type";

	private static final String EXTERNALLY_MODIFIED = "flag.externally.modified";

//	private static final String EDITABLE = "resource.editable";
//
//	private static final String UNDO_MANAGER = "resource.undo.manager";

	// Name is not stored in the node data, it is derived
	private String name;

	private UndoManager undoManager;

	private Set<ResourceListener> listeners;

	private volatile boolean open;

	private volatile boolean loaded;

	private volatile boolean saved;

	private volatile boolean ready;

	public Resource( URI uri ) {
		this( null, uri );
	}

	public Resource( String uri ) {
		this( null, URI.create( uri ) );
	}

	public Resource( ResourceType type ) {
		this( type, (URI)null );
	}

	public Resource( ResourceType type, String uri ) {
		this( type, URI.create( uri ) );
	}

	public Resource( ResourceType type, URI uri ) {
		if( type == null && uri == null ) throw new RuntimeException( "The type and uri cannot both be null." );

		// FIXME Finish implementing Resource constructor
		setType( type );
		setUri( uri );

		// TODO What is the FX undo manager
		//undoManager = new ResourceUndoManager();

		listeners = new CopyOnWriteArraySet<>();
		//addDataListener( new DataHandler() );
	}

	public URI getUri() {
		return getValue( URI_VALUE_KEY );
	}

	public void setUri( URI uri ) {
		setValue( URI_VALUE_KEY, uri );
		updateResourceName( uri );
	}

	public ResourceType getType() {
		return getValue( TYPE_VALUE_KEY );
	}

	public void setType( ResourceType type ) {
		setValue( TYPE_VALUE_KEY, type );
	}

	/**
	 * The codec used to load/save the resource. The codec is usually null until
	 * the resource is loaded or saved. Then the codec used for that operation is
	 * stored for convenience to be used for later load or save operations.
	 *
	 * @return
	 */
	public Codec getCodec() {
		return getValue( CODEC_VALUE_KEY );
	}

	public void setCodec( Codec codec ) {
		setValue( CODEC_VALUE_KEY, codec );
	}

	public Scheme getScheme() {
		Scheme scheme = getScheme();
		if( scheme != null ) return scheme;

		URI uri = getUri();
		if( uri == null ) return null;

		//scheme = Schemes.getScheme( uri.getScheme() );
		if( scheme == null ) throw new RuntimeException( "Scheme not registered: " + uri.getScheme() );

		setValue( SCHEME_VALUE_KEY, scheme );

		return scheme;
	}

	/**
	 * Get the name of the resource. This returns the resource type name if the
	 * URI is null, the entire URI if the path portion of the URI is null, or the
	 * file portion of the URI path.
	 *
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * A convenience method to get the file name from the URI.
	 *
	 * @return The file name from the URI.
	 */
	public String getFileName() {
		String name = null;
		URI uri = getUri();
		try {
			name = uri.toURL().getFile();
		} catch( MalformedURLException exception ) {
			log.error( "Error getting file name from: " + uri, exception );
		}

		return name;
	}

	public UndoManager getUndoManager() {
		return undoManager;
	}

		public boolean isExternallyModified() {
			return getFlag( EXTERNALLY_MODIFIED );
		}

		public void setExternallyModified( boolean modified ) {
			setFlag( EXTERNALLY_MODIFIED, modified );
		}

	public synchronized final boolean isOpen() {
		return open;
	}

	public synchronized final void open() throws ResourceException {
		if( isOpen() ) return;

		Scheme scheme = getScheme();
		if( scheme != null ) scheme.open( this );

		open = true;

		fireResourceOpened( new ResourceEvent( Resource.class, this ) );
	}

	public synchronized final boolean isLoaded() {
		return loaded;
	}

	public synchronized final void load( ResourceManager manager ) throws ResourceException {
		if( !isOpen() ) throw new ResourceException( this, "Resource must be opened to be loaded." );

		loaded = false;
		Scheme scheme = getScheme();
		if( scheme != null ) scheme.load( this, getCodec() );
		loaded = true;

		fireResourceLoaded( new ResourceEvent( Resource.class, this ) );
	}

	public synchronized final boolean isReady() {
		return ready;
	}

	public synchronized void setReady() {
		if( this.ready == true ) return;

		this.ready = true;
		notifyAll();

		fireResourceReady( new ResourceEvent( Resource.class, this ) );
	}

	public synchronized final boolean isSaved() {
		return saved;
	}

	public synchronized final void save( ResourceManager manager ) throws ResourceException {
		if( !isOpen() ) throw new ResourceException( this, "Resource must be opened to be saved." );
		if( getUri() == null ) throw new ResourceException( this, "URI must be set in order to save resource." );

		saved = false;
		Scheme scheme = getScheme();
		if( scheme != null ) scheme.save( this, getCodec() );
		saved = true;

		fireResourceSaved( new ResourceEvent( Resource.class, this ) );
	}

	public synchronized final boolean isClosed() {
		return !open;
	}

	public synchronized final void close( ResourceManager manager ) throws ResourceException {
		if( !isOpen() ) return;

		Scheme scheme = getScheme();
		if( scheme != null ) scheme.close( this );

		open = false;

		fireResourceClosed( new ResourceEvent( Resource.class, this ) );
	}

	public boolean exists() throws ResourceException {
		Scheme scheme = getScheme();
		return scheme == null ? false : scheme.exists( this );
	}

	public boolean delete() throws ResourceException {
		Scheme scheme = getScheme();
		return scheme == null ? false : scheme.delete( this );
	}

	/**
	 * Is the resource a container for other resources.
	 */
	public boolean isFolder() throws ResourceException {
		Scheme scheme = getScheme();
		return scheme == null ? false : scheme.isFolder( this );
	}

	/**
	 * Is the resource hidden.
	 */
	public boolean isHidden() throws ResourceException {
		Scheme scheme = getScheme();
		return scheme == null ? false : scheme.isHidden( this );
	}

	/**
	 * Get the child resources if this resource is a container for other
	 * resources.
	 */
	public List<Resource> listResources() throws ResourceException {
		Scheme scheme = getScheme();
		return scheme == null ? null : scheme.listResources( this );
	}

	public TreePath toTreePath() {
		List<Resource> path = new ArrayList<Resource>();

		Resource resource = this;
		while( resource != null ) {
			path.add( resource );
			resource = (Resource)resource.getParent();
		}

		Collections.reverse( path );

		return new TreePath( path.toArray( new Resource[ path.size() ] ) );
	}

	public Resource getParent() {
		// TODO Implement Resource.getParent()
		return null;
	}

	public void addResourceListener( ResourceListener listener ) {
		listeners.add( listener );
	}

	public void removeResourceListener( ResourceListener listener ) {
		listeners.remove( listener );
	}

	public void refresh() {
		fireResourceRefresh( new ResourceEvent( Resource.class, this ) );
	}

	@Override
	public int hashCode() {
		URI uri = getUri();
		if( uri == null ) return System.identityHashCode( this );
		return uri.hashCode();
	}

	@Override
	public boolean equals( Object object ) {
		if( !(object instanceof Resource) ) return false;
		Resource that = (Resource)object;

		URI thisUri = this.getUri();
		URI thatUri = that.getUri();

		if( thisUri == null && thatUri == null ) return this == that;
		if( thisUri == null || thatUri == null ) return false;

		return thisUri.equals( thatUri );
	}

	@Override
	public String toString() {
		URI uri = getUri();
		ResourceType type = getType();
		String resourceTypeName = type == null? "Unknown resource type" : type.getName();
		return uri == null ? resourceTypeName : uri.toString();
	}

	protected void fireResourceOpened( ResourceEvent event ) {
		for( ResourceListener listener : listeners ) {
			listener.resourceOpened( event );
		}
	}

	protected void fireResourceLoaded( ResourceEvent event ) {
		for( ResourceListener listener : listeners ) {
			listener.resourceLoaded( event );
		}
	}

	protected void fireResourceReady( ResourceEvent event ) {
		for( ResourceListener listener : listeners ) {
			listener.resourceReady( event );
		}
	}

	protected void fireResourceSaved( ResourceEvent event ) {
		for( ResourceListener listener : listeners ) {
			listener.resourceSaved( event );
		}
	}

	protected void fireResourceClosed( ResourceEvent event ) {
		for( ResourceListener listener : listeners ) {
			listener.resourceClosed( event );
		}
	}

	protected void fireResourceModified( ResourceEvent event ) {
		for( ResourceListener listener : listeners ) {
			listener.resourceModified( event );
		}
	}

	protected void fireResourceUnmodified( ResourceEvent event ) {
		for( ResourceListener listener : listeners ) {
			listener.resourceUnmodified( event );
		}
	}

	protected void fireResourceRefresh( ResourceEvent event ) {
		for( ResourceListener listener : listeners ) {
			listener.resourceRefreshed( event );
		}
	}

	private void updateResourceName( URI uri ) {
		String name = null;
		String path = null;

		// If the URI is null return the type name.
		if( name == null && uri == null ) name = getType().getName();

		// If the path is null return the entire URI.
		if( name == null && uri != null ) {
			path = uri.getPath();
			if( StringUtils.isEmpty( path ) ) name = uri.toString();
		}

		// Get the folder name from the path.
		if( name == null && path != null ) {
			try {
				if( isFolder() ) {
					if( path.endsWith( "/" ) ) path = path.substring( 0, path.length() - 1 );
					name = path.substring( path.lastIndexOf( '/' ) + 1 );
				}
			} catch( ResourceException exception ) {
				// Intentionally ignore exception.
			}
		}

		// Return just the name from the path.
		if( name == null && path != null ) name = path.substring( path.lastIndexOf( '/' ) + 1 );

		this.name = name;
	}

	//	private class DataHandler extends DataAdapter {
	//
	//		@Override
	//		public void metaAttributeChanged( MetaAttributeEvent event ) {
	//			if( DataNode.MODIFIED == event.getAttributeName() ) {
	//				if( Boolean.TRUE == event.getNewValue() ) {
	//					fireResourceModified( new ResourceEvent( this, (Resource)event.getSender() ) );
	//				} else {
	//					fireResourceUnmodified( new ResourceEvent( this, (Resource)event.getSender() ) );
	//				}
	//			}
	//		}
	//
	//	}

}