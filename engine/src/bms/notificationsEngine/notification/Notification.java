package bms.notificationsEngine.notification;

import java.util.HashMap;
import java.util.Map;

public class Notification {
    static int serialNumberCount;

    private int serialNum;
    private String content;

    //notifications consts
    public static final int EDIT = 1;
    public static final int DELETED = 2;
    public static final int APPROVED = 3;
    public static final int REJECTED = 4;
    public static final int REOEPNED = 4;


    static Map<Integer, String> notiesDictionary;
    static{
        notiesDictionary = new HashMap<>();
        notiesDictionary.put(EDIT, "edited!");
        notiesDictionary.put(DELETED, "deleted!");
        notiesDictionary.put(APPROVED, "approved!");
        notiesDictionary.put(REJECTED, "rejected!");
        notiesDictionary.put(REOEPNED, "reopened!");
    }

    public int getSerialNum() { return serialNum; }

    public String getContent() { return content; }

    public void setSerialNum(int serialNum) { this.serialNum = serialNum; }

    public void setContent(String content) { this.content = content; }

    public Notification(int msgType, String resDate, String from, String to){

        this.serialNum = ++serialNumberCount;
        this.content ="Your Reservation on: "+resDate+" from: " +from+
                " to: "+ to+ " has been "+ notiesDictionary.get(msgType);
    }

    public Notification(String msgContent){

        this.serialNum = ++serialNumberCount;
        this.content = msgContent;
    }


}
