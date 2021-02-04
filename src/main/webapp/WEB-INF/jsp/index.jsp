<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, java.text.*" %>
<%@ taglib prefix="custom" uri="http://makestory.net/tld/customTag" %> 
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
<custom:toSpan color="blueviolet" iterNum="5">
	hello
</custom:toSpan>
<hr />
<%
//out.println("Header");
//out.println(response.getHeader("Content-Type"));
//out.close();
%>
<script src="/static/webpack/test.js"></script>
<!-- webjar test //-->
<script src="/webjars/jquery/2.2.1/jquery.min.js"></script>
<script>
console.log($);
</script>
</body>
</html>