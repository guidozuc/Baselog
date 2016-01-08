package util;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 
 */

/**
 * @author zuccong
 *
 */
public class LifeloggingHandler extends DefaultHandler{
	boolean users = false;
	boolean user = false;
	boolean minutes = false;
	boolean minute = false;
	boolean location = false;
	
	
	public void startElement(String uri, String localName,String qName, 
			Attributes attributes) throws SAXException {

		System.out.println("Start Element :" + qName);

		if (qName.equalsIgnoreCase("users")) {
			this.users = true;
		}

		if (qName.equalsIgnoreCase("user")) {
			this.user = true;
		}

		if (qName.equalsIgnoreCase("minutes")) {
			this.minutes = true;
		}

		if (qName.equalsIgnoreCase("minute")) {
			this.minute = true;
		}
		if (qName.equalsIgnoreCase("location")) {
			this.location = true;
		}
	}
	
	public void characters(char ch[], int start, int length) throws SAXException {

		if (users) {
			System.out.println("users : " + new String(ch, start, length));
			this.users = false;
		}

		if (user) {
			System.out.println("user : " + new String(ch, start, length));
			this.user = false;
		}

		if (minutes) {
			System.out.println("minutes : " + new String(ch, start, length));
			this.minutes = false;
		}

		if (minute) {
			System.out.println("minute : " + new String(ch, start, length));
			this.minute = false;
		}

		if (location) {
			System.out.println("location : " + new String(ch, start, length));
			this.location = false;
		}
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {
			System.out.println("End Element :" + qName);
	}
	
	
	/**
	 * 
	 */
	public LifeloggingHandler() {
		// TODO Auto-generated constructor stub
	}

}
