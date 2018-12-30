package server;

import database.DatabaseHandler;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * servlet that handles login action
 */
public class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html");
        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
        VelocityContext context = new VelocityContext();
        Template template = ve.getTemplate("login.html");
        PrintWriter out = response.getWriter();
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        out.println(writer.toString());
        //when the user logs in, grab their id, and genre preferences and saved movies
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //grab the username
        String userName = request.getParameter("username");
        //if the username doesn't already exist, give an alert and redirect to login.html --> do when I set up the database
        DatabaseHandler databaseHandler = (DatabaseHandler)(request.getServletContext().getAttribute("database"));
        boolean exists = databaseHandler.userExists(userName);
        if(!exists)
        {
            response.sendRedirect("login.html");
            return;
        }
        //grab the password
        String pwd = request.getParameter("pwd");
        System.out.println(userName+pwd);

        //access the database to see if the pwd+salt --> hash matches
        boolean correctInfo = databaseHandler.correctInfo(userName,pwd);
        //if the pwd doesn't match
        if(!correctInfo)
        {
            response.sendRedirect("login.html");
            return;
        }
        //if the pwd does match
        //log the user in
        HttpSession session = request.getSession();
        boolean logstate = true;
        session.getServletContext().setAttribute("logstate",logstate);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        session.getServletContext().setAttribute("time",dtf.format(now));
        //grab the user's name
        MovieRecommender movieRecommender = (MovieRecommender)request.getServletContext().getAttribute("data");
        int id = movieRecommender.getUserWithName(userName);;
        session.getServletContext().setAttribute("name", userName);
        session.getServletContext().setAttribute("id", id);
        //display the form asking for genre preference and 15 movies for them to rate
        response.sendRedirect("profile.html");
    }
}