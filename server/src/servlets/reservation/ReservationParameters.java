package servlets.reservation;

import bms.engine.boatsManagement.boat.Boat;
import bms.engine.membersManagement.member.Member;
import bms.engine.reservationsManagment.reservation.Reservation;

import java.util.ArrayList;
import java.util.List;

public class ReservationParameters {
    String participantRowerEmail;
    String trainingDate;
    int activityID;
    String activityTime;
    List<String> boatTypes;
    List<String> wantedMemberEmails;
    List<String> wantedMemberNames;
    String reservationMadeBy;
    String getReservationMadeAt;
    int boatID;
    List<String> actualMemberEmails;
    int status;
    String boat;
    boolean isManager;

public ReservationParameters(Reservation reservation,boolean isManager){
    this.participantRowerEmail = reservation.getParticipantRower().getEmailAddress();
    this.trainingDate = reservation.getTrainingDate().toString();
    this.activityID = reservation.getActivity().getId();
    List<String> boatTypes = new ArrayList<>();
    for (Boat.BoatType boatType : reservation.getBoatTypes()){
        boatTypes.add(boatType.getShortName());
    }
    this.boatTypes = boatTypes;
    List<String> wanted = new ArrayList<>();
    for (Member member : reservation.getWantedRowers()){
        wanted.add(member.getEmailAddress());
    }
    this.wantedMemberEmails = wanted;
    List<String> wantedNames = new ArrayList<>();
    for (Member member : reservation.getWantedRowers()){
        wantedNames.add(member.getName());
    }
    this.wantedMemberNames = wantedNames;
    if (!(reservation.getReservationBoat()==null)){
        this.boatID = reservation.getReservationBoat().getSerialNum();
        this.boat = reservation.getReservationBoat().toString();
    }
    List<String> actual = new ArrayList<>();
    for (Member member : reservation.getActualRowers()){
        actual.add(member.getEmailAddress());
    }
    this.actualMemberEmails = actual;
    if (reservation.getIsApproved()==null){
        this.status = 1;
    }
    else if(reservation.getIsApproved()){
        this.status = 2;
    }
    else {
        this.status = 3;
    }
    this.activityTime = reservation.getActivity().toString();
    this.getReservationMadeAt = reservation.getReservationDateTime().toString();
    this.reservationMadeBy = reservation.getReservationMember().getEmailAddress();
    this.isManager = isManager;
}
}
