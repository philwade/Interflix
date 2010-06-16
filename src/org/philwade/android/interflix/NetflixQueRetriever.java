package org.philwade.android.interflix;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.content.SharedPreferences;

public class NetflixQueRetriever extends NetflixDataRetriever {
	
	
	public NetflixQueRetriever(SharedPreferences prefs)
	{
		super(prefs);
	}
	
	public NetflixTitle[] getInstantQue() throws Exception
	{
		return getQue("/queues/instant");
	}
	
	public NetflixTitle[] getDiscQue() throws Exception
	{
		return getQue("/queues/disc");
	}
	public NetflixTitle[] getQue(String uri) throws Exception
	{
		String url = "http://api.netflix.com/users/" + userId + uri;
		Document xml = fetchDocument(url);
		NodeList titleNodes = xml.getElementsByTagName("queue_item");
		NetflixTitle[] results = constructTitleObjects(titleNodes);
		return results;
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
	                    
}
