<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>Java验证框架测试</title>
  </head>
  
  <body>
  	<h1>${test.username }</h1><br/><!-- 从Model中读取 -->
  	<h1>${test.password }</h1><br/><!-- 从Model中读取 -->
  	<hr/>
  	<h1>${testModel.username }</h1><br/><!-- 从ModelAttribute中读取 -->
  	<h1>${testModel.password }</h1><br/><!-- 从ModelAttribute中读取 -->
  	
  	${org.springframework.validation.BindingResult.validatorTest }
  </body>
</html>