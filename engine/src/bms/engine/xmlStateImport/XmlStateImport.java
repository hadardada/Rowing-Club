package bms.engine.xmlStateImport;

import bms.engine.Engine;

import bms.engine.boatsManagement.boat.Boat;
import bms.engine.boatsManagement.boat.boatsListsExceptions.*;

import bms.engine.membersManagement.member.Member;
import bms.engine.membersManagement.member.memberListsExceptions.*;

import bms.engine.activitiesManagement.activity.Activity;
import bms.engine.activitiesManagement.activity.ActivityExceptions.*;

import bms.engine.reservationsManagment.reservation.Reservation;
import bms.engine.reservationsManagment.reservation.reservationsExceptions.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class XmlStateImport {
    Engine stateEngine;
    String path;

    public XmlStateImport(Engine stateEngine) {
        this.stateEngine = stateEngine;
    }

    public void loadState (){
        try {

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringElementContentWhitespace(true);

            DocumentBuilder builder = factory.newDocumentBuilder();
            File file = new File("C:\\temp\\state.xml");
            if (file.length()==0)
                return;
            InputStream xmlFileInputStream = new FileInputStream(file);
            Document doc = builder.parse(xmlFileInputStream);

            doc.getDocumentElement().normalize();
            Element root = doc.getDocumentElement();

            //Activities
            Node activitiesNode =root.getFirstChild();
            activityImport (activitiesNode);

            //Boats
            Node boatsNode = activitiesNode.getNextSibling();
            boatsImport(boatsNode);

            //Members
            Node memberNode = boatsNode.getNextSibling();
            membersImport(memberNode);

            //Reservations:
            //closed:
            boolean closed = true;
            Node closedReservationNode = memberNode.getNextSibling();
            reservationsImport(closedReservationNode, closed);
            //open:
            closed = false;
            Node opendReservationNode = closedReservationNode.getNextSibling();
            reservationsImport(opendReservationNode, false);

        }
        catch (ParserConfigurationException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } catch (SAXException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

    }

    private void activityImport (Node activities){
        int activitiesNum = activities.getChildNodes().getLength();
        Node activity = activities.getFirstChild();
        for (int i = 0; i< activitiesNum; i++)
        {
            NamedNodeMap activityAttributes = activity.getAttributes();
            String name = activityAttributes.getNamedItem("activityName").getNodeValue();
            LocalTime starts = LocalTime.parse(activityAttributes.getNamedItem("starts").getNodeValue(), DateTimeFormatter.ISO_LOCAL_TIME);
            LocalTime ends = LocalTime.parse(activityAttributes.getNamedItem("ends").getNodeValue(), DateTimeFormatter.ISO_LOCAL_TIME);
            int id = Integer.parseInt(activityAttributes.getNamedItem("id").getNodeValue());
            Boat.BoatType requestedType;
            Node boatType = activity.getLastChild();
            NamedNodeMap boatTypesAttributes = boatType.getAttributes();
            if (boatTypesAttributes.item(0).getNodeValue().isEmpty()) // boat type is null
                requestedType = null;
            else{
                Boat.BoatType.BoatSize size = Boat.BoatType.BoatSize.valueOf(boatTypesAttributes.getNamedItem("rowersNum").getNodeValue());
                boolean wide = Boolean.parseBoolean(boatTypesAttributes.getNamedItem("wide").getNodeValue());
                boolean singleOar = Boolean.parseBoolean(boatTypesAttributes.getNamedItem("singleOar").getNodeValue());
                boolean coxswain = Boolean.parseBoolean(boatTypesAttributes.getNamedItem("coxswain").getNodeValue());
                boolean coastal = Boolean.parseBoolean(boatTypesAttributes.getNamedItem("coastal").getNodeValue());
                requestedType = new Boat.BoatType(size, singleOar, wide, coxswain, coastal);}
            try{stateEngine.addNewActivity(starts, ends, name, requestedType, true);}
            catch (EndTimeIsLowerException e){
                System.out.println(e.getMessage());
            }
            activity = activity.getNextSibling();
        }
    }

    private void boatsImport (Node boats) {
        int boatsNum = boats.getChildNodes().getLength();
        Node boat = boats.getFirstChild();
        for (int i = 0; i < boatsNum; i++) {
            NamedNodeMap boatAttributes = boat.getAttributes();
            int serialNum = Integer.parseInt(boatAttributes.getNamedItem("serialNumber").getNodeValue());
            String name = boatAttributes.getNamedItem("name").getNodeValue();
            boolean isPrivate = Boolean.parseBoolean(boatAttributes.getNamedItem("isPrivate").getNodeValue());
            boolean outOfOrder = Boolean.parseBoolean(boatAttributes.getNamedItem("outOfOrder").getNodeValue());

            Node boatType = boat.getFirstChild();
            NamedNodeMap boatTypesAttributes = boatType.getAttributes();
            Boat.BoatType.BoatSize size = Boat.BoatType.BoatSize.valueOf(boatTypesAttributes.getNamedItem("rowersNum").getNodeValue());
            boolean wide = Boolean.parseBoolean(boatTypesAttributes.getNamedItem("wide").getNodeValue());
            boolean singleOar = Boolean.parseBoolean(boatTypesAttributes.getNamedItem("singleOar").getNodeValue());
            boolean coxswain = Boolean.parseBoolean(boatTypesAttributes.getNamedItem("coxswain").getNodeValue());
            boolean coastal = Boolean.parseBoolean(boatTypesAttributes.getNamedItem("coastal").getNodeValue());
            try {
                stateEngine.addNewBoat(name, serialNum, isPrivate, outOfOrder, new Boat.BoatType(size, singleOar, wide, coxswain, coastal), true);
            } catch (BoatAlreadyExistsException | HelmsmanException | SingleWithTwoOarsException e) {
                System.out.println(e.getMessage());// just dont add this boat
            }
            boat = boat.getNextSibling();
        }
    }
     private void membersImport (Node members){
         int numOfMembers = members.getChildNodes().getLength();
         Node member = members.getFirstChild();
         for (int i = 0; i < numOfMembers; i++) {
             NamedNodeMap memberAttributes = member.getAttributes();
             int serialNumber = Integer.parseInt(memberAttributes.getNamedItem("serialNumber").getNodeValue());
             int age = Integer.parseInt(memberAttributes.getNamedItem("age").getNodeValue());
             String email = memberAttributes.getNamedItem("emailAddress").getNodeValue();
             String name = memberAttributes.getNamedItem("name").getNodeValue();
             boolean hasPrivateBoat;
             int privateNumber;
             if (memberAttributes.getNamedItem("hasPrivateBoat").getNodeValue().equals("true")){// has a private boat{
                hasPrivateBoat = Boolean.parseBoolean(memberAttributes.getNamedItem("hasPrivateBoat").getNodeValue());
                privateNumber = Integer.parseInt(memberAttributes.getNamedItem("privateBoatNumber").getNodeValue());
             }
             else{
                 hasPrivateBoat =false;
                 privateNumber = -1;
             }

             String phoneNumber = memberAttributes.getNamedItem("phoneNumber").getNodeValue();
             String password = memberAttributes.getNamedItem("password").getNodeValue();
             String notes = memberAttributes.getNamedItem("notes").getNodeValue();
             int level = Integer.parseInt(memberAttributes.getNamedItem("level").getNodeValue());
             LocalDate joined = LocalDate.parse(memberAttributes.getNamedItem("joined").getNodeValue(), DateTimeFormatter.ISO_LOCAL_DATE);
             LocalDate expire = LocalDate.parse(memberAttributes.getNamedItem("expire").getNodeValue(), DateTimeFormatter.ISO_LOCAL_DATE);
             boolean manager = Boolean.parseBoolean(memberAttributes.getNamedItem("isManager").getNodeValue());
             try {stateEngine.addNewMember(name, notes, email, password, age, phoneNumber, hasPrivateBoat, privateNumber, level,
                     manager, joined, expire, true);}
             catch(EmailAddressAlreadyExistsException| BoatDoesNotExistException| ExpiryDateIsBeforeSignUpException e)
             {

                 // just move on without adding this member
             }
             catch (Exception e)
             {


             }
             member = member.getNextSibling();

         }

        }

    private void reservationsImport (Node reservations, boolean closed) {

        int numOfRes = reservations.getChildNodes().getLength();
        Node reservation = reservations.getFirstChild();
        for (int i = 0; i < numOfRes; i++) {
            NamedNodeMap resAttributes = reservation.getAttributes();
            Member reservationOwner = stateEngine.getMembers().get(resAttributes.getNamedItem("participantRower").getNodeValue());
            LocalDate trainingDate =LocalDate.parse(resAttributes.getNamedItem("trainingDate").getNodeValue(), DateTimeFormatter.ISO_LOCAL_DATE);
            Member reservationCreator = stateEngine.getMembers().get(resAttributes.getNamedItem("reservationMember").getNodeValue());
            Activity resActivity = stateEngine.findActivityById(Integer.parseInt(resAttributes.getNamedItem("activityId").getNodeValue()));
            LocalDateTime creationDate = LocalDateTime.parse(resAttributes.getNamedItem("createdOn").getNodeValue(), DateTimeFormatter.ISO_LOCAL_DATE_TIME	);
            Boolean isAppoved = Boolean.parseBoolean(resAttributes.getNamedItem("isApproved").getNodeValue());
            //additional rowersList
            Node wantedAdditionalRowers = reservation.getFirstChild();
            int additionalRowersNum = wantedAdditionalRowers.getChildNodes().getLength();
            Node resMember = wantedAdditionalRowers.getFirstChild();
            List<Member> wantedAdditionalRowersList = new ArrayList<>();
            for (int j=0; j< additionalRowersNum;j++) // creates the list of additional members to this reservation
                //(by finding the existing members from members map - does not creates new members)
            {
                String emailKey;
                emailKey = resMember.getAttributes().getNamedItem("emailAddress").getNodeValue();
                wantedAdditionalRowersList.add(stateEngine.getMembers().get(emailKey));
                resMember=resMember.getNextSibling();
            }
            //requested Boat Types
            Node boatTypes = wantedAdditionalRowers.getNextSibling();
            int boatTypesNum = boatTypes.getChildNodes().getLength();
            Node resBoatType = boatTypes.getFirstChild();
            List<Boat.BoatType> requestedBoatTypes = new ArrayList<>();
            for (int j=0; j< boatTypesNum;j++)
            {
                //creates new boat types and put them on list
                NamedNodeMap boatTypesAttributes = resBoatType.getAttributes();
                Boat.BoatType.BoatSize size = Boat.BoatType.BoatSize.valueOf(boatTypesAttributes.getNamedItem("rowersNum").getNodeValue());
                boolean wide = Boolean.parseBoolean(boatTypesAttributes.getNamedItem("wide").getNodeValue());
                boolean singleOar = Boolean.parseBoolean(boatTypesAttributes.getNamedItem("singleOar").getNodeValue());
                boolean coxswain = Boolean.parseBoolean(boatTypesAttributes.getNamedItem("coxswain").getNodeValue());
                boolean coastal = Boolean.parseBoolean(boatTypesAttributes.getNamedItem("coastal").getNodeValue());
                requestedBoatTypes.add(new Boat.BoatType(size, singleOar, wide, coxswain, coastal));
                resBoatType = resBoatType.getNextSibling();
            }
            Boat assignedBoat;
            ArrayList<Member> approvedRowersList = new ArrayList<>();
            if (Boolean.TRUE.equals(isAppoved)){ // if the imported reservation is an approved one (taken from closedReservationList)
            //additional rowersList
                Node approvedRowers = boatTypes.getNextSibling();
                int approvedRowersNum = approvedRowers.getChildNodes().getLength();
                Node resApprovedRower = approvedRowers.getFirstChild();
                for (int j=0; j< approvedRowersNum;j++) // creates the list of approved (actual) members to this reservation
                //(by finding the existing members from members map - does not creates new members)
                {
                    String emailKey;
                    emailKey = resApprovedRower.getAttributes().getNamedItem("emailAddress").getNodeValue();
                    approvedRowersList.add(stateEngine.getMembers().get(emailKey));
                    resApprovedRower=resApprovedRower.getNextSibling();
                }
                //find assigned boat
                int assignedBoatNum = Integer.parseInt(resAttributes.getNamedItem("reservationBoat").getNodeValue());
                assignedBoat = stateEngine.getBoatsMap().get(assignedBoatNum);
            }
            else { //not an approved boat
                assignedBoat = null;
            }

            try {
                Reservation newRes;
                newRes = stateEngine.addNewReservation(reservationOwner, trainingDate, resActivity, requestedBoatTypes, wantedAdditionalRowersList, creationDate, reservationCreator, true);
                if (closed) {
                    if (Boolean.TRUE.equals(isAppoved)){
                        stateEngine.assignBoatToReservation(newRes, assignedBoat, true);
                        stateEngine.assignApprovedRowersToReservation(approvedRowersList, newRes, true);
                        stateEngine.updateReservationIsApproved(true, newRes, true);
                        stateEngine.removeReservationFromOpenReservation(newRes);
                        stateEngine.addReservationToClosedReservation(newRes);

                    }
                    else if (Boolean.FALSE.equals(isAppoved)) {
                        stateEngine.rejectReservation( newRes, true);
                    }

                }
            }
            catch (ParticipentRowerIsOnListException e)
            {
                System.out.println(e.getMessage());
                //move on without adding
            } catch (BoatSizeMismatchException e) {
                System.out.println(e.getMessage());
            } catch (ApprovedReservationWithNoBoatException e) {
                System.out.println(e.getMessage());
            }
            reservation = reservation.getNextSibling();
        }
    }
}
