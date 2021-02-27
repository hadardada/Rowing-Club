package servlets.reservation;

import bms.engine.Engine;
import bms.engine.boatsManagement.boat.Boat;
import bms.engine.reservationsManagment.reservation.Reservation;
import com.google.gson.Gson;
import servlets.boats.BoatsParameter;

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

import static constants.Constants.ENGINE_ATTRIBUTE_NAME;

@WebServlet(name = "relevantBoatsServlet", urlPatterns = {"/reservation/getRelevantBoat"})
public class getRelevantBoat extends HttpServlet {
        private Gson gson = new Gson();
        Engine bmsEngine;

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            bmsEngine = (Engine)req.getServletContext().getAttribute(ENGINE_ATTRIBUTE_NAME);

            String resMadeAtParameter = req.getParameter("createdOn");
            String resMadeByParameter = req.getParameter("creator");
            String resTrainingDateParameter = req.getParameter("date");

            LocalDateTime resMadeAt = LocalDateTime.parse(resMadeAtParameter, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            LocalDate trainingDate = LocalDate.parse(resTrainingDateParameter, DateTimeFormatter.ISO_LOCAL_DATE);
            Reservation reservation = this.bmsEngine.findResByResMadeAt(resMadeAt, resMadeByParameter, trainingDate);

            List<Boat> boats = this.bmsEngine.getRelevantBoats(reservation);
            PrintWriter out = resp.getWriter();
            List<BoatsParameter> boatsParametersList = new ArrayList<>();
            for (Boat boat: boats) {
                boatsParametersList.add(convertBoatToParameters(boat));
            }
            out.print(gson.toJson(boatsParametersList));
            resp.setStatus(200);
        }
        private BoatsParameter convertBoatToParameters(Boat boat){
            BoatsParameter boatsParameter = new BoatsParameter(boat);
            return boatsParameter;
        }
}
