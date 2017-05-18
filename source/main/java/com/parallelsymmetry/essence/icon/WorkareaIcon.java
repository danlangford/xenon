package com.parallelsymmetry.essence.icon;

import com.parallelsymmetry.essence.ProgramIcon;

public class WorkareaIcon extends ProgramIcon {

	@Override
	protected void render() {
		fillRect( g( 5 ), g( 5 ), g( 22 ), g( 22 ) );
		drawRect( g( 5 ), g( 5 ), g( 22 ), g( 22 ) );
	}

	public static void main( String[] commands ) {
		proof( new WorkareaIcon() );
	}

}