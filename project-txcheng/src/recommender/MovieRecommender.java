package recommender;

import java.io.File;
import java.io.*;
import java.util.*;

/**
 * MovieRecommender is the class that utilizes the movie cllection and usercollection classes
 * to generate a list of recommended movies.
 */
public class MovieRecommender {

    private static UserCollection users;
    private static MovieCountCollection movieCount;
    private static MovieCollection movies;
    private static Object userlock;

    public MovieRecommender(Object userlock)
    {
        this.userlock = userlock;
    }
    /**
     * loads all the info from the text files
     */
    public void loadFiles()
    {

        System.out.println("Loading files");
        File inputPath = new File( "input" + File.separator + "smallSet"+ File.separator);
        users = new UserCollection();
        users.loadUsers(inputPath.toString());
        movies = new MovieCollection();
        movies.loadMovies(inputPath.toString());
        movieCount = new MovieCountCollection();
        movieCount.load(inputPath.toString());
    }

    /**
     * adds a movie rating for a given userID
     * @param userID
     * @param movieId
     * @param rating
     */
    public void addRating(int userID, int movieId, double rating)
    {
        synchronized (userlock)
        {
            users.addRating(userID,movieId,rating);
        }
    }
    /**
     * Sees if a user with the given user ID exists
     * @param userID
     * @return
     */
    public synchronized boolean hasUser(int userID)
    {
        return users.hasUser(userID);
    }

    /**
     * Sees if a user with the given username exists
     * @param username
     * @return
     */
    public synchronized boolean hasUser(String username)
    {
        return users.hasUser(username);
    }

    /**
     * adds a user object to the user database
     * @param input
     */
    public synchronized void addUser(User input)
    {
        synchronized (userlock)
        {
            users.addUser(input);
            System.out.println("Added a new user!");
        }
    }
    /**
     * returns the id of an existing user with a given username
     * @param username
     * @return
     */
    public synchronized int getUserWithName(String username)
    {
        return users.getUserWithName(username);
    }
    /**
     * returns the smallest userID value
     * @return
     */
    public synchronized int smallestID()
    {
        return users.getSmallestID();
    }

    /**
     * returns the movieID for a given movie
     * Returns 0 if it can't find the movie
     * @param movieTitle
     * @return
     */
    public synchronized int getMovieID(String movieTitle)
    {
        return movies.getMovieID(movieTitle);
    }
    /**
     * This method takes in a User and numRecs to generate a list
     * of recommended movies with a size of numRecs or less
     * @param id target user id
     * @param numRecs number of recommended movies in the output
     * @return arraylist of <Movie, year> String pairs
     */
    public synchronized ArrayList<ArrayList<String>> makeRecommendationList(int id, int numRecs, String type, ArrayList<String> genres, String sortType) {
        ArrayList<ArrayList<String>> output = new ArrayList<>();
        //"good" returns recommended movies
        //"bad" returns anti-recommended movies
        ArrayList<Integer> recMovieIDs = users.getRecs(id, type);
        ArrayList<SuggestedMovie> recMoviesGenre = new ArrayList<>();
        for(int movieID: recMovieIDs)
        {
            for(String genre: genres)
            {
                if(movies.isGenre(movieID,genre.trim().toLowerCase()))
                {
                    SuggestedMovie input = new SuggestedMovie(movieID,movieCount.getCount(movieID),movies.getYear(movieID));
                    recMoviesGenre.add(input);
                    break;
                }
            }
        }
        if(sortType.equals("pop"))
        {
            Collections.sort(recMoviesGenre, new PopularitySorter());
        }
        else if(sortType.equals("year"))
        {
            Collections.sort(recMoviesGenre, new YearSorter());
        }
        int count = 0;
        for(SuggestedMovie movie: recMoviesGenre)
        {
            ArrayList<String> movieTitleYear = new ArrayList();
            movieTitleYear.add(movies.getTitle(movie.getMovieID()));
            movieTitleYear.add(movies.getYear(movie.getMovieID()));
            output.add(movieTitleYear);
            count++;
            if(count>=numRecs)
            {
                break;
            }
        }
        return output;
    }

    /**
     * returns a list of reviewed movies for a given user
     * @param id userid
     * @return
     */
    public synchronized ArrayList<ArrayList<String>> getReviewedMovies(int id)
    {
        ArrayList<ArrayList<String>> movieYearPairs = new ArrayList<>();
        users.getReviewedMovies(id).forEach(i ->{
            ArrayList<String> movieYear = new ArrayList<>();
            movieYear.add(movies.getTitle(i));
            movieYear.add(movies.getYear(i));
            movieYearPairs.add(movieYear);
        });
        return movieYearPairs;
    }

    /**
     * adds a movie to a user's saved movie list
     * @param userID user
     * @param movieTitle movie
     */
    public synchronized void addSavedMovie(int userID, String movieTitle)
    {
        int movieID = movies.getMovieID(movieTitle);
        users.addSavedMovie(userID,movieID);
    }

    /**
     * returns a list of saved movies for a given user
     * @param userID user
     * @return
     */
    public synchronized ArrayList<ArrayList<String>> getSavedMovie(int userID)
    {
        ArrayList<Integer> savedMovies = users.getSavedMovie(userID);
        ArrayList<ArrayList<String>> movieYearPairs = new ArrayList<>();
        savedMovies.forEach(movieID -> {
            ArrayList<String> movieYear = new ArrayList<>();
            movieYear.add(movies.getTitle(movieID));
            movieYear.add(movies.getYear(movieID));
            movieYearPairs.add(movieYear);
        });
        return movieYearPairs;
    }

    /**
     * clears the saved movie list for a given user
     * @param userID user
     */
    public synchronized void clearSavedMovies(int userID)
    {
        users.clearSavedMovies(userID);
    }
}
