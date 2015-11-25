import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

/**
 * 
 */

/**
 * @author zuccong
 *
 */
public class DBStorage {

	
	Connection connection=null;
	String DBName="";
	String username="";
	String password="";
	static final String DB_URL = "jdbc:mysql://localhost/";
	
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
	 * Creates a database if it doesn't exist
	 * 
	 * @param DBName the name of the database to create
	 * @param username the username to access the database application
	 * @param password the password associated to the username
	 */
	public void connect(String DBName, String username, String password) throws SQLException, ClassNotFoundException {
		this.DBName=DBName;
		this.username=username;
		this.password=password;
		String myDriver = "org.gjt.mm.mysql.Driver";
		String myUrl = "jdbc:mysql://localhost/";
		Class.forName(myDriver);
		this.connection =  DriverManager.getConnection(DB_URL,username,password);
		Statement st = connection.createStatement();
		st.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DBName + ";");
		//st.close();
		System.out.println("Database created successfully...");
	}
	
	/**
	 * Connects to the database
	 * 
	 */
	public void connect() throws SQLException, ClassNotFoundException {
		String myDriver = "org.gjt.mm.mysql.Driver";
		String myUrl = "jdbc:mysql://localhost/";
		Class.forName(myDriver);
		this.connection =  DriverManager.getConnection("jdbc:mysql://localhost/" + this.DBName,  this.username, this.password);
		Statement st = connection.createStatement();
		System.out.println("Creating DB");
		st.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DBName + ";");
		
		//st.close();
		System.out.println("Database created successfully...");
		System.out.println("connected...");
	}
	
	
	/**
	 * Creates the tables required by the Lifelogging database
	 * 
	 * 
	 */
	public void createLifeloggingTables() throws SQLException, ClassNotFoundException {
		if(this.connection.isClosed())
			this.connect();
	
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
	public void removeLifeloggingTables() throws SQLException, ClassNotFoundException {
		if(this.connection.isClosed())
			this.connect();

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
		if(this.connection.isClosed())
			this.connect();

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
	public void LoadLifeloggingData(String XMLFilePath) throws SQLException, ClassNotFoundException {
		if(this.connection.isClosed())
			this.connect();
	
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
		if(this.connection.isClosed())
			this.connect();
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
		if(this.connection.isClosed())
			this.connect();
		
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
			moment.minute = rs.getString("id");
			moment.date = rs.getString("date");
			moment.location = rs.getString("location");
			moment.activity = rs.getString("activity");
			moment.imagepath=image_path;
			String queryimages = "SELECT `image-path` FROM image WHERE id='" + moment.minute+ "'";
			Statement subst = this.connection.createStatement();
			ResultSet subrs = subst.executeQuery(queryimages);
			while (subrs.next())
			{
				moment.images.add(subrs.getString("image-path"));
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
		String query = "SELECT * FROM minute WHERE id < " + referenceMoment.minute
				+ "AND id > " + String.valueOf(Integer.parseInt(referenceMoment.minute)  - range);
	    Statement st = this.connection.createStatement();
		ResultSet rs = st.executeQuery(query);
		while (rs.next())
		{
			Moment aMoment = new Moment();
			aMoment.minute = rs.getString("id");
			aMoment.date = rs.getString("date");
			aMoment.location = rs.getString("location");
			aMoment.activity = rs.getString("activity");
			aMoment.imagepath = rs.getString("image-path");
			String queryimages = "SELECT `image-path` FROM image WHERE id='" + aMoment.minute+ "'";
			Statement subst = this.connection.createStatement();
			ResultSet subrs = subst.executeQuery(queryimages);
			while (subrs.next())
			{
				aMoment.images.add(subrs.getString("image-path"));
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
		String query = "SELECT * FROM minute WHERE id > " + referenceMoment.minute
				+ "AND id < " + String.valueOf(Integer.parseInt(referenceMoment.minute) + range);
	    Statement st = this.connection.createStatement();
		ResultSet rs = st.executeQuery(query);
		while (rs.next())
		{
			Moment aMoment = new Moment();
			aMoment.minute = rs.getString("id");
			aMoment.date = rs.getString("date");
			aMoment.location = rs.getString("location");
			aMoment.activity = rs.getString("activity");
			aMoment.imagepath = rs.getString("image-path");
			String queryimages = "SELECT `image-path` FROM image WHERE id='" + aMoment.minute+ "'";
			Statement subst = this.connection.createStatement();
			ResultSet subrs = subst.executeQuery(queryimages);
			while (subrs.next())
			{
				aMoment.images.add(subrs.getString("image-path"));
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
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		

	}

}
