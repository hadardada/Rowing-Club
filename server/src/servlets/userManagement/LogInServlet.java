package servlets.userManagement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import userManager.UserManager;
import utilities.ServletUtils;
import utilities.SessionUtils;
import constants.Constants;
import static constants.Constants.USERNAME;

@WebServlet(name = "LogInServlet", urlPatterns = "/login")


public class LogInServlet  extends HttpServlet {

    private final String MANAGER_MENU = "managerIndex.html";
    private final String MEMBER_MENU = "memberIndex.html";
    private final String SIGN_UP_URL = "login.html";

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String usernameFromSession = SessionUtils.getUsername(request);
        UserManager userManager = ServletUtils.getUserManager(getServletContext());

        //user is already logged in
        if (usernameFromSession != null) {
            //if user is manager, redirect to manager main menu
            if(userManager.isUserManager(usernameFromSession))
                response.sendRedirect(MANAGER_MENU);
            else // redirect to non-manager member menu
                response.sendRedirect(MEMBER_MENU);
            return;
        }

        //user is not logged in yet
        String usernameFromParameter = request.getParameter(USERNAME);
        if (usernameFromParameter == null || usernameFromParameter.trim().isEmpty()) {
            //no username in session and no username in parameter -
            //redirect back to the index page
            //this returns an HTTP code and URL back to the browser telling it to load the URL
            response.sendRedirect(SIGN_UP_URL);
        } else {
            //normalize the username value
            usernameFromParameter = usernameFromParameter.trim();

            if (userManager.isUserExists(usernameFromParameter)) {
                String errorMessage = "Username " + usernameFromParameter + " already exists. Please enter a different username.";
                // username already exists, forward the request back to index.jsp
                // with a parameter that indicates that an error should be displayed
                // the request dispatcher obtained from the servlet context is one that MUST get an absolute path (starting with'/')
                // and is relative to the web app root
                // see this link for more details:
                // http://timjansen.github.io/jarfiller/guide/servlet25/requestdispatcher.xhtml
                request.setAttribute(Constants.USER_NAME_ERROR, errorMessage);
               // getServletContext().getRequestDispatcher(LOGIN_ERROR_URL).forward(request, response);
            } else {
                //add the new user to the users list
                userManager.addUser(usernameFromParameter);
                //set the username in a session so it will be available on each request
                //the true parameter means that if a session object does not exists yet
                //create a new one

                request.getSession(true).setAttribute(Constants.USERNAME, usernameFromParameter.trim());

                //redirect the request to the chat room - in order to actually change the URL
                System.out.println("On login, request URI is: " + request.getRequestURI());
                if(userManager.isUserManager(usernameFromParameter.trim()))
                    response.sendRedirect(MANAGER_MENU);
                else // redirect to non-manager member menu
                    response.sendRedirect(MEMBER_MENU);
            }
        }
    }

    }
