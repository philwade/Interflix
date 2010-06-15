package org.philwade.android.interflix;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class TitleActivity extends Activity
{
	public NetflixTitle title;
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.title);
		getTitleData();
	}
	
	final Handler titleHandler = new Handler();
	
	final Runnable fillInTitle = new Runnable()
	{
		public void run() {
			setProgressBarIndeterminateVisibility(false);
			TextView titleTitle = (TextView) findViewById(R.id.title_title);
			titleTitle.setText(title.title);
			TextView titleSynopsis = (TextView) findViewById(R.id.title_synopsis);
			titleSynopsis.setText(title.synopsis);	
			
			ImageView coverView = (ImageView) findViewById(R.id.title_cover);
			NetflixDataRetriever retriever = new NetflixDataRetriever(getSharedPreferences(InterFlix.PREFS_FILE, 0));
			retriever.fetchImageOnThread(title.coverArt, coverView);
		}
	};
	
	public void getTitleData()
	{
		Thread t = new Thread()
		{
			public void run()
			{
				setProgressBarIndeterminateVisibility(true);
				Element node = null;
				try{
					NetflixSearchRetriever searchRetriever = new NetflixSearchRetriever(getSharedPreferences(InterFlix.PREFS_FILE, 0));
					NodeList nodes = searchRetriever.getSearchTitlesNodeList("glee");	
					node = (Element) nodes.item(0);
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), "Unable to retrieve title data", 3000).show();
				e.printStackTrace();
				}
				
				
				if(node != null)
				{
					title = new NetflixTitle(node);
					titleHandler.post(fillInTitle);
				}
			}
		};
		
		t.start();
	}
}
