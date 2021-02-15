package bms.engine.boatsManagement.boat.boatsListsExceptions;

public class SingleWithTwoOarsException extends Exception{
    private  String EXCEPTION_MESSAGE = "Single sized boat cannot have one oar.\n" ;

    public SingleWithTwoOarsException(){ }


    @Override
    public String getMessage() {
        return EXCEPTION_MESSAGE;
    }
}
