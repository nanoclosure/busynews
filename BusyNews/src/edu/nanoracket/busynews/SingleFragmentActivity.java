package edu.nanoracket.busynews;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

public abstract class SingleFragmentActivity extends SherlockFragmentActivity {
    protected abstract Fragment createFragment();
    
    protected int getLayoutResId() {
    	return R.layout.activity_fragment;
		
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.fragmentContainer);

        if (fragment == null) {
        	
            fragment = createFragment();
            
            manager.beginTransaction()
                .add(R.id.fragmentContainer, fragment)
                .commit();
        }
    }
}
