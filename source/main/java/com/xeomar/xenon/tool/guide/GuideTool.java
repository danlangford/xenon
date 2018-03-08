package com.xeomar.xenon.tool.guide;

import com.xeomar.xenon.ProgramProduct;
import com.xeomar.xenon.resource.Resource;
import com.xeomar.xenon.tool.ProgramTool;
import com.xeomar.xenon.workarea.*;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class GuideTool extends ProgramTool {

	private TreeView guideView;

	private SelectedItemListener selectedItemListener;

	private ActiveGuideListener activeGuideListener;

	public GuideTool( ProgramProduct product, Resource resource ) {
		super( product, resource );
		setId( "tool-guide" );
		setGraphic( product.getProgram().getIconLibrary().getIcon( "guide" ) );
		setTitle( product.getResourceBundle().getString( "tool", "guide-name" ) );
		getChildren().add( guideView = new TreeView() );
		guideView.setShowRoot( false );
	}

	@Override
	public Workpane.Placement getPlacement() {
		return Workpane.Placement.DOCK_LEFT;
	}

	@Override
	@SuppressWarnings( "unchecked" )
	protected void resourceReady( ToolParameters parameters ) throws ToolException {
		// Connect to the resource guide
		Guide guide = getResource().getResource( Guide.GUIDE_KEY );
		if( guide == null ) return;

		// Set guide tree root
		TreeItem root = guide.getRoot();
		if( root != null ) guideView.setRoot( root );
	}

	@Override
	protected void resourceRefreshed() {
		// Update the guide? Or will the guide be updated by the resource?
	}

	@Override
	protected void allocate() throws ToolException {
		// Attach to the workpane and listen for current tool changes
		getWorkpane().addWorkpaneListener( this::switchGuide );

		Tool activeTool = getWorkpane().getActiveTool();
		if( activeTool != null ) setResourceGuide( activeTool.getResource() );
	}

	@Override
	protected void deallocate() throws ToolException {
		// Disconnect from the resource guide
	}

	private void switchGuide( WorkpaneEvent event ) {
		if( !(event instanceof WorkpaneToolEvent) ) return;

		WorkpaneToolEvent toolEvent = (WorkpaneToolEvent)event;
		switch( event.getType() ) {
			case TOOL_ACTIVATED: {
				setResourceGuide( toolEvent.getTool().getResource() );
				break;
			}
			case TOOL_CONCEALED: {
				setResourceGuide( null );
			}
		}
	}

	@SuppressWarnings( "unchecked" )
	private void setResourceGuide( Resource resource ) {
		if( resource == null ) {
			guideView.setRoot( null );
			return;
		}

		Guide guide = resource.getResource( Guide.GUIDE_KEY );
		if( guide == null ) return;

		// Set the guide view root
		guideView.setRoot( guide.getRoot() );

		// Set the guide view selection mode
		guideView.getSelectionModel().setSelectionMode( guide.getSelectionMode() );

		// Add the selected item listener
		if( selectedItemListener != null ) guideView.getSelectionModel().selectedItemProperty().removeListener( selectedItemListener );
		guideView.getSelectionModel().selectedItemProperty().addListener( selectedItemListener = new SelectedItemListener( guide ) );

		// Add the guide active property listener
		if( activeGuideListener != null ) guide.activeProperty().removeListener( activeGuideListener );
		guide.activeProperty().addListener( activeGuideListener = new ActiveGuideListener( guide ) );
	}

	private static class SelectedItemListener implements javafx.beans.value.ChangeListener<TreeItem<GuideNode>> {

		private Guide guide;

		SelectedItemListener( Guide guide ) {
			this.guide = guide;
		}

		@Override
		@SuppressWarnings( "unchecked" )
		public void changed( ObservableValue<? extends TreeItem<GuideNode>> observable, TreeItem<GuideNode> oldSelection, TreeItem<GuideNode> newSelection ) {
			guide.setSelectedItem( newSelection );
		}

	}

	private class ActiveGuideListener implements javafx.beans.value.ChangeListener<Boolean> {

		private Guide guide;

		ActiveGuideListener( Guide guide ) {
			this.guide = guide;
		}

		@Override
		@SuppressWarnings( "unchecked" )
		public void changed( ObservableValue observable, Boolean oldSelection, Boolean newSelection ) {
			if( newSelection ) {
				guideView.setRoot( guide.getRoot() );
			} else {
				guideView.setRoot( null );
			}
		}

	}

}