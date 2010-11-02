package org.philwade.android.interflix;


import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import android.content.SharedPreferences;

public class NetflixQueRetriever extends NetflixDataRetriever {
	
	
	public NetflixQueRetriever(SharedPreferences prefs)
	{
		super(prefs);
	}
	
	public NetflixTitle[] getInstantQue() throws Exception
	{
		return getInstantQue(0);
	}
	
	public NetflixTitle[] getInstantQue(int offset) throws Exception
	{
		return getQue("/queues/instant", offset);
	}
	
	public NetflixTitle[] getDiscQue() throws Exception
	{
		return getDiscQue(0);
	}
	public NetflixTitle[] getDiscQue(int offset) throws Exception
	{
		return getQue("/queues/disc", offset);
	}
	
	public NetflixTitle[] getQue(String uri) throws Exception
	{
		return getQue(uri, 0);
	}
	
	public NetflixTitle[] getQue(String uri, int offset) throws Exception
	{
		String url = "http://api.netflix.com/users/" + userId + uri + "?max_results=" + OFFSET_INCREMENT + "&start_index=" + offset;
		Document xml = fetchDocument(url);
		NodeList etagList = xml.getElementsByTagName("etag");
		String etag = etagList.item(0).getChildNodes().item(0).getNodeValue();
		saveEtag(etag);
		NodeList titleNodes = xml.getElementsByTagName("queue_item");
		NetflixTitle[] results = constructTitleObjects(titleNodes);
		return results;
	}
	
}
