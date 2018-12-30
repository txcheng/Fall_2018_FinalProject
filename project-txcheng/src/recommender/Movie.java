package recommender;

import java.util.ArrayList;

/**
 * The Movie class stores the title, year, and id information of a movie.
 * It has methods to return this information.
 */
public class Movie {
    private String title, year;
    private ArrayList<String> genres;
    private int id;

    /**
     * Constructor
     * @param id movieID
     * @param title movie title
     * @param year movie year
     */
    public Movie(int id, String title, String year, ArrayList<String> genres)
    {
        this.id = id;
        this.title = title;
        this.year = year;
        this.genres = genres;
    }

    /**
     * Id getter
     * @return movieID
     */
    public int getId() {
        return id;
    }

    /**
     * Year getter
     * @return movieYear
     */
    public String getYear() {
        return year;
    }

    /**
     * Title getter
     * @return movieTitle
     */
    public String getTitle() {
        return title;
    }

    /**
     * Checks to see if the movie if of a particular genre
     * @param genre genre we're looking for
     * @return T/F
     */
    public boolean isGenre(String genre)
    {
        return genres.contains(genre.trim().toLowerCase());
    }

    /**
     * toString function so that I can check a movie's data
     * @return String containing id, title, year
     */
    @Override
    public String toString() {
        String output = "";
        output += (id + ", " + title + ", " + year);
        return output;
    }
}
