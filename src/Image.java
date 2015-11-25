import java.util.HashMap;
import java.util.Vector;

/**
 * 
 */

/**
 * @author zuccong
 *
 */
public class Image {

	String imageURL;
	HashMap<String,Concept> conceptVector;
	
	Image(String imageURL, HashMap<String,Concept> conceptVector){
		this.imageURL = imageURL;
		this.conceptVector = conceptVector;
	}
	
	Image(String imageURL){
		this.imageURL=imageURL;
		this.conceptVector = new HashMap<String,Concept>();
	}

	/**
	 * @return the imageURL
	 */
	public String getImageURL() {
		return imageURL;
	}

	/**
	 * @param imageURL the imageURL to set
	 */
	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	/**
	 * @return the conceptVector
	 */
	public HashMap<String, Concept> getConceptVector() {
		return conceptVector;
	}

	/**
	 * @param conceptVector the conceptVector to set
	 */
	public void setConceptVector(HashMap<String, Concept> conceptVector) {
		this.conceptVector = conceptVector;
	}
	
	public void addConcept(Concept aConcept) {
		this.conceptVector.put(aConcept.getConceptid(), aConcept);
	}
	
	public String toString() {
		String toPrint=this.imageURL;
		toPrint = toPrint.concat(" -> " + this.conceptVector.toString());
		return toPrint;
	}
}
