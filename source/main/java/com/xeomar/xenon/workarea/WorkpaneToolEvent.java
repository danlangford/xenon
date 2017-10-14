package com.xeomar.xenon.workarea;

import com.xeomar.xenon.worktool.Tool;

public class WorkpaneToolEvent extends WorkpaneEvent {

	private Tool tool;

	public WorkpaneToolEvent( Object source, Type type, Workpane workpane, Tool tool ) {
		super( source, type, workpane );
		this.tool = tool;
	}

	public Tool getTool() {
		return tool;
	}

}
