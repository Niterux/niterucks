package io.github.niterux.niterucks.niterucksfeatures.playerlist;

import java.net.URL;

public record RESTAPIPlayerListProviderConfig(String[] supportedServerAddresses, URL RESTAPIEndpoint) {}
