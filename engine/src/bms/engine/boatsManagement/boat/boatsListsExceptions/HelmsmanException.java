package bms.engine.boatsManagement.boat.boatsListsExceptions;

public class HelmsmanException extends Exception {

    private  String EXCEPTION_MESSAGE ;

    public HelmsmanException(){ }

    public HelmsmanException(int eight){
        if (eight == 8) // eight without coxwain
            EXCEPTION_MESSAGE = "A Boat propelled by eight rowers must have a coxswain.";
        else // one with coxwain
            EXCEPTION_MESSAGE = " A Boat propelled by one rower cannot have a coxswain.";
    }


    @Override
    public String getMessage() {
        return EXCEPTION_MESSAGE;
    }

}

