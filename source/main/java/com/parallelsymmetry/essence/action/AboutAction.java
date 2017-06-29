package com.parallelsymmetry.essence.action;

import com.parallelsymmetry.essence.Action;
import com.parallelsymmetry.essence.Program;
import com.parallelsymmetry.essence.resource.Resource;
import com.parallelsymmetry.essence.worktool.Tool;
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
			resource = program.getResourceManager().createResource( URI.create( "program:about" ) );
			program.getResourceManager().open( resource );
		}

		// NEXT Open the resource

		// TODO Open the about tool
		Tool tool = program.getToolManager().getEditTool( resource );
		if( tool == null ) return;

		program.getWorkspaceManager().getActiveWorkspace().getActiveWorkarea().getWorkpane().addTool( tool, true );
	}

}
