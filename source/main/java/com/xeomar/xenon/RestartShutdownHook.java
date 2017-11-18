package com.xeomar.xenon;

import com.xeomar.util.JavaUtil;
import com.xeomar.util.OperatingSystem;
import com.xeomar.util.Parameters;
import com.xeomar.util.TextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This shutdown hook is used when a program restart is requested. When a restart is requested the program registers an instance of this shutdown hook, and stops the program, which triggers this shutdown hook to start the program again.
 *
 * @author soderquistmv
 */
public class RestartShutdownHook extends Thread {

	private static final Logger log = LoggerFactory.getLogger( RestartShutdownHook.class );

	private volatile ProcessBuilder builder;

	public RestartShutdownHook( Program service, String... commands ) {
		super( "Restart Hook" );

		builder = new ProcessBuilder( getRestartExecutablePath( service ) );
		builder.directory( new File( System.getProperty( "user.dir" ) ) );

		if( !isWindowsLauncherFound( service ) ) {
			// Add the VM parameters to the commands.
			RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
			for( String command : runtimeBean.getInputArguments() ) {
				if( "abort".equals( command ) ) continue;
				if( "exit".equals( command ) ) continue;
				if( !builder.command().contains( command ) ) builder.command().add( command );
			}

			// Add the classpath information.
			List<URI> classpath = JavaUtil.getClasspath();
			boolean jar = classpath.size() == 1 && classpath.get( 0 ).getPath().endsWith( ".jar" );
			builder.command().add( jar ? "-jar" : "-cp" );
			builder.command().add( runtimeBean.getClassPath() );
			if( !jar ) builder.command().add( service.getClass().getName() );
		}

		Parameters overrideParameters = Parameters.parse( commands );

		// Collect program flags.
		Map<String, List<String>> flags = new HashMap<>();
		for( String name : service.getProgramParameters().getFlags() ) {
			flags.put( name, service.getProgramParameters().getValues( name ) );
		}
		for( String name : overrideParameters.getFlags() ) {
			flags.put( name, overrideParameters.getValues( name ) );
		}

		// Collect program URIs.
		List<String> uris = new ArrayList<>();
		for( String uri : service.getProgramParameters().getUris() ) {
			if( !uris.contains( uri ) ) uris.add( uri );
		}
		for( String uri : overrideParameters.getUris() ) {
			if( !uris.contains( uri ) ) uris.add( uri );
		}

		// Add the collected flags.
		for( String flag : flags.keySet() ) {
			builder.command().add( flag );
			for( String value : flags.get( flag ) ) {
				builder.command().add( value );
			}
		}

		// Add the collected URIs.
		if( uris.size() > 0 ) {
			builder.command().add( "--" );
			for( String uri : uris ) {
				builder.command().add( uri );
			}
		}

		log.trace( "Restart command: ", TextUtil.toString( builder.command(), " " ) );
	}

	private static String getRestartExecutablePath( Program service ) {
		String executablePath = OperatingSystem.getJavaExecutablePath();

		String launcherPath = getWindowsLauncherPath( service );
		if( new File( launcherPath ).exists() ) executablePath = launcherPath;

		return executablePath;
	}

	private static boolean isWindowsLauncherFound( Program service ) {
		return new File( getWindowsLauncherPath( service ) ).exists();
	}

	private static String getWindowsLauncherPath( Program program ) {
		return program.getHomeFolder().toString() + File.separator + program.getCard().getArtifact() + ".exe";
	}

	@Override
	public void run() {
		if( builder == null ) return;

		try {
			builder.start();
		} catch( IOException exception ) {
			log.error( "Error restarting program", exception );
		}
	}

}