<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="utf-8"%>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>

<style>
    #tree_from{
        margin-top: 4px;
        margin-left: 20px;
        width:310px;
        position: relative;
        display: inline-block;
        border: 1px solid #d3d3d3;
        height: 355px;
        overflow-y: auto;
        overflow-x: hidden;
    }
    #tree_to{
        width:310px;
        position: absolute;
        top: 32px;
        right: 15px;
        margin-left: 5px;
        border: 1px solid #d3d3d3;
        height: 355px;
        overflow-y: auto;
        overflow-x: hidden;
    }
    .tree-from-title{
        margin-top: 15px;
        margin-left: 20px;
    }
    .tree-to-title{
        position: absolute;
        top: 14px;
        right: 280px;
    }
    .tree-option {  position: absolute;  top: 90px;  margin-top: 90px;  left: 340px;  }
    .update-footer{right: 10px;bottom: 0;}
</style>