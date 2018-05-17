package cz.muni.fi.cfg.servlet;

import cz.muni.fi.admin.ServicesController;
import cz.muni.fi.admin.ServicesController.ConversionType;
import cz.muni.fi.cfg.conversions.CFGComparator;
import cz.muni.fi.cfg.conversions.Modes;
import cz.muni.fi.cfg.conversions.TransformationTypes;
import cz.muni.fi.cfg.grammar.ContextFreeGrammar;
import cz.muni.fi.cfg.parser.Parser;
import cz.muni.fi.cfg.parser.ParserException;
import cz.muni.fi.xpastirc.db.DBHandler;
import cz.muni.fi.xpastirc.db.MySQLHandler;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author NICKT
 */
public class Evaluate extends HttpServlet {

  private static Logger log = Logger.getLogger(Evaluate.class);

  private static Modes getMode(HttpServletRequest request, boolean isExternal) {

    Modes mode;

    if (isExternal) {
      mode = Modes.simple;
    } else {
      mode = Modes.valueOf(request.getParameter("mode"));
    }

    log.debug("Mode: " + mode);

    return mode;
  }

  private static String getStudentData(HttpServletRequest request, boolean isExternal)
          throws ParserException {

    String studentData;

    if (isExternal) {
      studentData = request.getParameter("s");
    } else {
      studentData = request.getParameter("studentData");
      request.setAttribute("studentData", studentData);
    }

    if (studentData == null || studentData.equals("")) {
      throw new ParserException("Empty student data");
    }

    log.debug("Student data: " + studentData);

    return studentData;
  }

  private static String getTeacherData(HttpServletRequest request, boolean isExternal)
          throws ParserException {

    String teacherData;

    if (isExternal) {
      String externalData = request.getParameter("t");
      String[] parsedData = Parser.parseISString(externalData);
      teacherData = parsedData[1];
    } else {
      teacherData = request.getParameter("teacherData");
      request.setAttribute("teacherData", teacherData);
    }

    if (teacherData == null || teacherData.equals("")) {
      throw new ParserException("Empty teacher's data");
    }

    log.debug("Teacher data: " + teacherData);

    return teacherData;
  }

  private static TransformationTypes getTransformation(HttpServletRequest request, boolean isExternal)
          throws ParserException {

    String transformation;

    if (isExternal) {
      String externalData = request.getParameter("t");
      String[] parsedData = Parser.parseISString(externalData);
      transformation = parsedData[0];
    } else {
      transformation = request.getParameter("stud");
    }
    request.setAttribute("stud", transformation);

    log.debug("Transformation type: " + transformation);
    
    try {
      return TransformationTypes.valueOf(transformation);
    }
    catch(Exception e) {
      throw new ParserException("incorrect transformation");
    }
  }

  private static void processRequest(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException, ParserException, IllegalAccessException, SQLException {

    // set encoding
    request.setCharacterEncoding("utf-8");
    response.setContentType("text/html;charset=UTF-8");

    // get data
    boolean isExternal = request.getParameterMap().keySet().contains("s");
    String studentData = getStudentData(request, isExternal);
    String teacherData = getTeacherData(request, isExternal);
    TransformationTypes stud = getTransformation(request, isExternal);
    
        
    if (!ServicesController.instance().isAllowed(ConversionType.fromTransformationTypes(stud))) {
      throw new IllegalAccessException("Operace " + stud + " není momentálně povolena");
    }

    // parse data
    ContextFreeGrammar studentCFG = Parser.parse(studentData);
    ContextFreeGrammar teacherCFG = Parser.parse(teacherData);
    List<String> ordering = Parser.orderingOfNonTerminals(teacherData);

    // compare
    String[] result = CFGComparator.compare(studentCFG, teacherCFG, ordering, stud, mode);
    //put to db
            int mod = (mode == Modes.simple?1:
                      (mode == Modes.normal?2
                      :0));
            long key = -10;
            DBHandler h = null;
            try {
                h = MySQLHandler.getHandler();
                key = h.logEqual(mod, teacherData, "CFG", studentData, stud.toString(), request.getRemoteAddr());
            } catch (ClassNotFoundException ex) {
                if (mode != Modes.simple)
                {
                    log.error("Comparison failed: " + ex.getMessage(), ex);
                    //throw new SQLException("Chyba: Nemám ovládač databáze");
                }
            } catch (SQLException ex) {
                if (mode != Modes.simple)
                {
                    log.error("Comparison failed: " + ex.getMessage(), ex);
                    //throw new SQLException("Chyba: špatný dotaz db: " + ex.getMessage());
                }
            }
            
            boolean eq = (result[0]).equals("true");
            
            if (h!= null){
                try {
                    h.logEqualAnswer(key, eq);
                } catch (SQLException ex) {

                    //pri vypadku DB nic nedelat
                }
            }
    // return result
    request.setAttribute("mode", request.getParameter("mode"));
    if (mode == Modes.simple) {
        PrintWriter out= response.getWriter();;
      try { 
        if (result[0].equals("true")) {out.println(result[0]);}
        else {out.println(result[0]+"||"+result[1]/*+"||"+result[2]*/);}
      } finally {out.close();}
    } else {
      request.setAttribute("windowData", result);
      request.getRequestDispatcher("/resultevaluation.jsp").forward(request, response);
    }
  }

  private static Modes mode;

  private static void processAndRedirect(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
    try {
      boolean isExternal = request.getParameterMap().keySet().contains("s");
      mode = getMode(request, isExternal);
      processRequest(request, response);
      log.info("Comparison successful");
    } catch (IllegalAccessException e) {
      if (mode == Modes.simple) {
        PrintWriter out = response.getWriter();;
        try {
          out.println("false||" + e.getMessage());
        } finally {out.close();}
      }
      else {
        request.setAttribute("error", e.getMessage());
        request.getRequestDispatcher("/indexcfg.jsp").forward(request, response);
      }
      log.info("Comparison failed: " + e.getMessage());
    } catch (ParserException e) {
      if (mode == Modes.simple) {
        PrintWriter out = response.getWriter();;
        try {
          out.println("false||" + e.getMessage());
        } finally {out.close();}
      }
      else {
        request.setAttribute("error", "Neplatné vstupní údaje");
        request.getRequestDispatcher("/indexcfg.jsp").forward(request, response);
      }
      log.info("Comparison failed: incorrect input data", e);
    } catch (SQLException s){
      if (mode == Modes.simple) {
        PrintWriter out = response.getWriter();;
        try {
          out.println("false||" + s);
        } finally {out.close();}
      }
      else {
        request.setAttribute("error", s);
        request.getRequestDispatcher("/indexcfg.jsp").forward(request, response);
      }
    } catch (Throwable t) {
      if (mode == Modes.simple) {
        PrintWriter out = response.getWriter();;
        try {
          out.println("false||" + "Neočekávaná chyba, prosím kontaktuje administrátora.");
        } finally {out.close();}
      }
      else {
        request.setAttribute("error", "Neočekávaná chyba, prosím kontaktuje administrátora.");
        request.getRequestDispatcher("/indexcfg.jsp").forward(request, response);
      }
      log.error("Comparison failed: " + t.getMessage(), t);
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
    return "Servlet comparing students results against application result.";
  }
}
