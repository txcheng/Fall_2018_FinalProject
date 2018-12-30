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
 * servlet that handles the display of the user's saved movies as well as the addition and clearing of the list
 */
public class SavedMovieServlet extends HttpServlet {

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
        //if the user is logged in, show saved movies
        //if the user is not logged in, redirect to login page and give window alert

        response.setContentType("text/html");
        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
        VelocityContext context = new VelocityContext();
        Template template = ve.getTemplate("savedmovies.html");
        MovieRecommender movieRecommender = (MovieRecommender)request.getServletContext().getAttribute("data");
        int id = (int) (session.getServletContext().getAttribute("id"));
        ArrayList<ArrayList<String>> savedMovie = movieRecommender.getSavedMovie((id));
        PrintWriter out = response.getWriter();
        StringWriter writer = new StringWriter();
        context.put("savedMovie",savedMovie);
        template.merge(context, writer);
        out.println(writer.toString());
        //session.getServletContext().setAttribute("gmovies" , movieGRecommendations);
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String movieTitle = request.getParameter("movietitle");
        MovieRecommender movieRecommender = (MovieRecommender)request.getServletContext().getAttribute("data");
        //grab the user's id
        int id = (int)session.getServletContext().getAttribute("id");
        movieRecommender.addSavedMovie(id,movieTitle);
        response.sendRedirect("savedmovies.html");
    }
}
