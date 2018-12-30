package recommender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * The user class is used to store userId and the movies the user have rated, paired with the user's rating
 * The method also can return all movies a user has rated between an upper and lower bound (inclusive).
 * It also can return the intersection of rated movies between this user and another user.
 */
public class User {
    private int id;
    private String name;
    private HashMap<Integer, Double> opinions;
    private ArrayList<Integer> savedMovies;

    /**
     * Constructor
     * @param id user's id
     */
    public User(int id, String name)
    {
        this.id = id;
        this.name = name;
        this.opinions = new HashMap<>();
        this.savedMovies = new ArrayList<>();
    }

    public User(int id)
    {
        this.id = id;
        this.name = null;
        this.opinions = new HashMap<>();
        this.savedMovies = new ArrayList<>();
    }

    public String getName()
    {
        return name;
    }
    /**
     * Adds a movieID, rating (K,V) pair to the opinions treemap
     * Overwrites duplicates
     * @param movieID movieID
     * @param rating ratind
     */
    public void addRating(int movieID, double rating)
    {
        //overwrite duplicate inputs
        opinions.put(movieID, rating);
    }

    /**
     * Returns the user's rating of a given movie or -1 if the user has not reviewed
     * the given movie
     * @param movieID movieID
     * @return rating
     */
    public double getRating(int movieID)
    {
        if(opinions.containsKey(movieID))
        {
            return opinions.get(movieID);
        }
        return -1;
    }

    /**
     * Checks to see if the user has reviewed a given movie
     * @param movieID movieID
     * @return boolean value if the user has seen a movie
     */
    public boolean reviewedMovie(int movieID)
    {
        return opinions.containsKey(movieID);
    }

    /**
     * Returns an arraylist of movies the user has seen
     * @return arraylist of movies the user has seen
     */
    public ArrayList<Integer> getMovies()
    {
        ArrayList<Integer> moviesSeen = new ArrayList<>();
        for(int i: opinions.keySet())
        {
            moviesSeen.add(i);
        }
        return moviesSeen;
    }

    /**
     * Returns an arraylist of movies above or equal to lower bound and less than or equal to upper bound
     * @return arraylist of movies the user has rated above or equal to 5.0
     */
    public ArrayList<Integer> getMoviesWithinRating(double lower, double upper)
    {
        ArrayList<Integer> ratings = new ArrayList<>();
        for (int i: opinions.keySet())
        {
            if(opinions.get(i)>=lower && opinions.get(i)<=upper)
            {
                ratings.add(i);
            }
        }
        return ratings;
    }

    /**
     * Returns the user's id
     * @return userID
     */
    public int getId()
    {
        return id;
    }

    /**
     * Generates an arraylist of arraylists that contain ratings on the interserctions of rated movies between
     * two given users
     * @param userID2 second user
     * @return arraylist<arraylist<double>> intersection
     */
    public ArrayList<ArrayList<Double>> getRatingsforSameMovie(User userID2)
    {
        ArrayList<ArrayList<Double>> output = new ArrayList<>();
        ArrayList<Double> ratings1 = new ArrayList<>();
        ArrayList<Double> ratings2 = new ArrayList<>();
        ArrayList<Integer> user1Movies = this.getMovies();
        ArrayList<Integer> user2Movies = userID2.getMovies();
        //sort to make sure orders are the same
        Collections.sort(user1Movies);
        Collections.sort(user2Movies);
        user1Movies.forEach((m)->
        {
            //get the intersection
            if(user2Movies.contains(m))
            {
                //get the ratings for each and save them in their respective arraylists
                ratings1.add(this.getRating(m));
                ratings2.add(userID2.getRating(m));
            }
        });
        //add the arraylists to the output
        output.add(ratings1);
        output.add(ratings2);
        return output;
    }

    /**
     * adds a saved movie
     * @param movieID movie
     */
    public void addSavedMovie(int movieID)
    {
        if(!savedMovies.contains(movieID))
        {
            savedMovies.add(movieID);
        }
    }

    /**
     * retrieves the user's saved movies
     * @return
     */
    public ArrayList<Integer> getSavedMovies()
    {
        ArrayList<Integer> movies = new ArrayList<>();
        for(int i : savedMovies)
        {
            movies.add(i);
        }
        return movies;
    }

    /**
     * clear's the user's saved movies
     */
    public void clearSaved()
    {
        savedMovies = new ArrayList<>();
    }
}
