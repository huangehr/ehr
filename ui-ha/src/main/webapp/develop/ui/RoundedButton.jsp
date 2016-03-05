<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="utf-8"%>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>
<%@include file="/WEB-INF/ehr/commons/jsp/commonHead.jsp" %>

<style>
    .button {
        display: inline-block;
        text-align: center;
        text-decoration: none;
        font: bold 12px/25px Arial, sans-serif;
        height:30px;
        line-height: 30px;;
        -webkit-border-radius: 30px;
        -moz-border-radius: 30px;
        border-radius: 30px;
        border:  1px solid #c8c8c8;
    }

    .u-dataset-active {
        color: #ffffff;
        background: #3094d5;
        position: absolute;
        width: 80px;
        border: 0;
        height: 30px;
        z-index: 2;
    }

    .u-std-active {
        color: #ffffff;
        background: #3094d5;
        position: absolute;
        width: 80px;
        border: 0;
        height: 30px;
        z-index: 2;
        left: 80px;
    }

    .u-dataset {
        color: #000000;
        background: #ffffff;
        position: absolute;
        width:160px;
        z-index: 1;
        text-align: left;
        padding-left: 20px;
    }


    .u-stdictionary {
        color: #000000;
        background: #ffffff;
        position: absolute;
        width: 160px;
        z-index: 1;
        left: 0px;
        text-align: right;
        padding-right: 20px;
    }

</style>

<div style="display: inline-block;position:relative;margin: 50px 50px 0;">
    <a href="#" id="u_dataset_btn" class="button u-dataset-active">数据集</a>
    <a href="#" id="u_std_btn" class="button u-stdictionary">字典</a>
</div>


<%@include file="/WEB-INF/ehr/commons/jsp/commonFoot.jsp" %>
<script>
  $(function () {
      $("#u_dataset_btn").click(function(){
          $("#u_dataset_btn").removeClass("u-dataset").addClass("u-dataset-active");
          $("#u_std_btn").removeClass("u-std-active").addClass("u-stdictionary ");
      })

      $("#u_std_btn").click(function(){
          $("#u_dataset_btn").removeClass("u-dataset-active").addClass("u-dataset");
          $("#u_std_btn").removeClass("u-stdictionary ").addClass("u-std-active");
      })
  });
</script>