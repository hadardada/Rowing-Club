//package servlets.boats;
//
//import bms.engine.Engine;
//import bms.engine.boatsManagement.boat.Boat;
//import com.google.gson.Gson;
//
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.ArrayList;
//import java.util.List;
//
//import static constants.Constants.ENGINE_ATTRIBUTE_NAME;
//
//@WebServlet(name = "relevantBoatsServlet", urlPatterns = {"/boats/getRelevant"})
//public class getRelevantBoat extends HttpServlet {
//    private Gson gson = new Gson();
//    Engine bmsEngine;
//
//    @Override
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
//        bmsEngine = (Engine)req.getServletContext().getAttribute(ENGINE_ATTRIBUTE_NAME);
//        String resId =
//        PrintWriter out = resp.getWriter();
//        out.print(gson.toJson(boatsParametersList));
//        resp.setStatus(200);
//    }
//
//    private BoatsParameter convertBoatToParameters(Boat boat){
//        BoatsParameter boatsParameter = new BoatsParameter(boat);
//        return boatsParameter;
//    }
//}
//
//}
