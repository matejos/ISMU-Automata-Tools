package cz.muni.fi.cfg.servlet;

import cz.muni.fi.admin.ServicesController;
import cz.muni.fi.admin.ServicesController.TransformationType;
import cz.muni.fi.cfg.conversions.CFGConvertor;
import cz.muni.fi.cfg.conversions.Modes;
import cz.muni.fi.cfg.conversions.TransformationTypes;
import cz.muni.fi.cfg.grammar.ContextFreeGrammar;
import cz.muni.fi.cfg.parser.Parser;
import cz.muni.fi.cfg.parser.ParserException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author NICKT
 */
public class Convert extends HttpServlet {

  private static Logger log = Logger.getLogger(Convert.class);
  
  private static Modes getMode(HttpServletRequest request) {
    Modes mode = Modes.valueOf(request.getParameter("mode"));
    log.debug("Mode: " + mode);
    return mode;
  }  
  
  private static ContextFreeGrammar getCfg(HttpServletRequest request) throws ParserException {
    ContextFreeGrammar cfg = Parser.parse(request.getParameter("inputData"));
    log.debug("CFG: " + cfg);
    return cfg;
  }  
  
  private static List<String> getOrdering(HttpServletRequest request) throws ParserException {
    List<String> ordering = Parser.orderingOfNonTerminals(request.getParameter("inputData"));
    log.debug("Ordering: " + ordering);
    return ordering;
  }
  
  private static TransformationTypes getTransformation(HttpServletRequest request) throws ParserException {
    if (request.getParameter("stud") == null) {
      throw new ParserException("Nebyla zvolená operace.");
    }
    TransformationTypes transformation = TransformationTypes.valueOf(request.getParameter("stud"));
    log.debug("Transformation type: " + transformation);
    return transformation;
  }

  private static void processRequest(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException, ParserException, IllegalAccessException {

    // set encoding
    request.setCharacterEncoding("utf-8");
    response.setContentType("text/html;charset=UTF-8");
    
    // get data
    Modes mode = getMode(request);
    ContextFreeGrammar cfg = getCfg(request);
    List<String> ordering = getOrdering(request);
    TransformationTypes transformation = getTransformation(request);
        
    if (!ServicesController.instance().isAllowed(TransformationType.fromTransformationTypes(transformation))) {
      throw new IllegalAccessException("Operace " + transformation + " není momentálně povolena");
    }
    
    if (Boolean.parseBoolean(request.getParameter("generateISString")))  {
      // b:CFG-NE1:A->Aa'|Bb'|b'a', <Ab'>->Ab', B->Aa'|B<Ab'>|b'a', a'->a, b'->b
      String cfgString = cfg.toString().replaceAll("[ \\n\\r]", "").replace(",", ", ");
      request.setAttribute("ISString", "f:CFG-" + transformation.toString() + ":" + cfgString);
    }
    
    Map<String, String> resultMap = CFGConvertor.convert(cfg, transformation, ordering, mode);
    for (Map.Entry<String, String> entry : resultMap.entrySet()) {
      entry.setValue(entry.getValue().replaceAll("(,\\n|,\\r\\n)", "\r\n"));
    }

    request.setAttribute("windowData", resultMap);
    request.setAttribute("inputData", request.getParameter("inputData"));
    
    String dispatcher = (transformation.equals(TransformationTypes.ANA)) ? "/resultanalysis.jsp" : "/resultconversion.jsp";

    request.getRequestDispatcher(dispatcher).forward(request, response);
  }

  private static void processAndRedirect(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
    try {
      processRequest(request, response);
      log.info("Conversion successful");
    } catch (IllegalAccessException e) {
      log.info("Comparison failed: " + e.getMessage());
      request.setAttribute("error", e.getMessage());
      request.getRequestDispatcher("/indexcfg.jsp").forward(request, response);
    } catch (ParserException e) {
      log.info("Conversion failed: incorrect input data", e);
      request.setAttribute("error", "Neplatné vstupní údaje");
      request.getRequestDispatcher("/indexcfg.jsp").forward(request, response);
    } catch (Throwable t) {
      log.error("Conversion failed: " + t.getMessage(), t);
      request.setAttribute("error", "Neočekávaná chyba, prosím kontaktuje administrátora.");
      request.getRequestDispatcher("/indexcfg.jsp").forward(request, response);
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
    return "Servlet for converting Context-free grammar";
  }
}
