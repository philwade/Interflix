package org.philwade.android.interflix;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import oauth.signpost.OAuth;
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
    	//use prefs to populate consumer correctly here...
    }
    
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
    
    public void signRequest(HttpGet request) throws OAuthException, OAuthExpectationFailedException, OAuthCommunicationException
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
