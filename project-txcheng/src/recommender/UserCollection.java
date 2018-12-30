package recommender;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * UserCollection is a collection of user objects. This master class compiles all the user data together
 * reads user data from a csv file named "ratings.csv"
 * is used to find users that are most similar in rating to a given userID
 * and generates a list of suggested movieIDs based on a list of UserSimilarityScore
 */
public class UserCollection {
    private HashMap<Integer, User> users;
    /**
     * Constructor
     */
    public UserCollection()
    {
        users = new HashMap<>();
    }

    /**
     * Takes an input String Path and reads it, makes users from the parsed info, and adds
     * <userID, User> to the users TreeMap
     * @param dir target dir path
     */
    public void loadUsers(String dir)
    {
        //Open path
        Path dirPath = Paths.get(dir);
        try(DirectoryStream<Path> files = Files.newDirectoryStream(dirPath)){
            //look through the directory
            for(Path p: files)
            {
                String target = "ratings.csv";
                //get the correct File "ratings.csv"
                if(p.getFileName().toString().equals(target))
                {
                    //read through the file
                    File ratings = p.toFile();
                    //read by line
                    try(BufferedReader reader = new BufferedReader(new FileReader(ratings)))
                    {
                        //skip the title line
                        String fileLine = reader.readLine();
                        while((fileLine = reader.readLine()) != null)
                        {
                            //split line with ,
                            String [] line = fileLine.split(",");
                            //[userID, movieID, rating, timestamp]
                            int userId = Integer.parseInt(line[0]);
                            int movieId = Integer.parseInt(line[1]);
                            double rating = Double.parseDouble(line[2]);
                            //if user doesn't already exist,
                            if(!users.containsKey(userId))
                            {
                                //make a new one
                                User newUser = new User(userId);
                                //add it to the Map
                                users.put(userId, newUser);
                            }
                            //add rating
                            users.get(userId).addRating(movieId,rating);
                        }
                        break;
                    }
                    catch (IOException e)
                    {
                        System.out.println("error opening ratings.csv");
                    }
                }
            }
        }
        catch(IOException e)
        {
            System.out.println("Can't find the input directory.");
        }
    }

    /**
     * inserts a User object into the collection
     * @param inputUser User to be inserted
     */
    public void addUser(User inputUser)
    {
        users.put(inputUser.getId(),inputUser);
        System.out.println("added new user");
        System.out.println(users.get(inputUser.getId()).getName());
    }
    /**
     * Returns if a userID exists in the collection
     * @param userID userID
     * @return boolean if the userID exists
     */
    public boolean hasUser(int userID)
    {
        return users.containsKey(userID);
    }

    /**
     * Checks if a user with a given username exists
     * @param username
     * @return
     */
    public boolean hasUser(String username)
    {
        for(int id: users.keySet())
        {
            if(users.get(id).getName()!=null && username.equals(users.get(id).getName()))
            {
                return true;
            }

        }
        return false;
    }

    /**
     * addes a user's movie rating
     * @param userID
     * @param movieId
     * @param rating
     */
    public void addRating(int userID, int movieId, double rating)
    {
        users.get(userID).addRating(movieId,rating);
    }
    /**
     * returns the ID of a user with a given name
     * Returns 0 if there is no such user
     * @param username
     * @return
     */
    public int getUserWithName(String username)
    {
        for(int id: users.keySet())
        {
            if(username.equals(users.get(id).getName()))
            {
                return id;
            }

        }
        return 0;
    }
    /**
     * returns the smallest userID value
     * @return
     */
    public int getSmallestID()
    {
        SortedSet<Integer> keys = new TreeSet<>(users.keySet());
        return keys.first();
    }
    /**
     * Calculates the Pearsons coeff between userID1 and userID2
     * @param userID1 first user
     * @param userID2 second user
     * @return double similarityScore
     */
    private double calculatePearsonCoeffs(ArrayList<Double> userID1, ArrayList<Double>  userID2)
    {
        double output = 0;
        double sumX = 0;
        double sumY = 0;
        double sumXY = 0;
        double sumXX = 0;
        double sumYY = 0;
        for(int i = 0; i < userID1.size(); i++)
        {
            sumX += userID1.get(i);
            sumXX += Math.pow(userID1.get(i),2);
            sumY += userID2.get(i);
            sumYY += Math.pow(userID2.get(i),2);
            sumXY += (userID1.get(i) * userID2.get(i));
        }
        double n = userID1.size();
        double numerator = (n*sumXY) - (sumX*sumY);
        double denom = Math.pow(((n*sumXX)-(sumX*sumX)),0.5) * Math.pow((n*sumYY)-(sumY*sumY), 0.5);
        output =  numerator/denom;
        return output;
    }

    /**
     * Returns an sorted arraylist of UserSimilarityScore class( stores userID and similarity score) based on similarity to userID's user
     * @param userID input userID
     * @return ArrayList<UserSimilarityScore> similarUsers
     */
    private ArrayList<UserSimilarityScore> getSuggestedUsers(int userID)
    {
        ArrayList<UserSimilarityScore> similarityScores = new ArrayList<>();
        if(!users.containsKey(userID))
        {
            System.out.println("user doesn't exist");
        }
        //iterate through the collection
        users.forEach((k,v)->{
            //if the userID is not the same
            if(k != userID)
            {
                // generate list of all movies they both have seen
                ArrayList<ArrayList<Double>> intersection = users.get(userID).getRatingsforSameMovie(v);
                /*try{
                    intersection = users.get(userID).getRatingsforSameMovie(v);
                }
                catch (NullPointerException e)
                {
                    System.out.println("There are no similar users");
                    return;
                }*/
                // generate a similarity score
                double score = calculatePearsonCoeffs(intersection.get(0), intersection.get(1));
                if(!Double.isNaN(score) && Double.isFinite(score)) {
                    UserSimilarityScore newUser = new UserSimilarityScore(k, score);
                    similarityScores.add(newUser);
                }
            }
        });
        //sort the users by similarity Score
        Collections.sort(similarityScores);
        return similarityScores;
    }

    /**
     * Returns a list of movie suggestions for userID
     * @param userID input user
     * @param type type of rec (good or bad)
     * @return ArrayList<ArrayList<Integer>> movieIds
     */
    public ArrayList<Integer> getRecs(int userID, String type)
    {
        double upper = 0;
        double lower = 0;
        if(type.equals("good"))
        {
            upper = 5.0;
            lower = 4.999;
        }
        else if (type.equals("bad"))
        {
            upper = 1.0;
            lower = 0.5;
        }
        ArrayList<UserSimilarityScore> similarUsers = getSuggestedUsers(userID);
        if(similarUsers.size() == 0)
        {
            ArrayList<Integer> suggestedMovies = new ArrayList<>();
            return suggestedMovies;
        }
        Collections.sort(similarUsers);
        double max = similarUsers.get(0).getScore();
        ArrayList<Integer> suggestedMovies = new ArrayList<>();
        for(UserSimilarityScore user: similarUsers)
        {
            int id = user.getId();
            //use threshold here to get users that are about similar
            if(Math.abs(max-user.getScore())<0.001)
            {
                users.get(id).getMoviesWithinRating(lower, upper).forEach((m)->
                {
                    //if userID has not seen it
                    if(!users.get(userID).reviewedMovie(m) && !suggestedMovies.contains(m))
                    {
                        //add to the movie list
                        suggestedMovies.add(m);
                    }
                });
            }
            else{
                break;
            }
        }
        return suggestedMovies;
    }

    /**
     * returns a list of reviewed movies for a given user
     * @param userID user
     * @return
     */
    public ArrayList<Integer> getReviewedMovies(int userID)
    {
        ArrayList<Integer> output = users.get(userID).getMovies();

        return output;
    }

    /**
     * adds a movie to a user's saved movie list
     * @param userID user
     * @param movieID movie
     */
    public void addSavedMovie(int userID, int movieID)
    {
        users.get(userID).addSavedMovie(movieID);
    }

    /**
     * returns a list of a user's saved movies
     * @param userID user
     * @return
     */
    public ArrayList<Integer> getSavedMovie(int userID)
    {
        return users.get(userID).getSavedMovies();
    }

    /**
     * clear's the saved movie list of a user
     * @param userID
     */
    public void clearSavedMovies(int userID)
    {
        users.get(userID).clearSaved();
    }
}
