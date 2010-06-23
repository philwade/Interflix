package org.philwade.android.interflix;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

//import oauth.signpost.OAuthConsumer;
//import oauth.signpost.basic.DefaultOAuthConsumer;

public class NetflixDataRetriever {
		
	static final String consumerKey = "zksyhhsj8uk85ckxpxurfw4v";
	static final String sharedSecret = "rAqtAeRYGT";
	public static final String NETFLIX_REQUEST_TOKEN_URL = "http://api.netflix.com/oauth/request_token";
    public static final String NETFLIX_ACCESS_TOKEN_URL = "http://api.netflix.com/oauth/access_token";
    public static final String NETFLIX_AUTHORIZE_URL = "https://api-user.netflix.com/oauth/login";
    public static final String APPLICATION_NAME = "InterFlix";
    public static final String APP_URI = "interflix-app:///";
    public static final int HTTP_GET = 0;
    public static final int HTTP_POST = 1;
    private static final Uri CALLBACK_URI = Uri.parse("interflix-app:///");
    public String userId;
    public String userToken;
    public String userSecret;
    
    public CommonsHttpOAuthConsumer consumer = new CommonsHttpOAuthConsumer(consumerKey, sharedSecret);
	public OAuthProvider provider = new CommonsHttpOAuthProvider(NETFLIX_REQUEST_TOKEN_URL, NETFLIX_ACCESS_TOKEN_URL, NETFLIX_AUTHORIZE_URL);
	private SharedPreferences prefs;
   
    public NetflixDataRetriever(SharedPreferences preferences)
    {
    	this.prefs = preferences;
    	userToken =  prefs.getString("oauth_token", null);
    	userSecret = prefs.getString("oauth_token_secret", null);
    	userId = prefs.getString("user_id", null);
    	if(userId != null)
    	{
    		consumer.setTokenWithSecret(userToken, userSecret);
    	}
    }
    
    public Document fetchDocument(String urlString) throws OAuthExpectationFailedException, OAuthCommunicationException, ParserConfigurationException, SAXException, IOException, OAuthException
    {
    	return fetchDocument(urlString, HTTP_GET);
    }
    
    public Document fetchDocument(String urlString, int requestType) throws ParserConfigurationException, SAXException, IOException, OAuthExpectationFailedException, OAuthCommunicationException, OAuthException
    {
    	InputStream stream = fetch(urlString, true, requestType);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(stream);
    }
    
    public static String[] nodeListToArray(NodeList list)
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
    
    public HttpGet createRequest(String url) throws IOException
    {
		HttpGet request = new HttpGet(url);
		return request;
    }
    
    public static void saveUserKeys(SharedPreferences prefs, String oauth_token, 
    								String oauth_token_secret, String user_id)
    {
    	SharedPreferences.Editor editor = prefs.edit();
    	editor.putString("oauth_token", oauth_token);
    	editor.putString("oauth_token_secret", oauth_token_secret);
    	editor.putString("user_id", user_id);
    	editor.commit();
    	
    }
    
    public void saveEtag(String etag)
    {
    	SharedPreferences.Editor editor = prefs.edit();
    	editor.putString("etag", etag);
    	editor.commit();
    }
    
    public String getEtag()
    {
    	return prefs.getString("etag", null);
    }
    
    public void signRequest(HttpRequestBase request) throws OAuthException, OAuthExpectationFailedException, OAuthCommunicationException
    {
    	consumer.sign(request);
    }
    
    public Uri requestAuthUri() throws OAuthMessageSignerException, OAuthNotAuthorizedException, OAuthExpectationFailedException, OAuthCommunicationException
    {
    	//found out where to send the user
    	String authUrl = provider.retrieveRequestToken(consumer, CALLBACK_URI.toString());
    	authUrl = OAuth.addQueryParameters(authUrl, OAuth.OAUTH_CONSUMER_KEY, consumerKey,
                "application_name", APPLICATION_NAME);	
    	Uri authUri = Uri.parse(authUrl);
    	
    	//save this stuff - we need it again to get the access token and secret
    	String requestToken = authUri.getQueryParameter("oauth_token");
    	SharedPreferences.Editor editor = prefs.edit();
    	editor.putString("request_key_secret", consumer.getTokenSecret());
		editor.putString("request_key", requestToken);
    	editor.commit();
    	
    	return authUri;
    }
    
    public void setupAccessTokens(String oauth_token) throws OAuthMessageSignerException, OAuthNotAuthorizedException, OAuthExpectationFailedException, OAuthCommunicationException
    {
    	consumer.setTokenWithSecret(prefs.getString("request_key", ""), prefs.getString("request_key_secret", ""));
    	provider.retrieveAccessToken(consumer, oauth_token);
    	saveUserKeys(prefs, consumer.getToken(), consumer.getTokenSecret(), provider.getResponseParameters().get("user_id").first());
    }
    
    public void fetchImageOnThread(final String urlString, final ImageView imageView)
    {
    	final Handler handler = new Handler() {
    		@Override
    		public void handleMessage(Message message) {
    			imageView.setImageDrawable((Drawable) message.obj);
    		}
    	};

    	Thread thread = new Thread() {
    		@Override
    		public void run() {
    			//TODO : set imageView to a "pending" image
    			Drawable drawable = null;
				try {
					drawable = fetchDrawable(urlString);
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (OAuthExpectationFailedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (OAuthCommunicationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (OAuthException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			Message message = handler.obtainMessage(1, drawable);
    			handler.sendMessage(message);
    		}
    	};
    	thread.start();
    }
    
    public Drawable fetchDrawable(String urlString) throws IllegalStateException, IOException, OAuthExpectationFailedException, OAuthCommunicationException, OAuthException
    {
    	InputStream stream = fetch(urlString);
    	Drawable drawable = Drawable.createFromStream(stream, "src");
    	return drawable;
    }
    
    public InputStream fetch(String urlString) throws ClientProtocolException, OAuthExpectationFailedException, OAuthCommunicationException, IOException, OAuthException
    {
    	return fetch(urlString, false, HTTP_GET);
    }
    
    public InputStream fetch(String urlString, boolean signed, int requestType) throws ClientProtocolException, IOException, OAuthExpectationFailedException, OAuthCommunicationException, OAuthException
    {
    	DefaultHttpClient httpClient = new DefaultHttpClient();
    	HttpRequestBase request = getRequest(urlString, requestType);
    	
    	if(signed)
    	{
    		signRequest(request);
    	}
    	HttpResponse response = httpClient.execute(request);
    	InputStream stream = response.getEntity().getContent();
    	
    	return stream;
    }
    
    public HttpRequestBase getRequest(String urlString, int request_type)
    {
    	switch(request_type)
    	{
    		case HTTP_GET:
    			return new HttpGet(urlString);
    		case HTTP_POST:
    			return new HttpPost(urlString);
    	}
    	return null;
    }
    
    public NetflixTitle[] constructTitleObjects(NodeList list)
	{
		int length = list.getLength();
		NetflixTitle[] titles = new NetflixTitle[length];
		for(int i = 0; i < length;i++)
		{
			Element el = (Element) list.item(i);
			titles[i] = new NetflixTitle(el);
		}
		
		return titles;
	}
    
    public void cleanPreferences()
    {
    	SharedPreferences.Editor editor = prefs.edit();
    	editor.putString("request_key", null);
    	editor.putString("request_key_secret", null);
    	editor.putString("user_id", null);
    	editor.putString("oauth_token", null);
    	editor.putString("oauth_token_secret", null);
    	editor.commit();
    }
    
    
}
