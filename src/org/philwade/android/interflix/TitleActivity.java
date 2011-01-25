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
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RatingBar.OnRatingBarChangeListener;

public class TitleActivity extends Activity
{
	public NetflixTitle title;
	public String intentUrl;
	public Button queButton;
	public Button instantQueButton;
	public Button rateThisButton;
	public Dialog rateDialog;
	private static final int RATE_DIALOG = 0;
	@SuppressWarnings("unused")
	private static final String TAG = "TitleActivity";
	private Float userRating;
	private NetflixDataRetriever retriever;
	private ErrorReceiver mErrorReceiver;
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mErrorReceiver = new ErrorReceiver(this);
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
		rateThisButton = (Button) findViewById(R.id.rateThis);
		rateThisButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showDialog(RATE_DIALOG);
			}
		});
		retriever = new NetflixDataRetriever(getSharedPreferences(InterFlix.PREFS_FILE, 0));
		setProgressBarIndeterminateVisibility(true);
		refreshTitle();
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
				queButton.setEnabled(false);
				queButton.setText(R.string.inq_text);
			}
			else
			{
				if(!title.discAvailable())
				{
					queButton.setText(R.string.disc_unavailable);
				}
				queButton.setEnabled(title.discAvailable());
			}
			if(title.inInstantQ(retriever))
			{
				instantQueButton.setEnabled(false);
				instantQueButton.setText(R.string.inq_instant_text);
			}
			else
			{
				if(!title.instantAvailable())
				{
					instantQueButton.setText(R.string.instant_unavailable);
				}
				instantQueButton.setEnabled(title.instantAvailable());
			}
		
			RatingBar ratingDisplay;
			if(userRating != null)
			{
				CheckBox notInterested = (CheckBox) findViewById(R.id.title_not_interested);
				ratingDisplay = (RatingBar) findViewById(R.id.userRatingbar);
				ratingDisplay.setVisibility(View.VISIBLE);
				notInterested.setEnabled(false);
				ratingDisplay.setIsIndicator(true);
				if(userRating == NetflixTitle.NOT_INTERESTED)
				{
					ratingDisplay.setRating(0);
					notInterested.setChecked(true);
					notInterested.setVisibility(View.VISIBLE);
				}
				else
				{
					ratingDisplay.setRating(userRating);
					notInterested.setChecked(false);
				}
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
				Button cancelButton = (Button) rateDialog.findViewById(R.id.rate_pick_cancel);
				final RatingBar rater = (RatingBar) rateDialog.findViewById(R.id.popupRatingbar);
				final CheckBox notInterested = (CheckBox) rateDialog.findViewById(R.id.rate_pick_not_interested);
				rater.setOnRatingBarChangeListener(new OnRatingBarChangeListener(){
					public void onRatingChanged(RatingBar ratingBar,
							float rating, boolean fromUser) {
						notInterested.setChecked(false);
					}
				});
				notInterested.setOnCheckedChangeListener(new OnCheckedChangeListener(){
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if(isChecked)
						{
							rater.setRating(0);
							buttonView.setChecked(isChecked);
						}
						
					}
				});
				okButton.setOnClickListener(new OnClickListener(){
					public void onClick(View v) {
						float setRating;
						if(notInterested.isChecked())
						{
							setRating = NetflixTitle.NOT_INTERESTED;
						}
						else
						{
							setRating = rater.getRating();
						}
						doRating(setRating);
						rateDialog.dismiss();
					}
				});
				cancelButton.setOnClickListener(new OnClickListener(){
					public void onClick(View v) {
						removeDialog(RATE_DIALOG);
					}
				});
				return rateDialog;
		}
		return null;
	}
	
	public void doRating(final float rating)
	{
			setProgressBarIndeterminateVisibility(true);
			Thread t = new Thread()
			{
				public void run()
				{
					title.rate((int) rating, retriever, mErrorReceiver);
					titleHandler.post(HideRatingBars);
					refreshTitle();
				}
			};
			t.start();
	}
	
	public OnClickListener addListener = new OnClickListener()
	{
		public void onClick(View v) {

			setProgressBarIndeterminateVisibility(true);
			Thread t = new Thread()
			{
				public void run()
				{
					title.addToDVDQue(retriever, mErrorReceiver);
					refreshTitle();
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
					title.addToInstantQue(retriever);
					refreshTitle();
				}
			};
			t.start();
		}
	};
	
	public final Runnable HideRatingBars = new Runnable()
	{
		
		public void run() {
			RatingBar publicRating = (RatingBar) findViewById(R.id.ratingbar);
			publicRating.setVisibility(View.INVISIBLE);
			RatingBar mUserRating = (RatingBar) findViewById(R.id.userRatingbar);
			mUserRating.setVisibility(View.INVISIBLE);
			CheckBox mCheckBox = (CheckBox) findViewById(R.id.title_not_interested);
			mCheckBox.setVisibility(View.INVISIBLE);
		}
	};
	
	public void refreshTitle()
	{
		Thread t = new Thread()
		{
			public void run()
			{
				Document node = null;
				try {
					node = retriever.fetchDocument(intentUrl+"?expand=synopsis,formats");
				} catch (OAuthExpectationFailedException e) {
					mErrorReceiver.sendEmptyMessage(ErrorReceiver.AUTH_FAIL);
				} catch (OAuthCommunicationException e) {
					mErrorReceiver.sendEmptyMessage(ErrorReceiver.AUTH_FAIL);
				} catch (ParserConfigurationException e) {
					mErrorReceiver.sendEmptyMessage(ErrorReceiver.PARSE_FAIL);
				} catch (SAXException e) {
					mErrorReceiver.sendEmptyMessage(ErrorReceiver.PARSE_FAIL);
				} catch (IOException e) {
					mErrorReceiver.sendEmptyMessage(ErrorReceiver.BROKEN_NETWORK);
				} catch (OAuthException e) {
					mErrorReceiver.sendEmptyMessage(ErrorReceiver.AUTH_FAIL);
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
}
