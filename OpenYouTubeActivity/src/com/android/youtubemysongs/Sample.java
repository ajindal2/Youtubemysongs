package com.android.youtubemysongs;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gdata.client.youtube.YouTubeQuery;
import com.google.gdata.data.youtube.*;
import com.google.gdata.util.ServiceException;
import com.google.gdata.client.youtube.YouTubeService;
import com.android.youtubemysongs.R;

public class Sample extends Activity {

	ListView musiclist;
    Cursor musiccursor;
    int music_column_index;
    int count;
    MediaPlayer mMediaPlayer;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.sample);
          init_phone_music_grid();
    }
    
    @SuppressWarnings("deprecation")
	private void init_phone_music_grid() {
          System.gc();
          String[] proj = { MediaStore.Audio.Media._ID,MediaStore.Audio.Media.DATA,MediaStore.Audio.Media.DISPLAY_NAME,MediaStore.Video.Media.SIZE };
          musiccursor = managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,proj, null, null, null);
          count = musiccursor.getCount();
          musiclist = (ListView) findViewById(R.id.PhoneMusicList);
          musiclist.setAdapter(new MusicAdapter(getApplicationContext()));
          musiclist.setOnItemClickListener(musicgridlistener);
          mMediaPlayer = new MediaPlayer();
    }

    private OnItemClickListener musicgridlistener = new OnItemClickListener() {
          public void onItemClick(AdapterView parent, View v, int position,long id) {
        	  String id0=null;
        	  try {
        		  System.gc();
        		  //String artist = musiccursor.getString(musiccursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
        		 // String name = musiccursor.getString(musiccursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
        		  //String albumName = musiccursor.getString(musiccursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
        		  
        		  String songname = "Hai Junoon";
        		  songname = songname.replace(" ", "%20");
        		  Log.v("rahul", songname);
        		  URL jsonURL = new URL("http://gdata.youtube.com/feeds/api/videos?q="+songname+"&max-results=1&format=5&v=2&alt=jsonc"); 
        		  URLConnection jc = jsonURL.openConnection(); 
        		  InputStream is = jc.getInputStream(); 
        		  String jsonTxt = IOUtils.toString( is );
        		  JSONObject jj = new JSONObject(jsonTxt); 
        		  JSONObject jdata = jj.getJSONObject("data");
        		  JSONArray aitems = jdata.getJSONArray("items");
        		  JSONObject item0 = aitems.getJSONObject(0);
        		  id0 = item0.getString("id"); 
        		  
        		  //Log.v("aanchal",artist);
        		  //Log.v("aanchal",name);
        		  //Log.v("aanchal",name);
        	  } catch (Exception e) {
        		  e.printStackTrace();
        	  }
        	 
        	  
        	 // String videoId="1ybUPCdkYvI";
        	  /*YouTubeService service = new YouTubeService("YoutubeMySongs-1.0");
        	  YouTubeQuery query=null;
        		try {
        			query = new YouTubeQuery(new URL("http://gdata.youtube.com/feeds/api/videos"));
        		} catch (MalformedURLException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
            	// order results by the number of views (most viewed first)
            	query.setOrderBy(YouTubeQuery.OrderBy.VIEW_COUNT);

            	// search for puppies and include restricted content in the search results
            	query.setFullTextQuery("puppy");
            	query.setSafeSearch(YouTubeQuery.SafeSearch.NONE);

            	VideoFeed videoFeed=null;
        		try {
        			videoFeed = service.query(query, VideoFeed.class);
        		} catch (IOException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		} catch (ServiceException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
            	List<VideoEntry> allVideos = videoFeed.getEntries() ;
            	VideoEntry videoEntry = allVideos.iterator().next();
            	 
            	YouTubeMediaGroup mediaGroup = videoEntry.getMediaGroup();
            	 videoId=mediaGroup.getVideoId();*/
              Intent lVideoIntent = new Intent(null, Uri.parse("ytv://"+id0), Sample.this, YouTubemysongs.class);
              startActivity(lVideoIntent);
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
                TextView tv = new TextView(mContext.getApplicationContext());
                String id = null;
                if (convertView == null) {
                      music_column_index = musiccursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
                      musiccursor.moveToPosition(position);
                      id = musiccursor.getString(music_column_index);
                      music_column_index = musiccursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE);
                      musiccursor.moveToPosition(position);
                      id += " Size(KB):" + musiccursor.getString(music_column_index);
                      tv.setText(id);
                } else
                      tv = (TextView) convertView;
                return tv;
          }
    }
}



