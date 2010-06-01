package phil.interflix;

import android.app.ListActivity;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.w3c.dom.Document;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import phil.interflix.NetflixDataRetriever;

public class QueList extends ListActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        URL u = null;
        HttpURLConnection request = null;
		try {
			u = new URL("http://api.netflix.com/catalog/titles/autocomplete?oauth_consumer_key=zksyhhsj8uk85ckxpxurfw4v&term=arrested");
			request = (HttpURLConnection) u.openConnection();
			request.connect();
			Document doc = NetflixDataRetriever.loadXMLFromConnection(request);
			String[] titles = NetflixDataRetriever.nodeListToArray(doc.getElementsByTagName("title"));
			setListAdapter(new ArrayAdapter<String>(this, R.layout.que, titles));
    	} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
    		//catches from new URL()
			Toast.makeText(getApplicationContext(), "MalformedURLException up in here", 200).show();
			setListAdapter(new ArrayAdapter<String>(this, R.layout.que, FAILURE));
			e.printStackTrace();
		}
		catch (IOException e) {
			//catches from URL.openConnection()
			// TODO Auto-generated catch block
			Toast.makeText(getApplicationContext(), "IOException", 200).show();
			setListAdapter(new ArrayAdapter<String>(this, R.layout.que, FAILURE));
			e.printStackTrace();
		}
		catch (Exception e){
			Toast.makeText(getApplicationContext(), "General Exception up in here", 200).show();
			e.printStackTrace();
			setListAdapter(new ArrayAdapter<String>(this, R.layout.que, FAILURE));
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
