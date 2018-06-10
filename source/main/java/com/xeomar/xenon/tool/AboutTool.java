package com.xeomar.xenon.tool;

import com.xeomar.product.ProductCard;
import com.xeomar.settings.Settings;
import com.xeomar.util.*;
import com.xeomar.xenon.BundleKey;
import com.xeomar.xenon.Program;
import com.xeomar.xenon.ProgramProduct;
import com.xeomar.xenon.ProgramSettings;
import com.xeomar.xenon.resource.Resource;
import com.xeomar.xenon.tool.guide.GuideNode;
import com.xeomar.xenon.tool.guide.GuidedTool;
import com.xeomar.xenon.workarea.ToolException;
import com.xeomar.xenon.workarea.ToolParameters;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import org.slf4j.Logger;
import org.tbee.javafx.scene.layout.MigPane;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.lang.management.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AboutTool extends GuidedTool {

	private static final Logger log = LogUtil.get( MethodHandles.lookup().lookupClass() );

	public static final String SUMMARY = "summary";

	public static final String PRODUCTS = "products";

	public static final String DETAILS = "details";

	private String titleSuffix;

	private Map<String, Node> nodes;

	private SummaryPane summaryPane;

	private TextArea summaryText;

	private BorderPane productsPane;

	private TextArea productsText;

	private BorderPane detailsPane;

	private TextArea detailsText;

	public AboutTool( ProgramProduct product, Resource resource ) {
		super( product, resource );
		setId( "tool-about" );

		setGraphic( product.getProgram().getIconLibrary().getIcon( "about" ) );
		setTitleSuffix( product.getResourceBundle().getString( "tool", "about-suffix" ) );

		summaryPane = new SummaryPane();

		//		summaryText = new TextArea();
		//		summaryText.setEditable( false );
		//		summaryPane = new BorderPane();
		//		summaryPane.setCenter( summaryText );

		productsText = new TextArea();
		productsText.setEditable( false );
		productsPane = new BorderPane();
		productsPane.setCenter( productsText );

		detailsText = new TextArea();
		detailsText.setEditable( false );
		detailsText.setFont( Font.font( "Monospaced", 12.0 ) );
		detailsPane = new BorderPane();
		detailsPane.setCenter( detailsText );

		nodes = new ConcurrentHashMap<>();
		nodes.put( SUMMARY, summaryPane );
		nodes.put( PRODUCTS, productsPane );
		nodes.put( DETAILS, detailsPane );
	}

	public String getTitleSuffix() {
		return titleSuffix;
	}

	public void setTitleSuffix( String titleSuffix ) {
		this.titleSuffix = titleSuffix;
	}

	@Override
	protected void allocate() throws ToolException {
		log.debug( "Tool allocate" );
		super.allocate();
	}

	@Override
	protected void display() throws ToolException {
		log.debug( "Tool display" );
		super.display();
	}

	@Override
	protected void activate() throws ToolException {
		log.debug( "Tool activate" );
		super.activate();
	}

	@Override
	protected void deactivate() throws ToolException {
		log.debug( "Tool deactivate" );
		super.deactivate();
	}

	@Override
	protected void conceal() throws ToolException {
		log.debug( "Tool conceal" );
		super.conceal();
	}

	@Override
	protected void deallocate() throws ToolException {
		log.debug( "Tool deallocate" );
		super.deallocate();
	}

	@Override
	protected void resourceReady( ToolParameters parameters ) throws ToolException {
		super.resourceReady( parameters );
		resourceRefreshed();
		selectedPage( SUMMARY );
	}

	@Override
	protected void resourceRefreshed() throws ToolException {
		super.resourceRefreshed();
		ProductCard metadata = getResource().getModel();
		if( titleSuffix == null ) {
			setTitle( metadata.getName() );
		} else {
			setTitle( metadata.getName() + " - " + titleSuffix );
		}
		//summaryText.setText( getSummaryText( metadata ) );
		summaryPane.update( metadata );
		productsText.setText( getProductsText( (Program)getProduct() ) );
		detailsText.setText( getDetailsText( (Program)getProduct() ) );
	}

	private class SummaryPane extends MigPane {

		private Label name;

		private Label version;

		private Label provider;

		private ImageView osIcon;

		private Label osName;

		public SummaryPane() {
			add( osIcon = new ImageView() );
			add( osName = new Label( "" ) );
		}

		public void update( ProductCard card ) {
			String osProperty = System.getProperty( "os.name" ).toLowerCase();
			String input = getClass().getResource( "/icons/" + osProperty + ".png" ).toExternalForm();
			if( input != null ) {
				osIcon.setImage( new Image( input, 64, 64, true, true ) );
			} else {
				log.error( "Unable to load OS icon: icons/" + osProperty + ".svg" );
			}

			osName.setText( System.getProperty( "os.name" ) );

		}

	}

	private String getSummaryText( ProductCard metadata ) {
		Version version = new Version( metadata.getVersion() );

		StringBuilder builder = new StringBuilder();
		builder.append( metadata.getName() );
		builder.append( " " );
		builder.append( version.toHumanString() );
		builder.append( "\n  by " );
		builder.append( metadata.getProvider() );
		builder.append( "\n" );

		builder.append( "\n" );

		builder.append( System.getProperty( "java.runtime.name" ) );
		builder.append( " " );
		builder.append( System.getProperty( "java.runtime.version" ) );
		builder.append( "\n  by " );
		builder.append( System.getProperty( "java.vm.vendor" ) );
		builder.append( "\n" );

		builder.append( "\n" );

		builder.append( System.getProperty( "os.name" ) );
		builder.append( " " );
		builder.append( System.getProperty( "os.version" ) );
		builder.append( "\n" );

		builder.append( "\n" );
		builder.append( "\n" );

		int year = Calendar.getInstance( TimeZone.getTimeZone( "UTC" ) ).get( Calendar.YEAR );

		builder.append( "\u00A9" ); // Copyright symbol
		if( year == metadata.getInception() ) {
			builder.append( metadata.getInception() );
		} else {
			builder.append( metadata.getInception() );
			builder.append( "-" );
			builder.append( year );
		}
		builder.append( " " );
		builder.append( metadata.getProvider() );
		builder.append( " " );
		builder.append( metadata.getCopyrightSummary() );
		builder.append( "\n" );

		return builder.toString();
	}

	private String getProductsText( Program program ) {
		StringBuilder builder = new StringBuilder();

		builder.append( "Product Information" );

		return builder.toString();
	}

	private String getDetailsText( Program program ) {
		ProductCard metadata = program.getCard();
		StringBuilder builder = new StringBuilder();

		// Framework summary
		builder.append( getHeader( "Program: " + metadata.getName() + " " + metadata.getVersion() ) );

		// The current date
		builder.append( "\n" );
		builder.append( getHeader( "Timestamp: " + DateUtil.format( new Date(), DateUtil.DEFAULT_DATE_FORMAT ) ) );

		// JVM commands
		builder.append( "\n" );
		RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
		builder.append( getHeader( "JVM commands: " + TextUtil.toString( runtimeMXBean.getInputArguments(), " " ) ) );

		// Program commands
		Application.Parameters parameters = program.getParameters();
		builder.append( "\n" );
		builder.append( getHeader( "Program commands: " + (parameters == null ? "" : TextUtil.toString( parameters.getRaw(), " " )) ) );

		// Program details
		builder.append( "\n" );
		builder.append( getHeader( "Program details" ) );
		builder.append( "\n" );
		builder.append( Indenter.indent( getProductDetails( program.getCard() ), 4, " " ) );
		builder.append( "\n" );
		builder.append( Indenter.indent( getProgramDetails( program ), 4, " " ) );

		// Operating system
		builder.append( "\n" );
		builder.append( getHeader( "Operating system" ) );

		builder.append( "\n" );
		builder.append( Indenter.indent( getOperatingSystemDetail(), 4, " " ) );

		// Runtime
		builder.append( "\n" );
		builder.append( getHeader( "Runtime" ) );
		builder.append( "\n" );
		builder.append( Indenter.indent( getRuntimeDetail( program ), 4, " " ) );

		// Memory
		builder.append( "\n" );
		builder.append( getHeader( "Memory" ) );
		builder.append( "\n" );
		builder.append( Indenter.indent( getMemoryDetail(), 4, " " ) );

		//		// Installed modules
		//		builder.append( "\n" );
		//		builder.append( getHeader( "Installed modules" ) );
		//
		//		builder.append( "\n" );
		//		Set<ServiceModule> modules = getProgram().getProductManager().getModules();
		//		if( modules.size() == 0 ) {
		//			builder.append( "    No optional modules installed.\n" );
		//		} else {
		//			List<ServiceModule> moduleList = new ArrayList<ServiceModule>( modules );
		//			Collections.sort( moduleList );
		//			for( ServiceModule module : moduleList ) {
		//				builder.append( Indenter.indent( getProductDetails( module.getCard() ), 4, " " ) );
		//				builder.append( "\n" );
		//			}
		//		}

		// Threads
		builder.append( "\n" );
		builder.append( getHeader( "Threads" ) );
		builder.append( "\n" );
		builder.append( Indenter.indent( getThreadDetail(), 4, " " ) );

		// System properties
		builder.append( "\n" );
		builder.append( getHeader( "System properties" ) );
		builder.append( "\n" );
		builder.append( Indenter.indent( getProperties( System.getProperties() ), 4, " " ) );

		//		// Settings
		//		builder.append( "\n" );
		//		builder.append( getHeader( "Settings" ) );
		//		builder.append( "\n" );
		//		builder.append( Indenter.indent( getSettingsDetail(), 4, " " ) );

		return builder.toString();
	}

	private String getHeader( String text ) {
		StringBuilder builder = new StringBuilder();

		builder.append( "--- " );
		builder.append( text );
		builder.append( "\n" );

		return builder.toString();
	}

	private String getProgramDetails( Program program ) {
		StringBuilder builder = new StringBuilder();

		builder.append( "Home folder: " + program.getHomeFolder() + "\n" );
		builder.append( "Data folder: " + program.getDataFolder() + "\n" );

		return builder.toString();
	}

	private String getProductDetails( ProductCard card ) {
		StringBuilder builder = new StringBuilder();

		builder.append( "Product:     " + card.getName() + "\n" );
		builder.append( "Provider:    " + card.getProvider() + "\n" );
		builder.append( "Inception:   " + card.getInception() + "\n" );
		builder.append( "Summary:     " + card.getSummary() + "\n" );

		builder.append( "Group:       " + card.getGroup() + "\n" );
		builder.append( "Artifact:    " + card.getArtifact() + "\n" );
		builder.append( "Version:     " + card.getVersion() + "\n" );
		builder.append( "Timestamp:   " + card.getTimestamp() + "\n" );
		builder.append( "Source URI:  " + card.getCardUri() + "\n" );

		//		ProductManager productManager = getProgram().getProductManager();
		//		builder.append( "Enabled:     " + productManager.isEnabled( card ) + "\n" );
		//		builder.append( "Updatable:   " + productManager.isUpdatable( card ) + "\n" );
		//		builder.append( "Removable:   " + productManager.isRemovable( card ) + "\n" );

		return builder.toString().trim();
	}

	private String getOperatingSystemDetail() {
		StringBuilder builder = new StringBuilder();

		OperatingSystemMXBean bean = ManagementFactory.getOperatingSystemMXBean();
		builder.append( "Name:        " + bean.getName() + "\n" );
		builder.append( "Arch:        " + bean.getArch() + "\n" );
		builder.append( "Version:     " + bean.getVersion() + "\n" );
		builder.append( "Processors:  " + bean.getAvailableProcessors() + "\n" );

		return builder.toString();
	}

	private String getRuntimeDetail( Program program ) {
		StringBuilder builder = new StringBuilder();

		RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
		long uptime = bean.getUptime();
		builder.append( "Start time:        " + DateUtil.format( new Date( bean.getStartTime() ), DateUtil.DEFAULT_DATE_FORMAT ) + "\n" );
		builder.append( "Current time:      " + DateUtil.format( new Date(), DateUtil.DEFAULT_DATE_FORMAT ) + "\n" );
		builder.append( "Uptime:            " + DateUtil.formatDuration( uptime ) + "\n" );

		Settings programSettings = program.getSettingsManager().getSettings( ProgramSettings.PROGRAM );
		long lastUpdateCheck = program.getUpdateManager().getLastUpdateCheck();
		long nextUpdateCheck = program.getUpdateManager().getNextUpdateCheck();
		if( nextUpdateCheck < System.currentTimeMillis() ) nextUpdateCheck = 0;

		String unknown = program.getResourceBundle().getString( BundleKey.UPDATE, "unknown" );
		String notScheduled = program.getResourceBundle().getString( BundleKey.UPDATE, "not-scheduled" );
		builder.append( "Last update check: " + (lastUpdateCheck == 0 ? unknown : DateUtil.format( new Date( lastUpdateCheck ), DateUtil.DEFAULT_DATE_FORMAT )) + "\n" );
		builder.append( "Next update check: " + (nextUpdateCheck == 0 ? notScheduled : DateUtil.format( new Date( nextUpdateCheck ), DateUtil.DEFAULT_DATE_FORMAT )) + "\n" );

		return builder.toString();
	}

	private String getMemoryDetail() {
		StringBuilder builder = new StringBuilder();

		long max = Runtime.getRuntime().maxMemory();
		long total = Runtime.getRuntime().totalMemory();
		long used = total - Runtime.getRuntime().freeMemory();
		builder.append( "Summary: " + FileUtil.getHumanBinSize( used ) + " / " + FileUtil.getHumanBinSize( total ) + " / " + FileUtil.getHumanBinSize( max ) + "\n" );

		MemoryMXBean bean = ManagementFactory.getMemoryMXBean();
		builder.append( "\n" );
		builder.append( "Heap use:     " + bean.getHeapMemoryUsage() + "\n" );
		builder.append( "Non-heap use: " + bean.getNonHeapMemoryUsage() + "\n" );

		builder.append( "\n" );
		List<MemoryPoolMXBean> pools = ManagementFactory.getMemoryPoolMXBeans();
		for( MemoryPoolMXBean pool : pools ) {
			builder.append( pool.getName() + " (" + pool.getType() + "): " + pool.getUsage() + "\n" );
		}

		return builder.toString();
	}

	private String getThreadDetail() {
		StringBuilder builder = new StringBuilder();

		ThreadMXBean bean = ManagementFactory.getThreadMXBean();

		builder.append( "Current thread count:        " + bean.getThreadCount() + "\n" );
		builder.append( "Maximum thread count:        " + bean.getPeakThreadCount() + "\n" );

		builder.append( "\n" );
		List<ThreadInfo> threads = Arrays.asList( bean.getThreadInfo( bean.getAllThreadIds() ) );
		Collections.sort( threads, new ThreadInfoNameComparator() );
		for( ThreadInfo thread : threads ) {
			builder.append( TextUtil.leftJustify( thread.getThreadState().toString(), 15 ) );
			builder.append( "  " );
			builder.append( thread.getThreadName() );
			builder.append( "\n" );
		}

		return builder.toString();
	}

	@Override
	protected void guideNodeChanged( GuideNode oldNode, GuideNode newNode ) {
		if( newNode != null ) selectedPage( newNode.getId() );
	}

	private void selectedPage( String item ) {
		getChildren().clear();
		if( item != null ) getChildren().add( nodes.get( item ) );
	}

	private String getProperties( Properties properties ) {
		StringBuilder builder = new StringBuilder();

		// Load the keys into a list.
		int keyColumnWidth = 0;
		List<String> keys = new ArrayList<>();
		for( Object object : properties.keySet() ) {
			String key = object.toString();
			keys.add( key );
			keyColumnWidth = Math.max( keyColumnWidth, key.length() );
		}

		// Sort the key list
		Collections.sort( keys );
		keyColumnWidth += 2;

		for( String key : keys ) {
			String value = properties.getProperty( key );

			if( key.endsWith( ".path" ) ) {
				String[] elements = value.split( File.pathSeparator );

				// Append the key with the first element.
				builder.append( TextUtil.leftJustify( key, keyColumnWidth ) );
				builder.append( TextUtil.toPrintableString( elements[ 0 ] ) );
				builder.append( "\n" );

				// Append the remaining elements with padding.
				int count = elements.length;
				for( int index = 1; index < count; index++ ) {
					builder.append( TextUtil.pad( keyColumnWidth ) );
					builder.append( TextUtil.toPrintableString( elements[ index ] ) );
					builder.append( "\n" );
				}
			} else {
				builder.append( TextUtil.leftJustify( key, keyColumnWidth ) );
				builder.append( TextUtil.toPrintableString( value ) );
				builder.append( "\n" );
			}
		}

		return builder.toString();
	}

}
