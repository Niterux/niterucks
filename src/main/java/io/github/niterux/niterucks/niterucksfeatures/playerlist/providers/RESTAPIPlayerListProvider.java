package io.github.niterux.niterucks.niterucksfeatures.playerlist.providers;

import io.github.niterux.niterucks.Niterucks;
import io.github.niterux.niterucks.api.playerlist.PlayerListProvider;
import org.jetbrains.annotations.Nullable;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class RESTAPIPlayerListProvider implements PlayerListProvider {
	private final HttpRequest request;
	private final ScheduledThreadPoolExecutor APITimer = new ScheduledThreadPoolExecutor(1);
	protected RESTAPIPlayerListProviderConfig config;
	HttpClient client = HttpClient.newHttpClient();
	private ScheduledFuture<?> periodicDataRetriever;
	private String[] players;
	private boolean enabled = false;
	private int totalRequestsThisSession = 0;
	private int totalErrorsThisSession = 0;

	public RESTAPIPlayerListProvider(RESTAPIPlayerListProviderConfig config) {
		this.config = config;
		request = HttpRequest.newBuilder().uri(config.RESTAPIEndpoint()).build();
	}

	@Override
	public @Nullable String[] getPlayerNames() {
		if (!enabled)
			return null;
		return players;
	}

	@Override
	public void onDisconnectedFromServer() {
		if (!enabled)
			return;
		players = null;
		totalRequestsThisSession = 0;
		totalErrorsThisSession = 0;
		enabled = false;
		if (periodicDataRetriever != null)
			periodicDataRetriever.cancel(true);
	}

	@Override
	public void onConnectedToServer(String serverAddress) {
		players = null;
		if (isSupportedServer(serverAddress)) {
			periodicDataRetriever = APITimer.scheduleAtFixedRate(() -> this.dataReadingHandler(serverAddress), 2L, 5L, TimeUnit.SECONDS);
			enabled = true;
		}
	}

	private void dataReadingHandler(String serverAddress) {
		HttpResponse<String> response;
		try {
			totalRequestsThisSession++;
			response = client.send(request, HttpResponse.BodyHandlers.ofString());
			this.players = dataHandler(response.body(), serverAddress);
		} catch (Exception e) {
			this.players = null;
			Niterucks.LOGGER.warn("Receiving data from player list api failed! {}, from {}", e.getClass().getName(), config.toString());
			if ((totalRequestsThisSession + 5) / (totalErrorsThisSession++) <= 2) {
				Niterucks.LOGGER.info("Stopping problematic RESTAPIPlayerListProvider {}", config.toString());
				Niterucks.LOGGER.info("totalRequestsThisSession: {}, totalErrorsThisSession: {}", totalRequestsThisSession, totalErrorsThisSession);
				Niterucks.LOGGER.debug("Stacktrace for API error: ", e);
				onDisconnectedFromServer();
			}
		}

	}

	public boolean isSupportedServer(String serverAddress) {
		for (String supportedServerAddress : config.supportedServerAddresses()) {
			if (supportedServerAddress.equals(serverAddress)) {
				return true;
			}
		}
		return false;
	}

	public abstract String[] dataHandler(String data, String serverAddress);
}

