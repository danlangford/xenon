package com.xeomar.xenon.notice;

import com.xeomar.util.HashUtil;
import com.xeomar.util.LogUtil;
import com.xeomar.xenon.node.Node;
import org.slf4j.Logger;

import java.lang.invoke.MethodHandles;

public class Notice extends Node {

	private static final Logger log = LogUtil.get( MethodHandles.lookup().lookupClass() );

	private static final String ID = "id";

	private static final String TIMESTAMP = "timestamp";

	// TODO How severe/important is this message ???
	// Is this just a range 1-N? Or are these named?
	private static final String SEVERITY = "severity";

	// TODO What type of message is this message ???
	// Similar to the Alert.AlertType: NONE, INFORMATION, WARNING, CONFIRMATION, ERROR
	private static final String TYPE = "type";

	private static final String TITLE = "title";

	private static final String MESSAGE = "message";

	private static final String ACTION = "action";

	private static final String READ = "read";

	public Notice( String title, String message ) {
		this( title, message, null );
	}

	public Notice( String title, String message, Runnable action ) {
		definePrimaryKey( ID );
		defineNaturalKey( TITLE );

		setValue( ID, HashUtil.hash( title + message ) );
		setValue( TIMESTAMP, System.currentTimeMillis() );
		setValue( TITLE, title );
		setValue( MESSAGE, message );
		setValue( ACTION, action );
		setFlag( READ, false );
		setModified( false );
	}

	public String getId() {
		return getValue( ID );
	}

	public Long getTimestamp() {
		return getValue( TIMESTAMP );
	}

	public String getTitle() {
		return getValue( TITLE );
	}

	public String getMessage() {
		return getValue( MESSAGE );
	}

	public Runnable getAction() {
		return getValue( ACTION );
	}

	public boolean isRead() {
		return getFlag( READ );
	}

	public void setRead( boolean read ) {
		setFlag( READ, read );
	}

}