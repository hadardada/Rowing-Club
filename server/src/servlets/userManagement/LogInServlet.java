package servlets.userManagement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import bms.engine.userManager.UserManager;
import utilities.ServletUtils;
import utilities.SessionUtils;
import constants.Constants;
import static constants.Constants.USERNAME;
import static constants.Constants.PASSWORD;


@WebServlet(name = "LogInServlet", urlPatterns = "/loginToServer")


public class LogInServlet  extends HttpServlet {

    private final String MANAGER_MENU = "/menu/main.html";
    private final String MEMBER_MENU = "/menu/mainMember.html";
    private final String SIGN_UP_URL = "login.html";
    String errorMsg = "";

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
        String usernameFromParameter = request.getParameter(USERNAME).trim();
        String passwordFromParameter = request.getParameter(PASSWORD);

        if (usernameFromParameter == null || usernameFromParameter.isEmpty()) {
            //no username in session and no username in parameter -
            //redirect back to the index page
            //this returns an HTTP code and URL back to the browser telling it to load the URL
            response.sendRedirect(SIGN_UP_URL);
        } else {
            if (userManager.isUserLogged(usernameFromParameter)) {
                if (userManager.isUserManager(usernameFromParameter.trim()))
                    response.sendRedirect(MANAGER_MENU);
                else // redirect to non-manager member menu
                    response.sendRedirect(MEMBER_MENU);
            } else {
                if (userManager.isUserExists(usernameFromParameter)) // if user is a member in this club
                {
                    if (userManager.isPasswordCorrect(usernameFromParameter, passwordFromParameter)) {
                        userManager.addUser(usernameFromParameter);
                        request.getSession(true).setAttribute(Constants.USERNAME, usernameFromParameter.trim());
                        if (userManager.isUserManager(usernameFromParameter.trim()))
                            response.sendRedirect(MANAGER_MENU);
                        else // redirect to non-manager member menu
                            response.sendRedirect(MEMBER_MENU);
                    }
                    else{ // wrong password
                        errorMsg = "passwordErr";
                        response.sendRedirect(SIGN_UP_URL);

                    }
                }
                else{ //user is not even a member in the club.
                    errorMsg = "usernameErr";
                    response.sendRedirect(SIGN_UP_URL);

                }
            }
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect(SIGN_UP_URL);



    }

}