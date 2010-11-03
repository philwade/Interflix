package org.philwade.android.interflix;

import java.io.IOException;
import java.net.URLEncoder;

import org.philwade.android.interflix.R;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

//TODO: this maybe shouldn't extend QueActivity
public class MovieSearch extends QueActivity {
	public Button okButton;
	public ListView lv;
	public EditText editText;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
        editText = (EditText) findViewById(R.id.entry);
        okButton = (Button) findViewById(R.id.ok);
        setListAdapter(new ArrayAdapter<NetflixTitle>(getApplicationContext(), R.layout.quelist));
        ListView lv = getListView();
		lv.setOnItemClickListener(clickListener);
		okButton.setOnClickListener(searchListen);
    }
    
    public OnClickListener searchListen = new OnClickListener()
    {
		public void onClick(View v) {
			showDialog(PROGRESS_DIALOG);
			try {
				String term = editText.getEditableText().toString();
				String cleanTerm = URLEncoder.encode(term);
				retrieveResults(cleanTerm);
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(), "Something horrible has happened", 3000).show();
				e.printStackTrace();
			}
		}
    };
    
	public void retrieveResults(final String searchTerm)
	{
		Thread t = new Thread()
		{
			public void run()
			{
				NetflixSearchRetriever searchRetriever;
				try {
					searchRetriever = new NetflixSearchRetriever(getSharedPreferences(InterFlix.PREFS_FILE, 0));
					queItems = searchRetriever.getSearchTitles(searchTerm);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				queHandler.post(updateQue);
			}
				
		};
		t.start();
	}

	@Override
	void getQueContents() {
		// TODO Auto-generated method stub
		
	}

}
