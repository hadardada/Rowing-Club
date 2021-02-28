package servlets.member;
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
@WebServlet(name = "DeleteMemberServlet", urlPatterns = {"/member/delete"})

public class DeleteMemberServlet extends HttpServlet {
    private Gson gson = new Gson();
    Engine bmsEngine;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        bmsEngine = (Engine)req.getServletContext().getAttribute(ENGINE_ATTRIBUTE_NAME);
        BufferedReader reader = req.getReader();
        String newActivityJsonString = reader.lines().collect(Collectors.joining());
        String memberEmail = gson.fromJson(newActivityJsonString,String.class);
        deleteMember(memberEmail);
        resp.setStatus(200);
    }

    private void deleteMember(String memberEmail){
        this.bmsEngine.removeMember(memberEmail);
    }
}
