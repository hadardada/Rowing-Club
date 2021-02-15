package bms.engine.XMLImportAndExport;

import engine.multipleExceptions.XmlMultipleExceptions;
import bms.engine.Engine;
import boat.Boat;
import boat.boatsListsExceptions.*;
import member.Member;
import activity.Activity;
import member.memberListsExceptions.*;
import activity.ActivityExceptions.*;

import jaxb.activities.generated.Activities;
import jaxb.boats.generated.BoatType;
import jaxb.boats.generated.Boats;
import jaxb.members.generated.Members;
import jaxb.members.generated.RowingLevel;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class XMLImportAndExport {
    Engine bmsEngine;

    public XMLImportAndExport(Engine engine) {
        this.bmsEngine = engine;
    }

    ///////////////////////////Unmarshal Boats///////////////////////////////

    //This method reads BoatList from given input with the given validation
    //if the unite flag is on, then both lists (the imported one and the existing one) will be united
    //otherwise, the existing one will be deleted, and the list will contain the imported only
    public boolean unmarshalBoatsFromXML(boolean uniteFlag, InputStream inputStream) throws XmlMultipleExceptions {
        XmlMultipleExceptions exceptionsList = new XmlMultipleExceptions();
        try {
            //InputStream inputStream = new FileInputStream(inputStream);
            Boats boats = deserializeBoatsFrom(inputStream);
            if (!uniteFlag)
                bmsEngine.removeAllBoats();
            Set<Integer> boatsSerialNums = new HashSet<>(); // set of serial numbers from imported boats
            for (jaxb.boats.generated.Boat boat : boats.getBoat()) {
                //creates instances of the imported boats
                Boat.BoatType.BoatSize rowersNum = boatTypeEnumToBoatSizeEnum(boat.getType().ordinal());
                Boolean singleOar = boatTypeEnumToOars(boat.getType().ordinal());
                Boolean isWide = boat.isWide();
                Boolean hasHelmsman = boatTypeEnumToHasHelmsman(boat.getType().ordinal());
                Boolean isCoastal = boat.isCostal();
                Boat.BoatType newBoatType = new Boat.BoatType(rowersNum, singleOar, isWide, hasHelmsman, isCoastal);
                String boatName = boat.getName();
                boolean isPrivate;
                if (Boolean.TRUE.equals(boat.isPrivate()))
                    isPrivate = true;
                else
                    isPrivate = false;
                boolean isOutOfOrder;
                if (Boolean.TRUE.equals(boat.isOutOfOrder()))
                    isOutOfOrder = true;
                else
                    isOutOfOrder = false;
                int serialNumber = Integer.parseInt(boat.getId());
                if (boatsSerialNums.contains(serialNumber)) // meaning a boat with the same serial number was already imported from this xml file
                    throw new BoatAlreadyExistsException(serialNumber);
                //Boat newBoat = new Boat(boatName, isPrivate, isOutOfOrder,newBoatType);
                // newBoat.setSerialNum(Integer.parseInt(boat.getId()));
                if (uniteFlag)
                    checkAndChangeBoatSerialNumber(serialNumber); // if new boats serial number is the same as one of the existing boats
                try {
                    bmsEngine.addNewBoat(boatName, serialNumber, isPrivate, isOutOfOrder, newBoatType, false);
                    boatsSerialNums.add(serialNumber);
                } catch (BoatAlreadyExistsException | SingleWithTwoOarsException | HelmsmanException e) {// a boat with the same number will not be added to list
                    exceptionsList.addException(e.getMessage());
                }
            }
        } catch (JAXBException e) {
            exceptionsList.addException("Could not import elements from file, please make sure this is an xml file that contains only boats and matches the schema");
        }catch (BoatAlreadyExistsException e){
            exceptionsList.addException(e.getMessage());
        }
        if (exceptionsList.hasExceptions())
            throw exceptionsList;
        return true;
    }


    public String marshalBoats() throws JAXBException{
            JAXBContext jaxbContext = JAXBContext.newInstance(Boats.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            Boats generatedNewBoats = new Boats();
            List<Boat> engineBoats = bmsEngine.getBoats();
            for (Boat boat : engineBoats) {
                jaxb.boats.generated.Boat newGeneratedBoat = new jaxb.boats.generated.Boat();
                newGeneratedBoat.setId(String.valueOf(boat.getSerialNum()));
                newGeneratedBoat.setName(boat.getBoatName());
                newGeneratedBoat.setType(BoatType.fromValue(convertBoatTypeToGeneratedEnum(boat.getBoatType())));
                newGeneratedBoat.setPrivate(boat.isPrivateProperty());
                newGeneratedBoat.setWide(boat.getBoatType().isWide());
                newGeneratedBoat.setHasCoxswain(boat.getBoatType().hasHelmsman());
                newGeneratedBoat.setCostal(boat.getBoatType().isCoastal());
                newGeneratedBoat.setOutOfOrder(boat.isOutOfOrder());

                generatedNewBoats.getBoat().add(newGeneratedBoat);
            }
            StringWriter stringWriterXml = new StringWriter();
            jaxbMarshaller.marshal(generatedNewBoats, stringWriterXml);
            String StringXml = stringWriterXml.toString();
            return StringXml;
    }

    //This method gets string(imported BoatType.value) and return Engine's Boat.BoatType enum value
    public Boat.BoatType.BoatSize boatTypeEnumToBoatSizeEnum(int typeIndex) {
        if (typeIndex == 0) // "SINGLE"
            return Boat.BoatType.BoatSize.SOLO;
        else if ((typeIndex >= 1) && (typeIndex <= 4))
            return Boat.BoatType.BoatSize.PAIR;
        else if ((typeIndex >= 5) && (typeIndex <= 8))
            return Boat.BoatType.BoatSize.FOUR;
        else  // 8 rowers
            return Boat.BoatType.BoatSize.EIGHT;
    }

    //This method gets string(imported BoatType.value) and return Boolean value of oneOar field
    public Boolean boatTypeEnumToOars(int typeIndex) {
        if (((typeIndex >= 3) && (typeIndex <= 6)) || (typeIndex == 10))// pair, Coxed pair, four, Coxed four, Eight
            return true; // have one oar
        else
            return false;
    }

    //This method gets index number of generated Enum Boatype and returns Boolean value of hasHelmaman field
    public Boolean boatTypeEnumToHasHelmsman(int typeIndex) {
        if ((typeIndex == 0) || (typeIndex == 1) || (typeIndex == 3) || (typeIndex == 5) || (typeIndex == 7))
            return false;
        else
            return true;
    }

    //This method checks if such serial number as the given boat's exists in the boats map
    //if it is found, the existing boat's id number is replaced to a new number, and it's key on the map also changes
    public void checkAndChangeBoatSerialNumber(int serialNum) {

        boolean usedSerialNum;
        Integer overlappingSerialNum = serialNum;
        if (bmsEngine.getBoatsMap().containsKey(serialNum)) { // if a boat with such key already exists, then change it
            usedSerialNum = true;
            serialNum *= 1000; // assuming a regular boat house has a lot less, so it's a whole new range of serial numbers
        } else
            usedSerialNum = false;

        while (usedSerialNum) {
            if (!bmsEngine.getBoatsMap().containsKey(serialNum))
            // if we reached a serial number that does not exist on Map
            {
                bmsEngine.changeBoatsSerialNumber(overlappingSerialNum, serialNum);
                usedSerialNum = false;
            }
            serialNum += 1; // looks for a new serial number that is not already linked to a boat

        }
    }

    public String convertBoatTypeToGeneratedEnum(Boat.BoatType type) {
        if (type.getBoatSize() == Boat.BoatType.BoatSize.SOLO)
            return "Single";
        else if (type.getBoatSize() == Boat.BoatType.BoatSize.PAIR) {
            if (type.isSingleOar()) // always expected to have boolean value
            {
                if (type.hasHelmsman())
                    return "Coxed_Pair";
                else
                    return "Pair";
            } else {
                if (type.hasHelmsman())
                    return "Coxed_Double";
                else
                    return "Double";
            }
        } else if (type.getBoatSize() == Boat.BoatType.BoatSize.FOUR) {
            if (type.isSingleOar()) // always expected to have boolean value
            {
                if (type.hasHelmsman())
                    return "Coxed_Four";
                else
                    return "Four";
            } else {
                if (type.hasHelmsman())
                    return "Coxed_Quad";
                else
                    return "Quad";
            }
        } else if (type.isSingleOar())
            return "Eight";
        else
            return "Octuple";
    }

    private static Boats deserializeBoatsFrom(InputStream in) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(Boats.class);
        Unmarshaller u = jc.createUnmarshaller();
        return (Boats) u.unmarshal(in);
    }


    //////////////////////////////////////////// Unmarshal Members ///////////////////////////////////////////

    public void unmarshalMembersFromXML(boolean uniteFlag, InputStream inputStream, String loggedManagerEmail) throws XmlMultipleExceptions {
        XmlMultipleExceptions exceptionsList = new XmlMultipleExceptions();
        try {
            Members members = deserializeMembersFrom(inputStream);
            if (!uniteFlag)
                bmsEngine.removeAllMembers(loggedManagerEmail);
            for (jaxb.members.generated.Member member : members.getMember()) {
                try {
                    String email = member.getEmail();
                    int serialNumber = Integer.parseInt(member.getId());
                    String memberName = member.getName();
                    int memberAge = member.getAge();
                    String notes = member.getComments();
                    int level = rowingLevelEnumToInt(member.getLevel());
                    boolean hasBoat;
                    int privateBoatNum;
                    if (member.isHasPrivateBoat() == null || member.isHasPrivateBoat() == false) {
                        hasBoat = false;
                        privateBoatNum = -1;
                    } else {
                        hasBoat = member.isHasPrivateBoat();
                        privateBoatNum = Integer.parseInt(member.getPrivateBoatId());
                    }
                    String phoneNumber = member.getPhone();
                    String password = member.getPassword();
                    boolean isManager;
                    if (Boolean.TRUE.equals(member.isManager()))
                        isManager = true;
                    else
                        isManager = false;
                    LocalDate membershipStarts = LocalDate.of(member.getJoined().getYear(), member.getJoined().getMonth(), member.getJoined().getDay());
                    LocalDate membershipExpires = LocalDate.of(member.getMembershipExpiration().getYear(), member.getMembershipExpiration().getMonth(), member.getMembershipExpiration().getDay());
                    bmsEngine.addNewMember(memberName, notes, email, password, memberAge, phoneNumber, hasBoat, privateBoatNum, level, isManager, membershipStarts, membershipExpires, false);
                } catch (EmailAddressAlreadyExistsException | BoatDoesNotExistException | ExpiryDateIsBeforeSignUpException e) {
                    exceptionsList.addException(e.getMessage());
                }

            }
        } catch (JAXBException e) {
            exceptionsList.addException("Could not import elements from file, please make sure this is an xml file that contains only members and matches the schema");
        }
        if (exceptionsList.hasExceptions())
            throw exceptionsList;
    }

    private static Members deserializeMembersFrom(InputStream in) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(Members.class);
        Unmarshaller u = jc.createUnmarshaller();
        return (Members) u.unmarshal(in);
    }

    private int rowingLevelEnumToInt(RowingLevel level) {
        return (level.ordinal() + 1);
    }

    private RowingLevel rowingLevelIntToEnum(int level) {
        switch (level) {
            case 1:
                return RowingLevel.BEGINNER;
            case 2:
                return RowingLevel.INTERMEDIATE;
            case 3:
        }
        return RowingLevel.ADVANCED;
    }

    public String marshalMembers() throws JAXBException{
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Members.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true); //marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            Members generatedNewMembers = new Members();
            List<Member> engineMembers = bmsEngine.getAllMembers();
            for (Member member : engineMembers) {
                jaxb.members.generated.Member newMemberToXML = new jaxb.members.generated.Member();

                newMemberToXML.setId(String.valueOf(member.getSerialNumber()));
                newMemberToXML.setName(member.getName());
                newMemberToXML.setAge(member.getAge());
                newMemberToXML.setLevel(rowingLevelIntToEnum(member.getLevel()));
                newMemberToXML.setComments(member.getNotes());
                newMemberToXML.setHasPrivateBoat(member.getPrivateBoatStatus());
                newMemberToXML.setPrivateBoatId(String.valueOf(member.getPrivateBoatSerialNumber()));
                newMemberToXML.setPhone(member.getPhoneNumber());
                newMemberToXML.setEmail(member.getEmailAddress());
                newMemberToXML.setPassword(member.getPassword());
                newMemberToXML.setManager(member.getIsManager());
                newMemberToXML.setJoined(fromLocalDateToXML(member.getSignUpDate()));
                newMemberToXML.setMembershipExpiration(fromLocalDateToXML(member.getExpirationDate()));

                generatedNewMembers.getMember().add(newMemberToXML);
            }

            StringWriter stringWriterXml = new StringWriter();
            jaxbMarshaller.marshal(generatedNewMembers, stringWriterXml);
            String StringXml = stringWriterXml.toString();
            return StringXml;
        } catch (DatatypeConfigurationException e) {
            return (e.getMessage());
        }
    }

    public XMLGregorianCalendar fromLocalDateToXML(LocalDate date) throws DatatypeConfigurationException {
        try {
            XMLGregorianCalendar xmlGregorianCalendar =
                    DatatypeFactory.newInstance().newXMLGregorianCalendar(date.toString());
            return xmlGregorianCalendar;
        } catch (DatatypeConfigurationException e) {
            return null;
        }
    }

//////////////////////////////////////////// Unmarshal Activities ///////////////////////////////////////////


    public void unmarshalActivitiesFromXML(boolean uniteFlag, InputStream inputStream) throws XmlMultipleExceptions {
        XmlMultipleExceptions exceptionsList = new XmlMultipleExceptions();
        try {
            Activities activities = deserializeActivitiesFrom(inputStream);
            if (!uniteFlag)
                bmsEngine.removeAllActivities();
            for (jaxb.activities.generated.Timeframe timeframe : activities.getTimeframe()) {
                //creates instances of the imported activities
                LocalTime startTime = getTimeFromString(timeframe.getStartTime());
                LocalTime endTime = getTimeFromString(timeframe.getEndTime());
                String activityName = timeframe.getName();
                Boat.BoatType requstedBoatType = null;
                if (timeframe.getBoatType() != null) { // since boatType in activity can be undefined
                    Boat.BoatType.BoatSize rowersNum = boatTypeEnumToBoatSizeEnum(timeframe.getBoatType().ordinal());
                    Boolean hasHelmsman = boatTypeEnumToHasHelmsman(timeframe.getBoatType().ordinal());
                    Boolean singleOar = boatTypeEnumToOars(timeframe.getBoatType().ordinal());
                    requstedBoatType = new Boat.BoatType(rowersNum, singleOar, null, hasHelmsman, null);
                }
                bmsEngine.addNewActivity(startTime, endTime, activityName, requstedBoatType, false);
            }
        } catch (JAXBException e) {
            exceptionsList.addException("Could not import elements from file, please make sure this is an xml file that contains only activities and matches the schema");
        } catch (EndTimeIsLowerException e) {
            exceptionsList.addException(e.getMessage());
        }
        if (exceptionsList.hasExceptions())
            throw exceptionsList;

    }


    private static Activities deserializeActivitiesFrom(InputStream in) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(Activities.class);
        Unmarshaller u = jc.createUnmarshaller();
        return (Activities) u.unmarshal(in);
    }

    private LocalTime getTimeFromString(String timeString) {

        LocalTime converted;
        int hours, minutes;
        int colonIndx = timeString.indexOf(':');
        hours = (Integer.parseInt(timeString.substring(0, colonIndx)));
        minutes = (Integer.parseInt(timeString.substring(colonIndx + 1)));
        converted = LocalTime.of(hours, minutes);
        return converted;
        // DateTimeException
    }


    public String marshalActivities() throws JAXBException {

        JAXBContext jaxbContext = JAXBContext.newInstance(Activities.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        Activities generatedNewActivities = new Activities();
        List<Activity> engineActivities = bmsEngine.getActivities();
        for (Activity activity : engineActivities) {
            jaxb.activities.generated.Timeframe newActivityToXML = new jaxb.activities.generated.Timeframe();

            newActivityToXML.setName(activity.getName());
            newActivityToXML.setStartTime(localTimeToString(activity.getStarts()));
            newActivityToXML.setEndTime(localTimeToString(activity.getEnds()));
            if (activity.getSpecifiedType() != null)
                newActivityToXML.setBoatType(jaxb.activities.generated.BoatType.fromValue(convertBoatTypeToGeneratedEnum(activity.getSpecifiedType())));
            generatedNewActivities.getTimeframe().add(newActivityToXML);
        }
        StringWriter stringWriterXml = new StringWriter();
        jaxbMarshaller.marshal(generatedNewActivities, stringWriterXml);
        String StringXml = stringWriterXml.toString();
        return StringXml;
    }

    public String localTimeToString(LocalTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return time.format(formatter);
    }
}
