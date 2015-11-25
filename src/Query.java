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


/*
 * A query topic looks like this:
 * 
 * <topic>
	<id>001</id>
<type>precision</type>
	<uid>u1</uid>
<title>Reading Papers</title>
	<description>Find the moment(s) when I am reading a printed paper document.</description>
<narrative>Moments which show the user reading printed paper documents are relevant. Moments in which papers appear on a desk, but not being read, are not relevant. Moments in which the user is carrying papers are not relevant. Moments in which the user is reading shopping receipts are not relevant.</narrative>
</topic>
<topic>
 * 
 * */

public class Query {
	String qid="";
	String uid="";
	String type="";
	String title="";
	String description="";
	String narrative="";
	String concept_mapping_string="";
	
	HashMap<String, Double> weightedQuery = new HashMap<String, Double>();
	
	/**
	 * @return the qid
	 */
	public String getQid() {
		return qid;
	}
	/**
	 * @param qid the qid to set
	 */
	public void setQid(String qid) {
		this.qid = qid;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the narrative
	 */
	public String getNarrative() {
		return narrative;
	}
	/**
	 * @param narrative the narrative to set
	 */
	public void setNarrative(String narrative) {
		this.narrative = narrative;
	}
	/**
	 * @return the concept_maping
	 */
	public String getConcept_maping() {
		return concept_mapping_string;
	}
	/**
	 * @param concept_maping the concept_maping to set
	 */
	public void setConcept_maping(String concept_maping) {
		this.concept_mapping_string = concept_maping;
	}

	

	/**
	 * @return the uid
	 */
	public String getUid() {
		return uid;
	}
	/**
	 * @param uid the uid to set
	 */
	public void setUid(String uid) {
		this.uid = uid;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Populates a weighted query structure, but all weights are equal
	 * @param concept_string a string containing a sequence of concepts
	 */
	public void formWeightedQuery_unbiased(String concept_string) {
		if(concept_string.length()>0) {
			String[] concepts = concept_string.split(" ");
			for(int i=0; i<concepts.length;i++)
				this.weightedQuery.put(concepts[i], 1.0);
		}else
			System.err.println("Empty concept translation for query " + this.qid + " - the query will probably not retrieve anything");
	}
	
	public static void main(String[] args) {
		QuerySet queryset = new QuerySet();
		queryset.readQueryFile("/Users/zuccong/data/ntcir2015_lifelogging/lifeloggin_topics_dryrun.txt");

	}

}
