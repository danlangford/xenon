package com.xeomar.xenon.tool.product;

import com.xeomar.xenon.task.Task;
import com.xeomar.product.RepoCard;
import com.xeomar.product.RepoCardComparator;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.List;

class RefreshProductRepos extends Task<Void> {

	private ProductTool productTool;

	RefreshProductRepos( ProductTool productTool ) {this.productTool = productTool;}

	@Override
	public Void call() {
		List<RepoCard> cards = new ArrayList<>( productTool.getProgram().getProductManager().getRepos() );
		cards.sort( new RepoCardComparator( RepoCardComparator.Field.NAME ) );
		cards.sort( new RepoCardComparator( RepoCardComparator.Field.RANK ) );
		Platform.runLater( () -> productTool.getRepoPage().setRepos( cards ) );
		return null;
	}

}
