package bms.engine.engine;

//import bms.engine.XMLImportAndExport.XMLImportAndExport;
import  bms.engine.engine.multipleExceptions.XmlMultipleExceptions;

import bms.engine.boatsManagement.boat.Boat;
import bms.engine.boatsManagement.boat.boatsListsExceptions.*;

import bms.engine.membersManagement.member.Member;
import bms.engine.membersManagement.member.memberListsExceptions.*;

import bms.engine.activitiesManagement.activity.Activity;
import bms.engine.activitiesManagement.activity.ActivityExceptions.*;

import bms.engine.reservationsManagment.reservation.Reservation;
import bms.engine.reservationsManagment.reservation.reservationsExceptions.*;

import javax.xml.bind.JAXBException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public interface BmsEngine {

    public boolean setBoatsManagement();



    ///////////////////////////////////////////////////////////////////////////////////// Boat ////////////////////////////////////////////////////////////////////////////

    //This method adds a new boat with the given parameters to boats list
    // throws exception if boat with the same serial number is already on the engine's boats list
    public boolean addNewBoat(String name, Integer serialNum, Boolean isPrivate, Boolean isStalled, Boat.BoatType newBoatType, boolean loadProcess)
            throws BoatAlreadyExistsException, HelmsmanException, SingleWithTwoOarsException;

    public Boat getBoatById(Integer serNum);


    ////////////////////////////// boat updates/////////////////////////////////////////
    public boolean updateBoatName(Integer serNum, String newName);

    public boolean updatePrivate(Integer serNum, boolean newOwnership);

    //This method updates boat to be stalled or back to be active.
    //if back to active - boat type is added to boat type list if out of order - boat type is removed from boat type list
    //returns true only if status really altered (stalled became active or active to stalled). returns false otherwise
    public boolean changeBoatStatus(Integer serNum, boolean outOfOrder);

    //This method allows changing a given boat's number of oars (true - 1, false - 2)
    //it updates boatType's Map
    public void changeOars(Integer serNum, boolean isSingleOar) throws SingleWithTwoOarsException;

    //This method updates a given boat's coxswain presence (true - there is, false - there isn't)
    //it updates boatType's Map
    public boolean changeHelmsman(Integer serNum, Boolean hasHelmsman) throws HelmsmanException;

    //This method updates a given boat to be coastal(true) or not (false)
    //it updates boatType's Map
    public boolean changeCoastal(Integer serNum, Boolean coastal);

    //This method changes a given boat serial number
    //returns false if a boat with such number is already in the boats map (so the update did not happen)
    // true if succeeded
    public boolean changeBoatsSerialNumber(Integer serNum, int newNum);


    //This method deletes the boat permanently from boat list, and deletes all future scheduling this bms.engine.boatsManagement.boat is assign to
    public boolean deleteBoat(Boat boat);

    public boolean removeAllBoats();

    //This method deletes all future scheduling this boat is assign to
    public boolean releaseBoatFromAllReservations(Boat boat);


    //This method releases boat from reservation and deletes the activity from boat's scheduling
    // (since it's now available again at that time)
    public boolean releaseBoatFromReservation(Boat boat, Reservation clientReservation);

    //This method removes days that have passed from scheduling map of a given boat
    //those reservations will still be saved in the closed reservations, but it's irrlevant to keep them in the boat information
    public void cleanHistoryFromBoats();

    public List<Boat.BoatType> getCurrentBoatTypes();


    ///////////////////////////////////////////////////////////////////////////////////// getters ////////////////////////////////////////////////////////////////////////////

  //  public XMLImportAndExport getXMLSchema();

    public Map<String, Member> getMembers();

    public List<Activity> getActivities();

    public List<Boat> getBoats();

    public Map<Integer, Boat> getBoatsMap();

    public List<Reservation> getOpenReservation();

    public List<Reservation> getClosedReservation();


    ///////////////////////////////////////////////////////////////////////////////////// Member ////////////////////////////////////////////////////////////////////////////


    public Member getMemberByEmail(String email);


        //this function set the member age
    public boolean updateMemeberAge(int age, String Email);


    //this function set the member phone number
    public boolean updateMemeberPhoneNumber(String phoneNumber, String memberEmail);


    //this method update the private bms.engine.boatsManagement.boat number, return false if this number doesn't exist in the bms.engine.boatsManagement.boat list
    //(or represent no private bms.engine.boatsManagement.boat), true if it does
    // private bms.engine.boatsManagement.boat for none bms.engine.boatsManagement.boat is -1

    public boolean updateMemberPrivateBoatNumber(int privateBoatSerialNumber, String memberEmail);

    public boolean updateMemeberLevel(int level, String memberEmail);

    public void updateMemberIsManager(boolean isManager, String memberEmail);

    public boolean updateMemberPrivateBoatStatus(boolean havePrivateBoat, String memberEmail);


    public boolean updateMemberSignUpDate(LocalDate signUpDate, String memberEmail);


    public boolean updateMemberExpirationDate(LocalDate expirationDate, String memberEmail) throws ExpiryDateIsBeforeSignUpException;


    public boolean updateMemberName(String name, String memberEmail);

    public boolean updateMemberNotes(String notes, String memberEmail);

    public boolean updateMemberPassword(String password, String memberEmail);

    public boolean updateEmailAddress(String emailAddress, String memberEmail) throws EmailAddressAlreadyExistsException;

    public List<Member> getMembersMaxRowing(int listSize, String memberEmail);

    //this function creates a new member in the club and add it to the members map
    public boolean addNewMember(String name, String notes, String email, String password,
                                int age, String phoneNumber, boolean havePrivateBoat, int privateBoatSerialNumber,
                                int rowingLevel, boolean isManager, LocalDate signUpDate,
                                LocalDate expirationDate, boolean loadProcess)
            throws EmailAddressAlreadyExistsException, BoatDoesNotExistException, ExpiryDateIsBeforeSignUpException;

    // this method remove the given member from member list, from all reservation this member was related to
    //if member was participant member, then reservation is deleted
    //if member had a privateBoat - boat is removed as well.
    public boolean removeMember(String removedMemberEmail);

    public boolean removeAllMembers(String loggedManagerEmail);

    public List<Member> getAllMembers();

    ///////////////////////////////////////////////////////////////////////////////////// Activity ////////////////////////////////////////////////////////////////////////////
    public Activity findActivityById(int id);

    //This Method creates and add a new activity (=time window) to the activities list
    // if optionalType is not offered, should be received as NULL
    public Activity addNewActivity(LocalTime starts, LocalTime ends, String activityName,
                                   Boat.BoatType optionalType, boolean loadingProcess) throws EndTimeIsLowerException;

    public boolean changeStartingTime(Integer id, LocalTime starts);

    public boolean changeEndingTime(Integer id, LocalTime ends);

    public boolean changeActivityName(Integer id, String name);


    public boolean changeActivityBoatType(Integer id, Boat.BoatType newType);


    //This method removes an activity from the activities the boat house offers
    // by deleting an activity, all future reservations that have applied for this activity
    // are also being deleted.
    public boolean removeActivityFromList(Integer id);

    //This method allows updating an activity by removing it and returning it to the List
    public boolean updateActivityOnList(Integer id);

    public boolean removeAllActivities();


    //This method creates and adds new reservation to reservation Map (key is its trainingDate)
    //if trainingDate is before today, reservation is not created and exception is thrown
    //if participantRower (reservation owner) is in the additional rowers reservation is not created and exception is thrown.
    public Reservation addNewReservation(Member participantRower, LocalDate trainingDate, Activity trainingTime,
                                         List<Boat.BoatType> boatTypes, List<Member> wantedAdditionalRowers,
                                         LocalDateTime reservationDateTime, Member reservationMember,
                                         boolean loadProcess) throws ParticipentRowerIsOnListException ;

    //This reservation approves a reservation, if reservation owner has a private bms.engine.boatsManagement.boat
    //that its type matches the ones on the requested bms.engine.boatsManagement.boat types.
    public boolean approveResAutomatically(Member participantRower, Reservation clientReservation, boolean loadProcess) ;


    public Member updateReservatioParticipantRower(Member participantRower, Reservation clientReservation) throws ParticipentRowerIsOnListException ;

    public LocalDate updateReservatioTrainingDate(LocalDate trainingDate, Reservation clientReservation) ;

    public Activity updateReservatioActivity(Activity trainingTime, Reservation clientReservation);

    public boolean updateReservatioBoatTypesRemove(Boat.BoatType boatType, Reservation clientReservation) ;

    public boolean updateReservatioBoatTypesAdd(Boat.BoatType boatType, Reservation clientReservation) ;

    public LocalDateTime updateReservationDateTime(LocalDateTime reservationDateTime, Reservation clientReservation) ;

    //This reservation update the reservation approve status, return false if the reservation doesn't have bms.engine.boatsManagement.boat
    // (means the reservation can't be approved)
    public Boolean updateReservationIsApproved(Boolean isApproved, Reservation clientReservation, boolean loadProcess) ;


    public List<Member> updateReservationAdditinalWantedRowers(List<Member> wantedRowers, Reservation clientReservation) throws ParticipentRowerIsOnListException ;

    public List<Member> updateReservationAdditinalActualRowers(List<Member> actualRowers, Reservation clientReservation);

    public Boat updateReservationBoat(Boat reservationBoat, Reservation clientReservation, boolean loadProcess) ;

    // This method removes reservation link from member
    public boolean removeReservationFromMember(String currentMemberEmail, Reservation clientReservation, boolean loadProcess) ;

    //This method sets reservation to be pending(pending = null) and releases the bms.engine.boatsManagement.boat that was assigned to it
    //since editing an approved reservation is only available after the reservation is reopened.
    public boolean reopenReservation(Reservation clientReservation) ;

    // This method removes member from reservation (assuming this member is not owner of the reservation)
    public boolean removeMemberFromReservationActualList(String currentMemberEmail, Reservation clientReservation) ;

    // This method removes member from reservation (assuming this member is not owner of the reservation)
    public boolean removeMemberFromReservationGeneral(Member currentMember, Reservation clientReservation) ;


    public boolean addMemberToWantedRowers(String currentMemberEmail, Reservation clientReservation);

    //This Method delete a reservation permanently from all reservation lists.
    public boolean deleteReservation(Reservation clientReservationToDelete) ;

    public Reservation duplicateReservation(Reservation fromReservation) ;

    public Reservation getNewReservation(Reservation oldReservation) ;


    ///////////////////////////////////////////////////////////////////////////////////// Reservation filters ////////////////////////////////////////////////////////////////////////////

    public List<Reservation> getFuturePendingReservationOfMember(String memberEmail) ;

    public List<Reservation> getAllFutureReservationOfMember(String memberEmail) ;

    public List<Reservation> getReservationHistoryOfMember(String memberEmail) ;

    public List<Reservation> getReservationHistory() ;

    public List<Reservation> getFuturePendingReservationForDay(LocalDate date) ;

    public List<Reservation> getFuturePendingReservationForWeek() ;

    public List<Reservation> getFutureApprovedReservationForDay(LocalDate date);

    public List<Reservation> getFutureApprovedReservationForWeek();

    public List<Reservation> getFutureReservationForWeek();

    public List<Reservation> getAllFutureReservationForWeek();

    public List<Reservation> getFutureReservationForDay(LocalDate date);

    public List<Reservation> getFuturePendingReservationSameActivity(LocalDate date, Integer id, Reservation mergedReservation);

    public List<Reservation> getClosedReservationOfMember(String memberEmail);

//////////////////////////////////////////////////////////////////////// Scheduling methods //////////////////////////////////////////////////////////////////////////

    //This method returns a set of boats that are relevant to a given reservation (by type, by availability)
    public List<Boat> getRelevantBoats(Reservation clientReservation);

    //this function return false if the reservation doesn't have a bms.engine.boatsManagement.boat or it
    public boolean assignApprovedRowersToReservation(List<Member> approvedRowers, Reservation clientReservation,
                                                     boolean loadProcess) throws ApprovedReservationWithNoBoatException, BoatSizeMismatchException;

    public boolean updateApprovedStatus(Reservation clientReservation);

    //This method gets reservation and assign a boat to it
    //It is also sets this boat to be reserved on that date and time window (activity).
    public boolean assignBoatToReservation(Reservation clientReservation, Boat boat, boolean loadProcess);

    public boolean addReservationToClosedReservation(Reservation clientReservation);

    public boolean addReservationToOpenReservation(Reservation clientReservation);

    public boolean removeReservationFromOpenReservation(Reservation clientReservation);

    public boolean removeReservationFromCloseReservation(Reservation clientReservation);

    //This method gets two list of chosen members taken from to reservations, them into the main reservation.
    public boolean mergeReservations(List<Member> chosenMain, List<Member> chosenAdded,
                                     Reservation mainReservation, Reservation addedReservation)
            throws BoatSizeMismatchException, ApprovedReservationWithNoBoatException;

    public boolean isRowerAvailable(String memberEmail, Reservation clientReservation, Integer id);

    public boolean rejectReservation(Reservation clientReservation, boolean loadingProcess);

    public List<Boat> autoAssignBoatToReservation (Set<Boat.BoatType> boatTypes, Reservation clientReservation);

///////////////////////////////////////////////////////////////////////////////////// Login Method ////////////////////////////////////////////////////////////////////////////

    public boolean loginTest(String email);

    public boolean addLoginMember(String memberEmail);

    public boolean removeLoginMember(String memberEmail);


    public boolean checkMemberPassword(String email, String password);

    public String toString();

    ///////////////////////////////////////////////////XML import and export //////////////////////////////////////

    public String importXmlFromInputStream(int type, String xmlString, boolean unite, String loggedManagerEmail) throws XmlMultipleExceptions;

    public String exportXmlToString (int type) throws JAXBException;

    public void saveState();

    public void loadState();
}