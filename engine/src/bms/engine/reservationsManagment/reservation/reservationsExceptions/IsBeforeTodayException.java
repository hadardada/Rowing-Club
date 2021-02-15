package bms.engine.reservationsManagment.reservation.reservationsExceptions;

public class IsBeforeTodayException extends Exception{
    private final String EXCEPTION_MESSAGE = "Training Date choice cannot be before today";

    public IsBeforeTodayException() {
    }

    @Override
    public String getMessage() {
        return EXCEPTION_MESSAGE;

    }
}
