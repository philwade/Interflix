package org.philwade.android.interflix;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.philwade.android.interflix.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

//TODO: this maybe shouldn't extend QueActivity
public class MovieSearch extends QueActivity {
	public Button okButton;
	public ListView lv;
	public EditText editText;
	public String currentSearch;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        offsetIncrement = Integer.parseInt(getSharedPreferences(InterFlix.PREFS_FILE, 0).getString("search step", "25"));
		setContentView(R.layout.search);
        editText = (EditText) findViewById(R.id.entry);
        okButton = (Button) findViewById(R.id.ok);
        ListView lv = getListView();
		lv.setOnItemClickListener(clickListener);
		lv.addFooterView(moreButton);
        setListAdapter(new TitleAdapter(getApplicationContext(), R.layout.quelist));
		okButton.setOnClickListener(searchListen);
		
		editText.setOnKeyListener(new OnKeyListener(){

			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_ENTER)
				{
					doSearch();
					return true;
				}
				return false;
			}
			
		});
    }
    
    public OnClickListener searchListen = new OnClickListener()
    {
		public void onClick(View v) {
			doSearch();
		}
    };
    
    public void doSearch()
    {
			showDialog(PROGRESS_DIALOG);
			appendNew = false;
			getApplicationContext();
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

			try {
				String term = editText.getEditableText().toString();
				String cleanTerm = URLEncoder.encode(term);
				currentSearch = cleanTerm;
				retrieveResults(cleanTerm);
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(), "Something horrible has happened", 3000).show();
				e.printStackTrace();
			}
    	
    }
    
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
					queLength = searchRetriever.resultsLength;
					
					if(queLength > offsetIncrement)
					{
						moreButton.setEnabled(true);
					}
					else
					{
						moreButton.setEnabled(false);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				QUE_OFFSET = QUE_OFFSET + offsetIncrement;
				queHandler.post(updateQue);
			}
				
		};
		t.start();
	}

	@Override
	void getQueContents() {
		// adds to the end of the list
		appendNew = true;
		showDialog(PROGRESS_DIALOG);
		Thread t = new Thread()
		{
			public void run()
			{
				try {
					NetflixSearchRetriever searchRetriever = new NetflixSearchRetriever(getSharedPreferences(InterFlix.PREFS_FILE, 0));
					queItems = searchRetriever.getSearchTitles(currentSearch, QUE_OFFSET);
					queLength = searchRetriever.resultsLength;
				} catch (Exception e) {
					e.printStackTrace();
				}
				QUE_OFFSET = QUE_OFFSET + offsetIncrement;
				queHandler.post(updateQue);
			}
				
		};
		t.start();	
	}

	@Override
	void changeQuePosition(NetflixTitle title, int position) {
		// TODO Auto-generated method stub
		
	}
	
}
