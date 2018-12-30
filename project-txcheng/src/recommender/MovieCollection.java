package recommender;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * The MovieCollection Class is a collection of many Movie class objects.
 * This class can populate its HashMap with a filepath input and can sort a given ArrayList of movieIds by Year (descending)
 */
public class MovieCollection {
    private HashMap <Integer, Movie> movies;

    /**
     * Constructor
     */
    public MovieCollection()
    {
        movies = new HashMap<>();
    }

    /**
     * Reads through a dir, grabs "movies.cv" , creating movies and adding the <movieID, Movie> (K,V) pair
     * to the TreeMap movies
     * @param dir directory
     */
    public void loadMovies(String dir)
    {
        //Open path
        Path dirPath = Paths.get(dir);
        try(DirectoryStream<Path> files = Files.newDirectoryStream(dirPath)){
            //look through the directory
            for(Path p: files)
            {
                String target = "movies.csv";
                //get the correct File "movies.csv"
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
                            int movieId;
                            String year;
                            String title;
                            ArrayList<String> genres = new ArrayList<>();
                            if(fileLine.contains("\"\""))
                            {
                                String [] line = fileLine.split(",");
                                movieId = Integer.parseInt(line[0]);
                                String [] inner = line[1].split("\\(");
                                title = inner[0].trim();
                                for(int i = 1; i < inner.length-1; i++)
                                {
                                    title += (" (" + inner[i]);
                                }
                                String [] yearInner = inner[inner.length-1].split("\\)");
                                year = yearInner[0];
                                //parse the genres
                                String genreInfo = line[2];
                                String [] preProccessedGenres = genreInfo.split("\\|");
                                //System.out.println(genreInfo);
                                for(String genre: preProccessedGenres)
                                {
                                    genres.add(genre.trim().toLowerCase());
                                }
                            }
                            else if(fileLine.contains("\""))
                            {
                                String [] line = fileLine.split("\"");
                                movieId = Integer.parseInt(line[0].split(",")[0]);
                                String[] inner = line[1].split("\\(");
                                title = inner[0].trim();
                                for(int i = 1; i < inner.length-1; i++)
                                {
                                    title += (" (" + inner[i]);
                                }
                                //last value in () is the year
                                String [] yearInner = inner[inner.length-1].split("\\)");
                                year = yearInner[0];
                                String genreInfo = line[2];
                                genreInfo = genreInfo.substring(1);
                                String [] preProccessedGenres = genreInfo.split("\\|");
                                for(String genre: preProccessedGenres)
                                {
                                    //System.out.println(genre);
                                    genres.add(genre.trim().toLowerCase());
                                }
                            }
                            else
                            {
                                String [] line = fileLine.split(",");
                                movieId = Integer.parseInt(line[0]);
                                String [] inner = line[1].split("\\(");
                                title = inner[0].trim();
                                for(int i = 1; i < inner.length-1; i++)
                                {
                                    title += (" (" + inner[i]);
                                }
                                String [] yearInner = inner[inner.length-1].split("\\)");
                                year = yearInner[0];
                                //parse the genres
                                String genreInfo = line[2];
                                String [] preProccessedGenres = genreInfo.split("\\|");
                                for(String genre: preProccessedGenres)
                                {
                                    //System.out.println(genre);
                                    genres.add(genre.trim().toLowerCase());
                                }
                            }
                            //if user doesn't already exist,
                            if(!movies.containsKey(movieId))
                            {
                                //make a new one
                                Movie newMovie = new Movie(movieId,title,year, genres);
                                //add it to the Map
                                movies.put(movieId, newMovie);
                            }
                        }
                        break;
                    }
                    catch (IOException e)
                    {
                        System.out.println("Error opening movies.csv");
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
     * checks if a movie is of a given genre
     * @param movieID
     * @param genre
     * @return
     */
    public boolean isGenre(int movieID, String genre)
    {
        return movies.get(movieID).isGenre(genre.trim().toLowerCase());
    }

    /**
     * returns the year for a given movie
     * @param movieID movie
     * @return
     */
    public String getYear(int movieID)
    {
        return movies.get(movieID).getYear();
    }

    /**
     * returns the title for a given movie
     * @param movieID movie
     * @return
     */
    public String getTitle(int movieID)
    {
        return movies.get(movieID).getTitle();
    }
    /**
     * returns the movieID for a given movie
     * Returns 0 if it can't find the movie
     * @param movieTitle
     * @return
     */
    public int getMovieID(String movieTitle)
    {
        for(int movieID: movies.keySet())
        {
            if(movies.get(movieID).getTitle().equals(movieTitle))
            {
                return movieID;
            }
        }
        return 0;
    }
}
