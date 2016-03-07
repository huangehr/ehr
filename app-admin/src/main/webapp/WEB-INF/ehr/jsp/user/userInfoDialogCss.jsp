<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="utf-8"%>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>

<style>
    .m-form-inline .m-form-group label{ width: 110px; }
    /*.m-form-inline .m-form-group { margin-top: 10px; }*/
    .u-upload-img{ position: absolute; left: 400px; }
    .u-public-manage{ text-align: center; margin:3px; display:none; }
    /*.u-btn-cancel{ margin-bottom: 10px; }*/
    .u-btn-color{ background: #4bb941; }
    .u-btn-pk-color{ background: #2D9BD2; }
    .txt-public-content{ width:389px; height: 120px; }
    .u-border{ border-top:1px dashed #cccccc; height:1px; margin-top: -10px; }
    .u-reset-password{ margin-top: 10px; margin-bottom: 10px;margin-left: 150px; }
    .save-f-ml150{ margin-left: 150px; margin-top: 50px; margin-bottom: 20px; }
    .cancel-f-ml91{ top: 20px; margin-left: 91px; }
    .u-f-mt5{ border: 0; margin-top: 5px; }
    .u-public-key-msg{width: 400px; height:90px; border: 0;margin-top: 5px;}
    .f-mb{ margin-bottom: 0;position: absolute; left: 90px}
    .f-ml-t{ position: absolute;  left: 10px; }
    .f-t30{ position: relative;  top: 30px;  left: -130px; }
    .u-bd{ border: 1px solid #c8c8c8;height: auto;margin-left: 10px;margin-right: 10px;margin-bottom: 10px; }
    .u-bd .m-form-group{padding-bottom: 0;}
    .f-wtl{ width:40px;top: -10px;left: 10px;background: #fff;text-align: center; }

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
    .l-dialog-body {
        overflow: hidden;
    }
    .f-w88{ width: 88px; }
    .f-h110{ height: 110px }
</style>