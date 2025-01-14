package com.avereon.xenon.icon;

import com.avereon.xenon.ProgramIcon;

public class ProductIcon extends ProgramIcon {

	@Override
	protected void render() {
		fillOval( g( 5 ), g( 5 ), g( 22 ), g( 22 ) );
		drawOval( g( 5 ), g( 5 ), g( 22 ), g( 22 ) );
	}

	public static void main( String[] commands ) {
		proof( new ProductIcon() );
	}

}
