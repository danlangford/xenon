package com.xeomar.xenon.tool;

import com.xeomar.xenon.FxProgramTestCase;
import com.xeomar.xenon.resource.Resource;
import com.xeomar.xenon.resource.type.ProgramGuideType;
import com.xeomar.xenon.resource.type.ProgramSettingsType;
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

		program.getResourceManager().open( program.getResourceManager().createResource( ProgramSettingsType.URI ) );
		// NEXT Adding time should not cause a problem, but it does
		Thread.sleep( 500 );
		workpaneWatcher.waitForEvent( WorkpaneEvent.Type.TOOL_ADDED );
		workpaneWatcher.waitForEvent( WorkpaneEvent.Type.TOOL_ADDED );

		assertThat( pane.getActiveTool(), instanceOf( SettingsTool.class ) );
		assertThat( pane.getTools().size(), is( 2 ) );
	}

//	@Test
//	public void testOpenToolTwice() throws Exception {
//				Workpane pane = program.getWorkspaceManager().getActiveWorkspace().getActiveWorkarea().getWorkpane();
//				assertThat( pane.getTools().size(), is( 0 ) );
//
//				program.getResourceManager().open( program.getResourceManager().createResource( ProgramSettingsType.URI ) );
//				workpaneWatcher.waitForEvent( WorkpaneEvent.Type.TOOL_ADDED );
//				workpaneWatcher.waitForEvent( WorkpaneEvent.Type.TOOL_ADDED );
//
//				assertThat( pane.getActiveTool(), instanceOf( SettingsTool.class ) );
//				assertThat( pane.getTools().size(), is( 2 ) );
//
//
//		program.getResourceManager().open( program.getResourceManager().createResource( ProgramSettingsType.URI ) );
////		workpaneWatcher.waitForEvent( WorkpaneEvent.Type.TOOL_ACTIVATED );
////
////		assertThat( pane.getTools().size(), is( 2 ) );
//	}

//	@Test
//	public void testClosingResourceWillCloseTool() throws Exception {
//		testOpenTool();
//		Resource resource = program.getResourceManager().createResource( ProgramSettingsType.URI );
//
//		Workpane pane = program.getWorkspaceManager().getActiveWorkspace().getActiveWorkarea().getWorkpane();
//		assertThat( pane.getActiveTool(), instanceOf( SettingsTool.class ) );
//		assertThat( pane.getTools().size(), is( 2 ) );
//
//		program.getResourceManager().closeResources( resource );
//		workpaneWatcher.waitForEvent( WorkpaneEvent.Type.TOOL_REMOVED );
//
//		assertThat( pane.getTools().size(), is( 1 ) );
//	}

}