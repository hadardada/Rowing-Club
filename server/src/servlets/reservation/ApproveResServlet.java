package servlets.reservation;

import bms.engine.Engine;
import bms.engine.boatsManagement.boat.Boat;
import bms.engine.boatsManagement.boat.boatsListsExceptions.BoatSizeMismatchException;
import bms.engine.membersManagement.member.Member;
import bms.engine.reservationsManagment.reservation.Reservation;
import bms.engine.reservationsManagment.reservation.reservationsExceptions.ApprovedReservationWithNoBoatException;
import bms.engine.reservationsManagment.reservation.reservationsExceptions.ParticipentRowerIsOnListException;
import com.google.gson.Gson;
import servlets.member.MemberParameters;

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
import java.time.format.DateTimeFormatter;

import static constants.Constants.ENGINE_ATTRIBUTE_NAME;

@WebServlet(name = "ApproveResServlet", urlPatterns = {"/reservation/approve"})
public class ApproveResServlet extends HttpServlet {
    private Gson gson = new Gson();
    Engine bmsEngine;
    Reservation originalRes;


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        bmsEngine = (Engine) req.getServletContext().getAttribute(ENGINE_ATTRIBUTE_NAME);

        String resMadeAtParameter = req.getParameter("createdOn");
        String resMadeByParameter = req.getParameter("creator");
        String resTrainingDateParameter = req.getParameter("date");

        LocalDateTime resMadeAt = LocalDateTime.parse(resMadeAtParameter, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDate trainingDate = LocalDate.parse(resTrainingDateParameter, DateTimeFormatter.ISO_LOCAL_DATE);
        Reservation reservation = this.bmsEngine.findResByResMadeAt(resMadeAt, resMadeByParameter, trainingDate);
        originalRes = reservation;

        List<Member> newWanted = new ArrayList<>();
        List<Member> wanted = reservation.getWantedRowers();
        newWanted.addAll(wanted);
        newWanted.add(reservation.getParticipantRower());
        List<MemberParameters> memberParametersList = new ArrayList<>();
        for (Member member : newWanted) {
            memberParametersList.add(convertMemberToParameters(member));
        }
        PrintWriter out = resp.getWriter();
        out.print(gson.toJson(memberParametersList));
        resp.setStatus(200);
    }

    private MemberParameters convertMemberToParameters(Member member) {
        MemberParameters memberParameters = new MemberParameters(member);
        return memberParameters;
    }

    public static class ShortApproveReservation {
        int BoatId;
        List<String> ActualMembers;

        public ShortApproveReservation( int BoatId, List<String> ActualMembers) {
            this.BoatId = BoatId;
            this.ActualMembers = ActualMembers;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        bmsEngine = (Engine)req.getServletContext().getAttribute(ENGINE_ATTRIBUTE_NAME);
        BufferedReader reader = req.getReader();
        String newMergeJsonString = reader.lines().collect(Collectors.joining());
        ShortApproveReservation newMergeParameters = gson.fromJson(newMergeJsonString, ShortApproveReservation.class);
        try {
            approveRes(newMergeParameters);
            resp.setStatus(200);
        }
        catch (ApprovedReservationWithNoBoatException e){

        }
        catch (BoatSizeMismatchException e){

        }
    }

    private void approveRes(ShortApproveReservation parameters) throws ApprovedReservationWithNoBoatException, BoatSizeMismatchException {
        List<Member> actual = new ArrayList<>();
        for (String member: parameters.ActualMembers){
            actual.add(this.bmsEngine.getMemberByEmail(member));
        }
        actual.add(originalRes.getParticipantRower());
        Boat boat = this.bmsEngine.getBoatById(parameters.BoatId);
        this.bmsEngine.assignBoatToReservation(originalRes,boat,false);
        this.bmsEngine.assignApprovedRowersToReservation(actual,originalRes,false);
        this.bmsEngine.updateApprovedStatus(originalRes);
    }
}
