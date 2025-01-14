package com.avereon.xenon.icon;

import com.avereon.xenon.ProgramIcon;

public class WorkareaRenameIcon extends WorkareaIcon {

	@Override
	protected void render() {
		super.render();

		setFillPaint( getIconFillPaint( GradientTone.LIGHT ) );
		fillRect( g( 3 ), g( 11 ), g( 26 ), g( 10 ) );
		drawRect( g( 3 ), g( 11 ), g( 26 ), g( 10 ) );
		drawLine( g( 9 ), g( 13 ), g( 9 ), g( 19 ) );
	}

	public static void main( String[] commands ) {
		ProgramIcon.proof( new WorkareaRenameIcon() );
	}

}
