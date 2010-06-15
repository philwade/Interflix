package org.philwade.android.interflix;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

public class NetflixTitle {

	public String title;
	public String coverArt;
	public String synopsis;
	public int id;
	
	public NetflixTitle(Element titleElement)
	{
		NodeList titles = titleElement.getElementsByTagName("title");
		NamedNodeMap titleAttributes = titles.item(0).getAttributes();
		title = titleAttributes.getNamedItem("short").getNodeValue();
		
		NodeList links = titleElement.getElementsByTagName("link");
		handleLinks(links);
		NodeList art = titleElement.getElementsByTagName("box_art");
		Element artEl = (Element) art.item(0);
		coverArt = artEl.getAttribute("small");
		NodeList id = titleElement.getElementsByTagName("id");
	}
	
	public void handleLinks(NodeList links)
	{
		final String synopsisString = new String("synopsis");
		int length = links.getLength();
		for(int i = 0; i < length;i++)
		{
			Element el = (Element) links.item(i);
			//all link nodes should have a title attribute
			String textLinkTitle = el.getAttribute("title");
			
			if(textLinkTitle.equals(synopsisString))
			{
				synopsis = el.getAttribute("href");
				return;
			}
			
		}
	}
}
