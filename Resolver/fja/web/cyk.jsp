<%@page import="cz.muni.fi.admin.ServicesController"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="sc" value="<%=ServicesController.instance()%>"/>
<c:set var="tts" value="<%=ServicesController.TransformationType.values()%>"/>
<c:set var="cts" value="<%=ServicesController.ConversionType.values()%>"/>
<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page import="cz.muni.fi.xpastirc.fja.config.Configuration"%>
<%
    Configuration configuration = Configuration.getConfiguration();
    if (configuration.needLogin(request.getRemoteAddr(), session.getAttribute("Login"))){
        response.sendRedirect("login.jsp?from=convertcfg.jsp");
        return;
    }
%>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="cs" lang="cs">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta http-equiv="Content-Language" content="cs" />
    <link rel="stylesheet" type="text/css" href="style/bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="style/style_fjamp.css">
    <link href="./style/favicon.gif" rel="icon" type="image/gif" />
    <script src="js/CFGParser.js" type="text/javascript"></script>
    <script src="js/CYKParser.js" type="text/javascript"></script>
    <script type="text/javascript" src="js/util.js?v=2"></script>
    <script type="text/javascript" src="js/jquery.js"></script>
    <script type="text/javascript" src="js/bootstrap.min.js"></script>
    <script type="text/javascript" src="js/exampleCyk.js"></script>
    <title>Bezkontextové gramatiky</title>
</head>
<body>
<script>
    document.write(printHeader("${sessionScope.Login}", "cfg"));
</script>
<div class="container">
    <div class="panel panel-default">
        <div class="panel-heading">Algoritmus Cocke-Younger-Kasami</div>
        <div class="panel-body cykpanel-body">
            <noscript>
                <div class="alertWindow">
                    <div class="errorMessage">
                        JavaScript není povolen! Pro plnou funkčnost jej prosím zapněte.
                    </div>
                </div>
            </noscript>
            <c:if test="${! empty error}">
                <div class="alert alert-danger">
                    <div class="errorMessage">
                        <c:out value="${error}" />
                    </div>
                </div>
            </c:if>
            <div class="window">
                <div class="examples">
                    <label for="selExample">Příklady užití služby:</label>
                    <select class="form-control" onchange="ex(id)" id="selExample">
                        <option style="display:none" disabled selected>Vyberte příklad</option>
                        <option>Příklad bez chyb</option>
                        <option>Příklad s chybama</option>
                    </select>
                </div>
                <form method="post" action="generatecfg" name="generate">
                    <h3 class="transformTitle">Vstupní gramatika:</h3>
                    <div class="row">

                        <div class="col-sm-6">
                            <div class="form-group has-feedback">
                                <textarea id="generate" name="t" class="form-control" rows="10"><c:if test="${! empty param.t}"><c:out value="${param.t}" /></c:if></textarea>
                            </div>
                            <div id="generate-error" class="alert alert-info" title="Nápověda syntaxe.">
                                <div id="generate-i" class=""></div>
                                <div id="generate-error-text">Zde se zobrazuje nápověda syntaxe.</div>
                            </div>
                            <div class="form-group">
                                <label for="sel1" title="Zvolte si mód odpovědi.">Mód:</label>
                                <select name="mode" class="form-control" id="sel1">
                                    <option value="verbose" title="">Detailní mód</option>
                                    <option value="tf" title="">IS mód</option>
                                </select>
                            </div>
                            <div class="checkbox">
                                <label title="V odpovědi zobrazí i řetězec pro odpovědník."><input type="checkbox" id ="cykISString" name="cykISString" value="true">Vygenerovat řetězec pro odpovědník</label>
                            </div>
                            <input value="Ověř" type="submit" class="btn btn-primary" title="Ověř správnost vyplnění tabulky." />
                        </div>
                        <div class="col-sm-6">
                            <div class="form-group">
                                <label for="word">Slovo:</label>
                                <input name="word" class="form-control" id="word" type="text" maxlength="10" value="<c:if test="${! empty param.word}"><c:out value="${param.word}" /></c:if>"/>
                            </div>
                            <table id="table" class="table cyk">
                                <tbody>
                                <c:forEach var="j" begin="0" end="9">
                                    <tr id="row${j}">
                                    <c:forEach var="i" begin="0" end="${j}">
                                        <c:set var="name" value="t${i}-${9-j}" />
                                        <td>
                                            <input name="${name}" id="${name}" type="text" autocomplete="off" class="form-control" value="<c:out value="${empty param[name] ? '' : param[name]}" />" />
                                            <script>invalidate('CYK','${name}');</script>
                                        </td>
                                    </c:forEach>
                                    </tr>
                                </c:forEach>
                                <tr>
                                <c:forEach var="i" begin="0" end="9">
                                    <td>
                                        <input class="form-control" name="letter${i}" id="letter${i}" type="text" disabled="disabled"/>
                                    </td>
                                </c:forEach>
                                </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
    <script type="text/javascript">
        var word = document.getElementById("word");

        if (word.addEventListener) {
            word.addEventListener("input", changeTable, false);
            word.addEventListener("keyup", changeTable, false);
            word.addEventListener("blur", changeTable, false);
        }
        window.addEventListener("load",changeTable,false);

        function changeTable(event) {
            form = document.forms["generate"];
            for (i = 0; i < 10; i++) {
                for (j = 9-i; j >= 0; j--) {
                    form.elements["t" + i + "-" + j].style.visibility = (j > 9 - word.value.length) ? "visible" : "hidden";
                }
                form.elements.namedItem("letter"+i).value = word.value.charAt(i);
                form.elements.namedItem("letter"+i).style.visibility = (i < word.value.length) ? "visible" : "hidden";
                $("#row"+i).prop('hidden', (!(i < word.value.length)));
                //form.elements["letter"+i].style.top = (-(10-word.value.length)*23)+"px";
            }
        }
        register('generate', CFGParser.parse, document.getElementById('generate'));
    </script>
</div>
</body>
</html>