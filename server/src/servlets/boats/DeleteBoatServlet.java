package servlets.boats;
import bms.engine.Engine;

import bms.engine.boatsManagement.boat.Boat;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

import static constants.Constants.ENGINE_ATTRIBUTE_NAME;
@WebServlet(name = "deleteActivityServlet", urlPatterns = {"/boats/delete"})

public class DeleteBoatServlet extends HttpServlet {
    private Gson gson = new Gson();
    Engine bmsEngine;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        bmsEngine = (Engine)req.getServletContext().getAttribute(ENGINE_ATTRIBUTE_NAME);
        BufferedReader reader = req.getReader();
        String newActivityJsonString = reader.lines().collect(Collectors.joining());
        int boatId = gson.fromJson(newActivityJsonString,Integer.class);
        deleteBoat(boatId);
        resp.setStatus(200);
    }

    private void deleteBoat(int boatId){
        Map<Integer,Boat> boats = this.bmsEngine.getBoatsMap();
        Boat boat = boats.get(boatId);
        this.bmsEngine.deleteBoat(boat);
    }
}
