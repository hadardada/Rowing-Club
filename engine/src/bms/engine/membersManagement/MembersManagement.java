package bms.engine.membersManagement;

import bms.engine.membersManagement.member.Member;
import bms.engine.membersManagement.member.memberListsExceptions.*;
import bms.engine.reservationsManagment.reservation.Reservation;
import bms.engine.boatsManagement.boat.boatsListsExceptions.*;

import bms.engine.boatsManagement.BoatsManagement;
import bms.engine.reservationsManagment.ReservationsManagement;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

public class MembersManagement implements Serializable {

    private Map<String, Member> members;
    private Set<Member> loginMembers;
    private BoatsManagement boats;
    private ReservationsManagement reservations;

    public MembersManagement(BoatsManagement boats){
        this.members =  new HashMap<>();
        this.loginMembers = new HashSet<>();
        this.boats = boats;
    }

    public boolean addLoginMember(Member member){
        Member memberOnlist = getMemberByEmail(member.getEmailAddress());
        if (this.loginMembers.contains(memberOnlist)) {
            return false;
        }
        else {
            this.loginMembers.add(memberOnlist);
            return true;
        }
    }


    public boolean setBaotsManagement(BoatsManagement boatsManagement){
        this.boats = boatsManagement;
        return true;
    }

    public Map<String, Member> getMembers(){
        return this.members;
    }

    //this function set the member age
    public int updateMemeberAge(int age, Member member) {

        member.setAge(age);
        return member.getAge();
    }

    //this function set the member phone number
    public String updateMemeberPhoneNumber(String phoneNumber, Member member) {
        member.setPhoneNumber(phoneNumber);
        return member.getPhoneNumber();
    }


    //this method update the private boat number, return false if this number doesn't exist in the boat list
    //(or represent no private boat), true if it does
    // private boat for none boat is -1

    public boolean updateMemberPrivateBoatNumber(int privateBoatSerialNumber, Member member) {
        if (!member.getPrivateBoatStatus() && privateBoatSerialNumber == -1) {
            return true;
        } else if (member.getPrivateBoatStatus() && privateBoatSerialNumber == -1) {
            return false;
        } else if (this.boats.getBoats().containsKey(privateBoatSerialNumber)) {
            member.setPrivateBoatSerialNumber(privateBoatSerialNumber);
            this.boats.getBoats().get(privateBoatSerialNumber).setPrivateProperty(true);
            return true;
        }
        return false;
    }

    public int updateMemeberLevel(int level, Member member) {
        member.setLevel(level);
        return member.getLevel();
    }

    public void updateMemberIsManager(boolean isManager , Member member) {
        member.setIsManager(isManager);
    }

    public boolean updateMemberPrivateBoatStatus(boolean havePrivateBoat, Member member) {
        member.setPrivateBoatStatus(havePrivateBoat);
        return member.getPrivateBoatStatus();
    }

    public LocalDate updateMemberSignUpDate(LocalDate signUpDate, Member member) {
        member.setSignUpDate(signUpDate);
        return member.getSignUpDate();
    }

    public LocalDate updateMemberExpirationDate(LocalDate expirationDate, Member member) throws ExpiryDateIsBeforeSignUpException {
        member.setExpirationDate(expirationDate);
        return member.getExpirationDate();
    }

    public boolean updateMemberName(String name, Member member) {
        if (name.isEmpty())
            return false;
        member.setName(name);
        return true;
    }

    public String updateMemberNotes(String notes, Member member) {
        member.setNotes(notes);
        return member.getNotes();
    }

    public boolean updateMemberPassword(String password, Member member) {
        if (password.isEmpty())
            return false;
        member.setPassword(password);
        return true;
    }


    public String updateEmailAddress(String emailAddress, Member member) throws EmailAddressAlreadyExistsException {
        String oldAddress = member.getEmailAddress();
        if (this.members.containsKey(emailAddress))
            throw new EmailAddressAlreadyExistsException(emailAddress);
        member.setEmailAddress(emailAddress);
        this.members.remove(oldAddress);
        this.members.put(emailAddress, member);
        return member.getEmailAddress();
    }


    //this function creates a new member in the club and add it to the members map
    public boolean addNewMember(String name, String notes, String email, String password,
                                int age, String phoneNumber, boolean havePrivateBoat, int privateBoatSerialNumber,
                                int rowingLevel, boolean isManager, LocalDate signUpDate, LocalDate expirationDate) throws EmailAddressAlreadyExistsException, BoatDoesNotExistException, ExpiryDateIsBeforeSignUpException {
        boolean noBoatExceptionFlag = false;
        email = email.toLowerCase();

        if (this.members.containsKey(email)) {
            if (this.members.size() == 1) //admin
                return false;
                throw new EmailAddressAlreadyExistsException(email);
        }

        if (signUpDate.isAfter(expirationDate))
            throw new ExpiryDateIsBeforeSignUpException(email);
        if (havePrivateBoat) {
            if (!this.boats.getBoats().containsKey(privateBoatSerialNumber)) { // change given values if given boat is not found

                havePrivateBoat = false;
                privateBoatSerialNumber = -1;
                noBoatExceptionFlag = true;
            }
            else
            {
                this.boats.getBoats().get(privateBoatSerialNumber).setPrivateProperty(true);
            }
        }
        Member newMember = new Member(name, age, notes, rowingLevel, havePrivateBoat,
                privateBoatSerialNumber, phoneNumber, email, password, isManager, signUpDate, expirationDate);
        members.put(email, newMember);
        if (noBoatExceptionFlag)
            throw new BoatDoesNotExistException();// letting user know it was added but with some changes

        return true;
    }
    public List<Member> getAllMembers() {
        List<Member> membersList = new ArrayList<>(this.members.values());
        return Collections.unmodifiableList(membersList);
    }

    public List<Member> getMembersMaxRowing(int listSize, Member member) {
        Map <Member,Integer> rowingMap = new HashMap<>();
        for (Reservation reservation: member.getMyReservations()){
            if (reservation.getIsApproved()== null){
                continue;
            }
            else if (reservation.getIsApproved()){
                for (Member member1: reservation.getActualRowers()){
                    if (member1 != member){
                        if (rowingMap.containsKey(member1)){
                            int integer = rowingMap.get(member1);
                            rowingMap.put(member1,integer++);
                        }
                        else {
                            rowingMap.put(member1,0);
                        }
                    }
                }
            }
                    rowingMap.remove(member);
        }
        if (rowingMap.size()==0){
            Set<String> memberSet = members.keySet();
            int i = 0;
            List <Member> max = new ArrayList<>();
            for (String email : memberSet) {
                if (email.equals(member.getEmailAddress())) {
                    continue;
                } else if (i == 4) {
                    break;
                } else {
                    max.add(members.get(email));
                    i++;
                }
            }
            return max;

        }
        List<Member> maxMembers = getMembersMax(rowingMap,listSize);
        if(maxMembers.size() < listSize){
            Set<String> memberSet = members.keySet();
            int i = 0;
            for (String email : memberSet) {
                for (Member member1: maxMembers) {
                    if (!email.equals(member1.getEmailAddress())) {
                        continue;
                    }
                    else{ break;}
                }
                if (i == listSize - maxMembers.size()) { break;}
                maxMembers.add(members.get(email));
                i++;
            }
        }
        return maxMembers;
    }

    public List<Member> getMembersMax(Map <Member,Integer> rowingMap,int listSize){
        List<Member> max = new ArrayList<>();
        if (rowingMap.size() < listSize){
            listSize = rowingMap.size();
        }
        for (int i = 0 ; i < listSize ; i++){
                Member member = Collections.max(rowingMap.entrySet(), Map.Entry.comparingByValue()).getKey();
                max.add(member);
                rowingMap.remove(member);
        }

        return max;

    }

    public Member getMemberByEmail(String email){
        return members.get(email.toLowerCase());
    }

    public boolean isMemberOnList (String email){
        return members.containsKey(email.toLowerCase());
    }

    public boolean checkMemberPassword(String email, String password)
    {
        return this.members.get(email.toLowerCase()).getPassword().equalsIgnoreCase(password);
    }

    public boolean removeLoginMember (Member member){
        if (loginMembers.contains(member)){
            loginMembers.remove(member);
            return true;
        }
        else {
            return false;
        }
    }
}
