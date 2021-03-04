package servlets.member;

import bms.engine.Engine;
import bms.engine.boatsManagement.boat.boatsListsExceptions.BoatDoesNotExistException;
import bms.engine.membersManagement.member.memberListsExceptions.EmailAddressAlreadyExistsException;
import bms.engine.membersManagement.member.memberListsExceptions.ExpiryDateIsBeforeSignUpException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UpdateRequestObject {
    public static final int UPDATE_NAME = 1;
    public static final int UPDATE_EMAIL = 2;
    public static final int UPDATE_AGE = 3;
    public static final int UPDATE_PHONE = 4;
    public static final int UPDATE_PASSWORD = 5;
    public static final int UPDATE_PRIVATE = 6;
    public static final int UPDATE_MANAGER = 7;
    public static final int UPDATE_LEVEL = 8;
    public static final int UPDATE_NOTES = 9;
    public static final int UPDATE_EXP = 10;
    public static final int UPDATE_JOIN = 11;


    int whatToUpdate;
    String updateTo;
    String memberId;

    public UpdateRequestObject() {
    }

    public void detectUpdate (Engine bmsEngine) throws EmailAddressAlreadyExistsException, BoatDoesNotExistException, ExpiryDateIsBeforeSignUpException {
        memberId = memberId.toLowerCase();
        switch (this.whatToUpdate){
            case (UPDATE_NAME): {
                bmsEngine.updateMemberName(updateTo, memberId);
                return;
            }
            case (UPDATE_EMAIL):{
                updateTo = updateTo.toLowerCase();
                bmsEngine.updateEmailAddress( updateTo, memberId);
                return;
            }
            case (UPDATE_AGE):{
                int age = Integer.parseInt(updateTo);
                bmsEngine.updateMemeberAge(age, memberId);
                return;
            }
            case (UPDATE_PHONE): {
                bmsEngine.updateMemeberPhoneNumber(updateTo, memberId);
                return;
            }
            case (UPDATE_PASSWORD):{
                bmsEngine.updateMemberPassword(updateTo, memberId);
                return;
            }
            case (UPDATE_PRIVATE):{
                int privateBoat = Integer.parseInt(updateTo);
                if (privateBoat == -1) // no boat
                    bmsEngine.updateMemberPrivateBoatStatus(false, memberId);
                else{
                    if (bmsEngine.getBoatById(privateBoat) == null) // checks if such a boat even exists
                        throw new BoatDoesNotExistException(privateBoat,null);
                    else{
                        bmsEngine.updateMemberPrivateBoatStatus(true, memberId);
                        bmsEngine.updateMemberPrivateBoatNumber(privateBoat, memberId);
               }
            }
                return;
            }
            case (UPDATE_MANAGER):{
                boolean isManager = Boolean.parseBoolean(updateTo);
                bmsEngine.updateMemberIsManager(isManager, memberId);
                return;
            }
            case (UPDATE_LEVEL):{
                int level = Integer.parseInt(updateTo);
                bmsEngine.updateMemeberLevel(level,memberId);
                return;
            }
            case (UPDATE_NOTES):{
                bmsEngine.updateMemberNotes(updateTo, memberId);
                return;
            }
            case (UPDATE_EXP):{
                LocalDate date = LocalDate.parse(updateTo, DateTimeFormatter.ISO_LOCAL_DATE);
                bmsEngine.updateMemberExpirationDate(date, memberId);
                return;
            }
            case (UPDATE_JOIN):{
                LocalDate date = LocalDate.parse(updateTo, DateTimeFormatter.ISO_LOCAL_DATE);
                bmsEngine.updateMemberSignUpDate(date, memberId);
                return;

            }
        }
    }

}
