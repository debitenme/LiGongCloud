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
<base href="<%=basePath%>">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="shortcut icon" href="resource/img/favicon.jpg" type="images/x-icon" />
<link rel="stylesheet" type="text/css" href="resource/js/jquery-easyui-1.3.3/themes/default/easyui.css">
<link rel="stylesheet" type="text/css" href="resource/js/jquery-easyui-1.3.3/themes/icon.css">
<script type="text/javascript" src="resource/js/jquery-easyui-1.3.3/jquery.min.js"></script>
<script type="text/javascript" src="resource/js/jquery-easyui-1.3.3/jquery.easyui.min.js"></script>
<script type="text/javascript" src="resource/js/jquery-easyui-1.3.3/locale/easyui-lang-zh_CN.js"></script>
<script type="text/javascript">
$(function() {
	//
	var treeData = [ {
		text : "沈阳理工大学网盘系统",
		children : [ {
			text : "我的文件",
			attributes : {
				url : "LiGongCloudView/adminAndNormalFile.jsp"
			}
		}, {
			text : "我的消息",
			attributes : {
				url : "LiGongCloudView/adminAndNormalInfo.jsp"
			}
		},{
			text : "校内资源",
			attributes : {
				url : "LiGongCloudView/resource.jsp"
			}
		}  ]
	} ];
	//实例化树型
	$("#tree").tree({
		data : treeData,
		lines : true,
		onClick : function(node) {
			if (node.attributes) {
				openTab(node.text, node.attributes.url);
			}
		}
	});
	//新增tab
	function openTab(text, url) {
		if ($("#tabs").tabs('exists', text)) {
			$("#tabs").tabs('select', text);
		} else {
			var content = "<iframe frameborder='0' scrolling='auto' style='width:100%;height:100%;' src="
					+ url + "></iframe>";
			$("#tabs").tabs('add', {
				title : text,
				closable : true,
				content : content
			});
		}
	}
	
	//以下是自己写的js
	$("#gb").hide();
	$("#usernameF").hide();
	$("#passwordF").hide();
	$("#password2F").hide();
	$("#emailF").hide();
	
	$("#updateButton").click(function(){
		$("#gb").show();
		$("#gbt").hide();
	});
	
	$("#notButton").click(function(){
		$("#gb").hide();
		$("#gbt").show();
	});
	
	$("#formSubmit").click(function(){
		var search_str = /^[\w\-\.]+@[\w\-\.]+(\.\w+)+$/;
		var email_val = $("#email").val();
		var p1 = $("#password").val();
		var p2 = $("#password2").val();
		if(!search_str.test(email_val)){       
			alert("邮箱格式不正确");
			$("#email").focus();
			return false;
		}
		if( p1==null){
			alert("请输入密码");
			return false;
		}
		if( p1 != p2 ){
			alert("密码不一致");
			return false;
		}
		$("#updateForm").submit();
	});
	
	$("#username").focus(function(){
		$("#usernameF").show();
	});
	$("#username").focusout(function(){
		$("#usernameF").hide();
	});
	$("#password").focus(function(){
		$("#passwordF").show();
	});
	$("#password").focusout(function(){
		var p1 = $("#password").val();
		if( p1 == "" ){
			$("#passwordF").show();
			return false;
		}
		$("#passwordF").hide();
	});
	$("#password2").focus(function(){
		$("#password2F").show();
	});
	$("#password2").focusout(function(){
		var p1 = $("#password").val();
		var p2 = $("#password2").val();
		if( p1 != p2 ){
			$("#password2F").show();
			return false;
		}
		$("#password2F").hide();
	});
	$("#email").focus(function(){
		$("#emailF").show();
	});
	$("#email").focusout(function(){
		var search_str = /^[\w\-\.]+@[\w\-\.]+(\.\w+)+$/;
		var email_val = $("#email").val();
		if(!search_str.test(email_val)){       
			//$("#email").focus();
			$("#emailF").show();
			return false;
		}
		$("#emailF").hide();
	});
});
</script>
<title>沈阳理工大学网盘系统</title>
</head>
<body class="easyui-layout">

<!-- head start -->
	<div data-options="region:'north'"
		style="height:100px;background-color:white">
		<img alt="" src="resource/img/main.jpg"
			style="height:98px;width:700px;float:left;">
		<div style="margin:10px 10px 20px;float:right;">
			当前登录用户学号： &nbsp;<font color="red">${uBean.stuNo}</font>
			<h3><a href="login/login">安全退出</a></h3>
		</div>
	</div>
<!-- head end -->

<!-- 信息 start -->
	<div data-options="region:'south'" style="height:30px;padding:5px;"
		align="center">友情链接：<a href="http://218.25.35.28/" >沈阳理工大学教学网</a></div>
	<div data-options="region:'west',split:true" title="导航菜单"
		style="width:200px;">
		<ul id="tree" class="easyui-tree"></ul>
	</div>
<!-- 信息 end -->	

<!-- 主页面 start -->
	<div data-options="region:'center'">
		<div class="easyui-tabs" fit="true" border="false" id="tabs">
			<div title="个人信息" style="padding:10px">
<!-- 基本信息 start -->				
				<div style="position:absolute;left:30px;height:210px;width:220px;display:inline;background:#E8E8E8;" >
					<h3>您的基本信息</h3>
					    <table>
					    	<tr><td>学号： </td>  <td> ${uBean.stuNo}</td></tr>
					    	<tr><td>姓名： </td>  <td> ${uBean.username}</td></tr>
					    	<tr><td>邮箱： </td>  <td> ${uBean.email}</td></tr>
					    	<tr><td>性别： </td>  <td> ${uBean.gender}</td> </tr>
					    	<tr><td>参加日期： </td> <td>${uBean.joindate}</td></tr>
					    	<tr>
					    		<td><br/><input type="button" value="修改信息 " id="updateButton" > </td>
					    		<td><br/><input type="button" value="取消修改 " id="notButton" ></td>
					    	</tr>
				    </table>
    			</div>
<!-- 基本信息 end -->	
    			
<!-- 消息提示 start -->    			
    			<!-- <div style="position:absolute;top:480px; left:30px;height:60px;width:350px;display:inline;background:#99FFFF;">
    				<h3>您有<font color="red" size="6">xx</font>条新消息，请点击“我的消息”进行查看</h3>
    			</div> -->
<!-- 消息提示 end -->

<!-- 修改信息 start -->    			
    			<div id="gb"  style="position:absolute;  left:300px;height:430px;width:800px;display:inline;background-image: url(resource/img/ligong.jpg);">
    				<form action="userManage/reviseUser" method="post" id="updateForm" >
						<input type="hidden" value="${uBean.id}" id="id" name="id" >
						<h3>修改信息</h3>
					    <table>
					    	<tr><td>姓名： </td>  <td> <input type="text" value="${uBean.username}" id="username" name="username" ></td><td><span id="usernameF"><font color="red">姓名最好是真名</font> </span></td></tr>
					    	<tr><td>密码： </td>  <td> <input type="password" value="" id="password" name="password" ></td><td><span id="passwordF"><font color="red">请输入密码</font> </span></td></tr>
					    	<tr><td>确认密码： </td>  <td> <input type="password" value="" id="password2" name="password2" ></td><td><span id="password2F"><font color="red">请确保密码一致</font> </span></td></tr>
					    	<tr><td>邮箱： </td>  <td> <input type="text" value="${uBean.email}" id="email" name="email" ></td><td><span id="emailF"><font color="red">请输入正确的邮箱格式</font> </span></td></tr>
					    	<tr>
						    	<td>性别： </td>  
						    	<td> 
							    	<select name="gender" id="gender">
										<option value="">请选择</option>
										<option value="1" selected="selected" >男</option>
										<option value="0">女</option>
									</select>
								</td> 
							</tr>
					    	<tr>
					    		<td><input type="button" id="formSubmit" value="保存 " > </td>
					    		<td></td>
					    	</tr>
					    </table>
    				</form>
    			</div>
<!-- 修改信息 end -->

<!-- 网盘信息 start -->
				<div style="position:absolute;top:260px;left:30px;height:210px;width:220px;display:inline;background:#E8E8E8;">
					<h3>您的网盘信息</h3>
					<table>
				    	<tr><td>网盘总容量(M)： </td>  <td> ${dBean.totalSize}</td></tr>
				    	<tr><td>已使用容量： </td>  <td> ${dBean.usedSize}</td></tr>
				    	<tr><td>存储文件数量： </td>  <td> ${dBean.fileNumber}</td></tr>
				    	<tr><td>分享文件数量： </td>  <td> ${dBean.shareNumber}</td> </tr>
				    </table>
				</div>
<!-- 网盘信息 end -->

<!-- 背景 start background-image:resource/img/main.jpg   	-->   		
    			<div id="gbt" style="position:absolute;left:300px;height:430px;width:800px;display:inline;background-image: url(resource/img/ligong.jpg);">
<!-- 背景 end -->


			</div>
		</div>
	</div>
<!-- 主页面stop -->
	
</body>
</html>