package org.coderocks;


public class Mp3Song {
	
	private String filename;
	private int duration;
	private byte [] song;
	private String title;
	private long id;
	private String artist;
	private String album;
	private short year;
	private byte[] image;
	
	public Mp3Song () {
		
	}
	
	public Mp3Song (String filename, int duration, byte[] song, String title, long id) {
		
		this.filename=filename;
		this.duration=duration;
		this.song=song;
		this.title=title;
		this.id=id;
		
	}
	
	String getFilename() {
		return this.filename;
	}
	
	byte[] getSong () {
		return song;
	}
	
	int getDuration () {
		return duration;
	}
	
	void setSong ( byte[] song ) {
		this.song = song;
	}
	
	void setFilename ( String filename ) { 
		this.filename = filename;
	}
	
	void setDuration ( int duration2) {
		this.duration = duration2;
	}

	public void setTitle( String title ) {
			this.title = title;
	}
	
	public String getTitle() {
		return this.title;
	}

	public void setId(long id) {
		this.id=id;
	}
	
	long getId () {
		return this.id;
	}

	public String getArtist() {

		return artist;
	}
	
	public void setArtist ( String artist ) {
		this.artist=artist;
	}

	public String getAlbum() {
		return album;
	}
	
	public void setAlbum ( String album ) {
		this.album=album;
	}

	public short getYear() {
		return this.year;
	}
	
	void setYear( short year ) {
		this.year=year;
	}

	public void setImage(byte[] image) {
		this.image=image;
		
	}
	
	public byte[] getImage () {
		return this.image;
	}


}
