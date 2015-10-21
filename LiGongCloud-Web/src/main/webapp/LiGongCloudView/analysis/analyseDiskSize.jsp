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
<title>用户网盘容量使用情况</title>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">
<link rel="stylesheet" type="text/css" href="resource/js/jquery-easyui-1.3.3/themes/default/easyui.css">
<link rel="stylesheet" type="text/css" href="resource/js/jquery-easyui-1.3.3/themes/icon.css">
<script type="text/javascript" src="resource/js/jquery-easyui-1.3.3/jquery.min.js"></script>
<script type="text/javascript" src="resource/js/jquery-easyui-1.3.3/jquery.easyui.min.js"></script>
<script type="text/javascript" src="resource/js/jquery-easyui-1.3.3/locale/easyui-lang-zh_CN.js"></script>
<script type="text/javascript" src="resource/js/highcharts/highcharts.js"></script>
<script type="text/javascript" src="resource/js/highcharts/exporting.js"></script>

<script type="text/javascript">

$(function() {

	var option;

	option = {
        chart: {
            type: "column",
            renderTo: "container"
        },
        credits: {
        	enabled:false
        },
        title: {
        	enabled : true,
			useHTML : true,
            text: "<div style='font-family:宋体'>"+"用户网盘容量使用情况分析"+"</div>"
        },

        xAxis: {
        	 categories: []
        },
        yAxis: {
            min: 0,
            title: {
                text: "用户(数量)"
            },
            labels: {
                formatter: function() {
                    return this.value;
                }
            },
        },  
        tooltip: {
            headerFormat: "<span style='font-size:10px'>{point.key}</span><table>",
            pointFormat: "<tr><td style='padding:0'><b>{point.y} 位 </b> </td></tr>",
            footerFormat: "</table>",
            shared: true,
            useHTML: true
        },
        plotOptions: {
            column: {
                pointPadding: 0.3,
                borderWidth: 0
            }
        },
        legend: {
            itemStyle: {
                fontFamily:"宋体"
            }
        },
        series :[]
    };
    
    var a = ["~100","101-200","201-300","301-400","401-500","500~"];
	option.xAxis.categories = a;
	
	var aa =${diskNum};
	//var aa = [78,104,193,177,119,23];
	option.series.push({name: "使用网盘容量范围(M)",data: aa});
	
	var chart = new Highcharts.Chart(option);
});

function exportHighcharts(){ 
	$("#titlename").val($(".highcharts-title").text());
    var chart_line = $("#container").highcharts();  
    var svg_line = chart_line.getSVG();   
    var svg = svg_line;
    $("#svg").val(svg);  
    var type="image/png";
	$("#dForm").attr("action","diskManage/exportHighCharts?type="+type+"");
	$("#dForm").submit();  
}
</script>
</head>

<body>

	<div id="tb">
		<div>
			<a href="diskManage/roleBack"
				class="easyui-linkbutton" data-options="iconCls:'icon-edit'"
				plain="true">返回</a>
			<a href="javascript:exportHighcharts()"
				class="easyui-linkbutton" data-options="iconCls:'icon-edit'"
				plain="true">导出</a>  
		</div>
	</div>
	
	<!-- 用于highchair导出的form -->
	<form  method="post" id="dForm" name="dForm">
		<input type="hidden" name="titlename" id="titlename" />
		<input type="hidden" name="svg" id="svg" /> 
	</form>
	
	<!-- 用于形成highchair图形的div -->
	<div id="container" style="width:85%;margin:0 auto;"></div>
</body>
</html>
