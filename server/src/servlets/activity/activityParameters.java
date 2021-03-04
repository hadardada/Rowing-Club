package servlets.activity;
import bms.engine.activitiesManagement.activity.Activity;

import java.time.LocalTime;

public class activityParameters {
    String activityName;
    String startTime;
    String endTime;
    String rowersNum;
    Boolean singleOar;
    Boolean wide;
    Boolean helmsman;
    Boolean coastal;
    Boolean hasBoat;
    String boatName;
    String id;

    public activityParameters(){}
    public activityParameters(String activityName, String startTime,
                              String endTime, String rowersNum,
                              Boolean singleOar, Boolean wide,
                              Boolean helmsman,Boolean coastal, Boolean hasBoat, String boatName,String id)
    {
        this.activityName = activityName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.rowersNum = rowersNum;
        this.singleOar = singleOar;
        this.wide = wide;
        this.helmsman = helmsman;
        this.coastal = coastal;
        this.hasBoat = hasBoat;
        this.boatName = boatName;
        this.id = id;
    }

    public activityParameters(Activity activity){
        this.activityName = activity.getName();
        this.startTime = activity.getStarts().toString();
        this.endTime = activity.getEnds().toString();
        if (activity.getSpecifiedType() == null)
            this.boatName = "Unspecified";
        else
            this.boatName = activity.getSpecifiedType().getShortName();
        //this.id = activity.getId();

    }

}
