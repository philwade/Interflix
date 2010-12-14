package org.philwade.android.interflix;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthException;
import oauth.signpost.exception.OAuthExpectationFailedException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class TitleActivity extends Activity
{
	public NetflixTitle title;
	public String intentUrl;
	public Button queButton;
	public Button instantQueButton;
	public Button rateThisButton;
	public TextView inDVDQText;
	public TextView inInstantQText;
	public Dialog rateDialog;
	private static final int RATE_DIALOG = 0;
	private Float userRating;
	private NetflixDataRetriever retriever;
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
		rateThisButton = (Button) findViewById(R.id.rateThis);
		rateThisButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showDialog(RATE_DIALOG);
			}
		});
		retriever = new NetflixDataRetriever(getSharedPreferences(InterFlix.PREFS_FILE, 0));
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
			retriever.fetchImageOnThread(title.coverArt, coverView);
			if(title.inDVDQ(retriever))
			{
				inDVDQText.setVisibility(View.VISIBLE);
				queButton.setEnabled(false);
			}
			else
			{
				queButton.setEnabled(title.discAvailable());
			}
			if(title.inInstantQ(retriever))
			{
				inInstantQText.setVisibility(View.VISIBLE);
				instantQueButton.setEnabled(false);
			}
			else
			{
				instantQueButton.setEnabled(title.instantAvailable());
			}
		
			RatingBar ratingDisplay;
			if(userRating != null)
			{
				ratingDisplay = (RatingBar) findViewById(R.id.userRatingbar);
				ratingDisplay.setRating(userRating);
				ratingDisplay.setVisibility(View.VISIBLE);
				ratingDisplay.setIsIndicator(true);
			}
			else
			{	
				ratingDisplay = (RatingBar) findViewById(R.id.ratingbar);
				ratingDisplay.setRating(title.rating);
				ratingDisplay.setVisibility(View.VISIBLE);
				ratingDisplay.setIsIndicator(true);
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
					node = retriever.fetchDocument(intentUrl+"?expand=synopsis,formats");
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				
				if(node != null)
				{
					title = new NetflixTitle(node);
					userRating = title.getUserRating(retriever);
					titleHandler.post(fillInTitle);
				}
			}
		};
		
		t.start();
	}
	
	@Override
	protected Dialog onCreateDialog(int id)
	{
		switch(id)
		{
			case RATE_DIALOG:
				rateDialog = new Dialog(this);
				rateDialog.setTitle("Rate this title");
				rateDialog.setContentView(R.layout.rate_title_dialog);
				Button okButton = (Button) rateDialog.findViewById(R.id.rate_pick_ok);
				okButton.setOnClickListener(new OnClickListener(){
					public void onClick(View v) {
						// TODO Auto-generated method stub
						RatingBar rating = (RatingBar) rateDialog.findViewById(R.id.popupRatingbar);
						doRating(rating.getRating());
						rateDialog.dismiss();
					}
				});
				return rateDialog;
		}
		return null;
	}
	
	public void doRating(float rating)
	{
		Toast.makeText(this, Float.toString(rating), 3000).show();
	}
	public OnClickListener addListener = new OnClickListener()
	{
		public void onClick(View v) {

			setProgressBarIndeterminateVisibility(true);
			Thread t = new Thread()
			{
				public void run()
				{
					Document node = null;
					title.addToDVDQue(retriever);
					try {
						node = retriever.fetchDocument(intentUrl+"?expand=synopsis,formats");
					} catch (OAuthExpectationFailedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (OAuthCommunicationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ParserConfigurationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SAXException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (OAuthException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					title = new NetflixTitle(node);
					titleHandler.post(fillInTitle);
				}
			};
			t.start();
		}
	};
	
	public OnClickListener instantAddListener = new OnClickListener()
	{
		public void onClick(View v) {
			setProgressBarIndeterminateVisibility(true);
			Thread t = new Thread()
			{
				public void run()
				{
					Document node = null;
					title.addToInstantQue(retriever);
					try {
						node = retriever.fetchDocument(intentUrl+"?expand=synopsis,formats");
					} catch (OAuthExpectationFailedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (OAuthCommunicationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ParserConfigurationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SAXException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (OAuthException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					title = new NetflixTitle(node);
					titleHandler.post(fillInTitle);
				}
			};
			t.start();
		}
	};
}
