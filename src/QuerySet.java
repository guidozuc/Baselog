import java.io.File;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 */

/**
 * @author zuccong
 *
 */
public class QuerySet {

	HashMap<String, Query> queryset = new HashMap<String, Query>();
	
	public void readQueryFile(String filepath) {
		try {

			File fXmlFile = new File(filepath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();

			System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

			NodeList nList = doc.getElementsByTagName("topic");

			System.out.println("----------------------------");

			for (int temp = 0; temp < nList.getLength(); temp++) {

				Node nNode = nList.item(temp);

				System.out.println("\nCurrent Element :" + nNode.getNodeName());

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;
					Query query = new Query();
					//System.out.println("id : " + eElement.getElementsByTagName("id").item(0).getTextContent());
					query.setQid(eElement.getElementsByTagName("id").item(0).getTextContent());
					//System.out.println("uid : " + eElement.getElementsByTagName("uid").item(0).getTextContent());
					query.setUid(eElement.getElementsByTagName("uid").item(0).getTextContent());
					//System.out.println("type : " + eElement.getElementsByTagName("type").item(0).getTextContent());
					query.setType(eElement.getElementsByTagName("type").item(0).getTextContent());
					//System.out.println("title : " + eElement.getElementsByTagName("title").item(0).getTextContent());
					query.setTitle(eElement.getElementsByTagName("title").item(0).getTextContent());
					//System.out.println("description : " + eElement.getElementsByTagName("description").item(0).getTextContent());
					query.setDescription(eElement.getElementsByTagName("description").item(0).getTextContent());
					//System.out.println("narrative : " + eElement.getElementsByTagName("narrative").item(0).getTextContent());
					query.setNarrative(eElement.getElementsByTagName("narrative").item(0).getTextContent());
					//System.out.println("mapping : " + eElement.getElementsByTagName("mapping").item(0).getTextContent());
					query.setConcept_maping(eElement.getElementsByTagName("mapping").item(0).getTextContent());
					
					query.formWeightedQuery_unbiased(query.getConcept_maping());
					this.queryset.put(query.getQid(), query);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		QuerySet queryset = new QuerySet();
		queryset.readQueryFile("/Users/zuccong/data/ntcir2015_lifelogging/lifeloggin_topics_dryrun.txt");
		for (String qid : queryset.queryset.keySet()) {
			Query query = queryset.queryset.get(qid);
			System.out.println("qid = " + qid);
			for(String concept : query.weightedQuery.keySet()) {
				System.out.println(concept + " -> " + query.weightedQuery.get(concept));
			}
		}
	}

}
