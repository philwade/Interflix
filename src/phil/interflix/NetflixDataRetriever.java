package phil.interflix;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.content.SharedPreferences;
import android.net.Uri;

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
    private static final Uri CALLBACK_URI = Uri.parse("interflix-app:///");
	
    public static Document loadXMLFromEntity(HttpEntity entity) throws Exception
    {
    	BufferedReader in = new BufferedReader(
    	new InputStreamReader(entity.getContent()));
    	String inputLine;
    	StringBuilder xml = new StringBuilder();

    	while ((inputLine = in.readLine()) != null) 
    		xml.append(inputLine);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml.toString()));
        return builder.parse(is);
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
    
    public static void signRequest(HttpGet request) throws OAuthException, OAuthExpectationFailedException, OAuthCommunicationException
    {
    	OAuthConsumer consumer = new CommonsHttpOAuthConsumer(consumerKey, sharedSecret);
    	consumer.sign(request);
    }
    
    public static String requestAuthUrl(SharedPreferences prefs) throws OAuthMessageSignerException, OAuthNotAuthorizedException, OAuthExpectationFailedException, OAuthCommunicationException
    {
    	SharedPreferences.Editor editor = prefs.edit();
    	OAuthProvider provider = new CommonsHttpOAuthProvider(NETFLIX_REQUEST_TOKEN_URL, NETFLIX_ACCESS_TOKEN_URL, NETFLIX_AUTHORIZE_URL);
    	OAuthConsumer consumer = new CommonsHttpOAuthConsumer(consumerKey, sharedSecret);
    	String authUrl = provider.retrieveRequestToken(consumer, CALLBACK_URI.toString());
    	editor.putString("request_key_secret", consumer.getTokenSecret());
    	editor.commit();
    	authUrl = OAuth.addQueryParameters(authUrl, OAuth.OAUTH_CONSUMER_KEY, consumerKey,
                "application_name", APPLICATION_NAME);	
    	return authUrl;
    }
    
    public static void setupAccessTokens(String oauth_token, SharedPreferences prefs) throws OAuthMessageSignerException, OAuthNotAuthorizedException, OAuthExpectationFailedException, OAuthCommunicationException
    {
    	OAuthConsumer consumer = new CommonsHttpOAuthConsumer(consumerKey, sharedSecret);
    	consumer.setTokenWithSecret(prefs.getString("request_key", ""), prefs.getString("request_key_secret", ""));
    	OAuthProvider provider = new CommonsHttpOAuthProvider(NETFLIX_REQUEST_TOKEN_URL, NETFLIX_ACCESS_TOKEN_URL, NETFLIX_AUTHORIZE_URL);
    	provider.retrieveAccessToken(consumer, oauth_token);
    	saveUserKeys(prefs, consumer.getToken(), consumer.getTokenSecret(), provider.getResponseParameters().get("user_id").first());
    }
    
    
}
