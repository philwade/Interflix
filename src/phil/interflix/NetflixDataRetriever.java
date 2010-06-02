package phil.interflix;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthException;
import oauth.signpost.exception.OAuthExpectationFailedException;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

//import oauth.signpost.OAuthConsumer;
//import oauth.signpost.basic.DefaultOAuthConsumer;

public class NetflixDataRetriever {
	
	static final String consumerKey = "zksyhhsj8uk85ckxpxurfw4v";
	static final String sharedSecret = "rAqtAeRYG";
	
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
    
    public static void signRequest(HttpGet request) throws OAuthException, OAuthExpectationFailedException, OAuthCommunicationException
    {
    	OAuthConsumer consumer = new CommonsHttpOAuthConsumer(consumerKey, sharedSecret);
    	consumer.sign(request);
    }
    
    
}
