package com.avereon.xenon.scheme;

import com.avereon.xenon.Program;
import com.avereon.xenon.resource.Codec;
import com.avereon.xenon.resource.Resource;
import com.avereon.xenon.resource.ResourceException;

import java.io.IOException;

public class ProgramScheme extends BaseScheme {

	public ProgramScheme( Program program ) {
		super( program );
	}

	@Override
	public String getName() {
		return "program";
	}

	@Override
	public boolean exists( Resource resource ) {
		return true;
	}

	@Override
	public void load( Resource resource, Codec codec ) throws ResourceException {
		if( codec != null ) {
			try {
				codec.load( resource, null );
			} catch( IOException exception ) {
				throw new ResourceException( resource,  "Unable to load " + resource.getUri(), exception );
			}
		}
	}

	@Override
	public void save( Resource resource, Codec codec ) throws ResourceException {
		if( codec != null ) {
			try {
				codec.save( resource, null );
			} catch( IOException exception ) {
				throw new ResourceException( resource,  "Unable to save resource", exception );
			}
		}
	}

}
