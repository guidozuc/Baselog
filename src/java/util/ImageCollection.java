package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.google.common.collect.ListMultimap;
import dao.TimelineDao;
import model.Concept;
import model.Image;

/**
 * 
 */

/**
 * @author zuccong
 *
 */
public class ImageCollection {

	HashMap<String, Image> collection = new HashMap<String, Image>(); //<imageURL, image_object>
	ConceptVocabulary conceptVocabulary;
	InvertedImageIndex index;
	/**
	 * @return the conceptVocabulary
	 */
	public ConceptVocabulary getConceptVocabulary() {
		return conceptVocabulary;
	}

	/**
	 * @param conceptVocabulary the conceptVocabulary to set
	 */
	public void setConceptVocabulary(ConceptVocabulary conceptVocabulary) {
		this.conceptVocabulary = conceptVocabulary;
	}

	/**
	 * @param filepath the path to the file that contains the set of images and their distribution over concepts.
	 * @throws IOException 
	 */
	public void readCollection(String filepath) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filepath));
		Vector<String> conceptIds = new Vector<String>(); //the indexes of the vector start from 0
		//to avoid having to subtract when reading the file around we add a dummy at position 0
		conceptIds.add(0, "dummy");
		try {
			String line = br.readLine();
			String[] header = line.split(",");
			//we assume that at header[i] there is the label of the file path
			for(int i=1; i<header.length;i++) {
				conceptIds.add(header[i]);
			}
			line = br.readLine();
			//the header has been read, now fill in the image collection
			while (line != null) {
				String[] fields = line.split(",");
				//note that if there is a java.lang.ArrayIndexOutOfBoundsException exception raised here, this may because of the incorrect field separator, e.g. tab instead of ";"
				//TODO: should make separators dynamic to support any type of separator
				String imageURL = fields[0].trim();
				Image image = new Image(imageURL);
				for(int i=1; i<fields.length;i++) {
					String conceptid = conceptIds.get(i);
					//if(Double.parseDouble(fields[i])>0.0) {
					Concept thisConcept = new Concept(conceptid, this.conceptVocabulary.getConceptDescription(conceptid),
						this.conceptVocabulary.getConceptkeywords(conceptid), Double.parseDouble(fields[i]));
						image.addConcept(thisConcept);
					//}
					
				}
				this.addImage(imageURL, image);
				line = br.readLine();
			}	
		} finally {
			br.close();
		}
	}
	
	public void addImage(String imageID, Image anImage) {
		this.collection.put(imageID, anImage);
	}
	
	public String getImage(String imageID) {
		Image theImage = this.collection.get(imageID);
		return theImage.toString();
	}
	
	public Image getImageObject(String imageID) {
		Image theImage = this.collection.get(imageID);
		return theImage;
	}
	
	/**
	 * 
	 * This method prints a ranking of results. Printing happens in the format "imageid score"
	 * 
	 * @param conceptResultsArray a multimap contained an ordered list of results
	 */
	public void printResults(ListMultimap<Double, Image> conceptResultsArray) {
		for (Double score : conceptResultsArray.keySet()) {
			List<Image> images = conceptResultsArray.get(score);
			 Iterator<Image> iterator = images.iterator();
			while(iterator.hasNext())
				System.out.println(iterator.next().getImageURL() + " " + score);
		}
		return;
	}

}
