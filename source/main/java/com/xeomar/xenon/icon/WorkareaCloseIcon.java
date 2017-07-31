package com.xeomar.xenon.icon;

import com.xeomar.xenon.ProgramIcon;

public class WorkareaCloseIcon extends WorkareaIcon {

	@Override
	protected void render() {
		super.render();

		drawLine( g( 9 ), g( 9 ), g( 23 ), g( 23 ) );
		drawLine( g( 9 ), g( 23 ), g( 23 ), g( 9 ) );
	}

	public static void main( String[] commands ) {
		ProgramIcon.proof( new WorkareaCloseIcon() );
	}

}