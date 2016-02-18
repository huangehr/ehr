/** 机构数据采集
 * Created by zqb on 2015/11/17.
 */

define(function (require, exports, module) {
    var $ = require('base').$,
        context = require('base').context;
    $(function () {
        initOrg($('#adapter_org').val());

        $("#switch_dataSet").click(function(){
            $("#orgDataSetDiv").show();
            $("#orgDictDiv").hide();
        });
        $("#switch_dict").click(function(){
            $("#orgDataSetDiv").hide();
            $("#orgDictDiv").show();
            $(window).trigger('resize');
        });

        function initOrg(code){
            $("#adapter_org_name").val("");
            $('#adapter_org_parent').val("");
            $('#adapter_org_description').val("");
            $.ajax({
                type: "POST",
                url: context.path + "/adapterorg/getAdapterOrg",
                data: {code: code},
                dataType: "json",
                success: function (data) {
                    if(data.successFlg){
                        var model = data.obj.adapterOrg;
                        $("#adapter_org_name").val(model.name);
                        $('#adapter_org_parent').val(model.parentValue);
                        $('#adapter_org_description').val(model.description);
                    }
                },
                error: function (data) {
                }
            });
        }

        $("#switch_dataSet").trigger("click");
    })
});