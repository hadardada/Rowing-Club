package bms.engine.activitiesManagement.activity.ActivityExceptions;

public class EndTimeIsLowerException extends Exception{
    private final String EXCEPTION_MESSAGE = "Activity's Ending Time cannot be before Starting Time";

    public EndTimeIsLowerException() {
    }

    @Override
    public String getMessage() {
        return EXCEPTION_MESSAGE;
    }
}
