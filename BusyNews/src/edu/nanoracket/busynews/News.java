package edu.nanoracket.busynews;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.json.JSONException;
import org.json.JSONObject;

public class News {
	
	private static final String JSON_TITLE = "title";
	private static final String JSON_PUBDATE = "pubDate";
	private static final String JSON_LINK = "link";
	private static final String JSON_DESCRIPTION = "description";
	
	private String mTitle;
	private String mPubDate;
	private String mLink;
	private String mDescription;
	
	public News(){
		
	}
	
	//Load News from saved file
	public News(JSONObject json) throws JSONException{
		mTitle = json.getString(JSON_TITLE);
		mPubDate = json.getString(JSON_PUBDATE);
		mLink = json.getString(JSON_LINK);
		mDescription = json.getString(JSON_DESCRIPTION);
	}
	
	//Convert News class to JSONObject
	public JSONObject toJSON() throws JSONException{
		JSONObject json = new JSONObject();
		json.put(JSON_TITLE, mTitle);
		json.put(JSON_PUBDATE, mPubDate);
		json.put(JSON_LINK, mLink);
		json.put(JSON_DESCRIPTION, mDescription);

        return json;
	}
	
	public News(String title, String pubDate, String link, String description) {
		mTitle = title;
		mPubDate = pubDate;
		mLink = link;
		mDescription = description;
	}

	
	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		mTitle = title;
	}

	public String getPubDate() {
		return mPubDate;
	}

	public void setPubDate(String pubDate) {
		mPubDate = pubDate;
	}

	public String getLink() {
		return mLink;
	}

	public void setLink(String link) {
		mLink = link;
	}

	public String getDescription() {
		return mDescription;
	}

	public void setDescription(String description) {
		mDescription = description;
	}
	
	public String getTime(String pubDate){
		
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss zzz");
		Date time = new Date();
		try{
			time = sdf.parse(pubDate);
		}catch(ParseException e){
			e.printStackTrace();
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(time);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		String timeString = Integer.toString(hour)+":"+Integer.toString(minute);
		
		return timeString;
	}

	@Override
	public String toString(){
		return mTitle;
	}

}
