package servlets.activity;

import bms.engine.Engine;
import bms.engine.boatsManagement.boat.Boat;

import java.time.LocalTime;

public class UpdateRequestObject {
    public static final int UPDATE_NAME = 1;
    public static final int UPDATE_STARTS = 2;
    public static final int UPDATE_ENDS = 3;
    public static final int UPDATE_TYPE = 4;

    int whatToUpdate;
    String updateTo;
    activityParameters parameters;
    int activityId;

    public UpdateRequestObject() {
    }

    public void detectUpdate (Engine bmsEngine)  {
        switch (this.whatToUpdate){
            case (UPDATE_NAME): {
                bmsEngine.changeActivityName(activityId, updateTo);
                return;
            }
            case (UPDATE_STARTS):{
                LocalTime from = LocalTime.parse(updateTo);
                bmsEngine.changeStartingTime(activityId, from);
                return;
            }
            case (UPDATE_ENDS):{
                LocalTime to = LocalTime.parse(updateTo);
                bmsEngine.changeEndingTime(activityId, to);
                return;
            }
            case (UPDATE_TYPE):{
                if(updateTo == null) { // requset is to set activity to be without specified boat
                    bmsEngine.changeActivityBoatType(activityId, null);
                    updateTo = "Unspecified";
                }
                else{
                    Boat.BoatType.BoatSize size = Boat.BoatType.BoatSize.valueOf(parameters.rowersNum.toUpperCase());
                    Boat.BoatType reqType = new Boat.BoatType(size, parameters.singleOar, parameters.wide, parameters.helmsman, parameters.coastal);
                    bmsEngine.changeActivityBoatType(activityId, reqType);
                    updateTo= reqType.getShortName();
                }
            }
        }
    }

}
