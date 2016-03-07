<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextRoot" value="<%=request.getContextPath()%>" scope="page" />
<c:set var="devgMode" value="true" scope="page" />
<html>
<head>
    <title>入库情况查询</title>
    <%@include file="/WEB-INF/ehr/commons/jsp/commonHeader.jsp"%>
        <script>
            function judgFailTime() {
                var time = new Date();
                var b = 60*24; //分钟数
                time.setMinutes(time.getMinutes() - b, time.getSeconds(), 0);
                return time;
            }

            function formatterdate(val) {
                var date = new Date(val);
                return date.format("yyyy-MM-dd hh:mm:ss");
            }

            Date.prototype.format = function (format) {
                var o = {
                    "M+": this.getMonth() + 1, // month
                    "d+": this.getDate(), // day
                    "h+": this.getHours(), // hour
                    "m+": this.getMinutes(), // minute
                    "s+": this.getSeconds(), // second
                    "q+": Math.floor((this.getMonth() + 3) / 3), // quarter
                    "S": this.getMilliseconds()
                    // millisecond
                };
                if (/(y+)/.test(format))
                    format = format.replace(RegExp.$1, (this.getFullYear() + "")
                            .substr(4 - RegExp.$1.length));
                for (var k in o)
                    if (new RegExp("(" + k + ")").test(format))
                        format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k]
                                : ("00" + o[k]).substr(("" + o[k]).length));
                return format;
            }



            //加载js模块组件
        seajs.use(['jquery','modules/components/grid','viewController','utils','modules/components/linkageSelect','jValidate','datepickerLocal'], function ($, Grid, ViewController, Util, LinkageSelect, JValidate) {
            // TODO
            var archiveStatus;
            $(function () {

                archiveStatus =$("#archiveStatus").val();
                $("#archiveStatus").change(function () {
                    archiveStatus=$(this).find ("option:selected").val();
                });

                $('#fromTime').datepicker({
                    format: "yyyy-mm-dd",
                    autoclose: true
                });

                $('#toTime').datepicker({
                    format: "yyyy-mm-dd",
                    autoclose: true
                });

                //搜索
                $("#search_id").click(function(){
                    var fromTime=$("#fromTime").val();
                    var toTime=$("#toTime").val();

                    grid.instance.jqGrid('setGridParam',{
                        url :"${contextRoot}/jsonArchive/searchArchives",
                        page : 1,
                        postData:{fromTime:fromTime,toTime:toTime,archiveStatus:archiveStatus}, //发送数据
                    }).trigger("reloadGrid");
                });

                var today = new Date().format("yyyy-MM-dd hh:mm:ss");
                var preday = judgFailTime().format("yyyy-MM-dd hh:mm:ss");

                $("#fromTime").val(preday);
                $("#toTime").val(today);
                //查询数据
                var fromTime=$("#fromTime").val();
                var toTime=$("#toTime").val();
                var UserTypeDictId = 15;
                //grid生成


                var grid = new Grid('#dataGrid',{
                    url: "${contextRoot}/jsonArchive/searchArchives",
                    datatype:'json',
                    postData:{
                        fromTime:fromTime,
                        toTime:toTime,
                        archiveStatus:archiveStatus
                    },
                    height: 530,
                    rowNum : 10,
                    colNames:['序号','id','接收时间','解析时间', '接收状态', '文档路径','失败原因'],
                    colModel:[
                        {name:'order',index:'order',sorttype: 'int', width:80,  align: "center"},
                        {name:'id',index:'id', hidden: true},
                        {name:'receiveDate',index:'receiveDate', width:200, align:"center",formatter:function(value,grid, rows, state){
                            return formatterdate(rows.receiveDate);
                        }  },
                        {name:'parseDate',index:'parseDate', width:200, align:"center",formatter:function(value,grid, rows, state){
                            return formatterdate(rows.parseDate);
                        } },
                        {name:'archiveStatus',index:'archiveStatus', width:75, align:"center",formatter: function (value,grid, rows, state) {
                            var status="";
                            if(rows.archiveStatus == 'Received'){
                                status="已缓存";
                            } else if(rows.archiveStatus == 'Acquired'){
                                status="正在入库";
                            } else if(rows.archiveStatus == 'Failed'){
                                status="入库失败";
                            } else if(rows.archiveStatus == 'Finished'){
                                status="已入库";
                            } else if(rows.archiveStatus == 'InDoubt'){
                                status="未能入库的档案";
                            }
                            return status;

                        }},
                        {name:'remotePath',index:'remotePath',width:400, align:"center"},
                        {name:'message',index:'message', width:400, align:"center"},
                    ],
                    jsonReader : {
                        root:"detailModelList",
                        page: "currPage",
                        total: "totalPage",
                        records: "totalCount",
                        repeatitems: false,
                        id: "0"
                    }
                }).render();
            });
        });
/*            $(function () {
            $("#datepicker").datepicker();
        });*/



    </script>
</head>
<body>
<div style="float: right;" class="collectdiv">
    <div id="formdiv">
        <form class="form-inline one-row-form" id="search_form">
            <div class="form-group">

                <%--<input name="fromTime" id="fromTime" class="Wdate" type="text"  style="height: 34px;" onfocus="WdatePicker({skin:'default'})"/> -
                <input name="toTime" id="toTime" class="Wdate" type="text" style="height: 34px;" onfocus="WdatePicker({skin:'default'})"/>--%>
            </div>

            <div class="input-append  date">
                <label>入库时间：</label>
                <input type="text" id="fromTime" name="fromTime" class="inputwidth">
                <input type="text" id="toTime" name="toTime" class="inputwidth">
                <label>入库状态：</label>
                <select id="archiveStatus" name="archiveStatus">
                    <option value="0">已缓存</option>
                    <option value="1">正在入库库</option>
                    <option value="2">入库失败</option>
                    <option value="3">已入库</option>
                    <option value="4">未能入库的档案</option>
                </select>
            </div>
            <div class="input-append  date">

            </div>
            <br><br><br><br><br><br>

            <div class="form-group formgdiv">
                <button type="button" class="btn btn-primary" id="search_id">搜索</button>
            </div>
        </form>

<!--主画面内容-->


<div id="dataGrid" data-pagerbar-items="10" class="f-mt10">
</div>




</body>
</html>