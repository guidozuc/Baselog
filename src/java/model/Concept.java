package model;

import java.util.Vector;

/**
 * 
 */

/**
 * @author zuccong
 *
 */
public class Concept {

	private String conceptid;
	private String description;
	private Vector<String> keywords;
	double score;
	double idf;

	public Concept(){
		conceptid="";
		description="";
		keywords=new Vector<String>();
	}

	public Concept(String conceptid, String description, Vector<String> keywords){
		this.conceptid = conceptid;
		this.description = description;
		this.keywords=keywords;
	}

	public Concept(String conceptid, String description, Vector<String> keywords, double score){
		this.conceptid = conceptid;
		this.description = description;
		this.keywords=keywords;
		this.score=score;
	}

	/*
	 * this parses keyword phrases like "great white shark,white shark,man-eater,man-eating shark,Carcharodon carcharias"
	 *
	 * */
	public Concept(String conceptid, String description, String keywordsPhrases){
		this.conceptid = conceptid;
		this.description = description;
		this.keywords=new Vector<String>();
		String[] fields = keywordsPhrases.split(",");
		//this doesn't take into account ngrams
		//TODO: take into account ngrams for keywords
		for(int i=0; i<fields.length;i++) {
			String aPhrase = fields[i];
			String[] keywordsInPhrase = aPhrase.split(" ");
			for(int j=0; j<keywordsInPhrase.length;j++)
				this.keywords.add(keywordsInPhrase[j].toLowerCase());
		}
	}

	/**
	 * @return the conceptid
	 */
	public String getConceptid() {
		return conceptid;
	}


	/**
	 * @param conceptid the conceptid to set
	 */
	public void setConceptid(String conceptid) {
		this.conceptid = conceptid;
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
	 * @return the keywords
	 */
	public Vector<String> getKeywords() {
		return keywords;
	}

	/**
	 * @param keywords the keywords to set
	 */
	public void setKeywords(Vector<String> keywords) {
		this.keywords = keywords;
	}

	/**
	 * @return the score
	 */
	public double getScore() {
		return score;
	}

	/**
	 * @param score the score to set
	 */
	public void setScore(double score) {
		this.score = score;
	}

	public double getIdf() {
		return idf;
	}

	public void setIdf(double idf) {
		this.idf = idf;
	}

}
