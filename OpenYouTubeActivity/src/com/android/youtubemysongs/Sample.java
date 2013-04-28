package com.android.youtubemysongs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

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
          String[] proj = { MediaStore.Audio.Media._ID,
		  MediaStore.Audio.Media.DATA,
		  MediaStore.Audio.Media.DISPLAY_NAME,
		  MediaStore.Video.Media.SIZE };
          musiccursor = managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,proj, null, null, null);
          count = musiccursor.getCount();
          musiclist = (ListView) findViewById(R.id.PhoneMusicList);
          musiclist.setAdapter(new MusicAdapter(getApplicationContext()));
          musiclist.setOnItemClickListener(musicgridlistener);
          mMediaPlayer = new MediaPlayer();
    }

    private OnItemClickListener musicgridlistener = new OnItemClickListener() {
          public void onItemClick(AdapterView parent, View v, int position,long id) {
        	  
        	  String videoId="1ybUPCdkYvI";
        	  /*YouTubeService service = new YouTubeService("Aanchal Jindal","AI39si5ScWdXzn9WgOB7t8DyeT704NJZotXGdIx1RCtczOwPQWIVuw2Dks3kygY18ucirOLYY3yV3pPmp-sJJsG2dXyyKnIW0A");
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
              Intent lVideoIntent = new Intent(null, Uri.parse("ytv://"+videoId), Sample.this, YouTubemysongs.class);
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



