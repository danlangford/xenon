package com.avereon.xenon.notice;

import com.avereon.util.LogUtil;
import com.avereon.xenon.Program;
import com.avereon.xenon.task.Task;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.slf4j.Logger;

import java.lang.invoke.MethodHandles;

public class NoticePane extends GridPane {

	private static final Logger log = LogUtil.get( MethodHandles.lookup().lookupClass() );

	private Program program;

	private Notice notice;

	private Node closeIcon;

	public NoticePane( Program program, Notice notice, boolean flyout ) {
		this.program = program;
		this.notice = notice;
		this.getStyleClass().addAll( flyout ? "notice-flyout" : "notice", "padded" );

		Node noticeIcon = program.getIconLibrary().getIcon( "notice" );
		Label title = new Label( notice.getTitle() );
		closeIcon = program.getIconLibrary().getIcon( "close" );
		Label message = new Label( notice.getMessage() );
		message.setWrapText( true );

		noticeIcon.getStyleClass().addAll( "padded" );
		closeIcon.getStyleClass().addAll( "padded" );
		title.getStyleClass().addAll( "notice-title" );
		message.getStyleClass().addAll( "notice-message" );

		GridPane.setConstraints( noticeIcon, 1, 1 );
		GridPane.setConstraints( title, 2, 1 );
		GridPane.setHgrow( title, Priority.SOMETIMES );
		GridPane.setConstraints( closeIcon, 3, 1 );
		GridPane.setConstraints( message, 2, 2 );
		GridPane.setColumnSpan( message, 2 );

		getChildren().addAll( noticeIcon, title, closeIcon, message );
	}

	public Notice getNotice() {
		return notice;
	}

	public void executeNoticeAction() {
		Runnable action = notice.getAction();
		if( action == null ) return;
		program.getTaskManager().submit( Task.of( "", action ) );
	}

	public Node getCloseButton() {
		return closeIcon;
	}

}
