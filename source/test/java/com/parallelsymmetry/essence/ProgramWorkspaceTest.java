package com.parallelsymmetry.essence;

import com.parallelsymmetry.essence.event.ProgramStartedEvent;
import com.parallelsymmetry.essence.testutil.FxTestCase;
import javafx.stage.Stage;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ProgramWorkspaceTest extends FxTestCase {

	@Test
	public void testWorkspaceDefaultWindowSize() throws Exception {
		waitForEvent( ProgramStartedEvent.class );
		Stage stage = program.getWorkspaceManager().getActiveWorkspace().getStage();

		assertThat( stage.getWidth(), is( 960d ) );
		assertThat( stage.getHeight(), is( 540d ) );
	}

	@Test
	public void testWorkspaceWindowTitle() throws Exception {
		waitForEvent( ProgramStartedEvent.class );
		String workareaName = program.getWorkspaceManager().getActiveWorkspace().getActiveWorkarea().getName();
		assertThat( program.getWorkspaceManager().getActiveWorkspace().getStage().getTitle(), is( workareaName + " - " + metadata.getName() ) );
	}

}
