package server;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Handles logout action
 */
public class LogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //check if there is a user logged in
        HttpSession session = request.getSession();
        if(session.getServletContext().getAttribute("logstate") == null)
        {
            response.sendRedirect("profile.html");
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
        logstate = false;
        session.getServletContext().setAttribute("logstate",logstate);
        response.sendRedirect("index.html");
    }
}
