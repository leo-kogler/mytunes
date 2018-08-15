package org.coderocks;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.tag.TagField;
import org.jaudiotagger.tag.mp4.Mp4FieldKey;
import org.jaudiotagger.tag.mp4.Mp4Tag;
import org.springframework.beans.factory.annotation.Configurable;
import com.vaadin.annotations.Push;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.AllUploadFinishedHandler;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.MultiFileUpload;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.UploadFinishedHandler;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.UploadStateWindow;

import de.odysseus.ithaka.audioinfo.AudioInfo;
import de.odysseus.ithaka.audioinfo.m4a.M4AInfo;
import de.odysseus.ithaka.audioinfo.mp3.MP3Info;
import de.steinwedel.messagebox.MessageBox;

@Push
@SuppressWarnings("serial")
@Configurable
@SpringView(name = UploadView.NAME)
public class UploadView extends VerticalLayout implements View {

	public static final String NAME = "UploadView";
	String folder;
	File tempFolder;
	String tempFolderPath;
	String filePath;

	FormLayout content;

	@PostConstruct
	void init() {

		
		folder = System.getProperty("user.home") + File.separator + "myfolder";

		setSizeFull();
		addStyleName("login-background"); 
		
		Panel panel = new Panel();
		panel.setWidth("800px");
		panel.setHeight("600px");
		
		
		
		VerticalLayout vLayout = new VerticalLayout();
		vLayout.setSizeFull();

		content = new FormLayout();

		UploadFinishedHandler uploadFinishedHandler = new UploadFinishedHandler() {

			@Override
			public void handleFile(InputStream stream, String filename, String mimeType, long length,
					int filesLeftInQueue) {
				
				SQLConnection conn = new SQLConnection();
				conn.connect();
				File file = null;
				try {

					Mp3Song song = new Mp3Song();
					song.setFilename(filename);

					FileOutputStream fos = null;

					tempFolderPath = folder + File.separator + "tmp";
					filePath = tempFolderPath + File.separator + filename;
					// tempFolder = File.createTempFile(tempFolderPath,
					// Long.toString(System.nanoTime()));
					new File(tempFolderPath).mkdirs();
					Label lbl = new Label(filename);
					content.addComponent(lbl);
					file = new File(filePath);
					fos = new FileOutputStream(file);
					byte[] buffer = new byte[stream.available()];
					stream.read(buffer);
					fos.write(buffer);
					fos.close();

					song.setSong(buffer);

					String artist = "unknown";
					String album = "unknown";
					String title = "unknow";
					short year = 0;
					int duration = 3;
					byte[] cover; 

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
					cover = audioInfo.getCover();
					
					song.setDuration(duration);
					song.setYear(year);
					song.setArtist(artist);
					song.setAlbum(album);
					song.setTitle(title);
					song.setImage(cover);

					conn.insertSong(song);
					conn.insertSongMeta(song);

				} catch (FileNotFoundException ex) {

				} catch (IOException ex) {

				}
				conn.close();

			}
		};

		UploadStateWindow uploadStateWindow = new UploadStateWindow();
		MultiFileUpload multiFileUpload = new MultiFileUpload(uploadFinishedHandler, uploadStateWindow);

		
		multiFileUpload.setAllUploadFinishedHandler(new AllUploadFinishedHandler() {
			@Override
			public void finished() {
				MessageBox
				.createInfo()
				.withCaption("FileUpload")
				.withMessage("Upload went fine !")
				.withOkButton()
				.open();
			}
		});
		content.addComponent(multiFileUpload);
		panel.setContent(content);
		vLayout.addComponent(panel);
		vLayout.setComponentAlignment(panel, Alignment.MIDDLE_CENTER);
		addComponent(vLayout);
		
		content.addComponent(new Button("Navigate to Grid", new Button.ClickListener() {
			@Override
			public void buttonClick(final Button.ClickEvent event) {

				getUI().getNavigator().navigateTo(Mp3GridView.NAME);
				Page.getCurrent().setUriFragment("!" + Mp3GridView.NAME);

			}
		}));


	}

	@Override
	public void enter(ViewChangeEvent event) {

	}
}