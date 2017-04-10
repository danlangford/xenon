package com.parallelsymmetry.essence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class ProgramThreadFactory implements ThreadFactory {

	private static Logger log = LoggerFactory.getLogger( Program.class );

	private static AtomicInteger count = new AtomicInteger();

	@Override
	public Thread newThread( Runnable runnable ) {
		Thread thread = new Thread( runnable, "program-thread-" + count.getAndIncrement() );
		thread.setUncaughtExceptionHandler( new ExceptionWatcher() );
		return thread;
	}

	private static class ExceptionWatcher implements Thread.UncaughtExceptionHandler {

		@Override
		public void uncaughtException( Thread thread, Throwable throwable ) {
			log.error("Error on thread " + thread.getName(), throwable );
		}
	}

}