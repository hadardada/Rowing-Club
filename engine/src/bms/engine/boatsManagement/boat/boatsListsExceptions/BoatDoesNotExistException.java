package bms.engine.boatsManagement.boat.boatsListsExceptions;

public class BoatDoesNotExistException extends Exception{
    int boat = 0;
    String email = "";
    String EXCEPTION_MESSAGE = "No such boat (no. is found on list. "+
            "User is added but without a private boat";

    public BoatDoesNotExistException(int boat, String email) {
        this.boat = boat;
        this.email = email;
        if (email == null)
            this.EXCEPTION_MESSAGE = "No such boat (no. "+boat+") is found in the club";
        else
            this.EXCEPTION_MESSAGE = "No such boat (no. "+boat+") is found on list. "+
                "User "+email+" is added but without a private boat";
    }

    public BoatDoesNotExistException() {
    }
    @Override
    public String getMessage() {
        return EXCEPTION_MESSAGE;
    }
}

