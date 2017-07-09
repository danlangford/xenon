package com.parallelsymmetry.essence;

import com.parallelsymmetry.essence.product.Product;
import com.parallelsymmetry.essence.resource.Resource;
import com.parallelsymmetry.essence.resource.ResourceType;
import com.parallelsymmetry.essence.util.Controllable;
import com.parallelsymmetry.essence.workarea.Workpane;
import com.parallelsymmetry.essence.workarea.WorkpaneView;
import com.parallelsymmetry.essence.workspace.ToolInstanceMode;
import com.parallelsymmetry.essence.worktool.Tool;
import com.parallelsymmetry.essence.worktool.ToolMetadataComparator;
import javafx.application.Platform;
import org.slf4j.Logger;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

public class ToolManager implements Controllable<ToolManager> {

	private static Logger log = LogUtil.get( ToolManager.class );

	private Program program;

	private SortedSet<ToolMetadata> toolMetadataSet;

	private Map<Class<? extends ProductTool>, ToolMetadata> toolClassMetadata;

	private Map<ResourceType, List<Class<? extends ProductTool>>> resourceTypeToolClasses;

	private Map<String, String> aliases;

	public ToolManager( Program program ) {
		this.program = program;
		toolClassMetadata = new ConcurrentHashMap<>();
		resourceTypeToolClasses = new ConcurrentHashMap<>();
		toolMetadataSet = new ConcurrentSkipListSet<>( new ToolMetadataComparator() );
		aliases = new ConcurrentHashMap<>();
	}

	public void registerTool( ResourceType resourceType, ToolMetadata metadata ) {
		Class<? extends ProductTool> type = metadata.getType();
		toolClassMetadata.put( type, metadata );

		List<Class<? extends ProductTool>> resourceTypeToolClasses = this.resourceTypeToolClasses.computeIfAbsent( resourceType, k -> new CopyOnWriteArrayList<Class<? extends ProductTool>>() );
		resourceTypeToolClasses.add( type );

		log.debug( "Tool registered: resourceType={} -> tool={}", resourceType.getKey(), type.getName() );
	}

	//	public void registerTool( Product product, ResourceType resourceType, Class<? extends ProductTool> type, String name, Node icon ) {
	//		ToolMetadata metadata = new ToolMetadata().setProduct( product ).setType( type ).setName( name ).setIcon( icon );
	//
	//	}

	public void unregisterTool( ResourceType resourceType, Class<? extends ProductTool> type ) {
		toolClassMetadata.remove( type );

		List<Class<? extends ProductTool>> resourceTypeTools = resourceTypeToolClasses.get( resourceType );
		if( resourceTypeTools != null ) resourceTypeTools.remove( type );

		log.debug( "Tool unregistered: resourceType={} -> tool={}", resourceType.getKey(), type.getName() );
	}

	public ProductTool getTool( Resource resource ) {
		return getToolInstance( resource );
	}

	public void openTool( Resource resource ) {
		openTool( resource, null, null );
	}

	public void openTool( Resource resource, WorkpaneView view ) {
		openTool( resource, view == null ? null : view.getWorkPane(), view );
	}

	// TODO Rename to createResourceTool
	public void openTool( Resource resource, Workpane pane, WorkpaneView view ) {
		// OK...so this is a bit of a mess here since there is a lot of decision
		// making to be done in just this little spot. So...I'm going to try and
		// lay out the process flow:

		// The only thing cannot be null is the resource
		if( resource == null ) throw new NullPointerException( "Resource cannot be null" );

		// Get the resource type to look up the registered tool classes
		ResourceType resourceType = resource.getType();

		// Next, determine which tool class will be used
		Class<? extends ProductTool> toolClass = null;
		List<Class<? extends ProductTool>> toolClasses = resourceTypeToolClasses.get( resourceType );
		if( toolClasses == null ) {
			// There are no registered tools for the resource type
			log.warn( "No tools registered for resource type {}", resourceType.getKey() );
		} else if( toolClasses.size() == 1 ) {
			// There is exactly one tool registered for the resource type
			log.debug( "One tool registered for resource type {}", resourceType.getKey() );
			toolClass = toolClasses.get( 0 );
		} else {
			// There is more than one tool registered for the resource type
			log.warn( "Multiple tools registered for resource type {}", resourceType.getKey() );
			toolClass = toolClasses.get( 0 );
		}

		// Next thing to check is how many instances the tool allows
		ToolInstanceMode instanceMode = null;
		//if( instanceMode == null ) instanceMode = program.getSettings().getInstanceMode( toolClass );
		if( instanceMode == null ) instanceMode = toolClassMetadata.get( toolClass ).getInstanceMode();
		if( instanceMode == null ) instanceMode = ToolInstanceMode.UNLIMITED;

		// Before checking for existing tools, the workpane needs to be determined
		if( pane == null && view != null ) pane = view.getWorkPane();
		if( pane == null ) pane = program.getWorkspaceManager().getActiveWorkspace().getActiveWorkarea().getWorkpane();
		if( pane == null ) throw new NullPointerException( "Workpane cannot be null when opening tool" );

		// If the view is null, determine what view to put the tool in
		// If the view is not null, that means the user wants to put the tool in that view
		if( view == null ) {
			// Get the tool placement
			Workpane.Placement placement = null;
			if( placement == null ) placement = toolClassMetadata.get( toolClass ).getPlacement();
			if( placement == null ) placement = Workpane.Placement.SMART;
			view = pane.determineViewFromPlacement( placement );
		}

		// Create a tool if it is needed
		// If this instance mode is SINGLETON, check for an existing tool in the workpane
		Tool tool = null;
		boolean alreadyExists = false;
		if( instanceMode == ToolInstanceMode.SINGLETON ) tool = findToolOfClassInPane( pane, toolClass );
		if( tool == null ) {
			tool = getToolInstance( resource );
		} else {
			alreadyExists = true;
		}
		if( tool == null ) {
			String title = program.getResourceBundle().getString( "program", "no-tool-for-resource-title" );
			String message = program.getResourceBundle().getString( "program", "no-tool-for-resource-message" );
			program.getNotifier().warning( title, (Object)message, resource.getUri().toString() );
			return;
		}

		if( pane == null ) throw new NullPointerException( "Workpane cannot be null when opening tool" );
		if( view == null ) throw new NullPointerException( "WorkpaneView cannot be null when opening tool" );
		if( tool == null ) throw new NullPointerException( "Tool cannot be null when opening tool" );

		final Workpane finalPane = pane;
		final WorkpaneView finalView = view;
		final Tool finalTool = tool;

		if( alreadyExists && instanceMode == ToolInstanceMode.SINGLETON ) {
			Platform.runLater( () -> finalPane.setActiveTool( finalTool ) );
		} else {
			Platform.runLater( () -> finalPane.addTool( finalTool, finalView, true ) );
		}
	}

	public Product getToolProduct( ProductTool tool ) {
		ToolMetadata data = toolClassMetadata.get( tool.getClass() );
		return data == null ? null : data.getProduct();
	}

	public String getToolClassName( String className ) {
		String alias = null;
		if( className != null ) alias = aliases.get( className );
		return alias == null ? className : alias;
	}

	public void addToolAlias( String oldName, String newName ) {
		aliases.putIfAbsent( oldName, newName );
	}

	@Override
	public boolean isRunning() {
		return false;
	}

	@Override
	public ToolManager start() {
		// TODO Implement ToolManager.start()
		return this;
	}

	@Override
	public ToolManager awaitStart( long timeout, TimeUnit unit ) throws InterruptedException {
		// TODO Implement ToolManager.awaitStart()
		return this;
	}

	@Override
	public ToolManager restart() {
		// TODO Implement ToolManager.restart()
		return this;
	}

	@Override
	public ToolManager awaitRestart( long timeout, TimeUnit unit ) throws InterruptedException {
		// TODO Implement ToolManager.awaitRestart()
		return this;
	}

	@Override
	public ToolManager stop() {
		// TODO Implement ToolManager.stop()
		return this;
	}

	@Override
	public ToolManager awaitStop( long timeout, TimeUnit unit ) throws InterruptedException {
		// TODO Implement ToolManager.awaitStop()
		return this;
	}

	private ProductTool getToolInstance( Resource resource ) {
		ResourceType resourceType = resource.getType();
		if( resourceType == null ) {
			log.warn( "Resource type is null: " + resource );
			return null;
		}

		List<Class<? extends ProductTool>> typeTools = resourceTypeToolClasses.get( resourceType );
		if( typeTools == null ) {
			log.warn( "No toolClassMetadata registered for resource type: " + resourceType );
			return null;
		}

		Class<? extends ProductTool> toolClass = typeTools.get( 0 );

		// TODO If there is more than one tool for a type then ask the user.

		ProductTool tool = getToolInstance( toolClass, resource );

		if( tool == null ) {
			log.warn( "Tool not found for resource: {}", resource );
		} else {
			log.debug( "Tool created for resource: {} -> {}", resource, toolClass );
		}

		return tool;
	}

	private ProductTool getToolInstance( Class<? extends ProductTool> type, Resource resource ) {
		ProductTool tool = null;

		try {
			Constructor<? extends ProductTool> constructor = type.getConstructor( Product.class, Resource.class );
			Product product = toolClassMetadata.get( type ).getProduct();
			tool = constructor.newInstance( product, resource );
			// FIXME Should Tool.setReady() be implemented differently?
			// There really is no point to calling tool.setReady() here because
			// the constructor just completed. Calling tool.setReady() on the
			// same thread as the constructor just doesn't help any.
			//tool.setReady();
		} catch( Exception exception ) {
			log.error( "Error creating instance: " + type.getName(), exception );
		}

		return tool;
	}

	private Tool findToolOfClassInPane( Workpane pane, Class<? extends Tool> type ) {
		for( Tool paneTool : pane.getTools() ) {
			if( type == paneTool.getClass() ) return paneTool;
		}
		return null;
	}

}
