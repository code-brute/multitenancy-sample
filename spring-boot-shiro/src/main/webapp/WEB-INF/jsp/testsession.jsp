<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>Test Session</title>
</head>

<body>
<%
String name = request.getParameter("username");
if(name != null)
	session.setAttribute("username", name);

Object obj = session.getAttribute("username");
if(obj != null){
	out.println(obj);
}
%>
</body>
</html>
