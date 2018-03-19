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
        <link rel="stylesheet" type="text/css" href="style/style_reg.css">
    </head>
    <body>
        <div class="header" id="header">
            <div class="topLine">
            </div>
            <div class="headerAuthor">
            </div>
            <div class="menuLine">
                <div class="innerMenu">
                    <ul class="menuServices">
                        <li><a href="./index.jsp" title="Regulární jazyky">Regulární jazyky</a></li>
                        <li><a href="./indexcfg.jsp" title="Bezkontextové gramatiky">Bezkontextové gramatiky</a></li>
                    </ul>
                    <ul class="menu">
                        <% if ((session.getAttribute("Login") != null)){%>
                            <li>P&#345;ihlá&#353;en jako "<c:out value="${sessionScope.Login}"/>"</li>
                            <li><a href="${pageContext.request.contextPath}/Logout">Odhlásit</a></li>
                        <%}%>
                        <li><a class="current" href="./admin.jsp" title="Nastavení">Nastavení</a></li>
                        <li><a href="./help.jsp" title="Nápověda">Nápověda</a></li>
                        <li><a href="./author.jsp" title="O aplikaci">O aplikaci</a></li>
                    </ul>
                </div>
            </div>
        </div>
        <div class="page">
        <div class="content">
        <div class="window2">
        <center>
            <a href="log.jsp?action=clean" style="color:#123456" title="">Promazat</a>&nbsp;
            <a href="log.jsp?action=delete" style="color:#123456" title="Smaže všechny záznamy">Smazat</a>
        <%
        for (int i=1;i<=pages;i++){
            out.print(p==i?"<b>":"");
            out.print("<a href=\"log.jsp?sort="+orderBy+"&order="+(asc?"asc":"desc")+"&page="+i+"\" style=\"color:#123456\">"+i+"</a> ");
            out.print(p==i?"</b>":"");
        }
        out.println("<br>");
        %>
        <table class="output"cellspacing="2" cellpadding="1">
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
        </center>
        </div>
        </div>
        </div>
    </body>
</html>
