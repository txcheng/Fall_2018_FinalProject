package server;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import recommender.MovieRecommender;
import recommender.User;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * servlet that handles the profile creation of a new user
 */
public class NewUserServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //we display the form
        response.setContentType("text/html");
        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
        VelocityContext context = new VelocityContext();
        //show them a form that asks for their preferred genres and make them rate movies
        Template template = ve.getTemplate("newuserform.html");
        PrintWriter out = response.getWriter();
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        out.println(writer.toString());
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //process the inputs and add info to the db
        //get the genres
        String genre1 = request.getParameter("genre1");
        String genre2 = request.getParameter("genre2");
        ArrayList<String> genres = new ArrayList<>();
        genres.add(genre1);
        genres.add(genre2);
        //add the arraylist to the session
        HttpSession session = request.getSession();
        session.getServletContext().setAttribute("genres", genres);
        session.getServletContext().setAttribute("logstate", true);
        //get the ratings
        HashMap<Integer, Double> profileRatings = new HashMap<>();
        for(int i = 1; i < 16; i++)
        {
            String ratingS = request.getParameter("movie"+i+"-rating");
            //go through the form data and save it in movies
            double rating = Double.parseDouble(ratingS);
            profileRatings.put(i, rating);
        }
        //grab user's id
        MovieRecommender movieRecommender = (MovieRecommender)request.getServletContext().getAttribute("data");
        int id = (int)request.getServletContext().getAttribute("id");
        System.out.println("id is: " +id);
        System.out.println(movieRecommender.hasUser(id));
        //add to movieRecommender
        for(int movieID: profileRatings.keySet())
        {
            //input the ratings if the rating is non-zero
            if(profileRatings.get(movieID)>0)
            {
                movieRecommender.addRating(id,movieID, profileRatings.get(movieID));
            }
        }
        //redirect to profile
        response.sendRedirect("profile.html");
    }
}