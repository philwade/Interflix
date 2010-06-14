package org.philwade.android.interflix;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

public class NetflixTitle {

	public String title;
	public String coverArt;
	public String synopsis;
	public int id;
	private Element movieElement;
	
	public NetflixTitle(Element titleElement)
	{
		movieElement = titleElement;
		NodeList titles = titleElement.getElementsByTagName("title");
		NamedNodeMap titleAttributes = titles.item(0).getAttributes();
		title = titleAttributes.getNamedItem("short").getNodeValue();
		
	}
}
