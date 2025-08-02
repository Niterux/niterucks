package io.github.niterux.niterucks.niterucksfeatures.playerlist.providers;

import java.net.URI;

public record RESTAPIPlayerListProviderConfig(String[] supportedServerAddresses, URI RESTAPIEndpoint) {
}
