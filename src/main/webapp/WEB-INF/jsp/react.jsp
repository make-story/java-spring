<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, java.text.*" %>
<%@ taglib prefix="front" uri="http://makestory.net/tld/manifestTag" %> 
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>react</title>
<!-- 번들 CSS //-->
<front:manifest tool="webpack" type="css" filename="react"></front:manifest>
</head>
<body>
<div id="app"></div> 
<!-- 리액트 테스트 //-->
<div id="root"></div>
<!-- 번들 JS //-->
<front:manifest tool="webpack" type="js" filename="react"></front:manifest>
</body>
</html>