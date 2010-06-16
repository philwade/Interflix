package org.philwade.android.interflix;

import org.philwade.android.interflix.R;
import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ArrayAdapter;

public class InstantQueActivity extends ListActivity {
	public NetflixTitle[] queItems;
	public void onCreate(Bundle savedInstanceState) {
		   super.onCreate(savedInstanceState);
			setListAdapter(new ArrayAdapter<NetflixTitle>(this, R.layout.quelist));
			getQueContents();
		}
	
	final Handler queHandler = new Handler();
	
	final Runnable updateQue = new Runnable()
	{
		@SuppressWarnings("unchecked")
		public void run() 
		{
			getParent().setProgressBarIndeterminateVisibility(false);
			if(queItems != null)
			{
				ArrayAdapter<NetflixTitle> la = (ArrayAdapter<NetflixTitle>) getListAdapter();
				la.clear();
				for(NetflixTitle item : queItems)
				{
					la.add(item);
				}
				la.notifyDataSetChanged();
			}
		}
	};
	
	public void getQueContents()
	{
		Thread t = new Thread()
		{
			public void run()
			{
				getParent().setProgressBarIndeterminateVisibility(true);
				NetflixQueRetriever queRetriever = new NetflixQueRetriever(getSharedPreferences(InterFlix.PREFS_FILE, 0));
				try {
					queItems = queRetriever.getInstantQue();
				} catch (Exception e) {
					//Toast.makeText(getApplicationContext(), "Unable to retrieve instant que", 2000).show();
					e.printStackTrace();
				}
				queHandler.post(updateQue);
			}
				
		};
		t.start();
	}
}
