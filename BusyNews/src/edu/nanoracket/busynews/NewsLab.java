package edu.nanoracket.busynews;

import java.net.URL;
import java.util.ArrayList;
import android.content.Context;
import android.util.Log;

// Create a singleton class to store news list
public class NewsLab {
	public static final String TAG = "NewsLab";
	public static final String FILENAME = "newses.json";

	private static NewsLab sNewsLab;
	private Context mAppContext;
	private ArrayList<News> mNewses;
	private NewsJSONSerializer mJsonSerializer;
	
	private NewsLab(Context context){
		mAppContext = context;
		mJsonSerializer = new NewsJSONSerializer(mAppContext, FILENAME);
		try {
	         mNewses = mJsonSerializer.loadNewses();
	     } catch (Exception e) {
	         mNewses = new ArrayList<News>();
	         Log.e(TAG, "Error loading crimes: ", e);
	     }
	}
	
	public static NewsLab get(Context c){
		if(sNewsLab == null){
			sNewsLab = new NewsLab(c.getApplicationContext());
		}
		return sNewsLab;
	}
	
	public ArrayList<News> getNewses(){
		return mNewses;
	}
	
	public News getNews(URL url){
		for(News news : mNewses){
			if(news.getLink().equals(url))
				return news;
		}
		return null;
	}
	
	public void addNews(News news){
		mNewses.add(news);
	}
	public boolean saveNewses(){
		try{
			mJsonSerializer.saveNews(mNewses);
			Log.d(TAG,"Newses saved to file");
			return  true;
		}catch(Exception e){
			Log.d(TAG,"Newses save error");
			return false;
		}
	}
}
