package org.philwade.android.interflix;

import java.io.IOException;

import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthException;
import oauth.signpost.exception.OAuthExpectationFailedException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import android.content.SharedPreferences;


public class NetflixSearchRetriever extends NetflixDataRetriever {
	
	public NetflixSearchRetriever(SharedPreferences prefs) throws IOException
	{
		super(prefs);
	}
	
	public NetflixTitle[] getSearchTitles(String searchString) throws Exception
	{
		String url = "http://api.netflix.com/catalog/titles?term=" + searchString + "&max_results=25"; 
		Document xml = fetchDocument(url);
		NodeList titleNodes = xml.getElementsByTagName("catalog_title");
		NetflixTitle[] results = constructTitleObjects(titleNodes);
		return results;
	}
	
	public String[] searchPeople(String term) throws OAuthExpectationFailedException, OAuthCommunicationException, OAuthException
	{
		String url = "http://api.netflix.com/catalog/people?term=" + term + "&max_results=25";
		try {
			Document xml = fetchDocument(url);
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
