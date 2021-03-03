package servlets.activity;

import bms.engine.Engine;
import bms.engine.activitiesManagement.activity.Activity;
import com.google.gson.Gson;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;


import static constants.Constants.ENGINE_ATTRIBUTE_NAME;

@WebServlet(name = "displayActivityServlet", urlPatterns = {"/activity/showAll"})
public class displayActivity extends HttpServlet {
    private Gson gson = new Gson();
    Engine bmsEngine;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        bmsEngine = (Engine)req.getServletContext().getAttribute(ENGINE_ATTRIBUTE_NAME);
        List<Activity> activitiesList = this.bmsEngine.getActivities();
        List<activityParameters> activitiesParametersList = new ArrayList<activityParameters>();
        for (Activity activity : activitiesList)
        {
            activitiesParametersList.add(convertActivityToParameters(activity));
        }
        PrintWriter out = resp.getWriter();
        out.print(gson.toJson(activitiesParametersList));
        resp.setStatus(200);
    }

    private activityParameters convertActivityToParameters(Activity activity){
        boolean hasBoat = false;
        String boatName = "";
        String boatSize = "";
        Boolean singleOar = false;
        Boolean wide = false;
        Boolean helmsman = false;
        Boolean coastal = false;

        if (!(activity.getSpecifiedType() == null)){
            hasBoat = true;
            boatName = activity.getSpecifiedType().getShortName();
            boatSize = activity.getSpecifiedType().getBoatSize().toString();
            singleOar = activity.getSpecifiedType().isSingleOar();
            wide = activity.getSpecifiedType().isWide();
            helmsman = activity.getSpecifiedType().hasHelmsman();
            coastal = activity.getSpecifiedType().isCoastal();

        }
        String from = activity.getStarts().toString();
        String to = activity.getEnds().toString();
        String id = String.valueOf(activity.getId());
        activityParameters activityParameter = new activityParameters(activity.getName()
                ,from,to,boatSize
                ,singleOar
                ,wide
                ,helmsman
                ,coastal
                ,hasBoat,boatName,id);
        return activityParameter;
    }

}
