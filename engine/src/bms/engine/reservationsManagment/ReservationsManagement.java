package bms.engine.reservationsManagment;

import bms.engine.boatsManagement.boat.Boat;
import bms.engine.boatsManagement.boat.boatsListsExceptions.*;
import bms.engine.membersManagement.member.Member;
import bms.engine.activitiesManagement.activity.Activity;
import bms.engine.reservationsManagment.reservation.Reservation;
import bms.engine.reservationsManagment.reservation.reservationsExceptions.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;


public class ReservationsManagement implements Serializable {
    private Map<LocalDate, ArrayList<Reservation>> openReservations;
    private Map<LocalDate, ArrayList<Reservation>> closedReservations;

    public ReservationsManagement(){
        this.openReservations = new HashMap<>();
        this.closedReservations = new HashMap<>();
    }
    //This method goes over all future approved reservations, and releases a given boat
    //from all reservations it was assigned to.
    //It returns a map of dates and activities of the released reservations
    public Map <LocalDate, ArrayList<Activity>> releaseBoatFromAllReservations (Boat boat) {
        Map<LocalDate, ArrayList<Activity>> res = new HashMap<>();
        Set<LocalDate> dates = new HashSet<>();
        dates.addAll(closedReservations.keySet());
        for (LocalDate date : dates) {
            if (!date.isBefore(LocalDate.now())) { // date is not before today: meaning - future
                ArrayList<Reservation> reservationsForDay = new ArrayList<>();
                reservationsForDay.addAll(closedReservations.get(date));
                for (Reservation reservation : reservationsForDay) {
                    if (reservation.getReservationBoat() == boat) {
                        {
                            reservation.releaseBoatFromReservation();
                            removeReservationFromCloseReservation(reservation);
                            addReservationToOpenReservation(reservation);
                            if (!res.containsKey(reservation.getTrainingDate())) {
                                ArrayList<Activity> activities = new ArrayList<>();
                                res.put(reservation.getTrainingDate(), activities);
                            }
                            res.get(reservation.getTrainingDate()).add(reservation.getActivity());
                        }

                    }
                }
            }
        }
        return res;
    }

    //This method creates and adds new reservation to reservation Map (key is its trainingDate)
    //if trainingDate is before today, reservation is not created and exception is thrown
    //if participantRower (reservation owner) is in the additional rowers reservation is not created and exception is thrown.
    public Reservation addNewReservation(Member participantRower, LocalDate trainingDate, Activity trainingTime,
                                         List<Boat.BoatType> boatTypes, List<Member> wantedAdditionalRowers, LocalDateTime reservationDateTime, Member reservationMember) throws ParticipentRowerIsOnListException {

        LocalDate today = LocalDate.now();
        if ((wantedAdditionalRowers.contains(participantRower))&& (participantRower != null))
            throw new ParticipentRowerIsOnListException();

        Reservation newReservation = new Reservation(participantRower, trainingDate, trainingTime, // create the new reservation
                boatTypes, wantedAdditionalRowers, reservationDateTime, reservationMember);

        if (openReservations.containsKey(trainingDate)) { //add the new reservation to the open reservations map by the training date
            openReservations.get(trainingDate).add(newReservation);
        } else {
            ArrayList<Reservation> openReservation = new ArrayList<>();
            openReservation.add(newReservation);
            openReservations.put(trainingDate, openReservation);
        }

        for (Member rower : wantedAdditionalRowers) {         //add the new reservation to the involve rowers
            rower.getMyReservations().add(newReservation);
        }
        if (!wantedAdditionalRowers.contains(reservationMember)) {
            reservationMember.getMyReservations().add(newReservation);
        }

        if (participantRower != reservationMember) {
            participantRower.getMyReservations().add(newReservation);
        }

        return newReservation;
    }


    //This reservation approves a reservation, if reservation owner has a private boat
    //that its type matches the ones on the requested boat types.
    //
    public boolean approveResAutomatically(Member participantRower, Reservation reservation, Boat privateBoat) {
        try {
            if (privateBoat != null) {
                if (reservation.getBoatTypes().contains(privateBoat.getBoatType())) {
                    //if reservation owner has a private boat that matches one of the boatTypes requested
                    //and private boat is not out of order - then boat is automaticlly assign to member
                    if (!privateBoat.isOutOfOrder()) {
                        List<Member> newActual = new ArrayList<>();
                        newActual.addAll(reservation.getWantedRowers());
                        newActual.add(reservation.getParticipantRower());
                        assignBoatToReservation(reservation, privateBoat);
                        assignApprovedRowersToReservation(newActual, reservation);
                        updateApprovedStatus(reservation);
                        return true;
                    }
                }
            }
        } catch (BoatSizeMismatchException e)
        {
            e.getMessage();
            reservation.releaseBoatFromReservation();
        } catch (ApprovedReservationWithNoBoatException e) {
            //this method is making sure the private boat is found and then calls the assign method, so
            //exception is never expected to be thrown here
        }
        return false;
    }


    public void updateReservatioParticipantRower(Member participantRower, Reservation myReservation, Boat boat) throws ParticipentRowerIsOnListException {
        if (myReservation.getWantedRowers().contains(participantRower))
            throw new ParticipentRowerIsOnListException();
        this.removeReservationFromMember(myReservation.getParticipantRower(), myReservation);

        myReservation.setParticipantRower(participantRower);
        if (participantRower.getPrivateBoatStatus()) {
            approveResAutomatically(participantRower, myReservation,boat);
        }
    }

    public boolean updateReservatioTrainingDate(LocalDate trainingDate, Reservation myReservation) {

        this.removeReservationFromOpenReservation(myReservation);
        myReservation.setTrainingDate(trainingDate);
        this.addReservationToOpenReservation(myReservation);
        return true;
    }

    public boolean updateReservatioActivity(Activity trainingTime, Reservation myReservation) {
        myReservation.setActivity(trainingTime);
        return true;
    }

    public boolean updateReservatioBoatTypesRemove(Boat.BoatType boatType, Reservation myReservation) {
        if (myReservation.getBoatTypes().contains(boatType)) {
            myReservation.getBoatTypes().remove(boatType);
            return true;
        }
        return false;
    }

    public boolean updateReservatioBoatTypesAdd(Boat.BoatType boatType, Reservation myReservation) {
        if (myReservation.getBoatTypes().contains(boatType)) {
            return false;
        }
        myReservation.getBoatTypes().add(boatType);
        return true;
    }

    public LocalDateTime updateReservationDateTime(LocalDateTime reservationDateTime, Reservation myReservation) {
        myReservation.setReservationDateTime(reservationDateTime);
        return myReservation.getReservationDateTime();
    }

    //This reservation update the reservation approve status, return false if the reservation doesn't have boat
    // (means the reservation can't be approved)
    public Boolean updateReservationIsApproved(Boolean isApproved, Reservation myReservation) {
        if ((isApproved) && (myReservation.getReservationBoat() == null)) {
            return false;
        }
        myReservation.setIsApproved(isApproved);
        return myReservation.getIsApproved();
    }

    public List<Member> updateReservationAdditinalWantedRowers(List<Member> wantedRowers, Reservation myReservation) throws ParticipentRowerIsOnListException {
        if (wantedRowers.contains(myReservation.getParticipantRower())) {
            //Participant Rower ia already considered to be part of the requested rowers and should be on the ADDITIONAL rowers list
            wantedRowers.remove(myReservation.getParticipantRower());
            myReservation.setWantedRowers(wantedRowers);
            throw new ParticipentRowerIsOnListException();
        }
        myReservation.setWantedRowers(wantedRowers);
        for (Member rower : wantedRowers) { //add the updated reservation to the involve rowers
            if (!rower.getMyReservations().contains(myReservation))
                rower.getMyReservations().add(myReservation);
        }
        return myReservation.getWantedRowers();
    }


    public List<Member> updateReservationAdditinalActualRowers(List<Member> actualRowers, Reservation myReservation) {
        myReservation.setActualRowers(actualRowers);
        return myReservation.getActualRowers();
    }

    public Boat updateReservationBoat(Boat reservationBoat, Reservation myReservation) {
        myReservation.setBoat(reservationBoat);
        return myReservation.getReservationBoat();
    }

    // This method removes reservation link from member
    public boolean removeReservationFromMember(Member currentMember, Reservation myReservation) {
        List<Reservation> membersReservations = currentMember.getMyReservations();
        if (membersReservations.contains(myReservation)) {
            currentMember.getMyReservations().remove(myReservation);
            return true;
        }
        return false;
    }

    //This method sets reservation to be pending(pending = null) and releases the boat that was assigned to it
    //since editing an approved reservation is only available after the reservation is reopened.
    public boolean reopenReservation(Reservation reservation) {
        if (!reservation.getIsApproved())
            return false;
        reservation.reopenReservation();
        if (reservation.getReservationBoat() != null)
            reservation.getReservationBoat().releaseActivityFromBoat(reservation.getTrainingDate(), reservation.getActivity());
        reservation.releaseBoatFromReservation();

        this.removeReservationFromCloseReservation(reservation);
        this.addReservationToOpenReservation(reservation);
        return true;
    }

    // This method removes member from reservation (assuming this member is not owner of the reservation)
    public boolean removeMemberFromReservationActualList(Member currentMember, Reservation myReservation) {
        myReservation.getActualRowers().remove(currentMember);
        return true;
    }

    // This method removes member from reservation (assuming this member is not owner of the reservation)
    public boolean removeMemberFromReservationGeneral(Member currentMember, Reservation myReservation) {
        myReservation.getWantedRowers().remove(currentMember);
        return true;
    }


    public boolean addMemberToWantedRowers(Member currentMember, Reservation myReservation) {
        if (myReservation.getWantedRowers().contains(currentMember) || myReservation.getParticipantRower() == currentMember) {
            return false;
        }
        myReservation.getWantedRowers().add(currentMember);
        currentMember.getMyReservations().add(myReservation);
        return true;
    }

    //This Method delete a reservation permanently from all reservation lists.
    public boolean deleteReservation(Reservation reservationToDelete) {

        if (reservationToDelete == null){
            return false;
        }
        LocalDate today = LocalDate.now();

        if (reservationToDelete.getIsApproved()==null){
            removeReservationFromOpenReservation(reservationToDelete);
        }
        else {
            if (reservationToDelete.getIsApproved()) {
                for (Member member : reservationToDelete.getActualRowers()) {
                    removeReservationFromMember(member, reservationToDelete);
                }
            }
            removeReservationFromCloseReservation(reservationToDelete);
        }
        for (Member member : reservationToDelete.getWantedRowers()) {
            removeReservationFromMember(member, reservationToDelete);
        }
        removeReservationFromMember(reservationToDelete.getParticipantRower(),reservationToDelete);
        removeReservationFromMember(reservationToDelete.getReservationMember(), reservationToDelete);
        return true;
    }

    ///////////////////////////////////////////////////////////////////////////////////// Reservation filters ////////////////////////////////////////////////////////////////////////////
    //Returns all open reservations
    public List<Reservation> getOpenReservationForDate (LocalDate date){
        return openReservations.get(date);
    }

    public List<Reservation> getClosedReservationForDate (LocalDate date){
        return closedReservations.get(date);
    }

    public List<Reservation> getOpenReservation(){
        List<Reservation> openReservation = new ArrayList<>();
        if (!this.openReservations.isEmpty()){
            for (LocalDate date:this.openReservations.keySet()){
                openReservation.addAll(this.openReservations.get(date));
            }
        }
        return openReservation;
    }

    //Returns all closed reservations
    public List<Reservation> getClosedReservation(){
        List<Reservation> closeReservation = new ArrayList<>();
        if (!this.closedReservations.isEmpty()){
            for (LocalDate date:this.closedReservations.keySet()){
                closeReservation.addAll(this.closedReservations.get(date));
            }
        }
        return (closeReservation);
    }

    //This method returns reservations for a members with filter
    //H - history reservations, P - pending future reservations, F - all future reservations for member,
    public List<Reservation> getReservationOfMemberFiltered(Member member, char filter) {
        List<Reservation> filteredReservations = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (Reservation reservation : member.getMyReservations()) {
            if (filter == 'H'){
                if (reservation.getTrainingDate().isBefore(today))
                    filteredReservations.add(reservation);
            }
            else{
                if ((reservation.getTrainingDate().isAfter(today) || reservation.getTrainingDate().isEqual(today))) {
                    if (reservation.getIsApproved()==null && filter == 'P') {
                            filteredReservations.add(reservation);
                    }
                    else if (filter == 'F'){
                        filteredReservations.add(reservation);
                    }
                }
            }
        }
        return filteredReservations;
    }

    public List<Reservation> getReservationHistory() {
        List<Reservation> historyReservation = new ArrayList<>();
        int i = 1;
        while(i <= 7){
            if (this.closedReservations.containsKey(LocalDate.now().minusDays(i))){
                historyReservation.addAll(this.closedReservations.get(LocalDate.now().minusDays(i)));
            }
            if (this.openReservations.containsKey(LocalDate.now().minusDays(i))){
                historyReservation.addAll(this.openReservations.get(LocalDate.now().minusDays(i)));
            }
            i++;
        }
        return (historyReservation);
    }


    public List<Reservation> getFuturePendingReservationForDay(LocalDate date) {
        List<Reservation> futurePendingReservation = new ArrayList<>();

        if (this.openReservations.containsKey(date)) {
            for (Reservation reservation : this.openReservations.get(date))
                if (reservation.getIsApproved() == null) {
                    futurePendingReservation.add(reservation);
                }
        }

        return (futurePendingReservation);
    }

    public List<Reservation> getFuturePendingReservationForWeek() {
        List<Reservation> futurePendingReservation = new ArrayList<>();
        LocalDate today = LocalDate.now();
        int i = 0;
        while (i <= 7) {
            LocalDate trainingDay = today.plusDays(i);
            if (openReservations.containsKey(trainingDay)) {
                for (Reservation reservation : this.openReservations.get(trainingDay)) {
                    if (reservation.getIsApproved() == null) {
                        futurePendingReservation.add(reservation);
                    }
                }
            }
            i++;
        }
        return (futurePendingReservation);
    }

    public List<Reservation> getFutureApprovedReservationForDay(LocalDate date) {
        List<Reservation> futureApprovedReservation = new ArrayList<>();

        if (this.closedReservations.containsKey(date)) {
            for (Reservation reservation : this.closedReservations.get(date))
                if (reservation.getIsApproved()) {
                    futureApprovedReservation.add(reservation);
                }
        }
        return (futureApprovedReservation);
    }


    public List<Reservation> getFutureApprovedReservationForWeek() {
        List<Reservation> futureApprovedReservation = new ArrayList<>();
        LocalDate today = LocalDate.now();
        int i = 0;
        while (i <= 7) {
            LocalDate trainingDay = today.plusDays(i);
            if (this.closedReservations.containsKey(trainingDay)) {
                for (Reservation reservation : this.closedReservations.get(trainingDay)) {
                    if (reservation.getIsApproved()) {
                        futureApprovedReservation.add(reservation);
                    }
                }
            }
            i++;
        }
        return (futureApprovedReservation);
    }

    public List<Reservation> getFutureReservationForWeekPrivate() {
        List<Reservation> futureRes = new ArrayList<>();
        futureRes.addAll(this.getFuturePendingReservationForWeek());
        if (getFutureApprovedReservationForWeek().size() == 0) {
            return futureRes;
        }
        futureRes.addAll(this.getFutureApprovedReservationForWeek());
        return futureRes;
    }

    public List<Reservation> getFutureReservationForWeek() {
        List<Reservation> futureRes = getFutureReservationForWeekPrivate();
        return (futureRes);
    }

    public List<Reservation> getAllFutureReservationForWeek() {
        List<Reservation> futureRes = new ArrayList<>();
        LocalDate today = LocalDate.now();
        int i = 0;
        while (i <= 7) {
            LocalDate trainingDay = today.plusDays(i);
            if (this.openReservations.containsKey(trainingDay)){
                futureRes.addAll(this.openReservations.get(trainingDay));
            }
            if (this.closedReservations.containsKey(trainingDay)){
                futureRes.addAll(this.closedReservations.get(trainingDay));
            }
            i++;
        }
        return (futureRes);
    }

    public List<Reservation> getFutureReservationForDay(LocalDate date) {
        List<Reservation> futureRes = new ArrayList<>();
        futureRes.addAll(this.getFuturePendingReservationForDay(date));
        futureRes.addAll(this.getFutureApprovedReservationForDay(date));
        return (futureRes);
    }


    public List<Reservation> getFuturePendingReservationSameActivity(LocalDate date, Activity activity, Reservation mergedReservation) {
        List<Reservation> futurePendingReservation = new ArrayList<>();

        for (Reservation reservation : this.openReservations.get(date)) {
            if ((reservation.getActivity() == activity) && (reservation != mergedReservation)) {
                futurePendingReservation.add(reservation);
            }
        }
        return (futurePendingReservation);
    }


    public List<Reservation> getClosedReservationOfMember(Member member) {
        List<Reservation> historyReservation = new ArrayList<>();
        for (Reservation reservation : member.getMyReservations()) {
            if (reservation.getIsApproved()) {
                historyReservation.add(reservation);
            }
        }
        return (historyReservation);
    }


//////////////////////////////////////////////////////////////////////// Scheduling methods //////////////////////////////////////////////////////////////////////////

    //this function return false if the reservation doesn't have a boat or it
    public boolean assignApprovedRowersToReservation(List<Member> approvedRowers, Reservation reservation) throws ApprovedReservationWithNoBoatException, BoatSizeMismatchException {
        if (reservation.getReservationBoat() == null) {
            throw new ApprovedReservationWithNoBoatException();
        }
        if (reservation.getReservationBoat().getMaxNumOfRowers() != (approvedRowers.size())) {
            throw new BoatSizeMismatchException(approvedRowers.size(), reservation.getReservationBoat().getMaxNumOfRowers());
        }

        if (reservation.getActualRowers().isEmpty()){
            reservation.setActualRowers(approvedRowers);
        }

        else // then add list to the actual rower list
            reservation.addRowersToActual(approvedRowers);
        //Premission to edit and view reservation now should be taken from members that were originaly
        //in this reservation, but haven't got into the scheduling
        int sizeOfWanted = reservation.getWantedRowers().size();
        for (int i =sizeOfWanted-1; i>=0; i--) {
            if (!reservation.getActualRowers().contains(reservation.getWantedRowers().get(i)))
                removeReservationFromMember(reservation.getWantedRowers().get(i), reservation);
        }
        return true;
    }


    public boolean updateApprovedStatus(Reservation reservation) {
        addReservationToClosedReservation(reservation);
        removeReservationFromOpenReservation(reservation);
        updateReservationIsApproved(true, reservation);
        return true;
    }

    //This method gets reservation and assign a boat to it
    //It is also sets this boat to be reserved on that date and time window (activity).
    public boolean assignBoatToReservation(Reservation reservation, Boat boat) {
        if (boat == null)// meaning there were no boats to assign
            return false;
        updateReservationBoat(boat, reservation);
        boat.bindBoatToActivity(reservation.getTrainingDate(), reservation.getActivity());
        return true;
    }


    public boolean addReservationToClosedReservation(Reservation reservation) {
        LocalDate date = reservation.getTrainingDate();
        if (closedReservations.containsKey(date)) { //add the new reservation to the closed reservations map by the training date
            closedReservations.get(date).add(reservation);
        } else {
            ArrayList<Reservation> closedReservation = new ArrayList<>();
            closedReservation.add(reservation);
            closedReservations.put(date, closedReservation);
        }
        return true;
    }

    public boolean addReservationToOpenReservation(Reservation reservation) {
        if (reservation.getIsApproved() == null) {
            LocalDate date = reservation.getTrainingDate();
            if (openReservations.containsKey(date)) { //add the new reservation to the open reservations map by the training date
                openReservations.get(date).add(reservation);
            } else {
                ArrayList<Reservation> reservations = new ArrayList<>();
                reservations.add(reservation);
                openReservations.put(date, reservations);
            }
            return true;
        }
        return false;
    }

    //This method removes all future reservations with that activity on them
    public boolean removeReservationsByActivity(Activity activity){
        List<Reservation> reservations = getAllFutureReservationForWeek();
        for (Reservation reservation : reservations) {
            if (reservation.getActivity() == activity) {
                this.deleteReservation(reservation);
            }
        }
        return true;
    }

    public boolean removeReservationFromOpenReservation(Reservation reservation) {
        LocalDate date = reservation.getTrainingDate();
        if (this.openReservations.containsKey(date)) {
            if (this.openReservations.get(date).contains(reservation)) {
                this.openReservations.get(date).remove(reservation);
                return true;
            }
            return false;
        }
        return false;
    }

    public boolean removeReservationFromCloseReservation(Reservation reservation) {
        LocalDate date = reservation.getTrainingDate();
        if (this.closedReservations.containsKey(date)) {
            if (this.closedReservations.get(date).contains(reservation)) {
                this.closedReservations.get(date).remove(reservation);
                return true;
            }
            return false;
        }
        return false;
    }

    //This method gets two list of chosen members taken from to reservations, them into the main reservation.
    public boolean mergeReservations(List<Member> chosenMain, List<Member> chosenAdded, Reservation mainReservation, Reservation addedReservation) {


        if (chosenAdded.contains(addedReservation.getParticipantRower())) { // if the main rower is moved from one reservation to another
            //than delete its original reservation
            deleteReservation(addedReservation);
        } else {
            addedReservation.getWantedRowers().remove(addedReservation.getParticipantRower());
            for (Member member : chosenAdded) {
                this.removeMemberFromReservationGeneral(member, addedReservation);
                this.removeReservationFromMember(member, addedReservation);
            }
        }

        for (Member member : chosenAdded) {
            if (!mainReservation.getWantedRowers().contains(member)) {
                this.addMemberToWantedRowers(member, mainReservation);
                member.getMyReservations().add(mainReservation);
            }
        }
        if (chosenAdded.contains(mainReservation.getParticipantRower())){
            chosenAdded.remove(mainReservation.getParticipantRower());
        }

        Set<Member> combine = new HashSet<>(chosenAdded);
        combine.addAll(chosenMain);
        List<Member> wantedRowerMain = new ArrayList<>(mainReservation.getWantedRowers());
        for (Member member : wantedRowerMain) {
            if (!combine.contains(member)) {
                this.removeMemberFromReservationGeneral(member, mainReservation);
                this.removeReservationFromMember(member, mainReservation);
            }
        }
       /* if (mainReservation.getWantedRowers().contains(mainReservation.getParticipantRower()))
        */
        return true;
    }


    public boolean isRowerAvailable(Member member, Reservation reservation, Activity activity) {
        for (Reservation reservationMember : this.getFutureApprovedReservationForDay(reservation.getTrainingDate())) {
            if (reservationMember.getActivity().getOverlappingActivities().contains(activity) || reservationMember.getActivity() == activity) {
                if (reservationMember.getActualRowers().contains(member)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean rejectReservation (Reservation reservation){
        reservation.setIsApproved(false);
        this.removeReservationFromOpenReservation(reservation);
        this.addReservationToClosedReservation(reservation);
        return true;
    }

    public List<Boat> autoAssignBoatToReservation (Set<Boat.BoatType> boatTypes, Reservation reservation){
        Map<Boat,Integer> autoBoats = new HashMap<>();
        List<Member> resMembers = new ArrayList<>(reservation.getWantedRowers());
        List<Boat> boats = new ArrayList<>();
        resMembers.add(reservation.getParticipantRower());
        for (Reservation reservation1 : getClosedReservation()){
            if (reservation1.getIsApproved()) {
                for (Member member : resMembers) {
                    if (!reservation1.getActualRowers().contains(member)) {
                        break;
                    }
                }
                if (boatTypes.contains(reservation1.getReservationBoat().getBoatType())) {
                    if (autoBoats.containsKey(reservation1.getReservationBoat())) {
                        int num = autoBoats.get(reservation1.getReservationBoat());
                        autoBoats.put(reservation1.getReservationBoat(), num++);
                    } else {
                        autoBoats.put(reservation1.getReservationBoat(), 0);
                    }
                }
            }
        }

        for (int i = 0; i < autoBoats.size(); i++){
            Boat maxBoat = Collections.max(autoBoats.entrySet(), Map.Entry.comparingByValue()).getKey();
            boats.add(maxBoat);
            autoBoats.remove(maxBoat);
        }
        return boats;
    }


    public Reservation findByResMadeAtByWho(LocalDateTime reservationMadeAt, String reservationMemberEmail, LocalDate date){
        if (this.closedReservations.containsKey(date)) {
            for (Reservation reservation : this.closedReservations.get(date)) {
                if (reservation.getReservationDateTime().equals(reservationMadeAt)
                        && reservation.getReservationMember().getEmailAddress().equals(reservationMemberEmail)){
                    return reservation;
                }
            }
        }
        if (this.openReservations.containsKey(date)) {
            for (Reservation reservation : this.openReservations.get(date)) {
                if (reservation.getReservationDateTime().equals(reservationMadeAt)
                        && reservation.getReservationMember().getEmailAddress().equals(reservationMemberEmail)){
                    return reservation;
                }
            }
        }
        return null;
    }


}
