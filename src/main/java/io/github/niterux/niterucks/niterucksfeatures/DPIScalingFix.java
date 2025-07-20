package io.github.niterux.niterucks.niterucksfeatures;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class DPIScalingFix implements PreLaunchEntrypoint {
	@Override
	public void onPreLaunch() {
		System.setProperty("sun.java2d.uiScale", "1");
	}
}
