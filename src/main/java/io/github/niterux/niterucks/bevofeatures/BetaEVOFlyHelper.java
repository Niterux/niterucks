package io.github.niterux.niterucks.bevofeatures;

import io.github.niterux.niterucks.Niterucks;

public class BetaEVOFlyHelper {
	public static boolean flyAllowed = false;
	public static boolean flying = false;
	public static int flySpeed = Niterucks.CONFIG.defaultFlySpeed.get();
	public static boolean flyingButtonHeld = false;
	public static boolean flyingTouchedGround = true;
	public static boolean[] flyingControls = {
		false, //fly button
		false, //up
		false, //down
		false //speed modifier
	};

	public static void resetFly() {
		flyAllowed = false;
		flying = false;
		flySpeed = Niterucks.CONFIG.defaultFlySpeed.get();
		flyingButtonHeld = false;
		flyingTouchedGround = true;

		BetaEVOFlyHelper.flyingControls = new boolean[]{
			false, //fly button
			false, //up
			false, //down
			false //speed modifier
		};
	}
}
