package bms.engine.activitiesManagement.activity;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import bms.engine.boatsManagement.boat.Boat;

public class Activity implements Serializable {
    static int serialNumberCount;

    //class data members
    private String name;
    private LocalTime starts;
    private LocalTime ends;
    private Boat.BoatType specifiedType;
    private Set<Activity> overlappingActivities;
    private int id;

    ///////////////// constructors ////////////////
    public Activity(String activityName, LocalTime from, LocalTime until, Boat.BoatType optionalType){
        this.name = activityName;
        this.starts = from;
        this.ends = until;
        this.specifiedType = optionalType;
        this.overlappingActivities = new HashSet<>();
        this.id = ++serialNumberCount;
    }

    public Activity (String activityName, LocalTime from, LocalTime until, int id, Boat.BoatType type){
        this.name = activityName;
        this.starts = from;
        this.ends = until;
        this.overlappingActivities = new HashSet<>();
        this.specifiedType = type;
        this.id = id;
    }

    public Activity (Activity other){
        this.name = other.name;
        this.starts = other.starts;
        this.ends = other.ends;
        this.specifiedType = other.specifiedType;
        this.overlappingActivities =new HashSet<>();
        this.overlappingActivities.addAll(other.overlappingActivities);
    }
    ///////////////// getters ////////////////

    public String getName() {
        return name;
    }

    public LocalTime getStarts() {
        return starts;
    }

    public LocalTime getEnds() {
        return ends;
    }

    public int getId(){
        return id;
    }

    public Boat.BoatType getSpecifiedType() {
        return specifiedType;
    }

    public Set<Activity> getOverlappingActivities() {
        return overlappingActivities;
    }

    public String toString(){
        return String.format("Name: "+ name +" starts:" + starts + " ends:" + ends );
    }
    ///////////////// setters ////////////////

    public boolean setName(String name) {
        if (name.equals("")){
            return false;
        }
        this.name = name;
        return true;
    }

    public void setStarts(LocalTime starts) {
        this.starts = starts;
    }

    public void setEnds(LocalTime ends) {
        this.ends = ends;
    }
    public void setId(int id) {this.id = id;}
    public void setSpecifiedType(Boat.BoatType specifiedType) {
        this.specifiedType = specifiedType;
    }

    ///////////////// Methods ////////////////

    public void addActivityToOverlappingActivitiesSet (Activity other) {
        this.overlappingActivities.add(other);
    }

    public void setOverlappingActivities(Set<Activity> overlappingActivities) {
        this.overlappingActivities = overlappingActivities;
    }

    public boolean isStartingEarlier (Activity other)
    {
        if (this.starts.compareTo(other.getStarts())<0)
            return true;
        else
            return false;
    }
    public boolean isActivityOverlapping(Activity other)
    {
        int startsToStartsCompare, startsToEndsCompare, endsToEndsCompare, endsToStartsCompare;
        startsToStartsCompare = this.starts.compareTo(other.starts);
        startsToEndsCompare = this.starts.compareTo(other.ends);
        endsToEndsCompare = this.ends.compareTo(other.ends);
        endsToStartsCompare = this.ends.compareTo(other.starts);

        if (startsToStartsCompare<= 0){ // if this bms.engine.ActivitiesManagement.activity starts before/when other bms.engine.ActivitiesManagement.activity starts
            if (endsToEndsCompare >=0) // if this bms.engine.ActivitiesManagement.activity ends after/when other bms.engine.ActivitiesManagement.activity ends
                return true; // then they are overlapping
            else if(endsToStartsCompare >0 ) // if this bms.engine.ActivitiesManagement.activity ends after other bms.engine.ActivitiesManagement.activity starts
                return true;
        }
        else { // if this bms.engine.ActivitiesManagement.activity starts after other bms.engine.ActivitiesManagement.activity
            if (endsToEndsCompare <= 0 ) // if this bms.engine.ActivitiesManagement.activity ends before/when other bms.engine.ActivitiesManagement.activity ends
                return true; // then they are overlapping
            else if (startsToEndsCompare < 0) // if this bms.engine.ActivitiesManagement.activity starts before other bms.engine.ActivitiesManagement.activity ends
                return true;
        }
        return false; // if we reached this point, activities are not overlapping
    }
}

