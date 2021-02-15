package bms.engine.membersManagement.member.memberListsExceptions;

public class EmailAddressAlreadyExistsException extends Exception{
    String member;
    private String EXCEPTION_MESSAGE = "This email address is already related to another member.";

    public EmailAddressAlreadyExistsException() {
    }

    public EmailAddressAlreadyExistsException(String member) {
        this.EXCEPTION_MESSAGE = member +": This email address is already related to another member";
    }

    @Override
    public String getMessage() {
        return EXCEPTION_MESSAGE;
    }

}
