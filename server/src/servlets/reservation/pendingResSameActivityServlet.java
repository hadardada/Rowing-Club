package servlets.reservation;

import bms.engine.Engine;
import bms.engine.activitiesManagement.activity.Activity;
import bms.engine.reservationsManagment.reservation.Reservation;
import com.google.gson.Gson;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static constants.Constants.ENGINE_ATTRIBUTE_NAME;

@WebServlet(name = "pendingResSameActivityServlet", urlPatterns = {"/reservation/pendingResSameActivity"})
public class pendingResSameActivityServlet extends HttpServlet {
    private Gson gson = new Gson();
    Engine bmsEngine;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        bmsEngine = (Engine)req.getServletContext().getAttribute(ENGINE_ATTRIBUTE_NAME);

        String resMadeAtParameter = req.getParameter("createdOn");
        String resMadeByParameter = req.getParameter("creator");
        String resTrainingDateParameter = req.getParameter("date");

        LocalDateTime resMadeAt = LocalDateTime.parse(resMadeAtParameter);
        LocalDate trainingDate = LocalDate.parse(resTrainingDateParameter);
        Reservation reservation = this.bmsEngine.findResByResMadeAt(resMadeAt, resMadeByParameter, trainingDate);

        int activityID = reservation.getActivity().getId();

      //  int activityIdInt = Integer.parseInt(activityId);
        List<Reservation> pendingResSameActivity = this.bmsEngine.getFuturePendingReservationSameActivity(trainingDate,activityID,reservation);
        List<ReservationParameters> toSent = new ArrayList<>();

        for (Reservation res: pendingResSameActivity) {
            toSent.add(convertReservationToParameters(res));
        }
        PrintWriter out = resp.getWriter();
        out.print(gson.toJson(toSent));
        resp.setStatus(200);
    }

    private ReservationParameters convertReservationToParameters(Reservation res) {
        ReservationParameters resParameters = new ReservationParameters(res,true);
        return resParameters;
    }
}
