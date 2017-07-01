package com.parallelsymmetry.essence;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import org.slf4j.Logger;

public abstract class Action<T extends ActionEvent> implements EventHandler<T> {

	protected static Logger log = LogUtil.get( Action.class );

	protected Program program;

	public Action( Program program ) {
		this.program = program;
	}

	public abstract boolean isEnabled();

}
