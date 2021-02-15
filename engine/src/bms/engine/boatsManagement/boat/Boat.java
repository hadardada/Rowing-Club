package bms.engine.boatsManagement.boat;

import bms.engine.boatsManagement.boat.boatsListsExceptions.*;
import bms.engine.activitiesManagement.activity.Activity;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.ArrayList;
import java.time.LocalDate;

public class Boat implements Serializable {
    static int serialNumberCount;
    private int serialNum;
    private String boatName;
    private boolean privateProperty;
    private boolean outOfOrder;
    private BoatType type;
    private Map<LocalDate, List<Activity>> boatScheduling;


    public static class BoatType implements Serializable{
        public enum BoatSize implements Serializable{
            SOLO, PAIR, FOUR, EIGHT
        }
        private BoatSize rowersNum;
        private Boolean singleOar;
        private Boolean wide;
        private Boolean helmsman;
        private Boolean coastal;
        private String shortName;
        ///shorts map ///

        static Map<BoatSize, String> shortsRowers;

        static {
            shortsRowers = new HashMap<>();
            shortsRowers.put(BoatSize.SOLO, "1");
            shortsRowers.put(BoatSize.PAIR, "2");
            shortsRowers.put(BoatSize.FOUR, "4");
            shortsRowers.put(BoatSize.EIGHT, "8");
        }

        public BoatType(){

        }
        //boat type constructor
        public BoatType(BoatSize numOfRowers, Boolean hasOneOar, Boolean isWide, Boolean hasHelmsman, Boolean isCoastal) {
            this.rowersNum = numOfRowers;
            this.singleOar = hasOneOar;
            this.wide = isWide;
            this.helmsman = hasHelmsman;
            this.coastal = isCoastal;
            setShortName(this);
        }

        public BoatType(BoatType other){
            this.rowersNum = other.rowersNum;
            this.singleOar = other.singleOar;
            this.wide = other.wide;
            this.helmsman = other.helmsman;
            this.coastal = other.coastal;
            setShortName(this);
        }

            ////// boat type getters/////
        public BoatSize getBoatSize() {
            return rowersNum;
        }

        public Boolean isSingleOar() {
            return singleOar;
        }

        public Boolean isWide() {
            return wide;
        }

        public Boolean hasHelmsman() {
            return helmsman;
        }

        public Boolean isCoastal() {
            return coastal;
        }

        public String getShortName() {
            return shortName;
        }

        public int getMaxBoatSize(){
            //This method returns an int value of maximum rowers this boat contains, based on its type
            int rowersNum = (this.shortsRowers.get(this.rowersNum)).charAt(0) - '0'; // char values taken from "shortsRowers" map
            if (this.helmsman) // helmsman is one more person this boat can contain
                rowersNum++;
            return rowersNum;

        }
        ///// boat type setters/////

        private boolean setRowersNum(BoatSize size) {
            rowersNum = size;
            setShortName(this);
            return true;
        }

        private boolean setWidth(boolean isWide) {
            wide = isWide;
            setShortName(this);
            return true;
        }

        public boolean setOars(boolean isSingle) throws SingleWithTwoOarsException {
            if ((this.rowersNum.equals(BoatSize.SOLO))&&(isSingle)) // if tries to change a single boat to have a single oar
                    throw new SingleWithTwoOarsException();
            singleOar = isSingle;
            setShortName(this);
            return true;
        }

        public boolean setCoastal(boolean isCoastal) {
            coastal = isCoastal;
            setShortName(this);
            return true;
        }

        public boolean setHelmsman(boolean hasHelmsman) throws HelmsmanException {
            if ((!hasHelmsman)&&(this.rowersNum.equals(BoatSize.EIGHT))) // if the update makes a eight boat with no coxwain
                throw new HelmsmanException(8);
            if ((hasHelmsman)&&(this.rowersNum.equals(BoatSize.SOLO))) // if the update makes a single boat with coxwain
                throw new HelmsmanException(1);
            helmsman = hasHelmsman;
            setShortName(this);
            return true;
        }

        private void setShortName(BoatType typeOfBoat) {
            this.shortName = shortsRowers.get(this.rowersNum);
            if (Boolean.TRUE.equals(this.singleOar)) {
                if (Boolean.TRUE.equals(this.helmsman))
                    this.shortName = this.shortName.concat("+");
                else
                    this.shortName = this.shortName.concat("-");
            } else {
                this.shortName = this.shortName.concat("X");
                if (Boolean.TRUE.equals(this.helmsman))
                    this.shortName = this.shortName.concat("+");
            }

            if (Boolean.TRUE.equals(this.wide))
                this.shortName = this.shortName.concat(" wide");

            if (Boolean.TRUE.equals(this.coastal))
                this.shortName = this.shortName.concat(" coastal");
        }


        ////////////////boat type compare, hashcode and equals////////////
        @Override
        public int hashCode()
        {
            return shortName.hashCode();
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o)
                return true;
            if (o == null || o.getClass()!= this.getClass())
                return false;
            else {
                BoatType other = (BoatType) o;
                return (this.shortName.equals(other.getShortName()));
        }
        }

        public boolean compareBoatTypes(BoatType compareTo) {
            //checks if this boatType can answer the needs of somebody
            //meaning, if some of the fields weren't specified, they automaticlly mathces the fields in this boat.
            if ((compareTo.rowersNum == null) || (this.rowersNum.equals(compareTo.rowersNum))) {
                if ((compareTo.coastal == null) || (this.coastal.equals(compareTo.coastal))) {
                    if ((compareTo.helmsman == null) || (this.helmsman.equals(compareTo.helmsman))) {
                        if ((compareTo.wide == null) || (this.wide.equals(compareTo.wide))) {
                            if ((compareTo.singleOar == null) || (this.singleOar.equals(compareTo.singleOar))) {
                                return true; // only if all fields are the same
                            }
                        }
                    }
                }
            }

            return false;
        }
    }
    ///////////////// constructors ////////////////

    public Boat(String name, Integer serialNum, boolean isPrivate, boolean isOutOfOrder, BoatType typeOfBoat) {
        this.boatName = name;
        if (serialNum==null)
            this.serialNum = ++serialNumberCount;
        else// might get this value from XML file and in that case, no need to autofill
            this.serialNum = serialNum;
        this.privateProperty = isPrivate;
        this.outOfOrder = isOutOfOrder;
        this.type = typeOfBoat;
        this.boatScheduling = new HashMap<>();
    }

    public Boat(){}

    ///////////////// getters ////////////////

    public int getSerialNum() {
        return serialNum;
    }

    public int getSerialNumberCount(){return serialNumberCount;}

    public String getBoatName() {
        return boatName;
    }

    public boolean isPrivateProperty() {
        return privateProperty;
    }

    public boolean isOutOfOrder() {
        return outOfOrder;
    }

    public BoatType getBoatType() {
        return type;
    }

    public Map<LocalDate, List<Activity>> getBoatScheduling() {
        return boatScheduling;
    }

    public String toString() {
        return String.format("boat serial num:" + serialNum + " Type:" + type.shortName);
    }

    public int getMaxNumOfRowers() {
        //This method returns an int value of maximum rowers this boat contains, based on its type
        int rowersNum = (this.type.shortsRowers.get(this.type.rowersNum)).charAt(0) - '0'; // char values taken from "shortsRowers" map
        if (this.type.helmsman) // helmsman is one more person this boat can contain
            rowersNum++;
        return rowersNum;
    }
    ///////////////// setters ////////////////

    public boolean setSerialNum(int serialNum) {
        this.serialNum = serialNum;
        return true;
    }

    public boolean setBoatName(String boatName) {
        if (boatName.isEmpty())
            return false;
        this.boatName = boatName;
        return true;

    }

    public boolean setPrivateProperty(boolean privateProperty) {
        this.privateProperty = privateProperty;
        return true;

    }

    public boolean setOutOfOrder(boolean outOfOrder) {
        this.outOfOrder = outOfOrder;
        return true;

    }

    public boolean setBoatType(BoatType type) {
        this.type = type;
        return true;

    }

    public boolean setBoatScheduling(Map<LocalDate, List<Activity>> boatScheduling) {
        this.boatScheduling = boatScheduling;
        return true;

    }

    public boolean setSerialNumberCount(int staticCount){
        this.serialNumberCount = staticCount;
        return true;
    }
    ////////////////Boat compare, hashcode and equals////////////

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BoatType)) return false;

        Boat boat = (Boat) obj;
        if (this.serialNum == boat.serialNum)// לבדוק אם זו אכן ההשוואה הרצויה
            return true;
        else
            return false;
    }


    public boolean checkBoatAvailability(LocalDate date, Activity timeWindow) {
        List<Activity> activitiesForDate = boatScheduling.get(date);
        if (this.outOfOrder)
            return false;
        if (activitiesForDate == null) // meaning nothing was schedualled for that date, so boat is obviously available
            return true;
        else {
            if (activitiesForDate.contains(timeWindow)) // if activity is already in the activities this boat is linked to on that day/
                return false;
            else {
                for (Activity boatActivityOverlapping : activitiesForDate) {
                    // checks if timeWindow is overlapping with any other bms.engine.ActivitiesManagement.activity this boat is already reserved to
                    if (boatActivityOverlapping.getOverlappingActivities().contains(timeWindow))
                        return false; // meaning this bms.engine.ActivitiesManagement.activity is overlapping with another bms.engine.ActivitiesManagement.activity of this boat
                }
            }
            // bms.engine.ActivitiesManagement.activity is not on the activities for that day, therefore boat is available on this time window
            return true;

        }
    }

    public boolean bindBoatToActivity(LocalDate date, Activity timeWindow) {
        List<Activity> activitiesForDate = boatScheduling.get(date);
        if (activitiesForDate == null) { // if there is no activity on that date, this date needs to be put in the map
            activitiesForDate = new ArrayList<>();
            this.boatScheduling.put(date, activitiesForDate);
        }
        activitiesForDate.add(timeWindow); // add the activity to this date's activities
        return true;
    }

    public boolean releaseActivityFromBoat(LocalDate date, Activity timeWindow) {
        this.boatScheduling.get(date).remove(timeWindow);
        return true;
    }

    public void cleanBoatHistory () {
        LocalDate Today = LocalDate.now();
        Map<LocalDate, List<Activity>> mapActivities = this.boatScheduling;
        for (Map.Entry pair : mapActivities.entrySet()) {
            LocalDate date = (LocalDate) pair.getKey();
            if (date.isBefore(Today)) // if date on map has passed
                mapActivities.remove(date); // then remove it
        }
    }
}