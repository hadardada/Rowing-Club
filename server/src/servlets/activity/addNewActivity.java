package servlets.activity;

import bms.engine.Engine;
import bms.engine.activitiesManagement.activity.ActivityExceptions.*;
import bms.engine.activitiesManagement.ActivitiesManagement;

import bms.engine.boatsManagement.boat.Boat;
import bms.engine.boatsManagement.boat.boatsListsExceptions.BoatAlreadyExistsException;
import bms.engine.boatsManagement.boat.boatsListsExceptions.HelmsmanException;
import bms.engine.boatsManagement.boat.boatsListsExceptions.SingleWithTwoOarsException;
import com.google.gson.Gson;
import servlets.boats.BoatsParameter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.stream.Collectors;

@WebServlet(name = "addNewActivityServlet", urlPatterns = {"/activity/addNew"})
public class addNewActivity extends HttpServlet {
    private Gson gson = new Gson();
    Engine bmsEngine;


    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //super.doPost(req, resp);
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
        Boat.BoatType.BoatSize size = Boat.BoatType.BoatSize.valueOf(parameters.rowersNum.toUpperCase());
        Boat.BoatType newBoatType = new Boat.BoatType(size,parameters.singleOar, parameters.wide, parameters.helmsman, parameters.coastal);
        bmsEngine.addNewActivity(parameters.startTime, parameters.endTime,parameters.activityName, newBoatType,true);
    }
}
