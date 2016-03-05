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
            '<td  class="' + classes.join(' ') + '" data-date="' + date.format() + '">' +
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
            '<div style="width:32px;height:32px;margin-left: 28px; margin-top: 10px; ' +
                'background: url(\''+ $.Context.STATIC_PATH+'/browser/imgs/medicine01_icon.png\') no-repeat scroll 0 0 transparent;" ></div></div>'
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

    function bindCalendarEvent(){
        $.each($('.fc-day-grid').find('.fc-day-number'), function (i, el) {
            $(el).click(function () {
                var date = $(this).attr('data-date');
                $.each($('.fc-day-grid').find('.fc-state-select'), function (i, el) {
                    $(el).toggleClass('fc-state-select', false);
                });
                $(this).toggleClass('fc-state-select', true);
                $.each($('.fc-day-grid').find('.fc-fl-bg-active'), function (i, el) {
                    $(el).css('background-color', '#FFF');
                    $(el).removeClass('.fc-fl-bg-active');
                });

                var el = $('.fc-day-grid').find('.fc-fl-bg[data-date="'+date+'"] > div');
                el.css('background-color', '#FF6400');
                el.addClass('fc-fl-bg-active');

                $('#medi_info_title').html(date+'&nbsp;用药信息');
            });
        });
        

    }

    function setTimeInfo(isToday){
        var date = calendarNode.fullCalendar('getDate');
        ms_tx_year.html(date.year()+'年');
        ms_tx_month.html( (date.month()+1)+'月');

        if(isToday){
            var today = date.format('YYYY-MM-DD');
            var el = $('.fc-day-grid').find('.fc-day-number[data-date="'+today+'"]');
            $(el).click();
        }
    }
    function bindEvent(){

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
                        url : $.Context.STATIC_PATH+ '/browser/data/data1.json',
                        pagination : true,
                        height : 440,
                        columns: [
                            {title:'序号', field:'id'},
                            {title:'药品名称', field:'name'},
                            {title:'单位', field:'price'},
                            {title:'规格', field:'aa'},
                            {title:'近3个月服用量', field:'bb'},
                            {title:'近6个月服用量', field:'src'},
                            {title:'上一次服用时间', field:'src'}
                        ]
                    });
                }
                else if(id==2){
                    $.getJSON($.Context.STATIC_PATH + '/browser/data/portal-data.json', function (data) {

                        $('#formulation_records').timeline({
                            horizontal: true,
                            data:data.timeLine,
                            prevPage:$('.mb_large_prev'),
                            nextPage:$('.mb_large_next')
                            } );
                    });


                }
            })
        })

        $('#ms_year_left').click(function () {
            calendarNode.fullCalendar('prevYear');
            setTimeInfo();
            bindCalendarEvent();
        });
        $('#ms_year_right').click(function () {
            calendarNode.fullCalendar('nextYear');
            setTimeInfo();
            bindCalendarEvent();
        })
        $('#ms_month_right').click(function () {
            calendarNode.fullCalendar('next');
            setTimeInfo();
            bindCalendarEvent();
        })
        $('#ms_month_left').click(function () {
            calendarNode.fullCalendar('prev');
            setTimeInfo();
            bindCalendarEvent();
        })
        $('#ms_today').click(function () {
            calendarNode.fullCalendar('today');
            bindCalendarEvent();
            setTimeInfo(true);
        })

    }
    bindEvent();

    calendarNode.fullCalendar({
        dayNamesShort:['日', '一', '二', '三', '四', '五', '六'],
        contentHeight:440,
        header: false,
        editable: true,
        eventLimit: true,
        firstDay: 1
    });
    bindCalendarEvent();
    setTimeInfo(true);
    $('#ms_page_bar').msPagination({
        total: 51,
        clickFunc: function (opts) {
//                alert(opts.curPage);
        }
    });
})