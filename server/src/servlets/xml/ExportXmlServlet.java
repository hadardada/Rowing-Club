package servlets.xml;

import bms.engine.Engine;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.PrintWriter;

import static constants.Constants.ENGINE_ATTRIBUTE_NAME;

@WebServlet(name = "ExportXmlServlet", urlPatterns = {"/data/export"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)

public class ExportXmlServlet  extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Engine bmsEngine = (Engine) req.getServletContext().getAttribute(ENGINE_ATTRIBUTE_NAME);

        PrintWriter out = resp.getWriter();
        resp.setContentType("text/xml");
        String dataType = req.getParameter("exportRadios");
        String filename ="";
        switch (dataType){
            case ("1"):
                filename = "activities.xml";
                break;
            case ("2"):
                filename = "boats.xml";
                break;
            case ("3"):
                filename = "members.xml";
                break;
        }
        try {
            String file = bmsEngine.exportXmlToString(Integer.parseInt(dataType));
            resp.setHeader("Content-Disposition","attachment; filename=\"" + filename + "\"");
            out.print(file);
        } catch (JAXBException e) {
        }




    }
}
