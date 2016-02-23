<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>
<%@include file="/WEB-INF/ehr/commons/jsp/commonHead.jsp" %>
<%@include file="/WEB-INF/ehr/commons/jsp/commonFoot.jsp" %>
<link rel="stylesheet" href="browse.css">
<link rel="stylesheet" href="detail.css">
<link rel="stylesheet" href="floatMenu.css">
<link href='fullcalendar/fullcalendar.css' rel='stylesheet' />
<link href='fullcalendar/fullcalendarExt.css' rel='stylesheet' />
<link href='fullcalendar/fullcalendar.print.css' rel='stylesheet' media='print' />
<script src='fullcalendar/lib/moment.min.js'></script>
<script src='fullcalendar/fullcalendar.min.js'></script>
<link rel="stylesheet" href="/ha/develop/testUI/grid/css/bootstrap.min.css">
<link rel="stylesheet" href="/ha/develop/testUI/grid/css/bootstrap-table.min.css">
<script src="/ha/develop/testUI/grid/js/bootstrap.min.js"></script>
<script src="/ha/develop/testUI/grid/js/tableExport.js"></script>
<script src="/ha/develop/testUI/grid/js/bootstrap-table.js"></script>
<script src="/ha/develop/testUI/grid/js/bootstrap-table-export.js"></script>
<script src="echarts-2.2.7/build/dist/echarts.js"></script>
<body>
<div style="text-align: center">

    <div class="sec-detail s-bc19 ">
        <div class="top ">
            <div class="local">
                <div class="ico"></div>
                <div class="content"><span id="name">刘永辉</span>&nbsp;&nbsp;>&nbsp;&nbsp;<span id="topc">高血压</span></div>
            </div>
            <div class="title">
                <div class="ico s-c0">返回</div>
                <div id="topt" class="content">高血压</div>
            </div>
        </div>
        <div class="cut-line ">&nbsp;</div>

        <div class="vis-mid">
            <div class="m-left">
                <div class="m-l-top">
                    <div class="m-l-t-i">
                        <div class="c-h70 f-fb c-pt10"><span class="s-c4 f-fs50 c-mr5">4</span>年</div>
                        <div class="c-h50" style="height: 50px">
                            <div class="c-fl c-w36 c-h36 c-bi-ill c-ml50" ></div>
                            <div class="c-fl f-lh36  c-ml10 s-c17">患病年数</div>
                        </div>
                    </div>
                    <div class="m-l-t-i">
                        <div class="c-h70 f-fb c-pt10"><span class="s-c3 f-fs50 c-mr5">3</span>次</div>
                        <div class="c-h50">
                            <div class="c-fl c-w36 c-h36 c-bi-doc c-ml50" ></div>
                            <div class="c-fl f-lh36  c-ml10 s-c17">就诊次数</div>
                        </div>
                    </div>
                    <div class="m-l-t-i">
                        <div class="c-h70 f-fb c-pt10"><span class="s-c1 f-fs50 c-mr5">2</span>次</div>
                        <div class="c-h50">
                            <div class="c-fl c-w36 c-h36 c-bi-hos c-ml50" ></div>
                            <div class="c-fl f-lh36  c-ml10 s-c17">住院次数</div>
                        </div>
                    </div>
                </div>
                <div class="m-l-bot">
                    <div class="c-h70 s-c1 c-pt30">2015-05-05&nbsp;&nbsp;就诊&nbsp;&nbsp;厦门市第一医院</div>
                    <div class="s-c17">最近一次就诊时间</div>
                </div>
            </div>
            <div class="m-right">
                <div class="c-h70 c-w70 c-bi-med c-mat8 "></div>
                <div class="c-h110 c-ml10r10 f-tl f-lh26">胺碘酮、奎尼丁、美西律、普罗帕酮、莫雷西苏、普萘若尔、硝酸甘油、硝酸异山梨酯、胺碘酮、奎尼丁、美西律、普罗帕酮、莫雷西苏、普萘若尔、硝酸甘油、硝酸异山梨酯</div>
                <div class="s-c17">常用药物</div>
            </div>
        </div>
        <div class="cut-line ">&nbsp;</div>

        <div class="vis-barner">
            <div id="b_item_1" class="b-item-f ">就诊事件</div>
            <div id="b_item_2" class="b-item-s">历史用药情况</div>
            <div id="b_item_3" class="b-item-s">历史检查情况</div>
            <div id="b_item_4" class="b-item-s b-item-s-active">体征指标动态</div>
            <div id="b_item_5" class="b-item-s">保健活动</div>
            <div id="b_item_ar" class="b-item-ar"></div>
        </div>

        <div class="">
            <!-- 就诊事件 -->
            <div id="b_detail_1" class="vis-details" style="height: 550px;display: none;">&nbsp;</div>
            <!-- 用药情况 -->
            <div id="b_detail_2" class="vis-details" style="display: none;">
                <div class="medi-bar">
                    <div id="mb_bar_item_1" class="drug-list-bar" >药品清单</div>
                    <div id="mb_bar_item_2" class="pres-list-bar" >处方记录</div>
                    <div id="mb_bar_item_3" class="medi-list-bar all-list-bar-active" >用药记录</div>
                </div>
                <!-- 药品清单 -->
                <div id="mb_bar_detail_1" style="display: none; height: 480px; margin: 10px" class="">
                    <table id="mb_table">
                        <thead>
                        <tr>
                            <th data-field="id" data-formatter="idFormatter">ID</th>
                            <th data-field="name">Item Name</th>
                            <th data-field="price">Item Price</th>
                        </tr>
                        </thead>
                    </table>
                </div>
                <!-- 处方记录 -->
                <div id="mb_bar_detail_2" style="display: none; height: 480px;  margin: 10px" class=""></div>
                <!-- 用药记录 -->
                <div id="mb_bar_detail_3" style="" class="medi-detl">
                    <div id="medi_list" class="medi-list">
                        <div class="medi-search" >
                            <div class="ms-year">
                                <div id="ms_year_left" class="ms-left ms-left-active"></div>
                                <div id="ms_tx_year" class="ms-text">2015年</div>
                                <div id="ms_year_right" class="ms-right ms-right-active"></div>
                            </div>
                            <div class="ms-month">
                                <div id="ms_month_left" class="ms-left ms-left-active"></div>
                                <div id="ms_tx_month" class="ms-text">11月</div>
                                <div id="ms_month_right" class="ms-right ms-right-active"></div>
                            </div>
                            <div id="ms_today" class="ms-today">返回今日</div>
                        </div>
                        <div id="medi_info_title" class="medi-info-title" >2015-11-06&nbsp;用药信息</div>

                        <div id="medi_calendar" class="medi-calendar"></div>
                        <div id="medi_info" class="medi-info">
                            <div style="display: none" class="ms-grid">
                                <div class="ms-grid-row">
                                    <div class="ms-grid-row-seq">1</div>
                                    <div class="ms-grid-row-title">胺碘酮</div>
                                    <div class="ms-grid-row-info">
                                        <div class="ms-grid-row-info-left">2粒</div>
                                        <div class="ms-grid-row-info-right">16:35</div>
                                    </div>
                                </div>

                                <div class="ms-grid-row">
                                    <div class="ms-grid-row-seq">2</div>
                                    <div class="ms-grid-row-title">胺碘酮</div>
                                    <div class="ms-grid-row-info">
                                        <div class="ms-grid-row-info-left">2粒</div>
                                        <div class="ms-grid-row-info-right">16:35</div>
                                    </div>
                                </div>

                                <div class="ms-grid-row">
                                    <div class="ms-grid-row-seq">3</div>
                                    <div class="ms-grid-row-title">胺碘酮</div>
                                    <div class="ms-grid-row-info">
                                        <div class="ms-grid-row-info-left">2粒</div>
                                        <div class="ms-grid-row-info-right">16:35</div>
                                    </div>
                                </div>

                                <div class="ms-grid-row">
                                    <div class="ms-grid-row-seq">4</div>
                                    <div class="ms-grid-row-title">胺碘酮</div>
                                    <div class="ms-grid-row-info">
                                        <div class="ms-grid-row-info-left">2粒</div>
                                        <div class="ms-grid-row-info-right">16:35</div>
                                    </div>
                                </div>

                                <div class="ms-grid-row">
                                    <div class="ms-grid-row-seq">5</div>
                                    <div class="ms-grid-row-title">胺碘酮</div>
                                    <div class="ms-grid-row-info">
                                        <div class="ms-grid-row-info-left">2粒</div>
                                        <div class="ms-grid-row-info-right">16:35</div>
                                    </div>
                                </div>
                            </div>
                            <div style="display: none" class="ms-page-bar">
                                <div class="ms-left ms-page-pre ms-left-active">&nbsp;</div>
                                <a href="javascript:void(0)" class="ms-page-num ms-page-num-active">1</a>
                                <a href="javascript:void(0)" class="ms-page-num ">2</a>
                                <a href="javascript:void(0)" class="ms-page-num ">3</a>
                                <a href="javascript:void(0)" class="ms-page-num ">4</a>
                                <a href="javascript:void(0)" class="ms-page-num ">5</a>
                                <div  class="ms-right ms-page-next ms-right-active">&nbsp;</div>
                            </div>
                            <div class="ms-no-info">
                                今日无用药信息
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <!-- 检查情况 -->
            <div id="b_detail_3" class="vis-details" style="display: none;height: 550px">&nbsp;</div>
            <!-- 体征指标动态 -->
            <div id="b_detail_4" class="vis-details" style="height: 870px">
                <div class="bodysta-type-bar">
                    <div  class="ms-left bodysta-type-left">&nbsp;</div>
                    <div style="float: left; overflow: hidden; height: 56px">
                        <ul>
                            <li><a class="active" href="javascript:void (0)">身高体重</a></li>
                            <li><a href="javascript:void (0)">血压脉搏</a></li>
                            <li><a href="javascript:void (0)">血糖</a></li>
                            <li><a href="javascript:void (0)">血氧</a></li>
                            <li><a href="javascript:void (0)">脂肪</a></li>
                            <li><a href="javascript:void (0)">骨密度</a></li>
                        </ul>
                    </div>
                    <div  class="ms-right ms-right-active bodysta-type-right">&nbsp;</div>
                </div>
                <div class="bodysta-type-content">
                    <div id="charts" class="bodysta-type-charts"></div>
                    <div class="bodysta-type-info">
                        <div class="bodysta-type-info-title">指标说明</div>
                        <div style="height: 200px" class="">&nbsp;</div>
                        <div class="bodysta-type-info-title">指标分析</div>
                        <div class="">&nbsp;</div>
                    </div>
                </div>
                <div class="bodysta-type-records">
                    <div class="bodysta-type-r-bar">
                        <div class="bodysta-type-r-title">历史记录</div>
                        <div class="bodysta-type-r-search"></div>
                    </div>
                    <div class="bodysta-type-r-grid">
                        <table id="bt_table">
                            <thead>
                            <tr>
                                <th data-field="id" data-formatter="idFormatter">ID</th>
                                <th data-field="name">Item Name</th>
                                <th data-field="price">Item Price</th>
                            </tr>
                            </thead>
                        </table>
                    </div>
                </div>
            </div>
            <!-- 保健活动 -->
            <div id="b_detail_5" class="vis-details" style="display: none;height: 550px">&nbsp;</div>
        </div>
    </div>
</div>
<script src="float-menu-tree.js"></script>
<script>
    function clk(date){
        $.each($('.fc-day-grid').find('.fc-fl-bg-active'), function (i, el) {
            $(el).css('background-color', '#FFF');
            $(el).removeClass('.fc-fl-bg-active');
        });

        var el = $('.fc-day-grid').find('.fc-fl-bg[data-date="'+date+'"] > div');
        el.css('background-color', '#FF6400');
        el.addClass('fc-fl-bg-active');

        $('#medi_info_title').html(date+'&nbsp;用药信息');
    }
    $(function (win) {
        var data = ['2015-11-30'];
        var calendarNode = $('#medi_calendar');
        var ms_tx_year = $('#ms_tx_year');
        var ms_tx_month = $('#ms_tx_month');
        $.fullCalendar.DayGrid.prototype.renderNumberCellHtml = function(date) {
            var classes;

            if (!this.view.dayNumbersVisible) { // if there are week numbers but not day numbers
                return '<td/>'; //  will create an empty space above events :(
            }

            classes = this.getDayClasses(date);
            classes.unshift('fc-day-number');

            return '' +
                    '<td onclick="clk(\''+date.format()+'\')" class="' + classes.join(' ') + '" data-date="' + date.format() + '">' +
                    date.date() +
                    '</td>';
        }

        $.fullCalendar.DayGrid.prototype.renderBgCellHtml = function(date) {
            var isShowBg = false;
            for(var i=0;i<data.length;i++){
                if(data[i]==date.format()){
                    isShowBg=true;
                    break;
                }
            }
            var d = data;
            var view = this.view;
            var classes = this.getDayClasses(date);

            classes.unshift('fc-day', view.widgetContentClass);
            var cls = classes.join(' ');

            var bg = '<div class="fc-fl-bg-normal" style="height: '+($.Util.isIE()? '61px' : '100%')+' ; background-color: #fff; border: 2px solid; border-color: '+(cls.indexOf('fc-today')==-1?'#fff' : '#FF6400')+'">';
            bg += isShowBg ?
                        '<div style="width:32px;height:32px;margin-left: 28px; margin-top: 10px; background: url(\'imgs/medicine01_icon.png\') no-repeat scroll 0 0 transparent;" ></div></div>'
                        : '&nbsp;</div>';
            return '<td class="' + cls + ' fc-fl-bg"' +
                    ' data-date="' + date.format('YYYY-MM-DD') + '"' +
                    '>'+bg+'</td>';
        }


        function load(id, hid){
            $('#b_detail_'+hid).hide();
            $('#b_detail_'+id).show();
            if(id==2){
                $('#medi_calendar').fullCalendar('render');
            }
        }

        function setTimeInfo(){
            var date = calendarNode.fullCalendar('getDate');
            ms_tx_year.html(date.year()+'年');
            ms_tx_month.html( (date.month()+1)+'月');
        }
        function bindEvent(){
            //大导航栏绑定事件
            $.each($('div[id^="b_item_"]'), function (i, el) {
                $(el).click(function (e) {
                    var hid = 1;
                    var id =parseInt($(el).attr('id').replace('b_item_',''));
                    $.each($('div[class*="b-item-s-active"]'), function (i, el) {
                        hid = parseInt($(el).attr('id').replace('b_item_',''));
                        $(el).removeClass('b-item-s-active');
                    })
                    var ar = $('#b_item_ar');
                    if(id==1){
                        $(el).addClass('b-item-f-active');
                        ar.css('left','175px');
                    }
                    else{
                        $.each($('div[class*="b-item-f-active"]'), function (i, el) {
                            $(el).removeClass('b-item-f-active');
                        })
                        $(el).addClass('b-item-s-active');
                        ar.css('left',(430 + (id-2)*150) + 'px' );
                    }
                    load(id, hid);
                })
            });
            //用药情况的事件绑定
            $.each($('.medi-bar').find('div[id^="mb_bar_item"]'), function (i, el) {
                $(el).click(function () {
                    var id = parseInt($(this).attr('id').replace('mb_bar_item_', ''));
                    var hid = 0;
                    $.each($('.medi-bar').find('.all-list-bar-active'), function (i, el) {
                        hid = parseInt($(el).attr('id').replace('mb_bar_item_', ''));
                        $(el).removeClass('all-list-bar-active');
                    })
                    $(el).addClass('all-list-bar-active');
                    $('#mb_bar_detail_'+hid).hide();
                    $('#mb_bar_detail_'+id).show();

                    if(id==1){
                        $('#mb_table').bootstrapTable({
                            url : 'data/data1.json',
                            pagination : true,
                            height : 440
                        });
                    }
                })
            })

            $('#ms_year_left').click(function () {
                calendarNode.fullCalendar('prevYear');
                setTimeInfo();
            });
            $('#ms_year_right').click(function () {
                calendarNode.fullCalendar('nextYear');
                setTimeInfo();
            })
            $('#ms_month_right').click(function () {
                calendarNode.fullCalendar('next');
                setTimeInfo();
            })
            $('#ms_month_left').click(function () {
                calendarNode.fullCalendar('prev');
                setTimeInfo();
            })
            $('#ms_today').click(function () {
                calendarNode.fullCalendar('today');
                setTimeInfo();
            })
        }
        bindEvent();

        $('#bt_table').bootstrapTable({
            url : 'data/data1.json',
            pagination : true,
            height : 320,
            pageSize: 4
        });

        $('#medi_calendar').fullCalendar({
            dayNamesShort:['日', '一', '二', '三', '四', '五', '六'],
            contentHeight:440,
            header: false,
            editable: true,
            eventLimit: true
        });
        setTimeInfo();
        clk(calendarNode.fullCalendar('getDate').format('YYYY-MM-DD'));

        require.config({
            paths: {
                echarts: 'echarts-2.2.7/build/dist'
            }
        });

        // 使用
        require(
                [
                    'echarts',
                    'echarts/chart/line' // 使用柱状图就加载bar模块，按需加载
                ],
                function (ec) {
                    var myChart = ec.init(document.getElementById('charts'));
                    var option = {
                        tooltip : {
                            trigger: 'item',
                            formatter : function (params) {
                                var date = new Date(params.value[0]);
                                data = date.getFullYear() + '-'
                                        + (date.getMonth() + 1) + '-'
                                        + date.getDate() + ' '
                                        + date.getHours() + ':'
                                        + date.getMinutes();
                                return data + '<br/>'
                                        + params.value[1] + ', '
                                        + params.value[2];
                            }
                        },
                        dataZoom: {
                            show: true,
                            start : 70
                        },
                        grid: {
                            y2: 80
                        },
                        xAxis : [
                            {
                                type : 'time',
                                splitNumber:10
                            }
                        ],
                        yAxis : [
                            {
                                type : 'value'
                            }
                        ],
                        series : [
                            {
                                name: 'series1',
                                type: 'line',
                                showAllSymbol: true,
                                itemStyle: {
                                    normal: {
                                        color: function(params) {
                                            var colorList = [
                                                '#C1232B','#B5C334','#FCCE10','#E87C25','#27727B'
                                            ];
                                            debugger
                                            return colorList[params.dataIndex]
                                        }
                                    }
                                },
                                symbolSize: function (value){
                                    return  2;
                                },
                                data: (function () {
                                    var d = [];
                                    var len = 0;
                                    var now = new Date();
                                    var value;
                                    while (len++ < 200) {
                                        d.push([
                                            new Date(2014, 9, 1, 0, len * 10000),
                                            (Math.random()*30).toFixed(2) - 0,
                                            (Math.random()*100).toFixed(2) - 0
                                        ]);
                                    }
                                    return d;
                                })()
                            }
                        ]
                    };
                    myChart.setOption(option);
                })

    })
</script>
</body>