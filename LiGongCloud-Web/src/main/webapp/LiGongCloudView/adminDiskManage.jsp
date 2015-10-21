<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>用户管理</title>
<base href="<%=basePath%>">
<link rel="stylesheet" type="text/css" href="resource/js/jquery-easyui-1.3.3/themes/default/easyui.css">
<link rel="stylesheet" type="text/css" href="resource/js/jquery-easyui-1.3.3/themes/icon.css">
<script type="text/javascript" src="resource/js/jquery-easyui-1.3.3/jquery.min.js"></script>
<script type="text/javascript" src="resource/js/jquery-easyui-1.3.3/jquery.easyui.min.js"></script>
<script type="text/javascript" src="resource/js/jquery-easyui-1.3.3/locale/easyui-lang-zh_CN.js"></script>
<script type="text/javascript" src="resource/js/highcharts/highcharts.js"></script>
<script type="text/javascript" src="resource/js/highcharts/exporting.js"></script>
<style type="text/css">
input {
	width: 80px;
}
</style>
<script type="text/javascript">
var url;

//查询
function searchDisk() {
	$('#dg').datagrid('load', {
		s_user : $('#s_user').val(),
		s_susedSize : $('#s_susedSize').val(),
		s_eusedSize : $('#s_eusedSize').val(),
	});
}

//修改网盘空间
function updateDiskSizeD(){
	var selectedRows = $("#dg").datagrid('getSelections');
	
	if (selectedRows.length == 0) {
		$.messager.alert("系统提示", "请选择要修改的网盘！");
		return;
	}
	if (selectedRows.length > 1) {
		$.messager.alert("系统提示", "请逐条修改网盘！");
		return;
	}
	
	$('#dlgupdateDiskSize').dialog('open').dialog("setTitle", "修改网盘空间");

	$('#u_user').val(selectedRows[0].user);
	$('#u_totalSize').val(selectedRows[0].totalSize);
	
	url = "diskManage/updateDiskSize";
}

function updateDiskSize(){
	$('#updateDiskSizeF').form("submit", {
		url : url,
		onSubmit : function() {
			return $(this).form("validate");
		},
		success : function(result) {
			if (result.errorMsg) {
				$.messager.alert("系统提示", result.errorMsg);
				return;
			} else {
				$.messager.alert("系统提示", "修改成功");
				$("#dlgupdateDiskSize").dialog("close");
				$("#dg").datagrid("reload");
			}
		}
	});
}

function closeupdateDiskSizeDialog(){
	$('#dlgupdateDiskSize').dialog("close");
}

//发送使用建议(与消息管理模块的)
function sendUsedAdvise(){
	
	var selectedRows = $("#dg").datagrid('getSelections');
	
	if (selectedRows.length == 0) {
		$.messager.alert("系统提示", "请选择用户！");
		return;
	}
	if (selectedRows.length > 1) {
		$.messager.alert("系统提示", "请关注每一位不同的用户！");
		return;
	}
	
	$('#dlgSendMessage').dialog('open').dialog("setTitle", "发送使用建议");

	$('#sendToUser').val(selectedRows[0].user);
	$('#sendTitle').val("网盘使用建议");
	$('#sendContext').val("");

	url = "messageManage/sendMessage";
}

function sendMessage(){
	$('#SendMessageF').form("submit", {
		url : url,
		onSubmit : function() {
			return $(this).form("validate");
		},
		success : function(result) {
			var a = eval("("+result+")");
			$.messager.alert("系统提示", a.errorMeg);
			$("#dlgSendMessage").dialog("close");
			$("#dg").datagrid("reload");
		}
	});
}

function closeSendMessageButtonDialog(){
	$('#dlgSendMessage').dialog("close");
}

</script>
</head>
<body>
	<table id="dg" title="" class="easyui-datagrid"
		style="width:700px;height:250px" fitColumns="true" rownumbers="true"
		fit="true" pagination="true" url="diskManage/loadDisk" toolbar="#tb">
		<thead>
			<tr>
				<th field="cb" checkbox="true"></th>
				<th data-options="field:'id'" width="50" >ID</th>
				<th data-options="field:'user'" width="50">网盘所有者</th>
				<th data-options="field:'totalSize'" width="50">网盘总容量(M)</th>
				<th data-options="field:'usedSize'" width="50">以使用容量(M)</th>
				<th data-options="field:'fileNumber'" width="50">文件总数量</th>
				<th data-options="field:'shareNumber'" width="50">文件分享数量</th>
			</tr>
		</thead>
	</table>
	
	<div id="tb">
		<div>
			<a href="javascript:updateDiskSizeD()"
				class="easyui-linkbutton" data-options="iconCls:'icon-edit'"
				plain="true">修改网盘容量</a>
			<a href="javascript:sendUsedAdvise()"
				class="easyui-linkbutton" data-options="iconCls:'icon-edit'"
				plain="true">发送使用建议</a>  
			<!-- <a href="javascript:analyseDiskSize()" -->
			<a href="diskManage/analyseDiskSize"
				class="easyui-linkbutton" data-options="iconCls:'icon-edit'"
				plain="true">分析用户网盘容量使用情况</a>
			<a href="diskManage/analyseFileSave"
				class="easyui-linkbutton" data-options="iconCls:'icon-edit'"
				plain="true">分析用户网盘文件存储情况</a>
			<a href="diskManage/analyseFileShare"
				class="easyui-linkbutton" data-options="iconCls:'icon-edit'"
				plain="true">分析用户网盘文件分享情况</a>
		</div>
		<hr>
		
		<div>
			<form id="search" method="post" enctype="multipart/form-data"> 
				<table>
					<tr>
						<td>&nbsp;&nbsp;网盘所有者:&nbsp;&nbsp;</td>
						<td><input type="text" name="s_user" id="s_user" size="10" /></td>
						<td>&nbsp;&nbsp;网盘使用容量(M):&nbsp;&nbsp;</td>
						<td><input type="text" name="s_susedSize" id="s_susedSize" size="10" /></td>
						<td>&nbsp;&nbsp;至&nbsp;&nbsp;</td>
						<td><input type="text" name="s_eusedSize" id="s_eusedSize" size="10" /></td>
						<td><a href="javascript:searchDisk()" class="easyui-linkbutton"
						data-options="plain:true,iconCls:'icon-search'" plain="true">&nbsp;&nbsp;搜索&nbsp;&nbsp;</a></td>
					</tr>
				</table>
			</form>
		</div>
		<hr>
		
		<!-- highchart -->
		<div id="container" style="width:85%;margin:0 auto;"></div>
	</div>

	<!-- 修改网盘空间 -->
	<div id="dlgupdateDiskSize" class="easyui-dialog"
		style="width:400px;height:200px;padding:20px 10px 0;" closed="true"
		buttons="#updateDiskSizeButton">
		<form id="updateDiskSizeF" method="post">
			<table>
				<tr>
					<td>网盘所有者&nbsp;&nbsp;</td>
					<td><input type="text"  id="u_user" name="u_user" readonly="readonly" style="width:150px;"/></td>
				</tr>
				<tr>
					<td>网盘总容量(M)&nbsp;&nbsp;</td>
					<td><input type="text"  id="u_totalSize" name="u_totalSize" style="width:150px;"/></td>
				</tr>
			</table>
		</form>
	</div>
	<div id="updateDiskSizeButton">
		<a href="javascript:updateDiskSize()" class="easyui-linkbutton" iconCls="icon-ok">修改</a> 
		<a href="javascript:closeupdateDiskSizeDialog()" class="easyui-linkbutton" iconCls="icon-cancel">关闭</a>
	</div>
	
	<!-- 发送消息 -->
	<div id="dlgSendMessage" class="easyui-dialog"
		style="width:350px;height:310px;padding:20px 10px 0;" closed="true"
		buttons="#sendMessageButton">
		<form id="SendMessageF" method="post">
			<table>
				<tr>
					<td>发送给：</td>
					<td><input type="text" id="sendToUser" name="sendToUser" style="width:230px;" readonly="readonly"></td>
				</tr>
				<tr>
					<td>消息名：</td>
					<td><input type="text" id="sendTitle" name="sendTitle" style="width:230px;" readonly="readonly"></td>
				</tr>
				<tr>
					<td>消息内容：</td>
					<td><textarea rows=10 cols=30 id="sendContext" name="sendContext" ></textarea></td>
				</tr>
			</table>
		</form>
	</div>
	<div id="sendMessageButton">
		<a href="javascript:sendMessage()" class="easyui-linkbutton" iconCls="icon-ok">发送</a> 
		<a href="javascript:closeSendMessageButtonDialog()" class="easyui-linkbutton" iconCls="icon-cancel">关闭</a>
	</div>
</body>
</html>
