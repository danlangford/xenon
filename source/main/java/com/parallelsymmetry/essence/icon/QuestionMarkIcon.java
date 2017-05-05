package com.parallelsymmetry.essence.icon;

import com.parallelsymmetry.essence.ProgramIcon;

public class QuestionMarkIcon extends ProgramIcon {

	@Override
	protected void render() {
		beginPath();
		moveTo( C, ZB );
		bezierCurveTo( ZG, ZB, ZG, K, ZG, ZE );
		bezierCurveTo( ZG, ZG, ZG, ZJ, C, ZJ );
		bezierCurveTo( ZJ, ZJ, ZJ, ZG, ZJ, ZE );
		bezierCurveTo( ZJ, K, ZJ, ZB, C, ZB );
		closePath();
		fill();
		draw();

		fillOval( ZG, ZL, K, K );
		drawOval( ZG, ZL, K, K );
	}

}
