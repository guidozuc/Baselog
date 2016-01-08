package db;

import model.Moment;

import java.sql.*;
import java.util.Vector;

/**
 * 
 */

/**
 * @author zuccong
 *
 */
public class DBStorage {

	
	private Connection connection;
	private String DBName;
	private String username;
	private String password;
	private String DB_URL;

	public DBStorage(String url, String dbName, String username, String password) throws SQLException {
		this.username=username;
		this.password=password;
		this.DBName = dbName;
		this.DB_URL = url;
		this.DBName = dbName;

		try {
			Class.forName("org.gjt.mm.mysql.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		Connection tmpConnection = DriverManager.getConnection(DB_URL,username,password);

		Statement stmt = tmpConnection.createStatement();
		stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);
		stmt.close();
		tmpConnection.close();

		this.connection = DriverManager.getConnection(DB_URL + dbName,username,password);

		System.out.println("Database created successfully...");
	}

	public String getDBName() {
		return DBName;
	}

	public void setDBName(String dBName) {
		DBName = dBName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Creates the tables required by the Lifelogging database
	 * 
	 * 
	 */
	public void createLifeloggingTables() throws SQLException, ClassNotFoundException {

		String sqlCreateTblMinute = "CREATE TABLE IF NOT EXISTS minute ("
	            + "   id INT NOT NULL,"
	            + "   date VARCHAR(50) NOT NULL,"
	            + "   location VARCHAR(100),"
	            + "   activity VARCHAR(100),"
	            + "   `image-path` VARCHAR(100),"
	            + "	  INDEX `image-path` (`image-path`),"
	            + "	  PRIMARY KEY (id, date))";
		
		
		String sqlCreateTblImage = "CREATE TABLE IF NOT EXISTS image ("
	            + "   seq INT AUTO_INCREMENT,"
	            + "   id INT NOT NULL,"
	            + "   `image-path` VARCHAR(100),"
	            + "   PRIMARY KEY (seq),"
	            + "   FOREIGN KEY (id)"
	            + "	  REFERENCES minute(id)"
	            + "	  ON DELETE CASCADE) ENGINE=INNODB";
		
		 Statement stmt = this.connection.createStatement();
		    stmt.execute(sqlCreateTblMinute);
		    stmt.execute(sqlCreateTblImage);
		    stmt.close();
	}
	
	/**
	 * Removes the tables required by the Lifelogging database
	 * 
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public void removeLifeloggingTables() throws SQLException {

		String sqlCommand1 = "DROP TABLE IF EXISTS minute";
		String sqlCommand2 = "DROP TABLE IF EXISTS image";
		Statement stmt = this.connection.createStatement();

		stmt.execute(sqlCommand2);
		stmt.execute(sqlCommand1);
		stmt.close();
	}

	/**
	 * Clears the tables (removes all data from) required by the Lifelogging database
	 * 
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public void clearLifeloggingTables() throws SQLException, ClassNotFoundException {
		String sqlCommand1 = "DELETE TABLE IF EXISTS minute";
		String sqlCommand2 = "DELETE TABLE IF EXISTS image";
		Statement stmt = this.connection.createStatement();
		stmt.execute(sqlCommand1);
		stmt.execute(sqlCommand2);
		stmt.close();
	}
	
	/**
	 * Imports data into the tables required by the Lifelogging database
	 * @param XMLFilePath path to the XML file containing the dataset
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * 
	 */
	public void LoadLifeloggingData(String XMLFilePath) throws SQLException {
		String loaddata1 = "LOAD XML INFILE '" + XMLFilePath + "'"
	            + "INTO TABLE minute ROWS IDENTIFIED BY '<minute>'";
		String loaddata2 = "LOAD XML INFILE '" + XMLFilePath + "'"
	            + "INTO TABLE image ROWS IDENTIFIED BY '<image-path>'";
		
		Statement stmt = this.connection.createStatement();
		stmt.execute(loaddata1);
		stmt.execute(loaddata2);
		stmt.close();
	}
	
	/**
	 * Check whether an image exists into the database
	 * 
	 * @param imageString the path to the image (take care this has to be a path as recorded in the database)
	 * @return true if the image exists
	 */
	public boolean imageExists(String imageString) throws ClassNotFoundException, SQLException {
		String query = "SELECT * FROM minute WHERE `image-path`='" + imageString+"'";
		Statement st = this.connection.createStatement();
		// execute the query, and get a java resultset
		ResultSet rs = st.executeQuery(query);
		if(rs.getRow()==0)
			return false;
		else
			return true;
	}
	
	
	public void listAllMoments() throws ClassNotFoundException, SQLException {
		String query = "SELECT * FROM minute";
	    Statement st = this.connection.createStatement();
		ResultSet rs = st.executeQuery(query);

		// iterate through the java resultset
		while (rs.next())
		{
			String id = rs.getString("id");
			String date = rs.getString("date");
			String location = rs.getString("location");
			String activity = rs.getString("activity");
			String image_path = rs.getString("image-path");
			
			// print the results
			System.out.format("%s, %s, %s, %s, %s\n", id, date, location, activity, image_path);
		}	
	}
	
	public Moment queryMoment(String image_path) throws SQLException {
		Moment moment = new Moment();
		String queryminute = "SELECT id FROM image WHERE `image-path`='" + image_path+ "'";
		Statement st = this.connection.createStatement();
		ResultSet rs = st.executeQuery(queryminute);
		String minute="";
		while (rs.next())
		{
			minute = rs.getString("id");
		}
		
		String query = "SELECT * FROM minute WHERE id='" + minute + "'";
		st = this.connection.createStatement();
		rs = st.executeQuery(query);
		int counter=0;
		while (rs.next())
		{
			moment.setMinute(rs.getString("id"));
			moment.setDate(rs.getString("date"));
			moment.setLocation(rs.getString("location"));
			moment.setActivity(rs.getString("activity"));
			moment.setImagepath(image_path);
			String queryimages = "SELECT `image-path` FROM image WHERE id='" + moment.getMinute()+ "'";
			Statement subst = this.connection.createStatement();
			ResultSet subrs = subst.executeQuery(queryimages);
			while (subrs.next())
			{
				moment.getImages().add(subrs.getString("image-path"));
			}
			counter++;
		}
		if(counter>1)
			System.err.println("Attention! More than a moment associated to image " + image_path + ". Returning only the last moment");
		return moment;
	}
	
	public void listAllMomentsWithImages() throws SQLException {
		String query = "SELECT * FROM minute WHERE `image-path` IS NOT NULL";
	    Statement st = this.connection.createStatement();
		ResultSet rs = st.executeQuery(query);

		// iterate through the java resultset
		while (rs.next())
		{
			String id = rs.getString("id");
			String date = rs.getString("date");
			String location = rs.getString("location");
			String activity = rs.getString("activity");
			String image_path = rs.getString("image-path");
			
			// print the results
			System.out.format("%s, %s, %s, %s, %s\n", id, date, location, activity, image_path);
		}
	}
	
	/**
	 * This method gets the moments happening before the reference moments, in particular the moments that occur in the range ]referenceMoment - range, referenceMoment{
	 * 
	 * @param referenceMoment the reference moment to be used to as boundary
	 * @param range how many minutes before we want to capture (remember the ranges are not inclusive of the boundaries)
	 * @return a vector of moments that happened before the referenceMoment
	 */
	public Vector<Moment> getMomentsBefore(Moment referenceMoment, int range) throws SQLException {
		Vector<Moment> before = new Vector<Moment>();
		String query = "SELECT * FROM minute WHERE id < " + referenceMoment.getMinute()
				+ "AND id > " + String.valueOf(Integer.parseInt(referenceMoment.getMinute())  - range);
	    Statement st = this.connection.createStatement();
		ResultSet rs = st.executeQuery(query);
		while (rs.next())
		{
			Moment moment = new Moment();
			moment.setMinute(rs.getString("id"));
			moment.setDate(rs.getString("date"));
			moment.setLocation(rs.getString("location"));
			moment.setActivity(rs.getString("activity"));
			moment.setImagepath(rs.getString("image_path"));
			String queryimages = "SELECT `image-path` FROM image WHERE id='" + moment.getMinute()+ "'";
			Statement subst = this.connection.createStatement();
			ResultSet subrs = subst.executeQuery(queryimages);
			while (subrs.next())
			{
				moment.getImages().add(subrs.getString("image-path"));
			}
		}
		return before;
	}
	
	/**
	 * This method gets the moments happening after the reference moments, in particular the moments that occur in the range ]referenceMoment, referenceMoment + range{
	 * 
	 * @param referenceMoment the reference moment to be used to as boundary
	 * @param range how many minutes after we want to capture (remember the ranges are not inclusive of the boundaries)
	 * @return a vector of moments that happened after the referenceMoment
	 */
	public Vector<Moment> getMomentsAfter(Moment referenceMoment, int range) throws SQLException {
		Vector<Moment> before = new Vector<Moment>();
		String query = "SELECT * FROM minute WHERE id > " + referenceMoment.getMinute()
				+ "AND id < " + String.valueOf(Integer.parseInt(referenceMoment.getMinute()) + range);
	    Statement st = this.connection.createStatement();
		ResultSet rs = st.executeQuery(query);
		while (rs.next())
		{
			Moment moment = new Moment();
			moment.setMinute(rs.getString("id"));
			moment.setDate(rs.getString("date"));
			moment.setLocation(rs.getString("location"));
			moment.setActivity(rs.getString("activity"));
			moment.setImagepath(rs.getString("image_path"));
			String queryimages = "SELECT `image-path` FROM image WHERE id='" + moment.getMinute()+ "'";
			Statement subst = this.connection.createStatement();
			ResultSet subrs = subst.executeQuery(queryimages);
			while (subrs.next())
			{
				moment.getImages().add(subrs.getString("image-path"));
			}
		}
		return before;
	}
	
	
	/*
	 * this method closes the connection to the database
	 * */
	public void closeConnection() {
		try{
			if(this.connection!=null)
				this.connection.close();
		}catch(SQLException se){
			se.printStackTrace();
		}
	}

}
