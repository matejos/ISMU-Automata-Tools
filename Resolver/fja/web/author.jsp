<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<!--
To change this license header, choose License Headers in Project Properties.
To change this template file, choose Tools | Templates
and open the template in the editor.
-->
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="cs" lang="cs">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta http-equiv="Content-Language" content="cs" />
        <link rel="STYLESHEET" type="text/css" href="./style/style_reg.css" />
        <link href="./style/favicon.gif" rel="icon" type="image/gif" />
        <title>O aplikaci</title>
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
                        <% if ((session.getAttribute("Login") != null)){%>
                            <li>P&#345;ihlá&#353;en jako "<c:out value="${sessionScope.Login}"/>"</li>
                            <li><a href="${pageContext.request.contextPath}/Logout">Odhlásit</a></li>
                        <%}%>
                        <li><a href="./admin.jsp" title="Nastavení">Nastavení</a></li>
                        <li><a href="./help.jsp" title="Nápověda">Nápověda</a></li>
                        <li><a class="current" href="./author.jsp" title="O aplikaci">O aplikaci</a></li>
                    </ul>
                </div>
            </div>
        </div>
        <div class="page">
            <div class="content">
                <div class="window2" style="height:300px;">
                    <div style="position:relative; left:50px; top:20px;">
                    <h2 class='transformTitle'>O aplikaci</h2>
                    <br/><br/>
                    Tato aplikace byla vytvářena jako několik bakalářských prací. Pokud narazíte na problém, kontaktujte některého z autorů((a) značí @).<br/>
                    <br/>
                    <b>Seznam autorů:</b><br/>
                    Adrián Elgyütt<br/>
                    Regulární část:<br/>
                    Bronislav Houdek: xsysel(a)seznam.cz<br/>
                    Tomáš Pastirčák: 324693(a)mail.muni.cz : <a href=https://is.muni.cz/auth/th/324693/fi_b>https://is.muni.cz/auth/th/324693/fi_b</a><br/>
                    Bezkontextová část:<br/>
                    Jonáš Ševčík: 255493(a)mail.muni.cz : <a href=http://is.muni.cz/th/255493/fi b>http://is.muni.cz/th/255493/fi b</a><br/>
                    Daniel Pelíšek: <a href=https://is.muni.cz/auth/th/359213/fi_b_a2>https://is.muni.cz/auth/th/359213/fi_b_a2</a><br/>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
