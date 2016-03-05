<%--
  Created by IntelliJ IDEA.
  User: AndyCai
  Date: 2015/11/23
  Time: 10:28
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>

<script>
    $(function () {
        var versionCode = $.Util.getUrlQueryString("versioncode");
        var cda_id = $.Util.getUrlQueryString("cda_id");
        list.dataset(versionCode,cda_id);
    });
</script>
