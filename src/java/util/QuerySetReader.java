package util;

import java.io.File;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import model.Query;
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
public class QuerySetReader {

	private HashMap<String, Query> querySet = new HashMap<String, Query>();

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

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;
					Query query = new Query();

					// top-level node elements
					query.setQid(eElement.getElementsByTagName("id").item(0).getTextContent());
					query.setUid(eElement.getElementsByTagName("uid").item(0).getTextContent());
					query.setType(eElement.getElementsByTagName("type").item(0).getTextContent());
					query.setTitle(eElement.getElementsByTagName("title").item(0).getTextContent());
					query.setDescription(eElement.getElementsByTagName("description").item(0).getTextContent());
					query.setNarrative(eElement.getElementsByTagName("narrative").item(0).getTextContent());

					// concept nodes
					NodeList mapping = eElement.getElementsByTagName("mapping").item(0).getChildNodes();
					for (int i = 0; i < mapping.getLength(); i++) {
						Node map = mapping.item(i);
						if (map.getNodeType() == Node.ELEMENT_NODE) {
							String name = map.getAttributes().getNamedItem("id").getNodeValue();
							Double weight = Double.parseDouble(map.getAttributes().getNamedItem("weight").getNodeValue());
							query.addWeightedQuery(name, weight);
						}
					}

					this.querySet.put(query.getQid(), query);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public HashMap<String, Query> getQuerySet() {
		return querySet;
	}

	public void setQuerySet(HashMap<String, Query> querySet) {
		this.querySet = querySet;
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		QuerySetReader queryset = new QuerySetReader();
		queryset.readQueryFile("/Users/zuccong/data/ntcir2015_lifelogging/lifeloggin_topics_dryrun.txt");
		for (String qid : queryset.querySet.keySet()) {
			Query query = queryset.querySet.get(qid);
			System.out.println("qid = " + qid);
			for(String concept : query.getWeightedQuery().keySet()) {
				System.out.println(concept + " -> " + query.getWeightedQuery().get(concept));
			}
		}
	}

}
