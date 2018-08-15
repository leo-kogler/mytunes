package org.coderocks;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SQLConnection {

	private Connection conn = null;

	public void connect() {

		try {
			this.conn = DriverManager
					.getConnection("jdbc:mysql://localhost:3306/mytunes?" + "user=root&password=@pAssw0rd!");

		} catch (SQLException ex) {
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}

	}

	public int insertSong(Mp3Song song) {
		int id = 0;
		try {
			String insert = "INSERT INTO mytunes.mp3songs (filename, song, duration, image )VALUES (?,?,?,?);";

			PreparedStatement st = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
			st.setString(1, song.getFilename());
			st.setBytes(2, song.getSong());
			st.setInt(3, song.getDuration());
			st.setBytes(4, song.getImage());

			st.execute();

			try (ResultSet generatedKeys = st.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					song.setId(generatedKeys.getLong(1));
				} else {
					throw new SQLException("Storing mp3song failed, no ID obtained.");
				}
			}

			// conn.close();
		} catch (Exception e) {
			System.err.println("Got an exception! ");
			System.err.println(e.getMessage());
			return id;
		}

		return id;
	}

	public void insertSongMeta(Mp3Song song) {
		try {

			String insert = "INSERT INTO mytunes.mp3meta (filename, title, songid, year, artist, album, duration )VALUES (?,?,?,?,?,?,?);";

			String title = song.getTitle();
			String filename = song.getFilename();

			if (title.equals("unknow")) {
				title = filename.split("//.")[0];
			}

			PreparedStatement st = conn.prepareStatement(insert);
			st.setString(1, song.getFilename());
			st.setString(2, title);
			st.setShort(4, song.getYear());
			st.setString(5, song.getArtist());
			st.setString(6, song.getAlbum());
			st.setInt(7, song.getDuration());
			st.setLong(3, song.getId());
			st.execute();

			// conn.close();
		} catch (Exception e) {
			System.err.println("Uuups ...");
			System.err.println(e.getMessage());

		}
	}

	public List<Mp3Song> getSongFromTitle(String filename) {
		List<Mp3Song> list = new ArrayList<Mp3Song>();

		String select = "";

		if (filename.equals("*")) {

			select = "SELECT * FROM mytunes.mp3meta;";

		} else {

			select = "SELECT * FROM mytunes.mp3meta WHERE filename like '" + filename + "' ;";

		}

		System.out.println(select);

		try {
			PreparedStatement st = conn.prepareStatement(select);
			ResultSet rs = st.executeQuery();
			while (rs.next()) {

				Mp3Song song = new Mp3Song();
				song.setFilename(rs.getString("filename"));
				song.setTitle(rs.getString("title"));
				song.setId(rs.getLong("songid"));
				song.setAlbum(rs.getString("album"));
				song.setArtist(rs.getString("artist"));
				song.setDuration(rs.getInt("duration"));
				song.setYear(rs.getShort("year"));
				list.add(song);

			}

		} catch (SQLException e) {

			e.printStackTrace();
		}

		return list;

	}

	public byte[] fetchSong(Mp3Song song) {

		String select = "SELECT * FROM mytunes.mp3songs WHERE id = '" + song.getId() + "' ;";

		byte[] ba = null;
		try {

			PreparedStatement st = conn.prepareStatement(select);
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				ba = rs.getBytes("song");
			}

		} catch (SQLException e) {

			e.printStackTrace();
		}

		return ba;

	}

	public byte[] fetchImage(long id) {

		String select = "SELECT * FROM mytunes.mp3songs WHERE id = '" + id + "' ;";

		byte[] ba = null;
		try {

			PreparedStatement st = conn.prepareStatement(select);
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				ba = rs.getBytes("image");
			}

		} catch (SQLException e) {

			e.printStackTrace();
		}

		return ba;

	}

	public void close() {

		try {
			conn.close();

		} catch (SQLException e) {

			e.printStackTrace();
		}

	}

	public void traverse(File file) {

		if (file.isDirectory()) {
			File[] fList = file.listFiles();
			for (File f : fList) {
				if (f.isDirectory()) {
					traverse(f);
				} else {
					f.delete();
				}
			}

		} else {
			System.out.println("is file");
		}

	}

	public void uploadPlayList(byte[] playlist) {
		String insert = "INSERT playlist, id into mytunes.playlists (playlist, id) VALUES (?,?);";

		PreparedStatement st;
		try {
			st = conn.prepareStatement(insert);
			st.setBytes(0, playlist);
			st.execute();
		} catch (SQLException e) {

			e.printStackTrace();
		}

	}

	public byte[] downStreamPlayList(int id) {
		String select = "SELECT * FROM mytunes.playlists WHERE id = " + id + ";";

		byte[] playlist = null;
		try {
			PreparedStatement st = conn.prepareStatement(select);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {

				playlist = rs.getBytes("playlist");

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return playlist;
	}

	public List<Mp3Song> getSongs(String field, String value) {
		List<Mp3Song> list = new ArrayList<Mp3Song>();

		String select = "SELECT * FROM mytunes.mp3meta WHERE " + field + " like '" + value + "' ;";

		try {
			PreparedStatement st = conn.prepareStatement(select);
			ResultSet rs = st.executeQuery();
			while (rs.next()) {

				Mp3Song song = new Mp3Song();
				song.setFilename(rs.getString("filename"));
				song.setTitle(rs.getString("title"));
				song.setId(rs.getLong("songid"));
				song.setAlbum(rs.getString("album"));
				song.setArtist(rs.getString("artist"));
				song.setDuration(rs.getInt("duration"));
				song.setYear(rs.getShort("year"));
				list.add(song);

			}

		} catch (SQLException e) {

			e.printStackTrace();
		}

		return list;

	}

	public byte[] fetchSongById(long id) {

		String select = "SELECT song FROM mytunes.mp3songs WHERE id = '" + id + "' ;";

		System.out.println(select);

		byte[] ba = null;
		try {

			PreparedStatement st = conn.prepareStatement(select);
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				ba = rs.getBytes("song");
			}

		} catch (SQLException e) {

			e.printStackTrace();
		}

		return ba;

	}
}
