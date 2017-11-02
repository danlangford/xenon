package com.xeomar.xenon;

import com.xeomar.razor.JavaUtil;
import com.xeomar.razor.LogUtil;
import com.xeomar.razor.OperatingSystem;
import com.xeomar.xenon.action.*;
import com.xeomar.xenon.event.ProgramStartedEvent;
import com.xeomar.xenon.event.ProgramStartingEvent;
import com.xeomar.xenon.event.ProgramStoppedEvent;
import com.xeomar.xenon.event.ProgramStoppingEvent;
import com.xeomar.xenon.product.Product;
import com.xeomar.xenon.product.ProductBundle;
import com.xeomar.xenon.product.ProductMetadata;
import com.xeomar.xenon.resource.ResourceType;
import com.xeomar.xenon.resource.type.*;
import com.xeomar.xenon.scheme.FileScheme;
import com.xeomar.xenon.scheme.ProgramScheme;
import com.xeomar.xenon.settings.Settings;
import com.xeomar.xenon.task.TaskManager;
import com.xeomar.xenon.tool.AboutTool;
import com.xeomar.xenon.tool.GuideTool;
import com.xeomar.xenon.tool.WelcomeTool;
import com.xeomar.xenon.tool.settings.SettingsTool;
import com.xeomar.xenon.workspace.ToolInstanceMode;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import org.slf4j.Logger;

import java.io.File;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class Program extends Application implements Product {

	public static final String STYLESHEET = "style.css";

	private static final long MANAGER_ACTION_SECONDS = 10;

	private static Logger log = LogUtil.get( Program.class );

	private static long programStartTime = System.currentTimeMillis();

	private SplashScreen splashScreen;

	private TaskManager taskManager;

	private ProductMetadata metadata;

	private File programDataFolder;

	private Settings programSettings;

	private ExecMode execMode;

	private IconLibrary iconLibrary;

	private ProductBundle productBundle;

	private ActionLibrary actionLibrary;

	private ProgramServer programServer;

	private SettingsManager settingsManager;

	private ToolManager toolManager;

	private ResourceManager resourceManager;

	private WorkspaceManager workspaceManager;

	private ProgramEventWatcher watcher;

	private ProgramNotifier notifier;

	private Set<ProgramEventListener> listeners;

	private ExitAction exitAction;

	private AboutAction aboutAction;

	private SettingsAction settingsAction;

	private WelcomeAction welcomeAction;

	private NoticeAction noticeAction;

	public static void main( String[] commands ) {
		launch( commands );
	}

	public Program() {
		// Create program action handlers
		exitAction = new ExitAction( this );
		aboutAction = new AboutAction( this );
		settingsAction = new SettingsAction( this );
		welcomeAction = new WelcomeAction( this );
		noticeAction = new NoticeAction( this );

		// Create the listeners set
		listeners = new CopyOnWriteArraySet<>();
	}

	@Override
	public void init() throws Exception {
		time( "init" );

		// NOTE Only do in init() what has to be done before the splash screen can be shown

		// Load the product metadata
		metadata = new ProductMetadata();

		// Print the program header
		printHeader( metadata );

		// Configure logging
		LogUtil.configureLogging( this, getParameter( ProgramParameter.LOG_LEVEL ) );

		// Create the program event watcher after configuring the logging
		addEventListener( watcher = new ProgramEventWatcher() );

		// Fire the program starting event after the event watcher is created
		fireEvent( new ProgramStartingEvent( this ) );

		// Determine the program exec mode
		String prefix = getExecModePrefix();
		programDataFolder = OperatingSystem.getUserProgramDataFolder( prefix + metadata.getArtifact(), prefix + metadata.getName() );
		time( "metadata" );

		// Create the settings manager before getting the program settings
		settingsManager = new SettingsManager( this ).start();

		// Get default settings map
		Properties properties = new Properties();
		properties.load( new InputStreamReader( getClass().getResourceAsStream( "/settings/default.properties" ), "utf-8" ) );
		Map<String, String> values = new HashMap<>();
		properties.forEach( ( k, v ) -> values.put( (String)k, (String)v ) );

		// Get the program settings after the settings manager and before the task manager
		programSettings = settingsManager.getSettings( ProgramSettings.PROGRAM );
		programSettings.setDefaultValues( values );
		time( "settings" );

		// Start the program server
		// TODO Check for another instance after getting the settings but before the splash screen is shown
		// https://stackoverflow.com/questions/41051127/javafx-single-instance-application
		// The fastest way to check might be to try and bind to the port defined in
		// the settings. The OS will quickly deny the bind.
		// Call Platform.exit() if there is already an instance
		boolean singleton = programSettings.getBoolean( "shutdown-keepalive", false );
		if( singleton && !(programServer = new ProgramServer( this )).start() ) Platform.exit();

		// Create the program notifier after creating the program settings
		notifier = new ProgramNotifier( this );

		// Create the executor service
		log.trace( "Starting task manager..." );
		taskManager = new TaskManager();
		taskManager.setSettings( programSettings );
		taskManager.start();
		taskManager.awaitStart( MANAGER_ACTION_SECONDS, TimeUnit.SECONDS );
		log.debug( "Task manager started." );
		time( "taskManager" );
	}

	@Override
	public void start( Stage stage ) throws Exception {
		// Do not implicitly close the program
		Platform.setImplicitExit( false );

		// Show the splash screen
		splashScreen = new SplashScreen( metadata.getName() );
		splashScreen.show();
		time( "splash displayed" );

		// Submit the startup task
		taskManager.submit( new Startup() );
	}

	@Override
	public void stop() throws Exception {
		taskManager.submit( new Shutdown() ).get();

		// Stop the task manager
		log.trace( "Stopping task manager..." );
		taskManager.stop();
		taskManager.awaitStop( MANAGER_ACTION_SECONDS, TimeUnit.SECONDS );
		log.debug( "Task manager stopped." );
	}

	public void requestExit() {
		requestExit( false );
	}

	public void requestExit( boolean force ) {
		boolean shutdownVerify = programSettings.getBoolean( "shutdown-verify", true );
		boolean shutdownKeepAlive = programSettings.getBoolean( "shutdown-keepalive", false );

		// If the user desires, prompt to exit the program
		if( !force && shutdownVerify ) {
			Alert alert = new Alert( Alert.AlertType.CONFIRMATION, "", ButtonType.YES, ButtonType.NO );
			alert.initOwner( getWorkspaceManager().getActiveWorkspace().getStage() );
			alert.setTitle( getResourceBundle().getString( "program", "program.close.title" ) );
			alert.setHeaderText( getResourceBundle().getString( "program", "program.close.message" ) );
			alert.setContentText( getResourceBundle().getString( "program", "program.close.prompt" ) );

			Optional<ButtonType> result = alert.showAndWait();
			if( result.isPresent() && result.get() != ButtonType.YES ) return;
		}

		if( !force && (shutdownKeepAlive || JavaUtil.isTest()) ) {
			log.debug( "Program keep alive" );
			workspaceManager.hideWindows();
		} else {
			log.debug( "Program exit" );
			Platform.exit();
		}
	}

	public String getParameter( String key ) {
		Parameters parameters = getParameters();
		// WORKAROUND Parameters are null during testing due to Java 9 incompatibility
		//if( parameters == null ) return System.getProperty( key );
		String value = parameters == null ? null : parameters.getNamed().get( key );
		if( value == null ) value = System.getProperty( key );
		return value;
	}

	public void processCommands( String[] commands ) {
		Stage current = getWorkspaceManager().getActiveWorkspace().getStage();
		Platform.runLater( () -> {
			current.show();
			current.requestFocus();
		} );
	}

	@Override
	public Program getProgram() {
		return this;
	}

	@Override
	public ProductMetadata getMetadata() {
		return metadata;
	}

	@Override
	public ClassLoader getClassLoader() {
		return getClass().getClassLoader();
	}

	@Override
	public ProductBundle getResourceBundle() {
		return productBundle;
	}

	public long getStartTime() {
		return programStartTime;
	}

	public File getDataFolder() {
		return programDataFolder;
	}

	public ProgramNotifier getNotifier() {
		return notifier;
	}

	public ExecutorService getExecutor() {
		return taskManager;
	}

	public IconLibrary getIconLibrary() {
		return iconLibrary;
	}

	public ActionLibrary getActionLibrary() {
		return actionLibrary;
	}

	public SettingsManager getSettingsManager() {
		return settingsManager;
	}

	public ToolManager getToolManager() {
		return toolManager;
	}

	public ResourceManager getResourceManager() {
		return resourceManager;
	}

	public WorkspaceManager getWorkspaceManager() {
		return workspaceManager;
	}

	public void addEventListener( ProgramEventListener listener ) {
		this.listeners.add( listener );
	}

	public void removeEventListener( ProgramEventListener listener ) {
		this.listeners.remove( listener );
	}

	public void fireEvent( ProgramEvent event ) {
		event.fire( listeners );
	}

	protected void finalize() {
		removeEventListener( watcher );
	}

	private static void time( String markerName ) {
		//System.out.println( "Time " + markerName + "=" + (System.currentTimeMillis() - programStartTime) );
	}

	private void printHeader( ProductMetadata metadata ) {
		ExecMode execMode = getExecMode();
		System.out.println( metadata.getName() + " " + metadata.getVersion() + (execMode == ExecMode.PROD ? "" : " [" + execMode + "]") );
		System.out.println( "Java " + System.getProperty( "java.runtime.version" ) );
	}

	private ExecMode getExecMode() {
		if( execMode != null ) return execMode;

		String execModeParameter = getParameter( ProgramParameter.EXECMODE );
		if( execModeParameter != null ) {
			try {
				execMode = ExecMode.valueOf( execModeParameter.toUpperCase() );
			} catch( IllegalArgumentException exception ) {
				// Intentionally ignore exception
			}
		}
		if( execMode == null ) execMode = ExecMode.PROD;

		return execMode;
	}

	private String getExecModePrefix() {
		return getExecMode().getPrefix();
	}

	private void doStartupTasks() throws Exception {
		// Create the product bundle
		productBundle = new ProductBundle( getClass().getClassLoader() );

		// Create the icon library
		iconLibrary = new IconLibrary();
		registerIcons();

		// Create the action library
		actionLibrary = new ActionLibrary( productBundle );
		registerActionHandlers();

		// Create the UI factory
		UiManager uiManager = new UiManager( Program.this );

		// Set the number of startup steps
		int managerCount = 5;
		int steps = managerCount + uiManager.getToolCount();
		Platform.runLater( () -> splashScreen.setSteps( steps ) );

		// Update the splash screen for the task manager which is already started
		Platform.runLater( () -> splashScreen.update() );

		// Update the product metadata
		metadata.loadContributors();
		Platform.runLater( () -> splashScreen.update() );

		// Start the resource manager
		log.trace( "Starting resource manager..." );
		resourceManager = new ResourceManager( Program.this );
		registerSchemes( resourceManager );
		registerResourceTypes( resourceManager );
		resourceManager.start();
		resourceManager.awaitStart( MANAGER_ACTION_SECONDS, TimeUnit.SECONDS );
		Platform.runLater( () -> splashScreen.update() );
		log.debug( "Resource manager started." );

		// Load the settings pages
		getSettingsManager().addSettingsPages( this, programSettings, "/settings/pages.xml" );

		// Start the tool manager
		log.trace( "Starting tool manager..." );
		toolManager = new ToolManager( this );
		registerTools( toolManager );
		toolManager.awaitStart( MANAGER_ACTION_SECONDS, TimeUnit.SECONDS );
		Platform.runLater( () -> splashScreen.update() );
		log.debug( "Tool manager started." );

		// Create the workspace manager
		log.trace( "Starting workspace manager..." );
		workspaceManager = new WorkspaceManager( Program.this ).start();
		workspaceManager.awaitStart( MANAGER_ACTION_SECONDS, TimeUnit.SECONDS );
		Platform.runLater( () -> splashScreen.update() );
		log.debug( "Workspace manager started." );

		// Restore the user interface
		log.trace( "Restore the user interface..." );
		Platform.runLater( () -> uiManager.restoreUi( splashScreen ) );
		uiManager.awaitRestore( MANAGER_ACTION_SECONDS, TimeUnit.SECONDS );
		log.debug( "User interface restored." );

		// TODO Start the update manager

		// Finish the splash screen
		log.info( "Startup steps: " + splashScreen.getCompletedSteps() + " of " + splashScreen.getSteps() );
		Platform.runLater( () -> splashScreen.done() );

		// Give the slash screen time to render and the user to see it
		Thread.sleep( 500 );
	}

	private void doShutdownTasks() {
		try {
			fireEvent( new ProgramStoppingEvent( this ) );

			// Stop the workspace manager
			log.trace( "Stopping workspace manager..." );
			workspaceManager.stop();
			workspaceManager.awaitStop( MANAGER_ACTION_SECONDS, TimeUnit.SECONDS );
			log.debug( "Workspace manager stopped." );

			// TODO Stop the UpdateManager

			// Stop the tool manager
			log.trace( "Stopping tool manager..." );
			toolManager.stop();
			toolManager.awaitStop( MANAGER_ACTION_SECONDS, TimeUnit.SECONDS );
			unregisterTools( toolManager );
			log.debug( "Tool manager stopped." );

			// Do not try to remove the settings pages during shutdown

			// Stop the resource manager
			log.trace( "Stopping resource manager..." );
			resourceManager.stop();
			resourceManager.awaitStop( MANAGER_ACTION_SECONDS, TimeUnit.SECONDS );
			unregisterResourceTypes( resourceManager );
			unregisterSchemes( resourceManager );
			log.debug( "Resource manager stopped." );

			// Disconnect the settings listener
			log.trace( "Stopping settings manager..." );
			settingsManager.stop();
			log.debug( "Settings manager stopped." );

			// Unregister action handlers
			unregisterActionHandlers();

			// Unregister icons
			unregisterIcons();

			// Stop the program server
			if( programServer != null ) programServer.stop();

			// NOTE Do not call Platform.exit() here, it was called already
		} catch( InterruptedException exception ) {
			log.error( "Program shutdown interrupted", exception );
		}
	}

	private void registerIcons() {}

	private void unregisterIcons() {}

	private void registerActionHandlers() {
		getActionLibrary().getAction( "exit" ).pushAction( exitAction );
		getActionLibrary().getAction( "about" ).pushAction( aboutAction );
		getActionLibrary().getAction( "settings" ).pushAction( settingsAction );
		getActionLibrary().getAction( "welcome" ).pushAction( welcomeAction );
		getActionLibrary().getAction( "notice" ).pushAction( noticeAction );
	}

	private void unregisterActionHandlers() {
		getActionLibrary().getAction( "exit" ).pullAction( exitAction );
		getActionLibrary().getAction( "about" ).pullAction( aboutAction );
		getActionLibrary().getAction( "settings" ).pullAction( settingsAction );
		getActionLibrary().getAction( "welcome" ).pullAction( welcomeAction );
		getActionLibrary().getAction( "notice" ).pullAction( noticeAction );
	}

	private void registerSchemes( ResourceManager manager ) {
		manager.addScheme( new ProgramScheme( this ) );
		manager.addScheme( new FileScheme( this ) );
	}

	private void unregisterSchemes( ResourceManager manager ) {
		manager.removeScheme( "program" );
		manager.removeScheme( "file" );
	}

	private void registerResourceTypes( ResourceManager manager ) {
		manager.registerUriResourceType( ProgramGuideType.URI, new ProgramGuideType( this ) );
		manager.registerUriResourceType( ProgramAboutType.URI, new ProgramAboutType( this ) );
		manager.registerUriResourceType( ProgramSettingsType.URI, new ProgramSettingsType( this ) );
		manager.registerUriResourceType( ProgramWelcomeType.URI, new ProgramWelcomeType( this ) );
		manager.registerUriResourceType( ProgramNoticeType.URI, new ProgramNoticeType( this ) );
	}

	private void unregisterResourceTypes( ResourceManager manager ) {
		manager.unregisterUriResourceType( ProgramNoticeType.URI );
		manager.unregisterUriResourceType( ProgramWelcomeType.URI );
		manager.unregisterUriResourceType( ProgramSettingsType.URI );
		manager.unregisterUriResourceType( ProgramAboutType.URI );
		manager.unregisterUriResourceType( ProgramGuideType.URI );
	}

	private void registerTools( ToolManager manager ) {
		registerTool( manager, ProgramGuideType.class, GuideTool.class, ToolInstanceMode.SINGLETON, "guide", "guide" );
		registerTool( manager, ProgramAboutType.class, AboutTool.class, ToolInstanceMode.SINGLETON, "about", "about" );
		registerTool( manager, ProgramSettingsType.class, SettingsTool.class, ToolInstanceMode.SINGLETON, "settings", "settings" );
		registerTool( manager, ProgramWelcomeType.class, WelcomeTool.class, ToolInstanceMode.SINGLETON, "welcome", "welcome" );
	}

	private void unregisterTools( ToolManager manager ) {
		unregisterTool( manager, ProgramWelcomeType.class, WelcomeTool.class );
		unregisterTool( manager, ProgramSettingsType.class, SettingsTool.class );
		unregisterTool( manager, ProgramAboutType.class, AboutTool.class );
		unregisterTool( manager, ProgramGuideType.class, GuideTool.class );
	}

	private void registerTool( ToolManager manager, Class<? extends ResourceType> resourceTypeClass, Class<? extends ProductTool> toolClass, ToolInstanceMode mode, String toolRbKey, String iconKey ) {
		ResourceType type = resourceManager.getResourceType( resourceTypeClass.getName() );
		String name = getResourceBundle().getString( "tool", toolRbKey + "-name" );
		Node icon = getIconLibrary().getIcon( iconKey );

		ToolMetadata metadata = new ToolMetadata();
		metadata.setProduct( this ).setType( toolClass ).setInstanceMode( mode ).setName( name ).setIcon( icon );
		manager.registerTool( type, metadata );
	}

	private void unregisterTool( ToolManager manager, Class<? extends ResourceType> resourceTypeClass, Class<? extends ProductTool> toolClass ) {
		manager.unregisterTool( resourceManager.getResourceType( resourceTypeClass.getName() ), toolClass );
	}

	private class Startup extends Task<Void> {

		@Override
		protected Void call() throws Exception {
			doStartupTasks();
			return null;
		}

		@Override
		protected void succeeded() {
			splashScreen.hide();
			time( "splash hidden" );

			workspaceManager.getActiveWorkspace().getStage().show();
			workspaceManager.getActiveWorkspace().getStage().toFront();

			// Program started event should be fired after the window is shown
			Program.this.fireEvent( new ProgramStartedEvent( Program.this ) );
			time( "program started" );

			// Set the workarea actions
			getActionLibrary().getAction( "workarea-new" ).pushAction( new NewWorkareaAction( Program.this ) );
			getActionLibrary().getAction( "workarea-rename" ).pushAction( new RenameWorkareaAction( Program.this ) );
			getActionLibrary().getAction( "workarea-close" ).pushAction( new CloseWorkareaAction( Program.this ) );
		}

		@Override
		protected void cancelled() {
			splashScreen.hide();
			time( "splash hidden" );
			log.warn( "Startup task cancelled" );
		}

		@Override
		protected void failed() {
			splashScreen.hide();
			log.error( "Error during startup task", getException() );
		}

	}

	private class Shutdown extends Task<Void> {

		@Override
		protected Void call() throws Exception {
			doShutdownTasks();
			return null;
		}

		@Override
		protected void succeeded() {
			Program.this.fireEvent( new ProgramStoppedEvent( Program.this ) );
		}

		@Override
		protected void cancelled() {
			log.error( "Shutdown task cancelled", getException() );
		}

		@Override
		protected void failed() {
			log.error( "Error during shutdown task", getException() );
		}

	}

}
