package bms.engine.reservationsManagment.reservation.reservationsExceptions;

public class ParticipentRowerIsOnListException extends Exception {
        private final String EXCEPTION_MESSAGE = "Participant rower (=reservation owner) cannot be a part of the requsted rowers on reservation";

        public ParticipentRowerIsOnListException() {
        }

        @Override
        public String getMessage() {
                return EXCEPTION_MESSAGE;

        }
}
