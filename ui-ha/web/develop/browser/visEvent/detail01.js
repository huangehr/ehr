$(function (win) {
    $.getJSON($.Context.STATIC_PATH + '/browser/data/portal-data.json', function (data) {

        $('#ev-detail').timeline({
            horizontal: true,
            data:data.timeLine,
            prevPage:$('.evt_1_large_prev'),
            nextPage:$('.evt_1_large_next')
        } );
    });
    
    $('#calendar_picker').keyup(function (e) {
        if(e.keyCode==13){
            var val = $(this).val();
            var timeline = $.TimeLine.getInstance(document.getElementById('ev-detail'));
            if(timeline) {
                var $li = $('#ev-detail').find('li[data-index='+val+']');
                timeline.locate($li, true);
            }
        }
    })


})