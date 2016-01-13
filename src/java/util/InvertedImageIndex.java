package util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import com.google.common.base.Supplier;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimaps;
import model.Concept;
import model.Image;

/**
 * 
 */

/**
 * @author zuccong
 *
 */
public class InvertedImageIndex {
	ListMultimap<String, Image> mainConceptIndex = ArrayListMultimap.create();
	ListMultimap<String, Image> mainKeywordIndex = ArrayListMultimap.create();
	
	public InvertedImageIndex(){}
	
	public InvertedImageIndex(ImageCollection iCollection){
		Iterator it = iCollection.collection.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        String imageURL = (String) pair.getKey();
	        Image im =  (Image) pair.getValue();
	        HashMap<String, Concept> cv = im.getConceptVector();
	        //now, iterate through the concept vector to index each keyword.
	        Iterator itCv = cv.entrySet().iterator();
	        while (itCv.hasNext()) {
	        	Map.Entry pairCv = (Map.Entry)itCv.next();
	        	String conceptid = (String) pairCv.getKey();
	        	Concept thisConcept = (Concept) pairCv.getValue();
	        	if(thisConcept.getScore()>0.0)
	        		this.putConceptImage(conceptid, im); // adds an image to the mainConceptIndex
	        	//now need to iterate through the thisConcept.keywords to be able to index the keywords
	        	Vector<String> someKeywords = thisConcept.getKeywords();
	        	Iterator itK = someKeywords.iterator();
	            while (itK.hasNext()){
	            	String akeyword = (String) itK.next();
	            	if(thisConcept.getScore()>0.0)
	            		this.putKeywordImage(akeyword, im);
	            }
	        }
	    }
	}
	
	public void putConceptImage(String aConceptid, Image anImage) {
		//if(mainConceptIndex.containsEntry(aConceptid, anImage))
		//	System.err.println("the index already contains this image for this keyword");
		mainConceptIndex.put(aConceptid, anImage);
	}
	
	public void putKeywordImage(String aKeyword, Image anImage) {
		//if(mainKeywordIndex.containsEntry(aKeyword, anImage))
		//	System.err.println("the index already contains this image for this keyword");
		mainKeywordIndex.put(aKeyword, anImage);
	}
	
	/**
	 * 
	 * This method allows to search the image collection by querying with a concept
	 * 
	 * @param aConcept a concept for which we want to find relevant images
	 * @param iCollection the collection of images used for retrieval (the same collection that has been indexed). Note that here the collection is used only for metadat information
	 * @return the ranking
	 */
	public  ListMultimap<Double, Image> findImagebyConcept(String aConcept, ImageCollection iCollection) {
		HashMap<String, Double> accumulator = new HashMap<String, Double>();
		List<Image> results = new Vector<Image>();
		results = mainConceptIndex.get(aConcept);
		Iterator<Image> iterator = results.iterator();
		while (iterator.hasNext()) {
			Image thisImage = iterator.next();
			Concept thisConcept = thisImage.getConceptVector().get(aConcept);
			double score = thisConcept.getScore();
			//System.out.println(thisImage.imageURL + " " + thisImage.conceptVector.toString() );
			if(accumulator.containsKey(thisImage.getImageURL()))
				score = score + accumulator.get(thisImage.getImageURL());
			accumulator.put(thisImage.getImageURL(), score);
		}
		//the object ranking contains <score, image> pairs
		ListMultimap<Double, Image> ranking = Multimaps.newListMultimap(
				  new TreeMap<Double, Collection<Image>>(),
				  new Supplier<List<Image>>() {
				    public List<Image> get() {
				      return Lists.newArrayList();
				    }
				  });
		Iterator it = accumulator.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        ranking.put((Double)pair.getValue(), iCollection.getImageObject((String)pair.getKey()));
	    }
		return ranking;
	}

	/**
	 * 
	 * This method allows to search the image collection by querying with a concept
	 * 
	 * @param conceptList a vector containing the list of concepts for which we want to find relevant images
	 * @param iCollection the collection of images used for retrieval (the same collection that has been indexed). Note that here the collection is used only for metadat information
	 * @return the ranking
	 */
	public  ListMultimap<Double, Image> findImagebyConcept(Vector<String> conceptList, ImageCollection iCollection) {
		ListMultimap<Double, Image> ranking = Multimaps.newListMultimap(
				  new TreeMap<Double, Collection<Image>>(),
				  new Supplier<List<Image>>() {
				    public List<Image> get() {
				      return Lists.newArrayList();
				    }
				  });
		Iterator<String> iterator = conceptList.iterator();
		while (iterator.hasNext()) {
			String aConcept = iterator.next();
			ranking = combine(ranking, findImagebyConcept(aConcept, iCollection));
		}
		return ranking;
	}
	
	/**
	 * 
	 * This method allows to search the image collection by querying with a concept
	 * 
	 * @param conceptList an hashmap containing the list of concepts for which we want to find relevant images with associated weights. The higher the weight, the more important a concept is in the context of this query and thus the more it will influence retrieval 
	 * @param iCollection the collection of images used for retrieval (the same collection that has been indexed). Note that here the collection is used only for metadata information
	 * @return the ranking
	 */
	public  ListMultimap<Double, Image> findImagebyConcept(HashMap<String, Double> conceptList, ImageCollection iCollection) {
		ListMultimap<Double, Image> ranking = Multimaps.newListMultimap(
				  new TreeMap<Double, Collection<Image>>(),
				  new Supplier<List<Image>>() {
				    public List<Image> get() {
				      return Lists.newArrayList();
				    }
				  });
		
		for (String aConcept : conceptList.keySet()) {
			double weight = conceptList.get(aConcept);
			ranking = combine(ranking, findImagebyConcept(aConcept, iCollection), weight);
		}
		return ranking;
	}
	
	/**
	 * 
	 * This ancillary method is used to combine two rankings of images
	 * 
	 * @param a a ranking of images (with associated scores)
	 * @param b another ranking of images (with associated scores)
	 * @return the merged ranking
	 */
	public ListMultimap<Double, Image> combine (ListMultimap<Double, Image> a, ListMultimap<Double, Image> b){
		HashMap<Image, Double> merged = new HashMap<Image, Double>();
		for (Double score : a.keySet()) {
			List<Image> images = a.get(score);
			Iterator<Image> iterator = images.iterator();
			while(iterator.hasNext()){
				Image anImage = iterator.next();
				merged.put(anImage, score);	
			}		
		}
		for (Double score : b.keySet()) {
			List<Image> images = b.get(score);
			Iterator<Image> iterator = images.iterator();
			while(iterator.hasNext()){
				Image anImage = iterator.next();
				double oldscore=0.0;
				if(merged.containsKey(anImage)) {
					oldscore = merged.get(anImage);
				}
				oldscore = score+oldscore;
				merged.put(anImage, oldscore);	
			}		
		}
		ListMultimap<Double, Image> ranking = Multimaps.newListMultimap(
				new TreeMap<Double, Collection<Image>>(),
				new Supplier<List<Image>>() {
					public List<Image> get() {
						return Lists.newArrayList();
					}
				});
		for(Image anImage: merged.keySet()) {
			double aScore = merged.get(anImage);
			ranking.put(aScore, anImage);
		}
		return ranking;
	}
	
	/**
	 * 
	 * This ancillary method is used to combine two rankings of images
	 * 
	 * @param a a ranking of images (with associated scores)
	 * @param b another ranking of images (with associated scores). This is the ranking that needs to be weighted by the weight
	 * @param weight the weight of this concept. Weights multiply the score of a concept for a query
	 * @return the merged ranking
	 */
	public ListMultimap<Double, Image> combine (ListMultimap<Double, Image> a, ListMultimap<Double, Image> b, Double weight){
		HashMap<Image, Double> merged = new HashMap<Image, Double>();
		for (Double score : a.keySet()) {
			List<Image> images = a.get(score);
			Iterator<Image> iterator = images.iterator();
			while(iterator.hasNext()){
				Image anImage = iterator.next();
				merged.put(anImage, score);	
			}		
		}
		for (Double score : b.keySet()) {
			List<Image> images = b.get(score);
			Iterator<Image> iterator = images.iterator();
			while(iterator.hasNext()){
				Image anImage = iterator.next();
				double oldscore=0.0;
				if(merged.containsKey(anImage)) {
					oldscore = weight * merged.get(anImage);
				}
				oldscore = score+oldscore;
				merged.put(anImage, oldscore);	
			}		
		}
		ListMultimap<Double, Image> ranking = Multimaps.newListMultimap(
				new TreeMap<Double, Collection<Image>>(),
				new Supplier<List<Image>>() {
					public List<Image> get() {
						return Lists.newArrayList();
					}
				});
		for(Image anImage: merged.keySet()) {
			double aScore = merged.get(anImage);
			ranking.put(aScore, anImage);
		}
		return ranking;
	}

	/**
	 * 
	 * This method allows to search the image collection by querying with a keyword. The retrieved set of images would be those for which the concept description conatined the keyword
	 * Note, this method does not support scoring at the moment. TODO: add scoring
	 * @param akeyword the query keyword to be used to find relevant images
	 * @return a set of images that are relevant to the query keyword
	 */
	public List<Image> findImagebyKeyword(String akeyword) {
		List<Image> results = new Vector<Image>();
		return results = mainKeywordIndex.get(akeyword);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
