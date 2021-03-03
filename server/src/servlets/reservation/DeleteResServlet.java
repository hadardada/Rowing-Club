package servlets.reservation;

import bms.engine.Engine;
import bms.engine.reservationsManagment.reservation.Reservation;
import bms.notificationsEngine.notification.Notification;
import bms.notificationsEngine.notificatiosnManager.NotificationsManager;
import com.google.gson.Gson;
import utilities.ServletUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import static constants.Constants.ENGINE_ATTRIBUTE_NAME;

@WebServlet(name = "DeleteResServlet", urlPatterns = {"/reservation/delete"})
public class DeleteResServlet extends HttpServlet {
    private Gson gson = new Gson();
    Engine bmsEngine;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        bmsEngine = (Engine)req.getServletContext().getAttribute(ENGINE_ATTRIBUTE_NAME);
        NotificationsManager notificationsMng = ServletUtils.getNotificationsManager(req.getServletContext());

        String resMadeAtParameter = req.getParameter("createdOn");
        String resMadeByParameter = req.getParameter("creator");
        String resTrainingDateParameter = req.getParameter("date");

        LocalDateTime resMadeAt = LocalDateTime.parse(resMadeAtParameter, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDate trainingDate = LocalDate.parse(resTrainingDateParameter, DateTimeFormatter.ISO_LOCAL_DATE);
        Reservation reservation = this.bmsEngine.findResByResMadeAt(resMadeAt, resMadeByParameter, trainingDate);

        deleteReservation(reservation);
        notificationsMng.addNewAutoNotification(Notification.DELETED,reservation);

        resp.setStatus(200);
    }

    private void deleteReservation(Reservation reservation){
        this.bmsEngine.deleteReservation(reservation);
    }
}
