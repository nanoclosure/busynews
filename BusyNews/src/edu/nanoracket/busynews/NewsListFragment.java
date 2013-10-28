package edu.nanoracket.busynews;

import java.lang.reflect.Field;
import java.util.ArrayList;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;



public class NewsListFragment extends VisibleFragment implements 
                                       ActionBar.OnNavigationListener {
	
	private static final String TAG = "BookListFragment";
	static ArrayList<News> mNewses;
	private Menu mOptionMenu;

    public static final String HOME = "home";
    public static final String SEGMENT = "segment";
    public static final String NEWSSEGMENT = "newsSegment";
    
	public static final String WIFI = "Wi-Fi";
    public static final String ANY = "Any";
    private static boolean wifiConnected = false;
    private static boolean mobileConnected = false;
    public static boolean refreshDisplay = true;

    // The user's current network preference setting.
    public static String networkPref = null;
    public static boolean alarmPref=false;
    
    public static int pollIntervalPref;
    private Callbacks mCallbacks;
    
    //Fragemnt callback interface
    public interface Callbacks{
    	void onNewsSelected(News news);
    }
    
    @Override
    public void onAttach(Activity activity){
    	super.onAttach(activity);
    	mCallbacks = (Callbacks)activity;
    }
    
    public void onDetach(){
    	super.onDetach();
    	mCallbacks = null;
    }

    // The BroadcastReceiver that tracks network connectivity changes.
    private NetworkReceiver receiver = new NetworkReceiver();
    
	
	@Override
	public void onCreate(Bundle SavedInstanceState){
		super.onCreate(SavedInstanceState);
		setHasOptionsMenu(true);
		final ActionBar actionBar = ((SherlockFragmentActivity)getActivity()).getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		
		// Set up the dropdown list navigation in the action bar.
		actionBar.setListNavigationCallbacks(
		       // Specify a SpinnerAdapter to populate the dropdown list.
				new ArrayAdapter<String>(getActionBarThemedContextCompat(),
				    android.R.layout.simple_list_item_1,
					android.R.id.text1, 
					getResources().getStringArray(R.array.listSegment)),
					this);
		
		// Register BroadcastReceiver to track connection changes.
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        getActivity().registerReceiver(receiver, filter);
	} 
	

	private Context getActionBarThemedContextCompat(){
		return ((SherlockFragmentActivity)getActivity()).getSupportActionBar().getThemedContext();
	}
	
	//Listener for Navigation Item 
	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
	    String s = (String)getResources()
	    		.getStringArray(R.array.listSegment)[itemPosition];
        Log.i(TAG,"segment selected: " + s);
	    Intent i = new Intent(getActivity(),NewsListActivity.class);
	    i.putExtra(SEGMENT, s);
	    Log.i(TAG,"Intent Action: " + i.getAction());
	    startActivity(i);
		return true;
	}
	
	// Refreshes the display if the network connection and the
    // pref settings allow it.
    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences sharedPrefs = PreferenceManager
        		.getDefaultSharedPreferences(getActivity());
        networkPref = sharedPrefs.getString("networkPref", "Any");
        alarmPref =sharedPrefs.getBoolean("AUTO_UPDATE", false);
        updateConnectedFlags();
 
        if (refreshDisplay) {
            loadPage();
        }
        
        if(alarmPref){
        	String s = sharedPrefs.getString("updatePref", "60");
        	Log.i(TAG,"The String Value is :" + s);
        	pollIntervalPref = Integer.valueOf(s);
        	PollService.setServiceAlarm(getActivity(), pollIntervalPref);
        	Log.i(TAG,"Start the poll service");
        }
    }
   
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            getActivity().unregisterReceiver(receiver);
        }
    } 

    // Checks the network connection and sets the wifiConnected and mobileConnected
    // variables accordingly.
    private void updateConnectedFlags() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getActivity().
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        } else {
            wifiConnected = false;
            mobileConnected = false;
        }
    }
    
    // Uses AsyncTask subclass to download the JSON feed from USATOday.com.
    public void loadPage() {
        if (((networkPref.equals(ANY)) && (wifiConnected || mobileConnected))
                || ((networkPref.equals(WIFI)) && (wifiConnected))) {
            // AsyncTask subclass
         	new FetchNewsesTask().execute();
         	setRefreshActionState(false);
        } else {
        	// Displays an error if the app is unable to load content.
            FragmentManager fm = getActivity()
            			.getSupportFragmentManager();
            NetworkAlertFragment alert = new NetworkAlertFragment();
            alert.show(fm, "dialog");
         	setRefreshActionState(false);
            Log.i(TAG,"Lost Connction");
        }
    }
    
    //AsyncTask subclass
	private class FetchNewsesTask extends AsyncTask<Void, Void, ArrayList<News>> {
		
		@Override
		protected ArrayList<News> doInBackground(Void... params){
		    Activity activity = getActivity();
		    if(activity == null)
		    	return new ArrayList<News>();
		    
		    String query = PreferenceManager.getDefaultSharedPreferences(activity)
		    		       .getString(NewsListFetchr.PREF_SEARCH_QUERY, null);
		    
		    String segment = PreferenceManager.getDefaultSharedPreferences(activity)
		    		.getString(NEWSSEGMENT, null);
		    
		    Log.i(TAG,"segment in FetchNewsesTask Class" + segment);
		    
			if(query != null){
				return new NewsListFetchr().searchNewses(query);
			} else if(segment != null){
			 return new NewsListFetchr().fetchNewses(segment);
			} else {
		     return new NewsListFetchr().fetchNewses(HOME);
			}
		}
	
		@Override
		protected void onPostExecute(ArrayList<News> newses){
			mNewses = newses;
			Log.i(TAG,"Book ArrayList Received: " + mNewses);
			setupAdapter();
		} 
	}
	
	//Setup listview adapter
	public void setupAdapter(){
		if(getActivity() == null  || getListView() == null) return;
		if(mNewses != null){
			setListAdapter(new NewsAdapter(mNewses));
		} else {
			setListAdapter(null);
		}
	}
	
	//NewAdapter inner class
	public class NewsAdapter extends ArrayAdapter<News>{
		public NewsAdapter(ArrayList<News> newses){
			super(getActivity(),android.R.layout.simple_list_item_1,newses);
		}
		
		@Override
		public View getView(int position,View convertView,ViewGroup parent){
			if(convertView == null){
				convertView = getActivity().getLayoutInflater()
						.inflate(R.layout.list_item_news, null);
			}
			
			News news = getItem(position);
			
			
			TextView titleTextView =
	                (TextView)convertView.findViewById(R.id.news_list_item_titleTextView);
	        titleTextView.setText(news.getTitle());
	        
	        String viewDeString = news.getTime(news.getPubDate())+" || "
                                  + news.getDescription();
	        
	        String viewString = news.getDescription();
	        TextView descriptionTextView = 
	                (TextView)convertView.findViewById(R.id.news_list_item_description);
	        descriptionTextView.setText(viewString);
	        
	        TextView dateTextView =
	                (TextView)convertView.findViewById(R.id.news_list_item_dateTextView);
	        dateTextView.setText(news.getPubDate());
	        
	        return convertView;
		}
	}
	
	//When news is selected, start new activity
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) { 
		News news = (News) mNewses.get(position);
        Log.i(TAG,"News selected: " + news);
        Uri bookReviewUri = Uri.parse(news.getLink());
        Log.i(TAG,"URL selected: " + bookReviewUri);
        mCallbacks.onNewsSelected(news);
    }
	
	@Override
	@TargetApi(11)
	public void onCreateOptionsMenu(Menu menu,MenuInflater inflater){
		mOptionMenu = menu;
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.news_list, menu);
		setRefreshActionState(false);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	            // pull out the SearchView
	            MenuItem searchItem = menu.findItem(R.id.action_search);
	            SearchView searchView = (SearchView)searchItem.getActionView();
	            // get the data from our searchable.xml as a SearchableInfo
	            SearchManager searchManager = (SearchManager)getActivity()
	                .getSystemService(Context.SEARCH_SERVICE);
	            ComponentName name = getActivity().getComponentName();
	            SearchableInfo searchInfo = searchManager.getSearchableInfo(name);
	            searchView.setSearchableInfo(searchInfo);
	    }
	}
	
	//MenuItem on Actionbar
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		int id = item.getItemId();
		if(id == R.id.action_refresh){
			setRefreshActionState(true);
			loadPage();
			return true;
		}else if (id == R.id.action_search) {
			getActivity().onSearchRequested();
			return true;
		}else if(id ==  R.id.action_cancel){
			PreferenceManager.getDefaultSharedPreferences(getActivity())
		       .edit()
		       .putString(NewsListFetchr.PREF_SEARCH_QUERY,null)
		       .commit();
		       loadPage();
		     return true;
		}else if(id == R.id.action_settings){
			Intent i = new Intent(getActivity(),Preference.class);
			startActivity(i);
			return true;
		}else{
			return super.onOptionsItemSelected(item);
		}	
	}
	
	
	public void setRefreshActionState(final boolean refreshing){
		if(mOptionMenu != null) {
			final MenuItem refreshItem = mOptionMenu.
					findItem(R.id.action_refresh);	
		if(refreshItem != null){
			if(refreshing){
				refreshItem.setActionView(R.layout.refresh_progress);
			} else {
				refreshItem.setActionView(null);
				}
		 }
	 }
	}
	
	private void getOverflowMenu() {

        try {
            ViewConfiguration config = ViewConfiguration.get(getActivity());
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if(menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    } 
	
    public class NetworkReceiver extends BroadcastReceiver {
       @Override
       public void onReceive(Context context, Intent intent) {
           ConnectivityManager connMgr =
                   (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
           NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

           // Checks the user prefs and the network connection. 
           if (WIFI.equals(networkPref) && networkInfo != null
                   && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
               refreshDisplay = true;
               Toast.makeText(context, R.string.wifi_connected, Toast.LENGTH_SHORT).show();
               
            } else if (ANY.equals(networkPref) && networkInfo != null) {
               refreshDisplay = true;
               
           } else {
               refreshDisplay = false;
               Toast.makeText(context, R.string.lost_connection, Toast.LENGTH_SHORT).show();
           }
       }
   } 
}
