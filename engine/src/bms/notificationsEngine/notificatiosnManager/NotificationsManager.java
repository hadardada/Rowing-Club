package bms.notificationsEngine.notificatiosnManager;

import bms.engine.membersManagement.member.Member;
import bms.engine.reservationsManagment.reservation.Reservation;
import bms.notificationsEngine.notification.Notification;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class NotificationsManager {
    private Map<Integer,Notification> allNoties; // a map of notification serial number and its content
    private Map<String,UserNotification> usersNoties; //a map of email address
    private Set<Integer> allManualNoties;




    public static class UserNotification{
        int newNotiesCounter;
        int totalNotiesCounter;
        Set<Integer> myNewNoties;
        Set<Integer> myManualNoties;

        ////////UserNotification Constractor/////////////
        public UserNotification(Set<Integer> currManualNoties){
            this.newNotiesCounter =currManualNoties.size() ;
            this.totalNotiesCounter = currManualNoties.size();
            this.myManualNoties = currManualNoties;
            this.myNewNoties = new HashSet<>();
        }

        public UserNotification() {
            this.newNotiesCounter =0;
            this.totalNotiesCounter = 0;
            this.myManualNoties = null;
            this.myNewNoties = new HashSet<>();
        }


            ////////UserNotification Methods/////////////

    }

//////////////NotificationsManager constructor///////////////////////////
    public NotificationsManager (Set <String> members){
        this.allNoties = new HashMap<>();
        this.usersNoties = new HashMap<>();
        for (String memberEmail: members){
            this.usersNoties.put(memberEmail, new UserNotification());
        }
        this.allManualNoties =new HashSet<>();
    }


//////////////NotificationsManager methods///////////////////////////

    public void addNewManualNotification(String msg){
        Notification newManualNoti = new Notification(msg);
        allNoties.put(newManualNoti.getSerialNum(), newManualNoti);
        allManualNoties.add(newManualNoti.getSerialNum());
        //goes over all email address of members in the club and increase their newNotiesCounter
        for (UserNotification user: usersNoties.values()){
            user.myNewNoties.add(newManualNoti.getSerialNum());
            user.totalNotiesCounter++;
            user.newNotiesCounter++;
        }
    }

    public void addNewAutoNotification(int msgType, Reservation res){
        List <Member> reservationMembers = res.getWantedRowers();
        String resDateStr = res.getTrainingDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
        String fromStr = res.getActivity().getStarts().format(DateTimeFormatter.ISO_LOCAL_TIME);
        String toStr = res.getActivity().getEnds().format(DateTimeFormatter.ISO_LOCAL_TIME);
        Notification newAutoNoti = new Notification(msgType, resDateStr, fromStr, toStr);
        allManualNoties.add(newAutoNoti.getSerialNum());
        for (Member resMember: reservationMembers){
            UserNotification user = usersNoties.get(resMember.getEmailAddress());
            user.myNewNoties.add(newAutoNoti.getSerialNum());
            user.totalNotiesCounter++;
            user.newNotiesCounter++;
        }
    }

    public List<String> getAllNewNotiesForUser(String email){
        List <String> newNoties = new ArrayList<>();
        UserNotification user = usersNoties.get(email);
        for (Integer msgNum: user.myNewNoties){
            newNoties.add(allNoties.get(msgNum).getContent());
            //now deletes autoMsg for user (because this function is being calld when member is viewing his msg)
        }
        user.myNewNoties.clear();
        user.newNotiesCounter = 0;
        return newNoties;
    }

    public List<Notification> getAllManualNotiesForUser(){
        List<Notification> manualNoties = new ArrayList<>();
        for (Integer msgNum: allManualNoties){
            manualNoties.add(allNoties.get(msgNum));
        }
        return manualNoties;
    }

    public int getNumberOfNewNoties(String email){
        UserNotification user = usersNoties.get(email);
        return user.newNotiesCounter;

    }

    public void deleteNotificationBySerNum (int serNum){
        allManualNoties.remove(serNum);
    }

}
