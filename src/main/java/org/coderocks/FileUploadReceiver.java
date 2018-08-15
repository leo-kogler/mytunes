package org.coderocks;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jaudiotagger.audio.AudioFile;
import org.springframework.beans.factory.annotation.Configurable;
import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import de.odysseus.ithaka.audioinfo.AudioInfo;
import de.odysseus.ithaka.audioinfo.m4a.M4AInfo;
import de.odysseus.ithaka.audioinfo.mp3.MP3Info;


@Configurable
public class FileUploadReceiver implements Receiver, SucceededListener {
	/**
	 * 
	 */

	private static final long serialVersionUID = 1L;
	File file;
	String folder = System.getProperty("user.home") + File.separator + "myfolder";

	public OutputStream receiveUpload(String fileName, String mimeType) {

		FileOutputStream fos = null;

		try {
			file = new File(folder + File.separator + fileName);
			// byte[] ba = new byte[(int) file.length()];

			fos = new FileOutputStream(file);
			
			
		} catch (final IOException e) {
			new Notification("Could not open file", e.getMessage(), Notification.TYPE_ERROR_MESSAGE.ERROR_MESSAGE)
					.show(Page.getCurrent());
			return null;
		}

		return fos;
	}

	@Override
	public void uploadSucceeded(SucceededEvent event) {

		SQLConnection conn = new SQLConnection();
		conn.connect();

		Mp3Song song = new Mp3Song();
		song.setFilename(file.getName());
		AudioFile af = new AudioFile();

		String artist = "unknown";
		String album = "unknown";
		String title = "unknow";
		short year = 0;
		int duration = 3;
	
		AudioInfo audioInfo = null;
		if (song.getFilename().contains("mp3")) {
		try (InputStream input = new BufferedInputStream(new FileInputStream(file))) {
			audioInfo = new MP3Info(input, file.length());

			
		} catch (Exception e) {
	
		}
		} else if (song.getFilename().contains("m4")) {
			

			try (InputStream input = new BufferedInputStream(new FileInputStream(file))) {
				audioInfo = new M4AInfo(input);

			} catch (Exception e) {

			}
		}
		
		
		album = audioInfo.getAlbum();
		artist = audioInfo.getArtist();
		title = audioInfo.getTitle();
		year = audioInfo.getYear();
		

		byte[] ba = null;
		try {
			ba = Files.readAllBytes(file.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		song.setSong(ba);
		song.setDuration(duration);
		song.setYear(year);
		song.setArtist(artist);
		song.setAlbum(album);
		song.setTitle(title);

		conn.insertSong(song);
		conn.insertSongMeta(song);
		file.delete();
		conn.close();

	}

	public static String getTimeStamp() {
	    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
	    Date current = new Date();
	    String date = sdfDate.format(current);
	    return date;
	}
	
}