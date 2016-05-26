<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>Java验证框架测试</title>
  </head>
  
  <body>
  	<h2>test2</h2>
  	<form:form method="post" modelAttribute="testModel" action="${pageContext.request.contextPath }/validator/test2">
  		<form:input path="username" /><br/>
  		<form:input path="password" /><br/>
  		<input type="submit" value="提交"/>
  	</form:form>
  	<hr>
  	<h2>test3</h2>
  	<form:form method="post" modelAttribute="testModel" action="${pageContext.request.contextPath }/validator/test3">
  		<h1><form:errors path="username" /></h1>
  		<h1><form:errors path="password" /></h1>
  		<form:input path="username" /><br/>
  		<form:input path="password" /><br/>
  		<input type="submit" value="提交"/>
  	</form:form>
  </body>
</html>