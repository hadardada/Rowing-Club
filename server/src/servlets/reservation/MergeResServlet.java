package servlets.reservation;
import bms.engine.Engine;
import bms.engine.membersManagement.member.Member;

import bms.engine.reservationsManagment.reservation.Reservation;
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

import static constants.Constants.ENGINE_ATTRIBUTE_NAME;

@WebServlet(name = "MergeResServlet", urlPatterns = {"/reservation/merge"})
public class MergeResServlet extends HttpServlet {
    private Gson gson = new Gson();
    Engine bmsEngine;
    Reservation originalRes;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        bmsEngine = (Engine) req.getServletContext().getAttribute(ENGINE_ATTRIBUTE_NAME);

        String resMadeAtParameter = req.getParameter("createdOn");
        String resMadeByParameter = req.getParameter("creator");
        String resTrainingDateParameter = req.getParameter("date");

        LocalDateTime resMadeAt = LocalDateTime.parse(resMadeAtParameter);
        LocalDate trainingDate = LocalDate.parse(resTrainingDateParameter);
        Reservation reservation = this.bmsEngine.findResByResMadeAt(resMadeAt, resMadeByParameter, trainingDate);
        originalRes = reservation;
        List<Member> wanted = reservation.getWantedRowers();
        wanted.add(reservation.getParticipantRower());
        List<MemberParameters> memberParametersList = new ArrayList<>();
        for (Member member : wanted) {
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

    public static class ShortMergeReservation {
        String createdOnMerge;
        String createdByMerge;
        List<String> wantedMerge;
        List<String> wantedOriginal;

        public ShortMergeReservation(String createdOnMerge, String createdByMerge, List<String> wantedMerge, List<String> wantedOriginal) {
            this.createdByMerge = createdByMerge;
            this.createdOnMerge = createdOnMerge;
            this.wantedMerge = wantedMerge;
            this.wantedOriginal = wantedOriginal;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        bmsEngine = (Engine)req.getServletContext().getAttribute(ENGINE_ATTRIBUTE_NAME);
        BufferedReader reader = req.getReader();
        String newMergeJsonString = reader.lines().collect(Collectors.joining());
        ShortMergeReservation newMergeParameters = gson.fromJson(newMergeJsonString, ShortMergeReservation.class);
        mergeRes(newMergeParameters);
        resp.setStatus(200);
    }
    private void mergeRes(ShortMergeReservation parameters){
        List<Member> originalWanted = new ArrayList<>();
        List<Member> mergeWanted = new ArrayList<>();
        for (String member: parameters.wantedOriginal){
            originalWanted.add(this.bmsEngine.getMemberByEmail(member));
        }
        for (String member: parameters.wantedMerge){
            mergeWanted.add(this.bmsEngine.getMemberByEmail(member));
        }
        LocalDateTime resMergeMadeAt = LocalDateTime.parse(parameters.createdOnMerge);
        Reservation mergedRes = this.bmsEngine.findResByResMadeAt(resMergeMadeAt,parameters.createdByMerge,originalRes.getTrainingDate());
        this.bmsEngine.mergeReservations(originalWanted,mergeWanted,originalRes,mergedRes);
    }
}

