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
	public boolean inDVDQ;
	public boolean inInstantQ;
	public boolean queStatusChecked = false;
	public String id;
	
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
		id = titleElement.getElementsByTagName("id").item(0).getChildNodes().item(0).getNodeValue();	
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
	
	private void setQueStatusFromCategory(Element category, Element statusValue)
	{
		String itemType = category.getAttribute("label");
		
		if(statusValue == null)
		{
			//find out type and say we got nothin
			if(itemType.equals("DVD"))
			{
				inDVDQ = false;
			}
			if(itemType.equals("Instant"))
			{
				inInstantQ = false;
			}
		}
		else
		{
			if(statusValue.getAttribute("label").equals("In Queue"))
			{
				if(itemType.equals("DVD"))
				{
					inDVDQ = true;
				}
				if(itemType.equals("Instant"))
				{
					inInstantQ = true;
				}
			}
		}
	}
	private void checkQueueStatus(NetflixDataRetriever retriever)
	{
		try {
			Document d = retriever.getTitleState(idUrl);
			NodeList statuses = d.getElementsByTagName("format");
			int statusLength = statuses.getLength();
			
			for(int i = 0; i < statusLength;i++)
			{
				Element status = (Element) statuses.item(i);
				Element category = (Element) status.getElementsByTagName("category").item(0); //get first element, check on sibilings
				Element categoryStatus = (Element) status.getElementsByTagName("category").item(1); //get first element, check on sibilings
				setQueStatusFromCategory(category, categoryStatus);
			}
			queStatusChecked = true;
		} catch (OAuthExpectationFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public boolean instantAvailable()
	{
		return instant;
	}
	
	public boolean discAvailable()
	{
		return disc;
	}
	
	public boolean inDVDQ(NetflixDataRetriever retriever)
	{
		if(queStatusChecked == false)
		{
			checkQueueStatus(retriever);
		}
		return inDVDQ;
	}
	
	public boolean inInstantQ(NetflixDataRetriever retriever)
	{
		if(queStatusChecked == false)
		{
			checkQueueStatus(retriever);
		}
		return inInstantQ;
	}
	
	public boolean addToInstantQue(NetflixDataRetriever retriever)
	{
		if(!inInstantQ(retriever) && instantAvailable())
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
		if(!inDVDQ(retriever) && discAvailable())
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
		Element parent = (Element) el.getParentNode();
		long avail_from = Integer.parseInt(parent.getAttribute("available_from"));
		long unixTime = System.currentTimeMillis() / 1000L;
		
		if(avail_from < unixTime)
		{
			if(label.equals("DVD"))
			{
				this.disc = true;
			}
			if(label.equals("instant"))
			{
				this.instant = true;
			}
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
	
	//que removal is done based on the id of an individual que item.
	public void removeFromQue(NetflixDataRetriever retriever)
	{
		try {
			retriever.removeFromQue(id);
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
}
