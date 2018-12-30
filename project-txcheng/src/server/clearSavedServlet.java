package server;

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
import java.util.ArrayList;

/**
 * servlet that handles the clearing of the saved movies list
 */
public class clearSavedServlet  extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //check if the user is logged in
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
        MovieRecommender movieRecommender = (MovieRecommender)request.getServletContext().getAttribute("data");
        int id = (int) session.getServletContext().getAttribute("id");
        movieRecommender.clearSavedMovies(id);
        response.sendRedirect("savedmovies.html");


        //session.getServletContext().setAttribute("gmovies" , movieGRecommendations);
    }
}
