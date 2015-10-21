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
<style type="text/css">
input {
	width: 80px;
}
</style>
<script type="text/javascript">
var url;

//查询
function searchMessage() {
	$('#dg').datagrid('load', {
		s_title : $('#s_title').val(),
		s_userTo : $('#s_userTo').val(),
		s_toUser : $('#s_toUser').val(),
		s_smessageDate : $('#s_smessageDate').datebox("getValue"),
		s_emessageDate : $('#s_emessageDate').datebox("getValue"),
	});
}


//标记消息为已处理
function dealOkMessage() {
	var selectedRows = $("#dg").datagrid('getSelections');
	if (selectedRows.length == 0) {
		$.messager.alert("系统提示", "请选择要处理的消息！");
		return;
	}
	if (selectedRows.length > 1) {
		$.messager.alert("系统提示", "请注意每一条消息！");
		return;
	}
	var strIds = [];
	for (var i = 0; i < selectedRows.length; i++) {
		strIds.push(selectedRows[i].id);
	}
	var ids = strIds.join(",");
	$.messager.confirm("系统提示", "您确认要标记这<font color=red>"
			+ selectedRows.length + "</font>个消息为以处理状态吗？", function(r) {
		if (r) {
			$.post("messageManage/dealOkMessage", {
				delIds : ids
			}, function(result) {
				var a = eval(result);
				$.messager.alert("系统提示", a.errorMeg);
				$("#dg").datagrid("reload");
				
			}, "json");
		}
	});
}

//标记消息为未处理
function dealNotMessage() {
	var selectedRows = $("#dg").datagrid('getSelections');
	if (selectedRows.length == 0) {
		$.messager.alert("系统提示", "请选择要处理的消息！");
		return;
	}
	if (selectedRows.length > 1) {
		$.messager.alert("系统提示", "请注意每一条消息！");
		return;
	}
	var strIds = [];
	for (var i = 0; i < selectedRows.length; i++) {
		strIds.push(selectedRows[i].id);
	}
	var ids = strIds.join(",");
	$.messager.confirm("系统提示", "您确认要标记这<font color=red>"
			+ selectedRows.length + "</font>个消息为未处理状态吗？", function(r) {
		if (r) {
			$.post("messageManage/dealNotMessage", {
				delIds : ids
			}, function(result) {
				var a = eval(result);
				$.messager.alert("系统提示", a.errorMeg);
				$("#dg").datagrid("reload");
				
			}, "json");
		}
	});
}
//查看消息详细信息
function lookMessageD(){
	var selectedRows = $("#dg").datagrid('getSelections');
	if (selectedRows.length == 0) {
		$.messager.alert("系统提示", "请选择要查看的消息！");
		return;
	}
	if (selectedRows.length > 1) {
		$.messager.alert("系统提示", "请逐条消息进行详细查看！");
		return;
	}
	$('#dlglookMessage').dialog('open').dialog("setTitle", "消息详情");
	$('#l_title').val(selectedRows[0].title);
	$('#l_context').val(selectedRows[0].content);
}
function closelookMessageDialog(){
	$('#dlglookMessage').dialog("close");
}

//发送消息
function sendMessageD(){
	$('#dlgSendMessage').dialog('open').dialog("setTitle", "发送消息");
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
//删除消息
function deleteMessage() {
	var selectedRows = $("#dg").datagrid('getSelections');
	if (selectedRows.length == 0) {
		$.messager.alert("系统提示", "请选择要删除的消息！");
		return;
	}
	var strIds = [];
	for (var i = 0; i < selectedRows.length; i++) {
		strIds.push(selectedRows[i].id);
	}
	var ids = strIds.join(",");
	$.messager.confirm("系统提示", "您确认要删掉这<font color=red>"
			+ selectedRows.length + "</font>个消息吗？", function(r) {
		if (r) {
			$.post("messageManage/deleteMessage", {
				delIds : ids
			}, function(result) {
				if (result.success) {
					$.messager.alert("系统提示", "<font color=red> 删除成功 ！！ </font>");
					$("#dg").datagrid("reload");
				} else {
					$.messager.alert("系统提示", '<font color=red>'
							+ selectedRows[result.errorIndex].name
							+ '</font>' + result.errorMsg);
				}
			}, "json");
		}
	});
}

</script>
</head>
<body>
	<table id="dg" title="" class="easyui-datagrid"
		style="width:700px;height:250px" fitColumns="true" rownumbers="true"
		fit="true" pagination="true" url="messageManage/loadMessage" toolbar="#tb">
		<thead>
			<tr>
				<th field="cb" checkbox="true"></th>
				<th data-options="field:'id'" width="50" >ID</th>
				<th data-options="field:'state'" width="50">消息状态</th>
				<th data-options="field:'userTo'" width="50">消息发送者</th>
				<th data-options="field:'toUser'" width="50">消息接收者</th>
				<th data-options="field:'title'" width="50">消息名</th>
				<th data-options="field:'content'" width="50">消息内容</th>
				<th data-options="field:'messageDate'" width="50">消息时间</th>
			</tr>
		</thead>
	</table>
	<div id="tb">
		<div>
			<a href="javascript:dealOkMessage()"
				class="easyui-linkbutton" data-options="iconCls:'icon-edit'"
				plain="true">标记消息为已处理</a>  
			<a href="javascript:dealNotMessage()"
				class="easyui-linkbutton" data-options="iconCls:'icon-edit'"
				plain="true">标记消息为未处理</a>
			<a href="javascript:lookMessageD()"
				class="easyui-linkbutton" data-options="iconCls:'icon-edit'"
				plain="true">查看消息详细信息</a>  
			<a href="javascript:sendMessageD()"
				class="easyui-linkbutton" data-options="iconCls:'icon-add'"
				plain="true">发送消息</a>
			<a href="javascript:deleteMessage()"
				class="easyui-linkbutton" data-options="iconCls:'icon-remove'"
				plain="true">删除消息</a>
		</div>
		<hr>
		
		<div>
			<form id="search" method="post" enctype="multipart/form-data"> 
				<table>
					<tr>
						<td>&nbsp;&nbsp;消息名:&nbsp;&nbsp;</td>
						<td><input type="text" name="s_title" id="s_title" size="10" /></td>
						<td>&nbsp;&nbsp;消息发送者:&nbsp;&nbsp;</td>
						<td><input type="text" name="s_userTo" id="s_userTo" size="10" /></td>
						<td>&nbsp;&nbsp;消息接收者:&nbsp;&nbsp;</td>
						<td><input type="text" name="s_toUser" id="s_toUser" size="10" /></td>
						<td>&nbsp;&nbsp;消息日期:</td>
						<td><input type="text" class="easyui-datebox" name="s_smessageDate" id="s_smessageDate" size="50"  width="50" editable="true" style="width:100px;"/></td>
				 					<td>&nbsp;&nbsp;至&nbsp;&nbsp;</td>
								   <td><input type="text" class="easyui-datebox" name="s_emessageDate" id="s_emessageDate" size="50" width="50" editable="true" style="width:100px;"/></td>
						<td><a href="javascript:searchMessage()" class="easyui-linkbutton"
						data-options="plain:true,iconCls:'icon-search'" plain="true">&nbsp;&nbsp;搜索&nbsp;&nbsp;</a></td>
					</tr>
				</table>
			</form>
		</div>
		<hr>
	</div>
	
	<!-- lookMessage -->
	<div id="dlglookMessage"  class="easyui-dialog"
		style="width:400px;height:300px;padding:20px 10px 0;" closed="true"
		buttons="#lookMessageButton">
		<table>
			<tr>
				<td>消息名</td>
				<td><input type="text" readonly="readonly" id="l_title" style="width:230px;"></td>
			</tr>
			<tr>
				<td>消息详细内容</td>
				<td><textarea rows=10 cols=30 readonly="readonly" id="l_context"></textarea></td>
			</tr>
		</table>
	</div>
	<div id="lookMessageButton">
		<a href="javascript:closelookMessageDialog()" class="easyui-linkbutton" iconCls="icon-cancel">关闭</a>
	</div> 
	
	
	<!-- 发送消息 -->
	<div id="dlgSendMessage" class="easyui-dialog"
		style="width:400px;height:310px;padding:20px 10px 0;" closed="true"
		buttons="#sendMessageButton">
		<form id="SendMessageF" method="post">
			<table>
				<tr>
					<td>发送给：</td>
					<td><input type="text" id="sendToUser" name="sendToUser" style="width:230px;"></td>
				</tr>
				<tr>
					<td>消息名：</td>
					<td><input type="text" id="sendTitle" name="sendTitle" style="width:230px;"></td>
				</tr>
				<tr>
					<td>消息内容：</td>
					<td><textarea rows=10 cols=30 id="sendContext" name="sendContext"></textarea></td>
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