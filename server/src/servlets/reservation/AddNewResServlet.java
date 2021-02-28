package servlets.reservation;

import bms.engine.Engine;
import bms.engine.activitiesManagement.activity.Activity;
import bms.engine.boatsManagement.boat.Boat;
import bms.engine.membersManagement.member.Member;
import bms.engine.reservationsManagment.reservation.reservationsExceptions.ParticipentRowerIsOnListException;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static constants.Constants.ENGINE_ATTRIBUTE_NAME;

@WebServlet(name = "AddNewResServlet", urlPatterns = {"/reservation/addNew"})
public class AddNewResServlet extends HttpServlet {
    private Gson gson = new Gson();
    Engine bmsEngine;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        bmsEngine = (Engine)req.getServletContext().getAttribute(ENGINE_ATTRIBUTE_NAME);
        BufferedReader reader = req.getReader();
        String newReservationJsonString = reader.lines().collect(Collectors.joining());
       // String newJson = gson.toJson(newReservationJsonString);
        //    newReservationJsonString = newReservationJsonString.substring(1, newReservationJsonString.length() - 1);
        ReservationParameters newReservationParameters = gson.fromJson(newReservationJsonString, ReservationParameters.class);
        PrintWriter out = resp.getWriter();

        try {
            addNewReservation(newReservationParameters);
            resp.setStatus(200);
        }
        catch (ParticipentRowerIsOnListException e){
            resp.setStatus(404);
            out.print("Main Rower is in Rowers List");
        }
    }
    private void addNewReservation(ReservationParameters parameters) throws ParticipentRowerIsOnListException {
        Member participantRower = bmsEngine.getMemberByEmail(parameters.participantRowerEmail);
        String trainingDateString = parameters.trainingDate;
        LocalDate trainingDate = LocalDate.parse(parameters.trainingDate);
        Activity activity = bmsEngine.findActivityById(parameters.activityID);
        List<Boat.BoatType> boatTypes = bmsEngine.getCurrentBoatTypes();
        List<Boat.BoatType> resBoatTypes = new ArrayList<>();
        for (String boatTypeName: parameters.boatTypes) {
            for (Boat.BoatType boatType : boatTypes) {
                if (boatTypeName.equals(boatType.getShortName())) {
                    resBoatTypes.add(boatType);
                }
            }
        }
        List<Member> wantedMembers = new ArrayList<>();
            for(String member : parameters.wantedMemberEmails)
            {
                Member wantedRower = bmsEngine.getMemberByEmail(member);
                wantedMembers.add(wantedRower);
            }
        LocalDateTime resMadeAt = LocalDateTime.now();
        Member resMadeBy = bmsEngine.getMemberByEmail(parameters.reservationMadeBy);
        bmsEngine.addNewReservation(participantRower,trainingDate,activity,resBoatTypes,wantedMembers,resMadeAt,resMadeBy,false);
    }
}
