import java.io.*;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.google.common.collect.Interner;
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
	
	public static List<String> runQuery(Query query, InvertedImageIndex index, ImageCollection iCollection, TimelineDao timelineDao, BiFunction<HashMap<String, Double>, ImageCollection, ListMultimap<Double, Image>> func) throws SQLException {
		HashMap<String, Double> weightedQuery = query.getWeightedQuery();
		System.out.println("Finding images with concepts " + weightedQuery.toString());
		ListMultimap<Double, Image> conceptResultsArray = func.apply(weightedQuery, iCollection);
		List<String> resultdump = new ArrayList<String>(); //this is a very dirty way of dealing with the reversion of the conceptResultsArray
		for (Double score : conceptResultsArray.keySet()) {
			List<Image> images = conceptResultsArray.get(score);
			//Iterator<model.Image> iterator = images.iterator();

			for (int i = images.size() - 1; i >= 0; i--) {

				Image image = images.get(i);
				String line = query.getQid() + ", " + formatImageID(image.getImageURL() + ", 1, " + score);
				resultdump.add(line);

			}
		}
		return resultdump;
	}
	
	public static void printRanking(List<String> resultdump,  int maxRank) {
		/*The following reverses the list of results to iterate from the one with highest score to that with the lowest*/
		int count = 0;
		for (int i = resultdump.size() - 1; i >= 0 && count < maxRank; i--) {
			System.out.println(resultdump.get(i));
			count++;
		}
	}
	
	public static void printRanking(List<String> resultdump, PrintWriter writer, int maxRank) {
		/*The following reverses the list of results to iterate from the one with highest score to that with the lowest*/
		int count = 0;
		for (int i = resultdump.size() - 1; i >= 0 && count < maxRank; i--) {
			String result = resultdump.get(i);
			System.out.println(result);
			writer.println(result);
			count++;
		}
	}
	
	public static void scoreQueryset(QuerySetReader queryset, InvertedImageIndex index, ImageCollection iCollection, TimelineDao timelineDao, int maxRank, BiFunction<HashMap<String, Double>, ImageCollection, ListMultimap<Double, Image>> func) throws SQLException {
		for (String qid : queryset.getQuerySet().keySet()) {
			Query query = queryset.getQuerySet().get(qid);
			System.out.println("qid = " + qid);
			List<String> resultdump = runQuery(query, index, iCollection, timelineDao, func);
			printRanking(resultdump, maxRank);
			System.out.println("-----------------------------------------------------------------------------");
		}
	}
	
	public static void scoreQueryset(QuerySetReader queryset, InvertedImageIndex index, ImageCollection iCollection, TimelineDao timelineDao, PrintWriter writer, int maxRank, BiFunction<HashMap<String, Double>, ImageCollection, ListMultimap<Double, Image>> func) throws SQLException {
		for (String qid : queryset.getQuerySet().keySet()) {
			Query query = queryset.getQuerySet().get(qid);
			System.out.println("qid = " + qid);
			List<String> resultdump = runQuery(query, index, iCollection, timelineDao, func);
			printRanking(resultdump, writer, maxRank);
			System.out.println("-----------------------------------------------------------------------------");
		}
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {

		/**
		 * # Example config.properties:
		 * concepts=/Users/harryscells/data/ntcir2015_lifelogging/Caffe_concepts_list.txt
		 * collection=/Users/harryscells/data/ntcir2015_lifelogging/NTCIR_Lifelog_Formal_Dataset/NTCIR-Lifelog_Formal_Concepts.txt
		 * dataset=/Users/harryscells/data/ntcir2015_lifelogging/NTCIR_Lifelog_Formal_Dataset/NTCIR-Lifelog_Formal_dataset.xml
		 * topics=data/lifelogging_topics_formal.xml
		 * output=/Users/harryscells/data/ntcir2015_lifelogging/run1.txt
		 */

		String propertiesFileName = "config.properties";
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(propertiesFileName));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Need a config.properties to perform a run.");
		}

		String filepath_concept_list = properties.getProperty("concepts");
		ConceptVocabulary cv = new ConceptVocabulary();
		cv.readVocabulary(filepath_concept_list);
		ImageCollection iCollection = new ImageCollection();
		iCollection.setConceptVocabulary(cv);
		String filepath = properties.getProperty("collection");
		iCollection.readCollection(filepath);

		System.out.println("Start indexing");
		InvertedImageIndex index = new InvertedImageIndex(iCollection);
		System.out.println("... indexing finished");

		System.out.println("Setting up a timelineDao");
		TimelineDao timelineDao = new TimelineDao("jdbc:mysql://localhost:3306/", "NTCIRLifelogging", "root", null);
		timelineDao.loadTimeline(properties.getProperty("dataset"));
		System.out.println("... timelineDao finished");

		PrintWriter writer;
		QuerySetReader queryset = new QuerySetReader();
		queryset.readQueryFile(properties.getProperty("topics"));

		writer = new PrintWriter(properties.getProperty("output") + "run1.txt", "UTF-8");
		scoreQueryset(queryset, index, iCollection, timelineDao, writer, 100, index::findImageByConcept);

		writer = new PrintWriter(properties.getProperty("output") + "run2.txt", "UTF-8");
		scoreQueryset(queryset, index, iCollection, timelineDao, writer, 100, index::findImageByWeightedConcept);

		writer = new PrintWriter(properties.getProperty("output") + "run3.txt", "UTF-8");
		scoreQueryset(queryset, index, iCollection, timelineDao, writer, 100, index::findImageByIDFConcept);
		writer.close();
	}

}
