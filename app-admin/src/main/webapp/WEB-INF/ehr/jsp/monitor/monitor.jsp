<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="utf-8" %>
<html>
<head>
	<meta charset="utf-8">
	<title>数据收集监控</title>
	<%@include file="/WEB-INF/ehr/commons/jsp/commonHeader.jsp" %>
    <script>
        //加载js模块组件
        seajs.use(['jquery','i18n','app/monitor/monitor'], function ($,i18n, Monitor) {
        });
    </script>
</head>
<body>
	<div class="" id="message"></div>

	<div id="conditionAreaT" class="f-mt10 f-ml10" align="left">
        <table>
            <tr>
                <td>
                    <select name="table" class="f-w200" id="hosTableList" placeholder="请选择数据集">
                    </select>
                </td>
                <td>
                    <input type="text" class="f-ml10" name="id" id="id" value="" style="width: 240px" class="form-control"  placeholder="请输入rowkey">
                </td>
                <td>
                    <input type="button" id="btnQuery" class="btn btn-warning f-ml10" value="查询数据">
                    <input type="button" id="btnStatic" class="btn btn-warning f-ml10" value="统计数据集入库">
                    <input type="button" id="btnVerify" class="btn btn-warning f-ml10" value="数据核对">
                    <a href="http://tool.oschina.net/codeformat/json"  class="f-ml10"  target="_blank">结果格式化</a>
                </td>
            </tr>
        </table>
	</div>

	<div id="ResultAreaT" class="f-mt10 f-ml10" align="left">
		<table>
			<tr>
				<textarea id="result" rows="10" style="width:90%"></textarea>
			</tr>
		</table>
	</div>

	<div id="conditionAreaD" class="f-mt10 f-ml10" align="left">
		<table>
			<tr>
				<td>
					<a>开始时间：</a>
				</td>
				<td>
					<input size="16" type="text" name="start" id="start" value="" placeholder="设置开始时间" class="form_datetime">
				</td>
				<td>
					<a class="f-ml10">结束时间：</a>
				</td>
				<td>
					<input size="16" type="text" name="end" id="end" value="" placeholder="设置结束时间" class="form_datetime">
				</td>
                <td>
                    <input type="button" id="btnQueryReport" class="btn btn-warning f-ml10" value="生成数据入库报告">
                </td>
			</tr>
		</table>
	</div>

	<div id="ResultAreaD" class="f-mt10 f-ml10" align="left">
		<div id="tblStaticResult">
			<!-- 结果集列表区域 -->
		</div>
	</div>

</body>
</html>