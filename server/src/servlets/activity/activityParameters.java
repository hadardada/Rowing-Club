package servlets.activity;
import bms.engine.boatsManagement.boat.Boat;

import java.time.LocalTime;

public class activityParameters {
    String activityName;
    LocalTime startTime;
    LocalTime endTime;
    String rowersNum;
    boolean singleOar;
    boolean wide;
    boolean helmsman;
    boolean coastal;

    public activityParameters(){}
}
