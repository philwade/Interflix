package org.philwade.android.interflix;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import oauth.signpost.exception.OAuthException;

import org.philwade.android.interflix.R;
import org.xml.sax.SAXException;

import android.os.Bundle;
import android.widget.ListView;

public class InstantQueActivity extends QueActivity {
	public void onCreate(Bundle savedInstanceState) {
		   super.onCreate(savedInstanceState);
			ListView lv = getListView();
			lv.setOnItemClickListener(clickListener);
			lv.addFooterView(moreButton);
			mErrorReceiver = new ErrorReceiver(this);
			setListAdapter(new TitleAdapter(this, R.layout.quelist));
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
				} catch (ParserConfigurationException e) {
					mErrorReceiver.sendEmptyMessage(ErrorReceiver.PARSE_FAIL);
				} catch (SAXException e) {
					mErrorReceiver.sendEmptyMessage(ErrorReceiver.PARSE_FAIL);
				} catch (IOException e) {
					mErrorReceiver.sendEmptyMessage(ErrorReceiver.BROKEN_NETWORK);
				} catch (OAuthException e) {
					mErrorReceiver.sendEmptyMessage(ErrorReceiver.AUTH_FAIL);
				} catch (NullPointerException e) {
					mErrorReceiver.sendEmptyMessage(ErrorReceiver.DEFAULT);
				}
				queLength = queRetriever.resultsLength;
				QUE_OFFSET = QUE_OFFSET + offsetIncrement;
				queHandler.post(updateQue);
			}
				
		};
		t.start();
	}

	@Override
	void changeQuePosition(NetflixTitle title, int position) {
		NetflixQueRetriever queRetriever = new NetflixQueRetriever(getSharedPreferences(InterFlix.PREFS_FILE, 0));	
		try {
			queRetriever.changeInstantPosition(title, position);
		} catch (ParserConfigurationException e) {
			mErrorReceiver.sendEmptyMessage(ErrorReceiver.PARSE_FAIL);
		} catch (SAXException e) {
			mErrorReceiver.sendEmptyMessage(ErrorReceiver.PARSE_FAIL);
		} catch (IOException e) {
			mErrorReceiver.sendEmptyMessage(ErrorReceiver.BROKEN_NETWORK);
		} catch (OAuthException e) {
			mErrorReceiver.sendEmptyMessage(ErrorReceiver.AUTH_FAIL);
		}
	}
}
