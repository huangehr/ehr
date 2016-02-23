
define(function (require, exports, module) {

    require('datetimepickerCss');
    require('datetimepicker');
    require('datetimepickerLocal');
    require('bootstrap');
    require('bootstrapCss');

    var $ = require('base').$,
        context = require('base').context,
        Grid = require('modules/components/grid'),
        ViewController = require('viewController'),
        Util        = require('utils'),
        Common = require('app/common'),
        JValidate = require('jValidate');

    var dateGrid;

    $(function () {

        $(function(){
            //服务器监视表字典ID
            var hosTableList = 19;
            initDDL(hosTableList, $('#hosTableList'));

            $("#start").datetimepicker({
                format: 'yyyy-mm-dd',
                autoclose:true,
                todayBtn: true,
                startView:1,
                minView:1,
                maxView:4,
                todayHighlight:true
            });

            $("#end").datetimepicker({
                format: 'yyyy-mm-dd',
                autoclose:true,
                todayBtn: true,
                startView:1,
                minView:1,
                maxView:4,
                todayHighlight:true
            });

            createGrid();

            doStaticPerDay();

        });

        function FormatDate (strTime) {
            var date = new Date(strTime);
            return date.getFullYear()+"-"+(date.getMonth()+1)+"-"+date.getDate();
        }

        //根据传入的字典ID及显示对象，加载下拉框的数据。
        function initDDL(dictId, target) {
            //下拉框的方法
            var target = $(target);
            $.ajax({
                url: context.path + "/dict/searchDictEntryList",
                type: "post",
                dataType: "json",
                data: {dictId: dictId, page: "1", rows: "10"},
                success: function (data) {
                    var option ='<option value="0">选择所有</option>';
                    for (var i = 0; i < data.detailModelList.length; i++) {
                        option += '<option value=' + data.detailModelList[i].code + '>' + data.detailModelList[i].value + '</option>';
                    }
                    $(target).append(option);
                }
            });
        }

        function showFailure(XMLHttpRequest, textStatus, errorThrown) {

            $('#message').html('<a class="handle" href="#"><font color=red> 获取查询数据出错('+textStatus+'='+XMLHttpRequest.status+'.'+XMLHttpRequest.readyState+')</font></a>');
            //alert(search_url);
        }

        function showResponse(responseData) {
            $('#message').html("服务正常");
        }

        function showQueryResponse(responseData) {
            //JSON.stringify
            $('#result').html(JSON.stringify(responseData));
        }

        //查询hbase中的数据
        function doQuery(){
            var table = $("#hosTableList").val();
            var rowkey = $("#rowkey").val();

            $.ajax({
                url: context.path + "/monitor/query",
                data: {rowkey: rowkey, table: table},
                type: 'POST',
                dataType: 'json',
                timeout: 1000000,
                error: showFailure,
                success: showQueryResponse
            });
        }

        //点击生成入库报告
        $("#btnQueryReport").click(function(e) {
            if(dateGrid == undefined){

            }
            else{
                dateGrid.instance.jqGrid("clearGridData");
                doStaticPerDay();
            }
        });

        //查询根据表查询结果集
        $("#btnQuery").click(function(e) {
            doQuery();
        });

        //数据核对
        $("#btnVerify").click(function(e) {
            var table = $("#table").val();
            var querystring = $("#rowkey").val();

            if(table == "")return;
            if(querystring == "")return;

            $.ajax({
                url: context.path + "/monitor/dataCheck",
                type: "post",
                dataType: "json",
                data: {querystring: querystring, table: table},
                timeout: 10000,
                error: showFailure,
                success: showQueryResponse
            });
        });

        //初始化新增Grid
        function createGrid(){
            dateGrid = new Grid('#tblStaticResult', {
                datatype : "local",
                height : 250,
                shrinkToFit: true,
                scrollOffset: 0,
                widthLock:true,
                timeout:20000,
                //width:400,
                width: $(window).width(),
                //height: $(window).height() - 198,
                autoFit: true,
                //marginHeight:198,
                rowNum: 10,
                colNames : [ '数据集', '累计入库记录数', '当天入库记录数', '累计可识别', '累计不可识别','当天可识别', '当天不可识别' ],
                colModel : [
                    {name : 'table',index : 'id',width : 150,sorttype : "int"},
                    {name : 'count',index : 'invdate',width : 180,align : "right",sorttype : "date"},
                    {name : 'today_count',index : 'name', align : "right", width : 200},
                    {name : 'match_demographic_count',index : 'amount',width : 200,align : "right",sorttype : "float"},
                    {name : 'not_match_demographic_count',index : 'tax',width : 200,align : "right",sorttype : "float"},
                    {name : 'today_match_demographic_count',index : 'total',width : 200,align : "right",sorttype : "float"},
                    {name : 'today_not_match_demographic_count',index : 'note',align : "right",width : 200,sortable : false}
                ]
            }).render();
        }

        //根据开始时间与结束时间，生成数据入库报告
        function doStaticPerDay(){
            var HDSC0101Result = doStatic("HDSC01_01",showStaticResponse);
            var HDSC0209Result = doStatic("HDSC02_09",showStaticResponse);
            var HealthEventsResult = doStatic("HealthEvents",showStaticResponse);
            var HealthArchivesResult = doStatic("HealthArchives",showStaticResponse);
        }

        //对单表进行数据行统计，根据开始时间与结束时间、表格，生成数据入库报告
        function doStatic(table,callback){
            var start = $("#start").val();
            var currentDate = new Date();
            if(start == ""){
                start = FormatDate(currentDate);
            }

            var end = $("#end").val();
            if(end == ""){
                end = "*";
            }

            $.ajax({
                url: context.path + "/monitor/staticTable",
                type: "post",
                dataType: "json",
                data: {start: start, end: end, table: table},
                timeout: 1000000,
                error: showFailure,
                success: callback
            });
        }

        //往新成生的Grid中追加新的行。
        function showStaticResponse(responseData) {
            //JSON.stringify
            var result = responseData;
            var table = result.table;
            if(table == "HDSC01_01"){
                table = table+"(门诊)";
            }
            if(table == "HDSC02_09"){
                table = table+"(住院)";
            }

            result.table = table;

            if(dateGrid == undefined){
                createGrid();
                dateGrid.instance.jqGrid('addRowData', 1, result);
            }
            else{
                var i = dateGrid.instance.length;   //待确认
                dateGrid.instance.jqGrid('addRowData', i + 1, result);
            }
        }

        function reloadGrid() {
            $("#tblStaticResult").empty();
            createGrid();
            doStaticPerDay();
        }
    })
})