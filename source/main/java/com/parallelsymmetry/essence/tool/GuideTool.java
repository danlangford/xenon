package com.parallelsymmetry.essence.tool;

import com.parallelsymmetry.essence.Program;
import com.parallelsymmetry.essence.ProgramTool;
import com.parallelsymmetry.essence.resource.Resource;
import com.parallelsymmetry.essence.workarea.Workpane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class GuideTool extends ProgramTool {

	private TreeView guide;

	private TreeItem root;

	public GuideTool( Program program, Resource resource ) {
		super( program, resource );
		guide = new TreeView(  );
		getChildren().add( guide );
	}

	@Override
	protected void resourceRefreshed() {
		// When the resource is refreshed
		// Had the resource guide been modified?

	}

	@Override
	public Workpane.Placement getPlacement() {
		return Workpane.Placement.DOCK_LEFT;
	}

}
