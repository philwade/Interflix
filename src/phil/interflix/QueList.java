package phil.interflix;

import android.app.ListActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

//import oauth.signpost.OAuthConsumer;
//import oauth.signpost.basic.DefaultOAuthConsumer;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

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
			Document doc = loadXMLFromConnection(request);
			String[] titles = nodeListToArray(doc.getElementsByTagName("title"));
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
    
    public static Document loadXMLFromConnection(HttpURLConnection connection) throws Exception
    {
    	BufferedReader in = new BufferedReader(
    	new InputStreamReader(connection.getInputStream()));
    	String inputLine;
    	StringBuilder xml = new StringBuilder();

    	while ((inputLine = in.readLine()) != null) 
    		xml.append(inputLine);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml.toString()));
        return builder.parse(is);
    }
    
    public String[] nodeListToArray(NodeList list)
    {
    	int length = list.getLength();
    	String[] arr = new String[length];
    	for(int i = 0; i < length;i++)
    	{
    		NamedNodeMap attributes = list.item(i).getAttributes();
    		arr[i] = attributes.getNamedItem("short").getNodeValue();
    	}
    	
    	return arr;
    }

    static final String[] FAILURE = new String[] {
    	"Failed to load",
      };

}
