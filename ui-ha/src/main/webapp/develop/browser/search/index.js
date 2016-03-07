$(function (win) {
    $('#ms_page_bar').msPagination({
        total: 65,
        showItems: 11,
        clickFunc: function (opts) {
//                alert(opts.curPage);
        }
    });

    $('#back').click(function () {
        history.go(-1);
    });
})