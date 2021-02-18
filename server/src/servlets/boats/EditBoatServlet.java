package servlets.boats;
package servlets.boats;
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

@WebServlet(name = "EditBoatServlet", urlPatterns = {"/boats/edit"})
public class EditBoatServlet extends HttpServlet{

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp){
        Engine bmsEngine = (Engine)req.getServletContext().getAttribute(ENGINE_ATTRIBUTE_NAME);

    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp){
        Engine bmsEngine = (Engine)req.getServletContext().getAttribute(ENGINE_ATTRIBUTE_NAME);
        
    }

}
