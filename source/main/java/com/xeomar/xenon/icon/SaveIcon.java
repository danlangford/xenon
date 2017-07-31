package com.xeomar.xenon.icon;

import com.xeomar.xenon.ProgramIcon;

public class SaveIcon extends ProgramIcon {

	@Override
	protected void render() {
		double ox = g( 16 );
		double oy = g( 23 );

		double m = g( 18 );

		double shaft = g( 3 );
		double width = g( 9 );

		// Disk
		setFillPaint( getIconFillPaint( GradientShade.MEDIUM ) );
		fillCenteredOval( ox, oy, g( 10 ), g( 6 ) );
		drawCenteredOval( ox, oy, g( 10 ), g( 6 ) );

		// Arrow
		beginPath();
		moveTo( ox, oy );
		lineTo( ox - width, oy - width - g( 1 ) );
		lineTo( ox - shaft, oy - width - g( 1 ) );
		lineTo( ox - shaft, oy - m );
		lineTo( ox + shaft, oy - m );
		lineTo( ox + shaft, oy - width - g( 1 ) );
		lineTo( ox + width, oy - width - g( 1 ) );
		setFillPaint( getIconFillPaint( GradientShade.LIGHT ) );
		closePath();
		fillAndDraw();
	}

	public static void main( String[] commands ) {
		proof( new SaveIcon() );
	}

}