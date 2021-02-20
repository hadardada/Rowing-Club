package servlets.member;

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
