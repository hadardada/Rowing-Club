package bms.engine.boatsManagement.boat.boatsListsExceptions;

public class BoatDoesNotExistException extends Exception{
    private final String EXCEPTION_MESSAGE = "No such boat is found on list. User is added but without a private boat";

    public BoatDoesNotExistException() {
    }

    @Override
    public String getMessage() {
        return EXCEPTION_MESSAGE;
    }
}

