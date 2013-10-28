package edu.nanoracket.busynews;

import java.util.ArrayList;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

public class NewsPagerActivity extends SherlockFragmentActivity {
	
	private static final String TAG = "NewsPapgerActivity";
	private ViewPager mViewPager;
	private ArrayList<News> mNews;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		mViewPager = new ViewPager(this);
		mViewPager.setId(R.id.viewPager);
		setContentView(mViewPager);
		
		mNews = NewsListFragment.mNewses;
		Log.i(TAG,"Newses in pager activity: " + mNews);
		FragmentManager fm = getSupportFragmentManager();
		
		mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
			
			@Override
			public int getCount() {
				return mNews.size();
			}
			
			@Override
			public android.support.v4.app.Fragment getItem(int pos) {
				News news = mNews.get(pos);
				return NewsFragment.newInstance(news.getLink());
			}
		});
		
		String url = (String)getIntent().getData().toString();
		for(int i = 0; i< mNews.size();i++){
			if(mNews.get(i).getLink().equals(url)){
				mViewPager.setCurrentItem(i);
				break;
			}
		}
		
	}

	
}
