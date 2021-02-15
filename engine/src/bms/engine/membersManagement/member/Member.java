package bms.engine.membersManagement.member;

import bms.engine.membersManagement.member.memberListsExceptions.*;
import bms.engine.reservationsManagment.reservation.Reservation;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;

public class Member implements Serializable {

    static int serialNumberCount;

    //class data members

    private int serialNumber;
    private String name;
    private int age;
    private String notes;
    private int level;
    private boolean havePrivateBoat;
    private int privateBoatSerialNumber;
    private String phoneNumber;
    private String emailAddress;
    private String password;
    private boolean isManager;
    private LocalDate signUpDate;
    private LocalDate expirationDate;
    private ArrayList<Reservation> myReservations;


    ///////////////// getters ////////////////


    public int getSerialNumberCount () {return serialNumberCount;}
    public int getSerialNumber () {return serialNumber;}
    public int getAge () {return age;}
    public int getPrivateBoatSerialNumber () {return privateBoatSerialNumber;}
    public String getPhoneNumber () {return phoneNumber;}
    public int getLevel () {return level;}

    public boolean getIsManager () {return isManager;}
    public boolean getPrivateBoatStatus () {return havePrivateBoat;}

    public LocalDate getSignUpDate () {return signUpDate;}
    public LocalDate getExpirationDate () {return expirationDate;}

    public String getName () {return name;}
    public String getNotes () {return notes;}
    public String getPassword () {return password;}
    public String getEmailAddress () {return emailAddress;}

    public ArrayList<Reservation> getMyReservations () {return myReservations;}



    ///////////////// setters ////////////////


    //this function set the member age
    public boolean setAge (int age) {
        this.age = age;
        return true;
    }

    public boolean setPhoneNumber (String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return true;

    }

    public boolean setPrivateBoatSerialNumber (int privateBoatSerialNumber) {
        this.privateBoatSerialNumber = privateBoatSerialNumber;
        return true;
    }

    public boolean setLevel (int level) {
        this.level = level;
        return true;
    }


    public boolean setIsManager (boolean isManager) {
        this.isManager = isManager;
        return true;
    }

    public boolean setPrivateBoatStatus (boolean havePrivateBoat) {
        this.havePrivateBoat = havePrivateBoat;
        return true;
    }

    public boolean setSignUpDate (LocalDate signUpDate) {
        this.signUpDate = signUpDate;
        return true;
    }


    public boolean setExpirationDate (LocalDate expirationDate) throws ExpiryDateIsBeforeSignUpException {
        if (this.signUpDate.isAfter(expirationDate))
            throw new ExpiryDateIsBeforeSignUpException(this.emailAddress);
        this.expirationDate = expirationDate;
        return true;
    }


    public boolean setName (String name) {
        if (name.isEmpty())
            return false;
        this.name = name;
        return true;
    }



    public boolean setNotes (String notes) {
        this.notes = notes;
        return true;
    }


    public boolean setPassword (String password) {
        this.password = password;
        return true;
    }


    public boolean setEmailAddress (String emailAddress) {
        this.emailAddress = emailAddress;
        return true;
    }



    ///////////////// constructors ////////////////

    public Member (String name,  int age,  String notes,  int level,
                      boolean havePrivateBoat,  int privateBoatSerialNumber,  String phoneNumber,  String emailAddress,
                      String password,  boolean isManager,  LocalDate signUpDate,
                   LocalDate expirationDate){

        this.serialNumber = ++serialNumberCount;
        this.name = name;
        this.age = age;
        this.notes = notes;
        this.level = level;
        this.havePrivateBoat = havePrivateBoat;
        this.privateBoatSerialNumber = privateBoatSerialNumber;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
        this.password = password;
        this.isManager = isManager;
        this.signUpDate = signUpDate;
        this.expirationDate = expirationDate;
        this.myReservations = new ArrayList<>();
    }
}
