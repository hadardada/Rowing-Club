package bms.engine.xmlStateExport;

import bms.engine.Engine;
import boat.Boat;
import member.Member;
import activity.Activity;
import reservation.Reservation;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class XmlStateExport {
    Engine stateEngine;
    static String path;


    public XmlStateExport(Engine stateEngine) {
        this.stateEngine = stateEngine;
    }

    public String getPath (){return path;}

    public void saveStateToXml() {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();

            // root element: engine
            Element rootElement = doc.createElement("engine");
            doc.appendChild(rootElement);

            //Activities List
            Element activities = doc.createElement("activities");
            rootElement.appendChild(activities);
            exportActivities(activities, doc);

            //Boats map
            Element boats = doc.createElement("boats");
            rootElement.appendChild(boats);
            exportBoats(boats, doc);

            //members map
            Element members = doc.createElement("members");
            rootElement.appendChild(members);
            exportMembers(members, doc);

           ////Reservation Lists
            //closed
            Element closedReservations = doc.createElement("closedReservations");
            rootElement.appendChild(closedReservations);
            exportReservations(closedReservations, doc, stateEngine.getClosedReservation());
            //open
            Element openReservations = doc.createElement("openReservations");
            rootElement.appendChild(openReservations);
            exportReservations(openReservations, doc, stateEngine.getOpenReservation());

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(doc);
            StreamResult streamResult = new StreamResult(new File("state.xml"));
            transformer.transform(domSource, streamResult);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } catch (TransformerException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    private void exportMembers(Element membersNode, Document doc) {

        for (Member member : this.stateEngine.getAllMembers()) {
            String memberName = member.getName();
            String serialNumber = String.valueOf(member.getSerialNumber());
            String age = String.valueOf(member.getAge());
            String notes = member.getNotes();
            String level = String.valueOf(member.getLevel());
            String hasPrivateBoat = String.valueOf(member.getPrivateBoatStatus());
            String privateBoatSerialNumber;
            if (member.getPrivateBoatStatus())
                privateBoatSerialNumber = String.valueOf(member.getPrivateBoatSerialNumber());
            else
                privateBoatSerialNumber = "";
            String phoneNumber = member.getPhoneNumber();
            String emailAddress = member.getEmailAddress();
            String password = member.getPassword();
            String isManager = String.valueOf(member.getIsManager());
            String signUpDate = member.getSignUpDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
            String expirationDate = member.getExpirationDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
            membersNode.appendChild(createMember(doc, serialNumber, memberName, age, level, notes, hasPrivateBoat, privateBoatSerialNumber,
                    phoneNumber, emailAddress, password, isManager, signUpDate, expirationDate));
        }
    }

    private static Node createMember(Document doc, String serialNum, String name, String age, String level, String notes,
                                     String hasPrivateBoat, String privateBoatNumber, String phoneNumber, String email, String password, String isManager,
                                     String signUpDate, String expiryDate) {
        Element member = doc.createElement("Member");
        member.setAttribute("serialNumber", serialNum);
        member.setAttribute("name", name);
        member.setAttribute("age", age);
        member.setAttribute("notes", notes);
        member.setAttribute("level", level);
        member.setAttribute("hasPrivateBoat", hasPrivateBoat);
        member.setAttribute("privateBoatNumber", privateBoatNumber);
        member.setAttribute("phoneNumber", phoneNumber);
        member.setAttribute("emailAddress", email);
        member.setAttribute("password", password);
        member.setAttribute("isManager", isManager);
        member.setAttribute("joined", signUpDate);
        member.setAttribute("expire", expiryDate);
        return member;
    }

    private void exportBoats(Element boatNode, Document doc) {

        for (Boat boat : this.stateEngine.getBoats()) {
            String serialNumber = String.valueOf(boat.getSerialNum());
            String boatName = boat.getBoatName();
            String isPrivate = String.valueOf(boat.isPrivateProperty());
            String outOfOrder = String.valueOf(boat.isOutOfOrder());
            String rowersNum = boat.getBoatType().getBoatSize().name();
            String singleOar = String.valueOf(boat.getBoatType().isSingleOar());
            String wide = String.valueOf(boat.getBoatType().isWide());
            String coxwain = String.valueOf(boat.getBoatType().hasHelmsman());
            String coastal = String.valueOf(boat.getBoatType().isCoastal());

            boatNode.appendChild(createBoat(doc, serialNumber, boatName, isPrivate, outOfOrder, rowersNum,
                    singleOar, wide, coxwain, coastal));
        }
    }

    private static Node createBoat(Document doc, String serialNum, String boatName, String isPrivate, String outOfOrder, String rowersNum,
                                   String singleOar, String wide, String coxswain, String coastal) {
        Element boat = doc.createElement("Boat");
        boat.setAttribute("serialNumber", serialNum);
        boat.setAttribute("name", boatName);
        boat.setAttribute("isPrivate", isPrivate);
        boat.setAttribute("outOfOrder", outOfOrder);

        boat.appendChild(createBoatType(doc, rowersNum, singleOar, wide, coxswain, coastal));
        return boat;
    }

    private static Node createBoatType(Document doc, String rowersNum, String singleOar, String wide, String coxswain, String coastal) {
        Element boatType = doc.createElement("BoatType");
        boatType.setAttribute("rowersNum", rowersNum);
        boatType.setAttribute("singleOar", singleOar);
        boatType.setAttribute("wide", wide);
        boatType.setAttribute("coxswain", coxswain);
        boatType.setAttribute("coastal", coastal);
        return boatType;
    }

    private void exportActivities(Element activityNode, Document doc) {

        for (Activity activity : this.stateEngine.getActivities()) {
            String name = activity.getName();
            String starts = activity.getStarts().format(DateTimeFormatter.ISO_LOCAL_TIME);
            String ends = activity.getEnds().format(DateTimeFormatter.ISO_LOCAL_TIME);
            String id = String.valueOf(activity.getId());
            String rowersNum;
            String singleOar;
            String wide;
            String coxswain;
            String coastal;
            if (activity.getSpecifiedType() != null) {
                rowersNum = activity.getSpecifiedType().getBoatSize().name();
                singleOar = String.valueOf(activity.getSpecifiedType().isSingleOar());
                wide = String.valueOf(activity.getSpecifiedType().isWide());
                coxswain = String.valueOf(activity.getSpecifiedType().hasHelmsman());
                coastal = String.valueOf(activity.getSpecifiedType().isCoastal());
            } else {
                rowersNum = "";
                singleOar = "";
                wide = "";
                coxswain = "";
                coastal = "";
            }
            activityNode.appendChild(createActivity(doc, name, starts, ends,id,  rowersNum, singleOar, wide, coxswain, coastal));
        }
    }

    private static Node createActivity(Document doc, String name, String starts, String ends, String id,String rowersNum,
                                       String singleOar, String wide, String coxswain, String coastal) {
        Element activity = doc.createElement("Activity");
        activity.setAttribute("activityName", name);
        activity.setAttribute("starts", starts);
        activity.setAttribute("ends", ends);
        activity.setAttribute("id", id);
        activity.appendChild(createBoatType(doc, rowersNum, singleOar, wide, coxswain, coastal));
        return activity;
    }


   private  void exportReservations (Element reservationNode, Document doc, List<Reservation> reservationList) {

       for (Reservation reservation : reservationList) {
           Element res = doc.createElement("Reservation");
           String participantRower = reservation.getParticipantRower().getEmailAddress();
           String trainingDate = reservation.getTrainingDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
           String activityId = String.valueOf(reservation.getActivity().getId());
           String isApproved = String.valueOf(reservation.getIsApproved());
           String reservationCreator = reservation.getReservationMember().getEmailAddress();
           String creationDate = reservation.getReservationDateTime().format((DateTimeFormatter.ISO_LOCAL_DATE_TIME));
           //requested Boat Types
           List<Node> boatTypes = new ArrayList<>();
           for (Boat.BoatType type : reservation.getBoatTypes()) {
               String rowersNum = type.getBoatSize().name();
               String singleOar = String.valueOf(type.isSingleOar());
               String wide = String.valueOf(type.isWide());
               String coxswain = String.valueOf(type.hasHelmsman());
               String coastal = String.valueOf(type.isCoastal());
               boatTypes.add(createBoatType(doc, rowersNum, singleOar, wide, coxswain, coastal));
           }

           //additional wanted rowers
           List<Node> additionalRowers = new ArrayList<>();
           for (Member member : reservation.getWantedRowers()) {
               String email = member.getEmailAddress();
               additionalRowers.add(createMembersList(doc, email));

           }

           List<Node> approvedRowers = new ArrayList<>();
           String reservationBoat;
           // checks if reservation is approved and if it is then add boat number and actual rowers
           if (Boolean.TRUE.equals(reservation.getIsApproved())) {
               reservationBoat = String.valueOf(reservation.getReservationBoat().getSerialNum());

               for (Member member : reservation.getActualRowers()) {
                   String email = member.getEmailAddress();
                   approvedRowers.add(createMembersList(doc, email));
               }
           }
           else{ // reservation is not approved so no boat and no actual (approved) rowers
               reservationBoat = "";
           }
               reservationNode.appendChild(createReservation(doc, participantRower, trainingDate, activityId, reservationBoat,
                       isApproved,reservationCreator,creationDate, boatTypes, approvedRowers, additionalRowers));
           }
       }

    private static Node createReservation(Document doc, String participantRower, String trainingDate, String activityId,
                                          String reservationBoat, String isApproved ,String reservationMember,String creationDate, List <Node> boatTypes, List<Node> approvedRowers, List<Node> additionalRowersList)
    {
        Element reservation = doc.createElement("Reservation");
        reservation.setAttribute("participantRower", participantRower);
        reservation.setAttribute("trainingDate", trainingDate);
        reservation.setAttribute("reservationBoat", reservationBoat);
        reservation.setAttribute("activityId", activityId);
        reservation.setAttribute("reservationMember", reservationMember);
        reservation.setAttribute("createdOn", creationDate);
        reservation.setAttribute("isApproved", isApproved);
        Element boatTypesList = doc.createElement("boatTypesList");
        for (Node boatTypeNode: boatTypes)
            boatTypesList.appendChild(boatTypeNode);
        Element addRowers = doc.createElement("additionalRowersList");
        for (Node rower: additionalRowersList)
            addRowers.appendChild(rower);
        Element actualRowers = doc.createElement("approvedRowersList");
        for (Node rower: approvedRowers)
            actualRowers.appendChild(rower);

        reservation.appendChild(addRowers);
        reservation.appendChild(boatTypesList);
        reservation.appendChild(actualRowers);
        return reservation;
    }

    private static Node createMembersList(Document doc, String email) {
        Element resMemberEmail = doc.createElement("resMember");
        resMemberEmail.setAttribute("emailAddress", email);
        return resMemberEmail;
    }


}








