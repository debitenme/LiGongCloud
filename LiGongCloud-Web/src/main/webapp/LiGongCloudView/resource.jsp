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

//查找文件
function searchFile() {
	$('#dg').datagrid('load', {
		name : $('#name').val(),
		userStuNo : $('#userStuNo').val(),
		s_screateDate : $('#s_screateDate').datebox("getValue"),
		s_ecreateDate : $('#s_ecreateDate').datebox("getValue"),
	});
}

//查看文件详细信息
function lookFileD(){
	var selectedRows = $("#dg").datagrid('getSelections');
	if (selectedRows.length == 0) {
		$.messager.alert("系统提示", "请选择要查看的文件！");
		return;
	}
	if (selectedRows.length > 1) {
		$.messager.alert("系统提示", "请逐条文件进行详细查看！");
		return;
	}
	$('#dlglookFile').dialog('open').dialog("setTitle", "文件详情");

	$('#l_name').val(selectedRows[0].name);
	$('#l_userStuNo').val(selectedRows[0].userStuNo);
	$('#l_createDate').val(selectedRows[0].createDate);
	$('#l_type').val(selectedRows[0].type);
	$('#l_size').val(selectedRows[0].size);
	$('#l_isShare').val(selectedRows[0].isShare);
	$('#l_isLock').val(selectedRows[0].isLock);
	$('#l_shareDownload').val(selectedRows[0].shareDownload);
	$('#l_description').val(selectedRows[0].description);
}
function closelookFileDialog(){
	$('#dlglookFile').dialog("close");
}

//发送分享请求
function sendMessage(){
	var selectedRows = $("#dg").datagrid('getSelections');
	if (selectedRows.length == 0) {
		$.messager.alert("系统提示", "请选择请求分享的文件！");
		return;
	}
	if (selectedRows.length > 1) {
		$.messager.alert("系统提示", "请详细阅读每一个文件！");
		return;
	}
	var strIds = [];
	for (var i = 0; i < selectedRows.length; i++) {
		strIds.push(selectedRows[i].id);
	}
	//var ids = strIds.join(",");
	$.messager.confirm("系统提示", 
		" 确定向"+selectedRows[0].userStuNo+"请求分享？ ", 
		function(r) {
		if (r) {
			$.post("messageManage/sendMessage", {
				sendToUser : selectedRows[0].userStuNo,
				sendTitle  : "分享请求",
				sendContext: "您好！由于学习需要，非常想借鉴您的"+selectedRows[0].name+"这个文件，请分享给我,谢谢！！"
			}, function(result) {
				var a = eval(result);
				$.messager.alert("系统提示", a.errorMeg);
				$("#dg").datagrid("reload");
				
			}, "json");
		}
	});
}
</script>
</head>
<body>
	<table id="dg" title="" class="easyui-datagrid"
		style="width:700px;height:250px" fitColumns="true" rownumbers="true"
		fit="true" pagination="true" url="resource/loadResource" toolbar="#tb">
		<thead>
			<tr>
				<th field="cb" checkbox="true"></th>
				<th data-options="field:'id'" width="30" >ID</th>
				<th data-options="field:'userStuNo'" width="50" >文件拥有者</th>
				<th data-options="field:'name'" width="50">文件名</th>
				<th data-options="field:'type'" width="50">文件类型</th>
				<th data-options="field:'size'" width="50">文件大小(M)</th>
				<th data-options="field:'createDate'" width="50">上传日期</th>
				<th data-options="field:'isLock'" width="50">文件是否加密</th>
				<th data-options="field:'isShare'" width="50">文件是否分享</th>
				<th data-options="field:'shareDownload'" width="50">分享次数</th>
				<th data-options="field:'description'" width="50">备注</th>
			</tr>
		</thead>
	</table>
	
	<div id="tb">
		<div>
			<a href="javascript:lookFileD()"
				class="easyui-linkbutton" data-options="iconCls:'icon-remove'"
				plain="true">查看文件</a>
			<a href="javascript:sendMessage()"
				class="easyui-linkbutton" data-options="iconCls:'icon-remove'"
				plain="true">请求分享</a>
		</div>
		<hr>
		<div>
			<form id="search" method="post" enctype="multipart/form-data"> 
				<table>
					<tr>
						<td>&nbsp;&nbsp;文件名:&nbsp;&nbsp;</td>
						<td><input type="text" name="name" id="name" size="10" /></td>
						
						<td>&nbsp;&nbsp;文件拥有者:&nbsp;&nbsp;</td>
						<td><input type="text" name="userStuNo" id="userStuNo" size="10" /></td>
						
						<td>&nbsp;&nbsp;上传日期:</td>
						<td><input type="text" class="easyui-datebox" name="s_screateDate" id="s_screateDate" size="50"  width="50" editable="true" style="width:100px;"/></td>
				 					<td>&nbsp;&nbsp;至&nbsp;&nbsp;</td>
								   <td><input type="text" class="easyui-datebox" name="s_ecreateDate" id="s_ecreateDate" size="50" width="50" editable="true" style="width:100px;"/></td>
						
						<td><a href="javascript:searchFile()" class="easyui-linkbutton"
						data-options="plain:true,iconCls:'icon-search'" plain="true">&nbsp;&nbsp;搜索&nbsp;&nbsp;</a></td>
					</tr>
				
				</table>
			</form>
		</div>
	</div>	
	
	<!-- lookFile -->
	<div id="dlglookFile"  class="easyui-dialog"
		style="width:450px;height:400px;padding:20px 10px 0;" closed="true"
		buttons="#lookFileButton">
		<table>
			<tr>
				<td>文件名</td>
				<td colspan="3"><input type="text" readonly="readonly" id="l_name" style="width:260px;"></td>
			</tr>
			<tr>
				<td>文件拥有者</td>
				<td colspan="3"><input type="text" readonly="readonly" id="l_userStuNo" style="width:260px;"></td>
			</tr>
			<tr>
				<td>文件类型</td>
				<td><input type="text" readonly="readonly" id="l_type"></td>
				<td>文件大小</td>
				<td>&nbsp;<input type="text" readonly="readonly" id="l_size"></td>
			</tr>
			<tr>
				<td>文件是否分享</td>
				<td><input type="text" readonly="readonly" id="l_isShare"></td>
				<td>文件分享次数</td>
				<td>&nbsp;<input type="text" readonly="readonly" id="l_shareDownload"></td>
			</tr>
			<tr>
				<td>文件是否加密</td>
				<td><input type="text" readonly="readonly" id="l_isLock"></td>
				<td>文件上传时间</td>
				<td>&nbsp;<input type="text" readonly="readonly" id="l_createDate"></td>
			</tr>
			<tr>
				<td>文件详细内容</td>
				<td colspan="3"><textarea rows=10 cols=35 readonly="readonly" id="l_description"></textarea></td>
			</tr>
		</table>
	</div>
	<div id="lookFileButton">
		<a href="javascript:closelookFileDialog()" class="easyui-linkbutton" iconCls="icon-cancel">关闭</a>
	</div> 
		
		
</body>
</html>