package org.coderocks;

import com.kbdunn.vaadin.addons.mediaelement.MediaElementPlayer;
import com.vaadin.addon.contextmenu.ContextMenu;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileResource;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.Grid.GridContextClickEvent;

import org.vaadin.alump.labelbutton.LabelButton;
import org.vaadin.sliderpanel.SliderPanel;
import org.vaadin.sliderpanel.SliderPanelBuilder;
import org.vaadin.sliderpanel.SliderPanelStyles;
import org.vaadin.sliderpanel.client.SliderMode;
import org.vaadin.sliderpanel.client.SliderTabPosition;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;

@SpringView(name = Mp3GridView.NAME)
@Theme("valo")
@Push
public class Mp3GridView extends VerticalLayout implements View {

	/*
	 * @auhthor Leopold Kogler Spring 2018 inspired by Daniela, for Daniela...
	 */

	public static final String NAME = "Mp3GridView";
	private static final long serialVersionUID = 1L;
	private boolean responsive = true;
	private Mp3Song song;
	private Label currUser;

	List<Mp3Song> mp3songs = new ArrayList<Mp3Song>();

	BeanItemContainer<Mp3Song> container = new BeanItemContainer<Mp3Song>(Mp3Song.class, mp3songs);

	private Grid grid = new Grid();

	SliderPanel rightSlider;
	SliderPanel topSlider;
	SliderPanel leftSlider;
	VerticalLayout content;
	MediaElementPlayer audioPlayer;
	private String user;
	private org.coderocks.PlayList playList;
	private VerticalLayout leftContent;

	@PostConstruct
	void init() {

		addStyleName("main");
		setSizeFull();
		setMargin(false);
		setSpacing(false);

		// center layout with left and right slider
		HorizontalLayout contentLayout = new HorizontalLayout();
		contentLayout.setSpacing(false);
		contentLayout.setSizeFull();

		// dummy middle content
		VerticalLayout contentLabel = new VerticalLayout();
		contentLabel.setMargin(true);
		contentLabel.setSizeFull();

		updateGrid();

		grid.setColumns("album", "artist", "title");// "duration");
		grid.setHeight(100, Unit.PERCENTAGE);
		grid.setWidth(100, Unit.PERCENTAGE);
		grid.setResponsive(responsive);

		grid.getColumn("title").setExpandRatio(1);
		grid.getColumn("artist").setWidth(350);
		grid.getColumn("album").setWidth(350);
		// grid.getColumn("duration").setWidth(200);
		grid.addSelectionListener(e -> updateForm());

		topSlider = new SliderPanelBuilder(createSearchBar(), "Search Song").expanded(false).mode(SliderMode.TOP)
				.caption("Search for Object")
				// .style(SliderPanelStyles.COLOR_BLUE)
				// .style(SliderPanelStyles.COLOR_WHITE)
				.tabPosition(SliderTabPosition.MIDDLE).fixedContentSize(Page.getCurrent().getBrowserWindowHeight() / 4)
				.autoCollapseSlider(true).build(); // System.out.println(rightSlider.getWidth()
													// +"
													// "+
		addComponent(topSlider);
		rightSlider = new SliderPanelBuilder(content, "Play Song").tabPosition(SliderTabPosition.END)
				.mode(SliderMode.RIGHT)
				// .tabSize(Page.getCurrent().getBrowserWindowWidth()/2)
				// .style(SliderPanelStyles.COLOR_BLUE)
				.flowInContent(true).fixedContentSize(Page.getCurrent().getBrowserWindowWidth() / 2)
				.tabPosition(SliderTabPosition.MIDDLE).build();

		playList = new PlayList(0, 0, user);
		leftContent = createLeftPanelContent(playList);
		leftSlider = new SliderPanelBuilder(leftContent, "Playlist").expanded(false).mode(SliderMode.LEFT)
				.caption("Playlist")
				// .style(SliderPanelStyles.COLOR_BLUE)
				.style(SliderPanelStyles.COLOR_WHITE).tabPosition(SliderTabPosition.MIDDLE)
				.fixedContentSize(Page.getCurrent().getBrowserWindowWidth() / 4).autoCollapseSlider(true).build();

		contentLayout.addComponent(leftSlider);

		HorizontalSplitPanel hSplit = new HorizontalSplitPanel();
		hSplit.setFirstComponent(grid);
		hSplit.setMaxSplitPosition(90, Unit.PERCENTAGE);
		hSplit.setMinSplitPosition(90, Unit.PERCENTAGE);

		Mp3Upload uploadField = new Mp3Upload();
		hSplit.setSecondComponent(uploadField);

		contentLabel.addComponent(hSplit);
		contentLayout.addComponent(contentLabel);
		contentLayout.setComponentAlignment(contentLabel, Alignment.MIDDLE_CENTER);
		contentLayout.setExpandRatio(contentLabel, 1);

		contentLayout.addComponent(rightSlider);

		// fit full screen
		addComponent(contentLayout);
		setExpandRatio(contentLayout, 1);

		try {
			song = mp3songs.get(0);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		content = createSidePanelContent();

	}

	private HorizontalSplitPanel createSearchBar() {

		HorizontalSplitPanel vSplit = new HorizontalSplitPanel();
		HorizontalLayout pLayout = new HorizontalLayout();

		currUser = new Label();
		currUser.setSizeFull();
		currUser.setWidth(65, Unit.PERCENTAGE);

		List<String> list = new ArrayList<String>();
		list.add("filename");
		list.add("title");
		list.add("duration");
		list.add("artist");

		ComboBox comboBox = new ComboBox();
		comboBox.addItems(list);

		TextField tfSearch = new TextField();

		// Button btnSearch = new Button("search", e ->
		// new PreOrderBST());

		pLayout.addComponent(comboBox);
		pLayout.addComponent(tfSearch);
		pLayout.addComponent(new Button("search", new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(final Button.ClickEvent event) {

				String category = comboBox.getConvertedValue().toString();

				System.out.println("cat: " + category);

				String value = tfSearch.getValue();

				System.out.println("val: " + value);

				updateGridToSearchResult(category, value);

			}
		}));

		vSplit.setFirstComponent(currUser);
		vSplit.setSecondComponent(pLayout);

		return vSplit;
	}

	private VerticalLayout createSidePanelContent() {

		if (song == null) {
			song = new Mp3Song();
			song.setFilename("dummy.mp3");
		}

		VerticalLayout component;

		Label lblPrtName1 = new Label(song.getFilename());
		component = new VerticalLayout(lblPrtName1);

		SQLConnection conn = new SQLConnection();
		conn.connect();

		List<Mp3Song> songList = conn.getSongFromTitle(song.getFilename());
		System.out.println("ID " + song.getId());

		// Audio player with PlaybackEndedListener
		audioPlayer = new MediaElementPlayer();
		audioPlayer.setSource(new FileResource(getSong(song.getId())));
		audioPlayer.play();
		audioPlayer.addPlaybackEndedListener(player -> {
			player.setSource(new FileResource(getSong(song.getId() + 1)));
			player.play();
		});

		HorizontalSplitPanel vSplit = new HorizontalSplitPanel();
		VerticalLayout vertical = new VerticalLayout();

		FormLayout lpLayout = new FormLayout();
		FormLayout rpLayout = new FormLayout();

		Label lblAlbum = new Label("Album");
		lpLayout.addComponent(lblAlbum);
		Label lblSurfaceCaption = new Label();
		lblSurfaceCaption.setCaption(song.getAlbum());
		rpLayout.addComponent(lblSurfaceCaption);

		Label lblArtist = new Label("Artist");
		lpLayout.addComponent(lblArtist);
		Label lblMedDimCaption = new Label();
		lblMedDimCaption.setCaption(song.getArtist());
		rpLayout.addComponent(lblMedDimCaption);

		Label lblTitle = new Label("Title");
		lpLayout.addComponent(lblTitle);
		Label lblMaxDimCaption = new Label();
		lblMaxDimCaption.setCaption(song.getTitle());
		rpLayout.addComponent(lblMaxDimCaption);

		int duration = song.getDuration();

		int seconds = duration % 60;
		int minute = (duration - seconds) / 60;

		String mp3Second;

		if (seconds < 10) {
			mp3Second = "0" + seconds;
		} else {
			mp3Second = "" + seconds;
		}

		String mp3Minutes;

		if (minute < 10) {
			mp3Minutes = "0" + minute;
		} else {
			mp3Minutes = "" + minute;
		}

		Label lblDuration = new Label("Duration");
		lpLayout.addComponent(lblDuration);
		Label lblMinDimCaption = new Label();
		lblMinDimCaption.setCaption(mp3Minutes + ":" + mp3Second);
		rpLayout.addComponent(lblMinDimCaption);

		Label lblFileName = new Label("Filename");
		lpLayout.addComponent(lblFileName);
		Label lblPrtNameCaption = new Label();
		lblPrtNameCaption.setCaption(song.getFilename());
		rpLayout.addComponent(lblPrtNameCaption);

		vSplit.setFirstComponent(lpLayout);
		vSplit.setSecondComponent(rpLayout);
		vSplit.setMaxSplitPosition(50, Unit.PERCENTAGE);

		vertical.addComponent(vSplit);

		VerticalLayout rVertical = new VerticalLayout();
		VerticalLayout imgPanel = createImagePanel();

		rVertical.addComponent(imgPanel);

		HorizontalSplitPanel hDivider50 = new HorizontalSplitPanel();
		hDivider50.setFirstComponent(vertical);
		hDivider50.setSecondComponent(rVertical);
		hDivider50.setMaxSplitPosition(50, Unit.PERCENTAGE);

		component.addComponent(hDivider50);

		HorizontalLayout hLayout = new HorizontalLayout();
		hLayout.setHeight(Page.getCurrent().getBrowserWindowHeight() / 3.0 + "px");

		VerticalLayout playerLabel = new VerticalLayout();
		playerLabel.addComponent(audioPlayer);
		playerLabel.setComponentAlignment(audioPlayer, Alignment.BOTTOM_CENTER);

		// hLayout.addComponent(audioPlayer);
		// hLayout.setComponentAlignment(audioPlayer, Alignment.BOTTOM_CENTER);

		Label lblDaniela = new Label("Leo for Daniela, Vienna, Summer 2018");
		// hLayout.addComponent(lblDaniela);
		// hLayout.setComponentAlignment(lblDaniela, Alignment.BOTTOM_RIGHT);

		playerLabel.addComponent(lblDaniela);
		playerLabel.setComponentAlignment(lblDaniela, Alignment.BOTTOM_RIGHT);

		hLayout.addComponent(playerLabel);
		hLayout.setComponentAlignment(playerLabel, Alignment.BOTTOM_CENTER);

		VerticalLayout vLayout = new VerticalLayout();
		vLayout.addComponent(hLayout);
		vLayout.setComponentAlignment(hLayout, Alignment.MIDDLE_CENTER);
		component.addComponent(vLayout);
		component.setMargin(true);
		component.setSpacing(true);
		return component;

	}

	private void updateGrid() {

		ContextMenu grdContextMenu = new ContextMenu(grid, true);

		grdContextMenu.addContextMenuOpenListener(e -> {
			GridContextClickEvent gridE = (GridContextClickEvent) e.getContextClickEvent();


			Object propertyId = gridE.getPropertyId();
			
			grdContextMenu.removeItems();
			grdContextMenu.addItem("Called from column " + propertyId + " on row " + gridE.getRowIndex(),
					f -> Notification.show("did something"));
			
			grdContextMenu.addItem("Add " + getSongNamefromRow(gridE.getRowIndex()) + " to Playlist" , g -> addSongToPlayList(getSongFromRow(gridE.getRowIndex())));
			
		

		});

		SQLConnection conn = new SQLConnection();
		conn.connect();

		mp3songs = conn.getSongFromTitle("*");

		for (int j = 0; j < mp3songs.size(); j++) {
			System.out.println(mp3songs.get(j).getFilename());
		}

		grid.setContainerDataSource(new BeanItemContainer<Mp3Song>(Mp3Song.class, mp3songs));

		conn.close();
	}

	private Mp3Song getSongFromRow(int rowIndex) {

		Mp3Song mp3song = mp3songs.get(rowIndex);
		
		return mp3song;
	}

	private void addSongToPlayList(Mp3Song mp3song) {
	
		playList.getList().add(mp3song);
		leftSlider.removeFromParent(content);
		leftContent = createLeftPanelContent(playList);
		leftSlider.setContent(leftContent);
		
		// highlight playing song by color
		
		/*Label label1 = new Label(
		String.format("<font size = \"5\" color=\"white\"> MyText" )
		, ContentMode.HTML);
		 */
		
		rightSlider.removeFromParent(content);
		content = createSidePanelContent();
		rightSlider.setContent(content);
		
	}

	private String getSongNamefromRow(int rowIndex) {
		String songName = mp3songs.get(rowIndex).getTitle();
		return songName;
	}

	private void updateGridToSearchResult(String field, String value) {

		SQLConnection conn = new SQLConnection();
		conn.connect();

		mp3songs = conn.getSongs(field, value);

		grid.setContainerDataSource(new BeanItemContainer<Mp3Song>(Mp3Song.class, mp3songs));

	}

	@SuppressWarnings("static-access")
	private void updateForm() {
		if (grid.getSelectedRows().isEmpty()) {

		} else {
			song = (Mp3Song) grid.getSelectedRow();
			BeanFieldGroup.bindFieldsUnbuffered(song, this);

			rightSlider.removeFromParent(content);

			content = createSidePanelContent();

			rightSlider.setContent(content);

			/*
			 * audioPlayer.setSource(new FileResource(getSong(song.getId())));
			 * audioPlayer.addPlaybackEndedListener(player -> { player.setSource(new
			 * FileResource(getSong(song.getId() + 1))); player.play();
			 * 
			 * });
			 */

		}
	}


	private VerticalLayout createLeftPanelContent(PlayList playList) {
		VerticalLayout vl = new VerticalLayout();
		
		Label label = new Label(VaadinSession.getCurrent().getAttribute("user").toString()+ "'s playlist :");
		vl.addComponent(label);

		// if (playList.getList().size() != 0) {
		for (Mp3Song entry : playList.getList()) {
			LabelButton labelBtn = new LabelButton(entry.getTitle(),  event -> Notification.show("Clicked!"));
			vl.addComponent(labelBtn);
		}
		// }
		
		return vl;

	}

	public VerticalLayout createImagePanel() {

		VerticalLayout component;
		component = new VerticalLayout();
		Image image = new Image(song.getTitle(), createStreamResource());
		image.setWidth(250, Unit.PIXELS);
		image.setHeight(250, Unit.PIXELS);

		component.addComponent(image);

		component.setMargin(true);
		component.setSpacing(true);
		return component;
	}

	public File getSong(long id) {

		SQLConnection conn = new SQLConnection();
		conn.connect();
		File targetFile = null;
		OutputStream outStream;
		String folder = System.getProperty("user.home") + File.separator + "myfolder";

		File directory = new File(folder);
		if (!directory.exists()) {
			directory.mkdir();
		}

		try {

			song.setSong(conn.fetchSongById(id));
			song.setImage(conn.fetchImage(id));
			conn.close();
			targetFile = new File(folder + File.separator + song.getFilename());

			outStream = new FileOutputStream(targetFile);

			if (song.getSong() == null) {
				System.out.println("empty result set");
			} else {
				outStream.write(song.getSong());
			}
		} catch (IOException e) {

			e.printStackTrace();

			return new File(folder);
		}

		return targetFile;
	}

	private StreamResource createStreamResource() {
		return new StreamResource(new StreamSource() {
			@Override
			public InputStream getStream() {
				return new ByteArrayInputStream(song.getImage());
			}
		}, "Cover");
	}

	@Override
	public void enter(ViewChangeEvent event) {

		currUser.setCaption("Current user : " + VaadinSession.getCurrent().getAttribute("user").toString());
		System.out.println(currUser.getCaption());
	}

}
