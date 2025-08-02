package io.github.niterux.niterucks.config.optionstorage;

import com.google.gson.reflect.TypeToken;
import io.github.niterux.niterucks.Niterucks;
import it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AuthMeWholeListOptionStorage {
	private static final Path AUTHME_STORAGE_FILE = FabricLoader.getInstance().getConfigDir().resolve("niterucks_authme.json");
	private static final AuthMeWholeListOptionStorage INSTANCE = new AuthMeWholeListOptionStorage();
	public final List<AuthMeIPUsernamePassword> entries = new ArrayList<>();

	private AuthMeWholeListOptionStorage() {
	}

	public static AuthMeWholeListOptionStorage getInstance() {
		return INSTANCE;
	}

/*	public List<AuthMeIPUsernamePassword> getForServer(String ip) {
		if (ip == null) return Collections.emptyList();
		return entries.stream().filter(e -> ip.equals(e.getIpAddress())).toList();
	}*/

	public void save() {
		try {
			Files.createDirectories(AUTHME_STORAGE_FILE.getParent());
			try (var out = Files.newBufferedWriter(AUTHME_STORAGE_FILE)) {
				Niterucks.GSON.toJson(entries, out);
			}
		} catch (IOException e) {
			Niterucks.LOGGER.warn("Failed to save authme entries", e);
		}
	}

	public void load() {
		if (!Files.exists(AUTHME_STORAGE_FILE)) {
			save();
			return;
		}
		try (var in = Files.newBufferedReader(AUTHME_STORAGE_FILE)) {
			List<AuthMeIPUsernamePassword> loaded = Niterucks.GSON.fromJson(in, new TypeToken<>() {
			});
			entries.addAll(loaded);
		} catch (IOException e) {
			Niterucks.LOGGER.warn("Failed to save authme entries", e);
		}
	}

	public HashMap<String, ObjectObjectImmutablePair<String, String>> toHashMap() {
		HashMap<String, ObjectObjectImmutablePair<String, String>> easyIpList = new HashMap<>();
		entries.forEach((AuthMeIPUsernamePassword authMeIPUsernamePassword) -> easyIpList.putIfAbsent(authMeIPUsernamePassword.getIpAddress(), new ObjectObjectImmutablePair<>(authMeIPUsernamePassword.getUsername(), authMeIPUsernamePassword.getPassword())));
		return easyIpList;
	}
}
