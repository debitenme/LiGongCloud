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

//查找文件
function searchFile() {
	$('#dg').datagrid('load', {
		name : $('#name').val(),
		s_screateDate : $('#s_screateDate').datebox("getValue"),
		s_ecreateDate : $('#s_ecreateDate').datebox("getValue"),
	});
}

//删除文件
function deleteFile() {
	var selectedRows = $("#dg").datagrid('getSelections');
	
	if (selectedRows.length == 0) {
		$.messager.alert("系统提示", "请选择要删除的文件！");
		return;
	}
	var strIds = [];
	for (var i = 0; i < selectedRows.length; i++) {
		if(  "是" == selectedRows[i].isLock ){
			$.messager.alert("系统提示", "您选择的文件中有加密文件，请先解密！");
			return;
		}
		strIds.push(selectedRows[i].id);
	}
	var ids = strIds.join(",");
	$.messager.confirm("系统提示", "您确认要删掉这<font color=red>"
			+ selectedRows.length + "</font>个文件吗？", function(r) {
		if (r) {
			$.post("fileManage/deleteFile", {
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

//下载文件
function downloadFileA(){
	var selectedRows = $("#dg").datagrid('getSelections');
	if (selectedRows.length == 0) {
		$.messager.alert("系统提示", "请选择要下载的文件！");
		return;
	}
	
	if (selectedRows.length > 1) {
		$.messager.alert("系统提示", "请逐个文件下载！");
		return;
	}
	
	if (selectedRows[0].isLock=="是"){
		$("#lockPasswordMessage").hide();
		$('#dlgDownloadLock').dialog('open').dialog("setTitle", "请输入文件密码");
		$('#lockFileId').val(selectedRows[0].id);
		url = "fileManage/downloadLockFile";
	}else{
		downloadFile();
	}
}

function downloadFile() {

	var selectedRows = $("#dg").datagrid('getSelections');
	var strIds = [];
	for (var i = 0; i < selectedRows.length; i++) {
		strIds.push(selectedRows[i].id);
	}
	var ids = strIds.join(",");
	
	var overlay ="true";
	
	var action = "fileManage/downloadFile?delIds="+ids+"&&overlay="+overlay;
	$("#downloadF").attr("action",action);
	$("#downloadF").submit();
}

function downloadLockFile(){
	var p1 = $("#lockPassword").val();
	
	if(p1 == "" ){
		$("#lockPasswordMessage").show();
		return;
	}
	
	$('#downloadLockF').form("submit", {
		url : url,
		onSubmit : function() {
			return $(this).form("validate");
		},
		success : function(result) {
			var a = eval("("+result+")");
			
			if(a.success == 'true'){
				$("#dlgDownloadLock").dialog("close");
				//$('#dlgDownload').dialog('open').dialog("setTitle", "下载文件");
				downloadFile();
			}else{
				$.messager.alert("系统提示", a.errorMeg);
			}
		}
	});
}

function closeDownloadLockDialog(){
	$('#dlgDownloadLock').dialog("close");
}

function closeDownloadDialog(){
	$('#dlgDownload').dialog("close");
}

//分享文件
function shareFileD() {
	var selectedRows = $("#dg").datagrid('getSelections');
	if (selectedRows.length == 0) {
		$.messager.alert("系统提示", "请选择要分享的文件！");
		return;
	}
	$('#dlgShare').dialog('open').dialog("setTitle", "分享文件");
}
function shareFile() {

	var selectedRows = $("#dg").datagrid('getSelections');
	if (selectedRows.length == 0) {
		$.messager.alert("系统提示", "请选择要分享的文件！");
		return;
	}
	var strIds = [];
	for (var i = 0; i < selectedRows.length; i++) {
		strIds.push(selectedRows[i].id);
	}
	var ids = strIds.join(",");
	
	/* var shareNo = $("#shareNo").val();
	var action="fileManage/shareFile?delIds="+ids+"&&shareNo="+shareNo+"&&overlay=true";
	$("#shareF").attr("action",action);
	$("#shareF").submit();
	$('#dlgShare').dialog("close"); */
	url="fileManage/shareFile?delIds="+ids+"&&shareNo="+shareNo+"&&overlay=true";
	$('#shareF').form("submit", {
		url : url,
		onSubmit : function() {
			return $(this).form("validate");
		},
		success : function(result) {
			//转化为json数据
			var a = eval("("+result+")");
	
			$.messager.alert("系统提示", a.errorMeg);
			if(a.success == 'true'){
				$("#dg").datagrid("reload");
				$("#dlgShare").dialog("close");
			}
		}
	});
}

function closeFileDialog() {
	$('#dlg').dialog("close");
}

function closeShareDialog() {
	$('#dlgShare').dialog("close");
}

//上传文件
function uploadFile() {
	$('#dlg').dialog('open').dialog("setTitle", "上传文件");
}
function saveFile() {
	$('#dlg').dialog("close");
	$("#uploadF").submit();
	//alert("文件上传成功");

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

//加密文件
function lockFileD(){

	var selectedRows = $("#dg").datagrid('getSelections');
	$("#passwordMessage").hide();
	if (selectedRows.length == 0) {
		$.messager.alert("系统提示", "请选择要加密的文件！");
		return;
	}
	if (selectedRows.length > 1) {
		$.messager.alert("系统提示", "请逐条文件进行加密！");
		return;
	}
	$('#dlgLockFile').dialog('open').dialog("setTitle", "加密文件");
	
	$('#lick_title').val(selectedRows[0].name);
	$('#fileId').val(selectedRows[0].id);
	
	url = "fileManage/lockFile";
	
}

function lockFile(){
	
	var p1 = $("#lick_password").val();
	var p2 = $("#lick_password2").val();
	
	if( p1 != p2 || p1=="" || p2 == "" ){
		$("#passwordMessage").show();
		return;
	}
	
	$('#lockFileF').form("submit", {
	url : url,
	onSubmit : function() {
		return $(this).form("validate");
	},
	success : function(result) {
		if (result.errorMsg) {
			$.messager.alert("系统提示", result.errorMsg);
			return;
		} else {
			$.messager.alert("系统提示", "加密成功");
			//resetValue();
			$("#dlgLockFile").dialog("close");
			$("#dg").datagrid("reload");
		}
	}
});
}

function closeLockFileButtonDialog(){
	$('#dlgLockFile').dialog("close");
}

//解密文件
function notlockFileD(){

	var selectedRows = $("#dg").datagrid('getSelections');
	$("#notpasswordMessage").hide();
	if (selectedRows.length == 0) {
		$.messager.alert("系统提示", "请选择要解密的文件！");
		return;
	}
	if (selectedRows.length > 1) {
		$.messager.alert("系统提示", "请逐条文件进行解密！");
		return;
	}
	if (selectedRows[0].isLock=="否"){
		alert("该文件并没有加密");
		return;
	}
	$('#dlgnotLockFile').dialog('open').dialog("setTitle", "解密文件");
	
	$('#notlick_title').val(selectedRows[0].name);
	$('#notfileId').val(selectedRows[0].id);
	
	url = "fileManage/notlockFile";
	
}

function notlockFile(){
	
	var p1 = $("#notlick_password").val();
	
	if(  p1==""  ){
		$("#notpasswordMessage").show();
		return;
	}
	
	$('#notlockFileF').form("submit", {
		url : url,
		onSubmit : function() {
			return $(this).form("validate");
		},
		success : function(result) {
			//转化为json数据
			var a = eval("("+result+")");
	
			$.messager.alert("系统提示", a.errorMeg);
			if(a.success == 'true'){
				$("#dg").datagrid("reload");
				$("#dlgnotLockFile").dialog("close");
			}
		}
	});
}

function closenotLockFileButtonDialog(){
	$('#dlgnotLockFile').dialog("close");
}


</script>
</head>
<body>
	<table id="dg" title="" class="easyui-datagrid"
		style="width:700px;height:250px" fitColumns="true" rownumbers="true"
		fit="true" pagination="true" url="fileManage/loadFile" toolbar="#tb">
		<thead>
			<tr>
				<th field="cb" checkbox="true"></th>
				<th data-options="field:'id'" width="50" >ID</th>
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
			<a href="javascript:uploadFile()"
				class="easyui-linkbutton" data-options="iconCls:'icon-add'"
				plain="true">上传文件</a>  
			<a href="javascript:downloadFileA()"
				class="easyui-linkbutton" data-options="iconCls:'icon-edit'"
				plain="true">下载文件</a> 
			<a href="javascript:lookFileD()"
				class="easyui-linkbutton" data-options="iconCls:'icon-remove'"
				plain="true">查看文件</a>
			<a href="javascript:shareFileD()"
				class="easyui-linkbutton" data-options="iconCls:'icon-remove'"
				plain="true">分享文件</a>
			<a href="javascript:lockFileD()"
				class="easyui-linkbutton" data-options="iconCls:'icon-remove'"
				plain="true">加密文件</a>
			<a href="javascript:notlockFileD()"
				class="easyui-linkbutton" data-options="iconCls:'icon-remove'"
				plain="true">解密文件</a>
			<a href="javascript:deleteFile()"
				class="easyui-linkbutton" data-options="iconCls:'icon-remove'"
				plain="true">删除文件</a>
		</div>
		<hr>
		
		<div>
			<form id="search" method="post" enctype="multipart/form-data"> 
				<table>
					<tr>
						<td>&nbsp;&nbsp;文件名:&nbsp;&nbsp;</td>
						<td><input type="text" name="name" id="name" size="10" /></td>
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
		<hr>
		<div id="dlg"  class="easyui-dialog"
			style="width:400px;height:250px;padding:20px 10px 0;" closed="true"
			buttons="#fileButton">
			<form method="post" action="fileManage/uploadFile" enctype="multipart/form-data" id="uploadF" >
				<table>
					<tr>
						<tr><td>文件备注:&nbsp; <input type="text" name="description" id="description" class="easyui-validatebox" style="width:200px"></td></tr>
						<tr><td><input type="file" name="attachs" id="attachs" class="easyui-validatebox"  style="width:200px" required="true" ></td><tr>
						<!-- <td><input type="button" id="uploadB" value="上传"></td> -->
					</tr>
				</table>
			</form>
		</div>
		<br/>
	</div>
	<div id="fileButton">
		<a href="javascript:saveFile()" class="easyui-linkbutton" iconCls="icon-ok">上传</a> 
		<a href="javascript:closeFileDialog()" class="easyui-linkbutton" iconCls="icon-cancel">关闭</a>
	</div> 
	
	<div id="dlgShare"  class="easyui-dialog"
		style="width:400px;height:250px;padding:20px 10px 0;" closed="true"
		buttons="#shareButton">
		<form id="shareF" method="post" >
			<table>
				<tr>
					<tr><td>文件分享给:&nbsp;</td><td> <input type="text" name="shareNo" id="shareNo" class="easyui-validatebox" style="width:200px"></td></tr>
					<tr><td>注&nbsp;&nbsp;意:&nbsp;</td><td>请输入正确的学号或教师工号</td><tr>
					<!-- <td><input type="button" id="uploadB" value="上传"></td> -->
				</tr>
			</table>
		</form>
	</div>
	<div id="shareButton">
		<a href="javascript:shareFile()" class="easyui-linkbutton" iconCls="icon-ok">分享</a> 
		<a href="javascript:closeShareDialog()" class="easyui-linkbutton" iconCls="icon-cancel">关闭</a>
	</div> 

	<div id="dlgDownload"  class="easyui-dialog"
		style="width:400px;height:250px;padding:20px 10px 0;" closed="true"
		buttons="#downloadButton">
		<form id="downloadF" method="post" >
		</form>
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
	
	
	<!-- 加密文件 -->
	<div id="dlgLockFile" class="easyui-dialog"
		style="width:300px;height:200px;padding:20px 10px 0;" closed="true"
		buttons="#lockFileButton">
		<form id="lockFileF" method="post">
			<input type="hidden" id="fileId" name="fileId" >
			<table>
				<tr>
					<td>文件名：</td>
					<td><input type="text" id="lick_title" name="lick_title" readonly="readonly" style="width:160px;"></td>
				</tr>
				<tr>
					<td>文件密码：</td>
					<td><input type="password" id="lick_password" name="lick_password" style="width:160px;"></td>
				</tr>
				<tr>
					<td>确认密码：</td>
					<td><input type="password" id="lick_password2" name="lick_password2" style="width:160px;"></td>
				</tr>   
				<tr><td colspan="2"><span id="passwordMessage"><font color="red">请确保输入一致的密码</font> </span></td></tr>
			</table>
		</form>
	</div>
	<div id="lockFileButton">
		<a href="javascript:lockFile()" class="easyui-linkbutton" iconCls="icon-ok">发送</a> 
		<a href="javascript:closeLockFileButtonDialog()" class="easyui-linkbutton" iconCls="icon-cancel">关闭</a>
	</div>
	
	<!-- 解密文件 -->
	<div id="dlgnotLockFile" class="easyui-dialog"
		style="width:300px;height:200px;padding:20px 10px 0;" closed="true"
		buttons="#notlockFileButton">
		<form id="notlockFileF" method="post">
			<input type="hidden" id="notfileId" name="notfileId" >
			<table>
				<tr>
					<td>文件名：</td>
					<td><input type="text" id="notlick_title" name="notlick_title" readonly="readonly" style="width:160px;"></td>
				</tr>
				<tr>
					<td>输入密码：</td>
					<td><input type="password" id="notlick_password" name="notlick_password" style="width:160px;"></td>
				</tr>
				<tr><td colspan="2"><span id="notpasswordMessage"><font color="red">请输入该文件的密码</font> </span></td></tr>
			</table>
		</form>
	</div>
	<div id="notlockFileButton">
		<a href="javascript:notlockFile()" class="easyui-linkbutton" iconCls="icon-ok">发送</a> 
		<a href="javascript:closenotLockFileButtonDialog()" class="easyui-linkbutton" iconCls="icon-cancel">关闭</a>
	</div>
	
	<!-- 下载加密文件 -->
	<div id="dlgDownloadLock"  class="easyui-dialog"
		style="width:300px;height:200px;padding:20px 10px 0;" closed="true"
		buttons="#downloadLockButton">
		<form id="downloadLockF" method="post" >
			<input type="hidden" id="lockFileId" name="lockFileId" >
			<table>
				<tr>
					<td>文件密码</td><td><input type="password" id="lockPassword" name="lockPassword" style="width:160px;"/></td>
				</tr>
				<tr><td colspan="2"><span id="lockPasswordMessage"><font color="red">请输入该文件的密码</font> </span></td></tr>
			</table>
		</form>
	</div>
	<div id="downloadLockButton">
		<a href="javascript:downloadLockFile()" class="easyui-linkbutton" iconCls="icon-ok">下载</a> 
		<a href="javascript:closeDownloadLockDialog()" class="easyui-linkbutton" iconCls="icon-cancel">关闭</a>
	</div> 
</body>
</html>