package util;

import java.util.*;

import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimaps;
import model.Concept;
import model.Image;
import score.AdditiveScorer;
import score.WeightedScorer;

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

	HashMap<String, Integer> concepts = new HashMap<String, Integer>();
	
	public InvertedImageIndex(){}
	
	public InvertedImageIndex(ImageCollection iCollection){

		// loop over the entire collection of images
		for (Map.Entry<String, Image> imageCollection : iCollection.collection.entrySet()) {

			// get the image and concept vector for each item in the collection
			Image image = imageCollection.getValue();
			HashMap<String, Concept> conceptVector = image.getConceptVector();

			// loop over the concepts in the image
			for (Map.Entry<String, Concept> conceptEntry : conceptVector.entrySet()) {

				String conceptId = conceptEntry.getKey();
				Concept concept = conceptEntry.getValue();

				// maintain a list of TF for concepts
				if (!concepts.containsKey(conceptId)) {
					concepts.put(conceptId, 0);
				} else {
					concepts.put(conceptId, concepts.get(conceptId) + 1);
				}

				// drop concepts where the score is 0
				if (concept.getScore() > 0.0) {
					this.putConceptImage(conceptId, image);

					// add all the keywords in the concepts to the image
					for (String keyword : concept.getKeywords()) {
						this.putKeywordImage(keyword, image);
					}

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
	public ListMultimap<Double, Image> findImageByConcept(String aConcept, ImageCollection iCollection) {
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
	public ListMultimap<Double, Image> findImageByConcept(Vector<String> conceptList, ImageCollection iCollection) {
		ListMultimap<Double, Image> ranking = Multimaps.newListMultimap(
				  new TreeMap<Double, Collection<Image>>(),
				  new Supplier<List<Image>>() {
				    public List<Image> get() {
				      return Lists.newArrayList();
				    }
				  });
		for (String aConcept : conceptList) {
			ranking = AdditiveScorer.getInstance().combine(ranking, findImageByConcept(aConcept, iCollection));
		}
		return ranking;
	}

	/**
	 *
	 * This method allows to search the image collection by querying with a concept
	 *
	 * @param conceptList a hashmap containing the list of concepts for which we want to find relevant images with associated weights. The higher the weight, the more important a concept is in the context of this query and thus the more it will influence retrieval
	 * @param iCollection the collection of images used for retrieval (the same collection that has been indexed). Note that here the collection is used only for metadata information
	 * @return the ranking
	 */
	public ListMultimap<Double, Image> findImageByConcept(HashMap<String, Double> conceptList, ImageCollection iCollection) {
		ListMultimap<Double, Image> ranking = Multimaps.newListMultimap(
				new TreeMap<Double, Collection<Image>>(),
				new Supplier<List<Image>>() {
					public List<Image> get() {
						return Lists.newArrayList();
					}
				});

		for (String aConcept : conceptList.keySet()) {
			ranking = WeightedScorer.getInstance().combine(ranking, findImageByConcept(aConcept, iCollection), 1.0);
		}
		return ranking;
	}

	/**
	 * 
	 * This method allows to search the image collection by querying with a concept
	 * 
	 * @param conceptList a hashmap containing the list of concepts for which we want to find relevant images with associated weights. The higher the weight, the more important a concept is in the context of this query and thus the more it will influence retrieval
	 * @param iCollection the collection of images used for retrieval (the same collection that has been indexed). Note that here the collection is used only for metadata information
	 * @return the ranking
	 */
	public ListMultimap<Double, Image> findImageByWeightedConcept(HashMap<String, Double> conceptList, ImageCollection iCollection) {
		ListMultimap<Double, Image> ranking = Multimaps.newListMultimap(
				  new TreeMap<Double, Collection<Image>>(),
				  new Supplier<List<Image>>() {
				    public List<Image> get() {
				      return Lists.newArrayList();
				    }
				  });
		
		for (String aConcept : conceptList.keySet()) {
			double weight = conceptList.get(aConcept);
			ranking = WeightedScorer.getInstance().combine(ranking, findImageByConcept(aConcept, iCollection), weight);
		}
		return ranking;
	}


	/**
	 * Use IDF in the weighting process for finding images by concept
	 * @param conceptList a hashmap containing the list of concepts for which we want to find relevant images with associated weights. The higher the weight, the more important a concept is in the context of this query and thus the more it will influence retrieval
	 * @param imageCollection the collection of images used for retrieval (the same collection that has been indexed). Note that here the collection is used only for metadata information
     * @return the ranking
     */
	public ListMultimap<Double, Image> findImageByIDFConcept(HashMap<String, Double> conceptList, ImageCollection imageCollection) {

		ListMultimap<Double, Image> ranking = Multimaps.newListMultimap(
				new TreeMap<Double, Collection<Image>>(),
				new Supplier<List<Image>>() {
					public List<Image> get() {
						return Lists.newArrayList();
					}
				});

		double numConcepts = concepts.size();
		for (String conceptId : conceptList.keySet()) {

			double weight = conceptList.get(conceptId) * calculateIDF(numConcepts, concepts.get(conceptId));
			ranking = WeightedScorer.getInstance().combine(ranking, findImageByConcept(conceptId, imageCollection), weight);

		}
		return ranking;
	}

	private double calculateIDF(double N, double n_t) {
		return Math.log(N/n_t);
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
