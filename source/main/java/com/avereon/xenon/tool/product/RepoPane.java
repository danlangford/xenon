package com.avereon.xenon.tool.product;

import com.avereon.product.RepoCard;
import com.avereon.xenon.Program;
import com.avereon.xenon.UiFactory;
import com.avereon.xenon.task.Task;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.tbee.javafx.scene.layout.MigPane;

class RepoPane extends MigPane {

	private ProductTool productTool;

	private RepoCard source;

	private Label iconLabel;

	private Label nameLabel;

	private Label repoLabel;

	private Button enableButton;

	private Button removeButton;

	public RepoPane( ProductTool productTool, RepoCard source ) {
		super( "insets 0, gap " + UiFactory.PAD );

		this.productTool = productTool;
		this.source = source;

		setId( "tool-product-market" );

		Program program = productTool.getProgram();

		String iconUri = source.getIcon();
		Node marketIcon = program.getIconLibrary().getIcon( iconUri, "market", ProductTool.ICON_SIZE );

		iconLabel = new Label( null, marketIcon );
		iconLabel.setId( "tool-product-market-icon" );
		nameLabel = new Label( source.getName() );
		nameLabel.setId( "tool-product-market-name" );
		repoLabel = new Label( source.getRepo() );
		repoLabel.setId( "tool-product-market-uri" );

		enableButton = new Button( "", productTool.getProgram().getIconLibrary().getIcon( source.isEnabled() ? "disable" : "enable" ) );
		removeButton = new Button( "", program.getIconLibrary().getIcon( "remove" ) );

		add( iconLabel, "spany, aligny center" );
		add( nameLabel, "pushx" );
		add( enableButton );
		add( repoLabel, "newline" );
		add( removeButton );
	}

	RepoCard getSource() {
		return source;
	}

	void updateRepoState() {
		// TODO Update the repo state
		nameLabel.setDisable( !source.isEnabled() );
		repoLabel.setDisable( !source.isEnabled() );
		enableButton.setGraphic( productTool.getProgram().getIconLibrary().getIcon( source.isEnabled() ? "disable" : "enable" ) );
		//enableButton.setDisable( !source.isRemovable() );
		removeButton.setDisable( !source.isRemovable() );

		enableButton.setOnAction( ( event ) -> toggleEnabled() );
		removeButton.setOnAction( ( event ) -> removeRepo() );
	}

	private void toggleEnabled() {
		productTool.getProgram().getProductManager().setRepoEnabled( source, !productTool.getProgram().getProductManager().isRepoEnabled( source ) );
		updateRepoState();
	}

	private void removeRepo() {
		productTool.getProgram().getTaskManager().submit( Task.of( "Remove repo", () -> {
			try {
				productTool.getProgram().getProductManager().removeRepo( source );
				productTool.getSelectedPage().updateState();
			} catch( Exception exception ) {
				ProductTool.log.warn( "Error uninstalling product", exception );
			}
		} ));
	}

}
