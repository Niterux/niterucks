package io.github.niterux.niterucks.bevofeatures;

public class BetaEVOFlyHelper {
	public static boolean flyAllowed = false;
	public static boolean flying = false;
	public static int flySpeed = 16;
	public static boolean flyingButtonHeld = false;
	public static boolean flyingTouchedGround = true;
	public static boolean[] flyingControls = {
		false, //fly button
		false, //up
		false, //down
		false //speed modifier
	};

	public static void resetFly(){
		flyAllowed = false;
		flying = false;
		flySpeed = 16;
		flyingButtonHeld = false;
		flyingTouchedGround = true;

		boolean[] flyingControls = {
			false, //fly button
			false, //up
			false, //down
			false //speed modifier
		};
	}
}
