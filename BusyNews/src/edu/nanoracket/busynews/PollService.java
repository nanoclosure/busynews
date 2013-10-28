package edu.nanoracket.busynews;

import java.util.ArrayList;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

//Service to download news in the background
public class PollService extends IntentService {
	
	public static final String HOME = "home";
	private static final String TAG = "PollService";
	public static final String PREF_IS_ALARM_ON = "isAlarmOn";
	private static final int MINUTE = 60000;
	public static final String ACTION_SHOW_NOTIFICATION =
			"edu.nanoracket.buynews.SHOW_NOTIFICATION";
	public static final String PERM_PRIVATE = "edu.nanoracket.busynews.PRIVATE";
	
	public PollService(){
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		ConnectivityManager cm=(ConnectivityManager)
				getSystemService(Context.CONNECTIVITY_SERVICE);
		@SuppressWarnings("deprecation")
		boolean isNetworkAvailable = cm.getBackgroundDataSetting() &&
		             cm.getActiveNetworkInfo() != null;
		
		if(!isNetworkAvailable) return;
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String query = prefs.getString(NewsListFetchr.PREF_SEARCH_QUERY,null);
		String lastResultId = prefs.getString(NewsListFetchr.PREF_LAST_RESULT_ID, null);
		
		ArrayList<News> newses;
		
		
		if(query != null){
			newses = new NewsListFetchr().searchNewses(query);
		} else {
			newses =new NewsListFetchr().fetchNewses(HOME);
		}
		
		if(newses.size() == 0)
			return;
		
		String resultId = newses.get(0).getTitle();
		
		if(!resultId.equals(lastResultId)){
			Log.i(TAG,"Got a new result: " + resultId);
			
			//Notifications
			Resources r = getResources();
	        PendingIntent pi = PendingIntent
	                .getActivity(this, 0, new Intent(this, NewsListActivity.class), 0);

	        Notification notification = new NotificationCompat.Builder(this)
	                .setTicker(r.getString(R.string.new_news_title))
	                .setSmallIcon(android.R.drawable.ic_menu_report_image)
	                .setContentTitle(r.getString(R.string.new_news_title))
	                .setContentText(r.getString(R.string.new_news_text))
	                .setContentIntent(pi)
	                .setAutoCancel(true)
	                .build();
	        
	        showBackgroundNotification(0, notification);
		} else {
			Log.i(TAG,"Got a old result: " + resultId);
		}
		
		 prefs.edit()
         .putString(NewsListFetchr.PREF_LAST_RESULT_ID, resultId)
         .commit();
	}
	
    public static void setServiceAlarm(Context context, int updatePref){
		
		Intent i = new Intent(context,PollService.class);
		PendingIntent pi = PendingIntent.getService(context, 0, i, 0);
		
		AlarmManager alarmManager = (AlarmManager)
				context.getSystemService(Context.ALARM_SERVICE);
			alarmManager.setRepeating(AlarmManager.RTC, 
					System.currentTimeMillis(),updatePref*MINUTE, pi);
	} 
	
	
	public static boolean isServiceAlarmOn(Context context){
		Intent i = new Intent(context,PollService.class);
		PendingIntent pi = PendingIntent.getService(
				context, 0, i, PendingIntent.FLAG_NO_CREATE);
		return pi != null;
	}
	
	void showBackgroundNotification(int requestCode, Notification notification) {
        Intent i = new Intent(ACTION_SHOW_NOTIFICATION);
        i.putExtra("REQUEST_CODE", requestCode);
        i.putExtra("NOTIFICATION", notification);
        
        sendOrderedBroadcast(i, PERM_PRIVATE, null, null, Activity.RESULT_OK, null, null);
    }

}
