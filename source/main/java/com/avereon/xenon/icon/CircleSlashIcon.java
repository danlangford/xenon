package com.avereon.xenon.icon;

import com.avereon.xenon.ProgramIcon;
import javafx.scene.paint.Color;

public class CircleSlashIcon extends ProgramIcon {

	protected void render() {
		setFillPaint( Color.BLUE );
		fillOval( g( 2 ), g( 2 ), g( 30 ), g( 30 ) );

		setDrawWidth( g( 6 ) );
		setDrawPaint( Color.CORNFLOWERBLUE );
		drawLine( g( 4 ), g( 28 ), g( 28 ), g( 4 ) );
	}

	public static void main( String[] commands ) {
		proof( new CircleSlashIcon() );
	}

}