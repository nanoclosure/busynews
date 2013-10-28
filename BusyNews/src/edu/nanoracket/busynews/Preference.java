package edu.nanoracket.busynews;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.support.v4.app.NavUtils;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;

public class Preference extends SherlockPreferenceActivity
                      implements OnSharedPreferenceChangeListener{
	 @Override
	 protected void onCreate(Bundle savedInstanceState){
		 super.onCreate(savedInstanceState);
		 this.setTitle(R.string.action_settings);
		 addPreferencesFromResource(R.xml.preferences);
		 
		 if(NavUtils.getParentActivityName(this) !=null){
			 this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}
		
	 }
	 
	 public void onResume() {
	        super.onResume();
	        getPreferenceScreen().getSharedPreferences()
	                             .registerOnSharedPreferenceChangeListener(this);
	    }
		
		@Override
	    public void onPause() {
	        super.onPause();
	        getPreferenceScreen()
	                .getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	    }
		
		@Override
	    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
	        // Sets refreshDisplay to true so that when the user returns to the main
	        // activity, the display refreshes to reflect the new settings.
	        NewsListFragment.refreshDisplay = true;
	    }
		
		//Enable ANcestral Navigation
		@Override
		public boolean onOptionsItemSelected(MenuItem item){
			switch (item.getItemId()) {
			case android.R.id.home:
				if(NavUtils.getParentActivityName(this) !=null){
					NavUtils.navigateUpFromSameTask(this);
				}
				return true;
			default:
				return super.onOptionsItemSelected(item);
			}
		}
	
	

}
