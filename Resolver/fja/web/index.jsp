<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" 
    "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="cz.muni.fi.xpastirc.fja.config.Configuration"%>
    <%
      Configuration configuration = Configuration.getConfiguration();
      if (configuration.getReadFromIsOnly() //povoleno cteni jen z IS
              && !(request.getRemoteAddr().matches(configuration.getIsAddress()))//getRemoteHost v Linuxu ne vzdy funguje
              && (session.getAttribute("Login") == null)){
                response.sendRedirect("login.jsp?from=index.jsp");
          return;
      }
    %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Vyhodnocovací služba pro regulární jazyky</title>
        <link rel="stylesheet" type="text/css" href="style/style_reg.css">
        <script type="text/javascript" src="js/DFAParser.js"></script>
        <script type="text/javascript" src="js/NFAParser.js"></script>
        <script type="text/javascript" src="js/EFAParser.js"></script>
        <script type="text/javascript" src="js/GRAParser.js"></script>
        <script type="text/javascript" src="js/REGParser.js"></script>
        <script type="text/javascript" src="js/example.js"></script>
        <script type="text/javascript" src="js/util.js"></script>
        <script type="text/javascript" language="Javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.4.4/jquery.min.js"></script>
    </head>
    <body>
        <div class="header">
            <div class="topLine">
            </div>
            <div class="headerAuthor">
            </div>
            <div class="menuLine">
                <div class="innerMenu">
                    <ul class="menuServices">
                        <li><a class="current" href="./index.jsp" title="Regulární jazyky">Regulární jazyky</a></li>
                        <li><a href="./indexcfg.jsp" title="Bezkontextové gramatiky">Bezkontextové gramatiky</a></li>
                    </ul>
                    <ul class="menu">
                        <% if ((session.getAttribute("Login") != null)){%>
                            <li>P&#345;ihlá&#353;en jako "<c:out value="${sessionScope.Login}"/>"</li>
                            <li><a href="${pageContext.request.contextPath}/Logout">Odhlásit</a></li>
                        <%}%>
                        <li><a href="./admin.jsp" title="Nastavení">Nastavení</a></li>
                        <li><a href="./help.jsp" title="Nápověda">Nápověda</a></li>
                        <li><a href="./author.jsp" title="O aplikaci">O aplikaci</a></li>
                    </ul>
                </div>
            </div>
        </div>
        <div class="page">
            <div class="content">
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
                <div class="examples">
                  <select name="mod" title="Příklady">
                     <option value="0" title="Příklady užití služby">Příklady užití služby</option>
                     <option value="1" title="" onclick="ex(0);">Minimalizace</option>
                     <option value="1" title="" onclick="ex(1);">Ztotálnění</option>
                     <option value="1" title="" onclick="ex(2);">Minimalizace+Kanonizace</option>
                     <option value="1" title="" onclick="ex(3);">Determinizace</option>
                     <option value="1" title="" onclick="ex(4);">Ztotálnění II</option>
                     <option value="1" title="" onclick="ex(5);">Odstranění ɛ - kroků</option>
                     <option value="1" title="" onclick="ex(6);">Převod gramatiky</option>
                     <option value="1" title="" onclick="ex(7);">Gramatika dle automatu</option>
                     <option value="1" title="" onclick="ex(8);">Gramatika dle RE</option>
                     <option value="1" title="" onclick="ex(9);">RE dle automatu</option>
                     <option value="1" title="" onclick="ex(10);">Mód ALL</option>
                  </select>

                </div>
                <form method="post" action="Equal" name="equality">
                    <h2 class="transformTitle">Simulace odpovědníku:</h2>
                        <ul class="list">
                            Zadání příkladu:
                            <li><label><input name="teach" value="DFA" type="radio" onchange="invalidate('DFA', 't');"><b>DFA</b> - Det. konečný automat</label><br>
                            <li><label><input name="teach" value="EFA" type="radio" onchange="invalidate('EFA', 't');"><b>EFA</b> - NFA s epsilon kroky</label><br>
                            <li><label><input name="teach" value="GRA" type="radio" onchange="invalidate('GRA', 't');"><b>GRA</b> - Regulární gramatika</label><br>
                            <li><label><input name="teach" value="REG" type="radio" onchange="invalidate('REG', 't');"><b>REG</b> - Regulární výraz</label><br>
                        </ul>
                        <div class="input">
                            <textarea id="t" name="t" cols="25" rows="12"></textarea>
                        </div>
                        <ul class="list2">
                            Požadovaná odpověď:
                            <li><label><input name="stud" value="TOT" type="radio" onchange="invalidate('TOT', 's');"><b>TOT</b> - Totální DFA</label><br>
                            <li><label><input name="stud" value="MIC" type="radio" onchange="invalidate('MIC', 's');"><b>MIC</b> - Minimální kanonický DFA</label><br>
                            <li><label><input name="stud" value="MIN" type="radio" onchange="invalidate('MIN', 's');"><b>MIN</b> - Minimalní DFA</label><br>
                            <li><label><input name="stud" value="DFA" type="radio" onchange="invalidate('DFA', 's');"><b>DFA</b> - Det. konečný automat</label><br>
                            <li><label><input name="stud" value="NFA" type="radio" onchange="invalidate('NFA', 's');"><b>NFA</b> - Nedet. konečný automat</label><br>
                            <li><label><input name="stud" value="EFA" type="radio" onchange="invalidate('EFA', 's');"><b>EFA</b> - NFA s epsilon kroky</label><br>
                            <li><label><input name="stud" value="GRA" type="radio" onchange="invalidate('GRA', 's');"><b>GRA</b> - Regulární gramatika</label><br>
                            <li><label><input name="stud" value="REG" type="radio" onchange="invalidate('REG', 's');"><b>REG</b> - Regulární výraz</label><br>
                            <li><label><input name="stud" value="ALL" type="radio" onchange="invalidate('ALL', 's');"><b>ALL</b> - cokoliv</label><br>
                        </ul>
                    <div class="modes2">
                        <input type="checkbox" id ="showtable" name="intable" value="true" class="check" />
                        <label for="showtable" title="V odpovědi se zobrazí automaty v tabulce.">Zobrazit automaty v tabulce</label>
                        <br>
                        <input type="checkbox" id ="isomorphic" name="iso" value="true" class="check" />
                        <label for="isomorphic" title="Pro testování dodržení vyžadovaného postupu.">Testovat isomorfismus</label>
                        <br>
                            <select name="mod" title="Zvolte si mód odpovědi.">
                                <option value="verbose" title="Podrobný popis rozdílů mezi jazyky">Detailní mód</option>
                                <option value="tf" title="Zobrazení ekvivalence jazyků">IS mód</option>
                            </select>
                        <input value="Porovnej" type="submit" class="button" title="Převede a porovná formalismus." />
                    </div>
                         <div class="right">
                            <textarea class="answer" id="s" name="s" cols="25" rows="12"></textarea>
                         </div>
                    <div class="help">
                       <div id="t-error" class="parser_error" title="Nápověda syntaxe učitele.">Zde se zobrazuje nápověda syntaxe formuláře učitele. Začněte psát do formuláře vlevo.</div>
                       <div id="s-error" class="parser_error" title="Nápověda syntaxe studenta.">Zde se zobrazuje nápověda syntaxe formuláře studenta. Začněte psát do formuláře vpravo.</div>
                    </div>
                </form>
            </div>

            <div class="window">
                <form method="post" action="Convert" name="convert">
                    <h2 class="transformTitle">Převeď</h2>
                        <ul class="list">
                            <li><label><input name="teach2" value="DFA" type="radio" onchange="invalidate('DFA', 'convert');"><b>DFA</b></label><br>
                            <li><label><input name="teach2" value="EFA" type="radio" onchange="invalidate('EFA', 'convert');"><b>EFA</b> - NFA s epsilon kroky</label><br>
                            <li><label><input name="teach2" value="GRA" type="radio" onchange="invalidate('GRA', 'convert');"><b>GRA</b> - gramatika</label><br>
                            <li><label><input name="teach2" value="REG" type="radio" onchange="invalidate('REG', 'convert');"><b>REG</b> - Regulární výraz</label><br>
                         </ul>
                         <div class="input">
                             <textarea id="convert" name="convert" cols="25" rows="12"></textarea>
                         </div>
                         <ul class="list2">
                            <li><label><input name="stud" value="TOT" type="radio"><b>TOT</b> - totální DFA</label><br>
                            <li><label><input name="stud" value="MIC" type="radio"><b>MIC</b> - minimální kanonický DFA</label><br>
                            <li><label><input name="stud" value="MIN" type="radio"><b>MIN</b> - minimalní DFA</label><br>
                            <li><label><input name="stud" value="DFA" type="radio"><b>DFA</b></label><br>
                            <li><label><input name="stud" value="NFA" type="radio"><b>NFA</b></label><br>
                            <li><label><input name="stud" value="EFA" type="radio"><b>EFA</b> - NFA s epsilon kroky</label><br>
                         </ul>
                    <div class="help">
                      <div id="convert-error" class="parser_error" title="Nápověda syntaxe">Zde se zobrazuje nápověda syntaxe. Začněte psát do formuláře vlevo.</div>
                    </div>
                    <div class="modes">
                        <input type="checkbox" id ="generateISString" name="gen" value="true" class="check" /><label for="generateISString" title="V odpovědi zobrazí i řetězec pro odpovědník.">Vygenerovat řetězec pro odpovědník</label>
                        <br/>
                        <input type="checkbox" id ="showtable2" name="intable" value="true" class="check" /><label for="showtable2" title="V odpovědi se zobrazí automat v tabulce.">Zobrazit automaty v tabulce</label>
                        <br/>
                        <select name="mod">
                            <option value="verbose" selected="selected">Standardní</option>
                            <option value="details">Detailní</option>
                        </select>
                        <input value="Převeď" type="submit" class="button" title="Převede zadaný formalismus." />
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
                selector = document.querySelector('input[name="stud"]:checked');
                if(selector) {
                    invalidate(selector.value, 's');
                }
                selector = document.querySelector('input[name="teach2"]:checked');
                if(selector) {
                    invalidate(selector.value, 'convert');
                }
                $('input[type=radio][name=teach]').change(function() {
                    if (document.getElementById("t").value == '') {
                        document.getElementById("t-error").innerHTML = "Zde se zobrazuje nápověda syntaxe formuláře učitele. Začněte psát do formuláře vlevo.";
                    }
                    if (document.getElementById("convert").value == '') {
                        document.getElementById("convert-error").innerHTML = "Zde se zobrazuje nápověda syntaxe. Začněte psát do formuláře vlevo.";
                    }
                });
                $('input[type=radio][name=stud]').change(function() {
                    if (document.getElementById("s").value == '') {
                        document.getElementById("s-error").innerHTML = "Zde se zobrazuje nápověda syntaxe formuláře studenta. Začněte psát do formuláře vpravo.";
                    }
                });
            });
        </script>
    </body>
</html>
