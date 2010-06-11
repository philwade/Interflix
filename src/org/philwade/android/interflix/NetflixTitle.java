package org.philwade.android.interflix;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NetflixTitle {

	public String title;
	public String coverArt;
	public String synopsis;
	public int id;
	private Document movieDoc;
	
	public NetflixTitle(Node titleNode) throws ParserConfigurationException, TransformerException
	{
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer xf = tf.newTransformer();
		DOMResult dr = new DOMResult();
		xf.transform(new DOMSource(titleNode), dr);
		movieDoc = (Document) dr.getNode();

		NodeList titles = movieDoc.getElementsByTagName("title");
		NamedNodeMap titleAttributes = titles.item(0).getAttributes();
		title = titleAttributes.getNamedItem("short").getTextContent();
	}
}
