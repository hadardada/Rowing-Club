package servlets.member;
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

@WebServlet(name = "addNewMemberServlet", urlPatterns = {"/member/addNew"})
public class AddNewMemberServlet extends HttpServlet {

        private Gson gson = new Gson();
        Engine bmsEngine;

        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            bmsEngine = (Engine)req.getServletContext().getAttribute(ENGINE_ATTRIBUTE_NAME);
            BufferedReader reader = req.getReader();
            String newMemberJsonString = reader.lines().collect(Collectors.joining());
            MemberParameters newMemberParameters = gson.fromJson(newMemberJsonString, MemberParameters.class);

            try {
                addNewMember(newMemberParameters);
                resp.setStatus(200);
            }
            catch (EmailAddressAlreadyExistsException e){
                // all exceptions are being taken care of from browser
            }
            catch (BoatDoesNotExistException e){
                // all exceptions are being taken care of from browser
            }
            catch (ExpiryDateIsBeforeSignUpException e){
                // all exceptions are being taken care of from browser
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
