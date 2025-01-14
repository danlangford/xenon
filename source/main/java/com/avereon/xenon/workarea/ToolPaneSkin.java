package com.avereon.xenon.workarea;

import com.avereon.util.LogUtil;
import com.avereon.xenon.util.FxUtil;
import javafx.collections.ListChangeListener;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.SkinBase;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import org.slf4j.Logger;

import java.lang.invoke.MethodHandles;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ToolPaneSkin extends SkinBase<ToolPane> {

	private static final Logger log = LogUtil.get( MethodHandles.lookup().lookupClass() );

	private Pane header;

	private Pane toolArea;

	public static final double SIDE_PERCENT = 0.3;

	public static final double MINIMUM_PIXELS = 50;

	protected ToolPaneSkin( ToolPane control ) {
		super( control );

		Rectangle clipRect = new Rectangle( control.getWidth(), control.getHeight() );
		registerChangeListener( control.widthProperty(), event -> clipRect.setWidth( getSkinnable().getWidth() ) );
		registerChangeListener( control.heightProperty(), event -> clipRect.setHeight( getSkinnable().getHeight() ) );
		getSkinnable().setClip( clipRect );

		control.getTabs().forEach( tab -> tab.setToolPane( control ) );
		Set<Tool> tools = control.getTabs().stream().map( ToolTab::getTool ).peek( tool -> tool.setVisible( false ) ).collect( Collectors.toSet() );

		HBox tabContainer = new HBox();
		tabContainer.getStyleClass().add( "box" );
		tabContainer.getChildren().addAll( control.getTabs() );

		// Create a separate pane to capture drop target events in the header space
		Pane headerDrop = new Pane();
		headerDrop.getStyleClass().addAll( "tool-tab-drop" );

		// Create components
		header = new BorderPane( headerDrop, null, null, null, tabContainer );
		header.getStyleClass().addAll( "tool-pane-header-area" );

		toolArea = new ToolContentArea();
		toolArea.getChildren().addAll( tools );
		toolArea.getStyleClass().addAll( "tool-pane-content-area" );

		getChildren().setAll( header, toolArea );

		control.getTabs().addListener( (ListChangeListener<ToolTab>)change -> {
			while( change.next() ) {
				change.getRemoved().stream().filter( Objects::nonNull ).forEach( tab -> {
					if( !getSkinnable().getTabs().contains( tab ) ) tab.setToolPane( null );
					toolArea.getChildren().remove( tab.getTool() );
					// Tab is removed below
				} );

				change.getAddedSubList().stream().filter( Objects::nonNull ).forEach( tab -> {
					tab.setToolPane( getSkinnable() );
					// Tab is added below
					toolArea.getChildren().add( tab.getTool() );
				} );

				if( change.wasRemoved() ) tabContainer.getChildren().removeAll( change.getRemoved() );
				if( change.wasAdded() ) tabContainer.getChildren().addAll( change.getFrom(), change.getAddedSubList() );
			}

			getSkinnable().requestLayout();
		} );

		control.getSelectionModel().selectedItemProperty().addListener( ( observable, oldValue, newValue ) -> {
			getSkinnable().getTabs().stream().map( ToolTab::getTool ).forEach( tool -> tool.setVisible( false ) );
			ToolTab selectedTab = getSkinnable().getSelectionModel().getSelectedItem();
			if( selectedTab != null ) selectedTab.getTool().setVisible( true );
			getSkinnable().requestLayout();
		} );

		headerDrop.setOnMouseClicked( ( event ) -> {
			if( event.getClickCount() == 2 ) {
				WorkpaneView view = getSkinnable().getWorkpaneView();
				view.getWorkpane().setMaximizedView( view.isMaximized() ? null : view );
			}
		} );

		headerDrop.setOnDragEntered( ( event ) -> {
			log.warn( "Drag enter header: " + event.getDragboard().getUrl() );

			Bounds bounds = FxUtil.localToParent( headerDrop, getSkinnable().getWorkpane() );
			getSkinnable().getWorkpane().setDropHint( new WorkpaneDropHint( bounds ) );

			event.consume();
		} );

		headerDrop.setOnDragOver( ( event ) -> {
			event.acceptTransferModes( TransferMode.MOVE, TransferMode.COPY );
			event.consume();
		} );

		headerDrop.setOnDragExited( ( event ) -> {
			log.warn( "Drag exit header: " + event.getDragboard().getUrl() );

			getSkinnable().getWorkpane().setDropHint( null );
			event.consume();
		} );

		headerDrop.setOnDragDropped( ( event ) -> {
			log.warn( "Drag dropped on tool header: " + event.getDragboard().getUrl() + ": " + event.getAcceptedTransferMode() );
			control.handleDrop( event, -1, null );
		} );

		toolArea.setOnDragEntered( ( event ) -> {
			event.consume();
		} );

		toolArea.setOnDragOver( ( event ) -> {
			event.acceptTransferModes( TransferMode.COPY_OR_MOVE );

			Bounds dropBounds = getDropBounds( toolArea.getLayoutBounds(), getDropSide( event ) );
			Bounds dropHintBounds = FxUtil.localToParent( toolArea, getSkinnable().getWorkpane(), dropBounds );
			getSkinnable().getWorkpane().setDropHint( new WorkpaneDropHint( dropHintBounds ) );

			event.consume();
		} );

		toolArea.setOnDragExited( ( event ) -> {
			getSkinnable().getWorkpane().setDropHint( null );
			event.consume();
		} );

		toolArea.setOnDragDropped( ( event ) -> {
			control.handleDrop( event, -2, getDropSide( event ) );
		} );

		ToolTab selectedTab = getSkinnable().getSelectionModel().getSelectedItem();
		if( selectedTab != null ) selectedTab.getTool().setVisible( true );
	}

	private double getDropHintWidth( Bounds bounds ) {
		return Math.min( MINIMUM_PIXELS, SIDE_PERCENT * bounds.getWidth() );
	}

	private double getDropHintHeight( Bounds bounds ) {
		return Math.min( MINIMUM_PIXELS, SIDE_PERCENT * bounds.getHeight() );
	}

	private Side getDropSide( DragEvent event ) {
		Side position = null;

		Bounds bounds = ((Node)event.getSource()).getLayoutBounds();

		double dropWidth = getDropHintWidth( bounds );
		double dropHeight = getDropHintHeight( bounds );

		double northDistance = event.getY() - bounds.getMinY();
		double southDistance = bounds.getMinY() + bounds.getHeight() - event.getY();
		double eastDistance = bounds.getMinX() + bounds.getWidth() - event.getX();
		double westDistance = event.getX() - bounds.getMinX();

		// The following checks should be in this order: south, north, east, west
		if( southDistance > 0 && southDistance < dropHeight ) {
			position = Side.BOTTOM;
		}
		if( northDistance > 0 && northDistance < dropHeight ) {
			position = Side.TOP;
		}
		if( eastDistance > 0 && eastDistance < dropWidth ) {
			position = Side.RIGHT;
		}
		if( westDistance > 0 && westDistance < dropWidth ) {
			position = Side.LEFT;
		}

		return position;
	}

	private Bounds getDropBounds( Bounds bounds, Side side ) {
		if( side == null ) return bounds;

		double dropWidth = getDropHintWidth( bounds );
		double dropHeight = getDropHintHeight( bounds );

		switch( side ) {
			case LEFT: {
				return new BoundingBox( 0, 0, dropWidth, bounds.getHeight() );
			}
			case RIGHT: {
				return new BoundingBox( bounds.getWidth() - dropWidth, 0, dropWidth, bounds.getHeight() );
			}
			case TOP: {
				return new BoundingBox( 0, 0, bounds.getWidth(), dropHeight );
			}
			case BOTTOM: {
				return new BoundingBox( 0, bounds.getHeight() - dropHeight, bounds.getWidth(), dropHeight );
			}
		}

		return bounds;
	}

	@Override
	protected double computeMinWidth( double height, double topInset, double rightInset, double bottomInset, double leftInset ) {
		return 0;
	}

	@Override
	protected double computeMinHeight( double width, double topInset, double rightInset, double bottomInset, double leftInset ) {
		return 0;
	}

	@Override
	protected void layoutChildren( double contentX, double contentY, double contentWidth, double contentHeight ) {
		double headerSize = snapSizeY( header.prefHeight( -1 ) );
		header.resizeRelocate( contentX, contentY, contentWidth, headerSize );

		double toolHeight = contentHeight - headerSize;
		toolArea.resizeRelocate( contentX, headerSize, contentWidth, toolHeight );
	}

	private class ToolContentArea extends Pane {

		ToolContentArea() {
			Rectangle clipRect = new Rectangle( this.getWidth(), getHeight() );
			registerChangeListener( widthProperty(), event -> clipRect.setWidth( getSkinnable().getWidth() ) );
			registerChangeListener( heightProperty(), event -> clipRect.setHeight( getSkinnable().getHeight() ) );
			setClip( clipRect );
		}

		@Override
		protected void layoutChildren() {
			getChildren().forEach( child -> child.resize( getWidth(), getHeight() ) );
		}

	}

}
