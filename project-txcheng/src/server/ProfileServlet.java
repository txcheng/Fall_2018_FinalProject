package server;

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
 * servlet that handles the user's profile display
 */
public class ProfileServlet extends HttpServlet {

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
        //if the user is logged in, show profile
        response.setContentType("text/html");
        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
        VelocityContext context = new VelocityContext();
        Template template = ve.getTemplate("profile.html");
        ArrayList<String> genres = (ArrayList)session.getServletContext().getAttribute("genres");
        String name = (String)session.getServletContext().getAttribute("name");
        String time = (String)session.getServletContext().getAttribute("time");
        MovieRecommender movieRecommender = (MovieRecommender)request.getServletContext().getAttribute("data");
        int id = (int) (session.getServletContext().getAttribute("id"));
        ArrayList<ArrayList<String>> reviewedMovies = movieRecommender.getReviewedMovies(id);
        System.out.println("reviewed movies: " + reviewedMovies.size());
        context.put("reviewedMovies", reviewedMovies);
        context.put("username", name);
        context.put("genres", genres);
        context.put("lastlogin", time);
        PrintWriter out = response.getWriter();
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        out.println(writer.toString());
    }
}