$(function (win) {
    var isLoad = [false, false, false, false];
    var loadUrls = ['medEvent', 'medHistory', 'medCheck', 'signIndex', 'healthCare'] ;
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

        $('#back').click(function () {
            history.go(-1);
        });
    }
    bindEvent();

    function load(id, hid){
        $('#b_detail_'+hid).hide();
        $('#b_detail_'+id).show();
        if(!isLoad[id-1]){
            var el = $('#b_detail_'+id);
            el.append('<div class="r-loading"></div>');
            var url = $.Context.PATH + '/browser/medical/visEvent/' + loadUrls[id-1];
            el.load(url, function (b) {
                isLoad[id-1] = true;
            });
        }

    }

    $('#b_item_1').click();

})