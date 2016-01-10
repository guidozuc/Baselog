import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ListMultimap;
import dao.TimelineDao;
import model.Image;
import model.Query;
import util.*;

/**
 * 
 */

/**
 * @author zuccong
 *
 * This class implements Run 1 of our Dry Run NTCIR 2015 Lifelog submission.
 * In Run 1 we do a simple concept-based matching, by just returning images matched against query concepts.
 * model.Query concepts are extracted manually.
 *
 *
 */
public class Run1 {

	public static String formatImageID(String imageid) {
		File file = new File(imageid);
		imageid = file.getName().replace(".jpg", "");
		return imageid;
	}
	
	public static List<String> runQuery(Query query, InvertedImageIndex index, ImageCollection iCollection, TimelineDao timelineDao) throws SQLException {
		HashMap<String, Double> weightedQuery = query.getWeightedQuery();
		System.out.println("Finding images with concepts " + weightedQuery.toString());
		ListMultimap<Double, Image> conceptResultsArray = index.findImagebyConcept(weightedQuery, iCollection);
		List<String> resultdump = new ArrayList<String>(); //this is a very dirty way of dealing with the reversion of the conceptResultsArray
		for (Double score : conceptResultsArray.keySet()) {
			List<Image> images = conceptResultsArray.get(score);
			//Iterator<model.Image> iterator = images.iterator();
			ReverseIterator<Image> riterator = new ReverseIterator<Image>(images);
			while(riterator.hasNext()) {
				Image theNextImage = riterator.next();
				//String line = timelineDao.getDate(theNextImage) + " " + timelineDao.getMinute(theNextImage) + " " +  theNextImage.getImageURL() + " " + score;
				String line = query.getQid() + ", " + formatImageID(theNextImage.getImageURL() + ", 1, " + score);
				
				//System.out.println(timelineDao.getDate(theNextImage) + " " + timelineDao.getMinute(theNextImage) + " " +  theNextImage.getImageURL() + " " + score);
				resultdump.add(line);
			}	
		}
		return resultdump;
	}
	
	public static void printRanking(List<String> resultdump,  int maxRank) {
		/*The following reverses the list of results to iterate from the one with highest score to that with the lowest*/
		//TODO: this could be removed if scores were recorded as negative scores.
		ReverseIterator<String> reversedList = new ReverseIterator<String>(resultdump);
		Iterator<String> rit = reversedList.iterator();
		int count=0;
		while(rit.hasNext() && count<maxRank){
			System.out.println(rit.next());
			count++;
		}
	}
	
	public static void printRanking(List<String> resultdump, PrintWriter writer, int maxRank) {
		/*The following reverses the list of results to iterate from the one with highest score to that with the lowest*/
		//TODO: this could be removed if scores were recorded as negative scores.
		ReverseIterator<String> reversedList = new ReverseIterator<String>(resultdump);
		Iterator<String> rit = reversedList.iterator();
		int count=0;
		while(rit.hasNext() && count<maxRank){
			String nextResultLine = rit.next();
			System.out.println(nextResultLine);
			writer.println(nextResultLine);
			count++;
		}
		
	}
	
	public static void scoreQueryset(QuerySetReader queryset, InvertedImageIndex index, ImageCollection iCollection, TimelineDao timelineDao, int maxRank) throws SQLException {
		for (String qid : queryset.getQuerySet().keySet()) {
			Query query = queryset.getQuerySet().get(qid);
			System.out.println("qid = " + qid);
			List<String> resultdump = runQuery(query, index, iCollection, timelineDao);
			printRanking(resultdump, maxRank);
			System.out.println("-----------------------------------------------------------------------------");
		}
	}
	
	public static void scoreQueryset(QuerySetReader queryset, InvertedImageIndex index, ImageCollection iCollection, TimelineDao timelineDao, PrintWriter writer, int maxRank) throws SQLException {
		for (String qid : queryset.getQuerySet().keySet()) {
			Query query = queryset.getQuerySet().get(qid);
			System.out.println("qid = " + qid);
			List<String> resultdump = runQuery(query, index, iCollection, timelineDao);
			printRanking(resultdump, writer, maxRank);
			System.out.println("-----------------------------------------------------------------------------");
		}
	}
	/**
	 * @param args
	 * @throws IOException 
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
		//TODO: read paths from a configuration file; need to set gradlew to accept and parse inputs
		String filepath_concept_list = "/data/ntcir2015_lifelogging/Caffe_concepts_list.txt";//"/Users/harryscells/data/ntcir2015_lifelogging/Caffe_concepts_list.txt";
		ConceptVocabulary cv = new ConceptVocabulary();
		cv.readVocabulary(filepath_concept_list);
		ImageCollection iCollection = new ImageCollection();
		iCollection.setConceptVocabulary(cv);
		String filepath="/data/ntcir2015_lifelogging/NTCIR_Lifelog_formal_run_Dataset/NTCIR-Lifelog_formal_run_Concepts.txt";//"/Users/harryscells/data/ntcir2015_lifelogging/NTCIR_Lifelog_Dryrun_Dataset/NTCIR-Lifelog_Dryrun_Concepts.txt";
		iCollection.readCollection(filepath);

		System.out.println("Start indexing");
		InvertedImageIndex index = new InvertedImageIndex(iCollection);
		System.out.println("... indexing finished");

		System.out.println("Setting up a timelineDao");
		TimelineDao timelineDao = new TimelineDao("jdbc:mysql://localhost:3306/", "NTCIRLifelogging", "root", null);
		timelineDao.loadTimeline("/data/ntcir2015_lifelogging/NTCIR_Lifelog_formal_run_Dataset/NTCIR-Lifelog_formal_run_dataset.xml");//"/Users/harryscells/data/ntcir2015_lifelogging/NTCIR_Lifelog_Dryrun_Dataset/NTCIR-Lifelog_Dryrun_dataset.xml");
		System.out.println("... timelineDao finished");

		PrintWriter writer = new PrintWriter("/data/ntcir2015_lifelogging/runs/run1.txt", "UTF-8");//"/Users/harryscells/data/ntcir2015_lifelogging/run1.txt", "UTF-8");
		
		QuerySetReader queryset = new QuerySetReader();
		queryset.readQueryFile("data/lifelogging_topics_formal.xml");//("data/lifelogging_topics_dryrun.xml");
		scoreQueryset(queryset, index, iCollection, timelineDao, writer, 100);
		writer.close();
	}

}
