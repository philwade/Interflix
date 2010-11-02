package org.philwade.android.interflix;

import org.philwade.android.interflix.R;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class InstantQueActivity extends QueActivity {
	public void onCreate(Bundle savedInstanceState) {
		   super.onCreate(savedInstanceState);
			ListView lv = getListView();
			lv.setOnItemClickListener(clickListener);
			lv.addFooterView(moreButton);
			
			setListAdapter(new ArrayAdapter<NetflixTitle>(this, R.layout.quelist));
			getQueContents();
		}
	
	public void getQueContents()
	{
		showDialog(PROGRESS_DIALOG);
		Thread t = new Thread()
		{
			public void run()
			{
				NetflixQueRetriever queRetriever = new NetflixQueRetriever(getSharedPreferences(InterFlix.PREFS_FILE, 0));
				try {
					queItems = queRetriever.getInstantQue(QUE_OFFSET);
				} catch (Exception e) {
					e.printStackTrace();
				}
				QUE_OFFSET = QUE_OFFSET + NetflixQueRetriever.OFFSET_INCREMENT;
				queHandler.post(updateQue);
			}
				
		};
		t.start();
	}
}
