package phil.interflix;

import java.io.IOException;

import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthException;
import oauth.signpost.exception.OAuthExpectationFailedException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class NetflixQueRetriever extends NetflixDataRetriever {
	
	private HttpGet request = null;
	private DefaultHttpClient client = null;
	
	public NetflixQueRetriever()
	{
		client = new DefaultHttpClient();
	}
	
	public String[] getQue() throws OAuthExpectationFailedException, OAuthCommunicationException, OAuthException
	{
		String url = "http://api.netflix.com/catalog/people?term=neil";
		try {
			request = createRequest(url);
			signRequest(request);
			HttpResponse response = client.execute(request);
			Document xml = loadXMLFromEntity(response.getEntity());
			String[] results = peopleNodeHandler(xml.getElementsByTagName("name"));
			return results;
		} catch (IOException e) {
			String[] results = {"Failure"};
			return results;
		} catch (Exception e) {
			String[] results = {"Failure"};
			return results;
		}
	}
	
	public static String[] peopleNodeHandler(NodeList list)
	{
		int length = list.getLength();
    	String[] arr = new String[length];
    	for(int i = 0; i < length;i++)
    	{
    		arr[i] = list.item(i).getFirstChild().getNodeValue();
    	}
    	return arr;
	}
}
