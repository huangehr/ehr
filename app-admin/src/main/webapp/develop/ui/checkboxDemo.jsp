<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>
<%@include file="/WEB-INF/ehr/commons/jsp/commonHead.jsp" %>
<%@include file="/WEB-INF/ehr/commons/jsp/commonFoot.jsp" %>

<h3>示例： 复选框</h3>

<input type="checkbox" name="chbox" value="1">选项一
<input type="checkbox" name="chbox" value="2">选项二
<input type="checkbox" name="chbox" value="3">选项三


<script> type = "text/javascript" >
        $(function () {
            $('input:checkbox').ligerCheckBox();
        })
</script>