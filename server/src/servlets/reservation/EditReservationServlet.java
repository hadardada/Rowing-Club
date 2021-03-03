package servlets.reservation;


import bms.engine.Engine;
import bms.engine.boatsManagement.boat.Boat;
import bms.engine.membersManagement.member.Member;
import bms.engine.reservationsManagment.reservation.Reservation;
import bms.engine.reservationsManagment.reservation.reservationsExceptions.ParticipentRowerIsOnListException;
import bms.notificationsEngine.notificatiosnManager.NotificationsManager;
import com.google.gson.Gson;
import utilities.ServletUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static constants.Constants.ENGINE_ATTRIBUTE_NAME;

@WebServlet(name = "EditReservationServlet", urlPatterns = {"/reservation/edit"})

public class EditReservationServlet   extends HttpServlet {
    private Gson gson = new Gson();
    Engine bmsEngine;

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        bmsEngine = (Engine)req.getServletContext().getAttribute(ENGINE_ATTRIBUTE_NAME);
        NotificationsManager notificationsMng = ServletUtils.getNotificationsManager(req.getServletContext());

        String resMadeAtParameter = req.getParameter("createdOn");
        String resMadeByParameter = req.getParameter("creator");
        String resTrainingDateParameter = req.getParameter("date");

        LocalDateTime resMadeAt = LocalDateTime.parse(resMadeAtParameter, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDate trainingDate = LocalDate.parse(resTrainingDateParameter, DateTimeFormatter.ISO_LOCAL_DATE);
        Reservation reservation = this.bmsEngine.findResByResMadeAt(resMadeAt, resMadeByParameter, trainingDate);

        String whatToEditParameter = req.getParameter("what");
        if (whatToEditParameter.equals("mainRower")){
            String mainRowerEmail =req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            Member mainRower = bmsEngine.getMemberByEmail(mainRowerEmail);
                try {
                    bmsEngine.updateReservatioParticipantRower(mainRower, reservation);
                } catch (ParticipentRowerIsOnListException e) {
                    resp.setStatus(400);
                    try (PrintWriter out = resp.getWriter()) {
                        resp.setStatus(400);
                        out.print(e.getMessage());
                    }
                }

        }
        else if (whatToEditParameter.equals("trainingDate")){
            String date =req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            LocalDate newDate = LocalDate.parse(date,  DateTimeFormatter.ISO_LOCAL_DATE);
            bmsEngine.updateReservatioTrainingDate(newDate, reservation);
            resp.sendRedirect("/reservation/edit.html?creator="+resMadeByParameter+"&createdOn="+resMadeAtParameter+
                    "&date="+date);
        }
        else if (whatToEditParameter.equals("rowers")){
            String rowersStrings = req.getReader().lines().collect(Collectors.joining());
            List<String> rowerEmails = (List<String>)gson.fromJson(rowersStrings, ArrayList.class);
            List<Member> wantedMembers = new ArrayList<>();
            for(String member : rowerEmails)
            {
                Member wantedRower = bmsEngine.getMemberByEmail(member);
                wantedMembers.add(wantedRower);
            }
            try {
                bmsEngine.updateReservationAdditinalWantedRowers( wantedMembers, reservation);
            } catch (ParticipentRowerIsOnListException e) {
                try (PrintWriter out = resp.getWriter()) {
                    resp.setStatus(400);
                    out.print(e.getMessage());
                }
            }
        }
        else{// boats
            String boatTypeString = req.getReader().lines().collect(Collectors.joining());
            List<String> boatTypeFromUpdate = (List<String>)gson.fromJson(boatTypeString, ArrayList.class);
            List<Boat.BoatType> boatTypes = bmsEngine.getCurrentBoatTypes();
            List<Boat.BoatType> resBoatTypes = new ArrayList<>();
            for (String boatTypeName: boatTypeFromUpdate) {
                for (Boat.BoatType boatType : boatTypes) {
                   if (boatTypeName.equals(boatType.getShortName())) {
                        resBoatTypes.add(boatType);
                    }
                }
            }
            //bmsEngine.updateReservatioBoatTypesAdd(resBoatTypes, reservation);

        }

    }


}
