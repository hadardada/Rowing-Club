package servlets.boats;

import bms.engine.Engine;

public class UpdateRequestObject {
    public static final int UPDATE_NAME = 1;
    public static final int UPDATE_STATUS = 2;
    public static final int UPDATE_PRIVATE = 3;
    public static final int UPDATE_OARS = 4;
    public static final int UPDATE_COXSWAIN = 5;
    public static final int UPDATE_COASTAL = 6;

    int whatToUpdate;
    String updateTo;
    int boatIdNum;

    public UpdateRequestObject() {
    }

    public void detectUpdate (Engine bmsEngine){
        switch (this.whatToUpdate){
            case (UPDATE_NAME): {
                bmsEngine.updateBoatName(boatIdNum, updateTo);
                return;
            }
            case (UPDATE_STATUS):{
                Boolean newStatus = Boolean.parseBoolean(updateTo);
                bmsEngine.changeBoatStatus(boatIdNum, newStatus);
                return;
            }
        }
    }

}
