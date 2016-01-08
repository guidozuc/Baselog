package model;

import java.util.Vector;

/**
 * 
 */

/**
 * @author zuccong
 *
 */
/**
 * @author zuccong
 *
 */
public class Moment {

	private String date="";
	private String minute="";
	private String activity="";
	private String location="";
	private String imagepath="";
	private Vector<String> images = new Vector<String>();
	
	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}
	/**
	 * @param date the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}
	/**
	 * @return the minute
	 */
	public String getMinute() {
		return minute;
	}
	/**
	 * @param minute the minute to set
	 */
	public void setMinute(String minute) {
		this.minute = minute;
	}
	/**
	 * @return the activity
	 */
	public String getActivity() {
		return activity;
	}
	/**
	 * @param activity the activity to set
	 */
	public void setActivity(String activity) {
		this.activity = activity;
	}
	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}
	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}
	/**
	 * @return the imagepath
	 */
	public String getImagepath() {
		return imagepath;
	}
	/**
	 * @param imagepath the imagepath to set
	 */
	public void setImagepath(String imagepath) {
		this.imagepath = imagepath;
	}
	/**
	 * @return the images
	 */
	public Vector<String> getImages() {
		return images;
	}
	/**
	 * @param images the images to set
	 */
	public void setImages(Vector<String> images) {
		this.images = images;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "model.Moment [date=" + date + ", minute=" + minute + ", activity="
				+ activity + ", location=" + location + ", imagepath="
				+ imagepath + ", images=" + images + "]";
	}
	

	
	
}
