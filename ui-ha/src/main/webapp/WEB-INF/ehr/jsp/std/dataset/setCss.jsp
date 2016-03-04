<%--
  Created by IntelliJ IDEA.
  User: AndyCai
  Date: 2015/11/25
  Time: 10:26
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="utf-8"%>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>

<style>

  .image-create{
    margin-left:340px;
    margin-top: -25px;
    width: 22px;
    height: 22px;
    background: url(${staticRoot}/images/add_btn.png);
  }

  .image-create:hover{
    margin-left:340px;
    margin-top: -25px;
    width: 22px;
    height: 22px;
    background: url(${staticRoot}/images/add_btn_pre.png);
  }
  .image-modify{
    width: 22px;
    height: 22px;
    background: url(${staticRoot}/images/Modify_btn_pre.png);
  }
  .image-delete{
    width: 22px;
    height: 22px;
    margin-top: -22px;
    background: url(${staticRoot}/images/Delete_btn_pre.png);
  }

  .font_right{
    text-align: right;
  }

</style>