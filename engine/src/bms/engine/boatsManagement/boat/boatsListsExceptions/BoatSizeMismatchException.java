package bms.engine.boatsManagement.boat.boatsListsExceptions;

public class BoatSizeMismatchException extends Exception {
    int rowers;
    int boatCanContain;
    private  String EXCEPTION_MESSAGE ;

    public BoatSizeMismatchException(){
        EXCEPTION_MESSAGE = "Number of assigned members to boat does not match the number this boat should contain";
    }


    public BoatSizeMismatchException(int rowers, int boatCanContain) {
        this.rowers = rowers;
        this.boatCanContain = boatCanContain;
        if(rowers < boatCanContain)
            EXCEPTION_MESSAGE = "Not enough rowers were assigned to boat";
        else
            EXCEPTION_MESSAGE = "Too many rowers were assigned to boat";
    }

    @Override
    public String getMessage() {
        return EXCEPTION_MESSAGE;


    }
}