package com.aanchal.youtubemysongs;

import com.aanchal.youtubemysongs.Song;
import com.aanchal.youtubemysongs.DeveloperKey;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.aanchal.youtubemysongs.R;
import com.google.android.youtube.player.YouTubeStandalonePlayer;

public class MainActivity extends Activity {

	static final int MAX_QUERY_SONGS = 5;
	private static final String YOUTUBE_VIDEO_INFORMATION_URL = "http://www.youtube.com/get_video_info?&video_id=";

	ListView musiclist;
    Cursor musiccursor;
    Song querySong;
    int music_column_index;
    int count;
    private ConnectivityManager cm;
    Activity myself;
    
    class Process extends AsyncTask<Object, Void, String> {
		
		 private ProgressDialog progressDialog; 
		 
		 @Override
	        protected void onPreExecute()
	        {
	            super.onPreExecute();   
	            progressDialog = ProgressDialog.show(MainActivity.this, null, "Automagic search in progress...", true, false); 
	        }

	        @Override
	        protected String doInBackground(Object... param) {
	        	return getVideoIdForSong(querySong);
        	 }

	        @Override
	        protected void onPostExecute(String result)
	        {
	            super.onPostExecute(result);	            
	            progressDialog.dismiss();
	        	if (result == null) 
		        	   Toast.makeText(myself, "Sorry! No video found :(", Toast.LENGTH_SHORT).show();
	        	else {
             		Intent intent = YouTubeStandalonePlayer.createVideoIntent(
             	         myself , DeveloperKey.DEVELOPER_KEY, result,0, true, false);
             		startActivity(intent);
		        }    
	        }
	}

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.mainactivity);
          cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
          myself = this;
          init_phone_music_grid();
          
    }

    @SuppressWarnings("deprecation")
	private void init_phone_music_grid() {
          System.gc();
          String[] proj = { MediaStore.Audio.Media._ID,MediaStore.Audio.Media.DISPLAY_NAME,MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.TITLE,MediaStore.Audio.Media.ALBUM };
          musiccursor = managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,proj, null, null, null);
          count = musiccursor.getCount();
          musiclist = (ListView) findViewById(R.id.PhoneMusicList);
          musiclist.setAdapter(new MusicAdapter(getApplicationContext()));
          musiclist.setOnItemClickListener(musicgridlistener);
    }
    
	// returns query results in JSON form
	private static JSONArray getResults(String query) {
		if (query.contains("<unknown>") || query.contains("www") || query.contains("[") || query.contains("("))
			return null;
		try {
		// TODO: Get short and medium duration only, exclude long videos
		String url="http://gdata.youtube.com/feeds/api/videos?q="+query+"&max-results="+MAX_QUERY_SONGS+"&v=2&format=5&alt=jsonc";
		URL jsonURL = new URL(url);
		URLConnection jc = jsonURL.openConnection();
		InputStream is = jc.getInputStream();
		String jsonTxt = IOUtils.toString( is );
		JSONObject jj = new JSONObject(jsonTxt);
		JSONObject jdata = jj.getJSONObject("data");
		int totalItems = Math.min(MAX_QUERY_SONGS,jdata.getInt("totalItems"));
		JSONArray aitems = null;
		if (totalItems > 0)
			aitems = jdata.getJSONArray("items");
		return aitems;
		} catch (Exception e) {
			return null;
		}
	}
	
	// given a song, retrieve the youtube id, returns null if no playable video is found
	private static String getVideoIdForSong(Song song) {
		JSONArray[] results = new JSONArray[3];
		results[0] = getResults(song.getAlbumQueryString());
		results[1] = getResults(song.getArtistQueryString());
		results[2] = getResults(song.getArtistAlbumQueryString());
		int[] indexes = new int[3];
		indexes[0] = indexes[1] = indexes[2] = 0;
		while(true) {
			Boolean exhausted = true;
			for(int i = 0; i < 3; ++i) 
				if (results[i] != null && indexes[i] < results[i].length()) {
					try {
					  JSONObject item0 = results[i].getJSONObject(indexes[i]);
					  indexes[i]++;
					  String ret = item0.getString("id");
					  HttpClient lClient = new DefaultHttpClient();
					  HttpGet lGetMethod = new HttpGet(YOUTUBE_VIDEO_INFORMATION_URL + ret);
					  HttpResponse lResp = null;
					  lResp = lClient.execute(lGetMethod);
					  ByteArrayOutputStream lBOS = new ByteArrayOutputStream();
					  lResp.getEntity().writeTo(lBOS);
					  String lInfoStr = new String(lBOS.toString("UTF-8"));
					  if (!lInfoStr.contains("fail"))
						  return ret;
					  exhausted = false;
					} catch (Exception e) {
						// do nothing, continue
					}
					  
				}
			if (exhausted)
				break;
		}
		return null;
	}

    private OnItemClickListener musicgridlistener = new OnItemClickListener() {
          public void onItemClick(AdapterView parent, View v, int position,long id) {
        	  try {
                    //Upon clicking on a song name this will retrieve MAX_QUERY_SONGS number of songs from youtube and play the top result.
                    System.gc();
                    String lInfoStr = "fail";
                    music_column_index = musiccursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
                    int col1=musiccursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
                    int col2=musiccursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
                    musiccursor.moveToPosition(position);
                    String title = musiccursor.getString(music_column_index);
                    String artist = musiccursor.getString(col1);
                    String album = musiccursor.getString(col2);
                    querySong = new Song(title, album, artist);
                  	if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable() && cm.getActiveNetworkInfo().isConnected())
                  		new Process().execute(null,null,null); 
                  	else 
                  		Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_LONG).show();
             
                  } catch (Exception e) {e.printStackTrace();}
          }
    };

    public class MusicAdapter extends BaseAdapter {
      private Context mContext;

      public MusicAdapter(Context c) {
        mContext = c;
      }

      public int getCount() {
        return count;
      }

      public Object getItem(int position) {
        return position;
      }

      public long getItemId(int position) {
        return position;
      }

      public View getView(int position, View convertView, ViewGroup parent) {
        System.gc();
        String id = null;
        TextView tv;
        if (convertView == null) {
          tv = new TextView(mContext.getApplicationContext());
        } else{
          tv = (TextView) convertView;
        }
        musiccursor.moveToPosition(position);
        music_column_index = musiccursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
        id = musiccursor.getString(music_column_index);
        music_column_index = musiccursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
        String artist=musiccursor.getString(music_column_index);
        tv.setTextSize(12);

        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(id + "\n"+artist);
        stringBuilder.setSpan(new RelativeSizeSpan(1.5f), 0, id.length(),Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        stringBuilder.setSpan(new ForegroundColorSpan(Color.rgb(135, 206, 250)), id.length() + 1,id.length() + artist.length()+1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        tv.setText(stringBuilder);
        return tv;
      }
    }
}



