package org.coderocks.audio;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.vaadin.addon.audio.shared.util.Log;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class FileSelector extends Panel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String TEST_FILE_PATH = "/home/leokog/Music";

	public static abstract class SelectionCallback {
		public abstract void onSelected(String itemName);
	}

	private Set<SelectionCallback> callbacks;
	private ComboBox fileList;
	private Button addButton;

	public FileSelector() {

		super("File selector");

		callbacks = new HashSet<SelectionCallback>();
		HorizontalLayout wrapper = new HorizontalLayout();
		HorizontalLayout layout = new HorizontalLayout();
		layout.setSpacing(true);
		layout.setMargin(true);

		layout.addComponent(fileList = new ComboBox("File", listFileNames(TEST_FILE_PATH)));

		layout.addComponent(addButton = new Button("Add stream", new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				String fileName = (String) fileList.getValue();
				if (fileName != null && !fileName.equals("")) {
					for (SelectionCallback cb : callbacks) {
						cb.onSelected(fileName);

					}
					Log.message(this, "add stream " + fileName);
				} else {
					Log.warning(this, "no file selected, cannot add stream");
				}
			}
		}));
		layout.setComponentAlignment(addButton, Alignment.BOTTOM_CENTER);

		wrapper.addComponent(layout);
		wrapper.setComponentAlignment(layout, Alignment.MIDDLE_CENTER);
		setContent(wrapper);
	}
	
	//
	// File I/O routines require "new" Java features.
	//

	public static final List<String> listFileNames(String dir) {
		List<String> fnames = new ArrayList<String>();

		File d = new File(dir);
		File[] files = d.listFiles();

		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			if (f.isFile()) {
				fnames.add(f.getName());
			} else if (f.isDirectory()) {
				fnames.addAll(listFileNames(f.getPath()));
			}
		}
		return fnames;
	}

	public FileSelector(SelectionCallback cb) {
		this();
		callbacks.add(cb);
	}

	public ComboBox getFileList() {
		return fileList;
	}

	public Button getAddButton() {
		return addButton;
	}
}