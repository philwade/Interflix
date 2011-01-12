package org.philwade.android.interflix;


import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthException;
import oauth.signpost.exception.OAuthExpectationFailedException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.SharedPreferences;

public class NetflixQueRetriever extends NetflixDataRetriever {
	
	
	public NetflixQueRetriever(SharedPreferences prefs)
	{
		super(prefs);
	}
	
	public NetflixTitle[] getInstantQue() throws OAuthExpectationFailedException, OAuthCommunicationException, ParserConfigurationException, SAXException, IOException, OAuthException
	{
		return getInstantQue(0);
	}
	
	public NetflixTitle[] getInstantQue(int offset) throws OAuthExpectationFailedException, OAuthCommunicationException, ParserConfigurationException, SAXException, IOException, OAuthException
	{
		return getQue("/queues/instant", offset);
	}
	
	public NetflixTitle[] getDiscQue() throws OAuthExpectationFailedException, OAuthCommunicationException, ParserConfigurationException, SAXException, IOException, OAuthException
	{
		return getDiscQue(0);
	}
	public NetflixTitle[] getDiscQue(int offset) throws OAuthExpectationFailedException, OAuthCommunicationException, ParserConfigurationException, SAXException, IOException, OAuthException
	{
		return getQue("/queues/disc", offset);
	}
	
	public NetflixTitle[] getQue(String uri) throws OAuthExpectationFailedException, OAuthCommunicationException, ParserConfigurationException, SAXException, IOException, OAuthException
	{
		return getQue(uri, 0);
	}
	
	public NetflixTitle[] getQue(String uri, int offset) throws OAuthExpectationFailedException, OAuthCommunicationException, ParserConfigurationException, SAXException, IOException, OAuthException
	{
		String url = "http://api.netflix.com/users/" + userId + uri + "?max_results=" + OFFSET_INCREMENT + "&start_index=" + offset;
		Document xml = fetchDocument(url);
		NodeList etagList = xml.getElementsByTagName("etag");
		String etag = etagList.item(0).getChildNodes().item(0).getNodeValue();
		saveEtag(etag);
		setResultsLength(xml);
		NodeList titleNodes = xml.getElementsByTagName("queue_item");
		NetflixTitle[] results = constructTitleObjects(titleNodes);
		return results;
	}

}
