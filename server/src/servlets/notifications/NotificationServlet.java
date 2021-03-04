package servlets.notifications;

import bms.notificationsEngine.notification.Notification;
import bms.notificationsEngine.notificatiosnManager.NotificationsManager;
import com.google.gson.Gson;
import utilities.ServletUtils;
import utilities.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "NotificationServlet", urlPatterns = {"/notifications"})
public class NotificationServlet extends HttpServlet {
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String usernameFromSession = SessionUtils.getUsername(req);
        NotificationsManager notificationsMng = ServletUtils.getNotificationsManager(req.getServletContext());
        PrintWriter out = resp.getWriter();
        String content = req.getParameter("content");
        if (content.equals("new")){ // get new Notifications for member
            List<String> newNotifications = notificationsMng.getAllNewNotiesForUser(usernameFromSession);
            out.print(gson.toJson(newNotifications));
        }
        else if (content.equals("club")){
            List<Notification> clubNotifications = notificationsMng.getAllManualNotiesForUser();
            out.print(gson.toJson(clubNotifications));

        }
        else{ // get new messages counter
            int num = notificationsMng.getNumberOfNewNoties(usernameFromSession);
            out.print(num);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String usernameFromSession = SessionUtils.getUsername(req);
        NotificationsManager notificationsMng = ServletUtils.getNotificationsManager(req.getServletContext());
        String numberParameter= req.getParameter("notificationsRadios");
        if ( numberParameter!= null){ //delete
            int serNumber = Integer.parseInt(numberParameter);
            notificationsMng.deleteNotificationBySerNum(serNumber);
            resp.sendRedirect("/boathouse/notifications/manage.html");
        }
        else{
            String newMsg =req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            notificationsMng.addNewManualNotification(newMsg);
        }

    }

}
