import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;



/**
 * 
 */

/**
 * @author zuccong
 *
 */
public class TimelineXPath {

	
	/**
	 * @param args
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws XPathExpressionException 
	 */
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, XPathException  {
		/*SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setValidating(false); 
		factory.setFeature("http://xml.org/sax/features/namespaces", false);
		factory.setFeature("http://xml.org/sax/features/validation", false);
		factory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
		factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		SAXParser saxParser = factory.newSAXParser();

		util.LifeloggingHandler handler = new util.LifeloggingHandler();

		saxParser.parse("/Users/zuccong/data/ntcir2015_lifelogging/NTCIR_Lifelog_Dryrun_Dataset/NTCIR-Lifelog_Dryrun_dataset.xml", handler);
	*/
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true); // never forget this!
        factory.setValidating(false); 
		factory.setFeature("http://xml.org/sax/features/namespaces", false);
		factory.setFeature("http://xml.org/sax/features/validation", false);
		factory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
		factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse("/Users/zuccong/data/ntcir2015_lifelogging/NTCIR_Lifelog_Dryrun_Dataset/NTCIR-Lifelog_Dryrun_dataset.xml");
        XPathFactory xpathfactory = XPathFactory.newInstance();
        XPath xpath = xpathfactory.newXPath();
 
        System.out.println("n//1) Get book titles written after 2001");
        // 1) Get book titles written after 2001
        XPathExpression expr = xpath.compile("//user/days/day/minutes/minute/images/image[starts-with(image-path,'/u1/2015-02-18/b00001494_21i6bq_20150218_230009e')]/image-path/text()"); //"//user/days/day/images-directory/text()"
        
        // //user/days/day/minutes/minute[@id<5]/location/text()
        Object result = expr.evaluate(doc, XPathConstants.NODESET);
        NodeList nodes = (NodeList) result;
        for (int i = 0; i < nodes.getLength(); i++) {
            System.out.println(nodes.item(i).getNodeValue());
        }
	}

}
