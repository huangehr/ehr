<%--
  Created by IntelliJ IDEA.
  User: AndyCai
  Date: 2015/11/19
  Time: 10:12
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="utf-8"%>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>
<script src="${contextRoot}/develop/Scripts/cdaJs.js"></script>
<script >
  $(function(){
    cda.list.init();
  })
</script>
