package org.coderocks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.VaadinSessionScope;

import java.util.List;

@Component
@SpringComponent
@VaadinSessionScope
public class UserService {

	@Autowired
	private JdbcTemplate jdbcTemplate;


	public List<CADPartUserBean> getUserData(String emailAddr) {
		List <CADPartUserBean> list = jdbcTemplate.query("SELECT email, token, logins from mytunes.userdata WHERE email = '" + emailAddr + "';", (rs,
				rowNum) -> new CADPartUserBean(rs.getString("email"), rs.getString("token"), rs.getInt("logins")));
		return list;
	}
	
	public void insertNewUser (CADPartUserBean user) {
		jdbcTemplate.update("INSERT INTO mytunes.userdata (email, token, logins, firstname, lastname )VALUES (?,?,?,?,?);",
						user.getUserName(), user.getToken(), 0, user.getFirstName(), user.getLastName());
	}
	
	public void insertSong (Mp3Song song) {
		jdbcTemplate.update("INSERT INTO mytunes.mp3songs (filename, song, duration )VALUES (?,?,?);",
						song.getFilename(), song.getSong(), song.getDuration());
	}

}