package servlets.activity;
import bms.engine.boatsManagement.boat.Boat;

import java.time.LocalTime;

public class activityParameters {
    String activityName;
    LocalTime startTime;
    LocalTime endTime;
    Boat.BoatType boatTypes;

    public activityParameters(){}
}
