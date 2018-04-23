<%-- 
    Document   : result
    Created on : 11.4.2009, 14:37:31
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
            <div class="panel-heading">Převod</div>
            <div class="panel-body">
                    <h3 class="transformTitle">Vstupní gramatika:</h3>
                    <pre class="whitebg"><c:out value="${inputData}"/></pre>
                <c:if test="${! empty ISString}">
                    <div class="window">
                        <h3 class="transformTitle">Řetězec odpovědníku:</h3>
                        <pre class="isString whitebg"><c:out value="${ISString}" /></pre>
                    </div>
                </c:if>
                <div class="window">
                    <c:forEach items="${windowData}" var="i">
                    <h3 class="transformTitle"><c:out value="${i.key}"/></h3>
                    <pre class="whitebg"><c:out value="${i.value}"/></pre>
                    </c:forEach>
                    <form method="post" action="convertcfg.jsp">
                        <c:if test="${! empty inputData}">
                            <input type="hidden" name="inputData" value="<c:out value="${inputData}" />" />
                        </c:if>
                        <c:if test="${! empty stud}">
                            <input type="hidden" name="stud" value="<c:out value="${stud}" />" />
                        </c:if>
                        <c:if test="${! empty generateISString}">
                            <input type="hidden" name="generateISString" value="<c:out value="${generateISString}" />" />
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

