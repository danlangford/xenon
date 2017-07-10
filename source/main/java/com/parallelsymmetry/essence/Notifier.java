package com.parallelsymmetry.essence;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputControl;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Notifier {

	private static final Logger log = LoggerFactory.getLogger( Notifier.class );

	private Program program;

	public Notifier( Program program ) {
		this.program = program;
	}

	public void error( Object message ) {
		error( null, message, null );
	}

	/* Error methods */

	public void error( Throwable throwable ) {
		error( null, null, throwable );
	}

	public void error( String title, Throwable throwable ) {
		error( title, null, throwable );
	}

	public void error( Throwable throwable, Object message, String... parameters ) {
		error( null, throwable, message, parameters );
	}

	public void error( String title, Throwable throwable, Object message, String... parameters ) {
		alert( Alert.AlertType.ERROR, null, title, formatMessage( throwable, message ), parameters );
	}

	/* Warning methods */

	public void warning( Object message, String... parameters ) {
		warning( null, message, parameters );
	}

	public void warning( String title, Object message, String... parameters ) {
		alert( Alert.AlertType.WARNING, null, title, message, parameters );
	}

	/* Notify methods */

	public void notify( Object message, String... parameters ) {
		notify( null, message, parameters );
	}

	public void notify( String title, Object message, String... parameters ) {
		alert( Alert.AlertType.INFORMATION, null, title, message, parameters );
	}

	private Object formatMessage( Throwable throwable, Object message ) {
		String string = message == null ? null : message.toString();
		log.error( string, throwable );

		if( message == null && throwable != null ) return throwable.getLocalizedMessage();
		return message;
	}

	private void alert( Alert.AlertType type, Node graphic, String title, Object message, String... parameters ) {
		try {
			StringBuilder builder = new StringBuilder();

			if( message instanceof Node ) {
				if( message instanceof TextInputControl ) {
					// Handle text input controls
					builder = new StringBuilder( ((TextInputControl)message).getText() );
				} else if( message instanceof TextFlow ) {
					// Handle text flow nodes
					TextFlow flow = (TextFlow)message;
					for( Node node : flow.getChildren() ) {
						Text text = (Text)node;
						builder.append( text.getText() );
					}
				} else {
					builder = new StringBuilder( message.toString() );
				}
			} else {
				builder = new StringBuilder( message == null ? "null" : message.toString().trim() );
			}

			final String content = String.format( builder.toString(), parameters );

			log.info( content );

			Platform.runLater( () -> {
				Alert alert = new Alert( Alert.AlertType.NONE );
				if( message instanceof Node ) alert.getDialogPane().setContent( (Node)message );
				if( type != null ) alert.setAlertType( type );
				if( title != null ) alert.setTitle( title );
				if( graphic != null ) alert.setGraphic( graphic );
				alert.setContentText( content );
				alert.initOwner( program.getWorkspaceManager().getActiveWorkspace().getStage() );
				alert.show();
			} );
		} catch( Throwable throwable ) {
			throwable.printStackTrace( System.out );
		}
	}

}