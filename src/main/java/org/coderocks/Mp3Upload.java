package org.coderocks;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.springframework.beans.factory.annotation.Configurable;

import javazoom.jl.decoder.Header;
import com.vaadin.annotations.Push;

import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.AllUploadFinishedHandler;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.MultiFileUpload;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.UploadFinishedHandler;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.UploadStateWindow;

import de.odysseus.ithaka.audioinfo.AudioInfo;
import de.odysseus.ithaka.audioinfo.m4a.M4AInfo;
import de.odysseus.ithaka.audioinfo.mp3.MP3Info;
import de.steinwedel.messagebox.MessageBox;
import javazoom.jl.decoder.Bitstream;

@Push
@SuppressWarnings("serial")
public class Mp3Upload extends HorizontalLayout {

	public static final String NAME = "UploadView";
	String folder;
	File tempFolder;
	String tempFolderPath;
	String filePath;
	VerticalLayout vLayout;

	public Mp3Upload() {
		folder = System.getProperty("user.home") + File.separator + "myfolder";

		vLayout = new VerticalLayout();

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
					// vLayout.addComponent(lbl);
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
					int duration = 0;
					byte[] cover;

					Header h = null;
					AudioInfo audioInfo = null;
					Bitstream bitstream = null;
					if (song.getFilename().contains("mp3")) {

						try (InputStream input = new BufferedInputStream(new FileInputStream(file))) {

							AudioFile audioFile = AudioFileIO.read(file);
							duration = audioFile.getAudioHeader().getTrackLength();

							audioInfo = new MP3Info(input, file.length());

						} catch (Exception e) {

						}
					} else if (song.getFilename().contains("m4")) {

						try (InputStream input = new BufferedInputStream(new FileInputStream(file))) {

							AudioFile audioFile = AudioFileIO.read(file);
							duration = audioFile.getAudioHeader().getTrackLength();

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
		// multiFileUpload.setUploadButtonIcon(icon);
		addDropArea(multiFileUpload);

		multiFileUpload.setAllUploadFinishedHandler(new AllUploadFinishedHandler() {
			@Override
			public void finished() {
				MessageBox.createInfo().withCaption("Song Upload").withMessage("Upload finished !").withOkButton()
						.open();
			}
		});

		/*
		 * vLayout.addComponent(new Button("Navigate to Grid", new
		 * Button.ClickListener() {
		 * 
		 * @Override public void buttonClick(final Button.ClickEvent event) {
		 * 
		 * getUI().getNavigator().navigateTo(Mp3GridView.NAME);
		 * Page.getCurrent().setUriFragment("!" + Mp3GridView.NAME);
		 * 
		 * } }));
		 */
		vLayout.addComponent(multiFileUpload);
		vLayout.setComponentAlignment(multiFileUpload, Alignment.BOTTOM_CENTER);
		addComponent(vLayout);
		setComponentAlignment(vLayout, Alignment.TOP_CENTER);
	}

	private void addDropArea(MultiFileUpload multiFileUpload) {
		Label dropLabel = new Label("Drop files here...");
		dropLabel.addStyleName(ValoTheme.LABEL_HUGE);
		Panel dropArea = new Panel(dropLabel);
		dropArea.setWidth(300, Unit.PIXELS);
		dropArea.setHeight(150, Unit.PIXELS);

		DragAndDropWrapper dragAndDropWrapper = multiFileUpload.createDropComponent(dropArea);
		dragAndDropWrapper.setSizeUndefined();
		// vLayout.addComponent(dragAndDropWrapper, 2);
	}
}