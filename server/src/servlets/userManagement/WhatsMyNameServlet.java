package servlets.userManagement;

import bms.engine.Engine;
import bms.engine.engine.BmsEngine;
import bms.engine.userManager.UserManager;
import utilities.ServletUtils;
import utilities.SessionUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static constants.Constants.ENGINE_ATTRIBUTE_NAME;

@WebServlet(name = "WhatsMyNameServlet", urlPatterns = {"/userManagement/name"})

public class WhatsMyNameServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        BmsEngine engine = (Engine)req.getServletContext().getAttribute(ENGINE_ATTRIBUTE_NAME);
        String usernameFromSession = SessionUtils.getUsername(req);
        String emailParameter = req.getParameter("email");
        PrintWriter out = resp.getWriter();
        if (emailParameter != null) //asking for email address
            out.print(engine.getMemberByEmail(usernameFromSession).getEmailAddress());
        else //asks for name
            out.print(engine.getMemberByEmail(usernameFromSession).getName());
    }
}
