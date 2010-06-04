package phil.interflix;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MovieSearch extends ListActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String[] results;
		try {
			NetflixSearchRetriever search = new NetflixSearchRetriever(getSharedPreferences(InterFlix.PREFS_FILE, 0));
			results = search.getSearchTitles("arrest");
			setListAdapter(new ArrayAdapter<String>(this, R.layout.que, results));
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), "Error encountered.", 200).show();
			setListAdapter(new ArrayAdapter<String>(this, R.layout.search, FAILURE));
		}

        ListView lv = getListView();
        lv.setTextFilterEnabled(true);

        lv.setOnItemClickListener(clickListen);
    }
    
    public OnItemClickListener clickListen = new OnItemClickListener()
    {
		public void onItemClick(AdapterView<?> arg0, View view, int arg2,
				long arg3) {
            // When clicked, show a toast with the TextView text
            Toast.makeText(getApplicationContext(), ((TextView) view).getText(),
                Toast.LENGTH_SHORT).show();
			// TODO Auto-generated method stub
		}
    };
    

    

    static final String[] FAILURE = new String[] {
    	"Failed to load",
      };


}
