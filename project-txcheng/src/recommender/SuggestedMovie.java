package recommender;

/**
 * this class was made so I could sort movies by popularity (number of ratings) and
 * also by year
 */
public class SuggestedMovie{
    private int movieID;
    private int rating;
    private String year;
    public SuggestedMovie(int movieID, int rating, String year)
    {
        this.rating = rating;
        this.year = year;
        this.movieID = movieID;
    }
    public int getRating()
    {
        return rating;
    }
    public String getYear()
    {
        return year;
    }
    public int getMovieID()
    {
        return movieID;
    }
}
