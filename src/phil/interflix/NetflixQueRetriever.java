package phil.interflix;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;

import android.content.SharedPreferences;

public class NetflixQueRetriever extends NetflixDataRetriever {
	
	private HttpGet request = null;
	private DefaultHttpClient client = null;
	
	public NetflixQueRetriever(SharedPreferences prefs)
	{
		super(prefs);
		client = new DefaultHttpClient();
	}
	
	public String[] getInstantQue() throws Exception
	{
		return getQue("/queues/instant");
	}
	
	public String[] getDiscQue() throws Exception
	{
		return getQue("/queues/disc");
	}
	public String[] getQue(String uri) throws Exception
	{
		String url = "http://api.netflix.com/users/" + userId + uri;
		request = this.createRequest(url);
		try {
			signRequest(request);
			HttpResponse response = client.execute(request);
			Document xml = loadXMLFromEntity(response.getEntity());
			String[] results = nodeListToArray(xml.getElementsByTagName("title"));
			return results;
		} catch (ClientProtocolException e) {
			String[] results = {"Unable to retrieve"};
			return results;
		} catch (IOException e) {
			String[] results = {"Unable to retrieve"};
			return results;
		}
	}
}
