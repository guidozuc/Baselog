import java.sql.SQLException;
import java.util.Vector;

/**
 * 
 */

/**
 * @author zuccong
 * 
 * The Vectors before and after contain the moments that happen before and after the moment of reference.
 * The first element of the before vector is the farther in time from the reference moment within the range of time; vice versa for after
 * 
 *
 */
public class NearbyMoments {

	Vector<Moment> before = new Vector<Moment> ();
	Vector<Moment> after = new Vector<Moment> ();
	int range=0;
	
	
	/**
	 * @return the before
	 */
	public Vector<Moment> getBefore() {
		return before;
	}


	/**
	 * @param before the before to set
	 */
	public void setBefore(Vector<Moment> before) {
		this.before = before;
	}


	/**
	 * @return the after
	 */
	public Vector<Moment> getAfter() {
		return after;
	}


	/**
	 * @param after the after to set
	 */
	public void setAfter(Vector<Moment> after) {
		this.after = after;
	}


	/**
	 * @return the range
	 */
	public int getRange() {
		return range;
	}


	/**
	 * @param range the range to set
	 */
	public void setRange(int range) {
		this.range = range;
	}


	public void getNearbyMoments(Moment referenceMoment, Timeline timeline) throws SQLException {
		this.before = timeline.db.getMomentsBefore(referenceMoment, this.range);
		this.after = timeline.db.getMomentsAfter(referenceMoment, this.range);
		//TODO: get the ones after
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
