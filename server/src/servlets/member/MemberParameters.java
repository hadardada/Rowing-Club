package servlets.member;

import bms.engine.membersManagement.member.Member;

public class MemberParameters {
    String name;
    String notes;
    String email;
    String password;
    int age;
    String phoneNumber;
    boolean havePrivateBoat;
    int privateBoatSerialNumber;
    int rowingLevel;
    boolean isManager;
    String signUpDate;
    String expirationDate;


    //C'tors
    public MemberParameters(){}

    public MemberParameters(Member member){
        this.name = member.getName();
        this.notes = member.getNotes();
        this.email = member.getEmailAddress();
        this.password = member.getPassword();
        this.age = member.getAge();
        this.phoneNumber = member.getPhoneNumber();
        this.havePrivateBoat = member.getPrivateBoatStatus();
        this.privateBoatSerialNumber = member.getSerialNumber();
        this.rowingLevel = member.getLevel();
        this.isManager = member.getIsManager();
        this.signUpDate = member.getSignUpDate().toString();
        this.expirationDate = member.getExpirationDate().toString();
    }
    public MemberParameters(String name, String notes, String email, String password,
                            int age, String phoneNumber, boolean havePrivateBoat, int privateBoatSerialNumber,
                            int rowingLevel, boolean isManager, String signUpDate,
                            String expirationDate)
    {
        this.name = name;
        this.notes = notes;
        this.email = email;
        this.password = password;
        this.age = age;
        this.phoneNumber = phoneNumber;
        this.havePrivateBoat = havePrivateBoat;
        this.privateBoatSerialNumber = privateBoatSerialNumber;
        this.rowingLevel = rowingLevel;
        this.isManager = isManager;
        this.signUpDate = signUpDate;
        this.expirationDate = expirationDate;
    }
}
