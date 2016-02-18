<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="utf-8"%>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>

<style>

  input,#cdaVersion {
    font-family: SimSun;
    font-size: 14px;
    height: 30px;
    width: 240px;
  }

  .image-create{
    margin-left:20px;
    width: 22px;
    height: 22px;
    background: url(${staticRoot}/images/add_btn.png)  no-repeat;
  }

  .image-create:hover{
    cursor: pointer;
    margin-left:20px;
    width: 22px;
    height: 22px;
    background: url(${staticRoot}/images/add_btn_pre.png)  no-repeat;
  }
  .image-modify{
    width: 22px;
    height: 22px;
    background: url(${staticRoot}/images/Modify_btn_pre.png)  no-repeat;
  }
  .image-delete{
    width: 22px;
    height: 22px;
    margin-top: -22px;
    background: url(${staticRoot}/images/Delete_btn_pre.png)  no-repeat;
  }

  .font_right{
    text-align: right;
  }


  .description{
    height: 180px;
    width: 240px;
  }
  .condition input,
  .condition select{
    font-family: SimSun;
    font-size: 12px;
    height: 30px;
    width: 240px;
  }
  .condition button {
    width: 84px;
  }
  .body-head input{
    border: 0;
    font-size: 12px;
    width: 150px;
  }
  .grid_edit{
    margin-left: 30px;
    margin-top: 10px;
    width: 22px;
    height: 22px;
    background: url("${contextRoot}/develop/images/Modify_btn.png")  no-repeat;
    float: left;
    cursor: pointer;
  }
  .grid_delete{
    float: left;
    margin-left: 70px;
    margin-top: -22px;
    width: 22px;
    height: 22px;
    background: url("${contextRoot}/develop/images/Delete_btn.png")  no-repeat;
    cursor: pointer;
  }
</style>
