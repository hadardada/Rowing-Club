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
    String reservationMadeBy;
    String getReservationMadeAt;
    int boatID;
    List<String> actualMemberEmails;
    boolean status;
    String boat;

public ReservationParameters(Reservation reservation){
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
    this.boatID = reservation.getReservationBoat().getSerialNum();
    List<String> actual = new ArrayList<>();
    for (Member member : reservation.getActualRowers()){
        actual.add(member.getEmailAddress());
    }
    this.actualMemberEmails = actual;
    this.status = reservation.getIsApproved();
    this.activityTime = reservation.getActivity().toString();
    this.getReservationMadeAt = reservation.getReservationDateTime().toString();
    this.boat = reservation.getReservationBoat().toString();
}
}
