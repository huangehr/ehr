<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="utf-8"%>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>

<!DOCTYPE html>
<html>
  <head>
    <%@include file="/WEB-INF/ehr/commons/jsp/commonHead.jsp" %>
  </head>

  <body>

  <div  class="m-form-inline">
    <div id="div_user_info_form" data-role-form>
      <div class="m-form-group">
        <label >时间控件：</label>
        <div class="m-form-control essential">
          <input type="text" id="inp_date" class="required useTitle" placeholder="输入日期"  required-title="不能为空" data-attr-scan="date"/>
        </div>
      </div>
      <div class="m-form-group">
        <label>文本框带删除按钮控件：</label>
        <div class="l-text-wrapper m-form-control essential">
          <input type="text" id="inp_user_name" class="required useTitle" placeholder="输入用户名"  required-title="不能为空" data-attr-scan="user.name"/>
        </div>
      </div>
      <div class="m-form-group">
        <label>下拉框控件：</label>
        <div class="m-form-control">
          <input type="text" id="ipt_select" data-type="select" data-attr-scan="select" class="required useTitle" required-title="不能为空">
        </div>
      </div>
      <div class="m-form-group">
        <label>地址联动下拉控件：</label>
        <div class="m-form-control f-w240">
          <input type="text" id="inp_address_combo_select" data-type="comboSelect" data-attr-scan="comSelect" class="required f-ww f-h28 useTitle" required-title="不能为空">
        </div>
      </div><div class="m-form-group">
      <label>组织机构联动下拉控件：</label>
      <div class="m-form-control f-w240">
        <input type="text" id="inp_org_address_combo_select" data-type="comboSelect" data-attr-scan="orgComboSelect" class="required f-ww f-h28 useTitle" required-title="不能为空">
      </div>
    </div>
      <div class="m-form-group">
        <label>文本框带搜索按钮控件：</label>
        <div class="m-form-control ">
          <input type="text" id="inp_search_text" data-attr-scan="searchText"/>
        </div>
      </div>
      <div class="m-form-group">
        <label>单选框控件：</label>
        <div class="u-radio-wrap j-radio-wrapper m-form-control validate-one-required f-h30 f-w240 f-lh30" >
          <input type="radio" value="1" name="age" data-attr-scan="user.age" > 16-25
          <input type="radio" value="2" name="age" data-attr-scan="user.age" > 26-35
          <input type="radio" value="3" name="age" data-attr-scan="user.age"> 35-45
        </div>
      </div>
      <div class="m-form-group">
        <label>复选框控件：</label>
        <div class="u-checkbox-wrap m-form-control j-checkbox-wrapper checkbox-required f-h30 f-w240 f-lh30">
          <input type="checkbox" name="chbox" value="1" data-attr-scan >选项一
          <input type="checkbox" name="chbox" value="2" data-attr-scan >选项二
          <input type="checkbox" name="chbox" value="3" data-attr-scan >选项三
        </div>
      </div>
      <div class="m-form-group">
        <label>大按钮：</label>
        <div class="m-form-control">
          <div class="l-button u-btn u-btn-primary u-btn-large f-ib f-vam" >
            <span>保存</span>
          </div>
          <div class="l-button u-btn u-btn-cancel u-btn-large f-ib f-vam" >
            <span>关闭</span>
          </div>
          <div class="l-button u-btn u-btn-success u-btn-large f-ib f-vam" >
            <span>发送</span>
          </div>
        </div>
      </div>
      <div class="m-form-group">
        <label>小按钮：</label>
        <div class="m-form-control">
          <div class="l-button u-btn u-btn-primary u-btn-small f-ib f-vam" >
            <span>保存</span>
          </div>
          <div class="l-button u-btn u-btn-cancel u-btn-small f-ib f-vam" >
            <span>关闭</span>
          </div>
          <div class="l-button u-btn u-btn-success u-btn-small f-ib f-vam" >
            <span>发送</span>
          </div>
        </div>
      </div>
      <div class="m-form-group">
        <label>表单验证控件：</label>
        <div id="div_validate" class="l-button u-btn u-btn-primary u-btn-large f-ib f-vam" >
          <span>验证</span>
        </div>
      </div>
      <div class="m-form-group">
        <label>图片上传控件：</label>
        <div id="div_user_img_upload" class="u-upload alone f-ib f-tac f-vam" data-alone-file=true>
          <!--用来存放item-->
          <div id="div_file_list" class="uploader-list"></div>
          <div id="div_file_picker" class="f-mt10">选择文件</div>
        </div>
        <div id="div_upload_btn" class="l-button u-btn u-btn-primary u-btn-small f-ib f-vam" >
          <span>上传</span>
        </div>
      </div>

      <div class="m-form-group">
        <label>图片上传控件：</label>
        <div id="div_upload2" class="u-upload alone f-ib f-tac f-vam" data-alone-file=true>
          <!--用来存放item-->
          <div id="div_file_list2" class="uploader-list"></div>
          <div id="div_file_picker2" class="f-mt10">选择文件</div>
        </div>
        <div id="div_upload2_btn" class="l-button u-btn u-btn-primary u-btn-small f-ib f-vam" >
          <span>上传</span>
        </div>
      </div>
      <div class="m-form-group">
        <label>弹出框：</label>
        <div class="m-form-control">
          <div id="div_confirm_btn" class="l-button u-btn u-btn-primary u-btn-large f-ib f-vam" >
            <span>确认对话框</span>
          </div>
          <div id="div_success_btn" class="l-button u-btn  u-btn-success u-btn-large f-ib f-vam" >
            <span>成功对话框</span>
          </div>
          <div id="div_warn_btn" class="l-button u-btn  u-btn-cancel u-btn-large f-ib f-vam" >
            <span>警告对话框</span>
          </div>
          <div id="div_error_btn" class="l-button u-btn  u-btn-cancel u-btn-large f-ib f-vam" >
            <span>错误对话框</span>
          </div>
          <div id="div_waitting_btn" class="l-button u-btn u-btn-primary u-btn-large f-ib f-vam" >
            <span>等待对话框</span>
          </div>
          <div id="div_waitting3000_btn" class="l-button u-btn u-btn-primary u-btn-large f-ib f-vam" >
            <span>默认等待3秒对话框</span>
          </div>
          <div id="div_waitting1000_btn" class="l-button u-btn u-btn-primary u-btn-large f-ib f-vam" >
            <span>等待1秒对话框</span>
          </div>
          <div id="div_mutil_dialog_btn" class="l-button u-btn u-btn-primary u-btn-large f-ib f-vam" >
            <span>多层弹出对话框测试</span>
          </div>
        </div>
      </div>
    </div>
  </div>
  <div id="div_mutil_dialog_content" class="f-dn">
    <div style="width: 500px; height: 500px;">
      <div id="div_second_dialog_btn" class="l-button u-btn u-btn-primary u-btn-large f-ib f-vam" >
        <span>第二层</span>
      </div>
    </div>
  </div>

  <div id="div_user_info_grid" style="height: 500px;">

  </div>
  <%@include file="/WEB-INF/ehr/commons/jsp/commonFoot.jsp" %>
  <script>
    (function ($, win) {
      $(function () {

        /* ************************** 变量定义 ******************************** */

        // 通用工具类库
        var Util = $.Util;

        // 表单校验工具类
        var jValidation = $.jValidation;

        // 数据模型
        var DataModel = $.DataModel;
        var dataModel = DataModel.init();

        var form = null;

        /* ************************** 变量定义结束 **************************** */

        /* *************************** 函数定义 ******************************* */
        /**
         * 页面初始化。
         * @type {Function}
         */
        function pageInit() {
          form.init();
          //$.ligerDialog.waitting('<div><img src="/ha/develop/images/loading002.gif" style="width: 50px;">正在保存中,请稍候...</div>');
        }


        /* ************************** 函数定义结束 **************************** */

        /* *************************** 模块初始化 ***************************** */

        form = {
          $element: $('#div_user_info_form'),
          $date: $("#inp_date"),
          $userName: $("#inp_user_name"),
          $search: $("#inp_search_text"),
          $select: $("#ipt_select"),
          $addressDropdown: $('#inp_address_combo_select'),
          $orgAddressDropdown: $('#inp_org_address_combo_select'),
          $radio: $("input:radio"),
          $checkbox: $('input:checkbox'),
          $validateBtn: $('#div_validate'),
          $uploader: $('#div_user_img_upload'),
          $uploadBtn: $('#div_upload_btn'),
          $confirmBtn: $('#div_confirm_btn'),
          $successBtn: $('#div_success_btn'),
          $warnBtn: $('#div_warn_btn'),
          $errorBtn: $('#div_error_btn'),
          $waittingBtn: $('#div_waitting_btn'),
          $waitting3000Btn: $('#div_waitting3000_btn'),
          $waitting1000Btn: $('#div_waitting1000_btn'),
          $mutilDialogBtn: $('#div_mutil_dialog_btn'),
          $secondDialogBtn: $('#div_second_dialog_btn'),
          init: function () {
            this.$date.ligerDateEditor({initValue: '2015-11-13',format: "yyyy-MM-dd"});
            this.$userName.ligerTextBox({width: 240});
            this.$search.ligerTextBox({width: 240,isSearch:true,search: function() {
              alert("值为:" + $("#inp_search_text").val());
            }});
            this.$select.ligerComboBox({
              data: [
                { text: '张三', id: '1' },
                { text: '李四', id: '2' },
                { text: '赵武', id: '44' },
                { text: '赵武', id: '44' },
                { text: '赵武', id: '44' },
                { text: '赵武', id: '44' },
                { text: '赵武', id: '44' },
                { text: '赵武', id: '44' },
                { text: '赵武', id: '44' },
                { text: '赵武', id: '44' },
                { text: '赵武', id: '44' },
                { text: '赵武', id: '44' },
                { text: '赵武', id: '44' }

              ]
            });
            this.$radio.ligerRadio();
            this.$checkbox.ligerCheckBox();

            this.$element.attrScan();
            this.$element.Fields.fillValues({
              date:'2015-11-12',
              "user.name": "yyyy",
              searchText: 'search something',
              age: '1',
              chbox: ['1','3']
            });

            this.$uploader.instance = this.$uploader.webupload({
              server: '${contextRoot}/address/getParent'
            });

            this.$addressDropdown.addressDropdown({tabsData:[
              {name: '省份', url: '${contextRoot}/address/getParent', params: {level:'1'}},
              {name: '城市', url: '${contextRoot}/address/getChildByParent'},
              {name: '县区', url: '${contextRoot}/address/getChildByParent'},
              {name: '街道'}
            ]});
            this.$orgAddressDropdown.addressDropdown({tabsData:[
              {name: '省份', url: '${contextRoot}/address/getParent', params: {level:'1'}},
              {name: '城市', url: '${contextRoot}/address/getChildByParent'},
              {name: '医院', url: '${contextRoot}/address/getOrgs',beforeAjaxSend: function (ds, $options) {
                var province = $options.eq(0).attr('title'),
                        city = $options.eq(1).attr('title');
                ds.params = $.extend({},ds.params,{
                  province: province,
                  city: city
                });
              }}
            ]});

            userInfoGrid = $("#div_user_info_grid").ligerGrid($.LigerGridEx.config({
              url: '${contextRoot}/user/searchUsers',
              // 传给服务器的ajax 参数
              parms: {
                searchNm: '',
                searchType: ''
              },
              columns: [
                // 隐藏列：hide: true（隐藏），isAllowHide: false（列名右击菜单中不显示）
                {name: 'id', hide: true, isAllowHide: false},
                {name: 'loginCode', hide: true, isAllowHide: false},
                {
                  display: '姓名',
                  name: 'realName',
                  width: '10%',
                  minColumnWidth: 60,
                  editor: {type: 'text'},
                  validate: {required: true, digits: true}
                },
                {display: '类型', name: 'userTypeValue', width: '10%', align: 'left'},
                {display: '所属机构', name: 'organization', width: '20%'},
                {display: '邮件地址', name: 'email', width: '20%', resizable: true},
                {display: '是否激活', name: 'activated', width: '10%', minColumnWidth: 20},
                {display: '上次登录时间', name: 'lastLoginTime', width: '20%'},
                {
                  display: '操作', name: 'operator', width: '10%', render: function (row) {
                  var html = '<a href="javascript:void(0)" onclick="javascript:' + Util.format("$.publish('{0}',['{1}','{2}'])", "user:userInfoModifyDialog:open", row.id, 'modify') + '">编辑</a> / ';
                  if(Util.isStrEquals(row.activated,"是")){
                    html+= '<a href="javascript:void(0)" onclick="javascript:' + Util.format("$.publish('{0}',['{1}','{2}'])", "user:userInfoModifyDialog:failure", row.id,0) + '">失效</a>';
                  }else if(Util.isStrEquals(row.activated,"否")){
                    html+='<a href="javascript:void(0)" onclick="javascript:' + Util.format("$.publish('{0}',['{1}','{2}'])", "user:userInfoModifyDialog:failure", row.id,1) + '">开启</a>';
                  }

                  return html;
                }
                }
              ],
              enabledEdit: true
            }));
            // 自适应宽度
            userInfoGrid.adjustToWidth();

            this.bindEvents();

            window.form = this.$element;
            window.combo = this.$comboDropdown;
            console.log(this.$element.Fields.toJsonString());

          },
          bindEvents: function () {
            var validator = new jValidation.Validation(this.$element,{immediate:true,onSubmit:false});
            var self = this;
            this.$validateBtn.click(function () {
              if(validator.validate()) {
                alert('success');
              } else {
                alert('fail');
              }
            });
            this.$confirmBtn.click(function () {
              $.Notice.confirm('提示内容', function (isAgree) {
                if(isAgree) {
                  alert('yes');
                } else {
                  alert('no');
                }
              });
            });
            this.$successBtn.click(function () {
              $.Notice.success('提示内容');
            });
            this.$warnBtn.click(function () {
              $.Notice.warn('提示内容')
            });
            this.$errorBtn.click(function () {
              $.Notice.error('提示内容')
            });
            this.$waittingBtn.click(function () {
              $.Notice.waitting();
            });
            this.$waitting3000Btn.click(function () {
              $.Notice.waitting(true);
            });
            this.$waitting1000Btn.click(function () {
              $.Notice.waitting(1000);
            });
            this.$mutilDialogBtn.click(function () {
              $.ligerDialog.open({ target: $("#div_mutil_dialog_content"),isHidden: true, width: 500 });
            });
            this.$secondDialogBtn.click(function () {
              $.Notice.confirm('提示内容', function (isAgree) {
                if(isAgree) {
                  alert('yes');
                } else {
                  alert('no');
                }
              });
            });
            this.$uploadBtn.click(function() {
              var uploader = self.$uploader.instance;
              var files = uploader.getFiles('error');
              if(files.length) {
                uploader.retry();
              } else {
                uploader.upload();
              }
            });
          }
        };

        /* ************************* 模块初始化结束 ************************** */

        /* *************************** 页面初始化 **************************** */

        pageInit();

        /* ************************* 页面初始化结束 ************************** */
      });
    })(jQuery, window);
  </script>
  </body>
</html>
