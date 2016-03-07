<%--
  Created by IntelliJ IDEA.
  User: AndyCai
  Date: 2015/11/20
  Time: 17:53
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
    border-top: 1px solid #ccc;
    text-align: right;
  }
  .close-toolbar{
    margin-right: 20px;
  }

  input{
    height: 28px;
    width: 240px;
  }

 .pop_tab {
    height: 36px;
    line-height: 36px;
    background: url(${staticRoot}/images/sub_bg.gif) #eee repeat-x left bottom;
    padding-left: 2px;
 }

  .pop_tab li.cur {
    background: #fff;
    font-weight: bold;
    border-left: 1px solid #cacaca;
    border-right: 1px solid #cacaca;
  }
  .pop_tab li {
    cursor: default;
  }
  .pop_tab li {
    font-size: 12px;
    color: #000;
    float: left;
    padding: 0 14px;
  }

  .pane-attribute-toolbar{
      display: block;
      position: absolute;
      bottom: 0;
      left: 0;
      width: 100%;
      height: 50px;
      padding: 6px 0 4px;
      background-color: #fff;
      border-top: 1px solid #ccc;
      text-align: right;
  }
  .close-toolbar{
      margin-right: 20px;
  }
</style>


