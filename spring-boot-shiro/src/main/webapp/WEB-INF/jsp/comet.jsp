<%@ page pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>长连接测试</title>
    <script type="text/javascript" src="${pageContext.request.contextPath }/webjarslocator/jquery/jquery.js"></script>
    <script type="text/javascript">
    	$(function(){
			function longPolling(){
				$.getJSON('${pageContext.request.contextPath }/xs/cometservlet', function(data){
					console.log(data.result);
					$('#n1').html(data.result);
					longPolling();
				});
			}
			longPolling();
			
			function longPolling2(){
				$.get('${pageContext.request.contextPath }/async/test', function(data){
					console.log(data);
					$('#n2').html(data);
					longPolling2();
				});
			}
			longPolling2();
    	});
    </script>
  </head>
  
  <body>
  	<h1>长连接测试</h1>
  	<h2 id="n1"></h2>
  	<h2 id="n2"></h2>
  </body>
</html>