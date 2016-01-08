package model;

import java.util.ArrayList;
import java.util.HashMap;

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

	private String qid="";
	private String uid="";
	private String type="";
	private String title="";
	private String description="";
	private String narrative="";
	private String concept_mapping_string="";

	private HashMap<String, Double> weightedQuery = new HashMap<String, Double>();
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

	public HashMap<String, Double> getWeightedQuery() {
		return weightedQuery;
	}

	public void setWeightedQuery(HashMap<String, Double> weightedQuery) {
		this.weightedQuery = weightedQuery;
	}

	public void addWeightedQuery(String id, Double weight) {
		weightedQuery.put(id, weight);
	}

	public String getConcept_mapping_string() {
		return concept_mapping_string;
	}

	public void setConcept_mapping_string(String concept_mapping_string) {
		this.concept_mapping_string = concept_mapping_string;
	}

}
