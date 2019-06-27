package com.xeomar.xenon.workarea;

import com.xeomar.util.LogUtil;
import javafx.collections.ListChangeListener;
import javafx.scene.control.SkinBase;
import javafx.scene.input.TransferMode;
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

	private HBox header;

	private ToolContentArea toolArea;

	protected ToolPaneSkin( ToolPane control ) {
		super( control );

		Rectangle clipRect = new Rectangle( control.getWidth(), control.getHeight() );
		registerChangeListener( control.widthProperty(), event -> clipRect.setWidth( getSkinnable().getWidth() ) );
		registerChangeListener( control.heightProperty(), event -> clipRect.setHeight( getSkinnable().getHeight() ) );
		getSkinnable().setClip( clipRect );

		control.getTabs().forEach( tab -> tab.setToolPane( control ) );
		Set<Tool> tools = control.getTabs().stream().map( ToolTab::getTool ).peek( tool -> tool.setVisible( false ) ).collect( Collectors.toSet() );

		// Create components
		header = new HBox();
		header.getChildren().addAll( control.getTabs() );
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

				if( change.wasRemoved() ) header.getChildren().removeAll( change.getRemoved() );
				if( change.wasAdded() ) header.getChildren().addAll( change.getFrom(), change.getAddedSubList() );
			}

			getSkinnable().requestLayout();
		} );

		control.getSelectionModel().selectedItemProperty().addListener( ( observable, oldValue, newValue ) -> {
			getSkinnable().getTabs().stream().map( ToolTab::getTool ).forEach( tool -> tool.setVisible( false ) );
			ToolTab selectedTab = getSkinnable().getSelectionModel().getSelectedItem();
			if( selectedTab != null ) selectedTab.getTool().setVisible( true );
			getSkinnable().requestLayout();
		} );

		header.setOnDragOver( (event) -> {
			log.warn( "Drag over header: " + event.getDragboard().getUrl() );
			event.acceptTransferModes( TransferMode.COPY_OR_MOVE );
			event.consume();
		} );

		header.setOnDragEntered( (event) -> {
			log.warn( "Drag entered header: " + event.getDragboard().getUrl() );
			event.acceptTransferModes( TransferMode.COPY_OR_MOVE );
			event.consume();
		} );

		header.setOnDragExited( (event) -> {
			log.warn( "Drag exited header: " + event.getDragboard().getUrl() );
			event.acceptTransferModes( TransferMode.COPY_OR_MOVE );
			event.consume();
		} );

		control.setOnDragOver( (event) -> {
			log.warn( "Drag over tool area: " + event.getDragboard().getUrl() );
			event.acceptTransferModes( TransferMode.COPY_OR_MOVE );
			event.consume();
		} );

//		control.setOnDragDropped( (event) -> {
//			log.warn( "Drag dropped: " + event.getDragboard().getUrl() );
//			event.setDropCompleted(true );
//			event.consume();
//		} );

		ToolTab selectedTab = getSkinnable().getSelectionModel().getSelectedItem();
		if( selectedTab != null ) selectedTab.getTool().setVisible( true );
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
