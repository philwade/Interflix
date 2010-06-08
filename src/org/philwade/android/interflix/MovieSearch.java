package org.philwade.android.interflix;

import java.io.IOException;

import org.philwade.android.interflix.R;

import android.app.Activity;
import android.app.ListActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MovieSearch extends Activity {
	public Button okButton;
	public ListView lv;
	public EditText editText;
	public NetflixSearchRetriever searchRetriever;
	public ArrayAdapter<String> adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
        SharedPreferences prefs = getSharedPreferences(InterFlix.PREFS_FILE, 0);
        editText = (EditText) findViewById(R.id.entry);
        okButton = (Button) findViewById(R.id.ok);
        lv = (ListView) findViewById(R.id.search_title_view);
        adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.quelist);
        lv.setAdapter(adapter);
        try {
			searchRetriever = new NetflixSearchRetriever(prefs);
		} catch (IOException e) {
			Toast.makeText(getApplicationContext(), "Something terrible has happened", 3000).show();
			e.printStackTrace();
		}
		okButton.setOnClickListener(clickListen);
    }
    
    public OnClickListener clickListen = new OnClickListener()
    {
		public void onClick(View v) {
			try {
				String term = editText.getEditableText().toString();
				String[] results = searchRetriever.getSearchTitles(term);
				adapter.clear();
				int resultLength = results.length;
				for(int i = 0;i < resultLength;i++)
				{
					adapter.add(results[i]);
				}
				if(resultLength == 0)
				{
					adapter.add("No results dude.");
				}
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(), "Something horrible has happened", 3000).show();
				adapter.add("I got nothing for ya.");
				e.printStackTrace();
			}
			adapter.notifyDataSetChanged();
		}
    };
    

    

    static final String[] FAILURE = new String[] {
    	"Failed to load",
      };


}
