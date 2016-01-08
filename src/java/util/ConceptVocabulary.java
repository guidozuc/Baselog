package util;

import model.Concept;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

/**
 * 
 */

/**
 * @author harryscells
 *
 */
public class ConceptVocabulary {

	HashMap<String, Concept> vocabulary = new HashMap<String, Concept>(); //<conceptid, concept_object>
	
	public ConceptVocabulary(){
		this.vocabulary = new HashMap<String, Concept>();
	}
	
	public ConceptVocabulary(HashMap<String, Concept> vocabulary){
		this.vocabulary = vocabulary;
	}
	
	
	/**
	 * @return the vocabulary
	 */
	public HashMap<String, Concept> getVocabulary() {
		return vocabulary;
	}

	/**
	 * @param vocabulary the vocabulary to set
	 */
	public void setVocabulary(HashMap<String, Concept> vocabulary) {
		this.vocabulary = vocabulary;
	}
	
	
	/**
	 * @param filepath the path to the file that contains the vocabulary of concept. The vocabulary of concepts needs to be structured with one concept per file and be in the form "c_3;great white shark,white shark,man-eater,man-eating shark,Carcharodon carcharias"
	 * @throws IOException 
	 */
	public void readVocabulary(String filepath) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filepath));
		try {
			String line = br.readLine();
			while (line != null) {
				String[] fields = line.split(";");
				//note that if there is a java.lang.ArrayIndexOutOfBoundsException exception raised here, this may because of the incorrect field separator, e.g. tab instead of ";"
				//TODO: should make separators dynamic to support any type of separator
				String conceptid = fields[0].trim();
				String keywordsPhrases = fields[1].trim();
				Concept thisConcept = new Concept(conceptid, keywordsPhrases, keywordsPhrases);
				vocabulary.put(conceptid, thisConcept);
				line = br.readLine();
			}	
		} finally {
			br.close();
		}
	}
	
	/**
	 * Finds the description for a given concept id
	 * 
	 * @param conceptid the conceptid that we want to retrieve the description for
	 * @param print whether we want to print the retrieved description on console
	 * 
	 * @return a string containing the full description of the concept
	 * 
	 * @throws IllegalArgumentException 
	 */
	public String getConceptDescription(String conceptid, boolean print) {
		if(!this.vocabulary.containsKey(conceptid)) 
			throw new IllegalArgumentException("model.Concept id not found in the vocabulary for id " + conceptid);
		else {
			Concept aConcept = this.vocabulary.get(conceptid);
			String description = aConcept.getDescription();
			if(print)
				System.out.println("description for concept " + conceptid + ": " + description);
			return description;
		}
	}
	
	/**
	 * Finds the description for a given concept id. Like the method getConceptDescription(String conceptid, boolean print) but it does not print the found description on console
	 * 
	 * @param conceptid the conceptid that we want to retrieve the description for
	 * 
	 * @return a string containing the full description of the concept
	 * 
	 * @throws IllegalArgumentException 
	 */
	public String getConceptDescription(String conceptid) {
		return getConceptDescription(conceptid, false);
	}
	
	/**
	 * Finds the keywords for a given concept id
	 * 
	 * @param conceptid the conceptid that we want to retrieve the keywords for
	 * @param print whether we want to print the retrieved keywords on console
	 * 
	 * @return a vector containing the keywords associated to the concept
	 * 
	 * @throws IllegalArgumentException 
	 */
	public Vector<String> getConceptkeywords(String conceptid, boolean print) {
		if(!this.vocabulary.containsKey(conceptid)) 
			throw new IllegalArgumentException("model.Concept id not found in the vocabulary for id " + conceptid);
		else {
			Concept aConcept = this.vocabulary.get(conceptid);
			Vector<String> conceptKeywords = aConcept.getKeywords();
			if(print)
				System.out.println("Keywords for concept " + conceptid + ": " + conceptKeywords.toString());
			return conceptKeywords;
		}
	}
	

	/**
	 * Finds the keywords for a given concept id. Like the method getConceptkeywords(String conceptid, boolean print) but it does not print the found keywords on console
	 * 
	 * @param conceptid the conceptid that we want to retrieve the keywords for
	 * 
	 * @return a vector containing the keywords associated to the concept
	 * 
	 * @throws IllegalArgumentException 
	 */
	public Vector<String> getConceptkeywords(String conceptid) {
		return getConceptkeywords(conceptid, false);
	}
	
	
	/**
	 * 
	 * main method for test only
	 * 
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String filepath = "/Users/harryscells/data/ntcir2015_lifelogging/Caffe_concepts_list.txt";
		ConceptVocabulary cv = new ConceptVocabulary();
		cv.readVocabulary(filepath);
		cv.getConceptDescription("c_10", true);
		cv.getConceptkeywords("c_10", true);
	}
	
}
