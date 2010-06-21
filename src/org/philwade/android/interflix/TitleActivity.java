package org.philwade.android.interflix;

import org.w3c.dom.Document;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class TitleActivity extends Activity
{
	public NetflixTitle title;
	public String intentUrl;
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.title);
		if(getIntent().hasExtra("idUrl"))
		{
			Bundle data = getIntent().getExtras();
			intentUrl = data.getString("idUrl");
		}
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
			titleSynopsis.setText(Html.fromHtml(title.synopsis));	
			
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
				Document node = null;
				try{
					NetflixSearchRetriever searchRetriever = new NetflixSearchRetriever(getSharedPreferences(InterFlix.PREFS_FILE, 0));
					if(intentUrl != null)
					{
						node = searchRetriever.fetchDocument(intentUrl+"?expand=synopsis");
					}
					else
					{
						node = searchRetriever.fetchDocument("http://api.netflix.com/catalog/titles/movies/60021896?expand=synopsis");
					}
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
