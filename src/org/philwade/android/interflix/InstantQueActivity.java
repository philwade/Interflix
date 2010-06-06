package org.philwade.android.interflix;

import org.philwade.android.interflix.R;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class InstantQueActivity extends ListActivity {
	public void onCreate(Bundle savedInstanceState) {
		   super.onCreate(savedInstanceState);
	       String[] names = null;
	       NetflixQueRetriever queRetriever = new NetflixQueRetriever(getSharedPreferences(InterFlix.PREFS_FILE, 0));
			try {
				names = queRetriever.getInstantQue();
			} catch (Exception e) {
				Toast.makeText(this, "Unable to fetch instant que", 200).show();
				names = null;
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(names != null)
			{
				setListAdapter(new ArrayAdapter<String>(this, R.layout.quelist, names));
			}
			else
			{
				setListAdapter(new ArrayAdapter<String>(this, R.layout.quelist, QueList.FAILURE));
			}
		}
}