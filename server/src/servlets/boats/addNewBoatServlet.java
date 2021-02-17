package servlets.boats;

import bms.engine.boatsManagement.boat.Boat;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.stream.Collectors;

@WebServlet(name = "addNewBoatServlet", urlPatterns = {"/boats/addNew"})
public class addNewBoatServlet extends HttpServlet {

    private Gson gson = new Gson();
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //super.doPost(req, resp);
        BufferedReader reader = req.getReader();
        String newBoatJsonString = reader.lines().collect(Collectors.joining());

        Boat newBoat = gson.fromJson(newBoatJsonString, Boat.class);

    }


    //public void addNewBoat
}
