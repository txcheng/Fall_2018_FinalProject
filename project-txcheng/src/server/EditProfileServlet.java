package server;


import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
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
 * servlet that handles when the user edits genre preferences
 */
public class EditProfileServlet extends HttpServlet {

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
        //if the user is logged in, show edit profile form
        //if the user is not logged in, redirect to login page and give window alert
        response.setContentType("text/html");
        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
        VelocityContext context = new VelocityContext();
        Template template = ve.getTemplate("editprofile.html");
        //grab the genres from the form and edit the attribute in the session
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
        response.sendRedirect("profile.html");
    }
}