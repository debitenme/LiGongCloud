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

function searchUser() {

	var str1 = $("#s_sjoindate").val();
	var str2 = $("#s_ejoindate").val();
	//var pattern = /^[0-9]{10}$/;
	var pattern = /^\d{4}-\d{2}-\d{2}$/;
	if( str1!="" || str2!="" ){
		if (!pattern.test(str1) || !pattern.test(str2) ) {
			alert("请输入正确格式的时间");
			return false;
		} 
	}

	$('#dg').datagrid('load', {
		s_stuNo : $('#s_stuNo').val(),
		s_sjoindate : $('#s_sjoindate').datebox("getValue"),
		s_ejoindate : $('#s_ejoindate').datebox("getValue"),
	});
}

function deleteUser() {
	var selectedRows = $("#dg").datagrid('getSelections');
	if (selectedRows.length == 0) {
		$.messager.alert("系统提示", "请选择要删除的数据！");
		return;
	}
	var strIds = [];
	for (var i = 0; i < selectedRows.length; i++) {
		strIds.push(selectedRows[i].id);
	}
	var ids = strIds.join(",");
	$.messager.confirm("系统提示", "您确认要删掉这<font color=red>"
			+ selectedRows.length + "</font>条数据吗？", function(r) {
		if (r) {
			$.post("userManage/deleteUser", {
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
function registerUserAddDialog() {
	$('#dlg').dialog('open').dialog("setTitle", "用户信息");
	resetValue();
	url = ("userManage/addUser");
}

function closeUserDialog() {
	$('#dlg').dialog("close");
	resetValue();
}

function resetValue() {
	$('#stuNo').val("");
	$('#password').val("123456");
	$('#username').val("user");
	$('#gender').val("");
	$('#joindate').val("");
	$('#email').val("xx@xx.xx");
	$('#diskSize').val("500");
}

function saveUser() {
	var str = $("#stuNo").val();
	var pattern = /^[0-9]{10}$/;
	if (!pattern.test(str)) {
		alert("请输入正确格式的学号、教师工号");
		return false;
	} 
	
	var emailStr = $("#email").val();
	var emailPattern = /^[\w\-\.]+@[\w\-\.]+(\.\w+)+$/;
	if (!emailPattern.test(emailStr)) {
		alert("请输入正确格式的邮箱");
		return false;
	} 
	
	$('#fm').form("submit", {
		url : url,
		onSubmit : function() {
			return $(this).form("validate");
		},
		success : function(result) {
			var a = eval("("+result+")");
			//var a = eval(result);
			$.messager.alert("系统提示", a.errorMeg);
			resetValue();
			$("#dlg").dialog("close");
			$("#dg").datagrid("reload");
		}
	});
}

function updateUser() {
	var selectedRows = $("#dg").datagrid('getSelections');
	if (selectedRows.length != 1) {
		$.messager.alert("系统提示", "请选择修改的用户");
		return;
	}
	var row = selectedRows[0];

	$("#udlg").dialog("open").dialog("setTitle", "修改用户");

	$("#uid").val(row.id);
	$("#ustuNo").val(row.stuNo);
	$("#upassword").val("");
	$("#uusername").val(row.username);
	$("#uemail").val(row.email);
	$("#ujoindate").datebox("setValue", row.joindate);
	
	$("#udiskSize").val(row.diskSize);
	
	//url = "userManage/updateUser?id=" + row.id;
	url = "userManage/updateUser";
	//url = "userManage/test";
}
///////////////////////////////////////////////////////////////////////////////////////////
function usaveUser() {
	var str = $("#ustuNo").val();
	var pattern = /^[0-9]{10}$/;
	if (!pattern.test(str)) {
		alert("请输入正确格式的学号、教师工号");
		return ;
	} 
	
	var emailStr = $("#uemail").val();
	var emailPattern = /^[\w\-\.]+@[\w\-\.]+(\.\w+)+$/;
	if (!emailPattern.test(emailStr)) {
		alert("请输入正确格式的邮箱");
		return ;
	} 
	
	$("#ufm").form("submit", {
		url : url,
		onSubmit : function() {
			return $(this).form("validate");
		},
		success : function(result) {
			$.messager.alert("系统提示", "修改成功");
			resetValue();
			$("#udlg").dialog("close");
			$("#dg").datagrid("reload");
		}
	});
}
//发送消息
function sendMessageD(){

	var selectedRows = $("#dg").datagrid('getSelections');
	
	if (selectedRows.length == 0) {
		$.messager.alert("系统提示", "请选择用户！");
		return;
	}
	if (selectedRows.length > 1) {
		$.messager.alert("系统提示", "请关注每一位不同的用户！");
		return;
	}
	
	$('#dlgSendMessage').dialog('open').dialog("setTitle", "发送消息");

	$('#sendToUser').val(selectedRows[0].stuNo);
	$('#sendTitle').val("");
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

//导出excel
function exportExcel(){
	$('#search').form("submit", {
		url : "userManage/exportExcel"
	});
}
</script>
</head>
<body>
	<table id="dg" title="" class="easyui-datagrid"
		style="width:700px;height:250px" fitColumns="true" rownumbers="true"
		fit="true" pagination="true" url="userManage/loadUser" toolbar="#tb">
		<thead>
			<tr>
			<!-- hidden="true" -->
				<th field="cb" checkbox="true"></th>
				<th data-options="field:'id'" width="50" >ID</th>
				<th data-options="field:'stuNo'" width="50">学号</th>
				<th data-options="field:'password'" width="50" hidden="true">密码</th>
				<th data-options="field:'username'" width="50">姓名</th>
				<th data-options="field:'email'" width="50">邮箱</th>
				<th data-options="field:'joindate'" width="50">参加日期</th>
				<th data-options="field:'gender'" width="50">性别</th>
				<th data-options="field:'diskSize'" width="50">网盘大小（M）</th>
			</tr>
		</thead>
	</table>
	<div id="tb">
		<div>
			<a href="javascript:registerUserAddDialog()"
				class="easyui-linkbutton" data-options="iconCls:'icon-add'"
				plain="true">注册用户</a> 
			<a href="javascript:updateUser()"
				class="easyui-linkbutton" data-options="iconCls:'icon-add'"
				plain="true">修改用户</a> 
			<a href="javascript:deleteUser()"
				class="easyui-linkbutton" data-options="iconCls:'icon-remove'"
				plain="true">删除用户</a>
			<a href="javascript:sendMessageD()"
				class="easyui-linkbutton" data-options="iconCls:'icon-edit'"
				plain="true">发送消息</a> 
			<a href="javascript:exportExcel()"
				class="easyui-linkbutton" data-options="iconCls:'icon-edit'"
				plain="true">导出excel</a>
		</div>
		<hr>
		<div>
			<form id="search" method="post"> 
				&nbsp;学号:&nbsp;<input type="text" name="s_stuNo" id="s_stuNo" size="10" />
				&nbsp;参加日期:&nbsp;<input type="text" class="easyui-datebox" name="s_sjoindate" id="s_sjoindate" size="50"  width="50" editable="true" style="width:100px;" />
				 					至
								   <input type="text" class="easyui-datebox" name="s_ejoindate" id="s_ejoindate" size="50" width="50" editable="true" style="width:100px;"/>
				<a href="javascript:searchUser()" class="easyui-linkbutton"
						data-options="plain:true,iconCls:'icon-search'" plain="true">搜索</a>
			</form>
		</div>
		<hr>
		<br />
	</div>
	<div id="dlg" class="easyui-dialog"
		style="width:400px;height:250px;padding:20px 10px 0;" closed="true"
		buttons="#dlg-buttons">
		<form id="fm" method="post">
			<table>
				<tr>
					<td>&nbsp;学号:&nbsp;</td>
					<td><input type="text" name="stuNo" id="stuNo"
						class="easyui-validatebox" required="true"></td>
					<td>&nbsp;密码:&nbsp;</td>
					<td><input type="text" name="password" id="password"
						class="easyui-validatebox" required="true"></td>
				</tr>
				<tr>
					<td>&nbsp;姓名:&nbsp;</td>
					<td><input type="text" name="username" id="username"
						class="easyui-validatebox" required=false></td>
					<td>&nbsp;性别:&nbsp;</td>
					<td><select class="easyui-combobox" name="gender" id="gender"
						editable="false" panelHeight="auto" style="width:85px;" > 
							<option value="10">请选择</option>
							<option value="1" selected="selected" >男</option>
							<option value="0">女</option>
					</select></td>
				</tr>
				<tr>
					<td>&nbsp;参加日期:&nbsp;</td>
					<td><input type="text" class="easyui-datebox" name="joindate"
						id="joindate" editable="false" required="true" /></td>
					<td>&nbsp;邮箱:&nbsp;</td>
					<td><input type="text" name="email" id="email"
						class="easyui-validatebox" required="false"></td>
				</tr>
				<tr>
					<td>&nbsp;网盘大小:&nbsp;</td>
					<td><input type="text" name="diskSize" id="diskSize"
						class="easyui-validatebox" required="true"></td>
					<td></td>
					<td></td>
				</tr>
			</table>
		</form>
	</div>
	<div id="dlg-buttons">
		<a href="javascript:saveUser()" class="easyui-linkbutton" iconCls="icon-ok">保存</a> 
		<a href="javascript:closeUserDialog()" class="easyui-linkbutton" iconCls="icon-cancel">关闭</a>
	</div>
	
	<!-- 发送消息 -->
	<div id="dlgSendMessage" class="easyui-dialog"
		style="width:350px;height:310px;padding:20px 10px 0;" closed="true"
		buttons="#sendMessageButton">
		<form id="SendMessageF" method="post">
			<table>
				<tr>
					<td>发送给：</td>
					<td><input type="text" id="sendToUser" name="sendToUser" readonly="readonly" style="width:230px;"></td>
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
	
	
	
	
	
	<div id="udlg" class="easyui-dialog"
		style="width:400px;height:250px;padding:20px 10px 0;" closed="true"
		buttons="#udlg-buttons">
		<form id="ufm" method="post">
			<input type="hidden" name="uid" id="uid"/>
			<table>
				<tr>
					<td>&nbsp;学号:&nbsp;</td>
					<td><input type="text" name="ustuNo" id="ustuNo"
						class="easyui-validatebox" required="true"></td>
					<td>&nbsp;密码:&nbsp;</td>
					<td><input type="text" name="upassword" id="upassword"
						class="easyui-validatebox" required="true"></td>
				</tr>
				<tr>
					<td>&nbsp;姓名:&nbsp;</td>
					<td><input type="text" name="uusername" id="uusername"
						class="easyui-validatebox" required=false></td>
					<td>&nbsp;性别:&nbsp;</td>
					<td><select class="easyui-combobox" name="ugender" id="ugender"
						editable="false" panelHeight="auto" style="width:85px;" > 
							<option value="10">请选择</option>
							<option value="1" selected="selected" >男</option>
							<option value="0">女</option>
					</select></td>
				</tr>
				<tr>
					<td>&nbsp;参加日期:&nbsp;</td>
					<td><input type="text" class="easyui-datebox" name="ujoindate"
						id="ujoindate" editable="false" required="true" /></td>
					<td>&nbsp;邮箱:&nbsp;</td>
					<td><input type="text" name="uemail" id="uemail"
						class="easyui-validatebox" required="false"></td>
				</tr>
				<tr>
					<td>&nbsp;网盘大小:&nbsp;</td>
					<td><input type="text" name="udiskSize" id="udiskSize"
						class="easyui-validatebox" required="true"></td>
					<td></td>
					<td></td>
				</tr>
			</table>
		</form>
	</div>
	<div id="udlg-buttons">
		<a href="javascript:usaveUser()" class="easyui-linkbutton" iconCls="icon-ok">保存</a> 
		<a href="javascript:closeUserDialog()" class="easyui-linkbutton" iconCls="icon-cancel">关闭</a>
	</div>
	
</body>
</html>