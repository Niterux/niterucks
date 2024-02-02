package io.github.niterux.niterucks.bevofeatures;

public class PlayerNameStatus {
	protected String name;
	protected String nickname;
	protected String prefix;
	protected int color = 0xFFFFFF;
	protected boolean isRainbow;
	private boolean backLight; //unimplemented

	public boolean getIsRainbow() {
		return isRainbow;
	}

	public int getColor() {
		return color;
	}

	public PlayerNameStatus(String name, int color, boolean isRainbow) {
		this.name = name;
		this.color = color;
		this.isRainbow = isRainbow;
	}
}
