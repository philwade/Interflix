package phil.interflix;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class DiscQueActivity extends ListActivity {
	public void onCreate(Bundle savedInstanceState) {
	   super.onCreate(savedInstanceState);
       String[] names = null;
       NetflixQueRetriever queRetriever = new NetflixQueRetriever(getSharedPreferences(InterFlix.PREFS_FILE, 0));
		names = queRetriever.getQue();
		if(names != null)
		{
			setListAdapter(new ArrayAdapter<String>(this, R.layout.que, names));
		}
		else
		{
			setListAdapter(new ArrayAdapter<String>(this, R.layout.que, QueList.FAILURE));
		}
	}
}
