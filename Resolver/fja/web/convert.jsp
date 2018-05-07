<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<%@page import="cz.muni.fi.xpastirc.fja.config.Configuration"%>
<%
    Configuration configuration = Configuration.getConfiguration();
    if (configuration.needLogin(request.getRemoteAddr(), session.getAttribute("Login"))){
        response.sendRedirect("login.jsp?from=convert.jsp");
        return;
    }
%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Regulární jazyky - převod</title>
    <link rel="stylesheet" type="text/css" href="style/bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="style/style_fjamp.css">
    <link href="./style/favicon.gif" rel="icon" type="image/gif" />
    <script type="text/javascript" src="js/DFAParser.js?v=2"></script>
    <script type="text/javascript" src="js/NFAParser.js?v=2"></script>
    <script type="text/javascript" src="js/EFAParser.js?v=2"></script>
    <script type="text/javascript" src="js/GRAParser.js"></script>
    <script type="text/javascript" src="js/REGParser.js"></script>
    <script type="text/javascript" src="js/CFGParser.js"></script>
    <script type="text/javascript" src="js/util.js"></script>
    <script type="text/javascript" src="js/jquery.js"></script>
    <script type="text/javascript" src="js/bootstrap.min.js"></script>
</head>
<body>
<script>
    document.write(printHeader("${sessionScope.Login}", "reg"));
</script>
<div class="container">
    <div class="panel panel-default">
        <div class="panel-heading">Převod</div>
        <div class="panel-body">
            <c:if test="${! empty error2}">
                <div class="alertWindow">
                    <div class="errorMessage">
                        <c:out value="${error2}" />
                    </div>
                </div>
            </c:if>
            <noscript>
                <div class="alertWindow">
                    <div class="errorMessage">
                        JavaScript není povolen! Pro plnou funkčnost jej prosím zapněte.
                    </div>
                </div>
            </noscript>
            <div class="window">
                <form method="post" action="Convert" name="convert" id="theForm">
                    <h3>Zadání příkladu:</h3>
                    <div class="row">
                        <div class="col-sm-3">
                            <div class="form-group">
                                <div class="radio"><label><input name="teach" value="DFA" type="radio" onchange="invalidate('DFA', 't');"><samp><b>DFA</b></samp> - Det. konečný automat</label></div>
                                <div class="radio"><label><input name="teach" value="EFA" type="radio" onchange="invalidate('EFA', 't');"><samp><b>EFA</b></samp> - NFA s epsilon kroky</label></div>
                                <div class="radio"><label><input name="teach" value="GRA" type="radio" onchange="invalidate('GRA', 't');"><samp><b>GRA</b></samp> - Regulární gramatika</label></div>
                                <div class="radio"><label><input name="teach" value="REG" type="radio" onchange="invalidate('REG', 't');"><samp><b>REG</b></samp> - Regulární výraz</label></div>
                                <div class="alert alert-danger" id="t-choose" style="display:none;">
                                    <script>document.write(chooseOne)</script>
                                </div>
                            </div>
                        </div>
                        <div class="col-sm-9">
                            <div class="form-group has-feedback">
                                <textarea id="t" name="t" class="form-control" rows="10"></textarea>
                            </div>
                            <div id="t-error" class="alert alert-info" title="Nápověda syntaxe učitele.">
                                <div id="t-i" class=""></div>
                                <div id="t-error-text">Zde se zobrazuje nápověda syntaxe.</div>
                            </div>
                        </div>
                    </div>
                    <h3>Požadovaná odpověď:</h3>
                    <div class="row">
                        <div class="col-sm-3">
                            <div class="form-group">
                                <div class="radio"><label><input name="stud" value="TOT" type="radio" onchange="invalidate('', 's');"><samp><b>TOT</b></samp> - Totální DFA</label></div>
                                <div class="radio"><label><input name="stud" value="MIC" type="radio" onchange="invalidate('', 's');"><samp><b>MIC</b></samp> - Minimální kanonický DFA</label></div>
                                <div class="radio"><label><input name="stud" value="MIN" type="radio" onchange="invalidate('', 's');"><samp><b>MIN</b></samp> - Minimalní DFA</label></div>
                                <div class="radio"><label><input name="stud" value="DFA" type="radio" onchange="invalidate('', 's');"><samp><b>DFA</b></samp> - Det. konečný automat</label></div>
                                <div class="radio"><label><input name="stud" value="NFA" type="radio" onchange="invalidate('', 's');"><samp><b>NFA</b></samp> - Nedet. konečný automat</label></div>
                                <div class="radio"><label><input name="stud" value="EFA" type="radio" onchange="invalidate('', 's');"><samp><b>EFA</b></samp> - NFA s epsilon kroky</label></div>
                                <div class="alert alert-danger" id="s-choose" style="display:none;">
                                    <script>document.write(chooseOne)</script>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-sm-3">
                            <div class="form-group">
                                <div class="checkbox">
                                    <label title="V odpovědi se zobrazí automaty v tabulce."><input type="checkbox" id ="showtable" name="intable" value="true">Zobrazit automaty v tabulce</label>
                                </div>
                                <div class="checkbox">
                                    <label title="V odpovědi zobrazí i řetězec pro odpovědník."><input type="checkbox" id ="generateISString" name="gen" value="true">Vygenerovat řetězec pro odpovědník</label>
                                </div>
                            </div>
                        </div>
                        <div class="col-sm-2">
                            <div class="form-group">
                                <label for="sel1" title="Zvolte si mód odpovědi.">Mód:</label>
                                <select name="mod" class="form-control" id="sel1">
                                    <option value="verbose" selected="selected">Standardní</option>
                                    <option value="details">Detailní</option>
                                </select>
                            </div>
                        </div>
                        <div class="col-sm-7"></div>
                    </div>
                    <div class="row">
                        <div class="col-sm-1"></div>
                        <div class="col-sm-1">
                            <input value="Převeď" type="submit" class="btn btn-primary" title="Převede zadaný formalismus." />
                        </div>
                        <div class="col-sm-10"></div>
                    </div>
                </form>
            </div>
        </div>
    </div>
    <script>
        $(document).ready(function() {
            var selector = document.querySelector('input[name="teach"]:checked');
            if(selector) {
                invalidate(selector.value, 't');
            }
        });
        $('#theForm').submit(function() {
            var proceed = true;
            if (document.querySelector('input[name="teach"]:checked') == null) {
                $('#t-choose').fadeIn();
                proceed = false;
            }
            if (document.querySelector('input[name="stud"]:checked') == null) {
                $('#s-choose').fadeIn();
                proceed = false;
            }
            return proceed; // return false to cancel form action
        });
    </script>
</div>
</body>
</html>
