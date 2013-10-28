package edu.nanoracket.busynews;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.net.Uri;
import android.util.Log;

public class NewsListFetchr { 

	public static final String PREF_SEARCH_QUERY = "searchQuery";
	public static final String PREF_LAST_RESULT_ID = "lastREsultId";
	public static final String HOME = "home";
	private static final String TAG = "NewsListFetcher";
	private static final String ENDPOINT = "http://api.usatoday.com/open/articles";
	private static final String PATH = "topnews";
	private static final String MOBILE = "mobile";
	private static final String ENCODING ="json";
	private static final String API_KEY ="9rvr5mqap6mr8wyvfgwxgu5t";
	private static final String STORIES = "stories";
	private static final String DESCRIPTION = "description";
	private static final String PUBDATE = "pubDate";
	private static final String LINK = "link";
	private static final String TITLE = "title";
	
	//Download JSON String from the API URL
	byte[] getURLBytes(String urlSpec) throws IOException {
		
		URL url = new URL(urlSpec);
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		
		try{
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			InputStream in = connection.getInputStream();
			
			if(connection.getResponseCode() != HttpURLConnection.HTTP_OK){
				return null;
			}
			
			int bytesRead = 0;
			byte[] buffer = new byte[1024];
			while ((bytesRead=in.read(buffer))>0){
				out.write(buffer, 0,bytesRead);
			}
			
			out.close();
			return out.toByteArray();
		} finally {
			connection.disconnect();
		}
	}
	
	public String getNewsString (String urlSpec) throws IOException {
		return new String(getURLBytes(urlSpec));
	}
	
	
	public ArrayList<News> downloadNewses(String url){
		ArrayList<News> newses = new ArrayList<News>();
		try {
			String newsString = getNewsString(url);
			Log.i(TAG,"Received News: " +newsString);
			JSONObject mJsonObject = new JSONObject(newsString);
			Log.i(TAG,"Received JSON: " + mJsonObject);
			parseBooks(newses, mJsonObject);
			Log.i(TAG,"ArrayList : " + newses);			
		}catch(IOException ioe){
			Log.e(TAG,"Failed to fetch newses",ioe);
		}catch (JSONException e) {
			Log.e(TAG,"Error converting news data",e);
		}
		return newses;
	}
	
	//News Segment API
	public ArrayList<News> fetchNewses(String segment){
		String  url = Uri.parse(ENDPOINT).buildUpon()
				 .appendPath(MOBILE)
				 .appendPath(PATH)
			     .appendPath(segment)
			     .appendQueryParameter("encoding", ENCODING)
			     .appendQueryParameter("api_key", API_KEY)
			     .build().toString();
	    Log.i(TAG,"Reveived URL: " + url);
	    return downloadNewses(url);	
	}
	
	//News Serach API
	public ArrayList<News> searchNewses(String query){
		String  url = Uri.parse(ENDPOINT).buildUpon()
			     .appendQueryParameter("encoding", ENCODING)
			     .appendQueryParameter("keyword", query)
			     .appendQueryParameter("api_key", API_KEY)
			     .build().toString();
	    Log.i(TAG,"Reveived URL: " + url);
	    return downloadNewses(url);	
	}
	
	
	//Parese the json object to news class
	void parseBooks(ArrayList<News> newses,JSONObject jsonObject){
		JSONArray newsesArray;
		try {
			newsesArray = jsonObject.getJSONArray(STORIES);
			for(int i=0; i < newsesArray.length();i++){
				JSONObject t = newsesArray.getJSONObject(i);
				String description = t.getString(DESCRIPTION);
				String pubDate = t.getString(PUBDATE);
				String link = t.getString(LINK);
				String title = t.getString(TITLE);

				News news = new News();
				news.setDescription(description);
				news.setPubDate(pubDate);
				news.setLink(link);
				news.setTitle(title);
				newses.add(news);
				}
			}catch(JSONException e) {
				Log.e(TAG,"Error parsing data",e);
				}
			}
}
