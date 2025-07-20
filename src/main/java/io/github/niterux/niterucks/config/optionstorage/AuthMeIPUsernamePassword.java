package io.github.niterux.niterucks.config.optionstorage;

public class AuthMeIPUsernamePassword {
	private String ipAddress;

	private String username;
	private String password;
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

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
