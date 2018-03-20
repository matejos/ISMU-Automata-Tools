<%-- 
    Document   : resultgenerate
    Created on : April 10, 2011, 10:50:33 AM
    Author     : Daniel Pelisek <dpelisek@gmail.com>
--%>

<%@page import="java.util.Set"%>
<%@page import="java.util.HashSet"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<?xml version='1.0' encoding='utf-8'?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="cs" lang="cs">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta http-equiv="Content-Language" content="cs" />
        <link rel="STYLESHEET" type="text/css" href="./style/style_cfg.css" />
        <link href="./style/favicon.gif" rel="icon" type="image/gif" />
        <script src="./js/util.js" type="text/javascript"></script>
        <script src="js/CFGParser.js" type="text/javascript"></script>
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
                <c:if test="${! empty ISString}">
                    <div class="window">
                        <h2 class="transformTitle">Řetězec odpovědníku:</h2>
                        <pre class="isString"><c:out value="${ISString}" /></pre>
                    </div>
                </c:if>
                <div class="window">
                    <c:set var="n" value="${fn:length(teacherTable)}" />
                    <h2 class="transformTitle">Algoritmus Cocke-Younger-Kasami:</h2><br />
                    
                    <div class="resultTable">
                        <div class="property" style="margin-left: 0px;">Vzor:</div>
                        <c:forEach var="j" begin="1" end="${n}">
                            <c:forEach var="i" begin="0" end="${j-1}">
                                <div class="resultCell" style="<c:out value="${teacherTable[i][n-j] eq studentTable[i][n-j] ? '' : 'background-color: palegreen;'}" />" >
                                    <c:forEach items="${teacherTable[i][n-j]}" var="x" varStatus="status" >
                                        <c:out value="${x}${status.last ? '' : ', '}" />
                                    </c:forEach>
                                </div>
                            </c:forEach>
                            <br style="clear:both" />
                        </c:forEach>
                        <c:forEach var="i" begin="0" end="${fn:length(param.word)}" step="1">
                            <div class="resultCellLetter"><c:out value="${fn:substring(param.word, i, i + 1)}" /></div>
                        </c:forEach>
                    </div>
                    <div class="resultTable">
                        <div class="property" style="margin-left: 0px;">Vaše odpověď:</div>
                        <c:forEach var="j" begin="1" end="${n}">
                            <c:forEach var="i" begin="0" end="${j-1}">
                                <div class="resultCell" style="<c:out value="${teacherTable[i][n-j] eq studentTable[i][n-j] ? '' : 'background-color: #FEA7A7;'}" />" >
                                    <c:forEach items="${studentTable[i][n-j]}" var="x" varStatus="status" >
                                        <c:out value="${x}${status.last ? '' : ', '}" />
                                    </c:forEach>
                                </div>
                            </c:forEach>
                            <br style="clear:both" />
                        </c:forEach>
                        <c:forEach var="i" begin="0" end="${fn:length(param.word)}" step="1">
                            <div class="resultCellLetter"><c:out value="${fn:substring(param.word, i, i + 1)}" /></div>
                        </c:forEach>
                    </div>
                    <br style="clear:both" />
                        
                    <h2 class="transformTitle">Zadaná gramatika:</h2>
                    <pre><c:out value="${param.generateData}" /></pre>
                    <br />
                    <form method="post" action="indexcfg.jsp">
                        <c:forEach items="${param}" var="i">
                            <c:if test="${! empty i.value}">
                                <input type="hidden" name="<c:out value="${i.key}"/>" value="<c:out value="${i.value}" />" />
                            </c:if>
                        </c:forEach>
                        <input value="Zpět" type="submit" class="buttonZ" />
                    </form>
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

