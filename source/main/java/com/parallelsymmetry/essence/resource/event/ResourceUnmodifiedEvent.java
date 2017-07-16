package com.parallelsymmetry.essence.resource.event;

import com.parallelsymmetry.essence.resource.Resource;
import com.parallelsymmetry.essence.resource.ResourceEvent;

import static com.parallelsymmetry.essence.resource.ResourceEvent.Type.UNMODIFIED;

public class ResourceUnmodifiedEvent extends ResourceEvent {

	public ResourceUnmodifiedEvent( Object source, Resource resource ) {
		super( source, UNMODIFIED, resource );
	}

}