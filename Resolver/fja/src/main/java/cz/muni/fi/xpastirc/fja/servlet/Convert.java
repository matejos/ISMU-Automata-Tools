package cz.muni.fi.xpastirc.fja.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cz.muni.fi.fja.Centre;
import cz.muni.fi.xpastirc.parsers.PreParser;

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
 * "convert" - Mandatory parameter. This parameter has to contain teacher's model
 *       and can contain task definition.
 * "teach" - Optional parameter. When the request is comming from web interface
 *           then this parameter can contain type of teacher's model.
 *           Otherwise this should be defined in parameter "convert".
 * "stud" - Optional parameter. When the request is comming from web interface
 *          then this parameter can contain type of requested student's model.
 *          Otherwise this should be defined in parameter "convert".
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

    String modelInfo = request.getParameter("teach");
    String convertInfo = request.getParameter("stud");
    String model = request.getParameter("t");
    if (modelInfo.equals("GRA"))
        model = model.replaceAll("\n", ",\n");
    model = PreParser.parse(model);
    boolean tab = Boolean.parseBoolean(request.getParameter("intable"));
    Centre c = new Centre(verbose, details);
    c.convertDevice(modelInfo, model, convertInfo, tab);

    PrintWriter out = response.getWriter();
    try {
      if (!verbose) {
        out.println(c);
      } else {
          HttpSession session = request.getSession();
          Object loginO = session.getAttribute("Login");
          String login = "";
          if (loginO != null)
              login = (String) loginO;
          response.setContentType("text/html;charset=UTF-8");
          out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
          out.println("<html>");
          out.println("<head>");
          out.println("<title>P&#345;evod</title>");
          out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"style/bootstrap.min.css\">");
          out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"style/style_fjamp.css\">");
          out.println("<script type=\"text/javascript\" src=\"js/util.js\"></script>");
          out.println("<script type=\"text/javascript\" src=\"js/jquery.js\"></script>");
          out.println("<script type=\"text/javascript\" src=\"js/bootstrap.min.js\"></script>");
          out.println("</head>");
          out.println("<body>");
          out.println("<script>document.write(printHeader(\"" + login + "\", \"reg\"));</script>");
          out.println("<div class=\"container\">");
          out.println("<div class=\"panel panel-default\">");
          out.println("<div class=\"panel-heading\">P&#345;evod:" + modelInfo + "&#8594;" + convertInfo + "</div>");
          out.println("<div class=\"panel-body\">");
        if (generateQuestion) {
          out.println("<h3 class=\"transformationTitle\">Vygenerovaný &#345;et&#283;zec pro odpov&#283;dník:</h3>");
          out.println("<pre class='whitebg'>");
          out.println(c.getQuestion());
          out.println("</pre>");
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
