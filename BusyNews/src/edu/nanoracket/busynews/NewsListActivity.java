package edu.nanoracket.busynews;

import android.net.Uri;
import android.preference.PreferenceManager;
import android.app.SearchManager;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

public class NewsListActivity extends SingleFragmentActivity 
                   implements NewsListFragment.Callbacks{
	
	private static final String TAG = "NewsListActivity";
	
	@Override
	public Fragment createFragment(){
		return new NewsListFragment();
	}
	
	@Override
	protected int getLayoutResId(){
		return R.layout.activity_masterdetail;
	}
	
	//Determine whether the device is phone or tablet and start fragment respectively
	public void onNewsSelected(News news) {
		if(findViewById(R.id.detailFragmentContainer) == null){
			Intent i = new Intent(this, NewsPagerActivity.class);
			i.setData(Uri.parse(news.getLink()));
	        startActivity(i);
		}else{
			FragmentManager fm = getSupportFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			
			Fragment oldDetail = fm.findFragmentById(R.id.detailFragmentContainer);
			Fragment newDetail = NewsFragment.newInstance(news.getLink());
			
			if(oldDetail != null){
				ft.remove(oldDetail);
			}
			
			ft.add(R.id.detailFragmentContainer, newDetail);
			ft.commit();
		}
		
	}
	
	@Override
	public void onNewIntent(Intent intent) {
		
        NewsListFragment fragment = (NewsListFragment)getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainer);

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.i(TAG, "Received a new search query: " + query);

            PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putString(NewsListFetchr.PREF_SEARCH_QUERY, query)
                .commit();
        } else{
        	String segment = (String)intent.getStringExtra(NewsListFragment.SEGMENT);
    		Log.i(TAG,"segment in OnNewIntent: " + segment);
    		PreferenceManager.getDefaultSharedPreferences(this)
                              .edit()
                              .putString(NewsListFragment.NEWSSEGMENT,segment)
                              .commit();
        }
        Log.i(TAG,"segment in OnNewIntent: " + "Newtwork");
        fragment.loadPage();
    }

}
