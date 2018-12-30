package recommender;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * this class was made to count the popularity for all the movies in the given csv files
 * (number of ratings)
 */
public class MovieCountCollection {
    private HashMap<Integer,Integer> movieCount;
    public MovieCountCollection()
    {
        movieCount = new HashMap<>();
    }

    /**
     * loads the file
     * @param dir target dir
     */
    public void load(String dir)
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
                            //if movie doesn't already exist,
                            if(!movieCount.containsKey(movieId))
                            {
                                //add it to the Map
                                movieCount.put(movieId, 1);
                            }
                            else
                            {
                                movieCount.put(movieId, movieCount.get(movieId)+1);
                            }
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
     * returns the popularity of a given movie
     * @param movieID mobie
     * @return
     */
    public int getCount(int movieID)
    {
        return movieCount.get(movieID);
    }
}
