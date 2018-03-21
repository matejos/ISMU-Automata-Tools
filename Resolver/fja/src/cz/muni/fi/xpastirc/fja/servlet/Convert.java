package cz.muni.fi.xpastirc.fja.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cz.muni.fi.fja.Centre;
import javax.servlet.http.HttpSession;

/**
 * This servlet converts entered model to the chosen model.
 * 
 * All possible parameters:
 * "mod" - (=verbose, details, simple). Optional parameter if "mod" is 
 *         empty then default value is "simple".
 *         If "mod" is simple then is returned only "true"/"false".
 * "gen" - (=yes). Optional parameter if "gen" returns "yes" then generate 
 *         teacher's string.
 * "t" - Mandatory parameter. This parameter has to contain teacher's model
 *       and can contain task definition.
 * "teach" - Optional parameter. When the request is comming from web interface
 *           then this parameter can contain type of teacher's model.
 *           Otherwise this should be defined in parameter "t".  
 * "stud" - Optional parameter. When the request is comming from web interface
 *          then this parameter can contain type of requested student's model.
 *          Otherwise this should be defined in parameter "t".
 *           
 * @author Bronek
 */
public class Convert extends HttpServlet {

  /*****************************************************************
   *                                                               *
   *   Public                                                      *
   *                                                               *
   *****************************************************************/

  /**
   * Returns a short description of the servlet.
   */
  public String getServletInfo() {
    return "Convert entered device";
  }
  
  /*****************************************************************
   *                                                               *
   *   Proteceted                                                  *
   *                                                               *
   *****************************************************************/
  
  protected void processRequest(HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException {
    String mod = request.getParameter("mod");
    boolean generateQuestion = Boolean.parseBoolean(request.getParameter("gen"));
    boolean verbose = false;
    boolean details = false;
    if (mod != null) {
      if (mod.equals("verbose")) {
        verbose = true;
      } else if (mod.equals("details")) {
        verbose = true;
        details = true;
      }
    }

    String model = request.getParameter("t");
    String modelInfo = request.getParameter("teach");
    String convertInfo = request.getParameter("stud");
    boolean tab = Boolean.parseBoolean(request.getParameter("intable"));
    Centre c = new Centre(verbose, details);
    c.convertDevice(modelInfo, model, convertInfo, tab);

    PrintWriter out = response.getWriter();
    try {
      if (!verbose) {
        out.println(c);
      } else {
          response.setContentType("text/html;charset=UTF-8");
          out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
          out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"cs\" lang=\"cs\">");
          out.println("<head>");
          out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
          out.println("<meta http-equiv=\"Content-Language\" content=\"cs\" />");
          out.println("<title>P&#345;evod</title>");
          out.println("<link rel='stylesheet' type='text/css' href='style/style_reg.css'>");
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
          out.println("<li><a href=\"./help.jsp\" title=\"Nápověda\">Nápov&#283;da</a></li>");
          out.println("<li><a href=\"./author.jsp\" title=\"O aplikaci\">O aplikaci</a></li>");
          out.println("</ul>");
          out.println("</div>");
          out.println("</div>");
          out.println("</div>");
          out.println("<div class=\"page\">");
          out.println("<div class=\"content\">");
          out.println("<div class=\"window2\">");
        if (generateQuestion) {
            out.println("<div style=\"position:relative;left:15px;\">");
          out.println("<h2 class=\"transformationTitle\">Vygenerovaný &#345;et&#283;zec pro odpov&#283;dník:</h2>");
          out.println(c.getQuestion() + "<br/>");
          out.println("</div>");
          out.println("<hr>");
        }
        out.println(c);
        out.println("</div></div></div></body>");
        out.println("</html>");
      }
    } finally {
      out.close();
    }

  }

  /**
   * Handles the HTTP <code>GET</code> method.
   * 
   * @param request
   *          servlet request
   * @param response
   *          servlet response
   */
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    processRequest(request, response);
  }

  /**
   * Handles the HTTP <code>POST</code> method.
   * 
   * @param request
   *          servlet request
   * @param response
   *          servlet response
   */
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    processRequest(request, response);
  }

  /*****************************************************************
   *                                                               *
   *   Private                                                     *
   *                                                               *
   *****************************************************************/

  private static final long serialVersionUID = 8476050747795050901L;

}
