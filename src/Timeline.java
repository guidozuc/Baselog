import java.sql.SQLException;
import java.util.Properties;

/**
 * 
 */

/**
 * @author zuccong
 *
 */
public class Timeline {

	DBStorage db = new DBStorage();
	
	public void init() throws ClassNotFoundException, SQLException {
		Properties prop = new Properties();
		db.setDBName("NTCIRLifelogging");
		db.setUsername("root"); //change the username as required
		db.setPassword("birdFlu"); //change the password as required
		db.connect();
		db.removeLifeloggingTables();
		db.createLifeloggingTables();
	}
	
	public void loadTimeline(String XMLFilePath) throws ClassNotFoundException, SQLException {
		db.LoadLifeloggingData(XMLFilePath);
	}
	
	public void showTimeline() throws ClassNotFoundException, SQLException {
		db.listAllMoments();;
	}
	
	public void closeTimeline() {
		db.closeConnection();
	}
	
	public void getMomentsWithImages() throws SQLException {
		db.listAllMomentsWithImages();
	}
	
	/**
	 * 
	 * Get a moment by querying with an image
	 * @param image the image to be used as a query
	 * @return a moment 
	 */
	public Moment getMoment(Image image) throws SQLException {
		Moment moment = db.queryMoment(image.imageURL);
		return moment;
	}
	
	/**
	 * 
	 * Get a moment by querying with an image path
	 * @param image_path the path to the image used as a query
	 * @return a moment 
	 */
	public Moment getMoment(String image_path) throws SQLException {
		Moment moment = db.queryMoment(image_path);
		return moment;
	}
	
	public String getMinute(Image image) throws SQLException {
		Moment moment = db.queryMoment(image.imageURL);
		String minute = moment.getMinute();
		return minute;
	}
	
	public String getMinute(String image_path) throws SQLException {
		Moment moment = db.queryMoment(image_path);
		String minute = moment.getMinute();
		return minute;
	}
	
	public String getLocation(Image image) throws SQLException {
		Moment moment = db.queryMoment(image.imageURL);
		String location = moment.getLocation();
		return location;
	}
	
	public String getLocation(String image_path) throws SQLException {
		Moment moment = db.queryMoment(image_path);
		String location = moment.getLocation();
		return location;
	}
	
	public String getActivity(Image image) throws SQLException {
		Moment moment = db.queryMoment(image.imageURL);
		String activity = moment.getActivity();
		return activity;
	}
	
	public String getActivity(String image_path) throws SQLException {
		Moment moment = db.queryMoment(image_path);
		String activity = moment.getActivity();
		return activity;
	}
	
	public String getDate(Image image) throws SQLException {
		Moment moment = db.queryMoment(image.imageURL);
		String date = moment.getDate();
		return date;
	}
	
	public String getDate(String image_path) throws SQLException {
		Moment moment = db.queryMoment(image_path);
		String date = moment.getDate();
		return date;
	}
	
	
	/**
	 * @param args
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		
		Timeline timeline = new Timeline();
		timeline.init();
		timeline.loadTimeline("/Users/zuccong/data/ntcir2015_lifelogging/NTCIR_Lifelog_Dryrun_Dataset/NTCIR-Lifelog_Dryrun_dataset.xml");
		timeline.showTimeline();
		System.out.println("query for the moment associated with image /u1/2015-02-18/b00001277_21i6bq_20150218_205804e.jpg");
		String queryImage = "/u1/2015-02-18/b00001277_21i6bq_20150218_205804e.jpg";
		Moment aMoment = timeline.getMoment(queryImage);
		System.out.println(aMoment.toString());
		//timeline.getMomentsWithImages();
		timeline.closeTimeline();
	}

}
