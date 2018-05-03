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
      if (configuration.getReadFromIsOnly() //povoleno cteni jen z IS
              && !(request.getRemoteAddr().matches(configuration.getIsAddress()))//getRemoteHost v Linuxu ne vzdy funguje
              && (session.getAttribute("Login") == null)){
                response.sendRedirect("login.jsp?from=indexcfg.jsp");
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
        <script src="./js/util.js" type="text/javascript"></script>
        <script src="js/CFGParser.js" type="text/javascript"></script>
        <script type="text/javascript" src="js/util.js?v=2"></script>
        <script type="text/javascript" src="js/jquery.js"></script>
        <script type="text/javascript" src="js/bootstrap.min.js"></script>
        <title>Bezkontextové gramatiky</title>
    </head>
    <body>
    <script>
        document.write(printHeader("${sessionScope.Login}", "cfg"));
    </script>
    <div class="container">
        <div class="panel panel-default">
            <div class="panel-heading">Simulace odpovědníku</div>
            <div class="panel-body">
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
                    <form method="post" action="evaluatecfg" name="convert" id="theForm">
                        <div class="row">
                            <div class="col-sm-4">
                                <h3>Zadání příkladu:</h3>
                                <div class="form-group has-feedback">
                                    <textarea id="evaluate-teach" name="teacherData" class="form-control" rows="10"><c:if test="${(! empty teacherData) && empty error}"><c:out value="${teacherData}" /></c:if><% if (request.getParameter("teacherData") != null) {out.print(request.getParameter("teacherData"));} %></textarea>
                                </div>
                                <div id="evaluate-teach-error" class="alert alert-info" title="Nápověda syntaxe k formuláři pro zadání úkolu.">
                                    <div id="evaluate-teach-i" class=""></div>
                                    <div id="evaluate-teach-error-text">Zde se zobrazuje nápověda syntaxe.</div>
                                </div>
                            </div>
                            <div class="col-sm-4">
                                <h3>Vyřešený úkol:</h3>
                                <div class="form-group has-feedback">
                                    <textarea id="evaluate-stud" name="studentData" class="form-control" rows="10"><c:if test="${(! empty studentData) && empty error}"><c:out value="${studentData}" /></c:if><% if (request.getParameter("studentData") != null) {out.print(request.getParameter("studentData"));} %></textarea>
                                </div>
                                <div id="evaluate-stud-error" class="alert alert-info" title="Nápověda syntaxe k formuláři pro řešení úkolu.">
                                    <div id="evaluate-stud-i" class=""></div>
                                    <div id="evaluate-stud-error-text">Zde se zobrazuje nápověda syntaxe.</div>
                                </div>
                            </div>
                            <div class="col-sm-4">
                                <h3>Typ převodu:</h3>
                                <div class="form-group">
                                    <c:forEach var="ct" items="${cts}">
                                        <c:if test="${not empty ct.transformationTypes}">
                                            <div class="radio"><label><input name="stud" value="${ct.transformationTypes}" id="${ct}" type="radio" onchange="invalidate('', 's');" <c:if test="${!sc.isAllowed}">disabled</c:if>>${ct.description}</label></div>
                                        </c:if>
                                    </c:forEach>
                                    <div class="alert alert-danger" id="s-choose" style="display:none;">
                                        <script>document.write(chooseOne)</script>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-sm-2">
                                <div class="form-group">
                                    <label for="sel1" title="Zvolte si mód odpovědi.">Mód:</label>
                                    <select name="mode" class="form-control" id="sel1">
                                        <option value="normal" id="normal" title="V tomto módu je zobrazen pouze výsledný model gramatiky">Normální mód</option>
                                        <option value="simple" id="simple" title="V tomto módu je v závislosti na rovnosti gramatik zobrazena odpověď True/False a krátka informace.">IS mód</option>
                                    </select>
                                </div>
                            </div>
                            <div class="col-sm-10"></div>
                        </div>
                        <input value="Porovnej" type="submit" class="btn btn-primary" title="Porovná zadané gramatiky." />
                        <% String stud = request.getParameter("stud"); if (stud != null) { out.print("<script>$(" + stud + ").prop('checked', true);</script>"); } %>
                        <% String mode = request.getParameter("mode"); if (mode != null) { out.print("<script>$(" + mode + ").prop('selected', true);</script>"); } %>
                    </form>
                </div>
            </div>
        </div>
        <script type="text/javascript">
            register('evaluate-stud', CFGParser.parse, document.getElementById('evaluate-stud'));
            register('evaluate-teach', CFGParser.parse, document.getElementById('evaluate-teach'));
            $('#theForm').submit(function() {
                var proceed = true;
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
