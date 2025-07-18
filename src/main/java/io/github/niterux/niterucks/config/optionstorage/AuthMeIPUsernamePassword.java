package io.github.niterux.niterucks.config.optionstorage;

public class AuthMeIPUsernamePassword {
	private final String ipAddress;
	private final String username;
	private final String password;

	public AuthMeIPUsernamePassword() {
		this("", "", "");
	}

	public AuthMeIPUsernamePassword(AuthMeIPUsernamePassword ipUsernamePassword) {
		this(ipUsernamePassword.getIpAddress(), ipUsernamePassword.getUsername(), ipUsernamePassword.getPassword());
	}

	public AuthMeIPUsernamePassword(String ipAddress, String username, String password) {
		this.ipAddress = ipAddress;
		this.username = username;
		this.password = password;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
}
