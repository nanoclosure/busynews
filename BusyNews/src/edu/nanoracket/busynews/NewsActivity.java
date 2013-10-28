package edu.nanoracket.busynews;

import android.support.v4.app.Fragment;

public class NewsActivity extends SingleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		
		return new NewsFragment();
	}

}
