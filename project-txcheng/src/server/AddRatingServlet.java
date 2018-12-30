package server;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import recommender.MovieRecommender;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * servlet that handles adding new ratings for a given user
 */
public class AddRatingServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if(session.getServletContext().getAttribute("logstate") == null)
        {
            response.sendRedirect("index.html");
            return;
        }
        boolean logstate = (boolean) (session).getServletContext().getAttribute("logstate");
        //if the user is not logged in, redirect to login page and give window alert
        if(!logstate)
        {
            //alert the user that they are not logged in
            //redirect
            response.sendRedirect("index.html");
            return;
        }
        //if the user is logged in, show form

        response.setContentType("text/html");
        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
        VelocityContext context = new VelocityContext();
        Template template = ve.getTemplate("modifyrating.html");
        PrintWriter out = response.getWriter();
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        out.println(writer.toString());
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = request.getSession();
        MovieRecommender movieRecommender = (MovieRecommender)session.getServletContext().getAttribute("data");
        //get the movie name
        String movieTitle = request.getParameter("movieTitle");
        movieTitle =  StringEscapeUtils.escapeHtml4(movieTitle);
        //get the movieID from the name
        int movieID = movieRecommender.getMovieID(movieTitle);
        if(movieID == 0)
        {
            response.sendRedirect("modifyrating.html");
            return;
        }
        //get the userID
        int userID = (int)session.getAttribute("userID");
        //get the rating
        Double rating = Double.parseDouble(request.getParameter("ratings"));
        //add the rating to the data structure
        movieRecommender.addRating(userID,movieID,rating);
        System.out.println("rated the movie: "+movieTitle);
    }
}