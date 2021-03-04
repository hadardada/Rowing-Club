package servlets.userManagement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import bms.engine.userManager.UserManager;
import com.google.gson.Gson;
import utilities.ServletUtils;
import utilities.SessionUtils;
import constants.Constants;
import static constants.Constants.USERNAME;
import static constants.Constants.PASSWORD;


@WebServlet(name = "LogInServlet", urlPatterns = "/loginToServer")


public class LogInServlet  extends HttpServlet {
    private Gson gson = new Gson();

    private final String MANAGER_MENU = "/boathouse/menu/main.html";
    private final String MEMBER_MENU = "/boathouse/menu/mainMember.html";
    private final String SIGN_UP_URL = "/boathouse/login.html";
    PasswordError errorLogin = new PasswordError();

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String usernameFromSession = SessionUtils.getUsername(request);
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        //user is not logged in yet
        String usernameFromParameter = request.getParameter(USERNAME).trim();
        String passwordFromParameter = request.getParameter(PASSWORD);
        errorLogin.errorMsg = "";
        if (usernameFromSession != null) {
            //user is already logged in
            errorLogin.errorMsg = "already";
            response.sendRedirect(SIGN_UP_URL);
        }


        else if (usernameFromParameter == null || usernameFromParameter.isEmpty()) {
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
                        errorLogin.errorMsg = "passwordErr";
                        errorLogin.sentUserName = usernameFromParameter;
                        response.sendRedirect(SIGN_UP_URL);

                    }
                }
                else{ //user is not even a member in the club.
                    errorLogin.errorMsg = "usernameErr";
                    errorLogin.sentUserName = usernameFromParameter;
                    response.sendRedirect(SIGN_UP_URL);

                }
            }
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try (PrintWriter out = response.getWriter()) {
            out.print(gson.toJson(errorLogin));
            errorLogin.errorMsg="";
        }
    }

    public static class PasswordError {
        public String errorMsg;
        public String sentUserName;

        public PasswordError(){
            this.errorMsg="";
            this.sentUserName="";
        }
    }
}