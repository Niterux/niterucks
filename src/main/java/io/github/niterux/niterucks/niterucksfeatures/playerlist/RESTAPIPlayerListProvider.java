package io.github.niterux.niterucks.niterucksfeatures.playerlist;

import io.github.niterux.niterucks.api.playerlist.PlayerListProvider;

import java.util.concurrent.ScheduledThreadPoolExecutor;

public abstract class RESTAPIPlayerListProvider implements PlayerListProvider {
	private final RESTAPIPlayerListProviderConfig config;
	private boolean enabled = false;
	private final ScheduledThreadPoolExecutor APITimer = new ScheduledThreadPoolExecutor(1);

	public RESTAPIPlayerListProvider(RESTAPIPlayerListProviderConfig config) {
		this.config = config;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public RESTAPIPlayerListProviderConfig getConfig() {
		return config;
	}

	public void startTimer() {

	}
}
