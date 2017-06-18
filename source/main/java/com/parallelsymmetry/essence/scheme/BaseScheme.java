package com.parallelsymmetry.essence.scheme;

import com.parallelsymmetry.essence.Program;
import com.parallelsymmetry.essence.resource.Codec;
import com.parallelsymmetry.essence.resource.Resource;
import com.parallelsymmetry.essence.resource.ResourceException;
import com.parallelsymmetry.essence.resource.Scheme;

import java.net.URI;
import java.util.List;

public abstract class BaseScheme implements Scheme {

	protected Program program;

	public BaseScheme( Program program ) {
		this.program = program;
	}

	@Override
	public boolean canLoad( Resource resource ) throws ResourceException {
		return false;
	}

	@Override
	public boolean canSave( Resource resource ) throws ResourceException {
		return false;
	}

	@Override
	public void init( Resource resource ) throws ResourceException {};

	@Override
	public void open( Resource resource ) throws ResourceException {};

	@Override
	public void load( Resource resource, Codec codec ) throws ResourceException {};

	@Override
	public void save( Resource resource, Codec codec ) throws ResourceException {};

	@Override
	public void close( Resource resource ) throws ResourceException {};

	@Override
	public boolean create( Resource resource ) throws ResourceException {
		return false;
	}

	/**
	 * Does the resource exist according to the scheme. Must return true for a
	 * resource to be loaded.
	 */
	@Override
	public boolean exists( Resource resource ) throws ResourceException {
		return false;
	}

	@Override
	public void saveAs( Resource resource, Resource aoDestination ) throws ResourceException {}

	@Override
	public boolean rename( Resource resource, Resource aoDestination ) throws ResourceException {
		return false;
	}

	@Override
	public boolean delete( Resource resource ) throws ResourceException {
		return false;
	}

	@Override
	public boolean isFolder( Resource resource ) throws ResourceException {
		return false;
	}

	@Override
	public boolean isHidden( Resource resource ) throws ResourceException {
		return false;
	}

	@Override
	public List<Resource> getRoots() throws ResourceException {
		return null;
	}

	@Override
	public List<Resource> listResources( Resource resource ) throws ResourceException {
		return null;
	}

	@Override
	public long getSize( Resource resource ) throws ResourceException {
		return -1;
	}

	@Override
	public long getModifiedDate( Resource resource ) throws ResourceException {
		return -1;
	}

	protected boolean isSupported( Resource resource ) {
		URI uri = resource.getUri();
		return uri == null || uri.getScheme().equals( getName() );
	}

}