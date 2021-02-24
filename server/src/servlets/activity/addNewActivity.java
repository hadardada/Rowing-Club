package servlets.activity;
import java.time.LocalTime;


import bms.engine.Engine;
import bms.engine.activitiesManagement.activity.ActivityExceptions.*;

import bms.engine.boatsManagement.boat.Boat;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.stream.Collectors;

import static constants.Constants.ENGINE_ATTRIBUTE_NAME;

@WebServlet(name = "addNewActivityServlet", urlPatterns = {"/activity/addNew"})
public class addNewActivity extends HttpServlet {
    private Gson gson = new Gson();
    Engine bmsEngine;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        bmsEngine = (Engine)req.getServletContext().getAttribute(ENGINE_ATTRIBUTE_NAME);
        BufferedReader reader = req.getReader();
        String newActivityJsonString = reader.lines().collect(Collectors.joining());
        activityParameters newActivityParameters = gson.fromJson(newActivityJsonString, activityParameters.class);

        try {
            addNewActivity(newActivityParameters);
            resp.setStatus(200);
        }
        catch (EndTimeIsLowerException e){
            // all exceptions are being taken care of from browser
        }

    }
    private void addNewActivity(activityParameters parameters) throws EndTimeIsLowerException  {
        Boat.BoatType newBoatType = null;
        if (parameters.hasBoat) {
            Boat.BoatType.BoatSize size = Boat.BoatType.BoatSize.valueOf(parameters.rowersNum.toUpperCase());
            newBoatType = new Boat.BoatType(size, parameters.singleOar, parameters.wide, parameters.helmsman, parameters.coastal);
        }
        LocalTime from = LocalTime.parse(parameters.startTime);
        LocalTime to = LocalTime.parse(parameters.endTime);
        bmsEngine.addNewActivity(from, to,parameters.activityName, newBoatType,false);
    }
}
