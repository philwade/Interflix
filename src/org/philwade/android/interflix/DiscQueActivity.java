package org.philwade.android.interflix;

import org.philwade.android.interflix.R;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class DiscQueActivity extends QueActivity {
	public void onCreate(Bundle savedInstanceState) {
	   super.onCreate(savedInstanceState);
			setListAdapter(new ArrayAdapter<NetflixTitle>(this, R.layout.quelist));
			ListView lv = getListView();
			lv.setOnItemClickListener(clickListener);
			showDialog(PROGRESS_DIALOG);
			getQueContents();
			NetflixQueRetriever queRetriever = new NetflixQueRetriever(getSharedPreferences(InterFlix.PREFS_FILE, 0));
			Toast.makeText(getApplicationContext(), queRetriever.getEtag(), 3000);
	}
	
	public void getQueContents()
	{
		Thread t = new Thread()
		{
			public void run()
			{
				NetflixQueRetriever queRetriever = new NetflixQueRetriever(getSharedPreferences(InterFlix.PREFS_FILE, 0));
				try {
					queItems = queRetriever.getDiscQue();
				} catch (Exception e) {
					e.printStackTrace();
				}
				queHandler.post(updateQue);
			}
				
		};
		t.start();
	}
}
