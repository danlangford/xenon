package com.parallelsymmetry.essence.data.event;

import com.parallelsymmetry.essence.data.DataEvent;
import com.parallelsymmetry.essence.data.DataNode;

public class DataChildEvent extends DataValueEvent {

	private int index;

	private DataNode child;

	public DataChildEvent( DataEvent.Action action, DataNode sender, DataNode parent, int index, DataNode child ) {
		super( DataEvent.Type.DATA_CHILD, action, sender, parent );
		this.index = index;
		this.child = child;
	}

	public int getIndex() {
		return index;
	}

	public DataNode getChild() {
		return child;
	}

	@Override
	public String toString() {
		return getAction().toString() + ": " + getCause() + "(" + index + "): " + child;
	}

	@Override
	public DataEvent cloneWithNewSender( DataNode sender ) {
		return new DataChildEvent( getAction(), sender, getCause(), index, child ).setClone( true );
	}

}