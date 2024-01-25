package io.github.niterux.niterucks.bevofeatures;

public class BetaEVOFlyHelper {
	public static boolean flyAllowed = false;
	public static boolean flying = false;
	public static double flySpeed = 0.8;
	public static boolean flyingButtonHeld = false;
	public static boolean flyingTouchedGround = true;

	public static void resetFly(){
		flyAllowed = false;
		flying = false;
		flySpeed = 0.8;
		flyingButtonHeld = false;
		flyingTouchedGround = true;
	}
}
