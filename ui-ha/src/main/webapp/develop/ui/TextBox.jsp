<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="utf-8"%>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>
<%@include file="/WEB-INF/ehr/commons/jsp/commonHead.jsp" %>

<style type="text/css">
  .l-text-field{padding-right: 33px;}
</style>
<div style="margin-left: 30px;">
    <h3>示例一：文本框带删除按钮控件</h3>
    <input type="text" id="u-txt-delete"/>
    <br/>
    <br/>
    <h3>示例二：文本框带搜索按钮控件</h3>
    <input type="text" id="u-txt-search" placeholder="请输入序号或名称"/>
</div>

<%@include file="/WEB-INF/ehr/commons/jsp/commonFoot.jsp" %>
<script>
  $(function () {
      $("#u-txt-delete").ligerTextBox({width: 240});//生成宽度为240的文本框
      $("#u-txt-search").ligerTextBox({width: 240,isSearch:true,search: function() {
        //文本框中搜索按钮的回调事件处理，可以写查询的相应代码
        alert("值为:" + $("#u-txt-search").val());
      }});
  });
</script>