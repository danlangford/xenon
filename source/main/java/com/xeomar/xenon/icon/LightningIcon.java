package com.xeomar.xenon.icon;

import com.xeomar.xenon.ProgramIcon;

public class LightningIcon extends ProgramIcon {

	@Override
	protected void render() {
		// Disk
		setFillPaint( getIconFillPaint( GradientShade.DARK ) );
		fillCenteredOval( g( 16 ), g( 16 ), g( 12 ), g( 6 ) );
		drawCenteredOval( g( 16 ), g( 16 ), g( 12 ), g( 6 ) );

		// Bolt
		beginPath();
		moveTo( g( 16 ), g( 5 ) );
		lineTo( g( 11 ), g( 17 ) );
		lineTo( g( 15 ), g( 17 ) );
		lineTo( g( 9 ), g( 29 ) );
		lineTo( g( 21 ), g( 13 ) );
		lineTo( g( 17 ), g( 13 ) );
		lineTo( g( 21 ), g( 5 ) );
		closePath();
		fill( GradientShade.LIGHT );
		draw();
	}

	public static void main( String[] commands ) {
		proof( new LightningIcon() );
	}

}
