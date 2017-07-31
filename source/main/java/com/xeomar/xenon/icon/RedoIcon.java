package com.xeomar.xenon.icon;

import com.xeomar.xenon.ProgramIcon;

public class RedoIcon extends ProgramIcon {

	@Override
	protected void render() {
		beginPath();
		moveTo( g( 17 ), g( 7 ) );
		lineTo( g( 29 ), g( 13 ) );
		lineTo( g( 17 ), g( 19 ) );
		closePath();
		fillAndDraw( GradientShade.LIGHT );

		beginPath();
		addArc( g( 17 ), g( 19 ), g( 14 ), g( 8 ), 90, 180 );
		addArc( g( 17 ), g( 21 ), g( 10 ), g( 6 ), 270, -180 );
		closePath();

		fillAndDraw();
	}

	public static void main( String[] commands ) {
		proof( new RedoIcon() );
	}

}