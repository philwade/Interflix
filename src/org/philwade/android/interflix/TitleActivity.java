package org.philwade.android.interflix;

import org.w3c.dom.Document;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class TitleActivity extends Activity
{
	public NetflixTitle title;
	public String intentUrl;
	public Button queButton;
	public Button instantQueButton;
	public TextView inDVDQText;
	public TextView inInstantQText;
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
		instantQueButton = (Button) findViewById(R.id.addInstantQueue);
		instantQueButton.setOnClickListener(instantAddListener);
		queButton = (Button) findViewById(R.id.addQueue);
		queButton.setOnClickListener(addListener);
		inDVDQText = (TextView) findViewById(R.id.inq_text);
		inInstantQText = (TextView) findViewById(R.id.inq_instant_text);
		getTitleData();
	}
	
	final Handler titleHandler = new Handler();
	final Handler queAddHandler = new Handler();
	
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
			if(title.inDVDQ(retriever))
			{
				inDVDQText.setVisibility(View.VISIBLE);
			}
			else
			{
				queButton.setEnabled(title.discAvailable());
			}
			if(title.inInstantQ(retriever))
			{
				inInstantQText.setVisibility(View.VISIBLE);
			}
			else
			{
				instantQueButton.setEnabled(title.instantAvailable());
			}
		}
	};
	
	public void getTitleData()
	{
		setProgressBarIndeterminateVisibility(true);
		Thread t = new Thread()
		{
			public void run()
			{
				Document node = null;
				try{
					NetflixDataRetriever searchRetriever = new NetflixDataRetriever(getSharedPreferences(InterFlix.PREFS_FILE, 0));
					if(intentUrl != null)
					{
						node = searchRetriever.fetchDocument(intentUrl+"?expand=synopsis,formats");
					}
					else
					{
						//TODO remove this as it is for testing
						node = searchRetriever.fetchDocument("http://api.netflix.com/catalog/titles/movies/60021896?expand=synopsis,formats");
					}
				} catch (Exception e) {
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
	
	public OnClickListener addListener = new OnClickListener()
	{
		public void onClick(View v) {
			NetflixDataRetriever retriever = new NetflixDataRetriever(getSharedPreferences(InterFlix.PREFS_FILE, 0));
			title.addToDVDQue(retriever);
		}
	};
	
	public OnClickListener instantAddListener = new OnClickListener()
	{
		public void onClick(View v) {
			NetflixDataRetriever retriever = new NetflixDataRetriever(getSharedPreferences(InterFlix.PREFS_FILE, 0));
			title.addToInstantQue(retriever);
		}
	};
}
