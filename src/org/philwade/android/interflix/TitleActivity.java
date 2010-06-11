package org.philwade.android.interflix;

import java.io.IOException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class TitleActivity extends Activity
{
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.title);
		try {
			NetflixSearchRetriever searchRetriever = new NetflixSearchRetriever(getSharedPreferences(InterFlix.PREFS_FILE, 0));
			NodeList nodes = searchRetriever.getSearchTitlesNodeList("glee");
			Node node = nodes.item(0);
			
			if(node != null)
			{
				NetflixTitle title = new NetflixTitle(node);
				TextView titleTitle = (TextView) findViewById(R.id.title_title);
				titleTitle.setText(title.title);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
