<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>沈阳理工大学网盘系统</title>
<base href="<%=basePath%>">
<meta charset="UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<link rel="shortcut icon" href="resource/img/favicon.jpg"
	type="images/x-icon" />
<link rel="stylesheet" href="resource/css/bootstrap.min.css" />
<link rel="stylesheet" href="resource/css/bootstrap-responsive.min.css" />
<link rel="stylesheet" href="resource/css/matrix-login.css" />
<link rel="stylesheet" href="resource/css/login.css">
<script type="text/javascript" src="resource/js/jquery-easyui-1.3.3/jquery.min.js"></script>
<script type="text/javascript" src="resource/js/jquery-easyui-1.3.3/jquery.easyui.min.js"></script>
<script type="text/javascript" src="resource/js/jquery-easyui-1.3.3/locale/easyui-lang-zh_CN.js"></script>
<style type="text/css">
body {
	background-image: url("resource/img/sky.png");
}
</style>

<script type="text/javascript">

$(function() {
	$("#normal").change(function(){
		$("#username").attr("placeholder","学号");
	});
	$("#admin").change(function(){
		$("#username").attr("placeholder","用户名");
	});
});

</script>

</head>
<body>
	<section class="container">
	<div class="logosite">
		<img src="resource/img/logo_login.png" alt="Logo" />
	</div>
	<div class="login">
		<form id="loginform" name="loginform" class="form-vertical"
			method="post" action="login/login">
			<p class="login_type">
				<label id="role" > 
					<input type="radio" id="normal" name="loginType" checked="checked" value="normal">用户登陆
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
					<input type="radio" id="admin" name="loginType" value="admin">管理员登陆
				</label>
			</p>
			<p>
				<input type="text" name="username" id="username" value="" placeholder="学号或教师工号">
			</p>
			<p>
				<input type="password" name="password" value="" placeholder="密码">
			</p>
			<p class="submit">
				<input type="submit" name="commit" value="登陆">
			</p>
		</form>
	</div>
	</section>
</body>
</html>
