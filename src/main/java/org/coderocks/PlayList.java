package org.coderocks;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class PlayList {

	private int id;
	private int userid;
	private String username;
	private ArrayList<Mp3Song> pList;
	private Mp3Song song;

	public PlayList(int id, int userid, String username) {

		this.id = id;
		this.userid = userid;
		this.username = username;
		this.pList = new ArrayList<Mp3Song>();

	}

	void addSong(Mp3Song song) {
		this.pList.add(song);
	}

	void removeSong(Mp3Song song) {
		this.pList.remove(song);
	}

	Mp3Song getSong() {
		return this.song;
	}

	int getListSize() {
		return this.pList.size();
	}

	public ArrayList<Mp3Song> getList() {
		return this.pList;
	}

	public byte[] streamPlayList() {
		byte[] ba = null;

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		try {
			out = new ObjectOutputStream(bos);
			out.writeObject(this);
			out.flush();
			ba = bos.toByteArray();
			// THANK YOU ALL @ STACKOVERFLOW !!!
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bos.close();
			} catch (IOException ex) {
			}
		}

		return ba;
	}

}
/*ByteArrayInputStream bis = new ByteArrayInputStream(yourBytes);
ObjectInput in = null;
try {
  in = new ObjectInputStream(bis);
  Object o = in.readObject(); 
  ...
} finally {
  try {
    if (in != null) {
      in.close();
    }
  } catch (IOException ex) {
    // ignore close exception
  }
}
*/