<%-- 
    Document   : log
    Created on : 19.5.2011, 23:03:03
    Author     : fafner
--%>

<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="cz.muni.fi.xpastirc.db.DBHandler"%>
<%@page import="cz.muni.fi.xpastirc.db.MySQLHandler"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
 <% if ((session.getAttribute("Login") == null)){
        response.sendRedirect("login.jsp?from=admin");
        return;
    }

    DBHandler handler = MySQLHandler.getHandler();
    int pages = handler.getNumberOfPages();
    String orderBy = request.getParameter("sort");
    if (orderBy==null) orderBy = "teacherF";
    boolean asc = request.getParameter("order")!=null&& request.getParameter("order").equals("asc");
    if (request.getParameter("action")!=null && (request.getParameter("action").equals("clean")))
        handler.clean();
    if (request.getParameter("action")!=null && (request.getParameter("action").equals("delete")))
        handler.empty();
    int p;
    if (request.getParameter("page")!=null)
           p = Integer.parseInt(request.getParameter("page"));
    else
           p = 1;
    List<String> toPrint = handler.getPage(p, orderBy, asc);
    pageContext.setAttribute("toPrint", toPrint);
 %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Výpis logů</title>
        <link rel="stylesheet" type="text/css" href="style/bootstrap.min.css">
        <link rel="stylesheet" type="text/css" href="style/style_fjamp.css">
        <script type="text/javascript" src="js/util.js"></script>
        <script type="text/javascript" src="js/jquery.js"></script>
        <script type="text/javascript" src="js/bootstrap.min.js"></script>
    </head>
    <body>
    <script>
        document.write(printHeader("${sessionScope.Login}", "admin"));
    </script>
    <div class="container">
        <div class="panel panel-default">
            <div class="panel-heading">Výpis logů</div>
            <div class="panel-body">
                <div class="text-center">
                    <div class="btn-group">
                        <a type="button" class="btn btn-primary" href="log.jsp?action=clean" title="">Promazat</a>
                        <a type="button" class="btn btn-primary" href="log.jsp?action=delete" title="Smaže všechny záznamy">Smazat</a>
                    </div>
                </div>
                <ul class="pagination">
                    <%
                        for (int i=1;i<=pages;i++){
                            out.print("<li");
                            out.print(p==i?" class='active'>":">");
                            out.print("<a href=\"log.jsp?sort="+orderBy+"&order="+(asc?"asc":"desc")+"&page="+i+"\">"+i+"</a> ");
                            out.print("</li>");
                        }
                    %>
                </ul>
                <table class="output table" cellspacing="2" cellpadding="1">
                   <tr class="bg4">
                       <td><b>Formalismus učitele</b><a href="log.jsp?sort=teacherF&order=asc">▲</a>
                                              <a href="log.jsp?sort=teacherF&order=desc">▼</a></td>
                       <td><b>Automat učitele</b><a href="log.jsp?sort=teacherA&order=asc">▲</a>
                                              <a href="log.jsp?sort=teacherA&order=desc">▼</a></td>
                       <td><b>Formalismus studenta</b><a href="log.jsp?sort=studentF&order=asc">▲</a>
                                              <a href="log.jsp?sort=studentF&order=desc">▼</a></td>
                       <td><b>Automat studenta</b><a href="log.jsp?sort=studentA&order=asc">▲</a>
                                              <a href="log.jsp?sort=studentA&order=desc">▼</a></td>
                       <td><b>Čas zadání dotazu</b><a href="log.jsp?sort=time_start&order=asc">▲</a>
                                              <a href="log.jsp?sort=time_start&order=desc">▼</a></td>
                       <td><b>Doba dotazu</b></td><td><b>Výsledek</b></td>
                  </tr>
                  <%
                    //c:foreach blbe escapuje znaky... toz jinak
                    for (String s : toPrint)
                        out.println(s);
                    %>
                </table>
            </div>
            </div>
        </div>
    </body>
</html>
