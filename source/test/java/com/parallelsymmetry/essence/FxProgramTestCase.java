package com.parallelsymmetry.essence;

import com.parallelsymmetry.essence.event.ProgramStartedEvent;
import com.parallelsymmetry.essence.event.ProgramStoppedEvent;
import com.parallelsymmetry.essence.product.ProductMetadata;
import com.parallelsymmetry.essence.util.OperatingSystem;
import com.parallelsymmetry.essence.workarea.WorkpaneWatcher;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import java.io.File;
import java.io.IOException;

public abstract class FxProgramTestCase extends ApplicationTest {

	protected Program program;

	protected ProgramWatcher programWatcher;

	protected WorkpaneWatcher workpaneWatcher;

	protected ProductMetadata metadata;

	/**
	 * Override setup() in FxPlatformTestCase and does not call super.setup().
	 */
	@Before
	public void setup() throws Exception {
		// WORKAROUND The parameters defined below are null during testing due to Java 9 incompatibility
		System.setProperty( ProgramParameter.EXECMODE, ProgramParameter.EXECMODE_TEST );

		// Remove the existing program data folder
		try {
			String prefix = ExecMode.TEST.getPrefix();
			ProductMetadata metadata = new ProductMetadata();
			File programDataFolder = OperatingSystem.getUserProgramDataFolder( prefix + metadata.getArtifact(), prefix + metadata.getName() );
			if( programDataFolder != null && programDataFolder.exists() ) FileUtils.forceDelete( programDataFolder );
		} catch( IOException exception ) {
			throw new RuntimeException( exception );
		}

		program = (Program)FxToolkit.setupApplication( Program.class, ProgramParameter.EXECMODE, ProgramParameter.EXECMODE_TEST );
		program.addEventListener( programWatcher = new ProgramWatcher() );
		metadata = program.getMetadata();

		programWatcher.waitForEvent( ProgramStartedEvent.class );
		program.getWorkspaceManager().getActiveWorkspace().getActiveWorkarea().getWorkpane().addWorkpaneListener( workpaneWatcher = new WorkpaneWatcher() );
	}

	/**
	 * Override cleanup in FxPlatformTestCase and does not call super.cleanup().
	 */
	@After
	public void cleanup() throws Exception {
		FxToolkit.cleanupApplication( program );

		for( Window window : Stage.getWindows() ) {
			Platform.runLater( window::hide );
		}

		programWatcher.waitForEvent( ProgramStoppedEvent.class );
		program.removeEventListener( programWatcher );
	}

	@Override
	public void start( Stage stage ) throws Exception {}

//	protected void waitForEvent( Class<? extends ProgramEvent> clazz ) throws InterruptedException, TimeoutException {
//		programWatcher.waitForEvent( clazz );
//	}
//
//	protected void waitForEvent( Class<? extends ProgramEvent> clazz, long timeout ) throws InterruptedException, TimeoutException {
//		programWatcher.waitForEvent( clazz, timeout );
//	}
//
//	protected void waitForNextEvent( Class<? extends ProgramEvent> clazz ) throws InterruptedException, TimeoutException {
//		programWatcher.waitForNextEvent( clazz );
//	}
//
//	protected void waitForNextEvent( Class<? extends ProgramEvent> clazz, long timeout ) throws InterruptedException, TimeoutException {
//		programWatcher.waitForNextEvent( clazz, timeout );
//	}

}
