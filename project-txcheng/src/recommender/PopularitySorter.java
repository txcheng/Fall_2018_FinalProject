package recommender;
import java.util.Comparator;

/**
 * this was made so I could sort movies by popularity (number of ratings)
 */
public class PopularitySorter implements Comparator<SuggestedMovie>{
    @Override
    public int compare(SuggestedMovie o1, SuggestedMovie o2) {
        //it didn't like primitive types
        Integer rating1 = o1.getRating();
        Integer rating2 = o2.getRating();
        return -1 * (rating1.compareTo(rating2));
    }
}
