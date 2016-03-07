<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="utf-8"%>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>
<%@include file="/WEB-INF/ehr/commons/jsp/commonHead.jsp" %>

<div style="margin-left: 30px;">
  <h3>示时间控件:</h3>
  <input type="text" id="u-date"/>
</div>


<%@include file="/WEB-INF/ehr/commons/jsp/commonFoot.jsp" %>
<script>
  $(function () {
      var d = new Date();
      var vYear = d.getFullYear();
      var vMon = d.getMonth() + 1;
      var vDay = d.getDate()-1;
      var date = vYear+"-"+vMon+"-"+vDay;
      $("#u-date").ligerDateEditor({initValue: "2015-11-10", format: "yyyy-MM-dd"});//设置日期默认值为系统时间
  });
</script>