<%--
  Created by IntelliJ IDEA.
  User: sunpeng
  Date: 2017/9/13
  Time: 23:57
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/" + path;
%>
<html>
<head>
    <title>登录页</title>
</head>
<script type="text/javascript" src="js/jquery-3.2.1.min.js"></script>
<script type="text/javascript">
    function getUrl() {
        var url=document.location.href;
        var url1=url.split("=")[1];
        console.log(url1);
        $("#target").val(url1);
    }
</script>
<body>
<form name="for" action="/user/login" method="get">
    <input  name="name" type="text" value=""/>
    <input  name="pwd" type="text" value=""/>
    <input  id="target" name="target" type="text" value="" style="display: none"/>
    <input id="btn" type="submit" onclick="getUrl();" value="登录"/>
</form>
</body>
</html>
