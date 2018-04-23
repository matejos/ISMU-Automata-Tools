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
        <link rel="stylesheet" type="text/css" href="style/bootstrap.min.css">
        <link rel="stylesheet" type="text/css" href="style/style_fjamp.css">
        <script type="text/javascript" src="js/util.js"></script>
        <script type="text/javascript" src="js/jquery.js"></script>
        <script type="text/javascript" src="js/bootstrap.min.js"></script>
        <link href="./style/favicon.gif" rel="icon" type="image/gif" />
        <title>O aplikaci</title>
    </head>
    <body>
    <script>
        document.write(printHeader("${sessionScope.Login}", "about"));
    </script>
    <div class="container">
        <div class="panel panel-default">
            <div class="panel-heading">O aplikaci</div>
            <div class="panel-body">
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
    </body>
</html>
