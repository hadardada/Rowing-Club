package servlets.boats;

import bms.engine.Engine;
import static constants.Constants.ENGINE_ATTRIBUTE_NAME;

import bms.engine.boatsManagement.boat.Boat;
import com.google.gson.Gson;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.Collectors;

@WebServlet(name = "EditBoatServlet", urlPatterns = {"/boats/edit"})
public class EditBoatServlet extends HttpServlet{
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Engine bmsEngine = (Engine)req.getServletContext().getAttribute(ENGINE_ATTRIBUTE_NAME);
        String boatIdParameter = req.getParameter("boatId");
        if (boatIdParameter != null) {
            Integer boatSerialNum = Integer.parseInt(boatIdParameter);
            if (boatSerialNum != null) {
                Boat requestedBoat = bmsEngine.getBoatById(boatSerialNum);
                if (requestedBoat != null) {
                    BoatsParameter respBoat = new BoatsParameter(requestedBoat);
                    resp.setContentType("application/json");
                    try (PrintWriter out = resp.getWriter()) {
                        out.print(gson.toJson(respBoat));
                    }
                } else {
                    resp.setStatus(404);
                }
            }
        }
        else
        {
            //return empty page?
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Engine bmsEngine = (Engine)req.getServletContext().getAttribute(ENGINE_ATTRIBUTE_NAME);


        BufferedReader reader = req.getReader();
        String updateReqJsonString = reader.lines().collect(Collectors.joining());
        UpdateRequestObject updateReq = gson.fromJson(updateReqJsonString, UpdateRequestObject.class);
        updateReq.detectUpdate(bmsEngine);

        resp.setContentType("application/json");

        try (PrintWriter out = resp.getWriter()) {
            out.print(gson.toJson(updateReq));
        }

    }

}
