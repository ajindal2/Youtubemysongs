package com.aanchal.youtubemysongs;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.util.Log;
import com.aanchal.youtubemysongs.Song;
import com.aanchal.youtubemysongs.DeveloperKey;
import com.aanchal.youtubemysongs.JSONParser;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SongLogger {
	String youtubeId;
	Song userSong;
	int songDuration;
	long startTime;
	String androidId;
	String lang;
	int selectedIndex;
	static final int VERSION = 1;
	JSONParser jsonParser = new JSONParser();
	
	
	public SongLogger(String android_id) {
		this.startTime = -1;
		this.androidId = android_id;
		//Log.v("Logging", android_id + android_id.length());
		this.lang = Locale.getDefault().getLanguage();
	}
	
	void videoStarted(String videoId, Song song, int duration, int index) {
		youtubeId = videoId;
		userSong = song;
		songDuration = duration;
		startTime = System.currentTimeMillis();
		selectedIndex = index;
	}
	
	void resetAndSend(Context mContext) {
		if (startTime != -1) {
			int elapsedTime = (int) ((System.currentTimeMillis() - startTime)/1000);
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("uid", androidId));
			params.add(new BasicNameValuePair("title", userSong.title));
			params.add(new BasicNameValuePair("album", userSong.album));
			params.add(new BasicNameValuePair("artist", userSong.artist));
			params.add(new BasicNameValuePair("videoid", youtubeId));
			params.add(new BasicNameValuePair("duration", Integer.toString(songDuration)));
			params.add(new BasicNameValuePair("watched", Integer.toString(elapsedTime)));
			params.add(new BasicNameValuePair("version", Integer.toString(selectedIndex))); // USE VERSION field
			params.add(new BasicNameValuePair("lang", lang));
			
			jsonParser.makeHttpRequest(DeveloperKey.DB_URL,"POST", params);
			
			}
		startTime = -1;
	}

}
