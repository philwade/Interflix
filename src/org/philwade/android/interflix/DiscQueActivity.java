package org.philwade.android.interflix;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthException;
import oauth.signpost.exception.OAuthExpectationFailedException;

import org.philwade.android.interflix.R;
import org.xml.sax.SAXException;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class DiscQueActivity extends QueActivity {
	public void onCreate(Bundle savedInstanceState) {
	   super.onCreate(savedInstanceState);
			ListView lv = getListView();
			lv.setOnItemClickListener(clickListener);
			lv.addFooterView(moreButton);
			setListAdapter(new TitleAdapter(this, R.layout.quelist));
			mErrorReceiver = new ErrorReceiver(this);
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
						queItems = queRetriever.getDiscQue(QUE_OFFSET);
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
					queLength = queRetriever.resultsLength;
				QUE_OFFSET = QUE_OFFSET + NetflixQueRetriever.OFFSET_INCREMENT;
				queHandler.post(updateQue);
			}
				
		};
		t.start();
	}

	@Override
	void changeQuePosition(NetflixTitle title, int position) {
		NetflixQueRetriever queRetriever = new NetflixQueRetriever(getSharedPreferences(InterFlix.PREFS_FILE, 0));	
		queRetriever.changeDiscPosition(title, position);
	}
}
