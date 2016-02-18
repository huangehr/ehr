
$(function (win) {
    function bindEvent(){
        $('#back').click(function () {
            history.go(-1);
        });

        $.each($('.ht-menu').find('.ht-sl'), function (i, el) {
            $(el).mouseover(function () {
                $(this).toggleClass('ht-sl-active', true);
                $(this).find('.ht-sl-box').show();
            })
            $(el).mouseout(function () {
                $(this).toggleClass('ht-sl-active', false);
                $(this).find('.ht-sl-box').hide();
            })
        })

        $.each($('.ht-menu').find('a'), function (i, el) {
            $(el).click(function () {
                $.each($('.ht-menu').find('a[class*="select"]'), function (i, el) {
                    $(el).toggleClass('select', false);
                })
                $(this).toggleClass('select', true);
                $.each($('.ht-menu').find('.ht-sl'), function (i, el) {
                    $(el).mouseout();
                })
            })
        })
    }
    bindEvent();

    $('#bt_table').bootstrapTable({
        url : $.Context.STATIC_PATH + '/browser/data/data1.json',
        pagination : true,
        height : 320,
        pageSize: 4,
        columns: [
            {title:'序号', field:'id'},
            {title:'测量时间', field:'name'},
            {title:'身高(cm)', field:'price'},
            {title:'体重(kg)', field:'aa'},
            {title:'健康指数', field:'bb'},
            {title:'数据来源', field:'src'}
        ]
    });

    function initLineChart() {
        var myChart = echarts.init(document.getElementById('charts'));
        var option = {
            tooltip : {
                trigger: 'item',
                formatter : function (params) {
                    var data = "<div style='text-align: left; '>" +
                        "数值：" +params.value[1]+ "<br>" +
                        "单位：<br>" +
                        "数据来源："+ (params.value[3]==0?"手动录入":"导入") +"</div>";
                    return data;
                }
            },
            dataZoom: {
                show: true,
                start : 90
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
                            lineStyle : {
                                color : '#787878',
                                width: 1
                            },
                            color: function(params) {
                                var colorList = [
                                    '#23AFFF','#FF6400'
                                ];
                                var data = params.series.data[params.dataIndex];
                                if(data)
                                    return colorList[data[3]]
                                return colorList[0];
                            }
                        }
                    },
                    symbolSize: function (value){
                        return  4;
                    },
                    markLine: {
                        data: [
                            [{name: '标线1起点', value: 20, x: 80, y: 200},
                                {name: '标线1终点',value: 20, x: 720, y: 200}]
                        ]
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
                                (Math.random()*100).toFixed(2) - 0,
                                parseInt(Math.random()*2)
                            ]);
                        }
                        return d;
                    })()
                }
            ]
        };
        myChart.setOption(option);
    }
    initLineChart();


})