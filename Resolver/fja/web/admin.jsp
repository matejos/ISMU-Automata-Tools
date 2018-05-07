<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="cz.muni.fi.xpastirc.fja.config.Configuration" %>
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
        if (request.getParameter("operationMode") != null) {
            configuration.setOperationMode(request.getParameter("operationMode"));
        }
    }

    String isAddress = configuration.getIsAddress();
    String bannedBad = configuration.getBannedBad();
    String bannedGood = configuration.getBannedGood();
    String dbName = configuration.getDbName();
    String dbServer = configuration.getDbServer();
    String dbUser = configuration.getDbUser();
    String dbPass = configuration.getDbPass();
    String logCount = configuration.getLogCount();
    String logDelete = configuration.getLogDelete();
    String operationMode = configuration.getOperationMode();

%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Administrace</title>
    <link rel="stylesheet" type="text/css" href="style/bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="style/style_fjamp.css">
    <link href="./style/favicon.gif" rel="icon" type="image/gif"/>
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
        <div class="panel-heading">Administrace vkládaných úloh</div>
        <div class="panel-body">
            <form method="get" action="admin.jsp">
                <table class="table table-hover">
                    <tr>
                        <td>
                            Mód provozu
                        </td>
                        <td>
                            <div class="form-group">
                                <div class="radio"><label><input name="operationMode" id="<%=Configuration.ALLOW_ALL%>" value="allowAll" type="radio">Provoz bez omezení</label></div>
                                <div class="radio"><label><input name="operationMode" id="<%=Configuration.ALLOW_ONLY_ADDRESS_OR_LOGGED_IN%>" value="allowOnlyAddressOrLoggedIn" type="radio">Provoz pouze z povolených adres nebo po přihlášení</label></div>
                                <div class="radio"><label><input name="operationMode" id="<%=Configuration.ALLOW_ONLY_LOGGED_IN%>" value="allowOnlyLoggedIn" type="radio">Provoz pouze po přihlášení</label></div>
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            Povolené adresy (<a href="javascript:;" class="is_add">vyplň adresu IS</a>)
                        </td>
                        <td><input type="text" name="isaddress" class="form-control" value="<%= isAddress%>">
                        </td>
                    </tr>
                    <tr>
                        <td>
                            Maximální počet špatných pokusů za poslední hodinu na IP
                        </td>
                        <td><input type="text" name="bannedbad" class="form-control" value="<%= bannedBad%>">
                        </td>
                    </tr>
                    <tr>
                        <td>
                            Maximální počet pokusů za poslední hodinu
                        </td>
                        <td><input type="text" name="bannedgood" class="form-control" value="<%= bannedGood%>">
                        </td>
                    </tr>
                    <tr>
                        <td>
                            Server databáze
                        </td>
                        <td><input type="text" name="dbserver" class="form-control" value="<%= dbServer%>">
                        </td>
                    </tr>
                    <tr>
                        <td>
                            Název databáze
                        </td>
                        <td><input type="text" name="dbname" class="form-control" value="<%= dbName%>">
                        </td>
                    </tr>
                    <tr>
                        <td>
                            Uživatel databáze
                        </td>
                        <td><input type="text" name="dbuser" class="form-control" value="<%= dbUser%>">
                        </td>
                    </tr>
                    <tr>
                        <td>
                            Heslo databáze
                        </td>
                        <td><input type="text" name="dbpass" class="form-control" value="<%= dbPass%>">
                        </td>
                    </tr>
                    <tr>
                        <td>
                            Výpis logu - počet záznamů na stránku
                        </td>
                        <td><input type="text" name="logcount" class="form-control" value="<%= logCount%>">
                        </td>
                    </tr>
                    <tr>
                        <td>
                            Počet dotazů po kterých promazat logy
                        </td>
                        <td><input type="text" name="logdelete" class="form-control" value="<%= logDelete%>">
                        </td>
                    </tr>
                </table>
                <div class="text-center">
                    <input type=submit class="btn btn-primary" value="Nastav" name="set">
                </div>
                <% if (operationMode != null) { out.print("<script>$(" + operationMode + ").prop('checked', true);</script>"); } %>
            </form>
            <p></p>
            <div class="text-center">
                <a href="log.jsp">Výpis logů</a>
            </div>
        </div>
    </div>
</div>
<script>
    $(document).ready(function () {
        $('.is_add').on('click', function () {
            $("[name=isaddress]").val(<% out.print("\"" + Configuration.IS_IP_ADDRESS + "\""); %>);
        });
    });
</script>
</body>

</html>
