package io.github.niterux.niterucks.niterucksfeatures.playerlist.providers;

import io.github.niterux.niterucks.Niterucks;
import io.github.niterux.niterucks.api.playerlist.PlayerListProvider;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.Nullable;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class RESTAPIPlayerListProvider implements PlayerListProvider {
	private static final ScheduledThreadPoolExecutor APITimer = new ScheduledThreadPoolExecutor(1);
	private static final ObjectArrayList<ScheduledFuture<?>> periodicDataRetrievers = new ObjectArrayList<>();
	private final HttpRequest request;
	private final Object APILock = new Object();
	protected RESTAPIPlayerListProviderConfig config;
	HttpClient client = HttpClient.newHttpClient();
	private ScheduledFuture<?> periodicDataRetriever;
	private volatile String[] players;
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
		synchronized (this.APILock) {
			return players;
		}
	}

	@Override
	public void onDisconnectedFromServer() {
		if (!enabled)
			return;
		players = null;
		totalRequestsThisSession = 0;
		totalErrorsThisSession = 0;
		enabled = false;
		if (periodicDataRetrievers.isEmpty())
			return;
		var iterator = periodicDataRetrievers.iterator();
		while (iterator.hasNext()) {
			iterator.next().cancel(true);
			iterator.remove();
		}
	}

	@Override
	public void onConnectedToServer(String serverAddress) {
		players = null;
		if (isSupportedServer(serverAddress)) {
			this.periodicDataRetriever = APITimer.scheduleAtFixedRate(() -> this.dataReadingHandler(serverAddress), 2L, 5L, TimeUnit.SECONDS);
			periodicDataRetrievers.add(this.periodicDataRetriever);
			enabled = true;
		}
	}

	public boolean isSupportedServer(String serverAddress) {
		for (String supportedServerAddress : config.supportedServerAddresses()) {
			if (supportedServerAddress.equals(serverAddress))
				return true;
		}
		return false;
	}

	private void dataReadingHandler(String serverAddress) {
		HttpResponse<String> response = null;
		try {
			totalRequestsThisSession++;
			response = client.send(request, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() != 200)
				Niterucks.LOGGER.warn("Received non 200 status code from {}", config.RESTAPIEndpoint().toString());
			String[] players = dataHandler(response.body(), serverAddress);
			synchronized (this.APILock) {
				this.players = players;
			}
		} catch (
			InterruptedException e) {//Everything is going to be okay, relax
		} catch (Exception e) {
			synchronized (this.APILock) {
				this.players = null;
			}
			Niterucks.LOGGER.warn("Receiving data from player list api failed! {}, from {}", e.getClass().getName(), config.RESTAPIEndpoint().toString());
			if (response != null)
				Niterucks.LOGGER.warn(response.body());
			if ((totalRequestsThisSession + 5) / (totalErrorsThisSession++) <= 2) {
				Niterucks.LOGGER.info("Stopping problematic RESTAPIPlayerListProvider {}", config.toString());
				Niterucks.LOGGER.info("totalRequestsThisSession: {}, totalErrorsThisSession: {}", totalRequestsThisSession, totalErrorsThisSession);
				Niterucks.LOGGER.debug("Stacktrace for API error: ", e);
				killDataRetriever();
			}
		}

	}

	public abstract String[] dataHandler(String data, String serverAddress);

	private void killDataRetriever() {
		if (!enabled)
			return;
		players = null;
		totalRequestsThisSession = 0;
		totalErrorsThisSession = 0;
		enabled = false;
		periodicDataRetriever.cancel(true);
	}
}

