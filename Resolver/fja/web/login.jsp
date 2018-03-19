<%-- 
    Document   : login
    Created on : 20.10.2010, 12:48:04
    Author     : fafner
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<%
if(request.getLocalPort() != 8443 ){
            response.sendRedirect("https://"+request.getServerName()+":8443"+request.getRequestURI()
                    + "?from="+request.getParameter("from"));
            return;
       }
   String err = request.getParameter("err");
   if (err != null){
        out.println(err);
    }
    %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>FJAAdmin - Login</title>
        <link rel="stylesheet" type="text/css" href="style/style_cfg.css" />
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
                        <li><a class="current" href="./admin.jsp" title="Nastavení">Nastavení</a></li>
                        <li><a href="./help.jsp" title="Nápověda">Nápověda</a></li>
                        <li><a href="./author.jsp" title="O aplikaci">O aplikaci</a></li>
                    </ul>
                </div>
            </div>
        </div>
        <div class="page">
            <div class="content">
                <div class="window">
                <center>
                    <br/>
                    <br/>
                    <form method="POST" action="<%= "login" %>">
                        <%
                        //out.println(System.getProperty("java.library.path"));
                        %>
                        Uživatelské jméno: <br/> <input type="text" value="" name="username"><br /><br/>
                        Uživatelské heslo: <br/> <input type="password" value="" name="password"><br /><br/>
                        <input type="hidden" value="<%request.getParameter("from"); %>">
                        <input type="submit" value="Odeslat">
                    </form>
                    <br/>
                    <br/>
                </center>
                </div>
            </div>
        </div>
    </body>
</html>
