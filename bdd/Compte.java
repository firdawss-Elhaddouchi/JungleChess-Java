package jungleGame.bdd;

import java.sql.*;

public class Compte {
	private int id;
    private String username;
    private String password;
    
    public Compte(int id,String username, String password) {
		this.id=id;
		this.username=username;
		this.password=password;
	}
    /***getters***/
    public int getId() {
		return id;
	}
    public String getPassword() {
		return password;
	}
    public String getUsername() {
		return username;
	}
    
    /***setters***/
    public void setId(int id) {
		this.id = id;
	}
    public void setPassword(String password) {
		this.password = password;
	}
    public void setUsername(String username) {
		this.username = username;
	}
    
}
