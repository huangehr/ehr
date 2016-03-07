/**
 * Created by zqb on 2015/10/26.
 */

define(function (require, exports, module) {

    require('app/adapter/adapter.css');
    require('modules/chosen/chosen.css');
    var $ = require('base').$,
        context = require('base').context,
        Grid = require('modules/components/grid'),
        ViewController = require('viewController'),
        LinkageSelect = require('modules/components/linkageSelect'),
        Chosen = require('modules/chosen/chosen.jquery'),
        JValidate = require('jValidate'),
        LigerUi = require("modules/liger/ligerTree");
    $.i18n.properties({              //加载资浏览器语言对应的资源文件
        name:'message',    //资源文件名称
        path: context.resourcePath+'/resources/i18n/', //资源文件路径
        mode:'map',                  //用Map的方式使用资源文件中的值
        callback: function() {       //加载成功后设置显示内容
        }
    });

    $(function () {
        initDDL(21, $('#type'));
        initDDL(21,$("#adapter_org_type"));
        initVersionDDL($('#version'));
        var chosen = null;
        var treeFrom = null;
        var treeTo = null;
        var adapterDataSet=null;
        var planId=null;
        var addFlg;
        var searchNm = $("#search-no").val();
        var grid = new Grid('#data-grid', {
            url: context.path + "/adapter/searchAdapterPlan",
            mtype:'post',
            datatype: 'json',
            shrinkToFit: true,
            scrollOffset: 0,
            width: $(window).width(),
            height: $(window).height() - 160,
            autoFit: true,
            marginHeight:160,
            rowNum: 10,
            //scroll: true, //滚动加载
            multiselect: true,//多选
            viewrecords: true, // 是否显示总记录数
            colNames: ["序号","ID","方案代码","方案名称","类别代码","类别","机构代码","映射机构","版本代码","标准版本",$.i18n.prop('grid.operation')],
            colModel: [
                {
                    name: 'order',
                    index: 'order',
                    align: "center",
                    width: 50
                },
                {
                    name: 'id',
                    index: 'id',
                    align: "center",
                    hidden:true
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
                    name: 'version',
                    index: 'version',
                    align: "center",
                    hidden:true
                },
                {
                    name: 'versionName',
                    index: 'versionName',
                    align: "center"
                },
                {
                    name: 'operator ',
                    index: 'operator',
                    sortable: false,
                    width:180,
                    align: "center",
                    formatter: function (value, grid, rows, state) {
                        return '<a data-toggle="modal" data-target="#customize-row-modal" class="J_customize-btn f-mr10" data-rowid="' + grid.rowId + '" >'+"定制"+'</a>' +
                            '/<a href='+context.path+'/adapterDataSet/initial?adapterPlanId='+rows.id+' class="J_adapter-btn f-ml10 f-mr10" >'+"适配"+'</a>' +
                            '/<a data-toggle="modal" data-target="#modify-row-modal" class="J_modify-btn f-ml10 f-mr10" data-rowid="' + grid.rowId + '" >'+$.i18n.prop("btn.update")+'</a>' +
                            '/<a data-toggle="modal" data-target="#delete-row-modal" class="J_delete-btn f-ml10" data-rowid="' + grid.rowId + '">'+$.i18n.prop("btn.delete")+'</a>';
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
                var rowIds = grid.instance.jqGrid('getGridParam', 'selarrrow');

            }
        }).render();

        new ViewController('#data-grid', {
            events: {
                'click .J_customize-btn': 'customize',
                'click .J_adapter-btn': 'adapter',
                'click .J_modify-btn': 'modifyItem',
                'click .J_delete-btn': 'deleteItem'
            },
            handlers: {
                customize: function (e) {
                    var cell = $(e.currentTarget);
                    var rowid = cell.attr("data-rowid");
                    var version = grid.instance.getRowData(rowid).version;
                    planId = grid.instance.getRowData(rowid).id;
                    //标准定制 start
                    getAdapterDataSet(planId,version);
                    getStdDataSet(version,adapterDataSet);

                    $("#left_right").click(function(){
                        adapterCustomize({fromTree:treeFrom,toTree:treeTo,fromTarget:"#tree_from",toTarget:"#tree_to",fromFlag:"std",toFlag:"adapter"});
                    });
                    $("#right_left").click(function(){
                        adapterCustomize({fromTree:treeTo,toTree:treeFrom,fromTarget:"#tree_to",toTarget:"#tree_from",fromFlag:"adapter",toFlag:"std"});
                    });
                    //end 标准定制
                },
                adapter: function (e) {
                    //
                },
                modifyItem: function (e) {
                    var cell = $(e.currentTarget);
                    var rowid = cell.attr("data-rowid");
                    var id = grid.instance.getRowData(rowid).id;
                    getModalInfo(id);
                },
                deleteItem: function (e) {
                    var cell = $(e.currentTarget);
                    var rowid = cell.attr("data-rowid");
                    var id = grid.instance.getRowData(rowid).id;
                    $("#delete-id").val(id);
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


        <!--适配方案校验器-->
        var validator = new JValidate.Validation($("#update-form"),{immediate:true,onSubmit:false});
        //焦点丢失校验
        $('#update-form').on('blur','.required',function() {
            JValidate.Validation.validateElement(this);
        });
        //模态
        $('#modify-row-modal').on('hidden.bs.modal', function (e) {
            validator.reset();
        });
        <!--新增采集标准校验器-->
        var adapterOrgValidator = new JValidate.Validation($("#update_org_form"),{immediate:true,onSubmit:false});
        //焦点丢失校验
        $('#update_org_form').on('blur','.required',function() {
            JValidate.Validation.validateElement(this);
        });
        //模态
        $('#adapter_org_modal').on('hidden.bs.modal', function (e) {
            validator.reset();
        });

        $(".J_searchBtn").click(function () {
            reloadGrid();
        });

        $("#add-btn").click(function () {
            detailMode("ADD");
            clearModal();
        });

        //delete multi row
        $("#delete-rows-btn").click(function () {
            var rowIds = grid.instance.jqGrid('getGridParam', 'selarrrow');
            var id=[];
            for(var i= 0;i<rowIds.length;i++){
                id[i]=grid.instance.getRowData(rowIds[i]).id;
            }
            delAdapterPlan(id);
        });

        //delete one row
        $("#delete-btn").click(function () {
            var id = $("#delete-id").val();
            delAdapterPlan(id);
        });


        $("#update-btn").click(function () {
            if(!validator.validate() ) {return;}
            //新增时做判断，映射机构与父方案的采集标准是否一致，是否要覆盖
            var org=$("#org").val();
            var id=$("#parentId").val();
            if (addFlg && org && id){
                var parentOrg="";
                var orgIsExistData=false;
                //映射机构是否已存在采集标准
                $.ajax({
                    type: "POST",
                    url: context.path + "/adapter/orgIsExistData",
                    data: {org:org},
                    dataType: 'json',
                    async: false,
                    success: function (data) {
                        orgIsExistData = data.successFlg;
                    },
                    error: function (data) {
                    }
                });
                //父方案信息
                $.ajax({
                    type: "POST",
                    url: context.path + "/adapter/getAdapterPlan",
                    data: {id: id},
                    dataType: "json",
                    async: false,
                    success: function (data) {
                        if (data.successFlg) {
                            var model = data.obj.adapterPlan;
                            parentOrg = model.org;
                        }
                    },
                    error: function (data) {
                    }
                });
                if (orgIsExistData && (org != parentOrg)) {
                    //映射机构存在采集标准，并且与父方案的采集标准不一致，不允许选取父方案或修改映射机构。
                    $('#msg_modal').modal();
                    $("#confirm").off('click').on('click',function () {
                        updateAdapterPlan();
                    });
                    $("#cancel").off('click').on('click',function () {
                        alert("请修改映射机构或父级方案！");
                    });
                }else{
                    updateAdapterPlan();
                }
            }else{
                updateAdapterPlan();
            }
        });

        function updateAdapterPlan(){
            $.ajax({
                type: "POST",
                url: context.path + "/adapter/updateAdapterPlan",
                data: $('#update-form').serialize(),
                dataType: 'json',
                success: function (data) {
                    if (data.successFlg) {
                        reloadGrid();
                        $(".close",$('#modify-row-modal')).click();
                    } else {
                        alert($.i18n.prop('message.distribution.failure'));
                        return;
                    }
                },
                error: function (data) {
                }
            });
        }

        $("#update_data").click(function () {
            var notes = treeTo.getData();
            var toTreeData;
            var adapterDataSet = [];

            getData(notes);
            function getData(data){
                for (var i = 0; i < data.length; i++) {
                    if(data[i].__status=="add"){//界面显示的树信息
                        toTreeData = {id:data[i].id.replace("adapter",""),pid:(data[i].pid||"").replace("adapter",""),text:data[i].text}; //获取节点数据
                        adapterDataSet.push(toTreeData);
                        if (data[i].children && data[i].children.length) {
                            getData(data[i].children);//获取下一级节点的数据
                        }
                    }
                }
            }
            $.ajax({
                type: "POST",
                url: context.path + "/adapter/adapterDataSet",
                data: {planId: planId,customizeData:JSON.stringify(adapterDataSet)},
                dataType: "json",
                success: function (data) {
                    if (data.successFlg) {
                        //成功
                        $(".close",$('#customize-row-modal')).click();
                    } else {
                        alert($.i18n.prop('message.distribution.failure'));
                        return;
                    }
                },
                error: function (data) {
                }
            });
        });

        $("#type").change(function(){
            var type = $("#type").val();
            initOrg($("#org"),type);
            $('.add-image').show();
            if (type=='1'){
                //厂商，没有父级方案
                $("#parent").hide();
            }else if (type=='2'){
                //医院，父级方案没有限制
                $("#parent").show();
                initAdapterPlans($("#parentId"),type);
            }
            if (type=='3'){
                //区域,父级方案只能选择厂商或区域选择
                $("#parent").show();
                initAdapterPlans($("#parentId"),type);
            }
        });

        //根据传入的字典ID及显示对象，加载下拉框的数据。
        function initDDL(dictId, target) {
            /*下拉框的方法*/
            $.ajax({
                url: context.path + "/dict/searchDictEntryList",
                type: "post",
                dataType: "json",
                data: {dictId: dictId, page: "1", rows: "5"},
                success: function (data) {
                    target.empty();
                    if (data.successFlg) {
                        //var option ='<option value="0">选择所有</option>';
                        var option = '';
                        for (var i = 0; i < data.detailModelList.length; i++) {
                            option += '<option value="' + data.detailModelList[i].code + '">' + data.detailModelList[i].value + '</option>';
                        }
                        target.append(option);
                    }
                }
            });
        }

        function initVersionDDL(target){
            $.ajax({
                url:context.path + "/standardsource/getVersionList",
                type:"post",
                dataType:"json",
                async: false,
                success:function(data){
                    if (data.successFlg) {
                        var versions = data.obj;
                        var option = ' ';
                        for (var i = 0; i < versions.length; i++) {
                            //version,versionName
                            var version = versions[i].split(",");
                            option += '<option value="' + version[0] + '">' + version[1] + '</option>';
                        }
                        target.append(option);
                    }
                }
            });
        }

        function initAdapterPlans(target,type){
            var nowId = $("#id").val();
            $.ajax({
                url:context.path + "/adapter/getAdapterPlanList",
                type:"post",
                dataType:"json",
                async: false,
                data:{type:type},
                success:function(data){
                    target.empty();
                    if (data.successFlg) {
                        var adapterPlans = data.obj;
                        var option = '<option value=""></option>';
                        for (var i = 0; i < adapterPlans.length; i++) {
                            //version,versionName
                            var adapterPlan = adapterPlans[i].split(",");
                            var parentId = adapterPlan[0];
                            var parentName = adapterPlan[1];
                            if (parentId !== nowId) {
                                option += '<option value=' + parentId + '>' + parentName + '</option>';
                            }
                        }
                        target.append(option);
                    }
                }
            });
        }

        function initOrg(target,type){
            $.ajax({
                url:context.path + "/adapter/getOrgList",
                type:"post",
                dataType:"json",
                async: false,
                data:{type:type},
                success:function(data){
                    target.empty();
                    if (data.successFlg) {
                        var organizations = data.obj;
                        var option = '<option value=""></option>';
                        for (var i = 0; i < organizations.length; i++) {
                            //orgCode,orgName
                            var org = organizations[i].split(",");
                            var orgCode = org[0];
                            var orgName = org[1];
                            option += '<option value="' + orgCode + '">' + orgName + '</option>';
                        }
                        target.append(option);
                    }
                }
            });
            if(!chosen) {
                chosen = target.chosen();
            } else {
                target.trigger("liszt:updated");
            }
        }

        //标准定制分发
        function adapterCustomize(params){
            var fromTree = params.fromTree; //来源树对象（ligerTree对象）
            var toTree = params.toTree; //目标树对象
            var fromTarget = params.fromTarget; //来源节点id（如："#tree_from"）
            var toTarget = params.toTarget; //目标节点id
            var fromFlag = params.fromFlag; //来源树特殊标志 如：（id="std987",fromFlag="std"）
            var toFlag = params.toFlag; //目标树特殊标志
            var data,dom,pid;
            var treeToData; //目标树要添加节点的实际data（带有特殊标志）
            var removeData = [];//来源树要删除的集合
            var from = fromTree.getChecked(); //勾选的内容
            //来源树到目标树节点生成
            for(var i= 0;i<from.length;i++){
                data = from[i].data;
                //是否父节点,即全选情况
                if (fromTree.hasChildren(data)) {
                    //要删除根节点
                    removeData.push($('#' + data.id, fromTarget)[0]); //记录要删除的节点,放后删除
                }else {
                    pid = data.pid;
                    dom = $('#' + pid.replace(fromFlag,toFlag), toTarget)[0]; //父节点dom
                    //父节点是否存在,不存在要创建
                    if (dom==null){
                        var parentData = fromTree.getDataByID(pid); //父节点的data
                        var parentDom = $('#' + parentData.pid.replace(fromFlag,toFlag), toTarget)[0]; //父节点的父节点dom
                        treeToData = [{id:parentData.id.replace(fromFlag,toFlag),pid:parentData.pid.replace(fromFlag,toFlag),text:parentData.text}]; //父节点要添加的实际data
                        toTree.append(parentDom,treeToData); //添加父节点
                        dom = $('#' + pid.replace(fromFlag,toFlag), toTarget)[0]; //提供父节点的dom
                    }
                    removeData.push($('#' + data.id, fromTarget)[0]); //记录药删除的节点,放后删除
                    treeToData = [{id:data.id.replace(fromFlag,toFlag),pid:pid.replace(fromFlag,toFlag),text:data.text}]; //添加树节点的实际data
                    toTree.append(dom,treeToData); //添加树节点
                    fromTree.getTextByID(data.id);
                }
            }
            //来源树放后删除节点
            for(var j =0;j<removeData.length;j++){
                //根节点不删，默认根节点id为0
                if (removeData[j].id!=fromFlag+'0' && removeData[j].id!=toFlag+'0'){
                    //fromTree.clear(removeData[j]);
                    fromTree.remove(removeData[j]); //删除树节点
                }
            }
            //refresh
            fromTree.refreshTree();
            toTree.refreshTree();
        }

        function getStdDataSet(version,adapterDataSet) {
            $.ajax({
                type: "POST",
                url: context.path + "/adapter/getStdDataSet",
                data: {version: version,adapterDataSet:JSON.stringify(adapterDataSet)},
                dataType: "json",
                async: false,
                success: function (data) {
                    if (data.successFlg) {
                        treeFrom = $("#tree_from").ligerTree({
                            data: data.obj,
                            nodeWidth: 190,
                            idFieldName :'id',
                            parentIDFieldName :'pid',
                            isExpand: 2
                        });
                    } else {
                    }
                },
                error: function (data) {
                }
            });
        }

        function getAdapterDataSet(planId,version) {
            $.ajax({
                type: "POST",
                url: context.path + "/adapter/getAdapterDataSet",
                data: {planId:planId,version: version},
                dataType: "json",
                async: false,
                success: function (data) {
                    if (data.successFlg) {
                        adapterDataSet = data.obj;
                        treeTo = $("#tree_to").ligerTree({
                            data: ($.extend(true,[],adapterDataSet)),
                            nodeWidth: 190,
                            idFieldName :'id',
                            parentIDFieldName :'pid',
                            isExpand: 2
                        });
                    } else {
                    }
                },
                error: function (data) {
                }
            });
        }

        function delAdapterPlan(id){
            $.ajax({
                type: "POST",
                url: context.path + "/adapter/delAdapterPlan",
                data: {id: id},
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
            $('input', $("#update-form")).val("");
            $('select', $("#update-form")).val("");
            $('textarea', $("#update-form")).val("");
        }

        function getModalInfo(id) {
            clearModal();
            $.ajax({
                type: "POST",
                url: context.path + "/adapter/getAdapterPlan",
                data: {id: id},
                dataType: "json",
                success: function (data) {
                    if (data.successFlg) {
                        var model = data.obj.adapterPlan;
                        $("#id").val(model.id);
                        $('#type').find("option[value='" + model.type + "']").prop("selected", true);
                        $("#code").val(model.code);
                        $('#name').val(model.name);
                        $('#version').find("option[value='" + model.version + "']").prop("selected", true);
                        $('#description').val(model.description);
                        $("#type").trigger("change");
                        $('#parentId').find("option[value='" + model.parentId + "']").prop("selected", true);
                        $('#org').find("option[value='" + model.org + "']").prop("selected", true);
                        $("#org").trigger("liszt:updated");
                        //可编辑控制
                        detailMode("MODIFY");
                    }
                },
                error: function (data) {
                }
            });
        }

        function detailMode(type){
            if (type=='ADD'){
                addFlg=true;
                $('#type').attr("disabled",false);
                $('#version').attr("disabled",false);
                $('#parentId').attr("disabled",false);
                $('#org').attr("disabled",false);
            }
            if (type=='MODIFY'){
                addFlg=false;
                $('#type').attr("disabled",true);
                $('#version').attr("disabled",true);
                $('#parentId').attr("disabled",true);
                $('#org').attr("disabled",true);
                $('.add-image').hide();
            }
        }

        function reloadGrid() {
            var searchNm = $("#search-no").val();
            grid.instance.jqGrid('setGridParam', {
                url: context.path + "/adapter/searchAdapterPlan",
                postData: {searchNm: searchNm} //发送数据
            }).trigger("reloadGrid");
        }


        //-------------------------新增映射机构-------------------------
        var orgChosen = null;
        $('.add-image').hide();
        $(".add-image").click(function () {
            $('#adapter_org_type').attr("disabled",false);
            $("#area_model").hide();
            clearAdapterOrg();
            $("#adapter_org_modal").modal();
            var type = $("#type").val();
            $('#adapter_org_type').find("option[value='" + type + "']").prop("selected", true);
            $('#adapter_org_type').trigger("change");
            $('#adapter_org_type').attr("disabled",true);
        });

        $("#update_adapter_org").click(function () {
            if(!adapterOrgValidator.validate() ) {return;}
            $.ajax({
                type: "POST",
                url: context.path + "/adapterorg/addAdapterOrg",
                data: $('#update_org_form').serialize(),
                dataType: 'json',
                success: function (data) {
                    if (data.successFlg) {
                        changeOrg();
                        $(".close",$('#adapter_org_modal')).click();
                    } else {
                        alert(data.errorMsg);
                        return;
                    }
                },
                error: function (data) {
                }
            });
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
                var org = $("#adapter_org").val();
                if (org){
                    //区域代码：机构代码+'ORG'+地区编码
                    $("#adapter_org_code").val(org+'ORG'+areaCode);
                    //默认名称为机构名+(地区名)
                    $("#adapter_org_name").val($("#adapter_org option:selected").text()+'('+area+')');
                }
            }
            $("#province").val(province);
            $("#city").val(city);
            $("#district").val(district);
            $("#town").val(town);
        });

        $("#adapter_org").change(function(){
            //默认名称为机构名
            var org = $("#adapter_org").val();
            var type = $("#adapter_org_type").val();
            var area = $("#area").val();
            if (type=='3' && area){
                //区域代码：机构代码+'ORG'+地区编码
                $("#adapter_org_code").val(org+'ORG'+$("#area_code").val());
                //区域名称：机构名+(地区名)
                $("#adapter_org_name").val($("#adapter_org option:selected").text()+'('+area+')');
            }else{
                $("#adapter_org_code").val(org);
                $("#adapter_org_name").val($("#adapter_org option:selected").text());
            }

        });

        $("#adapter_org_type").change(function(){
            var type = $("#adapter_org_type").val();
            if (type=='1'){
                //厂商，初始标准只能是厂商
                initOrganization($("#adapter_org"),type);//采集机构
                $("#area_model").hide();
            }else if (type=='2'){
                //医院，初始标准没有限制
                initOrganization($("#adapter_org"),type);//采集机构
                $("#area_model").hide();
            }
            if (type=='3'){
                //区域,初始标准只能选择厂商或区域
                initOrganization($("#adapter_org"),'1');//采集机构
                $("#area_model").show();
            }
        });

        function changeOrg(){
            var newOrg = $("#adapter_org_code").val();
            var type = $("#type").val();
            initOrg($("#org"),type);
            $('#org').find("option[value='" + newOrg + "']").prop("selected", true);
            $("#org").trigger("liszt:updated");
        }

        function clearAdapterOrg() {
            $('input', $("#update_org_form")).val("");
            $('select', $("#update_org_form")).val("");
            $('textarea', $("#update_org_form")).val("");
        }
        //采集机构
        function initOrganization(target,type){
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

    })
});