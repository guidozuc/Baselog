package score;

import com.google.common.collect.ListMultimap;
import model.Image;

/**
 * Created by Harry Scells on 13/01/2016.
 */
public interface Scorer {

    ListMultimap<Double, Image> combine(ListMultimap<Double, Image> a, ListMultimap<Double, Image> b, double weight);

}
