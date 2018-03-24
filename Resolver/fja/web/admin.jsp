<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="cz.muni.fi.xpastirc.fja.config.Configuration"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<% if ((session.getAttribute("Login") == null)) {
        response.sendRedirect("login.jsp?from=admin.jsp");
        return;
    }
    Configuration configuration = Configuration.getConfiguration();
    if (request.getParameter("set") != null && (request.getParameter("set").equals("Nastav"))) {
        if (request.getParameter("isaddress") != null) {
            configuration.setIsAddress(request.getParameter("isaddress"));
        }
        if (request.getParameter("bannedbad") != null) {
            configuration.setBannedBad(request.getParameter("bannedbad"));
        }
        if (request.getParameter("bannedgood") != null) {
            configuration.setBannedGood(request.getParameter("bannedgood"));
        }
        if (request.getParameter("dbserver") != null) {
            configuration.setDbServer(request.getParameter("dbserver"));
        }
        if (request.getParameter("dbname") != null) {
            configuration.setDbName(request.getParameter("dbname"));
        }
        if (request.getParameter("dbuser") != null) {
            configuration.setDbUser(request.getParameter("dbuser"));
        }
        if (request.getParameter("dbpass") != null) {
            configuration.setDbPass(request.getParameter("dbpass"));
        }
        if (request.getParameter("logcount") != null) {
            configuration.setLogCount(request.getParameter("logcount"));
        }
        if (request.getParameter("logdelete") != null) {
            configuration.setLogDelete(request.getParameter("logdelete"));
        }
        configuration.setReadFromIsOnly(request.getParameter("isOnly") != null);
    }

    boolean readFromIsOnly = configuration.getReadFromIsOnly();
    String isAddress = configuration.getIsAddress();
    String bannedBad = configuration.getBannedBad();
    String bannedGood = configuration.getBannedGood();
    String dbName = configuration.getDbName();
    String dbServer = configuration.getDbServer();
    String dbUser = configuration.getDbUser();
    String dbPass = configuration.getDbPass();
    String logCount = configuration.getLogCount();
    String logDelete = configuration.getLogDelete();

%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Administrace</title>
        <link rel="stylesheet" type="text/css" href="style/style_reg.css">
        <script type="text/javascript" language="Javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.4.4/jquery.min.js"></script>
        <style>
            .alnright { text-align: left; }
        </style>
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
                        <li>P&#345;ihlá&#353;en jako "<c:out value="${sessionScope.Login}"/>"</li>
                        <li><a href="${pageContext.request.contextPath}/Logout">Odhlásit</a></li>
                        <li><a class="current" href="./admin.jsp" title="Nastavení">Nastavení</a></li>
                        <li><a href="./help.jsp" title="Nápověda">Nápověda</a></li>
                        <li><a href="./author.jsp" title="O aplikaci">O aplikaci</a></li>
                    </ul>
                </div>
            </div>
        </div>
        <div class="page">
            <div class="content">
                <center>
                    <h1>Administrace vkládaných úloh</h1> 
                    <form method="get" action="admin.jsp">
                        <input type="button" class="turn_off_button" value="<%= readFromIsOnly ? "Opět spřístupni" : "Dočasně vypni"%>">
                        <table>
                            <tr>
                                <td class="alnright">
                                    Povolené adresy(<a href="javascript:;" class="is_add">vyplň adresu IS</a>)</td><td> <input type="text" name ="isaddress" value="<%= isAddress%>">
                                </td>
                            </tr>
                            <tr>
                                <td class="alnright">
                                    Maximální počet špatných pokusů za poslední hodinu na IP</td><td> <input type="text" name ="bannedbad"value="<%= bannedBad%>">
                                </td>
                            </tr>
                            <tr>
                                <td class="alnright">
                                    Maximální počet pokusů za poslední hodinu</td><td><input type="text" name ="bannedgood"value="<%= bannedGood%>">
                                </td>
                            </tr>
                            <tr>
                                <td class="alnright">
                                    Server databáze</td><td><input type="text" name ="dbserver"value="<%= dbServer%>">
                                </td>
                            </tr>
                            <tr>
                                <td class="alnright">
                                    Název databáze</td><td><input type="text" name ="dbname"value="<%= dbName%>">
                                </td>
                            </tr>
                            <tr>
                                <td class="alnright">
                                    Uživatel databáze</td><td><input type="text" name ="dbuser"value="<%= dbUser%>">
                                </td>
                            </tr>
                            <tr>
                                <td class="alnright">
                                    Heslo databáze</td><td><input type="text" name ="dbpass"value="<%= dbPass%>">
                                </td>
                            </tr>
                            <tr>
                                <td class="alnright">
                                    Výpis logu - počet záznamů na stránku</td><td><input type="text" name ="logcount"value="<%= logCount%>">
                                </td>
                            </tr>
                            <tr>
                                <td class="alnright">
                                    Počet dotazů po kterých promazat logy</td><td><input type="text" name ="logdelete"value="<%= logDelete%>">
                                </td>
                            </tr>
                            <tr>
                                <td class="alnright">Zpracovávej požadavky pouze povolených adres</td><td><input type=checkbox name="isOnly" value="yes" <%= readFromIsOnly ? "checked" : ""%>></td>
                            </tr>
                            <tr>
                                <td style="text-align: right;padding-right: 55px;" colspan="2"><input type=submit value="Nastav" name="set"></td>
                            </tr>
                        </table>
                    </form>
                    <a href="log.jsp">Výpis logů</a>
                    <br>
                </center>
            </div>
        </div>
        <script>
            $(document).ready(function() {
                $('.is_add').live('click', function() {
                    $("[name=isaddress]").val("147.251.49.*");
                });
                $('.turn_off_button').live('click', function() {
                    if (<%=readFromIsOnly%>) {
                        $("[name=isOnly]").attr('checked', false);
                    }
                    else {
                        $("[name=isaddress]").val("147.251.49.*");
                        $("[name=isOnly]").attr('checked', true);
                    }
                    $("[name=set]").click();
                });
            });
        </script>
    </body>

</html>
