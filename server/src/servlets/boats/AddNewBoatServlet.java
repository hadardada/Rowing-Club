package servlets.boats;

import bms.engine.Engine;
import static constants.Constants.ENGINE_ATTRIBUTE_NAME;

import bms.engine.boatsManagement.boat.Boat;
import bms.engine.boatsManagement.boat.boatsListsExceptions.BoatAlreadyExistsException;
import bms.engine.boatsManagement.boat.boatsListsExceptions.HelmsmanException;
import bms.engine.boatsManagement.boat.boatsListsExceptions.SingleWithTwoOarsException;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.stream.Collectors;

@WebServlet(name = "ad  dNewBoatServlet", urlPatterns = {"/boats/addNew"})
public class AddNewBoatServlet extends HttpServlet {

    private Gson gson = new Gson();
    Engine bmsEngine;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //super.doPost(req, resp);
        bmsEngine = (Engine)req.getServletContext().getAttribute(ENGINE_ATTRIBUTE_NAME);
        BufferedReader reader = req.getReader();
        String newBoatJsonString = reader.lines().collect(Collectors.joining());
        BoatsParameter newBoatParameters = gson.fromJson(newBoatJsonString, BoatsParameter.class);

        try {
            addNewBoat(newBoatParameters);
            resp.setStatus(200);
            }

        catch (SingleWithTwoOarsException|HelmsmanException|BoatAlreadyExistsException e)
        {
            // all exceptions are being taken care of from browser
        }

    }


    private void addNewBoat(BoatsParameter parameters) throws SingleWithTwoOarsException, HelmsmanException, BoatAlreadyExistsException {

        Boat.BoatType.BoatSize size = Boat.BoatType.BoatSize.valueOf(parameters.rowersNum.toUpperCase());
        Boat.BoatType newBoatType = new Boat.BoatType(size,parameters.singleOar, parameters.wide, parameters.helmsman, parameters.coastal);
        bmsEngine.addNewBoat(parameters.boatName, null, parameters.privateProperty, parameters.status, newBoatType, false);
    }
}
