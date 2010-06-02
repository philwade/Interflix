package phil.interflix;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;


public class NetflixSearchRetriever extends NetflixDataRetriever {
	
	private HttpGet request = null;
	private DefaultHttpClient client = null;
	
	public NetflixSearchRetriever() throws IOException
	{
		client = new DefaultHttpClient();
	}
	
	public String[] getSearchTitles(String searchString) throws Exception
	{
		String url = "http://api.netflix.com/catalog/titles/autocomplete?oauth_consumer_key=" + consumerKey + "&term=" + searchString; 
		request = this.createRequest(url);
		try {
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
