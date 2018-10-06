package com.xeomar.xenon.tool.welcome;

import com.xeomar.xenon.workarea.Workpane;
import com.xeomar.xenon.workarea.WorkpaneEvent;
import org.junit.Test;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class WelcomeToolOpenTwiceTest extends WelcomeToolTest {

	@Test
	public void execute() throws Exception {
		Workpane pane = program.getWorkspaceManager().getActiveWorkspace().getActiveWorkarea().getWorkpane();
		assertThat( pane.getTools().size(), is( 0 ) );

		clickOn( "#menu-help" );
		clickOn( "#menuitem-welcome" );
		workpaneWatcher.waitForEvent( WorkpaneEvent.Type.TOOL_ADDED );
		assertThat( pane.getActiveTool(), instanceOf( WelcomeTool.class ) );
		assertThat( pane.getActiveView().isMaximized(), is( true ) );
		assertThat( pane.getTools().size(), is( 1 ) );

		clickOn( "#menu-help" );
		clickOn( "#menuitem-welcome" );
		workpaneWatcher.waitForEvent( WorkpaneEvent.Type.TOOL_ACTIVATED );
		assertThat( pane.getActiveTool(), instanceOf( WelcomeTool.class ) );
		assertThat( pane.getActiveView().isMaximized(), is( true ) );
		assertThat( pane.getTools().size(), is( 1 ) );
	}

}
