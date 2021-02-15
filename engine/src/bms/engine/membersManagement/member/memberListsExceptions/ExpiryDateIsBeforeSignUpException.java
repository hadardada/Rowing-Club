package bms.engine.membersManagement.member.memberListsExceptions;

public class ExpiryDateIsBeforeSignUpException extends Exception{
    private  String EXCEPTION_MESSAGE;
    private String member;
    public ExpiryDateIsBeforeSignUpException() {
        EXCEPTION_MESSAGE = "Subscription expiry date must be AFTER sin up date ";
    }
    public ExpiryDateIsBeforeSignUpException(String member) {
        this.member = member;
        this.EXCEPTION_MESSAGE = "A problem with member: " +member +  ": Subscription expiry date must be AFTER sign up date";

    }

    @Override
    public String getMessage() {
        return EXCEPTION_MESSAGE;
    }
}
