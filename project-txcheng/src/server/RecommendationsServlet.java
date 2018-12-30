package server;

import database.DatabaseHandler;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import recommender.MovieRecommender;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

/**
 * Servlet that handles the recommendations page
 */
public class RecommendationsServlet extends HttpServlet {

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
        //if the user is logged in, show form to ask for number of recommendations and how they should be sorted
        response.setContentType("text/html");
        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
        VelocityContext context = new VelocityContext();
        Template template = ve.getTemplate("pre-recommendations.html");
        PrintWriter out = response.getWriter();
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        out.println(writer.toString());
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        ArrayList<String> genres = (ArrayList) session.getServletContext().getAttribute("genres");

        String sortType = request.getParameter("sortType");
        int numGRecs = Integer.parseInt(request.getParameter("recNum"));
        int numBRecs = Integer.parseInt(request.getParameter("anti-recNum"));
        MovieRecommender movieRecommender = (MovieRecommender)request.getServletContext().getAttribute("data");
        //grab the user's id
        int id = (int)session.getServletContext().getAttribute("id");
        //pass the user info to movie recommender
        ArrayList<ArrayList<String>> movieGRecommendations;
        ArrayList<ArrayList<String>> movieBRecommendations;
        movieGRecommendations = movieRecommender.makeRecommendationList(id,numGRecs,"good", genres, sortType);
        movieBRecommendations = movieRecommender.makeRecommendationList(id,numBRecs,"bad", genres, sortType);
        session.getServletContext().setAttribute("gmovies" , movieGRecommendations);
        session.getServletContext().setAttribute("bmovies" , movieBRecommendations);
        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
        VelocityContext context = new VelocityContext();
        Template template = ve.getTemplate("recommendations.html");
        context.put("movieRec",movieGRecommendations);
        context.put("antiMovieRec",movieBRecommendations);
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        PrintWriter out = response.getWriter();
        out.println(writer.toString());
    }
}
