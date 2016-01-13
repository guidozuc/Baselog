package score;

import com.google.common.base.Supplier;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimaps;
import model.Image;

import java.util.*;

/**
 * Created by Harry Scells on 13/01/2016.
 */
public final class WeightedScorer implements Scorer {

    /**
     * This method is used to combine two rankings of images
     *
     * @param a a ranking of images (with associated scores)
     * @param b another ranking of images (with associated scores). This is the ranking that needs to be weighted by the weight
     * @param weight the weight of this concept. Weights multiply the score of a concept for a query
     * @return the merged ranking
     */
    public ListMultimap<Double, Image> combine (ListMultimap<Double, Image> a, ListMultimap<Double, Image> b, double weight){
        HashMap<Image, Double> merged = new HashMap<Image, Double>();
        for (Double score : a.keySet()) {
            List<Image> images = a.get(score);
            for (Image anImage : images) {
                merged.put(anImage, score);
            }
        }
        for (Double score : b.keySet()) {
            List<Image> images = b.get(score);
            Iterator<Image> iterator = images.iterator();
            while(iterator.hasNext()){
                Image anImage = iterator.next();
                double oldscore=0.0;
                if(merged.containsKey(anImage)) {
                    oldscore = weight * merged.get(anImage);
                }
                oldscore = score+oldscore;
                merged.put(anImage, oldscore);
            }
        }
        ListMultimap<Double, Image> ranking = Multimaps.newListMultimap(
                new TreeMap<Double, Collection<Image>>(),
                new Supplier<List<Image>>() {
                    public List<Image> get() {
                        return Lists.newArrayList();
                    }
                });
        for(Image anImage: merged.keySet()) {
            double aScore = merged.get(anImage);
            ranking.put(aScore, anImage);
        }
        return ranking;
    }

    public static WeightedScorer getInstance() {
        return new WeightedScorer();
    }
    
}
