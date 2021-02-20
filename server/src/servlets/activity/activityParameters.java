package servlets.activity;
import java.time.LocalTime;

public class activityParameters {
    String activityName;
    String startTime;
    String endTime;
    String rowersNum;
    boolean singleOar;
    boolean wide;
    boolean helmsman;
    boolean coastal;
    boolean hasBoat;
    String boatName;

    public activityParameters(){}
    public activityParameters(String activityName, String startTime,
                              String endTime, String rowersNum,
                              boolean singleOar, boolean wide,
                              boolean helmsman,boolean coastal, boolean hasBoat, String boatName)
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
    }

}
