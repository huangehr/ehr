<%--
  Created by IntelliJ IDEA.
  User: AndyCai
  Date: 2015/11/26
  Time: 14:05
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="utf-8"%>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>
<style type="text/css">
  .pane-attribute-toolbar{
    display: block;
    position: absolute;
    bottom: 0;
    left: 0;
    width: 100%;
    height: 50px;
    padding: 6px 0 4px;
    background-color: #fff;
    /*border-top: 1px solid #ccc;*/
    text-align: right;
  }
  .close-toolbar{
    margin-right: 20px;
  }

  input{
    height: 28px;
    width: 240px;
  }

.table{
  margin-left: 30px;
}
.table label{
  font-weight: normal;
}

  .modal-body .table tr td {
    font-size: 12px;
    border: solid 0;
    padding: 5px 3px 5px 3px;
  }
  .modal-body td label{float: right;font-family: SimSun;font-size: 12px;padding-top: 4px;}

  .modal-body .table input{
    font-family: SimSun;
    font-size: 12px;
    height: 30px;
    width: 240px;
  }
  .modal-body .table input[type="checkbox"] {
    height: 20px;
    width: 20px;
    margin-top: 0;
    margin-left: 10px;
  }
  .modal-body .table select {
    font-family: SimSun;
    font-size: 12px;
    height: 30px;
    width: 240px;
  }
  .modal-body .table textarea {
    color: inherit;
  }

  .dataExplain{
    height: 180px;
    width: 240px;
  }
  .metaDataExplain{
    width: 576px;
    height: 120px;
  }
  .metaData-width{
    width: 660px;
  }

  .modal-body .table tr .split-line{
    border-top: 0;
  }
  .modal-body .table td .criterionDict{
    width: 250px;
  }
  .checkbox-title{
    line-height: 20px;
  }

  .chexkbox-null{
    margin-left: 150px;
    margin-top: -30px;
  }

  .btn-isnull:after{
    display: inline-block;
    content: '*';
    color: #FF0000;
  }
  .td-left{
    text-align: left;
  }
</style>