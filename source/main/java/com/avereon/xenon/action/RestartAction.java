package com.avereon.xenon.action;

import com.avereon.xenon.Action;
import com.avereon.xenon.Program;
import javafx.event.ActionEvent;

public class RestartAction extends Action {

	public RestartAction( Program program ) {
		super( program );
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public void handle( ActionEvent event ) {
		try {
			getProgram().requestRestart();
		} catch( Throwable throwable ) {
			log.error( "Error requesting restart", throwable );
		}
	}

}
