package com.avereon.xenon.icon;

import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;

import java.util.ArrayList;
import java.util.List;

/**
 * Use <a href="http://www.pic2icon.com/">Pic2Icon</a> to convert to Windows icon.
 */
public class XRingLargeIcon extends XLargeIcon {

	protected void render() {
		double outerRingMax = 15;
		double outerRingMin = 11;
		double innerRingMax = 7;
		double innerRingMin = 5;

		// Jet paint
		double jetRadius = Math.sqrt( 2 * (g( 15 ) * g( 15 )) );
		List<Stop> jetPaintStops = new ArrayList<>();
		jetPaintStops.add( new Stop( 0.2, XRingIcon.JET_CENTER ) );
		jetPaintStops.add( new Stop( 0.9, XRingIcon.JET_TIP ) );
		Paint jetPaint = radialPaint( g( 16 ), g( 16 ), jetRadius, jetPaintStops );

		// Ring paint
		List<Stop> ringPaintStops = new ArrayList<>();
		ringPaintStops.add( new Stop( 0.2, XRingIcon.RING_HIGHLIGHT ) );
		ringPaintStops.add( new Stop( 1.0, XRingIcon.RING_BASE ) );
		Paint ringPaint = radialPaint( g( 16 ), g( 16 ) * outerRingMax / outerRingMin, g( outerRingMax ), ringPaintStops );

		// Bottom of jet
		getGraphicsContext2D().save();
		clip( g( 0 ), g( 16 ), g( 32 ), g( 16 ) );
		xPath();
		fillAndDraw( jetPaint );
		getGraphicsContext2D().restore();

		// Ring
		startPath();
		addArc( g( 16 ), g( 16 ), g( outerRingMax ), g( outerRingMin ), 0, 360 );
		moveTo( g( 16 + innerRingMax ), g( 16 ) );
		addArc( g( 16 ), g( 16 ), g( innerRingMax ), g( innerRingMin ), 0, 360 );
		closePath();
		getGraphicsContext2D().save();
		getGraphicsContext2D().scale( 1, outerRingMin / outerRingMax );
		fillAndDraw( ringPaint );
		getGraphicsContext2D().restore();

		// Top of jet
		getGraphicsContext2D().save();
		clip( g( 0 ), g( 0 ), g( 32 ), g( 16 ) );
		xPath();
		fillAndDraw( jetPaint );
		getGraphicsContext2D().restore();
	}

	private void clip( double x, double y, double w, double h ) {
		startPath();
		addRect( x, y, w, h );
		closePath();
		clip();
	}

	public static void main( String[] commands ) {
		proof( new XRingLargeIcon() );
		//save( new XRingLargeIcon(), new File( System.getProperty( "user.home" ), "Downloads/xenon.png" ) );
		//save( new XRingLargeIcon(), "../../software/xenon/source/main/assembly/xenon.png");
		//save( new XRingLargeIcon(), "../../software/xenon/source/main/resources/program.png");
	}

}
