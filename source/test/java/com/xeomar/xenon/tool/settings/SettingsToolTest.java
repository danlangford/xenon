package com.xeomar.xenon.tool.settings;

import com.xeomar.xenon.FxProgramTestCase;
import com.xeomar.xenon.resource.Resource;
import com.xeomar.xenon.resource.type.ProgramGuideType;
import com.xeomar.xenon.resource.type.ProgramSettingsType;
import com.xeomar.xenon.tool.settings.SettingsTool;
import com.xeomar.xenon.workarea.Workpane;
import com.xeomar.xenon.workarea.WorkpaneEvent;
import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class SettingsToolTest extends FxProgramTestCase {

	@Test
	public void testGetRequiredToolResources() {
		Resource resource = new Resource( ProgramSettingsType.URI );
		SettingsTool tool = new SettingsTool( program, resource );
		Set<String> resources = tool.getResourceDependencies();
		Assert.assertThat( resources, containsInAnyOrder( ProgramGuideType.URI ) );
	}

	@Test
	public void testOpenTool() throws Exception {
		Workpane pane = program.getWorkspaceManager().getActiveWorkspace().getActiveWorkarea().getWorkpane();
		assertThat( pane.getTools().size(), is( 0 ) );

		clickOn( "#menu-edit" );
		clickOn( "#menuitem-settings" );
		workpaneWatcher.waitForEvent( WorkpaneEvent.Type.TOOL_ADDED );
		workpaneWatcher.waitForEvent( WorkpaneEvent.Type.TOOL_ADDED );
		assertThat( pane.getActiveTool(), instanceOf( SettingsTool.class ) );
		assertThat( pane.getTools().size(), is( 2 ) );
	}

	@Test
	public void testOpenToolTwice() throws Exception {
		Workpane pane = program.getWorkspaceManager().getActiveWorkspace().getActiveWorkarea().getWorkpane();
		assertThat( pane.getTools().size(), is( 0 ) );

		clickOn( "#menu-edit" );
		clickOn( "#menuitem-settings" );
		workpaneWatcher.waitForEvent( WorkpaneEvent.Type.TOOL_ADDED );
		workpaneWatcher.waitForEvent( WorkpaneEvent.Type.TOOL_ADDED );
		assertThat( pane.getActiveTool(), instanceOf( SettingsTool.class ) );
		assertThat( pane.getTools().size(), is( 2 ) );

		clickOn( "#menu-edit" );
		clickOn( "#menuitem-settings" );
		workpaneWatcher.waitForEvent( WorkpaneEvent.Type.TOOL_ACTIVATED );
		assertThat( pane.getTools().size(), is( 2 ) );
	}

	@Test
	public void testClosingResourceWillCloseTool() throws Exception {
		Workpane pane = program.getWorkspaceManager().getActiveWorkspace().getActiveWorkarea().getWorkpane();
		assertThat( pane.getTools().size(), is( 0 ) );

		program.getResourceManager().open( program.getResourceManager().createResource( ProgramSettingsType.URI ) );
		workpaneWatcher.waitForEvent( WorkpaneEvent.Type.TOOL_ADDED );
		workpaneWatcher.waitForEvent( WorkpaneEvent.Type.TOOL_ADDED );
		assertThat( pane.getActiveTool(), instanceOf( SettingsTool.class ) );
		assertThat( pane.getTools().size(), is( 2 ) );

		Resource resource = program.getResourceManager().createResource( ProgramSettingsType.URI );
		program.getResourceManager().closeResources( resource );
		workpaneWatcher.waitForEvent( WorkpaneEvent.Type.TOOL_REMOVED );
		assertThat( pane.getTools().size(), is( 1 ) );
	}

}