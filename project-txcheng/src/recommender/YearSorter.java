package recommender;
import java.util.Comparator;

/**
 * allows me to sort movies by year
 */
public class YearSorter implements Comparator<SuggestedMovie>{
    @Override
    public int compare(SuggestedMovie o1, SuggestedMovie o2) {
        return -1 * (o1.getYear().compareTo(o2.getYear()));
    }
}
