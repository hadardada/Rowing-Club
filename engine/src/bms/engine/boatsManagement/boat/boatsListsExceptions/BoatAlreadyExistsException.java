package bms.engine.boatsManagement.boat.boatsListsExceptions;

public class BoatAlreadyExistsException extends Exception {
    int serNum;
    private String EXCEPTION_MESSAGE;

    public BoatAlreadyExistsException(int serNum) {
        this.serNum = serNum;
        this.EXCEPTION_MESSAGE = "A boat with the same serial number: "+serNum+" already exists in the club";

    }

    @Override
    public String getMessage() {
        return EXCEPTION_MESSAGE;
    }

}
