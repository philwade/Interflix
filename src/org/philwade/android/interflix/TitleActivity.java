package org.philwade.android.interflix;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthException;
import oauth.signpost.exception.OAuthExpectationFailedException;

import org.apache.http.client.ClientProtocolException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

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
		queButton = (Button) findViewById(R.id.addQueue);
		queButton.setOnClickListener(addListener);
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
						node = searchRetriever.fetchDocument(intentUrl+"?expand=synopsis");
					}
					else
					{
						node = searchRetriever.fetchDocument("http://api.netflix.com/catalog/titles/movies/60021896?expand=synopsis");
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
			try {
				retriever.addToQue(title.idUrl);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OAuthExpectationFailedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OAuthCommunicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OAuthException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	};
}
