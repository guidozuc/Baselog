package dao;

import db.DBStorage;
import model.Image;
import model.Moment;

import java.io.File;
import java.nio.file.FileSystemException;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Vector;

/**
 * 
 */

/**
 * @author zuccong
 *
 */
public class TimelineDao {

	private DBStorage db;

	public TimelineDao(String url, String dbName, String username, String password) throws SQLException, ClassNotFoundException {
		this.db = new DBStorage(url, dbName, username, password);
		db.removeLifeloggingTables();
		db.createLifeloggingTables();
	}
	
	public void loadTimeline(String XMLFilePath) throws SQLException, FileSystemException {
		File xmlFile = new File(XMLFilePath);
		if (xmlFile.exists()) {
			db.LoadLifeloggingData(XMLFilePath);
		} else {
			throw new FileSystemException("XML file does no exist.");
		}
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
		Moment moment = db.queryMoment(image.getImageURL());
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
		Moment moment = db.queryMoment(image.getImageURL());
		String minute = moment.getMinute();
		return minute;
	}
	
	public String getMinute(String image_path) throws SQLException {
		Moment moment = db.queryMoment(image_path);
		String minute = moment.getMinute();
		return minute;
	}
	
	public String getLocation(Image image) throws SQLException {
		Moment moment = db.queryMoment(image.getImageURL());
		String location = moment.getLocation();
		return location;
	}
	
	public String getLocation(String image_path) throws SQLException {
		Moment moment = db.queryMoment(image_path);
		String location = moment.getLocation();
		return location;
	}
	
	public String getActivity(Image image) throws SQLException {
		Moment moment = db.queryMoment(image.getImageURL());
		String activity = moment.getActivity();
		return activity;
	}
	
	public String getActivity(String image_path) throws SQLException {
		Moment moment = db.queryMoment(image_path);
		String activity = moment.getActivity();
		return activity;
	}
	
	public String getDate(Image image) throws SQLException {
		Moment moment = db.queryMoment(image.getImageURL());
		String date = moment.getDate();
		return date;
	}
	
	public String getDate(String image_path) throws SQLException {
		Moment moment = db.queryMoment(image_path);
		String date = moment.getDate();
		return date;
	}

	public Vector<Moment> getMomentsBefore(Moment moment, int range) throws SQLException {
		return db.getMomentsBefore(moment, range);
	}
	public Vector<Moment> getMomentsAfter(Moment moment, int range) throws SQLException {
		return db.getMomentsAfter(moment, range);
	}

	/**
	 * @param args
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
//
//		TimelineDao timelineDao = new TimelineDao();
//		timelineDao.init();
//		timelineDao.loadTimeline("/Users/zuccong/data/ntcir2015_lifelogging/NTCIR_Lifelog_Dryrun_Dataset/NTCIR-Lifelog_Dryrun_dataset.xml");
//		timelineDao.showTimeline();
//		System.out.println("query for the moment associated with image /u1/2015-02-18/b00001277_21i6bq_20150218_205804e.jpg");
//		String queryImage = "/u1/2015-02-18/b00001277_21i6bq_20150218_205804e.jpg";
//		Moment aMoment = timelineDao.getMoment(queryImage);
//		System.out.println(aMoment.toString());
//		//timelineDao.getMomentsWithImages();
//		timelineDao.closeTimeline();
	}

}
