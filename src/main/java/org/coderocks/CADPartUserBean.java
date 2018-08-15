package org.coderocks;

public class CADPartUserBean {
	
	private String emailAdd;
	private String token;
	private int logins;
	private String firstname;
	private String lastname;

	public CADPartUserBean (String emailAdd, String token, int logins) {
		
		this.emailAdd = emailAdd;
		this.token = token;
		this.logins = logins;
		
	}

	public String getUserName () {
		return emailAdd;
	}
	
	public String getToken () {
		return token;
	}
	
	public int getLogins() {
		return logins;
	}

	public String getLastName() {

		return lastname;
	}
	
	public String getFirstName () {
		return firstname;
	}
	
	
	public void setFirstName (String firstname) {
		this.firstname = firstname;
	}
	
	public void setLastName (String lastname) {

		this.lastname = lastname;
	}
	
}
