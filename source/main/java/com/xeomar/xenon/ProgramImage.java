package com.xeomar.xenon;

import com.xeomar.util.LogUtil;
import com.xeomar.xenon.util.Colors;
import com.xeomar.xenon.util.JavaFxStarter;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;
import org.slf4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

/*
Conversion chart

A ----- 0
    J - 2
  F --- 4
    K - 6
D ----- 8
    L - 10
  G --- 12
    M - 14
C ----- 16
    N - 18
  H --- 20
    O - 22
E ----- 24
    P - 26
  I --- 28
    Q - 30
B ----- 32

ZA - 1
ZB - 3
ZC - 5
ZD - 7
ZE - 9
ZF - 11
ZG - 13
ZH - 15
ZI - 17
ZJ - 19
ZK - 21
ZL - 23
ZM - 25
ZN - 27
ZO - 29
ZP - 31
*/
public abstract class ProgramImage extends Canvas {

	protected enum GradientShade {
		LIGHT,
		MEDIUM,
		DARK
	}

	protected static final double RADIANS_PER_DEGREE = Math.PI / 180;

	protected static final double DEGREES_PER_RADIAN = 180 / Math.PI;

	private static Logger log = LogUtil.get( ProgramImage.class );

	private static final double DEFAULT_SIZE = 256;

	private static double DEFAULT_STROKE_WIDTH = 1.0 / 32.0;

	private static Color DEFAULT_STROKE_COLOR = new Color( 0.2, 0.2, 0.2, 1.0 );

	private static Color DEFAULT_FILL_COLOR = new Color( 0.8, 0.8, 0.8, 1.0 );

	private static double drawWidth = DEFAULT_STROKE_WIDTH;

	private static Color themeDrawColor;

	private static Color themeFillColor;

	private static Color drawColor;

	private static Color fillColor;

	private static ColorTheme theme;

	private double xOffset;

	private double yOffset;

	private GraphicsContext overrideContext;

	private Transform baseTransform = new Affine();

	static {
		//setColorTheme( new ColorTheme( new Color( 0.5, 0.75, 1.0, 1.0 ) ) );
		setColorTheme( new ColorTheme( new Color( 0.8, 0.8, 0.8, 1.0 ) ) );
		//setColorTheme( new ColorTheme( Color.GRAY ) );
	}

	public ProgramImage() {
		setSize( DEFAULT_SIZE );
		parentProperty().addListener( ( observable, oldValue, newValue ) -> { if( newValue != null ) fireRender(); } );
	}

	public static void setColorTheme( ColorTheme theme ) {
		ProgramImage.theme = theme;
		themeFillColor = theme.getPrimary();

		// A luminance greater than 0.5 is "bright"
		// A luminance less than 0.5 is "dark"
		double y = Colors.getLuminance( theme.getPrimary() );
		if( y < 0.2 ) {
			themeDrawColor = theme.getPrimary().deriveColor( 0, 1, 1.75, 1 );
		} else {
			themeDrawColor = theme.getPrimary().deriveColor( 0, 1, 0.25, 1 );
		}
	}

	public static void setDrawColor( Color color ) {
		drawColor = color;
	}

	public static void setFillColor( Color color ) {
		fillColor = color;
	}

	public ProgramImage setSize( double size ) {
		setHeight( size );
		setWidth( size );
		//fireRender();
		return this;
	}

	public Image getImage() {
		fireRender();
		return SwingFXUtils.toFXImage( getBufferedImage(), new WritableImage( (int)getWidth(), (int)getHeight() ) );
	}

	public BufferedImage getBufferedImage() {
		// Apparently images created from the snapshot method are not usable as
		// application icons. The following workaround creates an image that is
		// usable as an application icon. It may be more efficient to create
		// images differently if they are not needed for application icons.

		int width = (int)getWidth();
		int height = (int)getHeight();

		Pane pane = new Pane( this );
		pane.setBackground( Background.EMPTY );
		Scene scene = new Scene( pane );
		scene.setFill( Color.TRANSPARENT );

		//				// Just for research, set different color backgrounds per xformX
		//				int xformX = Math.min( width, height );
		//				if( xformX == 16 ) scene.setFillColor( Color.PURPLE );
		//				if( xformX == 24 ) scene.setFillColor( Color.BLUE );
		//				if( xformX == 32 ) scene.setFillColor( Color.GREEN );
		//				if( xformX == 64 ) scene.setFillColor( Color.YELLOW );
		//				if( xformX == 128 ) scene.setFillColor( Color.ORANGE );
		//				if( xformX == 256 ) scene.setFillColor( Color.RED );

		BufferedImage buffer = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
		SwingFXUtils.fromFXImage( scene.snapshot( new WritableImage( width, height ) ), buffer );
		return buffer;
	}

	public static void proof( ProgramImage icon ) {
		JavaFxStarter.startAndWait( 1000 );

		Platform.runLater( () -> {
			String title = icon.getClass().getSimpleName();

			icon.fireRender();

			VBox pane = new VBox();
			pane.getChildren().add( icon );

			Stage stage = new Stage();
			stage.setTitle( title );
			stage.setScene( new Scene( pane ) );

			stage.setResizable( true );
			stage.centerOnScreen();
			stage.sizeToScene();
			stage.show();
		} );
	}

	public static void proof( ProgramIcon icon ) {
		JavaFxStarter.startAndWait( 1000 );

		// Now show the icon window
		Platform.runLater( () -> {
			String title = icon.getClass().getSimpleName();

			ImageView imageView16 = new ImageView( resample( icon.copy().setSize( 16 ).fireRender().getImage(), 16 ) );
			ImageView imageView32 = new ImageView( resample( icon.copy().setSize( 32 ).fireRender().getImage(), 8 ) );

			ProgramImage icon128 = icon.copy().setSize( 128 ).fireRender();
			AnchorPane.setTopAnchor( icon128, 0.0 );
			AnchorPane.setLeftAnchor( icon128, 0.0 );

			ProgramImage icon64 = icon.copy().setSize( 64 ).fireRender();
			AnchorPane.setTopAnchor( icon64, 128.0 );
			AnchorPane.setLeftAnchor( icon64, 128.0 );

			ProgramImage icon32 = icon.copy().setSize( 32 ).fireRender();
			AnchorPane.setTopAnchor( icon32, 192.0 );
			AnchorPane.setLeftAnchor( icon32, 192.0 );

			ProgramImage icon16 = icon.copy().setSize( 16 ).fireRender();
			AnchorPane.setTopAnchor( icon16, 224.0 );
			AnchorPane.setLeftAnchor( icon16, 224.0 );

			ProgramImage icon8 = icon.copy().setSize( 8 ).fireRender();
			AnchorPane.setTopAnchor( icon8, 240.0 );
			AnchorPane.setLeftAnchor( icon8, 240.0 );

			AnchorPane iconPane = new AnchorPane();
			iconPane.getChildren().addAll( icon128, icon64, icon32, icon16, icon8 );

			GridPane pane = new GridPane();
			pane.add( icon.copy().setSize( DEFAULT_SIZE ).fireRender(), 1, 1 );
			pane.add( imageView16, 2, 1 );
			pane.add( imageView32, 2, 2 );
			pane.add( iconPane, 1, 2 );

			Stage stage = new Stage();
			stage.setTitle( title );
			stage.getIcons().addAll( icon128.copy().getImage(), icon64.copy().getImage(), icon32.copy().getImage(), icon16.copy().getImage() );
			stage.setScene( new Scene( pane ) );

			stage.setResizable( true );
			stage.centerOnScreen();
			stage.sizeToScene();
			stage.show();
		} );
	}

	public static void save( ProgramImage icon, String path ) {
		JavaFxStarter.startAndWait( 1000 );

		// Render and save the icon
		Platform.runLater( () -> {
			try {
				icon.fireRender();
				File file = new File( path ).getCanonicalFile();
				ImageIO.write( icon.getBufferedImage(), "png", file );
			} catch( Exception exception ) {
				exception.printStackTrace();
			}
		} );

		Platform.exit();
	}

	protected abstract void render();

	protected void reset() {
		getGraphicsContext2D().setTransform( new Affine( baseTransform ) );
	}

	protected void move( double x, double y ) {
		getGraphicsContext2D().translate( x, y );
	}

	protected void zoom( double x, double y ) {
		getGraphicsContext2D().scale( x, y );
	}

	protected void clip() {
		getGraphicsContext2D().clip();
	}

	protected void beginPath() {
		getGraphicsContext2D().beginPath();
	}

	protected void moveTo( double x, double y ) {
		getGraphicsContext2D().moveTo( xformX( x ), xformY( y ) );
	}

	protected void lineTo( double x, double y ) {
		getGraphicsContext2D().lineTo( xformX( x ), xformY( y ) );
	}

	protected void addArc( double cx, double cy, double rx, double ry, double start, double extent ) {
		getGraphicsContext2D().arc( xformX( cx ), xformY( cy ), xformX( rx ), xformY( ry ), start, extent );
	}

	protected void addRect( double x, double y, double w, double h ) {
		getGraphicsContext2D().rect( xformX( x ), xformY( y ), xformX( w ), xformY( h ) );
	}

	protected void addOval( double cx, double cy, double rx, double ry ) {
		getGraphicsContext2D().arc( xformX( cx ), xformY( cy ), xformX( rx ), xformY( ry ), 0, 360 );
	}

	protected void curveTo( double xc, double yc, double x1, double y1 ) {
		getGraphicsContext2D().quadraticCurveTo( xformX( xc ), xformY( yc ), xformX( x1 ), xformY( y1 ) );
	}

	protected void curveTo( double xc1, double yc1, double xc2, double yc2, double x1, double y1 ) {
		getGraphicsContext2D().bezierCurveTo( xformX( xc1 ), xformY( yc1 ), xformX( xc2 ), xformY( yc2 ), xformX( x1 ), xformY( y1 ) );
	}

	protected void closePath() {
		getGraphicsContext2D().closePath();
	}

	@Override
	public GraphicsContext getGraphicsContext2D() {
		return overrideContext == null ? super.getGraphicsContext2D() : overrideContext;
	}

	public void setGraphicsContext2D( GraphicsContext context ) {
		this.overrideContext = context;
	}

	protected void setLineWidth( double width ) {
		getGraphicsContext2D().setLineWidth( xformX( width ) );
	}

	protected void setLineCap( StrokeLineCap cap ) {
		getGraphicsContext2D().setLineCap( cap );
	}

	protected void setLineJoin( StrokeLineJoin join ) {
		getGraphicsContext2D().setLineJoin( join );
	}

	protected void setDrawPaint( Paint paint ) {
		getGraphicsContext2D().setStroke( paint );
	}

	protected void setFill( GradientShade shade ) {
		setFillPaint( getIconFillPaint( shade ) );
	}

	protected void setFillPaint( Paint paint ) {
		getGraphicsContext2D().setFill( paint );
	}

	protected void setFillRule( FillRule rule ) {
		getGraphicsContext2D().setFillRule( FillRule.EVEN_ODD );
	}

	protected void setTextAlign( TextAlignment alignment ) {
		getGraphicsContext2D().setTextAlign( alignment );
	}

	protected void setTextBaseLine( VPos baseline ) {
		getGraphicsContext2D().setTextBaseline( baseline );
	}

	protected void fillAndDraw() {
		fill();
		draw();
	}

	protected void fillAndDraw( Paint fillPaint ) {
		fill( fillPaint );
		draw();
	}

	protected void fillAndDraw( GradientShade shade ) {
		fillAndDraw( getIconFillPaint( shade ) );
	}

	protected void fillAndDraw( Paint fillPaint, Paint drawPaint ) {
		fill( fillPaint );
		draw( drawPaint );
	}

	protected void fillAndDraw( GradientShade shade, Paint drawPaint ) {
		fill( getIconFillPaint( shade ) );
		draw( drawPaint );
	}

	protected void fill() {
		getGraphicsContext2D().fill();
	}

	protected void fill( Paint paint ) {
		getGraphicsContext2D().setFill( paint );
		getGraphicsContext2D().fill();
		getGraphicsContext2D().setFill( getIconFillPaint() );
	}

	protected void fill( GradientShade shade ) {
		fill( getIconFillPaint( shade ) );
	}

	protected void draw() {
		getGraphicsContext2D().stroke();
	}

	protected void draw( Paint paint ) {
		getGraphicsContext2D().setStroke( paint );
		getGraphicsContext2D().stroke();
		getGraphicsContext2D().setStroke( getIconDrawColor() );
	}

	protected void draw( ProgramImage image ) {
		image.setGraphicsContext2D( getGraphicsContext2D() );
		image.fireRender();
	}

	protected void drawDot( double x, double y ) {
		double offset = 0.5 * getIconDrawWidth();
		drawOval( x - offset, y - offset, offset * 2, offset * 2 );
	}

	protected void fillRect( double x, double y, double w, double h ) {
		getGraphicsContext2D().fillRect( xformX( x ), xformY( y ), xformX( w ), xformY( h ) );
	}

	protected void fillOval( double x, double y, double w, double h ) {
		getGraphicsContext2D().fillOval( xformX( x ), xformY( y ), xformX( w ), xformY( h ) );
	}

	protected void fillCenteredOval( double cx, double cy, double rx, double ry ) {
		double x = cx - rx;
		double y = cy - ry;
		double w = rx * 2;
		double h = ry * 2;
		getGraphicsContext2D().fillOval( xformX( x ), xformY( y ), xformX( w ), xformY( h ) );
	}

	protected void fillText( String text, double x, double y, double textSize ) {
		fillText( text, x, y, textSize, 0 );
	}

	protected void fillText( String text, double x, double y, double textSize, double maxWidth ) {
		// Font sizes smaller than one don't scale as expected
		// so the workaround is to scale according the text size
		// and divide the coordinates by the size.

		double fontSize = 72;
		double scale = textSize / fontSize;

		// Scale the transform
		Affine transform = getGraphicsContext2D().getTransform().clone();
		getGraphicsContext2D().scale( scale, scale );

		// Draw the text
		getGraphicsContext2D().setFont( Font.font( fontSize ) );
		if( maxWidth == 0 ) {
			getGraphicsContext2D().fillText( text, x / scale, y / scale );
		} else {
			getGraphicsContext2D().fillText( text, x / scale, y / scale, maxWidth / scale );
		}

		// Reset transform
		getGraphicsContext2D().setTransform( transform );
	}

	protected void drawLine( double x1, double y1, double x2, double y2 ) {
		getGraphicsContext2D().strokeLine( xformX( x1 ), xformY( y1 ), xformX( x2 ), xformY( y2 ) );
	}

	protected void drawRect( double x, double y, double w, double h ) {
		getGraphicsContext2D().strokeRect( xformX( x ), xformY( y ), xformX( w ), xformY( h ) );
	}

	protected void drawOval( double x, double y, double w, double h ) {
		getGraphicsContext2D().strokeOval( xformX( x ), xformY( y ), xformX( w ), xformY( h ) );
	}

	protected void drawCenteredOval( double cx, double cy, double rx, double ry ) {
		double x = cx - rx;
		double y = cy - ry;
		double w = rx * 2;
		double h = ry * 2;
		getGraphicsContext2D().strokeOval( xformX( x ), xformY( y ), xformX( w ), xformY( h ) );
	}

	protected void drawText( String text, double x, double y, double textSize ) {
		drawText( text, x, y, textSize, 0 );
	}

	protected void drawText( String text, double x, double y, double textSize, double maxWidth ) {
		// Font sizes smaller than one don't scale as expected
		// so the workaround is to scale according the text size
		// and divide the coordinates by the size.

		double fontSize = 72;
		double scale = textSize / fontSize;

		// Scale line width
		double lineWidth = getGraphicsContext2D().getLineWidth();
		getGraphicsContext2D().setLineWidth( lineWidth / scale );

		// Scale the transform
		Affine transform = getGraphicsContext2D().getTransform().clone();
		getGraphicsContext2D().scale( scale, scale );

		// Draw the text
		getGraphicsContext2D().setFont( Font.font( fontSize ) );
		if( maxWidth == 0 ) {
			getGraphicsContext2D().strokeText( text, x / scale, y / scale );
		} else {
			getGraphicsContext2D().strokeText( text, x / scale, y / scale, maxWidth / scale );
		}

		// Reset transform
		getGraphicsContext2D().setTransform( transform );

		// Reset line width
		getGraphicsContext2D().setLineWidth( lineWidth );
	}

	protected void clearRect( double x, double y, double w, double h ) {
		getGraphicsContext2D().clearRect( xformX( x ), xformY( y ), xformX( w ), xformY( h ) );
	}

	protected double xformX( double value ) {
		//return size * (value + xOffset);
		//return size * value;
		return value;
	}

	protected double xformY( double value ) {
		//return size * (value + yOffset);
		//return size * value;
		return value;
	}

	protected double g( double value ) {
		return value / 32d;
	}

	protected double getIconDrawWidth() {
		return drawWidth;
	}

	protected Color getIconDrawColor() {
		return drawColor == null ? themeDrawColor : drawColor;
	}

	protected Color getIconFillColor() {
		return fillColor == null ? themeFillColor : fillColor;
	}

	protected Paint getIconFillPaint() {
		return getIconFillPaint( GradientShade.MEDIUM );
	}

	protected Paint getIconFillPaint( GradientShade shade ) {
		double buffer = 0.2;
		double y = Colors.getLuminance( theme.getPrimary() );

		double a = 0.75;
		double b = 0.25;
		double c = 0.2 * y;

		switch( shade ) {
			case LIGHT: {
				a = 1 - buffer;
				b = 0.4;
				break;
			}
			case MEDIUM: {
				a = 0.75;
				b = 0.35;
				break;
			}
			case DARK: {
				a = 0.6;
				b = 0 + buffer;
				break;
			}
		}

		Color colorA = Colors.getShade( getIconFillColor(), a + c );
		Color colorB = Colors.getShade( getIconFillColor(), b + c );

		//		Color colorA = Colors.mix( getIconFillColor(), Color.WHITE, a );
		//		Color colorB = Colors.mix( getIconFillColor(), Color.WHITE, b );

		return getGradientPaint( colorA, colorB );
	}

	protected Paint radialPaint( double x, double y, double r, Stop... stops ) {
		return new RadialGradient( 0, 0, xformX( x ), xformY( y ), xformX( r ), false, CycleMethod.NO_CYCLE, stops );
	}

	protected Paint radialPaint( double x, double y, double r, List<Stop> stops ) {
		return new RadialGradient( 0, 0, xformX( x ), xformY( y ), xformX( r ), false, CycleMethod.NO_CYCLE, stops );
	}

	protected static double distance( double x1, double y1, double x2, double y2 ) {
		return Math.sqrt( (Math.pow( y2 - y1, 2 ) + Math.pow( x2 - x1, 2 )) );
	}

	protected ProgramImage copy() {
		ProgramImage clone = null;

		try {
			clone = getClass().getDeclaredConstructor().newInstance();
			clone.setHeight( getHeight() );
			clone.setWidth( getWidth() );
			//clone.fireRender();
		} catch( Exception e ) {
			e.printStackTrace();
		}

		return clone;
	}

	private static Image resample( Image input, int scale ) {
		int w = (int)input.getWidth();
		int h = (int)input.getHeight();

		WritableImage output = new WritableImage( w * scale, h * scale );

		PixelReader reader = input.getPixelReader();
		PixelWriter writer = output.getPixelWriter();

		for( int y = 0; y < h; y++ ) {
			for( int x = 0; x < w; x++ ) {
				final int argb = reader.getArgb( x, y );
				for( int dy = 0; dy < scale; dy++ ) {
					for( int dx = 0; dx < scale; dx++ ) {
						writer.setArgb( x * scale + dx, y * scale + dy, argb );
					}
				}
			}
		}

		return output;
	}

	private Paint getGradientPaint( Color a, Color b ) {
		return new LinearGradient( xformX( 0.2 ), xformX( 0.2 ), xformX( 0.8 ), xformX( 0.8 ), false, CycleMethod.NO_CYCLE, new Stop( 0, a ), new Stop( 1, b ) );
	}

	public ProgramImage fireRender() {
		double size = Math.min( getWidth(), getHeight() );

		// Set the defaults
		setLineCap( StrokeLineCap.ROUND );
		setLineJoin( StrokeLineJoin.ROUND );
		setLineWidth( getIconDrawWidth() );
		setDrawPaint( getIconDrawColor() );
		setFillPaint( getIconFillPaint( GradientShade.MEDIUM ) );
		setFillRule( FillRule.EVEN_ODD );

		// Start rendering by clearing the icon area
		if( overrideContext == null ) {
			getGraphicsContext2D().setTransform( new Affine() );
			clearRect( 0, 0, 1, 1 );
			baseTransform = Transform.scale( size, size );
			reset();
		}

		//		// Just for research, set different color backgrounds per size
		//		if( size == 16 ) protected void setFillColor( Color.PURPLE );
		//		if( size == 24 ) protected void setFillColor( Color.BLUE );
		//		if( size == 32 ) protected void setFillColor( Color.GREEN );
		//		if( size == 64 ) protected void setFillColor( Color.YELLOW );
		//		if( size == 128 ) protected void setFillColor( Color.ORANGE );
		//		if( size == 256 ) protected void setFillColor( Color.RED );
		//		protected void fillRect( 0, 0, getWidth(), getHeight() );

		render();

		return this;
	}

	protected static class Point {

		private static final long serialVersionUID = -1520877460686311009L;

		public double x;

		public double y;

		public double z;

		public Point( double x, double y ) {
			this( x, y, 0 );
		}

		private Point( double x, double y, double z ) {
			this.x = x;
			this.y = y;
			this.z = z;
		}

		public final double getX() {
			return x;
		}

		public final double getY() {
			return y;
		}

		public final double getZ() {
			return z;
		}

		public final double getMagnitude() {
			return Math.sqrt( x * x + y * y + z * z );
		}

		public final double getAngle() {
			return Math.atan2( y, x );
		}

		public final double dot( Point vector ) {
			return x * vector.x + y * vector.y + z * vector.z;
		}

		public final Point cross( Point vector ) {
			return new Point( y * vector.z - z * vector.y, z * vector.x - x * vector.z, x * vector.y - y * vector.x );
		}

		public final Point plus( Point vector ) {
			return new Point( x + vector.x, y + vector.y, z + vector.z );
		}

		public final Point minus( Point vector ) {
			return new Point( x - vector.x, y - vector.y, z - vector.z );
		}

		public final Point times( double scale ) {
			return new Point( x * scale, y * scale, z * scale );
		}

	}

}