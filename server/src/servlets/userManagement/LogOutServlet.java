package servlets.userManagement;

import bms.engine.userManager.UserManager;
import utilities.ServletUtils;
import utilities.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;



@WebServlet(name = "LogOutServlet", urlPatterns = {"/logout"})


public class LogOutServlet extends HttpServlet {
    private String SIGN_UP_URL = "/boathouse/login.html";

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String usernameFromSession = SessionUtils.getUsername(request);
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        if (usernameFromSession != null) {
            userManager.removeUser(usernameFromSession);
            SessionUtils.clearSession(request);
            }
        response.sendRedirect(SIGN_UP_URL);
    }

}
