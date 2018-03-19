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
        <link rel="STYLESHEET" type="text/css" href="./style/style_cfg.css" />
        <link href="./style/favicon.gif" rel="icon" type="image/gif" />
        <script src="./js/util.js" type="text/javascript"></script>
        <script src="./js/cfgparser.js" type="text/javascript"></script>
        <title>Bezkontextové gramatiky</title>
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
                        <li><a class="current" href="./indexcfg.jsp" title="Bezkontextové gramatiky">Bezkontextové gramatiky</a></li>
                    </ul>
                    <ul class="menu">
                        <% if ((session.getAttribute("Login") != null)){%>
                            <li>P&#345;ihlá&#353;en jako "<c:out value="${sessionScope.Login}"/>"</li>
                            <li><a href="${pageContext.request.contextPath}/Logout">Odhlásit</a></li>
                        <%}%>
                        <li><a href="./admin.jsp" title="Nastavení">Nastavení</a></li>
                        <li><a href="./helpcfg.jsp" title="Nápověda">Nápověda</a></li>
                        <li><a href="./author.jsp" title="O aplikaci">O aplikaci</a></li>
                    </ul>
                </div>
            </div>
        </div>
        <div class="page">
            <div class="content">
                <noscript>
                    <div class="alertWindow">
                        <div class="errorMessage">
                            JavaScript není povolen! Pro plnou funkčnost jej prosím zapněte.
                        </div>
                    </div>
                </noscript>
                <c:if test="${! empty error2}">
                    <div class="alertWindow">
                        <div class="errorMessage">
                            <c:out value="${error2}" />
                        </div>
                    </div>
                </c:if>
                <div class="window">
                    <h2 class="transformTitle">Simulace odpovědníku:</h2>
                    <div class="innerSpan">
                        <div class="left" title="Nezadávejte vyřešení zadání, poněvadž by mohlo dojít chybnému vyhodnocení.">Zde vyplňte nevyřešené zadání úkolu. (Jinak může dojít k chybnému vyhodnocení)</div>
                        <div class="middle">Zde vyplňte vyřešený úkol.</div>
                        <div class="right">Zvolte jaký převod se po studentovi požadoval.</div>
                    </div>
                    <form method="post" action="evaluatecfg" name="convert">
                        <textarea id="evaluate-teach" name="teacherData" cols="25" rows="12" title="Model učitele. Zde vložte zadání úkolu např. S -> a | A, A -> b"><c:if test="${(! empty teacherData) && empty error2}"><c:out value="${teacherData}" /></c:if><% if (request.getParameter("teacherData") != null) {out.print(request.getParameter("teacherData"));} %></textarea>
                        <textarea id="evaluate-stud" name="studentData" cols="25" rows="12" title="Model studenta. Zadejte gramatiku např. S -> a | A, A -> b"><c:if test="${(! empty studentData) && empty error2}"><c:out value="${studentData}" /></c:if><% if (request.getParameter("studentData") != null) {out.print(request.getParameter("studentData"));} %></textarea>
                        <ul class="list2">
                          <c:forEach var="ct" items="${cts}">
                            <c:if test="${not empty ct.transformationTypes}">
                              <li>
                                <input name="stud" value="${ct.transformationTypes}" id="${ct}" type="radio" <c:if test="${!sc.isAllowed}">disabled</c:if>/>
                                <label for="${ct}">${ct.description}</label>
                              </li>
                            </c:if>
                          </c:forEach>
                        </ul>
                        <div class="modes2">
                            <select name="mode" title="Zvolte si mód odpovědi.">
                                <option value="normal" title="V tomto módu je zobrazen pouze výsledný model gramatiky">Normální mód</option>
                                <option value="simple" title="V tomto módu je v závislosti na rovnosti gramatik zobrazena odpověď True/False a krátka informace.">IS mód</option>
                            </select>
                            <input value="Porovnej" type="submit" class="button" title="Porovná zadané gramatiky." />
                        </div>
                        <div id="evaluate-teach-error" class="parser_error" title="Nápověda syntaxe k formuláři pro zadání úkolu.">Zde se zobrazuje nápověda syntaxe pro model gramatiky zadání. Začněte psát do formuláře vlevo nahoře.</div>
                        <div id="evaluate-stud-error" class="parser_error" title="Nápověda syntaxe k formuláři pro řešení úkolu.">Zde se zobrazuje nápověda syntaxe pro model gramatiky řešení. Začněte psát do formuláře vpravo nahoře.</div>
                    </form>
                </div>
                <c:if test="${! empty error}">
                    <div class="alertWindow">
                        <div class="errorMessage">
                            <c:out value="${error}" />
                        </div>
                    </div>
                </c:if>
                <div class="window">
                    <h2 class="transformTitle">Převeď:</h2>
                    <form method="post" action="convertcfg" name="convert">
                        <textarea id="convert" name="inputData" cols="25" rows="12" style="height: 270px;" title="Zde zadejte gramatiku k převodu. Např. S -> a | A, A -> b"><c:if test="${(! empty inputData) && empty error }"><c:out value="${inputData}" /></c:if><% if (request.getParameter("inputData") != null) {out.print(request.getParameter("inputData"));} %></textarea>
                        <div id="convert-error" class="parser_error" title="Nápověda syntaxe.">Zde se zobrazuje nápověda syntaxe. Začněte psát do formuláře nahoře.</div>
                        <ul class="list">
                          <c:forEach var="tt" items="${tts}">
                            <li>
                              <input name="stud" value="${tt.transformationTypes}" id="${tt}" type="radio" <c:if test="${!sc.isAllowed}">disabled</c:if>/>
                              <label for="${tt}">${tt.description}</label>
                            </li>
                          </c:forEach>
                        </ul>
                        <div class="modes">
                            <select name="mode" title="Zvolte mód odpovědi.">
                                <option value="normal" title="V tomto módu je zobrazen pouze výsledný model gramatiky">Normální mód</option>
                                <option value="verbose" title="V tomto módu je zobrazena posloupnost transformací gramatiky až do její finální formy.">Detailní mód</option>
                            </select>
                            <input type="checkbox" id ="generateISString" name="generateISString" value="true" class="check" /><label for="generateISString" title="V odpovědi zobrazí i řetězec pro odpovědník.">Vygenerovat řetězec pro odpovědník</label>
                            <input value="Převeď" type="submit" class="button" title="Převede gramatiku do požadované formy." />
                        </div>
                    </form>
                </div>
                <c:if test="${! empty error3}">
                    <div class="alertWindow">
                        <div class="errorMessage">
                            <c:out value="${error3}" />
                        </div>
                    </div>
                </c:if>
                <div class="window">
                    <h2 class="transformTitle">Algoritmus Cocke-Younger-Kasami:</h2>
                    <form method="post" action="generatecfg" name="generate">
                        <textarea id="generate" name="generateData" cols="25" rows="12" style="height: 270px;" title="Zde zadejte gramatiku. Např. S -> a | A, A -> b"><c:if test="${! empty param.generateData}"><c:out value="${param.generateData}" /></c:if></textarea>
                        <div id="generate-error" class="parser_error" title="Nápověda syntaxe.">Zde se zobrazuje nápověda syntaxe. Začněte psát do formuláře nahoře.</div>
                        <div id="table">
                            <c:forEach var="j" begin="0" end="9">
                                <c:forEach var="i" begin="0" end="${j}">
                                    <c:set var="name" value="t${i}-${9-j}" />
                                    <input name="${name}" type="text" class="cell" value="<c:out value="${empty param[name] ? '' : param[name]}" />" />
                                </c:forEach>
                                <br />
                            </c:forEach>
                            <c:forEach var="i" begin="0" end="9">
                                <input class="letter" name="letter${i}" type="text" disabled="disabled"/>
                            </c:forEach>
                        </div>
                        
                        <div class="modes">
                            <input name="word" id="word" type="text" maxlength="10" value="<c:if test="${! empty param.word}"><c:out value="${param.word}" /></c:if>"/><label for="word">&nbsp;Slovo</label>
                            <input type="checkbox" name="cykISString" id="cykISString" value="true" class="check" /><label for="cykISString" title="V odpovědi zobrazí i řetězec pro odpovědník.">Vygenerovat řetězec pro odpovědník</label>
                            <input value="Ověř" type="submit" class="button" title="Ověř správnost vyplnění tabulky." />
                        </div>
                    </form>
                </div>
            </div>
        </div>
        <div class="bottomGradient">
            <div class="bottomLine">
                <a href="#header" class="bottomArrow"><img src="./style/toparrow.gif" title="Nahoru" /></a>
            </div>
        </div>
        <script type="text/javascript">
            
            var word = document.getElementById("word");

            if (word.addEventListener) {
                word.addEventListener("input", changeTable, false);
                word.addEventListener("keyup", changeTable, false);
            }
            window.addEventListener("load",changeTable,false);
            
            function changeTable(event) {
                form = document.forms["generate"];
                for (i = 0; i < 10; i++) {
                    for (j = 9-i; j >= 0; j--) {
                        form.elements["t" + i + "-" + j].style.visibility = (j > 9 - word.value.length) ? "visible" : "hidden";
                    }
                    form.elements["letter"+i].value = word.value.charAt(i);
                    form.elements["letter"+i].style.visibility = (i < word.value.length) ? "visible" : "hidden";
                    form.elements["letter"+i].style.top = (-(10-word.value.length)*23)+"px";
                }
            }
            register('generate', cfgparser.parse, document.getElementById('generate'));
            register('evaluate-stud', cfgparser.parse, document.getElementById('evaluate-stud'));
            register('evaluate-teach', cfgparser.parse, document.getElementById('evaluate-teach'));
            register('convert', cfgparser.parse, document.getElementById('convert'));
            scroll(0,0);
        </script>
    </body>
</html>
