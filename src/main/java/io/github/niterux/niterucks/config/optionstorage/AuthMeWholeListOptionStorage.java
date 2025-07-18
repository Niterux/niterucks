package io.github.niterux.niterucks.config.optionstorage;

import it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;

import java.util.ArrayList;
import java.util.HashMap;

public class AuthMeWholeListOptionStorage {
	public ArrayList<AuthMeIPUsernamePassword> IPUsernamePassword;
	public String name;

	public AuthMeWholeListOptionStorage(String name) {
		this(name, new ArrayList<>());
	}

	public AuthMeWholeListOptionStorage(String name, ArrayList<AuthMeIPUsernamePassword> IPUsernamePassword) {
		this.name = name;
		this.IPUsernamePassword = IPUsernamePassword;
	}

	public HashMap<String, ObjectObjectImmutablePair<String, String>> toHashMap() {
		HashMap<String, ObjectObjectImmutablePair<String, String>> easyIpList = new HashMap<>();
		IPUsernamePassword.forEach((AuthMeIPUsernamePassword authMeIPUsernamePassword) -> easyIpList.putIfAbsent(authMeIPUsernamePassword.getIpAddress(), new ObjectObjectImmutablePair<>(authMeIPUsernamePassword.getUsername(), authMeIPUsernamePassword.getUsername())));
		return easyIpList;
	}
}
