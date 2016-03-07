<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>
<%@include file="/WEB-INF/ehr/commons/jsp/commonHead.jsp" %>
<%@include file="/WEB-INF/ehr/commons/jsp/commonFoot.jsp" %>

<h3>示例： 下拉框HTML示例</h3>
<input type="text" id="ipt_select">

<script> type = "text/javascript" >
        $(function () {                                                                             //前端需要展示的字段的写在columns里
            var columns = [
              {header: 'id', name: 'id'},			                                  //后台返回来的需要有字段id
              {header: '姓名', name: 'name'}	                                     //后台返回来的需要有字段age(自定义)
            ];
            $("#ipt_select").ligerComboBox(					                    //$("")对应input的id(自定义)
                    {
                      url:"/ha/dict/searchDictEntryList",					//数据源的url，需要返回json格式的 数组对象，（如果要用到带搜索的功能，那么在后台的接收的方法中需要接收字段“key”，查询语句也要带“key”作为条件查询）
                      valueField: 'id',								                        //对应colums的id
                      textField: 'name',								                       //对应colums的age(自定义)
                      width: 240,										                           //input的宽度
                      height: 28,										                          //input的高度
                      autocomplete: true					                              //搜索功能，是重新发送请求，所以后台url接收的方法中需要接收字段key
                    });

        })
    </script>