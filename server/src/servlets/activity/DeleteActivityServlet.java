package servlets.activity;
import bms.engine.Engine;

import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.stream.Collectors;

import static constants.Constants.ENGINE_ATTRIBUTE_NAME;
@WebServlet(name = "DeleteActivityServlet", urlPatterns = {"/activity/delete"})

public class DeleteActivityServlet extends HttpServlet {
    private Gson gson = new Gson();
    Engine bmsEngine;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        bmsEngine = (Engine)req.getServletContext().getAttribute(ENGINE_ATTRIBUTE_NAME);
        BufferedReader reader = req.getReader();
        String newActivityJsonString = reader.lines().collect(Collectors.joining());
        int activityId = gson.fromJson(newActivityJsonString,Integer.class);
        deleteActivity(activityId);
        resp.setStatus(200);
    }

    private void deleteActivity(int activityId){
        this.bmsEngine.removeActivityFromList(activityId);
    }
}
