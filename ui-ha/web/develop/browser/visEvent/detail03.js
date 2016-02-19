$(function (win) {
    $.getJSON($.Context.STATIC_PATH + '/browser/data/portal-data.json', function (data) {

        $('#evt-history').timeline({
            horizontal: true,
            data:data.timeLine,
            prevPage:$('#evt_3_large_prev'),
            nextPage:$('#evt_3_large_next')
        } );
    });
    
    $('#calendar_picker_his').keyup(function (e) {
        if(e.keyCode==13){
            var val = $(this).val();
            var timeline = $.TimeLine.getInstance(document.getElementById('evt-history'));
            if(timeline) {
                var $li = $('#evt-history').find('li[data-index='+val+']');
                timeline.locate($li, true);
            }
        }
    })


})