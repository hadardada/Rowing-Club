package servlets.member;
import java.io.PrintWriter;
import java.time.LocalDate;

import bms.engine.Engine;
import bms.engine.boatsManagement.boat.boatsListsExceptions.BoatDoesNotExistException;

import bms.engine.membersManagement.member.memberListsExceptions.EmailAddressAlreadyExistsException;
import bms.engine.membersManagement.member.memberListsExceptions.ExpiryDateIsBeforeSignUpException;
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

@WebServlet(name = "AddNewMemberServlet", urlPatterns = {"/member/addNew"})
public class AddNewMemberServlet extends HttpServlet {

        private Gson gson = new Gson();
        Engine bmsEngine;

        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            bmsEngine = (Engine)req.getServletContext().getAttribute(ENGINE_ATTRIBUTE_NAME);
            BufferedReader reader = req.getReader();
            String newMemberJsonString = reader.lines().collect(Collectors.joining());
            MemberParameters newMemberParameters = gson.fromJson(newMemberJsonString, MemberParameters.class);
            PrintWriter out = resp.getWriter();

            try {
                addNewMember(newMemberParameters);
                resp.setStatus(200);
            }
            catch (EmailAddressAlreadyExistsException e){
                resp.setStatus(404);
                out.print("Email Address Already Belong To Another Member");
            }
            catch (BoatDoesNotExistException e){
                resp.setStatus(404);
                out.print("Boat Serial Num Doesn't belong to any Boat in the Club");
            }
            catch (ExpiryDateIsBeforeSignUpException e){
                resp.setStatus(404);
                out.print("Expiry Date Is Before SignUp Exception");
            }
        }
        private void addNewMember(MemberParameters parameters) throws EmailAddressAlreadyExistsException, BoatDoesNotExistException, ExpiryDateIsBeforeSignUpException {
            LocalDate signUpDate = LocalDate.now();
            LocalDate now = LocalDate.now();
            LocalDate expirationDate = LocalDate.of(now.getYear()+1,now.getMonth(),now.getDayOfMonth());

            bmsEngine.addNewMember(parameters.name,parameters.notes,
                    parameters.email,parameters.password,parameters.age,
                    parameters.phoneNumber,parameters.havePrivateBoat,
                    parameters.privateBoatSerialNumber,parameters.rowingLevel,parameters.isManager,signUpDate,expirationDate,false);
        }
}
