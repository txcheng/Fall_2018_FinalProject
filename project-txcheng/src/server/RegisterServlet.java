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
 * servlet that handles new user registration
 */
public class RegisterServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/html");
        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
        VelocityContext context = new VelocityContext();
        Template template = ve.getTemplate("register.html");
        PrintWriter out = response.getWriter();
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        out.println(writer.toString());
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //grab the username
        String userName = request.getParameter("username");
        //if the username already exists, give an alert and redirect to register.html --> do when I set up the database
        //if the username doesn't already exist, give an alert and redirect to login.html --> do when I set up the database
        DatabaseHandler databaseHandler = (DatabaseHandler)(request.getServletContext().getAttribute("database"));
        boolean exists = databaseHandler.userExists(userName);
        if(exists)
        {
            response.sendRedirect("register.html");
            return;
        }

        //grab the password
        String pwd = request.getParameter("pwd");
        //send to the database to create a new user
        databaseHandler.registerUser(userName,pwd);
        System.out.println(userName+pwd);

        MovieRecommender movieRecommender = (MovieRecommender)request.getServletContext().getAttribute("data");
        //assign an id insert new user in
        int id = movieRecommender.smallestID() - 2;
        User newUser = new User(id, userName);
        movieRecommender.addUser(newUser);
        //log the user in
        HttpSession session = request.getSession();
        boolean logstate = true;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        session.getServletContext().setAttribute("logstate",logstate);
        session.getServletContext().setAttribute("time",dtf.format(now));
        session.getServletContext().setAttribute("name", userName);
        session.getServletContext().setAttribute("id", id);
        System.out.println("session id:" +session.getServletContext().getAttribute("id"));
        response.setStatus(HttpServletResponse.SC_OK);
        //display the form asking for genre preference and 15 movies for them to rate
        response.sendRedirect("newuserform.html");
    }
}
