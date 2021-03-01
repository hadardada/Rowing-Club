package servlets.xml;

import bms.engine.Engine;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Scanner;


import bms.engine.engine.multipleExceptions.XmlMultipleExceptions;
import utilities.SessionUtils;
import static constants.Constants.ENGINE_ATTRIBUTE_NAME;


@WebServlet(name = "ImportXmlServlet", urlPatterns = {"/data/import"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)

public class ImportXmlServlet extends HttpServlet {

    public static final String FILE_MENU = "/import-export/files.html";
    Engine bmsEngine;
    String errorMsg = "";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        bmsEngine = (Engine) req.getServletContext().getAttribute(ENGINE_ATTRIBUTE_NAME);
        Collection<Part> parts = req.getParts();
        StringBuilder fileContent = new StringBuilder();

        for (Part part : parts) {
            //to write the content of the file to a string
            if (part.getSubmittedFileName() != null) // put only data from file (and not parameters)
                fileContent.append(readFromInputStream(part.getInputStream()));
        }
        try {
            String xmlfile = fileContent.toString();
            String username = SessionUtils.getUsername(req);
            String typeOfData = req.getParameter("importType");
            String mergeData = req.getParameter("mergeData");
            importData(typeOfData, mergeData, username, xmlfile);
            errorMsg = "success";
            resp.sendRedirect(FILE_MENU);
        } catch (XmlMultipleExceptions e) {
            errorMsg = e.getMessage();
            resp.sendRedirect(FILE_MENU);
        }

    }

    private void importData(String typeOfData, String mergeData, String manager, String xml) throws XmlMultipleExceptions {
        int dataType = Integer.parseInt(typeOfData);
        boolean unite = Boolean.parseBoolean(mergeData);
        bmsEngine.importXmlFromInputStream(dataType, xml, unite, manager);

    }

    private String readFromInputStream(InputStream inputStream) {
        return new Scanner(inputStream).useDelimiter("\\Z").next();
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (PrintWriter out = resp.getWriter()) {
            out.print(errorMsg);
        }
    }
}
