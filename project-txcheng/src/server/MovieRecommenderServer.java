package server;

import database.DatabaseHandler;
import org.apache.velocity.app.VelocityEngine;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import recommender.MovieRecommender;

/**
 * The main server that is responsible for all servlets
 *
 */
public class MovieRecommenderServer {

    public final static int PORT = 8080;
    public static Object lock = new Object();
    public static void main(String[] args) throws Exception {
        //access the database
        DatabaseHandler databaseHandler = new DatabaseHandler();
        //create the movieRecommender data structure
        MovieRecommender movieRecommender = new MovieRecommender(lock);
        movieRecommender.loadFiles();

        //create the server
        Server server = new Server(PORT);
        //initialize Velocity
        VelocityEngine velocity = new VelocityEngine();
        velocity.init();
        //Context Handler/Session Manager
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        //add Servlets
        context.addServlet(HomeServlet.class, "/index.html");
        context.addServlet(LogoutServlet.class, "/logout");
        context.addServlet(LoginServlet.class, "/login.html");
        context.addServlet(RegisterServlet.class, "/register.html");
        context.addServlet(ProfileServlet.class, "/profile.html");
        context.addServlet(EditProfileServlet.class, "/editprofile.html");
        context.addServlet(RecommendationsServlet.class, "/recommendations.html");
        context.addServlet(AddRatingServlet.class, "/modifyrating.html");
        context.addServlet(SavedMovieServlet.class, "/savedmovies.html");
        context.addServlet(NewUserServlet.class, "/newuserform.html");
        context.addServlet(clearSavedServlet.class, "/clearsaved");
        //connect the server and the handler
        server.setHandler(context);
        //set the context path
        context.setContextPath("/");
        //set the attribute
        context.setAttribute("templateEngine", velocity);
        context.setAttribute("data", movieRecommender);
        context.setAttribute("database", databaseHandler);

        try {
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

