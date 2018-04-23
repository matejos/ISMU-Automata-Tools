<%-- 
    Document   : login
    Created on : 20.10.2010, 12:48:04
    Author     : fafner
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>FJAAdmin - Login</title>
        <link rel="stylesheet" type="text/css" href="style/bootstrap.min.css">
        <link rel="stylesheet" type="text/css" href="style/style_fjamp.css">
        <script type="text/javascript" src="js/util.js"></script>
        <script type="text/javascript" src="js/jquery.js"></script>
        <script type="text/javascript" src="js/bootstrap.min.js"></script>
    </head>
    <script>
        document.write(printHeader("${sessionScope.Login}", "admin"));
    </script>
    <div class="container">
        <div class="panel panel-default">
            <div class="panel-heading">Autentizace</div>
            <div class="panel-body">
                <div class="content">
                    <%
                        if(request.getLocalPort() != 8443 ){
                            response.sendRedirect("https://"+request.getServerName()+":8443"+request.getRequestURI()
                                    + "?from="+request.getParameter("from"));
                            return;
                        }
                        String err = request.getParameter("err");
                        if (err != null){
                            if (err.equals("IncorrectCredentials"))
                                err = "Špatné heslo nebo jméno.";
                            else if (err.equals("PermissionDenied"))
                                err = "Nemáte oprávnění spravovat nastavení.";

                            out.println("<div class='alert alert-danger text-center'>");
                            out.println(err);
                            out.println("</div>");
                        }
                    %>
                    <div class="window">
                        <form method="POST" action="<%= "login" %>">
                            <div class="row">
                                <div class="col-sm-4 col-xs-3"></div>
                                <div class="col-sm-4 col-xs-6">
                                    <div class="text-center">
                                        <div class="form-group">
                                            <label for="usr">Uživatelské jméno:</label>
                                            <input type="text" class="form-control" id="usr" name="username" autofocus>
                                        </div>
                                        <div class="form-group">
                                            <label for="pwd">Uživatelské heslo:</label>
                                            <input type="password" class="form-control" id="pwd" name="password">
                                        </div>
                                        <input type="hidden" value="<%request.getParameter("from"); %>">
                                        <input type="submit" class="btn btn-primary" value="Odeslat">
                                    </div>
                                </div>
                                <div class="col-sm-4 col-xs-3"></div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
    </body>
</html>
