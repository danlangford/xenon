package com.parallelsymmetry.essence.icon;

import com.parallelsymmetry.essence.IconRenderer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;

public class AboutIcon extends IconRenderer {

	public AboutIcon() {
		super();
	}

	public AboutIcon( double size ) {
		super( size );
	}

	protected void render( GraphicsContext gfx ) {
		gfx.setStroke( Color.RED.darker() );
		gfx.setLineCap( StrokeLineCap.ROUND );
		gfx.setLineWidth( 8 );

		gfx.strokeLine( 8, 8, 248, 248 );
		gfx.strokeLine( 248, 8, 8, 248 );
	}

}