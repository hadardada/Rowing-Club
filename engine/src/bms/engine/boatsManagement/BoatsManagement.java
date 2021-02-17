package bms.engine.boatsManagement;

import bms.engine.boatsManagement.boat.Boat;
import bms.engine.activitiesManagement.activity.Activity;
import bms.engine.boatsManagement.boat.boatsListsExceptions.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

public class BoatsManagement implements Serializable {

    private Map<Integer, Boat> boats;
    private Map<Boat.BoatType, Integer> boatTypes;

    public BoatsManagement(){
        this.boats = new HashMap<>();
        this.boatTypes = new HashMap<>();
    }

    public Map<Integer, Boat> getBoats() {
        return boats;
    }

    public Boat getBoatBySerialNum(Integer serNum) {
        return boats.get(serNum);
    }

    public List<Boat> getRelevantBoats (LocalDate date, Activity activity, List<Boat.BoatType> requestedBoatTypes){
        List<Boat> relevantBoats = new ArrayList<>();
        for (Boat boat : this.boats.values()) {
            if ((!boat.isPrivateProperty()) && (boat.checkBoatAvailability(date, activity))) {  //the above checks if bms.engine.boatsManagement.boat is not private and available
                for (Boat.BoatType requestedType : requestedBoatTypes) {
                    // checks on each bms.engine.boatsManagement.boat from boats list if it matches one of the types requested in the reservation
                    if (boat.getBoatType().compareBoatTypes(requestedType))
                        relevantBoats.add(boat);
                }
            }

        }
        return relevantBoats;
    }

    //This method adds a new boat with the given parameters to boats list
    // throws exception if boat with the same serial number is already on the engine's boats list
    public boolean addNewBoat(String name, Integer serialNum, Boolean isPrivate, Boolean isStalled, Boat.BoatType newBoatType)
            throws BoatAlreadyExistsException, HelmsmanException, SingleWithTwoOarsException {
        if (serialNum != null) {
            if (this.boats.containsKey(serialNum))
                throw new BoatAlreadyExistsException(serialNum);
        }
        if ((newBoatType.getBoatSize().equals(Boat.BoatType.BoatSize.EIGHT)) && (!newBoatType.hasHelmsman())) //no helmsman but 8 rowers
            throw new HelmsmanException(8);
        if (newBoatType.getBoatSize().equals(Boat.BoatType.BoatSize.SOLO)) {
            if (newBoatType.isSingleOar())  //solo boat with one oar - impossible!
                throw new SingleWithTwoOarsException();
            else if (newBoatType.hasHelmsman()) //solo boat with coxswain - impossible!
                throw new HelmsmanException(1);
        }

        Boat newBoat = new Boat(name, serialNum, isPrivate, isStalled, newBoatType);
        if (serialNum == null) {
            int serialCheck = newBoat.getSerialNumberCount()-1;
            while (this.boats.containsKey(serialCheck)) {
                serialCheck++;
            }
            newBoat.setSerialNumberCount(serialCheck + 1);
            newBoat.setSerialNum(serialCheck);
        }
        boats.put(newBoat.getSerialNum(), newBoat);
        putBoatTypeOnTypesMap(newBoat.getBoatType());
        return true;
    }

    //put new boat type on map or, if already exists, increases its counter
    public void putBoatTypeOnTypesMap(Boat.BoatType newBoatType) {
        Integer counter = boatTypes.get(newBoatType);
        if (counter == null)
            counter = 0;
        this.boatTypes.put(newBoatType, ++counter);
    }

    //remove bms.engine.boatsManagement.boat type from map, or decreases its counter
    public void removeFromTypesMap(Boat.BoatType type) {
        Integer counter = boatTypes.get(type);
        if (counter != null) {
            if (counter == 1) // last one
                this.boatTypes.remove(type);
            else
                this.boatTypes.put(type, --counter);
        }
    }


    ////////////////////////////// boat updates/////////////////////////////////////////

    public boolean updateBoatName(Boat boat, String newName) {
        boolean status = (boat.setBoatName(newName));
        return status;
    }

    public boolean updatePrivate(Boat boat, boolean newOwnership) {
        boolean status = (boat.setPrivateProperty(newOwnership));
        return status;
    }

    //This method returns true only if boat new status is different from before
    public boolean changeBoatActiveStatus(Boat boatToChange, boolean outOfOrder) {
        if (outOfOrder == boatToChange.isOutOfOrder()) // if status was not about to be changed
            return false; // do nothing
        else {
            boatToChange.setOutOfOrder(outOfOrder);
            if (outOfOrder) // boat has been updated to be out of ordered
                removeFromTypesMap(boatToChange.getBoatType());
            else //new status - boat's type is back to be on the boat type list this boathouse is offering
                removeFromTypesMap(boatToChange.getBoatType());
        }
        return true;
    }

    //This method allows changing a given boat's number of oars (true - 1, false - 2)
    //it updates boatType's Map
    public void changeOars(Boat boat, boolean isSingleOar) throws SingleWithTwoOarsException {
        removeFromTypesMap(boat.getBoatType());
        boat.getBoatType().setOars(isSingleOar);
        putBoatTypeOnTypesMap(boat.getBoatType());
    }

    //This method updates a given boat's coxswain presence (true - there is, false - there isn't)
    //it updates boatType's Map
    public boolean changeHelmsman(Boat boat, Boolean hasHelmsman) throws HelmsmanException {
        try {
            removeFromTypesMap(boat.getBoatType());
            boat.getBoatType().setHelmsman(hasHelmsman);
            putBoatTypeOnTypesMap(boat.getBoatType());
            return true;
        } catch (HelmsmanException e) {
            // since the exception is thrown from setter, boat type removal from boat types map already happened
            // but if it was thrown it means there was no change, so it should be returned to BoatTypes Map
            putBoatTypeOnTypesMap(boat.getBoatType());
            throw e;
        }


    }

    //This method updates a given boat to be coastal(true) or not (false)
    //it updates boatType's Map
    public boolean changeCoastal(Boat boat, Boolean coastal) {
        removeFromTypesMap(boat.getBoatType());
        boat.getBoatType().setCoastal(coastal);
        putBoatTypeOnTypesMap(boat.getBoatType());
        return true;
    }

    //This method changes a given boat serial number
    //returns false if a boat with such number is already in the boats map (so the update did not happen)
    public boolean changeBoatsSerialNumber(Boat boat, int newNum) {
        int oldNum = boat.getSerialNum();
        if (boats.containsKey(newNum))
            return false;
        else {
            if (oldNum != newNum) // making sure there is an actual change
                this.boats.remove(oldNum); // first remove the changed bms.engine.boatsManagement.boat from map
            boat.setSerialNum(newNum);
            this.boats.put(newNum, boat);// then add it with its new key ( = new serial number)
            return true;
        }
    }


    //This method deletes the boat permanently from boats list and from type list
    public boolean removeBoatFromList(Boat boat) {
        if (!this.boats.containsKey(boat.getSerialNum()))
            return false;
        boats.remove(boat.getSerialNum());
        removeFromTypesMap(boat.getBoatType());
        return true;
    }

    //This method removes days that have passed from scheduling map of a given boat
    public void cleanHistoryFromBoats() {
        for (Map.Entry pair : boats.entrySet()) {
            Integer serialNum = (Integer) pair.getKey();
            boats.get(serialNum).cleanBoatHistory();
        }
    }

    public List<Boat.BoatType> getCurrentBoatTypes() {
        List<Boat.BoatType> currentBoatTypes = new ArrayList<>();
        if (!this.boatTypes.isEmpty())
            currentBoatTypes.addAll(this.boatTypes.keySet());
        return currentBoatTypes;
    }

    public boolean deleteActivitiesFromBoatsScheduling (Boat boat, Map <LocalDate, ArrayList<Activity>> activitiesToDelete) {
        Set<LocalDate> dates = new HashSet<>();
        dates.addAll(activitiesToDelete.keySet());
        for (LocalDate date : dates) {
            ArrayList<Activity> activitiesForDay = new ArrayList<>();
            activitiesForDay.addAll(activitiesToDelete.get(date));
            for (Activity activity : activitiesForDay) {
                deleteSingleActivityFromBoatsScheduling(boat, activity,date);
            }
        }
        return true;
    }

    public boolean removeActivityFromAllBoats(Activity activity) {
        // removes activity (if found) on that day in the boat scheduling map
        for (Boat boat : this.boats.values()) {
            for (LocalDate date : boat.getBoatScheduling().keySet())
                boat.releaseActivityFromBoat(date, activity);
        }
        return true;
    }

    //This method removed the activity from a given date from a given boat's scheduling
    public boolean deleteSingleActivityFromBoatsScheduling(Boat boat, Activity activityToDelete, LocalDate date) {
        ArrayList<Activity> activitiesForDay = new ArrayList<>();
        activitiesForDay.addAll(boat.getBoatScheduling().get(date));
        for (Activity activity : activitiesForDay) {
            if (activity == activityToDelete) {
                boat.getBoatScheduling().get(date).remove(activity);
                // if (boat.getBoatScheduling().get(date).isEmpty())
                //    boat.getBoatScheduling().remove(date);
            }
        }
        return true;
    }
}















