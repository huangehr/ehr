$(function (win) {

    var msTypeBar = {
        $msLeft: $('#ms_left'),
        $msRight: $('#ms_right'),
        $msTypeLs: $('#ms_typeLs'),
        $ul: undefined,
        totalPage: 0,
        curPage: 1,
        pageSize: 6,
        selectItem: undefined,
        data: [
            '身高体重', '血压脉搏', '血糖', '血氧', '脂肪', '骨密度', '骨密度', '骨密度'
        ],
        init: function () {
            var data = this.data;
            this.$ul = $('<ul>');
            this.createLi();
            this.$ul.appendTo(this.$msTypeLs);
            this.setCurInfo();
            this.bindEvent();
            this.$msTypeLs.find('a:first').click();
        },
        createLi: function () {
            var m = this;
            m.$ul.empty();
            var s = m.pageSize*(m.curPage-1);
            for(var i= 0; i< m.pageSize && s+i< m.data.length; i++){
                m.$ul.append('<li><a id="ms_type_'+(s+i)+'" href="javascript:void (0)">'+ m.data[i+s]+'</a></li>');
            }
        },
        setCurInfo: function () {
            this.totalPage = parseInt(this.data.length/this.pageSize) + (this.data.length%this.pageSize==0? 0 : 1);
        },
        bindEvent: function (bingdPageNum) {
            var m = this;
            $.each(m.$msTypeLs.find("a"), function (i, el) {
                $(el).click(function () {
                    var id = parseInt($(this).attr('id').replace('ms_type_', ''));
                    if(id==m.selectItem){
                        $(this).toggleClass('active', true);
                        return;
                    }
                    m.selectItem = id;
                    $.each(m.$msTypeLs.find("a[class='active']"), function (i, el) {
                        $(el).removeClass('active');
                    })
                    $(this).addClass('active');

                })
            })
            if(bingdPageNum)
                return;
            m.$msLeft.click(function () {
                if(m.curPage==1)
                    return;
                m.curPage--;
                if(m.curPage==1)
                    $(this).toggleClass('ms-left-active', false);
                m.$msRight.toggleClass('ms-right-active', true);
                m.createLi();
                m.bindEvent(true);
                m.$msTypeLs.find('#ms_type_'+ m.selectItem).click();
            })

            m.$msRight.click(function () {
                if(m.curPage== m.totalPage)
                    return;
                m.curPage++;
                if(m.curPage== m.totalPage)
                    $(this).toggleClass('ms-right-active', false);
                m.$msLeft.toggleClass('ms-left-active', true);
                m.createLi();
                m.bindEvent(true);
                m.$msTypeLs.find('#ms_type_'+ m.selectItem).click();
            })
        }
    }
    msTypeBar.init();

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

    function initLineChart(ec) {
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