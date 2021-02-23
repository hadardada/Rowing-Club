package servlets.member;

import bms.engine.Engine;
import bms.engine.membersManagement.member.Member;
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

@WebServlet(name = "displayMemberServlet", urlPatterns = {"/member/showAll"})
public class displayMember extends HttpServlet {
    private Gson gson = new Gson();
    Engine bmsEngine;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        bmsEngine = (Engine)req.getServletContext().getAttribute(ENGINE_ATTRIBUTE_NAME);
        List<Member> memberList = this.bmsEngine.getAllMembers();
        List<MemberParameters> memberParametersList = new ArrayList<MemberParameters>();
        for (Member member : memberList)
        {
            memberParametersList.add(convertMemberToParameters(member));
        }
        PrintWriter out = resp.getWriter();
        out.print(gson.toJson(memberParametersList));
        resp.setStatus(200);
    }

    private MemberParameters convertMemberToParameters(Member member) {
        MemberParameters memberParameters = new MemberParameters(member);
        return memberParameters;
    }

}
