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
		var treeData = [ {
			text : "沈阳理工大学网盘管理系统",
			children : [ {
				text : "用户管理",
				attributes : {
					url : "LiGongCloudView/adminUserManage.jsp"
				}
			}, {
				text : "网盘管理",
				attributes : {
					url : "LiGongCloudView/adminDiskManage.jsp"
				}
			},{
				text : "文件管理",
				attributes : {
					url : "LiGongCloudView/adminAndNormalFile.jsp"
				}
			}, {
			text : "消息管理",
			attributes : {
				url : "LiGongCloudView/adminAndNormalInfo.jsp"
			}
		} , {
			text : "校内资源",
			attributes : {
				url : "LiGongCloudView/resource.jsp"
			}
		} ]
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
	});
</script>
<title>沈阳理工大学网盘管理系统</title>
</head>
<body class="easyui-layout">

<!-- head start -->
	<div data-options="region:'north'"
		style="height:100px;background-color:white">
		<img alt="" src="resource/img/mainAdmin.jpg"
			style="height:98px;width:700px;float:left;">
		<div style="margin:10px 10px 20px;float:right;">
			当前登录用户：&nbsp;<font color="red">管 理 员</font>
			<h3><a href="login/login">安全退出</a></h3>
		</div>
	</div>
<!-- head end -->

<!-- 信息 start -->
	<div data-options="region:'south'" style="height:30px;padding:5px;"
		align="center">友情链接：<a href="http://218.25.35.28/" >沈阳理工大学教学网</a></div>
	<div data-options="region:'west',split:true" title="为您导航"
		style="width:200px;">
		<ul id="tree" class="easyui-tree"></ul>
	</div>
<!-- 信息 end -->	

<!-- 主页面 start -->
	<div data-options="region:'center'">
		<div class="easyui-tabs" fit="true" border="false" id="tabs">
			<div title="首页" style="padding:10px">
				<div style="position:absolute;left:50px;padding-top:100px;">
					<font color="red" size="7">欢迎使用</font>
				</div>
				<div id="gbt" style="position:absolute;left:300px;height:430px;width:800px;display:inline;background-image: url(resource/img/ligong.jpg);">
				</div>
			</div>
		</div>
	</div>
<!-- 主页面stop -->
	 
	
</body>
</html>