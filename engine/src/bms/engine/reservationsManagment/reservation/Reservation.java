package bms.engine.reservationsManagment.reservation;

import bms.engine.activitiesManagement.activity.Activity;
import bms.engine.boatsManagement.boat.Boat;
import bms.engine.membersManagement.member.Member;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Reservation implements Serializable {


    private Member participantRower;
    private LocalDate trainingDate;
    private Activity trainingTime;
    private List<Boat.BoatType> boatTypes;
    private List<Member> wantedRowers;
    private List<Member> actualRowers;
    private LocalDateTime reservationDateTime;
    private Member reservationMember;
    private Boolean isApproved;
    private Boat reservationBoat;


    ///////////////// getters ////////////////


    public Member getParticipantRower () {return participantRower;}
    public LocalDate getTrainingDate () {return trainingDate;}
    public Activity getActivity () {return trainingTime;}
    public List<Boat.BoatType> getBoatTypes () {return boatTypes;}
    public List<Member> getWantedRowers () {return wantedRowers;}
    public List<Member> getActualRowers () {return actualRowers;}
    public LocalDateTime getReservationDateTime () {return reservationDateTime;}
    public Member getReservationMember () {return reservationMember;}
    public Boolean getIsApproved () {return isApproved;}
    public Boat getReservationBoat () {return reservationBoat;}

    ///////////////// setters ////////////////


    public boolean setParticipantRower (Member participantRower) {
        this.participantRower = participantRower;
        return true;
    }

    public boolean setTrainingDate (LocalDate trainingDate)  {
        LocalDate Today = LocalDate.now();
        this.trainingDate = trainingDate;
        return true;
    }

    public boolean setActivity (Activity trainingTime) {
        this.trainingTime = trainingTime;
        return true;
    }

    public boolean setBoatType (ArrayList<Boat.BoatType> boatTypes) {
        this.boatTypes = boatTypes;
        return true;
    }


    public boolean setReservationDateTime (LocalDateTime reservationDateTime) {
        this.reservationDateTime = reservationDateTime;
        return true;
    }

    public boolean setReservationMember (Member reservationMember) {
        this.reservationMember = reservationMember;
        return true;
    }

    public boolean setIsApproved (Boolean isApproved) {
        this.isApproved = isApproved;
        return true;
    }


    public boolean setWantedRowers (List<Member> wantedRowers) {
        this.wantedRowers = wantedRowers;
        return true;
    }

    public boolean setActualRowers (List<Member> actualRowers) {
        this.actualRowers.addAll(actualRowers);
        return true;
    }

    public boolean setBoat (Boat reservationBoat) {
        this.reservationBoat = reservationBoat;
        return true;
    }


    ///////////////// constructors ////////////////

    public Reservation
            (Member participantRower, LocalDate trainingDate, Activity trainingTime,
             List<Boat.BoatType> boatTypes, List<Member> wantedRowers, LocalDateTime reservationDateTime,
             Member reservationMember){

        this.participantRower = participantRower;
        this.trainingDate = trainingDate;
        this.trainingTime = trainingTime;
        this.boatTypes = boatTypes;
        this.wantedRowers = wantedRowers;
        this.actualRowers = new ArrayList<>();
        this.reservationDateTime = reservationDateTime;
        this.reservationMember = reservationMember;
        this.isApproved = null;
        this.reservationBoat = null;
    }

    public Reservation (Reservation other){

        this.participantRower = other.participantRower;
        this.trainingDate = other.trainingDate;
        this.trainingTime = other.trainingTime;
        this.boatTypes = new ArrayList<>(other.boatTypes);
        this.wantedRowers = new ArrayList<>(other.wantedRowers);
        this.actualRowers = null;
        this.reservationDateTime = LocalDateTime.now();
        this.reservationMember = other.reservationMember;
        this.isApproved = null;
        this.reservationBoat = null;
    }

    /////// other methods//////////////////
    public boolean reopenReservation (){
        this.setIsApproved(null); // making this reservation back to pending
        this.wantedRowers = this.actualRowers;
        this.wantedRowers.remove(this.participantRower);
        this.actualRowers = new ArrayList<>(); // since reservation is not approved, there aren't acutal rowers
        return true;
    }

    public boolean addRowersToActual (List<Member> rowers){
        if (this.actualRowers == null)
            this.actualRowers = new ArrayList<>();
        this.actualRowers.addAll(rowers);
        return true;
    }

    public boolean removeRowerFromWanted(Member rower){
        return this.wantedRowers.remove(rower);
    }
    public boolean releaseBoatFromReservation (){
        this.setBoat(null);
        if (Boolean.TRUE.equals(this.isApproved))
            this.setIsApproved(null);
        return true;
    }
}

