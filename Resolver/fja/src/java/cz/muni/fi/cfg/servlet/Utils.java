package cz.muni.fi.cfg.servlet;

import cz.muni.fi.admin.LogsController;
import cz.muni.fi.admin.ServicesController;
import cz.muni.fi.admin.ServicesController.ConversionType;
import cz.muni.fi.admin.ServicesController.OperationType;
import cz.muni.fi.admin.ServicesController.TransformationType;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Daniel Pelisek <dpelisek@gmail.com>
 * @version 1.0
 * @since 2011-04-10
 */
public class Utils extends HttpServlet {
  
  private enum Request{
    LOGS_JSON,
    TRANSFORMATIONS_JSON,
    CONVERSIONS_JSON,
    TEST,
    SET;
  }

  private static void processAndRedirect(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
    response.setCharacterEncoding("UTF-8");
    PrintWriter out = response.getWriter();
      try{
        Request r = Request.valueOf(request.getParameter("request").toUpperCase());
        switch(r) {
          case LOGS_JSON:
            out.print(LogsController.getLogsJson());
            break;
          case TRANSFORMATIONS_JSON:
            out.print(ServicesController.getTransformationsJson());
            break;
          case CONVERSIONS_JSON:
            out.print(ServicesController.getConversionsJson());
            break;
          case TEST:
            out.print("čau");
            break;
          case SET:
            String service = request.getParameter("service").toUpperCase();
            OperationType operation;
            try {
              operation = TransformationType.valueOf(service);
            }
            catch(IllegalArgumentException iae) {
              operation = ConversionType.valueOf(service);
            }
            boolean allowed = Boolean.parseBoolean(request.getParameter("allowed"));
            ServicesController.instance().setAllowed(operation, allowed);
            break;
        }
      }
      catch (Throwable t) {
        // ignore
      }
      finally{
          out.close();
      }
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
    processAndRedirect(request, response);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
    processAndRedirect(request, response);
  }

  @Override
  public String getServletInfo() {
    return "Servlet applying CYK algorithm on given word and given CFG in CNF.";
  }
}
