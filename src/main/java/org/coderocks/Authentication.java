package org.coderocks;

public class Authentication {
	
	private String username;
	private String password;
	
	public Authentication() {
		setUsername("myuser");
		setPassword("mypass");
	}

	private void setUsername(String username) {
		this.username = username;
	}
	
	private String getUsername(){
		return this.username;
	}

	private void setPassword(String password) {
		this.password = password;
	}
	
	private String getPassword(){
		return this.password;
	}
	
	public Boolean authenticate(String username, String password, String dbusername, String dbpassword){
		if(username.equals(dbusername) && password.equals(dbpassword)){
			return true;
		}
		return false;
	}

}
