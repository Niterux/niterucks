package io.github.niterux.niterucks.bevofeatures;

import io.github.niterux.niterucks.niterucksfeatures.RainbowManager;

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
		if (this.isRainbow)
			return RainbowManager.getColor();
		return color;
	}

	public String getName() {
		return name;
	}

	public String getNickname() {
		return nickname;
	}

	public String getPrefix() {
		return prefix;
	}

	public PlayerNameStatus(String name, int color, boolean isRainbow) {
		this.name = name;
		this.color = color;
		this.isRainbow = isRainbow;
	}

	public PlayerNameStatus(String name, String nickname, String prefix) {
		this.name = name;
		this.nickname = nickname;
		this.prefix = prefix;
	}
}
