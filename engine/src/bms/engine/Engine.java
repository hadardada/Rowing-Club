package bms.engine;

import bms.engine.engine.multipleExceptions.XmlMultipleExceptions;
import bms.engine.engine.BmsEngine;

import bms.engine.boatsManagement.boat.Boat;
import bms.engine.boatsManagement.boat.boatsListsExceptions.*;
import bms.engine.boatsManagement.BoatsManagement;

import bms.engine.membersManagement.member.Member;
import bms.engine.membersManagement.member.memberListsExceptions.*;
import bms.engine.membersManagement.MembersManagement;

import bms.engine.activitiesManagement.activity.Activity;
import bms.engine.activitiesManagement.activity.ActivityExceptions.*;
import bms.engine.activitiesManagement.ActivitiesManagement;

import bms.engine.reservationsManagment.reservation.Reservation;
import bms.engine.reservationsManagment.reservation.reservationsExceptions.*;
import bms.engine.reservationsManagment.ReservationsManagement;

import bms.engine.XMLImportAndExport.XMLImportAndExport;
import bms.engine.xmlStateExport.XmlStateExport;
import bms.engine.xmlStateImport.XmlStateImport;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import java.util.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.List;

public class Engine implements BmsEngine, Serializable {

    BoatsManagement boats;
    ActivitiesManagement activities;
    ReservationsManagement reservations;
    MembersManagement members;

    private Member loginMember;
    private XmlStateExport stateSaver;
    private XmlStateImport loader;
    private XMLImportAndExport schemaXml;

    /////////////////////////////////////////////////////////////////////////////////////constructor////////////////////////////////////////////////////////////////////////////

    public Engine() {

        this.boats = new BoatsManagement();
        this.activities = new ActivitiesManagement();
        this.reservations = new ReservationsManagement();
        this.reservations = new ReservationsManagement();
        this.members = new MembersManagement( this.boats);
        this.stateSaver = new XmlStateExport(this);
        this.loader = new XmlStateImport(this);
        this.schemaXml = new XMLImportAndExport(this);
        try {
            addNewMember("Admin", "none", "Admin123@gmail.com", "123456", 45, "0526676697", false, -1, 3, true, LocalDate.of(2020, 4, 8), LocalDate.of(2021, 4, 7), true);
        } catch (EmailAddressAlreadyExistsException | BoatDoesNotExistException| ExpiryDateIsBeforeSignUpException e) {
            System.out.println(e.getMessage());
        }
        this.loader.loadState();
    }

    public boolean setBoatsManagement(){
        this.members.setBaotsManagement(this.boats);
        return true;
    }




    ///////////////////////////////////////////////////////////////////////////////////// Boat ////////////////////////////////////////////////////////////////////////////

    //This method adds a new boat with the given parameters to boats list
    // throws exception if boat with the same serial number is already on the engine's boats list
    public boolean addNewBoat(String name, Integer serialNum, Boolean isPrivate, Boolean isStalled, Boat.BoatType newBoatType, boolean loadProcess)
            throws BoatAlreadyExistsException, HelmsmanException, SingleWithTwoOarsException {
        boats.addNewBoat(name, serialNum, isPrivate, isStalled, newBoatType);
        if (!loadProcess)
            this.stateSaver.saveStateToXml();
        return true;
    }

    public Boat getBoatById(Integer serNum){
        return this.boats.getBoatBySerialNum(serNum);
    }


    ////////////////////////////// boat updates/////////////////////////////////////////
    public boolean updateBoatName(Integer serNum, String newName) {
        Boat boat = boats.getBoatBySerialNum(serNum);
        boolean status = boats.updateBoatName(boat, newName);
        this.stateSaver.saveStateToXml();
        return status;
    }

    public boolean updatePrivate(Integer serNum, boolean newOwnership) {
        Boat boat = boats.getBoatBySerialNum(serNum);
        boolean status = (boat.setPrivateProperty(newOwnership));
        if (status == true)
            releaseBoatFromAllReservations(boat);
        this.stateSaver.saveStateToXml();
        return status;
    }

    //This method updates boat to be stalled or back to be active.
    //if back to active - boat type is added to boat type list if out of order - boat type is removed from boat type list
    //returns true only if status really altered (stalled became active or active to stalled). returns false otherwise
    public boolean changeBoatStatus(Integer serNum, boolean outOfOrder) {
        Boat boatToChange = boats.getBoatBySerialNum(serNum);
        if (!boats.changeBoatActiveStatus(boatToChange, outOfOrder)) // if status wasn't actually changed
            return false;
        else if (outOfOrder) // was changed to be out of order
            releaseBoatFromAllReservations(boatToChange);
        this.stateSaver.saveStateToXml();
        return true;
    }

    //This method allows changing a given boat's number of oars (true - 1, false - 2)
    //it updates boatType's Map
    public void changeOars(Integer serNum, boolean isSingleOar) throws SingleWithTwoOarsException {
        Boat boat = boats.getBoatBySerialNum(serNum);
        boats.changeOars(boat, isSingleOar);
        this.stateSaver.saveStateToXml();
    }

    //This method updates a given boat's coxswain presence (true - there is, false - there isn't)
    //it updates boatType's Map
    public boolean changeHelmsman(Integer serNum, Boolean hasHelmsman) throws HelmsmanException {
        Boat boat = boats.getBoatBySerialNum(serNum);
        boats.changeHelmsman(boat, hasHelmsman);
        this.stateSaver.saveStateToXml();
        return true;
    }

    //This method updates a given boat to be coastal(true) or not (false)
    //it updates boatType's Map
    public boolean changeCoastal(Integer serNum, Boolean coastal) {
        Boat boat = boats.getBoatBySerialNum(serNum);
        boats.changeCoastal(boat, coastal);
        this.stateSaver.saveStateToXml();
        return true;
    }

    //This method changes a given boat serial number
    //returns false if a boat with such number is already in the boats map (so the update did not happen)
    // true if succeeded
    public boolean changeBoatsSerialNumber(Integer serNum, int newNum) {
        Boat boat = boats.getBoatBySerialNum(serNum);
        boolean succeeded =  boats.changeBoatsSerialNumber(boat, newNum);
        if (succeeded)
            this.stateSaver.saveStateToXml();
        return succeeded;
    }


    //This method deletes the boat permanently from boat list, and deletes all future scheduling this boat is assign to
    public boolean deleteBoat(Boat boat) {
        Boat boatToDelete = boats.getBoatBySerialNum(boat.getSerialNum());
        if (boats.removeBoatFromList(boatToDelete)) // if removal from list failed
            return false; // return false
        releaseBoatFromAllReservations(boatToDelete);
        this.stateSaver.saveStateToXml();
        return true;
    }

    //This method deletes all boats from boats list, therefore realses all future approved reservations
    public boolean removeAllBoats() {
        Set <Boat> boatsTemp = new HashSet<>();
        boatsTemp.addAll(boats.getBoats().values());
        for (Boat boat : boatsTemp)
            deleteBoat(boat);
        this.stateSaver.saveStateToXml();
        return true;
    }

    //This method deletes all future scheduling this boat is assign to
    public boolean releaseBoatFromAllReservations(Boat boat) {
        boats.deleteActivitiesFromBoatsScheduling(boat, reservations.releaseBoatFromAllReservations(boat));
        return true;
    }

    //This method releases boat from reservation and deletes the activity from boat's scheduling
    // (since it's now available again at that time)
    public boolean releaseBoatFromReservation (Boat boat, Reservation clientReservation) {
        Reservation reservation = this.reservations.findByResMadeAtByWho(clientReservation.getReservationDateTime(),
                clientReservation.getReservationMember().getEmailAddress(),clientReservation.getTrainingDate());
        reservation.releaseBoatFromReservation();
        return boats.deleteSingleActivityFromBoatsScheduling(boat,reservation.getActivity(), reservation.getTrainingDate());
    }

    //This method removes days that have passed from scheduling map of a given boat
    //those reservations will still be saved in the closed reservations, but it's irrlevant to keep them in the boat information
    public void cleanHistoryFromBoats() {
        boats.cleanHistoryFromBoats();
        this.stateSaver.saveStateToXml();
    }

    public List<Boat.BoatType> getCurrentBoatTypes() {
        List<Boat.BoatType> currentBoatTypes = boats.getCurrentBoatTypes();
        return Collections.unmodifiableList(currentBoatTypes);
    }

    ///////////////////////////////////////////////////////////////////////////////////// getters ////////////////////////////////////////////////////////////////////////////

    public XMLImportAndExport getXMLSchema(){return this.schemaXml;}

    public Map<String, Member> getMembers() {
        return Collections.unmodifiableMap(members.getMembers());
    }

    public List<Activity> getActivities() {
        return Collections.unmodifiableList(activities.getActivities());
    }

    public List<Boat> getBoats() {
        List<Boat> boatsList = new ArrayList<>(boats.getBoats().values());
        return Collections.unmodifiableList(boatsList);
    }

    public Map<Integer, Boat> getBoatsMap() {
        return Collections.unmodifiableMap(boats.getBoats());
    }

    public List<Reservation> getOpenReservation(){
        return Collections.unmodifiableList(reservations.getOpenReservation());
    }

    public List<Reservation> getOpenReservationForDate (LocalDate date){
        return reservations.getOpenReservationForDate(date);
    }

    public List<Reservation> getClosedReservation(){
        return Collections.unmodifiableList(reservations.getClosedReservation());
    }

    public List<Reservation> getClosedReservationForDate (LocalDate date){
        return reservations.getClosedReservationForDate(date);
    }


    ///////////////////////////////////////////////////////////////////////////////////// Member ////////////////////////////////////////////////////////////////////////////

    public Member getMemberByEmail(String email){

        return this.members.getMembers().get(email);
    }



    //this function set the member age
    public boolean updateMemeberAge(int age, String memberEmail) {

        this.members.updateMemeberAge(age, this.members.getMemberByEmail(memberEmail));
        this.stateSaver.saveStateToXml();
        return true;
    }

    //this function set the member phone number
    public boolean updateMemeberPhoneNumber(String phoneNumber, String memberEmail) {
        this.members.updateMemeberPhoneNumber(phoneNumber, this.members.getMemberByEmail(memberEmail));
        this.stateSaver.saveStateToXml();
        return true;
    }


    //this method update the private boat number, return false if this number doesn't exist in the boat list
    //(or represent no private boat), true if it does
    // private boat for none boat is -1

    public boolean updateMemberPrivateBoatNumber(int privateBoatSerialNumber, String memberEmail) {

        boolean successStatus = this.members.updateMemberPrivateBoatNumber(privateBoatSerialNumber, this.members.getMemberByEmail(memberEmail));

        if (this.boats.getBoats().containsKey(privateBoatSerialNumber)) {
            this.stateSaver.saveStateToXml();
            return true;
        }
        return successStatus;
    }

    public boolean updateMemeberLevel(int level, String memberEmail) {
        this.members.updateMemeberLevel(level, this.members.getMemberByEmail(memberEmail));
        this.stateSaver.saveStateToXml();
        return true;
    }

    public void updateMemberIsManager(boolean isManager, String memberEmail) {
        this.members.updateMemberIsManager(isManager, this.members.getMemberByEmail(memberEmail));
        this.stateSaver.saveStateToXml();
    }

    public boolean updateMemberPrivateBoatStatus(boolean havePrivateBoat, String memberEmail) {
        boolean status = this.members.updateMemberPrivateBoatStatus(havePrivateBoat, this.members.getMemberByEmail(memberEmail));
        this.stateSaver.saveStateToXml();
        return status;
    }

    public boolean updateMemberSignUpDate(LocalDate signUpDate, String memberEmail) {
        this.members.updateMemberSignUpDate(signUpDate, this.members.getMemberByEmail(memberEmail));
        this.stateSaver.saveStateToXml();
        return true;
    }

    public boolean updateMemberExpirationDate(LocalDate expirationDate, String memberEmail) throws ExpiryDateIsBeforeSignUpException {
        this.members.updateMemberExpirationDate(expirationDate, this.members.getMemberByEmail(memberEmail));
        this.stateSaver.saveStateToXml();
        return true;
    }

    public boolean updateMemberName(String name, String memberEmail) {
        boolean status = this.members.updateMemberName(name, this.members.getMemberByEmail(memberEmail));
        this.stateSaver.saveStateToXml();
        return status;
    }

    public boolean updateMemberNotes(String notes, String memberEmail) {
        this.members.updateMemberNotes(notes, this.members.getMemberByEmail(memberEmail));
        this.stateSaver.saveStateToXml();
        return true;
    }

    public boolean updateMemberPassword(String password, String memberEmail) {
        boolean status = this.members.updateMemberPassword(password, this.members.getMemberByEmail(memberEmail));
        this.stateSaver.saveStateToXml();
        return status;
    }


    public boolean updateEmailAddress(String emailAddress, String memberEmail) throws EmailAddressAlreadyExistsException {
        this.members.updateEmailAddress(emailAddress, this.members.getMemberByEmail(memberEmail));

        this.stateSaver.saveStateToXml();
        return true;
    }


    //this function creates a new member in the club and add it to the members map
    public boolean addNewMember(String name, String notes, String email, String password,
                                int age, String phoneNumber, boolean havePrivateBoat, int privateBoatSerialNumber,
                                int rowingLevel, boolean isManager, LocalDate signUpDate,
                                LocalDate expirationDate, boolean loadProcess)
            throws EmailAddressAlreadyExistsException, BoatDoesNotExistException, ExpiryDateIsBeforeSignUpException {

        this.members.addNewMember(name,notes,email,password,age,phoneNumber,havePrivateBoat,
                privateBoatSerialNumber,rowingLevel,isManager,signUpDate,expirationDate);
        if (!loadProcess)// first insert : admin
            this.stateSaver.saveStateToXml();

        return true;
    }

    // this method remove the given member from member list, from all reservation this member was related to
    //if member was participant member, then reservation is deleted
    //if member had a privateBoat - boat is removed as well.
    public boolean removeMember(String removedMemberEmail) {
        Member removedMember = this.members.getMemberByEmail(removedMemberEmail);
        if (this.loginMember == removedMember) {
            return false;
        }
        LocalDate today = LocalDate.now();
        if (members.getMembers().remove(removedMember.getEmailAddress()) == null) // meaning, the user wasn't found
            return false;
        if (removedMember.getPrivateBoatStatus())
            deleteBoat(boats.getBoatBySerialNum(removedMember.getPrivateBoatSerialNumber()));
        for (Reservation reservation : removedMember.getMyReservations()) {
                if (reservation.getParticipantRower() == removedMember) // meaning the deleted member was the main rower in that reservation
                    deleteReservation(reservation); // in that case, if the main rower is deleted then its reservation is also deleted
                else {
                    // removed member was a part of a reservation
                    if (reservation.getIsApproved() == null || !reservation.getIsApproved()) { // if the res pending
                        removeMemberFromReservationGeneral(removedMember.getEmailAddress(), reservation); // remove from wanted

                    } else if (reservation.getIsApproved()) { // if the res approved
                        removeMemberFromReservationGeneral(removedMember.getEmailAddress(), reservation); // remove from wanted
                        removeMemberFromReservationActualList(removedMember.getEmailAddress(), reservation); // remove from actual
                        reservation.reopenReservation();
                    }

            }
        }
        return true;
    }

    public boolean removeAllMembers(String loggedManagerEmail) {
        Set<String > emailKeys = new HashSet<>();
        emailKeys.addAll(this.members.getMembers().keySet());
        for (String key : emailKeys)
            if (!key.equals(loggedManagerEmail))
                removeMember(this.members.getMembers().get(key).getEmailAddress());
        return true;
    }

    public List<Member> getAllMembers() {
        return this.members.getAllMembers();
    }

    public List<Member> getMembersMaxRowing(int listSize, String memberEmail) {
        return this.members.getMembersMaxRowing(listSize,this.members.getMemberByEmail(memberEmail));
    }

    ///////////////////////////////////////////////////////////////////////////////////// Activity ////////////////////////////////////////////////////////////////////////////
    public Activity findActivityById(int id){
        return activities.findActivityById(id);
    }


    //This Method creates and add a new activity (=time window) to the activities list
    // if optionalType is not offered, should be received as NULL
    public Activity addNewActivity(LocalTime starts, LocalTime ends, String activityName, Boat.BoatType optionalType, boolean loadingProcess) throws EndTimeIsLowerException {
        Activity newActivity = activities.addNewActivity(starts,ends,activityName,optionalType);
        if (!loadingProcess)
            this.stateSaver.saveStateToXml();
        return newActivity;
    }

    public boolean changeStartingTime(Integer id, LocalTime starts) {
        Activity activity = this.activities.findActivityById(id);
        boolean succeed = activities.changeStartingTime(activity, starts);
        updateActivityOnList(id);
        this.stateSaver.saveStateToXml();
        return succeed;
    }

    public boolean changeEndingTime(Integer id, LocalTime ends) {
        Activity activity = this.activities.findActivityById(id);
        boolean succeed = activities.changeEndingTime(activity, ends);
        updateActivityOnList(id);
        this.stateSaver.saveStateToXml();
        return succeed;
    }

    public boolean changeActivityName(Integer id, String name) {
        Activity activity = this.activities.findActivityById(id);
        boolean succeed = activities.changeActivityName(activity, name);
        this.stateSaver.saveStateToXml();
        return succeed;
    }

    public boolean changeActivityBoatType(Integer id, Boat.BoatType newType) {
        Activity activity = this.activities.findActivityById(id);
        boolean succeed = activities.changeActivityBoatType(activity,newType);
        this.stateSaver.saveStateToXml();
        return succeed;
    }

    //This method removes an activity from the activities the boat house offers
    // by deleting an activity, all future reservations that have applied for this activity
    // are also being deleted.
    public boolean removeActivityFromList(Integer id) {
        Activity activity = this.activities.findActivityById(id);
        activities.removeOverlappingActivity(activity);
        activities.removeActivity(activity);
        boats.removeActivityFromAllBoats(activity);
        reservations.removeReservationsByActivity(activity);
        this.stateSaver.saveStateToXml();
        return true;
    }

    //This method allows updating an activity by removing it and returning it to the List
    public boolean updateActivityOnList(Integer id) {
        Activity activity = this.activities.findActivityById(id);
        activities.removeOverlappingActivity(activity);
        removeActivityFromList(id);
        activities.insertActivitySorted(activity);
        return true;
    }

    public boolean removeAllActivities() {
        Set<Integer> activitiesID = new HashSet<>();
        for (Activity activity : activities.getActivities()){
            activitiesID.add(activity.getId());}
        for (Integer activityID : activitiesID){
            this.removeActivityFromList(activityID);
        }
        this.stateSaver.saveStateToXml();
        return true;
    }

    ///////////////////////////////////////////////////////// Reservations methods /////////////////////////////////////////////////////////////////

    //This method creates and adds new reservation to reservation Map (key is its trainingDate)
    //if trainingDate is before today, reservation is not created and exception is thrown
    //if participantRower (reservation owner) is in the additional rowers reservation is not created and exception is thrown.
    public Reservation addNewReservation(Member participantRower, LocalDate trainingDate, Activity trainingTime,
                                     List<Boat.BoatType> boatTypes, List<Member> wantedAdditionalRowers, LocalDateTime reservationDateTime, Member reservationMember, boolean loadProcess) throws ParticipentRowerIsOnListException {

        Member participantRowerOnList = members.getMemberByEmail(participantRower.getEmailAddress());
        Reservation newReservation = reservations.addNewReservation(participantRowerOnList, trainingDate,
                trainingTime, boatTypes, newMemberServerList(wantedAdditionalRowers), reservationDateTime, reservationMember);

        //reservation is approved Automatically if Boat is private
        if ((participantRowerOnList.getPrivateBoatStatus())&&(!loadProcess)) {
            approveResAutomatically(participantRowerOnList, newReservation, loadProcess);
        }

        if (!loadProcess)
            this.stateSaver.saveStateToXml();
            this.stateSaver.saveStateToXml();
        return newReservation;
    }

    public List<Member> newMemberServerList(List<Member> clientList){
        List<Member> newMemberList = new ArrayList<>();

        for (Member member: clientList){
            newMemberList.add(getMemberByEmail(member.getEmailAddress()));
        }

        return newMemberList;
    }

    public Reservation findResByResMadeAt (LocalDateTime madeAt, String email, LocalDate trainingDate){
        return this.reservations.findByResMadeAtByWho(madeAt,email,trainingDate);
    }

    //This reservation approves a reservation, if reservation owner has a private bms.engine.boatsManagement.boat
    //that its type matches the ones on the requested bms.engine.boatsManagement.boat types.
    public boolean approveResAutomatically(Member participantRower, Reservation clientReservation, boolean loadProcess) {
        Reservation reservation = this.reservations.findByResMadeAtByWho(clientReservation.getReservationDateTime(),
                clientReservation.getReservationMember().getEmailAddress(),clientReservation.getTrainingDate());
        Boat privateBoat = boats.getBoatBySerialNum(participantRower.getPrivateBoatSerialNumber());
       if (reservations.approveResAutomatically(participantRower,reservation,privateBoat)) {
           if (!loadProcess)
               this.stateSaver.saveStateToXml();
           return true;
       }
       else
           return false;
    }


    public Member updateReservatioParticipantRower(Member participantRower, Reservation clientReservation) throws ParticipentRowerIsOnListException {
        Reservation myReservation = this.reservations.findByResMadeAtByWho(clientReservation.getReservationDateTime(),
                clientReservation.getReservationMember().getEmailAddress(),clientReservation.getTrainingDate());
        Boat privateBoat = boats.getBoatBySerialNum(participantRower.getPrivateBoatSerialNumber());
        reservations.updateReservatioParticipantRower(participantRower, myReservation, privateBoat);
        this.stateSaver.saveStateToXml();
        return myReservation.getParticipantRower();
    }

    public LocalDate updateReservatioTrainingDate(LocalDate trainingDate, Reservation clientReservation) {
        Reservation myReservation = this.reservations.findByResMadeAtByWho(clientReservation.getReservationDateTime(),
                clientReservation.getReservationMember().getEmailAddress(),clientReservation.getTrainingDate());
        reservations.updateReservatioTrainingDate(trainingDate, myReservation);
        this.stateSaver.saveStateToXml();
        return myReservation.getTrainingDate();
    }

    public Activity updateReservatioActivity(Activity trainingTime, Reservation clientReservation) {
        Reservation myReservation = this.reservations.findByResMadeAtByWho(clientReservation.getReservationDateTime(),
                clientReservation.getReservationMember().getEmailAddress(),clientReservation.getTrainingDate());
        reservations.updateReservatioActivity(trainingTime, myReservation);
        this.stateSaver.saveStateToXml();
        return myReservation.getActivity();
    }

    public boolean updateReservatioBoatTypesRemove(Boat.BoatType boatType, Reservation clientReservation) {
        Reservation myReservation = this.reservations.findByResMadeAtByWho(clientReservation.getReservationDateTime(),
                clientReservation.getReservationMember().getEmailAddress(),clientReservation.getTrainingDate());
        if (reservations.updateReservatioBoatTypesRemove(boatType, myReservation)){
            this.stateSaver.saveStateToXml();
            return true;
        }
        return false;
    }

    public boolean updateReservatioBoatTypesAdd(Boat.BoatType boatType, Reservation clientReservation) {
        Reservation myReservation = this.reservations.findByResMadeAtByWho(clientReservation.getReservationDateTime(),
                clientReservation.getReservationMember().getEmailAddress(),clientReservation.getTrainingDate());
        if (reservations.updateReservatioBoatTypesAdd(boatType, myReservation)) {
            this.stateSaver.saveStateToXml();
            return true;
        }
        else
            return false;
    }

    public LocalDateTime updateReservationDateTime(LocalDateTime reservationDateTime, Reservation clientReservation) {
        Reservation myReservation = this.reservations.findByResMadeAtByWho(clientReservation.getReservationDateTime(),
                clientReservation.getReservationMember().getEmailAddress(),clientReservation.getTrainingDate());
        reservations.updateReservationDateTime(reservationDateTime, myReservation);
;       this.stateSaver.saveStateToXml();
        return myReservation.getReservationDateTime();
    }

    //This reservation update the reservation approve status, return false if the reservation doesn't have bms.engine.boatsManagement.boat
    // (means the reservation can't be approved)
    public Boolean updateReservationIsApproved(Boolean isApproved, Reservation clientReservation, boolean loadProcess) {
        Reservation myReservation = this.reservations.findByResMadeAtByWho(clientReservation.getReservationDateTime(),
                clientReservation.getReservationMember().getEmailAddress(),clientReservation.getTrainingDate());
        reservations.updateReservationIsApproved(isApproved, myReservation);
        if (!loadProcess)
            this.stateSaver.saveStateToXml();
        return myReservation.getIsApproved();
    }

    public List<Member> updateReservationAdditinalWantedRowers(List<Member> wantedRowers, Reservation clientReservation) throws ParticipentRowerIsOnListException {
        Reservation myReservation = this.reservations.findByResMadeAtByWho(clientReservation.getReservationDateTime(),
                clientReservation.getReservationMember().getEmailAddress(),clientReservation.getTrainingDate());
        return Collections.unmodifiableList(reservations.updateReservationAdditinalActualRowers(newMemberServerList(wantedRowers), myReservation));
    }


    public List<Member> updateReservationAdditinalActualRowers(List<Member> actualRowers, Reservation clientReservation) {
        Reservation myReservation = this.reservations.findByResMadeAtByWho(clientReservation.getReservationDateTime(),
                clientReservation.getReservationMember().getEmailAddress(),clientReservation.getTrainingDate());
        return Collections.unmodifiableList(reservations.updateReservationAdditinalActualRowers(newMemberServerList(actualRowers), myReservation));
    }

    public Boat updateReservationBoat(Boat reservationBoat, Reservation clientReservation, boolean loadProcess) {
        Reservation myReservation = this.reservations.findByResMadeAtByWho(clientReservation.getReservationDateTime(),
                clientReservation.getReservationMember().getEmailAddress(),clientReservation.getTrainingDate());
        Boat resBoat = reservations.updateReservationBoat(reservationBoat, myReservation);
        if (!loadProcess)
            this.stateSaver.saveStateToXml();
        return resBoat;
    }

    // This method removes reservation link from member
    public boolean removeReservationFromMember(String currentMemberEmail, Reservation clientReservation, boolean loadProcess) {
        Member currentMember = this.members.getMemberByEmail(currentMemberEmail);
        Reservation myReservation = this.reservations.findByResMadeAtByWho(clientReservation.getReservationDateTime(),
                clientReservation.getReservationMember().getEmailAddress(),clientReservation.getTrainingDate());
        if (reservations.removeReservationFromMember(currentMember, myReservation)) {
            if (!loadProcess)
                this.stateSaver.saveStateToXml();
            return true;
        }
        return false;
    }

    //This method sets reservation to be pending(pending = null) and releases the bms.engine.boatsManagement.boat that was assigned to it
    //since editing an approved reservation is only available after the reservation is reopened.
    public boolean reopenReservation(Reservation clientReservation) {
        Reservation reservation = this.reservations.findByResMadeAtByWho(clientReservation.getReservationDateTime(),
                clientReservation.getReservationMember().getEmailAddress(),clientReservation.getTrainingDate());
        if (!reservation.getIsApproved())
            return false;
        reservation.reopenReservation();
        if (reservation.getReservationBoat() != null)
            reservation.getReservationBoat().releaseActivityFromBoat(reservation.getTrainingDate(), reservation.getActivity());
        reservation.releaseBoatFromReservation();

        this.reservations.removeReservationFromCloseReservation(reservation);
        this.reservations.addReservationToOpenReservation(reservation);
        this.stateSaver.saveStateToXml();
        return true;
    }

   // This method removes member from reservation (assuming this member is not owner of the reservation)
    public boolean removeMemberFromReservationActualList(String currentMemberEmail, Reservation clientReservation) {
        Member currentMember = this.members.getMemberByEmail(currentMemberEmail);
        Reservation myReservation = this.reservations.findByResMadeAtByWho(clientReservation.getReservationDateTime(),
                clientReservation.getReservationMember().getEmailAddress(),clientReservation.getTrainingDate());
        myReservation.getActualRowers().remove(currentMember);
        this.stateSaver.saveStateToXml();
        return true;
    }

    // This method removes member from reservation (assuming this member is not owner of the reservation)
    public boolean removeMemberFromReservationGeneral(String currentMemberEmail, Reservation clientReservation) {
        Member currentMember = this.members.getMemberByEmail(currentMemberEmail);
        Reservation myReservation = this.reservations.findByResMadeAtByWho(clientReservation.getReservationDateTime(),
                clientReservation.getReservationMember().getEmailAddress(),clientReservation.getTrainingDate());
        myReservation.getWantedRowers().remove(currentMember);
        this.stateSaver.saveStateToXml();
        return true;
    }


    public boolean addMemberToWantedRowers(String currentMemberEmail, Reservation clientReservation) {
        Member currentMember = this.members.getMemberByEmail(currentMemberEmail);
        Reservation myReservation = this.reservations.findByResMadeAtByWho(clientReservation.getReservationDateTime(),
                clientReservation.getReservationMember().getEmailAddress(),clientReservation.getTrainingDate());
        if (reservations.addMemberToWantedRowers(currentMember, myReservation)){
        this.stateSaver.saveStateToXml();
        return true;
        }
        else
            return false;

    }

    //This Method delete a reservation permanently from all reservation lists.
    public boolean deleteReservation(Reservation clientReservationToDelete) {
       Reservation reservationToDelete = this.reservations.findByResMadeAtByWho(clientReservationToDelete.getReservationDateTime(),
                clientReservationToDelete.getReservationMember().getEmailAddress(),clientReservationToDelete.getTrainingDate());
        if (reservationToDelete.getReservationBoat() != null) // if a boat was assigned to this reservation
            releaseBoatFromReservation(reservationToDelete.getReservationBoat(), reservationToDelete);
        reservations.deleteReservation(reservationToDelete);
        this.stateSaver.saveStateToXml();
        return true;
    }

    public Reservation duplicateReservation(Reservation fromReservation) {
        try {
            return this.addNewReservation(fromReservation.getParticipantRower(),
                    fromReservation.getTrainingDate(),fromReservation.getActivity(),new ArrayList<>(fromReservation.getBoatTypes()),new ArrayList<>(fromReservation.getWantedRowers()),LocalDateTime.now(),fromReservation.getReservationMember(),false);
        }
        catch (ParticipentRowerIsOnListException e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    public Reservation getNewReservation(Reservation oldReservation){
        return this.reservations.findByResMadeAtByWho(oldReservation.getReservationDateTime()
                ,oldReservation.getReservationMember().getEmailAddress(),oldReservation.getTrainingDate());
    }


    ///////////////////////////////////////////////////////////////////////////////////// Reservation filters ////////////////////////////////////////////////////////////////////////////

    public List<Reservation> getFuturePendingReservationOfMember(String memberEmail) {
        return Collections.unmodifiableList(reservations.getReservationOfMemberFiltered(this.members.getMemberByEmail(memberEmail), 'P'));
    }

    public List<Reservation> getAllFutureReservationOfMember(String memberEmail) {
        return Collections.unmodifiableList(reservations.getReservationOfMemberFiltered(this.members.getMemberByEmail(memberEmail),'F' ));
    }

    public List<Reservation> getReservationHistoryOfMember(String memberEmail) {
        return Collections.unmodifiableList(reservations.getReservationOfMemberFiltered(this.members.getMemberByEmail(memberEmail), 'H'));
    }

    public List<Reservation> getReservationHistory() {
        return Collections.unmodifiableList(reservations.getReservationHistory());
    }

    public List<Reservation> getFuturePendingReservationForDay(LocalDate date) {
        return Collections.unmodifiableList(reservations.getFuturePendingReservationForDay(date));
    }

    public List<Reservation> getFuturePendingReservationForWeek() {
        return Collections.unmodifiableList(reservations.getFuturePendingReservationForWeek());
    }

    public List<Reservation> getFutureApprovedReservationForDay(LocalDate date) {
        return Collections.unmodifiableList(reservations.getFutureApprovedReservationForDay(date));
    }

    public List<Reservation> getFutureApprovedReservationForWeek() {
        return Collections.unmodifiableList(reservations.getFutureApprovedReservationForWeek());
    }

    private List<Reservation> getFutureReservationForWeekPrivate() {
        return reservations.getFutureReservationForWeekPrivate();
    }

    public List<Reservation> getFutureReservationForWeek() {
        List<Reservation> futureRes = reservations.getFutureReservationForWeek();
        return Collections.unmodifiableList(futureRes);
    }

    public List<Reservation> getAllFutureReservationForWeek() {
        return Collections.unmodifiableList(reservations.getAllFutureReservationForWeek());
    }

    public List<Reservation> getFutureReservationForDay(LocalDate date) {
        return Collections.unmodifiableList(reservations.getFutureReservationForDay(date));
    }

    public List<Reservation> getFuturePendingReservationSameActivity(LocalDate date, Integer id, Reservation mergedReservation) {
        Activity activity = this.activities.findActivityById(id);
        Reservation mergedReservationOnList = reservations.findByResMadeAtByWho
                (mergedReservation.getReservationDateTime(),mergedReservation.
                        getReservationMember().getEmailAddress(),mergedReservation.getTrainingDate());
        return Collections.unmodifiableList(reservations. getFuturePendingReservationSameActivity(date, activity, mergedReservationOnList));
    }

    public List<Reservation> getClosedReservationOfMember(String memberEmail) {
        return Collections.unmodifiableList(reservations.getClosedReservationOfMember(this.members.getMemberByEmail(memberEmail)));
    }


//////////////////////////////////////////////////////////////////////// Scheduling methods //////////////////////////////////////////////////////////////////////////

    //This method returns a set of boats that are relevant to a given reservation (by type, by availability)
    public List<Boat> getRelevantBoats(Reservation clientReservation) {
        Reservation reservation = this.reservations.findByResMadeAtByWho(clientReservation.getReservationDateTime(),
                clientReservation.getReservationMember().getEmailAddress(),clientReservation.getTrainingDate());
        List<Boat> relevantBoats = boats.getRelevantBoats(reservation.getTrainingDate(), reservation.getActivity(), reservation.getBoatTypes());
        Boat participantPrivateBoat = boats.getBoatBySerialNum(reservation.getParticipantRower().getPrivateBoatSerialNumber());
        if (participantPrivateBoat !=null ) // meaning participant rower has boat
        {
            if (!participantPrivateBoat.isOutOfOrder()) // and if this boat is not OOO, then add it to relevant boats list
                relevantBoats.add(participantPrivateBoat);
        }
        return Collections.unmodifiableList(relevantBoats);
    }

    //this function return false if the reservation doesn't have a boat or it
    public boolean assignApprovedRowersToReservation(List<Member> approvedRowers, Reservation clientReservation, boolean loadProcess) throws ApprovedReservationWithNoBoatException, BoatSizeMismatchException {
        Reservation reservation = this.reservations.findByResMadeAtByWho(clientReservation.getReservationDateTime(),
                clientReservation.getReservationMember().getEmailAddress(),clientReservation.getTrainingDate());
        if(reservations.assignApprovedRowersToReservation(newMemberServerList(approvedRowers), reservation)) {
            if (!loadProcess)
                this.stateSaver.saveStateToXml();
            return true;
        }
        else
            return false;
    }


    public boolean updateApprovedStatus(Reservation clientReservation) {
        Reservation reservation = this.reservations.findByResMadeAtByWho(clientReservation.getReservationDateTime(),
                clientReservation.getReservationMember().getEmailAddress(),clientReservation.getTrainingDate());
        reservations.updateApprovedStatus(reservation);
        this.stateSaver.saveStateToXml();
        return true;
    }

    //This method gets reservation and assign a boat to it
    //It is also sets this boat to be reserved on that date and time window (activity).
    public boolean assignBoatToReservation(Reservation clientReservation, Boat boat, boolean loadProcess) {
        Boat boatOnList = boats.getBoatBySerialNum(boat.getSerialNum());
        Reservation reservation = this.reservations.findByResMadeAtByWho(clientReservation.getReservationDateTime(),
                clientReservation.getReservationMember().getEmailAddress(),clientReservation.getTrainingDate());
        if (reservations.assignBoatToReservation(reservation, boatOnList))// meaning there were no boats to assign
        {
            if (!loadProcess)
                this.stateSaver.saveStateToXml();
            return true;
        }
        else
            return false;
    }

    public boolean addReservationToClosedReservation(Reservation clientReservation) {
        //Reservation reservation = this.reservations.findByResMadeAtByWho(clientReservation.getReservationDateTime(),
             //   clientReservation.getReservationMember().getEmailAddress(),clientReservation.getTrainingDate());
        return reservations.addReservationToClosedReservation(clientReservation);
    }

    public boolean addReservationToOpenReservation(Reservation clientReservation) {
        Reservation reservation = this.reservations.findByResMadeAtByWho(clientReservation.getReservationDateTime(),
                clientReservation.getReservationMember().getEmailAddress(),clientReservation.getTrainingDate());
        if (reservations.addReservationToOpenReservation(reservation)) {
            this.stateSaver.saveStateToXml();
            return true;
        }
        return false;
    }

    public boolean removeReservationFromOpenReservation(Reservation clientReservation) {
        Reservation reservation = this.reservations.findByResMadeAtByWho(clientReservation.getReservationDateTime(),
                clientReservation.getReservationMember().getEmailAddress(),clientReservation.getTrainingDate());
        if (reservations.removeReservationFromOpenReservation(reservation))
            return true;
        return false;
    }

    public boolean removeReservationFromCloseReservation(Reservation clientReservation) {
        Reservation reservation = this.reservations.findByResMadeAtByWho(clientReservation.getReservationDateTime(),
                clientReservation.getReservationMember().getEmailAddress(),clientReservation.getTrainingDate());
        if (reservations.removeReservationFromCloseReservation(reservation)) {
                this.stateSaver.saveStateToXml();
                return true;
            }
            return false;
    }

    //This method gets two list of chosen members taken from to reservations, them into the main reservation.
    public boolean mergeReservations(List<Member> chosenMain, List<Member> chosenAdded, Reservation clientReservationMain, Reservation clientReservationAdded) throws BoatSizeMismatchException, ApprovedReservationWithNoBoatException {
        Reservation mainReservation = this.reservations.findByResMadeAtByWho(clientReservationMain.getReservationDateTime(),
                clientReservationMain.getReservationMember().getEmailAddress(),clientReservationMain.getTrainingDate());
        Reservation addedReservation = this.reservations.findByResMadeAtByWho(clientReservationAdded.getReservationDateTime(),
                clientReservationAdded.getReservationMember().getEmailAddress(),clientReservationAdded.getTrainingDate());
        reservations.mergeReservations(newMemberServerList(chosenMain), newMemberServerList(chosenAdded), mainReservation, addedReservation);
        this.stateSaver.saveStateToXml();
        return true;
    }


    public boolean isRowerAvailable(String memberEmail, Reservation clientReservation, Integer id) {
        Reservation reservation = this.reservations.findByResMadeAtByWho(clientReservation.getReservationDateTime(),
                clientReservation.getReservationMember().getEmailAddress(),clientReservation.getTrainingDate());
        return reservations.isRowerAvailable(this.members.getMemberByEmail(memberEmail),
                reservation,this.activities.findActivityById(id));
    }

    public boolean rejectReservation (Reservation clientReservation, boolean loadingProcess){
        Reservation reservation = this.reservations.findByResMadeAtByWho(clientReservation.getReservationDateTime(),
                clientReservation.getReservationMember().getEmailAddress(),clientReservation.getTrainingDate());
        reservations.rejectReservation(reservation);
        if(!loadingProcess)
            this.stateSaver.saveStateToXml();
        return true;
    }

    public List<Boat> autoAssignBoatToReservation (Set<Boat.BoatType> boatTypes, Reservation clientReservation){
        Reservation reservation = this.reservations.findByResMadeAtByWho(clientReservation.getReservationDateTime(),
                clientReservation.getReservationMember().getEmailAddress(),clientReservation.getTrainingDate());
        return this.reservations.autoAssignBoatToReservation(boatTypes,reservation);
    }


///////////////////////////////////////////////////////////////////////////////////// Login Method ////////////////////////////////////////////////////////////////////////////

    public boolean loginTest(String email) {
        return members.isMemberOnList(email);
    }

    public boolean setLoginMember(String email) {
        if (this.loginTest(email)) {
            this.loginMember = members.getMemberByEmail(email);
            return true;
        } else {
            return false;
        }
    }


    public boolean checkMemberPassword(String email, String password) {
        return members.checkMemberPassword(email, password);
    }

    public boolean addLoginMember(String memberEmail){
        return this.members.addLoginMember(this.members.getMemberByEmail(memberEmail));
    }

    public boolean removeLoginMember(String memberEmail){return this.members.removeLoginMember(this.members.getMemberByEmail(memberEmail));}


    //////////////////////////////////////////////////////XML/////////////////////////////////////////////////////
    // 1- activities, 2 - boats, 3 - members
    public String importXmlFromInputStream(int type, String xmlString, boolean unite, String loggedManagerEmail) throws XmlMultipleExceptions {
        InputStream stream = new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8));
        switch (type){
            case 1:
                schemaXml.unmarshalActivitiesFromXML(unite, stream);
                break;
            case 2:
                schemaXml.unmarshalBoatsFromXML(unite, stream);
                break;
            case 3:
                schemaXml.unmarshalMembersFromXML(unite, stream, loggedManagerEmail);
        }
        return "XML import Succeed";

    }

    // 1- activities, 2 - boats, 3 - members
    public String exportXmlToString (int type) throws JAXBException {
        switch (type){
            case 1:
                return schemaXml.marshalActivities();
            case 2:
                return schemaXml.marshalBoats();
            case 3:
                return schemaXml.marshalMembers();
        }
        return "hey";
    }

    public void saveState() {
        this.stateSaver.saveStateToXml();
    }

    public void loadState(){
        this.loader.loadState();
    }
}