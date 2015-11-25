import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.SortedSetMultimap;

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
	
	
	/**
	 * 
	 * main method for test only
	 * 
	 * @param args
	 * @throws IOException 
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
		
		String filepath_concept_list = "/Users/zuccong/data/ntcir2015_lifelogging/Caffe_concepts_list.txt";
		ConceptVocabulary cv = new ConceptVocabulary();
		cv.readVocabulary(filepath_concept_list);
		ImageCollection iCollection = new ImageCollection();
		iCollection.setConceptVocabulary(cv);
		String filepath="/Users/zuccong/data/ntcir2015_lifelogging/NTCIR_Lifelog_Dryrun_Dataset/NTCIR-Lifelog_Dryrun_Concepts.txt";
		iCollection.readCollection(filepath);
		/*
		System.err.println("Find a specific image");
		System.out.println(iCollection.getImage("/u1/2015-02-18/b00000126_21i6bq_20150218_075247e.jpg"));
		
		System.err.println("\nIterate through all images in the collection");
		Iterator it = iCollection.collection.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        Image im =  (Image) pair.getValue();
	        System.out.println(pair.getKey() + " -> " + im.getImageURL() + " - " + im.getConceptVector().toString());
	    }
	    */
	    //creating the inverted indexes
	    System.out.println("Start indexing");
	    InvertedImageIndex index = new InvertedImageIndex(iCollection);
	    System.out.println("... indexing finished");
	    
	    System.out.println("Setting up a timeline");
	    Timeline timeline = new Timeline();
		timeline.init();
		timeline.loadTimeline("/Users/zuccong/data/ntcir2015_lifelogging/NTCIR_Lifelog_Dryrun_Dataset/NTCIR-Lifelog_Dryrun_dataset.xml");
	    System.out.println("... timeline finished");
	    
	    
	    Iterator<Image> iterator = null;
	    /*String keyword = "coffee";
	    System.out.println("Finding images with keyword " + keyword);
	    List<Image> results = index.findImagebyKeyword(keyword);
	    iterator = results.iterator();
		while (iterator.hasNext()) {
			System.out.println(iterator.next().getImageURL());
		}*/
		
		/*String concept = "c_505";
		System.out.println("Finding images with concept " + concept);
		ListMultimap<Double, Image> conceptResults = index.findImagebyConcept(concept, iCollection);
		for (Double score : conceptResults.keySet()) {
			List<Image> images = conceptResults.get(score);
			iterator = images.iterator();
			while(iterator.hasNext())
				System.out.println(iterator.next().getImageURL() + " " + score);
		}*/
		
		/*String[] array = {"c_505", "c_506"};
		Vector<String> query = new Vector(Arrays.asList(array)); 
		System.out.println("Finding images with concepts " + query.toString());
		ListMultimap<Double, Image> conceptResultsArray = index.findImagebyConcept(query, iCollection);
		for (Double score : conceptResultsArray.keySet()) {
			List<Image> images = conceptResultsArray.get(score);
			iterator = images.iterator();
			while(iterator.hasNext())
				System.out.println(iterator.next().getImageURL() + " " + score);
		}*/
		
		HashMap<String, Double> weightedQuery = new HashMap<String, Double>();
		weightedQuery.put("c_505", 1.0);
		weightedQuery.put("c_506", 1.0);
		System.out.println("Finding images with concepts " + weightedQuery.toString());
		ListMultimap<Double, Image> conceptResultsArray = index.findImagebyConcept(weightedQuery, iCollection);
		List<String> resultdump = new ArrayList<String>(); //this is a very dirty way of dealing with the reversion of the conceptResultsArray
		for (Double score : conceptResultsArray.keySet()) {
			List<Image> images = conceptResultsArray.get(score);
			iterator = images.iterator();
			ReverseIterator<Image> riterator = new ReverseIterator<Image>(images);
			while(riterator.hasNext()) {
				Image theNextImage = riterator.next();
				String line = timeline.getDate(theNextImage) + " " + timeline.getMinute(theNextImage) + " " +  theNextImage.getImageURL() + " " + score;
				//System.out.println(timeline.getDate(theNextImage) + " " + timeline.getMinute(theNextImage) + " " +  theNextImage.getImageURL() + " " + score);
				resultdump.add(line);
			}	
		}
		/*The following reverses the list of results to iterate from the one with highest score to that with the lowest*/
		//TODO: this could be removed if scores were recorded as negative scores.
		ReverseIterator<String> reversedList = new ReverseIterator<String>(resultdump);
		Iterator<String> rit = reversedList.iterator();
		while(rit.hasNext()){
			System.out.println(rit.next());
		}
	}

}
