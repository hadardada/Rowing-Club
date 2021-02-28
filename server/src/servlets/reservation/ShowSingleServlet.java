package servlets.reservation;
import bms.engine.Engine;
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



import static constants.Constants.ENGINE_ATTRIBUTE_NAME;

@WebServlet(name = "ShowSingleServlet", urlPatterns = {"/reservation/showSingle"})
public class ShowSingleServlet extends HttpServlet {
    private Gson gson = new Gson();
    Engine bmsEngine;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        bmsEngine = (Engine)req.getServletContext().getAttribute(ENGINE_ATTRIBUTE_NAME);

     //   String resMadeAtParameter = req.getParameter("createdOn");
        String gen = req.getParameter("creator");
   //     String resTrainingDateParameter = req.getParameter("date");
        String[] parts = gen.split("createdOn=");
        String creator = parts[0];
        String creatorGen = parts[1];
        String[] parts2 = creatorGen.split("date=");
        String resMadeAtParameter = parts2[0];
        String resTrainingDateParameter = parts2[1];







        LocalDateTime resMadeAt = LocalDateTime.parse(resMadeAtParameter);
        LocalDate trainingDate = LocalDate.parse(resTrainingDateParameter);
        Reservation reservation = this.bmsEngine.findResByResMadeAt(resMadeAt,creator,trainingDate);

        ReservationParameters reservationParameters = convertReservationToParameters(reservation);
        PrintWriter out = resp.getWriter();
        out.print(gson.toJson(reservationParameters));
        resp.setStatus(200);
    }

    private ReservationParameters convertReservationToParameters(Reservation res) {
        ReservationParameters resParameters = new ReservationParameters(res);
        return resParameters;
    }

}
