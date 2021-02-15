package bms.engine.reservationsManagment.reservation.reservationsExceptions;

public class ApprovedReservationWithNoBoatException extends Exception{


    private final String EXCEPTION_MESSAGE = "A boat must be assined to reservation before approval.";

    public ApprovedReservationWithNoBoatException() { }

    @Override
    public String getMessage() {
        return EXCEPTION_MESSAGE;


    }
}
