<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">


<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="cs" lang="cs">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta http-equiv="Content-Language" content="cs" />
        <link rel="STYLESHEET" type="text/css" href="./style/style_cfg.css" />
        <link href="./style/favicon.gif" rel="icon" type="image/gif" />
        <title>Převody CFG</title>
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
                        <li><a class="current" href="./helpcfg.jsp" title="Nápověda">Nápověda</a></li>
                        <li><a href="./author.jsp" title="O aplikaci">O aplikaci</a></li>
                    </ul>
                </div>
            </div>
        </div>
        <div class="page">
            <div class="content">
                <div class="window">
                    <div class="innerWindow">
                        <p>
                            <h1>CFG - Bezkontextová gramatika, syntaxe</h1>
                            Gramatika je typu 2, jestliže každé její pravidlo je tvaru A -> α, kde |α|>=1 s eventuální výjimkou pravidla S -> \e, pokud se S nevyskytuje na pravé straně žádného pravidla.<br />
                            <br />
                            Syntaxe je automaticky JavaScriptem kontrolována již v průběhu zadávání gramatiky do formuláře. Červené zbarvení formuláře značí špatnou syntaxi, oranžová značí, že je syntaxe správná, ale je očekáváno zadání dalších dat. Zelenou je značena bezchybná syntaxe.<br />
                            Pod formulářem jsou navíc zobrazovány očekávané, popř. chybné znaky.

                        </p>
                        <p>
                            <h2>Iniciální neterminál</h2>
                            Iniciálním neterminálem je zvolen první nalezený neterminál.
                        </p>
                        <p>
                            <h2>Neterminál</h2>
                            Neterminál je symbol z množiny {A, ..., Z}, popř. sekvence povolených symbolů uzavřených do zobáčků: &lt;cokoli&gt; popř. terminál nebo symbol množiny {A, ..., Z} s libovolným počtem apostrofů.
                        </p>
                        <p>
                            <h2>Terminál</h2>
                            Terminál je libovolný symbol různý od neterminálního symbolu a speciálních symbolů.
                        </p>
                        <p>
                            <h2>Pravidla</h2>
                            Pravidla jsou v takovém tvaru, aby odpovídala pravidlům gramatik typu 2. Jednotlivá pravidla jsou od sebe oddělena čárkami nebo novým řádkem nebo čárkou a novým řádkem.
                        </p>
                        <p>
                            <h2>Speciální symboly</h2>
                            Speciální symboly jsou "\n", ",", "<", ">", "|", "\", "'". Epsilon se značí jako "\e" (bez uvozovek).
                        </p>
                        <p>
                            <h1>Převody gramatik</h1>
                            V sekci převod jde převést bezkontextovou gramatiku do všech možných forem. Konkrétně lze provést převody odstranění nenormovaných symbolů, odstranění nedosažitelných symbolů, redukce, odstranění epsilon kroků, odstranění jednoduchých pravidel, převod na vlastní gramatiku, převod do Chomského normální formy, odstranění levé rekurze a převod do Greibachové normální formy.<br />
                            <h2>Módy odpovědí</h2>
                            Lze zvolit celkem dva módy odpovědí - normální a detailní. V normálním módu je zobrazena pouze výsledná transformace gramatiky, kdežto v módu detailním je vypsána celá posloupnost transformací gramatiky až do její požadované formy.<br />
                            <h2>Řetězec pro odpovědník</h2>
                            Učitel může zvolit možnost vygenerování řetězce reprezentující model zadání úkolu do odpovědníku ISu.
                        </p>
                        <p>
                            <h1>Simulace odpovědníku</h1>
                            Simulace odpovědníku reprezentuje vyhodnocování odpovědí úkolů.<br />
                            Do levého formuláře se zadává studentovo řešení úkolu. Do pravého formuláře se zadává zadání úkolu, nikoli jeho správné řešení. V případě zadání správného řešení by totiž mohlo dojít k opětovné transformaci zadané gramatiky, což by ve výsledku mohlo vést ke špatnému vyhodnocení příkladu.<br />
                            Ke korektnímu vyhodnocení odpovědi je také nutno zvolit patřičný druh transformace, který se po studentovi v zadání požadoval.
                            <h2>Módy odpovědí</h2>
                            Opět lze vybrat ze dvou módu odpovědí. Normální mód vypisuje informace o chybách, kdy např. při odstranění jednoduchých pravidel je nejdříve gramatika studenta kontrolována, zda byla odstraněna epsilon pravidla, v případě, že nikoli, je student na daný fakt upozorněn. V případě, že je studentova gramatika zkontrolována, že neobsahuje epsilon pravidla, ani jednoduchá pravidla, teprve až pak je porovnána s modelem správné odpovědi. Student má tedy k dispozici obsáhlou kontrolu řešení.<br />
                            True/False mód pouze vypisuje true, je-li odpověď správná, jinak vypisuje false.
                        </p>
                    </div>
                </div>
            </div>
        </div>
        <div class="bottomGradient">
            <div class="bottomLine">
                <a href="#header" class="bottomArrow"><img src="./style/toparrow.gif" title="Nahoru" /></a>
            </div>
        </div>
    </body>
</html>