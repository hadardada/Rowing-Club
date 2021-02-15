package bms.engine.activitiesManagement;

import bms.engine.boatsManagement.boat.Boat;
import bms.engine.activitiesManagement.activity.Activity;
import bms.engine.activitiesManagement.activity.ActivityExceptions.*;


import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

public class ActivitiesManagement implements Serializable {

    private List<Activity> activities;

    public ActivitiesManagement(){
        activities = new ArrayList<>();
    }


    ///////////////////////////////////// getters /////////////////////////////////////
    public List<Activity> getActivities() {
        return activities;
    }

    public Activity findActivityById(int id){
        for (Activity activity: activities){
            if (activity.getId() == id)
                return activity;
        }
        return null;
    }

    ///////////////////////////////////setters//////////////////////////////////////////

    //This method gets an activity and inserts it to the activity list
    //the insertion is sorted by the start time of the activity
    public void insertActivitySorted(Activity activity) {
        ListIterator<Activity> it = activities.listIterator();
        int compareStartingTimes;
        boolean stillLooking = true;
        while ((it.hasNext()) && (stillLooking)) {
            compareStartingTimes = (it.next().getStarts().compareTo(activity.getStarts()));
            if (compareStartingTimes > 0) // if we reached bms.engine.ActivitiesManagement.activity on list that starts later than the new one
            {
                it.previous(); // then adds the new bms.engine.ActivitiesManagement.activity before
                it.add(activity);
                stillLooking = false;
            }
        }
        if (stillLooking == true){
            it.add(activity); // went over the whole list and new bms.engine.ActivitiesManagement.activity starts later than everyone
        }
    }


    //This Method creates and add a new bms.engine.ActivitiesManagement.activity (=time window) to the activities list
    // if optionalType is not offered, should be received as NULL
    public Activity addNewActivity(LocalTime starts, LocalTime ends, String activityName, Boat.BoatType optionalType) throws EndTimeIsLowerException {
        if (ends.isBefore(starts))
            throw new EndTimeIsLowerException();
        Activity newActivity = new Activity(activityName, starts, ends, optionalType);
        checkOverlapFromList(newActivity);
        insertActivitySorted(newActivity);
        return newActivity;
    }
    //This Method finds the overlapping activities from list with a given bms.engine.ActivitiesManagement.activity and link them to each other
    public void checkOverlapFromList(Activity activityToCheck) {
        //this loop finds the range of activities to check overlap
        for (Activity activity : this.activities) {
            if (activity.getEnds().compareTo(activityToCheck.getStarts()) < 0) // while activities ends before new bms.engine.ActivitiesManagement.activity starts
                continue;
            if (activityToCheck.isActivityOverlapping(activity)) {
                // if there is an overlap, then add the activities to each other's overlappingActivities set
                activity.addActivityToOverlappingActivitiesSet(activityToCheck);
                activityToCheck.addActivityToOverlappingActivitiesSet(activity);
            }
            if (activity.getStarts().compareTo(activityToCheck.getEnds()) >= 0) // if new bms.engine.ActivitiesManagement.activity ends when/ before the rest of the activities begin
                continue;
        }
    }


    public boolean changeStartingTime(Activity activity, LocalTime starts) {
        activity.setStarts(starts);
            return true;
    }

    public boolean changeEndingTime(Activity activity, LocalTime ends) {
        activity.setEnds(ends);
        return true;
    }

    public boolean changeActivityName(Activity activity, String name) {
        boolean status = activity.setName(name);
        return status;
    }

    public boolean changeActivityBoatType(Activity activity, Boat.BoatType newType) {
        activity.setSpecifiedType(newType);
        return true;
    }

    //This method gets an activity and creates a new instance of activity with the same values
    // it adds the new activity to the activities lists, by calling the "addNewActivity" method,
    // and returns the new activity


    //This method removes activity from all overlapping activities
    public boolean removeOverlappingActivity(Activity activity) {
        //since overlapping is a symmetric relation, this loop goes over this activity's overlapping activities list
        //and removes this bms.engine.ActivitiesManagement.activity from their overlappingActivities list
        for (Activity anotherActivity : activity.getOverlappingActivities())
            anotherActivity.getOverlappingActivities().remove(activity);
        return true;
    }

    public boolean removeActivity (Activity activity){
        if (activities.isEmpty())
            return false;
        activities.remove(activity);
        return true;
    }


}
