package com.xeomar.xenon.icon;

public class ExitIcon extends XIcon {

	@Override
	protected void render() {
		xPath();
		fillAndDraw();
	}

	public static void main( String[] commands ) {
		proof( new ExitIcon() );
	}

}