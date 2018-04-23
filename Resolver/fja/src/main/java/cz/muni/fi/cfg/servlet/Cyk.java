package cz.muni.fi.cfg.servlet;

import cz.muni.fi.admin.ServicesController;
import cz.muni.fi.admin.ServicesController.ConversionType;
import cz.muni.fi.cfg.forms.Analyser;
import cz.muni.fi.cfg.grammar.ContextFreeGrammar;
import cz.muni.fi.cfg.parser.Parser;
import cz.muni.fi.cfg.parser.ParserException;
import java.io.IOException;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 * @author Daniel Pelisek <dpelisek@gmail.com>
 * @version 1.0
 * @since 2011-04-10
 */
public class Cyk extends HttpServlet {

  // TODO: simple mode
  private static Logger log = Logger.getLogger(Cyk.class);

  private static String getWord(HttpServletRequest request) throws ParserException {

    String word = request.getParameter("word");

    if (word == null || word.equals("")) {
      throw new ParserException("Nebylo zadáno žádné slovo.");
    }

    log.debug("Word: " + word);

    return word;
  }

  private static ContextFreeGrammar getCfg(HttpServletRequest request) throws ParserException {

    String cfgString = request.getParameter("generateData");
    ContextFreeGrammar cfg = Parser.parse(cfgString);

    StringBuilder reason = new StringBuilder();
    if (!new Analyser().isInCNF(cfg, reason)) {
      throw new ParserException("Zadaná gramatika není v CNF. " + reason);
    }

    log.debug("CFG: " + cfgString);

    return cfg;
  }

  private static List<List<Set<String>>> getTable(HttpServletRequest request, int size) {

    List<List<Set<String>>> table = new ArrayList<List<Set<String>>>();

    for (int i = 0; i < size; i++) {

      table.add(new ArrayList<Set<String>>());

      for (int j = 0; j < size - i; j++) {

        String cell = request.getParameter("t" + i + "-" + (10 - size + j));
        cell = cell.replace(" ", "");

        Set<String> cellSet = new HashSet<String>(Arrays.asList(cell.split(",")));

        if (cell.isEmpty()) {
          cellSet.clear();
        }

        table.get(i).add(cellSet);
      }
    }

    log.debug("Table: " + table);

    return table;
  }

  /**
   * Processes requests for both HTTP GET and POST methods.
   *
   * Get inputs from HTTP request, calculate the CYK on given CFG and eventually fill in request with new attributes.
   */
  private static void processRequest(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException, ParserException, IllegalAccessException {
    
    if (!ServicesController.instance().isAllowed(ConversionType.CYK)) {
      throw new IllegalAccessException("Operace C-Y-K není momentálně povolena");
    }

    // set encoding
    request.setCharacterEncoding("utf-8");
    response.setContentType("text/html;charset=UTF-8");

    // get data
    String word = getWord(request);
    ContextFreeGrammar cfg = getCfg(request);
    List<List<Set<String>>> studentTable = getTable(request, word.length());
    List<List<Set<String>>> teacherTable = new Analyser().cyk(cfg, word);

    if (Boolean.parseBoolean(request.getParameter("cykISString"))) {
      // b:CFG-CYK:aabb:A->Aa'|Bb'|b'a', <Ab'>->Ab', B->Aa'|B<Ab'>|b'a', a'->a, b'->b
      String cfgString = cfg.toString().replaceAll("[ \\n\\r]", "").replace(",", ", ");
      request.setAttribute("ISString", "f:CFG-CYK:" + word + ":" + cfgString);
    }

    request.setAttribute("studentTable", studentTable);
    request.setAttribute("teacherTable", teacherTable);
  }

  private static void processAndRedirect(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
    try {
      processRequest(request, response);
      request.getRequestDispatcher("/resultgenerate.jsp").forward(request, response);
      log.info("Comparison successful");
    } catch (ParserException e) {
      log.info("Comparison failed: " + e.getMessage());
      request.setAttribute("error3", e.getMessage());
      request.getRequestDispatcher("/indexcfg.jsp").forward(request, response);
    }  catch (IllegalAccessException e) {
      log.info("Comparison failed: " + e.getMessage());
      request.setAttribute("error3", e.getMessage());
      request.getRequestDispatcher("/indexcfg.jsp").forward(request, response);
    }
    catch (Throwable t) {
      log.error("Comparison failed: " + t.getMessage(), t);
      request.setAttribute("error3", "Neočekávaná chyba, prosím kontaktuje administrátora.");
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
    return "Servlet applying CYK algorithm on given word and given CFG in CNF.";
  }
}
