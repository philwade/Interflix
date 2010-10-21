package org.philwade.android.interflix;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthException;
import oauth.signpost.exception.OAuthExpectationFailedException;

import org.apache.http.client.ClientProtocolException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class NetflixTitle {

	public String title;
	public String coverArt;
	public String synopsis;
	public String idUrl;
	public boolean instant = false;
	public boolean disc = false;
	public boolean inDVDQ = false;
	public boolean inInstantQ = false;
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
		NodeList formats = titleElement.getElementsByTagName("delivery_formats");
		
		checkAvailablilty(formats);
		
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
	
	public boolean addToInstantQue(NetflixDataRetriever retriever)
	{
		if(!inInstantQ() && instantAvailable())
		{
			try {
				retriever.addToInstantQue(idUrl);
				return true;
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OAuthExpectationFailedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OAuthCommunicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OAuthException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}
		return false;
	}
	
	public boolean addToDVDQue(NetflixDataRetriever retriever)
	{
		if(!inDVDQ() && discAvailable())
		{
			try {
				retriever.addToDVDQue(idUrl);
				return true;
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OAuthExpectationFailedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OAuthCommunicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OAuthException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
	
	private void availabiltyEquals(Element el)
	{
		String label = el.getAttribute("label");
		if(label.equals("DVD"))
		{
			this.disc = true;
		}
		if(label.equals("instant"))
		{
			this.instant = true;
		}
	}
	private void checkAvailablilty(NodeList formats)
	{
		try
		{
			Element avail1 = (Element) formats.item(0).getChildNodes().item(0).getChildNodes().item(0);
			availabiltyEquals(avail1);
		}catch(NullPointerException e){
		}
		try
		{
			Element avail2 = (Element) formats.item(0).getChildNodes().item(1).getChildNodes().item(0);
			availabiltyEquals(avail2);
		}catch(NullPointerException e){
		}	
	}
}
