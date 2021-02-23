package servlets.activity;

import bms.engine.Engine;
import bms.engine.activitiesManagement.activity.Activity;
import bms.engine.boatsManagement.boat.Boat;
import com.google.gson.Gson;
import servlets.boats.BoatsParameter;
import servlets.boats.UpdateRequestObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.stream.Collectors;

import static constants.Constants.ENGINE_ATTRIBUTE_NAME;

@WebServlet(name = "EditActivityServlet", urlPatterns = {"/activity/edit"})

public class EditActivityServlet extends HttpServlet {
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Engine bmsEngine = (Engine) req.getServletContext().getAttribute(ENGINE_ATTRIBUTE_NAME);
        String activityIdParameter = req.getParameter("id");
        if ((activityIdParameter != null) && (activityIdParameter != "")) {
            Integer activityId = Integer.parseInt(activityIdParameter);
            if (activityId != null) {
                Activity requestedActivity = bmsEngine.findActivityById(activityId);
                if (requestedActivity != null) {
                    activityParameters respActivity = new activityParameters(requestedActivity);
                    resp.setContentType("application/json");
                    try (PrintWriter out = resp.getWriter()) {
                        out.print(gson.toJson(respActivity));
                    }
                } else {
                    try (PrintWriter out = resp.getWriter()) {
                        resp.setStatus(404);
                        out.print("Could not find an activity with id: " + activityIdParameter);
                    }
                }
            } else {
                try (PrintWriter out = resp.getWriter()) {
                    resp.setStatus(400);
                    out.print("Could not find activity with no value in the id number field");
                }
            }
        }
        else{
            try (PrintWriter out = resp.getWriter()) {
                resp.setStatus(400);
                out.print("Could not find activity with no value in the id number field");
            }
        }


    }


    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Engine bmsEngine = (Engine)req.getServletContext().getAttribute(ENGINE_ATTRIBUTE_NAME);
        BufferedReader reader = req.getReader();
        String updateReqJsonString = reader.lines().collect(Collectors.joining());
        servlets.activity.UpdateRequestObject updateReq = gson.fromJson(updateReqJsonString, servlets.activity.UpdateRequestObject.class);
        updateReq.detectUpdate(bmsEngine);

        resp.setContentType("application/json");

        try (PrintWriter out = resp.getWriter()) {
            out.print(gson.toJson(updateReq));
        }

    }

    }