//package servlets.activity;
//
//import bms.engine.Engine;
//import bms.engine.activitiesManagement.activity.Activity;
//import bms.engine.activitiesManagement.activity.ActivityExceptions.EndTimeIsLowerException;
//import bms.engine.boatsManagement.boat.Boat;
//import bms.engine.boatsManagement.boat.boatsListsExceptions.BoatAlreadyExistsException;
//import bms.engine.boatsManagement.boat.boatsListsExceptions.HelmsmanException;
//import bms.engine.boatsManagement.boat.boatsListsExceptions.SingleWithTwoOarsException;
//import com.google.gson.Gson;
//import servlets.boats.BoatsParameter;
//
//import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.time.LocalTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@WebServlet(name = "displayActivityServlet", urlPatterns = {"/activity/showAll"})
//public class displayActivity {
//    private Gson gson = new Gson();
//    Engine bmsEngine;
//
//
//    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        List<Activity> activitiesList = this.bmsEngine.getActivities();
//        List<activityParameters> activitiesParametersList = new ArrayList<>();
//        for (Activity activity : activitiesList)
//        {
//            boolean hasBoat = true;
//            if (activity.getSpecifiedType() == null){
//                hasBoat = false;
//            }
//            activityParameters activityParameter = new activityParameters(activity.getName()
//                    ,activity.getStarts(),activity.getEnds(),
//                    activity.getSpecifiedType().getBoatSize().toString(),
//                    activity.getSpecifiedType().isSingleOar(),
//                    activity.getSpecifiedType().isWide(),
//                    activity.getSpecifiedType().hasHelmsman(),
//                    activity.getSpecifiedType().isCoastal(),hasBoat);
//            activitiesParametersList.add(activityParameter);
//        }
//        PrintWriter out = resp.getWriter();
//        String activityList = gson.toJson(activitiesParametersList,activityParameters.class);
//        out.print(activityList);
//        resp.setStatus(200);
//    }
//}
