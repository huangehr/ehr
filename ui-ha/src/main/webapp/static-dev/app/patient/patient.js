/**
 * Created by zqb on 2015/10/9.
 */

define(function (require, exports, module) {
    require('app/patient/patient.css');
    require('modules/datepicker/datepicker.zh-CN');

    var $ = require('base').$,
        context = require('base').context,
        Grid = require('modules/components/grid'),
        ViewController = require('viewController'),
        LinkageSelect = require('modules/components/linkageSelect'),
        CardManager = require('app/patient/cardManager'),
        SearchCard = require('app/patient/searchCard'),
        JValidate = require('jValidate');

    $.i18n.properties({              //加载资浏览器语言对应的资源文件
        name:'message',    //资源文件名称
        path: context.resourcePath+'/resources/i18n/', //资源文件路径
        mode:'map',                  //用Map的方式使用资源文件中的值
        callback: function() {       //加载成功后设置显示内容
        }
    });

    $(function () {
        var idCardNo = '';
        /*下拉框初始化*/
        initDDL(5, $('#nation'));
        initDDL(4, $('#martialStatus'));
        initDDL(6, $('#residenceType'));

        var searchNm = $("#searchNm").val();
        var grid = new Grid('#dataGrid', {
            url: context.path + "/patient/searchPatient",
            datatype: 'json',
            shrinkToFit: true,
            scrollOffset: 0,
            width: $(window).width(),
            height: $(window).height()-160,
            autoFit: true,
            marginHeight:160,
            rowNum: 10,
            mtype:'POST',
            //autowidth:true,
            colNames: [$.i18n.prop('grid.order'), $.i18n.prop('grid.userName'), $.i18n.prop('grid.idCardNo'), 'gender', $.i18n.prop('grid.genderValue'), $.i18n.prop('grid.tel'), $.i18n.prop('grid.homeAddress'), $.i18n.prop('grid.operation')],
            colModel: [
                {
                    name: 'order',
                    index: 'order',
                    sorttype: 'int',
                    width: 50,
                    align: "center"
                },
                {
                    name: 'name',
                    index: 'name',
                    width: 300,
                    align: "center"
                },
                {
                    name: 'idCardNo',
                    index: 'idCardNo',
                    width: 300,
                    align: "center"
                },
                {
                    name: 'gender',
                    index: 'gender',
                    align: "center",
                    hidden: true
                },
                {
                    name: 'genderValue',
                    index: 'genderValue',
                    width: 120,
                    align: "center"
                },
                {
                    name: 'tel',
                    index: 'tel',
                    width: 260,
                    align: "center"
                },
                {
                    name: 'homeAddress',
                    index: 'homeAddress',
                    width: 500,
                    align: "center"
                },
                {
                    name: 'operator ',
                    index: 'operator',
                    width: 140,
                    sortable: false,
                    align: "center",
                    formatter: function (value, grid, rows, state) {
                        return '<a data-toggle="modal" data-target="#modifyRowModal" class="J_modify-btn" data-rowid="' + grid.rowId + '" >'+$.i18n.prop("btn.update")
                            /*+'</a> / <a data-toggle="modal" data-target="#deleteRowModal" class="J_delete-btn" data-rowid="' + grid.rowId + '">'+$.i18n.prop("btn.delete")+'</a>'*/;
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
            onSelectRow: function(id){
                detailMode('VIEW');
                idCardNo = grid.instance.getRowData(id).idCardNo;
                getModalInfo(idCardNo);
                CardManager.setIdCardNo(idCardNo);
                SearchCard.setIdCardNo(idCardNo);
                $("input,select", $("#updateTable")).prop('disabled', true);
                $('#modifyRowModal').modal();
            }
        }).render();

        new ViewController('#dataGrid', {
            events: {
                'click .J_modify-btn': 'modifyItem',
                'click .J_delete-btn': 'deleteItem'
            },
            handlers: {
                modifyItem: function (e) {
                    detailMode('MODIFY');
                    var cell = $(e.currentTarget);
                    var rowid = cell.attr("data-rowid");
                    idCardNo = grid.instance.getRowData(rowid).idCardNo;
                    getModalInfo(idCardNo);
                },
                deleteItem: function (e) {
                    var cell = $(e.currentTarget);
                    var rowid = cell.attr("data-rowid");
                    idCardNo = grid.instance.getRowData(rowid).idCardNo;
                    $("#delIdCardNo").val(idCardNo);
                }
            }
        });

        <!--地址级联 -->
        var addrSelector = new LinkageSelect([
            {
                selector: "#provinceSelect", // 一级联动下拉框id选择器
                url: context.path + "/patient/getParent", // 一级联动下拉框数据请求路径
                data: {level: 1}, // 一级联动下拉框数据请求发送给服务器的ajax数据
                blankItem: true, // 是否插入置空选项行，默认为true
                paramMap: { // 请求参数映射: 在触发下拉框change事件时，将选中项的value值和其显示文本设置为pid和province请求参数
                    id: 'pid',
                    name: 'province'
                }
            },
            {
                selector: "#citySelect", // 二级联动下拉框id选择器
                url: context.path + "/patient/getChildByParent",
                blankItem: true, // 是否插入置空选项行，默认为true
                paramMap: { // 请求参数映射: 在触发下拉框change事件时，将选中项的显示文本设置为city请求参数
                    id: 'pid',
                    name: "city"
                }
            },
            {
                selector: "#districtSelect", // 三级联动下拉框id选择器
                url: context.path + "/patient/getChildByParent",
                blankItem: true, // 是否插入置空选项行，默认为true
                paramMap: { // 请求参数映射: 在触发下拉框change事件时，将选中项的显示文本设置为city请求参数
                    id: 'pid',
                    name: "district"
                }
            },
            {
                selector: "#townSelect", // 四级联动下拉框id选择器
                url: context.path + "/patient/getChildByParent",
                blankItem: true, // 是否插入置空选项行，默认为true
                paramMap: { // 请求参数映射: 在触发下拉框change事件时，将选中项的显示文本设置为city请求参数
                    id: 'pid',
                    name: "town"
                }
            }
        ]);
        <!--卡管理 -->
        var opts = {operator: true};
        CardManager.init('#cardManagerContainer');
        SearchCard.init('#searchCardContainer', opts);

        <!--日期控件 -->
        $('#birthday').datepicker({
            format: "yyyy-mm-dd",
            autoclose: true
        });

        <!--校验器-->
        var validator = new JValidate.Validation($("#formPatient"),{immediate:true,onSubmit:false});
        $('#formPatient').on('blur','.required',function() {
            JValidate.Validation.validateElement(this);
        });
        $('#modifyRowModal').on('hidden.bs.modal', function (e) {
            validator.reset();
        });

        $("#searchBtn").click(function () {
            reloadGrid();
        });

        $("#addBtn").click(function () {
            detailMode('ADD');
            clearModal();
        });

        $("#addCardBtn").click(function(){
            $("#searchCardModal").addClass("modal-pl50");
        });

        //删除按钮点击保存事件
        $("#deleteBtn").click(function () {
            var idCardNo = $("#delIdCardNo").val();
            $.ajax({
                type: "POST",
                url: context.path + "/patient/deletePatient",
                data: {idCardNo: idCardNo},
                dataType: "json",
                success: function (data) {
                    if (data.successFlg) {
                        reloadGrid();
                    } else {
                        alert($.i18n.prop('message.update.failure'));
                        return;
                    }
                },
                error: function (data) {
                }
            });
        });
        //重置密码按钮事件
        $("#resetPassBtn").click(function () {
            var idCardNo = $("#idCardNo").val();
            $.ajax({
                type: "POST",
                url: context.path + "/patient/resetPass",
                data: {idCardNo: idCardNo},
                dataType: "json",
                success: function (data) {
                    if (data.successFlg) {
                        alert($.i18n.prop('message.reset.success'));
                    } else {
                        alert($.i18n.prop('message.reset.failure'));
                        return;
                    }
                },
                error: function (data) {
                }
            });
        });

        $('#birthPlace').click(function () {
            $('#addressType').val('birthPlace');
            setAddress($('#birthProvince').val(), $('#birthCity').val(), $('#birthDistrict').val(), $('#birthTown').val(), $('#birthExtra').val());
        });
        $('#homeAddress').click(function () {
            $('#addressType').val('homeAddress');
            setAddress($('#homeProvince').val(), $('#homeCity').val(), $('#homeDistrict').val(), $('#homeTown').val(), $('#homeExtra').val());
        });
        $('#workAddress').click(function () {
            $('#addressType').val('workAddress');
            setAddress($('#workProvince').val(), $('#workCity').val(), $('#workDistrict').val(), $('#workTown').val(), $('#workExtra').val());
        });

        $("#locationConfirmBtn").click(function () {
            var addressType = $("#addressType").val();
            getAddress(addressType);
        });

        $('#editBtn').click(function (){
            detailMode('MODIFY');
        });
        $("#updateBtn").click(function () {
            if(!validator.validate() ) {return;}
            $.ajax({
                type: "POST",
                url: context.path + "/patient/updatePatient",
                data: $('#formPatient').serialize(),
                dataType: 'json',
                success: function (data) {
                    if (data.successFlg) {
                        reloadGrid();
                        $(".close",$('#modifyRowModal')).click();
                    } else {
                        alert($.i18n.prop('message.update.failure'));
                        return;
                    }
                },
                error: function (data) {
                }
            });
        });

        $("#baseInfo").click(function () {
            $('#baseInfo').removeClass('tab-info').addClass('tab-info-click');
            $('#cardMgr').addClass('tab-info');
            $('#ha').addClass('tab-info');
            $("#baseInfo-content").show();
            $('#cardMgr-content').hide();
            $('#ha-content').hide();
        });
        $("#cardMgr").click(function () {
            $('#cardMgr').removeClass('tab-info').addClass('tab-info-click');
            $('#baseInfo').addClass('tab-info');
            $('#ha').addClass('tab-info');
            $('#cardMgr-content').show();
            $("#baseInfo-content").hide();
            $('#ha-content').hide();
        });
        $("#ha").click(function () {
            $('#ha').removeClass('tab-info').addClass('tab-info-click');
            $('#baseInfo').addClass('tab-info');
            $('#cardMgr').addClass('tab-info');
            $('#ha-content').show();
            $('#cardMgr-content').hide();
            $("#baseInfo-content").hide();
        });

        //明细弹出框模式（增、改、查）
        function detailMode(type) {
            if (type=='ADD'){
                $('.address','#updateTable').attr('placeholder',$.i18n.prop('message.click.select.details'));
                //不可编辑项控制
                $("input,select", $("#updateTable")).prop('disabled', false);
                $("#idCardNo", $("#updateTable")).prop('readonly', false);
                $("#name", $("#updateTable")).prop('readonly', false);
                //高级设置
                $("#password").hide();
                //tab设置
                $('#baseInfo').removeClass('tab-info-click').addClass('tab-info');
                $('#baseInfo').text($.i18n.prop('message.new.patient.information'));
                $('#baseInfo-content', '#modifyRowModal').css('height', '600px');
                $('#baseInfo').addClass("btn-disabled");
                $('#baseInfo-content').show();
                $('#cardMgr').hide();
                $('#cardMgr-content').hide();
                $('#ha').hide();
                $('#ha-content').hide();
                //footer设置
                $('#editBtn').hide();
                $('#updateBtn').show();
            }
            if (type=='MODIFY'){
                $('.address','#updateTable').attr('placeholder',$.i18n.prop('message.click.select.details'));
                //不可编辑项控制
                $("input,select", $("#updateTable")).prop('disabled', false);
                $("#idCardNo", $("#updateTable")).prop('readonly', true);
                $("#name", $("#updateTable")).prop('readonly', true);
                //tab设置
                $('#baseInfo').text($.i18n.prop('message.patient.basic.information'));
                $('#baseInfo-content', '#modifyRowModal').css('height', '680');
                $('#baseInfo').removeClass("btn-disabled");
                $('#baseInfo').trigger('click');
                $('#cardMgr').show();
                $('#ha').show();
                //高级设置
                $("#password").show();
                $("#resetPassBtn").removeClass("btn-disabled").removeClass("disabled");
                //footer设置
                $('#editBtn').hide();
                $('#updateBtn').show();
            }
            if (type=='VIEW'){
                $('.address','#updateTable').attr('placeholder','');
                //不可编辑设置
                $("input,select", $("#updateTable")).prop('disabled', false);
                //tab设置
                $('#baseInfo').text($.i18n.prop('message.patient.basic.information'));
                $('#baseInfo-content', '#modifyRowModal').css('height', '680');
                $('#baseInfo').removeClass("btn-disabled");
                $('#baseInfo-content').show();
                $('#cardMgr').show();
                $('#ha').show();
                $('#baseInfo').trigger('click');
                //高级设置
                $("#password").show();
                $("#resetPassBtn").addClass("btn-disabled").addClass("disabled");
                //footer设置
                $('#editBtn').show();
                $('#updateBtn').hide();
            }
        }

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
                    //var option ='<option value="0">选择所有</option>';
                    var option = '';
                    for (var i = 0; i < data.detailModelList.length; i++) {
                        option += '<option value=' + data.detailModelList[i].code + '>' + data.detailModelList[i].value + '</option>';
                    }
                    $(target).append(option);
                }
            });
        }

        function setAddress(province, city, district, town, extra) {
            addrSelector.selects[0].setValueByName(province);
            addrSelector.selects[1].setValueByName(city);
            addrSelector.selects[2].setValueByName(district);
            addrSelector.selects[3].setValueByName(town);
            $('#extraSelect').val(extra);
        }

        function getAddress(addressType) {
            var province = $('#provinceSelect').find('option:selected').html();
            var city = $('#citySelect').find('option:selected').html();
            var district = $('#districtSelect').find('option:selected').html();
            var town = $('#townSelect').find('option:selected').html();
            var extra = $('#extraSelect').val();

            if (addressType == 'birthPlace') {
                $("#birthProvince").val(province);
                $("#birthCity").val(city);
                $("#birthDistrict").val(district);
                $("#birthTown").val(town);
                $("#birthExtra").val(extra);
                $("#birthPlace").val(province + city + district + town + extra);
            }
            if (addressType == 'homeAddress') {
                $("#homeProvince").val(province);
                $("#homeCity").val(city);
                $("#homeDistrict").val(district);
                $("#homeTown").val(town);
                $("#homeExtra").val(extra);
                $("#homeAddress").val(province + city + district + town + extra);
            }
            if (addressType == 'workAddress') {
                $("#workProvince").val(province);
                $("#workCity").val(city);
                $("#workDistrict").val(district);
                $("#workTown").val(town);
                $("#workExtra").val(extra);
                $("#workAddress").val(province + city + district + town + extra);
            }
        }

        function clearModal() {
            $('input[type!="radio"]', $("#updateTable")).val("");
            $('input[type="radio"]', $("#updateTable")).prop("checked", false);
            $('select', $("#updateTable")).val("");
        }

        function getModalInfo(idCardNo) {
            clearModal();
            $.ajax({
                type: "POST",
                url: context.path + "/patient/getPatient",
                data: {idCardNo: idCardNo},
                dataType: "json",
                success: function (data) {
                    var model = data.obj.patientModel;
                    $("#name").val(model.name);
                    $("#idCardNo").val(model.idCardNo);
                    $("input[name='gender'][value='" + model.gender + "']").prop("checked", true);
                    $('#nation').find("option[value='" + model.nation + "']").prop("selected", true);
                    $('#nativePlace').val(model.nativePlace);
                    $('#martialStatus').find("option[value='" + model.martialStatus + "']").prop("selected", true);
                    $('#birthday').val(model.birthday);
                    $('#birthPlace').val(model.birthPlaceFull);
                    $('#birthProvince').val(model.birthPlace.province);
                    $('#birthCity').val(model.birthPlace.city);
                    $('#birthDistrict').val(model.birthPlace.district);
                    $('#birthTown').val(model.birthPlace.town);
                    $('#birthExtra').val(model.birthPlace.extra);
                    $('#homeAddress').val(model.homeAddressFull);
                    $('#homeProvince').val(model.homeAddress.province);
                    $('#homeCity').val(model.homeAddress.city);
                    $('#homeDistrict').val(model.homeAddress.district);
                    $('#homeTown').val(model.homeAddress.town);
                    $('#homeExtra').val(model.homeAddress.extra);
                    $('#workAddress').val(model.workAddressFull);
                    $('#workProvince').val(model.workAddress.province);
                    $('#workCity').val(model.workAddress.city);
                    $('#workDistrict').val(model.workAddress.district);
                    $('#workTown').val(model.workAddress.town);
                    $('#workExtra').val(model.workAddress.extra);
                    $("#residenceType").find("option[value='" + model.residenceType + "']").prop("selected", true);
                    $("#tel").val(model.tel);
                    $("#email").val(model.email);
                },
                error: function (data) {
                }
            });
        }

        function reloadGrid() {
            var searchNm = $("#searchNm").val();
            var searchWay = $("#searchWay").val();
            grid.instance.jqGrid('setGridParam', {
                url: context.path + "/patient/searchPatient",
                page : 1,
                postData:{searchNm:searchNm,searchWay:searchWay} //发送数据
            }).trigger("reloadGrid");
        }
    })
});