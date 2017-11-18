package com.xeomar.xenon.update;

import com.xeomar.product.Product;
import com.xeomar.xenon.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class DownloadTask extends Task<Download> {

	private static final Logger log = LoggerFactory.getLogger( DownloadTask.class );

	public static final int DEFAULT_CONNECT_TIMEOUT = 2000;

	public static final int DEFAULT_READ_TIMEOUT = 10000;

	private Product product;

	private URI uri;

	private File target;

	private int connectTimeout = DEFAULT_CONNECT_TIMEOUT;

	private int readTimeout = DEFAULT_READ_TIMEOUT;

	private Set<DownloadListener> listeners;

	public DownloadTask( Product product, URI uri ) {
		this( product, uri, null );
		listeners = new CopyOnWriteArraySet<>();
	}

	public DownloadTask( Product product, URI uri, File target ) {
		super( product.getResourceBundle().getString( "prompt", "download" ) + " " + uri.toString() );
		this.uri = uri;
		this.target = target;
	}

	public URI getUri() {
		return uri;
	}

	@Override
	public Download call() throws IOException {
		return download();
	}

	private Download download() throws IOException {
		URLConnection connection = uri.toURL().openConnection();
		connection.setConnectTimeout( connectTimeout );
		connection.setReadTimeout( readTimeout );
		connection.setUseCaches( false );
		connection.connect();

		int length = connection.getContentLength();
		String encoding = connection.getContentEncoding();
		InputStream input = connection.getInputStream();

		setMinimum( 0 );
		setMaximum( length );

		byte[] buffer = new byte[ 8192 ];
		Download download = new Download( uri, length, encoding, target );

		try {
			int read = 0;
			int offset = 0;
			while( (read = input.read( buffer )) > -1 ) {
				if( isCancelled() ) return null;
				download.write( buffer, 0, read );
				offset += read;
				setProgress( offset );
				fireEvent( new DownloadEvent( offset, length ) );
			}
			if( isCancelled() ) return null;
		} finally {
			download.close();
		}

		log.trace( "Resource downloaded: " + uri );
		log.debug( "        to location: " + download.getTarget() );

		return download;
	}

	public void addListener( DownloadListener listener ) {
		listeners.add( listener );
	}

	public void removeListener( DownloadListener listener ) {
		listeners.remove( listener );
	}

	private void fireEvent( DownloadEvent event ) {
		for( DownloadListener listener : new HashSet<>( listeners ) ) {
			try {
				listener.update( event );
			} catch( Throwable throwable ) {
				log.error( "Error updating download progress", throwable );
			}
		}
	}

}