package com.aanchal.youtubemysongs;

// Store Song Information
public class Song {
	public Song(String title, String album, String artist) {
		this.title = title;
		this.album = album;
		this.artist = artist;
	}
	public String getAlbumQueryString()  { return (title + " " + album).replace(" ","%20"); }
	public String getArtistQueryString() { return (title + " " + artist).replace(" ","%20"); }
	public String getArtistAlbumQueryString() { return (title + " " + artist + " " + album).replace(" ","%20"); }
	public String getTitleQueryString()  { return title.replace(" ","%20"); }
	public String title;
	public String album;
	public String artist;
}
