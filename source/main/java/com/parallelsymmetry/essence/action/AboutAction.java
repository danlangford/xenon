package com.parallelsymmetry.essence.action;

import com.parallelsymmetry.essence.Action;
import com.parallelsymmetry.essence.Program;
import com.parallelsymmetry.essence.resource.Resource;
import javafx.event.Event;

import java.net.URI;

public class AboutAction extends Action {

	private Resource resource;

	public AboutAction( Program program ) {
		super( program );
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public void handle( Event event ) {
		if( resource == null ) {
			try {
				resource = program.getResourceManager().createResource( URI.create( "program:about" ) );
				//program.getResourceManager().loadResourcesAndWait( resource );
				program.getResourceManager().open( resource );
			} catch( Exception exception ) {
				log.warn( "Error opening about resource", exception );
			}
		}

//		// FIXME These lines should be in ResourceManager or ToolManager
//		ProductTool tool = program.getToolManager().getTool( resource );
//		program.getWorkspaceManager().getActiveWorkspace().getActiveWorkarea().addTool( tool );
	}

}
