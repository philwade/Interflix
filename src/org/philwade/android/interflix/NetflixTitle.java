package org.philwade.android.interflix;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

public class NetflixTitle {

	public String title;
	public String coverArt;
	public String synopsis;
	public String idUrl;
	public boolean instant = false;
	public boolean disc = true;
	public boolean inDVDQ = false;
	public boolean inInstantQ = true;
	public int id;
	
	public NetflixTitle(Document rootElement)
	{
		this((Element) rootElement.getElementsByTagName("catalog_title").item(0));
	}
	public NetflixTitle(Element titleElement)
	{
		//TODO: push these assignments into functions so we can handle exceptions better
		NodeList titles = titleElement.getElementsByTagName("title");
		NamedNodeMap titleAttributes = titles.item(0).getAttributes();
		title = titleAttributes.getNamedItem("short").getNodeValue();
		
		NodeList synopsees = titleElement.getElementsByTagName("synopsis");
		try
		{
			synopsis = synopsees.item(0).getChildNodes().item(0).getNodeValue();
		}catch(NullPointerException e){
			//getElementsBytagname is trickseee
			synopsis = null;
		}
		NodeList art = titleElement.getElementsByTagName("box_art");
		Element artEl = (Element) art.item(0);
		coverArt = artEl.getAttribute("large");
		if(titleElement.getTagName().equals(new String("queue_item")))
		{
			NodeList links = titleElement.getElementsByTagName("link");
			idUrl = extractFromLinks(links, "http://schemas.netflix.com/catalog/title", "rel");
		}
		else
		{
			NodeList id = titleElement.getElementsByTagName("id");
			idUrl = id.item(0).getChildNodes().item(0).getNodeValue();
		}
	}
	
	public String toString()
	{
		return title;
	}
	
	public String extractFromLinks(NodeList links, String attributeValue, String attributeName)
	{
		final String toMatch = new String(attributeValue);
		int length = links.getLength();
		for(int i = 0; i < length;i++)
		{
			Element el = (Element) links.item(i);
			//all link nodes should have a title attribute
			String textLinkTitle = el.getAttribute(attributeName);
			
			if(textLinkTitle.equals(toMatch))
			{
				String value = el.getAttribute("href");
				return value;
			}
			
		}
		return null;
	}
	
	public boolean instantAvailable()
	{
		return instant;
	}
	
	public boolean discAvailable()
	{
		return disc;
	}
	
	public boolean inDVDQ()
	{
		return inDVDQ;
	}
	
	public boolean inInstantQ()
	{
		return inInstantQ;
	}
}
