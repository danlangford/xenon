package com.avereon.xenon.workspace;

import javafx.stage.Stage;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ProgramWorkspaceDefaultSceneSizeUIT extends ProgramWorkspaceUIT {

	@Test
	public void execute() {
		Stage stage = program.getWorkspaceManager().getActiveStage();
		assertThat( stage.isShowing(), is( true ) );
		assertThat( stage.getScene().getWidth(), is( 960d ) );
		assertThat( stage.getScene().getHeight(), is( 540d ) );
	}

}
