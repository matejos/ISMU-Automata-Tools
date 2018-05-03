<%-- 
    Document   : resultgenerate
    Created on : April 10, 2011, 10:50:33 AM
    Author     : Daniel Pelisek <dpelisek@gmail.com>
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<?xml version='1.0' encoding='utf-8'?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
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
            <div class="panel-heading">Algoritmus Cocke-Younger-Kasami</div>
                <div class="panel-body">
                    <c:if test="${! empty ISString}">
                        <div class="window">
                            <h3 class="transformTitle">Řetězec odpovědníku:</h3>
                            <pre class="isString"><c:out value="${ISString}" /></pre>
                        </div>
                    </c:if>
                    <div class="window">
                        <c:set var="n" value="${fn:length(teacherTable)}" />
                        <div class="row">
                            <div class="col-lg-6">
                                <h3>Vzor:</h3>
                                <table id="table" class="table table-bordered no-border table-min text-center">
                                    <tbody>
                                        <c:forEach var="j" begin="1" end="${n}">
                                            <tr>
                                                <c:forEach var="i" begin="0" end="${j-1}">
                                                    <td class="cyk-cell <c:out value="${teacherTable[i][n-j] eq studentTable[i][n-j] ? 'whitebg' : 'alert-success'}" />">
                                                        <c:forEach items="${teacherTable[i][n-j]}" var="x" varStatus="status" >
                                                            <c:out value="${x}${status.last ? '' : ', '}" />
                                                        </c:forEach>
                                                    </td>
                                                </c:forEach>
                                            </tr>
                                        </c:forEach>
                                        <tr>
                                        <c:forEach var="i" begin="0" end="${fn:length(param.word) - 1}" step="1">
                                            <td class="no-border cyk-last-row cyk-cell "><c:out value="${fn:substring(param.word, i, i + 1)}" /></td>
                                        </c:forEach>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                            <div class="col-lg-6" style="margin-left: 0px;">
                                <h3>Vaše odpověď:</h3>
                                <table id="table" class="table table-bordered no-border table-min text-center">
                                    <tbody>
                                    <c:forEach var="j" begin="1" end="${n}">
                                        <tr>
                                            <c:forEach var="i" begin="0" end="${j-1}">
                                                <td class="cyk-cell <c:out value="${teacherTable[i][n-j] eq studentTable[i][n-j] ? 'whitebg' : 'alert-danger'}" />">
                                                    <c:forEach items="${studentTable[i][n-j]}" var="x" varStatus="status" >
                                                        <c:out value="${x}${status.last ? '' : ', '}" />
                                                    </c:forEach>
                                                </td>
                                            </c:forEach>
                                        </tr>
                                    </c:forEach>
                                    <tr>
                                        <c:forEach var="i" begin="0" end="${fn:length(param.word) - 1}" step="1">
                                            <td class="no-border cyk-last-row cyk-cell"><c:out value="${fn:substring(param.word, i, i + 1)}" /></td>
                                        </c:forEach>
                                    </tr>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                        <h4 class="transformTitle">Zadaná gramatika:</h4>
                        <pre class="whitebg"><c:out value="${param.t}" /></pre>
                        <br>
                        <form method="post" action="cyk.jsp">
                            <c:forEach items="${param}" var="i">
                                <c:if test="${! empty i.value}">
                                    <input type="hidden" name="<c:out value="${i.key}"/>" value="<c:out value="${i.value}" />" />
                                </c:if>
                            </c:forEach>
                            <input value="Zpět" type="submit" class="btn btn-primary" />
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>

