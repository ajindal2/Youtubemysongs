package com.android.youtubemysongs;

<<<<<<< HEAD

=======
>>>>>>> 44e59fb2602c45d1183b2b7ea585ca373125b0ce
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.Toast;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.android.youtubemysongs.R;

public class Sample extends Activity {

	ListView musiclist;
    Cursor musiccursor;
    int music_column_index;
    int count;
    
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
          String[] proj = { MediaStore.Audio.Media._ID,MediaStore.Audio.Media.DISPLAY_NAME,MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.TITLE,MediaStore.Audio.Media.ALBUM };
          musiccursor = managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,proj, null, null, null);
          count = musiccursor.getCount();
          musiclist = (ListView) findViewById(R.id.PhoneMusicList);
          musiclist.setAdapter(new MusicAdapter(getApplicationContext()));
          musiclist.setOnItemClickListener(musicgridlistener);
    }

    private OnItemClickListener musicgridlistener = new OnItemClickListener() {
          public void onItemClick(AdapterView parent, View v, int position,long id) {
        	  String id0=null;
        	  int i=0;
        	  try {
        		  System.gc();
        		  String lInfoStr = "fail";
        		  music_column_index = musiccursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
        		  int col1=musiccursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
        		  int col2=musiccursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
        		  musiccursor.moveToPosition(position);
                  String title = musiccursor.getString(music_column_index);
                  String artist = musiccursor.getString(col1);
                  String album = musiccursor.getString(col2);  
                  
                  if(!artist.contains("<unknown>"))title=(title+" "+artist).replace(" ", "%20");
                  else if(!album.contains("<unknown>")) title=(title+" "+album).replace(" ", "%20");
                  else title=(title).replace(" ", "%20");
                  		  	
        		  String url="http://gdata.youtube.com/feeds/api/videos?q="+title+"&max-results=5&v=2&format=5&alt=jsonc";

        		  URL jsonURL = new URL(url); 
        		  URLConnection jc = jsonURL.openConnection(); 
        		  InputStream is = jc.getInputStream(); 
        		  String jsonTxt = IOUtils.toString( is );
        		  JSONObject jj = new JSONObject(jsonTxt); 
        		  JSONObject jdata = jj.getJSONObject("data");
        		  int totalItems = Math.min(10,jdata.getInt("totalItems"));
        		  
        		  JSONArray aitems = null;
        		  
        		  if (totalItems > 0)
        			  aitems = jdata.getJSONArray("items");
        		  
        		  while(lInfoStr.contains("fail")&&i<totalItems){ 
	        		  JSONObject item0 = aitems.getJSONObject(i);
	        		  id0 = item0.getString("id");   
	        		  HttpClient lClient = new DefaultHttpClient();
	      			  HttpGet lGetMethod = new HttpGet(YouTubemysongs.YOUTUBE_VIDEO_INFORMATION_URL + id0);
	      			  HttpResponse lResp = null;
	      			  lResp = lClient.execute(lGetMethod);	
	      			  ByteArrayOutputStream lBOS = new ByteArrayOutputStream();	
	      			  lResp.getEntity().writeTo(lBOS);
	      			  lInfoStr = new String(lBOS.toString("UTF-8"));
	      			  i++;
        		  }
        		  if(i>=totalItems){
        		      Toast.makeText(getApplicationContext(), "Sorry! No video found :(", Toast.LENGTH_SHORT).show();
        		  } else{
    	              Intent lVideoIntent = new Intent(null, Uri.parse("ytv://"+id0), Sample.this, YouTubemysongs.class);
    	              startActivity(lVideoIntent);
            	  }
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
        	 id += " - " + musiccursor.getString(music_column_index);
        	 tv.setText(id);
        	 return tv;	 
        }
    }
}



