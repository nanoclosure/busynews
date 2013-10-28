package edu.nanoracket.busynews;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.ShareActionProvider;

public class NewsFragment extends SherlockFragment {
	
	public static final String URL ="NewsFragment.Url";
	
	private String mUrl;
	private WebView mWebView;
	private ShareActionProvider mShareActionProvider;

	//Attach arguments to a fragment
    public static NewsFragment newInstance(String url) {
	        Bundle args = new Bundle();
	        args.putSerializable(URL, url);

	        NewsFragment fragment = new NewsFragment();
	        fragment.setArguments(args);

	        return fragment;
	  }
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		getActivity().setTitle(R.string.news_title);
		mUrl = (String)getArguments().getSerializable(URL);
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup parent,
			Bundle savedInstanceState){
		View v = inflater.inflate(R.layout.fragment_news,parent,false);
		
		//Enable the app icon
		if(NavUtils.getParentActivityName(getActivity()) !=null){
			((SherlockFragmentActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}
		
        final ProgressBar progressBar = (ProgressBar)v.findViewById(R.id.progressBar);
        progressBar.setMax(100); // WebChromeClient reports in range 0-100

		mWebView = (WebView)v.findViewById(R.id.webView);
		mWebView.getSettings().setJavaScriptEnabled(true);
	    mWebView.setWebViewClient(new WebViewClient() {
	            public boolean shouldOverrideUrlLoading(WebView view, String url) {
	                return false;
	            }
	        });
	    
	    mWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView webView, int progress) {
                if (progress == 100) {
                    progressBar.setVisibility(View.INVISIBLE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(progress);
                }
            }
        });
		
	    mWebView.loadUrl(mUrl);
		return v;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu,MenuInflater inflater){
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.news, menu);
		
		MenuItem item = menu.findItem(R.id.action_share);
	}
	
	private Intent createShareIntent(){
		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND); 
	    sharingIntent.setType("text/plain");
	    String shareBody = "Here is the share content body";
	    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
	    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
	    return sharingIntent;
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		
		switch (item.getItemId()) {
		case android.R.id.home:
			if(NavUtils.getParentActivityName(getActivity()) !=null){
				NavUtils.navigateUpFromSameTask(getActivity());
			}
			return true;
		/*	
		case R.id.action_share:
			Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND); 
		    sharingIntent.setType("text/plain");
		    String shareBody = "Here is the share content body";
		    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
		    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
		    setShareIntent(sharingIntent);
		    startActivity(sharingIntent);
		    
		    //startActivity(Intent.createChooser(sharingIntent, "Share via"));
			return true;
			*/
		default:
			return super.onOptionsItemSelected(item);
		}
	}
			

}
