package servlets.reservation;

import bms.engine.Engine;
import bms.engine.reservationsManagment.reservation.Reservation;
import bms.engine.userManager.UserManager;
import com.google.gson.Gson;
import utilities.ServletUtils;
import utilities.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static constants.Constants.ENGINE_ATTRIBUTE_NAME;

@WebServlet(name = "showAllServlet", urlPatterns = {"/reservation/weekly"})

public class showAllServlet extends HttpServlet {
    private Gson gson = new Gson();
    Engine bmsEngine;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        bmsEngine = (Engine) req.getServletContext().getAttribute(ENGINE_ATTRIBUTE_NAME);
        String usernameFromSession = SessionUtils.getUsername(req);
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        boolean personalView = !(userManager.isUserManager(usernameFromSession));
        //BufferedReader reader = req.getReader();
        String date = req.getParameter("date");
        String status = req.getParameter("status");
        List<ShortReservation> shortReservations = getRequestedRes(date, status, personalView, usernameFromSession);
        PrintWriter out = resp.getWriter();
        out.print(gson.toJson(shortReservations));
    }

    public static class ShortReservation {
        String createdOn;
        String createdBy;
        String mainRower;
        String activityId;
        String boatId;
        String status;
        String date;


        public ShortReservation(Reservation reservation) {
            this.createdOn = reservation.getReservationDateTime().format((DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            this.createdBy = reservation.getReservationMember().getEmailAddress();
            this.mainRower = reservation.getParticipantRower().getEmailAddress();
            this.activityId = String.valueOf(reservation.getActivity().getId());
            if (reservation.getIsApproved() == null) {
                this.status = "Pending";
                this.boatId = "";
            } else if (reservation.getIsApproved()) {
                this.status = "Approved";
                this.boatId = String.valueOf(reservation.getReservationBoat().getSerialNum());
            } else {
                this.status = "Rejected";
                this.boatId = "";
            }
            this.date = reservation.getTrainingDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
        }
    }

    public static class requestObj {
        String status;
    }

    //this method creates a list of ShortReservation object, based on the Reservations on a given date
    //the returned list can be of all the reservations for a given date (status = "all"), or only the approved ones
    public List<ShortReservation> getRequestedRes(String date, String status, boolean personalView, String ofMember) {
        List<ShortReservation> reservationsForDate = new ArrayList<>();
        if (personalView) { // show only reservations that are linked to a specific member
            List<Reservation> reservationsForMember;
            if (status.equals("future"))
                reservationsForMember = bmsEngine.getAllFutureReservationOfMember(ofMember);
            else //status == "history"
                reservationsForMember = bmsEngine.getReservationHistoryOfMember(ofMember);
            if (reservationsForMember!= null) {
                for (Reservation res : reservationsForMember) {
                    ShortReservation newShort = new ShortReservation(res);
                    reservationsForDate.add(newShort);
                }
            }
        }
        else { // manager view - can see all reservations of all members
            LocalDate requestedDate = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
            if(bmsEngine.getClosedReservationForDate(requestedDate)!= null){
            for (Reservation res : bmsEngine.getClosedReservationForDate(requestedDate)) {
                if (((status.equals("approved"))&&(Boolean.TRUE ==res.getIsApproved()))||(status.equals("all"))) {
                    ShortReservation newShort = new ShortReservation(res);
                    reservationsForDate.add(newShort);
                }
        }}
        if (status.equals("all")) {
            if (bmsEngine.getOpenReservationForDate(requestedDate)!= null){
                for (Reservation res : bmsEngine.getOpenReservationForDate(requestedDate)) {
                    ShortReservation newShort = new ShortReservation(res);
                    reservationsForDate.add(newShort);
            }}
        }}
        return reservationsForDate;
    }

}
