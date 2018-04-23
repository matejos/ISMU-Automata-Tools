<%-- 
    Document   : resultcomparation
    Created on : 12.4.2009, 14:57:18
    Author     : NICKT
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
            <div class="panel-heading">${windowData[0]}</div>
            <div class="panel-body">
                <pre class="leftPre whitebg"><c:out value="${windowData[2]}" /></pre>
                <pre class="rightPre whitebg"><c:out value="${windowData[1]}" /></pre>
                <div class="innerWindow">
                    <p><c:out value="${windowData[3]}" /></p>
                </div>
                <form method="post" action="indexcfg.jsp">
                    <c:if test="${! empty teacherData}">
                        <input type="hidden" name="teacherData" value="<c:out value="${teacherData}" />" />
                    </c:if>
                    <c:if test="${! empty studentData}">
                        <input type="hidden" name="studentData" value="<c:out value="${studentData}" />" />
                    </c:if>
                    <c:if test="${! empty stud}">
                        <input type="hidden" name="stud" value="<c:out value="${stud}2" />" />
                    </c:if>
                    <c:if test="${! empty mode}">
                        <input type="hidden" name="mode" value="<c:out value="${mode}" />" />
                    </c:if>
                    <input value="Zpět" type="submit" class="btn btn-primary" />
                </form>
            </div>
        </div>
    </div>
    </body>
</html>