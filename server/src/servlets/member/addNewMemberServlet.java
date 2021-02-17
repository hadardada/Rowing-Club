package servlets.member;
import bms.engine.membersManagement.member.Member;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.stream.Collectors;

@WebServlet(name = "addNewMemberServlet", urlPatterns = {"/member/addNew"})
public class addNewMemberServlet {
    private Gson gson = new Gson();

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //super.doPost(req, resp);
        BufferedReader reader = req.getReader();
        String newBoatJsonString = reader.lines().collect(Collectors.joining());

        Member newMember = gson.fromJson(newBoatJsonString, Member.class);
    }
}

