/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.muni.fi.xpastirc.fja.servlet;

import cz.muni.fi.RegularLanguage.Automaton.AutomatonRegExpresionConvertor;
import cz.muni.fi.RegularLanguage.Automaton.DeterministicFA;
import cz.muni.fi.RegularLanguage.Automaton.ENondeterministicFA;
import cz.muni.fi.RegularLanguage.Exceptions.RegLanguageException;
import cz.muni.fi.RegularLanguage.Grammar.RegGrammar;
import cz.muni.fi.RegularLanguage.Parser;
import cz.muni.fi.xpastirc.fawebinterface.comparing.ComplexLanguageInformation;
import cz.muni.fi.xpastirc.fawebinterface.comparing.LanguageInformation;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author fafner
 */
public class Convertx extends HttpServlet {
   
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Výsledek převodu jazyka</title>");
            out.println("</head>");
            out.println("<body>");
            LanguageInformation information;
            LanguageInformation coinf;
            String formalism = request.getParameter("teach");
            String input = request.getParameter("convert");
            String to= request.getParameter("stud");
            try {
                information = ComplexLanguageInformation.getLanguageInformation(formalism, input);
            } catch (Exception ex) {
                out.println("Vstupní formalismus nebyl správně zadán");
                printFooter(out);
                return;
            }

            out.println("<table width=\"100%\"><tr><td width=\"200px\"><b>Zadání a odpověď</b></td><td>Charakteristika</td><td>Slova z jazyka</td>"
                    + "<td width=\"30%\"><b>Původní popis</b></td><td><b>Výsledný převod na </b></td></tr>");
            out.println("<tr><td><b>T</b> = Jazyk zadání</td>");
            out.println("<td>"+information.getCharacteristics()+"</td><td>");
            String toPrint;
            //nastavovani toPrint
            //if (to.equals(""))
            int i=0;
            for (String word : information.getWords()){
                    out.print((i==0?"<b>":",<b>") + (word.equals("")?"\u025b":word) + "</b>" );
                    i++;
            }
           out.println( "</td><td>"+formalism +":<br><span class=\"automaton\">"
                    + input+"</span></td><td><span class=\"automaton\""
                    + HTMLEscaper.escapeHTML(information.toDFA().toString())+"</span></td></tr>");

            
        } finally { 
            out.close();
        }
    } 
    private void printFooter(PrintWriter out){
            out.println("");
            out.println("</body>");
            out.println("</html>");
            out.close();
    }
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
