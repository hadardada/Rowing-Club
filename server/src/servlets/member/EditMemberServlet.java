package servlets.member;

import bms.engine.Engine;
import bms.engine.activitiesManagement.activity.Activity;
import bms.engine.boatsManagement.boat.boatsListsExceptions.BoatDoesNotExistException;
import bms.engine.membersManagement.member.Member;
import bms.engine.membersManagement.member.memberListsExceptions.EmailAddressAlreadyExistsException;
import bms.engine.membersManagement.member.memberListsExceptions.ExpiryDateIsBeforeSignUpException;
import com.google.gson.Gson;
import servlets.activity.activityParameters;
import utilities.SessionUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.Collectors;

import static constants.Constants.ENGINE_ATTRIBUTE_NAME;

@WebServlet(name = "EditMemberServlet", urlPatterns = {"/member/edit"})

public class EditMemberServlet extends HttpServlet {

    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Engine bmsEngine = (Engine) req.getServletContext().getAttribute(ENGINE_ATTRIBUTE_NAME);
        String memberEmail = req.getParameter("email").toLowerCase();
        String usernameFromSession = SessionUtils.getUsername(req);

        if ((memberEmail != null) && (memberEmail != "")) {
            Member requestedMember = bmsEngine.getMemberByEmail(memberEmail);
                if (memberEmail.equals("me")){
                    requestedMember = bmsEngine.getMemberByEmail(usernameFromSession);
                }
                if (requestedMember != null) {
                    MemberParameters responseMember = new MemberParameters(requestedMember);
                    resp.setContentType("application/json");
                    try (PrintWriter out = resp.getWriter()) {
                        out.print(gson.toJson(responseMember));
                    }
                } else {
                    try (PrintWriter out = resp.getWriter()) {
                        resp.setStatus(400);
                        out.print("Could not find a member with email address: " + memberEmail);
                    }
                }
            } else {
                try (PrintWriter out = resp.getWriter()) {
                    resp.setStatus(400);
                    out.print("Could not find a member with no value in the email field");
                }
            }
        }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Engine bmsEngine = (Engine) req.getServletContext().getAttribute(ENGINE_ATTRIBUTE_NAME);
        BufferedReader reader = req.getReader();
        String updateReqJsonString = reader.lines().collect(Collectors.joining());
        servlets.member.UpdateRequestObject updateReq = gson.fromJson(updateReqJsonString, servlets.member.UpdateRequestObject.class);
        try{
            updateReq.detectUpdate(bmsEngine);
            if (updateReq.whatToUpdate == updateReq.UPDATE_EMAIL)
                req.changeSessionId();
            resp.setContentType("application/json");
            try (PrintWriter out = resp.getWriter()) {
                out.print(gson.toJson(updateReq));
            }
        }
        catch (BoatDoesNotExistException | EmailAddressAlreadyExistsException | ExpiryDateIsBeforeSignUpException e){
            try (PrintWriter out = resp.getWriter()) {
                resp.setStatus(400);
                out.print(e.getMessage());
            }
        }

    }

    }




