package phil.interflix;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class DiscQueActivity extends ListActivity {
	public void onCreate(Bundle savedInstanceState) {
	   super.onCreate(savedInstanceState);
       String[] names = null;
       NetflixQueRetriever queRetriever = new NetflixQueRetriever(getSharedPreferences(InterFlix.PREFS_FILE, 0));
		try {
			names = queRetriever.getDiscQue();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Toast.makeText(this, "Unable to fetch disc que", 200).show();
			names = null;
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
