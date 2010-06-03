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
	
}
