package edu.nanoracket.busynews;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;
import android.content.Context;

public class NewsJSONSerializer {
	
	private Context mContext;
	private String mFilename;
	
	public NewsJSONSerializer(Context context,String filename){
		mContext = context;
		mFilename = filename;
	}
	
	public ArrayList<News> loadNewses() throws IOException,JSONException{
		ArrayList<News> newses = new ArrayList<News>();
		BufferedReader reader = null;
		
		try{
			// open and read the file into a StringBuilder
            InputStream in = mContext.openFileInput(mFilename);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                // line breaks are omitted and irrelevant
                jsonString.append(line);
            }
            // parse the JSON using JSONTokener
            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
            // build the array of crimes from JSONObjects
            for (int i = 0; i < array.length(); i++) {
                newses.add(new News(array.getJSONObject(i)));
            }
		}catch(FileNotFoundException e){

		}finally{
			if(reader != null)
				reader.close();
		}
		
		return newses;
	}
	
	public void saveNews(ArrayList<News> newses)
	            throws JSONException, IOException{
		JSONArray array = new JSONArray();
		for(News news: newses)
			array.put(news.toJSON());
		
		Writer writer = null;
		try{
			OutputStream out = mContext
					.openFileOutput(mFilename, Context.MODE_PRIVATE);
			writer = new OutputStreamWriter(out);
			writer.write(array.toString());
			} finally{
				if(writer != null)
					writer.close();
				
			}
		}
}
