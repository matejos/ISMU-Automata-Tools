/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.muni.fi.xpastirc.fja.servlet;


import cz.muni.fi.RegularLanguage.Automaton.DeterministicFA;
import cz.muni.fi.RegularLanguage.Exceptions.RegLanguageException;
import cz.muni.fi.fja.parser.AutomatonToTable;
import cz.muni.fi.xpastirc.db.DBHandler;
import cz.muni.fi.xpastirc.db.MySQLHandler;
import cz.muni.fi.xpastirc.fawebinterface.comparing.*;
import cz.muni.fi.xpastirc.parsers.PreParser;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.TreeSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author fafner, Adrian Elgyutt
 */
@WebServlet(name="Equal", urlPatterns={"/Equal"})
public class Equal extends HttpServlet {
   
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected static void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            String mode = request.getParameter("mod");
            if (mode==null) mode="tf";
            String formalism_teach = request.getParameter("teach");
            String input_teach = PreParser.parse(request.getParameter("t"));
            String formalism_stud = request.getParameter("stud");
            String input_stud = PreParser.parse(request.getParameter("s"));
            boolean tab = Boolean.parseBoolean(request.getParameter("intable"));
            boolean testIso = Boolean.parseBoolean(request.getParameter("iso"));
            if (!mode.equals("tf"))
                printHeader(out, request);
            try{
                if(request.getParameter("teach")==null){
                    String[] externalData = exparse(request.getParameter("t"));
                    formalism_teach = externalData[0];
                    input_teach = externalData[3];
                    formalism_stud = externalData[1];
                    if ("Y".equals(externalData[2]))
                        testIso = true;
                }
            }catch(Exception e){
                out.println("Vstupní formalismus nebyl správně zadán: nebyl zvolen formalismus zadání.");
                printFooter(out);
                return;
            }
            LanguageInformation information_teach;
            LanguageInformation information_stud;
            LanguageInformation teachNotStud;
            LanguageInformation studNotTeach;
            LanguageInformation intersection;
            LanguageInformation complement;
            int mod = (mode.equals("tf")?1:
                      (mode.equals("verbose")?2
                      :0));
            long key = -10;
            DBHandler h = null;
            try {
                h = MySQLHandler.getHandler();
                key = h.logEqual(mod, input_teach, formalism_teach, input_stud, formalism_stud, request.getRemoteAddr());
            } catch (ClassNotFoundException ex) {
                if (!mode.equals("tf"))
                    out.println("Chyba: Nemám ovládač databáze<br>");
            } catch (SQLException ex) {
                //if (!mode.equals("tf"))
                    //out.println("Chyba: špatný dotaz db: " + ex.getMessage() + "<br>");
            }
    
                if (mode.equals("tf")){
                    //zjednoduseny vypis pro TF mod
                    /*if (key ==0 || key == -1){
                        out.println(key==0?"true":"false");
                        return;
                    }*/
                    /*start*/
                    int epsc = 0;
                    boolean isomorphic = false;
                    try{
                        information_teach = ComplexLanguageInformation.getLanguageInformation(formalism_teach, input_teach);
                        try{
                            information_stud = ComplexLanguageInformation.getLanguageInformation(formalism_stud, input_stud);}
                            catch (ENFAException e){
                                try{
                                information_stud = ComplexLanguageInformation.getLanguageInformation("EFA", input_stud);
                                epsc = information_stud.getEpscount();
                                } catch (Exception ex){
                                    out.println("false||Vstupní formalismus nebyl správně zadán. ||0%");
                                    return;
                                }
                            }
                        if (testIso) {
                            isomorphic = AutomatonIsomorphismChecker.areIsomorphic(information_teach.toDFA(),information_stud.toDFA());
                        }
                        if (!(formalism_teach.equals("DFA") || (formalism_teach.equals("MIC"))|| formalism_teach.equals("MIN")
                                || formalism_teach.equals("CAN") || formalism_teach.equals("TOT"))){
                            information_teach.toDFA().kanonize();
                            information_teach.toDFA().removeIrelevantStates();
                        }
                        TreeSet<Character> alphabet = new TreeSet<Character>();
                        for (Character c : information_teach.toDFA().getAlphabet())
                            alphabet.add(c);
                        for (Character c : information_stud.toDFA().getAlphabet())
                            alphabet.add(c);
                        for (Character c : alphabet){
                            information_stud.toDFA().addSymbol(c);
                            information_teach.toDFA().addSymbol(c);
                        }
                        teachNotStud = information_teach.aNotB(information_stud);
                        teachNotStud.toDFA().removeIrelevantStates();
                        studNotTeach = information_stud.aNotB(information_teach);
                        studNotTeach.toDFA().removeIrelevantStates();
                        
                    } catch (RegLanguageException ex) {
                        out.println("false||Vstupní formalismus nebyl správně zadán: " + ex.getMessage());
                        if ((formalism_stud.equals("DFA") || (formalism_stud.equals("MIC"))|| formalism_stud.equals("MIN")
                                || formalism_stud.equals("CAN") || formalism_stud.equals("TOT"))) out.println("Zkontrolujte, zda je automat deterministický. ");
                        out.println("||0%");
                        return;
                    }
                      catch (Exception ex){
                        out.println("false||Vstupní formalismus nebyl správně zadán. ||0%");
                        return;
                    }
                    //equals
                    boolean eq = information_stud.isEqual(information_teach);
                    int inclusion = information_teach.includes(information_stud);
                    boolean disjoint = information_teach.intersection(information_stud).isEmpty()==1;
                    boolean goodFormalism = AutomatonFormalismChecker.isInFormalism(formalism_stud, information_stud.toDFA(), epsc);
                    if (h!= null){
                        try {
                            h.logEqualAnswer(key, eq && goodFormalism);
                        } catch (SQLException ex) {
                            //pri vypadku DB nic nedelat
                        }
                    }
                    String gFormalism;
                    if (AutomatonFormalismChecker.getFeedback()!=null) gFormalism = AutomatonFormalismChecker.getFeedback();
                    else gFormalism="";
                    information_teach.getCharacteristics();
                    int i=0;
                    StringBuilder ISAnswer = new StringBuilder();
                    int percentage = 0;
                    if (goodFormalism && eq)
                    {
                        if (testIso && !isomorphic) {
                            percentage += 80;
                            ISAnswer.append("false||Nebyl správně vykonán požadovaný algoritmus.\n");
                            ISAnswer.append("Jazyk odpovědi je ekvivalentní se zadáním.\n");
                            ISAnswer.append("Odpověď splňuje požadovaný formalismus.");
                        }
                        else {
                            ISAnswer.append("true");
                        }
                    }
                    else{
                        ISAnswer.append("false||");
                        if(eq){
                            ISAnswer.append("Jazyk odpovědi je ekvivalentní se zadáním.\n");
                            if(("DFA".equals(formalism_teach)&&("DFA".equals(formalism_stud) || "TOT".equals(formalism_stud)
                                    || "MIC".equals(formalism_stud)|| "MIN".equals(formalism_stud)))||
                                    formalism_teach.equals(formalism_stud)) percentage += 40;
                            else percentage +=60;
                            
                        }
                        else if(inclusion==-1){
                            ISAnswer.append("Jazyky nejsou ekvivalentní: Jazyk odpovědi je podmnožinou jazyku zadání.\n");
                            if(!information_stud.getCharacteristics().equals(information_teach.getCharacteristics())){
                                ISAnswer.append("Jazyk odpovědi je ").append(information_stud.getCharacteristics()).append(" zatímco jazyk zadání je ").append(information_teach.getCharacteristics()).append(".\n");
                                percentage -=25;
                            }
                            ISAnswer.append("Jazyky se liší o ").append(teachNotStud.getCharacteristics()).append(" počet slov.\n");
                            if("konečný".equals(teachNotStud.getCharacteristics())){
                                percentage += 10;
                            }
                            ISAnswer.append("Příklady slov z jazyka řešení, které nejsou v odpovědi: ").append(teachNotStud.getWords()).append(".\n");
                            percentage += 20;
                        }
                        else if (inclusion == 1){
                            ISAnswer.append("Jazyky nejsou ekvivalentní: Jazyk odpovědi je nadmnožinou jazyku zadání.\n");
                            if(!information_stud.getCharacteristics().equals(information_teach.getCharacteristics())){
                                ISAnswer.append("Jazyk odpovědi je ").append(information_stud.getCharacteristics()).append(" zatímco jazyk zadání je ").append(information_teach.getCharacteristics()).append(".\n");
                                percentage -=25;
                            }
                            ISAnswer.append("Jazyky se liší o ").append(studNotTeach.getCharacteristics()).append(" počet slov.\n");
                            if("konečný".equals(studNotTeach.getCharacteristics())){
                                percentage += 10;
                            }
                            ISAnswer.append("Příklady slov z jazyka odpovědi, které nejsou v řešení: ").append(studNotTeach.getWords()).append(".\n");
                            percentage += 20;
                        }
                        else if (disjoint){
                            ISAnswer.append("Jazyky nejsou ekvivalentní: Jazyky jsou disjunktní.\n");
                            if(!information_stud.getCharacteristics().equals(information_teach.getCharacteristics())){
                                ISAnswer.append("Jazyk odpovědi je ").append(information_stud.getCharacteristics()).append(" zatímco jazyk zadání je ").append(information_teach.getCharacteristics()).append(".\n");
                            }
                            ISAnswer.append("\nPříklad slov z jazyka odpovědi: ").append(information_stud.getWords()).append(".\n");
                            ISAnswer.append("\nPříklad slov z jazyka řešení: ").append(information_teach.getWords()).append(".\n");
                        }else{
                            ISAnswer.append("Jazyk odpovědi není ekvivalentní se zadáním.\n");
                            if(!information_stud.getCharacteristics().equals(information_teach.getCharacteristics())){
                                ISAnswer.append("Jazyk odpovědi je ").append(information_stud.getCharacteristics()).append(" zatímco jazyk zadání je ").append(information_teach.getCharacteristics()).append(".\n");
                            }
                            ISAnswer.append("Příklad slov z jazyka odpovědi, které nejsou v řešení: ").append(studNotTeach.getWords()).append(".\n");
                            ISAnswer.append("Příklad slov z jazyka řešení, ktere nejsou v odpovědi: ").append(teachNotStud.getWords()).append(".\n");
                        }
                        if(goodFormalism){
                            ISAnswer.append("Odpověď splňuje požadovaný formalismus.");
                            percentage += 40;
                        }else{
                            ISAnswer.append("Odpověď nesplňuje požadovaný formalismus: ");
                            if("DFA".equals(formalism_stud) || "TOT".equals(formalism_stud)
                                    || "MIC".equals(formalism_stud)|| "MIN".equals(formalism_stud)) ISAnswer.append("automat");
                            ISAnswer.append(gFormalism);
                            ISAnswer.deleteCharAt(ISAnswer.length()-1);
                            ISAnswer.append(".");
                            percentage += AutomatonFormalismChecker.getFeedbackVal();
                        }
                        //ISAnswer.append("||").append(percentage).append("%");
                     }
                    out.println(ISAnswer.toString());
                    /*end*/
                    return;
                }
            boolean isomorphic = false;
            try{
                information_teach = ComplexLanguageInformation.getLanguageInformation(formalism_teach, input_teach);
                information_stud = ComplexLanguageInformation.getLanguageInformation(formalism_stud, input_stud);
                if (testIso) {
                    isomorphic = AutomatonIsomorphismChecker.areIsomorphic(information_teach.toDFA(),information_stud.toDFA());
                }
                if (!(formalism_teach.equals("DFA") || (formalism_teach.equals("MIC"))|| formalism_teach.equals("MIN")
                        || formalism_teach.equals("CAN") || formalism_teach.equals("TOT"))){
                    information_teach.toDFA().kanonize();
                    information_teach.toDFA().removeIrelevantStates();
                    //information_teach.toDFA().makeTotal();
                }
                if (!(formalism_stud.equals("DFA") || (formalism_stud.equals("MIC"))|| formalism_stud.equals("MIN")
                        || formalism_stud.equals("CAN") || formalism_stud.equals("TOT"))){
                    information_stud.toDFA().kanonize();
                    information_stud.toDFA().removeIrelevantStates();
                }
                TreeSet<Character> alphabet = new TreeSet<Character>();
                for (Character c : information_teach.toDFA().getAlphabet())
                    alphabet.add(c);
                for (Character c : information_stud.toDFA().getAlphabet())
                    alphabet.add(c);
                for (Character c : alphabet){
                    information_stud.toDFA().addSymbol(c);
                    information_teach.toDFA().addSymbol(c);
                }
                //ComplexLanguageInformation information_stud_copy = new ComplexLanguageInformation((ComplexLanguageInformation)information_stud);
                //information_stud_copy.toDFA().kanonize();
                teachNotStud = information_teach.aNotB(information_stud);
                teachNotStud.toDFA().removeIrelevantStates();
                studNotTeach = information_stud.aNotB(information_teach);
                studNotTeach.toDFA().removeIrelevantStates();
                intersection = information_teach.intersection(information_stud);
                intersection.toDFA().removeIrelevantStates();
                complement = information_teach.union(information_stud).complement();
                complement.toDFA().removeIrelevantStates();
            } catch (RegLanguageException ex) {
                out.println("Vstupní formalismus nebyl správně zadán: " + ex.getMessage());
                printFooter(out);
                return;
            }
              catch (Exception ex){
                out.println("Vstupní formalismus nebyl správně zadán. ");
                printFooter(out);
                return;
            }
            int inclusion = information_teach.includes(information_stud);
            //equals
            boolean eq = information_stud.isEqual(information_teach);
            boolean goodFormalism = AutomatonFormalismChecker.isInFormalism(formalism_stud, information_stud.toDFA(), 0);
            if (h!= null){
                try {
                    h.logEqualAnswer(key, eq && goodFormalism);
                } catch (SQLException ex) {

                    //pri vypadku DB nic nedelat
                }
            }

            out.println("<center>");
            String eqString;
            String eqImage;
            boolean disjoint = information_teach.intersection(information_stud).isEmpty()==1;

            if (goodFormalism && eq) {
                if (testIso && !isomorphic) {
                    out.println("<h1>Celkový výsledek: FALSE</h1>");
                    out.println("<span class=\"error\">Nebyl správně vykonán požadovaný algoritmus.</span><br>");
                }
                else {
                    out.println("<h1>Celkový výsledek: TRUE</h1>");
                }
            }
            else
                out.println("<h1>Celkový výsledek: FALSE</h1>");
            out.println(goodFormalism?"Odpověď splňuje požadovaný formalismus":"<span class=\"error\">Odpověď nesplňuje požadovaný formalismus</span>");
            out.println("<br>");
            //DEFINICE toho co se bude vypisovat v dalsim radku
            if (eq){
                eqString=("Jazyky jsou ekvivalentní");
                eqImage=("<img src=\"style/equal.png\" alt=\"TRUE\"></img>");
            }
            else if(inclusion==-1){
                eqString=("<span class=\"error\">Jazyky nejsou ekvivalentní: Jazyková inkluze S\u2282T </span>");
                eqImage=("<img src=\"style/inclusiontins.png\" alt=\"FALSE\"></img>");
            }
            else if (inclusion == 1){
                eqString=("<span class=\"error\">Jazyky nejsou ekvivalentní: Jazyková inkluze T\u2282S</span>");
                eqImage=("<img src=\"style/inclusionsint.png\" alt=\"FALSE\"></img>");
            }
            else if (disjoint){
                eqString=("<span class=\"error\">Jazyky nejsou ekvivalentní: Jazyky jsou disjunktní</span>");
                eqImage=("<img src=\"style/disjoint.png\">");
            }
            else{
                eqString=("<span class=\"error\">Jazyky nejsou ekvivalentní</span>");
                eqImage=("<img src=\"style/other.png\">");
            }
            out.println(eqString);
            out.println("</center>");
            out.println("<div class=\"leftTable\" style=\"display:block;word-wrap:break-word;\">");
            out.println("<center><h2 class=\"transformTitle\">Jazyk zadání = T</h2></center><br/>");
            out.println("<pre class=model>");
            out.println("<span class=\"arText\">Charakteristika</span><br/>"+information_teach.getCharacteristics()+"<br/>");
            System.out.println(information_teach.isFinal());
            int i=0;
            out.println("<span class=\"arText\">Slova z jazyka</span>");
            if (information_teach.isEmpty()!=1){

                for (String word : information_teach.getWords()){
                       out.print((i==0?"<b>":",<b>") + (word.equals("")?"\u025b":word) + "</b>" );
                       i++;
                }
            }
            out.println( "<br/><br/><span class=\"arText\">Původní popis</span><br/>"+formalism_teach +":<br/><span class=\"automaton\">"
                    + input_teach+"</span><br/><br/>");
            out.println("<span class=\"arText\">Popis pomocí minimálního DFA</span>");
            out.println("<span class=\"automaton\">");
            DeterministicFA teachInDFA = information_teach.toDFA();
            teachInDFA.minimize();
            teachInDFA.kanonize();
            if (tab) out.println(new AutomatonToTable(information_teach.toDFA().toString()).toString());
            else out.println(information_teach.toDFA().toString());
            out.println("</span>");
            out.println("</pre></div>");
            out.println("<div class=\"rightTable\" style=\"display:block;word-wrap:break-word;\">");
            out.println("<center><h2 class=\"transformTitle\">Jazyk odpovědi = S</h2></center><br/>");
            out.println("<pre class=model>");
            out.println("<span class=\"arText\">Charakteristika</span><br/>"+information_stud.getCharacteristics()+"<br/>");
            System.out.println(information_stud.isFinal());
            out.println("<span class=\"arText\">Slova z jazyka</span>");
            if (information_stud.isEmpty()!=1){
                i=0;
                for (String word : information_stud.getWords()){
                        out.print((i==0?"<b>":",<b>") + (word.equals("")?"\u025b":word) + "</b>" );
                        i++;
                }
            }
            DeterministicFA studInDFA = information_stud.toDFA();
            out.println( "<br/><br/><span class=\"arText\">Původní popis</span><br/>"+formalism_stud +":<br/><span class=\"automaton\">"
                    + input_stud+"</span><br/><br/>");
            out.println("<span class=\"arText\">Popis pomocí minimálního DFA</span>");
            out.println("<span class=\"automaton\">");
            studInDFA.minimize();
            studInDFA.kanonize();
            if (tab) out.println(new AutomatonToTable(studInDFA.toString()).toString());
            else out.println(studInDFA.toString());
            out.println("</span></td></tr>");
            out.println("</div>");
            out.println("<br/><div style=\"margin-left:45px;float:left;>");
                //odpovednik
                out.println("<hr width=\"100%\">");
                out.println("<h2 class=\"transformTitle\">Vygenerovaný řetězec pro odpovědník</h2><br/>");
                out.println("<div style=\"width:1110px;display:block;word-wrap:break-word;\"><pre class=model>"
                        + "<span class=\"arText\">"
                        + "b:DFA-"+formalism_teach+":"+HTMLEscaper.removeWhiteSpace(information_teach.toDFA().toString())
                        + "</span></pre></div>");
                out.println("<h2 class=\"transformTitle\">Vztahy mezi jazyky</h2><br/>");
                out.println("<div>\n" +"<p style=\"float: left;\">"+ eqImage +"</p>" +"<p>");
                out.println("<table class=\"output\"><tr class=\"bg4\">"
                        + "<td width=\"140px\"><b>Třídy popisující jazyk<b></td>"
                        + "<td width=\"100px\"><b>Charakteristika<b></td>"
                        + "<td width=\"100px\"><b>Slova z jazyka<b></td>"
                        +"<td width=\"500px\"><b>Automat popisující jazyk<b></td>");
                int conum=0;
                if (eq){
                    conum=2;
                //jen doplnek - na konci
                }
                else if (inclusion==-1){
                    out.println("<tr class=\"bg1\"><td><b>1</b></td>");
                    teachNotStud.printInformation(out, tab, 1);
                    conum=3;
                }
                else if (inclusion==1){
                    out.println("<tr class=\"bg2\"><td><b>2</b></td>");
                    studNotTeach.printInformation(out, tab, 2);
                    conum=3;
                }
                else if (disjoint){
                    conum=3;
                }
                else{
                    out.println("<tr class=\"bg1\"><td><b>1</b></td>");
                    teachNotStud.printInformation(out, tab, 1);
                    out.println("<tr class=\"bg3\"><td><b>2</b></td>");
                    intersection.printInformation(out, tab, 2);
                    out.println("<tr class=\"bg2\"><td><b>3</b></td>");
                    studNotTeach.printInformation(out, tab, 3);
                    conum=4;
                }
                out.println("<tr style=\"background-color: #ffffff;\"><td><b>"+conum+"</b></td>");
                complement.printInformation(out, tab, 4);
                
                out.println("</table></p></div></div>");
                printFooter(out);
        } finally { 
            out.close();
        }
    }
    
    private static String[] exparse(String exData){
        String teacherType = exData.substring(0, 3);
        String studentType = exData.substring(4, 7);
        String isomorphism = exData.substring(8, 9);
        String teacherData;
        if ("Y".equals(isomorphism) || "N".equals(isomorphism)) {
            teacherData = exData.substring(10);
        }
        else {
            isomorphism = "N";
            teacherData = exData.substring(8);
        }
        String[] returnArray = {teacherType, studentType, isomorphism, teacherData};
        return returnArray;
    }
    
    private static void printHeader(PrintWriter out, HttpServletRequest request){
            out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Výsledek převodu jazyka</title>");
            out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"style/style_reg.css\">");
            out.println("<script type=\"text/javascript\" language=\"Javascript\" src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.4.4/jquery.min.js\"></script>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div class=\"header\">");
            out.println("<div class=\"topLine\">");
            out.println("</div>");
            out.println("<div class=\"headerAuthor\">");
            out.println("</div>");
            out.println("<div class=\"menuLine\">");
            out.println("<div class=\"innerMenu\">");
            out.println("<ul class=\"menuServices\">");
            out.println("<li><a class=\"current\" href=\"./index.jsp\" title=\"Regulární jazyky\">Regulární jazyky</a></li>");
            out.println("<li><a href=\"./indexcfg.jsp\" title=\"Bezkontextové gramatiky\">Bezkontextové gramatiky</a></li>");
            out.println("</ul>");
            out.println("<ul class=\"menu\">");
            HttpSession session = request.getSession(false);
            if (session != null) {
                  if ((session.getAttribute("Login") != null)){
                  out.println("<li>P&#345;ihlá&#353;en jako \"" + session.getAttribute("Login") + "\"</li>");
                  String contextP = request.getContextPath();
                  out.println("<li><a href=\""+ contextP +"/Logout\">Odhlásit</a></li>");    
                  }
            }
            out.println("<li><a href=\"./admin.jsp\" title=\"Nastavení\">Nastavení</a></li>");
            out.println("<li><a href=\"./help.jsp\" title=\"Nápověda\">Nápověda</a></li>");
            out.println("<li><a href=\"./author.jsp\" title=\"O aplikaci\">O aplikaci</a></li>");
            out.println("</ul>");
            out.println("</div>");
            out.println("</div>");
            out.println("</div>");
            out.println("<div class=\"page\">");
            out.println("<div class=\"content\">");
            out.println("<div class=\"window2\">");
    }

    private static void printFooter(PrintWriter out){
            out.println("");
            out.println("<script>jQuery(document).ready(function(){\n" +
                "    jQuery('#hideshow1').live('click', function(event) {        \n" +
                "         jQuery('#aut1').toggle('hide');\n" +
                "    });\n" +
                "    jQuery('#hideshow2').live('click', function(event) {        \n" +
                "         jQuery('#aut2').toggle('hide');\n" +
                "    });\n" +
                "    jQuery('#hideshow3').live('click', function(event) {        \n" +
                "         jQuery('#aut3').toggle('hide');\n" +
                "    });\n" +
                "    jQuery('#hideshow4').live('click', function(event) {        \n" +
                "         jQuery('#aut4').toggle('hide');\n" +
                "    });\n" +
                "});</script>");
            out.println("</div>");
            out.println("</div>");
            out.println("</div>");
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
        return "Servlet comparing students results against application result.";
    }// </editor-fold>

}
