/** 采集标准
 * Created by zqb on 2015/11/19.
 */
define(function (require, exports, module) {

    require('app/adapter/adapterOrg.css');
    require('modules/chosen/chosen.css');
    var $ = require('base').$,
        context = require('base').context,
        Grid = require('modules/components/grid'),
        ViewController = require('viewController'),
        LinkageSelect = require('modules/components/linkageSelect'),
        Chosen = require('modules/chosen/chosen.jquery'),
        JValidate = require('jValidate');
    $.i18n.properties({              //加载资浏览器语言对应的资源文件
        name:'message',    //资源文件名称
        path: context.resourcePath+'/resources/i18n/', //资源文件路径
        mode:'map',                  //用Map的方式使用资源文件中的值
        callback: function() {       //加载成功后设置显示内容
        }
    });

    $(function () {
        initDDL(21, $('#type'));
        var orgChosen = null;
        var adapterOrgChosen = null;
        var code=null;
        var searchNm = $("#search_no").val();
        var addFlg;
        var grid = new Grid('#adapter_grid', {
            url: context.path + "/adapterorg/searchAdapterOrg",
            mtype:'post',
            datatype: 'json',
            shrinkToFit: true,
            scrollOffset: 0,
            width: $(window).width(),
            height: $(window).height() - 160,
            autoFit: true,
            marginHeight:160,
            rowNum: 10,
            multiselect: true,//多选
            viewrecords: true, // 是否显示总记录数
            colNames: ["序号","代码","名称","类别代码","类别","映射机构代码","映射机构","父级标准代码","父级标准",$.i18n.prop('grid.operation')],
            colModel: [
                {
                    name: 'order',
                    index: 'order',
                    align: "center",
                    width: 50
                },
                {
                    name: 'code',
                    index: 'code',
                    align: "center"
                },
                {
                    name: 'name',
                    index: 'name',
                    align: "center"
                },
                {
                    name: 'type',
                    index: 'type',
                    align: "center",
                    hidden:true
                },
                {
                    name: 'typeValue',
                    index: 'typeValue',
                    align: "center"
                },
                {
                    name: 'org',
                    index: 'org',
                    align: "center",
                    hidden:true
                },
                {
                    name: 'orgValue',
                    index: 'orgValue',
                    align: "center"
                },
                {
                    name: 'parent',
                    index: 'parent',
                    align: "center",
                    hidden:true
                },
                {
                    name: 'parentValue',
                    index: 'parentValue',
                    align: "center"
                },
                {
                    name: 'operator ',
                    index: 'operator',
                    sortable: false,
                    width:180,
                    align: "center",
                    formatter: function (value, grid, rows, state) {
                        return '<a href="'+context.path+'/orgdataset/initial?adapterOrg='+rows.code+'" class="J_adapter-btn f-ml10 f-mr10" >'+"维护"+'</a>' +
                            '/<a data-toggle="modal" data-target="#modify_row_modal" class="J_modify-btn f-ml10 f-mr10" data-rowid="' + grid.rowId + '" >'+$.i18n.prop("btn.update")+'</a>' +
                            '/<a data-toggle="modal" data-target="#delete_row_modal" class="J_delete-btn f-ml10" data-rowid="' + grid.rowId + '">'+$.i18n.prop("btn.delete")+'</a>';
                    }
                }
            ],
            jsonReader: {
                root: "detailModelList",
                page: "currPage",
                total: "totalPage",
                records: "totalCount",
                repeatitems: false,
                id: "0"
            },
            postData: {searchNm: searchNm},
            onSelectRow: function (rowId, status, e) {
            }
        }).render();

        new ViewController('#adapter_grid', {
            events: {
                'click .J_adapter-btn': 'adapter',
                'click .J_modify-btn': 'modifyItem',
                'click .J_delete-btn': 'deleteItem'
            },
            handlers: {
                adapter: function (e) {
                    //
                },
                modifyItem: function (e) {
                    var cell = $(e.currentTarget);
                    var rowid = cell.attr("data-rowid");
                    var code = grid.instance.getRowData(rowid).code;
                    getModalInfo(code);
                    //不可编辑控制
                    detailMode("MODIFY");
                },
                deleteItem: function (e) {
                    var cell = $(e.currentTarget);
                    var rowid = cell.attr("data-rowid");
                    var code = grid.instance.getRowData(rowid).code;
                    $("#delete_id").val(code);
                }
            }
        });

        <!--地址级联 -->
        var addrSelector = new LinkageSelect([
            {
                selector: "#province_select", // 一级联动下拉框id选择器
                url: context.path + "/patient/getParent", // 一级联动下拉框数据请求路径
                data: {level: 1}, // 一级联动下拉框数据请求发送给服务器的ajax数据
                blankItem: true, // 是否插入置空选项行，默认为true
                paramMap: { // 请求参数映射: 在触发下拉框change事件时，将选中项的value值和其显示文本设置为pid和province请求参数
                    id: 'pid',
                    name: 'province'
                }
            },
            {
                selector: "#city_select", // 二级联动下拉框id选择器
                url: context.path + "/patient/getChildByParent",
                blankItem: true, // 是否插入置空选项行，默认为true
                paramMap: { // 请求参数映射: 在触发下拉框change事件时，将选中项的显示文本设置为city请求参数
                    id: 'pid',
                    name: "city"
                }
            },
            {
                selector: "#district_select", // 三级联动下拉框id选择器
                url: context.path + "/patient/getChildByParent",
                blankItem: true, // 是否插入置空选项行，默认为true
                paramMap: { // 请求参数映射: 在触发下拉框change事件时，将选中项的显示文本设置为city请求参数
                    id: 'pid',
                    name: "district"
                }
            },
            {
                selector: "#town_select", // 四级联动下拉框id选择器
                url: context.path + "/patient/getChildByParent",
                blankItem: true, // 是否插入置空选项行，默认为true
                paramMap: { // 请求参数映射: 在触发下拉框change事件时，将选中项的显示文本设置为city请求参数
                    id: 'pid',
                    name: "town"
                }
            }
        ]);

        <!--校验器-->
        var validator = new JValidate.Validation($("#update_form"),{immediate:true,onSubmit:false});
        //焦点丢失校验
        $('#update_form').on('blur','.required',function() {
            JValidate.Validation.validateElement(this);
        });
        //模态
        $('#modify_row_modal').on('hidden.bs.modal', function (e) {
            validator.reset();
        });

        $(".J_searchBtn").click(function () {
            reloadGrid();
        });

        $("#add_btn").click(function () {
            detailMode("ADD");
            clearModal();
        });

        //delete multi row
        $("#delete_rows_btn").click(function () {
            var rowIds = grid.instance.jqGrid('getGridParam', 'selarrrow');
            var code=[];
            for(var i= 0;i<rowIds.length;i++){
                code[i]=grid.instance.getRowData(rowIds[i]).code;
            }
            delAdapterOrg(code.join(","));
        });

        //delete one row
        $("#delete_btn").click(function () {
            var code = $("#delete_id").val();
            delAdapterOrg(code);
        });

        $("#update_btn").click(function () {
            if(!validator.validate() ) {return;}
            if (addFlg) {
                $.ajax({
                    type: "POST",
                    url: context.path + "/adapterorg/addAdapterOrg",
                    data: $('#update_form').serialize(),
                    dataType: 'json',
                    success: function (data) {
                        if (data.successFlg) {
                            reloadGrid();
                            $(".close",$('#modify_row_modal')).click();
                        } else {
                            alert(data.errorMsg);
                            return;
                        }
                    },
                    error: function (data) {
                    }
                });
            }else{
                var code=$("#code").val();
                var name=$("#name").val();
                //todo:area
                var description=$("#description").val();
                $.ajax({
                    type: "POST",
                    url: context.path + "/adapterorg/updateAdapterOrg",
                    data: {code:code,name:name,description:description},
                    dataType: 'json',
                    success: function (data) {
                        if (data.successFlg) {
                            reloadGrid();
                            $(".close",$('#modify_row_modal')).click();
                        } else {
                            alert(data.errorMsg);
                            return;
                        }
                    },
                    error: function (data) {
                    }
                });
            }
        });

        $("#area").click(function(){
            addrSelector.selects[0].setValueByName("");
            addrSelector.selects[1].setValueByName("");
            addrSelector.selects[2].setValueByName("");
            addrSelector.selects[3].setValueByName("");
        });
        $("#location_confirm").click(function () {
            var province = $('#province_select').find('option:selected').html();
            var provinceCode = $('#province_select').val();
            var city = $('#city_select').find('option:selected').html();
            var cityCode = $('#city_select').val();
            var district = $('#district_select').find('option:selected').html();
            var districtCode = $('#district_select').val();
            var town = $('#town_select').find('option:selected').html();
            var townCode = $('#town_select').val();

            var area = province + city + district + town;
            var areaCode = townCode||districtCode||cityCode||provinceCode;
            $("#area_code").val(areaCode);
            $("#area").val(area);
            if (area){
                var org = $("#org").val();
                if (org){
                    //区域代码：机构代码+'ORG'+地区编码
                    $("#code").val(org + 'ORG' + areaCode);
                    //默认名称为机构名+(地区名)
                    $("#name").val($("#org option:selected").text()+'('+area+')');
                }
            }
            $("#province").val(province);
            $("#city").val(city);
            $("#district").val(district);
            $("#town").val(town);
        });

        $("#org").change(function(){
            //默认名称为机构名
            var org = $("#org").val();
            var type = $("#type").val();
            var area = $("#area").val();
            if (type=='3' && area){
                //区域代码：机构代码+'ORG'+地区编码
                $("#code").val(org+'ORG'+$("#area_code").val());
                //区域名称：机构名+(地区名)
                $("#name").val($("#org option:selected").text()+'('+area+')');
            }else{
                $("#code").val(org);
                $("#name").val($("#org option:selected").text());
            }

        });

        $("#type").change(function(){
            var type = $("#type").val();
            if (type=='1'){
                //厂商，初始标准只能是厂商
                initOrg($("#org"),type);//采集机构
                $("#areaModel").hide();
            }else if (type=='2'){
                //医院，初始标准没有限制
                initOrg($("#org"),type);//采集机构
                $("#areaModel").hide();
            }
            if (type=='3'){
                //区域,初始标准只能选择厂商或区域
                initOrg($("#org"),'1');//采集机构
                $("#areaModel").show();
            }
            initAdapterOrg($("#parent"),type);
        });

        //根据传入的字典ID及显示对象，加载下拉框的数据。
        function initDDL(dictId, targetInit) {
            /*下拉框的方法*/
            var target = $(targetInit);
            $.ajax({
                url: context.path + "/dict/searchDictEntryList",
                type: "post",
                dataType: "json",
                data: {dictId: dictId, page: "1", rows: "5"},
                success: function (data) {
                    if (data.successFlg) {
                        //var option ='<option value="0">选择所有</option>';
                        var option = '';
                        for (var i = 0; i < data.detailModelList.length; i++) {
                            option += '<option value="' + data.detailModelList[i].code + '">' + data.detailModelList[i].value + '</option>';
                        }
                        $(target).append(option);
                    }
                }
            });
        }

        //采集机构
        function initOrg(target,type){
            $.ajax({
                url:context.path + "/adapterorg/getOrgList",
                type:"post",
                dataType:"json",
                async: false,
                data:{type:type},
                success:function(data){
                    target.empty();
                    if (data.successFlg){
                        var organizations = data.obj;
                        var option ='<option value=""></option>';
                        for(var i=0;i<organizations.length;i++){
                            //orgCode,orgName
                            var org=organizations[i].split(",");
                            var orgCode = org[0];
                            var orgName = org[1];
                            option += '<option value="' + orgCode + '">' + orgName + '</option>';
                        }
                        target.append(option);
                    }
                }
            });
            if(!orgChosen) {
                orgChosen = target.chosen();
            } else {
                target.trigger("liszt:updated");
            }
        }

        //初始标准
        function initAdapterOrg(target,type){
            $.ajax({
                url:context.path + "/adapterorg/getAdapterOrgList",
                type:"post",
                dataType:"json",
                async: false,
                data:{type:type},
                success:function(data){
                    target.empty();
                    if (data.successFlg){
                        var organizations = data.obj;
                        var option ='<option value=""></option>';
                        for(var i=0;i<organizations.length;i++){
                            //orgCode,orgName
                            var org=organizations[i].split(",");
                            var orgCode = org[0];
                            var orgName = org[1];
                            option += '<option value="' + orgCode + '">' + orgName + '</option>';
                        }
                        target.append(option);
                    }
                }
            });
            if(!adapterOrgChosen) {
                adapterOrgChosen = target.chosen();
            } else {
                target.trigger("liszt:updated");
            }
        }

        function delAdapterOrg(code){
            $.ajax({
                type: "POST",
                url: context.path + "/adapterorg/delAdapterOrg",
                data: {code: code},
                dataType: "json",
                success: function (data) {
                    if (data.successFlg) {
                        reloadGrid();
                    } else {
                        alert($.i18n.prop('message.distribution.failure'));
                        return;
                    }
                },
                error: function (data) {
                }
            });
        }

        function clearModal() {
            $('input', $("#update_form")).val("");
            $('select', $("#update_form")).val("");
            $('textarea', $("#update_form")).val("");
        }

        function getModalInfo(code) {
            clearModal();
            $.ajax({
                type: "POST",
                url: context.path + "/adapterorg/getAdapterOrg",
                data: {code: code},
                dataType: "json",
                success: function (data) {
                    if (data.successFlg){
                        var model = data.obj.adapterOrg;
                        $('#type').find("option[value='" + model.type + "']").prop("selected", true);
                        $("#code").val(model.code);
                        $('#name').val(model.name);
                        $('#description').val(model.description);
                        $("#type").trigger("change");
                        $('#parent').find("option[value='" + model.parent + "']").prop("selected", true);
                        $("#parent").trigger("liszt:updated");
                        $('#org').find("option[value='" + model.org + "']").prop("selected", true);
                        $("#org").trigger("liszt:updated");
                        var province=model.area.province||"";
                        var city=model.area.city||"";
                        var district=model.area.district||"";
                        var town=model.area.town||"";
                        var area=province;
                        if (province!=city){
                            area +=city;
                        }
                        area+=district+town;
                        $("#area").val(area);
                    }
                },
                error: function (data) {
                }
            });
        }


        function reloadGrid() {
            var searchNm = $("#search_no").val();
            grid.instance.jqGrid('setGridParam', {
                url: context.path + "/adapterorg/searchAdapterOrg",
                postData: {searchNm: searchNm} //发送数据
            }).trigger("reloadGrid");
        }

        function detailMode(type){
            if (type=='ADD'){
                addFlg=true;
                $('#type').attr("disabled",false);
                $('#parent').attr("disabled",false);
                $('#org').attr("disabled",false);
                $('#code').attr("disabled",false);
                $('#area').attr("disabled",false);
            }
            if (type=='MODIFY'){
                addFlg=false;
                $('#type').attr("disabled",true);
                $('#parent').attr("disabled",true);
                $('#org').attr("disabled",true);
                $('#code').attr("disabled",true);
                $('#area').attr("disabled",true);
            }
        }

        $("#areaModel").hide();

    });

});