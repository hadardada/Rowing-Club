package servlets.boats;

import bms.engine.Engine;
import bms.engine.boatsManagement.boat.boatsListsExceptions.HelmsmanException;
import bms.engine.boatsManagement.boat.boatsListsExceptions.SingleWithTwoOarsException;

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

    public void detectUpdate (Engine bmsEngine)  {
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
            case (UPDATE_PRIVATE):{
                Boolean newOwnership;
                if (updateTo.equals("false")){
                    newOwnership = false;
                }
                else{
                    newOwnership = true;
                    String memberId = updateTo;
                }
                bmsEngine.updatePrivate(boatIdNum, newOwnership);
                return;
            }
            case (UPDATE_COASTAL):
                Boolean isCoastal = Boolean.parseBoolean(updateTo);
                bmsEngine.changeCoastal(boatIdNum, isCoastal);
                break;
            case (UPDATE_COXSWAIN):
                Boolean hasCoxswain = Boolean.parseBoolean(updateTo);
                try {
                    bmsEngine.changeHelmsman(boatIdNum, hasCoxswain);
                    break;
                }
                catch(HelmsmanException e){
                    // Do nothing - exception is not expected
                }
            case (UPDATE_OARS):{
                Boolean hasOneOar = Boolean.parseBoolean(updateTo);
                try {
                    bmsEngine.changeOars(boatIdNum, hasOneOar);
                    break;
                } catch (SingleWithTwoOarsException e) {
                    // Do nothing - exception is not expected
                }
            }


        }
        updateTo =bmsEngine.getBoatById(boatIdNum).getBoatType().getShortName();
    }

}
