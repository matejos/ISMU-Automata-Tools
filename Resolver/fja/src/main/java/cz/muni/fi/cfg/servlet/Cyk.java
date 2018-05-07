package cz.muni.fi.cfg.servlet;

import com.sun.org.apache.xpath.internal.operations.Bool;
import cz.muni.fi.admin.ServicesController;
import cz.muni.fi.admin.ServicesController.ConversionType;
import cz.muni.fi.cfg.forms.Analyser;
import cz.muni.fi.cfg.grammar.ContextFreeGrammar;
import cz.muni.fi.cfg.parser.Parser;
import cz.muni.fi.cfg.parser.ParserException;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cz.muni.fi.xpastirc.db.DBHandler;
import cz.muni.fi.xpastirc.db.MySQLHandler;
import org.apache.log4j.Logger;

/**
 * @author Daniel Pelisek <dpelisek@gmail.com>
 * @version 1.0
 * @since 2011-04-10
 */
public class Cyk extends HttpServlet {

    // TODO: simple mode
    private static Logger log = Logger.getLogger(Cyk.class);

    private static String getWord(HttpServletRequest request, String mode) throws ParserException {

        String word = request.getParameter("word");

        if (word == null || word.equals("")) {
            throw new ParserException("Nebylo zadáno žádné slovo.");
        }

        log.debug("Word: " + word);

        return word;
    }

    private static ContextFreeGrammar getCfg(String cfgString) throws ParserException {

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

    private static List<List<Set<String>>> getTableFromIS(HttpServletRequest request, int size) {

        HashMap<String, String> map = new HashMap<String, String>();
        String data = request.getParameter("s");
        String[] dataTokens = data.split(" ");
        for (int i = 0; i < dataTokens.length; i++) {
            String token = dataTokens[i];
            String paramName = token.substring(0, token.indexOf('='));
            String value = token.substring(token.indexOf('=')+1);
            map.put(paramName, value);
        }

        List<List<Set<String>>> table = new ArrayList<List<Set<String>>>();

        for (int i = 0; i < size; i++) {

            table.add(new ArrayList<Set<String>>());

            for (int j = 0; j < size - i; j++) {

                String cell = map.get("t" + i + "-" + (10 - size + j));
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
     * <p>
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

        String mode = request.getParameter("mode");
        if (mode == null) mode = "tf";
        Boolean inputIS = (mode.equals("tf") && request.getParameter("s") != null);

        // get data
        String word;
        ContextFreeGrammar cfg;
        List<List<Set<String>>> studentTable;

        String tlog = "";
        String slog = "";
        if (inputIS) {
            String t = request.getParameter("t");
            t = t.substring(t.indexOf(':') + 1);
            tlog = t;
            slog = request.getParameter("s");
            word = t.substring(0, t.indexOf(':'));
            cfg = getCfg(t.substring(t.indexOf(':') + 1));
            studentTable = getTableFromIS(request, word.length());
        }
        else {
            word = getWord(request, mode);
            tlog = word + ":" + request.getParameter("t");
            cfg = getCfg(request.getParameter("t"));
            studentTable = getTable(request, word.length());
            slog = studentTable.toString();
        }

        List<List<Set<String>>> teacherTable = new Analyser().cyk(cfg, word);

        int mod = (mode.equals("tf")?1:
                (mode.equals("verbose")?2
                        :0));
        long key = -10;
        DBHandler h = null;
        try {
            h = MySQLHandler.getHandler();
            key = h.logEqual(mod, tlog, "CFG", slog, "CYK", request.getRemoteAddr());
        } catch (ClassNotFoundException ex) {
            if (!mode.equals("tf"))
            {
                log.error("Comparison failed: " + ex.getMessage(), ex);
                //throw new SQLException("Chyba: Nemám ovládač databáze");
            }
        } catch (SQLException ex) {
            if (!mode.equals("tf"))
            {
                log.error("Comparison failed: " + ex.getMessage(), ex);
                //throw new SQLException("Chyba: špatný dotaz db: " + ex.getMessage());
            }
        }

        if (mode.equals("tf")) {
            PrintWriter out = response.getWriter();
            ;
            try {
                boolean result = studentTable.equals(teacherTable);
                String feedback = "feedback for " + word + ":";
                if (!result) {
                    int n = word.length();
                    for (int j = 0; j < n; j++) {
                        for (int i = 0; i < n - j; i++) {
                            boolean equality = studentTable.get(j).get(i).equals(teacherTable.get(j).get(i));
                            if (!equality)
                                feedback += "t" + j + "-" + (10 - n + i) + " ";
                        }
                    }
                }
                if (result)
                    out.println("true");
                else
                    out.println("false||" + feedback);
            } finally {
                out.close();
            }
        } else {
            if (Boolean.parseBoolean(request.getParameter("cykISString"))) {
                // b:CFG-CYK:aabb:A->Aa'|Bb'|b'a', <Ab'>->Ab', B->Aa'|B<Ab'>|b'a', a'->a, b'->b
                String cfgString = cfg.toString().replaceAll("[ \\n\\r]", "").replace(",", ", ");
                request.setAttribute("ISString", "f:CFG-CYK:" + word + ":" + cfgString);
            }

            request.setAttribute("studentTable", studentTable);
            request.setAttribute("teacherTable", teacherTable);
        }
    }

    private static void processAndRedirect(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
            request.getRequestDispatcher("/resultgenerate.jsp").forward(request, response);
            log.info("Comparison successful");
        } catch (ParserException e) {
            log.info("Comparison failed: " + e.getMessage());
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/cyk.jsp").forward(request, response);
        } catch (IllegalAccessException e) {
            log.info("Comparison failed: " + e.getMessage());
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/cyk.jsp").forward(request, response);
        } catch (Throwable t) {
            log.error("Comparison failed: " + t.getMessage(), t);
            request.setAttribute("error", "Neočekávaná chyba, prosím kontaktuje administrátora.");
            request.getRequestDispatcher("/cyk.jsp").forward(request, response);
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
