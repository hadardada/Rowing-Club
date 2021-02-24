package servlets.boats;

import bms.engine.Engine;
import bms.engine.boatsManagement.boat.Boat;
import com.google.gson.Gson;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static constants.Constants.ENGINE_ATTRIBUTE_NAME;


@WebServlet(name = "displayBoatsTypeServlet", urlPatterns = {"/boatsType/showAll"})
public class displayAllBoatType extends HttpServlet {
    private Gson gson = new Gson();
    Engine bmsEngine;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        bmsEngine = (Engine)req.getServletContext().getAttribute(ENGINE_ATTRIBUTE_NAME);
        List<Boat.BoatType> boatsTypeList = this.bmsEngine.getCurrentBoatTypes();
        List<String> BoatTypeShortName = new ArrayList();
        for (Boat.BoatType boatType : boatsTypeList)
        {
            BoatTypeShortName.add(boatType.getShortName());
        }
        PrintWriter out = resp.getWriter();
        out.print(gson.toJson(BoatTypeShortName));
        resp.setStatus(200);
    }
}
