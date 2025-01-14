package com.avereon.xenon.testutil;

import com.avereon.product.ProductEvent;
import com.avereon.product.ProductEventListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ProgramEventCollector implements ProductEventListener {

	private List<ProductEvent> events = new CopyOnWriteArrayList<>();

	@Override
	public void handleEvent( ProductEvent event ) {
		events.add( event );
	}

	public List<ProductEvent> getEvents() {
		return events;
	}

}
